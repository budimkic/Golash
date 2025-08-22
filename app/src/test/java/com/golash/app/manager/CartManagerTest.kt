package com.golash.app.manager

import com.golash.app.data.model.Product
import com.golash.app.data.repository.cart.FakeCartRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class CartManagerTest {

    private lateinit var repository: FakeCartRepository
    private lateinit var cartManager: CartManager
    private val product = Product("123", "hat", "This hat was worn by Keith Richards.", 00.00, emptyList())

    @Before
    fun setup() {
        repository = FakeCartRepository()
        cartManager = CartManager(repository)
    }

    @Test
    fun testAddItem() = runBlocking {
        cartManager.addItem(product)
        val cart = cartManager.getCart()
        assertEquals(1, cart.items.size)
        assertEquals(1, cart.items.first().quantity)
    }

    @Test
    fun testIncreaseQuantity() = runBlocking {
        cartManager.addItem(product)
        cartManager.increaseQuantity(product)
        val cart = cartManager.getCart()
        assertEquals(1, cart.items.size)
        assertEquals(2, cart.items[0].quantity)
    }

}

