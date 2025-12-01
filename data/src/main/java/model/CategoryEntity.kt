package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import transaction.model.Category
import transaction.model.CategoryType

@Entity(
    tableName = "tb_category",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parent_id"])
    ]
)

// Category model for entities layer
internal data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "type")
    val type: CategoryType
)

internal fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        parentId = parentId,
        title = title,
        icon = icon,
        type = type
    )
}