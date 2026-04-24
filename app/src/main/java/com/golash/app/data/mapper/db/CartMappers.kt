package com.golash.app.data.mapper.db

import com.golash.app.data.db.CartItemEntity
import com.golash.app.data.db.ProductImageEntity
import com.golash.app.domain.model.CartItem
import com.golash.app.domain.model.Product
import com.golash.app.domain.model.ProductDetails
import com.golash.app.domain.model.ProductImage

fun CartItemEntity.toCartItem(): CartItem {
    return CartItem(
        product = Product(
            id = productId,
            name = name,
            shortDescription = productDescription,

            details = ProductDetails(
                size = size,
                careInstructions = careInstructions,
                materials = materials
            ),

            price = price,
            images = images.map { it.toDomain() }
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

        //ProductDetails
        /*----------------------------*/
        size = product.details.size,
        careInstructions = product.details.careInstructions,
        materials = product.details.materials,
        /*----------------------------*/

        images = product.images.map { it.toEntity() },
        quantity = quantity
    )
}

private fun ProductImageEntity.toDomain(): ProductImage {
    return ProductImage(
        url = url,
        type = ProductImage.ImageType.valueOf(type)
    )
}

private fun ProductImage.toEntity(): ProductImageEntity {
    return ProductImageEntity(
        url = url,
        type = type.name
    )
}