package com.dan.listora.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dan.listora.ui.SettingsFragment

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean("notifications_enabled", false)
            val time = prefs.getString("notification_time", "12:00") ?: "12:00"

            if (enabled) {
                val parts = time.split(":")
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()

                // Usamos SettingsFragment para reutilizar su método
                val settings = SettingsFragment()
                settings.scheduleDailyNotification(context, hour, minute)
            }
        }
    }
}
