package ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.example.common.R
import com.example.common.databinding.ItemCustomRowBinding

class CustomRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ItemCustomRowBinding =
        ItemCustomRowBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.CustomRowView) {
            binding.rowText.text = getString(R.styleable.CustomRowView_rowText)
            binding.rowIconStart.setImageResource(
                getResourceId(R.styleable.CustomRowView_rowIcon, 0)
            )
            val showTick = getBoolean(R.styleable.CustomRowView_isTickVisible, false)
            binding.rowIconTick.visibility = if (showTick) VISIBLE else GONE
        }
    }

    fun setText(text: String) {
        binding.rowText.text = text
    }

    fun setTickVisible(visible: Boolean) {
        binding.rowIconTick.visibility = if (visible) VISIBLE else GONE
    }
}
