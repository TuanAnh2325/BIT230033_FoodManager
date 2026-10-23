package com.example.bit230033_foodmanager.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bit230033_foodmanager.utils.Constants

/**
 * Entity: Dish
 * Table name: BIT230033_Dish
 * Required prefix: BIT230033_ for all columns and table name
 */
@Entity(
    tableName = "BIT230033_Dish",
    foreignKeys = [
        ForeignKey(
            entity = BIT230033_DishCategory::class,
            parentColumns = ["BIT230033_Id"],
            childColumns = ["BIT230033_DishCategoryId"],
            onDelete = ForeignKey.RESTRICT // Do not allow deleting category in use
        )
    ],
    indices = [
        Index(value = ["BIT230033_DishCategoryId", "BIT230033_Name"], unique = true),
        Index(value = ["BIT230033_DishCategoryId"])
    ]
)
data class BIT230033_Dish(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BIT230033_Id")
    val id: Long = 0,

    @ColumnInfo(name = "BIT230033_Name")
    val name: String,

    @ColumnInfo(name = "BIT230033_Price")
    val price: Double,

    @ColumnInfo(name = "BIT230033_PreparationTime")
    val preparationTime: Int, // minutes

    @ColumnInfo(name = "BIT230033_IsAvailable")
    val isAvailable: Boolean = true,

    @ColumnInfo(name = "BIT230033_Description")
    val description: String? = null,

    @ColumnInfo(name = "BIT230033_DishCategoryId")
    val dishCategoryId: Long,

    // Offline & Sync Metadata
    @ColumnInfo(name = "BIT230033_SyncStatus")
    val syncStatus: Int = Constants.SYNC_STATUS_SYNCED,

    @ColumnInfo(name = "BIT230033_ServerId")
    val serverId: String? = null,

    @ColumnInfo(name = "BIT230033_UpdatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {

    val isQuick: Boolean
        get() = preparationTime <= Constants.QUICK_PREPARATION_TIME_THRESHOLD
}
