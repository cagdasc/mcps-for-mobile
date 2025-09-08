package com.cacaosd.droidmind.adb.layout_optimizer

import com.cacaosd.droidmind.adb.layout_optimizer.strategy.CollapseParentNodeStrategy
import com.cacaosd.droidmind.adb.layout_optimizer.strategy.NodeOptimisationStrategy
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

fun getLayoutOptimiser(xml: XML = xmlParser) =
    LayoutOptimiser(xml = xml, nodeOptimisationStrategy = CollapseParentNodeStrategy())

class LayoutOptimiser(private val xml: XML, private val nodeOptimisationStrategy: NodeOptimisationStrategy) {

    fun optimise(uiDumpFile: File): OptimisedHierarchy? {
        val uiText = uiDumpFile.readText()
        val hierarchy = xml.decodeFromString<Hierarchy>(uiText)
        return hierarchy.toOptimizedUi()
    }

    private fun Hierarchy.toOptimizedUi(): OptimisedHierarchy? {
        val rotation = ScreenRotation.fromInt(rotation.toInt())
        val cleanNode = nodeOptimisationStrategy.optimise(node = node)

        // TODO: Handle null case and return meaningful message to agent
        val root = cleanNode.toUiElement() ?: return null
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
