package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemCategoryMostUsingBinding
import helpers.standardize
import category.model.Category

class MostUsingCategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, MostUsingCategoryAdapter.MostUsingCategoryViewHolder>(
    CategoryDiffCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MostUsingCategoryViewHolder {
        val binding = ItemCategoryMostUsingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MostUsingCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MostUsingCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MostUsingCategoryViewHolder(
        private val binding: ItemCategoryMostUsingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(getItem(position))
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
                imageIcon.setImageResource(category.iconRes)
                textViewCategoryName.text = category.title.standardize()
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
}



