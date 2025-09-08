package com.cacaosd.droidmind.adb.layout_optimizer.strategy

import com.cacaosd.droidmind.adb.layout_optimizer.Node

interface NodeOptimisationStrategy {
    fun optimise(node: Node): Node
}
