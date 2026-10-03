# Module 01 — Value Object: ค่าที่บอกความหมายและรักษากฎของตัวเอง

เคยเห็น function รับ String หลายตัวแล้วไม่รู้ว่าตัวไหนหมายถึงอะไรไหม? บทนี้ทำให้ “รหัสบัญชี” เป็นมากกว่าข้อความหนึ่งก้อน

ก่อนเรียน: ระบุ rule จาก [บท 00](module-00-primitive-obsession.md) ได้ และอ่าน nullable/private จาก [บทเตรียม](../GETTING_STARTED.md)
หลังเรียน: อธิบาย value object, invariant, constructor, smart constructor และสร้างค่า valid ผ่านทางที่กำหนดได้
เวลา: 2 sessions × 90 นาที • Branch: `module-01-value-objects`

## 1. Concept: เริ่มจากความหมาย ไม่ใช่คำศัพท์

ลองเทียบเลข 100 สองตัว: อายุ 100 ปี กับเงิน 100 บาท ถึงเก็บเป็นเลขได้เหมือนกัน แต่กฎและการใช้งานไม่เหมือนกัน
**Domain type** คือชนิดข้อมูลที่เราเพิ่มเพื่อบอกความหมายของงาน เช่น `AccountId` หรือ `PositiveMoney`
ชื่อ type ทำให้คนและ compiler แยกการใช้งานได้ แทนที่จะจำจากตำแหน่ง parameter อย่างเดียว

**Value object** คือ object ที่เราสนใจ “ค่าที่มันแทน” มากกว่า identity ของ object นั้น
รหัสบัญชี `a-1` สองก้อนควรเท่ากันเมื่อข้อความรหัสเหมือนกัน แม้ถูกสร้างคนละครั้ง
ต่างจาก Account ที่เป็นบัญชีหนึ่งบัญชี: เปลี่ยนยอดแล้วก็ยังเป็นบัญชีเดิม เราจะเรียกสิ่งที่มี identity ว่า entity ในบท DDD

Value object ที่ดีมีชื่อที่สื่อความหมาย มีกฎชัดเจน และโดยปกติไม่เปิดให้แก้ค่าหลังสร้าง
การใส่ข้อมูลใน class เฉย ๆ ยังไม่พอ ถ้า `PositiveMoney(-100)` ยังสร้างได้ ชื่อกับความจริงของ object จะขัดกัน

## 2. Invariant คืออะไร

Invariant คือเงื่อนไขที่ต้องจริงสำหรับค่าหรือ object ทุกตัวที่เราประกาศว่า valid
สำหรับ `AccountId`: หลังสร้างแล้วค่าไม่ว่าง สำหรับ `PositiveMoney`: หลังสร้างแล้วจำนวนเงิน > 0 และมีทศนิยมไม่เกิน 2 ตำแหน่ง
ไม่ใช่แค่ตรวจ input ครั้งหนึ่ง แต่ต้องทำให้ช่องทางสร้าง/เปลี่ยนค่าทั้งหมดรักษากฎเดียวกันด้วย

กฎ “ถอนเงินได้เมื่อ balance พอ” ยังไม่ใช่ invariant ของ PositiveMoney เพราะค่าจำนวนเงินตัวเดียวไม่รู้ยอดบัญชี
นี่คือเหตุผลที่แยกกฎของ “ค่า” ออกจากกฎของ “operation” เราไม่สามารถย้ายทุก if ไปอยู่ constructor ได้ทั้งหมด

## 3. Constructor และ smart constructor ต่างกันอย่างไร

**Constructor** คือทางสร้าง instance เช่น `AccountId("a-1")` โดยเรียกชื่อ class
**Private constructor** คือการปิดทางนั้นจากคนภายนอก คนอื่นจึงต้องใช้ function ที่เราเปิดไว้
**Smart constructor** เป็นชื่อแนวคิดของ function สร้าง object ที่ตรวจเงื่อนไขก่อน เช่น `create` หรือ `parse`; ไม่ใช่ keyword ของ Kotlin

ตัวอย่างเล็กที่เรียนแยกจากไฟล์ production:

```kotlin
class AccountId private constructor(val value: String) {
    companion object {
        fun create(raw: String): AccountId? {
            val normalized = raw.trim()
            if (normalized.isEmpty()) return null
            return AccountId(normalized)
        }
    }

    override fun equals(other: Any?): Boolean =
        other is AccountId && value == other.value

    override fun hashCode(): Int = value.hashCode()
}
```

