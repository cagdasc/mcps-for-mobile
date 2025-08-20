package com.cacaosd.mcp.domain.session

interface ScenarioExecutor {
    suspend fun execute(deviceSerial: String?, packageName: String, prompt: String)
}
