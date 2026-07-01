package com.golash.app.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.domain.model.Product
import com.golash.app.data.repository.product.MockProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GalleryState {
    data object Idle : GalleryState()
    data object Loading : GalleryState()
    data class Success(val products: List<Product>) : GalleryState()
    data class Error(val message: String) : GalleryState()
}

@HiltViewModel
class GalleryViewModel @Inject constructor(private val repository: MockProductRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<GalleryState>(GalleryState.Loading)
    val uiState: StateFlow<GalleryState> = _uiState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.value = GalleryState.Loading
                delay(500)
                val products = repository.getProducts()
                _uiState.value = GalleryState.Success(products)
            } catch (e: Exception) {
                _uiState.value = GalleryState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refresh() {
        loadProducts()
    }
}