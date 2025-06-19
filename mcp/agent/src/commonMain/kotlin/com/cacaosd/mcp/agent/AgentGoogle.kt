package com.cacaosd.mcp.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.AIAgent.FeatureContext
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.llm.LLModel

fun getGoogleAgent(
    systemPrompt: String,
    llmModel: LLModel,
    toolRegistry: ToolRegistry = ToolRegistry.EMPTY,
    strategy: AIAgentStrategy = singleRunStrategy(),
    apiKey: String,
    installFeatures: FeatureContext.() -> Unit = {}
): AIAgent = AIAgent(
    executor = simpleGoogleAIExecutor(apiKey),
    llmModel = llmModel,
    strategy = strategy,
    systemPrompt = systemPrompt,
    toolRegistry = toolRegistry,
    installFeatures = installFeatures
)
