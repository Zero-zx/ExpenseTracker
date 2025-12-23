package transaction.usecase

import transaction.repository.LenderRepository
import javax.inject.Inject

class GetLenderByIdUseCase @Inject constructor(
    private val repository: LenderRepository
) {
    suspend operator fun invoke(lenderId: Long) = repository.getLenderById(lenderId)
}

