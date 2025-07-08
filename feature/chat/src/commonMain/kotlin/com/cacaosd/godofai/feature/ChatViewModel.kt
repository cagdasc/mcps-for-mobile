@file:OptIn(ExperimentalTime::class)

package com.cacaosd.godofai.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacaosd.godofai.feature.MessageBubble.Companion.request
import com.cacaosd.godofai.feature.MessageBubble.Companion.response
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
                        sender = MessageOwner.Assistant,
                        content = event.content.trimIndent()
                    )

                    is McpMessage.Request.Tool -> request(
                        sender = MessageOwner.Tool(toolName = event.toolName),
                        content = event.content
                    )
                }
            }
            .onEach {
                _chatScreenUiState.update { state ->
                    state.copy(
                        messages = listOf(it) + state.messages,
                        executionState = if (it.owner is MessageOwner.Assistant) {
                            ExecutionState.Success()
                        } else {
                            ExecutionState.Executing
                        }
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun addUserMessage(content: String, image: String? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            _chatScreenUiState.update { state ->
                state.copy(executionState = ExecutionState.Executing)
            }
            mcpMessageFlow.emit(McpMessage.Request.User(message = content)).also {
                agentClient.executePrompt(content)
            }
        }
    }
}
