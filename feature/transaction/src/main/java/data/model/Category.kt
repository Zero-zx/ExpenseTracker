package data.model

data class Category(
    val id: Long = 0,
    val parentId: Long?,
    val title: String,
    val icon: Int,
    val type: CategoryType
)