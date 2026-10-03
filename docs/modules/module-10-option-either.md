# Module 10 — Option/Either composition

**Concept.** `Option` models intentional absence; `Either<E, A>` models one typed failure channel. Do not use either to disguise programming bugs.

**Problem.** Implement find-account as `Option`, parsing/use-case chain as `Either`, with `map`/`flatMap`.

**Impact.** removes null checks and nested early returns while retaining explicit failure type.

**Before → after.** `Account?` + exception + if ladder → `Option<Account>` / `Either<Error, Outcome>`.

**Exercise.** compose raw request parsing, account lookup, and transfer decision without nullable value escaping.

**Homework.** Decide whether absent optional nickname, missing required account, and unavailable DB use Option, Either, or exception.

**Checkpoint.** explain why `Either<Throwable, A>` is usually too broad for domain behavior; write one `flatMap` chain by hand.

**Discussion prompts.** “absence is normal in this questionไหม?” “caller needs which failure distinctions?”

