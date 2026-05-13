package com.golash.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golash.app.ui.theme.CanopyDark
import com.golash.app.ui.theme.CanopyMid
import com.golash.app.ui.theme.LeafOrange
import com.golash.app.ui.theme.LeafStem
import com.golash.app.ui.theme.TextEarth
import com.golash.app.ui.theme.TrunkBrown
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

@Composable
fun OakTreeLoader(modifier: Modifier = Modifier) {

    val swayTransition = rememberInfiniteTransition(label = "sway")
    val swayAngle by swayTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swayAngle"
    )

    val leafTransition = rememberInfiniteTransition(label = "leaf")
    val leafProgress by leafTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing)
        ),
        label = "leafProgress"
    )

    val dotTransition = rememberInfiniteTransition(label = "dots")
    val dotPhase by dotTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)

        ),
        label = "dotPhase"
    )

    val dots by remember {
        derivedStateOf {
            ".".repeat(floor(dotPhase).toInt().coerceIn(0, 3))
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Canvas(
            modifier = Modifier
                .size(100.dp, 140.dp)
                .graphicsLayer(clip = false)
        ) {
            drawTrunkAndCanopy(cx = size.width / 2f, swayAngle = swayAngle)
            drawFallingLeaf(cx = size.width / 2f, progress = leafProgress)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Loading",
                color = TextEarth,
                fontSize = 16.sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )

            Box(modifier = Modifier.width(12.dp)) {
                Text(
                    text = dots,
                    color = TextEarth,
                    fontSize = 16.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}

private fun DrawScope.drawTrunkAndCanopy(cx: Float, swayAngle: Float) {
    val trunkBaseY = size.height - 10.dp.toPx()
    val trunkH = 30.dp.toPx()
    val trunkW = 5.dp.toPx()

    rotate(degrees = swayAngle, pivot = Offset(cx, trunkBaseY)) {
        val trunk = Path().apply {
            moveTo(cx - trunkW / 2f, trunkBaseY)
            lineTo(cx - trunkW / 2f + 1.dp.toPx(), trunkBaseY - trunkH)
            lineTo(cx + trunkW / 2f - 1.dp.toPx(), trunkBaseY - trunkH)
            lineTo(cx + trunkW / 2f, trunkBaseY)
            close()
        }

        drawPath(trunk, TrunkBrown)
        val canopyTop = trunkBaseY - trunkH

        data class Layer(val yOffset: Float, val radius: Float, val color: Color)

        listOf(
            Layer(-55.dp.toPx(), 28.dp.toPx(), CanopyDark),
            Layer(-42.dp.toPx(), 32.dp.toPx(), CanopyMid),
            Layer(-24.dp.toPx(), 28.dp.toPx(), CanopyDark),
        ).forEach { layer ->

            drawCircle(
                color = layer.color,
                radius = layer.radius,
                center = Offset(cx, canopyTop + layer.yOffset)
            )
        }
    }
}

private fun DrawScope.drawFallingLeaf(cx: Float, progress: Float) {
    val startY = 30.dp.toPx()
    val endY = size.height - 10.dp.toPx()
    val leafY = startY + progress * (endY - startY)
    val leafX = cx + 18.dp.toPx() * sin(progress * PI.toFloat() * 3f) + (progress * 12.dp.toPx())
    val leafRot = progress * 720f
    val alpha = when {
        progress < 0.10f -> progress / 0.10f
        progress > 0.85f -> 1f - (progress - 0.85f) / 0.15f
        else -> 1f
    }

    val rx = 5.dp.toPx()
    val ry = 3.dp.toPx()
    val stemLen = 4.dp.toPx()

    rotate(degrees = leafRot, pivot = Offset(leafX, leafY)) {
        drawOval(
            color = LeafOrange.copy(alpha = alpha),
            topLeft = Offset(leafX - rx, leafY - ry),
            size = Size(rx * 2, ry * 2)
        )

        drawLine(
            color = LeafStem.copy(alpha = alpha),
            start = Offset(leafX, leafY + ry),
            end = Offset(leafX, leafY + ry + stemLen),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

