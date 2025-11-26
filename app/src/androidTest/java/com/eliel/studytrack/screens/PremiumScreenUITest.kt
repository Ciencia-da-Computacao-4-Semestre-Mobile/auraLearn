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
class PremiumScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testPremiumFeaturesDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.estatisticas_detalhadas_de_estudo)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.sincronizacao_em_multiplos_dispositivos)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.temas_exclusivos)).assertIsDisplayed()
    }

    @Test
    fun testPlanCardsDisplayed() {
        composeTestRule.onNodeWithText("Plano Mensal").assertIsDisplayed()
        composeTestRule.onNodeWithText("R$ 9,90/mês").assertIsDisplayed()
        composeTestRule.onNodeWithText("Plano Anual").assertIsDisplayed()
        composeTestRule.onNodeWithText("R$ 99,90/ano").assertIsDisplayed()
    }

    @Test
    fun testSubscribeButtonOpensDialog() {
        composeTestRule.onNodeWithText("Plano Mensal").performScrollTo()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.assinar)).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.inscricao_em_breve)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.entendido)).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.inscricao_em_breve)).assertDoesNotExist()
    }

    @Test
    fun testCloseButtonNavigatesBack() {
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.fechar)).performClick()
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }
}