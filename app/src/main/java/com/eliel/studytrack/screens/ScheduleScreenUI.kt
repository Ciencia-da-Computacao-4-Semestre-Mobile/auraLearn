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
        }

        Spacer(modifier = Modifier.height(16.dp))


        when (selectedTabIndex) {
            0 -> TasksContent(
                tasks = tasks,
                subjects = subjects,
                onToggleComplete = { task ->
                    scope.launch {
                        TaskRepository.updateTaskStatus(task.id, !task.isCompleted)
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
                label = { Text("Filtrar por") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
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
                        if (!task.isCompleted) {
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
                taskToComplete?.let { task ->
                    onToggleComplete(task)
                }
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
    val backgroundColor = if (task.isCompleted) {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (task.isCompleted) {
        Color(0xFF4CAF50)
    } else {
        Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (task.isCompleted) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted)
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    else null
                )
                task.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Matéria: ${task.subject}",
                    fontSize = 12.sp,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Prazo: ${task.dueDate}",
                    fontSize = 12.sp,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (!task.isCompleted) {
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
                    tint = if (task.isCompleted)
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

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Nova Tarefa", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
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
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
                Spacer(modifier = Modifier.height(12.dp))
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
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        datePicker.show()
                                    }
                                }
                            }
                        }
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = estimatedTime, onValueChange = { estimatedTime = it }, label = { Text("Tempo estimado (min)") })
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
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
                                    isCompleted = false
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
@Composable
fun ConfirmCompleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Concluir Lição",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = "Deseja marcar esta lição como concluída?",
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
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
