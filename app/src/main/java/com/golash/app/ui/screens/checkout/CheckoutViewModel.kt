package com.golash.app.ui.screens.checkout

import android.util.Log
import androidx.compose.material3.Checkbox
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.data.api.RetrofitInstance
import com.golash.app.data.model.CheckoutRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CheckoutState {
    data object Idle : CheckoutState()
    data object Loading : CheckoutState()
    data class Success(val message: String) : CheckoutState()
    data class Error(val error: String): CheckoutState()
}


@HiltViewModel
class CheckoutViewModel : ViewModel() {

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    fun sendOrder(checkoutRequest: CheckoutRequest) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                val response = RetrofitInstance.api.checkout(checkoutRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    _checkoutState.value = CheckoutState.Success(body?.message ?: "Success")
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
}