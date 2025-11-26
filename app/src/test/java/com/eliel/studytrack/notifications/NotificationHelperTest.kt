package com.eliel.studytrack.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import com.eliel.studytrack.R
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class NotificationHelperTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = mockk(relaxed = true)
        notificationManagerCompat = mockk(relaxed = true)

        mockkStatic(NotificationManagerCompat::class)
        every { NotificationManagerCompat.from(context) } returns notificationManagerCompat

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mockkStatic(NotificationChannel::class)
            every { context.getSystemService(NotificationManager::class.java) } returns notificationManager
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `ensureChannel creates notification channel on Android O and above`() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.ensureChannel(context)

            verify {
                notificationManager.createNotificationChannel(
                    withArg {
                        assert(it.id == NotificationHelper.CHANNEL_ID)
                        assert(it.name == "Lembretes do StudyTrack")
                        assert(it.importance == NotificationManager.IMPORTANCE_DEFAULT)
                    }
                )
            }
        }
    }

    @Test
    fun `notify does not send notification if notifications are disabled`() {
        every { notificationManagerCompat.areNotificationsEnabled() } returns false

        NotificationHelper.notify(context, 1, "Test Title", "Test Message")

        verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `notify sends notification if notifications are enabled`() {
        every { notificationManagerCompat.areNotificationsEnabled() } returns true

        NotificationHelper.notify(context, 1, "Test Title", "Test Message")

        verify {
            notificationManagerCompat.notify(1, withArg {
                assert(it.channelId == NotificationHelper.CHANNEL_ID)
                assert(it.contentTitle == "Test Title")
                assert(it.contentText == "Test Message")
                assert(it.smallIcon.resId == R.drawable.ic_notifications)
            })
        }
    }

    @Test
    fun `notify does not send notification if POST_NOTIFICATIONS permission is not granted on Android 13+`() {
        if (Build.VERSION.SDK_INT >= 33) {
            mockkStatic(androidx.core.content.ContextCompat::class)
            every {
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            } returns PackageManager.PERMISSION_DENIED

            NotificationHelper.notify(context, 1, "Test Title", "Test Message")

            verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
        }
    }
}