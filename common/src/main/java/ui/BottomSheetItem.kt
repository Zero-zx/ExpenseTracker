package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.common.R
import com.example.common.databinding.BottomSheetItemBinding

class BottomSheetItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: BottomSheetItemBinding =
        BottomSheetItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.BottomSheetItem, 0, 0)
            val imageSrc = typedArray.getResourceId(R.styleable.BottomSheetItem_src, 0)
            val text = typedArray.getString(R.styleable.BottomSheetItem_text)

            binding.imageView.setImageResource(imageSrc)
            binding.textView.text = text

            typedArray.recycle()
        }
        // Set clickable and focusable
        isClickable = true
        isFocusable = true
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.root.setOnClickListener(l)
    }
}