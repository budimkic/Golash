package com.golash.app.domain.repository

import com.golash.app.domain.model.Cart
import com.golash.app.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository{
    fun getCart(): Flow<Cart>
    suspend fun updateCart(item: CartItem)
    suspend fun removeItem(item: CartItem)
    suspend fun clearCart()
}