# Module 05 — State Machine: รู้ทั้ง “อยู่ตรงไหน” และ “ไปทางไหนได้”

มีสถานะที่หน้าตาถูกต้องยังไม่พอ ถ้ารายการสำเร็จแล้วถูกสั่งเริ่มใหม่ เราควรยอมให้มันย้อนกลับไหม?

ก่อนเรียน: อธิบาย state variants จาก [บท 04](module-04-illegal-states.md) ได้
หลังเรียน: เขียน transition table แยก state/command/event และทดสอบทั้งทางที่อนุญาตและปฏิเสธ
เวลา: 2 sessions × 90 นาที • Branch: `module-05-state-machine`

## 1. Concept: ใช้ตัวอย่างประตูเริ่มก่อน

ประตูมี Closed กับ Open; คำสั่ง Open เมื่อ Closed ทำให้เป็น Open
คำสั่ง Close เมื่อ Open ทำให้เป็น Closed แต่คำสั่ง Open เมื่อ Open อาจไม่ทำอะไรหรือปฏิเสธ ขึ้นอยู่กับกติกาที่เราตกลง
**State machine** คือ model ที่ระบุสถานะและกฎการเปลี่ยนระหว่างสถานะเหล่านั้น
ไม่จำเป็นต้องเริ่มจากไลบรารี state machine; sealed types + function + tests ก็เป็น implementation ได้

**Transition** คือการเปลี่ยนจาก state หนึ่งไปอีก state หนึ่งเพราะคำสั่ง/เหตุการณ์ที่รับเข้ามา
**Command** คือคำขอให้ทำอะไร เช่น Start หรือ Succeed คำสั่งอาจถูกปฏิเสธได้
**Event** คือข้อเท็จจริงที่เกิดขึ้นแล้ว เช่น TransactionStarted จึงใช้ความหมาย/ชื่อที่เป็นอดีต
**Terminal state** คือ state ที่ workflow นี้ไม่อนุญาตให้ไปต่อ เช่น Succeeded/Failed ตามกติกาที่เลือก

## 2. Problem: if กระจายทำให้ทางเดินไม่ตรงกัน

ถ้า controller หนึ่งกำหนด status="SUCCESS" ได้ตรง ๆ แต่อีกตัวอนุญาตเฉพาะ PROCESSING ก่อน ทีมมี workflow คนละฉบับ
การเปลี่ยน status field ไม่บอกว่ากฎใดอนุญาต และไม่บอกข้อมูลที่ต้องสร้างพร้อมกัน เช่นเวลาที่สำเร็จ
Before: `transaction.status = "SUCCESS"` ถูกเรียกจากที่ใดก็ได้
After: ส่งคำสั่งเข้า function เปลี่ยนสถานะและรับ Accepted หรือ Rejected

## 3. Transition table ของธนาคารตัวอย่าง

เราตกลงให้ transaction เดิน Pending → Processing → Succeeded หรือ Failed:

| State ปัจจุบัน | Start | Succeed | Fail(reason) |
|---|---|---|---|
| Pending | Processing | ปฏิเสธ | ปฏิเสธ |
| Processing | ปฏิเสธ | Succeeded | Failed |
| Succeeded | ปฏิเสธ | ปฏิเสธ | ปฏิเสธ |
| Failed | ปฏิเสธ | ปฏิเสธ | ปฏิเสธ |

ตารางนี้เป็นกฎคอร์ส ถ้างานจริงอนุญาต retry หลัง Failed ให้เพิ่มเส้นทางหรือสร้าง attempt ใหม่อย่างชัดเจน
อย่าเปิด retry โดยเงียบ ๆ เพียงเพราะ method รับ command ตัวเดิมได้

## 4. อ่าน API ทีละชนิด

```kotlin
sealed interface TransactionCommand {
    data object Start : TransactionCommand
    data object Succeed : TransactionCommand
    data class Fail(val reason: FailureReason) : TransactionCommand
}

sealed interface Transition {
    data class Accepted(val transaction: Transaction) : Transition
    data class Rejected(
        val state: Transaction,
        val command: TransactionCommand,
    ) : Transition
}
```

Transaction คือ “สิ่งที่เป็นอยู่”; TransactionCommand คือ “สิ่งที่ขอ”; Transition คือ “ผลจากการขอ”
Rejected มี state/command เดิมเพื่อให้ caller อธิบายได้ว่าคำขอใดไม่เหมาะ โดยไม่แก้ transaction

