# Facilitator guide

ใช้โครง 90 นาที: 10 นาที recall, 15 นาที concept + before/after, 35 นาที pair exercise, 15 นาที code review, 10 นาที checkpoint, 5 นาที assign homework. อย่าแจก solution ก่อนทีมอธิบายว่า invalid state คืออะไรและ type ไหนควรเป็นเจ้าของ invariant.

การประเมินไม่ใช่จำนวน test ที่ผ่านอย่างเดียว: ดูว่า (1) name เป็น ubiquitous language, (2) constructor ปิด invariant หรือไม่, (3) expected failure ปรากฏใน type signature หรือไม่, (4) boundary ทำ I/O รั่วเข้ามาใน domain หรือไม่. ให้ใช้ discussion prompts จากเอกสารแต่ละโมดูลและเฉลยหลัง checkpoint.

## Module 11 — Library application

แบ่งเป็นสอง sessions ตาม [เอกสารบท](modules/module-11-arrow.md): gate + laws + flatMap ก่อน, แล้วค่อย Raise DSL + checkpoint. ให้ทีมทำนาย type ของ map ที่คืน Either ซ้อน และ trace Left ด้วยมือก่อนแนะนำ bind.

ให้ทุก pair ทำ acceptance suite เดียวกัน: `./gradlew test exerciseTest`. Baseline ต้องผ่าน แต่ exerciseTest แดงใน starter ตามตั้งใจ. เฉลยและ rubric อยู่ใน [solution notes](https://github.com/ultimagz/type-driven-banking-solutions/blob/module-11-arrow/docs/modules/module-11-arrow-solutions.md).

Checkpoint 20 คะแนน ผ่านที่ 16 พร้อม exercise tests ผ่านทุกข้อ. อย่าให้ syntax สั้นเป็นคะแนนแทนความเข้าใจ: learner ต้องอธิบาย query order, expected error และ domain rule ownership ได้.
