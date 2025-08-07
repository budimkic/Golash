package com.golash.app.ui.screens.detail

import android.graphics.Paint
import android.graphics.Typeface
import android.view.ViewConfiguration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.golash.app.data.model.Product
import com.golash.app.ui.screens.cart.AddToCartResult
import com.golash.app.ui.screens.cart.CartViewModel
import com.golash.app.ui.theme.CrimsonText
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.Inter
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.absoluteValue

private const val MIN_ALPHA = 0.5f
private const val MAX_ALPHA = 1f
private const val PAGER_ASPECT_RATIO = 1f


@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val addToCartResult by cartViewModel.addToCartResult.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(addToCartResult) {
        addToCartResult?.let { result ->

            when (result) {
                is AddToCartResult.Success -> {
                    scope.launch { snackbarHostState.showSnackbar("Added to cart! :)") }
                }

                is AddToCartResult.Error -> {
                    scope.launch { snackbarHostState.showSnackbar("Whoops, try again!") }
                }

                else -> {}
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        when (val state = detailUiState) {
            is DetailUiState.Success ->
                DetailContent(
                    modifier = Modifier.padding(paddingValues),
                    product = state.product,
                    onAddToCart = { product: Product -> cartViewModel.addToCart(product) })

            is DetailUiState.Loading -> {}
            is DetailUiState.Error -> {}
        }
    }
}

@Composable
private fun DetailContent(modifier: Modifier = Modifier, product: Product, onAddToCart: (Product) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { product.imageUrls.size })
    var showCurvedText by remember {mutableStateOf(false)}
    val textAlpha by animateFloatAsState(
        targetValue = if (showCurvedText) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "textAlpha"
    )

    val scope = rememberCoroutineScope()
    val pulseScale = remember { androidx.compose.animation.core.Animatable(1f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Linen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(PAGER_ASPECT_RATIO)
            ) {
                HorizontalPager(
                    state = pagerState, userScrollEnabled = true, modifier = Modifier.fillMaxSize()
                ) { page ->
                    PagerImageItem(product, pagerState, page)
                }
                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp), horizontalArrangement = Arrangement.Center
                ) {
                    val activeColor = Ivory
                    val inactiveColor = Ivory.copy(0.5f)
                    if (pagerState.pageCount > 1) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color =
                                if (pagerState.currentPage == iteration) activeColor else inactiveColor

                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(DeepBark.copy(alpha = 0.4f))
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Product Information Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Product Name - Clean and Bold
                Text(
                    text = product.name,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBark,
                    fontFamily = Marcellus,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.sp
                )

                // Simple underline
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(1.dp)
                        .background(DeepBark.copy(alpha = 0.3f))
                        .padding(bottom = 16.dp)
                )

                // Product Description - Clean and Readable
                //TODO change Font
                Text(
                    text = product.description,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = DeepBark,
                    fontFamily = CrimsonText,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.1.sp
                )
            }

        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.background(
                    color = DarkChestnut,
                    shape = NotchedLabelShape(notchRadius = 14f),
                )
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "${product.price.toInt()} RSD",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ivory,
                    fontFamily = Marcellus
                )
            }

            Box(modifier = Modifier.size(90.dp)) {
                if (textAlpha > 0f) {
                    CurvedText(
                        text = "ADD TO CART",
                        buttonRadius = 22.5f,
                        alpha = textAlpha,
                        modifier = Modifier.size(90.dp)
                    )
                }


                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .scale(pulseScale.value)
                        .align(Alignment.Center)
                        .background(color = DarkChestnut, shape = CircleShape)
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                var longPressJob: Job? = null
                                var longPressed = false

                                // Start pulse on press
                                scope.launch { pulseScale.animateTo(1.15f, tween(100)) }

                                longPressJob = scope.launch {
                                    delay(ViewConfiguration.getLongPressTimeout().toLong())
                                    longPressed = true
                                    showCurvedText = true
                                }

                                val up = waitForUpOrCancellation()
                                longPressJob.cancel()

                                if (up == null) {
                                    // Gesture was cancelled, animate back
                                    scope.launch { pulseScale.animateTo(1f, tween(300)) }
                                    showCurvedText = false
                                    return@awaitEachGesture
                                }

                                if (!longPressed) {
                                    // TAP: quick pulse, add to cart
                                    scope.launch { pulseScale.animateTo(1f, tween(300)) }
                                    onAddToCart(product)
                                    showCurvedText = false
                                } else {
                                    // LONG PRESS: animate back, keep text visible until release
                                    scope.launch { pulseScale.animateTo(1f, tween(300)) }
                                    showCurvedText = false
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Cart",
                        tint = Ivory,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurvedText(
    text: String,
    buttonRadius: Float,
    alpha: Float = 1f,
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current
    val textColor = DarkChestnut

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Convert button radius from dp to px
        val radiusPx = with(density) { (buttonRadius + 15).dp.toPx() }

        val path = Path().apply {
            val startAngle = PI.toFloat() * 0.8f // Start angle (slightly above left)
            val sweepAngle = PI.toFloat() * 0.8f // Sweep angle (how much of the circle to cover)

            // Add arc to path
            addArc(
                oval = Rect(
                    left = centerX - radiusPx,
                    top = centerY - radiusPx,
                    right = centerX + radiusPx,
                    bottom = centerY + radiusPx
                ),
                startAngleDegrees = startAngle * 180f / PI.toFloat(),
                sweepAngleDegrees = sweepAngle * 180f / PI.toFloat()
            )
        }

        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                color = textColor.toArgb()
                textSize = with(density) { 10.sp.toPx() }
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true

                letterSpacing = 0.05f
            }
            paint.alpha = (alpha * 255).toInt()
            drawTextOnPath(text, path.asAndroidPath(), 0f, -8f, paint)
        }

    }

}

