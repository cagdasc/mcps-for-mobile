package com.cacaosd.mcp.domain

interface AgentClient {
    suspend fun executePrompt(prompt: String)
    suspend fun stop()
}
