package transaction.usecase

import android.net.Uri
import transaction.model.TransactionImage
import transaction.repository.TransactionImageRepository
import javax.inject.Inject

/**
 * Use case for saving transaction images.
 * Handles image compression, resizing, and storage.
 */
class SaveTransactionImageUseCase @Inject constructor(
    private val imageRepository: TransactionImageRepository
) {
    suspend operator fun invoke(sourceUri: Uri): Result<TransactionImage> {
        return imageRepository.saveImage(sourceUri)
    }
}

