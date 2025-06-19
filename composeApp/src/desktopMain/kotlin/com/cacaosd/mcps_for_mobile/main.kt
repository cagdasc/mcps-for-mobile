package com.cacaosd.mcps_for_mobile

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.agents.features.tokenizer.feature.MessageTokenizer
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.tokenizer.SimpleRegexBasedTokenizer
import com.cacaosd.mcp.adb.device_controller.getDeviceControllerTools
import com.cacaosd.mcp.agent.getGoogleAgent
import com.cacaosd.mcp.agent.toolExecutionStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    delay(2_000)
    val systemPrompt =
        """
            You are a helpful AI assistant that can interact with android emulator. 
            So you don't expect direction, you can give your own decision.
        """.trimIndent()

    val agent = getGoogleAgent(
        systemPrompt = systemPrompt,
        llmModel = GoogleModels.Gemini2_0Flash,
        toolRegistry = ToolRegistry {
            tools(getDeviceControllerTools().asTools())
        },
        strategy = toolExecutionStrategy("Adb tool execution strategy"),
        apiKey = localProperties.getProperty("GEMINI_API_KEY"),
        installFeatures = {
            install(EventHandler) {
                onToolCall { tool, toolArgs ->
                    println("Tool called: ${tool.name} with args $toolArgs")
                }

                onToolCallResult { tool, toolArgs, result ->
                    println("Tool call result: ${tool.name} with args $toolArgs and result: ${result?.toStringDefault()}")
                }
            }

            install(MessageTokenizer) {
                tokenizer = SimpleRegexBasedTokenizer()
            }
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
