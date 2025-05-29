package dev.aaa1115910.bv.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.bv.util.focusedBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PgcCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>,
    onClick: (CarouselData.CarouselItem) -> Unit
) {
    CarouselContent(
        modifier = modifier,
        data = data,
        onClick = onClick
    )
}

@Composable
fun UgcCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>,
    onClick: (CarouselData.CarouselItem) -> Unit
) {
    CarouselContent(
        modifier = modifier,
        data = data,
        onClick = onClick
    )
}

@Composable
fun CarouselContent(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>,
    onClick: (CarouselData.CarouselItem) -> Unit
) {
    return
    Carousel(
        itemCount = data.size,
        modifier = modifier
            .height(240.dp)
            .clip(MaterialTheme.shapes.large)
            .focusedBorder(),
        onClick = { itemIndex ->
            onClick(data[itemIndex])
        }
    ) { itemIndex ->
        CarouselCard(
            data = data[itemIndex]
        )
    }
}

@Composable
fun CarouselCard(
    modifier: Modifier = Modifier,
    data: CarouselData.CarouselItem
) {
    AsyncImage(
        modifier = modifier.fillMaxWidth(),
        model = data.cover,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Carousel(
    itemCount: Int,
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = CarouselDefaults.TimeToDisplayItemMillis,
    contentTransformStartToEnd: ContentTransform = fadeIn(tween(1000))
        .togetherWith(fadeOut(tween(1000))),
    contentTransformEndToStart: ContentTransform = fadeIn(tween(1000))
        .togetherWith(fadeOut(tween(1000))),
    onClick: (index: Int) -> Unit,
    content: @Composable AnimatedContentScope.(index: Int) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    var isMovingBackward by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentIndex, itemCount) {
        while (true) {
            delay(autoScrollInterval)
            if (itemCount == 0 || hasFocus) continue
            isMovingBackward = false
            currentIndex = (currentIndex + 1) % itemCount
        }
    }

    Box(
        modifier = modifier
            .onFocusChanged { focusState ->
                hasFocus = focusState.isFocused
            }
            .clickable { onClick(currentIndex) }
            .onKeyEvent {
                when {
                    itemCount == 0 -> false
                    it.type == KeyEventType.KeyUp -> false
                    it.key == Key.DirectionLeft -> {
                        isMovingBackward = true
                        currentIndex = (currentIndex - 1 + itemCount) % itemCount
                        true
                    }

                    it.key == Key.DirectionRight -> {
                        isMovingBackward = false
                        currentIndex = (currentIndex + 1) % itemCount
                        true
                    }

                    else -> false
                }
            }
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                if (isMovingBackward) {
                    contentTransformEndToStart
                } else {
                    contentTransformStartToEnd
                }
            },
            label = "CarouselAnimation"
        ) { activeItemIndex ->
            if (itemCount > 0) content(activeItemIndex)
        }
        CarouselDefaults.IndicatorRow(
            itemCount = itemCount,
            activeItemIndex = currentIndex,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun CarouselPreview() {
    val colors = remember { mutableStateListOf<Color>() }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            delay(8000)
            colors.addAll(
                listOf(
                    Color.Red,
                    Color.Yellow,
                    Color.Green,
                    Color.Blue,
                    Color.Cyan,
                    Color.Magenta,
                    Color.Gray,
                )
            )
        }
    }

    Column {
        Button(onClick = {}) { Text(text = "button") }
        Row {
            Button(onClick = {}) { Text(text = "button") }
            Carousel(
                itemCount = colors.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.large)
                    .focusedBorder(),
                onClick = {

                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(color = colors[it])
                ) {}
            }
        }

        Button(onClick = {}) { Text(text = "button") }
    }
}