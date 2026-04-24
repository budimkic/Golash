package com.golash.app.data.repository.cart

import com.golash.app.domain.model.Cart

interface CartRepository{
    suspend fun saveCart(cart: Cart)
    suspend fun loadCart(): Cart
    suspend fun clearCart()
}