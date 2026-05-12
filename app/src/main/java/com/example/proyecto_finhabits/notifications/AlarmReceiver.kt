package com.example.proyecto_finhabits.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.proyecto_finhabits.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
        // Reschedule for next day
        val prefs = context.getSharedPreferences("finhabits_prefs", Context.MODE_PRIVATE)
        val hour = prefs.getInt("reminder_hour", 21)
        val minute = prefs.getInt("reminder_minute", 0)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        if (enabled) {
            import_scheduleAlarm(context, true, hour, minute)
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "finhabits_reminders"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios FinHabits", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Recordatorios diarios para registrar tus gastos"
            }
            nm.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "addExpense")
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val messages = listOf(
            "💰 ¿Ya registraste tus gastos de hoy?",
            "📊 Tus finanzas te esperan — registra tus movimientos",
            "🔥 ¡Mantén tu racha! Registra tus gastos ahora",
            "💚 Un pequeño hábito, un gran cambio financiero"
        )
        val msg = messages.random()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("FinHabits")
            .setContentText(msg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        nm.notify(1001, notification)
    }
}

// Forward reference to avoid circular import
fun import_scheduleAlarm(context: Context, enabled: Boolean, hour: Int, minute: Int) {
    com.example.finhabits.ui.screens.scheduleAlarm(context, enabled, hour, minute)
}
