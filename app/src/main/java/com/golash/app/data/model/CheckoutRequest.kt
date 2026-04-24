package com.golash.app.data.model

import com.golash.app.domain.model.Cart

data class CheckoutRequest(
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val cart: Cart
)