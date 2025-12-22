package com.example.other.other.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.other.databinding.ItemSettingBinding
import com.example.other.other.model.SettingItem
import ui.gone
import ui.visible

class SettingAdapter(
    private val onItemClick: (SettingItem) -> Unit
) : ListAdapter<SettingItem, SettingAdapter.SettingViewHolder>(SettingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val binding = ItemSettingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SettingViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SettingViewHolder(
        private val binding: ItemSettingBinding,
        private val onItemClick: (SettingItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingItem) {
            binding.apply {
                iconSetting.setImageResource(item.iconRes)
                textSettingTitle.text = item.title

                if (item.subtitle != null) {
                    textSettingSubtitle.visible()
                    textSettingSubtitle.text = item.subtitle
                } else {
                    textSettingSubtitle.gone()
                }

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    private class SettingDiffCallback : DiffUtil.ItemCallback<SettingItem>() {
        override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem == newItem
        }
    }
}

