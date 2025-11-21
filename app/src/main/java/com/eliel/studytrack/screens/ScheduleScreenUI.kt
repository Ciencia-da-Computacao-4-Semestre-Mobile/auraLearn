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
import androidx.compose.ui.res.stringResource
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
                text = stringResource(R.string.cronograma),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { showNewTaskDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nova_tarefa), tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.nova_tarefa), color = MaterialTheme.colorScheme.onPrimary)
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
                Text(text = stringResource(R.string.tarefas), modifier = Modifier.padding(8.dp))
            }
            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                Text(text = stringResource(R.string.materias), modifier = Modifier.padding(8.dp))
            }
            Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                Text(text = stringResource(R.string.plano_de_estudos), modifier = Modifier.padding(8.dp))
            }
        }


        Spacer(modifier = Modifier.height(16.dp))


        when (selectedTabIndex) {
            0 -> TasksContent(
                tasks = tasks,
                subjects = subjects,
                onToggleComplete = { task ->
                    scope.launch {
                        TaskRepository.updateTaskStatus(task.id, !task.completed)
                        tasks = TaskRepository.getTasks()
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
            2 -> StudyPlanContent()
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
                label = { Text(stringResource(R.string.filtrar_por)) },
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
            title = stringResource(R.string.excluir_tarefa),
            message = stringResource(R.string.tem_certeza_que_deseja_excluir_esta_tarefa),
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
                    Text(stringResource(R.string.concluir), color = Color.White)
                }
            } else {
                Text(
                    text = stringResource(R.string.concluida),
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.excluir),
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
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nova_materia), tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = stringResource(R.string.nova_materia), color = MaterialTheme.colorScheme.onPrimary)
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
            title = stringResource(R.string.excluir_materia),
            message = stringResource(R.string.tem_certeza_que_deseja_excluir_esta_materia_todas_as_tarefas_relacionadas_tambem_ser_o_afetadas),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                Icon(Icons.Default.Close, stringResource(R.string.excluir_materia))
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
                Text(stringResource(R.string.nova_tarefa), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.titulo)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.descricao)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = if (selectedSubject.isNotBlank()) selectedSubject else "Selecione uma matéria",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.materia)) },
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
                Text(stringResource(R.string.prioridade), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                val priorities = listOf("BAIXA", "MEDIA", "ALTA")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorities.forEach { priority ->
                        val color = when (priority) {
                            "BAIXA" -> Color(0xFF2196F3)
                            "MEDIA" -> Color(0xFFFFC107)
                            "ALTA" -> Color(0xFFF44336)
                            else -> Color.Gray
                        }
                        Box(
                            modifier = Modifier
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
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(priority, color = color, fontWeight = FontWeight.Medium)
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
                    label = { Text(stringResource(R.string.prazo)) },
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
                    label = { Text(stringResource(R.string.tempo_estimado_min)) },
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
                    ) { Text(stringResource(R.string.cancelar)) }

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
                    ) { Text(stringResource(R.string.salvar)) }
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
                Text(stringResource(R.string.excluir))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelar))
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
                text = stringResource(R.string.concluir_tarefa),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = stringResource(R.string.tem_certeza_que_deseja_marcar_esta_tarefa_como_concluida),
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
                Text(stringResource(R.string.concluir))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelar))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun StudyPlanContent(
    viewModel: StudyPlanViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewPlanDialog by remember { mutableStateOf(false) }
    var openedPlan by remember { mutableStateOf<com.eliel.studytrack.data.firestore.StudyPlan?>(null) }
    var showDeletePlanDialog by remember { mutableStateOf(false) }
    var planIdToDelete by remember { mutableStateOf<String?>(null) }
    val scaffoldPadding = 0.dp

    Column(Modifier
        .fillMaxSize()
        .padding(scaffoldPadding)) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.planos_de_estudo), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = { showNewPlanDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.novo_plano), tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.novo_plano), color = MaterialTheme.colorScheme.onPrimary)
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
                    Text(stringResource(R.string.nenhum_plano_criado), modifier = Modifier.padding(8.dp))
                } else {
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
            onConcludePlan = { viewModel.markPlanCompleted(plan) }
        )
    }

    if (showDeletePlanDialog) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.excluir_plano),
            message = stringResource(R.string.tem_certeza_que_deseja_excluir_este_plano_de_estudos),
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
    onConcludePlan: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${plan.materia} • ${plan.horasPorDia}h/dia", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(painterResource(id = R.drawable.ic_close), contentDescription = stringResource(R.string.fechar))
                    }
                }

                Spacer(Modifier.height(12.dp))

                val completedCount = plan.days.count { it.completed }
                val allComplete = completedCount == plan.totalDays
                Column(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Progresso: $completedCount / ${plan.totalDays} dias concluídos", modifier = Modifier.weight(1f))
                        if (allComplete) {
                            Text(
                                text = stringResource(R.string.concluido),
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = completedCount / (plan.totalDays.coerceAtLeast(1)).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn {
                    items(plan.days) { day ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = day.completed,
                                onCheckedChange = { onToggleDay(day.dayIndex) },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Dia ${day.dayIndex}: ${day.text}",
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                softWrap = true,
                                color = if (day.completed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Divider(Modifier.padding(vertical = 6.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.fechar)) }
                    if (allComplete) {
                        Button(
                            onClick = onConcludePlan,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) { Text(stringResource(R.string.concluir_plano)) }
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
                        text = stringResource(R.string.concluido),
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(painterResource(id = R.drawable.ic_close), contentDescription = stringResource(R.string.excluir), tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
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
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text(stringResource(R.string.novo_plano_de_estudos), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(value = materia, onValueChange = { materia = it }, label = { Text(stringResource(R.string.materia)) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = tema, onValueChange = { tema = it }, label = { Text(stringResource(R.string.tema)) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = objetivo, onValueChange = { objetivo = it }, label = { Text(stringResource(R.string.objetivo)) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = dias, onValueChange = { dias = it }, label = { Text(stringResource(R.string.tempo_dias)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = horas, onValueChange = { horas = it }, label = { Text(stringResource(R.string.horas_por_dia)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.cancelar)) }
                    Button(onClick = {
                        val diasInt = dias.toIntOrNull() ?: 1
                        val horasInt = horas.toIntOrNull() ?: 1
                        onSave(materia.trim(), tema.trim(), objetivo.trim(), diasInt, horasInt)
                    }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.gerar_e_salvar))
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.nova_materia), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.nome_da_materia)) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.cor), fontWeight = FontWeight.Medium)
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
                    label = { Text(stringResource(R.string.meta_semanal_h)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.cancelar)) }
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
                    ) { Text(stringResource(R.string.salvar)) }
                }
            }
        }
    }
}