Signature ของ checkpoint คือ `Transaction.transition(command, now): Transition`
จุดข้างหน้าชื่อ function หมายถึง extension function: เราเพิ่มวิธีเรียก `transaction.transition(...)` โดยไม่ได้สร้าง method ใน class โดยตรง
มันยังเป็น function ที่รับ Transaction เป็น input อยู่ ไม่ได้ให้สิทธิ์ข้าม private fields

Trace: Pending(t-1) + Start เวลา 10:00 → Processing(t-1, 10:00)
Processing + Succeed เวลา 10:01 → Succeeded(t-1, 10:01)
Succeeded + Start เวลา 10:02 → Rejected; state เดิมยัง Succeeded

อีกทางหนึ่งอาจออกแบบ `Pending.start(): Processing` เป็น API ที่รับ type เฉพาะ ช่วยจำกัด caller ตั้งแต่ compile-time
กรณีคำสั่งมาจาก network/storage เรามักยังต้องตรวจ runtime ว่า state ที่โหลดมาตรงกับคำสั่งหรือไม่

## 5. Impact และความเข้าใจที่ต้องไม่พลาด

มี transition table กลาง ทำให้ product/dev/test คุยเส้นทางเดียวกัน และ tests ไล่ตาม cell ได้
การเพิ่ม state ใหม่ทำให้เห็นคำถามเรื่อง command ทุกแบบ ไม่ใช่เพียงเพิ่ม String หนึ่งคำ
State machine ไม่รับรองเรื่อง concurrency เอง: สอง process ที่โหลด Pending พร้อมกันอาจต้องใช้ version/transaction ที่ persistence boundary
ถ้าต้องรับรองเวลาที่เรียงถูก ต้องเพิ่ม temporal rule เช่น now >= startedAt และ typed rejection ไม่ใช่หวังว่า Instant ป้องกันให้เอง

## 6. Guided exercise — จากตารางสู่ function

เปิด `Module05Exercise.kt` ใน starter:

1. เขียนตาราง 4×3 ตามหัวข้อ 3 ก่อนแตะ Kotlin
2. สร้าง commands และ outcomes ที่มีชื่อชัด
3. เติม transition ด้วย when ตาม state แล้วตาม command
4. ใช้ Instant ที่ส่งเข้ามา ไม่เรียกเวลาปัจจุบันภายใน
5. ใน rejection ให้คืน state เดิม; ไม่ mutate แล้วค่อย reject
6. ทำ tests ครบ 12 คู่ในตาราง โดยใช้เวลา fixed

คำใบ้: เริ่มจาก Pending+Start หนึ่งกรณี จากนั้นทำ invalid Pending+Succeed ก่อนเพิ่ม Processing
ผลที่ตรวจ: variant, id เดิม, timestamp ที่ส่งมา และ input object ไม่เปลี่ยน
ถ้า state/command ใหม่เพิ่มเข้ามา ให้แก้ทั้งตาราง/implementation/tests พร้อมกัน

## 7. Homework

ออกแบบการโต้แย้งรายการบัตร: Opened → Investigating → Resolved/Rejected
เขียนความหมายของแต่ละ state, command ที่รับ, data ที่ต้องมี และเหตุผลปฏิเสธ
เพิ่มโจทย์ repeat command: ถ้ารับ Resolve ซ้ำควร reject หรือคืนผลเดิม? เขียน policy ของตน ไม่จำเป็นต้องเลือกเหมือนทีมอื่นถ้าอธิบายได้

## 8. Checkpoint (10 คะแนน)

1. แยก state, command, event ด้วยคำตัวอย่างอย่างละหนึ่ง (3)
2. Pending+Succeed ควรได้อะไรตามกฎคอร์ส (1)
3. Terminal state หมายถึงอะไร และเกิดใหม่อีก attempt ได้ไหม (2)
4. ทำไม tests ต้องมีคู่ที่ไม่อนุญาตด้วย (2)
5. State machine กันสอง process เขียนพร้อมกันได้เองหรือไม่ (2)

ผ่านที่ 8/10 พร้อม transition matrix ที่ trace ได้ทุก cell

## 9. สำหรับผู้สอน

ใช้บัตร state/command ให้คนหนึ่งเป็นระบบ อีกคนส่งคำสั่ง; ระบบต้องตอบพร้อมชี้ cell ในตาราง
อย่าถามเพียง “เขียนเมื่อไรเป็น sealed” ให้ถามว่า “ทางเดินไหนอนุญาตและใครตัดสิน?”
Discussion: retry เป็นเส้นทางเดิมหรือ attempt ใหม่? เวลาเริ่ม/จบใครเป็นคนให้? คำสั่งซ้ำควรตอบอย่างไร?

ถัดไป: [Module 06 — แยกการตัดสินใจออกจากโลกภายนอก](module-06-functional-core.md)
