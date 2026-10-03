# Branch map — เลือกโค้ดให้ตรงกับบท

Branch คือจุดประวัติที่เลือก checkout มาเรียน ไม่ใช่ package หรือ Gradle module บทในคอร์สใช้คำว่า module แต่ repository นี้เปลี่ยน snapshot ของโปรเจกต์ผ่าน Git branches

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
  -> module-11-arrow (= main)
```

ดูชื่อบท คำถามที่เรียน และลิงก์เอกสารใน [สารบัญ](COURSE_INDEX.md)

## Starter กับ solution ต่างกันอย่างไร

Starter branch คือจุดเริ่มทำโจทย์ Solution branch ชื่อเดียวกันคือแนวโค้ดเฉลยสำหรับเปรียบเทียบหลังทำ แต่ไม่ใช่คำสัญญาว่า automated tests ใน snapshots เก่าครอบคลุมทุกกฎ ดูข้อจำกัดใน [คู่มือผู้เรียน](LEARNER_GUIDE.md)

แต่ละ branch เป็น descendant ของบทก่อนหน้า หมายถึงประวัติของบทก่อนอยู่ในประวัติของบทหลัง จึงดูพัฒนาการของโค้ดได้:

```sh
git diff module-01-value-objects..module-02-adt -- src
```

เอกสารครบ 12 บทและศัพท์กลางอยู่ทุก branch เพื่อเปิดอ่านได้สะดวก แต่โค้ดและเครื่องมือ build ยังเป็น snapshot ของบทนั้น ไม่ต้องทำ TODO ของทุกบทที่มองเห็นพร้อมกัน

## บท 11 และ main

บท 00–10 ยังไม่มี Arrow/Gradle Wrapper ใช้ JDK 21 และ Gradle ที่ติดตั้งตามบทเริ่มต้น บท 11 เพิ่ม Wrapper และ library application ให้ domain checkpoint ที่ทำเสร็จจากบทก่อนเพื่อโฟกัสแบบฝึก Arrow

ใน starter บท 11 primitive baseline ย้ายไป package `io.typebanking.legacy`, domain types อยู่ `io.typebanking` และแบบฝึกใหม่อยู่ `arrowlesson` ให้เติม 4 TODO ใน `TransferPreview.kt` แล้วรัน baseline/exercise suites ตามเอกสาร

```sh
git diff module-10-option-either..module-11-arrow -- src build.gradle.kts
```

main ชี้ snapshot ล่าสุด ไม่ใช่จุดเริ่มต้นที่ต้องทำก่อนบท 00

## มีงานอยู่แล้วจะอัปเดต branch อย่างไร

เก็บงานด้วย commit หรือ stash ตาม [learner guide](LEARNER_GUIDE.md) ก่อน จากนั้น fetch และอัปเดต course branch ที่ยังไม่ได้แก้ด้วย fast-forward:

```sh
git fetch origin
git switch module-01-value-objects
git merge --ff-only origin/module-01-value-objects
```

หากคำสั่งปฏิเสธเพราะประวัติแยกกัน อย่า force/reset งานตัวเอง ให้ทำแบบฝึกต่อใน branch ส่วนตัวหรือขอผู้สอนช่วยเลือกวิธีนำเอกสารเข้า งานที่เขียนไปแล้วมีค่ามากกว่าการทำให้ branch หน้าตาเหมือนเครื่องคนอื่นทันที
