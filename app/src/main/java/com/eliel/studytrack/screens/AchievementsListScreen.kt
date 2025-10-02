package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsListScreen(navController: NavHostController) {
    val unlocked = DataSource.achievements.filter { it.isUnlocked }
    val locked = DataSource.achievements.filter { !it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas as Conquistas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues) // usar o padding do Scaffold
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Conquistas Desbloqueadas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (unlocked.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma conquista desbloqueada ainda.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    items(unlocked) { achievement ->
                        AchievementItem(achievement = achievement)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Conquistas Futuras",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (locked.isEmpty()) {
                    item {
                        Text(
                            text = "Sem conquistas futuras por enquanto.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    items(locked) { achievement ->
                        AchievementItem(achievement = achievement)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    )
}

@Composable
fun AchievementItem(achievement: Achievement) {
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
                painter = painterResource(
                    id = if (achievement.isUnlocked) R.drawable.ic_trophy_fill else R.drawable.ic_lock
                ),
                contentDescription = "Achievement Icon",
                tint = if (achievement.isUnlocked) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = achievement.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
