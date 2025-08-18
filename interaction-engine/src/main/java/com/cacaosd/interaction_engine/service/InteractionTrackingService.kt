package com.cacaosd.interaction_engine.service

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.lifecycleScope
import com.cacaosd.interaction_engine.model.AccessibilityEventModel
import com.cacaosd.interaction_engine.model.contentChangeTypeToString
import com.cacaosd.interaction_engine.model.eventTypeToText
import com.cacaosd.interaction_engine.model.windowChangeToText
import com.cacaosd.interaction_engine.receiver.InteractionEvent
import com.cacaosd.interaction_engine.receiver.InteractionEventData
import com.cacaosd.interaction_engine.receiver.InteractionListener
import com.cacaosd.interaction_engine.receiver.InteractionReceiver
import com.cacaosd.interaction_engine.util.json
import com.cacaosd.interaction_engine.util.saveStringToDownloads
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Accessibility-based tracking service.
 * Activate it over adb with: `adb shell settings put secure enabled_accessibility_services com.cacaosd.interaction_engine/com.cacaosd.interaction_engine.service.InteractionTrackingService`
 * Deactivate it with: `adb shell am force-stop com.cacaosd.interaction_engine`
 */
class InteractionTrackingService : LifecycleAccessibilityService() {

    @OptIn(ExperimentalAtomicApi::class)
    private val eventFlowLock = AtomicBoolean(true)
    private val filter = IntentFilter(InteractionReceiver.INTERACTION_EVENT_ACTION)
    private val interactionReceiver = InteractionReceiver().apply {
        addListener(interactionListener())
    }

    private val accessibilityEventFlow = MutableSharedFlow<AccessibilityEvent>()
    private val eventCollection = mutableListOf<AccessibilityEventModel>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerInteractionEngineInteractionReceiver()

        Log.d(SERVICE_NAME, "InteractionTrackingService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || eventFlowLock.get()) return

        lifecycleScope.launch {
            accessibilityEventFlow.emit(event)
        }

        Log.d(SERVICE_NAME, "Logging event ${event.eventType} from package ${event.packageName}")
    }

    override fun onInterrupt() {
        // no-op
    }

    private fun registerInteractionEngineInteractionReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(interactionReceiver, filter, RECEIVER_EXPORTED)
        } else {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            registerReceiver(interactionReceiver, filter)
        }
    }

    private fun interactionListener() = object : InteractionListener {
        override fun onInteraction(interactionEventData: InteractionEventData) {
            when (interactionEventData.interactionEvent) {
                InteractionEvent.StartRecording -> {
                    accessibilityEventFlow
                        .onStart {
                            eventFlowLock.set(false)
                            Log.d(SERVICE_NAME, "Started recording accessibility events")
                        }
                        .map { accessibilityEvent ->
                            AccessibilityEventModel(
                                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                eventType = eventTypeToText(accessibilityEvent.eventType),
                                applicationPackage = accessibilityEvent.packageName?.toString(),
                                viewClass = accessibilityEvent.className?.toString(),
                                text = accessibilityEvent.text.joinToString(separator = " ") { it.toString() },
                                source = AccessibilityEventModel.Source(
                                    id = accessibilityEvent.source?.viewIdResourceName,
                                    contentDescription = accessibilityEvent.source?.contentDescription?.toString(),
                                    isEnabled = accessibilityEvent.source?.isEnabled ?: false,
                                    classname = accessibilityEvent.source?.className.toString(),
                                    text = accessibilityEvent.source?.text.toString()
                                ),
                                windowChanges = windowChangeToText(accessibilityEvent.windowChanges),
                                contentChangeType = contentChangeTypeToString(accessibilityEvent.contentChangeTypes)
                            )
                        }
                        .filter { it.applicationPackage == null || it.applicationPackage == interactionEventData.appPackage }
                        .onEach(eventCollection::add)
                        .launchIn(lifecycleScope)
                }

                InteractionEvent.StopRecording -> {
                    eventFlowLock.set(true)
                    Log.d(SERVICE_NAME, "Stopped recording accessibility events")
                    val applicationPackage = interactionEventData.appPackage
                    saveStringToDownloads(
                        fileName = "${applicationPackage}_agent_events_${Clock.System.now().epochSeconds}.log",
                        content = json.encodeToString(eventCollection)
                    )
                    eventCollection.clear()
                }
            }
        }
    }

    companion object {
        const val SERVICE_NAME = "InteractionTrackingService"
    }
}
