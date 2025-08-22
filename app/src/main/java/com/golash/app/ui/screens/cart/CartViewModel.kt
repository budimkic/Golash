package com.golash.app.ui.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golash.app.data.model.Cart
import com.golash.app.data.model.Product
import com.golash.app.data.repository.cart.CartRepository
import com.golash.app.data.repository.cart.InMemoryCartRepository
import com.golash.app.manager.CartManager
import com.golash.app.ui.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CartState {
    data object Loading : CartState()
    data class Success(val cart: Cart) : CartState()
    data class Error(val message: String) : CartState()
}

sealed class AddToCartResult {
    data object Loading : AddToCartResult()
    data object Success : AddToCartResult()
    data class Error(val message: String) : AddToCartResult()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartManager: CartManager
) : ViewModel() {

    private val _cartState = MutableStateFlow<CartState>(CartState.Loading)
    val cartState: StateFlow<CartState> = _cartState

    private val _addToCartResult = MutableSharedFlow<AddToCartResult>()
    val addToCartResult: SharedFlow<AddToCartResult> = _addToCartResult

    init {
        viewModelScope.launch { loadCart() }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            _addToCartResult.emit(AddToCartResult.Loading)
            try {
                cartManager.addItem(product)
                _addToCartResult.emit(AddToCartResult.Success)
                loadCart()
            } catch (e: Exception) {
                _addToCartResult.emit(AddToCartResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun increaseQuantity(product: Product) {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            try {
                cartManager.increaseQuantity(product)
                _cartState.value = CartState.Success(cartManager.getCart())
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            try {
                cartManager.decreaseQuantity(product)
                _cartState.value = CartState.Success(cartManager.getCart())
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun loadCart() {
        return try {
            val cart = cartManager.loadCart()
            _cartState.value = CartState.Success(cart)
        } catch (e: Exception) {
            _cartState.value = CartState.Error(e.message ?: "Unknown error")
            throw e
        }
    }

    fun refresh() {
        viewModelScope.launch { loadCart() }
    }

    fun clearCart() {
        viewModelScope.launch { cartManager.clearCart() }
    }
}