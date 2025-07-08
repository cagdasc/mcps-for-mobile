@file:OptIn(ExperimentalEncodingApi::class, ExperimentalFoundationApi::class)

package com.cacaosd.godofai.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.cacaosd.ui_theme.AppTheme
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun ChatScreen(
    chatScreenUiState: ChatScreenUiState,
    onSendMessage: (String) -> Unit,
) {
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }

    Surface(color = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.large, vertical = AppTheme.sizes.medium),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
        ) {
            Column(
                modifier = Modifier.weight(.4f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
            ) {
                TextField(
                    value = currentMessage,
                    onValueChange = {
                        currentMessage = it
                    },
                    label = { Text(text = "Scenario", style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = MaterialTheme.shapes.small,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val input = currentMessage.text.trim()
                        if (input.isNotEmpty()) {
                            onSendMessage(input)  // Trigger the send message callback
                            currentMessage = TextFieldValue("")  // Clear the input field
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = chatScreenUiState.executionState is ExecutionState.Idle
                ) {
                    Text(
                        "Run Scenario",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (chatScreenUiState.executionState is ExecutionState.Executing) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(start = AppTheme.sizes.medium).size(AppTheme.sizes.xlarge),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Device name here", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(AppTheme.sizes.xxlarge))
                Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)) {
                    DeviceSpecBox(modifier = Modifier.weight(1f), spec = "Battery", value = "85%")
                    DeviceSpecBox(modifier = Modifier.weight(1f), spec = "Resolution", value = "1080x2400")
                    DeviceSpecBox(modifier = Modifier.weight(1f), spec = "Android", value = "13")
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(vertical = AppTheme.sizes.medium)
                    ) {
                        items(chatScreenUiState.messages) { item ->
                            ChatBubble(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceSpecBox(modifier: Modifier = Modifier, spec: String, value: String) {
    Column(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(AppTheme.sizes.xxlarge),
        verticalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
    ) {
        Text(
            text = spec,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ChatBubble(message: MessageBubble) {
    val (backgroundColor, contentColor) = when (message.owner) {
        MessageOwner.User -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        MessageOwner.Assistant -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        is MessageOwner.Tool -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
    }

    val alignment = when {
        message.isRequest() -> Alignment.TopEnd
        else -> Alignment.TopStart
    }
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = AppTheme.sizes.medium),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = AppTheme.sizes.large, vertical = AppTheme.sizes.xmedium)
                .fillMaxWidth(.4f),
            verticalArrangement = Arrangement.spacedBy(AppTheme.sizes.small)
        ) {
            Text(
                text = message.owner.displayName,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
