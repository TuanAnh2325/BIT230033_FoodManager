package com.example.bit230033_foodmanager.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bit230033_foodmanager.data.local.entity.DishWithDetails
import com.example.bit230033_foodmanager.data.repository.DishRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DishDetailViewModel(
    private val dishId: Long,
    private val repository: DishRepository
) : ViewModel() {

    val dishWithDetails: StateFlow<DishWithDetails?> =
        repository.getDishWithDetailsFlowById(dishId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setThumbnail(imageId: Long) {
        viewModelScope.launch {
            repository.setExclusiveThumbnail(dishId, imageId)
        }
    }

    fun addImage(imageUrl: String, setAsThumbnail: Boolean) {
        viewModelScope.launch {
            repository.addImageToDish(dishId, imageUrl, setAsThumbnail)
        }
    }

    fun deleteDish(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteDish(dishId)
            onDeleted()
        }
    }

    class Factory(
        private val dishId: Long,
        private val repository: DishRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DishDetailViewModel(dishId, repository) as T
        }
    }
}
