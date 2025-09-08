package com.cacaosd.droidmind.di

import com.cacaosd.droidmind.domain.AgentClient
import com.cacaosd.droidmind.domain.McpMessage
import com.cacaosd.droidmind.feature.ChatViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel {
        val googleAgentClient: AgentClient = get(GoogleAgentQualifier)

        val mcpMessageFlow: MutableSharedFlow<McpMessage> =
            get<MutableSharedFlow<McpMessage>>(McpMessageFlowQualifier)

        ChatViewModel(
            agentClient = googleAgentClient,
            mcpMessageFlow = mcpMessageFlow,
            deviceController = get(AndroidDeviceControllerQualifier)
        )
    }
}
