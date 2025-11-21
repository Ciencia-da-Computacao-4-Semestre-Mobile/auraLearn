
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
import androidx.compose.ui.res.stringResource

@Composable
fun ReportsScreenUI(navController: NavHostController) {
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
                        value = DataSource.totalCompletedTasks.value.toString(),
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_check_circle,
                        iconColor = Color(0xFF6200EE),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReportStatCard(
                        title = stringResource(R.string.horas_estudadas),
                        value = "${DataSource.totalStudyTime.value}h",
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
                        value = DataSource.totalStudySessions.value.toString(),
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_book_open,
                        iconColor = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReportStatCard(
                        title = "Média por Sessão",
                        value = "${DataSource.dailyGoalAverage.value}min",
                        period = stringResource(R.string.esta_semana),
                        icon = R.drawable.ic_chart,
                        iconColor = Color(0xFF00C853),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))


                Text(text = stringResource(R.string.progresso_por_materia), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                DataSource.subjects.forEach { subject ->
                    SubjectProgressItem(subject = subject)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))


                Text(text = stringResource(R.string.performance_por_dia_da_semana), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                PerformanceByDayOfWeekContent()
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
fun PerformanceByDayOfWeekContent() {
    val performanceData = mapOf(
        stringResource(R.string.domingo) to Pair(0, 0),
        stringResource(R.string.segunda) to Pair(0, 0),
        stringResource(R.string.terca) to Pair(0, 0),
        stringResource(R.string.quarta) to Pair(0, 0),
        stringResource(R.string.quinta) to Pair(0, 0),
        stringResource(R.string.sexta) to Pair(0, 0),
        stringResource(R.string.sabado) to Pair(0, 0)
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
                        modifier = Modifier
                            .weight(2f)
                            .height(8.dp),
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

val Subject.completionPercentage: Int
    get() = (Math.random() * 100).toInt()

val Subject.generalProgressPercentage: Int
    get() = (Math.random() * 100).toInt()


