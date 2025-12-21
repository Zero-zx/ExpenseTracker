package presentation.datasetting

class WeekTabFragment : DataSettingTabFragment() {

    override fun updateUI(state: DataSettingUiState) {
        val items = getWeekOptions(state)
        adapter.submitList(items)
    }

    private fun getWeekOptions(state: DataSettingUiState): List<DataSettingAdapterItem> {
        val items = mutableListOf<DataSettingAdapterItem>()
        val selectedOption = state.selectedOption as? DataSettingOption.WeekOption

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "This week",
                isSelected = selectedOption?.type == WeekOptionType.THIS_WEEK,
                onClick = {
                    parentViewModel?.selectWeekOption(WeekOptionType.THIS_WEEK)
                    applySelection()
                }
            )
        )

        items.add(
            DataSettingAdapterItem.OptionItem(
                text = "Last week",
                isSelected = selectedOption?.type == WeekOptionType.LAST_WEEK,
                onClick = {
                    parentViewModel?.selectWeekOption(WeekOptionType.LAST_WEEK)
                    applySelection()
                }
            )
        )

        return items
    }
}

