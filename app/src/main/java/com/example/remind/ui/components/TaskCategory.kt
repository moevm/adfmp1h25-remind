package com.example.remind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.remind.ui.models.Task

@Composable
fun TaskCategory(category: String, tasks: List<Task>, onTaskUpdated: (Task) -> Unit) {
    var isExpanded by remember { mutableStateOf(true) }
    val tasksState = remember(tasks) { tasks.toMutableStateList() }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .background(Color(0xFFEFEFEF)), // Цвет заголовка
            tonalElevation = 4.dp
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Text(category, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle"
                )
            }
        }

        if (isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                tasksState.forEach { task ->
                    TaskItem(
                        task = task,
                        onTaskUpdated = { updatedTask ->
                            val index = tasksState.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                tasksState[index] = updatedTask
                                onTaskUpdated(updatedTask)
                            }
                        }
                    )
                }
            }
        }
    }
}




