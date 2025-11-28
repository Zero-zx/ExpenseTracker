package base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VBinding : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> VBinding,
    private val onClick: ((T) -> Unit)? = null
) : RecyclerView.Adapter<BaseAdapter<T, VBinding>.BaseViewHolder>() {
    private val items = mutableListOf<T>()

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

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<T>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}