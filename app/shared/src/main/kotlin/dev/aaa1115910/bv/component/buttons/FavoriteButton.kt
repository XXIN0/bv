package dev.aaa1115910.bv.component.buttons

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.swapList

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    userFavoriteFolders: List<FavoriteFolderMetadata> = emptyList(),
    favoriteFolderIds: List<Long> = emptyList(),
    onAddToDefaultFavoriteFolder: () -> Unit,
    onUpdateFavoriteFolders: (List<Long>) -> Unit
) {
    var showFavoriteDialog by remember { mutableStateOf(false) }

    Button(
        modifier = modifier,
        onClick = {
            if (showFavoriteDialog) return@Button
            if (isFavorite) {
                showFavoriteDialog = true
            } else onAddToDefaultFavoriteFolder()
        }
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
            contentDescription = null
        )
    }

    FavoriteDialog(
        show = showFavoriteDialog,
        onHideDialog = { showFavoriteDialog = false },
        userFavoriteFolders = userFavoriteFolders,
        favoriteFolderIds = favoriteFolderIds,
        onUpdateFavoriteFolders = onUpdateFavoriteFolders
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalTvMaterial3Api::class)
@Composable
private fun FavoriteDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    userFavoriteFolders: List<FavoriteFolderMetadata> = emptyList(),
    favoriteFolderIds: List<Long> = emptyList(),
    onUpdateFavoriteFolders: (List<Long>) -> Unit
) {
    val selectedFavoriteFolderIds = remember { mutableStateListOf<Long>() }
    val defaultFocusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            selectedFavoriteFolderIds.swapList(favoriteFolderIds)
            defaultFocusRequester.requestFocus()
        }
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onHideDialog,
            confirmButton = {},
            title = { Text(text = stringResource(R.string.favorite_dialog_title)) },
            text = {
                FlowRow(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    userFavoriteFolders.forEachIndexed { index, userFavoriteFolder ->
                        val selected = selectedFavoriteFolderIds.contains(userFavoriteFolder.id)
                        var hasFocus by remember { mutableStateOf(false) }

                        val itemModifier =
                            if (index == 0) Modifier.focusRequester(defaultFocusRequester)
                            else Modifier

                        FilterChip(
                            modifier = itemModifier.onFocusChanged { hasFocus = it.hasFocus },
                            selected = selected,
                            onClick = {
                                if (selectedFavoriteFolderIds.contains(userFavoriteFolder.id)) {
                                    selectedFavoriteFolderIds.remove(userFavoriteFolder.id)
                                } else {
                                    selectedFavoriteFolderIds.add(userFavoriteFolder.id)
                                }
                                onUpdateFavoriteFolders(selectedFavoriteFolderIds)
                            },
                            leadingIcon = {
                                Row {
                                    AnimatedVisibility(visible = selected) {
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = Icons.Rounded.Done,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(text = userFavoriteFolder.title)
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoriteButtonEnablePreview() {
    BVTheme {
        FavoriteButton(
            isFavorite = true,
            onAddToDefaultFavoriteFolder = {},
            onUpdateFavoriteFolders = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoriteButtonDisablePreview() {
    BVTheme {
        FavoriteButton(
            isFavorite = false,
            onAddToDefaultFavoriteFolder = {},
            onUpdateFavoriteFolders = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Preview(device = "id:tv_1080p", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoriteDialogPreview() {
    val userFavoriteFolders = listOf(
        FavoriteFolderMetadata(0, 0, 0, "收藏夹1", null, false, 0),
        FavoriteFolderMetadata(1, 1, 0, "收藏夹2", null, false, 0),
        FavoriteFolderMetadata(2, 2, 0, "收藏夹3", null, false, 0),
        FavoriteFolderMetadata(3, 3, 0, "收藏夹4", null, false, 0),
        FavoriteFolderMetadata(4, 4, 0, "收藏夹5", null, false, 0),
        FavoriteFolderMetadata(5, 5, 0, "收藏夹6", null, false, 0),
        FavoriteFolderMetadata(6, 6, 0, "收藏夹7", null, false, 0),
        FavoriteFolderMetadata(7, 7, 0, "收藏夹8", null, false, 0),
        FavoriteFolderMetadata(8, 8, 0, "收藏夹9", null, false, 0),
        FavoriteFolderMetadata(9, 9, 0, "收藏夹10", null, false, 0),
    )
    BVTheme {
        FavoriteDialog(
            show = true,
            onHideDialog = {},
            userFavoriteFolders = userFavoriteFolders,
            onUpdateFavoriteFolders = {}
        )
    }
}