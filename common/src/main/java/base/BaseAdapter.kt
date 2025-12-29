package base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VBinding : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> VBinding,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val onClick: ((T) -> Unit)? = null
) : ListAdapter<T, BaseAdapter<T, VBinding>.BaseViewHolder>(diffCallback) {

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