package com.example.home.event.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.home.databinding.ItemEventBinding
import transaction.model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventListAdapter(
    private val onItemClick: (Event) -> Unit
) : ListAdapter<Event, EventListAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onItemClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(event: Event) {
            binding.apply {
                textViewEventName.text = event.eventName
                textViewStartDate.text = "Start: ${dateFormat.format(Date(event.startDate))}"
                textViewEndDate.text = if (event.endDate != null) {
                    "End: ${dateFormat.format(Date(event.endDate!!))}"
                } else {
                    "End: Ongoing"
                }
                textViewParticipants.text = "${event.numberOfParticipants} participants"
                textViewStatus.text = if (event.isActive) "Active" else "Inactive"

                root.setOnClickListener {
                    onItemClick(event)
                }
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}

