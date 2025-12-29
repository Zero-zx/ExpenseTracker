package presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.statistics.databinding.ItemChartHeaderBinding
import com.github.mikephil.charting.charts.BarChart

/**
 * Adapter for Chart header in RecyclerView
 * Used with ConcatAdapter to combine chart and list items
 */
class ChartHeaderAdapter(
    private val barChart: BarChart
) : RecyclerView.Adapter<ChartHeaderAdapter.ChartHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartHeaderViewHolder {
        val binding = ItemChartHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChartHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChartHeaderViewHolder, position: Int) {
        holder.bind(barChart)
    }

    override fun getItemCount(): Int = 1

    class ChartHeaderViewHolder(
        private val binding: ItemChartHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chart: BarChart) {
            // Remove chart from old parent if any
            (chart.parent as? ViewGroup)?.removeView(chart)
            
            // Add chart to header layout
            binding.chartContainer.removeAllViews()
            binding.chartContainer.addView(chart)
        }
    }
}


