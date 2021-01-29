package com.example.thisprojectisforrelaxtime.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.main.MainActivity


class TodayTaskWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, RemoteViewsServiceTask::class.java)
            val views = RemoteViews(context.packageName, R.layout.today_task_widget)
            views.setImageViewResource(R.id.imageIcon, R.mipmap.ic_launcher)
            views.setRemoteAdapter(R.id.listTasks, intent)
            views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
            val iSetting = Intent(context, MainActivity::class.java)
            val piSetting = PendingIntent.getActivity(context, 0, iSetting, 0)
            views.setOnClickPendingIntent(R.id.layoutWidget, piSetting)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val intent = Intent(context, RemoteViewsServiceTask::class.java)
    val views = RemoteViews(context.packageName, R.layout.today_task_widget)
    views.setImageViewResource(R.id.imageIcon, R.mipmap.ic_launcher)
    views.setRemoteAdapter(R.id.listTasks, intent)
    views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
    val iSetting = Intent(context, MainActivity::class.java)
    val piSetting = PendingIntent.getActivity(context, 0, iSetting, 0)
    views.setOnClickPendingIntent(R.id.layoutWidget, piSetting)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
