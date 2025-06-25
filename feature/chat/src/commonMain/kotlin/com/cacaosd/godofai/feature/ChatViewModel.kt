@file:OptIn(ExperimentalTime::class)

package com.cacaosd.godofai.feature

import ai.koog.agents.core.agent.AIAgent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacaosd.godofai.feature.MessageBubble.Companion.request
import com.cacaosd.godofai.feature.MessageBubble.Companion.response
import com.cacaosd.mcp.agent.event.AgentEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class ChatViewModel(private val agent: AIAgent, agentEventFlow: SharedFlow<AgentEvent>) :
    ViewModel() {
    private val _chatScreenUiState = MutableStateFlow(ChatScreenUiState())
    val chatScreenUiState: StateFlow<ChatScreenUiState> = _chatScreenUiState

    init {
        agentEventFlow
            .map { event ->
                when (event) {
                    is AgentEvent.AssistantMessage -> listOf(
                        response(
                            sender = "AI",
                            content = event.content
                        )
                    )

                    is AgentEvent.Prompt -> {
//                        event.messages.mapNotNull { message ->
//                            when (message) {
//                                is Message.System -> response(sender = "System", content = message.content)
//                                is Message.User -> request(sender = "User", content = message.content)
//                                is Message.Tool.Call -> null
//                                is Message.Assistant -> null
//                                is Message.Tool.Result -> null
//                            }
//                        }
                        emptyList()
                    }

                    is AgentEvent.ToolMessage -> listOf(response(sender = "Tool - ${event.toolName}", content = event.content))
                }
            }
            .onEach {
                _chatScreenUiState.update { state ->
                    state.copy(messages = state.messages + it)
                }
            }.launchIn(viewModelScope)
    }

    fun addUserMessage(content: String, image: String? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            _chatScreenUiState.update { state ->
                state.copy(messages = state.messages + request(sender = "User", content = content))
            }
            agent.run(content)
        }
    }
}
