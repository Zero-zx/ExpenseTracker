package add

import account.model.AccountType
import androidx.fragment.app.activityViewModels
import base.BaseBottomSheet
import com.example.login.databinding.BottomSheetAccountTypeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountTypeBottomSheet() : BaseBottomSheet<BottomSheetAccountTypeBinding>(
    BottomSheetAccountTypeBinding::inflate,
) {
    private val viewModel: AddAccountViewModel by activityViewModels()

    override fun initView() {
        val adapter = AccountTypeAdapter(viewModel.selectedAccountType.value ?: AccountType.CASH) {
            viewModel.updateAccountType(it)
            dismiss()
        }
        binding.recyclerView.adapter = adapter
        adapter.submitList(AccountType.entries)
    }
}