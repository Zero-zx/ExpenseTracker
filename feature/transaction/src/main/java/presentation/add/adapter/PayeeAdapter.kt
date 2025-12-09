package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemEventTransactionBinding
import transaction.model.PayeeTransaction

class PayeeAdapter(
    private val onItemClick: (PayeeTransaction) -> Unit,
    private val onItemUpdate: (PayeeTransaction) -> Unit
) : ListAdapter<PayeeTransaction, PayeeAdapter.PayeeViewHolder>(PayeeDiffCallback()) {
    private val selectedPayeeIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayeeViewHolder {
        val binding = ItemEventTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PayeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PayeeViewHolder, position: Int) {
        holder.bind(getItem(position), selectedPayeeIds.contains(getItem(position).id))
    }

    inner class PayeeViewHolder(
        private val binding: ItemEventTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payee: PayeeTransaction, isSelected: Boolean) {
            binding.apply {
                itemView.isSelected = isSelected
                textViewName.text = payee.name

                itemView.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick(getItem(position))
                        // Selection state will be updated via setSelectedPayees
                    }
                }

                imageViewEdit.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemUpdate(getItem(position))
                    }
                }
            }
        }
    }

    private class PayeeDiffCallback : DiffUtil.ItemCallback<PayeeTransaction>() {
        override fun areItemsTheSame(oldItem: PayeeTransaction, newItem: PayeeTransaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PayeeTransaction, newItem: PayeeTransaction): Boolean {
            return oldItem == newItem
        }
    }

    fun setSelectedPayees(payees: List<PayeeTransaction>) {
        val newIds = payees.map { it.id }.toSet()
        if (newIds == selectedPayeeIds) return

        // Find positions that changed and notify them
        val previousIds = selectedPayeeIds.toSet()
        selectedPayeeIds.clear()
        selectedPayeeIds.addAll(newIds)

        // Notify items that were selected or deselected
        val changedIds = (previousIds + newIds) - (previousIds.intersect(newIds))
        changedIds.forEach { changedId ->
            val position = currentList.indexOfFirst { it.id == changedId }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }
}
