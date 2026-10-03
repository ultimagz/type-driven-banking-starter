# Module 04 — Make illegal states unrepresentable

**Concept.** ใส่ข้อมูลที่จำเป็นไว้ใน variant ที่ต้องการมัน ไม่ใช้ nullable bag of fields.

**Problem.** Refactor `Transaction(status, completedAt?, failureReason?)` เป็น Pending, Processing(startedAt), Succeeded(completedAt), Failed(reason, failedAt).

**Impact.** Pending ที่มี completedAt หรือ Failed ที่ไม่มี reason ถูกสร้างไม่ได้จาก public API.

**Before → after.** one class + nullable fields → sealed hierarchy with state-owned data.

**Exercise.** Model Order: paid/shipped/cancelled/trackingNumber flags ให้เป็น ADT.

**Homework.** Loan Application: Draft, Submitted, UnderReview, Approved, Rejected, Disbursed พร้อม data ของแต่ละ state.

**Checkpoint.** ระบุ illegal combinations 5 แบบของ nullable-bag model และอธิบาย type variant ที่กำจัดแต่ละแบบ.

**Discussion prompts.** “field นี้มีความหมายในทุก state หรือไม่?” “เรากำลัง encode fact หรือ workflow?”

