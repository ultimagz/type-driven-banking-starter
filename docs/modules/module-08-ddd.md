# Module 08 — DDD in practice

**Concept.** Entity มี identity; value object มี equality by value; aggregate root guards consistency; domain event states facts that happened.

**Problem.** Make Account aggregate root. On successful withdrawal/transfer emit `MoneyWithdrawn`/`TransferRecorded` event without publishing it from domain.

**Impact.** rule ownership ชัด, transaction boundary ชัด, event can drive adapters.

**Before → after.** anemic DTO + service rules → aggregate behavior + events.

**Exercise.** ระบุ aggregate boundary และ invariant ที่ต้อง atomic; add event to successful operation.

**Homework.** Write ubiquitous-language glossary for Account, AvailableBalance, PostedTransaction, Transfer.

**Checkpoint.** account กับ transaction อยู่ aggregate เดียวกันไหม? ใช้ consistency rule เป็นเหตุผล ไม่ใช่คำตอบตายตัว.

**Discussion prompts.** “คำนี้หมายความตรงกันสำหรับ product/dev ไหม?” “event คือ command ที่ผ่านมาแล้วหรือ fact ที่เกิดแล้ว?”

