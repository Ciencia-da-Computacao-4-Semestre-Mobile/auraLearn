package com.eliel.studytrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF381E72),
    onSecondary = Color(0xFF332D41),
    onTertiary = Color(0xFF492532),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
)

private val LightColorScheme = lightColorScheme(
    primary = StudyTrackPrimary,
    onPrimary = StudyTrackOnPrimary,
    primaryContainer = StudyTrackPrimaryContainer,
    onPrimaryContainer = StudyTrackOnPrimaryContainer,
    secondary = StudyTrackSecondary,
    onSecondary = StudyTrackOnSecondary,
    secondaryContainer = StudyTrackSecondaryContainer,
    onSecondaryContainer = StudyTrackOnSecondaryContainer,
    tertiary = StudyTrackTertiary,
    onTertiary = StudyTrackOnTertiary,
    tertiaryContainer = StudyTrackTertiaryContainer,
    onTertiaryContainer = StudyTrackOnTertiaryContainer,
    error = StudyTrackError,
    onError = StudyTrackOnError,
    errorContainer = StudyTrackErrorContainer,
    onErrorContainer = StudyTrackOnErrorContainer,
    background = StudyTrackBackground,
    onBackground = StudyTrackOnBackground,
    surface = StudyTrackSurface,
    onSurface = StudyTrackOnSurface,
    surfaceVariant = StudyTrackSurfaceVariant,
    onSurfaceVariant = StudyTrackOnSurfaceVariant,
    outline = StudyTrackOutline,
    outlineVariant = StudyTrackOutlineVariant,
)

@Composable
fun StudyTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
