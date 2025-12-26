package helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import com.example.common.R

fun String.standardize(): String {
    return this[0].uppercase() + this.substring(1)
}

fun createAvatarDrawable(context: Context, text: String): BitmapDrawable {
    val size = 32.dpToPx(context)
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    val backgroundColor = ContextCompat.getColor(context, R.color.blue_bg)


    // Draw circle background
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = backgroundColor
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // Draw first letter in white
    paint.color = Color.WHITE
    paint.textSize = (size * 0.5f)
    paint.textAlign = Paint.Align.CENTER
    val firstLetter = text.firstOrNull().toString()
    val textBounds = android.graphics.Rect()
    paint.getTextBounds(firstLetter, 0, firstLetter.length, textBounds)
    val textHeight = textBounds.height()
    val textY = size / 2f + textHeight / 2f
    canvas.drawText(firstLetter, size / 2f, textY, paint)

    return bitmap.toDrawable(context.resources)
}

fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

/**
 * Formats a Double value as currency, showing only integer part if there's no decimal component.
 * @param currencySymbol The currency symbol to append (default: "$")
 * @return Formatted currency string with symbol
 */
fun Double.formatAsCurrency(currencySymbol: String = "$"): String {
    val formattedAmount = if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
    return "$formattedAmount $currencySymbol"
}

/**
 * Formats a Double value without currency symbol, showing only integer part if there's no decimal component.
 * @return Formatted number string
 */
fun Double.formatAsAmount(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}

