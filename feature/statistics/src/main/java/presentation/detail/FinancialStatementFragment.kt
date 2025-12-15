package presentation.detail

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentFinancialStatementBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.FinancialStatementAdapter
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class FinancialStatementFragment : BaseFragment<FragmentFinancialStatementBinding>(
    FragmentFinancialStatementBinding::inflate
) {
    private val viewModel: FinancialStatementViewModel by viewModels()
    
    // Lazy initialization để chỉ tạo khi cần
    private val currencyFormatter by lazy { 
        NumberFormat.getCurrencyInstance(Locale.getDefault()) 
    }
    
    private val assetsAdapter = FinancialStatementAdapter { accountId ->
        // TODO: Handle asset item click - navigate to account detail
    }
    
    private val liabilitiesAdapter = FinancialStatementAdapter { accountId ->
        // TODO: Handle liability item click - navigate to account detail
    }

    override fun initView() {
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        binding.recyclerViewAssets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = assetsAdapter
        }

        binding.recyclerViewLiabilities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = liabilitiesAdapter
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {}
                is UIState.Loading -> {}
                is UIState.Success -> {
                    updateUI(state.data)
                }
                is UIState.Error -> {
                    // TODO: Handle error - show error message to user
                }
            }
        }
    }

    private fun updateUI(data: presentation.detail.model.FinancialStatementData) {
        binding.apply {
            // Update summary cards
            updateNetWorth(data.netWorth)
            updateTotalAmount(data.totalAmount, data.assets.size)
            updateLiabilities(data.totalLiabilities, data.liabilities.size)
            
            // Update section headers with counts
            textViewAssetsHeader.text = "Assets (${data.assets.size})"
            textViewLiabilitiesHeader.text = "Liabilities (${data.liabilities.size})"

            // Update lists
            assetsAdapter.submitList(data.assets)
            liabilitiesAdapter.submitList(data.liabilities)
        }
    }

    private fun updateNetWorth(netWorth: Double) {
        binding.textViewNetWorth.apply {
            text = currencyFormatter.format(netWorth)
            setTextColor(
                requireContext().getColor(
                    if (netWorth < 0) com.example.common.R.color.red_expense
                    else com.example.common.R.color.green_income
                )
            )
        }
    }

    private fun updateTotalAmount(totalAmount: Double, assetCount: Int) {
        binding.textViewTotalAmount.apply {
            text = currencyFormatter.format(totalAmount)
            setTextColor(
                requireContext().getColor(
                    if (totalAmount < 0) com.example.common.R.color.red_expense
                    else com.example.common.R.color.green_income
                )
            )
        }
        binding.textViewTotalAmountLabel.text = "Total amount ($assetCount)"
    }

    private fun updateLiabilities(totalLiabilities: Double, liabilityCount: Int) {
        binding.textViewLiabilitiesAmount.apply {
            text = currencyFormatter.format(totalLiabilities)
            setTextColor(requireContext().getColor(com.example.common.R.color.red_expense))
        }
        binding.textViewLiabilitiesLabel.text = "Liabilities ($liabilityCount)"
    }
}
