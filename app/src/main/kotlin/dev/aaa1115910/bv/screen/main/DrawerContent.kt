package dev.aaa1115910.bv.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.isDpadRight
import dev.aaa1115910.bv.util.isKeyDown

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    isLogin: Boolean = false,
    avatar: String = "",
    onDrawerItemChanged: (DrawerItem) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onShowUserPanel: () -> Unit = {},
    onFocusToContent: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(DrawerItem.Home) }
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()
    val itemColors = NavigationDrawerItemDefaults.colors()
    val iconColors = IconButtonDefaults.colors(
        containerColor = when {
            selectedItem == DrawerItem.User -> itemColors.selectedContentColor
            else -> itemColors.containerColor
        },
        focusedContainerColor = itemColors.focusedContainerColor
    )

    LaunchedEffect(selectedItem) {
        onDrawerItemChanged(selectedItem)
    }

    Column(
        modifier = modifier
            .width(60.dp)
            .fillMaxHeight()
            .padding(vertical = 12.dp)
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.isDpadRight()) {
                    if (keyEvent.isKeyDown()) {
                        onFocusToContent()
                        return@onPreviewKeyEvent true
                    }
                }
                false
            },
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 用户头像
        IconButton(
            modifier = Modifier,
            colors = iconColors,
            onClick = {
                if (isLogin) {
                    onShowUserPanel()
                } else {
                    onLogin()
                }
            }
        ) {
            if (isLogin) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    colors = SurfaceDefaults.colors(
                        containerColor = Color.Gray
                    )
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        model = avatar,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            } else {
                Icon(
                    imageVector = DrawerItem.User.displayIcon,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        listOf(
            DrawerItem.Search,
            DrawerItem.Home,
            DrawerItem.UGC,
            DrawerItem.PGC,
        ).forEach { item ->
            IconButton(
                modifier = Modifier
                    .onFocusChanged { if (it.hasFocus) selectedItem = item }
                    .ifElse(
                        item == DrawerItem.Home,
                        focusRestorerModifiers.childModifier
                    ),
                colors = iconColors,
                onClick = { selectedItem = item }
            ) {
                Icon(
                    imageVector = item.displayIcon,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier,
            colors = iconColors,
            onClick = onOpenSettings
        ) {
            Icon(
                imageVector = DrawerItem.Settings.displayIcon,
                contentDescription = null
            )
        }
    }
}

enum class DrawerItem(
    val displayName: String,
    val displayIcon: ImageVector
) {
    User(displayName = "点击登录", displayIcon = Icons.Default.AccountCircle),
    Search(displayName = "搜索", displayIcon = Icons.Default.Search),
    Home(displayName = "首页", displayIcon = Icons.Default.Home),
    UGC(displayName = "UGC", displayIcon = Icons.Default.OndemandVideo),
    PGC(displayName = "PGC", displayIcon = Icons.Default.Movie),
    Settings(displayName = "设置", displayIcon = Icons.Default.Settings), ;
}

@Preview(device = "id:tv_1080p")
@Composable
private fun DrawerContentPreview() {
    BVTheme {
        DrawerContent()
    }
}