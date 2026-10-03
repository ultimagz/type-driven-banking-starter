package io.typebanking.arrowlesson

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.raise.either
import io.typebanking.Account
import io.typebanking.AccountId
import io.typebanking.PositiveMoney
import io.typebanking.TransferOutcome
import io.typebanking.decideTransfer
import java.time.Instant

fun parseAmount(raw: String): Either<PreviewError, PositiveMoney> =
    TODO("Exercise 1: parse decimal input, reuse PositiveMoney.parse, mapLeft to PreviewError")

fun requireAccount(
    found: Option<Account>,
    missing: PreviewError,
): Either<PreviewError, Account> = TODO("Exercise 2: use Option.fold; preserve the caller-supplied missing error")

/**
 * Read-only preview. Both variants reuse the exact rules from M03-M08.
 * Explicit time preserves deterministic decisions. No persistence occurs here.
 */
class TransferPreview(private val accounts: AccountLookup) {
    fun withFlatMap(raw: RawTransfer, at: Instant): Either<PreviewError, TransferOutcome.Success> =
        TODO("Exercise 3: compose parse -> lookup -> decide with flatMap and one error channel")

    fun withRaise(raw: RawTransfer, at: Instant): Either<PreviewError, TransferOutcome.Success> =
        TODO("Exercise 4: translate the same sequence to either { bind() }")

    private fun parseSourceId(raw: String): Either<PreviewError, AccountId> =
        AccountId.parse(raw).toArrow().mapLeft { PreviewError.SourceIdInvalid(it) }

    private fun parseDestinationId(raw: String): Either<PreviewError, AccountId> =
        AccountId.parse(raw).toArrow().mapLeft { PreviewError.DestinationIdInvalid(it) }

    private fun decide(
        source: Account,
        destination: Account,
        amount: PositiveMoney,
        at: Instant,
    ): Either<PreviewError, TransferOutcome.Success> =
        when (val decision = decideTransfer(source, destination, amount, at)) {
            is TransferOutcome.Success -> Either.Right(decision)
            is TransferOutcome.Rejected -> Either.Left(PreviewError.Rejected(decision))
        }
}
