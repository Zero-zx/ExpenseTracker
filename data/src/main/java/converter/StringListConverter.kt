package converter

import android.util.Log
import androidx.room.TypeConverter

/**
 * TypeConverter để convert List<String> ↔ String
 * Sử dụng Unit Separator (U+001F) làm delimiter để tránh conflict với tên người
 */
class StringListConverter {
    companion object {
        private const val TAG = "StringListConverter"
        // Unit Separator character - ít khi xuất hiện trong tên người
        private const val DELIMITER = "\u001F"
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.takeIf { it.isNotEmpty() }
            ?.joinToString(DELIMITER) { it.trim() }
            ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return when {
            value.isNullOrBlank() -> emptyList()
            else -> {
                try {
                    value.split(DELIMITER)
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse string list", e)
                    emptyList()
                }
            }
        }
    }
}

