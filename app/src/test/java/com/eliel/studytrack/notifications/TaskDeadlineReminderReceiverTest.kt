package com.eliel.studytrack.notifications

import android.content.Context
import android.content.Intent
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class TaskDeadlineReminderReceiverTest {

    private lateinit var context: Context
    private lateinit var intent: Intent
    private val receiver = TaskDeadlineReminderReceiver()

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
    fun `onReceive sends notification with task title, subject, and when label`() {
        every { intent.getStringExtra("task_title") } returns "Math Homework"
        every { intent.getStringExtra("task_subject") } returns "Math"
        every { intent.getStringExtra("when_label") } returns "Faltam 2 dias"
        every { intent.getIntExtra("notify_id", 2000) } returns 2001

        receiver.onReceive(context, intent)

        verify {
            NotificationHelper.notify(
                context,
                2001,
                "Prazo de Tarefa",
                "Faltam 2 dias: Math Homework (Math)"
            )
        }
    }

    @Test
    fun `onReceive sends notification with task title and when label if subject is empty`() {
        every { intent.getStringExtra("task_title") } returns "Science Project"
        every { intent.getStringExtra("task_subject") } returns ""
        every { intent.getStringExtra("when_label") } returns "Hoje"
        every { intent.getIntExtra("notify_id", 2000) } returns 2002

        receiver.onReceive(context, intent)

        verify {
            NotificationHelper.notify(
                context,
                2002,
                "Prazo de Tarefa",
                "Hoje: Science Project"
            )
        }
    }

    @Test
    fun `onReceive sends default notification if task title is missing`() {
        every { intent.getStringExtra("task_title") } returns null
        every { intent.getStringExtra("task_subject") } returns "History"
        every { intent.getStringExtra("when_label") } returns "Falta 1 dia"
        every { intent.getIntExtra("notify_id", 2000) } returns 2003

        receiver.onReceive(context, intent)

        verify {
            NotificationHelper.notify(
                context,
                2003,
                "Prazo de Tarefa",
                "Falta 1 dia: Tarefa (History)"
            )
        }
    }
}