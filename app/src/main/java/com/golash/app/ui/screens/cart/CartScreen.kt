package com.golash.app.ui.screens.cart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.golash.app.R
import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.RawCotton
import androidx.compose.foundation.lazy.items

@Composable
fun CartScreen(cartViewModel: CartViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { cartViewModel.refresh() }
    val cartState by cartViewModel.cartState.collectAsState()

    when (cartState) {
        is CartState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is CartState.Success -> {
            val cart = (cartState as CartState.Success).cart
            CartContent(cart = cart, onRemove = {}, cartViewModel)
        }

        is CartState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.default_error_msg), color = Color.Red)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { cartViewModel.refresh() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }

}

@Composable
private fun CartContent(cart: Cart, onRemove: (String) -> Unit, cartViewModel: CartViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
    ) {
        if (cart.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.fill_your_cart),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }


            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .weight(1f)
            ) {
                items(cart.items) { cartItem ->
                    CartItemRow(
                        cartItem = cartItem,
                        onIncreaseQuantity = { cartViewModel.increaseQuantity(cartItem.product) },
                        onDecreaseQuantity = { cartViewModel.decreaseQuantity(cartItem.product) })
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (cart.items.isNotEmpty()) {
            CartFooter(cart.totalPrice) { }
        }
    }
}


@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Linen)
            .padding(12.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
            ) {
                if (cartItem.product.primaryImage?.type?.name == stringResource(R.string.resource)) {
                    val resourceId = cartItem.product.primaryImage?.url?.toIntOrNull()

                    resourceId?.let { id ->
                        Image(
                            painterResource(id = id),
                            contentDescription = stringResource(R.string.product_image),
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Fit
                            // fit entire image, no cropping
                        )
                    } ?: Log.e(
                        "CartScreen",
                        stringResource(R.string.error_invalid_resource)
                    )
                } else if (cartItem.product.primaryImage?.type?.name == stringResource(R.string.remote)) {
                    AsyncImage(
                        model = cartItem.product.primaryImage,
                        contentDescription = cartItem.product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp)),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    cartItem.product.name,
                    fontFamily = Marcellus,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = DeepBark,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${"%.2f".format(cartItem.product.price)} RSD",
                    color = DeepBark,
                    fontWeight = FontWeight.SemiBold, fontFamily = Marcellus, fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onDecreaseQuantity() }
                    ) {
                        Text(
                            "-",
                            fontWeight = FontWeight.Bold,
                            color = DeepBark,
                            fontFamily = Marcellus
                        )
                    }
                    Text("${cartItem.quantity}", Modifier, color = DeepBark, fontFamily = Marcellus)
                    IconButton(onClick = { onIncreaseQuantity() },
                        enabled = cartItem.quantity < 5
                    ) {
                        Text(
                            "+",
                            fontWeight = FontWeight.Bold,
                            color = DeepBark,
                            fontFamily = Marcellus
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun CartFooter(total: Double, onCheckout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total: ${"%.2f".format(total)} RSD",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = Marcellus,
                fontSize = 20.sp,
                color = DeepBark
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = EarthBrown,
                        shape = CircleShape
                    )
                    .clip(
                        CircleShape
                    )
            ) {
                IconButton(onClick = { onCheckout }, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = stringResource(R.string.checkout),
                        tint = Ivory,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }
    }

}

