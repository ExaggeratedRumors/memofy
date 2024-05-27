package com.ertools.memofy.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Notification permission is not granted", Toast.LENGTH_LONG).show()
                return
            }
            notify(1, builder.build())
        }
    }
}