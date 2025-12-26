package presentation.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemTransactionCategoryBinding
import category.model.CategoryType
import helpers.formatAsCurrency
import transaction.model.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionCategoryAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemSelect: ((Transaction) -> Unit)? = null,
    private var isSelectionMode: Boolean = false,
    private var selectedTransactionIds: Set<Long> = emptySet()
) : ListAdapter<Transaction, TransactionCategoryAdapter.CategoryViewHolder>(TransactionDiffCallback()) {


    fun setSelectionMode(isSelectionMode: Boolean) {
        this.isSelectionMode = isSelectionMode
        notifyDataSetChanged()
    }

    fun setSelectedTransactions(selectedIds: Set<Long>) {
        this.selectedTransactionIds = selectedIds
        notifyDataSetChanged()
    }

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
                    val transaction = getItem(pos)
                    if (isSelectionMode && onItemSelect != null) {
                        onItemSelect(transaction)
                    } else {
                        onItemClick(transaction)
                    }
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
                textAmount.text = "$sign${transaction.amount.formatAsCurrency("₫")}"

                val amountColor = when (transaction.category.type) {
                    CategoryType.INCOME -> binding.root.context.getColor(com.example.common.R.color.green_income)
                    CategoryType.EXPENSE -> binding.root.context.getColor(com.example.common.R.color.red_expense)
                    else -> binding.root.context.getColor(com.example.common.R.color.black_text)
                }
                textAmount.setTextColor(amountColor)

                textSource.text = transaction.account.username

                // Show/hide checkbox based on selection mode
                if (isSelectionMode) {
                    checkboxSelection.visibility = View.VISIBLE
                    checkboxSelection.isChecked = selectedTransactionIds.contains(transaction.id)
                } else {
                    checkboxSelection.visibility = View.GONE
                }
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
