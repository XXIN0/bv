package dev.aaa1115910.bv.component

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.entity.ugc.UgcType
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.util.isDpadLeft
import dev.aaa1115910.bv.util.isKeyDown

@Composable
fun TopNav(
    modifier: Modifier = Modifier,
    items: List<TopNavItem>,
    isLargePadding: Boolean,
    initialSelectedItem: TopNavItem? = null,
    onSelectedChanged: (TopNavItem) -> Unit = {},
    onClick: (TopNavItem) -> Unit = {},
    onLeftKeyEvent: () -> Unit = {}
) {
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    var selectedNav by remember(initialSelectedItem) { 
        mutableStateOf(initialSelectedItem ?: items.first()) 
    }
    
    var selectedTabIndex by remember(initialSelectedItem) { 
        mutableIntStateOf(
            if (initialSelectedItem != null) {
                val index = items.indexOf(initialSelectedItem)
                if (index >= 0) index else 0
            } else 0
        ) 
    }
    
    val verticalPadding by animateDpAsState(
        targetValue = if (isLargePadding) 24.dp else 12.dp,
        label = "top nav vertical padding"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp, verticalPadding),
        horizontalArrangement = Arrangement.Center
    ) {
        TabRow(
            modifier = Modifier
                .then(focusRestorerModifiers.parentModifier)
                .onPreviewKeyEvent { keyEvent ->
                    // 只有在最左边的选项，按左键时才向外传递事件
                    if (keyEvent.isDpadLeft() && keyEvent.isKeyDown() && selectedTabIndex == 0) {
                        onLeftKeyEvent()
                        return@onPreviewKeyEvent true
                    }
                    false
                },
            selectedTabIndex = selectedTabIndex,
            separator = { Spacer(modifier = Modifier.width(12.dp)) },
        ) {
            items.forEachIndexed { index, tab ->
                NavItemTab(
                    modifier = Modifier
                        .ifElse(index == 0, focusRestorerModifiers.childModifier),
                    topNavItem = tab,
                    selected = index == selectedTabIndex,
                    onFocus = {
                        selectedNav = tab
                        selectedTabIndex = index
                        onSelectedChanged(tab)
                    },
                    onClick = { onClick(tab) }
                )
            }
        }
    }
}

@Composable
private fun TabRowScope.NavItemTab(
    modifier: Modifier = Modifier,
    topNavItem: TopNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    onFocus: () -> Unit
) {
    val context = LocalContext.current

    Tab(
        modifier = modifier,
        selected = selected,
        onFocus = onFocus,
        onClick = onClick
    ) {
        Text(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            text = topNavItem.getDisplayName(context),
            color = LocalContentColor.current,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

interface TopNavItem {
    fun getDisplayName(context: Context = BVApp.context): String
}

enum class HomeTopNavItem(private val displayName: String) : TopNavItem {
    Recommend("推荐"),
    Popular("热门"),
    Dynamics("动态");

    override fun getDisplayName(context: Context): String {
        return displayName
    }
}

enum class UgcTopNavItem(private val ugcType: UgcType) : TopNavItem {
    Douga(UgcType.Douga),
    Game(UgcType.Game),
    Kichiku(UgcType.Kichiku),
    Music(UgcType.Music),
    Dance(UgcType.Dance),
    Cinephile(UgcType.Cinephile),
    Ent(UgcType.Ent),
    Knowledge(UgcType.Knowledge),
    Tech(UgcType.Tech),
    Information(UgcType.Information),
    Food(UgcType.Food),
    Life(UgcType.Life),
    Car(UgcType.Car),
    Fashion(UgcType.Fashion),
    Sports(UgcType.Sports),
    Animal(UgcType.Animal);

    override fun getDisplayName(context: Context): String {
        return ugcType.getDisplayName(context)
    }
}

enum class PgcTopNavItem(private val pgcType: PgcType) : TopNavItem {
    Anime(PgcType.Anime),
    GuoChuang(PgcType.GuoChuang),
    Movie(PgcType.Movie),
    Documentary(PgcType.Documentary),
    Tv(PgcType.Tv),
    Variety(PgcType.Variety);

    override fun getDisplayName(context: Context): String {
        return pgcType.getDisplayName(context)
    }
}