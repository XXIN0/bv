package dev.aaa1115910.bv.player.mobile.controller.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoListPart
import dev.aaa1115910.bv.player.entity.VideoListPgcEpisode
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisode
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisodeTitle
import dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
import dev.aaa1115910.bv.util.ifElse

@Composable
fun VideoListMenu(
    modifier: Modifier = Modifier,
    onClickVideoListItem: (VideoListItem) -> Unit
) {
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val list = videoPlayerConfigData.availableVideoList
    val selectedVideoListItem by remember(videoPlayerConfigData.currentVideoCid) {
        derivedStateOf {
            list.first {
                when (it) {
                    is VideoListPart -> it.cid == videoPlayerConfigData.currentVideoCid
                    is VideoListUgcEpisode -> it.cid == videoPlayerConfigData.currentVideoCid
                    is VideoListPgcEpisode -> it.cid == videoPlayerConfigData.currentVideoCid
                    else -> false
                }
            }
        }
    }
    val isUgcSeason by remember {
        derivedStateOf {
            videoPlayerConfigData.availableVideoList.any { it is VideoListUgcEpisode }
        }
    }

    Surface(
        modifier = modifier
            .width(400.dp)
            .fillMaxHeight()
            .clickable(false) {},
        color = Color.Black.copy(0.4f),
        contentColor = Color.White.copy(alpha = 0.9f),
        shape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
        )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = list) { item ->
                VideoListItem(
                    item = item,
                    selected = item == selectedVideoListItem,
                    inUgcEpisode = isUgcSeason,
                    onClick = onClickVideoListItem
                )
            }
        }
    }
}

@Composable
fun VideoListMenuController(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideController: () -> Unit = {},
    onClickVideoListItem: (VideoListItem) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .ifElse(show, Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { if (show) onHideController() }
                )
            }),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = show,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            VideoListMenu(
                modifier = Modifier,
                onClickVideoListItem = onClickVideoListItem
            )
        }
    }
}

@Composable
private fun VideoListItem(
    modifier: Modifier = Modifier,
    item: VideoListItem,
    selected: Boolean,
    inUgcEpisode: Boolean,
    onClick: (VideoListItem) -> Unit
) {
    val textPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .ifElse({ item !is VideoListUgcEpisodeTitle }, Modifier.clickable { onClick(item) }),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        contentColor = if (selected) contentColorFor(MaterialTheme.colorScheme.primaryContainer) else Color.White.copy(
            alpha = 0.9f
        )
    ) {
        when (item) {
            is VideoListPart -> {
                Text(
                    text = (" - ".takeIf { inUgcEpisode }
                        ?: "") + "P${item.index + 1} ${item.title}",
                    modifier = modifier
                        .padding(textPadding),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            is VideoListUgcEpisode -> {
                Text(
                    text = "EP${item.index + 1} ${item.title}",
                    modifier = modifier
                        .padding(textPadding),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            is VideoListPgcEpisode -> {
                Text(
                    text = item.title,
                    modifier = modifier
                        .padding(textPadding),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            is VideoListUgcEpisodeTitle -> {
                Text(
                    text = "EP${item.index + 1} ${item.title}",
                    modifier = modifier
                        .padding(textPadding),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Preview
@Composable
private fun VideoListItemPreview() {
    MaterialTheme {
        VideoListItem(
            item = VideoListPart(
                aid = 0,
                cid = 0,
                title = "This is title",
                index = 2
            ),
            selected = false,
            inUgcEpisode = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun VideoListMenuContentNormalPartPreview() {
    CompositionLocalProvider(
        LocalVideoPlayerConfigData provides VideoPlayerConfigData(
            availableVideoList = List(20) {
                VideoListPart(
                    aid = it.toLong(),
                    cid = it.toLong(),
                    title = "This is title $it",
                    index = it
                )
            },
            currentVideoCid = 3
        )
    ) {
        MaterialTheme {
            VideoListMenu(
                onClickVideoListItem = {}
            )
        }
    }
}

@Preview
@Composable
private fun VideoListMenuContentPgcSeasonPreview() {
    CompositionLocalProvider(
        LocalVideoPlayerConfigData provides VideoPlayerConfigData(
            availableVideoList = List(20) {
                VideoListPgcEpisode(
                    aid = it.toLong(),
                    cid = it.toLong(),
                    title = "This is title $it",
                    index = it
                )
            },
            currentVideoCid = 3
        )
    ) {
        MaterialTheme {
            VideoListMenu(
                onClickVideoListItem = {}
            )
        }
    }
}

@Preview
@Composable
private fun VideoListMenuContentUgcSeasonPreview() {
    CompositionLocalProvider(
        LocalVideoPlayerConfigData provides VideoPlayerConfigData(
            availableVideoList = listOf(
                *(0..1).map {
                    VideoListUgcEpisode(
                        aid = it.toLong(),
                        cid = it.toLong(),
                        title = "This is title for ep ${it + 1}",
                        index = it
                    )
                }.toTypedArray(),
                VideoListUgcEpisodeTitle(
                    title = "This is title for ep 3",
                    index = 2
                ),
                *(0..4).map {
                    VideoListPart(
                        aid = it.toLong(),
                        cid = it.toLong(),
                        title = "part $it in ep3",
                        index = it
                    )
                }.toTypedArray(),
                *(3..5).map {
                    VideoListUgcEpisode(
                        aid = it.toLong(),
                        cid = it.toLong(),
                        title = "This is title for ep ${it + 1}",
                        index = it
                    )
                }.toTypedArray()
            ),
            currentVideoCid = 3
        )
    ) {
        MaterialTheme {
            VideoListMenu(
                onClickVideoListItem = {}
            )
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun VideoListMenuControllerPreview() {
    CompositionLocalProvider(
        LocalVideoPlayerConfigData provides VideoPlayerConfigData(
            availableVideoList = List(20) {
                VideoListPgcEpisode(
                    aid = it.toLong(),
                    cid = it.toLong(),
                    title = "This is title $it",
                    index = it
                )
            },
            currentVideoCid = 3
        )
    ) {
        MaterialTheme {
            VideoListMenuController(
                show = true,
                onHideController = {},
                onClickVideoListItem = {}
            )
        }
    }
}