package dev.aaa1115910.bv.screen.settings

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.screen.main.DrawerItem
import dev.aaa1115910.bv.screen.main.drawerItemFocusRequesters
import dev.aaa1115910.bv.screen.settings.content.*
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.*

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    defaultFocusRequester: FocusRequester,
) {
    val showLargeTitle by remember { derivedStateOf { true } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )

    var currentMenu by remember { mutableStateOf(SettingsMenuNavItem.Resolution) }
    var focusInNav by remember { mutableStateOf(false) }
    var focusInContent by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    BackHandler(enabled = focusInNav || focusInContent) {
        if (focusInNav) {
            drawerItemFocusRequesters[DrawerItem.Settings]?.requestFocus(scope)
        } else {
            focusInNav = true
        }
    }

    Scaffold(
        modifier = modifier
            .focusRequester(defaultFocusRequester)
            .onPreviewKeyEvent { keyEvent ->
                // 只有在最左边的选项，按左键时才向外传递事件
                if (keyEvent.isKeyDown()) {
                    // 已经是最上或最下时拦截事件
                    if (keyEvent.isDpadUp() && currentMenu == SettingsMenuNavItem.entries.first()) {
                        return@onPreviewKeyEvent true
                    }
                    if (keyEvent.isDpadDown() && currentMenu == SettingsMenuNavItem.entries.last()) {
                        return@onPreviewKeyEvent true
                    }
                }
                false
            },
        topBar = {
            Box(
                modifier = Modifier.padding(
                    start = 48.dp,
                    top = 12.dp,
                    bottom = 8.dp,
                    end = 48.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.title_activity_settings),
                        fontSize = titleFontSize.sp
                    )
                    Text(
                        text = "",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            val contentFocusRequester = remember { FocusRequester() }
            SettingsNav(
                modifier = Modifier
                    .onFocusChanged { focusInNav = it.hasFocus }
                    .weight(3f)
                    .fillMaxHeight(),
                currentMenu = currentMenu,
                onMenuChanged = { currentMenu = it },
                isFocusing = focusInNav,
                onClickItem = {
                    contentFocusRequester.requestFocus(scope)
                }
            )
            SettingContent(
                modifier = Modifier
                    .focusRequester(contentFocusRequester)
                    .onFocusChanged { focusInContent = it.hasFocus }
                    .weight(5f)
                    .fillMaxSize(),
                onBackNav = { focusInNav = true },
                currentMenu = currentMenu
            )
        }
    }
}

@Composable
fun SettingsNav(
    modifier: Modifier = Modifier,
    currentMenu: SettingsMenuNavItem,
    onMenuChanged: (SettingsMenuNavItem) -> Unit,
    isFocusing: Boolean,
    onClickItem: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (item in SettingsMenuNavItem.entries - listOf(SettingsMenuNavItem.PlayerType)) {
            val buttonModifier = if (currentMenu == item) Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
            else Modifier.fillMaxWidth()
            item {
                SettingsMenuButton(
                    modifier = buttonModifier,
                    text = item.getDisplayName(context),
                    selected = currentMenu == item,
                    onFocus = {
                        onMenuChanged(item)
                    },
                    onClick = onClickItem
                )
            }
        }
    }
}

enum class SettingsMenuNavItem(private val strRes: Int) {
    Resolution(R.string.settings_item_resolution),
    VideoCodec(R.string.settings_item_codec),
    Audio(R.string.settings_item_audio),
    PlayerType(R.string.settings_item_player_type),
    UI(R.string.settings_item_ui),
    Api(R.string.settings_item_api),
    Other(R.string.settings_item_other),
    Storage(R.string.settings_item_storage),
    Network(R.string.settings_item_network),
    Info(R.string.settings_item_info),
    About(R.string.settings_item_about);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    onBackNav: () -> Unit,
    currentMenu: SettingsMenuNavItem
) {
    Box(
        modifier = modifier
            .padding(24.dp)
    ) {
        SettingsDetail(
            modifier = Modifier.fillMaxSize(),
            onFocusBackMenuList = {
                onBackNav()
            }
        ) {
            when (currentMenu) {
                SettingsMenuNavItem.Resolution -> ResolutionSetting()
                SettingsMenuNavItem.Info -> InfoSetting()
                SettingsMenuNavItem.About -> AboutSetting()
                SettingsMenuNavItem.VideoCodec -> VideoCodecSetting()
                SettingsMenuNavItem.Audio -> AudioSetting()
                SettingsMenuNavItem.Other -> OtherSetting()
                SettingsMenuNavItem.Network -> NetworkSetting()
                SettingsMenuNavItem.PlayerType -> PlayerTypeSetting()
                SettingsMenuNavItem.UI -> UISetting()
                SettingsMenuNavItem.Storage -> StorageSetting()
                SettingsMenuNavItem.Api -> ApiSetting()
            }
        }
    }
}

@Composable
fun SettingsMenuButton(
    modifier: Modifier = Modifier,
    text: String,
    onFocus: () -> Unit,
    onLoseFocus: () -> Unit = {},
    onClick: () -> Unit = {},
    selected: Boolean
) {
    ListItem(
        modifier = modifier
            .onFocusChanged { if (it.hasFocus) onFocus() else onLoseFocus() },
        selected = selected,
        onClick = onClick,
        headlineContent = {
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp
                ),
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }
    )
}

@Preview
@Composable
fun SettingsMenuButtonPreview() {
    BVTheme {
        Box(
            modifier = Modifier.size(200.dp, 100.dp)
        ) {
            SettingsMenuButton(
                modifier = Modifier.align(Alignment.Center),
                text = "This is button",
                selected = true,
                onFocus = {}
            )
        }
    }
}

@Composable
fun SettingsDetail(
    modifier: Modifier = Modifier,
    onFocusBackMenuList: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.isDpadLeft() && keyEvent.isKeyDown()) {
                    onFocusBackMenuList()
                    return@onPreviewKeyEvent true
                }
                false
            }
    ) {
        content()
    }
}
