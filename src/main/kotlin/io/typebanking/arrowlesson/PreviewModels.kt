package io.typebanking.arrowlesson

import io.typebanking.ConstructionError
import io.typebanking.TransferOutcome

data class RawTransfer(val sourceId: String, val destinationId: String, val amount: String)

sealed interface PreviewError {
    data class SourceIdInvalid(val reason: ConstructionError) : PreviewError
    data class DestinationIdInvalid(val reason: ConstructionError) : PreviewError
    data class AmountNotDecimal(val raw: String) : PreviewError
    data class AmountInvalid(val reason: ConstructionError) : PreviewError
    data object SourceMissing : PreviewError
    data object DestinationMissing : PreviewError
    data class Rejected(val reason: TransferOutcome.Rejected) : PreviewError
}
