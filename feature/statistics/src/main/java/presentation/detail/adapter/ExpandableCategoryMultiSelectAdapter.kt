package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import category.model.Category
import com.example.statistics.databinding.ItemCategoryMultiSelectChildBinding
import com.example.statistics.databinding.ItemCategoryMultiSelectParentBinding
import helpers.standardize
import ui.toggleChevronRotation

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
        // Only auto-expand if not already expanded
        val newParentIds = allCategories.filter { it.parentId == null }.map { it.id }
        expandedParentIds.addAll(newParentIds.filter { !expandedParentIds.contains(it) })
        val categoryItems = buildCategoryItems(categories)
        // Always submit a new list to ensure rebinding
        super.submitList(categoryItems.toList())
    }

    fun filter(query: String) {
        searchQuery = query.lowercase().trim()
        val categoryItems = buildCategoryItems(allCategories)
        super.submitList(categoryItems)
    }

    /**
     * Update checkbox state for specific category IDs without rebinding entire item
     * This is the most efficient way - only updates checkbox, not the whole ViewHolder
     */
    fun updateCheckboxState(recyclerView: RecyclerView, categoryIds: List<Long>) {
        if (categoryIds.isEmpty()) return
        
        categoryIds.forEach { categoryId ->
            // Find position of this category
            for (i in 0 until itemCount) {
                val item = getItem(i)
                if (item.category.id == categoryId) {
                    // Find ViewHolder for this position
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
                    if (viewHolder is CategoryViewHolder) {
                        val isSelected = selectedCategoryIds().contains(categoryId)
                        viewHolder.updateCheckboxState(isSelected)
                    }
                    break
                }
            }
        }
    }
    
    /**
     * Force refresh all items (use only when necessary, e.g., select all)
     */
    fun refresh() {
        notifyItemRangeChanged(0, itemCount)
    }

    private fun buildCategoryItems(categories: List<Category>): List<CategoryItem> {
        val filteredCategories = if (searchQuery.isBlank()) {
            categories
        } else {
            categories.filter { category ->
                category.title.lowercase().contains(searchQuery)
            }
        }

        val parentCategories = filteredCategories.filter { it.parentId == null }
        val result = mutableListOf<CategoryItem>()

        parentCategories.forEach { parent ->
            val childCategories = filteredCategories.filter { it.parentId == parent.id }

            // Only show parent if it matches or has matching children when searching
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

                // Only expand if user has explicitly expanded this parent
                if (expandedParentIds.contains(parent.id)) {
                    childCategories.forEach { child ->
                        result.add(CategoryItem(child, isParent = false, hasChildren = false))
                    }
                }
            }
        }

        val filterChildCategories =
            filteredCategories.filter { it.parentId != null && parentCategories.none { parent -> parent.id == it.parentId } }

        filterChildCategories.forEach {
            result.add(CategoryItem(it, isParent = false, hasChildren = false))
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
        abstract fun updateCheckboxState(isSelected: Boolean)
    }

    inner class ParentCategoryViewHolder(
        private val binding: ItemCategoryMultiSelectParentBinding
    ) : CategoryViewHolder(binding) {
        
        private var onCategoryToggleCallback: (() -> Unit)? = null

        fun bind(
            category: Category,
            isExpanded: Boolean,
            onToggle: () -> Unit,
            hasChildren: Boolean,
            isSelected: Boolean,
            onCategoryToggle: () -> Unit
        ) {
            onCategoryToggleCallback = onCategoryToggle
            binding.apply {
                // Set icon
                imageIcon.setImageResource(category.iconRes)

                // Set category name
                textViewCategoryName.text = category.title.standardize()

                // Set initial chevron rotation based on expanded state
                iconChevron.rotation = if (isExpanded) 90f else 0f

                iconChevron.setOnClickListener {
                    onToggle()
                    iconChevron.toggleChevronRotation()
                }

                // Show/hide chevron based on whether category has children
                if (hasChildren) {
                    iconChevron.visibility = android.view.View.VISIBLE
                } else {
                    iconChevron.visibility = android.view.View.INVISIBLE
                }

                // Hide nested RecyclerView (we're using flat list instead)
                recyclerViewChildCategories.visibility = ViewGroup.GONE

                // Setup checkbox - detach listener before setting checked programmatically
                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = isSelected
                
                // Attach listener to toggle selection
                checkbox.setOnCheckedChangeListener { _, _ ->
                    onCategoryToggle()
                }
            }
        }

        override fun bind(category: Category, isSelected: Boolean, onCategoryToggle: () -> Unit) {
            // Not used for parent
        }
        
        override fun updateCheckboxState(isSelected: Boolean) {
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = isSelected
            // Re-attach listener if available
            onCategoryToggleCallback?.let { callback ->
                binding.checkbox.setOnCheckedChangeListener { _, _ ->
                    callback()
                }
            }
        }
    }

    inner class ChildCategoryViewHolder(
        private val binding: ItemCategoryMultiSelectChildBinding
    ) : CategoryViewHolder(binding) {
        
        private var onCategoryToggleCallback: (() -> Unit)? = null

        override fun bind(category: Category, isSelected: Boolean, onCategoryToggle: () -> Unit) {
            onCategoryToggleCallback = onCategoryToggle
            binding.apply {
                imageIcon.setImageResource(category.iconRes)
                textViewCategoryName.text = category.title.standardize()

                // Setup checkbox - detach listener before setting checked programmatically
                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = isSelected
                
                // Attach listener to toggle selection
                checkbox.setOnCheckedChangeListener { _, _ ->
                    onCategoryToggle()
                }
            }
        }
        
        override fun updateCheckboxState(isSelected: Boolean) {
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = isSelected
            // Re-attach listener if available
            onCategoryToggleCallback?.let { callback ->
                binding.checkbox.setOnCheckedChangeListener { _, _ ->
                    callback()
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
