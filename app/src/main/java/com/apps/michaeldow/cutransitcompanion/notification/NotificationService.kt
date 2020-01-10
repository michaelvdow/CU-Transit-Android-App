package com.apps.michaeldow.cutransitcompanion.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import com.apps.michaeldow.cutransitcompanion.API.ApiFactory
import com.apps.michaeldow.cutransitcompanion.API.responses.DeparturesResponse
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.views.main_activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext


class NotificationService: Service() {

    private val CHANNEL_ID = "CU Transit Update Service"
    private val ALARM_CHANNEL_ID = "CU Transit Alarm Service"
    private lateinit var departure: Departure
    private var receiver: BroadcastReceiver? = null
    private var alarmTime: Int = 1
    private val notificationId = 325

    // Coroutine stuff
    private var job = Job()
    private val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    // Notification stuff
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var manager: NotificationManager? = null
    private lateinit var runnable: Runnable
    private var notified: Boolean = false
    private val checkDuration: Long = 20000
    private var timeLeft = 100
    private var running = true

    companion object {
        fun startService(context: Context, departure: Departure, alarmTime: Int) {
            stopService(context)
            val startIntent = Intent(context, NotificationService::class.java)
            startIntent.putExtra("alarmTime", alarmTime)
            startIntent.putExtra("departure", departure)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, NotificationService::class.java)
            context.stopService(stopIntent)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        departure = intent?.getSerializableExtra("departure") as Departure
        alarmTime = intent?.getIntExtra("alarmTime", 1)
        createNotificationChannel()

        val args = Bundle().apply {
            putString("stop_id", departure.stop_id)
        }

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.departuresFragment)
            .setArguments(args)
            .createPendingIntent()


        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(departure.expected_mins.toString() + " " + getString(R.string.minutes))
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(getString(R.string.until) + " " + departure.headsign + " " + getString(R.string.arrives))
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0))
            .setSound(null)

        // Handle dismissing notification
        receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                stopService(context as Context)
            }
        }
        registerReceiver(receiver, IntentFilter("com.apps.michaeldow.cutransitcompanion.cancelNotification"))

        val cancelIntent = Intent("com.apps.michaeldow.cutransitcompanion.cancelNotification")
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingCancelIntent = PendingIntent.getBroadcast(baseContext, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        notificationBuilder.addAction(R.drawable.ic_cancel, getString(R.string.dismiss), pendingCancelIntent)

        startForeground(notificationId, notificationBuilder.build())

        createPeriodicCheck()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        running = false
    }

    private fun createPeriodicCheck() {
        val handler = Handler()
        runnable = Runnable {
            if (running) {
                try {
                    val api = ApiFactory.mtdApi
                    scope.launch {
                        val response = api.getDeparturesByStop(departure.stop_id, 45, 45).await()
                        if (response.isSuccessful) {
                            updateNotificationUI(response.body())
                        }
                    }
                } catch (e: SocketTimeoutException) {


                } catch (e: Throwable) {}


                handler.postDelayed(runnable, checkDuration)
            } else {
                stopSelf()
            }
        }
        handler.postDelayed(runnable, 0)
    }

    private fun updateNotificationUI(response: DeparturesResponse?) {
        if (response != null) {
            val departures = response.departures
            for (departure in departures) {
                if (departure.trip.trip_id == this.departure.trip.trip_id) {
                    notificationBuilder.setContentTitle(departure.expected_mins.toString() + " " + getString(
                        R.string.minutes))
                    timeLeft = departure.expected_mins

                    // Notify user if it's past the alarm time
                    if (!notified && timeLeft <= alarmTime) {

                        notificationBuilder.setChannelId(ALARM_CHANNEL_ID)
                        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
                        notificationBuilder.priority = NotificationCompat.PRIORITY_MAX
                        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        notificationBuilder.setVibrate(longArrayOf(300, 750, 300, 750, 300))
                        notificationBuilder.setContentTitle(departure.expected_mins.toString() + " " + getString(R.string.minutes))
                        val notification = notificationBuilder.build()
                        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        manager?.notify(notificationId, notification)
                        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        notificationBuilder.setVibrate(longArrayOf(0))
                        notificationBuilder.setDefaults(0)
                        notificationBuilder.setChannelId(CHANNEL_ID)

                        notified = true
                    } else {
                        manager?.notify(notificationId, notificationBuilder.build())
                    }
                    break
                }
            }
        }
    }


    private fun createNotificationChannel() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            serviceChannel.vibrationPattern = longArrayOf(0)
            serviceChannel.setSound(null, null)
            manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)

            val notificationChannel = NotificationChannel(ALARM_CHANNEL_ID, ALARM_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.vibrationPattern = longArrayOf(300, 750, 300, 750, 300)
            notificationChannel.enableVibration(true)
            val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            notificationChannel.setSound(alarmSound, audioAttributes)
            manager?.createNotificationChannel(notificationChannel)
        }
    }

}