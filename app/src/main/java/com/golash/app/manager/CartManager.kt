package com.golash.app.manager

import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product
import com.golash.app.data.repository.cart.CartRepository
import javax.inject.Inject

class CartManager @Inject constructor (private val cartRepository: CartRepository) {

    private var currentCart = Cart(emptyList())

    suspend fun loadCart(): Cart {
        currentCart = cartRepository.loadCart()
        return currentCart
    }

    suspend fun addItem(product: Product) {
        if (currentCart.items.any { it.product.id == product.id }) {
            return
        }
        val updatedItems = currentCart.items + CartItem(product, 1)
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