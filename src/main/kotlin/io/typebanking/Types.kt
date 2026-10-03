package io.typebanking

import java.math.BigDecimal
import java.math.RoundingMode

sealed interface ConstructionError {
    data class Blank(val field: String) : ConstructionError
    data class InvalidMoney(val value: BigDecimal) : ConstructionError
}

class AccountId private constructor(val value: String) {
    companion object {
        fun parse(raw: String): Either<ConstructionError, AccountId> =
            if (raw.isBlank()) Either.Left(ConstructionError.Blank("accountId")) else Either.Right(AccountId(raw.trim()))
    }
    override fun equals(other: Any?) = other is AccountId && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = value
}

class TransactionId private constructor(val value: String) {
    companion object {
        fun parse(raw: String): Either<ConstructionError, TransactionId> =
            if (raw.isBlank()) Either.Left(ConstructionError.Blank("transactionId")) else Either.Right(TransactionId(raw.trim()))
    }
    override fun equals(other: Any?) = other is TransactionId && value == other.value
    override fun hashCode() = value.hashCode()
}

class PositiveMoney private constructor(val value: BigDecimal) {
    companion object {
        fun parse(raw: BigDecimal): Either<ConstructionError, PositiveMoney> =
            if (raw <= BigDecimal.ZERO || raw.scale() > 2) Either.Left(ConstructionError.InvalidMoney(raw))
            else Either.Right(PositiveMoney(raw.setScale(2, RoundingMode.UNNECESSARY)))
    }
}

@ConsistentCopyVisibility
data class Balance private constructor(val value: BigDecimal) {
    companion object {
        val zero = Balance(BigDecimal.ZERO.setScale(2))
        fun of(value: BigDecimal): Balance = Balance(value.setScale(2, RoundingMode.UNNECESSARY))
    }
    fun add(amount: PositiveMoney) = Balance(value + amount.value)
    fun subtract(amount: PositiveMoney) = Balance(value - amount.value)
    fun canCover(amount: PositiveMoney) = value >= amount.value
}
