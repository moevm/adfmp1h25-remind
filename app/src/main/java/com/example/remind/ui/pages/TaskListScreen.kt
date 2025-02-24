package com.example.remind.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.remind.R
import com.example.remind.ui.components.TaskCategory
import com.example.remind.ui.models.Task
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onUpdateTasks: (List<Task>) -> Unit,
    onOpenCamera: (Int) -> Unit
) {
    var sortDesk by remember { mutableStateOf(false) }

    val tasksState = remember(tasks) { tasks.toMutableStateList() }
    val hasCompletedTasks = tasksState.any { it.isCompleted }
    val groupedTasks = tasksState.groupBy { it.category }
    fun deleteTask(task: Task) {
        tasksState.remove(task)
        onUpdateTasks(tasksState.toList()) // Обновить список задач
    }
    fun sortTask(desc: Int) {
        tasksState.sortWith(Comparator{a: Task, b: Task->
            if(a.completedAt.isNullOrBlank() || b.completedAt.isNullOrBlank()) {
                return@Comparator 1
            }
            if(a.completedAt!! < b.completedAt!!){
                return@Comparator 1*desc
            }
            if(a.completedAt!! > b.completedAt!!){
                return@Comparator -1*desc
            }
            return@Comparator 0
        })

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Список дел",
                style = MaterialTheme.typography.headlineSmall
            )

            Row {
                IconButton(onClick = { /* Фильтрация */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Фильтр"
                    )
                }
                IconButton(onClick = { sortTask(-1) }) {
                    iconSortTime()
//                    Image(
//                        painter = painterResource(id = R.drawable.sort),
//                        contentDescription = "Сортировка"
//                    )
                }
            }
        }

        TextButton(
            onClick = {
                tasksState.replaceAll {
                    it.copy(
                        isCompleted = false,
                        completedAt = null,
                        imageDate = null,
                        image = null
                    )
                }
                onUpdateTasks(tasksState)
            },
            enabled = hasCompletedTasks
        ) {
            Text("Сбросить все отметки")
        }

        LazyColumn {
            groupedTasks.forEach { (category, tasksInCategory) ->
                item(key = category) {
                    TaskCategory(
                        category = category,
                        tasks = tasksInCategory,
                        onTaskUpdated = { updatedTask ->
                            val index = tasksState.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                tasksState[index] = updatedTask.apply {
                                    completedAt =
                                        if (isCompleted) LocalDateTime.now().toString() else null
                                }
                                onUpdateTasks(tasksState.toList())
                            }
                        },
                        onTaskDeleted = { task -> deleteTask(task) },
                        onOpenCamera = { taskId -> onOpenCamera(taskId) }
                    )
                }
            }
        }
    }

    LaunchedEffect(tasksState) {
        onUpdateTasks(tasksState.toList())
    }
}

@Composable
fun iconSortTime() {
    Box(
        modifier = Modifier.size(width = 30.dp, height = 29.dp)
    ) {
        Image(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = R.drawable.sort2),
            contentDescription = "Сортировка"
        )

        Image(
            modifier = Modifier
                .padding(start = 18.dp, top = 17.dp)
                .size(12.dp),
            painter = painterResource(id = R.drawable.clock),
            contentDescription = "Время"
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTaskList() {
    iconSortTime()
}