package com.cacaosd.mcps_for_mobile

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/*
PROMPT:

Find available android device,
list installed apps,
open "com.cacaosd.later.dev", and take screenshot
dump ui and find Search tab and click it
dump ui and find edittext for search and type "Google" and send done event,
dump ui search result list and tap first element in result list which has title, description and date
AS a result tell me what you see on screen and tell me which tool did you use
 */
fun main() = application {
    val windowState = rememberWindowState(size = DpSize(width = 1020.dp, height = 1080.dp))
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "MCPs for mobile",
    ) {
        App()
    }
}
