package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.example.common.R
import com.example.common.databinding.SquareIconButtonBinding

class SquareIconButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: SquareIconButtonBinding =
        SquareIconButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.SquareIconButton, 0, 0)
            val imageSrc = typedArray.getResourceId(R.styleable.SquareIconButton_src, 0)
            val text = typedArray.getString(R.styleable.SquareIconButton_text)

            binding.iconItem.setImageResource(imageSrc)
            binding.textItemTitle.text = text

            typedArray.recycle()
        }
        // Set clickable and focusable
        isClickable = true
        isFocusable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Make it square - use width for both dimensions
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    fun setIcon(@DrawableRes iconRes: Int) {
        binding.iconItem.setImageResource(iconRes)
    }

    fun setTitle(title: String) {
        binding.textItemTitle.text = title
    }

    fun showNewBadge(show: Boolean) {
        if (show) {
            binding.badgeNew.visible()
        } else {
            binding.badgeNew.gone()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.root.setOnClickListener(l)
    }
}