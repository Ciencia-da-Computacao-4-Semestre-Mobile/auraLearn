package com.eliel.studytrack.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eliel.studytrack.R
import com.eliel.studytrack.data.ChatTutorUiState
import com.eliel.studytrack.data.ChatTutorViewModel
import com.eliel.studytrack.data.ChatTutorViewModel.sendMessage
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTutorScreen(
    navController: com.eliel.studytrack.screens.FakeNavController,
    chatViewModel: ChatTutorViewModel = viewModel()
) {
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }

    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ChatTutor)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.como_posso_te_ajudar),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    items(messages) { message ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                color = if (message.isUser) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    color = if (message.isUser)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }


                if (chatUiState is ChatTutorUiState.Loading) {
                    item {
                        TypingAnimation()
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.digite_sua_pergunta)) },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (userInput.text.isNotBlank()) Color.Black
                                else Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (userInput.text.isNotBlank()) {
                                    val question = userInput.text
                                    messages = messages + ChatMessage(question, true)
                                    userInput = TextFieldValue("")
                                    question.sendMessage()
                                }
                            },
                            enabled = userInput.text.isNotBlank(),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = "Enviar",
                                tint = Color.White
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
        }

        LaunchedEffect(chatUiState) {
            when (val state = chatUiState) {
                is ChatTutorUiState.Success -> {
                    messages = messages + ChatMessage(state.answer, isUser = false)
                }
                is ChatTutorUiState.Error -> {
                    messages = messages + ChatMessage("Erro: ${state.error}", false)
                }
                else -> {}
            }
        }


        LaunchedEffect(messages, chatUiState) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size)
            }
        }
    }
}

@Composable
fun TypingAnimation() {
    val dots = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            dots.animateTo(3f, animationSpec = tween(durationMillis = 900))
            dots.snapTo(0f)
        }
    }

    val dotCount = dots.value.toInt()

    Text(
        text = "Digitando" + ".".repeat(dotCount),
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}
