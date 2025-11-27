package ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.common.R
import com.example.common.databinding.CustomTextViewBinding

class CustomTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomTextViewBinding =
        CustomTextViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomTextView, 0, 0)
            val imageSrc = typedArray.getResourceId(R.styleable.CustomTextView_src, 0)
            val text = typedArray.getString(R.styleable.CustomTextView_text)
            val textColor =
                typedArray.getColor(R.styleable.CustomTextView_textColor, Color.BLACK)
            val textSize = typedArray.getDimension(
                R.styleable.CustomTextView_textSize,
                16f
            ) // Default 16sp

            if (imageSrc != 0) {
                binding.imageView.setImageResource(imageSrc)
            }
            binding.textView.text = text
            binding.textView.setTextColor(textColor)
            binding.textView.textSize =
                textSize / resources.displayMetrics.scaledDensity // Convert px to sp

            typedArray.recycle()
        }
    }

    fun setImage(resId: Int) {
        binding.imageView.setImageResource(resId)
    }

    fun setText(text: String) {
        binding.textView.text = text
    }

    fun setTextColor(color: Int) {
        binding.textView.setTextColor(color)
    }

    fun setTextSize(sizeSp: Float) {
        binding.textView.textSize = sizeSp
    }
}
