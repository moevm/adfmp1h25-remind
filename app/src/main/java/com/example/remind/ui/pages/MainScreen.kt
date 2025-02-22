package com.example.remind.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.remind.data.FileManager
import com.example.remind.ui.components.TabBar
import com.example.remind.ui.models.Task

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCamera by remember { mutableStateOf(false) }
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        val fileManager = FileManager()
        tasks = fileManager.loadTasksFromFile(context)
        isLoading = false
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
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                tasks.isEmpty() -> EmptyScreen(navController = navController)
                else -> TaskListScreen(
                    tasks = tasks,
                    onUpdateTasks = { updatedTasks ->
                        tasks = updatedTasks
                        FileManager().saveTasksToFile(context, updatedTasks)
                    },
                    onOpenCamera = { taskId ->
                        showCamera = true
                        selectedTaskId = taskId
                    }
                )
            }
        }
    }

    if (showCamera) {
        CameraScreen(
            onImageCaptured = { path ->
                tasks = tasks.map { task ->
                    if (task.id == selectedTaskId) {
                        task.copy(image = path)
                    } else task
                }
                FileManager().saveTasksToFile(context, tasks)
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    }
}

