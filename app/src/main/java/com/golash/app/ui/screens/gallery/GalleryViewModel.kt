package com.golash.app.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.data.model.Product
import com.golash.app.data.repository.product.MockProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GalleryUIState {
    data object Loading : GalleryUIState()
    data class Success(val products: List<Product>) : GalleryUIState()
    data class Error(val message: String) : GalleryUIState()
}

@HiltViewModel
class GalleryViewModel @Inject constructor(private val repository: MockProductRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUIState>(GalleryUIState.Loading)
    val uiState: StateFlow<GalleryUIState> = _uiState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.value = GalleryUIState.Loading
                val products = repository.getProducts()
                _uiState.value = GalleryUIState.Success(products)
            } catch (e: Exception) {
                _uiState.value = GalleryUIState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refresh() {
        loadProducts()
    }

}