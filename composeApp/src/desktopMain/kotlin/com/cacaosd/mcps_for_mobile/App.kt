package com.cacaosd.mcps_for_mobile

import ChatScreenBranch
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cacaosd.godofai.feature.ChatViewModel
import com.cacaosd.mcps_for_mobile.di.featureModule
import com.cacaosd.mcps_for_mobile.di.mainModule
import com.cacaosd.ui_theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    initKoin()

    AppTheme {
        val chatViewModel = koinViewModel<ChatViewModel>()
        ChatScreenBranch(chatViewModel)
    }
}

@Composable
fun initKoin() {
    KoinApplication(application = {
        // your preview config here
        modules(mainModule, featureModule)
    }) {
        // Compose to preview with Koin
    }
}
