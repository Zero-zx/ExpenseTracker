package presentation.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemTransactionBinding
import data.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionListAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionListAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance()
        private val dateFormatter = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.apply {
                // Category
//                textViewCategory.text = transaction.category.title
//                textViewCategoryIcon.text = transaction.category.icon

                // Description
                if (!transaction.description.isNullOrEmpty()) {
                    textViewDescription.text = transaction.description
                    textViewDescription.visibility = View.VISIBLE
                } else {
                    textViewDescription.visibility = View.GONE
                }

                // Date
                textViewDate.text = dateFormatter.format(Date(transaction.createAt))

                // Partner
//                transaction.partner?.let { partner ->
//                    textViewPartner.text = "Partner: ${partner.partnerName}"
//                    textViewPartner.visibility = View.VISIBLE
//                } ?: run {
//                    textViewPartner.visibility = View.GONE
//                }

                // Event
//                transaction.event?.let { event ->
//                    textViewEvent.text = "Event: ${event.eventName}"
//                    textViewEvent.visibility = View.VISIBLE
//                } ?: run {
//                    textViewEvent.visibility = View.GONE
//                }
//
//                // Amount and Type
//                val (sign, colorRes) = when (transaction.type) {
//                    TransactionType.IN -> "+" to R.color.income_green
//                    TransactionType.OUT -> "-" to R.color.expense_red
//                    TransactionType.LEND -> "→" to R.color.lend_blue
//                    TransactionType.LOAN -> "←" to R.color.loan_orange
//                }
//
//                val color = ContextCompat.getColor(binding.root.context, colorRes)
//                textViewAmount.text = "$sign${currencyFormatter.format(transaction.amount)}"
//                textViewAmount.setTextColor(color)
//                textViewType.text = transaction.type.name
//                textViewType.setTextColor(color)
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
