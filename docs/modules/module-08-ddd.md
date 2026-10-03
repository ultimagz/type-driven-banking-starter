# Module 08 — DDD: ตั้งชื่อให้ตรงงานและให้กฎมีเจ้าของ

ถ้าฝ่ายงานบอก “บัญชี” แต่ทีม dev คนหนึ่งหมายถึง DTO อีกคนหมายถึงยอดใน cache เรากำลังออกแบบเรื่องเดียวกันหรือไม่?

ก่อนเรียน: เข้าใจ value object/core/use case/repository จาก [บท 01](module-01-value-objects.md) และ [บท 07](module-07-clean-boundary.md)
หลังเรียน: แยก entity/value object, aggregate/root, event/command และอธิบายขอบเขต consistency ได้
เวลา: 2 sessions × 90 นาที • Branch: `module-08-ddd`

## 1. Concept: DDD เริ่มจากภาษาของงาน

**DDD — Domain-Driven Design** คือแนวทางออกแบบ software โดยให้ความเข้าใจงาน/กฎของ domain เป็นตัวนำ model และภาษาที่ทีมใช้
ไม่ได้เริ่มจากโครง package ที่มีชื่อ entity/aggregate ครบ และไม่ได้บังคับว่าทุก class ต้องเป็น object ที่มี methods มากมาย
เราต้องรู้ก่อนว่าสิ่งนั้นหมายถึงอะไร ใครรู้กฎ และอะไรต้องถูกต้องพร้อมกัน

**Ubiquitous language** คือคำที่คนงานและคนพัฒนาใช้ร่วมกันในขอบเขตหนึ่ง เช่น AvailableBalance ไม่ได้หมายถึงยอด ledger ทุกกรณี
เขียน glossary ของทีมและใช้คำเดียวกันใน requirement, types, tests และ discussion
**Bounded context** คือขอบเขตที่ศัพท์/model หนึ่งมีความหมายตรงกัน เช่น “Account” ในธนาคารกับ “Account” สำหรับ login อาจเป็นคนละเรื่อง
ไม่ต้องมี microservices เพื่อมี bounded contexts เราเริ่มจากแยกความหมายในโปรเจกต์เดียวได้

## 2. Entity กับ value object

Entity คือสิ่งที่มี identity ต่อเนื่องแม้ข้อมูลเปลี่ยน บัญชี a-1 มียอด 100 แล้วเป็น 75 ก็ยังเป็นบัญชี a-1
Value object สนใจค่า เช่น AccountId("a-1") สองก้อนเท่ากันเพราะข้อความรหัสเหมือนกัน
การมี ID field ไม่ได้ทำให้ object ทุกตัวเป็น entity โดยอัตโนมัติ ต้องถามว่าฝ่ายงานติดตามสิ่งนี้ในฐานะตัวเดิมหรือสนใจเฉพาะค่า

| สิ่ง | คำถามว่าเหมือนกันด้วยอะไร | ตัวอย่าง |
|---|---|---|
| Account entity | บัญชีตัวเดิมหรือไม่ | id เดิม แม้ยอดเปลี่ยน |
| AccountId value | ค่ารหัสเท่ากันหรือไม่ | a-1 เท่ากับ a-1 |
| PositiveMoney value | จำนวนเงินในบริบทนี้เท่ากันหรือไม่ | ต้องมี policy scale/currency ชัด |

Checkpoint PositiveMoney ในรีโปยังไม่ implement value equality เต็มรูปแบบ; homework ให้ทำเพิ่มเติมได้ อย่าเชื่อว่าชื่อ type ทำให้ equality ถูกเอง

## 3. Aggregate/root: ขอบเขตที่ต้องรักษากฎร่วมกัน

**Aggregate** คือกลุ่มข้อมูล/พฤติกรรมที่เราถือเป็นหนึ่งขอบเขต consistency
**Aggregate root** คือ object หลักที่รับการเปลี่ยนแปลงจากภายนอกและรักษากฎของกลุ่มนั้น
**Consistency** คือข้อมูลที่เกี่ยวข้องกันยังสอดคล้องตามกฎหลัง operation เสร็จ

ในแบบฝึก Account เป็น root ของยอด/สถานะของบัญชีหนึ่งบัญชี ให้การถอนผ่าน root แทนเปิดให้ caller กำหนดยอดตามใจ
ถ้าบัญชีมีข้อมูลย่อย Caller ไม่ควรไปแก้บางชิ้นจนกฎของ root พัง
Aggregate ไม่ได้หมายถึงรวมทุก object ที่เกี่ยวข้องกันไว้ใน class เดียว เช่นการโอนเกี่ยวกับสองบัญชี แต่ไม่ได้ทำให้สองบัญชีต้องเป็น aggregate เดียวเสมอ

กฎ “เงินรวมก่อน/หลังโอนต้องตรงกัน” ข้ามสอง root จึงต้องมี decision/orchestration และ persistence consistency ที่ออกแบบไว้
คอร์สใช้ pure decideTransfer ร่วมกับ use case/repository contract ไม่อ้างว่าแค่เรียก withdraw แล้ว deposit จะ atomic ในฐานข้อมูลเอง

## 4. Before → after: กฎอยู่กับใคร

Before: Account เป็นเพียง field bag; use case หลายตัว copy/paste checks ของ balance/status
After: Account.withdraw รับ amount/time และคืน Success หรือ Rejected; rule ยอด/สถานะอยู่ใกล้ข้อมูลที่รู้เรื่องนั้น

