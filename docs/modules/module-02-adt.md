# Module 02 — Product/Sum Types และ ADT: ออกแบบ “มีครบ” กับ “เลือกอย่างหนึ่ง”

ไม่ต้องเริ่มจากพีชคณิต ให้เริ่มจากฟอร์มสองแบบ: แบบแรกต้องกรอกหลายช่องพร้อมกัน แบบที่สองต้องเลือกสถานะหนึ่งสถานะ

ก่อนเรียน: เข้าใจ type, value object และ invariant จาก [บท 01](module-01-value-objects.md)
หลังเรียน: แยก AND/OR ใน requirement ได้ ใช้ data class/sealed interface ได้ถูกหน้าที่ และอ่าน ADT ออก
เวลา: 2 sessions × 90 นาที • Branch: `module-02-adt`

## 1. Product type: ต้องมีหลายอย่างร่วมกัน

สมมติที่อยู่จัดส่งต้องมีถนน เมือง และรหัสไปรษณีย์ นี่คือ “ถนน AND เมือง AND รหัสไปรษณีย์”
**Product type** คือชนิดข้อมูลที่รวมค่าหลายช่องเข้าด้วยกัน แต่ละ object ต้องมีค่าของทุกช่องที่กำหนด
Kotlin `data class` เป็นเครื่องมือที่ใช้แทนแนวคิดนี้ได้ตรงไปตรงมา:

```kotlin
data class Address(val street: String, val city: String, val postalCode: String)
```

ตัวอย่างธนาคาร `Account(id, balance, status)` รวมรหัส AND ยอดเงิน AND สถานะ ไม่ใช่เลือกแค่บางช่อง
คำว่า product ไม่ได้หมายถึงสินค้า เหตุผลของชื่อคือถ้าช่องแรกมี 2 ค่าที่เป็นไปได้และช่องที่สองมี 3 แบบ เราสร้างคู่ได้ 2×3=6 แบบ
ความเข้าใจ AND สำคัญกว่าการจำสูตร: ยิ่งเพิ่ม independent fields ยิ่งเพิ่ม combinations ที่ต้องดูว่ามีความหมายหรือไม่

## 2. Sum type: เลือกได้หนึ่งแบบจากหลายแบบ

บัญชีตามกติกาเรามี Active, Frozen หรือ Closed ณ เวลาหนึ่งเป็นเพียงแบบใดแบบหนึ่ง
**Sum type** คือชนิดข้อมูลที่มีทางเลือกหลายแบบ แต่ค่าหนึ่งค่าจะเป็นหนึ่งทางเลือก ไม่ใช่เป็นทุกแบบพร้อมกัน
คำว่า sum ไม่ได้บอกให้บวกเงิน เหตุผลคือถ้าทางเลือกหนึ่งมี 2 แบบและอีกทางเลือกมี 3 แบบ รวมทางเลือกได้ 2+3=5 แบบ

```kotlin
sealed interface AccountStatus {
    data object Active : AccountStatus
    data object Frozen : AccountStatus
    data object Closed : AccountStatus
}
```

`interface` ระบุชนิดร่วมของทั้งสามแบบ; `: AccountStatus` อ่านว่า “เป็นหนึ่งรูปแบบของ AccountStatus”
`sealed` จำกัด direct subtypes ตามกฎของ Kotlin ทำให้ compiler รู้ชุดทางเลือกสำหรับการตรวจ `when`
`data object` คือสถานะแบบไม่มีข้อมูลเพิ่มและใช้ instance ที่แชร์กัน ไม่ต้องเขียน `Active()` ทุกครั้ง
ถ้าทุกทางเลือกไม่มีข้อมูลพิเศษ `enum class` ก็เป็นตัวเลือกที่เหมาะ; ใช้ sealed เมื่อบางแบบต้องมีข้อมูลต่างกัน

## 3. ADT คืออะไร

**ADT — Algebraic Data Type** ในคอร์สนี้หมายถึงการประกอบชนิดข้อมูลด้วย product และ sum เพื่อบอกชุดค่าที่เป็นไปได้
ตัวอย่างง่าย: Account มี id AND balance AND status ขณะที่ status เป็น Active OR Frozen OR Closed
คำว่า algebraic ในชื่อมาจาก AND ที่นับแบบคูณและ OR ที่นับแบบบวก ไม่ได้หมายความว่าต้องเรียนคณิตศาสตร์ขั้นสูงก่อนเขียนโค้ด

ลองอีกตัวอย่าง: ผลชำระเงินเป็น Pending OR Paid(receiptId) OR Failed(reason)
แต่ละทางเลือกอาจเป็น product ภายใน เช่น Failed ต้องมี reason AND failedAt
Value object กับ ADT จึงทำงานร่วมกัน: value object ดูแลความถูกต้องของค่าแต่ละช่อง ส่วน ADT ดูแลวิธีประกอบช่อง/ทางเลือก

## 4. Problem: Boolean หลายตัวเปิดทางให้สถานะสับสน

Before:

```kotlin
data class AccountFlags(
    val isActive: Boolean,
    val isFrozen: Boolean,
    val isClosed: Boolean,
)
```

Boolean มี true/false; สามตัวอิสระมี 2³=8 combinations
แต่เราต้องการเพียงสามสถานะ เช่น active=true, frozen=true, closed=true คือชุดข้อมูลที่ไม่รู้ว่าควรถอนเงินได้หรือไม่
ทุก caller ต้องช่วยตีความ combinations และอาจให้คำตอบต่างกัน

