package presentation.detail.model

import category.model.Category
import category.model.CategoryType

data class SelectableCategory(
    val id: Long = 0,
    val parentId: Long?,
    val title: String,
    val icon: String,
    val type: CategoryType,
    val iconRes: Int,
    val isSelected: Boolean = true
) {
    fun toCategory() = Category(
        id = id,
        parentId = parentId,
        title = title,
        icon = icon,
        type = type
    )
}

fun Category.toSelectable(isSelected: Boolean = false) = SelectableCategory(
    id = id,
    parentId = parentId,
    title = title,
    icon = icon,
    type = type,
    iconRes = iconRes,
    isSelected = isSelected
)