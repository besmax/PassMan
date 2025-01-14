package bes.max.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

enum class DragValue { Start, Center, End }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Information(
    title: String? = null,
    text: String? = null,
    modifier: Modifier,
    duration: Long = 3_000,
    content: (@Composable () -> Unit)? = null
) {
    val density = LocalDensity.current
    val defaultActionSize = with(density) { 420.dp.toPx() }
    var visible by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(duration)
        visible = false
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center,
            anchors = DraggableAnchors {
                DragValue.Start at -defaultActionSize
                DragValue.Center at 0f
                DragValue.End at defaultActionSize
            },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { defaultActionSize },
            snapAnimationSpec = tween(durationMillis = 300, easing = LinearEasing),
            decayAnimationSpec = exponentialDecay(),
            confirmValueChange = { true }
        )
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(all = 24.dp)
                .offset {
                    val offset = state
                        .requireOffset()
                        .roundToInt()
                    if (offset.absoluteValue >= defaultActionSize) {
                        visible = false
                    }
                    IntOffset(x = offset, y = 0)
                }
                .alpha(calculateAlpha(state.requireOffset(), defaultActionSize * 0.5f))
                .anchoredDraggable(state, Orientation.Horizontal),
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            vertical = 4.dp,
                            horizontal = 12.dp
                        )
                    )
                }

                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            vertical = 4.dp,
                            horizontal = 12.dp
                        )
                    )
                }

                if (content != null) {
                    content()
                }

            }
        }
    }
}

@Composable
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
fun InformationPreview() {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Information(
            title = "Title",
            text = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            modifier = Modifier
                .padding(top = 36.dp)
                .align(Alignment.BottomCenter)
        )
    }

}

private fun calculateAlpha(offset: Float, defaultActionSize: Float): Float {
    return 1f - (offset.absoluteValue / defaultActionSize)
}