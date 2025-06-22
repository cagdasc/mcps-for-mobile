package com.cacaosd.mcps_for_mobile.di

import com.cacaosd.godofai.feature.ChatViewModel
import com.cacaosd.mcp.agent.client.AgentClientBuilder
import com.cacaosd.mcp.agent.event.AgentEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

val featureModule = module {
    viewModel {
        val systemPrompt =
            """
            You are a helpful AI assistant that can interact with android emulator. 
            So you don't expect direction, you can give your own decision.
        """.trimIndent()

        val googleAgentBuilder: AgentClientBuilder = get(GoogleAgentQualifier)
        val googleAgent = googleAgentBuilder.withSystemPrompt(systemPrompt).build()

        val agentEventFlow: MutableSharedFlow<AgentEvent> = get<MutableSharedFlow<AgentEvent>>(AgentMessageFlowQualifier)

        ChatViewModel(agent = googleAgent,agentEventFlow=agentEventFlow) }
}