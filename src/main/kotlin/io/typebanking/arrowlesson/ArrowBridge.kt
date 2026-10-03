package io.typebanking.arrowlesson

import arrow.core.Either as ArrowEither
import arrow.core.None
import arrow.core.Option as ArrowOption
import arrow.core.Some
import io.typebanking.Account
import io.typebanking.AccountId
import io.typebanking.AccountRepository
import io.typebanking.Either as HandwrittenEither
import io.typebanking.Option as HandwrittenOption

/** Migrate a boundary explicitly; domain types and invariants stay unchanged. */
fun <E, A> HandwrittenEither<E, A>.toArrow(): ArrowEither<E, A> = when (this) {
    is HandwrittenEither.Left -> ArrowEither.Left(error)
    is HandwrittenEither.Right -> ArrowEither.Right(value)
}

fun <A> HandwrittenOption<A>.toArrow(): ArrowOption<A> = when (this) {
    is HandwrittenOption.Some -> Some(value)
    HandwrittenOption.None -> None
}

fun interface AccountLookup {
    fun find(id: AccountId): ArrowOption<Account>
}

/** Lookup-only adapter: a preview must never call saveAll. */
class RepositoryLookup(private val repository: AccountRepository) : AccountLookup {
    override fun find(id: AccountId): ArrowOption<Account> = repository.find(id).toArrow()
}
