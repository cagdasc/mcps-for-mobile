package com.cacaosd.mcps_for_mobile.di

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import com.cacaosd.mcp.adb.device_controller.getAdbDeviceControllerTools
import com.cacaosd.mcp.agent.event.EventMapper
import com.cacaosd.mcp.agent.toolExecutionStrategy
import com.cacaosd.mcp.domain.McpMessage
import com.cacaosd.mcps_for_mobile.localProperties
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.dsl.module
import java.util.*

val utilityModule = module {
    single { EventMapper() }
    single<MutableSharedFlow<McpMessage>>(qualifier = McpMessageFlowQualifier) { MutableSharedFlow() }
    single<Properties> { localProperties }
}

val toolsModule = module {
    single<ToolRegistry> {
        ToolRegistry {
            tools(getAdbDeviceControllerTools().asTools())
        }
    }
    single { toolExecutionStrategy("Adb tool execution strategy") }
}

