package com.cacaosd.mcp.agent.client

import ai.koog.agents.utils.use
import com.cacaosd.mcp.domain.AgentClient

class DefaultAgentClient(
    builder: AgentClientBuilder
) : AgentClient {
    private val agent = builder.build()

    override suspend fun executePrompt(prompt: String) {
        agent.use {
            it.run(prompt)
        }
    }

    override suspend fun stop() {
        // FIXME: This is buggy.
        // https://github.com/JetBrains/koog/issues/569
        agent.reportProblem(IllegalStateException("AgentClient is stopped."))
    }
}