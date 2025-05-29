package dev.aaa1115910.bv.tv.activities.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import dev.aaa1115910.bv.tv.screens.settings.SettingsScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                val settingFocusRequester = remember { FocusRequester() }
                SettingsScreen(defaultFocusRequester = settingFocusRequester)
            }
        }
    }
}
