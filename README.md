# Type-Driven Domain Modeling with Kotlin — Learner Starter

รีโปสำหรับผู้เรียนของคอร์สทีม 10–12 สัปดาห์ ใช้ Mini Banking System เดียวเพื่อเห็นการเปลี่ยนจาก primitive code เป็น domain model ที่ type ช่วยป้องกันข้อผิดพลาด

## อ่านจากพื้นฐานก่อนเริ่มแบบฝึก

ถ้ายังไม่รู้จักศัพท์ออกแบบ เริ่มที่ [Kotlin และวิธีใช้ repo](docs/GETTING_STARTED.md) แล้วตาม [สารบัญ 12 บท](docs/COURSE_INDEX.md) แต่ละบทอธิบายคำใหม่ด้วยตัวอย่างง่ายก่อนเชื่อมกับโค้ดธนาคาร มี before/after, แบบฝึกทีละขั้น, คำใบ้, homework และ checkpoint พร้อมเกณฑ์ผ่าน

เปิด [ศัพท์กลาง](docs/GLOSSARY.md) เมื่อเจอคำไม่คุ้น, [คู่มือผู้เรียน](docs/LEARNER_GUIDE.md) สำหรับวิธีทำโจทย์/อ่าน test และ [คู่มือผู้สอน](docs/FACILITATOR_GUIDE.md) สำหรับแผน session/คำถาม/จุดที่มักสับสน เอกสารชุดนี้อยู่ทุก module branch แต่โค้ดยังเปลี่ยนตามบท ให้ checkout branch ให้ตรงโจทย์

## เริ่มต้น

บน `main`/`module-11-arrow` ใช้ JDK 21 และ Gradle Wrapper ที่ให้ไว้แล้ว:

```bash
./gradlew test
./gradlew exerciseTest
```

แต่ละ branch คือ **จุดเริ่มต้นของโมดูล** มี skeleton/TODO และโค้ด checkpoint ให้เรียนตามโจทย์ บาง snapshots มีแนวคิดของบทหลังอยู่ก่อนแล้ว ให้โฟกัส exercise ของบทปัจจุบัน Tests ในบท 00–10 เป็นชุดตั้งต้น ไม่ได้ตรวจทุก requirement ต้องเพิ่ม tests ตามเอกสาร; บท 11 แยก baseline กับ acceptance suite ชัดเจน ใช้ solution หลังทำ checkpoint และอธิบายเหตุผลแล้ว

บท 11: `test` เป็น baseline ที่ผ่านแล้ว; `exerciseTest` ตั้งใจแดงจนเติม 4 TODO ใน `TransferPreview.kt`. อ่าน [Module 11 — Arrow](docs/modules/module-11-arrow.md). JDK 27 ที่ติดตั้งเป็น default ต้องเปลี่ยน Gradle JVM/JAVA_HOME เป็น JDK 21 ก่อนรัน Wrapper รุ่นนี้.

Branches 00–10 เก็บ snapshots เดิมและยังไม่มี Wrapper/Arrow: ใช้ Gradle 8.10+ กับ JDK 21 เพื่อเรียนตามบท แล้วกลับมาที่ `module-11-arrow` เมื่อผ่านพื้นฐาน Option/Either.

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
| 11 | `module-11-arrow` | Functor/Monad laws → Arrow Option/Either → Raise DSL |

ดูภาพรวม branch ที่ [docs/BRANCH_MAP.md](docs/BRANCH_MAP.md), รูปแบบการเรียนที่ [docs/LEARNER_GUIDE.md](docs/LEARNER_GUIDE.md), และเอกสารสอนที่ `docs/modules/`.

## กติกาการทำแบบฝึก

1. อ่าน `concept` และ `problem` ในเอกสารโมดูล
2. ทำ TODO โดยไม่เปลี่ยน public tests
3. เขียน/ปรับ test ของตัวเองอย่างน้อยหนึ่งกรณีก่อนดูเฉลย
4. ตอบ checkpoint โดยอ้างถึง code ของทีม
5. เก็บ homework แยก branch ของตน เช่น `learner/alex/module-03`

## Scripts

`./scripts/check-branch.sh` แสดง branch และ command ถัดไป. `./scripts/reset-module.sh` คืนเฉพาะไฟล์ที่โมดูลประกาศไว้จาก branch ปัจจุบัน (คำสั่งนี้ทิ้งการแก้ไขไฟล์เป้าหมาย จึงให้ commit หรือ stash ก่อน).
