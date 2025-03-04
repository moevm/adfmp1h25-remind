package com.example.remind.ui.components

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.remind.data.FileManager
import com.example.remind.ui.pages.getCurrentTime

@Composable
fun TaskItem(
    task: Task,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onOpenCamera: (Int) -> Unit
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState()
    var showPhotoPicker by remember { mutableStateOf(false) }
    var showFullScreenImage by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { selectedUri ->
                val fileManager = FileManager()
                val savedPath = fileManager.saveImageFromUri(context, selectedUri)
                val imageDate = fileManager.getImageDateFromUri(context, selectedUri)

                val updatedTask = task.copy(
                    image = savedPath,
                    imageDate = imageDate,
                    completedAt = imageDate,
                    isCompleted = true
                )
                onTaskUpdated(updatedTask)

                showPhotoPicker = false
            }
        }
    )

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
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Удалить",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (task.isCompleted) Color(0xFFDFFFD6) else Color(0xFFFFE5E5))
                .border(
                    1.dp,
                    if (task.isCompleted) Color(0xFFD4F0D2) else Color(0xFFF0DEDE),
                    RoundedCornerShape(4.dp)
                )
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
                                completedAt = if (!task.isCompleted) getCurrentTime() else null,
                                image = if (task.isCompleted) null else task.image,
                                imageDate = if (task.isCompleted) null else task.imageDate
                            )
                            onTaskUpdated(updatedTask)
                        }
                ) {
                    if (task.isCompleted) {
                        Icon(
                            painter = painterResource(id = R.drawable.checkmark),
                            contentDescription = "Чекбокс",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
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

                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (task.isCompleted) Color(0xFFB2E6AC) else Color(0xFFE6CCCC))
                        .clickable { showPhotoPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.photo),
                        contentDescription = "Добавить фото",
                        tint = Color.White
                    )
                }
            }

            task.image?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(60.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ImagePreview(
                        path = it,
                        onClick = { showFullScreenImage = true }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    task.imageDate?.let { date ->
                        Text(
                            text = formatTime(date),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showPhotoPicker) {
        PhotoPickerSheet(
            onDismiss = { showPhotoPicker = false },
            onPickGallery = {
                galleryLauncher.launch("image/*")
            },
            onTakePhoto = {
                showPhotoPicker = false
                onOpenCamera(task.id)
            }
        )
    }

    if (showFullScreenImage && task.image != null) {
        Dialog(
            onDismissRequest = { showFullScreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { showFullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = "Закрыть",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                val fullScreenBitmap = BitmapFactory.decodeFile(task.image)
                val rotatedBitmap = rotateBitmap(fullScreenBitmap, 0f)
                Image(
                    bitmap = rotatedBitmap.asImageBitmap(),
                    contentDescription = "Полноэкранное фото",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }


}

@Composable
private fun ImagePreview(path: String, onClick: () -> Unit) {
    val bitmap by remember(path) {
        mutableStateOf(BitmapFactory.decodeFile(path))
    }

    bitmap?.let {
        val rotatedBitmap = rotateBitmap(it, 0f)
        Image(
            bitmap = Bitmap.createScaledBitmap(rotatedBitmap, 60, 60, true).asImageBitmap(),
            contentDescription = "Превью фото",
            modifier = Modifier
                .size(60.dp)
                .clickable(onClick = onClick)
        )
    }
}

private fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}


private fun formatTime(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd.MM HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
        }
        outputFormat.format(inputFormat.parse(timestamp) ?: Date())
    } catch (e: Exception) {
        timestamp.replace("T", " ")
    }
}

