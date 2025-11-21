package data.model

data class Category(
    val id: Long,
    val parentId: Long?,
    val title: String,
    val icon: String,
    val type: CategoryType
)