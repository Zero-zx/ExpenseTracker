package presentation.add.adapter

import account.model.Account
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemAccountTransactionBinding


class AccountAdapter(
    private val onItemClick: (Account) -> Unit
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {
    private var selectedPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class AccountViewHolder(
        private val binding: ItemAccountTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previouslySelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previouslySelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(account: Account, isSelected: Boolean) {
            binding.apply {
                itemView.isSelected = isSelected
                textViewName.text = account.username
                textViewAmount.text = account.balance.toString()
            }
        }
    }

    private class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }
}