package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.Screen
import com.eliel.studytrack.data.firestore.UserData
import com.eliel.studytrack.data.firestore.UserRepository
import com.eliel.studytrack.auth.AuthViewModel
import kotlinx.coroutines.launch
import com.eliel.studytrack.ui.theme.ThemeController
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenUI(
    navController: NavHostController,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scope = rememberCoroutineScope()
    var userData by remember { mutableStateOf<UserData?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    val pomodoroTime = remember { mutableStateOf(25) }
    val shortBreakTime = remember { mutableStateOf(5) }
    val longBreakTime = remember { mutableStateOf(15) }
    val dailyStudyGoalSessions = remember { mutableStateOf(4) }

    val studyRemindersEnabled = remember { mutableStateOf(true) }
    val taskDeadlinesEnabled = remember { mutableStateOf(true) }
    val achievementsUnlockedEnabled = remember { mutableStateOf(false) }
    val dailySummaryEnabled = remember { mutableStateOf(false) }

    val appTheme = remember { mutableStateOf("Claro") }


    LaunchedEffect(Unit) {
        scope.launch {
            userData = UserRepository.getCurrentUser()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        userData?.name ?: "Usuário",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        userData?.email ?: "email@exemplo.com",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                    Text(
                        "Plano: ${userData?.plan ?: "Gratuito"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        SectionCard(
            iconId = R.drawable.ic_timer,
            iconTint = MaterialTheme.colorScheme.error, // destaque com cor de erro (ou primary se preferir)
            title = "Timer Pomodoro"
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeDropdown(
                        label = "Tempo de Estudo (min)",
                        options = listOf(20, 25, 30, 35, 40),
                        value = pomodoroTime,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TimeDropdown(
                        label = "Pausa Curta (min)",
                        options = listOf(5, 10, 15),
                        value = shortBreakTime,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeDropdown(
                        label = "Pausa Longa (min)",
                        options = listOf(15, 20, 25, 30),
                        value = longBreakTime,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TimeDropdown(
                        label = "Meta Diária (sessões)",
                        options = listOf(2, 4, 6, 8),
                        value = dailyStudyGoalSessions,
                        isSession = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        SectionCard(
            iconId = R.drawable.ic_notifications,
            iconTint = MaterialTheme.colorScheme.primary,
            title = "Notificações"
        ) {
            SettingSwitch("Lembretes de estudo", studyRemindersEnabled)
            SettingSwitch("Prazos de tarefas", taskDeadlinesEnabled)
            SettingSwitch("Conquistas desbloqueadas", achievementsUnlockedEnabled)
            SettingSwitch("Resumo diário", dailySummaryEnabled)
        }

        Spacer(modifier = Modifier.height(20.dp))


        AppearanceSection(appTheme)

        Spacer(modifier = Modifier.height(20.dp))


        SectionCard(
            iconId = R.drawable.ic_info,
            iconTint = MaterialTheme.colorScheme.primary,
            title = "Sobre o App"
        ) {
            Text("Versão: 1.0.0", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Última atualização: 04/10/2025", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Desenvolvido por Eliel", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = { navController.navigate(Screen.Premium.route) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700),
                contentColor = Color.Black
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_crown),
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Assine o Premium", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_exit),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair da Conta", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}



@Composable
fun SectionCard(
    iconId: Int,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SettingSwitch(label: String, state: MutableState<Boolean>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = state.value,
            onCheckedChange = { state.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdown(
    label: String,
    options: List<Int>,
    value: MutableState<Int>,
    modifier: Modifier = Modifier,
    isSession: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = if (isSession) "${value.value} sessões" else "${value.value} minutos",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(if (isSession) "$option sessões" else "$option minutos", color = MaterialTheme.colorScheme.onSurface) },
                        onClick = {
                            value.value = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppearanceSection(appTheme: MutableState<String>) {
    val isDark = remember { mutableStateOf(ThemeController.isDarkMode?.invoke() ?: false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_palette),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aparência",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Modo Escuro", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "Interface escura para estudos noturnos",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Switch(
                    checked = isDark.value,
                    onCheckedChange = {
                        isDark.value = it
                        appTheme.value = if (it) "Escuro" else "Claro"
                        ThemeController.toggleTheme?.invoke()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}
