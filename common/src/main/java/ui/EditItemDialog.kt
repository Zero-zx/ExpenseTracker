package ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.core.widget.addTextChangedListener
import com.example.common.databinding.DialogEditItemBinding

class EditItemDialog private constructor(
    private val context: Context,
    private val builder: Builder
) {

    private val dialog: Dialog = Dialog(context)
    private val binding: DialogEditItemBinding =
        DialogEditItemBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(builder.cancelable)
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside)
        setupDialog()
    }

    private fun setupDialog() {
        binding.apply {
            // Set title
            textViewTitle.text = builder.title

            // Set initial name
            editTextName.setText(builder.initialName)
            editTextName.setSelection(builder.initialName.length)

            // Set input hint
            textInputLayout.hint = builder.inputHint

            // Set completed checkbox visibility and state
            checkboxCompleted.visibility = if (builder.showCompletedCheckbox) {
                checkboxCompleted.isChecked = builder.isCompleted
                View.VISIBLE
            } else {
                View.GONE
            }

            // Set close button
            imageViewClose.setOnClickListener {
                dismiss()
            }

            // Set delete button
            if (builder.showDeleteButton) {
                buttonDelete.visibility = View.VISIBLE
                buttonDelete.setOnClickListener {
                    builder.onDeleteClickListener?.invoke(this@EditItemDialog)
                        ?: dismiss()
                }
            } else {
                buttonDelete.visibility = View.GONE
            }

            // Set cancel button
            buttonCancel.setOnClickListener {
                builder.onCancelClickListener?.invoke(this@EditItemDialog)
                    ?: dismiss()
            }

            // Set done button
            buttonDone.setOnClickListener {
                val name = editTextName.text?.toString()?.trim() ?: ""
                if (name.isBlank()) {
                    textInputLayout.error = "Name cannot be empty"
                    return@setOnClickListener
                }

                val isCompleted = if (builder.showCompletedCheckbox) {
                    checkboxCompleted.isChecked
                } else {
                    false
                }

                builder.onDoneClickListener?.invoke(name, isCompleted)
                dismiss()
            }

            // Clear error on text change
            editTextName.addTextChangedListener {
                textInputLayout.error = null
            }

            // Request focus on name input
            editTextName.requestFocus()
        }
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun isShowing(): Boolean = dialog.isShowing

    /**
     * Builder class for EditItemDialog
     */
    class Builder(private val context: Context) {
        internal var title: String = "Edit Item"
        internal var initialName: String = ""
        internal var inputHint: String = "Name"
        internal var showCompletedCheckbox: Boolean = false
        internal var isCompleted: Boolean = false
        internal var showDeleteButton: Boolean = true
        internal var cancelable: Boolean = true
        internal var canceledOnTouchOutside: Boolean = true

        internal var onDoneClickListener: ((name: String, isCompleted: Boolean) -> Unit)? = null
        internal var onDeleteClickListener: ((EditItemDialog) -> Unit)? = null
        internal var onCancelClickListener: ((EditItemDialog) -> Unit)? = null

        /**
         * Set the dialog title
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Set the initial name value
         */
        fun setInitialName(name: String): Builder {
            this.initialName = name
            return this
        }

        /**
         * Set the input field hint
         */
        fun setInputHint(hint: String): Builder {
            this.inputHint = hint
            return this
        }

        /**
         * Set whether to show the completed checkbox
         */
        fun setShowCompletedCheckbox(show: Boolean): Builder {
            this.showCompletedCheckbox = show
            return this
        }

        /**
         * Set the initial completed state (only relevant if checkbox is shown)
         */
        fun setCompleted(completed: Boolean): Builder {
            this.isCompleted = completed
            return this
        }

        /**
         * Set whether to show the delete button
         */
        fun setShowDeleteButton(show: Boolean): Builder {
            this.showDeleteButton = show
            return this
        }

        /**
         * Set whether the dialog is cancelable
         */
        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        /**
         * Set whether the dialog is canceled when touched outside
         */
        fun setCanceledOnTouchOutside(canceled: Boolean): Builder {
            this.canceledOnTouchOutside = canceled
            return this
        }

        /**
         * Set the listener for the Done button
         * @param listener Callback with (name, isCompleted) parameters
         */
        fun setOnDoneClickListener(listener: (name: String, isCompleted: Boolean) -> Unit): Builder {
            this.onDoneClickListener = listener
            return this
        }

        /**
         * Set the listener for the Delete button
         */
        fun setOnDeleteClickListener(listener: (EditItemDialog) -> Unit): Builder {
            this.onDeleteClickListener = listener
            return this
        }

        /**
         * Set the listener for the Cancel button
         */
        fun setOnCancelClickListener(listener: (EditItemDialog) -> Unit): Builder {
            this.onCancelClickListener = listener
            return this
        }

        /**
         * Build and show the dialog
         */
        fun show(): EditItemDialog {
            val dialog = build()
            dialog.show()
            return dialog
        }

        /**
         * Build the dialog without showing it
         */
        fun build(): EditItemDialog {
            return EditItemDialog(context, this)
        }
    }
}

