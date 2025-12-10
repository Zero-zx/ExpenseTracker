package transaction.repository

import android.net.Uri
import transaction.model.TransactionImage

/**
 * Repository interface for managing transaction images.
 * Follows Clean Architecture - interface in domain, implementation in data.
 */
interface TransactionImageRepository {
    /**
     * Save an image from URI (camera or gallery).
     * Returns Result with TransactionImage containing file metadata.
     */
    suspend fun saveImage(sourceUri: Uri): Result<TransactionImage>

    /**
     * Delete a single transaction image.
     */
    suspend fun deleteImage(image: TransactionImage): Result<Unit>

    /**
     * Delete multiple transaction images.
     */
    suspend fun deleteImages(images: List<TransactionImage>): Result<Unit>
}

