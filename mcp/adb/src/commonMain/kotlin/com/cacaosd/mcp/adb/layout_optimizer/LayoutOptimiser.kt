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
        val rotation = ScreenRotation.fromInt(rotation.toInt())
        val cleanNode = node.cleanAndReindex()

        // TODO: Handle null case and return meaningful message to agent
        val root = cleanNode?.toUiElement() ?: return null
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

    private fun Node.cleanAndReindex(): Node? {
        // Recursively clean children
        val cleanedChildren = children.mapNotNull { it.cleanAndReindex() }

        // Determine if this node is meaningful
        val isMeaningful = text.isNotBlank()
                || resourceId.isNotBlank()
                || contentDesc.isNotBlank()
                || clickable
                || focusable
                || !enabled

        // Collapse meaningless parent with a single child
        if (!isMeaningful && cleanedChildren.size == 1) {
            return cleanedChildren.first()
        }

        // Remove node entirely if not meaningful and has no children
        if (!isMeaningful && cleanedChildren.isEmpty()) {
            return null
        }

        // Reindex children
        val reIndexedChildren = cleanedChildren.mapIndexed { idx, child ->
            child.copy(index = idx.toString())
        }

        // Return cleaned and re-indexed node
        return this.copy(children = reIndexedChildren)
    }

    private fun String.toRect(): Rect? {
        val match = Regex("""\[(\d+),(\d+)]\[(\d+),(\d+)]""").find(this) ?: return null
        val (left, top, right, bottom) = match.destructured
        return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }
}
