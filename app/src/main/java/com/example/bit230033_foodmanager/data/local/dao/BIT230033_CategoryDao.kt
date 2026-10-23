package com.example.bit230033_foodmanager.data.local.dao

import androidx.room.*
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BIT230033_CategoryDao {

    @Query("SELECT * FROM BIT230033_DishCategory ORDER BY BIT230033_Name ASC")
    fun getAllCategories(): Flow<List<BIT230033_DishCategory>>

    @Query("SELECT * FROM BIT230033_DishCategory WHERE BIT230033_Id = :id")
    suspend fun getCategoryById(id: Long): BIT230033_DishCategory?

    @Query("SELECT COUNT(*) FROM BIT230033_DishCategory WHERE BIT230033_Name = :name AND BIT230033_Id != :excludeId")
    suspend fun isCategoryNameExists(name: String, excludeId: Long = 0): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: BIT230033_DishCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<BIT230033_DishCategory>): List<Long>

    @Update
    suspend fun updateCategory(category: BIT230033_DishCategory)

    @Delete
    suspend fun deleteCategory(category: BIT230033_DishCategory)

    @Query("DELETE FROM BIT230033_DishCategory WHERE BIT230033_Id = :id")
    suspend fun deleteCategoryById(id: Long)
}
