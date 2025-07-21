@file:OptIn(ExperimentalTime::class, ObsoleteCoroutinesApi::class)

package com.cacaosd.mcp.feature

import ChatScreenAction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacaosd.mcp.adb.device_controller.DeviceController
import com.cacaosd.mcp.domain.AgentClient
import com.cacaosd.mcp.domain.McpMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

private const val DEVICE_POLL_INTERVAL = 5000L
private const val INSTALLED_PACKAGES_POLL_INTERVAL = 20_000L

class ChatViewModel(
    private val agentClient: AgentClient,
    private val mcpMessageFlow: MutableSharedFlow<McpMessage>,
    private val deviceController: DeviceController
) : ViewModel() {
    private val _chatScreenUiState = MutableStateFlow(ChatScreenUiState())
    val chatScreenUiState: StateFlow<ChatScreenUiState> = _chatScreenUiState

    private var installedAppsJob: Job? = null

    init {
        collectAgentEvent()
        pollForConnectedDevices()
    }

    fun onAction(action: ChatScreenAction) {
        when (action) {
            is ChatScreenAction.DeviceSelected -> {
                setSelectedDevice(action.deviceData)
            }

            is ChatScreenAction.PromptChanged -> {
                updatePrompt(action.prompt)
            }

            ChatScreenAction.RunScenarioClicked -> {
                addUserMessage()
            }

            is ChatScreenAction.AppSelected -> setSelectedApp(action.installedApp)
            is ChatScreenAction.RemoveChip -> {
                _chatScreenUiState.update { state ->
                    val chipItems = state.chipItems.toMutableSet().apply {
                        remove(action.chipItem)
                    }
                    state.copy(chipItems = chipItems)
                }
            }
        }
    }

    private fun pollForConnectedDevices() {
        ticker(DEVICE_POLL_INTERVAL, 0L).receiveAsFlow()
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

    private fun pollForInstalledApp(): Job {
        return ticker(INSTALLED_PACKAGES_POLL_INTERVAL, 0L).receiveAsFlow()
            .onEach {
                val serial = _chatScreenUiState.value.selectedDevice?.serial
                val listOfApps = withContext(Dispatchers.IO) { deviceController.listInstalledPackages(serial) }
                _chatScreenUiState.update { state ->
                    state.copy(installedApps = listOfApps.sorted().map { InstalledApp(packageName = it) })
                }
            }.launchIn(viewModelScope)
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

    private fun setSelectedDevice(deviceData: DeviceData) {
        _chatScreenUiState.update { state ->
            val chipItems = state.chipItems.toMutableSet().apply {
                add(deviceData)
            }
            state.copy(selectedDevice = deviceData, chipItems = chipItems)
        }

        installedAppsJob?.cancel()
        installedAppsJob = pollForInstalledApp()
    }

    private fun setSelectedApp(installedApp: InstalledApp) {
        _chatScreenUiState.update { state ->
            val chipItems = state.chipItems.toMutableSet().apply {
                add(installedApp)
            }
            state.copy(selectedApp = installedApp, chipItems = chipItems)
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
