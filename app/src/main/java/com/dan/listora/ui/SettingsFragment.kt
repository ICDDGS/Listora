package com.dan.listora.ui

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dan.listora.R
import java.util.Calendar
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.dan.listora.notifications.NotificationReceiver

class SettingsFragment : Fragment() {

    private lateinit var switchNotifications: Switch
    private lateinit var textSelectedTime: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if (isGranted) {
            prefs.edit().putBoolean("notifications_enabled", true).apply()
            switchNotifications.isChecked = true
        } else {
            prefs.edit().putBoolean("notifications_enabled", false).apply()
            switchNotifications.isChecked = false
            Toast.makeText(requireContext(),
                getString(R.string.permiso_de_notificaciones_denegado), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        switchNotifications = view.findViewById(R.id.switch_notifications)
        textSelectedTime = view.findViewById(R.id.text_selected_time)

        val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", false)
        switchNotifications.isChecked = notificationsEnabled

        val savedTime = prefs.getString("notification_time", null)
        if (notificationsEnabled && savedTime != null) {
            val parts = savedTime.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            val amPmFormat = android.text.format.DateFormat.format("hh:mm a", calendar)
            textSelectedTime.text = amPmFormat
        } else {
            textSelectedTime.text = "—"
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefs.edit().putBoolean("notifications_enabled", true).apply()

                val savedTimeNow = prefs.getString("notification_time", null)
                val hour: Int
                val minute: Int

                if (savedTimeNow == null) {
                    hour = 12
                    minute = 0
                    prefs.edit().putString("notification_time", "12:00").apply()
                } else {
                    val parts = savedTimeNow.split(":")
                    hour = parts[0].toInt()
                    minute = parts[1].toInt()
                }

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                val amPmFormat = android.text.format.DateFormat.format("hh:mm a", calendar)
                textSelectedTime.text = amPmFormat

                scheduleDailyNotification(requireContext(), hour, minute)

            } else {
                prefs.edit().putBoolean("notifications_enabled", false).apply()
                textSelectedTime.text = "—"
                Toast.makeText(requireContext(),
                    getString(R.string.notificaciones_desactivadas), Toast.LENGTH_SHORT).show()
            }
        }

        textSelectedTime.setOnClickListener {
            if (!prefs.getBoolean("notifications_enabled", false)) {
                Toast.makeText(requireContext(),
                    getString(R.string.primero_activa_las_notificaciones), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                prefs.edit().putString("notification_time", time).apply()

                val amPmFormat = android.text.format.DateFormat.format("hh:mm a", calendar.apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                })

                textSelectedTime.text = amPmFormat

                scheduleDailyNotification(requireContext(), selectedHour, selectedMinute)

            }, hour, minute, true).show()
        }

        return view
    }

    fun scheduleDailyNotification(context: Context, hour: Int, minute: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
