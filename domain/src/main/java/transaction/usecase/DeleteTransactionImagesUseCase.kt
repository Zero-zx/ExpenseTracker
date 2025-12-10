package transaction.usecase

import transaction.model.TransactionImage
import transaction.repository.TransactionImageRepository
import javax.inject.Inject

/**
 * Use case for deleting transaction images.
 */
class DeleteTransactionImagesUseCase @Inject constructor(
    private val imageRepository: TransactionImageRepository
) {
    suspend operator fun invoke(images: List<TransactionImage>): Result<Unit> {
        return imageRepository.deleteImages(images)
    }

    suspend fun deleteSingle(image: TransactionImage): Result<Unit> {
        return imageRepository.deleteImage(image)
    }
}

