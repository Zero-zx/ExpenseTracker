package transaction.usecase

import transaction.model.Borrower
import transaction.repository.BorrowerRepository
import javax.inject.Inject

class DeleteBorrowerUseCase @Inject constructor(
    private val repository: BorrowerRepository
) {
    suspend operator fun invoke(borrower: Borrower) = repository.deleteBorrower(borrower)
}

