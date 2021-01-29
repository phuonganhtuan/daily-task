package com.example.thisprojectisforrelaxtime.alltasks

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.data.database.AppDatabase
import com.example.thisprojectisforrelaxtime.data.entity.Task
import com.example.thisprojectisforrelaxtime.databinding.ActivityAllTaskBinding
import com.example.thisprojectisforrelaxtime.statistic.StatisticActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class AllTasksActivity : AppCompatActivity(), OnUpdateTaskStatus {

    private val viewModel by lazy { AllTaskVM(AppDatabase.invoke(this).taskDao()) }

    private val allTasksAdapter by lazy { AllTasksAdapter(this) }

    private lateinit var viewBinding: ActivityAllTaskBinding

    private var currentPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAllTaskBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupView()
        observeData()
        handleEvent()
    }

    override fun onUpdateTask(task: Task, position: Int) {
        currentPos = position
        viewModel.updateTask(task)
    }

    private fun setupView() = with(viewBinding) {
        allTasksAdapter.setHasStableIds(true)
        recyclerAllTasks.apply {
            layoutManager = CacheLM(this@AllTasksActivity)
            adapter = allTasksAdapter
        }
        appBarMain.stateListAnimator = null
    }

    private fun observeData() = with(viewModel) {
        tasksForDisplay.observe(this@AllTasksActivity, Observer {
            viewBinding.progressBar.visibility = View.GONE
            allTasksAdapter.apply {
                taskDays = it
                notifyDataSetChanged()
            }
            viewBinding.recyclerAllTasks.scrollToPosition(currentPos)
            viewBinding.textEmptyTask.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun handleEvent() = with(viewBinding) {
        buttonBack.setOnClickListener {
            finish()
        }
        appBarMain.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scale = abs(verticalOffset.toFloat() / appBarMain.totalScrollRange.toFloat())
            textTitleToolbar.alpha = 1 - scale * 2
            textAllTaskTitle.alpha = scale
        })
        buttonChart.setOnClickListener {
            startActivity(Intent(this@AllTasksActivity, StatisticActivity::class.java))
        }
        buttonTime.setOnClickListener {
            setupRangePickerDialog()
        }
    }

    private fun setupRangePickerDialog() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val constraintsBuilder = CalendarConstraints.Builder()
        try {
            builder.setCalendarConstraints(constraintsBuilder.build())
            builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen)
            val picker = builder.build()
            getDateRange(picker)
            picker.show(supportFragmentManager, picker.toString())
        } catch (e: IllegalArgumentException) {
        }
    }

    private fun getDateRange(materialCalendarPicker: MaterialDatePicker<androidx.core.util.Pair<Long, Long>>) {
        materialCalendarPicker.addOnPositiveButtonClickListener { selection ->
            viewBinding.textTitleToolbar.text =
                "Daily Tasks\n(${materialCalendarPicker.headerText})"
            viewBinding.textAllTaskTitle.text =
                "Daily Tasks (${materialCalendarPicker.headerText})"
            filterTasks(selection)
        }
    }

    private fun filterTasks(selection: androidx.core.util.Pair<Long, Long>) {
        currentPos = 0
        viewModel.filterTaskByTime(Date(selection.first), Date(selection.second))
    }
}
