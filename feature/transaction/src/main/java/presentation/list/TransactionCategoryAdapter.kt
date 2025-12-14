package presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemTransactionCategoryBinding
import transaction.model.CategoryType
import transaction.model.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionCategoryAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionCategoryAdapter.CategoryViewHolder>(TransactionDiffCallback()) {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemTransactionCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemTransactionCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(pos))
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.apply {
                imageIcon.setImageResource(transaction.category.iconRes)
                textTitle.text = transaction.category.title

                // Build amount text safely
                val sign = when (transaction.category.type) {
                    CategoryType.INCOME -> "+"
                    CategoryType.EXPENSE -> "-"
                    CategoryType.LEND -> "→"
                    CategoryType.BORROWING -> "←"
                    else -> ""
                }
                val formattedAmount = currencyFormatter.format(transaction.amount).replace("$", "₫")
                textAmount.text = buildString { append(sign); append(formattedAmount) }

                val amountColor = when (transaction.category.type) {
                    CategoryType.INCOME -> binding.root.context.getColor(com.example.common.R.color.green_income)
                    CategoryType.EXPENSE -> binding.root.context.getColor(com.example.common.R.color.red_expense)
                    else -> binding.root.context.getColor(com.example.common.R.color.black_text)
                }
                textAmount.setTextColor(amountColor)

                textSource.text = transaction.account.username
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
