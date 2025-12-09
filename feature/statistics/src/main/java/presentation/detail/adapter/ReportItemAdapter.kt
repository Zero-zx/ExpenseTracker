package presentation.detail.adapter

import base.BaseAdapter
import com.example.statistics.databinding.ItemReportBinding
import presentation.detail.model.ReportItem
import java.text.NumberFormat
import java.util.Locale

class ReportItemAdapter(
    onClick: ((ReportItem) -> Unit)? = null
) : BaseAdapter<ReportItem, ItemReportBinding>(
    { inflater, parent, _ ->
        ItemReportBinding.inflate(inflater, parent, false)
    },
    onClick
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

