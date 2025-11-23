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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.data.DataSource
import com.eliel.studytrack.data.firestore.SubjectRepository
import com.eliel.studytrack.data.firestore.PomodoroRepository
import com.eliel.studytrack.data.firestore.PomodoroSessionData
import kotlinx.coroutines.launch
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


    var subjects by remember { mutableStateOf(listOf<com.eliel.studytrack.data.firestore.SubjectData>()) }
    var selectedSubject by remember { mutableStateOf("Escolha a matéria para estudar") }
    var expanded by remember { mutableStateOf(false) }
    var sessions by remember { mutableStateOf(listOf<PomodoroSessionData>()) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            subjects = SubjectRepository.getSubjects()
            selectedSubject = subjects.firstOrNull()?.name ?: selectedSubject
            try { sessions = PomodoroRepository.getSessions() } catch (_: Exception) {}
        }
    }


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

                val chosen = selectedSubject.takeIf { it.isNotBlank() && it != "Escolha a matéria para estudar" } ?: "Geral"
                scope.launch {
                    try {
                        PomodoroRepository.addSession(
                            PomodoroSessionData(
                                subject = chosen,
                                minutes = DataSource.pomodoroTime.value
                            )
                        )
                        sessions = PomodoroRepository.getSessions()
                    } catch (_: Exception) {}
                }

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
                    text = stringResource(R.string.pomodoro_timer),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.tecnica_de_produtividade_para_estudos),
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
                        contentDescription = stringResource(R.string.tempo_de_estudo),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.tempo_de_estudo),
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
                            contentDescription = stringResource(R.string.controle),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (timerState) {
                                TimerState.STOPPED -> stringResource(R.string.iniciar)
                                TimerState.RUNNING -> stringResource(R.string.pausar)
                                TimerState.PAUSED -> stringResource(R.string.continuar)
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
                            contentDescription = stringResource(R.string.resetar),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.resetar), fontWeight = FontWeight.Medium)
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
                    contentDescription = stringResource(R.string.selecionar_materia),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.selecionar_materia),
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
                    placeholder = { Text(stringResource(R.string.escolha_a_materia_para_estudar)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
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
                                            .background(Color(subject.color), CircleShape)
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
                        painter = painterResource(id = R.drawable.ic_trophy_fill),
                        contentDescription = "Trophy",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.stats_session),
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
                    fun isToday(ts: com.google.firebase.Timestamp): Boolean {
                        val cal = java.util.Calendar.getInstance();
                        val todayY = cal.get(java.util.Calendar.YEAR)
                        val todayD = cal.get(java.util.Calendar.DAY_OF_YEAR)
                        val c2 = java.util.Calendar.getInstance(); c2.time = ts.toDate()
                        return todayY == c2.get(java.util.Calendar.YEAR) && todayD == c2.get(java.util.Calendar.DAY_OF_YEAR)
                    }
                    val todaySessions = sessions.filter { isToday(it.completedAt) }
                    val todayMinutes = todaySessions.sumOf { it.minutes }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = todaySessions.size.toString(), color = Color(0xFF00C853), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.pomodoros_concluidos), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${todayMinutes}min", color = Color(0xFF00C853), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.tempo_focado_hoje), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    text = stringResource(R.string.dicas_para_uma_sess_o_produtiva),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.elimine_distracoes_celular_redes_sociais), fontSize = 14.sp)
                Text(stringResource(R.string.mantenha_agua_por_perto), fontSize = 14.sp)
                Text(stringResource(R.string.nas_pausas_levante_se_e_movimente_se), fontSize = 14.sp)
                Text(stringResource(R.string.a_cada_4_pomodoros_faca_uma_pausa_mais_longa), fontSize = 14.sp)
            }
        }
    }
}