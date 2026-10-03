# ศัพท์กลางสำหรับเปิดดูระหว่างเรียน

ไม่ต้องจำทุกคำก่อนเริ่ม แต่ละบทอธิบายคำใหม่ในบริบทจริง เอกสารนี้ช่วยทวนและชี้ว่าควรกลับไปอ่านบทใด

| คำ | ความหมายในคอร์ส | ตัวอย่าง / อ่านต่อ |
|---|---|---|
| Domain | เรื่องของงานที่ระบบรับผิดชอบ | บัญชี/ถอน/โอน, 00 |
| Business rule | เงื่อนไขของงาน | ถอนเกินยอดไม่ได้, 00 |
| State | ข้อมูล ณ จุดหนึ่ง | a-1 มียอด 100, 00 |
| Invariant | กฎที่ต้องจริงเสมอสำหรับ state/value ที่ valid | PositiveMoney>0, 01 |
| Primitive obsession | ใช้ชนิดทั่วไปจนความหมาย/กฎของงานไม่ปรากฏ | status:String, 00 |
| Value object | ค่าเท่ากันด้วยค่าที่แทน ไม่ใช่ object identity | AccountId, 01 |
| Constructor | ทางสร้าง instance ของ class | AccountId(...), 01 |
| Smart constructor | function สร้างค่าโดยตรวจ rule ก่อน | create/parse, 01 |
| Parse boundary | จุดแปลง raw input เป็น domain value หรือรายงานผิด | amount:String→PositiveMoney, 01 |
| Normalize | ทำค่าให้เป็นรูปแบบมาตรฐานตาม policy | trim รหัส, 01 |
| Product type | มีข้อมูลหลายช่องร่วมกัน (AND) | idANDbalance, 02 |
| Sum type | หนึ่งค่ามีได้หนึ่ง variant จากหลายทางเลือก (OR) | ActiveORClosed, 02 |
| ADT (Algebraic Data Type) | ประกอบชนิดด้วย product/sum | Account+AccountStatus, 02 |
| Variant | ทางเลือกหนึ่งของ sum type | Frozen, 02 |
| Exhaustive | ครบทุกทางเลือกที่ต้องรับมือ | when ไม่มี else ครอบครบ, 02 |
| Smart cast | compiler รู้ type ที่แคบลงหลังตรวจ | is Success แล้วอ่าน account, 04 |
| Typed error | error เป็นข้อมูลมีชนิด/สาเหตุชัด | InsufficientFunds, 03 |
| Illegal state | ข้อมูลที่ขัดกฎ domain | Pending มี completedAt, 04 |
| Command | คำขอให้ทำงาน อาจถูกปฏิเสธ | Start, 05 |
| Event | ข้อเท็จจริงที่เกิดแล้ว | MoneyWithdrawn, 08 |
| Transition | เปลี่ยน state ตามคำสั่ง/กฎ | Pending→Processing, 05 |
| Terminal | state ที่ workflow นี้ไม่ไปต่อ | Succeeded, 05 |
| Pure function | input เดิม→output เดิมและไม่มี observable side effect | decideTransfer, 06 |
| Side effect | ผลนอกค่าที่คืน เช่นเปลี่ยน object/เขียน DB | saveAll, 06 |
| Immutability | ไม่แก้ข้อมูลเดิมหลังสร้าง | คืน account ใหม่, 06 |
| Functional core / shell | แยกกฎการคิดออกจากงาน I/O | decide กับ load/save, 06 |
| I/O | ติดต่อสิ่งภายนอก เช่น file/network/database | repository, 06 |
| Boundary | เส้นแบ่งหน้าที่/ข้อมูลที่ข้าม | raw request→domain, 07 |
| Use case | จัดลำดับงานหนึ่งเรื่องของระบบ | TransferUseCase, 07 |
| Port | contract ที่ application ต้องการ | AccountRepository, 07 |
| Adapter | implementation ที่เชื่อม contract กับเครื่องมือ | SQLrepository, 07 |
| Dependency injection | ส่งสิ่งที่ใช้เข้ามาแทนสร้างเอง | constructor รับ fake/clock, 07 |
| Atomicity | สำเร็จทั้งชุดหรือไม่มีผลบางส่วน | save สองบัญชี, 07 |
| Fake | implementation ง่ายที่ใช้ทดลองได้ | Map ใน memory, 07 |
| Entity | สิ่งที่เป็นตัวเดิมตาม identity แม้ข้อมูลเปลี่ยน | Accounta-1, 08 |
| Aggregate/root | ขอบเขต consistency และจุดเข้าการเปลี่ยนแปลง | Account รักษากฎยอด, 08 |
| Bounded context | ขอบที่คำ/model มีความหมายเดียวกัน | banking กับ login, 08 |
| Property | ข้อความของกฎสำหรับ input กลุ่มหนึ่ง | ฝากไม่ลดยอด, 09 |
| Generator | วิธีสร้าง input สำหรับ test | valid amounts, 09 |
| Counterexample / shrinking | กรณีที่ผิดกฎ/ลดให้เล็กยัง fail | balance1 ถอน 2, 09 |
| Option | มีค่า Some หรือไม่มี None | nickname/accountlookup, 10 |
| Either | failureLeft หรือ successRight | Either<Error,Account>, 10 |
| Generic | ใช้ชื่อแทน type ที่ระบุภายหลัง | Option<A>, 10 |
| Lambda | function ที่ส่งเป็นค่าได้ | { it+1 }, 10 |
| map | แปลงค่าข้างในโดยคง context | Right10→Right11, 10 |
| flatMap | ต่อขั้นที่คืน context โดยไม่ห่อเพิ่ม | parse→lookup, 10 |
| Short-circuit | failure แล้วไม่ทำ dependent ขั้นถัดไป | parsefail ไม่ load, 10 |
| Functor | pattern ของ map ที่รักษา laws | identity/composition, 11 |
| Monad | pattern ของ pure/flatMap ที่รักษา laws | ต่อ typedresults, 11 |
| pure/unit (Monad) | ยกค่าปกติเข้า context | Right(10), 11 |
| bind | รับ success หรือหยุด failure ใน scope | eitherblock, 11 |
| fold | แยกทุกแขนแล้วคืน type เดียว | Some/None→Either, 11 |
| mapLeft | แปลง error ข้อมูลอย่าง explicit | Construction→PreviewError, 11 |
| Fail-fast / accumulate | หยุดที่ error แรก / รวม independent errors | workflow/form, 11 |
| Library / dependency | โค้ดที่เรียกใช้ / สิ่งที่ build ต้องใช้ | ArrowCore, 11 |

