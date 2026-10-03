# Module 05 — State machine

**Concept.** ADT บอก state ที่มีได้; state machine บอก transition ที่อนุญาต.

**Problem.** เขียน `start`, `succeed`, `fail` ของ transaction โดย invalid transition คืน typed rejection และ terminal states ไปต่อไม่ได้.

**Impact.** workflow ไม่กระจายเป็น if/switch ตาม controller และ test transition table ได้.

**Before → after.** `status = "SUCCESS"` จากที่ไหนก็ได้ → `Transaction.transition(command, now)`.

**Exercise.** สร้าง transition matrix และ tests ทุก cell สำคัญ.

**Homework.** Model card dispute: Opened → Investigating → Resolved/Rejected พร้อม rules.

**Checkpoint.** differentiate state, command, event; transition ใดควร reject และเพราะอะไร?

**Discussion prompts.** “ใครอนุญาต transition นี้?” “terminal state คืออะไร?”

