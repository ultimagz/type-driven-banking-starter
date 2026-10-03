# Module 06 — Functional Core: แยกการคิดออกจากการทำงานกับโลกภายนอก

ถ้าอยากทดสอบกฎว่า “วันนี้ยังโอนได้ไหม” เราควรต้องรอวันพรุ่งนี้หรือเปิดฐานข้อมูลจริงหรือไม่? บทนี้ทำให้การตัดสินใจทดลองซ้ำได้

ก่อนเรียน: อ่านผลลัพธ์แบบ typed error และ immutable transition จาก [บท 05](module-05-state-machine.md)
หลังเรียน: แยก pure function/side effect และส่งเวลา/policy เป็น input ของ domain
เวลา: 2 sessions × 90 นาที • Branch: `module-06-functional-core`

## 1. Concept: เครื่องคิดเลขกับพนักงานธนาคาร

เครื่องคิดเลขรับ 100 และ 30 แล้วคืน 70 ซ้ำกี่ครั้งก็เหมือนเดิม
พนักงานต้องไปอ่านยอดจริง รับเวลา บันทึกบัญชี และส่งใบเสร็จ งานเหล่านี้มีสิ่งภายนอกเข้ามาเกี่ยว
เราแยกบทบาทได้: ส่วนหนึ่งคิดว่าผลควรเป็นอะไร อีกส่วนเตรียม input แล้วนำผลไปทำงานจริง

**Pure function** คือ function ที่ให้ input เดิมแล้วได้ output เดิม และไม่มี side effect ที่สังเกตได้ภายนอก
**Deterministic** หมายถึง input เดิมให้ผลเดิม แต่อาจยังมี side effect; จึงไม่ใช่คำแทน pure เสมอไป
**Functional core** คือกลุ่มกฎ/การคำนวณที่เป็น pure **Imperative shell** คือส่วนที่อ่าน เขียน และเรียกโลกภายนอกตามลำดับ

**Immutability** คือการไม่เปลี่ยนข้อมูลเดิมหลังสร้าง หากต้องการ state ใหม่ก็สร้างค่าใหม่
`val account` แปลว่า assign ตัวแปรใหม่ไม่ได้ แต่ถ้า object ข้างในมี `var balance` ยอดยังเปลี่ยนได้ จึงไม่เท่ากับ deep immutability
**Referential transparency** คือสามารถแทนการเรียก function ด้วยผลของมันได้โดยความหมายของโปรแกรมไม่เปลี่ยน เป็นเครื่องมือช่วยคิดเกี่ยวกับ pure code

## 2. Problem: input ที่ซ่อนอยู่

Before: method เรียก `Instant.now()`, อ่าน config จาก server หรือ query DB ข้างใน
ดู signature แล้วเหมือนมี input เพียง amount แต่ผลขึ้นกับเวลา ยอดที่โหลด และ config ที่คนอ่านไม่เห็น
Tests ที่ผ่านวันนี้อาจไม่ผ่านพรุ่งนี้ หรือเปลี่ยนเมื่อ external service ไม่ตอบ

ลองถาม function ว่า “สิ่งที่ต้องรู้ทั้งหมดอยู่ใน parameters หรือยัง?”
Time, randomness และ database เป็น input จริงแม้ไม่ได้เขียนในวงเล็บ ถ้า core แอบไปหาเองเราจะควบคุมการทดลองยาก

## 3. Before → after ด้วยตัวอย่างเวลา

```kotlin
// Before: เวลาจริงเป็น input ที่ซ่อนอยู่
fun expired(deadline: Instant): Boolean = Instant.now() >= deadline

// After: เปลี่ยนชื่อในไฟล์สอนเพื่อเปรียบสองแบบได้
fun expiredAt(deadline: Instant, now: Instant): Boolean = now >= deadline
```

แบบหลังทดสอบว่า now ก่อน/เท่ากับ/หลัง deadline ได้ทันที ไม่ต้องเปลี่ยน clock ของเครื่อง
เวลาจะมาจากไหนเป็นหน้าที่ของ shell: งานจริงอาจใช้ Clock.systemUTC ส่วน test ใช้ Clock.fixed
Instant เป็นจุดเวลา; policy แบบ “ก่อน 22:00 ตามเวลาประเทศไทย” ต้องมี timezone/policy เพิ่ม ไม่ควรเดาจาก Instant อย่างเดียว

ในโปรเจกต์ signature ที่ใช้คือ:

```text
decideTransfer(source: Account, destination: Account,
               amount: PositiveMoney, at: Instant): TransferOutcome
```

