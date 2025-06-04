package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.util.formatMinSec

/**
 * @author LiCheng
 * @date 2025/6/4
 * @desc
 */
@Composable
fun VideoBottomController(
    modifier: Modifier = Modifier,
    seekDataLambda: () -> VideoPlayerSeekData,
    isPlayingLambda: () -> Boolean,
    isShowDanmakuLambda: () -> Boolean,
    onClickPlay: () -> Unit = {},
    onClickSupport: () -> Unit = {},
    onClickDanmaku: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Row(
        modifier = modifier
    ) {
        ControlItem(
            imageVector = if (isPlayingLambda()) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            onClick = onClickPlay
        )
        ControlItem(
            imageVector = Icons.Outlined.ClearAll,
            showSlash = isShowDanmakuLambda(),
            onClick = onClickDanmaku
        )
        ControlItem(
            imageVector = Icons.Rounded.ThumbUp,
            onClick = onClickSupport
        )
        ControlItem(
            imageVector = Icons.Rounded.Settings,
            onClick = onClickSetting
        )
        ControlItem(
            imageVector = Icons.Rounded.Close,
            onClick = onClickBack
        )

        Spacer(modifier = Modifier.weight(1f))

        TimeInfo(
            seekDataLambda = seekDataLambda
        )

    }
}

@Composable
private fun ControlItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    showSlash: Boolean = false,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .padding(end = 10.dp)
            .clickable {
                onClick()
            },
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            modifier = Modifier
                .padding(12.dp, 4.dp)
                .size(40.dp),
            imageVector = imageVector,
            contentDescription = null,
            tint = Color.White
        )
        if (showSlash) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .padding(8.dp)
            ) {
                val gap = 15f
                drawLine(
                    color = Color.White,
                    start = Offset(gap, gap),
                    end = Offset(size.width - gap, size.height - gap),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun TimeInfo(
    modifier: Modifier = Modifier,
    seekDataLambda: () -> VideoPlayerSeekData,
) {
    val seekData = seekDataLambda()
    Text(
        modifier = modifier.padding(top = 16.dp, bottom = 0.dp, end = 40.dp),
        text = "${seekData.position.formatMinSec()} / ${seekData.duration.formatMinSec()}",
        color = Color.White
    )
}