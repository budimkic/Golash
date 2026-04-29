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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CartState {
    data object Loading : CartState()
    data class Success(val cart: Cart) : CartState()
    data object LoadCartError : CartState()
    data object CartActionError : CartState()
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

    private val _cartActionErrorState = MutableSharedFlow<CartState>()
    val cartActionErrorState: SharedFlow<CartState> = _cartActionErrorState.asSharedFlow()

    init {
        observeCart()
        //simulateError()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OnIncreaseQuantity -> increaseQuantity(action.product)
            is Action.OnDecreaseQuantity -> decreaseQuantity(action.product)
            is Action.OnClearCart -> clearCart()
        }
    }

    fun simulateError() {
        _cartState.value = CartState.LoadCartError
    }

    private fun observeCart() {
        viewModelScope.launch {
            try {
                cartManager.cart.collect { freshCart ->
                    _cartState.value = CartState.Success(freshCart)
                }
            } catch (e: Exception) {
                _cartState.value = CartState.LoadCartError
            }

        }
    }

    private fun increaseQuantity(product: Product) {
        viewModelScope.launch {
            try {
                cartManager.increaseQuantity(product)
            } catch (e: Exception) {
                _cartActionErrorState.emit(CartState.CartActionError)
            }
        }
    }

    private fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            try {
                cartManager.decreaseQuantity(product)
            } catch (e: Exception) {
                _cartActionErrorState.emit(CartState.CartActionError)
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch { cartManager.clearCart() }
    }
}