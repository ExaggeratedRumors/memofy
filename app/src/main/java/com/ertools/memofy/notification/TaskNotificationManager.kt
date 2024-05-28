package com.ertools.memofy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class TaskNotificationManager {

    fun configureNotificationChannel(activity: AppCompatActivity) {
        val name = "Task Notifications"
        val descriptionText = "Notifications for tasks"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(Utils.NOTIFICATION_CHANNEL_TASK, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun scheduleTaskNotification(context: Context, task: Task, minutesBefore: Int) {
        if(task.finishedAt.isNullOrEmpty()) return
        val delayMillis = calculateNotificationDelay(task.finishedAt, minutesBefore)

        val workRequest = OneTimeWorkRequestBuilder<TaskNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                Utils.NOTIFICATION_DATA_TITLE to task.title,
                Utils.NOTIFICATION_DATA_DESCRIPTION to task.description,
                Utils.NOTIFICATION_DATA_FINISH_TIME to task.finishedAt,
                Utils.NOTIFICATION_DATA_FINISH_DELAY to minutesBefore
            ))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun calculateNotificationDelay(finishedAt: String?, minutesBefore: Int): Long {
        if (finishedAt.isNullOrEmpty()) return 0L

        val sdf = SimpleDateFormat(Utils.DATE_FORMAT, Locale.getDefault())
        val finishDate = sdf.parse(finishedAt) ?: return 0L
        val notificationTime = Calendar.getInstance().apply {
            time = finishDate
            add(Calendar.MINUTE, -minutesBefore)
        }
        return notificationTime.timeInMillis - System.currentTimeMillis()
    }
}