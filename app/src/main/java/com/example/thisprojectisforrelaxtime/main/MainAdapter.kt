package com.example.thisprojectisforrelaxtime.main

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.data.entity.Priority
import com.example.thisprojectisforrelaxtime.data.entity.Status
import com.example.thisprojectisforrelaxtime.data.entity.Task
import com.example.thisprojectisforrelaxtime.databinding.ItemTaskSwipeableBinding

class MainAdapter(private val listener: OnTaskInteract?) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    var tasks = emptyList<Task>()

    private val viewBinderHelper = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemView =
            ItemTaskSwipeableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(
            holder.itemView.findViewById(R.id.swipeLayout), tasks[position].id.toString()
        )
        viewBinderHelper.closeLayout(tasks[position].id.toString())
        holder.bindData(tasks[position], position, tasks.size)
    }

    override fun getItemCount() = tasks.size

    class MainViewHolder(
        private val itemVH: ItemTaskSwipeableBinding,
        private val listener: OnTaskInteract?
    ) :
        RecyclerView.ViewHolder(itemVH.root) {

        private var item = Task()
        private var pos = 0

        init {
            itemVH.apply {
                editTitle.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        editTitle.clearFocus()
                        if (editTitle.text.isNullOrBlank()) {
                            editTitle.setText(item.title)
                            editTitle.clearFocus()
                            return@setOnEditorActionListener false
                        }
                        listener?.onUpdateTask(item.copy(title = editTitle.text.toString()), pos)
                    }
                    true
                }
                editDes.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        editDes.clearFocus()
                        if (editDes.text.isNullOrBlank()) {
                            listener?.onUpdateTask(item.copy(description = "No description"), pos)
                            return@setOnEditorActionListener false
                        }
                        listener?.onUpdateTask(
                            item.copy(description = editDes.text.toString()),
                            pos
                        )
                    }
                    true
                }
//                editTitle.addTextChangedListener(object : TextWatcher {
//                    override fun beforeTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        count: Int,
//                        after: Int
//                    ) {
//                    }
//
//                    override fun afterTextChanged(s: Editable?) {
//                        if (!s.isNullOrBlank()) item.title = s.toString()
//                    }
//
//                    override fun onTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        before: Int,
//                        count: Int
//                    ) {
//                    }
//                })
//                editDes.addTextChangedListener(object : TextWatcher {
//                    override fun beforeTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        count: Int,
//                        after: Int
//                    ) {
//                    }
//
//                    override fun afterTextChanged(s: Editable?) {
//                        item.description =
//                            if (!s.isNullOrBlank()) s.toString() else "No description"
//                    }
//
//                    override fun onTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        before: Int,
//                        count: Int
//                    ) {
//                    }
//                })
//                editTitle.setOnClickListener {
//                    listener?.onUpdateTask(item, pos)
//                }
//                editDes.setOnClickListener {
//                    listener?.onUpdateTask(item, pos)
//                }
                checkDone.setOnClickListener {
                    val status =
                        if (checkDone.isChecked) Status.Done.ordinal else Status.New.ordinal
                    listener?.onUpdateTask(item.copy(status = status), pos)
                }
                layoutDelete.setOnClickListener {
                    listener?.onDeleteTask(item.id, pos)
                }
                viewChangePriority.setOnClickListener {
                    val newPriority = when (item.priority) {
                        Priority.Default.ordinal -> Priority.Medium.ordinal
                        Priority.Medium.ordinal -> Priority.High.ordinal
                        else -> Priority.Default.ordinal
                    }
                    item.priority = newPriority
                    listener?.onUpdateTask(item, pos)
                }
                editTitle.apply {
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                }
                editDes.apply {
                    imeOptions = EditorInfo.IME_ACTION_DONE
                    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                }
            }
        }

        fun bindData(itemData: Task, position: Int, size: Int) {
            item = itemData
            pos = position
            itemVH.apply {
                editTitle.apply {
                    setText(itemData.title)
                    clearFocus()
                }
                editDes.apply {
                    setText(itemData.description)
                    clearFocus()
                }
                checkDone.isChecked = itemData.status == Status.Done.ordinal
                viewBottom.visibility = if (position == size - 1) View.GONE else View.VISIBLE
                val bg = when (item.priority) {
                    Priority.Default.ordinal -> R.drawable.bg_low
                    Priority.Medium.ordinal -> R.drawable.bg_normal
                    Priority.High.ordinal -> R.drawable.bg_high
                    else -> R.drawable.bg_low
                }
                viewPriority.background = ContextCompat.getDrawable(itemView.context, bg)
            }
        }
    }
}

interface OnTaskInteract {
    fun onUpdateTask(task: Task, position: Int)
    fun onDeleteTask(taskId: Int, position: Int)
}
