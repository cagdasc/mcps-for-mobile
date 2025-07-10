package com.cacaosd.mcps_for_mobile

import ChatScreenBranch
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.cacaosd.godofai.feature.ChatViewModel
import com.cacaosd.mcp.adb.AppConfigManager
import com.cacaosd.mcps_for_mobile.di.featureModule
import com.cacaosd.mcps_for_mobile.di.mainModule
import com.cacaosd.ui_theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(mainModule, featureModule)
        val appConfigManager = koin.get<AppConfigManager>()
        appConfigManager.initializeApp()
    }) {
        AppTheme {
            val chatViewModel = koinViewModel<ChatViewModel>()
            Surface {
                ChatScreenBranch(chatViewModel)
            }
        }
    }


}
