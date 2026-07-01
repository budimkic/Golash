package com.golash.app.ui.screens.cart

import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.golash.app.R
import com.golash.app.domain.model.CartItem
import com.golash.app.ui.components.AnimatedErrorState
import com.golash.app.ui.components.OakTreeLoader
import com.golash.app.ui.theme.AbrasiveRed
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Gold
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.WarmSand
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private const val TAG = "CartScreen"

enum class CartUiPhase { LOADING, SUCCESS, ERROR, EMPTY }

@Composable
fun CartScreen(cartViewModel: CartViewModel = hiltViewModel()) {
    val cartState by cartViewModel.cartState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        cartViewModel.cartActionErrorState.collect { errorState ->
            if (errorState is CartState.CartActionError) {
                snackbarHostState.showSnackbar("Whoops, try again!")
            }
        }
    }

    CartContent(cartState, onAction = { action -> cartViewModel.onAction(action) })
}

@Composable
private fun CartContent(
    cartState: CartState,
    onAction: (Action) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Uses uiPhase to prevent full-screen re-animations on granular data changes (price/quantity updates, etc.)
    // AnimatedContent will only trigger when transitioning between macro UI states (SUCCESS -> EMPTY, etc.)
    val uiPhase = remember(cartState) {
        when (cartState) {
            is CartState.Loading -> CartUiPhase.LOADING
            is CartState.LoadCartError -> CartUiPhase.ERROR
            is CartState.Success -> {
                if (cartState.cart.items.isEmpty()) CartUiPhase.EMPTY else CartUiPhase.SUCCESS
            }

            else -> CartUiPhase.LOADING
        }
    }

    AnimatedContent(
        targetState = uiPhase,
        transitionSpec = {
            fadeIn(animationSpec = tween(1000, easing = EaseInOut)) togetherWith
                    fadeOut(animationSpec = tween(400, easing = EaseInOut))
        },
        label = "cart_phase_animation",
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
    ) { phase ->
        when (phase) {
            CartUiPhase.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    OakTreeLoader(modifier = Modifier.wrapContentSize())
                }
            }

            CartUiPhase.EMPTY -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                }
            }

            CartUiPhase.SUCCESS -> {
                val successState = cartState as? CartState.Success
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (successState != null) {

                            items(successState.cart.items, key = { it -> it.product.id}) { cartItem ->
                                Log.d(TAG, cartItem.product.id)
                                CartItemRow(
                                    cartItem = cartItem,
                                    onIncreaseQuantity = {
                                        onAction(
                                            Action.OnIncreaseQuantity(
                                                cartItem.product
                                            )
                                        )
                                    },
                                    onDecreaseQuantity = {
                                        onAction(
                                            Action.OnDecreaseQuantity(
                                                cartItem.product
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                    if (successState != null) {
                        CartFooter(successState.cart.totalPrice,) { showDialog = true }
                    }
                }

                if (showDialog) {
                    CustomDialog(
                        onDismissRequest = { showDialog = false },
                        value = "",
                        onValueChange = { })
                }
            }

            CartUiPhase.ERROR -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AnimatedErrorState()
                }
            }
        }
    }
}


@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = WarmSand,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val containerHeight = 90.dp
            val aspectRatio = 960f / 1280f
            val containerWidth = containerHeight * aspectRatio

            Spacer(Modifier.width(12.dp))

            Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(width = containerWidth, height = containerHeight),
                    colors = CardDefaults.cardColors(containerColor = WarmSand)
                ) {
                    if (cartItem.product.primaryImage?.type?.name == stringResource(R.string.resource)) {
                        val resourceId = cartItem.product.primaryImage?.url?.toIntOrNull()
                        resourceId?.let { id ->
                            Image(
                                painter = painterResource(id = id),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else if (cartItem.product.primaryImage?.type?.name == stringResource(R.string.remote)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(cartItem.product.primaryImage?.url)
                                .crossfade(300)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
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
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDecreaseQuantity()
                        }
                    ) {
                        Text(
                            "-",
                            fontWeight = FontWeight.Bold,
                            color = DeepBark,
                            fontFamily = Marcellus
                        )
                    }
                    Text("${cartItem.quantity}", Modifier, color = DeepBark, fontFamily = Marcellus)
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onIncreaseQuantity()
                    }
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
private fun CustomDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)

    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Linen),
            color = Linen,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.padding(24.dp)) {
                TextField(value = value, onValueChange = onValueChange, label = { Text("Name") })
            }

        }
    }
}

