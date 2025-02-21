package com.example.remind.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.remind.ui.components.TabBar

@Composable
fun MainScreen(navController: NavController) {
    val tasks by remember { mutableStateOf(listOf<String>()) }

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
                //NewTaskLayout()
                TaskListScreen(tasks)
            }
        }
    }
}