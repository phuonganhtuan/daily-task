package com.example.thisprojectisforrelaxtime.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String = "",
    var description: String = "",
    val status: Int = Status.New.ordinal,
    var createDate: String = "",
    var priority: Int = Priority.Default.ordinal,
    val progress: Int = 0
)

enum class Status { New, InProgress, Done, Rejected }

enum class Priority { Default, Medium, High }
