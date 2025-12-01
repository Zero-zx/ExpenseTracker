package add

import account.model.AccountType
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import base.BaseFragment
import base.UIState
import com.example.login.databinding.FragmentAddAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAccountFragment : BaseFragment<FragmentAddAccountBinding>(
    FragmentAddAccountBinding::inflate
) {

    private val viewModel: AddAccountViewModel by activityViewModels()

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

        binding.buttonBack.setOnClickListener {
            viewModel.navigateBack()
        }
    }

    fun createAccount() {
        val username = binding.editeTextAccountName.text?.toString() ?: ""
        val accountTypeText = binding.textViewAccountType.getText()
        val balanceText = binding.editTextAmount.text?.toString() ?: "0"

        if (username.isBlank()) {
            Toast.makeText(
                context,
                "Please enter a username",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val accountType = try {
            AccountType.valueOf(accountTypeText.uppercase())
        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                context,
                "Please select a valid account type",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

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
            type = accountType,
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
            binding.textViewAccountType.setText(
                accountType?.rawValue ?: AccountType.CASH.rawValue
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetState()
    }
}

