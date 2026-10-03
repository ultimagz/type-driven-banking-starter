# Module 10 — Option/Either: ต่อหลายขั้นโดยยังเห็นความหมายของ “ไม่มี” กับ “ผิด”

เมื่อ parse → load account → decide แต่ละขั้นอาจไม่สำเร็จ เราจะต่อขั้นเหล่านี้โดยไม่ทำ if ซ้อนจนอ่านไม่ออกได้อย่างไร?

ก่อนเรียน: ADT/typed error/pure functions จาก [บท 02](module-02-adt.md), [บท 03](module-03-typed-errors.md), [บท 06](module-06-functional-core.md)
หลังเรียน: อ่าน generic types, แยก Option/Either, และเขียน map/flatMap แบบ handwritten ก่อนใช้ Arrow
เวลา: 2 sessions × 90 นาที • Branch: `module-10-option-either`

## 1. Concept: คำถามต่างกันต้องการคำตอบต่างกัน

ถาม “มีชื่อเล่นไหม?” ไม่มีชื่อเล่นเป็นคำตอบปกติ ไม่ต้องมี error reason
ถาม “โอนเงินสำเร็จไหม?” ถ้าไม่สำเร็จ caller ต้องรู้ว่า source missing หรือเงินไม่พอ
**Option<A>** มี Some(value) OR None; ใช้แทนการมีหรือไม่มีค่าที่ตั้งใจออกแบบไว้
**Either<E, A>** มี Left(error) OR Right(value); ใช้แทน failure ที่มีชนิดชัดเจนหรือ success value

ตัว A/E เป็นชื่อแทนชนิดข้อมูล ไม่ใช่ค่าจริง **Generic type** คือ type ที่ทำงานกับชนิดข้อมูลที่ระบุภายหลัง
Option<Account> หมายถึงเมื่อมีค่า ค่านั้นเป็น Account; Either<ConstructionError, AccountId> หมายถึงซ้ายเป็น ConstructionError ขวาเป็น AccountId
ธรรมเนียมในคอร์ส: Left เป็น failure และ Right เป็น success; ชื่อนี้ไม่ได้แปลว่า left คือ “ผิด” ตามภาษาทั่วไป

Nullable `Account?` ใช้ได้ใน Kotlin ไม่จำเป็นต้องเปลี่ยนทุก nullable เป็น Option
Option มีประโยชน์เมื่ออยากให้ “มี/ไม่มี” เป็นชนิดที่ใช้ operators ร่วมกันชัดเจน ส่วน Either ต้องการข้อมูลเหตุผลของ failure
Kotlin Result ใช้ Throwable เป็น failure; business errors ที่เราอยากจำกัดชุดสาเหตุด้วย sealed types อาจเหมาะกับ Either มากกว่า

## 2. อ่าน handwritten types ก่อนดูไลบรารี

```kotlin
sealed interface Option<out A> {
    data class Some<A>(val value: A) : Option<A>
    data object None : Option<Nothing>
}

sealed interface Either<out E, out A> {
    data class Left<E>(val error: E) : Either<E, Nothing>
    data class Right<A>(val value: A) : Either<Nothing, A>
}
```

นี่คือ sum types ที่รู้จักจากบท 02: เลือกหนึ่งแบบจากสองแบบ
`Nothing` ใน subtype บอกว่าไม่มีค่าของช่องนั้น เช่น Left ไม่มี success value; ไม่ต้องสร้างค่าปลอมของ A
`out` เป็นการบอกความสัมพันธ์ของ generic types ในทางที่อ่าน/ผลิตค่าได้ สำหรับบทนี้อ่าน definition และ trace เมื่อพบ Some/None ก่อน ยังไม่ต้องพิสูจน์ variance
หากอยากทบทวน Kotlin generic syntax ดู [ศัพท์กลาง](../GLOSSARY.md)

## 3. Problem: function หนึ่งสำเร็จแต่อีก function อาจผิด

ถ้า parse สำเร็จจึงจะ load ได้ ถ้า load ไม่พบจึงไม่ควรตัดสินใจถอนจากบัญชี default
Before: if result!=null, if อีกตัว!=null, try/catch, then mutate; caller ต้องจำ semantics แต่ละช่อง
After: ใช้ชื่อของผลลัพธ์และ operators ให้แต่ละขั้นส่งผลไปต่ออย่างมีความหมาย
สิ่งสำคัญคือ failure ต้องหยุด dependent work ไม่ใช่แค่ทำให้ code สั้น

## 4. map: เปลี่ยนค่าข้างใน แต่คงรูปของผล

**Lambda** คือ function ที่เขียนเป็นค่าหรือส่งเข้าอีก function เช่น `{ number -> number + 1 }`
**Higher-order function** คือ function ที่รับ function หรือคืน function; map รับสิ่งที่ทำกับ success value

ตัวอย่าง extension ที่ใช้กับ handwritten Either:

```kotlin
fun <E, A, B> Either<E, A>.mapValue(f: (A) -> B): Either<E, B> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> Either.Right(f(value))
    }
```

`(A) -> B` อ่านว่า function รับ A แล้วคืน B; ถ้าเจอ Left ไม่เรียก f และรักษา error เดิม
Right(10).mapValue { it+1 } เป็น Right(11) ส่วน Left("stop") ยังเป็น Left("stop")
คำว่า **right-biased** หมายถึง operators อย่าง map/flatMap ทำงานกับ Right เป็นหลัก ไม่ใช่กับ error side

## 5. flatMap: ขั้นถัดไปอาจคืนผลสำเร็จ/ผิดอีกครั้ง

