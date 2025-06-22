@file:OptIn(ExperimentalTime::class)

package com.cacaosd.godofai.feature

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.agent.chatAgentStrategy
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacaosd.mcp.agent.event.AgentEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class ChatViewModel(private val agent: AIAgent, agentEventFlow: MutableSharedFlow<AgentEvent>) :
    ViewModel() {
    // StateFlow to hold the list of chat messages
    private val _chatScreenUiState = MutableStateFlow(ChatScreenUiState())
    val chatScreenUiState: StateFlow<ChatScreenUiState> = _chatScreenUiState

    private var job: Job? = null

    init {
        agentEventFlow.onEach { event ->
            if (event is AgentEvent.Prompt) {
                _chatScreenUiState.update { it.copy(messages = event.messages) }
            } else {
                println(event)
            }

        }.launchIn(viewModelScope)
    }

    fun addUserMessage(content: String, image: String? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            agent.run(content)
        }
    }

    private fun getModels() {
        viewModelScope.launch(Dispatchers.Default) {

        }
    }

    fun selectModel(model: String) {
        _chatScreenUiState.update { state -> state.copy(selectedModel = model) }

    }

    fun loadModel(model: String?) {
    }

    /**
     * Unload a specific model.
     */
    fun unloadModel(model: String?) {

    }

    fun stopInteraction() {
        if (job?.isActive == true) {
            job?.cancel()
        }
    }

    fun createAgent(id: String, toolRegistry: ToolRegistry = ToolRegistry.EMPTY): AIAgent {
        return AIAgent(
//            systemPrompt = """
//        You are a helpful AI assistant that can interact with a web browser.
//        You can navigate to websites, accept cookies, and perform actions on web pages.
//        Use the tools provided to accomplish tasks.
//    """.trimIndent(),
            llmModel = LLModel(
                provider = LLMProvider.Ollama,
                id = id,
                capabilities = listOf(
                    LLMCapability.Temperature,
                    LLMCapability.Schema.JSON.Simple,
                    LLMCapability.Tools,
                    LLMCapability.ToolChoice,
                )
            ),
            toolRegistry = toolRegistry,
            strategy = chatAgentStrategy(),
            executor = simpleOllamaAIExecutor()
        )
    }

}
