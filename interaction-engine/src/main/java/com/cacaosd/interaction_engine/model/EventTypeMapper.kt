package com.cacaosd.interaction_engine.model

import android.view.accessibility.AccessibilityEvent

fun eventTypeToText(eventType: Int): String = when (eventType) {
    AccessibilityEvent.TYPE_VIEW_CLICKED -> "View clicked"
    AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> "View long-clicked"
    AccessibilityEvent.TYPE_VIEW_SELECTED -> "View selected"
    AccessibilityEvent.TYPE_VIEW_FOCUSED -> "View focused"
    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "View text changed"
    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "Window state changed"
    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> "Notification state changed"
    AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> "View hover enter"
    AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> "View hover exit"
    AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> "Touch exploration gesture start"
    AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> "Touch exploration gesture end"
    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "Window content changed"
    AccessibilityEvent.TYPE_VIEW_SCROLLED -> "View scrolled"
    AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> "Text selection changed"
    AccessibilityEvent.TYPE_ANNOUNCEMENT -> "Announcement"
    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> "Accessibility focused"
    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> "Accessibility focus cleared"
    AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> "Text traversed at movement granularity"
    AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> "Gesture detection start"
    AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> "Gesture detection end"
    AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> "Touch interaction start"
    AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> "Touch interaction end"
    AccessibilityEvent.TYPE_WINDOWS_CHANGED -> "Windows changed"
    AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> "Context clicked"
    AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> "Assist reading context"
    else -> "Unknown event type"
}
