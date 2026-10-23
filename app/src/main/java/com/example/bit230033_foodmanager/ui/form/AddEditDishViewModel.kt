package com.example.bit230033_foodmanager.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_Dish
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.data.local.entity.DishWithDetails
import com.example.bit230033_foodmanager.data.repository.CategoryRepository
import com.example.bit230033_foodmanager.data.repository.DishRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddEditDishViewModel(
    private val dishId: Long,
    private val dishRepository: DishRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val isEditMode: Boolean = dishId > 0

    val categories: StateFlow<List<BIT230033_DishCategory>> =
        categoryRepository.allCategories
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _existingDish = MutableStateFlow<DishWithDetails?>(null)
    val existingDish: StateFlow<DishWithDetails?> = _existingDish.asStateFlow()

    init {
        if (isEditMode) {
            viewModelScope.launch {
                val dish = dishRepository.getDishWithDetailsById(dishId)
                _existingDish.value = dish
            }
        }
    }

    suspend fun saveDish(
        name: String,
        price: Double,
        prepTime: Int,
        categoryId: Long,
        isAvailable: Boolean,
        description: String?,
        images: List<Pair<String, Boolean>> // Pair(url, isThumbnail)
    ): Result<Unit> {
        val existing = _existingDish.value?.dish

        return if (isEditMode && existing != null) {
            val updated = existing.copy(
                name = name.trim(),
                price = price,
                preparationTime = prepTime,
                dishCategoryId = categoryId,
                isAvailable = isAvailable,
                description = description?.trim()
            )
            dishRepository.updateDish(updated, images)
        } else {
            val newDish = BIT230033_Dish(
                name = name.trim(),
                price = price,
                preparationTime = prepTime,
                dishCategoryId = categoryId,
                isAvailable = isAvailable,
                description = description?.trim()
            )
            val thumbIndex = images.indexOfFirst { it.second }.let { if (it >= 0) it else 0 }
            dishRepository.insertDish(newDish, images.map { it.first }, thumbIndex).map { }
        }
    }

    class Factory(
        private val dishId: Long,
        private val dishRepository: DishRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditDishViewModel(dishId, dishRepository, categoryRepository) as T
        }
    }
}
