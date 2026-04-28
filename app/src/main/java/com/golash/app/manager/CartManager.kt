package com.golash.app.manager

import com.golash.app.domain.model.Cart
import com.golash.app.domain.model.CartItem
import com.golash.app.domain.model.Product
import com.golash.app.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class CartManager @Inject constructor(private val cartRepository: CartRepository) {

    val cart: Flow<Cart> = cartRepository.getCart()

    private suspend fun getCurrentCart(): Cart = cart.first()

    suspend fun addItem(product: Product) {
        val currentItems = getCurrentCart().items
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            increaseQuantity(product)
        } else {
            cartRepository.updateCart(CartItem(product, 1))
        }
    }

    suspend fun increaseQuantity(product: Product) {
        val currentItems = getCurrentCart().items
        val item = currentItems.find { it.product.id == product.id}

        if (item != null) {
            cartRepository.updateCart(item.copy(quantity = item.quantity + 1))
        }
    }

    // Remove item entirely if quantity drops below 1.
    suspend fun decreaseQuantity(product: Product) {
      val currentItems = getCurrentCart().items
        val item = currentItems.find { it.product.id == product.id }

        if (item != null) {
            if (item.quantity > 1){
                cartRepository.updateCart(item.copy(quantity = item.quantity - 1))
            } else {
                cartRepository.removeItem(item)
            }
        }
    }

    suspend fun clearCart() {
       cartRepository.clearCart()
    }
}