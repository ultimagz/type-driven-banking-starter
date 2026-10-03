# เริ่มเรียนเมื่อยังไม่คล่อง Kotlin หรือศัพท์ออกแบบ

ใช้เอกสารนี้ก่อน Module 00 หากเห็นโค้ดแล้วไม่รู้ว่าต้องอ่านส่วนไหน ไม่ต้องเคยเรียน Functional Programming หรือ DDD มาก่อน
เป้าหมายของคอร์สคือ “บอกกฎของงานด้วยข้อมูลและ API” ส่วนศัพท์เป็นชื่อที่ช่วยคุยกันเมื่อเข้าใจปัญหาแล้ว

## 1. การเรียนกับ repo

Repository คือโฟลเดอร์โค้ดที่ Git เก็บประวัติ ส่วน branch คือจุดประวัติหนึ่งที่เราเลือกดู/ทำงานต่อได้
Starter คือโจทย์; solution คือแนวทางที่ทำแล้ว; tests คือโค้ดตรวจพฤติกรรม ไม่ใช่แทนคำอธิบายทั้งหมด

```bash
git clone https://github.com/ultimagz/type-driven-banking-starter.git
cd type-driven-banking-starter
git switch module-00-primitive-obsession
```

ถ้า clone มาก่อนรอบอัปเดตเอกสาร ให้ `git fetch origin` แล้วอัปเดต branch ของตนหลัง commit/stash งานก่อน
อ่าน [COURSE_INDEX.md](COURSE_INDEX.md) เลือกบทตามลำดับ ไม่จำเป็นต้องอ่าน solution ก่อนทำ
แต่ละบทมี “ก่อนเรียน / หลังเรียน / problem / ตัวอย่าง / guided exercise / homework / checkpoint”

เช็กเครื่องมือก่อนเริ่มด้วย `java -version` และ `gradle --version` สำหรับบท 00–10 ใช้ JDK 21 และ Gradle 8.10 ขึ้นไปที่รองรับ JDK นี้ ถ้า IDE เลือก Java คนละตัวกับ terminal ให้ตั้ง Gradle JVM ของโปรเจกต์เป็น 21 ด้วย
บน main/บท 11 มี Gradle Wrapper ซึ่งดาวน์โหลด Gradle รุ่นที่ repo กำหนดให้ครั้งแรก จึงไม่ต้องติดตั้ง Gradle แยก แต่ยังต้องมี JDK 21 หากติดปัญหา setup ให้ผู้สอนช่วยก่อนเริ่มแบบฝึก ไม่ต้องแก้ business code เพื่อให้เครื่องมือเริ่มทำงาน

โค้ดใน Markdown มีทั้งตัวอย่างที่ลองแยกได้และ snippets ที่อ้าง types ของบทก่อน ให้ดูข้อความก่อน code block
อย่าคัดลอก class ชื่อเดิมไปเพิ่มซ้ำใน package เดียวกัน ควรทดลองใน scratch file/package แยกหรือปรับ type ที่มีอยู่ตามโจทย์

## 2. อ่าน function signature

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}
```

`fun` ประกาศ function, `add` เป็นชื่อ, วงเล็บเป็น inputs, `: Int` หลังวงเล็บคือผลที่คืน
`add(2, 3)` ส่ง 2 และ 3 เข้าไปแล้วได้ 5; `return` จบ function พร้อมค่าที่ให้ caller
รูปย่อ `fun add(a: Int, b: Int): Int = a + b` มีความหมายเดียวกันสำหรับตัวอย่างนี้
`Unit` คือไม่มีค่าผลลัพธ์ที่ caller ต้องใช้ ไม่ได้บอกว่าข้างในไม่มี side effects

ฝึกสองนาที: เปลี่ยนชื่อเป็น subtract แล้วทายผลก่อนรัน; อธิบาย input/output ด้วยภาษาคน

## 3. Class, object, field และ constructor

```kotlin
data class Person(val name: String, var age: Int)
val person = Person("Pat", 20)
person.age = 21
```

Class เป็นแบบของข้อมูล Object/instance คือข้อมูลหนึ่งก้อนที่สร้างตามแบบ; fields/properties คือช่องข้อมูล
Constructor คือทางสร้าง object เช่น Person(...)
`val` บอกว่า property/ตัวแปรนั้น assign ใหม่ไม่ได้; `var` เปลี่ยนได้; `val person` ไม่กันการแก้ person.age ที่เป็น var
Data class ให้เครื่องมืออย่าง value equality และ copy; ไม่ได้ validate business rules ให้เอง

ลอง `val next = person.copy(age = 22)` จะได้ object ใหม่ แต่ fields ที่เป็น objects ซ้อนอาจยังแชร์กัน เพราะ copy เป็น shallow copy
**Shallow** หมายถึงไม่สร้าง objects ข้างในใหม่ทุกชั้นตามไปด้วย จึงต้องดูว่า fields นั้น mutable หรือไม่

## 4. Nullable และ if/when

```kotlin
fun nameLength(name: String?): Int {
    if (name == null) return 0
    return name.length
}
```

เครื่องหมาย `?` หลัง type อนุญาต null; null หมายถึงไม่มีค่า ต้องกำหนดความหมายใน API ว่าไม่มีเพราะอะไร
หลัง if ตรวจ null แล้ว Kotlin รู้ว่าช่วงถัดไป name เป็น String ที่มีค่า จึงอ่าน length ได้
`!!` คือยืนยันว่ามีค่าโดยให้ throw ถ้าไม่มี ไม่ควรใช้เพื่อหนีการคิดว่า null ต้องจัดการอย่างไร
`when` เลือกแขนตามค่าหรือชนิด เช่นตรวจว่า outcome เป็น Success หรือ Failure เราจะใช้จริงในบท 02–03

## 5. BigDecimal สำหรับจำนวนเงิน

BigDecimal เป็นเลข decimal ที่ไม่ใช้การแทนค่าทศนิยมแบบ binary ของ Double เหมาะกับกฎเงินของตัวอย่างนี้
สร้างจากข้อความ เช่น `BigDecimal("0.10")`; ไม่เริ่มจาก Double แล้วคิดว่าความคลาดเคลื่อนจะหายเอง
`scale()` บอกจำนวนตำแหน่งทศนิยมที่เก็บ; `1.20`มี scale2, `1.200`มี scale3
คอร์สกำหนดจำนวนตำแหน่งทศนิยมของ input (scale) ไม่เกินสองตำแหน่งสำหรับ PositiveMoney ไม่ใช่กฎสากลของเงินทุกระบบ
BigDecimal.precision() เป็นคนละเรื่อง: นับจำนวนหลักสำคัญทั้งหมด เช่น 123.45 มี precision=5 และ scale=2

```kotlin
import java.math.BigDecimal

