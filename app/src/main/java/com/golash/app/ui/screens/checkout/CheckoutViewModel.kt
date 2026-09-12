package com.golash.app.ui.screens.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.data.api.RetrofitInstance
import com.golash.app.data.model.CheckoutRequest
import com.golash.app.domain.model.Cart
import com.golash.app.manager.CartManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface Action {
    data class OnCheckout(val checkoutRequest: CheckoutRequest) : Action
}
/*
@HiltViewModel
class CheckoutViewModel @Inject constructor(private val cartManager: CartManager) : ViewModel() {

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    init {
        observeCart()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OnCheckout -> sendOrder(action.checkoutRequest)
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            try {
                cartManager.cart.collect { cart ->
                    _checkoutState.value = CheckoutState.LoadSuccess(cart)
                }
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error(e.message.toString())
            }
        }
    }

    private fun sendOrder(checkoutRequest: CheckoutRequest) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                val response = RetrofitInstance.api.checkout(checkoutRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    //_checkoutState.value = CheckoutState.Success(body?.message ?: "Success")
                    Log.d("CheckoutViewModel", "Order success: ${body?.message}")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _checkoutState.value = CheckoutState.Error(errorMessage.toString())
                }
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error(e.message ?: "Network error")
                Log.e("CheckoutViewModel", "Network Error: ${e.message}")
            }
        }
    }
}*/