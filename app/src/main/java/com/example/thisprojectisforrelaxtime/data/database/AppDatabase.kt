package com.example.thisprojectisforrelaxtime.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thisprojectisforrelaxtime.data.dao.TaskDao
import com.example.thisprojectisforrelaxtime.data.entity.Task

@Database(entities = [Task::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        private const val DATABASE_NAME = "DailyTrackingDB"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, DATABASE_NAME
        ).build()
    }
}
