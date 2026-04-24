package com.golash.app.domain.model

data class Cart(
    val items: List<CartItem>
) {
    val totalPrice: Double
        get() = items.sumOf { it.product.price * it.quantity }

    val totalItems: Int
        get() = items.sumOf { it.quantity }
}

data class CartItem(
    val product: Product,
    val quantity: Int
)

