package ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.example.common.R

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var cornerRadius: Float = 0f
    private var backgroundColor: Int = Color.WHITE
    private var rippleColor: Int = Color.parseColor("#E5E7EB")
    private var strokeWidth: Int = 0
    private var strokeColor: Int = Color.TRANSPARENT

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomButton, 0, 0)

            cornerRadius = typedArray.getDimension(R.styleable.CustomButton_cornerRadius, 0f)
            backgroundColor = typedArray.getColor(R.styleable.CustomButton_backgroundColor, Color.WHITE)
            rippleColor = typedArray.getColor(R.styleable.CustomButton_rippleColor, Color.parseColor("#E5E7EB"))
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CustomButton_strokeWidth, 0)
            strokeColor = typedArray.getColor(R.styleable.CustomButton_strokeColor, Color.TRANSPARENT)

            typedArray.recycle()
        }

        setupBackground()
    }

    private fun setupBackground() {
        // Create the background shape
        val backgroundShape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            cornerRadii = floatArrayOf(
                cornerRadius, cornerRadius,  // top-left
                cornerRadius, cornerRadius,  // top-right
                cornerRadius, cornerRadius,  // bottom-right
                cornerRadius, cornerRadius   // bottom-left
            )
            if (strokeWidth > 0) {
                setStroke(strokeWidth, strokeColor)
            }
        }

        // Create the ripple mask
        val rippleMask = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadii = floatArrayOf(
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius
            )
        }

        // Create ripple drawable
        val rippleDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            backgroundShape,
            rippleMask
        )

        background = rippleDrawable
    }

    fun setButtonBackgroundColor(color: Int) {
        backgroundColor = color
        setupBackground()
    }

    fun setButtonCornerRadius(radius: Float) {
        cornerRadius = radius
        setupBackground()
    }

    fun setButtonRippleColor(color: Int) {
        rippleColor = color
        setupBackground()
    }

    fun setButtonStroke(width: Int, color: Int) {
        strokeWidth = width
        strokeColor = color
        setupBackground()
    }
}

