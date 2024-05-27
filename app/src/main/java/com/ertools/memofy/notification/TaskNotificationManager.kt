package com.ertools.memofy.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TaskNotificationManager(context: Context, params: WorkerParameters) : Worker(context, params) {
    fun scheduleNotification(context: Context, task: Task, minutesBefore: Int) {
        if (task.finishedAt.isNullOrEmpty()) return

        val sdf = SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault())
        val finishDate = sdf.parse(task.finishedAt) ?: return
        val notificationTime = Calendar.getInstance().apply {
            time = finishDate
            add(Calendar.MINUTE, -minutesBefore)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra(Utils.INTENT_TASK_TITLE, task.title)
            putExtra(Utils.INTENT_TASK_DESCRIPTION, task.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationTime.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun scheduleNotification(context: Context, title: String?, description: String?, triggerAtMillis: Long) {
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

    override fun doWork(): Result {
        val title = inputData.getString("title")
        val description = inputData.getString("description")
        val finishedAt = inputData.getString("finishedAt")
        val minutesBefore = inputData.getInt("minutesBefore", 10)

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

    fun scheduleTaskNotification(context: Context, task: Task, minutesBefore: Int) {
        val workRequest = OneTimeWorkRequestBuilder<TaskNotificationManager>()
            .setInitialDelay(calculateDelay(task.finishedAt, minutesBefore), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "title" to task.title,
                "description" to task.description,
                "finishedAt" to task.finishedAt,
                "minutesBefore" to minutesBefore
            ))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun calculateDelay(finishedAt: String?, minutesBefore: Int): Long {
        if (finishedAt.isNullOrEmpty()) return 0L

        val sdf = SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault())
        val finishDate = sdf.parse(finishedAt) ?: return 0L
        val notificationTime = Calendar.getInstance().apply {
            time = finishDate
            add(Calendar.MINUTE, -minutesBefore)
        }
        return notificationTime.timeInMillis - System.currentTimeMillis()
    }
}