package bes.max.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import kotlin.random.Random

private const val DEFAULT_ANIM_START_VAL = 0.001f
private const val DEFAULT_ANIM_END_VAL = 1f

@Composable
fun AnimatedBackground(
    animIcon: ImageVector,
    modifier: Modifier = Modifier,
    elementCount: Int = 80,
    secondIcon: ImageVector? = null,
    finalIcon: ImageVector? = null,
    backgroundColor: Color = Color(0xFF0A1F3A),
    iconColor: Color = Color.White,
    finalIconColor: Color = Color.DarkGray,
    accelerationProgress: Boolean = true,
) {
    val painter = rememberVectorPainter(animIcon)
    val additionalPainter = secondIcon?.let { rememberVectorPainter(it) }
    val finalIconPainter = finalIcon?.let { rememberVectorPainter(it) }

    val elements = remember {
        buildList<Element> {
            repeat(elementCount / 2) {
                add(
                    Element(
                        x = Random.nextFloat(),
                        y = Random.nextFloat(),
                        elementSize = Random.nextFloat() * 20 + 10, // Random size between 10 and 30
                        speed = Random.nextFloat() * 2 + 1, // Random speed between 1 and 3
                        painter = painter,
                        color = iconColor,
                        finalIconPainter = finalIconPainter,
                        finalIconColor = finalIconColor,
                        accelerationProgress = accelerationProgress,
                    )
                )
            }
            repeat(elementCount / 2) {
                add(
                    Element(
                        x = Random.nextFloat(),
                        y = Random.nextFloat(),
                        elementSize = Random.nextFloat() * 20 + 10, // Random size between 10 and 30
                        speed = Random.nextFloat() * 2 + 1, // Random speed between 1 and 3
                        painter = additionalPainter ?: painter,
                        color = iconColor,
                        finalIconPainter = finalIconPainter,
                        finalIconColor = finalIconColor,
                        accelerationProgress = accelerationProgress,
                    )
                )
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = DEFAULT_ANIM_START_VAL,
        targetValue = DEFAULT_ANIM_END_VAL,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        elements.forEach { element ->
            element.draw(this@Canvas)
            element.update(animationProgress)
        }
    }
}

class Element(
    var x: Float, // Normalized x position (0 to 1)
    var y: Float, // Normalized y position (0 to 1)
    val speed: Float, // Speed of falling
    val painter: VectorPainter,
    val color: Color,
    val finalIconColor: Color,
    val elementSize: Float = 1f, // Size of the element
    val accelerationProgress: Boolean = true,
    val finalIconPainter: VectorPainter? = null,
    val slowBeforeTheBottom: Boolean = false,
) {
    var alpha = 1f
    private val finalIconY = 0.93f

    fun update(animationProgress: Float) {
        x = (x).coerceIn(0f, 1f)
        y = if (accelerationProgress) accelerationProgress(y) else linearProgress(y)
        if (y >= 0.98f) {
            x = Random.nextFloat()
            y = Random.nextFloat()
        }
        alpha = (1 - y).coerceIn(minimumValue = 0.1f, maximumValue = 1f)
    }

    fun draw(drawScope: DrawScope) {
        val xCoord = x * drawScope.size.width
        val yCoord = y * drawScope.size.height

        val drawingPainter =
            if (y >= finalIconY && finalIconPainter != null) finalIconPainter else painter
        val left = if (y >= finalIconY && finalIconPainter != null) {
            xCoord - finalIconPainter.intrinsicSize.width * 0.5f
        } else {
            xCoord - painter.intrinsicSize.width * 0.5f
        }
        val top = if (y >= finalIconY && finalIconPainter != null) {
            yCoord - finalIconPainter.intrinsicSize.height * 0.5f
        } else {
            yCoord - painter.intrinsicSize.height * 0.5f
        }

        drawScope.translate(
            left = left,
            top = top,
        ) {
            with(drawingPainter) {
                drawScope.draw(
                    size = drawingPainter.intrinsicSize,
                    alpha = alpha,
                    colorFilter = ColorFilter.tint(
                        color = if (y >= finalIconY && finalIconPainter != null) finalIconColor
                        else color
                    )
                )
            }
        }
    }

    private fun linearProgress(value: Float) =
        if (value >= finalIconY && slowBeforeTheBottom) value + 0.001f else value + speed * 0.0009f

    private fun accelerationProgress(value: Float) =
        if (value >= finalIconY && slowBeforeTheBottom) value + 0.001f else value * (speed * 0.0009f + 1f)
}
