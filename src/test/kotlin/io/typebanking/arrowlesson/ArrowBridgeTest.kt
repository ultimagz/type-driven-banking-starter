package io.typebanking.arrowlesson

import arrow.core.Either as ArrowEither
import arrow.core.None
import arrow.core.Some
import io.typebanking.Either as HandwrittenEither
import io.typebanking.Option as HandwrittenOption
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrowBridgeTest {
    @Test fun `right conversion preserves the value`() {
        assertEquals(ArrowEither.Right(42), HandwrittenEither.Right(42).toArrow())
    }
    @Test fun `left conversion preserves the typed error`() {
        assertEquals(ArrowEither.Left("invalid"), HandwrittenEither.Left("invalid").toArrow())
    }
    @Test fun `some conversion preserves the value`() {
        assertEquals(Some("account"), HandwrittenOption.Some("account").toArrow())
    }
    @Test fun `none stays absent`() {
        assertEquals(None, HandwrittenOption.None.toArrow())
    }
}
