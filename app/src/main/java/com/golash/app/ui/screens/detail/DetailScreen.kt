package com.golash.app.ui.screens.detail

import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.ViewConfiguration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.golash.app.R
import com.golash.app.domain.model.Product
import com.golash.app.ui.components.AnimatedErrorState
import com.golash.app.ui.screens.cart.CartViewModel
import com.golash.app.ui.theme.CrimsonText
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.absoluteValue


private const val MIN_ALPHA = 0.5f
private const val MAX_ALPHA = 1f
private const val PAGER_ASPECT_RATIO = 1f

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val addToCartResult by detailViewModel.addToCartResult.collectAsStateWithLifecycle(initialValue = null)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(addToCartResult) {
        addToCartResult?.let { result ->
            when (result) {
                is AddToCartResult.Success -> {
                    snackbarHostState.showSnackbar("Added to cart! :)")
                }

                is AddToCartResult.Error -> {
                    Log.e("DetailScreen", result.message)
                    snackbarHostState.showSnackbar("Whoops, try again!")
                }

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData -> CustomSnackbar(snackbarData) }
            )
        },
        containerColor = Linen
    ) { paddingValues ->
        when (val state = detailUiState) {
            is DetailUiState.Success -> {
                DetailContent(
                    modifier = Modifier.padding(paddingValues),
                    product = state.product,
                    action = { action -> detailViewModel.onAction(action) },
                    snackbarHostState = snackbarHostState
                )
            }

            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DetailUiState.Error -> {
                AnimatedErrorState()
            }
        }
    }
}

