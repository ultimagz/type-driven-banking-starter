# Learner guide

หนึ่งโมดูลใช้ 2 sessions x 75–90 นาที: session แรกอ่าน code/rule และ pair ทำ exercise; session สอง review, checkpoint, และ retrospective. ให้ตั้งชื่อ business rule ก่อนตั้งชื่อ type เสมอ เช่น `PositiveMoney` หรือ `WithdrawalRejected` ดีกว่า `ValidatedAmount`/`Error1`.

ก่อนเริ่ม: `git switch module-XX-name && gradle test`.

เมื่อ test แดง ให้แยกว่าเป็น construction rule, decision rule หรือ integration failure. ห้ามแก้ test เพื่อทำให้ผ่าน; เป้าหมายคือ API ที่อ่าน signature แล้วเห็นกฎธุรกิจ. หลังจบบท compare กับ solution และเขียนสิ่งที่ต่างหนึ่งข้อใน PR/notes ของทีม.

