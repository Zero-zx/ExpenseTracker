package repository

import android.net.Uri
import dao.TransactionImageDao
import datasource.storage.FileManager
import model.TransactionImageEntity
import transaction.model.TransactionImage
import transaction.repository.TransactionImageRepository
import javax.inject.Inject

/**
 * Implementation of TransactionImageRepository.
 * Delegates to FileManager for actual file operations.
 */
internal class TransactionImageRepositoryImpl @Inject constructor(
    private val fileManager: FileManager,
    private val imageDao: TransactionImageDao
) : TransactionImageRepository {

    override suspend fun saveImage(sourceUri: Uri): Result<TransactionImage> {
        return fileManager.saveImage(sourceUri)
    }

    override suspend fun insertImage(image: TransactionImage): Result<Long> {
        return try {
            val entity = TransactionImageEntity(
                id = image.id,
                transactionId = image.transactionId,
                filePath = image.filePath,
                fileName = image.fileName,
                mimeType = image.mimeType,
                fileSize = image.fileSize,
                createdAt = image.createdAt
            )
            val imageId = imageDao.insertImage(entity)
            Result.success(imageId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(image: TransactionImage): Result<Unit> {
        return fileManager.deleteImage(image)
    }

    override suspend fun deleteImages(images: List<TransactionImage>): Result<Unit> {
        return fileManager.deleteImages(images)
    }
}

