import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cacaosd.mcp.feature.ChatScreen
import com.cacaosd.mcp.feature.ChatViewModel
import com.cacaosd.mcp.feature.DeviceData

@Composable
fun ChatScreenBranch(chatViewModel: ChatViewModel) {

    val chatScreenUiState by chatViewModel.chatScreenUiState.collectAsState()

    ChatScreen(
        chatScreenUiState = chatScreenUiState,
        onAction = chatViewModel::onAction
    )
}

sealed interface ChatScreenAction {
    data class DeviceSelected(val deviceData: DeviceData) : ChatScreenAction
    data class PromptChanged(val prompt: String) : ChatScreenAction
    object RunScenarioClicked : ChatScreenAction
}
