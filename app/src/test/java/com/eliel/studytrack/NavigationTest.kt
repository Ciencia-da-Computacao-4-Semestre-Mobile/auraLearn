package com.eliel.studytrack.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import com.eliel.studytrack.R
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotificationHelperTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun testEnsureChannel_CreatesChannelOnOreoAndAbove() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = mock<NotificationManager>()
            whenever(context.getSystemService(NotificationManager::class.java)).thenReturn(notificationManager)

            NotificationHelper.ensureChannel(context)

            verify(notificationManager).createNotificationChannel(any())
        }
    }

    @Test
    fun testEnsureChannel_DoesNothingBelowOreo() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val notificationManager = mock<NotificationManager>()
            whenever(context.getSystemService(NotificationManager::class.java)).thenReturn(notificationManager)

            NotificationHelper.ensureChannel(context)

            verify(notificationManager, Mockito.never()).createNotificationChannel(any())
        }
    }

    @Test
    fun testNotify_DoesNotNotifyWithoutPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            val notificationManagerCompat = mock<NotificationManagerCompat>()
            whenever(NotificationManagerCompat.from(context)).thenReturn(notificationManagerCompat)
            whenever(notificationManagerCompat.areNotificationsEnabled()).thenReturn(true)

            val contextMock = mock<Context>()
            whenever(contextMock.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS))
                .thenReturn(PackageManager.PERMISSION_DENIED)

            NotificationHelper.notify(contextMock, 1, "Title", "Text")

            verify(notificationManagerCompat, Mockito.never()).notify(any(), any())
        }
    }

    @Test
    fun testNotify_SendsNotificationWhenEnabled() {
        val notificationManagerCompat = mock<NotificationManagerCompat>()
        whenever(NotificationManagerCompat.from(context)).thenReturn(notificationManagerCompat)
        whenever(notificationManagerCompat.areNotificationsEnabled()).thenReturn(true)

        NotificationHelper.notify(context, 1, "Title", "Text")

        verify(notificationManagerCompat).notify(Mockito.eq(1), any())
    }

    @Test
    fun testNotify_DoesNotSendNotificationWhenDisabled() {
        val notificationManagerCompat = mock<NotificationManagerCompat>()
        whenever(NotificationManagerCompat.from(context)).thenReturn(notificationManagerCompat)
        whenever(notificationManagerCompat.areNotificationsEnabled()).thenReturn(false)

        NotificationHelper.notify(context, 1, "Title", "Text")

        verify(notificationManagerCompat, Mockito.never()).notify(any(), any())
    }
}