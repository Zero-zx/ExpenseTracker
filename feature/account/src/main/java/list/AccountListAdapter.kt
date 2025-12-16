package list

import account.model.Account
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.login.databinding.ItemAccountBinding
import java.text.NumberFormat

class AccountListAdapter(
    private val onItemClick: (Account) -> Unit
) : ListAdapter<Account, AccountListAdapter.AccountViewHolder>(AccountDiffCallback()) {

    private var selectedAccountId: Long? = null

    fun updateSelectedAccount(accountId: Long?) {
        val oldSelectedId = selectedAccountId
        selectedAccountId = accountId

        // Notify changes for old and new selected items
        currentList.forEachIndexed { index, account ->
            if (account.id == oldSelectedId || account.id == accountId) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AccountViewHolder(
        private val binding: ItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance()

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(account: Account) {
            binding.apply {
                textViewUsername.text = account.username
                iconAccount.setImageResource(account.type.iconRes)
                textViewBalance.text = currencyFormatter.format(account.balance)

                // Show selection indicator
                val isSelected = account.id == selectedAccountId
                root.isSelected = isSelected
                root.alpha = if (isSelected) 1.0f else 0.7f
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

