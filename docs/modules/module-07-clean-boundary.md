# Module 07 — Clean Architecture Boundary: ให้กฎธุรกิจรู้เฉพาะสิ่งที่จำเป็น

ถ้าเปลี่ยนที่เก็บบัญชีจาก memory เป็นฐานข้อมูล ทำไมกฎ “ถอนเกินยอดไม่ได้” ต้องเปลี่ยนไปด้วย? บทนี้แยกหน้าที่ให้กฎไม่ผูกกับเครื่องมือ

ก่อนเรียน: แยก core/shell จาก [บท 06](module-06-functional-core.md) ได้
หลังเรียน: อ่าน use case, repository, port, adapter และทดสอบ flow ด้วย fake ได้
เวลา: 2 sessions × 90 นาที • Branch: `module-07-clean-boundary`

## 1. Concept: ช่องบริการกับพนักงานหลังเคาน์เตอร์

คนขอโอนเงินรู้เพียง “ส่งคำขอโอน” ไม่จำเป็นต้องรู้ว่าพนักงานใช้ SQL หรือไฟล์ในการอ่านบัญชี
เรากำหนดข้อตกลงว่าขออะไรได้/ตอบอะไร แล้วให้ implementation แต่ละแบบทำตามข้อตกลงนั้น
**Boundary** คือเส้นแบ่งความรับผิดชอบและรูปแบบข้อมูลที่ข้ามเส้นนั้น

**Use case** คือการทำงานหนึ่งเรื่องที่ผู้ใช้/ระบบต้องการ เช่น Transfer หรือ OpenAccount; มันจัดลำดับการอ่าน ตัดสินใจ และบันทึก
**Repository** คือ abstraction ที่ให้ application ขอโหลด/เก็บ domain objects โดยไม่ต้องรู้รายละเอียด storage
**Port** คือข้อตกลงการติดต่อที่ฝั่ง application/domain ต้องการ; ใน Kotlin มักเป็น interface
**Adapter** คือ implementation ที่ต่อ port เข้ากับสิ่งจริง เช่น SQL repository หรือ in-memory repository

**Dependency** คือสิ่งที่โค้ดอ้างถึงเพื่อ compile/ทำงาน และ **Dependency inversion** ในที่นี้คือให้ application เป็นเจ้าของ interface ที่ต้องการ แล้ว storage implementation มา implement
ไม่ได้แปลว่าสลับลำดับการเรียก: use case ยังเรียก repository ตอนรัน แต่ dependency ของ source code ชี้เข้าหา contract ที่ application กำหนด

## 2. Problem และ before/after

Before: BankingService รู้ชื่อ table, query SQL, format JSON, เช็กยอด และเลือกข้อความ HTTP status ใน class เดียว
กฎกับรายละเอียด storage เปลี่ยนไปด้วยกัน และ test ต้องเปิดระบบหลายอย่างแม้แค่อยากดูว่า self-transfer ถูกปฏิเสธหรือไม่
After: domain รู้ Account/rules; use case รู้ port; adapter รู้วิธีเชื่อม storage; UI/transport รู้วิธี map request/response

**DTO — Data Transfer Object** คือข้อมูลเพื่อส่งผ่าน boundary เช่น JSON request ที่มี raw strings
DTO อาจมีข้อมูลไม่ valid จึงต้อง parse เป็น domain values ก่อนทำ decision
Domain object ไม่ควรเชื่อว่าข้อมูลที่มาจาก DB/HTTP ถูกต้องเพียงเพราะ field ชื่อตรงกัน

## 3. เริ่มจาก port เล็กที่สุด

ภาพ API สำหรับสอน ใช้ nullable ก่อนเรียน Option ในบท 10:

```kotlin
interface DemoAccountRepository {
    fun find(id: AccountId): Account?
    fun savePair(source: Account, destination: Account)
}
```

`interface` บอกว่า implementation ต้องให้ methods อะไร โดยยังไม่บอกว่าใช้ฐานข้อมูลแบบใด
`Account?` ในตัวอย่างหมายถึงหาไม่พบได้ ส่วน checkpoint จริงของรีโปใช้ handwritten Option; มองเป็น “พบ/ไม่พบ” ก่อนเรียน operators
savePair ตั้งใจให้สองบัญชีถูกบันทึกครบทั้งคู่หรือไม่มีการบันทึก ตาม contract ที่ adapter ต้องทำจริง
ชื่อ method ไม่ทำให้ database atomic อัตโนมัติ ต้องมี implementation ที่ใช้ transaction หรือกลไก consistency ที่ออกแบบไว้

ลำดับ use case:

```text
รับ domain input → load source → load destination → decideTransfer
  Success  → บันทึกสองบัญชีตาม contract → คืนผลสำเร็จ
  Rejected → คืนเหตุผล → ไม่มีการ save
```

**Atomic** แปลว่าการเปลี่ยนชุดนี้สำเร็จครบหรือไม่มีผลบางส่วน
ถ้าหัก source แล้ว save destination พังโดยไม่มี rollback ยอดรวมจะเสีย; จึงไม่ควรใช้ save ทีละบัญชีและอ้างว่า transfer เสร็จสมบูรณ์
ตัวอย่าง `AccountRepository.saveAll` ใน snapshot เป็น port เพื่อสอน boundary ยังไม่ใช่ database transaction implementation

