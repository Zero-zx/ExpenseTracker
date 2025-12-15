package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.statistics.databinding.ItemTripEventBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import presentation.detail.model.TripEventData

class TripEventAdapter(
    private val onItemClick: ((TripEventData) -> Unit)? = null
) : ListAdapter<TripEventData, TripEventAdapter.TripEventViewHolder>(
    TripEventDiffCallback()
) {

    companion object {
        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripEventViewHolder {
        val binding = ItemTripEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: TripEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TripEventViewHolder(
        private val binding: ItemTripEventBinding,
        private val onItemClick: ((TripEventData) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TripEventData) {
            binding.apply {
                val event = data.event
                
                // Set icon with first letter of event name
                textViewIcon.text = event.eventName.take(1).uppercase()
                
                // Set event name
                textViewName.text = event.eventName
                
                // Set date (start date)
                textViewDate.text = dateFormatter.format(event.startDate)
                
                // Set amount
                textViewAmount.text = currencyFormatter.format(data.totalAmount)
                
                root.setOnClickListener {
                    onItemClick?.invoke(data)
                }
            }
        }
    }

    private class TripEventDiffCallback : DiffUtil.ItemCallback<TripEventData>() {
        override fun areItemsTheSame(oldItem: TripEventData, newItem: TripEventData): Boolean {
            return oldItem.event.id == newItem.event.id
        }

        override fun areContentsTheSame(oldItem: TripEventData, newItem: TripEventData): Boolean {
            return oldItem == newItem
        }
    }
}

