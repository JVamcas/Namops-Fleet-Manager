package com.pet001kambala.namopsfleetmanager.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.ui.MainActivity
import com.pet001kambala.namopsfleetmanager.utils.Const

class FBMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        //check if msg contains notification payload
        msg.notification?.apply {
            sendLocalNotification(this)
        }
    }

    private fun sendLocalNotification(notification: RemoteMessage.Notification){
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, Const.TYRE_WORNOUT_NOTIFICATION_CHANNEL)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.seanam_logo)
            .setContentIntent(pendingIntent)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setSound(soundUri)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Const.TYRE_WORNOUT_NOTIFICATION_CHANNEL,
                "Tyre Worn-Out Alert",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1234,notificationBuilder.build())
    }

    override fun onNewToken(p0: String) {
    }
}