package presentation.add.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.transaction.R
import transaction.model.CategoryType

class CategoryDropdownAdapter(
    context: Context,
    private val items: List<CategoryType>
) : ArrayAdapter<CategoryType>(context, 0, items) {

    // Track the currently selected index
    var selectedPosition: Int = 0
        set(value) {
            val oldPosition = field
            if (oldPosition != value) {
                field = value
                // ArrayAdapter doesn't have notifyItemChanged(), so we use notifyDataSetChanged()
                // but only when position actually changes and is valid
                if (value >= 0 && value < items.size) {
                    notifyDataSetChanged()
                }
            }
        }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): CategoryType = items[position]

    // View that appears in the TextInput after selection
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent, showTick = false)
    }

    // Views that appear in the dropdown list
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent, showTick = true)
    }

    private fun createItemView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        showTick: Boolean
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.menu_item_category_type, parent, false)

        val textView = view.findViewById<TextView>(R.id.text1)
        val iconStart = view.findViewById<ImageView>(R.id.icon_start)
        val iconTick = view.findViewById<ImageView>(R.id.icon_tick)

        val item = items[position]

        textView.text = item.label
        iconStart.setImageResource(item.iconRes)

        if (showTick && position == selectedPosition) {
            iconTick.visibility = View.VISIBLE
        } else {
            iconTick.visibility = View.GONE
        }

        return view
    }
}