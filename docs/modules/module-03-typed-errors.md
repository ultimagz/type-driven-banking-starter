# Module 03 — Expected failure as data

**Concept.** Failure ที่ caller คาดหวังได้เป็นส่วนหนึ่งของ domain contract ไม่ใช่ generic exception.

**Problem.** ออกแบบ `WithdrawResult`/`TransferResult`: Success, InsufficientFunds, AccountFrozen, AccountClosed, SameAccount, DailyLimitExceeded.

**Impact.** UI/adapter handle ทุก outcome ผ่าน exhaustive `when`; signature บอก business behavior.

**Before → after.** `throw IllegalStateException()` → `sealed interface TransferResult`.

**Exercise.** เปลี่ยน withdraw ให้ immutable และคืน typed outcome; ห้ามใช้ `else` ใน caller.

**Homework.** เปลี่ยน login ที่ throw WrongPassword/Locked เป็น typed result.

**Checkpoint.** จัดประเภท: insufficient balance, timeout, DB broken, NPE — อะไรเป็น domain failure และอะไรเป็น exceptional infrastructure/programming fault?

**Discussion prompts.** “Caller ควรตัดสินใจอย่างไรเมื่อ failure นี้เกิด?” “failure นี้เป็นส่วนปกติของ flow ไหม?”

