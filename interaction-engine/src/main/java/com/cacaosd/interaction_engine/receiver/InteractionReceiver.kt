package com.cacaosd.interaction_engine.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.serialization.Serializable

/**
 * For start recording over commandline:
 * `adb shell am broadcast -a com.cacaosd.interaction_engine.INTERACTION_EVENT --es "INTERACTION_EVENT" "start_recording" --es "APP_PACKAGE" "com.google.android.youtube"`
 * For stop recording:
 * `adb shell am broadcast -a com.cacaosd.interaction_engine.INTERACTION_EVENT --es "INTERACTION_EVENT" "stop_recording" --es "APP_PACKAGE" "com.google.android.youtube"`
 */
class InteractionReceiver : BroadcastReceiver() {

    private val listeners = mutableListOf<InteractionListener>()

    fun addListener(listener: InteractionListener) {
        listeners.add(listener)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

        val interactionExtra =
            intent.getStringExtra(INTERACTION_EVENT_EXTRA) ?: error("Interaction event data is missing")

        val applicationExtra = intent.getStringExtra(APP_PACKAGE_EXTRA) ?: error("Application package is missing")

        listeners.forEach {
            it.onInteraction(
                InteractionEventData(
                    interactionEvent = InteractionEvent.fromCommand(
                        interactionExtra
                    )!!, appPackage = applicationExtra
                )
            )
        }
    }

    companion object {
        const val INTERACTION_EVENT_ACTION = "com.cacaosd.interaction_engine.INTERACTION_EVENT"
        private const val INTERACTION_EVENT_EXTRA = "INTERACTION_EVENT"
        private const val APP_PACKAGE_EXTRA = "APP_PACKAGE"
    }
}

interface InteractionListener {
    fun onInteraction(interactionEventData: InteractionEventData)
}

@Serializable
data class InteractionEventData(
    val interactionEvent: InteractionEvent,
    val appPackage: String,
)

@Serializable
enum class InteractionEvent(val command: String) {
    StartRecording("start_recording"),
    StopRecording("stop_recording");

    companion object {
        fun fromCommand(command: String?): InteractionEvent? {
            return entries.find { it.command == command }
        }
    }
}
