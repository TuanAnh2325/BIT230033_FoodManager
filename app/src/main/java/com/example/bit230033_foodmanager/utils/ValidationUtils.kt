package com.example.bit230033_foodmanager.utils

import android.content.Context
import com.example.bit230033_foodmanager.R

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val messageResId: Int) : ValidationResult()
}

object ValidationUtils {

    fun validateDishName(name: String): ValidationResult {
        if (name.trim().isEmpty()) {
            return ValidationResult.Error(R.string.err_dish_name_empty)
        }
        return ValidationResult.Success
    }

    fun validatePrice(priceStr: String): ValidationResult {
        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            return ValidationResult.Error(R.string.err_price_invalid)
        }
        return ValidationResult.Success
    }

    fun validatePreparationTime(prepTimeStr: String): ValidationResult {
        val time = prepTimeStr.toIntOrNull()
        if (time == null || time <= 0) {
            return ValidationResult.Error(R.string.err_prep_time_invalid)
        }
        return ValidationResult.Success
    }

    fun validateCategorySelected(categoryId: Long): ValidationResult {
        if (categoryId <= 0) {
            return ValidationResult.Error(R.string.err_category_required)
        }
        return ValidationResult.Success
    }

    fun validateImageUrl(url: String): ValidationResult {
        val trimmed = url.trim()
        if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
            return ValidationResult.Error(R.string.err_image_url_invalid)
        }
        return ValidationResult.Success
    }

    fun validateCategoryName(name: String): ValidationResult {
        if (name.trim().isEmpty()) {
            return ValidationResult.Error(R.string.err_cat_name_empty)
        }
        return ValidationResult.Success
    }

    fun validatePriceRange(minPrice: Double?, maxPrice: Double?): ValidationResult {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            return ValidationResult.Error(R.string.err_price_range_invalid)
        }
        return ValidationResult.Success
    }
}
