# Branch map

```text
module-00-primitive-obsession
  -> module-01-value-objects
  -> module-02-adt
  -> module-03-typed-errors
  -> module-04-illegal-states
  -> module-05-state-machine
  -> module-06-functional-core
  -> module-07-clean-boundary
  -> module-08-ddd
  -> module-09-property-thinking
  -> module-10-option-either
  -> module-11-arrow
```

Starter branch = assignment state. Solution branch ชื่อเดียวกัน = completed reference implementation ของโมดูลนั้น. ทุก branch เป็น descendant ของบทก่อนหน้า จึงสามารถดู `git diff module-02-adt..module-03-typed-errors` เพื่อทบทวนพัฒนาการได้.

Module 11 ให้ domain checkpoint ของบทก่อนแล้ว: primitive baseline ย้ายไป package legacy, domain types อยู่ `io.typebanking`, ส่วน library application อยู่ `arrowlesson`. Learner เติม 4 TODO และใช้ acceptance tests ชุดเดียวกับ solution. บทก่อนยังเป็น snapshots ที่ไม่มี Arrow.

`git diff module-10-option-either..module-11-arrow` แสดง dependency, bridge, preview workflow และ tests ที่เพิ่มจากพื้นฐาน handwritten types. `main` เป็นเวอร์ชันล่าสุด.
