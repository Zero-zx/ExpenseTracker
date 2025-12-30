package presentation.datasetting

import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentDataSettingTabBinding

abstract class DataSettingTabFragment : BaseFragment<FragmentDataSettingTabBinding>(
    FragmentDataSettingTabBinding::inflate
) {
    protected lateinit var adapter: DataSettingAdapter

    protected val parentViewModel: DataSettingViewModel?
        get() = (parentFragment as? DataSettingFragment)?.viewModel

    override fun initView() {
        adapter = DataSettingAdapter()
        binding.recyclerView.adapter = adapter
    }

    override fun observeData() {
        parentViewModel?.let { viewModel ->
            collectFlow(viewModel.uiState) { state ->
                when (state) {
                    is UIState.Success -> {
                        updateUI(state.data)
                    }

                    else -> {}
                }
            }
        }
    }

    abstract fun updateUI(state: DataSettingUiState)

    protected fun applySelection() {
        (parentFragment as? DataSettingFragment)?.applySelection()
    }
}


