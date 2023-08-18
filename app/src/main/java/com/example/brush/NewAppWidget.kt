package com.example.brush

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.brush.NewAppWidget.Companion.BUTTON_IS_VISIBLE
import com.example.brush.NewAppWidget.Companion.WIDGET_BUTTON_CLICKED
import com.example.brush.NewAppWidget.Companion.WIDGET_UPDATE_INTERVAL_HOURS
import java.lang.Exception

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    companion object {
        const val WIDGET_BUTTON_CLICKED = "widget_button_clicked"
        const val BUTTON_IS_VISIBLE = "visible_button"
        const val WIDGET_UPDATE_INTERVAL_HOURS = 9
    }
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, NewAppWidget::class.java)

        // Handle the button click event here
        if (intent.action == WIDGET_BUTTON_CLICKED) {

            views.setTextViewText(R.id.transparentSwitch, "")

            // Make the widget transparent
            views.setInt(R.id.transparentSwitch, "setBackgroundResource", android.R.color.transparent)

            appWidgetManager.updateAppWidget(componentName, views)
        }
        // make button visible again
        if (intent.action == BUTTON_IS_VISIBLE) {

            views.setTextViewText(R.id.transparentSwitch, "Have You Brushed Yet?")

            views.setInt(R.id.transparentSwitch, "setBackgroundResource", R.color.black)

            // This isn't outside of the if statements as this function can be called without intent actions
            // and it will cause bugs.
            appWidgetManager.updateAppWidget(componentName, views)
        }

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val updateInterval = WIDGET_UPDATE_INTERVAL_HOURS * 60 * 60 * 1000L // Convert to milliseconds

    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    val intent = Intent(context, NewAppWidget::class.java)
    intent.action = WIDGET_BUTTON_CLICKED


    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    // separate intent for periodic reset of widget state
    val alarmIntent = Intent(context, NewAppWidget::class.java)
    alarmIntent.action = BUTTON_IS_VISIBLE
    val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setRepeating(
        AlarmManager.RTC,
        System.currentTimeMillis() + updateInterval,
        updateInterval,
        alarmPendingIntent
    )

    views.setOnClickPendingIntent(R.id.transparentSwitch, pendingIntent)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}