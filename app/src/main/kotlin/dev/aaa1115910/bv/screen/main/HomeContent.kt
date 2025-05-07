package dev.aaa1115910.bv.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.component.HomeTopNavItem
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.screen.main.home.DynamicsScreen
import dev.aaa1115910.bv.screen.main.home.PopularScreen
import dev.aaa1115910.bv.screen.main.home.RecommendScreen
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    recommendViewModel: RecommendViewModel = koinViewModel(),
    popularViewModel: PopularViewModel = koinViewModel(),
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("HomeContent")

    val recommendState = rememberLazyListState()
    val popularState = rememberLazyListState()
    val dynamicState = rememberLazyListState()

    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }

    // 从全局状态获取上次选择的标签位置，如果没有则默认为Recommend
    // 将这个值提到可组合函数的顶部，避免在重组时重新计算
    val initialSelectedTabIndex = currentSelectedTabs[DrawerItem.Home]
    var selectedTab by remember(initialSelectedTabIndex) {
        mutableStateOf(
            initialSelectedTabIndex
                ?.let { HomeTopNavItem.entries.getOrNull(it) }
                ?: HomeTopNavItem.Recommend
        )
    }
    
    // 当选中标签变化时，保存到全局状态
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.Home] = selectedTab.ordinal
    }

    val currentListOnTop by remember {
        derivedStateOf {
            with(
                when (selectedTab) {
                    HomeTopNavItem.Recommend -> recommendState
                    HomeTopNavItem.Popular -> popularState
                    HomeTopNavItem.Dynamics -> dynamicState
                }
            ) {
                firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
            }
        }
    }

    //启动时刷新数据
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            recommendViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            popularViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            dynamicViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            userViewModel.updateUserInfo()
        }
    }

    //监听登录变化
    LaunchedEffect(userViewModel.isLogin) {
        if (userViewModel.isLogin) {
            //login
            userViewModel.updateUserInfo()
        } else {
            //logout
            userViewModel.clearUserInfo()
        }
    }

    BackHandler(focusOnContent || topNavHasFocus) {
        if (topNavHasFocus) {
            drawerItemFocusRequesters[DrawerItem.Home]?.requestFocus()
            return@BackHandler
        }
        navFocusRequester.requestFocus(scope)
        // scroll to top
        scope.launch(Dispatchers.Main) {
            when (selectedTab) {
                HomeTopNavItem.Recommend -> recommendState.animateScrollToItem(0)
                HomeTopNavItem.Popular -> popularState.animateScrollToItem(0)
                HomeTopNavItem.Dynamics -> dynamicState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .padding(end = 80.dp)
                    .onFocusChanged { topNavHasFocus = it.hasFocus },
                items = HomeTopNavItem.entries,
                isLargePadding = !focusOnContent && currentListOnTop,
                initialSelectedItem = selectedTab,
                onSelectedChanged = { nav ->
                    selectedTab = nav as HomeTopNavItem
                    when (nav) {
                        HomeTopNavItem.Recommend -> {}
                        HomeTopNavItem.Popular -> {}
                        HomeTopNavItem.Dynamics -> {
                            if (!dynamicViewModel.loading && dynamicViewModel.isLogin && dynamicViewModel.dynamicList.isEmpty()) {
                                scope.launch(Dispatchers.IO) { dynamicViewModel.loadMore() }
                            }
                        }
                    }
                },
                onClick = { nav ->
                    when (nav) {
                        HomeTopNavItem.Recommend -> {
                            logger.fInfo { "clear recommend data" }
                            recommendViewModel.clearData()
                            logger.fInfo { "reload recommend data" }
                            scope.launch(Dispatchers.IO) { recommendViewModel.loadMore() }
                        }

                        HomeTopNavItem.Popular -> {
                            logger.fInfo { "clear popular data" }
                            popularViewModel.clearData()
                            logger.fInfo { "reload popular data" }
                            scope.launch(Dispatchers.IO) { popularViewModel.loadMore() }
                        }

                        HomeTopNavItem.Dynamics -> {
                            dynamicViewModel.clearData()
                            scope.launch(Dispatchers.IO) { dynamicViewModel.loadMore() }
                        }
                    }
                },
                onLeftKeyEvent = {
                    // 顶部栏最左侧按左键时，跳转到左侧导航栏
                    drawerItemFocusRequesters[DrawerItem.Home]?.requestFocus()
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .onFocusChanged { focusOnContent = it.hasFocus }
        ) {
            AnimatedContent(
                targetState = selectedTab,
                label = "home animated content",
                transitionSpec = {
                    val coefficient = 10
                    if (targetState.ordinal < initialState.ordinal) {
                        fadeIn() + slideInHorizontally { -it / coefficient } togetherWith
                                fadeOut() + slideOutHorizontally { it / coefficient }
                    } else {
                        fadeIn() + slideInHorizontally { it / coefficient } togetherWith
                                fadeOut() + slideOutHorizontally { -it / coefficient }
                    }
                }
            ) { screen ->
                when (screen) {
                    HomeTopNavItem.Recommend -> RecommendScreen(lazyListState = recommendState)
                    HomeTopNavItem.Popular -> PopularScreen(lazyListState = popularState)
                    HomeTopNavItem.Dynamics -> DynamicsScreen(lazyListState = dynamicState)
                }
            }
        }
    }
}
