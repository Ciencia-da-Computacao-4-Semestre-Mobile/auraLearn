package com.eliel.studytrack.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar
import com.eliel.studytrack.data.firestore.TaskData

object ReminderScheduler {
    fun scheduleDailyPlanReminder(context: Context, planTitles: List<String> = emptyList()) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyPlanReminderReceiver::class.java).apply {
            putStringArrayListExtra("plan_titles", ArrayList(planTitles))
        }
        val pi = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

    fun cancelDailyPlanReminder(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyPlanReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }

    fun scheduleTaskDeadlines(context: Context, task: TaskData) {
        val date = parseDate(task.dueDate) ?: return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val times = listOf(-7, -2, -1, 0)
        times.forEachIndexed { idx, daysOffset ->
            val label = when (daysOffset) {
                -7 -> "Faltam 7 dias"
                -2 -> "Faltam 2 dias"
                -1 -> "Falta 1 dia"
                else -> "Hoje"
            }
            val cal = Calendar.getInstance().apply {
                timeInMillis = date
                add(Calendar.DAY_OF_YEAR, daysOffset)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            if (cal.timeInMillis > System.currentTimeMillis()) {
                val intent = Intent(context, TaskDeadlineReminderReceiver::class.java).apply {
                    putExtra("task_title", task.title)
                    putExtra("task_subject", task.subject)
                    putExtra("when_label", label)
                    putExtra("notify_id", 2000 + idx + task.id.hashCode())
                }
                val pi = PendingIntent.getBroadcast(
                    context,
                    2000 + idx + task.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                        } else {
                            am.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                        }
                    } else {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                    }
                } catch (_: SecurityException) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                    } else {
                        am.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
                    }
                }
            }
        }
    }

    private fun parseDate(dateStr: String): Long? {
        return try {
            val parts = dateStr.split("/")
            if (parts.size != 3) return null
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            cal.timeInMillis
        } catch (_: Exception) { null }
    }

    fun cancelTaskDeadlines(context: Context, taskId: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        listOf(0, 1, 2, 3).forEach { idx ->
            val intent = Intent(context, TaskDeadlineReminderReceiver::class.java)
            val requestCode = 2000 + idx + taskId.hashCode()
            val pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            am.cancel(pi)
        }
    }
}