ศัพท์ของโค้ดและเครื่องมือที่ใช้ข้ามบท:

| คำ | อ่านเป็นภาษาง่าย ๆ |
|---|---|
| Type / value | ชนิดของข้อมูล / ค่าหนึ่งของชนิดนั้น เช่น String / "a-1" |
| API / signature | วิธีเรียกใช้งานที่เปิดให้คนอื่น / ชื่อและชนิด input-output ของ function |
| Caller | โค้ดที่เรียกใช้ function นี้ |
| Compiler / compile-time | เครื่องมือแปลงและตรวจโค้ด / ช่วงก่อนโปรแกรมรันจริง |
| Runtime | ช่วงที่โปรแกรมกำลังทำงานกับค่าจริง |
| Interface | ข้อตกลงว่าชนิดหนึ่งมีสิ่งใดให้เรียกได้ ไม่ใช่ตัว implementation |
| Implementation | โค้ดที่ทำตามข้อตกลงจริง |
| Validation | ตรวจว่าข้อมูลผ่านเงื่อนไขหรือไม่; ต้องระบุว่าตรวจ rule ใดที่ไหน |
| DTO (Data Transfer Object) | รูปข้อมูลเพื่อส่งข้าม boundary ไม่ได้แปลว่าผ่านกฎ domain แล้ว |
| HTTP / SQL | วิธีคุย request-response ทางเว็บ / ภาษาที่ใช้กับฐานข้อมูลบางชนิด |
| Cache | สำเนาข้อมูลเพื่อเรียกใช้เร็วขึ้น อาจไม่ใช่ข้อมูลล่าสุดเสมอ |
| Policy / assumption | กติกาที่ตกลง / สิ่งที่สมมติว่าจริง ต้องเขียนให้เห็นก่อนใช้ |
| Contract | สิ่งที่ผู้เรียกกับผู้ทำตกลงเกี่ยวกับ input/output/พฤติกรรม |
| Happy path / edge case | ทางที่ทุกอย่างสำเร็จ / กรณีขอบ เช่นถอนพอดียอดหรือ amount=0 |
| Assertion / regression test | การตรวจความคาดหวัง / test เก็บกรณี bug ไว้กันกลับมาอีก |
| Serialization / deserialize | แปลงข้อมูลเป็นรูปส่งหรือเก็บ / อ่านรูปนั้นกลับเป็น object |
| Persistence / publication | บันทึกข้อมูลให้อยู่ต่อ / ส่ง event ให้ผู้รับ |
| Concurrency / race condition | งานหลายตัวเกิดทับช่วงกัน / ผลขึ้นกับจังหวะที่งานแข่งกัน |

คำคล้ายที่มักสับสน:

- Smart constructor สร้างค่า; smart cast เป็นการรู้ type หลังตรวจ
- Pure function เป็นเรื่อง side effect; pure ใน Monad คือ constructor ยกค่าเข้าบริบท
- Property ของ class คือ field; property ในการ testing คือข้อความของกฎ
- Domain event ที่ core สร้างไม่เท่ากับ event ที่ persist/publish แล้ว
- Type ที่หน้าตา valid ไม่ได้พิสูจน์ว่าผ่าน workflow หรือ database transaction มาแล้ว
