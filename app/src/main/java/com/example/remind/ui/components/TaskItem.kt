package com.example.remind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind.R
import com.example.remind.ui.models.Task
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun TaskItem(task: Task, onTaskUpdated: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (task.isCompleted) Color(0xFFDFFFD6) else Color(0xFFFFE5E5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                val updatedTask = task.copy(
                    isCompleted = !task.isCompleted,
                    completedAt = if (!task.isCompleted) getCurrentTime() else null
                )
                onTaskUpdated(updatedTask)
            },
            modifier = Modifier
                .size(44.dp)
                .padding(end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = if (task.isCompleted) Color.Black else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Black
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        painter = painterResource(id = R.drawable.checkmark),
                        contentDescription = "Чекбокс",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                fontSize = 16.sp
            )
            if (task.isCompleted && task.completedAt != null) {
                Text("Отмечено ${task.completedAt}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
fun getCurrentTime(): String {
    val calendar = Calendar.getInstance() // Это получает календарь с текущим временем и временной зоной устройства
    val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    dateFormat.timeZone = calendar.timeZone // Устанавливаем временную зону форматтера такую же, как у календаря
    return dateFormat.format(calendar.time)
}