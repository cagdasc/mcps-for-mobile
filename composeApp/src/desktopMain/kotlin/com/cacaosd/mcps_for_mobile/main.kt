package com.cacaosd.mcps_for_mobile

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.executor.clients.google.GoogleModels
import com.cacaosd.mcp.adb.device_controller.getDeviceControllerTools
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    delay(2_000)

    val agent = getGoogleAgent(
        llModel = GoogleModels.Gemini2_0Flash,
        toolRegistry = ToolRegistry {
            tools(getDeviceControllerTools().asTools())
        }
    )

    agent.run(
        """
        Find available android device,
        list installed apps,
        open "com.cacaosd.later.dev",
        dump ui and find Search tab and click it
        dump ui and find edittext for search and type "Google" and send done event,
        dump ui search result list and tap first element in result list which has title, description and date
        AS a result tell me what you see on screen and tell me which tool did you use
    """.trimIndent()
    )
}
