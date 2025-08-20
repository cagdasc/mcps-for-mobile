@file:OptIn(ExperimentalUuidApi::class)

package com.cacaosd.droidmind.di

import com.cacaosd.droidmind.agent.client.DefaultAgentClientFactory
import com.cacaosd.droidmind.agent.session.DefaultScenarioExecutor
import com.cacaosd.droidmind.domain.AgentClientFactory
import com.cacaosd.droidmind.domain.session.ScenarioExecutor
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
            agentEventFlow = get(AgentMessageFlowQualifier)
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

    single {
        DefaultScenarioExecutor(
            agentClient = get(GoogleAgentQualifier),
            deviceController = get(AndroidDeviceControllerQualifier)
        )
    } bind ScenarioExecutor::class
}
