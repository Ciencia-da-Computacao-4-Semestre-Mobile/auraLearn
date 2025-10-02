package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTutorScreen(navController: NavHostController) {
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Tutor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Voltar",
                            tint = Color.Black
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
                                text = "Como posso te ajudar?",
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
                                color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    color = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Digite sua pergunta...") },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (userInput.text.isNotBlank()) Color.Black else Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (userInput.text.isNotBlank()) {
                                    messages = messages + ChatMessage(userInput.text, isUser = true)
                                    userInput = TextFieldValue("")
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
    }
}
