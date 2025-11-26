package com.eliel.studytrack.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {

    @Test
    fun `test predefined colors`() {
        assertEquals(Color(0xFFD0BCFF), Purple80)
        assertEquals(Color(0xFFCCC2DC), PurpleGrey80)
        assertEquals(Color(0xFFEFB8C8), Pink80)

        assertEquals(Color(0xFF6650a4), Purple40)
        assertEquals(Color(0xFF625b71), PurpleGrey40)
        assertEquals(Color(0xFF7D5260), Pink40)

        assertEquals(Color(0xFF6750A4), StudyTrackPrimary)
        assertEquals(Color(0xFFFFFFFF), StudyTrackOnPrimary)
        assertEquals(Color(0xFFEADDFF), StudyTrackPrimaryContainer)
        assertEquals(Color(0xFF21005D), StudyTrackOnPrimaryContainer)

        assertEquals(Color(0xFF625B71), StudyTrackSecondary)
        assertEquals(Color(0xFFFFFFFF), StudyTrackOnSecondary)
        assertEquals(Color(0xFFE8DEF8), StudyTrackSecondaryContainer)
        assertEquals(Color(0xFF1D192B), StudyTrackOnSecondaryContainer)

        assertEquals(Color(0xFF7D5260), StudyTrackTertiary)
        assertEquals(Color(0xFFFFFFFF), StudyTrackOnTertiary)
        assertEquals(Color(0xFFFFD8E4), StudyTrackTertiaryContainer)
        assertEquals(Color(0xFF31111D), StudyTrackOnTertiaryContainer)

        assertEquals(Color(0xFFBA1A1A), StudyTrackError)
        assertEquals(Color(0xFFFFFFFF), StudyTrackOnError)
        assertEquals(Color(0xFFFFDAD6), StudyTrackErrorContainer)
        assertEquals(Color(0xFF410002), StudyTrackOnErrorContainer)

        assertEquals(Color(0xFFFFFBFE), StudyTrackBackground)
        assertEquals(Color(0xFF1C1B1F), StudyTrackOnBackground)
        assertEquals(Color(0xFFFFFBFE), StudyTrackSurface)
        assertEquals(Color(0xFF1C1B1F), StudyTrackOnSurface)
        assertEquals(Color(0xFFE7E0EC), StudyTrackSurfaceVariant)
        assertEquals(Color(0xFF49454F), StudyTrackOnSurfaceVariant)

        assertEquals(Color(0xFF79747E), StudyTrackOutline)
        assertEquals(Color(0xFFCAC4D0), StudyTrackOutlineVariant)

        assertEquals(Color(0xFFBB86FC), Purple200)
        assertEquals(Color(0xFF6200EE), Purple500)
        assertEquals(Color(0xFF3700B3), Purple700)
        assertEquals(Color(0xFF03DAC5), Teal200)
        assertEquals(Color(0xFF018786), Teal700)
        assertEquals(Color(0xFF000000), Black)
        assertEquals(Color(0xFFFFFFFF), White)
    }
}