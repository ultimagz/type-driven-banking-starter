# คู่มือผู้สอน — สอนเหตุผลก่อนศัพท์และเครื่องมือ

ผู้เรียนอาจเขียน Kotlin ได้บางส่วน แต่ยังไม่เคยรู้จัก domain modeling อย่าเริ่มจาก “วันนี้เรียน ADT” แล้วสมมติว่าทุกคนรู้ว่าปัญหาคืออะไร ให้เริ่มจากข้อมูลผิดที่สร้างได้ แล้วค่อยตั้งชื่อวิธีแก้

เตรียม [บทเริ่มต้น](GETTING_STARTED.md), [สารบัญ](COURSE_INDEX.md), [ศัพท์กลาง](GLOSSARY.md) และเอกสารของบทไว้ล่วงหน้า ใช้คำตอบ checkpoint จาก [solution repo](https://github.com/ultimagz/type-driven-banking-solutions/blob/main/docs/CHECKPOINT_ANSWERS.md) หลังให้ผู้เรียนตอบแล้ว

## 1. ตรวจพื้นฐานโดยไม่ทำให้คนรู้สึกว่าต้องรู้ทุกอย่างมาก่อน

ก่อนบท 00 ให้ทุกคนลองอ่าน function รับจำนวนเงินและคืนยอดใหม่ ถามเพียงสามเรื่อง: input คืออะไร, output คืออะไร, ถ้าถอนเกินยอดควรเกิดอะไร

ถ้าอ่าน function/val/var/nullable ไม่ออก ให้ทำตัวอย่าง Kotlin ในบทเตรียมตัวร่วมกัน 20–30 นาที ถ้า clone หรือรันทดสอบไม่ได้ แยกช่วย setup อย่าใช้เวลาส่วนใหญ่ของ concept lecture แก้เครื่องคนเดียว

ก่อนบทใหม่ ใช้คำถามทบทวนสองข้อจาก prerequisite ของบทนั้น หากหลายคนตอบไม่ออก ให้ย้อนเรื่องนั้นพร้อมตัวอย่างใหม่ ไม่ใช่พูดประโยคเดิมให้เร็วขึ้น คำตอบภาษาไทยพร้อมตัวอย่างมีคุณค่ากว่าจำคำว่า invariant ได้แต่บอกกฎไม่ได้

## 2. รูปแบบการอธิบายหนึ่ง concept

ใช้ลำดับนี้กับศัพท์ใหม่ทุกคำ:

1. **เรื่องใกล้ตัว:** “ส่งพัสดุแล้ว แต่ไม่มีเวลาที่ส่ง ข้อมูลนี้แปลว่าอะไร?”
2. **ปัญหาใน code:** เปิด status String คู่กับ timestamp nullable และสร้างข้อมูลขัดกันให้เห็น
3. **ภาษาธรรมดา:** “แต่ละสถานะควรมีเฉพาะข้อมูลที่ต้องใช้ และข้อมูลจำเป็นต้องหายไม่ได้”
4. **ตั้งชื่อแนวคิด:** นี่คือส่วนหนึ่งของ make illegal states unrepresentable; sum type คือทางเลือก ส่วน product คือข้อมูลที่ต้องมีร่วมกัน
5. **อ่าน after ทีละบรรทัด:** ใครสร้างค่า, caller เห็นอะไร, compiler ป้องกันอะไร, อะไรยังต้องตรวจตอนรัน
6. **ให้ผู้เรียนอธิบายกลับ:** เปลี่ยนเป็นกรณีอาหารส่งถึงแล้วแต่ไม่มี proofOfDelivery ให้ทีม model เอง

อย่ากระโดดจาก analogy ไป definition แล้วแจกโจทย์ทันที ต้องมีช่วง trace input จริงอย่างน้อยกรณีสำเร็จและกรณีถูกปฏิเสธ ใช้ BigDecimal("100") ไม่ปนรายละเอียด floating point เพิ่มก่อนจำเป็น

## 3. แผนสอง sessions ต่อบท

### Session A (90 นาที): เข้าใจและเริ่มลงมือ

| นาที | ทำอะไร | ตรวจว่าเข้าใจอย่างไร |
|---|---|---|
| 0–10 | ทบทวน prerequisite และ requirement | ทุกคนตอบด้วยตัวอย่างหนึ่งค่า |
| 10–25 | concept ผ่านเรื่องใกล้ตัวและคำจำกัดความ | ให้คนที่ยังไม่พูดอธิบายกลับ |
| 25–40 | before/after และ trace code | ให้ทีมทายผลก่อนรัน |
| 40–75 | guided exercise เป็นคู่ | สลับคนพิมพ์/คนทาย test |
| 75–85 | แชร์สิ่งที่ติดและ hint | แยกติดศัพท์/กฎ/syntax |
| 85–90 | สรุปและตกลงงานก่อน session B | แต่ละคู่บอกกฎหนึ่งข้อ |

### Session B (90 นาที): ตรวจและสะท้อนพัฒนาการ

15 นาทีทบทวน + 30 นาทีทำ tests/กรณีปฏิเสธ + 15 นาที peer review + 15 นาที checkpoint + 10 นาทีเฉลยเหตุผล + 5 นาทีเลือก homework

ให้ทีมเวลาคิดจริงก่อนผู้สอนพิมพ์คำตอบ หากจำเป็นต้องลด scope ให้ลด homework ไม่ลดช่วงอธิบายความหมายหรือกรณี failure ของ concept หลัก

## 4. ให้คำใบ้เป็นระดับ ไม่แจก implementation ทันที

- ระดับ 1 ถามกฎ: “ถ้า amount=0 ควรคืนอะไร?”
- ระดับ 2 ชี้ขอบเขต: “ใครเป็นคนยอมให้สร้าง PositiveMoney?”
- ระดับ 3 ชี้รูปผลลัพธ์: “มี valid value หรือ construction error ลองใช้สองทางเลือก”
- ระดับ 4 ชี้ตำแหน่ง: “เปิด factory ใน companion object และ test ค่า -1/0/1”
- ระดับ 5 ทำกรณีเล็กหนึ่งกรณีร่วมกัน แล้วให้ learner ทำกรณีที่เหลือ

ผู้เรียนที่เร็วกว่าให้เพิ่ม counterexample หรืออภิปราย trade-off ไม่ให้ไปทำโค้ดแทนคู่ที่ช้ากว่า หากหลายคู่ติดเหมือนกัน หยุดรวมชั้น 5 นาทีและอธิบาย prerequisite ที่ขาด

## 5. จุดที่มักสับสนในแต่ละบท

| บท | ความเข้าใจคลาดเคลื่อน | วิธีพากลับไปเข้าใจ |
|---|---|---|
| 00 | ใช้ String คือผิดทั้งหมด | เทียบ raw input กับข้อมูลที่ผ่านกฎแล้ว |
| 01 | private constructor ทำให้ทุกอย่าง valid | ลอง setter/copy/ช่องทางโหลดค่าที่ไม่ตรวจ |
| 02 | product คือสินค้า; sealed คือ state เสมอ | วาดฟอร์ม AND กับเมนู OR แล้วจึงนับค่าที่เป็นไปได้ |
| 03 | failure ทุกชนิดต้องเป็น business error | แยกเงินไม่พอ, bug, network outage ด้วยสิ่งที่ caller ต้องทำ |
| 04 | sealed พิสูจน์ทั้ง workflow แล้ว | ลองเรียก public Succeeded constructor โดยไม่ผ่าน Processing |
| 05 | state machine แก้ race condition เอง | วาดสอง process อ่าน state เก่าพร้อมกัน |
| 06 | val หรือผลเหมือนเดิมแปลว่า pure | ใส่ log/Instant.now แล้วหา effect และ input ซ่อน |
| 07 | interface ทำให้ save atomic | แยกคำสัญญาของ port กับหลักฐานจาก adapter/DB จริง |
| 08 | aggregate ต้องรวมทุกสิ่งที่เกี่ยวข้อง | ถามขอบเขตที่ต้อง consistent และต้นทุนของการรวม root |
| 09 | วน 100 ครั้งพิสูจน์ถูกทุก input | ยกกรณีที่ generator ไม่สร้าง และ assertion ที่ตรวจไม่ตรงกฎ |
| 10 | map กับ flatMap คือชื่อสองแบบของอย่างเดียว | เขียน type ซ้อนบนกระดาษและ trace Left ว่าเรียก lambda ไหม |
| 11 | ใช้ Monad/Arrow แล้ว domain rule ถูกเอง | แยก mechanics ของ library จาก rule ownership ของทีม |

ใน checkpoint บางบทมี types ของบทหลังอยู่ก่อนแล้ว บอกทีมว่าเป็น checkpoint code สำหรับอ่าน ไม่จำเป็นต้องเข้าใจทั้งโปรเจกต์ใน session เดียว เอกสารทุกบทอยู่ในทุก branch แต่ source ต้อง checkout ให้ตรงบท

## 6. ประเมินจากเหตุผลและหลักฐาน

Checkpoint 00–10 ใช้ 10 คะแนน ผ่านที่ 8 และต้องผ่านเงื่อนไขความเข้าใจท้ายบท บท 11 ใช้ 20 คะแนน ผ่านที่ 16 พร้อม acceptance tests ตามบท

คำถาม 2 คะแนนโดยทั่วไป: 1 คะแนนสำหรับอธิบายหลักถูก, 1 คะแนนสำหรับตัวอย่างหรือเหตุผลที่เชื่อมกับ code อย่าหักคะแนนเพราะไม่ใช้ศัพท์อังกฤษถ้าความหมายครบ ให้เวลาซ่อมความเข้าใจแล้วตอบใหม่ด้วยกรณีที่ต่างจากเดิม

Review exercise อย่างน้อยสี่ด้าน:

1. **ความหมาย:** อ่านชื่อ/signature แล้วเห็นกฎและผลที่เป็นไปได้ไหม
2. **ทางผิด:** caller ยังสร้างข้อมูลขัดกฎหรือเลี่ยง constructor ได้ไหม
3. **พฤติกรรม:** สำเร็จ/ปฏิเสธ/ขอบเขตมี tests ไหม และ reject แล้วไม่เกิด mutation/save ที่ผิดไหม
4. **ขอบเขต:** ใครรับผิดชอบเวลา, I/O, error mapping, consistency; code อ้างการรับรองเกินหลักฐานไหม

อย่าให้คะแนนจากจำนวน class/interface หรือ syntax สั้น Tests ผ่านแปลว่าผ่านเฉพาะสิ่งที่ suite ตรวจ ไม่ใช่ว่าถูกทุก business rule โดยอัตโนมัติ

## 7. ใช้ starter และ solution อย่างซื่อตรง

Starter 00–10 เป็น skeleton มี Any/TODO และ tests ตั้งต้น ไม่ใช่ acceptance suite สมบูรณ์ทุกบท ให้ทีมเพิ่ม tests จากรายการในเอกสาร บาง solution snapshot มี tests น้อยหรือไม่มี tests ใหม่ ต้องใช้ checkpoint และการ trace ประกอบ ไม่เรียกว่า “เฉลยครบเพราะเขียว”

ก่อนสอน ทดลอง exercise เองหนึ่งรอบและตรวจ diff ระหว่างบทเฉพาะ `src` ตาม learner guide หากตัวอย่างเพื่ออธิบายใช้ชื่อ function ต่างจาก snapshot ให้ชี้ว่าเป็นภาพย่อ ไม่สั่งให้ copy ลง package แล้วสร้างชื่อซ้ำ

เปิด solution หลังคู่เรียนบอก requirement/expected result ได้ เปรียบเทียบ behavior ก่อน implementation อื่นที่ถูกตาม contract ควรยอมรับได้ หากพบขอบเขตของ prototype เช่น unchecked cast ในบท 10 ให้ใช้เป็น code review exercise ตามเอกสาร ไม่สอนว่าเป็น production pattern

## 8. บท 11 — ใช้ไลบรารีหลังเข้าใจพื้นฐาน

เริ่มจาก preface และ knowledge gate ใน [Module 11](modules/module-11-arrow.md) ให้ trace handwritten map/flatMap และ laws ก่อนแนะนำ Raise DSL ซึ่งเป็นรูปแบบเขียน dependent flow ด้วย bind

ลำดับ exercise: parseAmount → requireAccount → withFlatMap → withRaise ใช้ acceptance contract เดียวกันทุกคู่:

```sh
./gradlew test
./gradlew exerciseTest
```

Baseline ต้องผ่าน ส่วน exerciseTest ใน starter ตั้งใจไม่ผ่านก่อนเติม TODO ให้ตรวจทั้ง query order, short-circuit, typed errors และยอดรวม ไม่ใช่เฉพาะผลสุดท้าย เฉลยเฉพาะบทอยู่ [solution notes](https://github.com/ultimagz/type-driven-banking-solutions/blob/module-11-arrow/docs/modules/module-11-arrow-solutions.md)

## 9. Retrospective ของทีม

จบแต่ละบทถาม: “ก่อนเรียนเราทำผิดแบบใดได้?”, “ตอนนี้ type กันอะไรได้?”, “อะไรยังต้องตรวจด้วย tests/runtime/adapter?” เก็บคำถามที่ยังไม่ตัดสินใจเป็น policy ของ Mini Banking System

เป้าหมายสุดท้ายไม่ใช่ให้ทุกคนพูดศัพท์ชุดเดียวกัน แต่ให้คุย business rule ได้ตรงกันและอ่าน code แล้วรู้ว่าข้อมูลผิดจะถูกหยุดตรงไหน
