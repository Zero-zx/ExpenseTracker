package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.common.R
import com.example.common.databinding.ViewCalculatorBinding
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

class CalculatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCalculatorBinding =
        ViewCalculatorBinding.inflate(LayoutInflater.from(context), this, true)

    // Calculator state
    private var calc: String = ""
    private val operation: String = "+-x÷"
    private var isCheckEqual: Boolean = false

    // Callbacks
    private var onAmountChangeListener: ((String) -> Unit)? = null
    private var onDoneListener: (() -> Unit)? = null

    // Formatting
    private val symbol: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
    private val maxLengthAmount = 50 // Maximum length for expressions
    private val maxSingleNumberLength = 13 // Maximum length for individual numbers

    init {
        setupCalculatorButtons()
        binding.lnSuggesstion.visibility = View.GONE
    }

    private fun setupCalculatorButtons() {
        binding.apply {
            // Number buttons
            btnKey0.setOnClickListener { addText("0", false) }
            btnKey1.setOnClickListener { addText("1", false) }
            btnKey2.setOnClickListener { addText("2", false) }
            btnKey3.setOnClickListener { addText("3", false) }
            btnKey4.setOnClickListener { addText("4", false) }
            btnKey5.setOnClickListener { addText("5", false) }
            btnKey6.setOnClickListener { addText("6", false) }
            btnKey7.setOnClickListener { addText("7", false) }
            btnKey8.setOnClickListener { addText("8", false) }
            btnKey9.setOnClickListener { addText("9", false) }
            btnKey000.setOnClickListener {
                val lastChar = if (calc.isNotEmpty()) calc.last().toString() else ""
                if (lastChar != "÷" && !operation.contains(lastChar)) {
                    addText("000", false)
                }
            }

            // Decimal point
            btnKeyDot.text = symbol.decimalSeparator.toString()
            btnKeyDot.setOnClickListener { addText(symbol.decimalSeparator.toString(), false) }

            // Operators
            btnKeyAdd.setOnClickListener { addText("+", true) }
            btnKeyMinus.setOnClickListener { addText("-", true) }
            btnKeyMulti.setOnClickListener { addText("x", true) }
            btnKeyDivide.setOnClickListener { addText("÷", true) }

            // Clear
            btnKeyC.setOnClickListener {
                calc = "0"
                updateDisplay()
                isCheckEqual = false
                updateButtonState()
            }

            // Backspace
            btnKeyBack.setOnClickListener { clickBack() }

            btnKeyEqual.setOnClickListener {
                if (calc.contains("+") || calc.contains("-") ||
                    calc.contains("x") || calc.contains("÷")
                ) {
                    // Calculate
                    isCheckEqual = true
                    val result = calculateEqual()
                    if (!result.contains("ERROR")) {
                        calc = formatCurrency(result.toDouble(), symbol)
                        updateDisplay()
                    }
                    updateButtonState()
                } else {
                    // Done - close keyboard
                    onDoneListener?.invoke()
                }
            }
        }
    }

    private fun addText(str: String, isOperator: Boolean) {
        try {
            // Initialize calc if empty
            if (calc.isEmpty() || calc == "0") {
                if (str != symbol.decimalSeparator.toString() && !isOperator) {
                    calc = str
                    updateDisplay()
                    updateButtonState()
                }
                return
            }

            // Get the cleaned version (without formatting)
            val cleaned = calc
                .replace(symbol.groupingSeparator.toString(), "")
                .replace(symbol.decimalSeparator.toString(), ".")

            // Handle decimal separator
            if (str == symbol.decimalSeparator.toString()) {
                // Find the last number segment
                var lastNumberStart = cleaned.length
                for (i in cleaned.length - 1 downTo 0) {
                    if (operation.contains(cleaned[i].toString())) {
                        lastNumberStart = i + 1
                        break
                    }
                    if (i == 0) {
                        lastNumberStart = 0
                    }
                }
                val lastNumber = cleaned.substring(lastNumberStart)

                // Only add decimal if the last number doesn't already have one
                if (!lastNumber.contains(".")) {
                    val newCleaned = cleaned + "."
                    calc = reFormatValue(newCleaned)
                    updateDisplay()
                    updateButtonState()
                }
                return
            }

            if (isOperator) {
                // Handle operator
                val lastChar = cleaned.lastOrNull()?.toString() ?: ""

                if (operation.contains(lastChar)) {
                    // Replace the last operator
                    val newCleaned = cleaned.substring(0, cleaned.length - 1) + str
                    calc = reFormatValue(newCleaned)
                } else {
                    // Append operator
                    val newCleaned = cleaned + str
                    calc = reFormatValue(newCleaned)
                }
                updateDisplay()
                updateButtonState()
            } else {
                // Handle number
                val newCleaned = cleaned + str

                // Check if valid
                if (checkValidMaxAmount(reFormatValue(newCleaned))) {
                    calc = reFormatValue(newCleaned)
                    updateDisplay()
                    updateButtonState()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickBack() {
        try {
            // If calc is empty or just "0", nothing to delete
            if (calc.isEmpty() || calc == "0") {
                calc = "0"
                updateDisplay()
                updateButtonState()
                return
            }

            // Remove the last character from the unformatted string
            val cleaned = calc
                .replace(symbol.groupingSeparator.toString(), "")
                .replace(symbol.decimalSeparator.toString(), ".")

            if (cleaned.isEmpty() || cleaned == "0") {
                calc = "0"
                updateDisplay()
                updateButtonState()
                return
            }

            // Remove last character from cleaned string
            val newCleaned = cleaned.substring(0, cleaned.length - 1)

            if (newCleaned.isEmpty()) {
                calc = "0"
            } else {
                // Reformat the value
                calc = reFormatValue(newCleaned)
            }

            updateDisplay()
            updateButtonState()
        } catch (e: Exception) {
            e.printStackTrace()
            calc = "0"
            updateDisplay()
            updateButtonState()
        }
    }

    private fun reFormatValue(value: String): String {
        if (value.isEmpty()) return ""

        try {
            val cleaned = value
                .replace(symbol.groupingSeparator.toString(), "")
                .replace(symbol.decimalSeparator.toString(), ".")

            // If no operators, just format the number
            if (!cleaned.contains("+") && !cleaned.contains("-") &&
                !cleaned.contains("x") && !cleaned.contains("÷")
            ) {
                return formatCurrency(cleaned.toDouble(), symbol)
            }

            // Format each number in the expression
            val result = StringBuilder()
            var currentNumber = ""

            for (char in cleaned) {
                if (!operation.contains(char.toString())) {
                    currentNumber += char
                } else {
                    if (currentNumber.isNotEmpty()) {
                        result.append(formatCurrency(currentNumber.toDouble(), symbol))
                        currentNumber = ""
                    }
                    result.append(char)
                }
            }

            if (currentNumber.isNotEmpty()) {
                result.append(formatCurrency(currentNumber.toDouble(), symbol))
            }

            return result.toString()
        } catch (e: Exception) {
            return value
        }
    }

    private fun formatCurrency(value: Double, symbols: DecimalFormatSymbols): String {
        val formatter = DecimalFormat().apply {
            decimalFormatSymbols = symbols
            maximumFractionDigits = 2
            isGroupingUsed = true
        }
        return formatter.format(value)
    }

    private fun checkValidMaxAmount(text: String): Boolean {
        return try {
            val result = calculateEqual(text)
            !result.contains("ERROR") && abs(result.toDouble()) <= 9_999_999_999_999.99
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateEqual(expression: String = calc): String {
        return try {
            var expr = expression

            if (expr.isEmpty()) return "0"

            // Remove trailing operator
            val lastChar = expr.last().toString()
            if (operation.contains(lastChar)) {
                expr = expr.substring(0, expr.length - 1)
            }

            // Clean and prepare expression
            expr = expr
                .replace(symbol.groupingSeparator.toString(), "")
                .replace(symbol.decimalSeparator.toString(), ".")
                .replace("x", "*")
                .replace("÷", "/")

            // Simple evaluator with operator precedence
            evaluateExpression(expr).toString()
        } catch (e: Exception) {
            "ERROR$calc"
        }
    }

    private fun evaluateExpression(expr: String): BigDecimal {
        val numbers = mutableListOf<BigDecimal>()
        val operators = mutableListOf<Char>()
        var currentNum = ""

        for (char in expr) {
            when (char) {
                in '0'..'9', '.' -> currentNum += char
                '+', '-', '*', '/' -> {
                    if (currentNum.isNotEmpty()) {
                        numbers.add(BigDecimal(currentNum))
                        currentNum = ""
                    }
                    operators.add(char)
                }
            }
        }
        if (currentNum.isNotEmpty()) {
            numbers.add(BigDecimal(currentNum))
        }

        // First pass: handle * and /
        var i = 0
        while (i < operators.size) {
            when (operators[i]) {
                '*' -> {
                    numbers[i] = numbers[i].multiply(numbers[i + 1])
                    numbers.removeAt(i + 1)
                    operators.removeAt(i)
                    i--
                }

                '/' -> {
                    numbers[i] = if (numbers[i + 1].compareTo(BigDecimal.ZERO) != 0) {
                        numbers[i].divide(numbers[i + 1], MathContext.DECIMAL64)
                    } else {
                        numbers[i]
                    }
                    numbers.removeAt(i + 1)
                    operators.removeAt(i)
                    i--
                }
            }
            i++
        }

        // Second pass: handle + and -
        var result = numbers[0]
        for (j in operators.indices) {
            when (operators[j]) {
                '+' -> result = result.add(numbers[j + 1])
                '-' -> result = result.subtract(numbers[j + 1])
            }
        }

        return result
    }

    private fun updateDisplay() {
        val displayText = if (calc.isEmpty() || calc == "0") "0" else calc
        onAmountChangeListener?.invoke(displayText)
    }

    private fun updateButtonState() {
        binding.apply {
            val hasOperator = calc.contains("+") || calc.contains("-") ||
                    calc.contains("x") || calc.contains("÷")

            if (hasOperator) {
//                tvDone.visibility = View.GONE
                tvDone.text = context.getString(R.string.KeyEqual)
//                imgMarkEqual.visibility = View.VISIBLE
            } else {
                tvDone.text = context.getString(R.string.Done)
//                tvDone.visibility = View.VISIBLE
//                imgMarkEqual.visibility = View.GONE
            }
        }
    }


    // Public API
    fun setOnAmountChangeListener(listener: (String) -> Unit) {
        onAmountChangeListener = listener
    }

    fun setOnDoneListener(listener: () -> Unit) {
        onDoneListener = listener
    }

    fun setAmount(amount: String) {
        calc = amount.ifEmpty { "0" }
        updateDisplay()
    }

    fun getCurrentAmount(): String {
        return try {
            val result = calculateEqual()
            if (result.contains("ERROR")) {
                "0"
            } else {
                result
            }
        } catch (e: Exception) {
            "0"
        }
    }

    fun clear(notify: Boolean = true) {
        calc = "0"
        isCheckEqual = false
        updateButtonState()
        if (notify) {
            updateDisplay()
        }
    }
}

