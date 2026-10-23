package com.example.bit230033_foodmanager.data.repository

import com.example.bit230033_foodmanager.data.local.dao.BIT230033_DishDao
import com.example.bit230033_foodmanager.data.local.dao.BIT230033_DishImageDao
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_Dish
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishImage
import com.example.bit230033_foodmanager.data.local.entity.DishWithDetails
import com.example.bit230033_foodmanager.sync.SyncManager
import com.example.bit230033_foodmanager.utils.Constants
import kotlinx.coroutines.flow.Flow

class DishRepository(
    private val dishDao: BIT230033_DishDao,
    private val imageDao: BIT230033_DishImageDao,
    private val syncManager: SyncManager? = null
) {

    val allDishesWithDetails: Flow<List<DishWithDetails>> =
        dishDao.getAllDishesWithDetails()

    fun getDishWithDetailsFlowById(id: Long): Flow<DishWithDetails?> =
        dishDao.getDishWithDetailsFlowById(id)

    suspend fun getDishWithDetailsById(id: Long): DishWithDetails? =
        dishDao.getDishWithDetailsById(id)

    /**
     * Requirement 3: Check if dish name is unique within the same category
     */
    suspend fun isDishNameExistsInSameCategory(
        catId: Long,
        name: String,
        excludeId: Long = 0
    ): Boolean {
        return dishDao.countDuplicateName(catId, name.trim(), excludeId) > 0
    }

    suspend fun insertDish(
        dish: BIT230033_Dish,
        images: List<String>,
        thumbnailIndex: Int = 0,
        isOnline: Boolean = true
    ): Result<Long> {
        if (isDishNameExistsInSameCategory(dish.dishCategoryId, dish.name)) {
            return Result.failure(IllegalArgumentException("Dish name already exists in this category."))
        }

        val syncStatus = if (isOnline) Constants.SYNC_STATUS_PENDING_INSERT else Constants.SYNC_STATUS_PENDING_INSERT
        val dishToSave = dish.copy(
            syncStatus = syncStatus,
            updatedAt = System.currentTimeMillis()
        )

        val newDishId = dishDao.insertDish(dishToSave)

        // Save images, ensuring at most 1 isThumbnail = true
        if (images.isNotEmpty()) {
            val imageEntities = images.mapIndexed { index, url ->
                BIT230033_DishImage(
                    dishId = newDishId,
                    imageUrl = url,
                    isThumbnail = (index == thumbnailIndex)
                )
            }
            imageDao.insertImages(imageEntities)
        }

        syncManager?.triggerAutoSync()
        return Result.success(newDishId)
    }

    suspend fun updateDish(
        dish: BIT230033_Dish,
        images: List<Pair<String, Boolean>>, // url to isThumbnail
        isOnline: Boolean = true
    ): Result<Unit> {
        if (isDishNameExistsInSameCategory(dish.dishCategoryId, dish.name, dish.id)) {
            return Result.failure(IllegalArgumentException("Dish name already exists in this category."))
        }

        val syncStatus = if (isOnline && dish.serverId != null) {
            Constants.SYNC_STATUS_PENDING_UPDATE
        } else {
            Constants.SYNC_STATUS_PENDING_INSERT
        }
        val dishToUpdate = dish.copy(
            syncStatus = syncStatus,
            updatedAt = System.currentTimeMillis()
        )

        dishDao.updateDish(dishToUpdate)

        // Replace images
        imageDao.deleteImagesForDish(dish.id)
        if (images.isNotEmpty()) {
            var hasThumbnail = false
            val imageEntities = images.map { (url, isThumb) ->
                val thumb = if (!hasThumbnail && isThumb) {
                    hasThumbnail = true
                    true
                } else false
                BIT230033_DishImage(
                    dishId = dish.id,
                    imageUrl = url,
                    isThumbnail = thumb
                )
            }
            // If no image marked as thumbnail, default first image to thumbnail
            val finalEntities = if (!hasThumbnail && imageEntities.isNotEmpty()) {
                imageEntities.mapIndexed { idx, img ->
                    if (idx == 0) img.copy(isThumbnail = true) else img
                }
            } else {
                imageEntities
            }
            imageDao.insertImages(finalEntities)
        }

        syncManager?.triggerAutoSync()
        return Result.success(Unit)
    }

    suspend fun deleteDish(dishId: Long) {
        val dish = dishDao.getDishById(dishId)
        if (dish != null) {
            if (dish.serverId != null) {
                // Soft delete to sync deletion to server
                dishDao.markDishAsDeleted(dishId)
            } else {
                dishDao.deleteDishById(dishId)
            }
            syncManager?.triggerAutoSync()
        }
    }

    suspend fun setExclusiveThumbnail(dishId: Long, imageId: Long) {
        imageDao.setExclusiveThumbnail(dishId, imageId)
    }

    suspend fun addImageToDish(dishId: Long, imageUrl: String, setAsThumbnail: Boolean = false) {
        if (setAsThumbnail) {
            imageDao.clearThumbnailForDish(dishId)
        }
        val currentImages = imageDao.getImagesForDish(dishId)
        val shouldBeThumbnail = setAsThumbnail || currentImages.none { it.isThumbnail }

        imageDao.insertImage(
            BIT230033_DishImage(
                dishId = dishId,
                imageUrl = imageUrl,
                isThumbnail = shouldBeThumbnail
            )
        )
    }
}
