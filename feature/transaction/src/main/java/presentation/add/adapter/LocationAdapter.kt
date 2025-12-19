package presentation.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemEventTransactionBinding
import transaction.model.Location

class LocationAdapter(
    private val onItemClick: (Location) -> Unit,
    private val onItemUpdate: (Location) -> Unit
) : ListAdapter<Location, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {
    private var selectedLocationId: Long? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemEventTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position), selectedLocationId == getItem(position).id)
    }

    inner class LocationViewHolder(
        private val binding: ItemEventTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location, isSelected: Boolean) {
            binding.apply {
                itemView.isSelected = isSelected
                textViewName.text = location.name

                itemView.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
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

    private class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }

    fun setSelectedLocation(location: Location?) {
        val newId = location?.id
        if (newId == selectedLocationId) return

        val previousId = selectedLocationId
        selectedLocationId = newId

        // Notify items that changed
        previousId?.let { id ->
            val position = currentList.indexOfFirst { it.id == id }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
        newId?.let { id ->
            val position = currentList.indexOfFirst { it.id == id }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }
}



