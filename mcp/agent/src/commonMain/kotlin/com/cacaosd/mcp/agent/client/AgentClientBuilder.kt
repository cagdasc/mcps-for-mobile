package com.cacaosd.mcp.agent.client

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.AIAgent.FeatureContext
import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel

/**
 * A builder for constructing an [AIAgent] with a fluent API.
 *
 * This builder simplifies the creation of an AIAgent by providing
 * chainable methods for configuration, making the setup process
 * more readable and maintainable.
 *
 * @param apiKey The Google AI API key.
 * @param llmModel The language model to be used by the agent.
 * @param systemPrompt The initial system prompt that defines the agent's role and behavior.
 */
class AgentClientBuilder(
    private val llmModel: LLModel,
    private val executor: PromptExecutor
) {
    private var systemPrompt: String = ""
    private var toolRegistry: ToolRegistry = ToolRegistry.EMPTY
    private var strategy: AIAgentStrategy = singleRunStrategy()
    private var features: FeatureContext.() -> Unit = {}

    /**
     * Sets the system prompt for the agent. This is a mandatory step.
     *
     * @param prompt The initial system prompt that defines the agent's role and behavior.
     * @return The builder instance for chaining.
     */
    fun withSystemPrompt(prompt: String): AgentClientBuilder {
        this.systemPrompt = prompt
        return this
    }

    /**
     * Sets the [ToolRegistry] for the agent.
     *
     * @param toolRegistry The collection of tools the agent can use.
     * @return The builder instance for chaining.
     */
    fun withTools(toolRegistry: ToolRegistry): AgentClientBuilder {
        this.toolRegistry = toolRegistry
        return this
    }

    /**
     * Sets the execution [AIAgentStrategy] for the agent.
     *
     * @param strategy The strategy defining how the agent will run (e.g., single run, loop).
     * @return The builder instance for chaining.
     */
    fun withStrategy(strategy: AIAgentStrategy): AgentClientBuilder {
        this.strategy = strategy
        return this
    }

    /**
     * Installs additional features for the agent.
     *
     * @param block A lambda with [FeatureContext] as its receiver to configure features.
     * @return The builder instance for chaining.
     */
    fun withFeatures(block: FeatureContext.() -> Unit): AgentClientBuilder {
        this.features = block
        return this
    }

    /**
     * Constructs and returns the configured [AIAgent].
     *
     * @return The final, configured AIAgent instance.
     */
    fun build(): AIAgent {
        return AIAgent(
            executor = executor,
            llmModel = llmModel,
            strategy = strategy,
            systemPrompt = systemPrompt,
            toolRegistry = toolRegistry,
            installFeatures = features
        )
    }
}
