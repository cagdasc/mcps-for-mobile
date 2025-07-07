package com.cacaosd.mcp.domain

interface AgentClientFactory {
    fun createGoogleAgent(apiKey: String): AgentClient
    fun createMetaLLamaAgent(): AgentClient
}
