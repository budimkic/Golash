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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.RawCotton

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
            CartContent(cart = cart, onRemove = {})
        }

        is CartState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Something went wrong.", color = Color.Red)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { cartViewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }

}

@Composable
private fun CartContent(cart: Cart, onRemove: (String) -> Unit) {
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
                        "Your cart is empty.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Gray
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
                items(cart.totalItems) { index ->
                    val cartItem = cart.items[index]
                    CartItemRow(
                        cartItem = cartItem,
                        onRemove = {}, onQuantityChange = {})
                }
            }
        }
        /* Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .height(2.dp)
                 .background(Color.Black)
         )*/
        Spacer(Modifier.height(16.dp))
        if (cart.items.isNotEmpty()) {
            /*Row(Modifier.padding(bottom = 30.dp, start = 20.dp)) {
                Text(
                    text =
                        "Total: ${"%.2f".format(cart.totalPrice)} RSD",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Marcellus
                    ),
                    color = Color.Black
                )
*/

            CartFooter(cart.totalPrice) { }
            /*  Text(
                  text =
                      "Total: ${"%.2f".format(cart.totalPrice)} RSD",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.Black
              )*/
        }
    }

    /*   Spacer(Modifier.height(16.dp))
       Button(
           onClick = {},
           modifier = Modifier
               .width(300.dp)
               .clip(shape = RoundedCornerShape(100.dp))
               .align(Alignment.CenterHorizontally)
               .height(56.dp)
               .padding(10.dp),
           colors = ButtonColors(Color.Black, Color.White, Color.White, Color.Gray),
           enabled = cart.items.isNotEmpty()
       ) { Text("ORDER") }*/
}


@Composable
private fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit, onQuantityChange: (Int) -> Unit) {

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
                AsyncImage(
                    model = cartItem.product.imageUrl,
                    contentDescription = cartItem.product.name,
                    contentScale = ContentScale.Fit, // Fill height & keep aspect ratio
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp)),  // Fill vertical space
                )
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
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        enabled = cartItem.quantity > 1
                    ) {
                        Text(
                            "-",
                            fontWeight = FontWeight.Bold,
                            color = DeepBark,
                            fontFamily = Marcellus
                        )
                    }
                    Text("${cartItem.quantity}", Modifier, color = DeepBark, fontFamily = Marcellus)
                    IconButton(onClick = { onQuantityChange(cartItem.quantity + 1) }) {
                        Text(
                            "+",
                            fontWeight = FontWeight.Bold,
                            color = DeepBark,
                            fontFamily = Marcellus
                        )
                    }
                }
            }
            /* IconButton(
                 onClick = onRemove,
                 modifier = Modifier.background(
                     color = Oak.copy(alpha = 0.2f),
                     shape = RoundedCornerShape(50)
                 )
             ) {
                 Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DeepBark)
             }*/

        }
    }

}

@Composable
private fun CartFooter(total: Double, onCheckout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.shadow(8.dp)
            //  .background(Oak)
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
                        contentDescription = "Checkout",
                        tint = Ivory,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }
    }

}

