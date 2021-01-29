package com.example.thisprojectisforrelaxtime.main

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction.Done
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.Observer
import com.example.thisprojectisforrelaxtime.R
import com.example.thisprojectisforrelaxtime.alltasks.AllTasksActivity
import com.example.thisprojectisforrelaxtime.alltasks.CacheLM
import com.example.thisprojectisforrelaxtime.data.database.AppDatabase
import com.example.thisprojectisforrelaxtime.data.entity.Status
import com.example.thisprojectisforrelaxtime.data.entity.Task
import com.example.thisprojectisforrelaxtime.databinding.ActivityMainBinding
import com.example.thisprojectisforrelaxtime.statistic.StatisticActivity
import com.example.thisprojectisforrelaxtime.widget.TodayTaskWidget
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs


class MainActivity : AppCompatActivity(), OnTaskInteract {

    private val taskAdapter by lazy { MainAdapter(this) }

    private lateinit var viewBinding: ActivityMainBinding

    private var currentPos = 0

    private val viewModel by lazy { MainViewModel(AppDatabase.invoke(this).taskDao()) }

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MainActivityView(viewModel) {
//                startActivity(Intent(this@MainActivity, AllTasksActivity::class.java))
//            }
//        }
//        observeData()
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupView()
        observeData()
        handleEvent()
    }

    override fun onUpdateTask(task: Task, position: Int) {
        currentPos = position
        viewModel.updateTask(task)
    }

    override fun onDeleteTask(taskId: Int, position: Int) {
        currentPos = position
        viewModel.deleteTaskById(taskId)
    }

    private fun setupView() = with(viewBinding) {
        recyclerTasks.layoutManager = CacheLM(this@MainActivity)
        recyclerTasks.adapter = taskAdapter
        textTaskTitle.text = getString(R.string.today_tasks, "0", "0")
        textTitleToolbar.text = getString(R.string.today_tasks, "0", "0")
        appBarMain.stateListAnimator = null
    }

    private fun observeData() = with(viewModel) {
        todayTask.observe(this@MainActivity, Observer {
            val numDone = it.count { task -> task.status == Status.Done.ordinal }
            viewBinding.textTaskTitle.text =
                getString(R.string.today_tasks, numDone.toString(), it.size.toString())
            viewBinding.textTitleToolbar.text =
                getString(R.string.today_tasks, numDone.toString(), it.size.toString())
            taskAdapter.apply {
                tasks = it.asReversed()
                notifyDataSetChanged()
                if (it.isNotEmpty()) {
                    val realPos = if (currentPos == it.size) currentPos - 1 else currentPos
                    viewBinding.recyclerTasks.scrollToPosition(realPos)
                }
            }
            viewBinding.textEmptyTask.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            updateWidget()
        })
    }