```text
Account.withdraw(amount, at)
  → Rejected(reason)
  → Success(account ใหม่, MoneyWithdrawn)
```

**Rich domain model** คือ model ที่มีความหมาย/behavior รักษากฎของตัวเอง
**Anemic model** เป็นคำเรียก model ที่เก็บ fields แต่กฎถูกย้ายไป services ทั้งหมด
ไม่ได้หมายความว่า DTO ต้องมี methods ธุรกิจ: DTO มีหน้าที่ส่งข้อมูลที่ boundary; root กับ DTO คนละบทบาท

## 5. Domain event คืออะไร

Command คือ “ขอให้ถอน” ส่วน **Domain event** คือ “มีการถอนเกิดขึ้นแล้วตามกฎที่ตัดสินสำเร็จ”
Event ต้องมีข้อมูลพอให้ผู้ใช้ event ทำงาน เช่น accountId, amount, occurredAt แต่ไม่ควรแบก object graph ใหญ่โดยไม่จำเป็น

```kotlin
data class MoneyWithdrawn(
    val accountId: AccountId,
    val amount: PositiveMoney,
    val occurredAt: Instant,
)
```

การคืน event จาก core ยังไม่เท่ากับส่งออกไปนอกระบบหรือบันทึกสำเร็จแล้ว
Shell จะ persist state และจัดการ publication ตาม policy; อย่าส่ง notification ว่าถอนสำเร็จก่อนบันทึกแล้วปล่อยให้ save fail
**Outbox** เป็นแนวคิดเก็บ event ร่วมกับ state ใน transaction แล้วส่งต่อภายหลัง เพื่อแก้ช่องว่างระหว่าง save กับ publish เป็น extension ไม่ต้อง implement ในบทนี้

## 6. Impact และ trade-offs

คนอ่าน signature เห็นคำของงานและรู้ที่มาของ rule ลดการตรวจซ้ำใน use cases
Events แยกการตัดสินใจออกจากการตอบสนอง เช่น audit/notification และ core ยังทดสอบได้โดยไม่ส่ง network message
แต่ aggregate boundary ที่ใหญ่เกินไปทำให้แก้ข้อมูลหลายอย่างพร้อมกันโดยไม่จำเป็น; เล็กเกินไปอาจกระจาย rule ที่ต้อง consistent
ใช้ requirement เป็นเหตุผลในการเลือก boundary ไม่ตัดสินจากจำนวน class หรือชื่อ pattern

## 7. Guided exercise — บัญชีและข้อเท็จจริงที่เกิด

เปิด `Module08Exercise.kt` ใน starter:

1. เขียน glossary 5 คำ: Account, Balance, Withdrawal, Transfer, Transaction
2. วาด Account root และข้อมูลที่มันคุม; ระบุ invariant 2 ข้อ
3. ย้าย rule ยอด/สถานะให้ behavior ของ root หรือ helper ที่ root ใช้
4. เมื่อถอนสำเร็จคืน account ใหม่พร้อม MoneyWithdrawn; rejection ไม่คืน event สำเร็จ
5. Test event accountId/amount/at ตรง input และ input account ไม่ถูกแก้
6. อธิบายว่า transfer สอง root ถูก save/publish ที่ boundary ใด

คำใบ้: ชื่อ event ใช้ข้อเท็จจริงที่เกิดแล้ว เช่น MoneyWithdrawn ไม่ใช้ WithdrawMoney ที่เป็นคำสั่ง
สิ่งที่ส่ง: glossary + boundary diagram + rule ownership + tests ของ success/rejection/events
ผ่านเมื่อเพื่อนบอกได้ว่าหาก rule หนึ่งเปลี่ยนต้องเปิดไฟล์ไหน และ publication ไม่รั่วเข้ามาใน pure core

## 8. Homework

ออกแบบ notification consumer ที่รับ MoneyWithdrawn แต่ยังไม่ต้องส่งจริง
เขียน policy ว่าถ้า event มาซ้ำจะทำอย่างไร; **Idempotency** คือการทำซ้ำแล้วไม่เกิดผลเพิ่มที่ผิดจาก intent
ถามว่า Account ใน authentication context กับ banking context แชร์ model เดียวกันควรเป็นหรือไม่ พร้อมข้อดี/ข้อเสีย

## 9. Checkpoint (10 คะแนน)

1. Entity กับ value object เท่ากันด้วยอะไร (2)
2. Aggregate root มีหน้าที่ใดนอกเหนือจากมีชื่อว่า Root (2)
3. Transfer ต้องรวมสอง Account เป็น aggregate เดียวเสมอไหม (2)
4. Command กับ event ต่างกันอย่างไร (2)
5. Core คืน event แล้วแปลว่า persist/publish สำเร็จแล้วไหม (2)

ผ่านที่ 8/10 พร้อม rule ownership ชัดและไม่สับสน event creation กับ publication

## 10. สำหรับผู้สอน

เริ่มจากถามคำว่า “ยอดเงิน” หมายถึงอะไร แล้วให้ทีมเห็นว่าความหมายไม่ชัดทำให้ type ไม่ชัด
Discussion: กฎใดต้อง atomic? event นี้เป็น fact ของ domain หรือ transport message? model คำเดียวกันมีความหมายต่าง context ได้ไหม?

ถัดไป: [Module 09 — ทดสอบกฎด้วยหลายกรณี](module-09-property-thinking.md)
