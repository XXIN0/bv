package dev.aaa1115910.bv.tv.screens.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.viewmodel.user.ToViewViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ToViewScreen(
    modifier: Modifier = Modifier,
    ToViewViewModel: ToViewViewModel = koinViewModel(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    onlyShowContent: Boolean = true
) {
    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 4 } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )

    LaunchedEffect(Unit) {
        ToViewViewModel.update()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (onlyShowContent) {
                return@Scaffold
            }
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.title_activity_toview),
                        fontSize = titleFontSize.sp
                    )
                    if (ToViewViewModel.noMore) {
                        Text(
                            text = stringResource(
                                R.string.load_data_count_no_more,
                                ToViewViewModel.histories.size
                            ),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    } else {
                        Text(
                            text = stringResource(
                                R.string.load_data_count,
                                ToViewViewModel.histories.size
                            ),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            state = lazyGridState,
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(dimensionResource(R.dimen.grid_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_padding)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_spacedBy))
        ) {
            itemsIndexed(ToViewViewModel.histories) { index, item ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    SmallVideoCard(
                        data = item,
                        onClick = {
                            VideoInfoActivity.actionStart(
                                context = context,
                                aid = item.avid,
                                proxyArea = ProxyArea.checkProxyArea(item.title)
                            )
                        },
                        onFocus = {
                            currentIndex = index
                            //预加载
                            // if (index + 20 > ToViewViewModel.histories.size) {
                            //     ToViewViewModel.update()
                            // }
                        }
                    )
                }
            }
        }
    }
}
