# Module 03 — Typed Error: บอกผลที่ไม่สำเร็จให้คนเรียกอ่านออก

ถอนเงินไม่สำเร็จเพราะเงินไม่พอ กับโปรแกรมมี bug ควรทำให้ caller เห็นเหมือนกันหรือไม่? บทนี้แยกสองเรื่องนั้นให้ชัด

ก่อนเรียน: สร้าง value object ได้และอ่าน sealed/when จาก [บท 02](module-02-adt.md)
หลังเรียน: ออกแบบผลสำเร็จ/ปฏิเสธเป็นข้อมูล และเขียน caller ที่จัดการทุกทางเลือก
เวลา: 2 sessions × 90 นาที • Branch: `module-03-typed-errors`

## 1. Concept: คนเรียกต้องรู้ว่าเกิดอะไรได้บ้าง

**Caller** คือโค้ดที่เรียก function เช่นหน้าถอนเงินที่เรียก `withdraw`
**Return type** คือชนิดค่าที่ function คืน ใน `fun label(...): String` ส่วน String บอกว่าคนเรียกจะได้รับข้อความ
**Typed error** คือการระบุข้อผิดพลาดเป็นชนิดข้อมูลที่มีชื่อ เช่น InsufficientFunds ไม่ใช่เพียงข้อความ “invalid”

ลองนึกถึงใบตอบรับคำขอ: อาจ “สำเร็จ” หรือ “ปฏิเสธเพราะเงินไม่พอ” caller จะเลือกข้อความหรือขั้นถัดไปได้จากใบตอบรับนี้
การปฏิเสธไม่จำเป็นต้องเป็นความผิดปกติของโปรแกรม ธนาคารตรวจยอดและปฏิเสธอย่างถูกกฎได้ทุกวัน

**Expected failure** คือผลไม่สำเร็จที่ workflow ตั้งใจรองรับ เช่น เงินไม่พอ/บัญชีถูกระงับ
**Unexpected failure** คือความผิดปกติที่ contract นี้ไม่ได้ตั้งใจใช้เป็นทางเลือกปกติ เช่น bug หรือข้อมูลภายในพัง
การจัดประเภทต้องดูบริบท: timeout อาจเป็น expected error ของ API integration ได้ แต่ไม่ควรกลายเป็นกฎ “บัญชีเงินไม่พอ” ของ domain

## 2. Problem: exception ที่ไม่มีความหมายเพียงพอ

Before จากโค้ดเดิม:

```kotlin
fun withdraw(account: Account, amount: PositiveMoney): Account {
    if (!account.balance.canCover(amount)) {
        throw IllegalStateException("invalid")
    }
    return account.copy(balance = account.balance.subtract(amount))
}
```

signature บอกเพียง “คืน Account” ไม่บอกผลอื่น คนเรียกจึงต้องอ่าน implementation หรือเดาจาก exception
การ parse ข้อความ exception เพื่อแยกเหตุผลเปราะบาง: แค่เปลี่ยน wording ก็อาจทำให้ behavior ของ UI เปลี่ยน
โค้ดข้างบนเป็นภาพปัญหาเพื่ออ่าน ไม่ใช่ implementation ครบทุก rule ของคอร์ส

## 3. After: ใช้ ADT เป็นใบตอบรับ

```kotlin
sealed interface WithdrawResult {
    data class Success(val account: Account) : WithdrawResult
    data object InsufficientFunds : WithdrawResult
    data object Frozen : WithdrawResult
    data object Closed : WithdrawResult
}
```

อ่านว่า outcome เป็น Success OR InsufficientFunds OR Frozen OR Closed
Success ต้องมี Account ใหม่ ส่วนเหตุผลปฏิเสธแต่ละแบบมีชื่อชัดเจน ไม่ต้องสร้างข้อความปลอมเพื่อให้ caller เดา
ใน snapshot เฉลยชื่ออาจเป็น `WithdrawOutcome` และจัดกลุ่ม `Rejected`; รูปแบบเดียวกันคือ success กับ failure ที่มีชื่อ

```kotlin
fun message(result: WithdrawResult): String = when (result) {
    is WithdrawResult.Success -> "ถอนสำเร็จ"
    WithdrawResult.InsufficientFunds -> "ยอดเงินไม่พอ"
    WithdrawResult.Frozen -> "บัญชีระงับชั่วคราว"
    WithdrawResult.Closed -> "บัญชีปิดแล้ว"
}
```

`is` ใช้ตรวจ variant ที่เป็น class และอาจมีข้อมูล; data object ใช้ชื่อสถานะตรง ๆ
คนเขียน domain ไม่ใส่ข้อความ UI ภาษาไทยไว้ใน error type: type บอกเหตุผล ส่วน caller เลือกข้อความตามบริบท/ภาษา

## 4. Impact และเรื่องที่ยังต้องตัดสินใจ

