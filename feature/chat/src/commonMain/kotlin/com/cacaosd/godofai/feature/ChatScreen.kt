@file:OptIn(ExperimentalEncodingApi::class)

package com.cacaosd.godofai.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    // State to hold the current user input
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }

    Surface(color = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.large, vertical = AppTheme.sizes.medium)
        ) {
            Column(modifier = Modifier.weight(.2f)) {

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
                    // Display the list of messages
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(vertical = AppTheme.sizes.medium)
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
                                .padding(AppTheme.sizes.medium),
                            maxLines = 6
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
    // Styling the bubbles based on role
    val backgroundColor = when {
        message.isRequest() -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
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
            message.sender?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
