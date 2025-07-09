@file:OptIn(ExperimentalUuidApi::class)

package com.cacaosd.mcp.agent.client

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.ToolResult
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.agents.features.tokenizer.feature.MessageTokenizer
import ai.koog.prompt.tokenizer.SimpleRegexBasedTokenizer
import com.cacaosd.mcp.agent.event.EventMapper
import com.cacaosd.mcp.domain.AgentClient
import com.cacaosd.mcp.domain.AgentClientFactory
import com.cacaosd.mcp.domain.McpMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.uuid.ExperimentalUuidApi

class DefaultAgentClientFactory(
    private val toolRegistry: ToolRegistry,
    private val aiAgentStrategy: AIAgentStrategy,
    private val eventMapper: EventMapper,
    private val agentEventFlow: MutableSharedFlow<McpMessage>
) :
    AgentClientFactory {
    override fun createGoogleAgent(apiKey: String): AgentClient {
        val builder = provideGoogleAgentBuilder(apiKey)
            .withSystemPrompt(systemPrompt)
            .withMaxIterations(50)
            .withTools(toolRegistry)
            .withStrategy(aiAgentStrategy)
            .withFeatures {
                installEventHandler(eventMapper = eventMapper, mcpMessageFlow = agentEventFlow)
                installSimpleRegexTokenizer()
            }
        return DefaultAgentClient(builder)
    }

    override fun createMetaLLamaAgent(): AgentClient {
        val builder = provideMataLLama32AgentBuilder()
            .withSystemPrompt(systemPrompt)
            .withMaxIterations(10)
            .withTools(toolRegistry)
            .withStrategy(aiAgentStrategy)
            .withFeatures {
                installEventHandler(eventMapper = eventMapper, mcpMessageFlow = agentEventFlow)
                installSimpleRegexTokenizer()
            }
        return DefaultAgentClient(builder)
    }

    private fun AIAgent.FeatureContext.installSimpleRegexTokenizer() {
        install(MessageTokenizer) {
            tokenizer = SimpleRegexBasedTokenizer()
        }
    }

    private fun AIAgent.FeatureContext.installEventHandler(
        eventMapper: EventMapper,
        mcpMessageFlow: MutableSharedFlow<McpMessage>
    ) {
        install(EventHandler) {
            onAfterLLMCall { prompt, tools, model, responses, sessionUuid ->
                responses.forEach { response ->
                    val mcpMessage = eventMapper.mapToMcpMessage(response)
                    mcpMessageFlow.emit(mcpMessage)
                }
            }

            onAgentRunError { strategyName, sessionUuid, throwable ->
                mcpMessageFlow.emit(
                    McpMessage.Response.AssistantWithError(
                        strategyName = strategyName,
                        throwable = throwable
                    )
                )
            }

            onToolCallResult { tool: Tool<*, *>, toolArgs: Tool.Args, result: ToolResult? ->
                // TODO: Handle tool call result if needed
            }
        }
    }

    companion object {
        private val systemPrompt =
            """
        You are a helpful AI assistant with the ability to directly interact with an Android Emulator. You are not limited to following only user instructions—instead, you can make autonomous decisions to accomplish tasks more effectively. This means you can:
        Detect the current emulator (by using list_connected_devices tool).
        Find the app which user requested with name or any description by list_installed_packages
        Launch the app
        Perform relevant actions (e.g., tap,input_text,send_key_event,device_screenshot,swipe) without waiting for explicit user direction.
        Understand what elements are in UI by get_ui_dump tool.
        Proactively navigate through the UI or system settings to fulfill the user’s overall goal.
        Handle unexpected scenarios or errors by adjusting your strategy accordingly.
        Ask for clarification only when absolutely necessary.
        Act as a proactive assistant who thinks and behaves like an intelligent automation agent, capable of completing workflows on Android without micromanagement.
        """.trimIndent()
    }
}