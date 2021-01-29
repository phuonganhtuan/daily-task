package com.example.thisprojectisforrelaxtime.widget

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import androidx.core.content.ContextCompat
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.data.database.AppDatabase
import com.example.thisprojectisforrelaxtime.data.entity.Priority
import com.example.thisprojectisforrelaxtime.data.entity.Status
import com.example.thisprojectisforrelaxtime.data.entity.Task
import java.text.SimpleDateFormat
import java.util.*


class WidgetRemoteFactory(private val context: Context, intent: Intent?) :
    RemoteViewsFactory {

    private val taskDao by lazy { AppDatabase.invoke(context).taskDao() }
    private var todayTasks = listOf<Task>()

    private val todayAsString
        get() = SimpleDateFormat("dd-MM-yyyy", Locale.KOREA).format(Calendar.getInstance().time)

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        todayTasks = taskDao.getTasksByDayForWidget(todayAsString).asReversed()

    }

    override fun onDestroy() {
    }

    override fun getCount() = todayTasks.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_task_widget)
        rv.setTextViewText(R.id.textTitleWidget, todayTasks[position].title)
        rv.setTextViewText(R.id.textDes, todayTasks[position].description)
        val imageId =
            if (todayTasks[position].status == Status.Done.ordinal) {
                R.drawable.ic_baseline_done_24
            } else {
                R.drawable.ic_baseline_done_24_trans
            }
        rv.setImageViewResource(R.id.imageStatus, imageId)
        val priorityBg = when (todayTasks[position].priority) {
            Priority.Default.ordinal -> R.drawable.bg_low
            Priority.Medium.ordinal -> R.drawable.bg_normal
            Priority.High.ordinal -> R.drawable.bg_high
            else -> R.drawable.bg_low
        }
        rv.setImageViewResource(R.id.imagePriority, priorityBg)
        return rv
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_task_widget)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int) = todayTasks[position].id.toLong()

    override fun hasStableIds() = true
}
