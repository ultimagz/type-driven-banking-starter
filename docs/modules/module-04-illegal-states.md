# Module 04 — Make Illegal States Unrepresentable: ทำให้ข้อมูลที่ขัดกันสร้างไม่ได้

คำนี้ยาว แต่คำถามเริ่มต้นสั้นมาก: “ทำไมรายการที่ยังไม่เสร็จถึงมีเวลาที่เสร็จแล้ว?”

ก่อนเรียน: เข้าใจ ADT จาก [บท 02](module-02-adt.md) และ typed outcome จาก [บท 03](module-03-typed-errors.md)
หลังเรียน: จับ nullable fields ที่สัมพันธ์กับ status และย้ายข้อมูลไปอยู่ variant ที่ต้องการมัน
เวลา: 2 sessions × 90 นาที • Branch: `module-04-illegal-states`

## 1. Concept: ข้อมูลบางอย่างมีความหมายเฉพาะบางสถานะ

คิดถึงพัสดุ: ก่อนส่งยังไม่มี tracking number หลังส่งต้องมี tracking number
ถ้าใช้ `status: String` กับ `trackingNumber: String?` เป็นสองช่องอิสระ เราสร้าง “ยังไม่ส่งแต่มีเลขติดตาม” หรือ “ส่งแล้วไม่มีเลข” ได้
**Representable** แปลว่า model ของเราอนุญาตให้สร้างข้อมูลชุดนั้นได้ **Unrepresentable** คือโครง type ไม่เปิดทางให้สร้าง combination นั้น

คำว่า **illegal state** ในบทนี้หมายถึงข้อมูลที่ขัดกฎของ domain ไม่เกี่ยวกับกฎหมาย
แนวคิดนี้ไม่ได้ทำให้ทุก rule ของระบบตรวจได้ตอน compile-time มันลด combinations ที่ไร้ความหมายด้วยการจัดโครงข้อมูล
กฎเกี่ยวกับค่าจริง เช่นเวลาสำเร็จต้องไม่ก่อนเวลาเริ่ม อาจยังต้องตรวจตอนสร้าง/เปลี่ยนสถานะ

## 2. Problem: nullable bag of fields

**Nullable** คือ field ที่อนุญาต null ส่วน **bag of fields** เป็นคำเรียก model ที่รวบช่องข้อมูลไว้ด้วยกันโดยไม่บอกความสัมพันธ์

```kotlin
data class LooseTransaction(
    val status: String,
    val completedAt: Instant?,
    val failureReason: String?,
)
```

ถามแต่ละสถานะ:

| State | completedAt | failureReason |
|---|---|---|
| Pending | ไม่ควรมี | ไม่ควรมี |
| Succeeded | ต้องมี | ไม่ควรมี |
| Failed | ไม่ใช่เวลาสำเร็จ | ต้องมีเหตุผล |

ทั้งสามช่องเป็นอิสระใน constructor จึงสร้าง Succeeded ที่ไม่มีเวลา หรือ Pending ที่มีทั้งเวลาและ reason ได้
ทุก caller ต้องรู้ตารางนี้และเขียน if/null checks เอง ตัวที่ลืมตรวจอาจแสดงข้อมูลผิดแม้ compile ผ่าน

## 3. After: state เป็นเจ้าของข้อมูลของมัน

```kotlin
sealed interface Transaction {
    val id: TransactionId

    data class Pending(override val id: TransactionId) : Transaction
    data class Processing(
        override val id: TransactionId,
        val startedAt: Instant,
    ) : Transaction
    data class Succeeded(
        override val id: TransactionId,
        val completedAt: Instant,
    ) : Transaction
    data class Failed(
        override val id: TransactionId,
        val reason: FailureReason,
        val failedAt: Instant,
    ) : Transaction
}
```

ตัวอย่าง reuse TransactionId/FailureReason ของ checkpoint; Instant คือจุดเวลาหนึ่ง ไม่ใช่ข้อความเวลาที่เดารูปแบบเอาเอง
`override val id` หมายถึงแต่ละ variant ให้ field id ตามที่ชนิดร่วมกำหนด
Pending ไม่มีช่อง completedAt จึงไม่ต้องคอยตรวจว่า field นี้เป็น null
Succeeded ต้องรับ completedAt ที่ไม่ nullable; Failed ต้องรับ reason และ failedAt

เมื่อ caller ใช้ when แล้วเข้าสู่ Succeeded มันอ่าน completedAt ได้เลย เพราะ compiler รู้ variant นั้น
**Smart cast** คือ compiler ช่วยมองค่าด้วยชนิดที่แคบลงหลังตรวจว่าเป็น variant อะไร ไม่ใช่ smart constructor ของบท 01

