package com.example.thisprojectisforrelaxtime.statistic

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.alltasks.CacheLM
import com.example.thisprojectisforrelaxtime.alltasks.TaskAdapter
import com.example.thisprojectisforrelaxtime.data.database.AppDatabase
import com.example.thisprojectisforrelaxtime.data.entity.Priority
import com.example.thisprojectisforrelaxtime.data.entity.Status
import com.example.thisprojectisforrelaxtime.data.entity.Task
import com.example.thisprojectisforrelaxtime.databinding.ActivityStatisticBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.math.abs


class StatisticActivity : AppCompatActivity() {

    private val viewModel by lazy { StatisticViewModel(AppDatabase.invoke(this).taskDao()) }
    private lateinit var viewBinding: ActivityStatisticBinding

    private val adapter by lazy { TaskAdapter() }

    private var currentStatusPriority = Priority.Default
    private var currentStatusDone = Status.Done

    private var normalTasks = listOf<Task>()
    private var mediumTasks = listOf<Task>()
    private var highTasks = listOf<Task>()
    private var normalDone = listOf<Task>()
    private var mediumDone = listOf<Task>()
    private var highDone = listOf<Task>()
    private var normalUndone = listOf<Task>()
    private var mediumUndone = listOf<Task>()
    private var highUndone = listOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupView()
        observeData()
        handleEvent()
    }

    private fun setupView() = with(viewBinding) {
        appBarMain.stateListAnimator = null
        updatePriorityView()
        tabDoneStatus.apply {
            addTab(newTab().apply { text = "Done" })
            addTab(newTab().apply { text = "Undone" })
        }
        recyclerTasks.layoutManager = CacheLM(this@StatisticActivity)
        recyclerTasks.adapter = adapter
        adapter.isOnlyRead = true
    }

    private fun updatePriorityView() = with(viewBinding) {
        viewBgNormal.visibility = View.GONE
        viewBgMedium.visibility = View.GONE
        viewBgHigh.visibility = View.GONE
        when (currentStatusPriority) {
            Priority.Default -> viewBgNormal.visibility = View.VISIBLE
            Priority.Medium -> viewBgMedium.visibility = View.VISIBLE
            Priority.High -> viewBgHigh.visibility = View.VISIBLE
        }
    }

    private fun observeData() = with(viewModel) {
        allTasks.observe(this@StatisticActivity, Observer {
            viewBinding.textEmptyTask.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            calculateAndDisplayChart(it)
            savedTasks = it
        })
        filterTask.observe(this@StatisticActivity, Observer {
            viewBinding.textEmptyTask.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            calculateAndDisplayChart(it)
        })
    }

    private fun handleEvent() = with(viewBinding) {
        buttonBack.setOnClickListener {
            finish()
        }
        appBarMain.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scale = abs(verticalOffset.toFloat() / appBarMain.totalScrollRange.toFloat())
            textTitleToolbar.alpha = 1 - scale * 2
            textStatisticTitle.alpha = scale
        })
        viewNormal.setOnClickListener {
            currentStatusPriority = Priority.Default
            updatePriorityView()
            adapter.apply {
                this.tasks = if (tabDoneStatus.selectedTabPosition == 0) {
                    normalDone
                } else {
                    normalUndone
                }
                notifyDataSetChanged()
            }
        }
        viewHigh.setOnClickListener {
            currentStatusPriority = Priority.High
            updatePriorityView()
            adapter.apply {
                this.tasks = if (tabDoneStatus.selectedTabPosition == 0) {
                    highDone
                } else {
                    highUndone
                }
                notifyDataSetChanged()
            }
        }
        viewMedium.setOnClickListener {
            currentStatusPriority = Priority.Medium
            updatePriorityView()
            adapter.apply {
                this.tasks = if (tabDoneStatus.selectedTabPosition == 0) {
                    mediumDone
                } else {
                    mediumUndone
                }
                notifyDataSetChanged()
            }
        }
        var isChangeState = false
        scrollViewStatistic.viewTreeObserver.addOnScrollChangedListener {
            val state = scrollViewStatistic.getChildAt(0).bottom <=
                    scrollViewStatistic.height + scrollViewStatistic.scrollY
            if (!(state && isChangeState)) {
                isChangeState = !isChangeState
                recyclerTasks.isNestedScrollingEnabled = state
            }
        }
        tabDoneStatus.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tabDoneStatus.selectedTabPosition == 0) {
                    when (currentStatusPriority) {
                        Priority.Default -> adapter.apply {
                            this.tasks = normalDone
                            notifyDataSetChanged()
                        }
                        Priority.Medium -> adapter.apply {
                            this.tasks = mediumDone
                            notifyDataSetChanged()
                        }
                        Priority.High -> adapter.apply {
                            this.tasks = highDone
                            notifyDataSetChanged()
                        }
                    }
                } else {
                    when (currentStatusPriority) {
                        Priority.Default -> adapter.apply {
                            this.tasks = normalUndone
                            notifyDataSetChanged()
                        }
                        Priority.Medium -> adapter.apply {
                            this.tasks = mediumUndone
                            notifyDataSetChanged()
                        }
                        Priority.High -> adapter.apply {
                            this.tasks = highUndone
                            notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
        buttonTime.setOnClickListener {
            setupRangePickerDialog()
        }
    }

    private fun calculateAndDisplayChart(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            viewBinding.apply {
                progressTotal.progress = 0
                progressHigh.progress = 0
                progressNormal.progress = 0
                progressMedium.progress = 0
                textTotal.text = "0/0"
                textHigh.text = "0/0"
                textNormal.text = "0/0"
                textMedium.text = "0/0"
                tabDoneStatus.visibility = View.GONE
                cardHigh.visibility = View.GONE
                cardNormal.visibility = View.GONE
                cardDefault.visibility = View.GONE
                adapter.tasks = emptyList()
                adapter.notifyDataSetChanged()
            }
            return
        }
        normalTasks = tasks.filter { it.priority == Priority.Default.ordinal }
        mediumTasks = tasks.filter { it.priority == Priority.Medium.ordinal }
        highTasks = tasks.filter { it.priority == Priority.High.ordinal }
        normalDone = normalTasks.filter { it.status == Status.Done.ordinal }
        mediumDone = mediumTasks.filter { it.status == Status.Done.ordinal }
        highDone = highTasks.filter { it.status == Status.Done.ordinal }
        normalUndone = normalTasks.filter { it.status != Status.Done.ordinal }
        mediumUndone = mediumTasks.filter { it.status != Status.Done.ordinal }
        highUndone = highTasks.filter { it.status != Status.Done.ordinal }
        val totalDone = tasks.filter { it.status == Status.Done.ordinal }
        viewBinding.apply {
            tabDoneStatus.visibility = View.VISIBLE
            cardHigh.visibility = View.VISIBLE
            cardNormal.visibility = View.VISIBLE
            cardDefault.visibility = View.VISIBLE
            val totalProgress = if (tasks.isNotEmpty()) {
                totalDone.size * 100 / tasks.size
            } else {
                0
            }
            progressTotal.progress = totalProgress
            val normalProgress = if (normalTasks.isNotEmpty()) {
                normalDone.size * 100 / normalTasks.size
            } else {
                0
            }
            progressNormal.progress = normalProgress
            val mediumProgress = if (mediumTasks.isNotEmpty()) {
                mediumDone.size * 100 / mediumTasks.size
            } else {
                0
            }
            progressMedium.progress = mediumProgress
            val highProgress = if (highTasks.isNotEmpty()) {
                highDone.size * 100 / highTasks.size
            } else {
                0
            }
            progressHigh.progress = highProgress
            textTotal.text = "${totalDone.size}/${tasks.size}"
            textNormal.text = "${normalDone.size}/${normalTasks.size}"
            textMedium.text = "${mediumDone.size}/${mediumTasks.size}"
            textHigh.text = "${highDone.size}/${highTasks.size}"
            viewNormal.performClick()
            tabDoneStatus.selectTab(tabDoneStatus.getTabAt(0))
            adapter.apply {
                this.tasks = normalDone
                notifyDataSetChanged()
            }
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

    private fun getDateRange(materialCalendarPicker: MaterialDatePicker<Pair<Long, Long>>) {
        materialCalendarPicker.addOnPositiveButtonClickListener { selection ->
            viewBinding.textTitleToolbar.text =
                "Statistic\n(${materialCalendarPicker.headerText})"
            viewBinding.textStatisticTitle.text =
                "Statistic (${materialCalendarPicker.headerText})"
            filterTasks(selection)
        }
    }

    private fun filterTasks(selection: androidx.core.util.Pair<Long, Long>) {
        viewModel.filterTaskByTime(Date(selection.first), Date(selection.second))
    }
}
