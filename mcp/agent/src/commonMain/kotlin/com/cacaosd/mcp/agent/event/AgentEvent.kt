package com.cacaosd.mcp.agent.event

import ai.koog.prompt.message.Message

// TODO: This should be domain model
sealed class AgentEvent {
//    data class ToolCall(val toolName: String, val args: String) : AgentEvent() {
//        override fun toString(): String = "Tool called: $toolName with args $args"
//    }
//
//    data class ToolResult(val toolName: String, val result: String) : AgentEvent()
    data class AssistantMessage(val content: String, val finishReason: String? = null) : AgentEvent()
    data class ToolMessage(val toolName: String, val content: String) : AgentEvent()
    data class Prompt(val messages: List<Message>) : AgentEvent()
}
