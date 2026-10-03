package io.typebanking.arrowlesson

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import io.typebanking.Either as HandwrittenEither
import io.typebanking.flatMap as handwrittenFlatMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/** Finite samples illustrate laws; these tests are not a mathematical proof. */
class ArrowLawsTest {
    private val samples: List<Either<String, Int>> =
        (-30..30).map { Either.Right(it) } + Either.Left("rejected")
    private fun pure(value: Int): Either<String, Int> = Either.Right(value)
    private fun next(value: Int): Either<String, Int> =
        if (value < 0) Either.Left("negative") else Either.Right(value + 1)
    private fun finish(value: Int): Either<String, Int> =
        if (value % 7 == 0) Either.Left("multiple-of-seven") else Either.Right(value * 2)

    @Test fun `functor identity preserves right and left`() {
        samples.forEach { assertEquals(it, it.map { value -> value }) }
    }
    @Test fun `functor composition preserves result and error`() {
        samples.forEach { input ->
            assertEquals(input.map { it + 1 }.map { it * 2 }, input.map { (it + 1) * 2 })
        }
    }
    @Test fun `monad left identity`() {
        (-30..30).forEach { value -> assertEquals(next(value), pure(value).flatMap(::next)) }
    }
    @Test fun `monad right identity`() {
        samples.forEach { assertEquals(it, it.flatMap(::pure)) }
    }
    @Test fun `monad associativity including failures`() {
        samples.forEach { input ->
            assertEquals(
                input.flatMap(::next).flatMap(::finish),
                input.flatMap { value -> next(value).flatMap(::finish) },
            )
        }
    }
    @Test fun `left never runs the next function`() {
        var invoked = false
        val left: Either<String, Int> = Either.Left("stop")
        assertEquals(left, left.flatMap { invoked = true; pure(it + 1) })
        assertFalse(invoked)
    }
    @Test fun `option map and flatMap preserve absence`() {
        val absent: Option<Int> = None
        var invoked = false
        assertEquals(None, absent.map { invoked = true; it + 1 })
        assertEquals(None, absent.flatMap { invoked = true; Some(it + 1) })
        assertFalse(invoked)
        assertEquals(Some(3), Some(1).flatMap { Some(it + 2) })
    }
    @Test fun `map nests contexts while flatMap sequences them`() {
        val mapped = pure(1).map(::next)
        val bound = pure(1).flatMap(::next)
        assertEquals(Either.Right(Either.Right(2)), mapped)
        assertEquals(Either.Right(2), bound)
    }
    @Test fun `handwritten flatMap preserves its error channel`() {
        val left: HandwrittenEither<String, Int> = HandwrittenEither.Left("stop")
        assertEquals(left, left.handwrittenFlatMap { HandwrittenEither.Right(it + 1) })
        val right: HandwrittenEither<String, Int> = HandwrittenEither.Right(1)
        assertEquals(HandwrittenEither.Right(2), right.handwrittenFlatMap { HandwrittenEither.Right(it + 1) })
    }
}
