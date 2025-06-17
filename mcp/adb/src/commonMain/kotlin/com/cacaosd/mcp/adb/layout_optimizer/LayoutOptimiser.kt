package com.cacaosd.mcp.adb.layout_optimizer

import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

fun getLayoutOptimiser(xml: XML = xmlParser) = LayoutOptimiser(xml = xml)

class LayoutOptimiser(private val xml: XML) {

    fun optimise(uiDumpFile: File): OptimisedHierarchy? {
        val uiText = uiDumpFile.readText()
        val hierarchy = xml.decodeFromString<Hierarchy>(uiText)
        return hierarchy.toOptimizedUi()
    }

    private fun Hierarchy.toOptimizedUi(): OptimisedHierarchy? {
        // TODO: Remove empty or unneeded children such as
        // TODO: It has non-empty text, content-desc, or resource-id
        // TODO: It is clickable, focusable, or enabled="false" (meaning it's disabled)
        // TODO: It has children that pass the same filter

        val rotation = ScreenRotation.fromInt(rotation.toInt())
        val root = node.toUiElement() ?: return null
        return OptimisedHierarchy(rotation = rotation, root = root)
    }

    private fun Node.toUiElement(): UiElement? {
        val rect = bounds.toRect() ?: return null
        val children = children.mapNotNull { it.toUiElement() }

        return UiElement(
            type = className.substringAfterLast('.'),
            text = text.takeIf { it.isNotBlank() },
            contentDescription = contentDesc.takeIf { it.isNotBlank() },
            bounds = rect,
            clickable = clickable,
            focusable = focusable,
            enabled = enabled,
            children = children
        )
    }

    private fun String.toRect(): Rect? {
        val match = Regex("""\[(\d+),(\d+)]\[(\d+),(\d+)]""").find(this) ?: return null
        val (left, top, right, bottom) = match.destructured
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
}
