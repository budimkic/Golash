package com.golash.app.data.model

import com.golash.app.domain.model.Cart
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShippingInfoState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val city: String = "",
    val postCode: String = "",
    val country: String = "",
    val additionalInfo: String = "",
    @Transient val errors: ShippingInfoErrors? = null
)

data class ShippingInfoErrors(
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val cityError: String? = null,
    val postCodeError: String? = null,
    val countryError: String? = null
)

@JsonClass(generateAdapter = true)
data class CheckoutRequest(
    val shippingInfoState: ShippingInfoState,
    val cart: Cart
)