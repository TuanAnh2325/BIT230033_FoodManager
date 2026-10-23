package com.example.bit230033_foodmanager.utils

object Constants {
    const val MSSV = "BIT230033"
    const val DATABASE_NAME = "BIT230033_food_database.db"

    // Sync Status constants
    const val SYNC_STATUS_SYNCED = 0
    const val SYNC_STATUS_PENDING_INSERT = 1
    const val SYNC_STATUS_PENDING_UPDATE = 2
    const val SYNC_STATUS_PENDING_DELETE = 3

    // MockAPI Base URL (Can be replaced with user's endpoint, or local mock)
    const val MOCKAPI_BASE_URL = "https://660000000000000000000000.mockapi.io/api/v1/"

    // Intent extras
    const val EXTRA_DISH_ID = "extra_dish_id"
    const val EXTRA_CATEGORY_ID = "extra_category_id"

    // Preparation time threshold
    const val QUICK_PREPARATION_TIME_THRESHOLD = 15 // minutes

    // Shared Preferences
    const val PREFS_NAME = "bit230033_food_manager_prefs"
    const val KEY_LANGUAGE = "key_app_language"
    const val LANG_ENGLISH = "en"
    const val LANG_VIETNAMESE = "vi"
}
