package list

import base.BaseBottomSheet
import com.example.login.databinding.BottomSheetAccountMenuBinding

class AccountMenuBottomSheet(
    private val onTransferAccount: () -> Unit,
    private val onAdjustAccount: () -> Unit,
    private val onShareAccount: () -> Unit,
    private val onEditAccount: () -> Unit,
    private val onDeleteAccount: () -> Unit,
    private val onInactiveAccount: () -> Unit
) : BaseBottomSheet<BottomSheetAccountMenuBinding>(
    BottomSheetAccountMenuBinding::inflate
) {

    override fun initListener() {
        binding.itemTransfer.setOnClickListener {
            onTransferAccount()
            dismiss()
        }

        binding.itemAdjustment.setOnClickListener {
            onAdjustAccount()
            dismiss()
        }

        binding.itemShareAccount.setOnClickListener {
            onShareAccount()
            dismiss()
        }
        binding.itemEdit.setOnClickListener {
            onEditAccount()
            dismiss()
        }

        binding.itemDelete.setOnClickListener {
            onDeleteAccount()
            dismiss()
        }

        binding.itemInactive.setOnClickListener {
            onInactiveAccount()
            dismiss()
        }
    }
}


