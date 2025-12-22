package com.example.other.other.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.example.other.databinding.ViewGridIconButtonBinding
import ui.gone
import ui.visible

class GridIconButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewGridIconButtonBinding =
        ViewGridIconButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
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

