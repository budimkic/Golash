package com.golash.app.manager

import android.util.Log
import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product
import com.golash.app.data.repository.cart.CartRepository
import javax.inject.Inject

class CartManager @Inject constructor(private val cartRepository: CartRepository) {

    private var currentCart : Cart? = null

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
            return
        }
        val updatedItems = cart.items + CartItem(product, 1)
        updateCart(Cart(updatedItems))
    }

    suspend fun removeItem(productId: String) {
        val cart = ensureCartLoaded()
        val updatedItems = cart.items.filterNot { it.product.id == productId }
        updateCart(Cart(updatedItems))
    }

    suspend fun increaseQuantity(product: Product) {
        val cart = ensureCartLoaded()
        val updatedItems = cart.items.map {
            if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
        }
        updateCart(Cart(updatedItems))
    }

    suspend fun decreaseQuantity(product: Product){
        val cart  = ensureCartLoaded()
        var updatedCart = cart.items.map {
            if(it.product.id == product.id) it.copy(quantity = it.quantity - 1) else it
        }.filter { it.quantity > 0 }

        updateCart(Cart(updatedCart))
    }

    suspend fun clearCart() {
        updateCart(Cart(emptyList()))
    }

   suspend fun getCart(): Cart = ensureCartLoaded()

    private suspend fun updateCart(updatedCart: Cart) {
        currentCart = updatedCart
        cartRepository.saveCart(updatedCart)
    }
}