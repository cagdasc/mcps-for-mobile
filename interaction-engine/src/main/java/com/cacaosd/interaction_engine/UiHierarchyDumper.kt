package com.cacaosd.interaction_engine

import android.content.Context
import android.util.Xml
import android.view.accessibility.AccessibilityNodeInfo
import com.cacaosd.interaction_engine.util.saveToDownloads
import org.xmlpull.v1.XmlSerializer

/** Helper class to dump the UI hierarchy into a file under `/sdcard` */
class UiHierarchyDumper {

    fun dump(
        root: AccessibilityNodeInfo,
        context: Context,
        fileName: String
    ) {
        val serializer: XmlSerializer = Xml.newSerializer()

        context.saveToDownloads(fileName = fileName) {
            serializer.setOutput(it, "UTF-8")
            serializer.startDocument("UTF-8", true)
            serializer.startTag("", "hierarchy")

            dumpNodeRecursive(serializer, root, 0)

            serializer.endTag("", "hierarchy")
            serializer.endDocument()
            serializer.flush()
        }
    }

    /**
     * Recursively dumps nodes to XML
     */
    private fun dumpNodeRecursive(serializer: XmlSerializer, node: AccessibilityNodeInfo?, index: Int) {
        if (node == null) return

        serializer.startTag("", "node")

        val rect = android.graphics.Rect()
        node.getBoundsInScreen(rect)

        // Collect attributes similar to uiautomator dump
        serializer.attribute("", "index", index.toString())
        serializer.attribute("", "text", node.text?.toString() ?: "")
        serializer.attribute("", "resource-id", node.viewIdResourceName ?: "")
        serializer.attribute("", "class", node.className?.toString() ?: "")
        serializer.attribute("", "package", node.packageName?.toString() ?: "")
        serializer.attribute("", "content-desc", node.contentDescription?.toString() ?: "")
        serializer.attribute("", "checkable", node.isCheckable.toString())
        serializer.attribute("", "checked", node.isChecked.toString())
        serializer.attribute("", "clickable", node.isClickable.toString())
        serializer.attribute("", "enabled", node.isEnabled.toString())
        serializer.attribute("", "focusable", node.isFocusable.toString())
        serializer.attribute("", "focused", node.isFocused.toString())
        serializer.attribute("", "scrollable", node.isScrollable.toString())
        serializer.attribute("", "long-clickable", node.isLongClickable.toString())
        serializer.attribute("", "password", node.isPassword.toString())
        serializer.attribute("", "selected", node.isSelected.toString())
        serializer.attribute("", "bounds", "[${rect.left},${rect.top}][${rect.right},${rect.bottom}]")

        serializer.attribute("", "drawing-order", node.drawingOrder.toString())
        serializer.attribute("", "hint", node.hintText?.toString() ?: "")

        // Recurse children
        for (i in 0 until node.childCount) {
            dumpNodeRecursive(serializer, node.getChild(i), i)
        }

        serializer.endTag("", "node")
    }
}
