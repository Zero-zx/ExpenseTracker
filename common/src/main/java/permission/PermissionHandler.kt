package permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionHandler(
    private val fragment: Fragment,
    private val onGranted: () -> Unit,
    private val onDenied: () -> Unit
) {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var currentPermissionType: PermissionType? = null // Track loại permission đang request

    private enum class PermissionType {
        CAMERA,
        GALLERY
    }

    fun setup(launcher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher = launcher
    }

    // Check and request camera permission
    fun checkCameraPermission() {
        currentPermissionType = PermissionType.CAMERA
        when {
            hasCameraPermission() -> onGranted()
            fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show rationale
                showPermissionRationale()
            }

            else -> requestCameraPermission()
        }
    }

    // Check and request gallery permission (different for Android 13+)
    fun checkGalleryPermission() {
        currentPermissionType = PermissionType.GALLERY
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            hasGalleryPermission() -> onGranted()
            fragment.shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationale()
            }

            else -> requestGalleryPermission()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasGalleryPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
    }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        permissionLauncher.launch(permission)
    }

    private fun showPermissionRationale() {
        // Show dialog explaining why permission is needed
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage("This permission is needed to attach photos to your transactions.")
            .setPositiveButton("Grant") { _, _ ->
                // Gọi đúng method dựa trên loại permission đang request
                when (currentPermissionType) {
                    PermissionType.CAMERA -> requestCameraPermission()
                    PermissionType.GALLERY -> requestGalleryPermission()
                    null -> onDenied() // Fallback nếu không có type
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                onDenied()
            }
            .show()
    }

    fun handlePermissionResult(permissions: Map<String, Boolean>) {
        if (permissions.values.all { it }) {
            onGranted()
        } else {
            // Check nếu user đã chọn "Don't ask again"
            val shouldShowRationale = permissions.keys.any { permission ->
                fragment.shouldShowRequestPermissionRationale(permission)
            }

            if (!shouldShowRationale) {
                // User đã chọn "Don't ask again" - có thể show dialog hướng dẫn mở Settings
                showSettingsDialog()
            } else {
                onDenied()
            }
        }
        // Reset permission type sau khi xử lý
        currentPermissionType = null
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage("Permission has been permanently denied. Please enable it in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                try {
                    val intent =
                        android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .apply {
                                data = android.net.Uri.fromParts(
                                    "package",
                                    fragment.requireContext().packageName,
                                    null
                                )
                            }
                    fragment.startActivity(intent)
                } catch (e: Exception) {
                    onDenied()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                onDenied()
            }
            .show()
    }
}