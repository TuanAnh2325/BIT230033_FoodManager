package com.example.bit230033_foodmanager.ui.list

import android.content.Context
import com.example.bit230033_foodmanager.R
import com.example.bit230033_foodmanager.utils.CategoryLocalizationHelper

enum class StatusFilter {
    ALL,
    AVAILABLE_ONLY,
    UNAVAILABLE_ONLY
}

enum class SortOption {
    DEFAULT,
    PRICE_ASC,
    PRICE_DESC,
    PREPARATION_TIME_ASC
}

data class FilterState(
    val searchQuery: String = "",
    val categoryId: Long? = null,
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val sortOption: SortOption = SortOption.DEFAULT
) {
    val isActive: Boolean
        get() = searchQuery.isNotBlank() ||
                categoryId != null ||
                statusFilter != StatusFilter.ALL ||
                minPrice != null ||
                maxPrice != null ||
                sortOption != SortOption.DEFAULT

    fun buildDescription(context: Context, categoryName: String? = null): String {
        val parts = mutableListOf<String>()
        if (searchQuery.isNotBlank()) parts.add("\"$searchQuery\"")
        if (categoryName != null) {
            parts.add(CategoryLocalizationHelper.getLocalizedCategoryName(context, categoryName))
        } else if (categoryId != null) {
            parts.add("${context.getString(R.string.filter_category)} #$categoryId")
        }
        if (statusFilter == StatusFilter.AVAILABLE_ONLY) parts.add(context.getString(R.string.badge_available))
        if (statusFilter == StatusFilter.UNAVAILABLE_ONLY) parts.add(context.getString(R.string.badge_unavailable))
        if (minPrice != null || maxPrice != null) {
            val min = minPrice?.toInt() ?: 0
            val max = maxPrice?.toInt()?.toString() ?: "∞"
            parts.add(context.getString(R.string.filter_summary_price, min.toString(), max))
        }
        if (sortOption == SortOption.PRICE_ASC) parts.add(context.getString(R.string.filter_summary_price_asc))
        if (sortOption == SortOption.PRICE_DESC) parts.add(context.getString(R.string.filter_summary_price_desc))
        if (sortOption == SortOption.PREPARATION_TIME_ASC) parts.add(context.getString(R.string.filter_summary_prep_asc))
        return parts.joinToString(" • ")
    }
}
