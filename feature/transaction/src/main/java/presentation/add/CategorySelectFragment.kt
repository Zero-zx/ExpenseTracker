package presentation.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transaction.databinding.FragmentCategorySelectBinding
import dagger.hilt.android.AndroidEntryPoint
import data.model.Category
import kotlinx.coroutines.launch
import presentation.CategoryUiState

@AndroidEntryPoint
class CategorySelectFragment : Fragment() {

    private var _binding: FragmentCategorySelectBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategorySelectViewModel by viewModels()
    private val adapter = ExpandableCategoryAdapter(
        onCategoryClick = { category ->
            // Pass selected category ID back to previous fragment
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_KEY to category.id)
            )
            requireActivity().onBackPressed()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategorySelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        observeUiState()
        viewModel.loadCategories()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategorySelectFragment.adapter
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryState.collect { state ->
                    when (state) {
                        is CategoryUiState.Loading -> {
                            // Show loading indicator if needed
                        }
                        is CategoryUiState.Success -> {
                            adapter.submitCategories(state.categories)
                        }
                        is CategoryUiState.Error -> {
                            // Handle error
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "category_select_request"
        const val RESULT_KEY = "selected_category_id"
    }
}

