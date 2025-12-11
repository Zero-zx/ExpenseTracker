package presentation.detail

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentTabNowBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.ReportItemAdapter
import presentation.detail.model.ReportItem

@AndroidEntryPoint
class NowTabFragment : BaseFragment<FragmentTabNowBinding>(
    FragmentTabNowBinding::inflate
) {
    private val viewModel: NowTabViewModel by viewModels()
    private val adapter = ReportItemAdapter()

    override fun initView() {
        binding.recyclerViewReportItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NowTabFragment.adapter
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {}
                is UIState.Loading -> {}
                is UIState.Success -> {
                    adapter.submitList(state.data)
                }
                is UIState.Error -> {}
            }
        }
    }
}


