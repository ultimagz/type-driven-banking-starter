# Module 00 — Smell the domain

**Concept.** Primitive obsession คือการใช้ `String`, `BigDecimal`, Boolean และ nullable fields แทนค่าที่มีความหมายต่างกันใน domain. Compiler จึงช่วยแยก account id, money และ status ไม่ได้.

**Problem.** `BankingService.kt` รับ `BigDecimal` ที่อาจเป็นศูนย์หรือติดลบ, status เป็นข้อความใดก็ได้ และแก้ account ที่ส่งเข้ามาโดยตรง. จงทำ rule-hunting ก่อน refactor.

**Impact.** Validation ซ้ำ, caller รู้ failure ตอน runtime, และ state ผิดหลุดถึง persistence ได้.

**Before → after.** วันนี้ยังเป็น `withdraw(Account, BigDecimal): Unit`; บทต่อไปจะเป็น operation ที่รับ domain types และคืนค่าใหม่/ผลลัพธ์ชัดเจน.

**Exercise.** ทำตาราง: rule, classification (input/domain/workflow), owner, และ invalid example อย่างน้อย 8 แถว. เพิ่ม requirement “ห้ามโอนเข้าบัญชีตัวเอง” ในเอกสาร design เท่านั้น.

**Homework.** วิเคราะห์ `Payment(amount: Double, currency: String, status: String)` อย่างน้อย 5 smells.

**Checkpoint.** เพราะเหตุใด `AccountId(" ")` ที่สร้างได้จึงเป็นปัญหา? กฎ `amount > 0` ควรอยู่ทุก method หรือที่ construction boundary?

**Discussion prompts.** “String สองตัวนี้สลับกันได้ไหม?” “กฎใดเป็นสิ่งที่ไม่มี caller ไหนควรส่งผ่านมาได้?”

