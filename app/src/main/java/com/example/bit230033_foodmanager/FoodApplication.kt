package com.example.bit230033_foodmanager

import android.app.Application
import android.content.Context
import com.example.bit230033_foodmanager.data.local.database.BIT230033_FoodDatabase
import com.example.bit230033_foodmanager.data.repository.CategoryRepository
import com.example.bit230033_foodmanager.data.repository.DishRepository
import com.example.bit230033_foodmanager.sync.NetworkMonitor
import com.example.bit230033_foodmanager.sync.SyncManager
import com.example.bit230033_foodmanager.utils.LocaleHelper

class FoodApplication : Application() {

    val database by lazy { BIT230033_FoodDatabase.getDatabase(this) }
    val networkMonitor by lazy { NetworkMonitor(this) }
    val syncManager by lazy { SyncManager(this, networkMonitor) }

    val dishRepository by lazy {
        DishRepository(database.dishDao(), database.dishImageDao(), syncManager)
    }

    val categoryRepository by lazy {
        CategoryRepository(database.categoryDao(), database.dishDao())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        // Warm up database and sync service
        syncManager.triggerAutoSync()
    }
}
