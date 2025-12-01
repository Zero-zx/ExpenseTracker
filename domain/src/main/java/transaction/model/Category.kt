package transaction.model

import constants.CategoryIcon

data class Category(
    val id: Long = 0,
    val parentId: Long?,
    val title: String,
    val icon: String,
    val type: CategoryType
) {
    val iconRes: Int
        get() = CategoryIcon.fromName(icon).iconRes
}


