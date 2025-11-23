package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.eliel.studytrack.data.Priority
import com.eliel.studytrack.data.Subject
import com.eliel.studytrack.data.firestore.TaskRepository
import com.eliel.studytrack.data.firestore.PomodoroRepository
import com.eliel.studytrack.data.firestore.SubjectRepository
import com.eliel.studytrack.data.firestore.PomodoroSessionData
import com.eliel.studytrack.data.firestore.StudyPlanRepository
import com.eliel.studytrack.data.firestore.StudyPlan
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource

@Composable
fun ReportsScreenUI(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var sessions by remember { mutableStateOf<List<PomodoroSessionData>>(emptyList()) }
    var tasks by remember { mutableStateOf<List<com.eliel.studytrack.data.firestore.TaskData>>(emptyList()) }
    var subjects by remember { mutableStateOf<List<com.eliel.studytrack.data.firestore.SubjectData>>(emptyList()) }
    var plans by remember { mutableStateOf<List<StudyPlan>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                sessions = PomodoroRepository.getSessions()
                tasks = TaskRepository.getTasks()
                subjects = SubjectRepository.getSubjects()
                plans = StudyPlanRepository.getPlansForCurrentUser()
            } catch (_: Exception) {}
        }
    }

    val totalCompletedTasks = tasks.count { it.completed }
    fun isInThisWeek(ts: com.google.firebase.Timestamp?): Boolean {
        if (ts == null) return false
        val cal = java.util.Calendar.getInstance()
        cal.firstDayOfWeek = java.util.Calendar.SUNDAY
        val start = cal.clone() as java.util.Calendar
        start.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)
        start.set(java.util.Calendar.HOUR_OF_DAY, 0)
        start.set(java.util.Calendar.MINUTE, 0)
        start.set(java.util.Calendar.SECOND, 0)
        start.set(java.util.Calendar.MILLISECOND, 0)
        val end = cal.clone() as java.util.Calendar
        end.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SATURDAY)
        end.set(java.util.Calendar.HOUR_OF_DAY, 23)
        end.set(java.util.Calendar.MINUTE, 59)
        end.set(java.util.Calendar.SECOND, 59)
        end.set(java.util.Calendar.MILLISECOND, 999)
        val d = ts.toDate().time
        return d in start.timeInMillis..end.timeInMillis
    }
    val weeklySessions = sessions.filter { isInThisWeek(it.completedAt) }
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
    fun parseDueDateToTimestamp(d: String): com.google.firebase.Timestamp? {
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val date = sdf.parse(d)
            if (date != null) com.google.firebase.Timestamp(date) else null
        } catch (_: Exception) { null }
    }
    val weeklyTaskMinutes = tasks.filter { it.completed }.sumOf { t ->
        val ts = t.completedAt ?: parseDueDateToTimestamp(t.dueDate)
        if (ts != null && isInThisWeek(ts)) parseEstimatedMinutes(t.estimatedTime) else 0
    }
    val weeklyPlanMinutes = plans.sumOf { plan ->
        val withDate = plan.days.filter { it.completed && isInThisWeek(it.completedAt) }.count() * plan.horasPorDia * 60
        val withoutDate = plan.days.filter { it.completed && it.completedAt == null }.count() * plan.horasPorDia * 60
        withDate + withoutDate
    }
    val totalStudyMinutes = weeklySessions.sumOf { it.minutes } + weeklyTaskMinutes + weeklyPlanMinutes
    val totalStudySessions = sessions.size
    val avgPerSession = if (totalStudySessions > 0) (totalStudyMinutes / totalStudySessions) else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.relatorios), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = stringResource(R.string.acompanhe_seu_progresso_nos_estudos), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            item {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ReportStatCard(
                        title = stringResource(R.string.tarefas_concluidas),
                        value = totalCompletedTasks.toString(),
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_check_circle,
                        iconColor = Color(0xFF6200EE),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReportStatCard(
                        title = stringResource(R.string.horas_estudadas),
                        value = String.format("%.1fh", totalStudyMinutes / 60f),
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_time,
                        iconColor = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ReportStatCard(
                        title = stringResource(R.string.sessoes_de_estudo),
                        value = totalStudySessions.toString(),
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_book_open,
                        iconColor = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReportStatCard(
                        title = "Média por Sessão",
                        value = "${avgPerSession}min",
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_chart,
                        iconColor = Color(0xFF00C853),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))


                Text(text = stringResource(R.string.progresso_por_materia), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                val tasksThisWeek = tasks.mapNotNull { t ->
                    val ts = t.completedAt ?: parseDueDateToTimestamp(t.dueDate)
                    if (ts != null && isInThisWeek(ts)) t else null
                }
                subjects.forEach { subject ->
                    val minutesPomodoro = weeklySessions.filter { it.subject == subject.name }.sumOf { it.minutes }
                    val minutesTasks = tasks.filter { it.completed && it.subject == subject.name }.sumOf { t ->
                        val ts = t.completedAt ?: parseDueDateToTimestamp(t.dueDate)
                        if (ts != null && isInThisWeek(ts)) parseEstimatedMinutes(t.estimatedTime) else 0
                    }
                    val minutesPlans = plans.filter { it.materia == subject.name }.sumOf { p ->
                        val withDate = p.days.filter { d -> d.completed && isInThisWeek(d.completedAt) }.count() * p.horasPorDia * 60
                        val withoutDate = p.days.filter { d -> d.completed && d.completedAt == null }.count() * p.horasPorDia * 60
                        withDate + withoutDate
                    }
                    val minutesBySubject = minutesPomodoro + minutesTasks + minutesPlans
                    val hoursBySubject = minutesBySubject / 60
                    val goal = subject.weeklyGoalHours
                    val generalPct = if (goal > 0) kotlin.math.min(100, ((hoursBySubject.toFloat() / goal.toFloat()) * 100).toInt()) else 0
                    val totalTasksSubjectWeek = tasksThisWeek.count { it.subject == subject.name }
                    val completedTasksSubjectWeek = tasksThisWeek.count { it.subject == subject.name && it.completed }
                    val completionPct = if (totalTasksSubjectWeek > 0) ((completedTasksSubjectWeek.toFloat() / totalTasksSubjectWeek.toFloat()) * 100).toInt() else 0
                    val uiSubject = Subject(
                        id = subject.id,
                        name = subject.name,
                        weeklyGoalHours = subject.weeklyGoalHours,
                        priority = Priority.MEDIA,
                        color = Color(subject.color),
                        currentWeeklyProgressHours = hoursBySubject,
                        completionPercentage = completionPct,
                        generalProgressPercentage = generalPct
                    )
                    SubjectProgressItem(subject = uiSubject)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))


                Text(text = stringResource(R.string.performance_por_dia_da_semana), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                PerformanceByDayOfWeekContent(tasks = tasks, plans = plans)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ReportStatCard(title: String, value: String, period: String, icon: Int, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = icon), contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = period, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SubjectProgressItem(subject: Subject) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(12.dp)
                    .background(subject.color, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = subject.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${subject.currentWeeklyProgressHours}h estudadas", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Conclusão de Tarefas: ${subject.completionPercentage}%", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(
                progress = subject.completionPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Progresso Geral: ${subject.generalProgressPercentage}%", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(
                progress = subject.generalProgressPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun PerformanceByDayOfWeekContent(
    tasks: List<com.eliel.studytrack.data.firestore.TaskData>,
    plans: List<com.eliel.studytrack.data.firestore.StudyPlan>
) {
    val scope = rememberCoroutineScope()
    var sessions by remember { mutableStateOf<List<PomodoroSessionData>>(emptyList()) }
    LaunchedEffect(Unit) {
        scope.launch { sessions = PomodoroRepository.getSessions() }
    }
    fun isInThisWeek(ts: com.google.firebase.Timestamp?): Boolean {
        if (ts == null) return false
        val cal = java.util.Calendar.getInstance()
        cal.firstDayOfWeek = java.util.Calendar.SUNDAY
        val start = cal.clone() as java.util.Calendar
        start.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)
        start.set(java.util.Calendar.HOUR_OF_DAY, 0)
        start.set(java.util.Calendar.MINUTE, 0)
        start.set(java.util.Calendar.SECOND, 0)
        start.set(java.util.Calendar.MILLISECOND, 0)
        val end = cal.clone() as java.util.Calendar
        end.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SATURDAY)
        end.set(java.util.Calendar.HOUR_OF_DAY, 23)
        end.set(java.util.Calendar.MINUTE, 59)
        end.set(java.util.Calendar.SECOND, 59)
        end.set(java.util.Calendar.MILLISECOND, 999)
        val d = ts.toDate().time
        return d in start.timeInMillis..end.timeInMillis
    }
    val weeklySessions = sessions.filter { isInThisWeek(it.completedAt) }
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
    fun parseDueDateToTimestamp(d: String): com.google.firebase.Timestamp? {
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val date = sdf.parse(d)
            if (date != null) com.google.firebase.Timestamp(date) else null
        } catch (_: Exception) { null }
    }
    val weeklyTasks = tasks.filter { it.completed }.mapNotNull { t ->
        val ts = t.completedAt ?: parseDueDateToTimestamp(t.dueDate)
        if (ts != null && isInThisWeek(ts)) Pair(ts, t) else null
    }
    val weeklyPlanDays = plans.flatMap { p -> p.days.filter { it.completed && it.completedAt != null && isInThisWeek(it.completedAt) }.map { Pair(p, it) } }
    fun dayOfWeek(ts: com.google.firebase.Timestamp): Int { val cal = java.util.Calendar.getInstance(); cal.time = ts.toDate(); return cal.get(java.util.Calendar.DAY_OF_WEEK) }
    val byDaySessions = weeklySessions.groupBy { dayOfWeek(it.completedAt) }.mapValues { it.value.sumOf { s -> s.minutes } }
    val byDayTasks = weeklyTasks.groupBy { dayOfWeek(it.first) }.mapValues { it.value.sumOf { pair -> parseEstimatedMinutes(pair.second.estimatedTime) } }
    val byDayPlans = weeklyPlanDays.groupBy { dayOfWeek(it.second.completedAt!!) }.mapValues { it.value.sumOf { pair -> pair.first.horasPorDia * 60 } }
    val byDay = (1..7).associateWith { (byDaySessions[it] ?: 0) + (byDayTasks[it] ?: 0) + (byDayPlans[it] ?: 0) }
    val names = listOf(
        stringResource(R.string.domingo),
        stringResource(R.string.segunda),
        stringResource(R.string.terca),
        stringResource(R.string.quarta),
        stringResource(R.string.quinta),
        stringResource(R.string.sexta),
        stringResource(R.string.sabado)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            names.forEachIndexed { index, dayName ->
                val minutes = byDay[index + 1] ?: 0
                val progress = (minutes / 120f).coerceIn(0f, 1f)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dayName, modifier = Modifier.weight(1f), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .weight(2f)
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = String.format("%dh %dmin", minutes / 60, minutes % 60), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

val Subject.completionPercentage: Int
    get() = (Math.random() * 100).toInt()

val Subject.generalProgressPercentage: Int
    get() = (Math.random() * 100).toInt()


