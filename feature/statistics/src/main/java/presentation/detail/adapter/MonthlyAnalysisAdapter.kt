package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.statistics.databinding.ItemMonthlyAnalysisBinding
import helpers.formatAsCurrency
import presentation.detail.model.MonthlyAnalysisItem

class MonthlyAnalysisAdapter(
    private val onItemClick: ((MonthlyAnalysisItem) -> Unit)? = null,
    private val isExpense: Boolean = true
) : ListAdapter<MonthlyAnalysisItem, MonthlyAnalysisAdapter.MonthlyAnalysisViewHolder>(
    MonthlyAnalysisDiffCallback()
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyAnalysisViewHolder {
        val binding = ItemMonthlyAnalysisBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MonthlyAnalysisViewHolder(binding, onItemClick, isExpense)
    }

    override fun onBindViewHolder(holder: MonthlyAnalysisViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MonthlyAnalysisViewHolder(
        private val binding: ItemMonthlyAnalysisBinding,
        private val onItemClick: ((MonthlyAnalysisItem) -> Unit)?,
        private val isExpense: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MonthlyAnalysisItem) {
            binding.apply {
                textViewMonth.text = item.monthLabel
                textViewAmount.text = item.amount.formatAsCurrency()

                // Set color based on expense/income
                textViewAmount.setTextColor(
                    root.context.getColor(
                        if (isExpense) com.example.common.R.color.red_expense
                        else com.example.common.R.color.green_income
                    )
                )
                
                root.setOnClickListener {
                    onItemClick?.invoke(item)
                }
            }
        }
    }

    private class MonthlyAnalysisDiffCallback : DiffUtil.ItemCallback<MonthlyAnalysisItem>() {
        override fun areItemsTheSame(oldItem: MonthlyAnalysisItem, newItem: MonthlyAnalysisItem): Boolean {
            return oldItem.monthLabel == newItem.monthLabel
        }

        override fun areContentsTheSame(oldItem: MonthlyAnalysisItem, newItem: MonthlyAnalysisItem): Boolean {
            return oldItem == newItem
        }
    }
}

