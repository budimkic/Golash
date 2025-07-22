package com.golash.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A shimmer effect composable that displays a loading animation or content based on loading state.
 * 
 * @param isLoading Whether to show the shimmer effect or the actual content
 * @param contentAfterLoading The content to display when loading is complete
 * @param modifier Modifier to be applied to the composable
 * @param shimmerColors Custom colors for the shimmer effect (optional)
 * @param animationDuration Duration of the shimmer animation in milliseconds
 * @param shimmerWidth Width of the shimmer gradient effect
 * @param shape Shape of the shimmer container
 * @param height Height of the shimmer container when loading
 */
@Composable
fun ShimmerEffect(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    ),
    animationDuration: Int = 1000,
    shimmerWidth: Float = 1000f,
    shape: Shape = RoundedCornerShape(4.dp),
    height: Dp = 200.dp
) {
    if (isLoading) {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = shimmerWidth,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translate"
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - shimmerWidth, 0f),
            end = Offset(translateAnim, 0f)
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(brush = brush)
        )
    } else {
        contentAfterLoading()
    }
}

/**
 * A shimmer effect modifier that can be applied to any composable.
 * 
 * @param isLoading Whether to show the shimmer effect
 * @param shimmerColors Custom colors for the shimmer effect
 * @param animationDuration Duration of the shimmer animation in milliseconds
 * @param shimmerWidth Width of the shimmer gradient effect
 */
@Composable
fun Modifier.shimmer(
    isLoading: Boolean,
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    ),
    animationDuration: Int = 1000,
    shimmerWidth: Float = 1000f
): Modifier {
    return if (isLoading) {
        val transition = rememberInfiniteTransition(label = "shimmer_modifier")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = shimmerWidth,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_modifier_translate"
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - shimmerWidth, 0f),
            end = Offset(translateAnim, 0f)
        )

        this.background(brush = brush)
    } else {
        this
    }
}