val balance = BigDecimal("100.00")
val amount = BigDecimal("25.00")
val remaining = balance - amount // 75.00
```

เมื่อใช้ equals/assertEquals กับ BigDecimal ให้รู้ว่า scale มีผล: 1.0 กับ 1.00 อาจไม่เท่ากันด้วย equals แม้ compareTo มองตัวเลขเท่ากัน
Tests จึงควรใช้ policy scale ของ domain ให้สอดคล้องกัน หรือเปรียบค่าตามความหมายที่ตั้งใจ

## 6. Lambdas และ generic types สำหรับบท 10–11

```kotlin
val numbers = listOf(1, 2, 3)
val doubled = numbers.map { number -> number * 2 } // [2, 4, 6]
```

Block ในปีกกาเป็น lambda ที่ส่งให้ map; number คือ input ของ function นี้
ถ้ามี parameter หนึ่งตัว เราเขียน `{ it * 2 }`ได้ โดย it หมายถึง input ตัวนั้น
`(Int) -> String` อ่านว่า function รับ Int แล้วคืน String; ไม่ใช่ค่าของ String ที่มีลูกศรอยู่ในข้อมูล
`Box<A>` คือ class ที่ใช้ชื่อ A แทน type ที่ caller ระบุ เช่น Box<Int>; Option<Account>ใช้ความคิดเดียวกัน

ไม่ต้องอ่าน Functor/Monad laws ก่อนทำนาย map บน List ง่ายๆได้ แล้วค่อยเรียนการ map ใน context success/failure

## 7. Tests และคำว่าเขียว/แดง

Tests มักใช้สามช่วง: Arrange เตรียมข้อมูล → Act เรียกสิ่งที่ตรวจ → Assert ตรวจความคาดหวัง
`@Test`บอก runner ว่า function นี้เป็น test; assertEquals(expected, actual)ตรวจผล; failure คือสิ่งที่ได้ไม่ตรงความคาดหวัง
Test เขียวไม่ได้แปลว่าทุก businessrule ถูก หาก test ยังไม่ได้ตรวจ rule นั้น เราต้องเพิ่ม coverage ตามโจทย์

Branches00–10 เป็น snapshots เดิม ใช้ JDK21 กับ Gradle ที่ติดตั้งเพื่อรัน `gradle test`; ยังไม่มี Wrapper ทุก branch
ต้นๆ starter มี placeholders ที่คืน Any/TODO และ tests ยังไม่ครอบคลุมแบบฝึกทั้งหมด ให้ปรับ signature และเพิ่ม tests ตามบท
Some early solution snapshots ยังไม่มี test files ของบทนั้นครบ; อ่าน checkpoint ด้วย ไม่ถือว่าคำสั่งไม่พบ tests คือผ่านทั้งคอร์ส

บน main/บท 11 มี Wrapper แล้ว ใช้ JDK21 และ:

```bash
./gradlew test
./gradlew exerciseTest
```

Baseline `test`ต้องผ่าน; ใน starter บท 11 `exerciseTest`ตั้งใจแดงจนเติม TODO ส่วน solution ผ่านทั้งสองชุด
Tests เหล่านี้ตรวจบท 11/ส่วน domain ที่มันใช้ ไม่ได้อ้างว่าครอบคลุมทุก requirement ทุกบทที่ผ่านมา

## 8. ถ้าติดให้หยุดตรงไหน

อ่านโค้ดไม่ออก → กลับหัวข้อ 2–6 แล้ว trace ทีละ input
รู้ศัพท์แต่เริ่มแบบฝึกไม่ถูก → เปิด guided exercise ทำข้อแรก และเขียน expected ก่อน implementation
Test แดง → แยก compile error, TODO ที่ยังไม่ทำ, หรือ businessbehavior ผิด แล้วอ่าน expected/actual
Checkpoint ตอบไม่ได้ → ย้อนหัวข้อ concept ของบทนั้นและยกตัวอย่างของตน ไม่ต้องท่อง definition ภาษาอังกฤษ

อ่านเพิ่มตามต้องการ: [Kotlin classes](https://kotlinlang.org/docs/classes.html), [null safety](https://kotlinlang.org/docs/null-safety.html), [lambdas](https://kotlinlang.org/docs/lambdas.html)
