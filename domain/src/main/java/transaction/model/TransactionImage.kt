package transaction.model

import android.content.Context
import java.io.File

data class TransactionImage(
    val id: Long = 0,
    val transactionId: Long,
    val filePath: String,
    val fileName: String,
    val mimeType: String = "image/jpeg",
    val fileSize: Long,
    val createdAt: Long
) {
    fun getFullPath(context: Context): File {
        return File(context.filesDir, filePath)
    }
}