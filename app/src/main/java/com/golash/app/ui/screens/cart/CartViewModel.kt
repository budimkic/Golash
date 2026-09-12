package com.golash.app.ui.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.data.api.RetrofitInstance
import com.golash.app.data.model.CheckoutRequest
import com.golash.app.data.model.ShippingInfoErrors
import com.golash.app.data.model.ShippingInfoState
import com.golash.app.domain.model.Cart
import com.golash.app.domain.model.Product
import com.golash.app.manager.CartManager
import com.golash.app.manager.NavigationManager
import com.squareup.moshi.JsonClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CartState {
    data object Loading : CartState()
    data class Success(val cart: Cart, val shippingInfo: ShippingInfoState = ShippingInfoState()) :
        CartState()

    data object LoadCartError : CartState()
    data object CartActionError : CartState()
}

sealed interface Action {
    data class OnIncreaseQuantity(val product: Product, val selectedSize: String) : Action
    data class OnDecreaseQuantity(val product: Product, val selectedSize: String) : Action
    data object OnClearCart : Action
    data class OnNavigate(val route: String) : Action

    // Field-specific actions that also clear the specific error when typing
    data class OnNameChanged(val name: String) : Action
    data class OnEmailChanged(val email: String) : Action
    data class OnPhoneChanged(val phone: String) : Action
    data class OnAddressChanged(val address: String) : Action
    data class OnCityChanged(val city: String) : Action
    data class OnPostCodeChanged(val postCode: String) : Action
    data class OnCountryChanged(val country: String) : Action
    data class OnAdditionalInfoChanged(val info: String) : Action

