package com.eliel.studytrack.ui.theme

object ThemeController {
    var toggleTheme: (() -> Unit)? = null
    var isDarkMode: (() -> Boolean)? = null
}