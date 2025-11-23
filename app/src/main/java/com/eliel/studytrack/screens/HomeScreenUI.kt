package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.Screen
import com.eliel.studytrack.data.DataSource
import com.eliel.studytrack.data.Task
import com.eliel.studytrack.data.firestore.TaskData
import com.eliel.studytrack.data.firestore.TaskRepository
import com.eliel.studytrack.data.firestore.PomodoroRepository
import com.eliel.studytrack.data.firestore.PomodoroSessionData
import com.eliel.studytrack.data.firestore.StudyPlanRepository
import com.eliel.studytrack.data.firestore.StudyPlan
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreenUI(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var tasks by remember { mutableStateOf<List<TaskData>>(emptyList()) }
    var sessions by remember { mutableStateOf<List<PomodoroSessionData>>(emptyList()) }
    var plans by remember { mutableStateOf<List<StudyPlan>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            tasks = TaskRepository.getTasks()
            try { sessions = PomodoroRepository.getSessions() } catch (_: Exception) {}
            try { plans = StudyPlanRepository.getPlansForCurrentUser() } catch (_: Exception) {}
        }
    }

    val completedCount = tasks.count { it.completed }
    val pendingCount = tasks.count { !it.completed }


    fun parseDueDateToMillis(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        val patterns = arrayOf("dd/MM/yyyy", "yyyy-MM-dd")
        for (p in patterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.getDefault()).apply { isLenient = false }
                val d = sdf.parse(dateStr)
                if (d != null) return d.time
            } catch (_: Exception) {}
        }
        return null
    }


    val upcomingTasks = remember(tasks) {
        tasks.filter { !it.completed && it.dueDate.isNotBlank() }
            .sortedBy { parseDueDateToMillis(it.dueDate) ?: Long.MAX_VALUE }
            .take(3)
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Bom dia! ☀️ Vamos começar seus estudos?"
            in 12..17 -> "Boa tarde! 🌞 Continue avançando!"
            else -> "Boa noite! 🌙 Hora de revisar e relaxar!"
        }
    }

    val currentDate = SimpleDateFormat(
        "EEEE, dd 'de' MMMM", Locale("pt", "BR")
    ).format(Date()).replaceFirstChar { it.uppercase() }

    val dailyStudyTime = DataSource.dailyStudyTime.value
    val completedTasksToday = DataSource.completedTasksToday.value
    val totalTasksToday = DataSource.totalTasksToday.value
    val dailyGoal = DataSource.dailyGoal.value
    val tasksForToday = DataSource.tasks.filter { !it.isCompleted && !it.isOverdue }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = currentDate,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = greeting,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_task),
                                    contentDescription = stringResource(R.string.tarefas),
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.tarefas),
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "$pendingCount pendentes / $completedCount concluídas",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fun parseEstimatedMinutes(s: String): Int {
                    val t = s.trim().lowercase()
                    return when {
                        t.contains("h") -> {
                            val parts = t.split("h")
                            val h = parts.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                            val m = parts.getOrNull(1)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                            h * 60 + m
                        }
                        else -> t.filter { it.isDigit() }.toIntOrNull() ?: 0
                    }
                }
                fun isInThisWeek(ts: Timestamp?): Boolean {
                    if (ts == null) return false
                    val cal = Calendar.getInstance()
                    cal.firstDayOfWeek = Calendar.SUNDAY
                    val start = cal.clone() as Calendar
                    start.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                    start.set(Calendar.HOUR_OF_DAY, 0)
                    start.set(Calendar.MINUTE, 0)
                    start.set(Calendar.SECOND, 0)
                    start.set(Calendar.MILLISECOND, 0)
                    val end = cal.clone() as Calendar
                    end.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    end.set(Calendar.HOUR_OF_DAY, 23)
                    end.set(Calendar.MINUTE, 59)
                    end.set(Calendar.SECOND, 59)
                    end.set(Calendar.MILLISECOND, 999)
                    val d = ts.toDate().time
                    return d in start.timeInMillis..end.timeInMillis
                }
                fun parseDueDateToTimestamp(d: String): Timestamp? {
                    return try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val date = sdf.parse(d)
                        if (date != null) Timestamp(date) else null
                    } catch (_: Exception) { null }
                }
                val weeklySessions = sessions.filter { isInThisWeek(it.completedAt) }
                val weeklyTaskMinutes = tasks.filter { it.completed }.sumOf { t ->
                    val ts = t.completedAt ?: parseDueDateToTimestamp(t.dueDate)
                    if (ts != null && isInThisWeek(ts)) parseEstimatedMinutes(t.estimatedTime) else 0
                }
                val weeklyPlanMinutes = plans.sumOf { plan ->
                    val withDate = plan.days.filter { d -> d.completed && isInThisWeek(d.completedAt) }.count() * plan.horasPorDia * 60
                    val withoutDate = plan.days.filter { d -> d.completed && d.completedAt == null }.count() * plan.horasPorDia * 60
                    withDate + withoutDate
                }
                val totalMinutes = weeklySessions.sumOf { it.minutes } + weeklyTaskMinutes + weeklyPlanMinutes
                val hoursStudiedText = String.format("%.1fh", totalMinutes / 60f)
                val totalDays = plans.sumOf { it.totalDays }
                val completedDays = plans.sumOf { it.days.count { d -> d.completed } }
                val progressPercentText = if (totalDays > 0) "${((completedDays.toFloat() / totalDays.toFloat()) * 100).toInt()}%" else "0%"

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.progresso_geral), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = progressPercentText, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chart),
                            contentDescription = stringResource(R.string.progresso),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.horas_estudadas), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = hoursStudiedText, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_book_open),
                            contentDescription = "Horas",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }


        item {
            Text(
                text = stringResource(R.string.Acoes_Rapidas),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(stringResource(R.string.pomodoro), R.drawable.ic_timer, Color(0xFFEF4444), modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.Pomodoro.route)
                }
                QuickActionCard(stringResource(R.string.cronograma), R.drawable.ic_calendar, Color(0xFF3B82F6), modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.Schedule.route)
                }
                QuickActionCard(stringResource(R.string.relatorios), R.drawable.ic_chart, Color(0xFF10B981), modifier = Modifier.weight(1f)) {
                    navController.navigate(Screen.Reports.route)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }


        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(id = R.drawable.ic_calendar),
                            contentDescription = stringResource(R.string.proximas),
                            tint = Color(0xFF3B82F6)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.proximas_tarefas),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (upcomingTasks.isEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painterResource(id = R.drawable.ic_check_circle),
                                contentDescription = stringResource(R.string.check),
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.nenhuma_tarefa_pendente), fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.voce_esta_em_dia_com_o_cronograma))
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(upcomingTasks) { task ->
                                Card(
                                    modifier = Modifier
                                        .width(240.dp)
                                        .clickable { navController.navigate(Screen.Schedule.route) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F7FD))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(
                                                        color = when (task.priority.uppercase(Locale.getDefault())) {
                                                            "ALTA" -> Color(0xFFE53935)
                                                            "BAIXA" -> Color(0xFF2196F3)
                                                            else -> Color(0xFFFFB300)
                                                        },
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                task.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color(0xFF0D47A1)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Matéria: ${task.subject}", color = Color.DarkGray, fontSize = 13.sp)
                                        Text("Prazo: ${task.dueDate}", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        item {
            Button(
                onClick = { navController.navigate(Screen.ChatTutor.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = "Chat Tutor",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Abrir Chat Tutor",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }


        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate(Screen.Premium.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_crown),
                    contentDescription = "Premium",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Assine o Premium",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}



@Composable
fun QuickActionCard(
    text: String,
    icon: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = text, tint = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
