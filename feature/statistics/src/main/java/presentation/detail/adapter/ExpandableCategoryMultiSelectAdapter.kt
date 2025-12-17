package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.statistics.databinding.ItemCategoryMultiSelectChildBinding
import com.example.statistics.databinding.ItemCategoryMultiSelectParentBinding
import helpers.standardize
import transaction.model.Category
import ui.setChevronRotation

class ExpandableCategoryMultiSelectAdapter(
    private val onCategoryToggle: (Long) -> Unit,
    private val onParentCategoryToggle: ((Long, List<Long>) -> Unit)? = null,
    private val selectedCategoryIds: () -> Set<Long>
) : ListAdapter<ExpandableCategoryMultiSelectAdapter.CategoryItem, ExpandableCategoryMultiSelectAdapter.CategoryViewHolder>(
    CategoryDiffCallback()
) {

    private val expandedParentIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return when (viewType) {
            VIEW_TYPE_PARENT -> {
                val binding = ItemCategoryMultiSelectParentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ParentCategoryViewHolder(binding)
            }

            VIEW_TYPE_CHILD -> {
                val binding = ItemCategoryMultiSelectChildBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChildCategoryViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position)
        val selectedIds = selectedCategoryIds()
        when (holder) {
            is ParentCategoryViewHolder -> {
                val childCategoryIds = allCategories.filter { it.parentId == item.category.id }.map { it.id }
                holder.bind(
                    category = item.category,
                    isExpanded = expandedParentIds.contains(item.category.id),
                    onToggle = { toggleExpansion(item.category.id) },
                    hasChildren = item.hasChildren,
                    isSelected = selectedIds.contains(item.category.id),
                    onCategoryToggle = { 
                        onParentCategoryToggle?.invoke(item.category.id, childCategoryIds) 
                            ?: onCategoryToggle(item.category.id)
                    }
                )
            }

            is ChildCategoryViewHolder -> {
                holder.bind(
                    item.category,
                    isSelected = selectedIds.contains(item.category.id),
                    onCategoryToggle = { onCategoryToggle(item.category.id) }
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isParent) VIEW_TYPE_PARENT else VIEW_TYPE_CHILD
    }

    private fun toggleExpansion(parentId: Long) {
        if (expandedParentIds.contains(parentId)) {
            expandedParentIds.remove(parentId)
        } else {
            expandedParentIds.add(parentId)
        }
        val categoryItems = buildCategoryItems(allCategories)
        submitList(categoryItems)
    }

    private var allCategories: List<Category> = emptyList()
    private var searchQuery: String = ""

    fun submitCategories(categories: List<Category>) {
        allCategories = categories
        val categoryItems = buildCategoryItems(categories)
        super.submitList(categoryItems)
    }

    fun filter(query: String) {
        searchQuery = query.lowercase().trim()
        val categoryItems = buildCategoryItems(allCategories)
        super.submitList(categoryItems)
    }

    private fun buildCategoryItems(categories: List<Category>): List<CategoryItem> {
        val filteredCategories = if (searchQuery.isBlank()) {
            categories
        } else {
            // Filter categories that match search query
            categories.filter { category ->
                category.title.lowercase().contains(searchQuery)
            }
        }

        val parentCategories = filteredCategories.filter { it.parentId == null }
        val result = mutableListOf<CategoryItem>()

        parentCategories.forEach { parent ->
            val childCategories = filteredCategories.filter { it.parentId == parent.id }
            
            // Only show parent if it matches or has matching children
            val shouldShowParent = searchQuery.isBlank() || 
                    parent.title.lowercase().contains(searchQuery) ||
                    childCategories.isNotEmpty()
            
            if (shouldShowParent) {
                result.add(
                    CategoryItem(
                        parent,
                        isParent = true,
                        hasChildren = childCategories.isNotEmpty()
                    )
                )

                // Auto-expand parent if searching
                val shouldExpand = expandedParentIds.contains(parent.id) || 
                        (searchQuery.isNotBlank() && childCategories.isNotEmpty())
                
                if (shouldExpand) {
                    childCategories.forEach { child ->
                        result.add(CategoryItem(child, isParent = false, hasChildren = false))
                    }
                }
            }
        }

        return result
    }

    abstract class CategoryViewHolder(
        binding: Any
    ) : RecyclerView.ViewHolder(
        when (binding) {
            is ItemCategoryMultiSelectParentBinding -> binding.root
            is ItemCategoryMultiSelectChildBinding -> binding.root
            else -> throw IllegalArgumentException("Unknown binding type")
        }
    ) {
        abstract fun bind(category: Category, isSelected: Boolean, onCategoryToggle: () -> Unit)
    }

    inner class ParentCategoryViewHolder(
        private val binding: ItemCategoryMultiSelectParentBinding
    ) : CategoryViewHolder(binding) {

        fun bind(
            category: Category,
            isExpanded: Boolean,
            onToggle: () -> Unit,
            hasChildren: Boolean,
            isSelected: Boolean,
            onCategoryToggle: () -> Unit
        ) {
            binding.apply {
                // Set icon
                imageIcon.setImageResource(category.iconRes)

                // Set category name
                textViewCategoryName.text = category.title.standardize()

                // Show/hide chevron based on whether category has children
                if (hasChildren) {
                    iconChevron.visibility = android.view.View.VISIBLE
                    iconChevron.setChevronRotation(isExpanded)
                    iconChevron.setOnClickListener {
                        onToggle()
                    }
                } else {
                    iconChevron.visibility = android.view.View.INVISIBLE
                }

                // Hide nested RecyclerView (we're using flat list instead)
                recyclerViewChildCategories.visibility = ViewGroup.GONE

                // Setup checkbox
                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = isSelected
                checkbox.setOnCheckedChangeListener { _, _ ->
                    onCategoryToggle()
                }

                root.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                }
            }
        }

        override fun bind(category: Category, isSelected: Boolean, onCategoryToggle: () -> Unit) {
            // Not used for parent
        }
    }

    inner class ChildCategoryViewHolder(
        private val binding: ItemCategoryMultiSelectChildBinding
    ) : CategoryViewHolder(binding) {

        override fun bind(category: Category, isSelected: Boolean, onCategoryToggle: () -> Unit) {
            binding.apply {
                imageIcon.setImageResource(category.iconRes)
                textViewCategoryName.text = category.title.standardize()

                // Setup checkbox
                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = isSelected
                checkbox.setOnCheckedChangeListener { _, _ ->
                    onCategoryToggle()
                }

                root.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.category.id == newItem.category.id &&
                    oldItem.isParent == newItem.isParent
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }

    data class CategoryItem(
        val category: Category,
        val isParent: Boolean,
        val hasChildren: Boolean = false
    )

    companion object {
        private const val VIEW_TYPE_PARENT = 0
        private const val VIEW_TYPE_CHILD = 1
    }
}

