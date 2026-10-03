package io.typebanking

import java.time.Instant

sealed interface Transaction {
    val id: TransactionId
    data class Pending(override val id: TransactionId) : Transaction
    data class Processing(override val id: TransactionId, val startedAt: Instant) : Transaction
    data class Succeeded(override val id: TransactionId, val completedAt: Instant) : Transaction
    data class Failed(override val id: TransactionId, val reason: FailureReason, val failedAt: Instant) : Transaction
}
enum class FailureReason { NETWORK, DECLINED }
sealed interface TransactionCommand { data object Start : TransactionCommand; data object Succeed : TransactionCommand; data class Fail(val reason: FailureReason) : TransactionCommand }
sealed interface Transition { data class Accepted(val transaction: Transaction) : Transition; data class Rejected(val state: Transaction, val command: TransactionCommand) : Transition }

fun Transaction.transition(command: TransactionCommand, now: Instant): Transition = when (this) {
    is Transaction.Pending -> if (command is TransactionCommand.Start) Transition.Accepted(Transaction.Processing(id, now)) else Transition.Rejected(this, command)
    is Transaction.Processing -> when (command) { TransactionCommand.Succeed -> Transition.Accepted(Transaction.Succeeded(id, now)); is TransactionCommand.Fail -> Transition.Accepted(Transaction.Failed(id, command.reason, now)); TransactionCommand.Start -> Transition.Rejected(this, command) }
    is Transaction.Succeeded, is Transaction.Failed -> Transition.Rejected(this, command)
}
