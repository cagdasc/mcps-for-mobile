package com.cacaosd.verificationengine

import android.view.accessibility.AccessibilityEvent.*

val eventMap = mapOf(
    TYPE_VIEW_CLICKED to "View clicked",
    TYPE_VIEW_LONG_CLICKED to "View long-clicked",
    TYPE_VIEW_SELECTED to "View selected",
    TYPE_VIEW_FOCUSED to "View focused",
    TYPE_VIEW_TEXT_CHANGED to "View text changed",
    TYPE_WINDOW_STATE_CHANGED to "Window state changed",
    TYPE_NOTIFICATION_STATE_CHANGED to "Notification state changed",
    TYPE_VIEW_HOVER_ENTER to "View hover enter",
    TYPE_VIEW_HOVER_EXIT to "View hover exit",
    TYPE_TOUCH_EXPLORATION_GESTURE_START to "Touch exploration gesture start",
    TYPE_TOUCH_EXPLORATION_GESTURE_END to "Touch exploration gesture end",
    TYPE_WINDOW_CONTENT_CHANGED to "Window content changed",
    TYPE_VIEW_SCROLLED to "View scrolled",
    TYPE_VIEW_TEXT_SELECTION_CHANGED to "Text selection changed",
    TYPE_ANNOUNCEMENT to "Announcement",
    TYPE_VIEW_ACCESSIBILITY_FOCUSED to "Accessibility focused",
    TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED to "Accessibility focus cleared",
    TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY to "Text traversed at movement granularity",
    TYPE_GESTURE_DETECTION_START to "Gesture detection start",
    TYPE_GESTURE_DETECTION_END to "Gesture detection end",
    TYPE_TOUCH_INTERACTION_START to "Touch interaction start",
    TYPE_TOUCH_INTERACTION_END to "Touch interaction end",
    TYPE_WINDOWS_CHANGED to "Windows changed",
    TYPE_VIEW_CONTEXT_CLICKED to "Context clicked",
    TYPE_ASSIST_READING_CONTEXT to "Assist reading context",
//        TYPE_SPEECH_STATE_CHANGE to "Speech state change",
//        TYPE_VIEW_TARGETED_BY_SCROLL to "Targeted by scroll"
)

fun eventTypeToText(eventType: Int): String = eventMap.getOrDefault(eventType, "")