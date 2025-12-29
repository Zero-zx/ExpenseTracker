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

        // Top edge - stop closer to corner (about 80% across)
        path.lineTo(w * 0.75f, 0f)

        // Bezier 1: Start curve - more pronounced bend at entry
        path.cubicTo(
            w * 0.76f, r * 0.3f,
            w * 0.8f, r * 0.05f,
            w * 0.82f, r * 1.0f
        )

        // Bezier 2: Deep inward curve - main circular arc, pulls deep into the shape
        path.cubicTo(
            w/2, h/2,
            w/2, h/2,
            w * 0.88f, r * 2.2f
        )

        // Bezier 3: Exit curve - smooth return to edge, gentle transition
        path.cubicTo(
            w * 0.91f, r * 2.3f,
            w * 0.95f, r * 2.4f,
            w, r * 2.5f
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