private class NotchedLabelShape(private val notchRadius: Float) : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): androidx.compose.ui.graphics.Outline {
        return androidx.compose.ui.graphics.Outline.Generic(
            // Create the main rectangle path
            Path().apply {
                // Start with the full rectangle
                val rect = Path().apply {
                    addRect(Rect(0f, 0f, size.width, size.height))
                }

                // Create notches in each corner
                val notches = Path().apply {
                    // Top-left notch
                    addOval(
                        Rect(
                            left = -notchRadius,
                            top = -notchRadius,
                            right = notchRadius,
                            bottom = notchRadius
                        )
                    )
                    // Top-right notch
                    addOval(
                        Rect(
                            left = size.width - notchRadius,
                            top = -notchRadius,
                            right = size.width + notchRadius,
                            bottom = notchRadius
                        )
                    )
                    // Bottom-left notch
                    addOval(
                        Rect(
                            left = -notchRadius,
                            top = size.height - notchRadius,
                            right = notchRadius,
                            bottom = size.height + notchRadius
                        )
                    )
                    // Bottom-right notch
                    addOval(
                        Rect(
                            left = size.width - notchRadius,
                            top = size.height - notchRadius,
                            right = size.width + notchRadius,
                            bottom = size.height + notchRadius
                        )
                    )
                }

                // Subtract the notches from the rectangle
                op(rect, notches, PathOperation.Difference)
            })
    }
}

@Composable
private fun PagerImageItem(product: Product, pagerState: PagerState, page: Int) {
    val calculateAlphaFraction: (Float) -> Float = { 1f - it.coerceIn(0f, 1f) }
    Card(
        Modifier
            .fillMaxWidth()
            .graphicsLayer {
                val pageOffset =
                    ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                alpha = lerp(
                    start = MIN_ALPHA,
                    stop = MAX_ALPHA,
                    fraction = calculateAlphaFraction(pageOffset)
                )
            }, shape = RectangleShape
    ) {
        AsyncImage(
            model = product.imageUrls[page],
            contentDescription = "",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

