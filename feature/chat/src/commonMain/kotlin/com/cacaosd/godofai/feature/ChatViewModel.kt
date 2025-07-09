@file:OptIn(ExperimentalTime::class)

package com.cacaosd.godofai.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacaosd.godofai.feature.MessageBubble.Companion.request
import com.cacaosd.godofai.feature.MessageBubble.Companion.response
import com.cacaosd.godofai.feature.MessageOwner.Assistant
import com.cacaosd.godofai.feature.MessageOwner.Tool
import com.cacaosd.mcp.domain.AgentClient
import com.cacaosd.mcp.domain.McpMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class ChatViewModel(private val agentClient: AgentClient, private val mcpMessageFlow: MutableSharedFlow<McpMessage>) :
    ViewModel() {
    private val _chatScreenUiState = MutableStateFlow(ChatScreenUiState())
    val chatScreenUiState: StateFlow<ChatScreenUiState> = _chatScreenUiState

    init {
        collectAgentEvent()
    }

    private fun collectAgentEvent() {
        mcpMessageFlow
            .map { event ->
                when (event) {
                    is McpMessage.Request.User -> request(sender = MessageOwner.User, content = event.message)
                    is McpMessage.Response.Assistant -> response(
                        sender = Assistant,
                        content = event.content.trimIndent()
                    )

                    is McpMessage.Request.Tool -> request(
                        sender = Tool(toolName = event.toolName),
                        content = event.content
                    )

                    is McpMessage.Response.AssistantWithError -> response(
                        sender = Assistant,
                        content = "Error happened while executing the prompt",
                        throwable = event.throwable
                    )
                }
            }
            .onEach { message ->
                _chatScreenUiState.update { state ->
                    state.copy(
                        messages = listOf(message) + state.messages,
                        executionState = when (message.owner) {
                            is Assistant -> {
                                message.asResponse()?.throwable?.let { ExecutionState.Error(it) }
                                    ?: ExecutionState.Success(message.content)
                            }

                            else -> ExecutionState.Executing
                        }
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun addUserMessage() {
        viewModelScope.launch(Dispatchers.Default) {
            _chatScreenUiState.update { state ->
                state.copy(executionState = ExecutionState.Executing)
            }
            val userMessage = _chatScreenUiState.value.prompt
            mcpMessageFlow.emit(McpMessage.Request.User(message = userMessage)).also {
                agentClient.executePrompt(userMessage)
            }
        }
    }

    fun updatePrompt(prompt: String) {
        _chatScreenUiState.update { state ->
            state.copy(prompt = prompt)
        }
    }
}