## 4. Fake ทำให้ทดสอบได้อย่างไร

**Fake** คือ implementation ที่ทำงานจริงแบบง่ายสำหรับทดสอบ เช่น Map ใน memory แทนฐานข้อมูล
**Stub** มักคืนค่าที่กำหนดไว้ ส่วน **Mock/Spy** มักใช้ตรวจการเรียก เราไม่ต้องเริ่มจาก mocking library เพื่อทำโจทย์นี้
Fake repository บันทึก list ของ find/save calls เพื่อให้ test ตรวจลำดับและ side effects ได้

ตัวอย่าง test story: source missing → ไม่ load destination และไม่ save; source/destination ครบแต่กฎ reject → ไม่มี save
เมื่อ success → save pair หนึ่งครั้งพร้อมยอดใหม่ทั้งสอง แต่ input เดิมยังไม่เปลี่ยน
Unit test ของ fake ไม่พิสูจน์ว่า SQL adapter จริง atomic; adapter ต้องมี integration tests ของมันต่างหาก

## 5. Impact และ trade-offs

เปลี่ยน storage ได้โดย domain rules ไม่ import SQL/HTTP framework ทำ test ได้เฉพาะจุดและ failure ownership ชัดขึ้น
แต่ interface ทุกอันมีค่าใช้จ่ายในการออกแบบ ไม่ต้องสร้าง port สำหรับ helper arithmetic เล็ก ๆ เพียงเพราะอยากมี layer เยอะ
เลือกเส้นแบ่งที่ช่วยแยกเหตุที่เปลี่ยนจริง เช่น business rule กับ storage หรือ clock

Infrastructure failure เช่น DB unavailable ต้องมีนโยบายที่ shell/adapter ไม่ควรแปลงเป็นเงินไม่พอ
ระบบอาจคืน typed integration error ที่ application boundary หรือปล่อย exception ให้ top-level handler; ต้องตกลงว่าจะ retry/report อย่างไร

## 6. Guided exercise — จัดหน้าที่ของ TransferUseCase

เปิด `Module07Exercise.kt` และ reuse pure decision:

1. เขียนสิ่งที่ use case ต้องขอจาก storage เป็น interface
2. สร้าง fake ด้วย map ของ AccountId → Account
3. ให้ use case รับ repository และ clock ผ่าน constructor; นี่เรียกว่า dependency injection คือส่งสิ่งที่ใช้เข้าไป ไม่ใช่สร้างเองข้างใน
4. Implement load → decide → save เฉพาะ success
5. ระบุ SourceMissing กับ DestinationMissing แยกกัน
6. Test lookup/save calls และเขียน contract atomicity ของ savePair/saveAll เป็นข้อความ

คำใบ้: constructor injection ไม่จำเป็นต้องใช้ DI framework ใช้ `TransferUseCase(fake, fixedClock)` ได้เลย
สิ่งที่ส่ง: package responsibility map + port + fake + use case + tests ทั้ง missing/rejected/success
ผ่านเมื่อ domain compile ได้โดยไม่ import storage framework และ test flow ไม่ต้องเปิด DB จริง

## 7. Homework

วาด REST adapter: raw JSON → parse → use case → response
REST/HTTP คือช่องทางรับส่ง request ผ่าน network; HTTP response code เป็นหน้าที่ transport ไม่ใช่ชื่อ domain error
ระบุว่า invalid input, account missing, business rejection และ infrastructure failure จะ map อย่างไร โดยยอมให้ policy ต่างกันตามทีม
เพิ่ม failure test ของ real adapter หากเลือก storage จริง: destination write พังแล้ว source ต้องไม่ถูกหักค้าง

## 8. Checkpoint (10 คะแนน)

1. Use case กับ pure domain decision ต่างกันตรงไหน (2)
2. Port กับ adapter ต่างกันอย่างไร ยก repository หนึ่งตัว (2)
3. Constructor injection จำเป็นต้องมี DI library ไหม (1)
4. interface saveAll รับรอง database atomicity เองหรือไม่ (2)
5. Fake ผ่าน tests บอกอะไรได้/ไม่ได้เกี่ยวกับ adapter จริง (2)
6. Domain ควรรู้ HTTP status หรือไม่ตามขอบเขตนี้ (1)

ผ่านที่ 8/10 พร้อมไม่มี save เมื่อ reject และไม่สับสน contract กับ implementation

## 9. สำหรับผู้สอน

ให้ทีมสลับ fake เป็น implementation อื่นแล้วดูว่าไฟล์ rule ต้องแก้หรือไม่
Discussion: ถอด SQL adapter แล้ว domain compile ไหม? interface เป็นภาษางานหรือภาษา table? ความรับผิดชอบ atomicity อยู่ตรงไหน?

ถัดไป: [Module 08 — DDD และเจ้าของกฎ](module-08-ddd.md)
