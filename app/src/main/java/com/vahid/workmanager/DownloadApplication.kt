package com.vahid.workmanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Build.VERSION

class DownloadApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "download_channel",
            "File Download",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}