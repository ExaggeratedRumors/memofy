package com.ertools.memofy.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ertools.memofy.R
import com.ertools.memofy.utils.Utils

class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(Utils.INTENT_TASK_TITLE)
        val description = intent.getStringExtra(Utils.INTENT_TASK_DESCRIPTION)

        val builder = NotificationCompat.Builder(context, Utils.NOTIFICATION_CHANNEL_TASK)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder)
    }
}