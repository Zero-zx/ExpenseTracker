package storage

import java.io.File

/**
 * Interface for file operations to maintain clean architecture.
 * Implemented by FileManager in the data layer.
 */
interface FileProvider {
    /**
     * Creates a temporary image file in cache directory.
     * Used by camera to store captured photos temporarily.
     */
    fun createTempImageFile(): File
}

