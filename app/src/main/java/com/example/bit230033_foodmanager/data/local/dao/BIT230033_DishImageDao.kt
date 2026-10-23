package com.example.bit230033_foodmanager.data.local.dao

import androidx.room.*
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishImage
import kotlinx.coroutines.flow.Flow

@Dao
interface BIT230033_DishImageDao {

    @Query("SELECT * FROM BIT230033_DishImage WHERE BIT230033_DishId = :dishId ORDER BY BIT230033_IsThumbnail DESC, BIT230033_ImageId ASC")
    fun getImagesForDishFlow(dishId: Long): Flow<List<BIT230033_DishImage>>

    @Query("SELECT * FROM BIT230033_DishImage WHERE BIT230033_DishId = :dishId ORDER BY BIT230033_IsThumbnail DESC, BIT230033_ImageId ASC")
    suspend fun getImagesForDish(dishId: Long): List<BIT230033_DishImage>

    @Query("SELECT * FROM BIT230033_DishImage WHERE BIT230033_DishId = :dishId AND BIT230033_IsThumbnail = 1 LIMIT 1")
    suspend fun getThumbnailForDish(dishId: Long): BIT230033_DishImage?

    @Query("UPDATE BIT230033_DishImage SET BIT230033_IsThumbnail = 0 WHERE BIT230033_DishId = :dishId")
    suspend fun clearThumbnailForDish(dishId: Long)

    @Query("UPDATE BIT230033_DishImage SET BIT230033_IsThumbnail = 1 WHERE BIT230033_ImageId = :imageId")
    suspend fun markImageAsThumbnail(imageId: Long)

    /**
     * Requirement 4.3: Ensure each dish has AT MOST 01 image with isThumbnail = true
     */
    @Transaction
    suspend fun setExclusiveThumbnail(dishId: Long, imageId: Long) {
        clearThumbnailForDish(dishId)
        markImageAsThumbnail(imageId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: BIT230033_DishImage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<BIT230033_DishImage>): List<Long>

    @Delete
    suspend fun deleteImage(image: BIT230033_DishImage)

    @Query("DELETE FROM BIT230033_DishImage WHERE BIT230033_ImageId = :imageId")
    suspend fun deleteImageById(imageId: Long)

    @Query("DELETE FROM BIT230033_DishImage WHERE BIT230033_DishId = :dishId")
    suspend fun deleteImagesForDish(dishId: Long)
}
