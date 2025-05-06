package dev.aaa1115910.bv.screen.main.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.LoadingTip
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.screen.main.ugc.gridItems
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PopularScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    popularViewModel: PopularViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentFocusedIndex by remember { mutableIntStateOf(0) }
    val shouldLoadMore by remember {
        derivedStateOf { currentFocusedIndex + 24 > popularViewModel.popularVideoList.size }
    }

    val onClickVideo: (UgcItem) -> Unit = { ugcItem ->
        VideoInfoActivity.actionStart(context, ugcItem.aid)
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            scope.launch(Dispatchers.IO) {
                popularViewModel.loadMore()
                //加载完成后重置shouldLoadMore为false，避免如果加载失败后无法重新加载
                currentFocusedIndex = -100
            }
        }
    }

    val padding = dimensionResource(R.dimen.grid_padding)
    val spacedBy = dimensionResource(R.dimen.grid_spacedBy)
    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        gridItems(
            data = popularViewModel.popularVideoList,
            columnCount = 4,
            modifier = Modifier
                .width(880.dp)
                .padding(padding),
            horizontalArrangement = Arrangement.spacedBy(spacedBy),
            itemContent = { index, item ->
                SmallVideoCard(
                    data = VideoCardData(
                        avid = item.aid,
                        title = item.title,
                        cover = item.cover,
                        play = with(item.play) { if (this == -1) null else this },
                        danmaku = with(item.danmaku) { if (this == -1) null else this },
                        upName = item.author,
                        time = item.duration * 1000L
                    ),
                    onClick = { onClickVideo(item) },
                    onFocus = { currentFocusedIndex = index }
                )
            }
        )

        if (popularViewModel.loading)
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingTip()
                }
            }
    }
}