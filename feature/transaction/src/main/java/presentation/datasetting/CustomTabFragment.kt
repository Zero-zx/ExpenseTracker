package presentation.datasetting

import android.widget.TextView
import base.UIState
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import ui.openDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CustomTabFragment : DataSettingTabFragment() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun updateUI(state: DataSettingUiState) {
        val items = getCustomOptions(state)
        adapter.submitList(items)
    }

    private fun getCustomOptions(state: DataSettingUiState): List<DataSettingAdapterItem> {
        val items = mutableListOf<DataSettingAdapterItem>()
        val selectedOption = state.selectedOption as? DataSettingOption.CustomOption

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "All time",
                isSelected = selectedOption?.type == CustomOptionType.ALL_TIME,
                onClick = {
                    parentViewModel?.selectCustomOption(CustomOptionType.ALL_TIME)
                    applySelection()
                }
            )
        )

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Custom",
                isSelected = selectedOption?.type == CustomOptionType.CUSTOM,
                onClick = {
                    parentViewModel?.selectCustomOption(CustomOptionType.CUSTOM)
                }
            )
        )

        if (selectedOption?.type == CustomOptionType.CUSTOM) {
            val fromDateText =
                state.customFromDate?.let { dateFormat.format(Date(it)) } ?: "Select date"
            val toDateText =
                state.customToDate?.let { dateFormat.format(Date(it)) } ?: "Select date"

            items.add(
                DataSettingAdapterItem.CustomDateItem(
                    label = "From",
                    dateText = fromDateText,
                    onClick = {
                        val textView = TextView(requireContext())
                        if (fromDateText != "Select date") {
                            textView.text = fromDateText
                        }
                        openDatePicker(textView) { dateMillis ->
                            parentViewModel?.setCustomFromDate(dateMillis)
                            val uiState = parentViewModel?.uiState?.value
                            if (uiState is UIState.Success) {
                                val updatedState = uiState.data
                                if (updatedState.customFromDate != null && updatedState.customToDate != null) {
                                    applySelection()
                                }
                            }
                        }
                    }
                )
            )

            items.add(
                DataSettingAdapterItem.CustomDateItem(
                    label = "To",
                    dateText = toDateText,
                    onClick = {
                        val textView = TextView(requireContext())
                        if (toDateText != "Select date") {
                            textView.text = toDateText
                        }
                        openDatePicker(textView) { dateMillis ->
                            parentViewModel?.setCustomToDate(dateMillis)
                            val uiState = parentViewModel?.uiState?.value
                            if (uiState is UIState.Success) {
                                val updatedState = uiState.data
                                if (updatedState.customFromDate != null && updatedState.customToDate != null) {
                                    applySelection()
                                }
                            }
                        }
                    }
                )
            )
        }

        return items
    }
}

