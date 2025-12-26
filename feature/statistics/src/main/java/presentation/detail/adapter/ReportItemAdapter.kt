package presentation.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import base.BaseListAdapter
import com.example.statistics.databinding.ItemReportBinding
import helpers.formatAsCurrency
import presentation.detail.model.ReportItem

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

    override fun onBind(item: ReportItem, binding: ItemReportBinding) {
        binding.apply {
            textViewTypeName.text = item.typeName
            textViewIncome.text = item.income.formatAsCurrency()
            textViewOutcome.text = item.outcome.formatAsCurrency()
            textViewRest.text = item.rest.formatAsCurrency()
        }
    }
}


