package io.typebanking

import java.time.Clock

interface AccountRepository {
    fun find(id: AccountId): Option<Account>
    fun saveAll(accounts: List<Account>)
}

sealed interface TransferUseCaseOutcome { data class Completed(val result: TransferOutcome.Success) : TransferUseCaseOutcome; data object SourceMissing : TransferUseCaseOutcome; data object DestinationMissing : TransferUseCaseOutcome; data class Rejected(val reason: TransferOutcome.Rejected) : TransferUseCaseOutcome }

class TransferUseCase(private val repository: AccountRepository, private val clock: Clock) {
    fun execute(sourceId: AccountId, destinationId: AccountId, amount: PositiveMoney): TransferUseCaseOutcome {
        val source = when (val found = repository.find(sourceId)) { is Option.Some -> found.value; Option.None -> return TransferUseCaseOutcome.SourceMissing }
        val destination = when (val found = repository.find(destinationId)) { is Option.Some -> found.value; Option.None -> return TransferUseCaseOutcome.DestinationMissing }
        return when (val decision = decideTransfer(source, destination, amount, clock.instant())) {
            is TransferOutcome.Success -> { repository.saveAll(listOf(decision.source, decision.destination)); TransferUseCaseOutcome.Completed(decision) }
            is TransferOutcome.Rejected -> TransferUseCaseOutcome.Rejected(decision)
        }
    }
}
