package model

import constants.CategoryIcon
import transaction.model.CategoryType

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

    internal val CATEGORY_LIST = CategoryIcon.entries.toList()
        .mapIndexed { index, icon ->
            CategoryEntity(
                id = index.toLong() + 1,
                parentId = icon.parentId,
                title = icon.title,
                icon = icon.iconName,
                type = if (icon.iconName in INCOME_CATEGORIES) {
                    CategoryType.INCOME
                } else {
                    CategoryType.EXPENSE
                }
            )
        }
}