package com.golash.app.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.domain.model.Cart
import com.golash.app.domain.model.Product
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

sealed interface Action {
    data class OnIncreaseQuantity(val product: Product) : Action
    data class OnDecreaseQuantity(val product: Product) : Action
    data object OnClearCart : Action
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartManager: CartManager
) : ViewModel() {

    private val _cartState = MutableStateFlow<CartState>(CartState.Loading)
    val cartState: StateFlow<CartState> = _cartState

    init {
        observeCart()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OnIncreaseQuantity -> increaseQuantity(action.product)
            is Action.OnDecreaseQuantity -> decreaseQuantity(action.product)
            is Action.OnClearCart -> clearCart()
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartManager.cart.collect { freshCart ->
                _cartState.value = CartState.Success(freshCart)
            }
        }
    }

    private fun increaseQuantity(product: Product) {
        viewModelScope.launch {
            try {
                cartManager.increaseQuantity(product)
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            try {
                cartManager.decreaseQuantity(product)
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch { cartManager.clearCart() }
    }
}