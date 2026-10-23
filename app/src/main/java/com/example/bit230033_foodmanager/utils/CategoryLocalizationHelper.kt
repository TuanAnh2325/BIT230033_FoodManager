package com.example.bit230033_foodmanager.utils

import android.content.Context
import com.example.bit230033_foodmanager.R

object CategoryLocalizationHelper {

    fun getLocalizedCategoryName(context: Context, categoryName: String): String {
        return when (categoryName.trim().lowercase()) {
            "appetizers", "khai vị", "món khai vị" -> context.getString(R.string.cat_appetizers)
            "main courses", "món chính" -> context.getString(R.string.cat_main_courses)
            "desserts & drinks", "desserts and drinks", "tráng miệng & đồ uống", "tráng miệng và đồ uống", "tráng miệng" -> context.getString(R.string.cat_desserts_drinks)
            else -> categoryName
        }
    }

    fun getLocalizedCategoryDescription(context: Context, categoryName: String, originalDesc: String?): String? {
        return when (categoryName.trim().lowercase()) {
            "appetizers", "khai vị", "món khai vị" -> context.getString(R.string.cat_desc_appetizers)
            "main courses", "món chính" -> context.getString(R.string.cat_desc_main_courses)
            "desserts & drinks", "desserts and drinks", "tráng miệng & đồ uống", "tráng miệng và đồ uống", "tráng miệng" -> context.getString(R.string.cat_desc_desserts_drinks)
            else -> originalDesc
        }
    }
}
