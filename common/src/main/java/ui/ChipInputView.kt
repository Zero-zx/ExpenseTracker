package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.common.R
import com.example.common.databinding.ChipInputViewBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import helpers.createAvatarDrawable
import helpers.dpToPx

class ChipInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ChipInputViewBinding =
        ChipInputViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun getEditText(): TextInputEditText {
        return binding.editTextInput
    }

    fun addChip(text: String, onRemove: (() -> Unit)? = null): Chip {
        val chip = Chip(context)
        chip.text = text
        chip.isCloseIconVisible = onRemove != null
        chip.setOnCloseIconClickListener {
            onRemove?.invoke()
            if (binding.chipGroup.childCount == 1) {
                binding.editTextInput.hint = context.getString(R.string.text_select_payee_name)
            }
        }
        chip.setEnsureMinTouchTargetSize(false)
        // Create circular avatar with first letter
        chip.chipIcon = createAvatarDrawable(context, text)
        chip.isChipIconVisible = true
        chip.chipIconSize = 24.dpToPx(context).toFloat()
        chip.chipStrokeWidth = 0f
        // Style the chip to match the image
        chip.setChipBackgroundColorResource(R.color.bg_chip)
        chip.shapeAppearanceModel = chip.shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(8f)
            .build()
        chip.setTextColor(ContextCompat.getColor(context, R.color.black_text))
        chip.setPadding(4.dpToPx(context), 0, 4.dpToPx(context), 0)
        chip.chipStartPadding = 4.dpToPx(context).toFloat()
        chip.chipEndPadding = 4.dpToPx(context).toFloat()
        chip.iconStartPadding = 0f
        chip.setCloseIconResource(R.drawable.ic_chip_close)
        chip.iconEndPadding = 4.dpToPx(context).toFloat()
        chip.textStartPadding = 4.dpToPx(context).toFloat()
        chip.textEndPadding = 4.dpToPx(context).toFloat()
        chip.closeIconStartPadding = 4.dpToPx(context).toFloat()
        chip.closeIconEndPadding = 0f
        val layoutParams = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            // Set top margin as requested (20dp)
            setMargins(0, 4.dpToPx(context), 8.dpToPx(context), 0)
            alignSelf = AlignItems.CENTER
        }

        chip.layoutParams = layoutParams

        // Add chip before the edit text (edit text is the last child in ChipGroup)
        val editTextIndex = binding.chipGroup.indexOfChild(binding.editTextInput)
        if (editTextIndex >= 0) {
            binding.chipGroup.addView(chip, editTextIndex)
        } else {
            binding.chipGroup.addView(chip)
        }

        // Scroll to end to show the edit text after adding chip
        binding.scrollView.post {
            binding.scrollView.fullScroll(FOCUS_DOWN)
        }

        binding.editTextInput.hint = ""
        return chip
    }

    fun removeChip(chip: Chip) {
        binding.chipGroup.removeView(chip)
    }

    fun removeChipByName(text: String) {
        for (i in 0 until binding.chipGroup.childCount) {
            val child = binding.chipGroup.getChildAt(i)
            if (child is Chip && child.text.toString() == text) {
                binding.chipGroup.removeView(child)
                break
            }
        }
    }

    fun getAllChipTexts(): List<String> {
        val texts = mutableListOf<String>()
        for (i in 0 until binding.chipGroup.childCount) {
            val child = binding.chipGroup.getChildAt(i)
            if (child is Chip) {
                texts.add(child.text.toString())
            }
        }
        return texts
    }

    fun disableHint() {
        binding.editTextInput.hint = ""
    }

    fun enableHint() {
        binding.editTextInput.hint = context.getString(R.string.text_select_payee_name)
    }

    fun clearChips() {
        binding.chipGroup.removeAllViews()
    }
}

