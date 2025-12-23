package transaction.usecase

import transaction.repository.BorrowerRepository
import javax.inject.Inject

class GetBorrowerByIdUseCase @Inject constructor(
    private val repository: BorrowerRepository
) {
    suspend operator fun invoke(borrowerId: Long) = repository.getBorrowerById(borrowerId)
}

