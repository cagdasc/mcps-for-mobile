package com.cacaosd.godofai.feature

import java.time.Instant
import java.util.*

data class ChatScreenUiState(
    val messages: List<MessageBubble> = emptyList(),
)

/**
 * Represents a message bubble in a conversation.
 *
 * This sealed interface defines the different types of messages that can appear
 * in a chat or conversation context, providing type safety and exhaustive handling.
 */
sealed interface MessageBubble {
    val id: String
    val sender: String?
    val content: String
    val timestamp: Instant
    val metadata: Map<String, Any>

    /**
     * Represents a request/input message from a user or system.
     *
     * @param id Unique identifier for the message.
     * @param sender The sender of the request (null for anonymous).
     * @param content The actual message content.
     * @param timestamp When the message was created.
     * @param metadata Additional metadata associated with the message.
     */
    data class Request(
        override val id: String = UUID.randomUUID().toString(),
        override val sender: String? = null,
        override val content: String,
        override val timestamp: Instant = Instant.now(),
        override val metadata: Map<String, Any> = emptyMap()
    ) : MessageBubble {
        init {
            require(content.isNotBlank()) { "Request content cannot be blank" }
        }
    }

    /**
     * Represents a response/output message from an AI agent or system.
     *
     * @param id Unique identifier for the message.
     * @param sender The sender of the response (null for anonymous).
     * @param content The actual message content.
     * @param timestamp When the message was created.
     * @param metadata Additional metadata associated with the message.
     * @param requestId Optional ID of the request this response is answering.
     */
    data class Response(
        override val id: String = UUID.randomUUID().toString(),
        override val sender: String? = null,
        override val content: String,
        override val timestamp: Instant = Instant.now(),
        override val metadata: Map<String, Any> = emptyMap(),
        val requestId: String? = null
    ) : MessageBubble {
        init {
            require(content.isNotBlank()) { "Response content cannot be blank" }
        }
    }

    companion object {
        /**
         * Creates a request message with the given content and optional sender.
         */
        fun request(content: String, sender: String? = null): Request {
            return Request(sender = sender, content = content)
        }

        /**
         * Creates a response message with the given content and optional sender.
         */
        fun response(content: String, sender: String? = null, requestId: String? = null): Response {
            return Response(sender = sender, content = content, requestId = requestId)
        }

        /**
         * Creates a request message with additional metadata.
         */
        fun requestWithMetadata(
            content: String,
            sender: String? = null,
            metadata: Map<String, Any>
        ): Request {
            return Request(sender = sender, content = content, metadata = metadata)
        }

        /**
         * Creates a response message with additional metadata.
         */
        fun responseWithMetadata(
            content: String,
            sender: String? = null,
            requestId: String? = null,
            metadata: Map<String, Any>
        ): Response {
            return Response(
                sender = sender,
                content = content,
                requestId = requestId,
                metadata = metadata
            )
        }
    }
}

/**
 * Extension functions for MessageBubble
 */

/**
 * Checks if this message is a request.
 */
fun MessageBubble.isRequest(): Boolean = this is MessageBubble.Request

/**
 * Checks if this message is a response.
 */
fun MessageBubble.isResponse(): Boolean = this is MessageBubble.Response

/**
 * Gets the message as a Request if it is one, null otherwise.
 */
fun MessageBubble.asRequest(): MessageBubble.Request? = this as? MessageBubble.Request

/**
 * Gets the message as a Response if it is one, null otherwise.
 */
fun MessageBubble.asResponse(): MessageBubble.Response? = this as? MessageBubble.Response

/**
 * Returns a formatted string representation of the message.
 */
fun MessageBubble.format(): String {
    val senderPrefix = sender?.let { "[$it] " } ?: ""
    val typePrefix = when (this) {
        is MessageBubble.Request -> "REQUEST"
        is MessageBubble.Response -> "RESPONSE"
    }
    return "$typePrefix: $senderPrefix$content"
}
