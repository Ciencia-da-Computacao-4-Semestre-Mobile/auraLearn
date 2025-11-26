package com.eliel.studytrack.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class TypeTest {

    @Test
    fun `Typography displayLarge has correct properties`() {
        val style = Typography.displayLarge
        assertEquals(FontFamily.Default, style.fontFamily)
        assertEquals(FontWeight.Normal, style.fontWeight)
        assertEquals(57.sp, style.fontSize)
        assertEquals(64.sp, style.lineHeight)
        assertEquals((-0.25).sp, style.letterSpacing)
    }

    @Test
    fun `Typography titleMedium has correct properties`() {
        val style = Typography.titleMedium
        assertEquals(FontFamily.Default, style.fontFamily)
        assertEquals(FontWeight.Medium, style.fontWeight)
        assertEquals(16.sp, style.fontSize)
        assertEquals(24.sp, style.lineHeight)
        assertEquals(0.15.sp, style.letterSpacing)
    }

    @Test
    fun `Typography bodySmall has correct properties`() {
        val style = Typography.bodySmall
        assertEquals(FontFamily.Default, style.fontFamily)
        assertEquals(FontWeight.Normal, style.fontWeight)
        assertEquals(12.sp, style.fontSize)
        assertEquals(16.sp, style.lineHeight)
        assertEquals(0.4.sp, style.letterSpacing)
    }

    @Test
    fun `Typography labelLarge has correct properties`() {
        val style = Typography.labelLarge
        assertEquals(FontFamily.Default, style.fontFamily)
        assertEquals(FontWeight.Medium, style.fontWeight)
        assertEquals(14.sp, style.fontSize)
        assertEquals(20.sp, style.lineHeight)
        assertEquals(0.1.sp, style.letterSpacing)
    }
}