อ่านทีละส่วน: `private constructor` ปิดการสร้างตรง ๆ; `val` ปิดการ assign ค่าใหม่; `companion object` เป็นที่วาง function ที่เรียกผ่านชื่อ class
`AccountId?` แปลว่าอาจได้ AccountId หรือ null; null ในตัวอย่างนี้หมายถึงสร้างไม่ได้
`trim()` เอาช่องว่างหัวท้ายออก; เราตกลงกฎ normalization นี้ไว้ก่อน ไม่ควรเดาว่าต้อง uppercase/lowercase ด้วย

`equals` เปรียบเทียบค่าข้างใน; `hashCode` ต้องสอดคล้องกันเพื่อให้ใช้ใน Set/Map ถูกต้อง
Kotlin data class ช่วยสร้าง value equality ได้ แต่ constructor/copy ต้องไม่เปิดช่องให้ข้าม invariant จึงไม่เลือกเพียงเพราะเขียนสั้น

```kotlin
val id = AccountId.create(" a-1 ")
if (id != null) {
    println(id.value) // a-1
}
```

อย่าเริ่มด้วย `!!`: เครื่องหมายนี้บอกให้ Kotlin เชื่อว่าไม่ null แต่ถ้าผิดจะ throw ตอนรัน
ตอนนี้ null เป็นสะพานเริ่มต้น บท 03/10 จะเพิ่มผลลัพธ์ที่บอกสาเหตุ ถ้า branch เฉลยใช้ `Either` แล้ว ให้อ่านเป็น “สำเร็จหรือผิด” ก่อนเรียนวิธี compose

## 4. Parse boundary แปลว่าอะไร

**Raw data** คือข้อมูลที่ยังไม่ผ่านกฎ เช่นข้อความจากช่องกรอกหรือ JSON
**Parse boundary** คือจุดแปลง raw data เป็นค่าที่โปรแกรมไว้ใจได้ หรือรายงานว่าทำไมสร้างไม่ได้
คำว่า parse ในที่นี้รวมการตรวจความหมาย ไม่ใช่เพียงแปลง String เป็นตัวเลข

```text
ข้อความจากผู้ใช้ → อ่านเป็น decimal → ตรวจว่าเป็นบวก/มีทศนิยมไม่เกิน 2 ตำแหน่ง → PositiveMoney
```

`"abc"` อ่านเป็น decimal ไม่ได้ ส่วน `"-5"` อ่านได้แต่ไม่ใช่ PositiveMoney
หลังสร้าง PositiveMoney สำเร็จ function ที่รับ type นี้ไม่ต้องตรวจ amount>0 ซ้ำ ยกเว้นขอบที่รับข้อมูลดิบใหม่ เช่น deserialize จาก storage

## 5. Problem, impact และ before/after

Before: `deposit(account, BigDecimal("-5"))` เรียกได้ ต้องหวังว่าฟังก์ชันตรวจเอง
After: `deposit(account, amount: PositiveMoney)` ต้องมี object ที่ผ่าน construction ก่อน
Compiler ช่วยป้องกันการส่ง BigDecimal ธรรมดาเข้ามา ส่วนการตัดสินว่าเลขที่อ่านจาก input >0 ยังเป็น runtime check ที่ boundary
อย่าพูดว่าตัวเลขผิดทั้งหมดถูกจับตอน compile-time: สิ่งที่เพิ่มขึ้นคือการจำกัดทางสร้างและชนิดค่าที่ operation ยอมรับ

Impact: rule ของจำนวนเงินมีเจ้าของแห่งเดียว ชื่อ signature อ่านแล้วรู้ความหมาย และ tests ของ rule อยู่ใกล้ type ที่รับผิดชอบ
Trade-off: มี code เพิ่มและต้องคิดเรื่อง equality/normalization จึงเลือกสร้าง type เมื่อมันช่วยสื่อหรือรักษากฎจริง ๆ

## 6. Guided exercise — สร้างค่าอย่างเป็นขั้นตอน

ใน starter เปิด `Module01Exercise.kt`: functions ที่คืน `Any` เป็นช่องให้ผู้เรียนออกแบบ type เอง ไม่ใช่ API ปลายทางที่แนะนำ

