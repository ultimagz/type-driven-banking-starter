package io.typebanking

import java.time.Instant

sealed interface AccountStatus { data object Active : AccountStatus; data object Frozen : AccountStatus; data object Closed : AccountStatus }

data class Account(val id: AccountId, val balance: Balance, val status: AccountStatus) {
    fun withdraw(amount: PositiveMoney, at: Instant): WithdrawOutcome = when {
        status is AccountStatus.Frozen -> WithdrawOutcome.Rejected.Frozen
        status is AccountStatus.Closed -> WithdrawOutcome.Rejected.Closed
        !balance.canCover(amount) -> WithdrawOutcome.Rejected.InsufficientFunds
        else -> WithdrawOutcome.Success(copy(balance = balance.subtract(amount)), MoneyWithdrawn(id, amount, at))
    }
    fun deposit(amount: PositiveMoney) = copy(balance = balance.add(amount))
}

sealed interface WithdrawOutcome {
    data class Success(val account: Account, val event: MoneyWithdrawn) : WithdrawOutcome
    sealed interface Rejected : WithdrawOutcome { data object InsufficientFunds : Rejected; data object Frozen : Rejected; data object Closed : Rejected }
}
data class MoneyWithdrawn(val accountId: AccountId, val amount: PositiveMoney, val occurredAt: Instant)

sealed interface TransferOutcome {
    data class Success(val source: Account, val destination: Account, val events: List<MoneyWithdrawn>) : TransferOutcome
    sealed interface Rejected : TransferOutcome { data object SameAccount : Rejected; data class Source(val reason: WithdrawOutcome.Rejected) : Rejected; data object DestinationUnavailable : Rejected }
}

fun decideTransfer(source: Account, destination: Account, amount: PositiveMoney, at: Instant): TransferOutcome {
    if (source.id == destination.id) return TransferOutcome.Rejected.SameAccount
    if (destination.status != AccountStatus.Active) return TransferOutcome.Rejected.DestinationUnavailable
    return when (val withdrawal = source.withdraw(amount, at)) {
        is WithdrawOutcome.Success -> TransferOutcome.Success(withdrawal.account, destination.deposit(amount), listOf(withdrawal.event))
        is WithdrawOutcome.Rejected -> TransferOutcome.Rejected.Source(withdrawal)
    }
}
