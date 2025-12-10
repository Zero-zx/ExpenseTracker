package repository

import android.net.Uri
import datasource.storage.FileManager
import transaction.model.TransactionImage
import transaction.repository.TransactionImageRepository
import javax.inject.Inject

/**
 * Implementation of TransactionImageRepository.
 * Delegates to FileManager for actual file operations.
 */
internal class TransactionImageRepositoryImpl @Inject constructor(
    private val fileManager: FileManager
) : TransactionImageRepository {

    override suspend fun saveImage(sourceUri: Uri): Result<TransactionImage> {
        return fileManager.saveImage(sourceUri)
    }

    override suspend fun deleteImage(image: TransactionImage): Result<Unit> {
        return fileManager.deleteImage(image)
    }

    override suspend fun deleteImages(images: List<TransactionImage>): Result<Unit> {
        return fileManager.deleteImages(images)
    }
}

