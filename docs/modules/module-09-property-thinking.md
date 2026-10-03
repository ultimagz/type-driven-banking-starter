# Module 09 — Property-Based Thinking: เปลี่ยนจาก “ตัวอย่างนี้ผ่าน” เป็น “กฎนี้ต้องจริง”

Test ฝากเงิน 100 ผ่าน บอกได้ไหมว่าฝาก 0.01, 999 หรือจำนวนอื่นแล้วระบบถูกต้อง? บทนี้ฝึกตั้งคำถามที่ครอบคลุมมากกว่าตัวอย่างเดียว

ก่อนเรียน: pure function/immutability จาก [บท 06](module-06-functional-core.md) และ rule ownership จาก [บท 08](module-08-ddd.md)
หลังเรียน: แยก example/property, สร้างหลาย input ที่ valid และใช้ counterexample หาความหมายของกฎ
เวลา: 2 sessions × 90 นาที • Branch: `module-09-property-thinking`

## 1. Concept: test คือการถามโปรแกรม

**Test** คือโค้ดที่เตรียมสถานการณ์ เรียกสิ่งที่จะตรวจ และเทียบผลกับสิ่งที่คาด
**Assertion** คือคำสั่งตรวจความคาดหวัง เช่น assertEquals หรือ assertTrue; ถ้าไม่จริง test fail
**Example test** ตรวจสถานการณ์หนึ่งชุด เช่นบัญชี 100 ฝาก 25 แล้วเป็น 125
**Property** คือข้อความของกฎที่ต้องจริงสำหรับ input กลุ่มที่กำหนด เช่นจำนวนเงินฝากที่ valid ต้องไม่ทำให้ยอดลดลง

คำว่า property ที่นี่ไม่ได้หมายถึง field ของ class แบบ `val balance` แม้สะกดเหมือนกัน
**Property-based testing** คือการใช้ตัวสร้าง inputs จำนวนมากมาตรวจ property และรายงานกรณีที่ผิด
บทนี้เริ่มจาก loop ที่กำหนดไว้ก่อนเพื่อเรียนวิธีคิด ยังไม่ได้เพิ่ม property-testing library

**Generator** คือวิธีสร้าง input สำหรับ test ต้องตรงกับขอบเขตของ property
**Counterexample** คือ input ที่ทำให้ข้อความของ property ไม่จริง
**Shrinking** คือการลด input ที่ fail ให้เล็ก/ง่ายขึ้นโดยยัง fail เพื่อให้เห็นสาเหตุได้ไว

## 2. Problem: examples ที่ผ่านอาจยังพลาดกฎ

Before: ทดสอบเฉพาะการฝาก 100 และถอน 30 อาจไม่เจอข้อผิดพลาดที่ amount เล็กมากหรือกรณีไม่พอ
After: เขียนกฎก่อน แล้วเลือก examples/generator ที่มีโอกาสจับ rule violation
ไม่ใช่ทดสอบ “เยอะ” อย่างเดียว: ถ้า assertion เปรียบผลกับสูตรผิดเดียวกับ implementation จำนวน input มากก็ไม่ช่วย

ตัวอย่างกฎสำหรับ domain นี้:

| Property | ขอบเขต | สิ่งที่ต้องตรวจ |
|---|---|---|
| ฝากเงินบวกไม่ลด balance | account/amount valid และ operation อนุญาต | ยอดหลัง ≥ ยอดก่อน |
| ถอนถูกปฏิเสธไม่แก้ account | ทุกเหตุผล Rejected | fields เดิมไม่เปลี่ยน |
| โอนสำเร็จรักษายอดรวม | สกุลเงินเดียว ไม่มี fee/rounding | source+destination ก่อนเท่าหลัง |
| terminal state ไม่เริ่มซ้ำ | กติกา machine บท 05 | command ใหม่ถูก reject |

Property ที่ไม่ระบุขอบเขตอาจผิดตั้งแต่ requirement เช่น “โอนแล้วเงินรวมเท่าเดิม” ต้องบอกว่ามี fee หรือไม่และนับเงินของใครบ้าง

## 3. Invariant กับ property test ไม่เหมือนกันเสียทีเดียว

Invariant คือกฎของ state ที่ต้องจริงเสมอ; property test คือวิธีตรวจข้อความของกฎผ่าน inputs
Property อาจเปรียบหลาย operations เช่นฝากแล้วถอนจำนวนเท่ากันกลับยอดเดิม เมื่อสถานะ/ยอดทำให้ทั้งสอง operation สำเร็จ
ความสัมพันธ์นี้เรียก **metamorphic relation** คือเปลี่ยน input/ขั้นตอนแล้วรู้ว่าผลควรสัมพันธ์กันอย่างไร แม้ไม่ได้ระบุผลลัพธ์ทุกค่าเป็นตัวเลขไว้ล่วงหน้า

Test หลายกรณีช่วยหาหลักฐานว่ากฎถูก แต่ตัวอย่างที่มีจำนวนจำกัดไม่ใช่ mathematical proof สำหรับ input ทั้งหมด
ต้องรักษาทั้ง construction tests, example tests ที่อ่านง่าย และ property tests ตามหน้าที่ของแต่ละแบบ

## 4. เริ่มด้วย loop ที่อ่านออก

ตัวอย่างวางใน DomainTest ของ checkpoint ที่มี helpers `id` และ `money` อยู่แล้ว; เพิ่ม import kotlin.test.assertTrue:

