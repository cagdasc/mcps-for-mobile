package com.cacaosd.droidmind.agent.session

import com.cacaosd.droidmind.adb.device_controller.DeviceController
import com.cacaosd.droidmind.domain.AgentClient
import com.cacaosd.droidmind.domain.session.ScenarioExecutor

class DefaultScenarioExecutor(private val agentClient: AgentClient, private val deviceController: DeviceController) :
    ScenarioExecutor {
    override suspend fun execute(deviceSerial: String?, packageName: String, prompt: String) {
//        deviceController.enableAccessibilityService(serial = deviceSerial)
//        delay(250.milliseconds)
//        deviceController.sendData(
//            deviceSerial, mapOf(
//                "INTERACTION_EVENT" to "start_recording",
//                "APP_PACKAGE" to packageName
//            )
//        )
//        delay(250.milliseconds)

        agentClient.executePrompt(prompt = prompt)

//        delay(250.milliseconds)
//
//        deviceController.sendData(
//            deviceSerial, mapOf(
//                "INTERACTION_EVENT" to "stop_recording",
//                "APP_PACKAGE" to packageName
//            )
//        )
//        delay(2000.milliseconds)
//        deviceController.disableAccessibilityService(serial = deviceSerial)
    }
}