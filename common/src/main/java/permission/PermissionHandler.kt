package permission

import android.Manifest
import android.content.Context
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

    fun setup(launcher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher = launcher
    }

    // Check and request camera permission
    fun checkCameraPermission() {
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
                requestCameraPermission()
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
            onDenied()
        }
    }
}