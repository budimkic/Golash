package com.golash.app.data.repository.cart

import com.golash.app.data.db.AppDatabase
import com.golash.app.data.mapper.toCartItem
import com.golash.app.data.mapper.toEntity
import com.golash.app.data.model.Cart

class RoomCartRepository(db: AppDatabase) : CartRepository {

    private val cartDao = db.cartDao()

    override suspend fun saveCart(cart: Cart) {
        val entities = cart.items.map { it.toEntity() }
        cartDao.insertAll(entities)
    }

    override suspend fun loadCart(): Cart {
        val items = cartDao.getAll().map { it.toCartItem() }
        return Cart(items)
    }

    override suspend fun clearCart() {
        cartDao.clear()
    }
}