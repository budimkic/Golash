package com.golash.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
    intervalMilis: Long = 7500L,
    modifier: Modifier = Modifier
) {

    var visible by remember { mutableStateOf(false) }

    if (products.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        visible = true

        while (true) {
            delay(intervalMilis)
            currentIndex = (currentIndex + 1) % products.size
        }


    }

    val product = products[currentIndex]

    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(12.dp).shadow(8.dp, RoundedCornerShape(24.dp))
        ,
        colors = CardDefaults.cardColors(containerColor = WarmSand),
        //elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
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
                enter = fadeIn(animationSpec = tween(durationMillis = 3000))
            ) {
                AnimatedContent(
                    targetState = product,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(
                                durationMillis = 800,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 800,
                                easing = FastOutSlowInEasing
                            )
                        )) togetherWith

                                (slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeOut(
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = FastOutSlowInEasing
                                    )
                                ))
                    }
                )
                { animatedProduct ->
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        // elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.size(290.dp)//.border(3.dp, DeepOlive, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    ) {
                        AsyncImage(
                            model = animatedProduct.imageUrl,
                            contentDescription = "My image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 3000))
            ) {
                AnimatedContent(
                    targetState = product.name,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(
                                durationMillis = 800,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 800,
                                easing = FastOutSlowInEasing
                            )
                        )) togetherWith

                                (slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeOut(
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = FastOutSlowInEasing
                                    )
                                ))
                    }
                )

                { animatedName ->

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


        //  Spacer(modifier = Modifier.height(4.dp))
        /*    Text(
                text = "${products[currentIndex].price} RSD",
                fontFamily = Marcellus,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Linen

            )*/
    }
}


