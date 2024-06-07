package com.ertools.memofy.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.ertools.memofy.R
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.utils.Utils

class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val task = intent.getSerializableExtra(Utils.INTENT_TASK_ID) as Task

        val args = Bundle()
        args.putSerializable(Utils.BUNDLE_TASK, task)

        val deepLink = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.nav_tasks)
            .addDestination(R.id.nav_task, args)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context, Utils.NOTIFICATION_CHANNEL_TASK)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(task.title)
            .setContentText(task.description)
            .setContentIntent(deepLink)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder)
    }
}