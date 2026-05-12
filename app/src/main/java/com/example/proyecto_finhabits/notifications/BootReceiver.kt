package com.example.proyecto_finhabits.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyecto_finhabits.ui.screens.scheduleAlarm

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("finhabits_prefs", Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean("notifications_enabled", true)
            val hour = prefs.getInt("reminder_hour", 21)
            val minute = prefs.getInt("reminder_minute", 0)
            scheduleAlarm(context, enabled, hour, minute)
        }
    }
}