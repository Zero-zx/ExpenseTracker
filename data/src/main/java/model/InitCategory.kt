package model

import constants.CategoryIcon
import data.model.CategoryType

object InitCategory {
    internal val CATEGORY_LIST = listOf(
        CategoryEntity(
            id = 1,
            parentId = null,
            title = CategoryIcon.SALARY.iconName,
            icon = CategoryIcon.SALARY.iconRes,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 2,
            parentId = null,
            title = CategoryIcon.HOME.iconName,
            icon = CategoryIcon.HOME.iconRes,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 3,
            parentId = null,
            title = CategoryIcon.TRANSPORT.iconName,
            icon = CategoryIcon.TRANSPORT.iconRes,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 4,
            parentId = null,
            title = CategoryIcon.TRAVEL.iconName,
            icon = CategoryIcon.TRAVEL.iconRes,
            type = CategoryType.IN
        )
    )
}