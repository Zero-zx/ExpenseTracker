package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.statistics.databinding.ItemCategoryMultiSelectChildBinding
import com.example.statistics.databinding.ItemCategoryMultiSelectParentBinding
import helpers.standardize
import presentation.detail.model.SelectableCategory
import ui.animateChevronRotation

class ExpandableCategoryMultiSelectAdapter(
    private val onCategoryClick: (SelectableCategory) -> Unit
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

    private var allCategories: List<SelectableCategory> = emptyList()

    fun submitCategories(categories: List<SelectableCategory>) {
        allCategories = categories
        expandedParentIds.addAll(allCategories.filter { it.parentId == null }.map { it.id })
        val categoryItems = buildCategoryItems(categories)
        super.submitList(categoryItems)
    }


    private fun buildCategoryItems(categories: List<SelectableCategory>): List<CategoryItem> {

        val parentCategories = categories.filter { it.parentId == null }
        val result = mutableListOf<CategoryItem>()

        parentCategories.forEach { parent ->
            val childCategories = categories.filter { it.parentId == parent.id }

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

        val filterChildCategories =
            categories.filter { it.parentId != null && parentCategories.none { parent -> parent.id == it.parentId } }

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
        abstract fun bind(category: SelectableCategory)
    }

    inner class ParentCategoryViewHolder(
        private val binding: ItemCategoryMultiSelectParentBinding
    ) : CategoryViewHolder(binding) {

        private var _onToggle: (() -> Unit)? = null
        private var _isExpanded = true

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        onCategoryClick(item.category)
                    }
                }
                iconChevron.setOnClickListener {
                    _onToggle?.invoke()
                    iconChevron.animateChevronRotation(
                        isExpanded = _isExpanded,
                        origin = -90f,
                        angle = 90f
                    )
                    _isExpanded = !_isExpanded
                }
            }
        }

        fun bind(
            category: SelectableCategory,
            isExpanded: Boolean,
            onToggle: () -> Unit,
            hasChildren: Boolean
        ) {
            _onToggle = onToggle
            _isExpanded = isExpanded
            binding.apply {
                imageIcon.setImageResource(category.iconRes)
                textViewCategoryName.text = category.title.standardize()

                if (hasChildren) {
                    iconChevron.visibility = View.VISIBLE
                } else {
                    iconChevron.visibility = View.INVISIBLE
                }

                checkbox.isChecked = category.isSelected

                root.isSelected = category.isSelected

                recyclerViewChildCategories.visibility = ViewGroup.GONE
            }
        }

        override fun bind(category: SelectableCategory) {
            // Not used for parent
        }
    }

    inner class ChildCategoryViewHolder(
        private val binding: ItemCategoryMultiSelectChildBinding
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

        override fun bind(category: SelectableCategory) {
            binding.apply {
                imageIcon.setImageResource(category.iconRes)
                textViewCategoryName.text = category.title.standardize()
                checkbox.isChecked = category.isSelected
                root.isSelected = category.isSelected
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
        val category: SelectableCategory,
        val isParent: Boolean,
        val hasChildren: Boolean = false
    )

    companion object {
        private const val VIEW_TYPE_PARENT = 0
        private const val VIEW_TYPE_CHILD = 1
    }
}

