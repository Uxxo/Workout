package com.example.personalworkoutnotebook.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.personalworkoutnotebook.R
import kotlinx.coroutines.*


class CountDownServiceII : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var wakeLock: PowerManager.WakeLock
    private var isTimerStarted = false

    private val CHANNEL_ID = "countdown_ii_silent_channel"
    private val NOTIFICATION_ID = 204

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "workoutNotebook:wakeLock")

        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(incomingIntent: Intent?, flags: Int, startId: Int): Int {
        if (incomingIntent == null) return START_STICKY

        val timersNumber = incomingIntent.getIntExtra(TIMER_NUMBER_EXTRA, -1)
        val minutes = incomingIntent.getIntExtra(MINUTES_EXTRA, -1)
        val seconds = incomingIntent.getIntExtra(SECONDS_EXTRA, -1)

        scope.launch { countDown(minutes, seconds, timersNumber) }

        return START_STICKY
    }

    private suspend fun countDown(incomingMinutes: Int, incomingSeconds: Int, timersNumber: Int) {
        if (incomingMinutes < 0 || incomingSeconds < 0) return

        wakeLock.acquire(10 * 60 * 1000L)
        isTimerStarted = true

        var minutes = incomingMinutes
        var seconds = incomingSeconds


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                buildSilentNotification(),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, buildSilentNotification())
        }


        while (minutes > 0 || seconds > 0) {
            sendMessageToActivity(minutes, seconds, timersNumber)

            if (seconds == 0) {
                minutes--
                seconds = 60
            }
            delay(1_000)
            seconds--
        }

        sendMessageToActivity(minutes, seconds, timersNumber)
        getSignal()
        delay(2_000)
        isTimerStarted = false
        sendMessageToActivity(incomingMinutes, incomingSeconds, timersNumber)

        wakeLock.release()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Работа таймера II в фоне",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildSilentNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Блокнот тренировок")
            .setContentText("Таймер запущен")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
    }

    private fun sendMessageToActivity(minutes: Int, seconds: Int, timersNumber: Int) {
        val intent = Intent("timerValue")
        intent.putExtra("minutes", minutes)
        intent.putExtra("seconds", seconds)
        intent.putExtra("timersNumber", timersNumber)
        intent.putExtra("isTimerStarted", isTimerStarted)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun getSignal() {
        val player = MediaPlayer.create(this, R.raw.gong)
        player.start()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    companion object {
        private const val MINUTES_EXTRA = "EXTRA_MINUTES"
        private const val SECONDS_EXTRA = "EXTRA_SECONDS"
        private const val TIMER_NUMBER_EXTRA = "EXTRA_TIMER_NUMBER"

        fun getIntent(context: Context, minutes: Int, seconds: Int, timersNumber: Int): Intent =
            Intent(context, CountDownServiceII::class.java).apply {
                putExtra(MINUTES_EXTRA, minutes)
                putExtra(SECONDS_EXTRA, seconds)
                putExtra(TIMER_NUMBER_EXTRA, timersNumber)
            }
    }
}
