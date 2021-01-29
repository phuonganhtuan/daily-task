package com.example.thisprojectisforrelaxtime.widget

import android.content.Intent
import android.widget.RemoteViewsService


class RemoteViewsServiceTask : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): WidgetRemoteFactory {
        return WidgetRemoteFactory(this.applicationContext, intent)
    }
}
