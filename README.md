# Type-Driven Domain Modeling with Kotlin — Learner Starter

รีโปสำหรับผู้เรียนของคอร์สทีม 10–12 สัปดาห์ ใช้ Mini Banking System เดียวเพื่อเห็นการเปลี่ยนจาก primitive code เป็น domain model ที่ type ช่วยป้องกันข้อผิดพลาด

## เริ่มต้น

ต้องมี JDK 21+ และ Gradle 8.10+ (หรือเพิ่ม Gradle Wrapper ของทีม) แล้วรัน:

```bash
gradle test
git switch module-00-primitive-obsession
```

แต่ละ branch คือ **จุดเริ่มต้นของโมดูล**: มี production code ตั้งแต่บทก่อนหน้า, TODO ที่ต้องทำ, tests ที่บอกพฤติกรรมที่ต้องทำให้ผ่าน และ `docs/modules` สำหรับอ่านก่อนลงมือ. อย่าเปิด solution ระหว่างทำ; ใช้เฉลยหลัง facilitator debrief เท่านั้น.

## เส้นทาง

| โมดูล | Branch | ผลลัพธ์ |
|---|---|---|
| 00 | `module-00-primitive-obsession` | หา rule และ smell |
| 01 | `module-01-value-objects` | smart constructor / parse boundary |
| 02 | `module-02-adt` | product/sum type |
| 03 | `module-03-typed-errors` | expected failure เป็น data |
| 04 | `module-04-illegal-states` | transaction states ถูกต้องตาม type |
| 05 | `module-05-state-machine` | explicit transitions |
| 06 | `module-06-functional-core` | pure rules, injected time |
| 07 | `module-07-clean-boundary` | ports/use case/adapters boundary |
| 08 | `module-08-ddd` | aggregate/events/ubiquitous language |
| 09 | `module-09-property-thinking` | invariant-oriented tests |
| 10 | `module-10-option-either` | compose absence/failure |

ดูภาพรวม branch ที่ [docs/BRANCH_MAP.md](docs/BRANCH_MAP.md), รูปแบบการเรียนที่ [docs/LEARNER_GUIDE.md](docs/LEARNER_GUIDE.md), และเอกสารสอนที่ `docs/modules/`.

## กติกาการทำแบบฝึก

1. อ่าน `concept` และ `problem` ในเอกสารโมดูล
2. ทำ TODO โดยไม่เปลี่ยน public tests
3. เขียน/ปรับ test ของตัวเองอย่างน้อยหนึ่งกรณีก่อนดูเฉลย
4. ตอบ checkpoint โดยอ้างถึง code ของทีม
5. เก็บ homework แยก branch ของตน เช่น `learner/alex/module-03`

## Scripts

`./scripts/check-branch.sh` แสดง branch และ command ถัดไป. `./scripts/reset-module.sh` คืนเฉพาะไฟล์ที่โมดูลประกาศไว้จาก branch ปัจจุบัน (คำสั่งนี้ทิ้งการแก้ไขไฟล์เป้าหมาย จึงให้ commit หรือ stash ก่อน).