ถ้า f คืน Either อยู่แล้ว การใช้ map จะได้ Either ซ้อนอีกชั้น:

```text
Right(10).mapValue { Right(it + 1) } → Right(Right(11))
Right(10).flatMap { Right(it + 1) }  → Right(11)
```

flatMap เรียก f ในกรณี Right แล้วใช้ผล Either จาก f โดยตรง แทนการห่อ Right เพิ่ม

```kotlin
fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> f(value)
    }
```

**Short-circuit** คือหยุดการต่อ dependent steps เมื่อได้ failure เช่น parse ผิดแล้วไม่ load บัญชี
ถ้าสองขั้นมี error คนละชนิด ให้แปลงเข้าชนิดร่วมอย่าง explicit ไม่ใช้ `error as OtherError`
Snapshot เดิมอาจมี flatMap prototype ที่ใช้ unchecked cast; ให้อ่านเป็นจุดวิจารณ์และแก้ตาม signature ที่ปลอดภัยข้างบน แบบแก้มีในบท 11 ด้วย
**Unchecked cast** คือการบอกให้เชื่อว่าเป็นชนิดอื่นโดยไม่มีหลักฐานเพียงพอ ไม่ใช่การเปลี่ยน error ข้อมูลเดิมให้เป็นแบบใหม่จริง ๆ

## 6. Option → Either: เมื่อ “ไม่มี” เป็น failure ของงานนี้

Repository find อาจคืน None ตามปกติ แต่ Transfer ต้องมี source จึงแปลง None เป็น SourceMissing
Some(account) → Right(account); None → Left(SourceMissing)
นี่คือการเพิ่มความหมายตามคำถามของ use case ไม่ใช่ประกาศว่า None ทุกแห่งเป็น error

อย่าแปลงหาไม่พบเป็น account ยอด 0: นั่นสร้างข้อมูลที่ business ไม่ได้ให้มา และทำให้ error หลงไปเป็น “เงินไม่พอ”
ใน Arrow บท 11 เราจะใช้ fold เพื่อแปลงสองแขน; ตอนนี้เริ่มจาก exhaustive when เองได้

## 7. Impact และ guided exercise

Impact: ลด null checks ซ้ำและรักษาชุด failure ที่ caller รู้จัก; tests ตรวจว่าขั้นต่อไปไม่ได้ทำงานเมื่อมี Left ได้
Trade-off: wrappers/generics/lambdas เป็นศัพท์ใหม่ ต้องอ่านแต่ละ type ก่อนพยายามเขียน chain ยาว และไม่ต้องใช้ abstraction นี้ทุก function

เปิด `Module10Exercise.kt`:

1. Implement Option.map/flatMap และ Either.map/flatMap ด้วย when
2. เขียน tests Some/None และ Right/Left ก่อนต่อ workflow
3. ให้ parse → lookup → decision ใช้ error type ร่วมที่มีเหตุผล ไม่ใช้ Any หรือ Throwable ครอบทุกอย่าง
4. เขียน chain สองขั้นด้วย flatMap แล้วใช้ map เพื่อแปลงผลสุดท้ายที่ fail ไม่ได้
5. ใส่ fake lookup/counter เพื่อพิสูจน์ว่า parsefail ไม่ query และ sourcefail ไม่ query destination
6. เปรียบ chain กับ when/early return เดิม แล้วอธิบายว่ากฎธุรกิจอยู่ที่เดิมอย่างไร

คำใบ้: ถ้า return type เริ่มเป็น Either<E, Either<E, A>> ให้ถามว่าขั้นนี้ควรใช้ flatMap แทน map หรือไม่
ผ่านเมื่อ trace ได้ทั้ง success/failure, error ไม่ถูก cast ทิ้ง และ caller จัดการผลได้ครบ

## 8. Homework

เลือกระหว่าง nullable/Option/Either สำหรับ nickname ที่ไม่จำเป็น, account ที่ transfer ต้องมี, malformed amount, DBconnection พัง
เหตุผลต้องอิงคำถามของ API ไม่ใช่เลือก Either ทุกกรณีเพราะดู functional
เพิ่ม helpers fold/mapError ให้ handwritten types และเขียน tests ของทั้งสองแขน

## 9. Checkpoint (10 คะแนน)

1. อ่าน Either<ConstructionError, AccountId>เป็นภาษาคน (2)
2. None ต่างจาก Left(SourceMissing)อย่างไร (2)
3. map ที่คืน Either ทำให้เกิด type อะไร และ flatMap ต่างอย่างไร (2)
4. Left ควรเรียก lambda ขั้นถัดไปไหม และตรวจด้วย test อย่างไร (2)
5. ทำไม casterror ไม่เท่ากับ maperror (2)

ผ่านที่ 8/10 พร้อม handwritten operators tests ก่อนเปิด librarysyntax

## 10. สำหรับผู้สอน

ใช้กล่องกระดาษติดป้าย Some/None/Right/Left แล้วส่งค่าผ่านทีละ function
ให้ผู้เรียนคนหนึ่งเป็น lambda และดูว่า Left ควรเรียกเขาหรือไม่
Discussion: ไม่มีค่าเป็นคำตอบปกติหรือเป็นปัญหาของงานนี้? ขั้นไหน fail ได้? error type ร่วมบอกเหตุผลพอไหม?
อ่าน syntax เพิ่ม: [Kotlin lambdas](https://kotlinlang.org/docs/lambdas.html)

ถัดไป: [Module 11 — พื้นฐาน Functor/Monad แล้วค่อยประยุกต์ Arrow](module-11-arrow.md)
