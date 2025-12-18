package ui

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Example usage of EditItemDialog for different scenarios
 */

// ============================================
// Example 1: Edit Event with Completed Checkbox
// ============================================
fun Fragment.exampleEditEvent() {
    showEditEventDialog(
        eventName = "Business Trip to NYC",
        isCompleted = false,
        onUpdate = { name, isCompleted ->
            // Update the event in database
            // viewModel.updateEvent(eventId, name, isCompleted)
        },
        onDelete = {
            // Delete the event
            // viewModel.deleteEvent(eventId)
        }
    )
}

// ============================================
// Example 2: Edit Payee without Completed Checkbox
// ============================================
fun Fragment.exampleEditPayee() {
    showEditPayeeDialog(
        payeeName = "John Doe",
        onUpdate = { name ->
            // Update the payee in database
            // viewModel.updatePayee(payeeId, name)
        },
        onDelete = {
            // Delete the payee
            // viewModel.deletePayee(payeeId)
        }
    )
}

// ============================================
// Example 3: Using Builder Pattern Directly
// ============================================
fun Context.exampleUsingBuilder() {
    EditItemDialog.Builder(this)
        .setTitle("Edit Location")
        .setInitialName("Home")
        .setInputHint("Location name")
        .setShowCompletedCheckbox(false)
        .setShowDeleteButton(true)
        .setOnDoneClickListener { name, _ ->
            // Handle update
        }
        .setOnDeleteClickListener { dialog ->
            // Handle delete
            dialog.dismiss()
        }
        .show()
}

// ============================================
// Example 4: Generic Item with Custom Settings
// ============================================
fun Fragment.exampleGenericItem() {
    showEditItemDialog(
        title = "Edit Tag",
        initialName = "Important",
        inputHint = "Tag name",
        showCompleted = false,
        isCompleted = false,
        onUpdate = { name, _ ->
            // Update tag
        },
        onDelete = {
            // Delete tag
        }
    )
}

// ============================================
// Example 5: Edit without Delete Button
// ============================================
fun Context.exampleEditWithoutDelete() {
    EditItemDialog.Builder(this)
        .setTitle("Edit Category")
        .setInitialName("Food")
        .setShowDeleteButton(false) // Hide delete button
        .setOnDoneClickListener { name, _ ->
            // Handle update only
        }
        .show()
}

// ============================================
// Example 6: Non-Cancelable Dialog
// ============================================
fun Context.exampleNonCancelable() {
    EditItemDialog.Builder(this)
        .setTitle("Required Update")
        .setInitialName("Must Update")
        .setCancelable(false)
        .setCanceledOnTouchOutside(false)
        .setOnDoneClickListener { name, _ ->
            // Handle update
        }
        .show()
}

// ============================================
// Example 7: Integration in Adapter
// ============================================
class EventAdapterExample {
    // In your adapter's bind method:
    fun bindItemEdit(fragment: Fragment, eventName: String) {
        // imageViewEdit.setOnClickListener {
        fragment.showEditEventDialog(
            eventName = eventName,
            isCompleted = false,
            onUpdate = { name, isCompleted ->
                // Update logic
            },
            onDelete = {
                // Delete logic with confirmation
                fragment.requireContext().let { context ->
                    CustomAlertDialog.Builder(context)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete") { dialog ->
                            // Perform delete
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        )
        // }
    }
}

