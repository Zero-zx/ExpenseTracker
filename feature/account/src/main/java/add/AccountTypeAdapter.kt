package add

import account.model.AccountType
import android.view.View
import base.BaseAdapter
import com.example.login.databinding.ItemAccountTypeBinding

class AccountTypeAdapter(
    private var selectedAccountType: AccountType = AccountType.CASH,
    onClick: (AccountType) -> Unit
) : BaseAdapter<AccountType, ItemAccountTypeBinding>(
    ItemAccountTypeBinding::inflate,
    onClick
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
        selectedAccountType = accountType
        notifyDataSetChanged()
    }
}