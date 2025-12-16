package presentation.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import base.BaseListAdapter
import com.example.statistics.databinding.ItemReportBinding
import presentation.detail.model.ReportItem
import java.text.NumberFormat
import java.util.Locale

class ReportItemAdapter(
    onClick: ((ReportItem) -> Unit)? = null
) : BaseListAdapter<ReportItem, ItemReportBinding>(
    inflateMethod = { inflater, parent, _ ->
        ItemReportBinding.inflate(inflater, parent, false)
    },
    diffCallback = object : DiffUtil.ItemCallback<ReportItem>() {
        override fun areItemsTheSame(oldItem: ReportItem, newItem: ReportItem): Boolean {
            return oldItem.typeName == newItem.typeName
        }

        override fun areContentsTheSame(oldItem: ReportItem, newItem: ReportItem): Boolean {
            return oldItem == newItem
        }
    },
    onClick = onClick
) {
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }

    override fun onBind(item: ReportItem, binding: ItemReportBinding) {
        binding.apply {
            textViewTypeName.text = item.typeName
            textViewIncome.text = "${formatAmount(item.income)}"
            textViewOutcome.text = "${formatAmount(item.outcome)}"
            textViewRest.text = "${formatAmount(item.rest)}"
        }
    }

    private fun formatAmount(amount: Double): String {
        return numberFormat.format(amount)
    }
}


