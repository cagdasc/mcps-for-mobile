package com.cacaosd.droidmind.domain

interface AgentClient {
    suspend fun executePrompt(prompt: String)
    suspend fun stop()
}
