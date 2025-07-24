package com.golash.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.golash.app.data.model.Product
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.WarmSand
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RotatingProductCard(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    intervalMillis: Long = 5000L,
    fadeInEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    if (products.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    // Every [intervalMillis], increment the index to show the next product
    // The % operator ensures we loop back to 0 when we reach the end of the list
    LaunchedEffect(Unit) {
        visible = true
        while (true) {
            delay(intervalMillis)
            currentIndex = (currentIndex + 1) % products.size
        }
    }
    val product = products[currentIndex]

    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(12.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { onProductClick(product.id) },
        colors = CardDefaults.cardColors(containerColor = WarmSand),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = if (fadeInEnabled) fadeIn(tween(3000)) else fadeIn(tween(0))
            ) {
                AnimatedContent(
                    targetState = product,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(800, easing = FastOutSlowInEasing)
                        ) + fadeIn(tween(800, easing = FastOutSlowInEasing))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                                ) + fadeOut(tween(800, easing = FastOutSlowInEasing)))
                    }
                ) { animatedProduct ->
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.size(290.dp)
                    ) {
                        AsyncImage(
                            model = animatedProduct.imageUrl,
                            contentDescription = "Product image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = visible,
                enter = if (fadeInEnabled) fadeIn(tween(3000)) else fadeIn(tween(0))
            ) {
                AnimatedContent(
                    targetState = product.name,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(800, easing = FastOutSlowInEasing)
                        ) + fadeIn(tween(800, easing = FastOutSlowInEasing))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                                ) + fadeOut(tween(800, easing = FastOutSlowInEasing)))
                    }
                ) { animatedName ->
                    Text(
                        text = animatedName,
                        fontFamily = Marcellus,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = EarthBrown
                    )
                }
            }
        }
    }
}


