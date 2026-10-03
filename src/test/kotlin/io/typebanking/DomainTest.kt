package io.typebanking

import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DomainTest {
    private val at = Instant.parse("2026-01-01T00:00:00Z")
    private fun id(value: String): AccountId = when (val parsed = AccountId.parse(value)) {
        is Either.Right -> parsed.value
        is Either.Left -> error("fixture account id was invalid: ${parsed.error}")
    }

    private fun money(value: String): PositiveMoney = when (val parsed = PositiveMoney.parse(BigDecimal(value))) {
        is Either.Right -> parsed.value
        is Either.Left -> error("fixture money was invalid: ${parsed.error}")
    }

    @Test fun `positive money rejects zero and over precision`() {
        assertIs<Either.Left<ConstructionError>>(PositiveMoney.parse(BigDecimal.ZERO))
        assertIs<Either.Left<ConstructionError>>(PositiveMoney.parse(BigDecimal("1.001")))
    }

    @Test fun `frozen account returns typed rejection`() {
        val account = Account(id("a"), Balance.of(BigDecimal("20.00")), AccountStatus.Frozen)
        assertEquals(WithdrawOutcome.Rejected.Frozen, account.withdraw(money("5.00"), at))
    }

    @Test fun `successful transfer preserves total balance`() {
        val source = Account(id("a"), Balance.of(BigDecimal("100.00")), AccountStatus.Active)
        val destination = Account(id("b"), Balance.of(BigDecimal("30.00")), AccountStatus.Active)
        val result = decideTransfer(source, destination, money("25.00"), at) as TransferOutcome.Success
        assertEquals(BigDecimal("130.00"), result.source.balance.value + result.destination.balance.value)
    }

    @Test fun `terminal transaction rejects another transition`() {
        val id = when (val parsed = TransactionId.parse("t")) {
            is Either.Right -> parsed.value
            is Either.Left -> error("fixture transaction id was invalid: ${parsed.error}")
        }
        val transaction = Transaction.Succeeded(id, at)
        assertIs<Transition.Rejected>(transaction.transition(TransactionCommand.Start, at))
    }

    @Test fun `property deposit never reduces a nonnegative balance`() {
        val account = Account(id("a"), Balance.zero, AccountStatus.Active)
        (1..100).forEach { pennies ->
            val deposited = account.deposit(money("$pennies.00"))
            check(deposited.balance.value >= account.balance.value)
        }
    }
}
