package com.cacaosd.droidmind.domain.session

interface ScenarioExecutor {
    suspend fun execute(deviceSerial: String?, packageName: String, prompt: String)
}