package com.cacaosd.interaction_engine.model

import android.view.accessibility.AccessibilityEvent

fun contentChangeTypeToString(type: Int): String? = when (type) {
    AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED -> "Undefined"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE -> "Subtree Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT -> "Text Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION -> "Content Description Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION -> "State Description Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE -> "Pane Title Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED -> "Pane Appeared"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED -> "Pane Disappeared"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_STARTED -> "Drag Started"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_DROPPED -> "Drag Dropped"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_DRAG_CANCELLED -> "Drag Cancelled"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_INVALID -> "Content Invalid"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_ERROR -> "Error"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_ENABLED -> "Enabled State Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_CHECKED -> "Checked State Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_EXPANDED -> "Expanded State Changed"
    AccessibilityEvent.CONTENT_CHANGE_TYPE_SUPPLEMENTAL_DESCRIPTION -> "Supplemental Description Changed"
    else -> null
}
