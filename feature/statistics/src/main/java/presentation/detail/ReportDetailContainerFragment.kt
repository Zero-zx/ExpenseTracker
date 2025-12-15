package presentation.detail

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.commit
import base.BaseFragment
import com.example.statistics.databinding.FragmentReportDetailContainerBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.ReportType

@AndroidEntryPoint
class ReportDetailContainerFragment : BaseFragment<FragmentReportDetailContainerBinding>(
    FragmentReportDetailContainerBinding::inflate
) {
    private var currentReportType: ReportType = ReportType.FINANCIAL_STATEMENT
    private var dropdownAdapter: ArrayAdapter<String>? = null
    private var isInitialLoad = true

    companion object {
        private const val KEY_CURRENT_REPORT_TYPE = "current_report_type"
        const val ARG_REPORT_TYPE = "report_type"

        fun newInstance(reportType: ReportType = ReportType.FINANCIAL_STATEMENT): ReportDetailContainerFragment {
            return ReportDetailContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REPORT_TYPE, reportType.name)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getString(KEY_CURRENT_REPORT_TYPE)?.let {
            currentReportType = ReportType.valueOfOrNull(it) ?: ReportType.FINANCIAL_STATEMENT
        }
    }

    override fun initView() {
        setupDropdownMenu()
        setupBackButton()
        loadInitialFragment()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Restore child fragment if needed
        if (savedInstanceState != null && childFragmentManager.fragments.isEmpty()) {
            switchFragment(currentReportType)
        }
    }

    override fun onResume() {
        super.onResume()
        // Only setup dropdown if adapter is null (fragment was recreated)
        if (dropdownAdapter == null) {
            setupDropdownMenu(preserveCurrentText = true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_REPORT_TYPE, currentReportType.name)
    }

    private fun setupDropdownMenu(preserveCurrentText: Boolean = false) {
        val reportTypes = ReportType.values()
        val reportTypeNames = reportTypes.map { it.getDisplayName() }.toTypedArray()

        dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            reportTypeNames
        )

        val autoCompleteTextView = binding.autoCompleteReportType as MaterialAutoCompleteTextView
        autoCompleteTextView.setAdapter(dropdownAdapter)
        autoCompleteTextView.threshold = 0 // Show all options immediately
        
        if (!preserveCurrentText || autoCompleteTextView.text.isNullOrEmpty()) {
            autoCompleteTextView.setText(currentReportType.getDisplayName(), false)
        }

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedType = reportTypes[position]
            if (selectedType != currentReportType) {
                currentReportType = selectedType
                switchFragment(selectedType)
            }
        }
    }

    private fun setupBackButton() {
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadInitialFragment() {
        // Only load fragment on initial creation, not when restoring
        if (isInitialLoad && childFragmentManager.fragments.isEmpty()) {
            arguments?.getString(ARG_REPORT_TYPE)?.let {
                currentReportType = ReportType.valueOfOrNull(it) ?: ReportType.FINANCIAL_STATEMENT
            }
            
            binding.autoCompleteReportType.setText(currentReportType.getDisplayName(), false)
            switchFragment(currentReportType)
            isInitialLoad = false
        }
    }

    private fun switchFragment(reportType: ReportType) {
        val fragment = createFragmentForType(reportType)
        
        childFragmentManager.commit {
            replace(binding.fragmentContainer.id, fragment)
            setReorderingAllowed(true)
        }
    }

    private fun createFragmentForType(reportType: ReportType): androidx.fragment.app.Fragment {
        return when (reportType) {
            ReportType.FINANCIAL_STATEMENT -> FinancialStatementFragment()
            ReportType.EXPENSE_VS_INCOME -> IncomeExpenseDetailFragment()
            ReportType.EXPENSE_ANALYSIS -> ExpenseAnalysisFragment()
            ReportType.INCOME_ANALYSIS -> IncomeAnalysisFragment()
            ReportType.MONEY_LENT_BORROWED -> PlaceholderReportFragment.newInstance("Money Lent/Borrowed")
            ReportType.PAYEE_PAYER -> PlaceholderReportFragment.newInstance("Payee/Payer")
            ReportType.TRIP_EVENT -> TripEventFragment()
            ReportType.FINANCIAL_ANALYSIS -> PlaceholderReportFragment.newInstance("Financial Analysis")
        }
    }
}
