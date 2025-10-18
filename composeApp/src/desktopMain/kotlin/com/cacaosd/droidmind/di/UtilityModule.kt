package com.cacaosd.droidmind.di

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import com.cacaosd.droidmind.adb.AppConfigManager
import com.cacaosd.droidmind.adb.device_controller.DeviceController
import com.cacaosd.droidmind.adb.device_controller.getAndroidDeviceController
import com.cacaosd.droidmind.agent.event.EventMapper
import com.cacaosd.droidmind.agent.toolExecutionStrategy
import com.cacaosd.droidmind.agent.tools.DeviceControllerTools
import com.cacaosd.droidmind.domain.McpMessage
import com.cacaosd.droidmind.localProperties
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.*

val utilityModule = module {
    single { EventMapper() }
    single<MutableSharedFlow<McpMessage>>(qualifier = AgentMessageFlowQualifier) { MutableSharedFlow() }
    single<Properties> { localProperties }
    single<Clock> { Clock.System }
}

val toolsModule = module {
    single { AppConfigManager(appName = "droidmind", appVersion = "0.0.1", packageName = "com.cacaosd.droidmind") }
    single(AndroidDeviceControllerQualifier) {
        getAndroidDeviceController(
            appConfigManager = get(),
            clock = get()
        )
    } bind DeviceController::class
    single { DeviceControllerTools(get(AndroidDeviceControllerQualifier)) }

    single<ToolRegistry> {
        ToolRegistry {
            tools(get<DeviceControllerTools>().asTools())
        }
    }
    single { toolExecutionStrategy("Adb tool execution strategy") }
}

