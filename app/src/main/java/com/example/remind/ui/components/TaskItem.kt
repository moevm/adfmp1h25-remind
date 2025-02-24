package com.example.remind.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState

@Composable
fun TaskItem(
    task: Task,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onOpenCamera: (Int) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    var showPhotoPicker by remember { mutableStateOf(false) }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onTaskDeleted(task)
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gallery),
                    contentDescription = "Удалить",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (task.isCompleted) Color(0xFFDFFFD6) else Color(0xFFFFE5E5))
                .border(1.dp, if (task.isCompleted) Color(0xFFD4F0D2) else Color(0xFFF0DEDE), RoundedCornerShape(4.dp))
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
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImagePreview(path = it)
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
    }

    if (showPhotoPicker) {
        PhotoPickerSheet(
            onDismiss = { showPhotoPicker = false },
            onPickGallery = { showPhotoPicker = false },
            onTakePhoto = {
                showPhotoPicker = false
                onOpenCamera(task.id)
            }
        )
    }
}

@Composable
private fun ImagePreview(path: String) {
    val bitmap by remember(path) {
        mutableStateOf(BitmapFactory.decodeFile(path))
    }

    bitmap?.let {
        Image(
            bitmap = Bitmap.createScaledBitmap(it, 60, 60, true).asImageBitmap(),
            contentDescription = "Превью фото",
            modifier = Modifier.size(60.dp)
        )
    }
}

private fun getCurrentTime(): String {
    return SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date())
}

private fun formatTime(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
        outputFormat.format(inputFormat.parse(timestamp) ?: Date())
    } catch (e: Exception) {
        timestamp.replace("T", " ")
    }
}
