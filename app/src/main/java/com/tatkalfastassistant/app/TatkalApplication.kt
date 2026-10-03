package com.tatkalfastassistant.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class TatkalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Tatkal Booking Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent reminder alarms for 10:00 AM (AC) and 11:00 AM (Non-AC) Tatkal railway booking"
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "tatkal_alerts_channel"
    }
}
