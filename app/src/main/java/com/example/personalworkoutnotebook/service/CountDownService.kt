package com.example.personalworkoutnotebook.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.ui.activity.CreateNewWorkoutActivity
import kotlinx.coroutines.*

class CountDownService : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var wakeLock: PowerManager.WakeLock


    private var isTimerStarted = false

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "workoutNotebook:wakeLock")

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(incomingIntent: Intent, flags: Int, startId: Int): Int {

        val timersNumber = incomingIntent.getIntExtra(TIMER_NUMBER_EXTRA, -1)
        val minutes = incomingIntent.getIntExtra(MINUTES_EXTRA, -1)
        val seconds = incomingIntent.getIntExtra(SECONDS_EXTRA, -1)


            scope.launch { countDown(minutes,seconds,timersNumber) }


        return super.onStartCommand(incomingIntent, flags, startId)
    }

    override fun stopService(intent: Intent?): Boolean {

        return super.stopService(intent)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }



    private suspend fun countDown(incomingMinutes: Int, incomingSeconds: Int, timersNumber: Int) {
        if (incomingMinutes < 0 || incomingSeconds < 0) return

        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)

        isTimerStarted = true

        var minutes = incomingMinutes
        var seconds = incomingSeconds


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
        sendMessageToActivity(incomingMinutes, incomingSeconds,timersNumber)

        wakeLock.release()
        stopSelf()
    }

    private fun sendMessageToActivity(minutes: Int,seconds: Int, timersNumber: Int) {
        val intent = Intent("timerValue")
        intent.putExtra("minutes", minutes)
        intent.putExtra("seconds", seconds)
        intent.putExtra("timersNumber", timersNumber)
        intent.putExtra("isTimerStarted", isTimerStarted)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun getSignal(){
        val player = MediaPlayer.create(this, R.raw.gong)
        player.start()
    }

    companion object {

        private const val MINUTES_EXTRA = "EXTRA_MINUTES"
        private const val SECONDS_EXTRA = "EXTRA_SECONDS"
        private const val TIMER_NUMBER_EXTRA = "EXTRA_TIMER_NUMBER"

        fun getIntent(context: Context, minutes: Int, seconds: Int, timersNumber: Int): Intent =
            Intent(context, CountDownService::class.java).apply {
                putExtra(MINUTES_EXTRA, minutes)
                putExtra(SECONDS_EXTRA, seconds)
                putExtra(TIMER_NUMBER_EXTRA, timersNumber)
            }

    }

}