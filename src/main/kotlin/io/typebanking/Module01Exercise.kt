package io.typebanking

import java.math.BigDecimal

// Exercise: replace these signatures with private-constructor value objects and a typed parse result.
fun parseAccountId(raw: String): Any = TODO("Return a valid AccountId or a construction error")
fun parsePositiveMoney(raw: BigDecimal): Any = TODO("Reject zero, negative values, and scale > 2")

