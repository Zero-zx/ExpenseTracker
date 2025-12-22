package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemCategoryChildBinding
import com.example.transaction.databinding.ItemCategoryParentBinding
import helpers.standardize
import transaction.model.Category
import ui.toggleChevronRotation

class ExpandableCategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<ExpandableCategoryAdapter.CategoryItem, ExpandableCategoryAdapter.CategoryViewHolder>(
    CategoryDiffCallback()
) {

    private val expandedParentIds = mutableSetOf<Long>()
    private var searchQuery: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return when (viewType) {
            VIEW_TYPE_PARENT -> {
                val binding = ItemCategoryParentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ParentCategoryViewHolder(binding)
            }

            VIEW_TYPE_CHILD -> {
                val binding = ItemCategoryChildBinding.inflate(
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
        when (holder) {
            is ParentCategoryViewHolder -> {
                holder.bind(
                    category = item.category,
                    isExpanded = expandedParentIds.contains(item.category.id),
                    onToggle = { toggleExpansion(item.category.id) },
                    hasChildren = item.hasChildren
                )
            }

            is ChildCategoryViewHolder -> {
                holder.bind(item.category)
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
                category.title.standardize().lowercase().contains(searchQuery)
            }
        }

        val parentCategories = filteredCategories.filter { it.parentId == null }
        val result = mutableListOf<CategoryItem>()

        parentCategories.forEach { parent ->
            val childCategories = filteredCategories.filter { it.parentId == parent.id }

            // Only show parent if it matches or has matching children
            val shouldShowParent = searchQuery.isBlank() ||
                    parent.title.standardize().lowercase().contains(searchQuery) ||
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
            is ItemCategoryParentBinding -> binding.root
            is ItemCategoryChildBinding -> binding.root
            else -> throw IllegalArgumentException("Unknown binding type")
        }
    ) {
        abstract fun bind(category: Category)
    }

    inner class ParentCategoryViewHolder(
        private val binding: ItemCategoryParentBinding
    ) : CategoryViewHolder(binding) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onCategoryClick(item.category)
                }
            }
        }

        fun bind(
            category: Category,
            isExpanded: Boolean,
            onToggle: () -> Unit,
            hasChildren: Boolean
        ) {
            binding.apply {
                // Set icon
                imageIcon.setImageResource(category.iconRes)

                // Set category name
                textViewCategoryName.text = category.title.standardize()

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

            }
        }

        override fun bind(category: Category) {
            // Not used for parent
        }
    }

    inner class ChildCategoryViewHolder(
        private val binding: ItemCategoryChildBinding
    ) : CategoryViewHolder(binding) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onCategoryClick(item.category)
                }
            }
        }

        override fun bind(category: Category) {
            binding.apply {
                imageIcon.setImageResource(category.iconRes)
                textViewCategoryName.text = category.title.standardize()

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

