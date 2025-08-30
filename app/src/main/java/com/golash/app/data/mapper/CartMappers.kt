package com.golash.app.data.mapper

import com.golash.app.data.db.CartItemEntity
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product

fun CartItemEntity.toCartItem(): CartItem {
    return CartItem(
        product = Product(
            id = productId,
            name = name,
            shortDescription = productDescription,
            details = details,
            price = price,
            images = images
        ),
        quantity = quantity
    )
}

fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        productId = product.id,
        name = product.name,
        price = product.price,
        productDescription = product.shortDescription,
        details = product.details,
        images = product.images,
        quantity = quantity
    )
}