package com.example.bit230033_foodmanager.data.repository

import com.example.bit230033_foodmanager.data.local.dao.BIT230033_CategoryDao
import com.example.bit230033_foodmanager.data.local.dao.BIT230033_DishDao
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: BIT230033_CategoryDao,
    private val dishDao: BIT230033_DishDao
) {

    val allCategories: Flow<List<BIT230033_DishCategory>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): BIT230033_DishCategory? =
        categoryDao.getCategoryById(id)

    suspend fun isCategoryNameExists(name: String, excludeId: Long = 0): Boolean =
        categoryDao.isCategoryNameExists(name, excludeId) > 0

    suspend fun insertCategory(category: BIT230033_DishCategory): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: BIT230033_DishCategory) =
        categoryDao.updateCategory(category)


    suspend fun deleteCategory(category: BIT230033_DishCategory): Result<Unit> {
        val count = dishDao.countDishesByCategory(category.id)
        return if (count > 0) {
            Result.failure(IllegalStateException("Cannot delete category because it contains $count dishes."))
        } else {
            categoryDao.deleteCategory(category)
            Result.success(Unit)
        }
    }
}
