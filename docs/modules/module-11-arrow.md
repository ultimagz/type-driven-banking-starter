# Module 11 — เรียนพื้นฐานก่อนประยุกต์ Arrow

Branch: `module-11-arrow` • prerequisite: Modules 03, 06, 07, 09, 10 • เวลา: 2 sessions × 90 นาที
เป้าหมาย: อธิบายสิ่งที่ไลบรารีทำได้จากหลักการที่รู้แล้ว และใช้มันต่อ workflow เดิมโดยรักษา business rules

## 1. Concept สำหรับผู้สอน: จากสิ่งที่ทำเองไปสู่ไลบรารี

บท 10 ใช้ `Option` และ `Either` ที่เขียนเอง บทนี้เพิ่มคำว่า Functor/Monad ให้กับรูปแบบการใช้งานที่ทีมเคยเห็นแล้ว:

| สิ่งที่รู้แล้ว | แนวคิด | API ที่ใช้ |
|---|---|---|
| เปลี่ยนค่าข้างในโดยยังมี context เดิม | Functor | `map` |
| ผลลัพธ์ของขั้นถัดไปมี context เช่นกัน | Monad | `flatMap` |
| ยกค่าธรรมดาเข้าบริบท success | pure / unit | `Either.Right(value)`, `Some(value)` |
| หยุดเมื่อเกิด expected failure | short-circuit | `Left`, `bind()` |
| ไม่มีค่าเป็นเรื่องปกติ | optionality | `Option` |
| ต้องแยกสาเหตุที่ล้มเหลว | typed error | `Either<Error, A>` |

“Context” ในบทนี้หมายถึงผลสำเร็จ/ล้มเหลวหรือมีค่า/ไม่มีค่า ไม่ใช่ Android Context
Functor และ Monad เป็นคุณสมบัติของวิธี compose พร้อมกฎที่ต้องรักษา ไม่ใช่ชื่อ class ที่จำเป็นต้องสร้าง
Arrow ปัจจุบันให้ใช้ `Option`, `Either`, operators และ Raise DSL ได้โดยตรง ไม่ต้องประกาศ `Functor<F>`/`Monad<F>` หรือ higher-kinded types เพื่อทำแบบฝึกนี้

## 2. Gate: พื้นฐานก่อนเปิด Arrow API (20 นาที)

ปิดเอกสารไลบรารี แล้วทำบนกระดาษหรือกับ data types ที่เขียนเอง:

1. วาดรูปผลลัพธ์ของ `Right(1).map { Right(it + 1) }`: ได้ context ซ้อนกัน
2. วาด `Right(1).flatMap { Right(it + 1) }`: เหลือ context ชั้นเดียว
3. อธิบายว่า lambda ของ `Left("stop").flatMap(...)` ทำงานหรือไม่
4. อ่าน `src/main/kotlin/io/typebanking/DataTypes.kt` และ trace `when` ทีละแขน
5. ระบุว่า failure ของสองขั้นจะใช้ error channel ร่วมกันอย่างไร

ให้ทีมผ่านก่อนเปิด syntax `either { bind() }`
Starter บทนี้มี checkpoint domain ที่ทำสำเร็จจากบทก่อนให้แล้ว เพื่อไม่ให้โจทย์ Arrow ผูกกับ TODO เก่าของผู้เรียนคนอื่น
ผู้เรียนยังเก็บ branch งานของตนจาก M10 เพื่อเทียบ implementation ได้ ส่วน primitive baseline อยู่ใน package legacy

### Functor laws

สำหรับ `map` ที่ไม่ทำ side effect:

```text
identity:    context.map { it } == context
composition: context.map(f).map(g) == context.map { g(f(it)) }
```

ใช้ตัวอย่าง `Either<String, Int>`: `Right(5)` และ `Left("invalid")`
กฎต้องถือสำหรับทั้งสองฝั่ง โดย `map` เปลี่ยน Right และรักษา Left เดิม

### Monad laws

ให้ `pure(a) = Either.Right(a)`, `f: A -> Either<E, B>`, `g: B -> Either<E, C>`:

