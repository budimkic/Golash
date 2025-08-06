package com.golash.app.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.data.model.Cart
import com.golash.app.data.model.Product
import com.golash.app.data.repository.cart.CartRepository
import com.golash.app.data.repository.cart.InMemoryCartRepository
import com.golash.app.manager.CartManager
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
        loadCart()
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            _addToCartResult.emit(AddToCartResult.Loading)
            try {
                cartManager.addItem(product)
                _addToCartResult.emit(AddToCartResult.Success)
                loadCart()

                ///TODO Emit Success after loadCart() completes, by making loadCart() a suspend function and calling it with await.
            } catch (e: Exception) {
                _addToCartResult.emit(AddToCartResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            try {
                val cart = cartManager.loadCart()
                _cartState.value = CartState.Success(cart)
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch { cartManager.clearCart() }
    }
}