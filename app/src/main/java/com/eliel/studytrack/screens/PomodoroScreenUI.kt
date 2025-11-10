package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.data.DataSource
import kotlinx.coroutines.delay

enum class TimerState {
    STOPPED, RUNNING, PAUSED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreenUI(navController: NavHostController) {
    val initialPomodoroTime = DataSource.pomodoroTime.value*60

    var timeRemaining by remember { mutableStateOf(initialPomodoroTime ) }
    var timerState by remember { mutableStateOf(TimerState.STOPPED) }

    val pomodorosCompleted = DataSource.pomodorosCompleted.value
    val focusedTimeToday = DataSource.focusedTimeToday.value
    val pomodoroTime by DataSource.pomodoroTime
    val shortBreak by DataSource.shortBreakTime
    val longBreak by DataSource.longBreakTime


    val subjects = DataSource.subjects
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Escolha a matéria para estudar") }
    var expanded by remember { mutableStateOf(false) }


    LaunchedEffect(timerState) {
        if (timerState == TimerState.RUNNING) {
            while (timeRemaining > 0 && timerState == TimerState.RUNNING) {
                delay(1000L)
                timeRemaining--
            }
            if (timeRemaining == 0) {
                timerState = TimerState.STOPPED
                DataSource.pomodorosCompleted.value = DataSource.pomodorosCompleted.value + 1
                val currentFocusedTime = DataSource.focusedTimeToday.value
                val minutes = currentFocusedTime.replace("min", "").toIntOrNull() ?: 0
                DataSource.focusedTimeToday.value = "${minutes + DataSource.pomodoroTime.value}min"

                timeRemaining = initialPomodoroTime
            }
        }
    }

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeDisplay = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Pomodoro Timer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Técnica de produtividade para estudos",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (timerState) {
                    TimerState.RUNNING -> Color(0xFFE53E3E)
                    TimerState.PAUSED -> Color(0xFFFFC107)
                    TimerState.STOPPED -> Color(0xFFE53E3E)
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_book_open),
                        contentDescription = "Tempo de Estudo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tempo de Estudo",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = timeDisplay,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LinearProgressIndicator(
                    progress = 1f - (timeRemaining.toFloat() / initialPomodoroTime.toFloat()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            timerState = when (timerState) {
                                TimerState.STOPPED -> TimerState.RUNNING
                                TimerState.RUNNING -> TimerState.PAUSED
                                TimerState.PAUSED -> TimerState.RUNNING
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFE53E3E)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = when (timerState) {
                                    TimerState.STOPPED -> R.drawable.ic_play
                                    TimerState.RUNNING -> R.drawable.ic_play
                                    TimerState.PAUSED -> R.drawable.ic_play
                                }
                            ),
                            contentDescription = "Controle",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (timerState) {
                                TimerState.STOPPED -> "Iniciar"
                                TimerState.RUNNING -> "Pausar"
                                TimerState.PAUSED -> "Continuar"
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            timerState = TimerState.STOPPED
                            timeRemaining = initialPomodoroTime
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_reset),
                            contentDescription = "Resetar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resetar", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = "Selecionar Matéria",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Selecionar Matéria",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Escolha a matéria para estudar") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(subject.color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(subject.name)
                                }
                            },
                            onClick = {
                                selectedSubject = subject.name
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trophy),
                        contentDescription = "Trophy",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Estatísticas da Sessão",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = DataSource.pomodorosCompleted.value.toString(),
                            color = Color(0xFF00C853),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Pomodoros Concluídos", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = DataSource.focusedTimeToday.value,
                            color = Color(0xFF00C853),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Tempo focado hoje", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dicas para uma sessão produtiva:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Elimine distrações (celular, redes sociais)", fontSize = 14.sp)
                Text("• Mantenha água por perto", fontSize = 14.sp)
                Text("• Nas pausas, levante-se e movimente-se", fontSize = 14.sp)
                Text("• A cada 4 pomodoros, faça uma pausa mais longa", fontSize = 14.sp)
            }
        }
    }
}