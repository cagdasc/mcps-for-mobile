import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cacaosd.godofai.feature.ChatScreen
import com.cacaosd.godofai.feature.ChatViewModel
import com.cacaosd.ui_theme.AppTheme

@Composable
fun ChatScreenBranch(chatViewModel: ChatViewModel) {
    // Observe or manage the message list
    val chatScreenUiState by chatViewModel.chatScreenUiState.collectAsState()

    AppTheme {
        ChatScreen(
            chatScreenUiState = chatScreenUiState,
            onModelSelected = { model ->
                chatViewModel.selectModel(model)
            },
            onLoadModel = { model ->
                chatViewModel.loadModel(model)
            },
            onUnloadModel = { model ->
                chatViewModel.unloadModel(model)
            },
            onSendMessage = { userMessage ->
                chatViewModel.addUserMessage(userMessage)
            },
            onStopInteraction = {
                chatViewModel.stopInteraction()
            }
        )
    }

}