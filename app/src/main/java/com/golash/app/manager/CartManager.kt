package com.golash.app.manager

import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.repository.cart.CartRepository

class CartManager(private val cartRepository: CartRepository) {

    private var currentCart = Cart(emptyList())

    suspend fun loadCart() {
        currentCart = cartRepository.loadCart()
    }

    suspend fun addItem(item: CartItem) {
        if (currentCart.items.any { it.product.id == item.product.id }) {
            return
        }
        val updatedItems = currentCart.items + CartItem(item.product, 1)
        updateCart(Cart(updatedItems))
    }

    suspend fun removeItem(productId: String) {
        val updatedItems = currentCart.items.filterNot { it.product.id == productId }
        updateCart(Cart(updatedItems))
    }

    suspend fun clearCart() {
        updateCart(Cart(emptyList()))
    }

    fun getCart(): Cart = currentCart

    private suspend fun updateCart(updatedCart: Cart) {
        currentCart = updatedCart
        cartRepository.saveCart(updatedCart)
    }
}