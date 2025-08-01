package com.golash.app.data.repository

import com.golash.app.data.model.Cart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCartRepository @Inject constructor() : CartRepository {

    private var cartData: Cart = Cart(emptyList())

    override suspend fun saveCart(cart: Cart) {
        cartData = cart
    }

    override suspend fun loadCart(): Cart {
        return cartData
    }

    override suspend fun clearCart() {
        cartData = Cart(emptyList())
    }
}