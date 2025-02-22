package com.example.remind.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.asImageBitmap
import java.util.*

@Composable
fun TaskItem(
    task: Task,
    onTaskUpdated: (Task) -> Unit,
    onOpenCamera: (Int) -> Unit
) {
    var showPhotoPicker by remember { mutableStateOf(false) }

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

        IconButton(
            onClick = { showPhotoPicker = true },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Добавить фото",
                tint = Color.DarkGray
            )
        }

        task.image?.let {
            ImagePreview(path = it)
        }
    }

    if (showPhotoPicker) {
        PhotoPickerSheet(
            onDismiss = { showPhotoPicker = false },
            onPickGallery = {
                showPhotoPicker = false
                // TODO: Добавить обработку выбора из галереи
            },
            onTakePhoto = {
                showPhotoPicker = false
                onOpenCamera(task.id)
            }
        )
    }
}

fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    dateFormat.timeZone = calendar.timeZone
    return dateFormat.format(calendar.time)
}


@Composable
fun ImagePreview(path: String) {
    val bitmap = remember {
        BitmapFactory.decodeFile(path)?.let {
            Bitmap.createScaledBitmap(it, 100, 100, true)
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Превью фото",
            modifier = Modifier.size(50.dp)
        )
    }
}
