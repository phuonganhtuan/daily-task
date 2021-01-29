package com.example.thisprojectisforrelaxtime.statistic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thisprojectisforrelaxtime.data.dao.TaskDao
import com.example.thisprojectisforrelaxtime.data.entity.Task
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticViewModel(private val taskDao: TaskDao) : ViewModel() {

    val filterTask = MutableLiveData<List<Task>>()
    val allTasks = taskDao.getAllTasks()
    var savedTasks = listOf<Task>()

    fun filterTaskByTime(beginDate: Date, endDate: Date) = viewModelScope.launch {
        val filteredTasks = savedTasks.filter {
            val taskDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.KOREA).parse(it.createDate)
                    ?: Calendar.getInstance().time
            taskDate.after(beginDate) && taskDate.before(endDate)
        }
        filterTask.value = filteredTasks
    }
}
