package transaction.usecase

import transaction.model.Lender
import transaction.repository.LenderRepository
import javax.inject.Inject

class DeleteLenderUseCase @Inject constructor(
    private val repository: LenderRepository
) {
    suspend operator fun invoke(lender: Lender) = repository.deleteLender(lender)
}

