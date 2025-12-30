package datasource.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import camera.ImageCompressor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import storage.FileProvider
import transaction.model.TransactionImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageCompressor: ImageCompressor
) : FileProvider {

    companion object {
        private const val IMAGES_DIR = "transaction_images"
        private const val MAX_IMAGE_SIZE = 1920 // Max width/height in pixels
        private const val JPEG_QUALITY = 85 // Compression quality
    }

    // Get images directory (app-specific storage)
    private fun getImagesDirectory(): File {
        val dir = File(context.filesDir, IMAGES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    // Generate unique filename
    private fun generateFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "IMG_${timestamp}_${UUID.randomUUID().toString().take(8)}.jpg"
    }

    // Create temporary file for camera (in cache)
    override fun createTempImageFile(): File {
        val fileName = "temp_${System.currentTimeMillis()}.jpg"
        return File(context.cacheDir, fileName)
    }

    // Save image from URI (gallery or camera)
    suspend fun saveImage(sourceUri: Uri): Result<TransactionImage> = withContext(Dispatchers.IO) {
        try {
            // Load bitmap from URI
            val bitmap = context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return@withContext Result.failure(IOException("Failed to load image"))

            // Compress and resize
            val compressedBitmap = imageCompressor.compress(bitmap, MAX_IMAGE_SIZE)

            // Generate filename and get destination
            val fileName = generateFileName()
            val destFile = File(getImagesDirectory(), fileName)

            // Save compressed bitmap
            FileOutputStream(destFile).use { outputStream ->
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                outputStream.flush()
            }

            // Create TransactionImage model
            val image = TransactionImage(
                transactionId = 0, // Will be set when transaction is saved
                filePath = "$IMAGES_DIR/$fileName",
                fileName = fileName,
                mimeType = "image/jpeg",
                fileSize = destFile.length(),
                createdAt = System.currentTimeMillis()
            )

            // Cleanup
            bitmap.recycle()
            compressedBitmap.recycle()

            // Cleanup temp file trong cache directory sau khi đã copy thành công
            // Tìm và xóa file tạm mới nhất (file có timestamp gần nhất với thời điểm hiện tại)
            try {
                val cacheDir = context.cacheDir
                if (cacheDir.exists() && cacheDir.isDirectory) {
                    val tempFiles = cacheDir.listFiles()?.filter { file ->
                        file.isFile && file.name.startsWith("temp_") && file.name.endsWith(".jpg")
                    } ?: emptyList()
                    
                    // Xóa file tạm mới nhất (file có timestamp lớn nhất trong tên file)
                    tempFiles.maxByOrNull { file ->
                        try {
                            // Extract timestamp from filename: temp_<timestamp>.jpg
                            file.name.removePrefix("temp_").removeSuffix(".jpg").toLongOrNull() ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }?.delete()
                }
            } catch (e: Exception) {
                // Ignore cleanup errors
            }

            Result.success(image)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete image file
    suspend fun deleteImage(image: TransactionImage): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = image.getFullPath(context)
            if (file.exists()) {
                file.delete()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete multiple images
    suspend fun deleteImages(images: List<TransactionImage>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            images.forEach { image ->
                val file = image.getFullPath(context)
                if (file.exists()) {
                    file.delete()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get file size in human-readable format
    fun getReadableFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024
        if (kb < 1024) return "$kb KB"
        val mb = kb / 1024
        return "$mb MB"
    }
}