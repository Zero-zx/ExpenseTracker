package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemEventTransactionBinding
import transaction.model.Event

class PayeeAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onItemUpdate: (Event) -> Unit
) : ListAdapter<Event, PayeeAdapter.EventViewHolder>(EventDiffCallback()) {
    private val selectedEventIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), selectedEventIds.contains(getItem(position).id))
    }

    inner class EventViewHolder(
        private val binding: ItemEventTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, isSelected: Boolean) {
            binding.apply {
                itemView.isSelected = isSelected
                textViewName.text = event.eventName

                itemView.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick(getItem(position))
                        // Selection state will be updated via setSelectedEvents
                    }
                }

                imageViewEdit.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemUpdate(getItem(position))
                    }
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

    fun setSelectedEvents(events: List<Event>) {
        val newIds = events.map { it.id }.toSet()
        if (newIds == selectedEventIds) return

        // Find positions that changed and notify them
        val previousIds = selectedEventIds.toSet()
        selectedEventIds.clear()
        selectedEventIds.addAll(newIds)

        // Notify items that were selected or deselected
        val changedIds = (previousIds + newIds) - (previousIds.intersect(newIds))
        changedIds.forEach { changedId ->
            val position = currentList.indexOfFirst { it.id == changedId }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }
}