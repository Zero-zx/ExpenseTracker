package presentation.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.login.databinding.FragmentAddAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import data.model.AccountType
import kotlinx.coroutines.launch
import presentation.AddAccountUiState

@AndroidEntryPoint
class AddAccountFragment : Fragment() {

    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeUiState()
    }


    private fun setupListeners() {
        binding.buttonSubmit.setOnClickListener {
            createAccount()
        }
        binding.buttonSave.setOnClickListener {
            createAccount()
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

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AddAccountUiState.Initial -> {
                            // Initial state, no action needed
                        }

                        is AddAccountUiState.Loading -> {
                            // Show loading indicator if needed
                            binding.buttonSubmit.isEnabled = false
                            binding.buttonSave.isEnabled = false
                        }

                        is AddAccountUiState.Success -> {
                            Toast.makeText(
                                context,
                                "Account added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.buttonSubmit.isEnabled = true
                            binding.buttonSave.isEnabled = true
                        }

                        is AddAccountUiState.Error -> {
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