    data object OnSubmitOrder : Action
}

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String?
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartManager: CartManager,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _cartState = MutableStateFlow<CartState>(CartState.Loading)
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    private val _cartActionErrorState = MutableSharedFlow<CartState>()
    val cartActionErrorState: SharedFlow<CartState> = _cartActionErrorState.asSharedFlow()

    private val _submitOrderEvent = MutableSharedFlow<String>()
    val submitOrderEvent: SharedFlow<String> = _submitOrderEvent.asSharedFlow()

    init {
        observeCart()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OnIncreaseQuantity -> increaseQuantity(action.product, action.selectedSize)
            is Action.OnDecreaseQuantity -> decreaseQuantity(action.product, action.selectedSize)
            is Action.OnClearCart -> clearCart()
            is Action.OnNavigate -> navigateTo(action.route)
            is Action.OnNameChanged -> updateShippingField { it.copy(name = action.name, errors = it.errors?.copy(nameError = null)) }
            is Action.OnEmailChanged -> updateShippingField { it.copy(email = action.email, errors = it.errors?.copy(emailError = null)) }
            is Action.OnPhoneChanged -> updateShippingField { it.copy(phoneNumber = action.phone, errors = it.errors?.copy(phoneError = null)) }
            is Action.OnAddressChanged -> updateShippingField { it.copy(address = action.address, errors = it.errors?.copy(addressError = null)) }
            is Action.OnCityChanged -> updateShippingField { it.copy(city = action.city, errors = it.errors?.copy(cityError = null)) }
            is Action.OnPostCodeChanged -> updateShippingField { it.copy(postCode = action.postCode, errors = it.errors?.copy(postCodeError = null)) }
            is Action.OnCountryChanged -> updateShippingField { it.copy(country = action.country, errors = it.errors?.copy(countryError = null)) }
            is Action.OnAdditionalInfoChanged -> updateShippingField { it.copy(additionalInfo = action.info) }
            is Action.OnSubmitOrder -> submitOrder()
        }
    }

    private fun updateShippingField(update: (ShippingInfoState) -> ShippingInfoState) {
        val currentState = _cartState.value
        if (currentState is CartState.Success) {
            _cartState.value = currentState.copy(shippingInfo = update(currentState.shippingInfo))
        }
    }

    private fun navigateTo(route: String) {
        navigationManager.navigateTo(route)
    }

    fun simulateError() {
        _cartState.value = CartState.LoadCartError
    }

    private fun observeCart() {
        viewModelScope.launch {
            delay(500)
            try {
                cartManager.cart.collect { freshCart ->
                    val currentState = _cartState.value
                    if (currentState is CartState.Success) {
                        _cartState.value = currentState.copy(cart = freshCart)
                    } else {
                        _cartState.value = CartState.Success(freshCart)
                    }
                }
            } catch (e: Exception) {
                _cartState.value = CartState.LoadCartError
            }
        }
    }
    private fun submitOrder() {
        viewModelScope.launch {
            val currentState = _cartState.value
            if (currentState !is CartState.Success) return@launch

            val info = currentState.shippingInfo

            // 1. Run local field validations
            val nameErr = if (info.name.isBlank()) "Name is required" else null
            val emailErr = if (info.email.isBlank()) "E-mail is required" else null
            val phoneErr = if (info.phoneNumber.isBlank()) "Phone number is required" else null
            val addressErr = if (info.address.isBlank()) "Address is required" else null
            val cityErr = if (info.city.isBlank()) "City is required" else null
            val postCodeErr = if (info.postCode.isBlank()) "Post code is required" else null
            val countryErr = if (info.country.isBlank()) "Country is required" else null

            // 2. If any error exists, populate the nested ShippingInfoErrors object and halt
            if (nameErr != null || emailErr != null || phoneErr != null || addressErr != null || cityErr != null || postCodeErr != null || countryErr != null) {
                _cartState.value = currentState.copy(
                    shippingInfo = info.copy(
                        errors = ShippingInfoErrors(
                            nameError = nameErr,
                            emailError = emailErr,
                            phoneError = phoneErr,
                            addressError = addressErr,
                            cityError = cityErr,
                            postCodeError = postCodeErr,
                            countryError = countryErr
                        )
                    )
                )
                return@launch
            }

            // 3. Clear errors before sending to the backend API request
            val cleanInfo = info.copy(errors = null)
            _cartState.value = currentState.copy(shippingInfo = cleanInfo)

            val checkoutRequest = CheckoutRequest(
                shippingInfoState = cleanInfo,
                cart = currentState.cart
            )

            try {
                val response = RetrofitInstance.api.checkout(checkoutRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    val message = body?.message ?: "Order placed successfully!"
                    _submitOrderEvent.emit(message)
                    Log.d("CartViewModel", "Order success: $message")
                } else {
                    // --- PARSE BACKEND ERROR HERE ---
                    val errorBodyJson = response.errorBody()?.string()
                    val errorMessage = if (!errorBodyJson.isNullOrEmpty()) {
                        try {
                            val adapter = com.squareup.moshi.Moshi.Builder().build()
                                .adapter(ErrorResponse::class.java)
                            val errorResponse = adapter.fromJson(errorBodyJson)
                            errorResponse?.error ?: "Invalid request"
                        } catch (e: Exception) {
                            "Invalid request"
                        }
                    } else {
                        "Unknown error"
                    }

                    // Map the backend error to the correct text field error state
                    when {
                        errorMessage.contains("email", ignoreCase = true) -> {
                            _cartState.value = currentState.copy(
                                shippingInfo = info.copy(
                                    errors = ShippingInfoErrors(emailError = errorMessage)
                                )
                            )
                        }
                        errorMessage.contains("phone", ignoreCase = true) -> {
                            _cartState.value = currentState.copy(
                                shippingInfo = info.copy(
                                    errors = ShippingInfoErrors(phoneError = errorMessage)
                                )
                            )
                        }
                        else -> {
                            // General fallback for any other errors
                            _submitOrderEvent.emit(errorMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Network Error: ${e.message}")
                _submitOrderEvent.emit(e.message ?: "Network error")
            }
        }
    }

    private fun increaseQuantity(product: Product, selectedSize: String) {
        viewModelScope.launch {
            try {
                cartManager.increaseQuantity(product, selectedSize)
            } catch (e: Exception) {
                _cartActionErrorState.emit(CartState.CartActionError)
            }
        }
    }

    private fun decreaseQuantity(product: Product, selectedSize: String) {
        viewModelScope.launch {
            try {
                cartManager.decreaseQuantity(product, selectedSize)
            } catch (e: Exception) {
                _cartActionErrorState.emit(CartState.CartActionError)
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch { cartManager.clearCart() }
    }
}