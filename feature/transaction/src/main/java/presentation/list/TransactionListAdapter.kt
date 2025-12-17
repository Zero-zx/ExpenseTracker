package presentation.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemTransactionBinding
import transaction.model.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionListAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemSelect: ((Transaction) -> Unit)? = null
) : ListAdapter<TransactionListItem, RecyclerView.ViewHolder>(TransactionListItemDiffCallback()) {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val viewPool = RecyclerView.RecycledViewPool()
    private var isSelectionMode: Boolean = false
    private var selectedTransactionIds: Set<Long> = emptySet()

    fun setSelectionMode(isSelectionMode: Boolean) {
        this.isSelectionMode = isSelectionMode
        notifyDataSetChanged()
    }

    fun setSelectedTransactions(selectedIds: Set<Long>) {
        this.selectedTransactionIds = selectedIds
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        // We only expect DateHeader items now
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item is TransactionListItem.DateHeader) {
            (holder as DateGroupViewHolder).bind(item)
        }
    }

    inner class DateGroupViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val childAdapter = TransactionCategoryAdapter(
            onItemClick = onItemClick,
            onItemSelect = onItemSelect
        )

        init {
            binding.recyclerViewCategories.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = childAdapter
                setRecycledViewPool(viewPool)
                isNestedScrollingEnabled = false
            }
        }

        fun bind(header: TransactionListItem.DateHeader) {
            binding.apply {
                textViewDate.text = header.date
                textViewDayLabel.text = header.dayName
                val formattedAmount = currencyFormatter.format(header.totalAmount)
                textViewDayAmount.text = formattedAmount.replace("$", "₫")

                textViewDayAmount.setTextColor(
                    if (header.totalAmount < 0) {
                        binding.root.context.getColor(com.example.common.R.color.red_expense)
                    } else {
                        binding.root.context.getColor(com.example.common.R.color.green_income)
                    }
                )

                // Update child adapter selection state
                childAdapter.setSelectionMode(this@TransactionListAdapter.isSelectionMode)
                childAdapter.setSelectedTransactions(this@TransactionListAdapter.selectedTransactionIds)

                // Show/hide group checkbox in selection mode and set checked state
                val groupIds = header.transactions.map { it.id }
                checkboxSelection.visibility =
                    if (this@TransactionListAdapter.isSelectionMode) View.VISIBLE else View.GONE

                // detach listener before setting checked programmatically
                checkboxSelection.setOnCheckedChangeListener(null)
                checkboxSelection.isChecked = this@TransactionListAdapter.selectedTransactionIds.containsAll(groupIds)

                // attach listener to toggle child selection by calling onItemSelect only for items that need toggling
                checkboxSelection.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                    onItemSelect?.let { selectFn ->
                        if (isChecked) {
                            // select all child transactions that are not currently selected
                            header.transactions.forEach { txn ->
                                if (!this@TransactionListAdapter.selectedTransactionIds.contains(txn.id)) {
                                    selectFn(txn)
                                }
                            }
                        } else {
                            // deselect all child transactions that are currently selected
                            header.transactions.forEach { txn ->
                                if (this@TransactionListAdapter.selectedTransactionIds.contains(txn.id)) {
                                    selectFn(txn)
                                }
                            }
                        }
                    }
                }

                // Submit child list
                childAdapter.submitList(header.transactions)
            }
        }
    }

    private class TransactionListItemDiffCallback : DiffUtil.ItemCallback<TransactionListItem>() {
        override fun areItemsTheSame(
            oldItem: TransactionListItem,
            newItem: TransactionListItem
        ): Boolean {
            return when {
                oldItem is TransactionListItem.DateHeader && newItem is TransactionListItem.DateHeader -> {
                    oldItem.date == newItem.date
                }

                oldItem is TransactionListItem.TransactionItem && newItem is TransactionListItem.TransactionItem -> {
                    oldItem.transaction.id == newItem.transaction.id
                }

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: TransactionListItem,
            newItem: TransactionListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
