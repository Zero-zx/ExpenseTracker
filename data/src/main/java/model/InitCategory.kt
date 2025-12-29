package model

import category.model.CategoryType
import constants.CategoryIcon

object InitCategory {
    // Income category icon names
    private val INCOME_CATEGORIES = setOf(
        "thu_luong",        // SALARY
        "thu_thuong",       // BONUS
        "thu_tien_lai",     // INTEREST
        "thu_khac",         // OTHER
        "thu_lai_tiet_kiem", // SAVING_INTEREST
        "thu_tien_vao"      // INCOME
    )

    private const val LENT_CATEGORY = "thu_cho_vay"

    private const val BORROW_CATEGORY = "thu_di_vay"

    private const val COLLECT_DEBT_CATEGORY = "thu_thu_no"
    private const val REPAYMENT_CATEGORY = "thu_tra_no"


    internal val CATEGORY_LIST = CategoryIcon.entries.toList()
        .mapIndexed { index, icon ->
            CategoryEntity(
                id = index.toLong() + 1,
                parentId = icon.parentId,
                title = icon.title,
                icon = icon.iconName,
                type = if (icon.iconName in INCOME_CATEGORIES) {
                    CategoryType.INCOME
                } else if (icon.iconName == LENT_CATEGORY) {
                    CategoryType.LEND
                } else if (icon.iconName == BORROW_CATEGORY) {
                    CategoryType.BORROWING
                } else if (icon.iconName == COLLECT_DEBT_CATEGORY) {
                    CategoryType.COLLECT_DEBT
                } else if (icon.iconName == REPAYMENT_CATEGORY) {
                    CategoryType.REPAYMENT
                } else {
                    CategoryType.EXPENSE
                }
            )
        }
}