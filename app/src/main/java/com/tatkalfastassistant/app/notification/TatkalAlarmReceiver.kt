package com.tatkalfastassistant.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.tatkalfastassistant.app.MainActivity
import com.tatkalfastassistant.app.TatkalApplication

class TatkalAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val trainNumber = intent.getStringExtra("TRAIN_NUM") ?: ""
        val travelClass = intent.getStringExtra("CLASS") ?: "3A"
        val fromCode = intent.getStringExtra("FROM_CODE") ?: "NDLS"
        val toCode = intent.getStringExtra("TO_CODE") ?: "CSMT"
        val journeyDate = intent.getStringExtra("DATE") ?: ""
        val quota = intent.getStringExtra("QUOTA") ?: "TQ"

        // Official IRCTC App or Web intent
        val irctcPackage = "cris.org.in.prs.ima"
        val launchIntent = context.packageManager.getLaunchIntentForPackage(irctcPackage)
            ?: Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.irctc.co.in/nget/booking/train-list?fromStation=$fromCode&toStation=$toCode&journeyDate=$journeyDate&quotaCode=$quota")
            )

        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, TatkalApplication.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("TATKAL WINDOW OPENING - Train $trainNumber ($travelClass)")
            .setContentText("Open official IRCTC now. Passenger details are prepped in Rapid Assistant.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_send,
                "Open Official IRCTC Flow",
                pendingIntent
            )
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.notify(101, notification)
    }
}
