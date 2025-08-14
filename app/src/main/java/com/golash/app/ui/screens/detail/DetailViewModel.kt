package com.golash.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.data.model.Product
import com.golash.app.data.repository.product.MockProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val product: Product) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: MockProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    var uiState: StateFlow<DetailUiState> = _uiState

    private val productId = savedStateHandle.get<String>("productId")

    init {
        refresh()
    }

    private fun loadProduct(productId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = DetailUiState.Loading
                //Get the product using the ID passed from the navigation
                productRepository.getProductById(productId)?.let { product ->
                    _uiState.value = DetailUiState.Success(product)
                } ?: run {
                    _uiState.value = DetailUiState.Error("Product not found")
                }

            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun refresh() {
        productId?.let { loadProduct(it) }
    }
}