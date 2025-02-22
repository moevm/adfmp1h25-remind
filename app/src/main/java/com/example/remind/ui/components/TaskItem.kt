package com.example.remind.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun TaskItem(
    task: Task,
    onTaskUpdated: (Task) -> Unit,
    onOpenCamera: (Int) -> Unit
) {
    var showPhotoPicker by remember { mutableStateOf(false) }

    val taskBackgroundColor = if (task.isCompleted) Color(0xFFDFFFD6) else Color(0xFFFFE5E5)
    val taskBorderColor = if (task.isCompleted) Color(0xFFD4F0D2) else Color(0xFFF0DEDE)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(taskBackgroundColor)
            .border(1.dp, taskBorderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(2.dp))
                    .background(if (task.isCompleted) Color.Black else Color.Transparent)
                    .clickable {
                        val updatedTask = task.copy(
                            isCompleted = !task.isCompleted,
                            completedAt = if (!task.isCompleted) getCurrentTime() else null
                        )
                        onTaskUpdated(updatedTask)
                    }
            ) {
                if (task.isCompleted) {
                    Icon(
                        painter = painterResource(id = R.drawable.checkmark),
                        contentDescription = "Чекбокс",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp).align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    fontSize = 16.sp
                )
                task.completedAt?.let { completedTime ->
                    Text(
                        text = "Отмечено ${formatTime(completedTime)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = { showPhotoPicker = true },
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Добавить фото",
                    tint = Color.DarkGray
                )
            }
        }

        task.image?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImagePreview(
                    path = it,
                    modifier = Modifier
                        .size(60.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                task.imageDate?.let { date ->
                    Text(
                        text = formatTime(date),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    if (showPhotoPicker) {
        PhotoPickerSheet(
            onDismiss = { showPhotoPicker = false },
            onPickGallery = {
                showPhotoPicker = false
            },
            onTakePhoto = {
                showPhotoPicker = false
                onOpenCamera(task.id)
            }
        )
    }
}

@Composable
fun ImagePreview(path: String, modifier: Modifier = Modifier) {
    val bitmap = remember {
        BitmapFactory.decodeFile(path)?.let {
            Bitmap.createScaledBitmap(it, 60, 60, true)
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Превью фото",
            modifier = modifier
        )
    }
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    return dateFormat.format(Date())
}

fun formatTime(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        timestamp
    }
}
