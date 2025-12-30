package ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.common.R
import com.example.common.databinding.CustomChipsViewBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import helpers.createAvatarDrawable

class CustomChipsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomChipsViewBinding =
        CustomChipsViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomTextView, 0, 0)
            val imageSrc = typedArray.getResourceId(R.styleable.CustomTextView_src, 0)
            val text = typedArray.getString(R.styleable.CustomTextView_text)
            val textColor =
                typedArray.getColor(R.styleable.CustomTextView_textColor, Color.BLACK)
            val scaleType = typedArray.getString(R.styleable.CustomTextView_scaleType)

            if (imageSrc != 0) {
                binding.imageView.setImageResource(imageSrc)
            }
            binding.imageView.scaleType = if (scaleType == "centerCrop") {
                ImageView.ScaleType.CENTER_CROP
            } else {
                ImageView.ScaleType.CENTER
            }
            binding.textView.text = text
            binding.textView.setTextColor(textColor)

            typedArray.recycle()
        }
    }

    fun addChip(text: String, hasAvatar: Boolean? = false, onRemove: (() -> Unit)? = null) {
        val chip = Chip(context)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.chipStrokeWidth = 0f
        chip.setCloseIconResource(R.drawable.ic_chip_close)

        chip.setOnCloseIconClickListener {
            onRemove?.invoke()
        }
        chip.shapeAppearanceModel = chip.shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(16f)
            .build()

        chip.setChipBackgroundColorResource(R.color.bg_chip)
        binding.chipGroup.addView(chip)

        if (hasAvatar == true) {
            binding.imageView.setImageDrawable(createAvatarDrawable(context, text))
        }
    }

    fun getTextView(): TextView {
        return binding.textView
    }

    fun getChipGroup(): ChipGroup {
        return binding.chipGroup
    }

    fun hideText() {
        binding.textView.gone()
    }

    fun showText() {
        binding.textView.visible()
    }
}
