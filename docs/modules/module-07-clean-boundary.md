# Module 07 — Clean architecture boundary

**Concept.** Use case orchestrates ports; domain does not depend on database/http/framework. Failures from ports stay distinct from business decisions.

**Problem.** Add `AccountRepository`, `TransferUseCase`, and output model. Keep repository as interface owned by application/domain side.

**Impact.** adapters replaceable, use case testable with fake repository, and domain vocabulary remains framework-free.

**Before → after.** service calls DB directly → use case loads, decides, saves through port.

**Exercise.** Implement transfer atomically at use-case boundary; map `NotFound` and persistence failure explicitly.

**Homework.** Sketch REST adapter mapping input parse errors, domain outcomes, and infrastructure failures.

**Checkpoint.** repository interface belongs to which side and why? Which errors should cross domain boundary?

**Discussion prompts.** “ถอด adapter นี้ออก domain compile ไหม?” “port language เป็น business language หรือ database language?”

