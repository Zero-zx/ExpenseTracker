package add

import account.model.AccountType
import base.BaseAdapter
import com.example.login.databinding.ItemAccountTypeBinding

class AccountTypeAdapter(
    onClick: (AccountType) -> Unit
) : BaseAdapter<AccountType, ItemAccountTypeBinding>(
    ItemAccountTypeBinding::inflate,
    onClick
) {
    override fun onBind(
        item: AccountType,
        binding: ItemAccountTypeBinding
    ) {
        binding.text.text = item.name
    }

}