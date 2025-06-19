package com.cacaosd.mcp.adb.layout_optimizer.strategy

import com.cacaosd.mcp.adb.layout_optimizer.Node

class CollapseParentNodeStrategy : NodeOptimisationStrategy {
    override fun optimise(node: Node): Node {
        return node.cleanAndReindex() ?: error("Node cannot be optimised.")
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
}