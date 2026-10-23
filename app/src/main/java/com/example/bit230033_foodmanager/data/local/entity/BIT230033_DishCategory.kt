package com.example.bit230033_foodmanager.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: DishCategory
 * Table name: BIT230033_DishCategory
 * Required prefix: BIT230033_ for all columns and table name
 */
@Entity(
    tableName = "BIT230033_DishCategory",
    indices = [Index(value = ["BIT230033_Name"], unique = true)]
)
data class BIT230033_DishCategory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BIT230033_Id")
    val id: Long = 0,

    @ColumnInfo(name = "BIT230033_Name")
    val name: String,

    @ColumnInfo(name = "BIT230033_Description")
    val description: String? = null
)
