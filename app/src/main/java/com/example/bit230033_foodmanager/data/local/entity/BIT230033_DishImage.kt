package com.example.bit230033_foodmanager.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: DishImage
 * Table name: BIT230033_DishImage
 * Required prefix: BIT230033_ for all columns and table name
 */
@Entity(
    tableName = "BIT230033_DishImage",
    foreignKeys = [
        ForeignKey(
            entity = BIT230033_Dish::class,
            parentColumns = ["BIT230033_Id"],
            childColumns = ["BIT230033_DishId"],
            onDelete = ForeignKey.CASCADE // When dish is deleted, delete all its images
        )
    ],
    indices = [Index(value = ["BIT230033_DishId"])]
)
data class BIT230033_DishImage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BIT230033_ImageId")
    val imageId: Long = 0,

    @ColumnInfo(name = "BIT230033_DishId")
    val dishId: Long,

    @ColumnInfo(name = "BIT230033_ImageUrl")
    val imageUrl: String,

    @ColumnInfo(name = "BIT230033_IsThumbnail")
    val isThumbnail: Boolean = false
)
