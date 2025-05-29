package dev.aaa1115910.bv.tv.screens.settings.content

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.HomeTopNavItem
import dev.aaa1115910.bv.component.settings.SettingListItem
import dev.aaa1115910.bv.entity.ThemeType
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.requestFocus
import kotlin.math.roundToInt

@Composable
fun UISetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showDensityDialog by remember { mutableStateOf(false) }
    var showHomeTabOrderDialog by remember { mutableStateOf(false) }
    var showThemeTypeDialog by remember { mutableStateOf(false) }
    val density by Prefs.densityFlow.collectAsState(context.resources.displayMetrics.widthPixels / 960f)
    val themeType by Prefs.themeTypeFlow.collectAsState(Prefs.themeType)

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.UI.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_ui_home_tab_order_title),
                        supportText = stringResource(R.string.settings_ui_home_tab_order_text),
                        onClick = { showHomeTabOrderDialog = true }
                    )
                }
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_ui_density_title),
                        supportText = stringResource(R.string.settings_ui_density_text),
                        onClick = { showDensityDialog = true }
                    )
                }
                // item {
                //     SettingListItem(
                //         title = stringResource(R.string.settings_ui_theme_type_title),
                //         supportText = stringResource(R.string.settings_ui_theme_type_text),
                //         onClick = { showThemeTypeDialog = true }
                //     )
                // }
            }
        }
    }

    UIDensityDialog(
        show = showDensityDialog,
        onHideDialog = { showDensityDialog = false },
        density = density,
        onDensityChange = { Prefs.density = it }
    )

    ThemeTypeDialog(
        show = showThemeTypeDialog,
        onHideDialog = { showThemeTypeDialog = false },
        themeType = themeType,
        onThemeTypeChange = { Prefs.themeType = it }
    )

    if (showHomeTabOrderDialog) {
        HomeTabOrderDialog(
            onHideDialog = { showHomeTabOrderDialog = false }
        )
    }
}

