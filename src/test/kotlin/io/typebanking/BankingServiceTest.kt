package io.typebanking

import io.typebanking.legacy.Account
import io.typebanking.legacy.BankingService

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BankingServiceTest {
    @Test fun `deposit increases balance`() {
        val account = Account("a-1", BigDecimal("100"), "ACTIVE")
        BankingService().deposit(account, BigDecimal("25"))
        assertEquals(BigDecimal("125"), account.balance)
    }
}
