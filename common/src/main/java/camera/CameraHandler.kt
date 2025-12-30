package camera

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import storage.FileProvider as AppFileProvider

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
    private var cameraImageUri: Uri? = null // Lưu FileProvider URI để sử dụng sau
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    fun setup() {
        // Camera launcher
        cameraLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && cameraImageUri != null) {
                // Sử dụng FileProvider URI đã lưu, không dùng Uri.fromFile()
                // KHÔNG xóa file ngay lập tức vì ViewModel sẽ đọc file bất đồng bộ
                // File sẽ được cleanup sau khi đã đọc thành công hoặc bị xóa tự động bởi hệ thống
                onImageCaptured(cameraImageUri!!)
                // Reset references để có thể tạo file mới cho lần chụp tiếp theo
                tempImageFile = null
                cameraImageUri = null
            } else {
                onError("Failed to capture image")
                // Cleanup nếu thất bại
                tempImageFile?.delete()
                tempImageFile = null
                cameraImageUri = null
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

            // Get URI using FileProvider (an toàn từ Android 7.0+)
            val uri = FileProvider.getUriForFile(
                fragment.requireContext(),
                "${fragment.requireContext().packageName}.fileprovider",
                tempImageFile!!
            )

            // Lưu URI để sử dụng trong callback
            cameraImageUri = uri

            // Launch camera
            cameraLauncher.launch(uri)

        } catch (e: Exception) {
            onError("Failed to open camera: ${e.message}")
            // Cleanup nếu có lỗi
            tempImageFile?.delete()
            tempImageFile = null
            cameraImageUri = null
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