package presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.BaseBottomSheet
import com.example.transaction.databinding.BottomSheetTransactionMenuBinding

class TransactionMenuBottomSheet(
    private val onSelectTransaction: () -> Unit,
    private val onDisplaySettings: () -> Unit,
    private val onFilterOption: () -> Unit
) : BaseBottomSheet<BottomSheetTransactionMenuBinding>(
    BottomSheetTransactionMenuBinding::inflate
) {

    override fun initListener() {
        binding.itemSelectTransaction.setOnClickListener {
            onSelectTransaction()
            dismiss()
        }

        binding.itemDisplaySettings.setOnClickListener {
            onDisplaySettings()
            dismiss()
        }

        binding.itemFilterOption.setOnClickListener {
            onFilterOption()
            dismiss()
        }
    }
}



