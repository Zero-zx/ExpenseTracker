package ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.example.common.R
import com.example.common.databinding.CustomTextViewBinding

class CustomDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomTextViewBinding =
        CustomTextViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.CustomTextView, 0, 0) {
                val imageSrc = getResourceId(R.styleable.CustomTextView_src, 0)
                val endSrc = getResourceId(R.styleable.CustomTextView_endSrc, 0)
                val text = getString(R.styleable.CustomTextView_text)

                if (imageSrc != 0) {
                    binding.imageView.setImageResource(imageSrc)
                }

                if (endSrc != 0) {
                    binding.iconChevron.setImageResource(endSrc)
                }
                binding.root.setBackgroundResource(R.drawable.rounded_ripple_image_button)
                binding.textView.setBackgroundResource(R.drawable.rounded_ripple_image_button)
                binding.textView.text = text
                binding.textView.setTextColor(Color.RED)
            }
        }
    }

    fun setImage(resId: Int) {
        binding.imageView.setImageResource(resId)
    }

    fun setText(text: String) {
        binding.textView.text = text
    }

    fun getText(): String {
        return binding.textView.text.toString()
    }

    fun clearText() {
        binding.textView.text = ""
    }

    fun getTextView(): TextView {
        return binding.textView
    }

    fun setTextColor(color: Int) {
        binding.textView.setTextColor(color)
    }

    fun setTextSize(sizeSp: Float) {
        binding.textView.textSize = sizeSp
    }

    fun setEndDrawableClickListener(listener: OnClickListener) {
        binding.iconChevron.setOnClickListener(listener)
    }

    fun setStartDrawable(resId: Int) {
        binding.imageView.setImageResource(resId)
    }

    fun hideEndDrawable() {
        binding.iconChevron.visibility = GONE
    }

    fun showEndDrawable() {
        binding.iconChevron.visibility = VISIBLE
    }
}
