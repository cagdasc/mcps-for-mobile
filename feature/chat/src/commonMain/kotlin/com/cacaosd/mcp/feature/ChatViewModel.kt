@file:OptIn(ExperimentalTime::class, ObsoleteCoroutinesApi::class)

package com.cacaosd.mcp.feature

import ChatScreenAction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacaosd.mcp.adb.device_controller.DeviceController
import com.cacaosd.mcp.domain.AgentClient
import com.cacaosd.mcp.domain.McpMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

class ChatViewModel(
    private val agentClient: AgentClient,
    private val mcpMessageFlow: MutableSharedFlow<McpMessage>,
    private val deviceController: DeviceController
) :
    ViewModel() {
    private val _chatScreenUiState = MutableStateFlow(ChatScreenUiState())
    val chatScreenUiState: StateFlow<ChatScreenUiState> = _chatScreenUiState

    init {
        collectAgentEvent()

        ticker(5000L, 0L).receiveAsFlow()
            .onEach {
                val devices = withContext(Dispatchers.IO) { deviceController.getDevices() }
                _chatScreenUiState.update { state ->
                    state.copy(deviceDataList = devices.map {
                        DeviceData(
                            name = it.name,
                            serial = it.serial,
                            batteryLevel = it.batteryLevel,
                            screenSize = it.dimensions,
                            osVersion = it.osVersion
                        )
                    })
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: ChatScreenAction) {
        when (action) {
            is ChatScreenAction.DeviceSelected -> {
                setSelectedDevice(action)
            }

            is ChatScreenAction.PromptChanged -> {
                updatePrompt(action.prompt)
            }

            ChatScreenAction.RunScenarioClicked -> {
                addUserMessage()
            }
        }
    }

    private fun collectAgentEvent() {
        mcpMessageFlow
            .map { event ->
                when (event) {
                    is McpMessage.Request.User -> MessageBubble.Companion.request(
                        sender = MessageOwner.User,
                        content = event.message
                    )

                    is McpMessage.Response.Assistant -> MessageBubble.Companion.response(
                        sender = MessageOwner.Assistant,
                        content = event.content.trimIndent()
                    )

                    is McpMessage.Request.Tool -> MessageBubble.Companion.request(
                        sender = MessageOwner.Tool(toolName = event.toolName),
                        content = event.content
                    )

                    is McpMessage.Response.AssistantWithError -> MessageBubble.Companion.response(
                        sender = MessageOwner.Assistant,
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
                            is MessageOwner.Assistant -> {
                                message.asResponse()?.throwable?.let { ExecutionState.Error(it) }
                                    ?: ExecutionState.Success(message.content)
                            }

                            else -> ExecutionState.Executing
                        }
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun setSelectedDevice(action: ChatScreenAction.DeviceSelected) {
        _chatScreenUiState.update { state ->
            state.copy(selectedDevice = action.deviceData)
        }
    }

    private fun addUserMessage() {
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

    private fun updatePrompt(prompt: String) {
        _chatScreenUiState.update { state ->
            state.copy(prompt = prompt)
        }
    }
}
