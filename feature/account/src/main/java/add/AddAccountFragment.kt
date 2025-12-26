package add

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import base.BaseFragment
import base.UIState
import com.example.login.databinding.FragmentAddAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CalculatorManager
import ui.CalculatorProvider
import ui.navigateBack
import ui.showNotImplementToast

@AndroidEntryPoint
class AddAccountFragment : BaseFragment<FragmentAddAccountBinding>(
    FragmentAddAccountBinding::inflate
) {

    private val viewModel: AddAccountViewModel by activityViewModels()
    private lateinit var calculatorManager: CalculatorManager

    override fun initView() {
        setupCalculator()
    }

    override fun initListener() {
        binding.buttonSubmit.setOnClickListener {
            createAccount()
        }

        binding.buttonSave.setOnClickListener {
            createAccount()
        }

        binding.textViewAccountType.setOnClickListener {
            val bottomSheet = AccountTypeBottomSheet()
            bottomSheet.show(parentFragmentManager, "AccountTypeBottomSheet")
        }

        binding.textViewCurrency.setOnClickListener {
            showNotImplementToast()
        }

        binding.buttonBack.setOnClickListener {
            navigateBack()
        }
    }

    fun createAccount() {
        val username = binding.editeTextAccountName.text?.toString() ?: ""
        val balanceText = binding.editTextAmount.text?.toString() ?: "0"

        val balance = balanceText.toDoubleOrNull() ?: 0.0
        if (balance < 0) {
            Toast.makeText(
                context,
                "Balance must be greater than or equal to 0",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.addAccount(
            username = username,
            balance = balance
        )
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {
                    // Initial state, no action needed
                }

                is UIState.Loading -> {
                    // Show loading indicator if needed
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSave.isEnabled = false
                }

                is UIState.Success -> {
                    Toast.makeText(
                        context,
                        "Account added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.buttonSubmit.isEnabled = true
                    binding.buttonSave.isEnabled = true
                    viewModel.navigateBack()
                }

                is UIState.Error -> {
                    Toast.makeText(
                        context,
                        "Error adding account: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.buttonSubmit.isEnabled = true
                    binding.buttonSave.isEnabled = true
                }
            }
        }

        collectState(viewModel.selectedAccountType)
        { accountType ->
            binding.textViewAccountType.apply {
                setText(
                    accountType.rawValue
                )

                setImage(accountType.iconRes)

            }
        }
    }

    private fun setupCalculator() {
        val calculatorProvider = activity as? CalculatorProvider
        val calculatorView = calculatorProvider?.getCalculatorView()

        if (calculatorView != null) {
            calculatorManager = CalculatorManager(
                calculatorView = calculatorView,
                amountEditText = binding.editTextAmount,
                context = requireContext()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetState()
    }

    override fun onStop() {
        super.onStop()
        calculatorManager.hide()
    }
}

