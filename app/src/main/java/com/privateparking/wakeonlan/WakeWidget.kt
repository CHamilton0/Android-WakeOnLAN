package com.privateparking.wakeonlan

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WakeWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (name, _, _) = WakePreferences.load(context)

                for (widgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.wake_widget)
                    views.setTextViewText(R.id.deviceName, name ?: "")

                    val intent = Intent(context, WakeWidget::class.java).apply {
                        action = "WAKE_ACTION"
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        widgetId, // Use widgetId to ensure uniqueness
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    views.setOnClickPendingIntent(R.id.buttonWake, pendingIntent)
                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "WAKE_ACTION") {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val (_, mac, ip) = WakePreferences.load(context)
                    val macAddress = mac ?: ""
                    val ipAddress = ip ?: ""
                    if (macAddress.isNotBlank() && ipAddress.isNotBlank()) {
                        sendWakeOnLan(macAddress, ipAddress)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
