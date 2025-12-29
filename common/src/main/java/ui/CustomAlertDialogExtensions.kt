package ui

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.common.R

/**
 * Show a simple alert dialog
 */
fun Context.showAlert(
    title: String,
    message: String,
    positiveText: String = "OK",
    onPositive: (() -> Unit)? = null
) {
    CustomAlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setIcon(R.drawable.ic_warning)
        .setPositiveButton(positiveText) { dialog ->
            onPositive?.invoke()
            dialog.dismiss()
        }
        .show()
}

/**
 * Show a confirmation dialog with Yes/No buttons
 */
fun Context.showConfirmation(
    title: String,
    message: String,
    positiveText: String = "Yes",
    negativeText: String = "No",
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    CustomAlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setIcon(R.drawable.ic_warning)
        .setPositiveButton(positiveText) { dialog ->
            onConfirm?.invoke()
            dialog.dismiss()
        }
        .setNegativeButton(negativeText) { dialog ->
            onCancel?.invoke()
            dialog.dismiss()
        }
        .show()
}

/**
 * Show a delete confirmation dialog
 */
fun Context.showDeleteConfirmation(
    itemName: String = "item",
    message: CharSequence,
    onDelete: () -> Unit
) {
    CustomAlertDialog.Builder(this)
        .setTitle("Delete $itemName")
        .setMessage(message)
        .setIcon(R.drawable.ic_warning)
        .setIconTintRes(R.color.red_expense)
        .setPositiveButton("Delete") { dialog ->
            onDelete()
            dialog.dismiss()
        }
        .setNegativeButton("Cancel") { dialog ->
            dialog.dismiss()
        }
        .show()
}

/**
 * Show a warning dialog
 */
fun Context.showWarning(
    title: String = "Warning",
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    CustomAlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setIcon(R.drawable.ic_warning)
        .setIconTintRes(R.color.orange_warning)
        .setPositiveButton("OK") { dialog ->
            onDismiss?.invoke()
            dialog.dismiss()
        }
        .show()
}

/**
 * Show an error dialog
 */
fun Context.showError(
    message: String,
    title: String = "Error",
    onDismiss: (() -> Unit)? = null
) {
    CustomAlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setIcon(R.drawable.ic_warning)
        .setIconTintRes(R.color.red_expense)
        .setPositiveButton("OK") { dialog ->
            onDismiss?.invoke()
            dialog.dismiss()
        }
        .show()
}

/**
 * Show a success dialog
 */
fun Context.showSuccess(
    message: String,
    title: String = "Success",
    onDismiss: (() -> Unit)? = null
) {
    CustomAlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setIcon(R.drawable.ic_warning) // Replace with success icon if available
        .setIconTintRes(R.color.green_income)
        .setPositiveButton("OK") { dialog ->
            onDismiss?.invoke()
            dialog.dismiss()
        }
        .show()
}

/**
 * Fragment extension - Show a simple alert dialog
 */
fun Fragment.showAlert(
    title: String,
    message: String,
    positiveText: String = "OK",
    onPositive: (() -> Unit)? = null
) {
    requireContext().showAlert(title, message, positiveText, onPositive)
}

/**
 * Fragment extension - Show a confirmation dialog
 */
fun Fragment.showConfirmation(
    title: String,
    message: String,
    positiveText: String = "Yes",
    negativeText: String = "No",
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    requireContext().showConfirmation(
        title,
        message,
        positiveText,
        negativeText,
        onConfirm,
        onCancel
    )
}

/**
 * Fragment extension - Show a delete confirmation dialog
 */
fun Fragment.showDeleteConfirmation(
    itemName: String = "item",
    message: CharSequence,
    onDelete: () -> Unit
) {
    requireContext().showDeleteConfirmation(itemName, message, onDelete)
}

/**
 * Fragment extension - Show a warning dialog
 */
fun Fragment.showWarning(
    title: String = "Warning",
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    requireContext().showWarning(title, message, onDismiss)
}

/**
 * Fragment extension - Show an error dialog
 */
fun Fragment.showError(
    message: String,
    title: String = "Error",
    onDismiss: (() -> Unit)? = null
) {
    requireContext().showError(message, title, onDismiss)
}

/**
 * Fragment extension - Show a success dialog
 */
fun Fragment.showSuccess(
    message: String,
    title: String = "Success",
    onDismiss: (() -> Unit)? = null
) {
    requireContext().showSuccess(message, title, onDismiss)
}

