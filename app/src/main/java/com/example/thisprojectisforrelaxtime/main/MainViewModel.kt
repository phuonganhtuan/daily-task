package com.example.thisprojectisforrelaxtime.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thisprojectisforrelaxtime.data.dao.TaskDao
import com.example.thisprojectisforrelaxtime.data.entity.Task
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val todayAsString
        get() = SimpleDateFormat("dd-MM-yyyy", Locale.KOREA).format(Calendar.getInstance().time)

    val todayTask = taskDao.getTasksByDay(todayAsString)

    fun insertTask(task: Task) = viewModelScope.launch {
        task.createDate = todayAsString
        taskDao.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.updateTask(task)
    }

    fun deleteTaskById(id: Int) = viewModelScope.launch {
        taskDao.deleteTaskById(id)
    }
}
