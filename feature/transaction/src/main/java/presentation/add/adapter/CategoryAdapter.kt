package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemCategoryBinding
import transaction.model.Category
import helpers.standardize


class CategoryAdapter(
    private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {
    // Track selected category by id so selection can be updated from outside (e.g. shared ViewModel)
    private var selectedCategoryId: Long? = null
    private var selectedPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previouslySelectedPosition = selectedPosition
                    // update selectedCategoryId and selectedPosition
                    selectedCategoryId = getItem(position).id
                    selectedPosition = position
                    if (previouslySelectedPosition != RecyclerView.NO_POSITION) notifyItemChanged(
                        previouslySelectedPosition
                    )
                    notifyItemChanged(selectedPosition)
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(category: Category, isSelected: Boolean) {
            binding.apply {
                // If selection was updated from outside, compute selection by id
                val selected = (category.id == selectedCategoryId) || isSelected
                itemView.isSelected = selected
                textCategory.text = category.title.standardize()
                imageIcon.setImageResource(category.iconRes)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Update adapter selection from external sources (e.g. shared ViewModel). Pass null to clear selection.
     * Returns the adapter position of the new selection or -1 if not in current list.
     */
    fun setSelectedCategory(category: Category?) {
        val newId = category?.id
        if (newId == selectedCategoryId) return
        val previousId = selectedCategoryId
        selectedCategoryId = newId

        // find positions and notify changes so UI updates
        val prevPos =
            if (previousId == null) -1 else currentList.indexOfFirst { it.id == previousId }
        val newPos = if (newId == null) -1 else currentList.indexOfFirst { it.id == newId }
        if (prevPos != -1) notifyItemChanged(prevPos)
        if (newPos != -1) notifyItemChanged(newPos)
        // update selectedPosition for internal click logic
        selectedPosition = newPos
    }
}