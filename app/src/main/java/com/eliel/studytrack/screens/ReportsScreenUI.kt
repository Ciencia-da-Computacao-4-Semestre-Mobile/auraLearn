
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.data.Achievement
import com.eliel.studytrack.data.DataSource
import com.eliel.studytrack.data.Priority
import com.eliel.studytrack.data.Subject
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward

@Composable
fun ReportsScreenUI(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(text = "Relatórios", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "Acompanhe seu progresso nos estudos", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            item {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ReportStatCard(
                        title = "Tarefas Concluídas",
                        value = DataSource.totalCompletedTasks.value.toString(),
                        period = "Esta semana",
                        icon = R.drawable.ic_check_circle,
                        iconColor = Color(0xFF6200EE),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReportStatCard(
                        title = "Horas Estudadas",
                        value = "${DataSource.totalStudyTime.value}h",
                        period = "Esta semana",
                        icon = R.drawable.ic_time,
                        iconColor = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ReportStatCard(
                        title = "Sessões de Estudo",
                        value = DataSource.totalStudySessions.value.toString(),
                        period = "Esta semana",
                        icon = R.drawable.ic_book_open,
                        iconColor = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReportStatCard(
                        title = "Média por Sessão",
                        value = "${DataSource.dailyGoalAverage.value}min",
                        period = "Esta semana",
                        icon = R.drawable.ic_chart,
                        iconColor = Color(0xFF00C853),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))


                Text(text = "Progresso por Matéria", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                DataSource.subjects.forEach { subject ->
                    SubjectProgressItem(subject = subject)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))


                Text(text = "Performance por Dia da Semana", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                PerformanceByDayOfWeekContent()
                Spacer(modifier = Modifier.height(24.dp))


                RecentAchievementsContent(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
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
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Progresso Geral: ${subject.generalProgressPercentage}%", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(
                progress = subject.generalProgressPercentage / 100f,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun PerformanceByDayOfWeekContent() {
    val performanceData = mapOf(
        "Domingo" to Pair(0, 0),
        "Segunda" to Pair(0, 0),
        "Terça" to Pair(0, 0),
        "Quarta" to Pair(0, 0),
        "Quinta" to Pair(0, 0),
        "Sexta" to Pair(0, 0),
        "Sábado" to Pair(0, 0)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            performanceData.forEach { (day, time) ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = day, modifier = Modifier.weight(1f), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    LinearProgressIndicator(
                        progress = 0.1f, // Placeholder progress
                        modifier = Modifier.weight(2f).height(8.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${time.first}h ${time.second}min", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RecentAchievementsContent(navController: NavHostController) {
    val achievements = DataSource.achievements.filter { it.isUnlocked }.take(3)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.ic_trophy_fill), contentDescription = "Conquistas Recentes", tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Conquistas Recentes", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (achievements.isEmpty()) {
                Text("Nenhuma conquista recente.", color = Color.White.copy(alpha = 0.7f))
            } else {
                achievements.forEach { achievement ->
                    AchievementDisplayItem(achievement = achievement)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }


            TextButton(
                onClick = { navController.navigate("achievements_list") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver todas as conquistas", color = Color.White, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ArrowForward, contentDescription = "Ver todas", tint = Color.White)
            }
        }
    }
}

@Composable
fun AchievementDisplayItem(achievement: Achievement) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(id = R.drawable.ic_trophy), contentDescription = "Conquista", tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = achievement.title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.White)
                Text(text = achievement.description, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }
        Text(text = "+${achievement.points}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}


val Subject.completionPercentage: Int
    get() = (Math.random() * 100).toInt()

val Subject.generalProgressPercentage: Int
    get() = (Math.random() * 100).toInt()


