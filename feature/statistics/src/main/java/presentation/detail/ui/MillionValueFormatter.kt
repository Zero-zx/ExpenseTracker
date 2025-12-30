package presentation.detail.ui

import com.github.mikephil.charting.formatter.ValueFormatter

class MillionValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return when {
            value == 0f -> "0"
            value < 1f -> String.format("%.3f", value)
            else -> String.format("%.0f", value)
        }
    }
}



