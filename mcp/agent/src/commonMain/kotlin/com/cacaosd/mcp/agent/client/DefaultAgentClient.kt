package com.cacaosd.mcp.agent.client

import com.cacaosd.mcp.domain.AgentClient

class DefaultAgentClient(
    builder: AgentClientBuilder
) : AgentClient {
    private val agent = builder.build()

    override suspend fun executePrompt(prompt: String) {
        agent.run(prompt)
    }
}