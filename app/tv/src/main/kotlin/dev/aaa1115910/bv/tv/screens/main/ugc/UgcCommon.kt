package dev.aaa1115910.bv.tv.screens.main.ugc

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.biliapi.entity.ugc.region.UgcFeedPage
import dev.aaa1115910.biliapi.repositories.UgcRepository
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.UgcCarousel
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun UgcRegionScaffold(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState,
    childRegionButtons: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    var currentFocusedIndex by remember { mutableIntStateOf(0) }
    val shouldLoadMore by remember {
        derivedStateOf { currentFocusedIndex + 24 > state.ugcItems.size }
    }

    LaunchedEffect(Unit) { if (state.ugcItems.isEmpty()) state.initUgcRegionData() }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            state.loadMore()
            currentFocusedIndex = -100
        }
    }

    val padding = dimensionResource(R.dimen.grid_padding)
    val spacedBy = dimensionResource(R.dimen.grid_spacedBy)

    LazyColumn(
        modifier = modifier,
        state = state.lazyListState
    ) {
        if (state.showCarousel) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    UgcCarousel(
                        modifier = Modifier
                            .width(880.dp)
                            .padding(32.dp, 0.dp),
                        data = state.carouselItems,
                        onClick = { item ->
                            VideoInfoActivity.actionStart(
                                context = context,
                                aid = item.avid!!
                            )
                        }
                    )
                }
            }
        }

        if (childRegionButtons != null) {
            item {
                childRegionButtons()
            }
        } else {
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
            }
        }

        gridItems(
            data = state.ugcItems,
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
                        play = item.play,
                        danmaku = item.danmaku,
                        upName = item.author,
                        time = item.duration * 1000L,
                        pubTime = item.pubTime
                    ),
                    onClick = { VideoInfoActivity.actionStart(context, item.aid) },
                    onFocus = { currentFocusedIndex = index }
                )
            }
        )
    }
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    key: ((index: Int) -> Any)? = null,
    columnCount: Int,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(Int, T) -> Unit,
) {
    val size = data.count()
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount
    items(rows, key = key) { rowIndex ->
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            item {
                Row(
                    verticalAlignment = verticalAlignment,
                    horizontalArrangement = horizontalArrangement,
                    modifier = modifier
                ) {
                    for (columnIndex in 0 until columnCount) {
                        val itemIndex = rowIndex * columnCount + columnIndex
                        if (itemIndex < size) {
                            Box(
                                modifier = Modifier.weight(1F, fill = true),
                                propagateMinConstraints = true
                            ) {
                                itemContent(itemIndex, data[itemIndex])
                            }
                        } else {
                            Spacer(Modifier.weight(1F, fill = true))
                        }
                    }
                }
            }
        }
    }
}

data class UgcScaffoldState(
    val context: Context,
    val scope: CoroutineScope,
    val lazyListState: LazyListState,
    val ugcType: UgcTypeV2,
    private val ugcRepository: UgcRepository
) {
    companion object {
        val logger = KotlinLogging.logger { }
    }

    val carouselItems = mutableStateListOf<CarouselData.CarouselItem>()
    val ugcItems = mutableStateListOf<UgcItem>()
    var nextPage by mutableStateOf(UgcFeedPage())
    var hasMore by mutableStateOf(true)
    var updating by mutableStateOf(false)
    var showCarousel by mutableStateOf(true)

    suspend fun initUgcRegionData() {
        loadUgcRegionData()
        loadMore()
    }

    suspend fun loadUgcRegionData() {
        if (!hasMore && updating) return
        updating = true
        logger.fInfo { "load ugc $ugcType region data" }
        runCatching {
            val carouselData = ugcRepository.getCarousel(ugcType)
            val data = ugcRepository.getRegionFeedRcmd(ugcType, nextPage)
            carouselItems.clear()
            ugcItems.clear()
            carouselItems.addAll(carouselData.items)
            ugcItems.addAll(data.items)
            nextPage = data.nextPage
            showCarousel = carouselItems.isNotEmpty()
        }.onFailure {
            logger.fInfo { "load $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 数据失败: ${it.message}".toast(context)
            }
        }
        hasMore = true
        updating = false
    }

    fun reloadAll() {
        logger.fInfo { "reload all $ugcType data" }
        scope.launch(Dispatchers.IO) {
            nextPage = UgcFeedPage()
            hasMore = true
            showCarousel = true
            carouselItems.clear()
            ugcItems.clear()
            initUgcRegionData()
        }
    }

    suspend fun loadMore() {
        if (!hasMore && updating) return
        updating = true
        runCatching {
            val data = ugcRepository.getRegionFeedRcmd(ugcType, nextPage)
            ugcItems.addAll(data.items)
            nextPage = data.nextPage
            hasMore = data.items.isNotEmpty()
        }.onFailure {
            logger.fInfo { "load more $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 更多推荐失败: ${it.message}".toast(context)
            }
        }
        updating = false
    }
}

@Composable
fun rememberUgcScaffoldState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    ugcType: UgcTypeV2,
    ugcRepository: UgcRepository = koinInject()
): UgcScaffoldState {
    return remember(
        context,
        scope,
        lazyListState,
        ugcType,
        ugcRepository
    ) {
        UgcScaffoldState(
            context = context,
            scope = scope,
            lazyListState = lazyListState,
            ugcType = ugcType,
            ugcRepository = ugcRepository
        )
    }
}