package model

import constants.CategoryIcon
import data.model.CategoryType

object InitCategory {
    internal val CATEGORY_LIST = listOf(
        CategoryEntity(
            id = 1,
            parentId = null,
            title = CategoryIcon.SALARY.iconName,
            icon = CategoryIcon.SALARY.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 2,
            parentId = null,
            title = CategoryIcon.HOME.iconName,
            icon = CategoryIcon.HOME.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 3,
            parentId = null,
            title = CategoryIcon.TRANSPORT.iconName,
            icon = CategoryIcon.TRANSPORT.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 4,
            parentId = null,
            title = CategoryIcon.TRAVEL.iconName,
            icon = CategoryIcon.TRAVEL.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 5,
            parentId = null,
            title = CategoryIcon.STUDY.iconName,
            icon = CategoryIcon.STUDY.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 6,
            parentId = null,
            title = CategoryIcon.SPORT.iconName,
            icon = CategoryIcon.SPORT.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 7,
            parentId = 3,
            title = CategoryIcon.BUS.iconName,
            icon = CategoryIcon.BUS.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 8,
            parentId = 2,
            title = CategoryIcon.CLOTHES.iconName,
            icon = CategoryIcon.CLOTHES.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 9,
            parentId = 2,
            title = CategoryIcon.BABY.iconName,
            icon = CategoryIcon.BABY.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 10,
            parentId = 2,
            title = CategoryIcon.FOOD.iconName,
            icon = CategoryIcon.FOOD.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 11,
            parentId = null,
            title = CategoryIcon.MOVIE.iconName,
            icon = CategoryIcon.MOVIE.iconName,
            type = CategoryType.IN
        ),
        CategoryEntity(
            id = 12,
            parentId = null,
            title = CategoryIcon.ATM.iconName,
            icon = CategoryIcon.ATM.iconName,
            type = CategoryType.IN
        )
    )
}