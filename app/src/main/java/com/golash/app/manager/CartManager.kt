package com.golash.app.manager

import android.util.Log
import com.golash.app.domain.model.Cart
import com.golash.app.domain.model.CartItem
import com.golash.app.domain.model.Product
import com.golash.app.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "CartManager"
class CartManager @Inject constructor(private val cartRepository: CartRepository) {

    val cart: Flow<Cart> = cartRepository.getCart().map { rawCart ->
        val sortedItems = rawCart.items.sortedBy { it.addedAt }
        rawCart.copy(items = sortedItems)
    }

    private suspend fun getCurrentCart(): Cart = cart.first()

    suspend fun addItem(product: Product, selectedSize: String) {
        val currentItems = getCurrentCart().items

        val existingItem = currentItems.find { it.product.id == product.id && it.selectedSize == selectedSize }

        if (existingItem != null) {
            increaseQuantity(product, selectedSize)
        } else {
            cartRepository.updateCart(CartItem(product, selectedSize, 1, addedAt = System.currentTimeMillis()))
        }
    }

    suspend fun increaseQuantity(product: Product, selectedSize: String) {
        val currentItems = getCurrentCart().items
        val item = currentItems.find { it.product.id == product.id && it.selectedSize == selectedSize}

        if (item != null) {
            cartRepository.updateCart(item.copy(quantity = item.quantity + 1))
        }
    }

    // Remove item entirely if quantity drops below 1.
    suspend fun decreaseQuantity(product: Product, selectedSize: String) {
      val currentItems = getCurrentCart().items
        val item = currentItems.find { it.product.id == product.id && it.selectedSize == selectedSize}

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