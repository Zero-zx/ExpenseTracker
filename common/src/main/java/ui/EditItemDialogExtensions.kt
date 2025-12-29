package ui

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Extension functions for EditItemDialog to provide type-safe builders for specific entities
 */

/**
 * Show edit dialog for Event
 */
fun Fragment.showEditDialog(
    title: String,
    inputHint: String?,
    name: String,
    isCompleted: Boolean = false,
    onUpdate: (name: String, isCompleted: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    requireContext().showEditDialog(
        title = title,
        inputHint = inputHint,
        name = name,
        isCompleted = isCompleted,
        onUpdate = onUpdate,
        onDelete = onDelete
    )
}

/**
 * Show edit dialog for Event (Context extension)
 */
fun Context.showEditDialog(
    title: String,
    inputHint: String?,
    name: String,
    isCompleted: Boolean = false,
    onUpdate: (name: String, isCompleted: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    EditItemDialog.Builder(this)
        .setTitle(title)
        .setInitialName(name)
        .setInputHint(inputHint ?: "")
        .setShowCompletedCheckbox(true)
        .setCompleted(isCompleted)
        .setShowDeleteButton(onDelete != null)
        .setOnDoneClickListener { name, completed ->
            onUpdate(name, completed)
        }
        .apply {
            onDelete?.let { deleteCallback ->
                setOnDeleteClickListener {
                    deleteCallback()
                }
            }
        }
        .show()
}

/**
 * Show edit dialog for Payee
 */
fun Fragment.showEditPayeeDialog(
    payeeName: String,
    onUpdate: (name: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    requireContext().showEditPayeeDialog(
        payeeName = payeeName,
        onUpdate = onUpdate,
        onDelete = onDelete
    )
}

/**
 * Show edit dialog for Payee (Context extension)
 */
fun Context.showEditPayeeDialog(
    payeeName: String,
    onUpdate: (name: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    EditItemDialog.Builder(this)
        .setTitle("Edit Payee")
        .setInitialName(payeeName)
        .setInputHint("Payee name")
        .setShowCompletedCheckbox(false)
        .setShowDeleteButton(onDelete != null)
        .setOnDoneClickListener { name, _ ->
            onUpdate(name)
        }
        .apply {
            onDelete?.let { deleteCallback ->
                setOnDeleteClickListener {
                    deleteCallback()
                }
            }
        }
        .show()
}

/**
 * Show a generic edit dialog for any item
 */
fun Fragment.showEditItemDialog(
    title: String,
    initialName: String,
    inputHint: String = "Name",
    showCompleted: Boolean = false,
    isCompleted: Boolean = false,
    onUpdate: (name: String, isCompleted: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    requireContext().showEditItemDialog(
        title = title,
        initialName = initialName,
        inputHint = inputHint,
        showCompleted = showCompleted,
        isCompleted = isCompleted,
        onUpdate = onUpdate,
        onDelete = onDelete
    )
}

/**
 * Show a generic edit dialog for any item (Context extension)
 */
fun Context.showEditItemDialog(
    title: String,
    initialName: String,
    inputHint: String = "Name",
    showCompleted: Boolean = false,
    isCompleted: Boolean = false,
    onUpdate: (name: String, isCompleted: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    EditItemDialog.Builder(this)
        .setTitle(title)
        .setInitialName(initialName)
        .setInputHint(inputHint)
        .setShowCompletedCheckbox(showCompleted)
        .setCompleted(isCompleted)
        .setShowDeleteButton(onDelete != null)
        .setOnDoneClickListener { name, completed ->
            onUpdate(name, completed)
        }
        .apply {
            onDelete?.let { deleteCallback ->
                setOnDeleteClickListener {
                    deleteCallback()
                }
            }
        }
        .show()
}

