package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.common.R
import com.example.common.databinding.CustomCardViewBinding

class CustomCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: CustomCardViewBinding =
        CustomCardViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomCardView, 0, 0)
            val imageSrc = typedArray.getResourceId(R.styleable.CustomCardView_src, 0)
            val text = typedArray.getString(R.styleable.CustomCardView_text)

            if (imageSrc != 0) {
                binding.imageView.setImageResource(imageSrc)
            }
            binding.textView.text = text

            typedArray.recycle()
        }
    }
}