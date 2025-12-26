package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.statistics.databinding.ItemAccountMultiSelectBinding
import account.model.Account
import helpers.formatAsCurrency

class AccountMultiSelectAdapter(
    private val onAccountToggle: (Long) -> Unit,
    private val selectedAccountIds: () -> Set<Long>
) : ListAdapter<Account, AccountMultiSelectAdapter.AccountViewHolder>(
    AccountDiffCallback()
) {

    private var fullList: List<Account> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountMultiSelectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = getItem(position)
        val isSelected = selectedAccountIds().contains(account.id)
        holder.bind(account, isSelected)
    }

    override fun submitList(list: List<Account>?) {
        fullList = list ?: emptyList()
        super.submitList(list)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.username.contains(query, ignoreCase = true) }
        }
        submitList(filteredList)
    }

    inner class AccountViewHolder(
        private val binding: ItemAccountMultiSelectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: Account, isSelected: Boolean) {
            binding.apply {
                textViewName.text = account.username
                textViewAmount.text = account.balance.formatAsCurrency()
                imageViewIconAccount.setImageResource(account.type.iconRes)

                // Show/hide checkbox based on selection
                imageViewChecked.isVisible = isSelected
                itemView.isSelected = isSelected

                root.setOnClickListener {
                    onAccountToggle(account.id)
                }
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

