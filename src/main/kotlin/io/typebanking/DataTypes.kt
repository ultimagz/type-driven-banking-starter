package io.typebanking

sealed interface Option<out A> {
    data class Some<A>(val value: A) : Option<A>
    data object None : Option<Nothing>
    fun <B> map(transform: (A) -> B): Option<B> = when (this) { is Some -> Some(transform(value)); None -> None }
    fun <B> flatMap(transform: (A) -> Option<B>): Option<B> = when (this) { is Some -> transform(value); None -> None }
}

sealed interface Either<out E, out A> {
    data class Left<E>(val error: E) : Either<E, Nothing>
    data class Right<A>(val value: A) : Either<Nothing, A>
    fun <B> map(transform: (A) -> B): Either<E, B> = when (this) { is Left -> this; is Right -> Right(transform(value)) }
}

// Keep one error channel when sequencing; changing E requires explicit mapping.
// An extension avoids unchecked casts in the covariant handwritten data type.
fun <E, A, B> Either<E, A>.flatMap(transform: (A) -> Either<E, B>): Either<E, B> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> transform(value)
    }
