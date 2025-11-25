package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.data.firestore.SubjectData
import com.eliel.studytrack.data.firestore.TaskData
import com.eliel.studytrack.data.firestore.SubjectRepository
import com.eliel.studytrack.data.firestore.TaskRepository
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.draw.clip
import com.eliel.studytrack.data.StudyPlanViewModel
import com.eliel.studytrack.data.StudyPlanUiState
import com.eliel.studytrack.data.firestore.StudyPlan


@Composable
fun ScheduleScreenUI(navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showNewTaskDialog by remember { mutableStateOf(false) }
    var showNewSubjectDialog by remember { mutableStateOf(false) }

    var subjects by remember { mutableStateOf(listOf<SubjectData>()) }
    var tasks by remember { mutableStateOf(listOf<TaskData>()) }

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        scope.launch {
            subjects = SubjectRepository.getSubjects()
            tasks = TaskRepository.getTasks()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cronograma",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { showNewTaskDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Tarefa", tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Nova Tarefa", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                Text(text = "Tarefas", modifier = Modifier.padding(8.dp))
            }
            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                Text(text = "Matérias", modifier = Modifier.padding(8.dp))
            }
            Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                Text(text = "Plano de Estudos", modifier = Modifier.padding(8.dp))
            }
        }


        Spacer(modifier = Modifier.height(16.dp))


        when (selectedTabIndex) {
            0 -> TasksContent(
                tasks = tasks,
                subjects = subjects,
                onToggleComplete = { task ->
                    scope.launch {
                        val newStatus = !task.completed
                        TaskRepository.updateTaskStatus(task.id, newStatus)
                        tasks = TaskRepository.getTasks()
                        val prefs = ctx.getSharedPreferences("studytrack_prefs", android.content.Context.MODE_PRIVATE)
                        if (newStatus) {
                            com.eliel.studytrack.notifications.ReminderScheduler.cancelTaskDeadlines(ctx, task.id)
                        } else if (prefs.getBoolean("pref_task_deadlines", true)) {
                            com.eliel.studytrack.notifications.ReminderScheduler.scheduleTaskDeadlines(ctx, task.copy(completed = false))
                        }
                    }
                },
                onDeleteTask = { id ->
                    scope.launch {
                        TaskRepository.deleteTask(id)
                        tasks = TaskRepository.getTasks()
                    }
                }
            )
            1 -> SubjectsContent(
                subjects = subjects,
                onNewSubjectClick = { showNewSubjectDialog = true },
                onDeleteSubject = { id ->
                    scope.launch {
                        SubjectRepository.deleteSubject(id)
                        subjects = SubjectRepository.getSubjects()
                    }
                }
            )
            2 -> StudyPlanContent(navController = navController)
        }
    }


    if (showNewTaskDialog) {
        NewTaskDialog(
            onDismiss = { showNewTaskDialog = false },
            subjects = subjects.map { it.name },
            onSave = { task ->
                scope.launch {
                    TaskRepository.addTask(task)
                    tasks = TaskRepository.getTasks()
                    showNewTaskDialog = false
                    val prefs = ctx.getSharedPreferences("studytrack_prefs", android.content.Context.MODE_PRIVATE)
                    if (prefs.getBoolean("pref_task_deadlines", true)) {
                        com.eliel.studytrack.notifications.ReminderScheduler.scheduleTaskDeadlines(ctx, task)
                    }
                }
            }
        )
    }


    if (showNewSubjectDialog) {
        NewSubjectDialog(
            onDismiss = { showNewSubjectDialog = false },
            onSave = { subject ->
                scope.launch {
                    SubjectRepository.addSubject(subject)
                    subjects = SubjectRepository.getSubjects()
                    showNewSubjectDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    tasks: List<TaskData>,
    subjects: List<SubjectData>,
    onToggleComplete: (TaskData) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val subjectOptions = listOf("Todas") + subjects.map { it.name }
    var selectedSubjectFilter by remember { mutableStateOf("Todas") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<String?>(null) }
    var taskToComplete by remember { mutableStateOf<TaskData?>(null) }

    val filteredTasks = tasks.filter {
        selectedSubjectFilter == "Todas" || it.subject == selectedSubjectFilter
    }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedSubjectFilter,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                subjectOptions.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(subject) },
                        onClick = {
                            selectedSubjectFilter = subject
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(filteredTasks) { task ->
                TaskItem(
                    task = task,
                    onComplete = {
                        if (!task.completed) {
                            taskToComplete = task
                            showCompleteDialog = true
                        }
                    },
                    onDelete = {
                        taskToDelete = task.id
                        showDeleteDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showCompleteDialog) {
        ConfirmCompleteDialog(
            onConfirm = {
                taskToComplete?.let { onToggleComplete(it) }
                showCompleteDialog = false
                taskToComplete = null
            },
            onDismiss = {
                showCompleteDialog = false
                taskToComplete = null
            }
        )
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = "Excluir Tarefa",
            message = "Tem certeza que deseja excluir esta tarefa?",
            onConfirm = {
                taskToDelete?.let { onDeleteTask(it) }
                showDeleteDialog = false
                taskToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                taskToDelete = null
            }
        )
    }
}

@Composable
fun TaskItem(task: TaskData, onComplete: () -> Unit, onDelete: () -> Unit) {
    val backgroundColor = if (task.completed) {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        task.completed -> Color(0xFF4CAF50)
        task.priority.equals("BAIXA", ignoreCase = true) -> Color(0xFF2196F3)
        task.priority.equals("MEDIA", ignoreCase = true) -> Color(0xFFFFC107)
        task.priority.equals("ALTA", ignoreCase = true) -> Color(0xFFF44336)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (task.completed) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.completed) {
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    } else null
                )

                task.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Matéria: ${task.subject}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Prioridade: ${task.priority}",
                    fontSize = 12.sp,
                    color = borderColor
                )
                Text(
                    text = "Prazo: ${task.dueDate}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (!task.completed) {
                Button(
                    onClick = onComplete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Concluir", color = Color.White)
                }
            } else {
                Text(
                    text = "✓ Concluída",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Excluir",
                    tint = if (task.completed)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Composable
fun SubjectsContent(
    subjects: List<SubjectData>,
    onNewSubjectClick: () -> Unit,
    onDeleteSubject: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var subjectToDelete by remember { mutableStateOf<String?>(null) }

    Column {
        Button(
            onClick = onNewSubjectClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nova Matéria", tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Nova Matéria", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(subjects) { subject ->
                SubjectItem(
                    subject = subject,
                    onDelete = {
                        subjectToDelete = subject.id
                        showDeleteDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = "Excluir Matéria",
            message = "Tem certeza que deseja excluir esta matéria? Todas as tarefas relacionadas também serão afetadas.",
            onConfirm = {
                subjectToDelete?.let { onDeleteSubject(it) }
                showDeleteDialog = false
                subjectToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                subjectToDelete = null
            }
        )
    }
}

@Composable
fun SubjectItem(subject: SubjectData, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(id = R.drawable.ic_book_open),
                contentDescription = "Matéria",
                tint = Color(subject.color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = subject.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Meta: ${subject.weeklyGoalHours}h/semana", fontSize = 12.sp)
                Text(text = "Progresso: ${subject.currentWeeklyProgressHours}h", fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Excluir matéria")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialog(
    onDismiss: () -> Unit,
    subjects: List<String>,
    onSave: (TaskData) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("30") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf("BAIXA") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Nova Tarefa", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = if (selectedSubject.isNotBlank()) selectedSubject else "Selecione uma matéria",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Matéria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject) },
                                onClick = {
                                    selectedSubject = subject
                                    expanded = false
                                }
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Text("Prioridade", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                val priorities = listOf("BAIXA", "MEDIA", "ALTA")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorities.forEach { priority ->
                        val color = when (priority) {
                            "BAIXA" -> Color(0xFF2196F3)
                            "MEDIA" -> Color(0xFFFFC107)
                            "ALTA" -> Color(0xFFF44336)
                            else -> Color.Gray
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedPriority == priority) 2.dp else 1.dp,
                                    color = if (selectedPriority == priority) color else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (selectedPriority == priority)
                                        color.copy(alpha = 0.2f)
                                    else
                                        Color.Transparent
                                )
                                .clickable { selectedPriority = priority }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(priority, color = color, fontWeight = FontWeight.Medium, fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                val context = LocalContext.current
                val calendar = Calendar.getInstance()
                val datePicker = remember {
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            dueDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = {},
                    label = { Text("Prazo") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { source ->
                            LaunchedEffect(source) {
                                source.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        datePicker.show()
                                    }
                                }
                            }
                        }
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = estimatedTime,
                    onValueChange = { estimatedTime = it },
                    label = { Text("Tempo estimado (min)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && selectedSubject.isNotBlank() && dueDate.isNotBlank()) {
                                val newTask = TaskData(
                                    id = UUID.randomUUID().toString(),
                                    title = title,
                                    description = description,
                                    subject = selectedSubject,
                                    dueDate = dueDate,
                                    estimatedTime = estimatedTime,
                                    completed = false,
                                    priority = selectedPriority
                                )
                                onSave(newTask)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Salvar") }
                }
            }
        }
    }
}


@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ConfirmCompleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Concluir Tarefa",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = "Tem certeza que deseja marcar esta tarefa como concluída?",
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Concluir")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun StudyPlanContent(
    navController: NavHostController,
    viewModel: StudyPlanViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewPlanDialog by remember { mutableStateOf(false) }
    var openedPlan by remember { mutableStateOf<com.eliel.studytrack.data.firestore.StudyPlan?>(null) }
    var showDeletePlanDialog by remember { mutableStateOf(false) }
    var planIdToDelete by remember { mutableStateOf<String?>(null) }
    val scaffoldPadding = 0.dp

    Column(Modifier.fillMaxSize().padding(scaffoldPadding)) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Planos de Estudo", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = { showNewPlanDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Plano", tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Novo Plano", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(Modifier.height(12.dp))

        when (uiState) {
            is StudyPlanUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is StudyPlanUiState.Generating -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text(text = (uiState as StudyPlanUiState.Generating).message)
                    }
                }
            }
            is StudyPlanUiState.Error -> {
                Text((uiState as StudyPlanUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            is StudyPlanUiState.SuccessPlans -> {
                val plans = (uiState as StudyPlanUiState.SuccessPlans).plans
                if (plans.isEmpty()) {
                    Text("Nenhum plano criado", modifier = Modifier.padding(8.dp))
                } else {
                    val ctx = LocalContext.current
                    val prefs = ctx.getSharedPreferences("studytrack_prefs", android.content.Context.MODE_PRIVATE)
                    if (prefs.getBoolean("pref_study_reminders", true)) {
                        com.eliel.studytrack.notifications.ReminderScheduler.scheduleDailyPlanReminder(
                            ctx,
                            plans.filter { plan -> plan.days.any { !it.completed } }.map { it.title }
                        )
                    }
                    LazyColumn {
                        items(plans) { plan ->
                            StudyPlanCard(
                                plan = plan,
                                onClick = { openedPlan = plan },
                                onDelete = {
                                    planIdToDelete = plan.id
                                    showDeletePlanDialog = true
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
            else -> {}
        }
    }

    if (showNewPlanDialog) {
        NewStudyPlanDialog(
            onDismiss = { showNewPlanDialog = false },
            onSave = { materia, tema, objetivo, dias, horas ->
                showNewPlanDialog = false
                viewModel.generateAndSavePlan(materia, tema, objetivo, dias, horas) { _ -> }
            }
        )
    }

    openedPlan?.let { plan ->
        StudyPlanDetailDialog(
            plan = plan,
            onDismiss = { openedPlan = null },
            onToggleDay = { dayIndex -> viewModel.toggleDayCompletion(plan, dayIndex) },
            onConcludePlan = { viewModel.markPlanCompleted(plan) },
            onReview = { materia, tema, dayText ->
                val encMateria = android.net.Uri.encode(materia)
                val encTema = android.net.Uri.encode(tema)
                val encDay = android.net.Uri.encode(dayText)
                navController.navigate(
                    com.eliel.studytrack.Screen.Review.route + "/" + encMateria + "/" + encTema + "/" + encDay
                )
            }
        )
    }

    if (showDeletePlanDialog) {
        ConfirmDeleteDialog(
            title = "Excluir Plano",
            message = "Tem certeza que deseja excluir este plano de estudos?",
            onConfirm = {
                planIdToDelete?.let { viewModel.deletePlan(it) }
                showDeletePlanDialog = false
                planIdToDelete = null
            },
            onDismiss = {
                showDeletePlanDialog = false
                planIdToDelete = null
            }
        )
    }
}
@Composable
fun StudyPlanDetailDialog(
    plan: com.eliel.studytrack.data.firestore.StudyPlan,
    onDismiss: () -> Unit,
    onToggleDay: (Int) -> Unit,
    onConcludePlan: () -> Unit,
    onReview: (materia: String, tema: String, dayText: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(20.dp).fillMaxHeight()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("${plan.materia} • ${plan.horasPorDia}h/dia", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(painterResource(id = R.drawable.ic_close), contentDescription = "Fechar")
                    }
                }

                Spacer(Modifier.height(12.dp))

                val introText = if (plan.descricao.isNotBlank()) plan.descricao else plan.objetivo
                var showDesc by remember { mutableStateOf(true) }
                if (introText.isNotBlank()) {
                    if (showDesc) {
                        Text(
                            introText,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { showDesc = !showDesc }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (showDesc) "Esconder objetivo" else "Mostrar objetivo")
                    }
                    Spacer(Modifier.height(12.dp))
                }

                val completedCount = plan.days.count { it.completed }
                val allComplete = completedCount == plan.totalDays
                Column(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(painterResource(id = R.drawable.ic_check_circle), contentDescription = null, tint = Color(0xFF4CAF50))
                            Spacer(Modifier.width(8.dp))
                            Text("$completedCount de ${plan.totalDays} dias", fontWeight = FontWeight.Medium)
                        }
                        if (allComplete) {
                            Text(
                                text = "✓ Concluído",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = completedCount / (plan.totalDays.coerceAtLeast(1)).toFloat(),
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(Modifier.weight(1f)) {
                    items(plan.days) { day ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = day.completed,
                                    onCheckedChange = { onToggleDay(day.dayIndex) },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(painterResource(id = R.drawable.ic_calendar), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            "Dia ${day.dayIndex}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp,
                                            color = if (day.completed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        day.text,
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        softWrap = true,
                                        color = if (day.completed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { onReview(plan.materia, plan.tema, day.text) },
                                        modifier = Modifier.align(Alignment.End)
                                    ) { Text("Revisar") }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Fechar")
                    }
                    if (allComplete) {
                        Button(
                            onClick = onConcludePlan,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) { Text("Concluir Plano") }
                    }
                }
            }
        }
    }
}



@Composable
fun StudyPlanItem(text: String) {
    var checked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = { checked = it })
            Spacer(Modifier.width(8.dp))
            Text(text)
        }
    }
}
@Composable
fun StudyPlanCard(
    plan: com.eliel.studytrack.data.firestore.StudyPlan,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val completedCount = plan.days.count { it.completed }
    val progress = completedCount / (plan.totalDays.coerceAtLeast(1)).toFloat()
    val borderColor = if (progress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book_open),
                    contentDescription = null,
                    tint = borderColor
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(plan.title, fontWeight = FontWeight.Bold)
                    Text("${plan.materia} • ${plan.horasPorDia}h/dia", style = MaterialTheme.typography.bodySmall)
                }
                if (progress >= 1f) {
                    Text(
                        text = "✓ Concluído",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(painterResource(id = R.drawable.ic_close), contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = borderColor,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(Modifier.height(4.dp))
            Text("$completedCount/${plan.totalDays} dias", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun NewStudyPlanDialog(
    onDismiss: () -> Unit,
    onSave: (materia: String, tema: String, objetivo: String, dias: Int, horas: Int) -> Unit
) {
    var materia by remember { mutableStateOf("") }
    var tema by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("5") }
    var horas by remember { mutableStateOf("1") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("Novo Plano de Estudos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(value = materia, onValueChange = { materia = it }, label = { Text("Matéria") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = tema, onValueChange = { tema = it }, label = { Text("Tema") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = objetivo, onValueChange = { objetivo = it }, label = { Text("Objetivo") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = dias, onValueChange = { dias = it }, label = { Text("Tempo (dias)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = horas, onValueChange = { horas = it }, label = { Text("Horas por dia") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(onClick = {
                        val diasInt = dias.toIntOrNull() ?: 1
                        val horasInt = horas.toIntOrNull() ?: 1
                        onSave(materia.trim(), tema.trim(), objetivo.trim(), diasInt, horasInt)
                    }, modifier = Modifier.weight(1f)) {
                        Text("Gerar e Salvar")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubjectDialog(
    onDismiss: () -> Unit,
    onSave: (SubjectData) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var weeklyGoal by remember { mutableStateOf("3") }
    var selectedColor by remember { mutableStateOf(0xFF2196F3) }

    val colors = listOf(
        0xFF2196F3,
        0xFF9C27B0,
        0xFFFF9800,
        0xFF4CAF50,
        0xFFE91E63,
        0xFFF44336,
        0xFF009688
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Nova Matéria", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome da matéria") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cor", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colors) { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(color), CircleShape)
                                .clickable { selectedColor = color }
                                .border(
                                    width = if (selectedColor == color) 3.dp else 1.dp,
                                    color = if (selectedColor == color) Color.Black else Color.LightGray,
                                    shape = CircleShape
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = weeklyGoal,
                    onValueChange = { weeklyGoal = it },
                    label = { Text("Meta semanal (h)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val newSubject = SubjectData(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    weeklyGoalHours = weeklyGoal.toIntOrNull() ?: 3,
                                    color = selectedColor,
                                    currentWeeklyProgressHours = 0
                                )
                                onSave(newSubject)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Salvar") }
                }
            }
        }
    }
}