package com.golash.app.data.repository.cart

import com.golash.app.data.model.Cart

class FakeCartRepository : CartRepository {
    private var cart = Cart(emptyList())

    override suspend fun saveCart(cart: Cart) {
        this.cart = cart
    }

    override suspend fun loadCart(): Cart {
        return cart
    }

    override suspend fun clearCart() {
        cart = Cart(emptyList())
    }
}