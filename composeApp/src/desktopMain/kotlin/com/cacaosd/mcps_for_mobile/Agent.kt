package com.cacaosd.mcps_for_mobile

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.llm.LLModel

fun getGoogleAgent(llModel: LLModel, toolRegistry: ToolRegistry): AIAgent {
    return AIAgent(
        systemPrompt = "You are a helpful AI assistant that can interact with android emulator. " +
                "So you don't expect direction, you can give your own decision.",
        executor = simpleGoogleAIExecutor(localProperties.getProperty("GEMINI_API_KEY")),
        llmModel = llModel,
        toolRegistry = toolRegistry,
        strategy = toolBasedStrategy("Tool based strategy"),
        installFeatures = {
            install(EventHandler) {
                onToolCall { tool, toolArgs ->
                    println("[Tool Call]: ${tool.name} -- $toolArgs")
                }

                onBeforeAgentStarted { strategy, agent ->
                    println("[Starting strategy]: ${strategy.name}")

                }
                onAgentFinished { strategyName, result ->
                    println("[Result]: $result")
                }
            }
        }
    )
}

fun toolBasedStrategy(name: String): AIAgentStrategy {
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
        edge(
            (nodeSendInput forwardTo nodeExecuteTool)
                    onToolCall { true }
        )

        // Send the tool result back to the LLM
        edge(nodeExecuteTool forwardTo nodeSendToolResult)

        // If the LLM calls another tool, execute it
        edge(
            (nodeSendToolResult forwardTo nodeExecuteTool)
                    onToolCall { true }
        )

        // If the LLM responds with a message, finish
        edge(
            (nodeSendToolResult forwardTo nodeFinish)
                    onAssistantMessage { true }
        )
    }
}
