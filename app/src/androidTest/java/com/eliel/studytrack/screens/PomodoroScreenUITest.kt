package com.eliel.studytrack.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eliel.studytrack.MainActivity
import com.eliel.studytrack.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PomodoroScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testPomodoroTimerDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pomodoro_timer)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.tecnica_de_produtividade_para_estudos)).assertIsDisplayed()
    }

    @Test
    fun testTimerStartsAndPauses() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.iniciar)).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pausar)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pausar)).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.continuar)).assertIsDisplayed()
    }

    @Test
    fun testTimerResets() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.iniciar)).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.resetar)).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.iniciar)).assertIsDisplayed()
    }

    @Test
    fun testSubjectSelection() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.selecionar_materia)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.escolha_a_materia_para_estudar)).performClick()
        composeTestRule.onAllNodesWithTag("DropdownMenuItem").onFirst().performClick()
    }

    @Test
    fun testPomodoroStatsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.stats_session)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.pomodoros_concluidos)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.tempo_focado_hoje)).assertIsDisplayed()
    }

    @Test
    fun testProductivityTipsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.dicas_para_uma_sess_o_produtiva)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.elimine_distracoes_celular_redes_sociais)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.mantenha_agua_por_perto)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.nas_pausas_levante_se_e_movimente_se)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.a_cada_4_pomodoros_faca_uma_pausa_mais_longa)).assertIsDisplayed()
    }
}