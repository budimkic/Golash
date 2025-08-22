package com.golash.app.manager

import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product
import com.golash.app.data.repository.cart.CartRepository
import javax.inject.Inject

/**
 * Single source of truth for the shopping cart.
 *
 * Responsibilities:
 *  - Caches cart in memory to avoid repeated repository calls.
 *  - All mutations must go through [updateCart] to stay in sync with repository.
 **/
class CartManager @Inject constructor(private val cartRepository: CartRepository) {

    // Lazily loaded cart cache; null until first access.
    private var currentCart: Cart? = null

    // Guarantees a loaded, non-null cart for callers.
    private suspend fun ensureCartLoaded(): Cart {
        if (currentCart == null) {
            currentCart = cartRepository.loadCart()
        }
        return currentCart!!
    }

    suspend fun loadCart(): Cart {
        currentCart = cartRepository.loadCart()
        return currentCart!!
    }


    suspend fun addItem(product: Product) {
        val cart = ensureCartLoaded()
        if (cart.items.any { it.product.id == product.id }) {
            increaseQuantity(product)
            return
        }
        val updatedCart = cart.items + CartItem(product, 1)
        updateCart(Cart(updatedCart))
    }

    suspend fun removeItem(productId: String) {
        val cart = ensureCartLoaded()
        val updatedCart = cart.items.filterNot { it.product.id == productId }
        updateCart(Cart(updatedCart))
    }

    suspend fun increaseQuantity(product: Product) {
        val cart = ensureCartLoaded()
        val updatedCart = cart.items.map {
            if (it.product.id == product.id && it.quantity < 5) it.copy(quantity = it.quantity + 1) else it
        }
        updateCart(Cart(updatedCart))
    }

    // Remove item entirely if quantity drops below 1.
    suspend fun decreaseQuantity(product: Product) {
        val cart = ensureCartLoaded()
        var updatedCart = cart.items.map {
            if (it.product.id == product.id) it.copy(quantity = it.quantity - 1) else it
        }.filter { it.quantity > 0 }

        updateCart(Cart(updatedCart))
    }

    suspend fun clearCart() {
        updateCart(Cart(emptyList()))
    }

    suspend fun getCart(): Cart = ensureCartLoaded()

    // All cart mutations must pass through here to keep cache and repository consistent.
    private suspend fun updateCart(updatedCart: Cart) {
        currentCart = updatedCart
        cartRepository.saveCart(updatedCart)
    }

    suspend fun getQuantity(product: Product): Int {
        val cart = ensureCartLoaded()
        return cart.items.find {
            it.product.id == product.id
        }?.quantity ?: 0
    }

}