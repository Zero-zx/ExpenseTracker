package presentation.datasetting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemDataSettingCustomDateBinding
import com.example.transaction.databinding.ItemDataSettingOptionBinding

sealed class DataSettingAdapterItem {
    data class OptionItem(
        val text: String,
        val isSelected: Boolean,
        val onClick: () -> Unit
    ) : DataSettingAdapterItem()

    data class CustomDateItem(
        val label: String,
        val dateText: String,
        val onClick: () -> Unit
    ) : DataSettingAdapterItem()
}

class DataSettingAdapter : ListAdapter<DataSettingAdapterItem, RecyclerView.ViewHolder>(
    DataSettingAdapterDiffCallback()
) {

    companion object {
        private const val VIEW_TYPE_OPTION = 0
        private const val VIEW_TYPE_CUSTOM_DATE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataSettingAdapterItem.OptionItem -> VIEW_TYPE_OPTION
            is DataSettingAdapterItem.CustomDateItem -> VIEW_TYPE_CUSTOM_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_OPTION -> {
                val binding = ItemDataSettingOptionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OptionViewHolder(binding)
            }

            VIEW_TYPE_CUSTOM_DATE -> {
                val binding = ItemDataSettingCustomDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CustomDateViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DataSettingAdapterItem.OptionItem -> {
                (holder as OptionViewHolder).bind(item)
            }

            is DataSettingAdapterItem.CustomDateItem -> {
                (holder as CustomDateViewHolder).bind(item)
            }
        }
    }

    class OptionViewHolder(
        private val binding: ItemDataSettingOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataSettingAdapterItem.OptionItem) {
            binding.apply {
                textViewOption.text = item.text
//                imageViewCheck.visibility = if (item.isSelected) View.VISIBLE else View.GONE
                root.setOnClickListener { item.onClick() }
            }
        }
    }

    class CustomDateViewHolder(
        private val binding: ItemDataSettingCustomDateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataSettingAdapterItem.CustomDateItem) {
            binding.apply {
                textViewLabel.text = item.label
                textViewDate.text = item.dateText
                textViewDate.setOnClickListener { item.onClick() }
            }
        }
    }

    private class DataSettingAdapterDiffCallback : DiffUtil.ItemCallback<DataSettingAdapterItem>() {
        override fun areItemsTheSame(
            oldItem: DataSettingAdapterItem,
            newItem: DataSettingAdapterItem
        ): Boolean {
            return when {
                oldItem is DataSettingAdapterItem.OptionItem &&
                        newItem is DataSettingAdapterItem.OptionItem -> {
                    oldItem.text == newItem.text
                }

                oldItem is DataSettingAdapterItem.CustomDateItem &&
                        newItem is DataSettingAdapterItem.CustomDateItem -> {
                    oldItem.label == newItem.label
                }

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: DataSettingAdapterItem,
            newItem: DataSettingAdapterItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}


