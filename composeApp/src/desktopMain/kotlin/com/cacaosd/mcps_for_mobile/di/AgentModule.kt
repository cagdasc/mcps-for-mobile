@file:OptIn(ExperimentalUuidApi::class)

package com.cacaosd.mcps_for_mobile.di

import com.cacaosd.mcp.agent.client.DefaultAgentClientFactory
import com.cacaosd.mcp.domain.AgentClientFactory
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

val agentModule = module {
    includes(utilityModule)
    single {
        DefaultAgentClientFactory(
            toolRegistry = get(),
            aiAgentStrategy = get(),
            eventMapper = get(),
            agentEventFlow = get(McpMessageFlowQualifier)
        )
    } bind AgentClientFactory::class

    single(GoogleAgentQualifier) {
        val agentClientFactory = get<AgentClientFactory>()
        val localProperties = get<Properties>()
        agentClientFactory.createGoogleAgent(localProperties.getProperty("GEMINI_API_KEY"))
    }
    single(MetaAgentQualifier) {
        val agentClientFactory = get<AgentClientFactory>()
        agentClientFactory.createMetaLLamaAgent()
    }
    single(CustomAgentQualifier) {
        val agentClientFactory = get<AgentClientFactory>()
        agentClientFactory.createCustomModel("qwen3:14b")
    }
}
