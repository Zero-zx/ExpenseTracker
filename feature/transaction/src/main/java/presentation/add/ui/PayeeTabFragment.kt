package presentation.add.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentPayeeTabBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.add.adapter.PayeeAdapter
import presentation.add.model.PayeeTabType
import presentation.add.viewModel.PayeeSelectViewModel
import transaction.model.Payee
import ui.ChipInputView
import ui.CustomAlertDialog
import ui.showEditPayeeDialog
import com.example.transaction.R as TransactionR

@AndroidEntryPoint
class PayeeTabFragment : BaseFragment<FragmentPayeeTabBinding>(
    FragmentPayeeTabBinding::inflate
) {
    private val viewModel: PayeeSelectViewModel by viewModels()
    private lateinit var adapter: PayeeAdapter
    private var tabType: PayeeTabType = PayeeTabType.RECENT

    // Cached parent fragment
    private val parentPayeeFragment: PayeeSelectFragment?
        get() = parentFragment as? PayeeSelectFragment

    private val selectedPayeeNames: Set<String>
        get() = parentPayeeFragment?.getSelectedPayeeNames() ?: emptySet()

    private var textChangeJob: Job? = null
    private val textChangeDelay = 1000L // 1 second delay before converting text to chip

    override fun initView() {
        adapter = PayeeAdapter(
            onItemClick = { payee -> togglePayeeSelection(payee.name) },
            onItemUpdate = { payee -> handlePayeeEdit(payee) }
        )

        // Setup recyclerView - use the one in layout_add_event
        binding.layoutAddEvent.post {
            val recyclerViewInLayout =
                binding.layoutAddEvent.findViewById<androidx.recyclerview.widget.RecyclerView>(
                    TransactionR.id.recycler_view_payees
                )
            recyclerViewInLayout?.adapter = adapter
        }
        // Also set adapter to root recyclerView for backward compatibility
        binding.recyclerView.adapter = adapter

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? PayeeTabType
        tabType = tabTypeArg ?: PayeeTabType.RECENT

        if (tabType == PayeeTabType.RECENT) {
            viewModel.loadRecentPayees()
        } else {
            ensureContactsPermissionAndLoad()
        }

        observeSelectedPayee()
    }

    fun observeSelectedPayee() {
        selectedPayeeNames.forEach {
            addChipToInput(it)
        }
    }


    private fun togglePayeeSelection(payeeName: String) {
        parentPayeeFragment?.onPayeeToggled(payeeName)
    }

    private fun addChipToInput(payeeName: String) {
        val inputView = binding.chipInputView
        val existingChips = inputView.getAllChipTexts()

        if (existingChips.contains(payeeName)) {
            return
        }

        inputView.addChip(payeeName) {
            // On remove chip - update parent fragment selection
            parentPayeeFragment?.let { parent ->
                if (parent.getSelectedPayeeNames().contains(payeeName)) {
                    parent.onPayeeToggled(payeeName)
                }
            }
        }

        // Update parent fragment selection if not already selected
        parentPayeeFragment?.let { parent ->
            if (!parent.getSelectedPayeeNames().contains(payeeName)) {
                parent.onPayeeToggled(payeeName)
            }
        }
    }

    private fun ensureContactsPermissionAndLoad() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.getAllPhoneContacts()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                showPermissionRationale()
            }

            else -> {
                requestReadContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Contacts permission required")
            .setMessage("We need access to your contacts to show them in a list.")
            .setPositiveButton("Allow") { _, _ ->
                requestReadContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private val requestReadContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.getAllPhoneContacts()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun initListener() {
        binding.buttonAddTrip.setOnClickListener {
            showAddPayeeView()
        }
        val editText = binding.chipInputView.getEditText()
        // Handle text change with debounce - convert text to chip when user stops typing
        binding.chipInputView.getEditText()
            .addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textChangeJob?.cancel()

                    val text = s?.toString()?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        textChangeJob = viewLifecycleOwner.lifecycleScope.launch {
                            delay(textChangeDelay)
                            editText.text?.toString()?.trim()?.let { finalText ->
                                if (finalText.isNotEmpty()) {
                                    addChipToInput(finalText)
                                    editText.text?.clear()
                                }
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            })

        binding.buttonSave.let {
            setConfirmButtonClickListener(it)
        }
    }


    private fun setConfirmButtonClickListener(button: MaterialButton) {
        button.setOnClickListener {
            val parent = parentPayeeFragment ?: return@setOnClickListener
            val inputView = binding.chipInputView

            // Handle any remaining text in editText - add it as chip
            handleRemainingText(inputView)

            // Sync chips with parent selection
            syncChipsWithParentSelectionForConfirm(inputView, parent)

            // Confirm selection and navigate back
            parent.onConfirmSelection()
        }
    }

    private fun handleRemainingText(inputView: ChipInputView) {
        val remainingText = binding.chipInputView.getEditText().text?.toString()?.trim() ?: return
        if (remainingText.isEmpty()) return

        val existingChips = inputView.getAllChipTexts()
        if (!existingChips.contains(remainingText)) {
            inputView.addChip(remainingText) {
                parentPayeeFragment?.let { parent ->
                    if (parent.getSelectedPayeeNames().contains(remainingText)) {
                        parent.onPayeeToggled(remainingText)
                    }
                }
            }
        }
        binding.chipInputView.getEditText().text?.clear()
    }

    private fun syncChipsWithParentSelectionForConfirm(
        inputView: ChipInputView,
        parent: PayeeSelectFragment
    ) {
        val chipTexts = inputView.getAllChipTexts().toSet()
        val parentSelected = parent.getSelectedPayeeNames().toMutableSet()

        // Add chips that are in ChipInputView but not in parent selection
        chipTexts.forEach { chipText ->
            if (!parentSelected.contains(chipText)) {
                parent.onPayeeToggled(chipText)
            }
        }

        // Remove from parent selection any payees that are not in ChipInputView
        parentSelected.forEach { selectedName ->
            if (!chipTexts.contains(selectedName)) {
                parent.onPayeeToggled(selectedName)
            }
        }
    }

    override fun observeData() {
        // Observe payees from PayeeSelectViewModel
        collectState(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    if (state.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        showRecyclerView()
                        adapter.submitList(state.data)
                        // Update selection based on parent fragment's selection
                        val currentSelectedNames = selectedPayeeNames
                        if (currentSelectedNames.isNotEmpty()) {
                            val selectedPayees =
                                state.data.filter { currentSelectedNames.contains(it.name) }
                            adapter.setSelectedPayees(selectedPayees)
                        } else {
                            adapter.setSelectedPayees(emptyList())
                        }
                    }
                }

                is UIState.Error -> {
                    showEmptyView()
                }

                else -> {
                    showEmptyView()
                }
            }
        }
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.recyclerView.isVisible = false
        binding.layoutAddEvent.isVisible = false
        binding.buttonAddTrip.isVisible = tabType == PayeeTabType.RECENT
    }

    fun showRecyclerView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = false
        binding.layoutAddEvent.isVisible = true
    }

    fun showAddPayeeView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = false
        binding.layoutAddEvent.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textChangeJob?.cancel()
    }

    fun refreshSelection() {
        // Refresh adapter selection
        val currentList = adapter.currentList
        if (currentList.isNotEmpty()) {
            val selectedPayees = currentList.filter { selectedPayeeNames.contains(it.name) }
            adapter.setSelectedPayees(selectedPayees)
        }

        // Sync chips in ChipInputView with parent selection
        if (binding.layoutAddEvent.isVisible) {
            syncChipsWithParentSelection(binding.chipInputView)
        }
    }

    private fun syncChipsWithParentSelection(inputView: ChipInputView) {
        val currentChipTexts = inputView.getAllChipTexts().toSet()
        val parentSelectedNames = selectedPayeeNames

        // Remove chips that are not in parent selection
        currentChipTexts.forEach { chipText ->
            if (!parentSelectedNames.contains(chipText)) {
                inputView.removeChipByName(chipText)
            }
        }

        // Add chips that are in parent selection but not in ChipInputView
        parentSelectedNames.forEach { selectedName ->
            if (!currentChipTexts.contains(selectedName)) {
                inputView.addChip(selectedName) {
                    parentPayeeFragment?.let { parent ->
                        if (parent.getSelectedPayeeNames().contains(selectedName)) {
                            parent.onPayeeToggled(selectedName)
                        }
                    }
                }
            }
        }
    }

    private fun handlePayeeEdit(payee: Payee) {
        showEditPayeeDialog(
            payeeName = payee.name,
            onUpdate = { name ->
                viewModel.updatePayee(
                    payee.copy(name = name)
                )
            },
            onDelete = {
                showDeleteConfirmation(payee)
            }
        )
    }

    private fun showDeleteConfirmation(payee: Payee) {
        CustomAlertDialog.Builder(requireContext())
            .setTitle("Delete Payee")
            .setMessage("Are you sure you want to delete '${payee.name}'?")
            .setPositiveButton("Delete") { dialog ->
                viewModel.deletePayee(payee.id)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        private const val ARG_TAB_TYPE = "payee_tab_type"
        const val ARG_SELECTED_PAYEE_IDS = "selected_payee_ids"

        fun newInstance(tabType: PayeeTabType): PayeeTabFragment {
            return PayeeTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}
