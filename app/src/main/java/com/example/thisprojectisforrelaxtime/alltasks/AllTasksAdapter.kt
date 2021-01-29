package com.example.thisprojectisforrelaxtime.alltasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.data.entity.Task
import com.example.thisprojectisforrelaxtime.databinding.ItemTaskDayBinding
import java.text.SimpleDateFormat
import java.util.*

class AllTasksAdapter(private val listener: OnUpdateTaskStatus?) :
    RecyclerView.Adapter<AllTasksAdapter.AllTaskViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 88
    }

    var taskDays = emptyList<List<Task>>()

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTaskViewHolder {
        val itemView =
            ItemTaskDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vh = AllTaskViewHolder(itemView, listener)
        vh.itemView.findViewById<RecyclerView>(R.id.recyclerTasksDay).setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(TYPE_NORMAL, 100)
        return vh
    }

    override fun onBindViewHolder(holder: AllTaskViewHolder, position: Int) {
        holder.bindData(taskDays[position], position)
    }

    override fun getItemViewType(position: Int) = TYPE_NORMAL

    override fun getItemCount() = taskDays.size

    override fun getItemId(position: Int) = taskDays[position][0].id.toLong()

    class AllTaskViewHolder(
        private val itemVH: ItemTaskDayBinding,
        private val listener: OnUpdateTaskStatus?
    ) :
        RecyclerView.ViewHolder(itemVH.root), OnUpdateTaskStatus {

        private var item = emptyList<Task>()
        private var pos = 0

        private val adapter = TaskAdapter(this)
        private val todayDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.KOREA).format(Calendar.getInstance().time)

        init {
            itemVH.apply {
                adapter.setHasStableIds(true)
                recyclerTasksDay.layoutManager = CacheLM(itemView.context)
                recyclerTasksDay.adapter = adapter
            }
        }

        override fun onUpdateTask(task: Task, position: Int) {
            listener?.onUpdateTask(task, pos)
        }

        fun bindData(itemData: List<Task>, position: Int) {
            item = itemData
            pos = position
            itemVH.apply {
                textTime.text =
                    if (itemData[0].createDate == todayDate) "Today" else itemData[0].createDate
                adapter.tasks = itemData
                adapter.currentPos = position
                adapter.notifyDataSetChanged()
            }
        }
    }
}
