package com.cacaosd.mcp.agent

import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*

fun toolExecutionStrategy(name: String): AIAgentStrategy<String, String> {
    return strategy(name) {
        val nodeSendInput by nodeLLMRequest()
        val nodeExecuteTool by nodeExecuteTool()
        val nodeSendToolResult by nodeLLMSendToolResult()

        // Define the flow of the agent
        edge(nodeStart forwardTo nodeSendInput)

        // If the LLM responds with a message, finish
        edge(
            (nodeSendInput forwardTo nodeFinish)
                    onAssistantMessage { true }
        )

        // If the LLM calls a tool, execute it
        edge((nodeSendInput forwardTo nodeExecuteTool) onToolCall { true })

        // Send the tool result back to the LLM
        edge((nodeExecuteTool forwardTo nodeSendToolResult))

        // If the LLM calls another tool, execute it
        edge((nodeSendToolResult forwardTo nodeExecuteTool) onToolCall { true })

        // If the LLM responds with a message, finish
        edge((nodeSendToolResult forwardTo nodeFinish) onAssistantMessage { true })
    }
}