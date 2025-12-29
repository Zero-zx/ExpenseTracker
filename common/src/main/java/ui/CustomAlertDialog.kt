package ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.common.databinding.DialogCustomAlertBinding
import helpers.dpToPx

/**
 * CustomAlertDialog - A reusable alert dialog
 *
 */
class CustomAlertDialog private constructor(
    private val context: Context,
    private val builder: Builder
) {
    private val dialog: Dialog = Dialog(context)
    private val binding: DialogCustomAlertBinding =
        DialogCustomAlertBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(builder.cancelable)
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside)
        setDialogWidth()
        setupDialog()
    }

    private fun setDialogWidth() {
        val metrics = context.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val marginPx = 16.dpToPx(context)

        // Calculate width: Screen width minus 16dp on both sides
        val width = screenWidth - (2 * marginPx)

        dialog.window?.setLayout(
            width,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    private fun setupDialog() {
        // Set title
        if (builder.title != null) {
            binding.textViewTitle.text = builder.title
            binding.textViewTitle.visibility = View.VISIBLE
            builder.titleColor?.let { binding.textViewTitle.setTextColor(it) }
            builder.titleSize?.let { binding.textViewTitle.textSize = it }
        } else {
            binding.textViewTitle.visibility = View.GONE
        }

        // Set message
        if (builder.message != null) {
            binding.textViewMessage.text = builder.message
            binding.textViewMessage.visibility = View.VISIBLE
            builder.messageColor?.let { binding.textViewMessage.setTextColor(it) }
            builder.messageSize?.let { binding.textViewMessage.textSize = it }
        } else {
            binding.textViewMessage.visibility = View.GONE
        }

        // Set icon
        if (builder.icon != null) {
            binding.imageViewIcon.setImageDrawable(builder.icon)
            binding.imageViewIcon.visibility = View.VISIBLE
            builder.iconTint?.let { binding.imageViewIcon.setColorFilter(it) }
        } else if (builder.iconRes != null) {
            binding.imageViewIcon.setImageResource(builder.iconRes!!)
            binding.imageViewIcon.visibility = View.VISIBLE
            builder.iconTint?.let { binding.imageViewIcon.setColorFilter(it) }
        } else {
            binding.imageViewIcon.visibility = View.GONE
        }

        // Set close button
        if (builder.showCloseButton) {
            binding.imageViewClose.visibility = View.VISIBLE
            binding.imageViewClose.setOnClickListener {
                builder.onCloseClickListener?.invoke(this) ?: dismiss()
            }
        } else {
            binding.imageViewClose.visibility = View.GONE
        }

        // Set positive button
        if (builder.positiveButtonText != null) {
            binding.buttonPositive.text = builder.positiveButtonText
            binding.buttonPositive.visibility = View.VISIBLE
            builder.positiveButtonTextColor?.let { binding.buttonPositive.setTextColor(it) }
            builder.positiveButtonBackgroundColor?.let {
                binding.buttonPositive.setBackgroundColor(it)
            }
            binding.buttonPositive.setOnClickListener {
                builder.onPositiveClickListener?.invoke(this) ?: dismiss()
            }
        } else {
            binding.buttonPositive.visibility = View.GONE
        }

        // Set negative button
        if (builder.negativeButtonText != null) {
            binding.buttonNegative.text = builder.negativeButtonText
            binding.buttonNegative.visibility = View.VISIBLE
            builder.negativeButtonTextColor?.let { binding.buttonNegative.setTextColor(it) }
            builder.negativeButtonBackgroundColor?.let {
                binding.buttonNegative.setBackgroundColor(it)
            }
            binding.buttonNegative.setOnClickListener {
                builder.onNegativeClickListener?.invoke(this) ?: dismiss()
            }
        } else {
            binding.buttonNegative.visibility = View.GONE
        }

        // Hide button container if both buttons are gone
        if (binding.buttonPositive.visibility == View.GONE &&
            binding.buttonNegative.visibility == View.GONE
        ) {
            binding.layoutButtons.visibility = View.GONE
        }

        // Custom view
        builder.customView?.let { customView ->
            // You can add a container in the layout for custom views if needed
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
     * Builder class for CustomAlertDialog
     */
    class Builder(private val context: Context) {
        internal var title: String? = null
        internal var titleColor: Int? = null
        internal var titleSize: Float? = null

        internal var message: CharSequence? = null
        internal var messageColor: Int? = null
        internal var messageSize: Float? = null

        internal var icon: Drawable? = null
        internal var iconRes: Int? = null
        internal var iconTint: Int? = null

        internal var positiveButtonText: String? = null
        internal var positiveButtonTextColor: Int? = null
        internal var positiveButtonBackgroundColor: Int? = null
        internal var onPositiveClickListener: ((CustomAlertDialog) -> Unit)? = null

        internal var negativeButtonText: String? = null
        internal var negativeButtonTextColor: Int? = null
        internal var negativeButtonBackgroundColor: Int? = null
        internal var onNegativeClickListener: ((CustomAlertDialog) -> Unit)? = null

        internal var showCloseButton: Boolean = true
        internal var onCloseClickListener: ((CustomAlertDialog) -> Unit)? = null

        internal var cancelable: Boolean = true
        internal var canceledOnTouchOutside: Boolean = true

        internal var customView: View? = null

        /**
         * Set the dialog title
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Set the dialog title color
         */
        fun setTitleColor(@ColorInt color: Int): Builder {
            this.titleColor = color
            return this
        }

        /**
         * Set the dialog title color from resource
         */
        fun setTitleColorRes(@ColorRes colorRes: Int): Builder {
            this.titleColor = ContextCompat.getColor(context, colorRes)
            return this
        }

        /**
         * Set the dialog title text size in SP
         */
        fun setTitleSize(size: Float): Builder {
            this.titleSize = size
            return this
        }

        /**
         * Set the dialog message
         */
        fun setMessage(message: CharSequence): Builder {
            this.message = message
            return this
        }

        /**
         * Set the dialog message color
         */
        fun setMessageColor(@ColorInt color: Int): Builder {
            this.messageColor = color
            return this
        }

        /**
         * Set the dialog message color from resource
         */
        fun setMessageColorRes(@ColorRes colorRes: Int): Builder {
            this.messageColor = ContextCompat.getColor(context, colorRes)
            return this
        }

        /**
         * Set the dialog message text size in SP
         */
        fun setMessageSize(size: Float): Builder {
            this.messageSize = size
            return this
        }

        /**
         * Set the dialog icon drawable
         */
        fun setIcon(icon: Drawable): Builder {
            this.icon = icon
            return this
        }

        /**
         * Set the dialog icon from resource
         */
        fun setIcon(@DrawableRes iconRes: Int): Builder {
            this.iconRes = iconRes
            return this
        }

        /**
         * Set the dialog icon tint color
         */
        fun setIconTint(@ColorInt color: Int): Builder {
            this.iconTint = color
            return this
        }

        /**
         * Set the dialog icon tint color from resource
         */
        fun setIconTintRes(@ColorRes colorRes: Int): Builder {
            this.iconTint = ContextCompat.getColor(context, colorRes)
            return this
        }

        /**
         * Set the positive button text and click listener
         */
        fun setPositiveButton(
            text: String,
            listener: ((CustomAlertDialog) -> Unit)? = null
        ): Builder {
            this.positiveButtonText = text
            this.onPositiveClickListener = listener
            return this
        }

        /**
         * Set the positive button text color
         */
        fun setPositiveButtonTextColor(@ColorInt color: Int): Builder {
            this.positiveButtonTextColor = color
            return this
        }

        /**
         * Set the positive button background color
         */
        fun setPositiveButtonBackgroundColor(@ColorInt color: Int): Builder {
            this.positiveButtonBackgroundColor = color
            return this
        }

        /**
         * Set the negative button text and click listener
         */
        fun setNegativeButton(
            text: String,
            listener: ((CustomAlertDialog) -> Unit)? = null
        ): Builder {
            this.negativeButtonText = text
            this.onNegativeClickListener = listener
            return this
        }

        /**
         * Set the negative button text color
         */
        fun setNegativeButtonTextColor(@ColorInt color: Int): Builder {
            this.negativeButtonTextColor = color
            return this
        }

        /**
         * Set the negative button background color
         */
        fun setNegativeButtonBackgroundColor(@ColorInt color: Int): Builder {
            this.negativeButtonBackgroundColor = color
            return this
        }

        /**
         * Show or hide the close button
         */
        fun setShowCloseButton(show: Boolean): Builder {
            this.showCloseButton = show
            return this
        }

        /**
         * Set the close button click listener
         */
        fun setOnCloseClickListener(listener: (CustomAlertDialog) -> Unit): Builder {
            this.onCloseClickListener = listener
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
         * Set a custom view (for advanced customization)
         */
        fun setCustomView(view: View): Builder {
            this.customView = view
            return this
        }

        /**
         * Build and return the CustomAlertDialog instance
         */
        fun build(): CustomAlertDialog {
            return CustomAlertDialog(context, this)
        }

        /**
         * Build and show the dialog immediately
         */
        fun show(): CustomAlertDialog {
            val dialog = build()
            dialog.show()
            return dialog
        }
    }
}

