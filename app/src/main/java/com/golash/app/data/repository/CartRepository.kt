package com.golash.app.data.repository

import com.golash.app.data.model.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {



   suspend fun getCartProducts(): List<Product> =
}