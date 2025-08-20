package com.cacaosd.droidmind.di

import com.cacaosd.droidmind.domain.AgentClient
import com.cacaosd.droidmind.domain.McpMessage
import com.cacaosd.droidmind.domain.session.ScenarioExecutor
import com.cacaosd.droidmind.feature.ChatViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel {
        val googleAgentClient: AgentClient = get(GoogleAgentQualifier)
        val scenarioExecutor: ScenarioExecutor = get()

        val mcpMessageFlow: MutableSharedFlow<McpMessage> =
            get<MutableSharedFlow<McpMessage>>(AgentMessageFlowQualifier)

        ChatViewModel(
            scenarioExecutor = scenarioExecutor,
            mcpMessageFlow = mcpMessageFlow,
            deviceController = get(AndroidDeviceControllerQualifier)
        )
    }
}