After: ใช้ `status: AccountStatus` หนึ่งค่าแทน flags สามตัว
ตอนนี้ค่าไม่สามารถเป็น Active และ Closed ใน field เดียวกันพร้อมกันได้
Impact: ลดชุดข้อมูลไร้ความหมาย ลด status ที่สะกดผิด และทำให้ branch ของ caller ผูกกับสถานะที่มีชื่อ
ขอบเขต: การเป็น Active ยังไม่รับประกันว่าเงินพอถอน กฎของ operation ต้องตรวจต่อ

## 5. when แบบครบทุกกรณี

**Exhaustive** แปลว่าครอบคลุมทางเลือกครบทั้งหมด ลองอ่าน function นี้เป็นตารางชื่อสถานะ:

```kotlin
fun label(status: AccountStatus): String = when (status) {
    AccountStatus.Active -> "พร้อมใช้งาน"
    AccountStatus.Frozen -> "ระงับชั่วคราว"
    AccountStatus.Closed -> "ปิดบัญชีแล้ว"
}
```

แต่ละแขนคืนข้อความ `when` เป็น expression ที่คืน String ให้ function
ไม่ใส่ `else` เพื่อให้ compiler แจ้งจุดที่ต้องแก้เมื่อเพิ่มสถานะใหม่ เช่น UnderReview
`else` ไม่ผิดทุกกรณี แต่ถ้าใช้เป็นทางหนีทั้งหมด เราอาจลืมตัดสินใจว่า status ใหม่ควรทำอะไร

## 6. Guided exercise — มอง requirement เป็น AND/OR

เปิด `Module02Exercise.kt` ใน starter แล้วทำตามนี้:

1. เขียนประโยค “AccountStatus คือ Active OR Frozen OR Closed” ก่อนเขียน Kotlin
2. สร้าง sealed hierarchy หรือเริ่มจาก enum แล้วอธิบายเหตุผลที่เลือก
3. ปรับ `accountStatusFromLegacy` ให้คืน type ที่เจาะจงและกำหนด policy ของ String ที่ไม่รู้จัก เช่นคืนผลผิด ไม่เดาเป็น Active
4. เขียน label ด้วย exhaustive when ไม่มี else
5. Model payment: Pending, Processing, Succeeded(receiptId), Failed(reason)
6. เขียน tests mapping ของสถานะที่รู้จักและ unknown status

ลองพลาดอย่างตั้งใจ: เพิ่มหนึ่ง variant แล้ว compile ดูว่า caller ไหนถูกเตือน
เมื่อส่งงาน ต้องมีรูปหรือตาราง AND/OR และอธิบายว่า payment failure ที่ไม่มี reason ถูกป้องกันตรงไหน
โค้ดตัวอย่างเพื่อสอนอาจชื่อไม่ตรงกับไฟล์ใน snapshot ทุกบท ให้ reuse domain types ของ branch ที่ checkout ไม่สร้าง AccountStatus ชื่อซ้ำใน package เดียวกัน

## 7. Homework

Refactor user flags loggedIn/suspended/deleted เป็นชุดสถานะที่ธุรกิจยอมรับ
ต้องถามก่อนว่าสถานะนั้น mutually exclusive จริงไหม: ถ้า suspended กับ loggedIn อยู่พร้อมกันได้ ห้ามบังคับเป็น OR โดยเดาเอง
ออกแบบ KYC: NotStarted, Reviewing, Approved, Rejected(reason) พร้อมตัวอย่างหนึ่งค่าของแต่ละแบบ
KYC คือขั้นตอนรู้จัก/ตรวจสอบตัวตนลูกค้าใน domain ตัวอย่าง ไม่ใช่ศัพท์ type system

## 8. Checkpoint (10 คะแนน)

1. ยก product type หนึ่งตัวและอ่านเป็นประโยค AND (2)
2. ยก sum type หนึ่งตัวและอ่านเป็นประโยค OR (2)
3. ADT เกี่ยวข้องกับสองแนวคิดนี้อย่างไร (2)
4. สาม boolean ให้ 8 combinations แต่กฎอนุญาตเพียงสาม หมายความว่าอะไร (2)
5. ถ้าเพิ่ม variant แล้ว caller มี else ทำไมเราจึงอาจพลาด behavior ใหม่ (2)

ผ่านที่ 8/10 และต้องแยก AND/OR ได้ ถ้ายังไม่แน่ใจให้กลับไปตัวอย่างที่อยู่กับสถานะ ไม่เริ่มจากชื่อ algebraic

## 9. สำหรับผู้สอน

ให้ทีมวาด “ฟอร์มหลายช่อง” กับ “ตัวเลือกสถานะ” ก่อนเปิด data class/sealed
ถาม “ข้อมูลนี้ต้องมีพร้อมกันหรือเป็นหนึ่งทางเลือก?” กับทุก field ที่เพิ่ม
Discussion: enum พอหรือยัง? Frozen ต้องมี reason ไหม? flags บางตัวเป็นคนละแกนจริงหรือไม่?
อ่านเพิ่ม: [Kotlin sealed classes/interfaces](https://kotlinlang.org/docs/sealed-classes.html)

ถัดไป: [Module 03 — ผลลัพธ์ที่ล้มเหลวก็เป็นข้อมูล](module-03-typed-errors.md)