    private fun handleEvent() = with(viewBinding) {
        buttonAddTask.setOnClickListener {
            viewModel.insertTask(Task(title = "New task", description = "No description"))
        }
        buttonViewAll.setOnClickListener {
            startActivity(Intent(this@MainActivity, AllTasksActivity::class.java))
        }
        appBarMain.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scale = abs(verticalOffset.toFloat() / appBarMain.totalScrollRange.toFloat())
            textTitleToolbar.alpha = 1 - scale * 2
            textTaskTitle.alpha = scale
        })
    }

    private fun updateWidget() {
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, TodayTaskWidget::class.java)
        )
        val appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listTasks)
    }
}
//
//@Composable
//fun MainActivityView(viewModel: MainViewModel?, navigateToDailyTask: () -> Unit) {
//
//    val taskList = viewModel?.todayTask?.observeAsState()?.value?.asReversed() ?: listOf(
//        Task(title = "Task 1"),
//        Task(title = "Task 2"),
//        Task(title = "Task 3"),
//        Task(title = "Task 4")
//    )
//
//    val colors = lightColors(
//        primary = colorResource(R.color.red_light),
//        primaryVariant = colorResource(R.color.red_variant),
//        secondary = colorResource(R.color.teal_200),
//        surface = colorResource(R.color.white)
//    )
//
//    val rootModifier = Modifier
//        .fillMaxWidth()
//        .fillMaxHeight()
//        .background(colorResource(id = R.color.white))
//        .padding(Dp(16f))
//
//    val cardModifier = Modifier
//        .fillMaxWidth()
//        .padding(Dp(0f), Dp(32f), Dp(0f), Dp(0f))
//        .heightIn(Dp(48f), Dp(500f))
//
//    val listModifier = Modifier
//        .fillMaxWidth()
//        .padding(Dp(16f))
//
//    val taskModifier = Modifier
//        .fillMaxWidth()
//        .padding(Dp(8f))
//
//    val checkboxModifier = Modifier
//        .padding(Dp(0f), Dp(0f), Dp(16f), Dp(0f))
//
//    val addButtonModifier = Modifier
//        .padding(Dp(4f))
//
//    MaterialTheme(
//        colors = colors,
//        typography = Typography(Poppin)
//    ) {
//        Column(rootModifier) {
//            val numDone = taskList.count { task -> task.status == Status.Done.ordinal }
//            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    fontSize = TextUnit.Sp(20),
//                    fontWeight = FontWeight.Bold,
//                    text = stringResource(
//                        R.string.today_tasks,
//                        numDone.toString(),
//                        taskList.size.toString()
//                    ),
//                    modifier = Modifier.constrainAs(createRef()) {
//                        top.linkTo(parent.top)
//                        start.linkTo(parent.start)
//                        bottom.linkTo(parent.bottom)
//                    }
//                )
//                Button(
//                    onClick = { navigateToDailyTask() },
//                    modifier = Modifier.constrainAs(createRef()) {
//                        top.linkTo(parent.top)
//                        end.linkTo(parent.end)
//                        bottom.linkTo(parent.bottom)
//                    }
//                ) {
//                    Text(
//                        text = stringResource(id = R.string.daily_tasks),
//                        color = colorResource(id = R.color.white)
//                    )
//                }
//            }
//            Card(
//                elevation = Dp(32f),
//                backgroundColor = colorResource(
//                    id = R.color.yellow_light
//                ),
//                modifier = cardModifier,
//            ) {
//                Column(horizontalAlignment = Alignment.End) {
//                    IconButton(
//                        onClick = { viewModel?.insertTask(Task(title = "New Task")) },
//                        modifier = addButtonModifier
//                    ) {
//                        Icon(
//                            imageVector = vectorResource(id = R.drawable.ic_baseline_add_24),
//                            tint = colorResource(id = R.color.black)
//                        )
//                    }
//                    LazyColumnForIndexed(
//                        items = taskList,
//                        modifier = listModifier
//                    ) { _, item ->
//                        var text = ""
//                        Row(modifier = taskModifier) {
//                            Checkbox(
//                                checked = item.status == Status.Done.ordinal,
//                                onCheckedChange = {
//                                    val status =
//                                        if (it) Status.Done.ordinal else Status.New.ordinal
//                                    viewModel?.updateTask(item.copy(status = status))
//                                },
//                                modifier = checkboxModifier
//                            )
//                            TextField(
//                                value = text,
//                                onValueChange = { str ->
//                                    text = str
//                                },
//                                textStyle = TextStyle(
//                                    color = colorResource(id = R.color.black),
//                                    fontSize = TextUnit.Companion.Sp(16)
//                                ),
//                                singleLine = true,
//                                onImeActionPerformed = { action, softwareController ->
//                                    if (action == Done) {
//                                        softwareController?.hideSoftwareKeyboard()
//                                        if (text.isNotBlank()) {
//                                            viewModel?.updateTask(item.copy(title = text))
//                                        } else {
//                                            viewModel?.updateTask(item.copy(title = item.title))
//                                        }
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun MainActivityViewPreview() {
//    MainActivityView(viewModel = null) {}
//}
