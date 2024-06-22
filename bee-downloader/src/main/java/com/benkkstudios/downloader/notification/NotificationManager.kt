package com.benkkstudios.downloader.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.benkkstudios.downloader.BeeDownloader
import com.benkkstudios.downloader.DOWNLOADING_CHANNEL
import com.benkkstudios.downloader.ERROR_CHANNEL
import com.benkkstudios.downloader.R
import com.benkkstudios.downloader.SUCCESS_CHANNEL
import com.benkkstudios.downloader.database.DownloadItem
import com.benkkstudios.downloader.utils.toIdInt

internal class NotificationManager(private val context: Context) {
    private var notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private val config = BeeDownloader.config

    init {
        if (config.notificationEnable) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                throw Exception("permission POST_NOTIFICATIONS not granted")
            }
        }
    }

    private fun createNotification(channelId: String, importance: Int): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId, channelId, importance).apply {
                notificationManager.createNotificationChannel(this)
            }
        }
        return NotificationCompat.Builder(context, channelId).apply {
            setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            setSmallIcon(config.notificationIcon)
            setVibrate(LongArray(0))
        }
    }


    @SuppressLint("MissingPermission")
    fun onProgress(item: DownloadItem, progress: Int? = null) {
        if (!config.notificationEnable) return
        createNotification(DOWNLOADING_CHANNEL, IMPORTANCE_LOW).apply {
            setOngoing(progress != null)
            setContentText(item.filename)
            setContentTitle("Download Started")
            setProgress(100, progress ?: 0, false)
            setPriority(NotificationCompat.PRIORITY_LOW)
        }.also {
            notificationManager.notify(item.toIdInt(), it.build())
        }
    }

    @SuppressLint("MissingPermission")
    fun onAllComplete() {
        if (!config.notificationEnable) return
        clearDownloadChannel()
        createNotification(SUCCESS_CHANNEL, IMPORTANCE_HIGH).apply {
            setOngoing(false)
            setContentText("all image downloaded successfully")
            setContentTitle("Download Complete")
            setPriority(NotificationCompat.PRIORITY_HIGH)
        }.also {
            notificationManager.notify(4123, it.build())
        }
    }

    @SuppressLint("MissingPermission")
    fun onComplete(item: DownloadItem) {
        if (!config.notificationEnable) return
        clearDownloadChannel()
        createNotification(SUCCESS_CHANNEL, IMPORTANCE_HIGH).apply {
            setOngoing(false)
            setContentText(item.filename)
            setContentTitle("Download Complete")
            setPriority(NotificationCompat.PRIORITY_HIGH)
        }.also {
            notificationManager.notify(item.toIdInt(), it.build())
        }
    }

    @SuppressLint("MissingPermission")
    fun onError(item: DownloadItem) {
        if (!config.notificationEnable) return
        clearDownloadChannel()
        createNotification(ERROR_CHANNEL, IMPORTANCE_HIGH).apply {
            setOngoing(false)
            setContentText(item.filename)
            setContentTitle("Download Error")
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(true)
            setDefaults(Notification.DEFAULT_ALL)
            setWhen(System.currentTimeMillis())
        }.also {
            notificationManager.notify(item.toIdInt(), it.build())
        }
    }

    @SuppressLint("MissingPermission")
    fun onCancel(item: DownloadItem) {
        if (!config.notificationEnable) return
        clearDownloadChannel()
        createNotification(SUCCESS_CHANNEL, IMPORTANCE_HIGH).apply {
            setOngoing(false)
            setContentText(item.filename)
            setContentTitle("Download Cancelled")
            setPriority(NotificationCompat.PRIORITY_HIGH)
        }.also {
            notificationManager.notify(item.toIdInt(), it.build())
        }
    }

    private fun clearDownloadChannel() {
        notificationManager.deleteNotificationChannel(DOWNLOADING_CHANNEL)
        notificationManager.deleteNotificationChannel(SUCCESS_CHANNEL)
    }
}