package com.ertools.memofy.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ertools.memofy.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskNotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString(Utils.NOTIFICATION_DATA_TITLE)
        val description = inputData.getString(Utils.NOTIFICATION_DATA_DESCRIPTION)
        val finishedAt = inputData.getString(Utils.NOTIFICATION_DATA_FINISH_TIME)
        val minutesBefore = inputData.getInt(
            Utils.NOTIFICATION_DATA_FINISH_DELAY,
            Utils.NOTIFICATION_TIME_DEFAULT
        )

        if (!finishedAt.isNullOrEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault())
            val finishDate = sdf.parse(finishedAt) ?: return Result.failure()
            val notificationTime = Calendar.getInstance().apply {
                time = finishDate
                add(Calendar.MINUTE, -minutesBefore)
            }

            if (notificationTime.timeInMillis > System.currentTimeMillis()) {
                scheduleNotification(applicationContext, title, description, notificationTime.timeInMillis)
            }
        }

        return Result.success()
    }

    private fun scheduleNotification(
        context: Context,
        title: String?,
        description: String?,
        triggerAtMillis: Long
    ) {
        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("description", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}