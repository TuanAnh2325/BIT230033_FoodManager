package com.example.bit230033_foodmanager.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.data.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<BIT230033_DishCategory>> =
        repository.allCategories
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addCategory(name: String, description: String?): Result<Long> {
        val trimmed = name.trim()
        if (repository.isCategoryNameExists(trimmed)) {
            return Result.failure(IllegalArgumentException("Category name already exists."))
        }
        val id = repository.insertCategory(BIT230033_DishCategory(name = trimmed, description = description?.trim()))
        return Result.success(id)
    }

    suspend fun deleteCategory(category: BIT230033_DishCategory): Result<Unit> {
        return repository.deleteCategory(category)
    }

    class Factory(
        private val repository: CategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryViewModel(repository) as T
        }
    }
}
