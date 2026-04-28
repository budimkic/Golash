package com.golash.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.domain.model.Product
import com.golash.app.domain.repository.ProductRepository
import com.golash.app.manager.CartManager
import com.golash.app.ui.screens.detail.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val product: Product) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

sealed class AddToCartResult {
    data object Loading : AddToCartResult()
    data object Success : AddToCartResult()
    data class Error(val message: String) : AddToCartResult()
}

sealed interface Action {
    data class OnAddToCart(val product: Product) : Action
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartManager: CartManager

) : ViewModel() {

    private val productIdFlow = savedStateHandle.getStateFlow<String?>(key = "productId", initialValue = null)

    val uiState: StateFlow<DetailUiState> = productIdFlow
        .filterNotNull()
        .flatMapLatest { id ->
            flow {
                emit(DetailUiState.Loading)
                try {
                    val product = productRepository.getProductById(id)
                    if (product != null) {
                        emit(DetailUiState.Success(product))
                    } else {
                        emit(DetailUiState.Error("Product not found"))
                    }
                } catch (e: Exception) {
                    emit(DetailUiState.Error(e.message ?: "Unknown error occurred"))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailUiState.Loading
        )

    private val _addToCartResult = MutableSharedFlow<AddToCartResult>()
    val addToCartResult = _addToCartResult.asSharedFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.OnAddToCart -> addToCart(action.product)
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            _addToCartResult.emit(AddToCartResult.Loading)
            try {
                cartManager.addItem(product)
                _addToCartResult.emit(AddToCartResult.Success)
            } catch (e: Exception) {
                _addToCartResult.emit(AddToCartResult.Error(e.message ?: "Unknown error"))
            }
        }
    }
}