เมื่อมีผลลัพธ์ชัด caller ใช้ exhaustive when และ compiler ช่วยเตือนเมื่อเพิ่ม failure ใหม่
Tests ตรวจชนิดผลลัพธ์ได้ ไม่ต้องรอ exception หรือเปรียบข้อความที่อาจเปลี่ยน
ข้อแลกเปลี่ยนคือ caller ต้องจัดการผลให้ครบ จะปล่อย failure ทิ้งโดยไม่บอกผู้ใช้ก็ยังทำได้ถ้าเราเขียน caller ผิด

กำหนดลำดับตรวจให้ชัดด้วย: ถ้าบัญชี Frozen และเงินไม่พอพร้อมกัน จะรายงานอะไร?
คอร์สใช้สถานะก่อนยอดเงินเพื่อให้เหตุผลสำคัญต่อ workflow ตรงกัน เรื่องนี้เป็น business decision ไม่ได้ถูกเลือกให้โดย sealed class

Typed errors ไม่ได้หมายความว่าห้าม throw ทุกแห่ง Bug หรือ infrastructure failure อาจยัง propagate ไปให้ shell/logging จัดการ
ถ้าต้อง map technical failure ที่ boundary ให้ทำ mapping เฉพาะสิ่งที่รู้จัก ไม่ catch ทุก Throwable แล้วเรียกทั้งหมดว่า InsufficientFunds

## 5. Guided exercise — ออกแบบผลของ withdrawal/transfer

เปิด `Module03Exercise.kt` ใน starter:

1. เขียน success กับเหตุผลปฏิเสธก่อนเขียน method
2. ปรับ placeholder ที่คืน Any ให้คืน WithdrawResult/Outcome ที่เจาะจง
3. ใช้บัญชีจากบทก่อนและ amount ที่ผ่าน construction แล้ว ไม่ตรวจ positivity ซ้ำใน operation
4. ตรวจ Frozen/Closed แล้วตรวจเงินพอ; failure ต้องไม่เปลี่ยนข้อมูลเดิม
5. เขียน caller แบบ when ไม่มี else ให้เหตุผลแต่ละแบบเป็นข้อความ
6. เพิ่ม transfer: SameAccount, source unavailable, destination unavailable, funds insufficient

DailyLimitExceeded เป็นโจทย์ขยาย: ต้องกำหนดข้อมูลยอดใช้วันนี้และ policy ก่อนเพิ่มผลลัพธ์ ไม่ใส่ variant แล้วอ้างว่าระบบตรวจ daily limit แล้ว

Tests อย่างน้อย: active+เงินพอ success, active+เงินไม่พอ, frozen, closed, และ failure ทุกแบบรักษายอดเดิม
สิ่งที่ส่ง: error taxonomy (ตารางเหตุผล/คนจัดการ) + implementation + caller + tests
ผ่านเมื่อ signature บอกผลทั้งหมดที่ business flow ตั้งใจรองรับ และ caller ไม่เดาจาก exception message

## 6. Homework

ออกแบบ login ที่เดิม throw WrongPassword/AccountLocked ให้คืน typed result
ระบุข้อมูลที่ success ต้องมีและข้อมูลที่ UI ไม่ควรเปิดเผย เช่น policy เรื่องข้อความ username/password ผิดให้ตอบเหมือนกันหรือไม่
เลือกหนึ่ง infrastructure error และบอกว่าจะเก็บไว้ที่ adapter หรือแปลงใน application boundary เพราะอะไร

## 7. Checkpoint (10 คะแนน)

1. Expected failure ต่างจาก bug อย่างไร ยกตัวอย่างธนาคาร (2)
2. เขียน ADT ผลถอนเงินพร้อม success และเหตุผลอย่างน้อยสามแบบ (3)
3. ทำไม UI ไม่ควรตัดสินใจจาก String “invalid” (1)
4. failure ต้องคืน account ที่ถูกหักไปครึ่งหนึ่งหรือไม่ เพราะอะไร (2)
5. timeout เป็น domain error เสมอไหม อธิบายโดยใช้บริบท (2)

ผ่านที่ 8/10 พร้อม tests ของผลสำเร็จและปฏิเสธ หากยังติด when ให้ย้อนบท 02

## 8. สำหรับผู้สอน

ให้ผู้เรียนสลับบทบาท caller/domain: ฝั่ง caller ต้องใช้แค่ signature เลือกข้อความ โดยไม่เปิด method
ถาม “ถ้า fail นี้เกิดขึ้น เราต้องทำอะไรต่อ?” แล้วดูว่าชนิด error ให้ข้อมูลพอหรือไม่
Discussion: rule order สำคัญไหม? UI text เป็นหน้าที่ของใคร? อะไรควร log เป็นเหตุผิดปกติ?

ถัดไป: [Module 04 — ให้ข้อมูลแต่ละสถานะอยู่ในแบบที่ถูกต้อง](module-04-illegal-states.md)
