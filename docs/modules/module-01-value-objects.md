# Module 01 — Value objects and parsing

**Concept.** Value object มีความหมายและ invariant; smart constructor เป็น parse boundary: raw input → valid domain value หรือ error.

**Problem.** สร้าง `AccountId`, `AccountName`, `PositiveMoney`, `TransactionId` โดย constructor ต้องไม่ public. `PositiveMoney` ต้องมากกว่า 0 และ scale ไม่เกิน 2.

**Impact.** method ที่รับ `PositiveMoney` ไม่ต้อง validate positivity ซ้ำ และไม่เผลอสลับ `AccountId` กับ `TransactionId`.

**Before → after.** `amount: BigDecimal` → `amount: PositiveMoney`; `id: String` → `id: AccountId`.

**Exercise.** ทำ factory ที่คืน `ConstructionError`; เขียน tests สำหรับ valid/invalid boundary ทุก type.

**Homework.** ออกแบบ `DailyTransferLimit`, `Percentage`, `Email`: invariant, valid/invalid examples, factory API.

**Checkpoint.** เหตุใด `Money` และ `PositiveMoney` ไม่ควรเป็น type เดียว? runtime check ที่ constructor ป้องกันอะไร และ type signature ป้องกันอะไร?

**Discussion prompts.** “ชื่อ type นี้สื่อ business meaning หรือสื่อ implementation?” “กฎนี้เป็นจริงหลัง construction เสมอหรือไม่?”

