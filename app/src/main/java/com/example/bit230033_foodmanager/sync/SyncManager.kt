package com.example.bit230033_foodmanager.sync

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SyncManager(
    private val context: Context,
    private val networkMonitor: NetworkMonitor
) {
    private val workManager = WorkManager.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        // Automatically monitor connection and trigger sync when connection becomes available
        scope.launch {
            networkMonitor.isConnected.collectLatest { isConnected ->
                if (isConnected) {
                    triggerAutoSync()
                }
            }
        }
    }

    fun triggerAutoSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "BIT230033_AutoSyncWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
