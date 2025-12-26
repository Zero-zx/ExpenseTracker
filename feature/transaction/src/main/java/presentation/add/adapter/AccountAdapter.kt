package presentation.add.adapter

import account.model.Account
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemAccountTransactionBinding
import helpers.formatAsCurrency

class AccountAdapter(
    private val onItemClick: (Account) -> Unit
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {
    private var selectedAccountId: Long? = null
    private var selectedPosition = RecyclerView.NO_POSITION
    private var fullList: List<Account> = emptyList()
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
                val selected = (account.id == selectedAccountId) || isSelected
                itemView.isSelected = selected
                imageViewChecked.isVisible = selected
                textViewName.text = account.username
                textViewAmount.text = account.balance.formatAsCurrency()
                imageViewIconAccount.setImageResource(account.type.iconRes)
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

    fun setSelectedAccount(account: Account?) {
        val newId = account?.id
        if (newId == selectedAccountId) return
        val previousId = selectedAccountId
        selectedAccountId = newId

        // find positions and notify changes so UI updates
        val prevPos =
            if (previousId == null) -1 else currentList.indexOfFirst { it.id == previousId }
        val newPos = if (newId == null) -1 else currentList.indexOfFirst { it.id == newId }
        if (prevPos != -1) notifyItemChanged(prevPos)
        if (newPos != -1) notifyItemChanged(newPos)
        // update selectedPosition for internal click logic
        selectedPosition = newPos
    }

    override fun submitList(list: List<Account>?) {
        if (list != null) {
            // If this is the first list or the list is larger than current fullList, update fullList
            // This handles both initial load and refresh scenarios
            if (fullList.isEmpty() || list.size >= fullList.size) {
                fullList = list
            }
        }
        super.submitList(list)
        // Update selectedPosition after submitting list
        if (selectedAccountId != null && list != null) {
            val newPos = list.indexOfFirst { it.id == selectedAccountId }
            if (newPos != -1 && newPos != selectedPosition) {
                val oldPos = selectedPosition
                selectedPosition = newPos
                if (oldPos != RecyclerView.NO_POSITION && oldPos < itemCount) {
                    notifyItemChanged(oldPos)
                }
                if (newPos < itemCount) {
                    notifyItemChanged(newPos)
                }
            } else if (newPos == -1) {
                // Selected account is not in current list, reset position
                if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < itemCount) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = RecyclerView.NO_POSITION
            }
        }
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.username.contains(query, ignoreCase = true) }
        }
        submitList(filteredList)
        // Update selectedPosition after filtering
        if (selectedAccountId != null) {
            val newPos = filteredList.indexOfFirst { it.id == selectedAccountId }
            if (newPos != -1 && newPos != selectedPosition) {
                val oldPos = selectedPosition
                selectedPosition = newPos
                if (oldPos != RecyclerView.NO_POSITION && oldPos < itemCount) {
                    notifyItemChanged(oldPos)
                }
                if (newPos < itemCount) {
                    notifyItemChanged(newPos)
                }
            } else if (newPos == -1) {
                // Selected account is not in filtered list, reset position
                if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < itemCount) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = RecyclerView.NO_POSITION
            }
        }
    }
}