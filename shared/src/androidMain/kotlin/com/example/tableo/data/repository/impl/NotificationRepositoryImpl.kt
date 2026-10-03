package com.example.tableo.data.repository.impl

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.tableo.data.repository.NotificationRepository

private const val ChannelId = "orders"

/** Lokale Benachrichtigungen über den NotificationManager. */
class NotificationRepositoryImpl(
    private val context: Context
) : NotificationRepository {

    init {
        // ab Android 8 braucht jede Benachrichtigung einen Kanal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ChannelId, "Bestellungen", NotificationManager.IMPORTANCE_HIGH)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun show(id: Int, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        // Antippen öffnet die App
        // MainActivity liegt im App-Modul, deshalb über den Start-Intent der App
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, ChannelId)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            // gleiche Benachrichtigung nochmal = kein neuer Ton
            .setOnlyAlertOnce(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}
