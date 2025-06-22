@file:OptIn(ExperimentalEncodingApi::class)

package com.cacaosd.godofai.feature

import ai.koog.prompt.message.Message
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

val kotlinCodeBlockRegex = """```kotlin\s*([\s\S]*?)```""".toRegex()

fun extractKotlinCodeBlocks(text: String): List<String> {
    return kotlinCodeBlockRegex.findAll(text)
        .map { it.groupValues[1] }
        .toList()
}

@Composable
fun ChatScreen(
    chatScreenUiState: ChatScreenUiState,
    onModelSelected: (String) -> Unit,
    onLoadModel: (String?) -> Unit,
    onUnloadModel: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onStopInteraction: () -> Unit,
) {
    // State to hold the current user input
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }


    val selectedModelEmpty = "Select Model"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.padding(8.dp).weight(1f)) {
                Text(
                    text = chatScreenUiState.selectedModel ?: selectedModelEmpty,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                        .padding(8.dp)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    chatScreenUiState.models.forEach { model ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            onModelSelected(model)
                        }, text = { Text(text = model) })
                    }
                }
            }
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onLoadModel(chatScreenUiState.selectedModel) },
                    enabled = chatScreenUiState.selectedModel != selectedModelEmpty
                ) {
                    Text("Load Model")
                }
                Button(
                    onClick = { onUnloadModel(chatScreenUiState.selectedModel) },
                    enabled = chatScreenUiState.selectedModel != selectedModelEmpty
                ) {
                    Text("Unload Model")
                }
            }
        }

        // Display the list of messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            reverseLayout = true  // Display newest messages at the bottom
        ) {
            items(chatScreenUiState.messages.asReversed()) { item ->
                ChatBubble(item)
            }
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Input text field
            BasicTextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                maxLines = 1,
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send button
            Button(
                onClick = {
                    val input = currentMessage.text.trim()
                    if (input.isNotEmpty()) {
                        onSendMessage(input)  // Trigger the send message callback
                        currentMessage = TextFieldValue("")  // Clear the input field
                    }
                },
            ) {
                Text("Send", color = Color.White)
            }
            if (chatScreenUiState.isActive) {
                Button(onClick = { onStopInteraction() }) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }

            val coroutineScope = rememberCoroutineScope()


            Button(
                modifier = Modifier.toolingGraphicsLayer(),
                onClick = {
                    coroutineScope.launch {

                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Camera",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    // Styling the bubbles based on role
    val backgroundColor = if (message.role == Message.Role.User) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val alignment = if (message.role == Message.Role.User) Alignment.TopEnd else Alignment.TopStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = message.content,
            modifier = Modifier
                .background(backgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(8.dp),
            color = Color.Black
        )
    }
}
