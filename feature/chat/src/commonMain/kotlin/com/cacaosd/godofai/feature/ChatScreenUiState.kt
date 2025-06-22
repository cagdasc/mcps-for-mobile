package com.cacaosd.godofai.feature

import ai.koog.prompt.message.Message

data class ChatScreenUiState(
    val messages: List<Message> = emptyList(),
    val models: List<String> = emptyList(),
    val selectedModel: String? = null,
    val isActive: Boolean = false
)
