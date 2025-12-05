package ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CustomBackground @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val path = Path()

    var fillColor = Color.parseColor("#3B9CF4") // Blue color from image
        set(value) {
            field = value
            invalidate()
        }

    var innerCornerRadius = 50f
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val r = innerCornerRadius

        path.reset()

        // Start from top-left
        path.moveTo(0f, 0f)

        // Top edge - stop before the curve
        path.lineTo(w - w/4, 0f)

        // Circular arc curve - follows a smooth circular pattern inward
        val curveDepth = w / 5f // How far the curve goes inward
        path.cubicTo(
            w - w/4, r,           // Control point 1: start pulling down and in
            w - curveDepth, r * 1.5f,    // Control point 2: deepest point of curve
            w, r * 3f              // End point: back to right edge
        )


        // Right edge down
        path.lineTo(w, h)

        // Bottom edge
        path.lineTo(0f, h)

        // Left edge back to start
        path.lineTo(0f, 0f)

        path.close()

        paint.color = fillColor
        canvas.drawPath(path, paint)
    }
}