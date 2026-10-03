# Module 06 — Functional core, imperative shell

**Concept.** Domain decision เป็น pure function: same inputs → same outputs; clock/ID/I/O ส่งเข้ามาทาง parameter/port.

**Problem.** ย้าย time และ repository call ออกจาก transfer decision; inject `Instant`/policy.

**Impact.** deterministic tests, replayable decisions, และ side effects อยู่ขอบระบบ.

**Before → after.** `Instant.now()` ใน entity → `decide(command, now)`.

**Exercise.** ทำ transfer decision ให้ไม่มี mutation/I/O และทดสอบด้วย fixed instant.

**Homework.** Refactor fee calculation ที่เรียก remote config ให้เป็น pure policy input.

**Checkpoint.** function นี้ pure หรือไม่: random ID, DB query, `now`, pure calculation? อธิบาย.

**Discussion prompts.** “ผลลัพธ์ test ซ้ำได้ไหม?” “dependency ไหนเป็นโลกภายนอก?”

