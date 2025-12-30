package presentation.datasetting

class QuarterTabFragment : DataSettingTabFragment() {

    override fun updateUI(state: DataSettingUiState) {
        val items = getQuarterOptions(state)
        adapter.submitList(items)
    }

    private fun getQuarterOptions(state: DataSettingUiState): List<DataSettingAdapterItem> {
        val items = mutableListOf<DataSettingAdapterItem>()
        val selectedOption = state.selectedOption as? DataSettingOption.QuarterOption

        for (i in 1..4) {
            items.add(
                DataSettingAdapterItem.OptionItem(
                    text = "Quarter $i",
                    isSelected = selectedOption?.quarter == i,
                    onClick = {
                        parentViewModel?.selectQuarter(i)
                        applySelection()
                    }
                )
            )
        }

        return items
    }
}


