package model

import constants.CategoryIcon
import transaction.model.CategoryType

object InitCategory {
    internal val CATEGORY_LIST = CategoryIcon.entries.toList()
        .mapIndexed { index, icon ->
            CategoryEntity(
                id = index.toLong() + 1,
                parentId = icon.parentId,
                title = icon.title,
                icon = icon.iconName,
                type = CategoryType.EXPENSE
            )
        }
}