## 4. Impact และขอบเขตของการป้องกัน

Before: เราสร้างข้อมูลอะไรก็ได้แล้วค่อยถามว่ามี fields ตรงกันไหม
After: fields ที่ไม่เกี่ยวกับ state นั้นไม่มีที่ให้ใส่ และ fields ที่จำเป็นถูก constructor ขอไว้
ผลคือ caller อ่านข้อมูลตรงไปตรงมาและผิดจาก nullable combinations ยากขึ้น

แต่ `Transaction.Succeeded(id, time)` ยังสร้างจากที่ไหนก็ได้ถ้า constructor public
ADT ในบทนี้รับรอง “รูปแบบของ state” ไม่ได้พิสูจน์ว่า workflow เคยผ่าน Processing มาแล้ว
บท 05 จึงเพิ่มทางเปลี่ยนสถานะที่ควบคุมได้ ส่วนกฎของเวลาและการโหลดข้อมูลดิบยังต้องมี construction checks

## 5. Guided exercise — จัด fields ตาม state

เปิด `Module04Exercise.kt` ใน starter แล้วออกแบบ Transaction หรือ model เทียบเคียง:

1. เขียนรายชื่อ states พร้อมความหมายหนึ่งประโยคต่อแบบ
2. ทำตารางว่า field ใด “ต้องมี / ห้ามมี / มีได้” ในแต่ละ state
3. แยก field ที่ร่วมกันทุกแบบ เช่น id ออกจาก field เฉพาะแบบ
4. สร้าง sealed variants แทน nullable bag
5. เขียน formatter/summary ด้วย exhaustive when
6. ทดสอบข้อมูลแต่ละ variant และอธิบาย invalid combination ที่ type ตัดออกได้

คำใบ้: ถ้าทุก variant ยังมี `reason: String?` เหมือนเดิม อาจยังไม่ได้ย้าย rule เข้าไปในโครงข้อมูล
อย่าเขียน runtime test ที่พยายามเรียก constructor ด้วย parameter ที่ไม่มี: นั่น compile ไม่ผ่านตั้งแต่แรก ให้บันทึกเป็น “ตัวอย่างที่ไม่ compile” ในโน้ต
Runtime tests ควรตรวจ formatting/behavior ของค่าที่สร้างได้ และกฎของเวลา/เหตุผลที่ตรวจด้วย constructor

## 6. Homework

ออกแบบ Order จาก paid/shipped/cancelled/trackingNumber โดยตกลงก่อนว่า cancel หลัง shipped ได้ไหม
จากนั้นออกแบบ LoanApplication: Draft, Submitted, UnderReview, Approved, Rejected, Disbursed
แต่ละ state ต้องมีข้อมูลอะไร เช่น Approved มีวงเงินที่อนุมัติ ส่วน Rejected มีเหตุผล
LoanApplication เป็นเพียงโจทย์ฝึก model ไม่ใช่ policy สินเชื่อที่ต้องยึดตามโลกจริง

## 7. Checkpoint (10 คะแนน)

1. ทำไม Succeeded + completedAt=null เป็น model ที่เปิดทางให้ข้อมูลผิด (2)
2. ยก illegal combinations สองแบบและ variant ที่กำจัดแต่ละแบบ (2)
3. แยก smart cast กับ smart constructor (2)
4. ADT รับรองว่า Succeeded ผ่าน Processing มาก่อนหรือไม่ (2)
5. กฎ completedAt >= startedAt ใช้ type นี้อย่างเดียวรับรองได้ไหม (2)

ผ่านที่ 8/10 และต้องรู้ขอบเขตในข้อ 4–5 เพื่อไม่เชื่อว่า sealed แก้ทุก rule ให้เอง

## 8. สำหรับผู้สอน

เริ่มจากตัวอย่างพัสดุก่อนคำว่า unrepresentable แล้วถามว่า “ช่องนี้ควรอยู่ในแบบไหน?”
ให้แต่ละ pair พยายามสร้าง invalid combination ของ model เพื่อนโดยใช้ public API
Discussion: field ไหนบังคับทุก state เกินจำเป็น? ค่าที่มาจาก DB ต้องแปลงกลับเข้ารูปแบบใหม่นี้อย่างไร?

ถัดไป: [Module 05 — ควบคุมทางเปลี่ยนสถานะ](module-05-state-machine.md)
