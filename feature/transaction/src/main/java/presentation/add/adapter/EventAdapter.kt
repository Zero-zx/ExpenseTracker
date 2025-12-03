package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemEventTransactionBinding
import transaction.model.Event

class EventAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onItemUpdate: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {
    private var selectedEventId: Long? = null
    private var selectedPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class EventViewHolder(
        private val binding: ItemEventTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, isSelected: Boolean) {
            binding.apply {
                val selected = (event.id == selectedEventId) || isSelected
                itemView.isSelected = selected
                textViewName.text = event.eventName

                itemView.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val previouslySelectedPosition = selectedPosition
                        selectedPosition = position
                        notifyItemChanged(previouslySelectedPosition)
                        notifyItemChanged(selectedPosition)
                        onItemClick(getItem(position))
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

    fun setSelectedEvent(event: Event?) {
        val newId = event?.id
        if (newId == selectedEventId) return
        val previousId = selectedEventId
        selectedEventId = newId

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