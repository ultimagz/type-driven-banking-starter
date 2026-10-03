# เส้นทางเรียนสำหรับผู้เริ่มต้น

เริ่มที่ [Kotlin และวิธีใช้ repo](GETTING_STARTED.md) และใช้ [ศัพท์กลาง](GLOSSARY.md) เป็นตัวช่วยระหว่างบท
ทุกบทเริ่มจากปัญหา/ตัวอย่างง่ายก่อนศัพท์ใหม่ แล้วค่อยเชื่อมกับ Mini Banking System

| บท | คำถามที่ตอบ | เอกสาร / branch |
|---|---|---|
| 00 | อ่านโค้ดแล้วรู้ได้อย่างไรว่ากฎอยู่ตรงไหน | [Primitive obsession](modules/module-00-primitive-obsession.md) / module-00-primitive-obsession |
| 01 | ทำให้ค่าที่ส่งเข้ามามีความหมายและผ่านกฎอย่างไร | [Value objects](modules/module-01-value-objects.md) / module-01-value-objects |
| 02 | อะไรต้องมีร่วมกัน และอะไรต้องเลือกอย่างหนึ่ง | [Product/Sum/ADT](modules/module-02-adt.md) / module-02-adt |
| 03 | caller จะรู้เหตุผลที่ไม่สำเร็จได้อย่างไร | [Typed errors](modules/module-03-typed-errors.md) / module-03-typed-errors |
| 04 | ทำให้ fields ที่ขัดกันไม่มีทางอยู่ด้วยกันอย่างไร | [Illegal states](modules/module-04-illegal-states.md) / module-04-illegal-states |
| 05 | แต่ละ state ไปต่อทางไหนได้บ้าง | [State machine](modules/module-05-state-machine.md) / module-05-state-machine |
| 06 | ทดสอบกฎโดยไม่ต้องเรียกโลกจริงอย่างไร | [Functional core](modules/module-06-functional-core.md) / module-06-functional-core |
| 07 | ใคร load ใครคิด ใคร save และ contract อยู่ไหน | [Clean boundary](modules/module-07-clean-boundary.md) / module-07-clean-boundary |
| 08 | คำของงานและเจ้าของกฎควรอยู่กับอะไร | [DDD](modules/module-08-ddd.md) / module-08-ddd |
| 09 | test มากกว่าตัวอย่างเดียวโดยยังตรวจ rule จริงอย่างไร | [Property thinking](modules/module-09-property-thinking.md) / module-09-property-thinking |
| 10 | ต่อขั้นที่อาจไม่มีค่า/ผิดโดยไม่ซ้อน if อย่างไร | [Option/Either](modules/module-10-option-either.md) / module-10-option-either |
| 11 | ใช้ library เมื่อเข้าใจ operators/laws แล้วอย่างไร | [Arrow](modules/module-11-arrow.md) / module-11-arrow |

บท 00–10 ไม่มี Arrow ให้คิด/ทำ handwritten ก่อน บท 11 มี library และ tests แยก baseline/exercise
Code snapshots อาจมีชื่อ API ต่างจากตัวอย่างสอนเล็กๆ ให้ดูบริบทของ code block และ reuse types ของ branch ที่ checkout
เอกสารชุดใหม่อยู่ทุก module branch เพื่อให้ผู้เรียนไม่ต้องไป main เพื่อค้นศัพท์; code ในแต่ละ branch ยังเป็น checkpoint เดิม

## วิธีใช้บทหนึ่ง

1. เช็ก “ก่อนเรียน” ถ้ายังไม่ได้ให้ย้อนบทที่ link ไว้
2. อ่านตัวอย่างและทายผลด้วยมือก่อนรัน
3. ทำ guided exercise ทีละข้อ โดยเขียน expected input/output ก่อน implementation
4. เขียน tests ตามกรณีในบท ไม่ใช้เพียง existing tests ที่เขียวอยู่ตัดสินว่าเสร็จ
5. ตอบ checkpoint ด้วยตัวอย่างของตน แล้วค่อย compare กับ solution
6. ทำ homework แยก branch และบันทึก policy/assumptions ที่โจทย์ไม่ได้กำหนด
