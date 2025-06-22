@file:OptIn(ExperimentalUuidApi::class)

package com.cacaosd.mcps_for_mobile.di

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.agents.features.tokenizer.feature.MessageTokenizer
import ai.koog.prompt.message.Message
import ai.koog.prompt.tokenizer.SimpleRegexBasedTokenizer
import com.cacaosd.mcp.adb.device_controller.getAdbDeviceControllerTools
import com.cacaosd.mcp.agent.client.provideGoogleAgentBuilder
import com.cacaosd.mcp.agent.client.provideMataLLama32AgentBuilder
import com.cacaosd.mcp.agent.event.AgentEvent
import com.cacaosd.mcp.agent.toolExecutionStrategy
import com.cacaosd.mcps_for_mobile.localProperties
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

internal object GoogleAgentQualifier : Qualifier {
    override val value: QualifierValue
        get() = TypeQualifier(GoogleAgentQualifier::class).value
}

internal object MetaAgentQualifier : Qualifier {
    override val value: QualifierValue
        get() = TypeQualifier(MetaAgentQualifier::class).value
}

internal object AgentMessageFlowQualifier : Qualifier {
    override val value: QualifierValue
        get() = TypeQualifier(AgentMessageFlowQualifier::class).value
}

val mainModule = module {
    single<MutableSharedFlow<AgentEvent>>(qualifier = AgentMessageFlowQualifier) { MutableSharedFlow() }
    single<ToolRegistry> {
        ToolRegistry {
            tools(getAdbDeviceControllerTools().asTools())
        }
    }
    single { toolExecutionStrategy("Adb tool execution strategy") }
    single(GoogleAgentQualifier) {
        val agentEventFlow: MutableSharedFlow<AgentEvent> =
            get<MutableSharedFlow<AgentEvent>>(AgentMessageFlowQualifier)
        val toolRegistry = get<ToolRegistry>()
        val agentStrategy = get<AIAgentStrategy>()

        provideGoogleAgentBuilder(apiKey = localProperties.getProperty("GEMINI_API_KEY"))
            .withTools(toolRegistry)
            .withStrategy(agentStrategy)
            .withFeatures {
                installEventHandler(agentEventFlow)
                installSimpleRegexTokenizer()
            }
    }
    single(MetaAgentQualifier) {
        val agentEventFlow: MutableSharedFlow<AgentEvent> =
            get<MutableSharedFlow<AgentEvent>>(AgentMessageFlowQualifier)
        val toolRegistry = get<ToolRegistry>()
        val agentStrategy = get<AIAgentStrategy>()

        provideMataLLama32AgentBuilder()
            .withTools(toolRegistry)
            .withStrategy(agentStrategy)
            .withFeatures {
                installEventHandler(agentEventFlow)
                installSimpleRegexTokenizer()
            }
    }
}

private fun AIAgent.FeatureContext.installSimpleRegexTokenizer() {
    install(MessageTokenizer) {
        tokenizer = SimpleRegexBasedTokenizer()
    }
}

private fun AIAgent.FeatureContext.installEventHandler(agentEventFlow: MutableSharedFlow<AgentEvent>) {
    install(EventHandler) {
        onToolCall { tool, toolArgs ->
            agentEventFlow.emit(AgentEvent.ToolCall(tool.name, toolArgs.toString()))
        }

        onToolCallResult { tool, toolArgs, result ->
            agentEventFlow.emit(AgentEvent.ToolResult(tool.name, result?.toStringDefault().orEmpty()))
        }


        onAfterLLMCall { prompt, tools, model, responses, sessionUuid ->
            agentEventFlow.emit(AgentEvent.Prompt(prompt.messages))

            responses.forEach { response ->
                when (response) {
                    is Message.Assistant -> agentEventFlow.emit(
                        AgentEvent.AssistantMessage(
                            response.content,
                            finishReason = response.finishReason
                        )
                    )

                    is Message.Tool.Call -> agentEventFlow.emit(AgentEvent.ToolArgs(response.content))
                }
            }
        }
    }
}