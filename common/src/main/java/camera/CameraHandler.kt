package camera

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import storage.FileProvider as AppFileProvider
import java.io.File

/**
 * Handles camera and gallery image selection.
 * Uses FileProvider interface to maintain clean architecture and avoid
 * direct dependency on data layer implementations.
 */
class CameraHandler(
    private val fragment: Fragment,
    private val fileProvider: AppFileProvider,
    private val onImageCaptured: (Uri) -> Unit,
    private val onError: (String) -> Unit
) {

    private var tempImageFile: File? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    fun setup() {
        // Camera launcher
        cameraLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && tempImageFile != null) {
                val uri = Uri.fromFile(tempImageFile)
                onImageCaptured(uri)
            } else {
                onError("Failed to capture image")
            }
        }

        // Gallery launcher
        galleryLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                onImageCaptured(uri)
            } else {
                onError("No image selected")
            }
        }
    }

    fun launchCamera() {
        try {
            // Create temp file using interface
            tempImageFile = fileProvider.createTempImageFile()

            // Get URI using FileProvider
            val uri = FileProvider.getUriForFile(
                fragment.requireContext(),
                "${fragment.requireContext().packageName}.fileprovider",
                tempImageFile!!
            )

            // Launch camera
            cameraLauncher.launch(uri)

        } catch (e: Exception) {
            onError("Failed to open camera: ${e.message}")
        }
    }

    fun launchGallery() {
        try {
            galleryLauncher.launch("image/*")
        } catch (e: Exception) {
            onError("Failed to open gallery: ${e.message}")
        }
    }
}