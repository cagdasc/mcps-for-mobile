package com.cacaosd.interaction_engine.model

import android.view.accessibility.AccessibilityEvent

fun windowChangeToText(flag: Int): String? = when (flag) {
    AccessibilityEvent.WINDOWS_CHANGE_ADDED -> "Window Added"
    AccessibilityEvent.WINDOWS_CHANGE_REMOVED -> "Window Removed"
    AccessibilityEvent.WINDOWS_CHANGE_TITLE -> "Window Title Changed"
    AccessibilityEvent.WINDOWS_CHANGE_BOUNDS -> "Window Bounds Changed"
    AccessibilityEvent.WINDOWS_CHANGE_LAYER -> "Window Layer Changed"
    AccessibilityEvent.WINDOWS_CHANGE_ACTIVE -> "Window Activated"
    AccessibilityEvent.WINDOWS_CHANGE_FOCUSED -> "Window Focused"
    AccessibilityEvent.WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED -> "Window Accessibility Focused"
    AccessibilityEvent.WINDOWS_CHANGE_PARENT -> "Window Parent Changed"
    AccessibilityEvent.WINDOWS_CHANGE_CHILDREN -> "Window Children Changed"
    AccessibilityEvent.WINDOWS_CHANGE_PIP -> "Window Entered PIP"
    else -> null
}
