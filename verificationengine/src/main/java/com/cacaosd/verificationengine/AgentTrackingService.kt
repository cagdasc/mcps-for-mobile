package com.cacaosd.verificationengine

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Accessibility-based tracking service. Writes JSON-lines to /sdcard/AgentEvents/agent_events.log
 * This keeps a simple append-only feed that can be pulled via ADB by your external agent.
 */
class AgentTrackingService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val timestampFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
    val logJson = JSONArray()

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val arg = intent?.getStringExtra("ARG")
            if (arg == "done") {
                savePublicFile(
                    context = this@AgentTrackingService,
                    fileName = "agent_events.log",
                    content = logJson.toString()
                )
            }

        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
//        this.serviceInfo
        Log.d("AgentTrackingService", "AgentTrackingService connected")

        val filter = IntentFilter("com.example.myapp.SERVICE_COMMAND")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(commandReceiver, filter, RECEIVER_EXPORTED)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val eventObj = JSONObject().apply {
            put("timestamp", timestampFmt.format(Date()))
            put("event_type", eventTypeToText(event.eventType))
            put("application_package", event.packageName?.toString())
            put("view_class", event.className?.toString())
            put("text", event.text.joinToString(separator = " ") { it })
            put("source", JSONObject().apply {
                put("id", event.source?.viewIdResourceName ?: "unknown")
                put("content_description", event.source?.contentDescription?.toString() ?: "none")
                put("is_enabled", event.source?.isEnabled)
                put("classname", event.source?.className)
                put("text", event.source?.text)
            })
            put("window_changes", event.windowChanges)
        }

        logJson.put(eventObj)
    }

    override fun onInterrupt() {
        // no-op
    }
}
