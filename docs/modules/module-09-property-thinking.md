# Module 09 — Property-based thinking

**Concept.** Example tests show examples; properties state rules for many generated cases. Start with invariant even if no generator library is introduced yet.

**Problem.** State properties: deposit never decreases balance; failed withdrawal preserves account; successful transfer preserves total money (ignoring fee).

**Impact.** catches overlooked edge cases and sharpens ambiguous domain rules.

**Before → after.** `deposit 100` example only → quantified invariant over valid values.

**Exercise.** Write loop-based generated test or add a generator library later; include shrinking thought experiment for a failing case.

**Homework.** Specify properties for daily limit and state transition terminality.

**Checkpoint.** distinguish an example, invariant, metamorphic relation; why must generators honor type constructors?

**Discussion prompts.** “อะไรต้องจริงสำหรับทุก input ที่ valid?” “counterexample ที่เล็กที่สุดหน้าตาอย่างไร?”

