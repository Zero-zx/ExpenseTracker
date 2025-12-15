package ui

import android.content.Context
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.common.R

/**
 * Manager class to handle calculator view integration in any fragment
 * Handles showing/hiding calculator and managing input synchronization
 */
class CalculatorManager(
    private val calculatorView: CalculatorView,
    private val amountEditText: EditText,
    private val context: Context
) {

    init {
        setupCalculator()
        setupEditTextListeners()
    }

    private fun setupCalculator() {
        calculatorView.apply {
            setOnAmountChangeListener { amount ->
                amountEditText.setText(amount)
            }

            setOnDoneListener {
                hide()
            }
        }
    }

    private fun setupEditTextListeners() {
        amountEditText.setOnClickListener {
            show()
        }

        amountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                show()
            }
        }
    }

    /**
     * Show the calculator and hide system keyboard
     */
    fun show() {
        // Hide system keyboard
        hideKeyboard()

        // Show calculator with slide-in animation
        calculatorView.apply {
            if (visibility != android.view.View.VISIBLE) {
                visibility = android.view.View.VISIBLE
                val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
                startAnimation(slideIn)
            }
        }

        // Set current amount if any
        val currentAmount = amountEditText.text?.toString() ?: ""
        if (currentAmount.isNotEmpty() && currentAmount != "0") {
            calculatorView.setAmount(currentAmount)
        }
    }

    /**
     * Hide the calculator
     */
    fun hide() {
        if (calculatorView.visibility == android.view.View.VISIBLE) {
            val slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom)
            slideOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    calculatorView.visibility = android.view.View.GONE
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
            calculatorView.startAnimation(slideOut)
        }
    }

    /**
     * Check if calculator is currently visible
     */
    fun isVisible(): Boolean {
        return calculatorView.visibility == android.view.View.VISIBLE
    }

    /**
     * Get the current amount from calculator
     */
    fun getCurrentAmount(): String {
        return calculatorView.getCurrentAmount()
    }

    /**
     * Set amount in calculator
     */
    fun setAmount(amount: String) {
        calculatorView.setAmount(amount)
    }

    /**
     * Clear calculator input
     */
    fun clear() {
        calculatorView.clear()
    }

    /**
     * Hide system keyboard
     */
    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(amountEditText.windowToken, 0)
        amountEditText.clearFocus()
    }

    /**
     * Cleanup resources when fragment is destroyed
     */
    fun cleanup() {
        amountEditText.setOnClickListener(null)
        amountEditText.setOnFocusChangeListener(null)
    }
}

