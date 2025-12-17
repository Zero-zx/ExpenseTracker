package ui

import android.content.Context
import android.graphics.Color
import com.example.common.R

/**
 * CustomAlertDialogExamples - Demonstrates various ways to use CustomAlertDialog
 */
object CustomAlertDialogExamples {

    /**
     * Example 1: Basic dialog with title and message
     */
    fun showBasicDialog(context: Context) {
        CustomAlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage("This is a basic alert message")
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 2: Confirmation dialog (Yes/No)
     */
    fun showConfirmationDialog(context: Context, onConfirm: () -> Unit) {
        CustomAlertDialog.Builder(context)
            .setTitle("Attention!")
            .setMessage("These records will not be listed in any reports (except the Financial Statement report). Are you sure?")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Yes") { dialog ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 3: Delete confirmation dialog with custom colors
     */
    fun showDeleteConfirmationDialog(context: Context, onDelete: () -> Unit) {
        CustomAlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
            .setIcon(R.drawable.ic_warning)
            .setIconTintRes(R.color.red_expense)
            .setPositiveButton("Delete") { dialog ->
                onDelete()
                dialog.dismiss()
            }
            .setPositiveButtonBackgroundColor(Color.parseColor("#EF4444"))
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 4: Success dialog
     */
    fun showSuccessDialog(context: Context) {
        CustomAlertDialog.Builder(context)
            .setTitle("Success!")
            .setMessage("Your transaction has been saved successfully.")
            .setIcon(R.drawable.ic_warning) // Replace with success icon
            .setIconTintRes(R.color.green_income)
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .setShowCloseButton(false)
            .show()
    }

    /**
     * Example 5: Error dialog
     */
    fun showErrorDialog(context: Context, errorMessage: String) {
        CustomAlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(errorMessage)
            .setIcon(R.drawable.ic_warning)
            .setIconTintRes(R.color.red_expense)
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 6: Non-cancelable dialog
     */
    fun showNonCancelableDialog(context: Context, onComplete: () -> Unit) {
        CustomAlertDialog.Builder(context)
            .setTitle("Please Wait")
            .setMessage("Processing your request...")
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .setShowCloseButton(false)
            .setPositiveButton("Complete") { dialog ->
                onComplete()
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 7: Custom styled dialog
     */
    fun showCustomStyledDialog(context: Context) {
        CustomAlertDialog.Builder(context)
            .setTitle("Custom Style")
            .setTitleColorRes(R.color.blue_primary)
            .setTitleSize(22f)
            .setMessage("This dialog has custom styling applied")
            .setMessageColorRes(R.color.gray_text)
            .setMessageSize(16f)
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Got it") { dialog ->
                dialog.dismiss()
            }
            .setPositiveButtonTextColor(Color.WHITE)
            .setPositiveButtonBackgroundColor(Color.parseColor("#098FD3"))
            .show()
    }

    /**
     * Example 8: Single button dialog
     */
    fun showSingleButtonDialog(context: Context) {
        CustomAlertDialog.Builder(context)
            .setTitle("Information")
            .setMessage("This is an informational message with only one button")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 9: Dialog without close button
     */
    fun showDialogWithoutCloseButton(context: Context) {
        CustomAlertDialog.Builder(context)
            .setTitle("Action Required")
            .setMessage("You must choose an option to continue")
            .setShowCloseButton(false)
            .setPositiveButton("Accept") { dialog ->
                dialog.dismiss()
            }
            .setNegativeButton("Decline") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 10: Dialog with custom close listener
     */
    fun showDialogWithCustomCloseListener(context: Context) {
        CustomAlertDialog.Builder(context)
            .setTitle("Custom Close")
            .setMessage("The close button has a custom action")
            .setOnCloseClickListener { dialog ->
                // Custom action on close
                dialog.dismiss()
            }
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Example 11: Reusable dialog instance
     */
    fun createReusableDialog(context: Context): CustomAlertDialog {
        return CustomAlertDialog.Builder(context)
            .setTitle("Reusable Dialog")
            .setMessage("This dialog can be shown multiple times")
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .build()
    }

    /**
     * Example 12: Financial warning dialog (matching the screenshot)
     */
    fun showFinancialWarningDialog(context: Context, onConfirm: () -> Unit, onCancel: () -> Unit) {
        CustomAlertDialog.Builder(context)
            .setTitle("Attention!")
            .setMessage("These records will not be listed in any reports (except the Financial Statement report). Are you sure?")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Yes") { dialog ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog ->
                onCancel()
                dialog.dismiss()
            }
            .setCancelable(true)
            .setCanceledOnTouchOutside(true)
            .show()
    }
}