@Composable
private fun UIDensityDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    density: Float,
    onDensityChange: (Float) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val defaultDensity by remember { mutableFloatStateOf(context.resources.displayMetrics.widthPixels / 960f) }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus(scope)
    }

    // 这里得采用固定的 Density，否则会导致更改 Density 时，对话框反复重新加载
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = defaultDensity,
            fontScale = LocalDensity.current.fontScale
        )
    ) {
        if (show) {
            AlertDialog(
                modifier = modifier,
                onDismissRequest = { onHideDialog() },
                title = { Text(text = stringResource(R.string.settings_ui_density_title)) },
                text = {
                    Column(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                            .fillMaxWidth()
                            .onPreviewKeyEvent {
                                if (it.key == Key.DirectionUp || it.key == Key.DirectionDown) {
                                    if (it.type == KeyEventType.KeyDown) {
                                        var newDensity = if (it.key == Key.DirectionUp)
                                            density + 0.1f else density - 0.1f
                                        newDensity = (newDensity * 10).roundToInt() / 10f
                                        if (newDensity < 0.5f) newDensity = 0.5f
                                        if (newDensity > 5f) newDensity = 5f
                                        onDensityChange(newDensity)
                                    }
                                }
                                false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Rounded.ArrowDropUp, contentDescription = null)
                        Text(text = "$density")
                        Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
fun ThemeTypeDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    themeType: ThemeType,
    onThemeTypeChange: (ThemeType) -> Unit
) {
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = stringResource(R.string.settings_ui_theme_type_title)) },
            text = {
                Column {
                    ThemeType.entries.forEach {
                        ListItem(
                            selected = themeType == it,
                            onClick = { onThemeTypeChange(it) },
                            headlineContent = {
                                Text(text = it.getDisplayName(LocalContext.current))
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = themeType == it,
                                    onClick = null
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Preview
@Composable
fun UIDensityDialogPreview() {
    val show by remember { mutableStateOf(true) }
    var density by remember { mutableFloatStateOf(1.0f) }

    BVTheme {
        UIDensityDialog(
            show = show,
            onHideDialog = {},
            density = density,
            onDensityChange = { density = it }
        )
    }
}

@Preview
@Composable
private fun ThemeTypeDialogPreview() {
    val show by remember { mutableStateOf(true) }
    val themeType by remember { mutableStateOf(ThemeType.Auto) }

    BVTheme {
        ThemeTypeDialog(
            show = show,
            onHideDialog = {},
            themeType = themeType,
            onThemeTypeChange = {}
        )
    }
}

@Composable
private fun HomeTabOrderDialog(
    modifier: Modifier = Modifier,
    onHideDialog: () -> Unit
) {
    val initialTabList = remember {
        val savedOrder = Prefs.homeTabOrder
        if (savedOrder.isNotEmpty()) {
            try {
                // 解析保存的顺序字符串
                val orderedTabs = savedOrder.split(",")
                    .mapNotNull { ordinal ->
                        ordinal.toIntOrNull()?.let { HomeTopNavItem.entries.getOrNull(it) }
                    }
                    .toMutableList()

                // 添加缺失的标签
                HomeTopNavItem.entries.forEach { tab ->
                    if (!orderedTabs.contains(tab)) orderedTabs.add(tab)
                }

                orderedTabs
            } catch (e: Exception) {
                HomeTopNavItem.entries.toList()
            }
        } else {
            HomeTopNavItem.entries.toList()
        }
    }
    var tabList by remember { mutableStateOf(initialTabList) }

    HomeTabOrderDialogContent(
        modifier = modifier,
        onHideDialog = onHideDialog,
        tabListLambda = { tabList },
        updateTabList = {
            tabList = it
            val orderString = tabList.joinToString(",") { it.ordinal.toString() }
            Prefs.homeTabOrder = orderString
        }
    )
}

@Composable
private fun HomeTabOrderDialogContent(
    modifier: Modifier = Modifier,
    onHideDialog: () -> Unit,
    tabListLambda: () -> List<HomeTopNavItem>,
    updateTabList: (List<HomeTopNavItem>) -> Unit
) {
    val tabList = tabListLambda()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isMovingTab by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onHideDialog() },
        title = { Text(text = stringResource(R.string.settings_ui_home_tab_order_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (!isMovingTab) {
                        stringResource(R.string.settings_ui_home_tab_order_desc_choose)
                    } else {
                        stringResource(R.string.settings_ui_home_tab_order_desc_confirm)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    modifier = Modifier
                        .onPreviewKeyEvent { keyEvent ->
                            if (isMovingTab && keyEvent.type == KeyEventType.KeyDown) {
                                when (keyEvent.key) {
                                    Key.DirectionLeft -> {
                                        if (selectedTabIndex > 0) {
                                            // 创建新列表并移动项目
                                            val newList = tabList.toMutableList()
                                            val item = newList.removeAt(selectedTabIndex)
                                            val newIndex = selectedTabIndex - 1
                                            newList.add(newIndex, item)

                                            // 更新状态
                                            updateTabList(newList)
                                            selectedTabIndex = newIndex
                                            return@onPreviewKeyEvent true
                                        }
                                    }

                                    Key.DirectionRight -> {
                                        if (selectedTabIndex < tabList.size - 1) {
                                            // 创建新列表并移动项目
                                            val newList = tabList.toMutableList()
                                            val item = newList.removeAt(selectedTabIndex)
                                            val newIndex = selectedTabIndex + 1
                                            newList.add(newIndex, item)

                                            // 更新状态
                                            updateTabList(newList)
                                            selectedTabIndex = newIndex
                                            return@onPreviewKeyEvent true
                                        }
                                    }
                                }
                            }
                            false
                        },
                    selectedTabIndex = selectedTabIndex,
                    separator = { Spacer(modifier = Modifier.width(12.dp)) },
                ) {
                    tabList.forEachIndexed { index, tab ->
                        Tab(
                            modifier = Modifier,
                            selected = selectedTabIndex == index,
                            onFocus = { selectedTabIndex = index },
                            onClick = { isMovingTab = !isMovingTab }
                        ) {
                            Text(
                                modifier = Modifier
                                    .height(32.dp)
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                text = tab.getDisplayName(context),
                                color = if (selectedTabIndex == index) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else {
                                    Color.White
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}