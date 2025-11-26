package com.eliel.studytrack.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.eliel.studytrack.data.firestore.TaskData
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class ReminderSchedulerTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        alarmManager = mockk(relaxed = true)

        mockkStatic(PendingIntent::class)
        mockkStatic(Calendar::class)
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `scheduleDailyPlanReminder sets alarm for 9 AM`() {
        val planTitles = listOf("Plan 1", "Plan 2")
        val intentSlot = slot<Intent>()
        val pendingIntent = mockk<PendingIntent>()

        every {
            PendingIntent.getBroadcast(
                context,
                1001,
                capture(intentSlot),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent

        ReminderScheduler.scheduleDailyPlanReminder(context, planTitles)

        verify {
            alarmManager.setInexactRepeating(
                eq(AlarmManager.RTC_WAKEUP),
                any(),
                eq(AlarmManager.INTERVAL_DAY),
                eq(pendingIntent)
            )
        }

        assert(intentSlot.captured.action == "com.eliel.studytrack.notifications.StudyPlanReminderReceiver")
        assert(intentSlot.captured.getStringArrayListExtra("plan_titles") == ArrayList(planTitles))
    }

    @Test
    fun `cancelDailyPlanReminder cancels the alarm`() {
        val pendingIntent = mockk<PendingIntent>()

        every {
            PendingIntent.getBroadcast(
                context,
                1001,
                any(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent

        ReminderScheduler.cancelDailyPlanReminder(context)

        verify { alarmManager.cancel(pendingIntent) }
    }

    @Test
    fun `scheduleTaskDeadlines sets alarms for task deadlines`() {
        val task = TaskData(
            id = "task1",
            title = "Test Task",
            subject = "Math",
            dueDate = "30/11/2025"
        )
        val intentSlot = slot<Intent>()
        val pendingIntent = mockk<PendingIntent>()

        every {
            PendingIntent.getBroadcast(
                context,
                any(),
                capture(intentSlot),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent

        ReminderScheduler.scheduleTaskDeadlines(context, task)

        verify(atLeast = 1) {
            alarmManager.setExactAndAllowWhileIdle(
                eq(AlarmManager.RTC_WAKEUP),
                any(),
                eq(pendingIntent)
            )
        }

        assert(intentSlot.captured.action == "com.eliel.studytrack.notifications.TaskDeadlineReminderReceiver")
        assert(intentSlot.captured.getStringExtra("task_title") == task.title)
        assert(intentSlot.captured.getStringExtra("task_subject") == task.subject)
    }

    @Test
    fun `cancelTaskDeadlines cancels all alarms for a task`() {
        val taskId = "task1"
        val pendingIntent = mockk<PendingIntent>()

        every {
            PendingIntent.getBroadcast(
                context,
                any(),
                any(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } returns pendingIntent

        ReminderScheduler.cancelTaskDeadlines(context, taskId)

        verify(exactly = 4) { alarmManager.cancel(pendingIntent) }
    }
}