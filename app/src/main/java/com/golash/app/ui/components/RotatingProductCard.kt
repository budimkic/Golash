package com.golash.app.ui.components

import android.util.Log
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

                    val containerWidth = 240.dp
                    val aspectRatio = 1280f / 960f
                    val containerHeight = containerWidth * aspectRatio
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.size(width = containerWidth, height = containerHeight),
                        colors = CardDefaults.cardColors(containerColor = WarmSand)
                    ) {
                        if (animatedProduct.primaryImage?.type?.name == "RESOURCE") {
                            val resourceId = animatedProduct.primaryImage?.url?.toIntOrNull()
                            resourceId?.let { id ->
                                Image(
                                    painterResource(id = id),
                                    contentDescription = "Product image",
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                    // fit entire image, no cropping
                                )
                            } ?: Log.e(
                                "RotatingProductCard",
                                "Invalid resource ID for product image"
                            )
                        } else if (animatedProduct.primaryImage?.type?.name == "REMOTE") {
                            AsyncImage(
                                model = animatedProduct.primaryImage?.url,
                                contentDescription = "Product image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Fit // keep full image visible
                            )
                        }

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


