package com.cacaosd.droidmind.adb.layout_optimizer.strategy

import com.cacaosd.droidmind.adb.layout_optimizer.Node

class DefaultNodeOptimisationStrategy : NodeOptimisationStrategy {
    override fun optimise(node: Node): Node = node
}
