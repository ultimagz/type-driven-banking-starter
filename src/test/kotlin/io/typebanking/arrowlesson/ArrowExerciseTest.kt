package io.typebanking.arrowlesson

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.typebanking.Account
import io.typebanking.AccountId
import io.typebanking.AccountRepository
import io.typebanking.AccountStatus
import io.typebanking.Balance
import io.typebanking.ConstructionError
import io.typebanking.Option as HandwrittenOption
import io.typebanking.TransferOutcome
import io.typebanking.WithdrawOutcome
import org.junit.jupiter.api.Tag
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

@Tag("exercise")
class ArrowExerciseTest {
    private val at = Instant.parse("2026-01-01T00:00:00Z")
    private val valid = RawTransfer("source", "destination", "25.00")
    private fun id(raw: String): AccountId = AccountId.parse(raw).toArrow().fold(
        ifLeft = { error("Invalid fixture: $it") },
        ifRight = { it },
    )
    private fun account(raw: String, status: AccountStatus = AccountStatus.Active, balance: String = "100.00") =
        Account(id(raw), Balance.of(BigDecimal(balance)), status)

    private class FakeLookup(private val accounts: Map<AccountId, Account>) : AccountLookup {
        val calls = mutableListOf<String>()
        override fun find(id: AccountId): Option<Account> {
            calls += id.value
            return accounts[id]?.let { Some(it) } ?: None
        }
    }
    private fun defaults() = listOf(account("source"), account("destination", balance = "30.00"))
        .associateBy { it.id }

    private fun invoke(
        style: String, preview: TransferPreview, raw: RawTransfer,
    ): Either<PreviewError, TransferOutcome.Success> =
        if (style == "flatMap") preview.withFlatMap(raw, at) else preview.withRaise(raw, at)

    private fun assertFailure(
        raw: RawTransfer,
        expected: PreviewError,
        expectedLookups: List<String>,
        accounts: Map<AccountId, Account> = defaults(),
    ) {
        for (style in listOf("flatMap", "raise")) {
            val lookup = FakeLookup(accounts)
            assertEquals(Either.Left(expected), invoke(style, TransferPreview(lookup), raw), style)
            assertEquals(expectedLookups, lookup.calls, style)
        }
    }

