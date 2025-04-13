package dev.aaa1115910.bv.player.mobile.controller.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
import dev.aaa1115910.bv.player.mobile.MaterialDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun ResolutionMenu(
    modifier: Modifier = Modifier,
    onClickResolution: (Int) -> Unit,
    onClose: () -> Unit
) {
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "清晰度") },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = videoPlayerConfigData.availableResolutionMap
                    .toList()
                    .sortedByDescending { it.first }
            ) { (code, name) ->
                ResolutionListItem(
                    text = name,
                    selected = videoPlayerConfigData.currentResolution == code,
                    onClick = {
                        println("click resolution menu: $name($code)")
                        onClickResolution(code)
                    }
                )
            }
        }
    }
}

@Composable
private fun ResolutionListItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {}
) {
    val textColor = if (selected) MaterialTheme.colorScheme.primary else Color.White

    Surface(
        modifier = modifier
            .size(200.dp, 48.dp),
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 32.dp),
                text = text,
                color = textColor
            )
        }
    }
}

private val availableResolutionMap = mapOf(
    127 to "8K",
    126 to "Dolby Vision",
    125 to "HDR",
    120 to "4K",
    116 to "1080P 60FPS",
    112 to "1080P+",
    80 to "1080P",
    74 to "720P 60FPS",
    64 to "720P",
    32 to "480P",
    16 to "360P",
    6 to "240P"
)

@Preview
@Composable
private fun ResolutionListItemSelectedPreview() {
    MaterialTheme {
        ResolutionListItem(
            text = "1080P 60FPS",
            selected = true
        )
    }
}

@Preview
@Composable
private fun ResolutionListItemUnselectedPreview() {
    MaterialTheme {
        ResolutionListItem(
            text = "1080P 60FPS",
            selected = false
        )
    }
}

@Preview(device = "spec:width=300dp,height=400dp,dpi=440")
@Composable
private fun ResolutionMenuPreview() {
    MaterialDarkTheme {
        CompositionLocalProvider(
            LocalVideoPlayerConfigData provides VideoPlayerConfigData(
                currentResolution = 32,
                availableResolutionMap = availableResolutionMap
            )
        ) {
            ResolutionMenu(
                onClickResolution = {},
                onClose = {}
            )
        }
    }
}