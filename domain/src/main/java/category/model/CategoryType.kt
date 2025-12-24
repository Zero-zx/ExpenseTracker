package category.model

import androidx.annotation.DrawableRes
import com.example.common.R

enum class CategoryType(val label: String, @param:DrawableRes val iconRes: Int) {
    EXPENSE("Expense", R.drawable.v2_ic_home_menu_expense),
    INCOME("Income", R.drawable.v2_ic_home_menu_income),
    LEND("Lend", R.drawable.ic_cho_vay),
    BORROWING("Borrowing", R.drawable.ic_vay),
    TRANSFER("Transfer", R.drawable.v2_ic_home_menu_transfer),
    ADJUSTMENT("Adjustment", R.drawable.v2_ic_home_menu_adjustment)
}



