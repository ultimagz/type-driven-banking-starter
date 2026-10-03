# Module 02 — Product and sum types

**Concept.** Product type คือ AND (`AccountId` AND `Balance`); sum type คือ OR (Active OR Frozen OR Closed). `sealed interface` ทำให้ `when` exhaustive.

**Problem.** แทน status string/boolean flags ด้วย `AccountStatus`; model payment เป็น Pending, Processing, Succeeded, Failed.

**Impact.** สถานะสะกดผิดและ boolean combination ที่ไร้ความหมายหายไปจาก representable values.

**Before → after.** `status: String` → `status: AccountStatus`; `failed: Boolean, reason: String?` → sealed variants.

**Exercise.** Refactor user flags `loggedIn/suspended/deleted` เป็น ADT และอธิบาย invalid combinations ที่กำจัดได้.

**Homework.** Model KYC: NotStarted, Reviewing, Approved, Rejected(reason).

**Checkpoint.** ยก 4 invalid states จาก `Payment(status: String, error: String?)`; compiler ช่วยอะไรเมื่อเพิ่ม variant ใหม่?

**Discussion prompts.** “ข้อมูลใดมีเฉพาะบาง state?” “นี่คือ AND หรือ OR?”

