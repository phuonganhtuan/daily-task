package com.example.thisprojectisforrelaxtime.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.thisprojectisforrelaxtime.data.entity.Task

@Dao
interface TaskDao {

    @Query("select * from Task order by createDate desc")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("select * from Task where createDate = :day")
    fun getTasksByDay(day: String): LiveData<List<Task>>

    @Query("select * from Task where createDate = :day")
    fun getTasksByDayForWidget(day: String): List<Task>

    @Insert
    suspend fun insertTask(task: Task)

    @Query("delete from Task where id = :id")
    suspend fun deleteTaskById(id: Int)

    @Update(onConflict = REPLACE)
    suspend fun updateTask(task: Task)

    @Query("select distinct createDate from Task")
    suspend fun getAllDays(): List<String>
}
