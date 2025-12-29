package presentation.datasetting

import android.widget.TextView
import androidx.core.os.bundleOf
import constants.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import ui.navigateBack
import ui.openMonthPicker
import ui.setSelectionResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MonthTabFragment : DataSettingTabFragment() {

    private val monthFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    override fun updateUI(state: DataSettingUiState) {
        val items = getMonthOptions(state)
        adapter.submitList(items)
    }

    private fun getMonthOptions(state: DataSettingUiState): List<DataSettingAdapterItem> {
        val items = mutableListOf<DataSettingAdapterItem>()
        val selectedOption = state.selectedOption as? DataSettingOption.MonthOption

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "This month",
                isSelected = selectedOption?.type == MonthOptionType.THIS_MONTH,
                onClick = {
                    parentViewModel?.selectMonthOption(MonthOptionType.THIS_MONTH)
                    applySelection()
                }
            )
        )

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Last month",
                isSelected = selectedOption?.type == MonthOptionType.LAST_MONTH,
                onClick = {
                    parentViewModel?.selectMonthOption(MonthOptionType.LAST_MONTH)
                    applySelection()
                }
            )
        )

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Select month",
                isSelected = selectedOption?.type == MonthOptionType.SELECT_MONTH,
                onClick = {
                    parentViewModel?.selectMonthOption(MonthOptionType.SELECT_MONTH)
                    val textView = TextView(requireContext())
                    openMonthPicker(textView) { monthMillis ->
                        val calendar = Calendar.getInstance().apply { timeInMillis = monthMillis }
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startDate = calendar.timeInMillis

                        calendar.add(Calendar.MONTH, 1)
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        val endDate = calendar.timeInMillis

                        val periodLabel = monthFormat.format(Date(startDate))

                        setSelectionResult(
                            FragmentResultKeys.REQUEST_DATA_SETTING,
                            bundleOf(
                                FragmentResultKeys.RESULT_START_DATE to startDate,
                                FragmentResultKeys.RESULT_END_DATE to endDate,
                                FragmentResultKeys.RESULT_PERIOD_LABEL to periodLabel
                            )
                        )
                        navigateBack()
                    }
                }
            )
        )

        return items
    }
}

