package com.example.bit230033_foodmanager.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Composite relation POJO for querying a dish along with its Category and all Images
 */
data class DishWithDetails(
    @Embedded
    val dish: BIT230033_Dish,

    @Relation(
        parentColumn = "BIT230033_DishCategoryId",
        entityColumn = "BIT230033_Id"
    )
    val category: BIT230033_DishCategory?,

    @Relation(
        parentColumn = "BIT230033_Id",
        entityColumn = "BIT230033_DishId"
    )
    val images: List<BIT230033_DishImage> = emptyList()
) {
    val thumbnailImage: BIT230033_DishImage?
        get() = images.firstOrNull { it.isThumbnail } ?: images.firstOrNull()

    val thumbnailUrl: String?
        get() = thumbnailImage?.imageUrl
}
