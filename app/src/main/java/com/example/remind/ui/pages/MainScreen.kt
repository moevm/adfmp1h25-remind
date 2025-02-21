package com.example.remind.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.remind.ui.components.TabBar
import com.example.remind.ui.models.Task
@Composable
fun MainScreen(navController: NavController) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Выключить утюг", false, null, "Бытовые задачи"),
                Task(2, "Купить молоко", true, "12.02 13:03", "Бытовые задачи"),
                Task(3, "Позвонить в банк", false, null, "Рабочие задачи"),
                Task(4, "Сделать отчёт", true, "11.02 16:30", "Рабочие задачи")
            )
        )
    }

    Scaffold(
        bottomBar = {
            TabBar(
                isEmpty = tasks.isEmpty(),
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (tasks.isEmpty()) {
                EmptyScreen(navController = navController)
            } else {
                TaskListScreen(
                    tasks = tasks,
                    onUpdateTasks = { updatedTasks ->
                        tasks = updatedTasks
                    }
                )
            }
        }
    }
}
