package com.golash.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckoutResponse(val success: Boolean, val message: String)