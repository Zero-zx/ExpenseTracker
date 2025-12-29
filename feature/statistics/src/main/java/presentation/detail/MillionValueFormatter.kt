package presentation.detail

import com.github.mikephil.charting.formatter.ValueFormatter

class MillionValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}M"
    }
}



