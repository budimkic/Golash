package com.golash.app.data.model

import androidx.annotation.StringRes
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
    @get:StringRes val nameError: Int? = null,
    @get:StringRes val emailError: Int? = null,
    @get:StringRes val phoneError: Int? = null,
    @get:StringRes val addressError: Int? = null,
    @get:StringRes val cityError: Int? = null,
    @get:StringRes val postCodeError: Int? = null,
    @get:StringRes val countryError: Int? = null
)

@JsonClass(generateAdapter = true)
data class CheckoutRequest(
    val shippingInfoState: ShippingInfoState,
    val cart: Cart
)