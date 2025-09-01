package com.golash.app.data.api

import com.golash.app.data.model.CheckoutRequest
import com.golash.app.data.model.CheckoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GolashApi {
    @POST("checkout")
    suspend fun checkout(@Body request: CheckoutRequest): Response<CheckoutResponse>
}