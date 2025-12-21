package presentation.datasetting

import android.widget.TextView
import androidx.core.os.bundleOf
import constants.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import ui.navigateBack
import ui.openDatePicker
import ui.setSelectionResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DateTabFragment : DataSettingTabFragment() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun updateUI(state: DataSettingUiState) {
        val items = getDateOptions(state)
        adapter.submitList(items)
    }

    private fun getDateOptions(state: DataSettingUiState): List<DataSettingAdapterItem> {
        val items = mutableListOf<DataSettingAdapterItem>()
        val selectedOption = state.selectedOption as? DataSettingOption.DateOption

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Today",
                isSelected = selectedOption?.type == DateOptionType.TODAY,
                onClick = {
                    parentViewModel?.selectDateOption(DateOptionType.TODAY)
                    applySelection()
                }
            )
        )

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Yesterday",
                isSelected = selectedOption?.type == DateOptionType.YESTERDAY,
                onClick = {
                    parentViewModel?.selectDateOption(DateOptionType.YESTERDAY)
                    applySelection()
                }
            )
        )

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Select day",
                isSelected = selectedOption?.type == DateOptionType.SELECT_DAY,
                onClick = {
                    parentViewModel?.selectDateOption(DateOptionType.SELECT_DAY)
                    val textView = TextView(requireContext())
                    openDatePicker(textView) { dateMillis ->
                        val calendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startDate = calendar.timeInMillis

                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        val endDate = calendar.timeInMillis

                        val periodLabel = dateFormat.format(Date(startDate))

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

