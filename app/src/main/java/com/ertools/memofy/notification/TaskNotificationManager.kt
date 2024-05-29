package com.ertools.memofy.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

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
        if(task.notification != true || task.finishedAt == null)
            throw IllegalStateException("ERROR: Task is not ready for notification.")
        if(!checkNotificationPermissions(context))
            throw IllegalStateException("ERROR: Notification permissions not granted.")

        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra(Utils.INTENT_TASK_TITLE, task.title)
            putExtra(Utils.INTENT_TASK_DESCRIPTION, task.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val sdf = SimpleDateFormat(Utils.DATE_FORMAT, Locale.getDefault())
        val finishDate = sdf.parse(task.finishedAt)
            ?: throw IllegalArgumentException("Wrong date format")
        val calendar = Calendar.getInstance().apply {
            time = finishDate
            add(Calendar.MINUTE, -minutesBefore)
        }
        val time = max(calendar.timeInMillis, System.currentTimeMillis())
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    fun cancelTaskNotification(context: Context, task: Task) {
        val intent = Intent(Utils.INTENT_TASK_TITLE).apply {
            putExtra(Utils.INTENT_TASK_TITLE, task.title)
            putExtra(Utils.INTENT_TASK_DESCRIPTION, task.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun checkNotificationPermissions(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isEnabled = notificationManager.areNotificationsEnabled()

        if (!isEnabled) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(intent)
            return false
        }
        return true
    }
}