@Composable
private fun CartFooter(total: Double, onCheckout: () -> Unit) {
    // Checks if the price went up or down
    var previousTotal by remember { mutableDoubleStateOf(total) }

    // Tracks parameters for the text gradient sweep
    var activeHighlightColor by remember { mutableStateOf(Gold) }
    var gradientSweepDirection by remember { mutableStateOf(true) } // true = left2right, false = right2left
    val colorProgress = remember { Animatable(0f) }

    // Button animation parameters
    val buttonScale = remember { Animatable(1f) }
    val buttonColor = remember { Animatable(EarthBrown) }
    val sparkleProgress = remember { Animatable(1f) }

    LaunchedEffect(total) {
        // Prevents animation on first open
        if (total == previousTotal) return@LaunchedEffect

        if (total > previousTotal) {
            // ─────────────────────────────────────────────────────
            // ACTION: ITEM ADDED — Starburst & Button Pop
            // ─────────────────────────────────────────────────────
            activeHighlightColor = Gold
            gradientSweepDirection = true

            // 0f -> 1f over 700ms EaseInOut
            launch {
                colorProgress.snapTo(0f)
                colorProgress.animateTo(1f, tween(700, easing = EaseInOut))
            }
            // 0f -> 1f over 500ms LinearOutSlowInEasing
            launch {
                sparkleProgress.snapTo(0f)
                sparkleProgress.animateTo(1f, tween(500, easing = LinearOutSlowInEasing))
            }
            // Expand button to 112% @ 100ms mark, shrink button to 98% @ 220ms mark, finish @ 300ms to 100%
            launch {
                buttonScale.animateTo(1f, keyframes {
                    durationMillis = 300; 1.12f at 100; 0.98f at 220
                })
            }

        } else {
            // ─────────────────────────────────────────────────────
            // ACTION: ITEM REMOVED — Negative Pulse & Color Drain
            // ─────────────────────────────────────────────────────
            activeHighlightColor = AbrasiveRed
            gradientSweepDirection = false

            // 0f -> 1f over 600ms EaseInOut
            launch {
                colorProgress.snapTo(0f)
                colorProgress.animateTo(1f, tween(600, easing = EaseInOut))
            }

            // Shrink button to 85% over 150ms FastOutLinearInEasing, recover size over 400ms LinearOutSlowInEasing
            launch {
                buttonScale.snapTo(1f)
                buttonScale.animateTo(
                    targetValue = 0.85f,
                    animationSpec = tween(150, easing = FastOutLinearInEasing)
                )
                buttonScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(400, easing = LinearOutSlowInEasing)
                )
            }

            // Color morph to Gray over 150ms FastOutLinearInEasing, back to EarthBrown over 400ms LinearOutSlowInEasing
            launch {
                buttonColor.animateTo(
                    targetValue = Gray,
                    animationSpec = tween(150, easing = FastOutLinearInEasing)
                )
                buttonColor.animateTo(
                    targetValue = EarthBrown,
                    animationSpec = tween(400, easing = LinearOutSlowInEasing)
                )
            }
        }

        // Update baseline price evaluation stats
        previousTotal = total
    }

    val whooshBrush = remember(colorProgress.value, activeHighlightColor, gradientSweepDirection) {
        object : ShaderBrush() {
            override fun createShader(size: androidx.compose.ui.geometry.Size): androidx.compose.ui.graphics.Shader {

                val width = size.width
                val leftBoundary = -width * 0.5f
                val rightBoundary = width * 1.5f

                val currentX = if (gradientSweepDirection)
                    leftBoundary + (rightBoundary - leftBoundary) * colorProgress.value
                else
                    rightBoundary - (rightBoundary - leftBoundary) * colorProgress.value

                val waveWidth = width * 0.35f
                val stop1 = ((currentX - waveWidth) / width).coerceIn(0f, 1f)
                val stop2 = (currentX / width).coerceIn(0f, 1f)
                val stop3 = ((currentX + waveWidth) / width).coerceIn(0f, 1f)

                return LinearGradientShader(
                    colors = listOf(DeepBark, DeepBark, activeHighlightColor, DeepBark, DeepBark),
                    colorStops = listOf(0f, stop1, stop2, stop3, 1f),
                    from = Offset.Zero,
                    to = Offset(width, 0f)
                )
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total: ${"%.2f".format(total)} RSD",
                style = MaterialTheme.typography.titleLarge.copy(brush = whooshBrush),
                fontWeight = FontWeight.Bold,
                fontFamily = Marcellus,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(buttonScale.value)
                        .drawBehind {
                            val p = sparkleProgress.value

                            // Only run the calculations while the animation is actively playing
                            if (p > 0f && p < 1f) {
                                // Fast fade-in: reaches 100% visibility/size halfway through the animation (at p = 0.5f)
                                val appearProgress = (p * 2f).coerceIn(0f, 1f)
                                // Delayed fade-out: stays 0 until p = 0.4f, then scales smoothly from 0 to 1 over the remaining 60% of the timeline
                                val fadeProgress = ((p - 0.4f) / 0.6f).coerceIn(0f, 1f)
                                // Opacity curve lifecycle: 0*1=0 (start); 1*1=1 (peak); 1*0=0 (end)
                                val sparkleAlpha = appearProgress * (1f - fadeProgress)

                                val maxSparkBurstDistance = 24.dp.toPx()
                                val singleSparkWidth = 1.5f.dp.toPx()
                                val sparkBurstAngles = listOf(0.0, 90.0, 180.0, 270.0)

                                val buttonRadius = size.width / 2f
                                val buttonCenter = Offset(buttonRadius, size.height / 2f)

                                sparkBurstAngles.forEach { angleDeg ->
                                    // Computers do not understand standard 360-degree circles inside trig functions, they expect angles in Radians (where 180-degrees = Pi)
                                    val rad = Math.toRadians(angleDeg)
                                    // Start particle spawn at radius; climb to maxSparkBurstDistance
                                    val totalSparkBurstDistance = (buttonRadius) + (p * maxSparkBurstDistance)

                                    val cx = buttonCenter.x + (cos(rad) * totalSparkBurstDistance).toFloat()
                                    val cy = buttonCenter.y + (sin(rad) * totalSparkBurstDistance).toFloat()

                                    val rayLen =
                                        5.dp.toPx() * appearProgress * (1f - fadeProgress * 0.2f)
                                    val diagLen = rayLen * 0.45f


                                    drawLine(
                                        Gold.copy(alpha = sparkleAlpha),
                                        Offset(cx - rayLen, cy),
                                        Offset(cx + rayLen, cy),
                                        singleSparkWidth,
                                        cap = StrokeCap.Round
                                    )
                                    drawLine(
                                        Gold.copy(alpha = sparkleAlpha),
                                        Offset(cx, cy - rayLen),
                                        Offset(cx, cy + rayLen),
                                        singleSparkWidth,
                                        cap = StrokeCap.Round
                                    )

                                    // Short diagonal rays
                                    drawLine(
                                        Gold.copy(alpha = sparkleAlpha * 0.6f),
                                        Offset(cx - diagLen, cy - diagLen),
                                        Offset(cx + diagLen, cy + diagLen),
                                        singleSparkWidth * 0.75f,
                                        cap = StrokeCap.Round
                                    )
                                    drawLine(
                                        Gold.copy(alpha = sparkleAlpha * 0.6f),
                                        Offset(cx - diagLen, cy + diagLen),
                                        Offset(cx + diagLen, cy - diagLen),
                                        singleSparkWidth * 0.75f,
                                        cap = StrokeCap.Round
                                    )

                                    // Bright center dot
                                    drawCircle(
                                        Gold.copy(alpha = sparkleAlpha),
                                        singleSparkWidth * 1.2f,
                                        center = Offset(cx, cy)
                                    )
                                }
                            }
                        }
                        // Animate between EarthBrown and Gray
                        .background(buttonColor.value, CircleShape)
                        .clip(CircleShape)
                ) {
                    IconButton(
                        onClick = { onCheckout() },
                        modifier = Modifier.fillMaxSize()
                    ) {
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
}