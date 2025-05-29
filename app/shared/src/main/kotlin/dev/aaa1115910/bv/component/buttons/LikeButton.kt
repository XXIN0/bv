package dev.aaa1115910.bv.component.buttons

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    isLiked: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedColor = animateColorAsState(
        targetValue = if (isPressed) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        )
    )
    Button(
        modifier = modifier.onPreviewKeyEvent {
            when (it.key) {
                Key.DirectionCenter, Key.Enter, Key.Spacebar -> {
                    if (it.type == KeyEventType.KeyDown) {
                        if (!isPressed) {
                            coroutineScope.launch {
                                repeat(20) { index ->
                                    if (!isPressed) return@launch
                                    progress = index / 20f
                                    delay(100)
                                }
                                if (progress >= 0.95f) {
                                    onLongClick()
                                    Log.d("LikeButton", "onKeyEvent: LongClick")
                                }
                            }
                        }
                        isPressed = true
                    } else {
                        isPressed = false
                        if (progress < 0.95f) onClick()
                    }
                }
            }
            false
        },
        colors = ButtonDefaults.colors(pressedContainerColor = animatedColor.value),
        onClick = {}
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
fun LikeButtonEnablePreview() {
    LikeButton(
        isLiked = false,
        onClick = {},
        onLongClick = {}
    )
}