```kotlin
val original = Account(id("a"), Balance.zero, AccountStatus.Active)
for (wholeUnits in 1..100) {
    val amount = money("$wholeUnits.00")
    val after = original.deposit(amount)
    assertTrue(after.balance.value >= original.balance.value)
    assertEquals(BigDecimal("0.00"), original.balance.value)
}
```

`1..100` คือช่วงเลข 1 ถึง 100; ทุก iteration ใช้ original เดิมเพื่อแยกกรณี ไม่สะสมยอดแล้วทำให้ cases พึ่งกันโดยไม่ตั้งใจ
`money` ต้องผ่าน smart constructor ของจริง เพราะ property นี้เริ่มจาก valid amounts
Construction tests สำหรับ 0/negative/จำนวนตำแหน่งทศนิยมเกินกฎ เป็นอีกชุดหนึ่ง ไม่ปนเข้า generator ที่อ้างว่า valid

## 5. จาก loop ไป generator/shrinking

เมื่อเพิ่ม input ranges ให้มีหน่วยย่อย 0.01, เท่าขอบยอด, และใกล้ daily limit ตามกฎที่มีจริง
ถ้าใช้ random ให้ seed หรือกลไกรายงาน input ซ้ำได้ **Seed** คือค่าเริ่มที่ทำให้ลำดับสุ่มเกิดซ้ำ
คอร์สนี้ loop deterministic จึงทดลองซ้ำได้ก่อนเพิ่มเครื่องมือ

ตัวอย่าง shrinking ด้วยมือ: failure เกิดเมื่อ balance1000 ถอน 1001 ลองลดเป็น balance1 ถอน 2 แล้วยัง fail หรือไม่?
Counterexample ที่เล็กทำให้เห็นว่าใช้เครื่องหมายเทียบผิดหรือลืม guard โดยไม่จมกับ fixture ใหญ่
ต้อง shrink ให้ยังอยู่ในเงื่อนไขของ property ไม่ลด PositiveMoney ไปเป็น 0 แล้วอ้างว่าเจอ domain operation bug

## 6. Impact และ guided exercise

Impact: property ช่วยชี้ว่ากฎคลุมเครือตรงไหนก่อนเขียน test และจับ edge cases ที่ทีมไม่ได้คิดเป็นตัวอย่างไว้
Trade-off: ข้อความ property/generator ที่ผิดทำให้ test ให้ความมั่นใจผิด และ failure จำนวนมากต้องมีวิธีลดกรณีให้เข้าใจ

เปิด `Module09Exercise.kt` แล้วทำ:

1. เปลี่ยน TODO เป็นประโยค property ที่ระบุ input assumptions
2. เลือกฝากไม่ลดยอด, ถอน reject ไม่เปลี่ยนข้อมูล, หรือโอนรักษายอดรวม
3. เขียน example test สั้นหนึ่งกรณีเพื่อให้คนอ่านเห็น intent
4. สร้าง loop อย่างน้อย 50 กรณีที่ valid ผ่าน smart constructors
5. เมื่อ test fail ให้บันทึก input และหา counterexample เล็กที่สุดด้วยมือ
6. ลองทำ implementation ผิดชั่วคราว เช่นหักปลายทางแทนบวก แล้วดูว่า test จับได้; คืน implementation เดิมก่อนส่ง

สิ่งที่ส่ง: property/assumptions + generator + test + บันทึก counterexample
ผ่านเมื่อ test อ่านเป็น business rule ได้และไม่คำนวณ expected ด้วย helper ตัวเดียวกับสิ่งที่กำลังทดสอบจนตรวจอะไรไม่ได้

## 7. Homework

เขียน properties สำหรับ daily limit (ต้องเพิ่ม policy/usage state ถ้าจะ implement จริง) และ terminal transaction
เพิ่ม property ฝากแล้วถอนจำนวนเท่ากันคืนยอดเดิม พร้อมระบุสถานะที่อนุญาตทั้งสองขั้น
ออกแบบ generator ของ Account โดยไม่สร้าง balance/status ที่ผิดกฎ และอธิบายเหตุผลของขอบเขต

## 8. Checkpoint (10 คะแนน)

1. Example test กับ property ต่างกันอย่างไร (2)
2. ทำไม generator ที่อ้างว่า valid ต้องผ่าน constructor ของจริง (2)
3. โอนรักษายอดรวมต้องมี assumptions อะไรอย่างน้อยสองข้อ (2)
4. Shrinking ช่วย debug อย่างไร (2)
5. Loop100 กรณีพิสูจน์ได้ไหมว่า function ถูกทุก input และ seed มีประโยชน์อะไร (2)

ผ่านที่ 8/10 พร้อม test ที่จับ rule violation ได้จริง

## 9. สำหรับผู้สอน

เริ่มจาก example100 ฝาก 25 แล้วถาม “อะไรที่ควรจริงไม่ว่าเงินฝากเป็นเลขใดที่ valid?”
ให้ทีมอธิบาย counterexample เป็นภาษาของงาน ไม่บอกเพียงว่า assertEquals แดง
Discussion: property นี้กว้างเกินไปไหม? generator หนีกรณียากหรือไม่? เพิ่ม input มากแต่ assertion เดิมผิดจะช่วยไหม?

ถัดไป: [Module 10 — ต่อผลลัพธ์ด้วย Option/Either](module-10-option-either.md)