@Composable
private fun DetailContent(
    modifier: Modifier = Modifier,
    product: Product,
    action: (Action) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val pagerState = rememberPagerState(pageCount = { product.images.size })
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var selectedSize by remember(product.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(Modifier.clickable { showDescriptionDialog = true }) {
                    Text(
                        text = product.name,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepBark,
                        fontFamily = Marcellus,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.sp
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show full shortDescription",
                        tint = DarkChestnut,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(1.dp)
                        .background(DeepBark.copy(alpha = 0.3f))
                        .padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDescriptionDialog = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = product.shortDescription,
                        modifier = Modifier
                            .weight(1f, fill = true)
                            .wrapContentHeight(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = DeepBark,
                        fontFamily = CrimsonText,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.1.sp,
                    )
                }

                if (showDescriptionDialog) {
                    CustomDialog(
                        product,
                        selectedSize = selectedSize,
                        onSizeSelected = { selectedSize = it },
                        onDismissRequest = { showDescriptionDialog = false })
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        CartFooter(
            product = product,
            selectedSize = selectedSize,
            onRequireSize = {
                // TODO add toast
               // scope.launch { snackbarHostState.showSnackbar("Pick a size!", duration = SnackbarDuration.Short) }

                showDescriptionDialog = true
            },
            onAddToCart =
                { size -> action(Action.OnAddToCart(product, size)) }
        )
    }
}

@Composable
private fun CartFooter(
    product: Product,
    selectedSize: String?,
    onRequireSize: () -> Unit,
    onAddToCart: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pulseScale = remember { Animatable(1f) }
    var showCurvedText by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .background(
                    color = DarkChestnut,
                    shape = NotchedLabelShape(notchRadius = 14f),
                )
        ) {
            Text(
                modifier = Modifier.padding(
                    10.dp
                ),
                text = "${product.price.toInt()} RSD",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Ivory,
                fontFamily = Marcellus
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(pulseScale.value)
                .background(DarkChestnut, CircleShape)
                .pointerInput(product, selectedSize) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        var longPressed = false

                        val longPressJob = scope.launch {
                            delay(ViewConfiguration.getLongPressTimeout().toLong())
                            longPressed = true
                            showCurvedText = true
                        }

                        // Start pulse immediately
                        scope.launch { pulseScale.animateTo(1.15f, tween(100)) }

                        val up = waitForUpOrCancellation()
                        longPressJob.cancel()

                        // Animate back to normal
                        scope.launch { pulseScale.animateTo(1f, tween(300)) }

                        if (up != null && !longPressed) {
                            val size = selectedSize
                            if (size != null) {
                                onAddToCart(size)
                            } else {
                                onRequireSize()
                            }
                        }

                        showCurvedText = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (showCurvedText) {
                CurvedText(
                    text = "ADD TO CART",
                    buttonRadius = 24f,
                    alpha = 1f,
                    modifier = Modifier.size(90.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to cart",
                tint = Ivory,
                modifier = Modifier.size(24.dp)
            )
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

    Canvas(modifier = modifier.offset(y = (-4).dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Convert button radius from dp to px
        val radiusPx = with(density) { (buttonRadius).dp.toPx() }

        val path = Path().apply {
            val startAngle = PI.toFloat() * 1.1f // Start angle (slightly above left)
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
                textSize = with(density) { 6.sp.toPx() }
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
                letterSpacing = 0.05f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            paint.alpha = (alpha * 255).toInt()
            drawTextOnPath(text, path.asAndroidPath(), 0f, 0f, paint)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Linen),
            contentAlignment = Alignment.Center
        ) {
            if (product.images[page].type?.name == "RESOURCE") {
                val resourceId = product.images[page].url?.toIntOrNull()
                resourceId?.let { id ->
                    Image(
                        painterResource(id = id),
                        contentDescription = "Product image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                } ?: Log.e(
                    "RotatingProductCard",
                    "Invalid resource ID for product image"
                )
            } else if (product.images[page].type?.name == "REMOTE") {
                AsyncImage(
                    model = product.images[page],
                    contentDescription = "Product image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = DeepOlive,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Ivory
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = snackbarData.visuals.message,
                color = Ivory,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CustomDialog(
    product: Product,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(Linen),
            color = Linen,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepBark,
                    fontFamily = Marcellus,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = product.shortDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DeepBark.copy(alpha = 0.9f),
                    fontFamily = CrimsonText,
                    lineHeight = 24.sp
                )

                VineDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row {
                    Text(
                        text = "Materials: ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepBark.copy(alpha = 0.9f),
                        fontFamily = CrimsonText,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )

                    Text(
                        text = product.details.materials,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepBark.copy(alpha = 0.9f),
                        fontFamily = CrimsonText,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "Care: ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepBark.copy(alpha = 0.9f),
                        fontFamily = CrimsonText,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )

                    Text(
                        text = product.details.careInstructions,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepBark.copy(alpha = 0.9f),
                        fontFamily = CrimsonText,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Size:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepBark.copy(alpha = 0.9f),
                        fontFamily = CrimsonText,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )

                    product.details.sizes.forEach { size ->
                        val isSelected = size == selectedSize
                        Box(
                            modifier = Modifier
                                .clickable { onSizeSelected(size) }
                                .border(width = 1.dp, color = DarkChestnut.copy(0.6f))
                                .background(color = if (!isSelected) Linen else DeepBark.copy(alpha = 0.9f))
                                .padding(4.dp)) {
                            Text(
                                text = size,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (!isSelected) DeepBark.copy(alpha = 0.9f) else Ivory,
                                fontFamily = CrimsonText,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                if (selectedSize == null) {
                    Text(
                        text = "Please select a size",
                        color = DarkChestnut,
                        fontFamily = CrimsonText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkChestnut
                    )
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        color = Ivory,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun VineDivider(
    modifier: Modifier = Modifier,
    vineColor: Color = DeepOlive,
    leafCount: Int = 5
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val width = size.width
        val height = size.height
        val midY = height / 2

        // The wavy stem, drawn as a smooth S-curve repeated across the width
        val stemPath = Path().apply {
            moveTo(0f, midY)
            val segments = 4
            val segmentWidth = width / segments
            for (i in 0 until segments) {
                val startX = i * segmentWidth
                val endX = startX + segmentWidth
                val controlY = if (i % 2 == 0) midY - 8.dp.toPx() else midY + 8.dp.toPx()
                quadraticBezierTo(
                    startX + segmentWidth / 2, controlY,
                    endX, midY
                )
            }
        }

        drawPath(
            path = stemPath,
            color = vineColor,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Scatter small leaf shapes along the stem
        repeat(leafCount) { i ->
            val t = (i + 0.5f) / leafCount
            val x = t * width
            // Alternate leaves above/below the stem, matching the wave direction
            val leafUp = (i % 2 == 0)
            val leafY = if (leafUp) midY - 6.dp.toPx() else midY + 6.dp.toPx()

            rotate(degrees = if (leafUp) -30f else 30f, pivot = Offset(x, leafY)) {
                drawOval(
                    color = vineColor.copy(alpha = 0.85f),
                    topLeft = Offset(x - 4.dp.toPx(), leafY - 2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 4.dp.toPx())
                )
            }
        }

        // A tiny leaf/bud at the very center, slightly larger, as a focal point
        /*  drawOval(
              color = vineColor,
              topLeft = Offset(width / 2 - 5.dp.toPx(), midY - 6.dp.toPx()),
              size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 6.dp.toPx())
          )*/
    }
}
