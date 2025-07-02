package com.dan.listora.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dan.listora.R
import androidx.room.Room
import com.dan.listora.data.db.ListDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                context,
                ListDataBase::class.java,
                "list_database"
            ).build()

            val pendientes = db.ingredientDAO().existsUnpurchased()

            if (pendientes) {
                val channelId = "listora_channel"
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId, "Listora Notificaciones", NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(context, channelId)
                    .setContentTitle(context.getString(R.string.recordatorio_de_compras))
                    .setContentText(context.getString(R.string.tienes_listas_con_ingredientes_pendientes))
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(1001, notification)
            }
        }
    }

}
