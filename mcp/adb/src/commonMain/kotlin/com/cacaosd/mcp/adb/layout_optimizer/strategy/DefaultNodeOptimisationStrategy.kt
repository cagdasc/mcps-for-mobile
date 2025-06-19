package com.cacaosd.mcp.adb.layout_optimizer.strategy

import com.cacaosd.mcp.adb.layout_optimizer.Node

class DefaultNodeOptimisationStrategy : NodeOptimisationStrategy {
    override fun optimise(node: Node): Node = node
}
