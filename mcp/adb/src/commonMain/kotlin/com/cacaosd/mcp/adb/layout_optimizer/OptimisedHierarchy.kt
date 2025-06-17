package com.cacaosd.mcp.adb.layout_optimizer

data class OptimisedHierarchy(val rotation: ScreenRotation, val root: UiElement)

enum class ScreenRotation(val value: Int, val description: String) {
    PORTRAIT(0, "Portrait (0°)"),
    LANDSCAPE(1, "Landscape (90°)"),
    REVERSE_PORTRAIT(2, "Reverse Portrait (180°)"),
    REVERSE_LANDSCAPE(3, "Reverse Landscape (270°)");

    companion object {
        fun fromInt(value: Int): ScreenRotation {
            return entries.first { it.value == value }
        }
    }

    override fun toString(): String = description
}

data class UiElement(
    val type: String,
    val text: String?,
    val contentDescription: String?,
    val bounds: Rect,
    val clickable: Boolean,
    val focusable: Boolean,
    val enabled: Boolean,
    val children: List<UiElement> = emptyList()
)

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int)
