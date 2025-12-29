package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R
import com.example.statistics.databinding.ItemFinancialStatementBinding
import java.text.NumberFormat
import java.util.Locale
import presentation.detail.model.AssetItem
import presentation.detail.model.LiabilityItem

class FinancialStatementAdapter(
    private val onItemClick: ((Long) -> Unit)? = null
) : ListAdapter<Any, RecyclerView.ViewHolder>(FinancialStatementDiffCallback()) {

    companion object {
        private const val TYPE_ASSET = 0
        private const val TYPE_LIABILITY = 1
        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AssetItem -> TYPE_ASSET
            is LiabilityItem -> TYPE_LIABILITY
            else -> TYPE_ASSET
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFinancialStatementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FinancialStatementViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is AssetItem -> (holder as FinancialStatementViewHolder).bindAsset(item)
            is LiabilityItem -> (holder as FinancialStatementViewHolder).bindLiability(item)
        }
    }

    class FinancialStatementViewHolder(
        private val binding: ItemFinancialStatementBinding,
        private val onItemClick: ((Long) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val itemId = binding.root.tag as? Long
                itemId?.let { onItemClick?.invoke(it) }
            }
        }

        fun bindAsset(asset: AssetItem) {
            binding.root.tag = asset.id
            binding.apply {
                textViewName.text = asset.name
                textViewAmount.text = currencyFormatter.format(asset.amount)
                textViewAmount.setTextColor(
                    root.context.getColor(
                        if (asset.amount < 0) R.color.red_expense
                        else R.color.green_income
                    )
                )
                setIcon(asset.iconRes)
            }
        }

        fun bindLiability(liability: LiabilityItem) {
            binding.root.tag = liability.id
            binding.apply {
                textViewName.text = liability.name
                textViewAmount.text = currencyFormatter.format(liability.amount)
                textViewAmount.setTextColor(
                    root.context.getColor(com.example.common.R.color.red_expense)
                )
                setIcon(liability.iconRes)
            }
        }

        private fun ItemFinancialStatementBinding.setIcon(iconRes: Int?) {
            imageViewIcon.setImageResource(
                iconRes ?: com.example.common.R.drawable.account_wallet
            )
            imageViewIcon.visibility = android.view.View.VISIBLE
        }
    }

    private class FinancialStatementDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is AssetItem && newItem is AssetItem -> oldItem.id == newItem.id
                oldItem is LiabilityItem && newItem is LiabilityItem -> oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
}
