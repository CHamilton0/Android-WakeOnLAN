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
        for (widgetId in appWidgetIds) {

            val views = RemoteViews(context.packageName, R.layout.wake_widget)

            // Button click action
            val intent = Intent(context, WakeWidget::class.java).apply {
                action = "WAKE_ACTION"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.buttonWake, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        println("Widget button pressed")

        if (intent.action == "WAKE_ACTION") {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val (mac, ip) = WakePreferences.load(context)
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
