package dev.aaa1115910.bv.tv.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.component.UgcTopNavItem
import dev.aaa1115910.bv.tv.screens.main.ugc.*
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UgcContent(
    modifier: Modifier = Modifier,
    contentFocusRequester: FocusRequester,
    dougaState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Douga),
    gameState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Game),
    kichikuState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Kichiku),
    musicState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Music),
    danceState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Dance),
    cinephileState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Cinephile),
    entState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Ent),
    knowledgeState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Knowledge),
    techState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Tech),
    informationState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Information),
    foodState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Food),
    shortPlayState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Shortplay),
    carState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Car),
    fashionState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Fashion),
    sportsState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Sports),
    animalState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Animal),
    vlogState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Vlog),
    paintingState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Painting),
    aiState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Ai),
    homeState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Home),
    outdoorsState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Outdoors),
    gymState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Gym),
    handmakeState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Handmake),
    travelState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Travel),
    ruralState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Rural),
    parentingState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Parenting),
    healthState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Health),
    emotionState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Emotion),
    lifeJoyState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.LifeJoy),
    lifeExperienceState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.LifeExperience),
    mysticismState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcTypeV2.Mysticism),
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UgcContent")
    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }

    // 使用remember的key参数确保只有在DrawerItem.UGC的tab状态变化时才重新计算
    val initialSelectedTabIndex = currentSelectedTabs[DrawerItem.UGC]
    var selectedTab by remember(initialSelectedTabIndex) {
        mutableStateOf(
            (initialSelectedTabIndex as? UgcTopNavItem)
                ?.let { UgcTopNavItem.entries.getOrNull(it.ordinal) }
                ?: UgcTopNavItem.Douga
        )
    }

    // 当选中标签变化时，保存到全局状态
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.UGC] = selectedTab
    }

    val navFocusRequester = remember { FocusRequester() }

    BackHandler(focusOnContent || topNavHasFocus) {
        logger.fInfo { "onFocusBackToNav" }
        if (topNavHasFocus) {
            drawerItemFocusRequesters[DrawerItem.UGC]?.requestFocus(scope)
            return@BackHandler
        }
        navFocusRequester.requestFocus(scope)
        // scroll to top
        scope.launch(Dispatchers.Main) {
            when (selectedTab) {
                UgcTopNavItem.Douga -> dougaState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Game -> gameState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Kichiku -> kichikuState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Music -> musicState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Dance -> danceState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Cinephile -> cinephileState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Ent -> entState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Knowledge -> knowledgeState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Tech -> techState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Information -> informationState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Food -> foodState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.ShortPlay -> shortPlayState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Car -> carState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Fashion -> fashionState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Sports -> sportsState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Animal -> animalState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Vlog -> vlogState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Painting -> paintingState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Ai -> aiState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Home -> homeState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Outdoors -> outdoorsState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Gym -> gymState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Handmake -> handmakeState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Travel -> travelState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Rural -> ruralState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Parenting -> parentingState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Health -> healthState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Emotion -> emotionState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.LifeJoy -> lifeJoyState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.LifeExperience -> lifeExperienceState.lazyListState.animateScrollToItem(
                    0
                )

                UgcTopNavItem.Mysticism -> mysticismState.lazyListState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .onFocusChanged { topNavHasFocus = it.hasFocus },
                items = UgcTopNavItem.entries,
                isLargePadding = !focusOnContent,
                initialSelectedItem = selectedTab,
                onSelectedChanged = { nav ->
                    selectedTab = nav as UgcTopNavItem
                },
                onClick = { nav ->
                    when (nav) {
                        UgcTopNavItem.Douga -> dougaState.reloadAll()
                        UgcTopNavItem.Game -> gameState.reloadAll()
                        UgcTopNavItem.Kichiku -> kichikuState.reloadAll()
                        UgcTopNavItem.Music -> musicState.reloadAll()
                        UgcTopNavItem.Dance -> danceState.reloadAll()
                        UgcTopNavItem.Cinephile -> cinephileState.reloadAll()
                        UgcTopNavItem.Ent -> entState.reloadAll()
                        UgcTopNavItem.Knowledge -> knowledgeState.reloadAll()
                        UgcTopNavItem.Tech -> techState.reloadAll()
                        UgcTopNavItem.Information -> informationState.reloadAll()
                        UgcTopNavItem.Food -> foodState.reloadAll()
                        UgcTopNavItem.ShortPlay -> shortPlayState.reloadAll()
                        UgcTopNavItem.Car -> carState.reloadAll()
                        UgcTopNavItem.Fashion -> fashionState.reloadAll()
                        UgcTopNavItem.Sports -> sportsState.reloadAll()
                        UgcTopNavItem.Animal -> animalState.reloadAll()
                        UgcTopNavItem.Vlog -> vlogState.reloadAll()
                        UgcTopNavItem.Painting -> paintingState.reloadAll()
                        UgcTopNavItem.Ai -> aiState.reloadAll()
                        UgcTopNavItem.Home -> homeState.reloadAll()
                        UgcTopNavItem.Outdoors -> outdoorsState.reloadAll()
                        UgcTopNavItem.Gym -> gymState.reloadAll()
                        UgcTopNavItem.Handmake -> handmakeState.reloadAll()
                        UgcTopNavItem.Travel -> travelState.reloadAll()
                        UgcTopNavItem.Rural -> ruralState.reloadAll()
                        UgcTopNavItem.Parenting -> parentingState.reloadAll()
                        UgcTopNavItem.Health -> healthState.reloadAll()
                        UgcTopNavItem.Emotion -> emotionState.reloadAll()
                        UgcTopNavItem.LifeJoy -> lifeJoyState.reloadAll()
                        UgcTopNavItem.LifeExperience -> lifeExperienceState.reloadAll()
                        UgcTopNavItem.Mysticism -> mysticismState.reloadAll()
                    }
                },
                onLeftKeyEvent = {
                    // 顶部栏最左侧按左键时，跳转到左侧导航栏
                    drawerItemFocusRequesters[DrawerItem.UGC]?.requestFocus(scope)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .focusRequester(contentFocusRequester)
                .onFocusChanged { focusOnContent = it.hasFocus }
        ) {
            AnimatedContent(
                targetState = selectedTab,
                label = "ugc animated content",
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
                    UgcTopNavItem.Douga -> DougaContent(state = dougaState)
                    UgcTopNavItem.Game -> GameContent(state = gameState)
                    UgcTopNavItem.Kichiku -> KichikuContent(state = kichikuState)
                    UgcTopNavItem.Music -> MusicContent(state = musicState)
                    UgcTopNavItem.Dance -> DanceContent(state = danceState)
                    UgcTopNavItem.Cinephile -> CinephileContent(state = cinephileState)
                    UgcTopNavItem.Ent -> EntContent(state = entState)
                    UgcTopNavItem.Knowledge -> KnowledgeContent(state = knowledgeState)
                    UgcTopNavItem.Tech -> TechContent(state = techState)
                    UgcTopNavItem.Information -> InformationContent(state = informationState)
                    UgcTopNavItem.Food -> FoodContent(state = foodState)
                    UgcTopNavItem.ShortPlay -> ShortPlayContent(state = shortPlayState)
                    UgcTopNavItem.Car -> CarContent(state = carState)
                    UgcTopNavItem.Fashion -> FashionContent(state = fashionState)
                    UgcTopNavItem.Sports -> SportsContent(state = sportsState)
                    UgcTopNavItem.Animal -> AnimalContent(state = animalState)
                    UgcTopNavItem.Vlog -> VlogContent(state = vlogState)
                    UgcTopNavItem.Painting -> PaintingContent(state = paintingState)
                    UgcTopNavItem.Ai -> AiContent(state = aiState)
                    UgcTopNavItem.Home -> dev.aaa1115910.bv.tv.screens.main.ugc.HomeContent(state = homeState)
                    UgcTopNavItem.Outdoors -> OutdoorsContent(state = outdoorsState)
                    UgcTopNavItem.Gym -> GymContent(state = gymState)
                    UgcTopNavItem.Handmake -> HandmakeContent(state = handmakeState)
                    UgcTopNavItem.Travel -> TravelContent(state = travelState)
                    UgcTopNavItem.Rural -> RuralContent(state = ruralState)
                    UgcTopNavItem.Parenting -> ParentingContent(state = parentingState)
                    UgcTopNavItem.Health -> HealthContent(state = healthState)
                    UgcTopNavItem.Emotion -> EmotionContent(state = emotionState)
                    UgcTopNavItem.LifeJoy -> LifeJoyContent(state = lifeJoyState)
                    UgcTopNavItem.LifeExperience -> LifeExperienceContent(state = lifeExperienceState)
                    UgcTopNavItem.Mysticism -> MysticismContent(state = mysticismState)
                }
            }
        }
    }
}