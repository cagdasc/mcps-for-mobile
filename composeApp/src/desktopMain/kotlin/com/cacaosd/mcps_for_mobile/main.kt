package com.cacaosd.mcps_for_mobile

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cacaosd.mcp.agent.client.AgentClientBuilder
import com.cacaosd.mcp.agent.event.AgentEvent
import com.cacaosd.mcps_for_mobile.di.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MCPs for mobile",
    ) {
        App()
    }
}

fun main2() = runBlocking {
    startKoin {
        modules(mainModule, featureModule)
    }

    val systemPrompt =
        """
            You are a helpful AI assistant that can interact with android emulator. 
            So you don't expect direction, you can give your own decision.
        """.trimIndent()

    val googleAgentBuilder: AgentClientBuilder = getKoin().get(GoogleAgentQualifier)
    val metaAgentBuilder: AgentClientBuilder = getKoin().get(MetaAgentQualifier)

    val googleAgent = googleAgentBuilder.withSystemPrompt(systemPrompt).build()
    val metaAgent = metaAgentBuilder.withSystemPrompt(systemPrompt).build()

    val messageFlow: MutableSharedFlow<AgentEvent> = getKoin().get(AgentMessageFlowQualifier)

    launch {
        messageFlow.collect {
            println(it.toString())
        }
    }

    delay(2_000)

    googleAgent.run(
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
