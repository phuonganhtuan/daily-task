package com.example.thisprojectisforrelaxtime.alltasks

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thisprojectisforrelaxtime.data.dao.TaskDao
import com.example.thisprojectisforrelaxtime.data.entity.Task
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AllTaskVM(private val taskDao: TaskDao) : ViewModel() {

    val tasksForDisplay = MediatorLiveData<List<List<Task>>>()
    private var allTasks = listOf<Task>()

    init {
        tasksForDisplay.addSource(taskDao.getAllTasks(), androidx.lifecycle.Observer {
            filterTasks(it)
            allTasks = it
        })
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.updateTask(task)
    }

    fun filterTaskByTime(beginDate: Date, endDate: Date) = viewModelScope.launch {
        val filteredTasks = allTasks.filter {
            val taskDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.KOREA).parse(it.createDate)
                    ?: Calendar.getInstance().time
            taskDate.after(beginDate) && taskDate.before(endDate)
        }
        filterTasks(filteredTasks)
    }

    private fun filterTasks(tasks: List<Task>) = viewModelScope.launch {
        val resultList = mutableListOf<List<Task>>()
        val listOfDay = taskDao.getAllDays()
        listOfDay.asReversed().forEach {
            val listTask = tasks.filter { task -> task.createDate == it }
            if (listTask.isNotEmpty()) resultList.add(listTask.asReversed())
        }
        tasksForDisplay.postValue(resultList)
    }
}
