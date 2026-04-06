package com.akash.classschuldeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    // Maps day string to Calendar.DAY_OF_WEEK constant
    private val dayOfWeekMap = mapOf(
        "MON"  to Calendar.MONDAY,
        "TUE"  to Calendar.TUESDAY,
        "WED"  to Calendar.WEDNESDAY,
        "THUR" to Calendar.THURSDAY,
        "FRI"  to Calendar.FRIDAY,
        "SAT"  to Calendar.SATURDAY,
        "SUN"  to Calendar.SUNDAY
    )

    fun scheduleClassReminders(context: Context, classes: List<schulde>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        classes.forEach { cls ->
            // Use a stable, unique ID per class to avoid cross-semester collisions
            val uniqueRequestCode = "${cls.branch}_${cls.sem}_${cls.day}_${cls.time}_${cls.subject}"
                .hashCode()
            scheduleReminder(context, alarmManager, cls, uniqueRequestCode)
        }
    }

    private fun scheduleReminder(
        context: Context,
        alarmManager: AlarmManager,
        cls: schulde,
        requestCode: Int
    ) {
        try {
            val timeParts = cls.time.split(" - ")[0].split(":")
            if (timeParts.size < 2) return

            val hour   = timeParts[0].trim().toIntOrNull() ?: return
            val minute = timeParts[1].trim().toIntOrNull() ?: return

            // Resolve the target day-of-week for this class
            val targetDayOfWeek = dayOfWeekMap[cls.day.uppercase()] ?: return

            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, targetDayOfWeek)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute - 10) // 10 mins before class
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If the calculated time is already in the past, move to next week
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

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
