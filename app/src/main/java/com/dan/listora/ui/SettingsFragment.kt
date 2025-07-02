package com.dan.listora.ui

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dan.listora.R
import java.util.Calendar
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import com.dan.listora.notifications.NotificationReceiver


class SettingsFragment : Fragment() {

    private lateinit var switchNotifications: Switch
    private lateinit var btnSelectTime: Button

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
            Toast.makeText(requireContext(), "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        switchNotifications = view.findViewById(R.id.switch_notifications)
        btnSelectTime = view.findViewById(R.id.btn_select_time)

        val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", false)
        switchNotifications.isChecked = notificationsEnabled

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                            // Podemos volver a solicitar
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        requireActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED -> {
                            // No se puede volver a pedir, se debe redirigir a Ajustes
                            Toast.makeText(
                                requireContext(),
                                "Debes habilitar el permiso manualmente en Ajustes > Aplicaciones > Listora",
                                Toast.LENGTH_LONG
                            ).show()
                            switchNotifications.isChecked = false
                        }
                        else -> {
                            // Permiso ya concedido
                            prefs.edit().putBoolean("notifications_enabled", true).apply()
                        }
                    }
                } else {
                    // Versiones < Android 13 no requieren este permiso
                    prefs.edit().putBoolean("notifications_enabled", true).apply()
                }
            } else {
                prefs.edit().putBoolean("notifications_enabled", false).apply()
            }
        }


        btnSelectTime.setOnClickListener {
            if (!prefs.getBoolean("notifications_enabled", false)) {
                Toast.makeText(requireContext(), "Primero activa las notificaciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val time = "$selectedHour:$selectedMinute"
                prefs.edit().putString("notification_time", time).apply()
                Toast.makeText(requireContext(), "Hora guardada: $time", Toast.LENGTH_SHORT).show()

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

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si la hora ya pasó hoy, programar para mañana
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
