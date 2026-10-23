package com.example.bit230033_foodmanager.data.local.dao

import androidx.room.*
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_Dish
import com.example.bit230033_foodmanager.data.local.entity.DishWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface BIT230033_DishDao {

    @Transaction
    @Query("SELECT * FROM BIT230033_Dish WHERE BIT230033_SyncStatus != 3 ORDER BY BIT230033_Id DESC")
    fun getAllDishesWithDetails(): Flow<List<DishWithDetails>>

    @Transaction
    @Query("SELECT * FROM BIT230033_Dish WHERE BIT230033_Id = :id AND BIT230033_SyncStatus != 3")
    fun getDishWithDetailsFlowById(id: Long): Flow<DishWithDetails?>

    @Transaction
    @Query("SELECT * FROM BIT230033_Dish WHERE BIT230033_Id = :id AND BIT230033_SyncStatus != 3")
    suspend fun getDishWithDetailsById(id: Long): DishWithDetails?

    @Query("SELECT * FROM BIT230033_Dish WHERE BIT230033_Id = :id")
    suspend fun getDishById(id: Long): BIT230033_Dish?

    // Requirement 4.4: Check if category is used by dishes
    @Query("SELECT COUNT(*) FROM BIT230033_Dish WHERE BIT230033_DishCategoryId = :catId AND BIT230033_SyncStatus != 3")
    suspend fun countDishesByCategory(catId: Long): Int

    // Requirement 3: Dish name unique in same category
    @Query("SELECT COUNT(*) FROM BIT230033_Dish WHERE BIT230033_DishCategoryId = :catId AND BIT230033_Name = :name AND BIT230033_Id != :excludeId AND BIT230033_SyncStatus != 3")
    suspend fun countDuplicateName(catId: Long, name: String, excludeId: Long = 0): Int

    // Sync helpers
    @Query("SELECT * FROM BIT230033_Dish WHERE BIT230033_SyncStatus != 0")
    suspend fun getPendingSyncDishes(): List<BIT230033_Dish>

    @Query("SELECT * FROM BIT230033_Dish WHERE BIT230033_ServerId = :serverId LIMIT 1")
    suspend fun getDishByServerId(serverId: String): BIT230033_Dish?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDish(dish: BIT230033_Dish): Long

    @Update
    suspend fun updateDish(dish: BIT230033_Dish)

    @Delete
    suspend fun deleteDish(dish: BIT230033_Dish)

    // Soft delete for offline sync (SyncStatus = 3)
    @Query("UPDATE BIT230033_Dish SET BIT230033_SyncStatus = 3, BIT230033_UpdatedAt = :timestamp WHERE BIT230033_Id = :id")
    suspend fun markDishAsDeleted(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM BIT230033_Dish WHERE BIT230033_Id = :id")
    suspend fun deleteDishById(id: Long)
}
