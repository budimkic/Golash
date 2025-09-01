package com.golash.app.data.model

data class CheckoutRequest(
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val cart: Cart
)