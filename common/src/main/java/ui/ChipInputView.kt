package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.common.databinding.ChipInputViewBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class ChipInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ChipInputViewBinding =
        ChipInputViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun getChipGroup(): ChipGroup {
        return binding.chipGroup
    }

    fun getEditText(): TextInputEditText {
        return binding.editTextInput
    }

    fun addChip(text: String, onRemove: (() -> Unit)? = null): Chip {
        val chip = Chip(context)
        chip.text = text
        chip.isCloseIconVisible = onRemove != null
        chip.setOnCloseIconClickListener {
            onRemove?.invoke()
        }
        // Add chip before the edit text (edit text is the last child in ChipGroup)
        val editTextIndex = binding.chipGroup.indexOfChild(binding.editTextInput)
        if (editTextIndex >= 0) {
            binding.chipGroup.addView(chip, editTextIndex)
        } else {
            binding.chipGroup.addView(chip)
        }
        
        // Scroll to end to show the edit text after adding chip
        binding.scrollView.post {
            binding.scrollView.fullScroll(android.view.View.FOCUS_DOWN)
        }
        
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

    fun clearChips() {
        binding.chipGroup.removeAllViews()
    }
}

