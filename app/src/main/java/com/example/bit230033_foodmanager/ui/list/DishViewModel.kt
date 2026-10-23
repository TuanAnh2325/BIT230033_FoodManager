package com.example.bit230033_foodmanager.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bit230033_foodmanager.data.local.entity.BIT230033_DishCategory
import com.example.bit230033_foodmanager.data.local.entity.DishWithDetails
import com.example.bit230033_foodmanager.data.repository.CategoryRepository
import com.example.bit230033_foodmanager.data.repository.DishRepository
import com.example.bit230033_foodmanager.sync.NetworkMonitor
import kotlinx.coroutines.flow.*

class DishViewModel(
    private val dishRepository: DishRepository,
    private val categoryRepository: CategoryRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    // Preserved filter state across screen navigation
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isConnected

    val categories: StateFlow<List<BIT230033_DishCategory>> =
        categoryRepository.allCategories
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered & sorted dishes stream
    val dishes: StateFlow<List<DishWithDetails>> = combine(
        dishRepository.allDishesWithDetails,
        _filterState
    ) { allDishes, filter ->
        allDishes.filter { item ->
            val dish = item.dish

            // 1. Search by partial name
            val matchName = if (filter.searchQuery.isBlank()) true
            else dish.name.contains(filter.searchQuery, ignoreCase = true)

            // 2. Filter by category
            val matchCategory = if (filter.categoryId == null) true
            else dish.dishCategoryId == filter.categoryId

            // 3. Filter by availability status
            val matchStatus = when (filter.statusFilter) {
                StatusFilter.ALL -> true
                StatusFilter.AVAILABLE_ONLY -> dish.isAvailable
                StatusFilter.UNAVAILABLE_ONLY -> !dish.isAvailable
            }

            // 4. Filter by price range
            val matchMinPrice = if (filter.minPrice == null) true
            else dish.price >= filter.minPrice

            val matchMaxPrice = if (filter.maxPrice == null) true
            else dish.price <= filter.maxPrice

            matchName && matchCategory && matchStatus && matchMinPrice && matchMaxPrice
        }.let { list ->
            // Sắp xếp
            when (filter.sortOption) {
                SortOption.DEFAULT -> list.sortedByDescending { it.dish.id }
                SortOption.PRICE_ASC -> list.sortedBy { it.dish.price }
                SortOption.PRICE_DESC -> list.sortedByDescending { it.dish.price }
                SortOption.PREPARATION_TIME_ASC -> list.sortedBy { it.dish.preparationTime }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun applyFilters(
        categoryId: Long?,
        status: StatusFilter,
        minPrice: Double?,
        maxPrice: Double?,
        sortOption: SortOption
    ) {
        _filterState.value = _filterState.value.copy(
            categoryId = categoryId,
            statusFilter = status,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sortOption = sortOption
        )
    }

    fun resetFilters() {
        val currentSearch = _filterState.value.searchQuery
        _filterState.value = FilterState(searchQuery = currentSearch)
    }

    fun clearSearch() {
        _filterState.value = _filterState.value.copy(searchQuery = "")
    }

    class Factory(
        private val dishRepository: DishRepository,
        private val categoryRepository: CategoryRepository,
        private val networkMonitor: NetworkMonitor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DishViewModel(dishRepository, categoryRepository, networkMonitor) as T
        }
    }
}
