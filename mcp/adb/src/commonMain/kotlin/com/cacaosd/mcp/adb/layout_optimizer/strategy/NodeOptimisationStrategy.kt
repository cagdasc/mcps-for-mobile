package com.cacaosd.mcp.adb.layout_optimizer.strategy

import com.cacaosd.mcp.adb.layout_optimizer.Node

interface NodeOptimisationStrategy {
    fun optimise(node: Node): Node
}
