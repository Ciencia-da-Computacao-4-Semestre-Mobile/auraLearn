package com.eliel.studytrack.screens

import androidx.compose.foundation.background
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
import com.eliel.studytrack.data.DataSource
import com.eliel.studytrack.data.Priority
import com.eliel.studytrack.data.Subject
import com.eliel.studytrack.data.Task
import java.util.*

@Composable
fun ScheduleScreenUI(navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showNewTaskDialog by remember { mutableStateOf(false) }
    var showNewSubjectDialog by remember { mutableStateOf(false) }

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
            0 -> TasksContent()
            1 -> SubjectsContent(onNewSubjectClick = { showNewSubjectDialog = true })
        }
    }


    if (showNewTaskDialog) {
        NewTaskDialog(
            onDismiss = { showNewTaskDialog = false },
            onSave = { task ->
                DataSource.tasks.add(task)
                showNewTaskDialog = false
            }
        )
    }

    if (showNewSubjectDialog) {
        NewSubjectDialog(
            onDismiss = { showNewSubjectDialog = false },
            onSave = { subject ->
                DataSource.subjects.add(subject)
                showNewSubjectDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent() {
    var expanded by remember { mutableStateOf(false) }
    val subjectOptions = DataSource.getSubjectNames()
    var selectedSubjectFilter by remember { mutableStateOf(subjectOptions[0]) }

    val filteredTasks = remember(selectedSubjectFilter) {
        DataSource.getTasksForSubject(selectedSubjectFilter)
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
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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
                TaskItem(task = task) { /* TODO: Atualizar tarefa */ }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onTaskClick: (Task) -> Unit) {
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
            RadioButton(selected = task.isCompleted, onClick = { onTaskClick(task) })
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                task.description?.let {
                    Text(text = it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Chip(
                        text = task.priority.name.lowercase(),
                        color = when (task.priority) {
                            Priority.ALTA -> Color.Red
                            Priority.MEDIA -> Color(0xFFFFC107)
                            Priority.BAIXA -> Color(0xFF00C853)
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (task.isOverdue) {
                        Chip(text = "Atrasada", color = Color(0xFFF44336))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(painterResource(id = R.drawable.ic_calendar), contentDescription = "Data", modifier = Modifier.size(16.dp))
                    Text(text = task.dueDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(painterResource(id = R.drawable.ic_time), contentDescription = "Tempo", modifier = Modifier.size(16.dp))
                    Text(text = task.estimatedTime, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SubjectsContent(onNewSubjectClick: () -> Unit) {
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
            items(DataSource.subjects) { subject ->
                SubjectItem(subject = subject)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SubjectItem(subject: Subject) {
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
            Icon(painterResource(id = R.drawable.ic_book_open), contentDescription = "Matéria", tint = subject.color)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = subject.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Chip(
                        text = subject.priority.name.lowercase(),
                        color = when (subject.priority) {
                            Priority.ALTA -> Color.Red
                            Priority.MEDIA -> Color(0xFFFFC107)
                            Priority.BAIXA -> Color(0xFF00C853)
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Meta: ${subject.weeklyGoalHours}h/semana", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = "Progresso semanal", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${subject.currentWeeklyProgressHours}h / ${subject.weeklyGoalHours}h", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(subject.color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun Chip(text: String, color: Color) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIA) }
    var dueDate by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("30") }

    var subjectExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val subjects = DataSource.subjects.map { it.name }
    val priorities = Priority.values().toList()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nova Tarefa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    placeholder = { Text("Digite o título da tarefa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))


                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    placeholder = { Text("Descreva a tarefa (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    ExposedDropdownMenuBox(
                        expanded = subjectExpanded,
                        onExpandedChange = { subjectExpanded = !subjectExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedSubject,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Matéria") },
                            placeholder = { Text("Escolher") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = subjectExpanded,
                            onDismissRequest = { subjectExpanded = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject) },
                                    onClick = {
                                        selectedSubject = subject
                                        subjectExpanded = false
                                    }
                                )
                            }
                        }
                    }


                    ExposedDropdownMenuBox(
                        expanded = priorityExpanded,
                        onExpandedChange = { priorityExpanded = !priorityExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = when (selectedPriority) {
                                Priority.ALTA -> "Alta"
                                Priority.MEDIA -> "Média"
                                Priority.BAIXA -> "Baixa"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Prioridade") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            priorities.forEach { priority ->
                                DropdownMenuItem(
                                    text = {
                                        Text(when (priority) {
                                            Priority.ALTA -> "Alta"
                                            Priority.MEDIA -> "Média"
                                            Priority.BAIXA -> "Baixa"
                                        })
                                    },
                                    onClick = {
                                        selectedPriority = priority
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Data Limite") },
                        placeholder = { Text("dd/mm/aaaa") },
                        modifier = Modifier.weight(1f)
                    )


                    OutlinedTextField(
                        value = estimatedTime,
                        onValueChange = { estimatedTime = it },
                        label = { Text("Tempo (min)") },
                        placeholder = { Text("30") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && selectedSubject.isNotBlank() && dueDate.isNotBlank()) {
                                val newTask = Task(
                                    id = UUID.randomUUID().toString(),
                                    title = title,
                                    description = description.takeIf { it.isNotBlank() },
                                    subject = selectedSubject,
                                    dueDate = dueDate,
                                    estimatedTime = "${estimatedTime}min",
                                    priority = selectedPriority,
                                    isCompleted = false,
                                    isOverdue = false
                                )
                                onSave(newTask)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotBlank() && selectedSubject.isNotBlank() && dueDate.isNotBlank()
                    ) {
                        Text("Salvar")
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
    onSave: (Subject) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFF6200EE)) }
    var weeklyGoal by remember { mutableStateOf("3") }
    var totalPlanned by remember { mutableStateOf("20") }

    val availableColors = listOf(
        Color(0xFF2196F3), // Azul
        Color(0xFF9C27B0), // Roxo
        Color(0xFFFF9800), // Laranja
        Color(0xFF4CAF50), // Verde
        Color(0xFF009688), // Verde água
        Color(0xFFE91E63), // Rosa
        Color(0xFFF44336), // Vermelho
        Color(0xFF6366F1)  // Azul roxo
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nova Matéria",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da matéria") },
                    placeholder = { Text("Nome da matéria") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Cor da matéria",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = color,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                                .then(
                                    if (selectedColor == color) {
                                        Modifier.background(
                                            Color.White.copy(alpha = 0.3f),
                                            CircleShape
                                        )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == color) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Selecionado",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = weeklyGoal,
                        onValueChange = { weeklyGoal = it },
                        label = { Text("Meta semanal (h)") },
                        placeholder = { Text("3") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    
                    OutlinedTextField(
                        value = totalPlanned,
                        onValueChange = { totalPlanned = it },
                        label = { Text("Total planejado (h)") },
                        placeholder = { Text("20") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && weeklyGoal.isNotBlank()) {
                                val newSubject = Subject(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    weeklyGoalHours = weeklyGoal.toIntOrNull() ?: 3,
                                    priority = Priority.MEDIA, // Prioridade padrão
                                    color = selectedColor,
                                    currentWeeklyProgressHours = 0
                                )
                                onSave(newSubject)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && weeklyGoal.isNotBlank()
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}

