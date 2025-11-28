package add

import account.model.AccountType
import androidx.fragment.app.viewModels
import base.BaseBottomSheet
import com.example.login.databinding.BottomSheetAccountTypeBinding

class AccountTypeBottomSheet(
    val onClick: (AccountType) -> Unit
) : BaseBottomSheet<BottomSheetAccountTypeBinding>(
    BottomSheetAccountTypeBinding::inflate,
) {

    private val viewModel: AddAccountViewModel by viewModels()

    override fun initView() {
        val adapter = AccountTypeAdapter {
            onClick(it)
            dismiss()
        }
        binding.recyclerView.adapter = adapter
        adapter.submitList(AccountType.entries)
    }
}