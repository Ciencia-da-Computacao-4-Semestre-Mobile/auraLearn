package com.eliel.studytrack.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun ReviewScreen(navController: NavHostController, materia: String, tema: String, dayText: String) {
    ReviewScreenUI(navController = navController, materia = materia, tema = tema, dayText = dayText)
}