Core รับข้อมูลบัญชีที่โหลดแล้ว ตรวจ self-transfer/status/balance และคืนบัญชีใหม่กับผลปฏิเสธ
มันไม่ query repository และไม่ save เอง ตัวอย่างผลที่คิดได้อาจเป็น Success(source ยอด 75, destination ยอด 55) แต่ยอดใน DB ยังไม่เปลี่ยนจน shell บันทึก

## 4. Impact และสิ่งที่ pure ไม่ได้แก้แทน

เราทดสอบ rule ได้โดยไม่ต้องสร้าง network/database จำลองผลย้อนหลังได้ และอ่าน dependency ของการตัดสินใจจาก signature
การคืนค่าใหม่ช่วยกัน side effects ที่ caller ไม่รู้ แต่ object ต้นทางและ fields ซ้อนอยู่ต้องออกแบบให้ไม่ mutable ด้วย
Pure core ไม่รับรองว่ายอดที่โหลดมายังล่าสุดก่อน save นั่นเป็นเรื่อง concurrency ที่ shell/repository boundary ต้องจัดการ
เราไม่ได้ลบ I/O จากระบบ เพียงทำให้รู้ว่ามันเกิดตรงไหน

## 5. Guided exercise — ทำ transfer decision ให้ทดลองซ้ำได้

เปิด `Module06Exercise.kt` แล้วทำงานบน types/rules ที่ทีมทำจากบทก่อน:

1. ขีดเส้นใต้ DB/network/time/random/logging ที่ถูกเรียกใน function ที่รวมหลายหน้าที่
2. แยก input ที่ต้องรู้จริง เช่น source, destination, amount, at
3. ใช้ typed result และคืน account ใหม่เมื่อสำเร็จ
4. ห้ามแก้ balance ของ input และห้ามเรียก save ใน function ตัดสินใจ
5. ให้ caller เป็นคนอ่าน Clock แล้วส่ง Instant เข้ามา
6. เขียน test ซ้ำ input ชุดเดียวกันสองครั้ง ตรวจค่า observable ที่เท่ากัน

ตัวอย่าง test ด้วยภาษาคน: “a มี 100, b มี 30, โอน 25 เวลา fixed → a ใหม่ 75, b ใหม่ 55; a เดิมยัง 100”
เพิ่ม case เงินไม่พอแล้วตรวจว่ายอดต้นทาง/ปลายทางเดิมทั้งคู่ไม่เปลี่ยน
คำใบ้: ถ้า test ต้องใช้ sleep เพื่อรอเวลา ให้พิจารณาว่ายังมี clock dependency ซ่อนอยู่หรือไม่

สิ่งที่ส่ง: ก่อน/หลังของ dependency list + pure decision + caller shell เล็ก ๆ + tests
ผ่านเมื่อทดสอบ decision ได้ด้วยข้อมูลใน memory โดยไม่ต้องเปลี่ยน global clock หรือเปิด service จริง

## 6. Homework

Fee calculator เดิมอ่าน remote config ภายใน: ออกแบบ `FeePolicy` เป็น input แล้วให้ shell โหลด policy
เขียนตัวอย่าง input/result เมื่อ fee=0 และเมื่อมี fee พร้อมบอกว่ากฎ “ยอดรวมเงินคงเดิม” ต้องเปลี่ยนขอบเขตใด
แยกการคำนวณเลขจากการส่งใบเสร็จ; เขียน test เฉพาะส่วนคิดโดยไม่เชื่อม email service

## 7. Checkpoint (10 คะแนน)

1. Function ที่ผลเดิมแต่เขียน log ทุกครั้งเป็น pure ตามนิยามคอร์สหรือไม่ (2)
2. ทำไม Instant.now ภายใน function เป็น input ซ่อนอยู่ (2)
3. `val` ทำให้ object ที่มี var ข้างใน immutable ไหม (2)
4. โอนเงินใน core สำเร็จแปลว่าข้อมูลใน DB เปลี่ยนแล้วหรือยัง (2)
5. pure core กับ shell ต่างกันอย่างไร ใช้ example หนึ่ง flow (2)

ผ่านที่ 8/10 และต้องอธิบายข้อมูลเดิม/ข้อมูลใหม่ได้ หากติด mutation ให้กลับบทเตรียม val/var/copy

## 8. สำหรับผู้สอน

ให้เปลี่ยน now เป็นสามเวลาใน test แบบทันที แล้วเทียบกับการรอเวลาจริง
Discussion: ใครควรสร้าง ID? logging ต้องอยู่ตรงไหน? pure code รับ policy ไม่ล่าสุดได้ไหม แล้วใครรับผิดชอบ?
คำว่า functional core เป็นรูปแบบจัดหน้าที่ ไม่ได้บังคับว่าทั้งโปรเจกต์ต้องใช้ภาษา functional หรือเลิกเขียน class

ถัดไป: [Module 07 — วาง boundary ของ use case และ repository](module-07-clean-boundary.md)