```text
left identity:  pure(a).flatMap(f) == f(a)
right identity: m.flatMap(::pure) == m
associativity:  m.flatMap(f).flatMap(g) == m.flatMap { f(it).flatMap(g) }
```

ความสำคัญเชิงงานจริง: ย้ายวงเล็บหรือแยก helper เพื่อ refactor workflow โดยยังรักษาผลลัพธ์
ถ้า lambda ทำ logging/สุ่ม/แก้ shared state การให้เหตุผลด้วยกฎเหล่านี้จะซับซ้อนขึ้น
Tests ของคอร์สใช้ตัวอย่างจำนวนหนึ่งเพื่อสำรวจ laws ไม่ใช่ข้อพิสูจน์สำหรับ input ทุกค่าที่เป็นไปได้

### จุดทบทวน type safety

`flatMap` ไม่ควรแปลง error channel ด้วย unchecked cast เช่น `error as OtherError`
บทนี้ใช้ handwritten extension ที่รักษา error type; เมื่อเปลี่ยนชนิด error ต้อง map เป็นข้อมูลใหม่อย่างชัดเจน
นี่เป็น review point ที่ทำให้เห็นว่าการดูแล primitive combinators เองต้องรับผิดชอบมากกว่าการเขียนให้ compile ได้

## 3. Problem และ impact

Requirement ใหม่: เพิ่ม **transfer preview** ให้รู้ยอดหลังโอนและ event ที่จะเกิด โดยยังไม่บันทึกบัญชี
รับ `RawTransfer(sourceId, destinationId, amount)` เป็นข้อความจาก boundary
Reuse `AccountId.parse`, `PositiveMoney.parse`, `decideTransfer` จาก Mini Banking System เดิม

ลำดับที่ตกลงร่วมกัน:

```text
parse source ID -> parse destination ID -> parse amount
  -> lookup source -> lookup destination -> decideTransfer
```

เมื่อ input ผิด ต้องไม่ query บัญชี
เมื่อ source หาไม่พบ ต้องไม่ query destination
เมื่อ business rule ปฏิเสธ ต้องคืน reason เดิม
เมื่อสำเร็จ คืน account ใหม่และ event โดยไม่ mutate/save account เดิม

ผลที่ต้องเห็น: Arrow ลด code compose และ combinators ที่เราต้องดูแลเอง
ไลบรารีไม่สามารถเลือก invariant, error taxonomy, aggregate boundary หรือ transaction strategy ให้เราได้

## 4. Dependency และ before/after

