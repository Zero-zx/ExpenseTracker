package add

import androidx.recyclerview.widget.DiffUtil
import account.model.AccountType
import android.view.View
import base.BaseListAdapter
import com.example.login.databinding.ItemAccountTypeBinding

class AccountTypeAdapter(
    private var selectedAccountType: AccountType = AccountType.CASH,
    onClick: (AccountType) -> Unit
) : BaseListAdapter<AccountType, ItemAccountTypeBinding>(
    inflateMethod = ItemAccountTypeBinding::inflate,
    diffCallback = object : DiffUtil.ItemCallback<AccountType>() {
        override fun areItemsTheSame(oldItem: AccountType, newItem: AccountType): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AccountType, newItem: AccountType): Boolean {
            return oldItem == newItem && oldItem == selectedAccountType
        }
    },
    onClick = onClick
) {
    override fun onBind(
        item: AccountType,
        binding: ItemAccountTypeBinding
    ) {
        binding.imageViewAccountType.setImageResource(item.iconRes)
        binding.textViewAccountType.text = item.rawValue
        binding.root.isSelected = item == selectedAccountType
        binding.imageViewChecked.visibility =
            if (item == selectedAccountType) View.VISIBLE else View.GONE
    }

    fun updateSelectedType(accountType: AccountType) {
        val oldSelected = selectedAccountType
        selectedAccountType = accountType
        // Only notify changed for the old and new selected items
        val oldPosition = currentList.indexOf(oldSelected)
        val newPosition = currentList.indexOf(accountType)
        if (oldPosition >= 0) notifyItemChanged(oldPosition)
        if (newPosition >= 0) notifyItemChanged(newPosition)
    }
}