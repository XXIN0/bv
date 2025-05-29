package dev.aaa1115910.bv.tv.screens.user

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.viewmodel.user.FavoriteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    favoriteViewModel: FavoriteViewModel = koinViewModel(),
    onlyShowContent: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 4 } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )
    val focusRequester = remember { FocusRequester() }
    val defaultFocusRequester = remember { FocusRequester() }
    var focusOnTabs by remember { mutableStateOf(true) }
    val lazyGridState = rememberLazyGridState()

    val currentTabIndex by remember {
        derivedStateOf {
            favoriteViewModel.favoriteFolderMetadataList.indexOf(favoriteViewModel.currentFavoriteFolderMetadata)
        }
    }

    val updateCurrentFavoriteFolder: (folderMetadata: FavoriteFolderMetadata) -> Unit =
        { folderMetadata ->
            favoriteViewModel.currentFavoriteFolderMetadata = folderMetadata
            favoriteViewModel.favorites.clear()
            favoriteViewModel.resetPageNumber()
            favoriteViewModel.updateFolderItems(force = true)
        }

    BackHandler(
        enabled = !focusOnTabs
    ) {
        scope.launch(Dispatchers.Main) {
            lazyGridState.animateScrollToItem(0)
            defaultFocusRequester.requestFocus()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (onlyShowContent) return@Scaffold
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stringResource(R.string.user_homepage_favorite)} - ${favoriteViewModel.currentFavoriteFolderMetadata?.title}",
                        fontSize = titleFontSize.sp
                    )
                    Text(
                        text = stringResource(
                            R.string.load_data_count,
                            favoriteViewModel.favorites.size
                        ),
                        color = Color.White.copy(alpha = 0.6f)
                    )
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
            item(
                span = { GridItemSpan(4) }
            ) {
                TabRow(
                    modifier = Modifier
                        .focusRequester(defaultFocusRequester)
                        .onFocusChanged { focusOnTabs = it.hasFocus }
                        .focusRequester(focusRequester),
                    selectedTabIndex = currentTabIndex,
                    separator = { Spacer(modifier = Modifier.width(12.dp)) },
                ) {
                    favoriteViewModel.favoriteFolderMetadataList.forEachIndexed { index, folderMetadata ->
                        Tab(
                            modifier = Modifier
                                .ifElse(index == 0, Modifier.focusRequester(focusRequester)),
                            selected = currentTabIndex == index,
                            onFocus = {
                                if (favoriteViewModel.currentFavoriteFolderMetadata != folderMetadata) {
                                    updateCurrentFavoriteFolder(folderMetadata)
                                }
                            },
                            onClick = { updateCurrentFavoriteFolder(folderMetadata) }
                        ) {
                            Box(
                                modifier = Modifier.height(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    text = folderMetadata.title,
                                    color = LocalContentColor.current,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
            itemsIndexed(favoriteViewModel.favorites) { index, history ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    SmallVideoCard(
                        data = history,
                        onClick = { VideoInfoActivity.actionStart(context, history.avid) },
                        onFocus = {
                            currentIndex = index
                            //预加载
                            if (index + 20 > favoriteViewModel.favorites.size) {
                                favoriteViewModel.updateFolderItems()
                            }
                        }
                    )
                }
            }
        }
    }
}