บท 00–10 ยังเป็น snapshot เดิม ไม่มี Arrow
บท 11 pin `io.arrow-kt:arrow-core:2.2.3`, Kotlin 2.2.20, JDK 21, Gradle Wrapper 8.14.4
ใช้เฉพาะ Core จึงไม่ต้องเพิ่ม Optics compiler plugin, coroutine effects หรือ annotation processing
รายละเอียดการติดตั้งอ้างอิง [Arrow setup](https://arrow-kt.io/learn/quickstart/setup/)

```kotlin
// Before: ใช้ constructor/type ที่ดูแลเอง
fun parseId(raw: String): io.typebanking.Either<ConstructionError, AccountId>

// After: ใช้ bridge ที่ขอบเขต โดย rule เดิมไม่เปลี่ยน
fun parseId(raw: String): arrow.core.Either<ConstructionError, AccountId> =
    AccountId.parse(raw).toArrow()
```

`arrowlesson/ArrowBridge.kt` ใช้ exhaustive `when` แปลง types ไม่ใช้ cast
เก็บ domain เดิมไว้ช่วยเปรียบก่อน/หลัง; ไม่ต้องย้ายทั้งระบบเพื่อเริ่มใช้ไลบรารี
Import alias ช่วยให้อ่านแยก handwritten Either และ Arrow Either ได้

เมื่อ operation ขั้นถัดไป fail ได้ ใช้ `flatMap`; เมื่อแปลง success value ธรรมดา ใช้ `map`
ใช้ `mapLeft` แปลง construction error ไปเป็น error ของ workflow โดยรักษาสาเหตุไว้
เมื่อเทียบโครง compose แล้วจึงเปิด Raise DSL:

```kotlin
// รูปแบบเทียบเคียงของขั้นที่มี error type ร่วมกัน
first().flatMap { a -> second(a).map { b -> summarize(b) } }

// รูปแบบเดียวกันใน either scope
either {
    val a = first().bind()
    val b = second(a).bind()
    summarize(b)
}
```

`bind()` รับ Right ไปใช้ต่อและหยุด scope เมื่อพบ Left
`either` ในแบบฝึกนี้ยังคืน `Either<PreviewError, Success>`; ไม่ได้เปลี่ยนไปใช้ exception เป็น public business contract
ดูการแปลรูปแบบนี้ใน [From Either to Raise](https://arrow-kt.io/learn/typed-errors/from-either-to-raise/)

## 5. Project structure ที่เพิ่ม

```text
io/typebanking/
  Account.kt, Types.kt, DataTypes.kt       # domain checkpoint / handwritten types
  TransferUseCase.kt                      # boundary จากบทก่อน
  arrowlesson/
    ArrowBridge.kt                       # adapter + AccountLookup port (ให้แล้ว)
    PreviewModels.kt                     # input + typed errors (ให้แล้ว)
    TransferPreview.kt                   # 4 TODO สำหรับผู้เรียน
tests/arrowlesson/
  ArrowBridgeTest.kt                      # ตรวจ data mapping
  ArrowLawsTest.kt                        # laws และ short-circuit
  ArrowExerciseTest.kt                    # acceptance tests ของ workflow
```

## 6. Exercises ต่อเนื่อง

### Exercise 1 — Parsing และ mapLeft

เติม `parseAmount`: แยกข้อความที่ไม่ใช่ decimal ออกจาก decimal ที่ผิด invariant
ใช้ smart constructor เดิม: amount > 0 และ scale <= 2
อย่าใช้ `Double`, rounding หรือ `abs()` เพื่อทำ invalid input ให้ผ่านโดยเงียบ ๆ

Acceptance: `"oops"` → AmountNotDecimal, `"0"`/`"-1"`/`"1.001"` → AmountInvalid, `"25"` → 25.00

### Exercise 2 — Option → required Either

เติม `requireAccount` ด้วย `Option.fold`
การไม่มีบัญชีอาจเป็น Option ที่ repository แต่สำหรับ transfer ต้องมี จึงต้องแปลง None เป็น SourceMissing หรือ DestinationMissing ตาม caller
Some ต้องคืน account เดิม ไม่สร้างบัญชีค่า default

### Exercise 3 — Compose ด้วย flatMap

เติม `TransferPreview.withFlatMap` ตามลำดับ parse → lookup → decide
ใช้ error channel `PreviewError` ให้ชัด, ไม่ nested `Either`, ไม่ unchecked cast
อ่าน helper ที่ให้แล้วเพื่อ reuse domain และเรียนรู้ว่า Arrow ไม่ได้แทน business rule

### Exercise 4 — Translate เป็น Raise DSL

เติม `withRaise` ด้วย `either { ...bind() }` โดยใช้ helpers เดียวกัน
ห้ามเปลี่ยนลำดับ query และ semantics ของ failure
ใช้ tests เดิมตรวจทั้งสอง implementation ก่อนลด code ให้สั้นลง

## 7. วิธีรันและเกณฑ์สำเร็จ

ตั้ง JDK 21 เป็น Gradle JVM ใน IDE หรือกำหนด JAVA_HOME เป็น JDK 21 ที่ติดตั้งไว้
ไม่ต้องติดตั้ง Gradle แยกเมื่ออยู่ branch นี้:

```bash
git switch module-11-arrow
./gradlew test
./gradlew exerciseTest
```

Starter: `test` ต้องผ่านตั้งแต่เริ่ม; `exerciseTest` ตั้งใจแดงจนเติม TODO
Solution: ทั้งสองคำสั่งต้องผ่าน
Acceptance tests มี tag exercise แยกจาก baseline เพื่อให้ทีมเห็น regression กับงานที่ยังทำไม่เสร็จชัดเจน
ใช้ fake lookup ตรวจ short-circuit และยืนยันว่า preview ไม่ save
แบบฝึกใช้ fixed Instant และเปรียบค่าที่สังเกตได้ของยอดเงิน/event
Unexpected database exception ต้องยัง propagate; อย่าเปลี่ยนทุก Throwable เป็น SourceMissing

## 8. Homework

1. **Migration spike:** เปลี่ยน factory ของ value object หนึ่งตัวให้คืน Arrow Either โดยตรง เขียน tests เดิมผ่าน และเทียบว่า bridge ถูกลบได้แค่ไหน
2. **Independent validation:** ออกแบบหน้า form ให้สะสม input errors ด้วย `zipOrAccumulate` หรือ `mapOrAccumulate`; เขียน test ว่า source ID, destination ID, amount ผิดพร้อมกันแล้วรายงานครบ แต่ยังไม่มี lookup
3. **Team decision note:** เขียน ADR 1 หน้า: ทำไมเลือก Arrow, dependency อยู่ layer ไหน, ทีมดูแลเวอร์ชันและ readability อย่างไร, เมื่อใด nullable/Kotlin Result เพียงพอ
4. **Law counterexample:** เขียน map ที่ผิดกฎ identity หรือ composition พร้อม test ที่จับได้ แล้วอธิบายการแก้

ข้อ 2 เป็น extension แยกจาก fail-fast preview: independent checks สะสมได้ แต่ dependent steps เช่น load หลัง parse ยังต้องมีค่าที่ valid
อ่าน [Arrow validation](https://arrow-kt.io/learn/typed-errors/validation/) แล้วออกแบบ expected behavior ก่อนเลือก syntax

## 9. Checkpoint test (ก่อนเปิดเฉลย)

1. ทำนาย type ของ `Right(1).map { Right(it + 1) }` และต่างจาก flatMap อย่างไร (2 คะแนน)
2. อธิบาย Functor laws สองข้อ และยกตัวอย่าง Left (2 คะแนน)
3. อธิบาย Monad laws สามข้อด้วย workflow ไม่ใช่ท่องศัพท์ (3 คะแนน)
4. ถ้า source ไม่พบ destination lookup ทำงานหรือไม่? ต้องเขียน test แบบใด? (2 คะแนน)
5. None ต่างจาก Left(SourceMissing) ตรงคำถามที่ API ตอบอย่างไร? (2 คะแนน)
6. mapLeft กับการ cast error ต่างกันอย่างไร? (2 คะแนน)
7. library รับผิดชอบอะไร และทีมยังต้องออกแบบ business rules อะไรเอง? (2 คะแนน)
8. กรณีไหนต้อง fail-fast และกรณีไหนควรสะสม errors? (2 คะแนน)
9. แสดงว่าการย้าย flatMap เป็น bind ยังรักษายอดรวมและไม่มีการ save (3 คะแนน)

ผ่านที่ 16/20 พร้อม exercise tests ทุกข้อผ่าน
ข้อ 3, 4, 9 ต้องอธิบายได้; คะแนนจากข้ออื่นไม่ใช้ชดเชยความเข้าใจ short-circuit ที่ยังผิด

## 10. Discussion prompts และ facilitator timing

Session A: 20 นาที gate, 20 นาที map/flatMap + laws, 15 นาที bridge review, 30 นาที exercise 1–3, 5 นาที reflection
Session B: 15 นาที recall, 20 นาที Raise translation, 25 นาที exercise 4 + tests, 20 นาที checkpoint/review, 10 นาที homework

ถามทีม:
- ถ้าลบ Arrow ออก กฎบัญชีของเรายังอยู่ตรงไหน?
- nested flatMap หรือ either block อ่านง่ายกว่าสำหรับ workflow นี้ เพราะอะไร?
- Shorter code ทำให้เราลืม handling ของ error variant ไหนได้หรือไม่?
- เปลี่ยน None เป็น account default แล้วเสีย invariant อะไร?
- ถ้า callback มี side effects การ rearrange chain ยังปลอดภัยแค่ไหน?
- ทีมควร expose Arrow ใน public domain API หรือเก็บใน application boundary?

เปิด `docs/modules/module-11-arrow-solutions.md` ใน solution repo หลัง checkpoint
