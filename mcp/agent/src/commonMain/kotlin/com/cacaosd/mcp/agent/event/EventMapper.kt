package com.cacaosd.mcp.agent.event

import ai.koog.prompt.message.Message
import com.cacaosd.mcp.domain.McpMessage

class EventMapper {
    fun mapToMcpMessage(message: Message.Response): McpMessage {
        return when (message) {
            is Message.Assistant -> McpMessage.Response.Assistant(
                content = message.content,
                finishReason = message.finishReason
            )

            is Message.Tool.Call -> McpMessage.Request.Tool(
                toolName = message.tool,
                content = message.content
            )
        }
    }
}
