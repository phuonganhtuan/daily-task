package com.example.thisprojectisforrelaxtime.alltasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.data.entity.Priority
import com.example.thisprojectisforrelaxtime.data.entity.Status
import com.example.thisprojectisforrelaxtime.data.entity.Task
import com.example.thisprojectisforrelaxtime.databinding.ItemTaskBinding

class TaskAdapter(private val listener: OnUpdateTaskStatus? = null) :
    RecyclerView.Adapter<TaskAdapter.TaskVH>() {

    var tasks = emptyList<Task>()

    var currentPos = 0

    var isOnlyRead = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val itemView = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskVH(itemView, listener)
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bindData(tasks[position], position, tasks.size, currentPos, isOnlyRead)
    }

    override fun getItemCount() = tasks.size

    override fun getItemId(position: Int) = tasks[0].id.toLong()

    class TaskVH(
        private val itemVH: ItemTaskBinding,
        private val listener: OnUpdateTaskStatus?
    ) :
        RecyclerView.ViewHolder(itemVH.root) {

        private var item = Task()
        private var pos = 0

        init {
            itemVH.apply {
                checkDone.setOnClickListener {
                    val status =
                        if (checkDone.isChecked) Status.Done.ordinal else Status.New.ordinal
                    listener?.onUpdateTask(item.copy(status = status), pos)
                }
            }
        }

        fun bindData(itemData: Task, position: Int, size: Int, currentPos: Int, isRead: Boolean) {
            itemVH.apply {
                textTitle.text = itemData.title
                textDes.text = itemData.description
                checkDone.isChecked = itemData.status == Status.Done.ordinal
                viewBottom.visibility = if (position == size - 1) View.GONE else View.VISIBLE
                val bg = when (itemData.priority) {
                    Priority.Default.ordinal -> R.drawable.bg_low
                    Priority.Medium.ordinal -> R.drawable.bg_normal
                    Priority.High.ordinal -> R.drawable.bg_high
                    else -> R.drawable.bg_low
                }
                viewPriority.background = ContextCompat.getDrawable(itemView.context, bg)
                if (currentPos > 1 || isRead) checkDone.isEnabled = false
            }
            item = itemData
            pos = position
        }
    }
}

interface OnUpdateTaskStatus {
    fun onUpdateTask(task: Task, position: Int)
}
