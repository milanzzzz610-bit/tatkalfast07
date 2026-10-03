package com.tatkalfastassistant.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.tatkalfastassistant.app.data.model.BookingProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object TatkalAlarmScheduler {

    private const val ACTION_ALARM_TRIGGER = "com.tatkalfastassistant.ALARM_TRIGGER"

    fun scheduleTatkalAlarm(
        context: Context,
        profile: BookingProfile,
        leadMinutes: Int = 5
    ): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return false
            }
        }

        val tz = TimeZone.getTimeZone("Asia/Kolkata")
        val calendar = Calendar.getInstance(tz)

        // Parse journey date (YYYY-MM-DD) if available, otherwise use today/tomorrow
        val bookingCalendar = Calendar.getInstance(tz)
        if (profile.journeyDate.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = tz }
                val jDate = sdf.parse(profile.journeyDate)
                if (jDate != null) {
                    bookingCalendar.time = jDate
                    // Tatkal opens 1 day prior to journey date
                    bookingCalendar.add(Calendar.DAY_OF_YEAR, -1)
                }
            } catch (_: Exception) {
                // Default to next occurrence
            }
        }

        bookingCalendar.set(Calendar.HOUR_OF_DAY, profile.openingHour)
        bookingCalendar.set(Calendar.MINUTE, 0)
        bookingCalendar.set(Calendar.SECOND, 0)
        bookingCalendar.set(Calendar.MILLISECOND, 0)

        // Subtract lead minutes
        bookingCalendar.add(Calendar.MINUTE, -leadMinutes)

        // If time is already past, roll over to tomorrow for testing
        if (bookingCalendar.timeInMillis <= calendar.timeInMillis) {
            bookingCalendar.timeInMillis = calendar.timeInMillis + (30 * 1000) // 30 sec fallback
        }

        val intent = Intent(context, TatkalAlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra("TRAIN_NUM", profile.preferredTrainNumber)
            putExtra("TRAIN_NAME", profile.preferredTrainName)
            putExtra("CLASS", profile.travelClass)
            putExtra("QUOTA", profile.quota)
            putExtra("FROM_CODE", profile.fromStationCode)
            putExtra("TO_CODE", profile.toStationCode)
            putExtra("DATE", profile.journeyDate)
            putExtra("PROFILE_ID", profile.id)
        }

        val requestCode = profile.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    bookingCalendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    bookingCalendar.timeInMillis,
                    pendingIntent
                )
            }
            return true
        } catch (_: SecurityException) {
            return false
        }
    }

    fun cancelTatkalAlarm(context: Context, profileId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TatkalAlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
        }
        val requestCode = profileId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
