package base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Base ListAdapter with DiffUtil support
 * Replaces BaseAdapter to use DiffUtil instead of notifyDataSetChanged()
 * 
 * Usage:
 * ```
 * class MyAdapter(
 *     onClick: ((Item) -> Unit)? = null
 * ) : BaseListAdapter<Item, ItemBinding>(
 *     inflateMethod = ItemBinding::inflate,
 *     diffCallback = object : DiffUtil.ItemCallback<Item>() {
 *         override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
 *         override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
 *     },
 *     onClick = onClick
 * ) {
 *     override fun onBind(item: Item, binding: ItemBinding) {
 *         binding.textView.text = item.name
 *     }
 * }
 * ```
 */
abstract class BaseListAdapter<T, VBinding : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> VBinding,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val onClick: ((T) -> Unit)? = null
) : ListAdapter<T, BaseListAdapter<T, VBinding>.BaseViewHolder>(diffCallback) {

    inner class BaseViewHolder(val binding: VBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            onBind(item, binding)
            binding.root.setOnClickListener { onClick?.invoke(item) }
        }
    }

    abstract fun onBind(item: T, binding: VBinding)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(
            inflateMethod.invoke(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}