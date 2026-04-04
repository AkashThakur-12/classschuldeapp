package com.akash.classschuldeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    fun scheduleClassReminders(context: Context, classes: List<schulde>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        classes.forEachIndexed { index, cls ->
            scheduleReminder(context, alarmManager, cls, index)
        }
    }

    private fun scheduleReminder(context: Context, alarmManager: AlarmManager, cls: schulde, requestCode: Int) {
        try {
            val timeParts = cls.time.split(" - ")[0].split(":")
            if (timeParts.size < 2) return

            val hour = timeParts[0].trim().toIntOrNull() ?: return
            val minute = timeParts[1].trim().toIntOrNull() ?: return

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute - 10) // 10 mins before
                set(Calendar.SECOND, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val intent = Intent(context, ClassAlarmReceiver::class.java).apply {
                putExtra("subject", cls.subject)
                putExtra("time", cls.time)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: Exception) {
            // Silently ignore scheduling errors
        }
    }
}
