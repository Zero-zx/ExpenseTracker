package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemEventTransactionBinding
import payee.model.Payee

class PayeeAdapter(
    private val onItemClick: (Payee) -> Unit,
    private val onItemUpdate: (Payee) -> Unit
) : ListAdapter<Payee, PayeeAdapter.PayeeViewHolder>(PayeeDiffCallback()) {
    // Use names instead of IDs since temporary payees all have id = -1L
    private val selectedPayeeNames = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayeeViewHolder {
        val binding = ItemEventTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PayeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PayeeViewHolder, position: Int) {
        holder.bind(getItem(position), selectedPayeeNames.contains(getItem(position).name))
    }

    inner class PayeeViewHolder(
        private val binding: ItemEventTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payee: Payee, isSelected: Boolean) {
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

    private class PayeeDiffCallback : DiffUtil.ItemCallback<Payee>() {
        override fun areItemsTheSame(oldItem: Payee, newItem: Payee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payee, newItem: Payee): Boolean {
            return oldItem == newItem
        }
    }

    fun setSelectedPayees(payees: List<Payee>) {
        val newNames = payees.map { it.name }.toSet()
        if (newNames == selectedPayeeNames) return

        // Find positions that changed and notify them
        val previousNames = selectedPayeeNames.toSet()
        selectedPayeeNames.clear()
        selectedPayeeNames.addAll(newNames)

        // Notify items that were selected or deselected
        val changedNames = (previousNames + newNames) - (previousNames.intersect(newNames))
        changedNames.forEach { changedName ->
            val position = currentList.indexOfFirst { it.name == changedName }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }
}
