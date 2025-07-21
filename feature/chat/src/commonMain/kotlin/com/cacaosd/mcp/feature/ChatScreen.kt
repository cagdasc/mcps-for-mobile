@file:OptIn(ExperimentalEncodingApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.cacaosd.mcp.feature

import ChatScreenAction
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.cacaosd.mcp.feature.composable.ChipFlowRow
import com.cacaosd.mcp.feature.composable.GenericDropdown
import com.cacaosd.ui_theme.AppTheme
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun ChatScreen(
    chatScreenUiState: ChatScreenUiState,
    onAction: (ChatScreenAction) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.large, vertical = AppTheme.sizes.medium),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
        ) {
            ScenarioInputContainer(chatScreenUiState, onAction)
            ChatContainer(chatScreenUiState, onAction)
        }
    }
}

@Composable
private fun RowScope.ChatContainer(chatScreenUiState: ChatScreenUiState, onAction: (ChatScreenAction) -> Unit) {
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                AppTheme.sizes.medium
            )
        ) {
            GenericDropdown(
                items = chatScreenUiState.deviceDataList,
                selectedItem = chatScreenUiState.selectedDevice,
                onItemSelected = { device ->
                    onAction(ChatScreenAction.DeviceSelected(device))
                },
                label = "Device",
                placeholder = "Select device",
                itemText = { it.name },
            )

            if (chatScreenUiState.selectedDevice != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
                ) {
                    DeviceSpecBox(
                        icon = Icons.Filled.BatteryFull,
                        spec = "Battery",
                        value = "${chatScreenUiState.selectedDevice.batteryLevel}%"
                    )
                    DeviceSpecBox(
                        icon = Icons.Filled.PhoneAndroid,
                        spec = "Screen",
                        value = chatScreenUiState.selectedDevice.screenSize ?: "Unknown Size"
                    )
                    DeviceSpecBox(
                        icon = Icons.Filled.Api,
                        spec = "API Level",
                        value = chatScreenUiState.selectedDevice.osVersion
                    )
                }
            }
        }

        if (chatScreenUiState.installedApps.isNotEmpty()) {
            GenericDropdown(
                items = chatScreenUiState.installedApps,
                selectedItem = chatScreenUiState.selectedApp,
                onItemSelected = { app ->
                    onAction(ChatScreenAction.AppSelected(app))
                },
                label = "App",
                placeholder = "Select an app",
                itemText = { it.packageName },
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                reverseLayout = true,
                contentPadding = PaddingValues(all = AppTheme.sizes.medium)
            ) {
                items(chatScreenUiState.messages) { item ->
                    ChatBubble(item)
                }
            }
        }
    }
}

@Composable
private fun RowScope.ScenarioInputContainer(
    chatScreenUiState: ChatScreenUiState,
    onAction: (ChatScreenAction) -> Unit
) {
    Column(
        modifier = Modifier.weight(.4f),
        verticalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
    ) {
        TextField(
            value = chatScreenUiState.prompt,
            onValueChange = {
                onAction(ChatScreenAction.PromptChanged(it))
            },
            label = {
                Column(
                    modifier = Modifier.padding(bottom = AppTheme.sizes.small),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.sizes.small)
                ) {
                    Text(text = "Scenario", style = MaterialTheme.typography.labelLarge)
                    if (chatScreenUiState.chipItems.isNotEmpty()) {
                        ChipFlowRow(
                            items = chatScreenUiState.chipItems.toList(),
                            onItemToggle = { item ->
                                onAction(ChatScreenAction.RemoveChip(item))
                            },
                            chipText = { it.label },
                        )
                    }
                }
            },
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
            enabled = chatScreenUiState.executionState !is ExecutionState.Executing
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (chatScreenUiState.prompt.isNotEmpty()) {
                    onAction(ChatScreenAction.RunScenarioClicked)
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            enabled = chatScreenUiState.prompt.isNotEmpty() && chatScreenUiState.executionState !is ExecutionState.Executing
        ) {
            Text(
                "Run Scenario",
                style = MaterialTheme.typography.bodyLarge
            )

            when (chatScreenUiState.executionState) {
                is ExecutionState.Executing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(start = AppTheme.sizes.medium).size(AppTheme.sizes.xlarge),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                }

                is ExecutionState.Error -> {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.padding(start = AppTheme.sizes.medium).size(AppTheme.sizes.xlarge),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun DeviceSpecBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    spec: String,
    value: String
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(AppTheme.sizes.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.sizes.medium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = spec,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
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
        modifier = Modifier.fillMaxWidth().padding(AppTheme.sizes.medium),
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
                color = contentColor
            )

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
        }
    }
}
