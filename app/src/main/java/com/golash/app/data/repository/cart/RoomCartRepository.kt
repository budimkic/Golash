package com.golash.app.data.repository.cart

import com.golash.app.data.db.AppDatabase
import com.golash.app.data.mapper.db.toCartItem
import com.golash.app.data.mapper.db.toEntity
import com.golash.app.domain.repository.CartRepository
import com.golash.app.domain.model.Cart
import com.golash.app.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomCartRepository(db: AppDatabase) : CartRepository {

    private val cartDao = db.cartDao()

    override fun getCart(): Flow<Cart> {
      return cartDao.getAll().map { entities ->
          Cart(entities.map { it.toCartItem() })
      }
    }

    override suspend fun updateCart(item: CartItem) {
        cartDao.insert(item.toEntity())
    }

    override suspend fun removeItem(item: CartItem) {
        cartDao.deleteById(item.product.id)
    }

    override suspend fun clearCart() {
        cartDao.clear()
    }
}