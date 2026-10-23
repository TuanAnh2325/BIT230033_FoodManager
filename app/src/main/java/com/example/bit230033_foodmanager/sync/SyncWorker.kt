package com.example.bit230033_foodmanager.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bit230033_foodmanager.data.local.database.BIT230033_FoodDatabase
import com.example.bit230033_foodmanager.data.remote.RetrofitClient
import com.example.bit230033_foodmanager.data.remote.dto.DishDto
import com.example.bit230033_foodmanager.data.remote.dto.DishImageDto
import com.example.bit230033_foodmanager.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = BIT230033_FoodDatabase.getDatabase(applicationContext)
        val dishDao = database.dishDao()
        val imageDao = database.dishImageDao()
        val apiService = RetrofitClient.apiService

        try {
            // Push pending local changes to MockAPI
            val pendingDishes = dishDao.getPendingSyncDishes()
            for (dish in pendingDishes) {
                val images = imageDao.getImagesForDish(dish.id).map {
                    DishImageDto(imageUrl = it.imageUrl, isThumbnail = it.isThumbnail)
                }

                when (dish.syncStatus) {
                    Constants.SYNC_STATUS_PENDING_INSERT -> {
                        val dishDto = DishDto(
                            name = dish.name,
                            price = dish.price,
                            preparationTime = dish.preparationTime,
                            isAvailable = dish.isAvailable,
                            description = dish.description,
                            dishCategoryId = dish.dishCategoryId,
                            images = images
                        )
                        try {
                            val response = apiService.createDish(dishDto)
                            if (response.isSuccessful && response.body() != null) {
                                val created = response.body()!!
                                dishDao.updateDish(
                                    dish.copy(
                                        serverId = created.id,
                                        syncStatus = Constants.SYNC_STATUS_SYNCED
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("SyncWorker", "Error posting dish to MockAPI", e)
                        }
                    }

                    Constants.SYNC_STATUS_PENDING_UPDATE -> {
                        if (dish.serverId != null) {
                            val dishDto = DishDto(
                                id = dish.serverId,
                                name = dish.name,
                                price = dish.price,
                                preparationTime = dish.preparationTime,
                                isAvailable = dish.isAvailable,
                                description = dish.description,
                                dishCategoryId = dish.dishCategoryId,
                                images = images
                            )
                            try {
                                val response = apiService.updateDish(dish.serverId, dishDto)
                                if (response.isSuccessful) {
                                    dishDao.updateDish(
                                        dish.copy(syncStatus = Constants.SYNC_STATUS_SYNCED)
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("SyncWorker", "Error updating dish on MockAPI", e)
                            }
                        }
                    }

                    Constants.SYNC_STATUS_PENDING_DELETE -> {
                        if (dish.serverId != null) {
                            try {
                                apiService.deleteDish(dish.serverId)
                            } catch (e: Exception) {
                                Log.e("SyncWorker", "Error deleting dish on MockAPI", e)
                            }
                        }
                        dishDao.deleteDishById(dish.id)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            Result.retry()
        }
    }
}
