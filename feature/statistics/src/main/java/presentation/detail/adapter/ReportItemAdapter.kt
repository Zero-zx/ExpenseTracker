package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
            textViewIncome.text = "Income: ${formatAmount(item.income)}"
            textViewOutcome.text = "Outcome: ${formatAmount(item.outcome)}"
            textViewRest.text = "Rest: ${formatAmount(item.rest)}"
        }
    }

    private fun formatAmount(amount: Double): String {
        return numberFormat.format(amount)
    }
}