1. เขียนกฎของ AccountId เป็นประโยคและตัวอย่างก่อน เช่น blank ไม่ผ่าน
2. สร้าง private constructor และ factory ที่คืน nullable เป็นเวอร์ชันแรก
3. ให้ `parseAccountId` เรียก factory; ปรับ return type ให้เจาะจง ไม่คง `Any`
4. สร้าง PositiveMoney ด้วย BigDecimal: ต้อง >0 และ `scale() <= 2`
5. ตรวจทุก construction path และเขียน tests ของขอบเขต
6. เพิ่ม AccountName ยาว 3–100 ตัวอักษรหลัง trim และ TransactionId ที่ไม่ blank; ตกลง normalization ให้ชัด

คำใบ้: สร้างด้วย `BigDecimal("1.20")` จากข้อความ ไม่ใช้ `BigDecimal(1.2)` จาก Double
คอร์สนี้ตรวจจำนวนตำแหน่งทศนิยม (scale) ของค่าที่รับมา: `1.230` มี scale=3 จึงปฏิเสธ แม้ค่าเชิงตัวเลขเท่ากับ 1.23; หากเปลี่ยน policy ต้องแก้ทั้ง rule/test
อย่าสับสนกับ BigDecimal.precision() ซึ่งนับจำนวนหลักสำคัญทั้งหมด เช่น 123.45 มี precision=5 แต่ scale=2
อย่าใช้ round เพื่อทำ input ผิดให้ผ่านโดยไม่ถามความหมายของงาน

| Input | คาดหวัง | เหตุผล |
|---|---|---|
| `" a-1 "` | AccountId a-1 | trim ตามกติกา |
| `"   "` | สร้างไม่ได้ | รหัสว่าง |
| money `0`, `-1` | สร้างไม่ได้ | ไม่บวก |
| money `0.01`, `10.00` | ผ่าน | บวกและจำนวนตำแหน่งทศนิยมอนุญาต |
| money `1.001` | สร้างไม่ได้ | เกินสองตำแหน่ง |

ผ่านเมื่อทั้ง input ที่ถูก/ผิดมี tests, ไม่เปิด constructor public และอธิบายกฎของแต่ละ type ได้
Automated tests ที่มีอยู่ใน branch ไม่ได้ตรวจ requirement ใหม่ทั้งหมด ให้เพิ่ม tests ของตนตามตาราง ไม่ใช้เพียง test เดิมสีเขียวตัดสินว่าเสร็จ

## 7. Homework

ออกแบบ DailyTransferLimit, Percentage, Email บนกระดาษ: ความหมาย, invariant, normalization, input ที่ผ่าน/ไม่ผ่าน และผลเมื่อสร้างไม่ได้
Percentage อนุญาต 0 และ 100 หรือไม่? Email ต้อง validate ระดับใด? ให้เขียนสมมติฐาน ไม่เริ่มจาก regex ที่ไปจำมา
เลือกหนึ่ง type มาลงมือเขียน พร้อมอย่างน้อย 4 boundary cases

## 8. Checkpoint (10 คะแนน)

1. อธิบาย invariant กับ smart constructor ด้วยภาษาคนละหนึ่งประโยค (2)
2. เหตุใด private constructor อย่างเดียวไม่พอ ถ้าภายหลังมี setter ที่รับค่าผิด (2)
3. PositiveMoney ต่างจากยอดเงินที่อาจเป็นศูนย์อย่างไร (2)
4. สิ่งใดถูกตรวจตอนรัน และสิ่งใด compiler ช่วยป้องกันหลังสร้างสำเร็จ (2)
5. Value object สองก้อนเท่ากันด้วยอะไร และควรตรวจ equality อย่างไร (2)

ผ่านที่ 8/10 พร้อม construction tests ครบ ถ้าติดข้อ 1 ให้ย้อนหัวข้อ 2–3 ไม่ต้องรีบอ่าน ADT

## 9. สำหรับผู้สอน

เริ่มจาก “100 เป็นเงินหรืออายุ?” แล้วค่อยตั้งชื่อ domain type
ให้ learner เล่นบทคนเขียน caller ลองหาทางสร้าง invalid object; ผู้สร้าง type ต้องอธิบายว่าปิดทางนั้นอย่างไร
Discussion: ชื่อ Money กว้างไปไหม? trim เป็นกฎของใคร? ค่าที่โหลดจาก DB ต้องผ่าน constructor หรือไม่?
อ่านเพิ่ม: [Kotlin classes/constructors](https://kotlinlang.org/docs/classes.html)

ถัดไป: [Module 02 — ข้อมูลที่ต้องมีร่วมกัน กับทางเลือกของสถานะ](module-02-adt.md)
