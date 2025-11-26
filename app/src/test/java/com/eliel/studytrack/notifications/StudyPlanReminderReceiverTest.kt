package com.eliel.studytrack.notifications

import android.content.Context
import android.content.Intent
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class StudyPlanReminderReceiverTest {

    private lateinit var context: Context
    private lateinit var intent: Intent
    private val receiver = StudyPlanReminderReceiver()

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        intent = mockk(relaxed = true)

        mockkObject(NotificationHelper)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `onReceive sends notification with titles if provided`() {
        val titles = arrayListOf("Math", "Science")
        every { intent.getStringArrayListExtra("plan_titles") } returns titles

        receiver.onReceive(context, intent)

        verify {
            NotificationHelper.notify(
                context,
                1001,
                "Plano de Estudos",
                "Continue seu cronograma: Math, Science."
            )
        }
    }

    @Test
    fun `onReceive sends default notification if no titles are provided`() {
        every { intent.getStringArrayListExtra("plan_titles") } returns null

        receiver.onReceive(context, intent)

        verify {
            NotificationHelper.notify(
                context,
                1001,
                "Plano de Estudos",
                "Você tem planos de estudo para continuar hoje."
            )
        }
    }
}