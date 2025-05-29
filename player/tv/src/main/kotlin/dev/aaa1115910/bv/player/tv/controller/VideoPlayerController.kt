package dev.aaa1115910.bv.player.tv.controller

import android.os.CountDownTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.entity.*
import dev.aaa1115910.bv.player.seekbar.SeekMoveState
import dev.aaa1115910.bv.player.shared.BuildConfig
import dev.aaa1115910.bv.player.shared.R
import dev.aaa1115910.bv.util.countDownTimer
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerController(
    modifier: Modifier = Modifier,
    videoPlayer: AbstractVideoPlayer,

    //player events
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExit: () -> Unit,
    onGoTime: (time: Long) -> Unit,
    onBackToStart: () -> Unit,
    onBackToHistory: () -> Unit,
    onPlayNewVideo: (VideoListItem) -> Unit,

    //menu events
    onResolutionChange: (Resolution) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onDanmakuMaskChange: (Boolean) -> Unit,
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit,

    onRequestFocus: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val videoPlayerSeekData = LocalVideoPlayerSeekData.current
    val videoPlayerHistoryData = LocalVideoPlayerHistoryData.current
    val videoPlayerStateData = LocalVideoPlayerStateData.current
    val videoPlayerDebugInfoData = LocalVideoPlayerDebugInfoData.current
    val logger = KotlinLogging.logger {}

    var showListController by remember { mutableStateOf(false) }
    var showMenuController by remember { mutableStateOf(false) }
    var showSeekController by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    val showClickableControllers by remember { derivedStateOf { showListController || showMenuController } }

    var lastPressBack by remember { mutableLongStateOf(0L) }
    var hasFocus by remember { mutableStateOf(false) }

    var goTime by remember { mutableLongStateOf(0L) }
    var seekChangeCount by remember { mutableIntStateOf(0) }
    var lastSeekChangeTime by remember { mutableLongStateOf(0L) }
    var moveState by remember { mutableStateOf(SeekMoveState.Idle) }

    var hideVideoInfoTimer: CountDownTimer? by remember { mutableStateOf(null) }

    val openSeekController = {
        if (!showSeekController) goTime = videoPlayerSeekData.position
        showSeekController = true
    }

    val calCoefficient = {
        if (System.currentTimeMillis() - lastSeekChangeTime < 200) {
            seekChangeCount++
            seekChangeCount / 5
        } else {
            seekChangeCount = 0
            0
        }
    }

    val onTimeForward = {
        val targetTime = goTime + (10000 + calCoefficient() * 5000)
        goTime =
            if (targetTime > videoPlayerSeekData.duration) videoPlayerSeekData.duration else targetTime
        lastSeekChangeTime = System.currentTimeMillis()
        moveState = SeekMoveState.Forward
        logger.info { "onTimeForward: [current=${videoPlayer.currentPosition}, goTime=$goTime]" }
    }
    val onTimeBack = {
        val targetTime = goTime - (5000 + calCoefficient() * 5000)
        goTime = if (targetTime < 0) 0 else targetTime
        lastSeekChangeTime = System.currentTimeMillis()
        moveState = SeekMoveState.Backward
        logger.info { "onTimeBack: [current=${videoPlayer.currentPosition}, goTime=$goTime]" }
    }

    //有历史播放记录时自动跳转播放进度
    LaunchedEffect(videoPlayerHistoryData.showBackToStart) {

        println("LiChengTest lastPlayed:${videoPlayerHistoryData.lastPlayed}, showBackToStart: ${videoPlayerHistoryData.showBackToStart}")
        if (videoPlayerHistoryData.showBackToStart) {
            delay(1000) // 不delay的话  应该播放器还没初始化好所以
            onBackToHistory()
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .onFocusChanged { hasFocus = it.hasFocus }
            .focusable()
            //.ifElse(hasFocus, Modifier.border(2.dp, Color.Yellow))
            .onPreviewKeyEvent {

                if (showClickableControllers) {
                    if (listOf(Key.Back, Key.Menu).contains(it.key)) {
                        if (it.type == KeyEventType.KeyUp) {
                            logger.fInfo { "[${it.key}] hide all controllers" }
                            showMenuController = false
                            showListController = false
                            showSeekController = false
                        }
                        onRequestFocus()
                        return@onPreviewKeyEvent true
                    }
                    return@onPreviewKeyEvent false
                }

                if (showSeekController) {
                    if (listOf(
                            Key.Back,
                            Key.Menu,
                            Key.DirectionDown,
                            Key.DirectionUp
                        ).contains(it.key)
                    ) {
                        if (it.type != KeyEventType.KeyDown) showSeekController = false
                        onRequestFocus()
                        return@onPreviewKeyEvent true
                    }
                }

                when (it.key) {
                    Key.DirectionCenter, Key.Enter, Key.Spacebar -> {
                        @Suppress("KotlinConstantConditions")
                        //自动跳转到上次播放位置后，按键返回到开头
                        if (!showClickableControllers && videoPlayerStateData.showBackToStart) {
                            if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                            onBackToStart()
                            // onBackToHistory()
                            return@onPreviewKeyEvent true
                        }

                        if (showSeekController) {
                            if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                            onGoTime(goTime)
                            moveState = SeekMoveState.Idle
                            showSeekController = false
                            return@onPreviewKeyEvent true
                        }

                        if (it.nativeKeyEvent.isLongPress) {
                            logger.fInfo { "[${it.key}] long press" }
                            showMenuController = true
                            return@onPreviewKeyEvent true
                        }

                        logger.fInfo { "[${it.key}] short press" }
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        if (videoPlayer.isPlaying) onPause() else onPlay()
                        return@onPreviewKeyEvent false
                    }

                    // KEYCODE_CENTER_LONG
                    // 一切设备上长按 DirectionCenter 键会是这个按键事件
                    Key(763) -> {
                        showMenuController = true
                        return@onPreviewKeyEvent true
                    }

                    Key.DirectionUp -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showListController = true
                        return@onPreviewKeyEvent true
                    }

                    Key.DirectionDown -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showInfo = !showInfo
                        if (showInfo) {
                            hideVideoInfoTimer = countDownTimer(3000, 1000, "hideVideoInfoTimer") {
                                showInfo = false
                            }
                        } else {
                            hideVideoInfoTimer?.cancel()
                        }
                        return@onPreviewKeyEvent true
                    }

                    Key.Menu -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showMenuController = !showMenuController
                        onRequestFocus()
                        return@onPreviewKeyEvent true
                    }

                    Key.Back -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }

                        if (!videoPlayer.isPlaying) {
                            logger.fInfo { "Exiting video player" }
                            onExit()
                            return@onPreviewKeyEvent true
                        }

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastPressBack < 1000 * 3) {
                            logger.fInfo { "Exiting video player" }
                            onExit()
                        } else {
                            lastPressBack = currentTime
                            R.string.video_player_press_back_again_to_exit.toast(context)
                        }
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaPlayPause -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        if (videoPlayer.isPlaying) onPause() else onPlay()
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaPlay -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        if (!videoPlayer.isPlaying) onPlay()
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaPause -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        if (videoPlayer.isPlaying) onPause()
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaFastForward -> {
                        if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        openSeekController()
                        onTimeForward()
                    }

                    Key.MediaRewind -> {
                        if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        openSeekController()
                        onTimeBack()
                    }

                    Key.DirectionLeft -> {
                        if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        openSeekController()
                        onTimeBack()
                    }

                    Key.DirectionRight -> {
                        if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        openSeekController()
                        onTimeForward()
                    }
                }

                false
            }
    ) {
        content()
        if (BuildConfig.DEBUG) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = videoPlayerDebugInfoData.debugInfo
                )
            }
        }
        BottomSubtitle()
        SkipTips()
        PlayStateTips()
        ControllerVideoInfo(
            show = showInfo,
            onHideInfo = { showInfo = false }
        )
        SeekController(
            show = showSeekController,
            goTime = goTime,
            moveState = moveState
        )
        VideoListController(
            show = showListController,
            onPlayNewVideo = onPlayNewVideo
        )
        MenuController(
            show = showMenuController,
            onResolutionChange = onResolutionChange,
            onCodecChange = onCodecChange,
            onAspectRatioChange = onAspectRatioChange,
            onPlaySpeedChange = onPlaySpeedChange,
            onAudioChange = onAudioChange,
            onDanmakuSwitchChange = onDanmakuSwitchChange,
            onDanmakuSizeChange = onDanmakuSizeChange,
            onDanmakuOpacityChange = onDanmakuOpacityChange,
            onDanmakuAreaChange = onDanmakuAreaChange,
            onDanmakuMaskChange = onDanmakuMaskChange,
            onSubtitleChange = onSubtitleChange,
            onSubtitleSizeChange = onSubtitleSizeChange,
            onSubtitleBackgroundOpacityChange = onSubtitleBackgroundOpacityChange,
            onSubtitleBottomPadding = onSubtitleBottomPadding
        )
    }
}



