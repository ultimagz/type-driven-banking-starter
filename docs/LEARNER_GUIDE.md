# Learner guide

หนึ่งโมดูลใช้ 2 sessions x 75–90 นาที: session แรกอ่าน code/rule และ pair ทำ exercise; session สอง review, checkpoint, และ retrospective. ให้ตั้งชื่อ business rule ก่อนตั้งชื่อ type เสมอ เช่น `PositiveMoney` หรือ `WithdrawalRejected` ดีกว่า `ValidatedAmount`/`Error1`.

ก่อนเริ่ม: `git switch module-XX-name && gradle test`.

เมื่อ test แดง ให้แยกว่าเป็น construction rule, decision rule หรือ integration failure. ห้ามแก้ test เพื่อทำให้ผ่าน; เป้าหมายคือ API ที่อ่าน signature แล้วเห็นกฎธุรกิจ. หลังจบบท compare กับ solution และเขียนสิ่งที่ต่างหนึ่งข้อใน PR/notes ของทีม.

## Module 11 — เริ่มใช้ไลบรารีหลังเข้าใจพื้นฐาน

ผ่าน knowledge gate ใน [เอกสารบท 11](modules/module-11-arrow.md) ก่อนใช้ Raise DSL. ลำดับงาน: trace handwritten map/flatMap → อ่าน law tests → parseAmount → requireAccount → withFlatMap → withRaise.

ใช้ JDK 21 แล้วรัน `./gradlew test` เพื่อตรวจ baseline และ `./gradlew exerciseTest` เพื่อตรวจแบบฝึก. ชุดหลังตั้งใจแดงก่อนเติม TODO. ทำ exercise ตามลำดับและห้ามปรับลำดับ lookup ให้ต่างจาก contract.

เมื่อเสร็จ compare API/behavior กับ [solution branch](https://github.com/ultimagz/type-driven-banking-solutions/tree/module-11-arrow). ต้องอธิบาย invariant ที่ยังเป็นหน้าที่ของ domain และผลของ short-circuit ได้ก่อนถือว่าผ่าน.
