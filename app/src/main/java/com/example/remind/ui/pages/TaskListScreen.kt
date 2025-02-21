package com.example.remind.ui.pages
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp

@Composable
fun TaskListScreen(tasks: List<String>) {
    val isEmpty = tasks.isEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
//        if (isEmpty) {
//            EmptyScreen(onAddClick = {
//
//            })
//        } else {
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                items(tasks) { task ->
//                    Text(
//                        text = task,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
//            }
//        }
    }
}