    @Test fun `amount parsing distinguishes malformed from invalid domain money`() {
        assertEquals(Either.Left(PreviewError.AmountNotDecimal("oops")), parseAmount("oops"))
        for (invalid in listOf("0.00", "-1.00", "1.001")) {
            val result = parseAmount(invalid)
            assertEquals(
                Either.Left(PreviewError.AmountInvalid(ConstructionError.InvalidMoney(BigDecimal(invalid)))),
                result,
            )
        }
        assertEquals(BigDecimal("25.00"), parseAmount("25").fold({ error("$it") }, { it.value }))
    }
    @Test fun `option absence becomes the precise required-account error`() {
        assertEquals(Either.Left(PreviewError.SourceMissing), requireAccount(None, PreviewError.SourceMissing))
        val found = account("source")
        assertEquals(Either.Right(found), requireAccount(Some(found), PreviewError.SourceMissing))
    }
    @Test fun `blank source id fails before lookup`() {
        assertFailure(valid.copy(sourceId = " "), PreviewError.SourceIdInvalid(ConstructionError.Blank("accountId")), emptyList())
    }
    @Test fun `blank destination id fails before lookup`() {
        assertFailure(valid.copy(destinationId = ""), PreviewError.DestinationIdInvalid(ConstructionError.Blank("accountId")), emptyList())
    }
    @Test fun `malformed amount prevents all lookups`() {
        assertFailure(valid.copy(amount = "NaN"), PreviewError.AmountNotDecimal("NaN"), emptyList())
    }
    @Test fun `nonpositive or overprecise amount prevents all lookups`() {
        for (invalid in listOf("0", "-1", "1.001")) {
            assertFailure(
                valid.copy(amount = invalid),
                PreviewError.AmountInvalid(ConstructionError.InvalidMoney(BigDecimal(invalid))),
                emptyList(),
            )
        }
    }
    @Test fun `missing source short circuits destination lookup`() {
        assertFailure(valid, PreviewError.SourceMissing, listOf("source"), emptyMap())
    }
    @Test fun `missing destination is distinct from missing source`() {
        assertFailure(valid, PreviewError.DestinationMissing, listOf("source", "destination"), mapOf(id("source") to account("source")))
    }
    @Test fun `same-account rejection comes from the existing domain`() {
        assertFailure(valid.copy(destinationId = "source"), PreviewError.Rejected(TransferOutcome.Rejected.SameAccount), listOf("source", "source"))
    }
    @Test fun `insufficient funds remains a typed rejection`() {
        assertFailure(
            valid.copy(amount = "101.00"),
            PreviewError.Rejected(TransferOutcome.Rejected.Source(WithdrawOutcome.Rejected.InsufficientFunds)),
            listOf("source", "destination"),
        )
    }
    @Test fun `frozen and closed source preserve the domain reason`() {
        for ((status, reason) in listOf(
            AccountStatus.Frozen to WithdrawOutcome.Rejected.Frozen,
            AccountStatus.Closed to WithdrawOutcome.Rejected.Closed,
        )) {
            val accounts = defaults() + (id("source") to account("source", status))
            assertFailure(valid, PreviewError.Rejected(TransferOutcome.Rejected.Source(reason)), listOf("source", "destination"), accounts)
        }
    }
    @Test fun `unavailable destination remains a domain rejection`() {
        val accounts = defaults() + (id("destination") to account("destination", AccountStatus.Closed))
        assertFailure(valid, PreviewError.Rejected(TransferOutcome.Rejected.DestinationUnavailable), listOf("source", "destination"), accounts)
    }
    @Test fun `successful preview preserves money and does not mutate loaded accounts`() {
        for (style in listOf("flatMap", "raise")) {
            val accounts = defaults()
            val lookup = FakeLookup(accounts)
            val result = invoke(style, TransferPreview(lookup), valid)
            val success = assertIs<Either.Right<TransferOutcome.Success>>(result).value
            assertEquals(BigDecimal("75.00"), success.source.balance.value)
            assertEquals(BigDecimal("55.00"), success.destination.balance.value)
            assertEquals(BigDecimal("130.00"), success.source.balance.value + success.destination.balance.value)
            assertEquals(BigDecimal("100.00"), accounts.getValue(id("source")).balance.value)
            assertEquals(at, success.events.single().occurredAt)
            assertEquals(BigDecimal("25.00"), success.events.single().amount.value)
            assertEquals(listOf("source", "destination"), lookup.calls)
        }
    }
    @Test fun `repository adapter never saves during preview`() {
        val accounts = defaults()
        var saved = false
        val repository = object : AccountRepository {
            override fun find(id: AccountId): HandwrittenOption<Account> =
                accounts[id]?.let { HandwrittenOption.Some(it) } ?: HandwrittenOption.None
            override fun saveAll(accounts: List<Account>) { saved = true; error("Preview must not save") }
        }
        assertTrue(TransferPreview(RepositoryLookup(repository)).withRaise(valid, at).isRight())
        assertEquals(false, saved)
    }
    @Test fun `unexpected infrastructure failure is not disguised as missing account`() {
        val broken = AccountLookup { throw IllegalStateException("database unavailable") }
        for (style in listOf("flatMap", "raise")) {
            assertFailsWith<IllegalStateException> { invoke(style, TransferPreview(broken), valid) }
        }
    }
    @Test fun `flatMap and Raise agree across valid generated amounts`() {
        for (cents in 1..100) {
            val raw = valid.copy(amount = BigDecimal(cents).movePointLeft(2).toPlainString())
            val first = TransferPreview(FakeLookup(defaults())).withFlatMap(raw, at)
            val second = TransferPreview(FakeLookup(defaults())).withRaise(raw, at)
            // PositiveMoney does not yet define value equality; compare observable results.
            fun snapshot(result: Either<PreviewError, TransferOutcome.Success>): String = result.fold(
                ifLeft = { "error:$it" },
                ifRight = { "${it.source.balance.value}:${it.destination.balance.value}:${it.events.single().amount.value}:${it.events.single().occurredAt}" },
            )
            assertEquals(snapshot(first), snapshot(second))
        }
    }
}
