package ui

import android.content.Context

/**
 * Practical examples for using CustomAlertDialog in real scenarios
 * within the Expense Tracker app
 */
object ExpenseTrackerDialogs {

    /**
     * Show confirmation dialog when excluding transaction from reports
     * (Matches the screenshot provided)
     */
    fun showExcludeFromReportsDialog(
        context: Context,
        onConfirm: () -> Unit,
        onCancel: () -> Unit = {}
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Attention!")
            .setMessage("These records will not be listed in any reports (except the Financial Statement report). Are you sure?")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setPositiveButton("Yes") { dialog ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog ->
                onCancel()
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show confirmation before deleting a transaction
     */
    fun showDeleteTransactionDialog(
        context: Context,
        onDelete: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction? This action cannot be undone.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
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
     * Show confirmation before deleting multiple transactions
     */
    fun showDeleteMultipleTransactionsDialog(
        context: Context,
        count: Int,
        onDelete: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Delete Transactions")
            .setMessage("Are you sure you want to delete $count transactions? This action cannot be undone.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
            .setPositiveButton("Delete All") { dialog ->
                onDelete()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show confirmation before deleting an account
     */
    fun showDeleteAccountDialog(
        context: Context,
        accountName: String,
        onDelete: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete '$accountName'? All transactions associated with this account will also be deleted.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
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
     * Show confirmation before deleting a category
     */
    fun showDeleteCategoryDialog(
        context: Context,
        categoryName: String,
        transactionCount: Int,
        onDelete: () -> Unit
    ) {
        val message = if (transactionCount > 0) {
            "Are you sure you want to delete '$categoryName'? This category is used in $transactionCount transactions."
        } else {
            "Are you sure you want to delete '$categoryName'?"
        }

        CustomAlertDialog.Builder(context)
            .setTitle("Delete Category")
            .setMessage(message)
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
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
     * Show success message after saving a transaction
     */
    fun showTransactionSavedDialog(
        context: Context,
        onDismiss: () -> Unit = {}
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Success!")
            .setMessage("Your transaction has been saved successfully.")
            .setIcon(com.example.common.R.drawable.ic_warning) // Replace with success icon
            .setIconTintRes(com.example.common.R.color.green_income)
            .setPositiveButton("OK") { dialog ->
                onDismiss()
                dialog.dismiss()
            }
            .setShowCloseButton(false)
            .show()
    }

    /**
     * Show error when transaction save fails
     */
    fun showTransactionSaveErrorDialog(
        context: Context,
        errorMessage: String
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("Failed to save transaction: $errorMessage")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show confirmation before clearing all data
     */
    fun showClearAllDataDialog(
        context: Context,
        onConfirm: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Clear All Data")
            .setMessage("This will permanently delete all transactions, accounts, and categories. This action cannot be undone. Are you absolutely sure?")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
            .setPositiveButton("Clear All") { dialog ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Show budget limit exceeded warning
     */
    fun showBudgetExceededDialog(
        context: Context,
        categoryName: String,
        budgetLimit: String,
        currentAmount: String
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Budget Limit Exceeded!")
            .setMessage("Your spending in '$categoryName' ($currentAmount) has exceeded your budget limit of $budgetLimit.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.orange_warning)
            .setPositiveButton("OK") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show confirmation before logging out
     */
    fun showLogoutConfirmationDialog(
        context: Context,
        onLogout: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setPositiveButton("Logout") { dialog ->
                onLogout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show data sync confirmation
     */
    fun showSyncDataDialog(
        context: Context,
        onSync: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Sync Data")
            .setMessage("This will synchronize your data with the cloud. Your local changes will be uploaded. Continue?")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setPositiveButton("Sync") { dialog ->
                onSync()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show permission required dialog
     */
    fun showPermissionRequiredDialog(
        context: Context,
        permissionName: String,
        onOpenSettings: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Permission Required")
            .setMessage("$permissionName permission is required for this feature to work. Please grant the permission in app settings.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.orange_warning)
            .setPositiveButton("Open Settings") { dialog ->
                onOpenSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show app update available dialog
     */
    fun showUpdateAvailableDialog(
        context: Context,
        version: String,
        onUpdate: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("A new version ($version) is available. Update now to get the latest features and improvements.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.blue_primary)
            .setPositiveButton("Update") { dialog ->
                onUpdate()
                dialog.dismiss()
            }
            .setNegativeButton("Later") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show network error dialog
     */
    fun showNetworkErrorDialog(
        context: Context,
        onRetry: () -> Unit
    ) {
        CustomAlertDialog.Builder(context)
            .setTitle("Network Error")
            .setMessage("Unable to connect to the server. Please check your internet connection and try again.")
            .setIcon(com.example.common.R.drawable.ic_warning)
            .setIconTintRes(com.example.common.R.color.red_expense)
            .setPositiveButton("Retry") { dialog ->
                onRetry()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }
}

