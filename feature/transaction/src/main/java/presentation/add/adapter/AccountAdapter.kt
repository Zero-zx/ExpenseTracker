package presentation.add.adapter

import account.model.Account
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemAccountTransactionBinding

class AccountAdapter(
    private val onItemClick: (Account) -> Unit
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {
    private var selectedAccountId: Long? = null
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
                val selected = (account.id == selectedAccountId) || isSelected
                itemView.isSelected = selected
                imageViewChecked.isVisible = selected
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
}