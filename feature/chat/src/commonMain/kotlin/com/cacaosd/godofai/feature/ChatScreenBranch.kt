import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cacaosd.godofai.feature.ChatScreen
import com.cacaosd.godofai.feature.ChatViewModel

@Composable
fun ChatScreenBranch(chatViewModel: ChatViewModel) {

    val chatScreenUiState by chatViewModel.chatScreenUiState.collectAsState()

    ChatScreen(
        chatScreenUiState = chatScreenUiState,
        onRunScenarioClicked = {
            chatViewModel.addUserMessage()
        },
        onTextChanged = {
            chatViewModel.updatePrompt(it)
        }
    )
}
