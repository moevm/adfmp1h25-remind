package com.example.remind.ui.pages

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.media.ExifInterface
import java.io.IOException

fun getExifRotation(imagePath: String): Int {
    return try {
        val exif = ExifInterface(imagePath)
        when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: IOException) {
        0
    }
}

@Composable
fun CameraScreen(
    onImageCaptured: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { androidx.camera.view.PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Для работы камеры нужно разрешение!", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            permissionGranted = true
        }
    }

    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) return@LaunchedEffect

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Ошибка инициализации камеры: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    fun rotateBitmapIfNeeded(imagePath: String): Bitmap {
        val rotationAngle = getExifRotation(imagePath)
        val bitmap = BitmapFactory.decodeFile(imagePath)
        return if (rotationAngle != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotationAngle.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    fun takePhoto() {
        if (imageCapture == null) {
            Log.e("CameraScreen", "Ошибка: imageCapture = null. Камера не инициализирована!")
            return
        }

        val file = File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val correctedBitmap = rotateBitmapIfNeeded(file.absolutePath)

                    val correctedFile = File(
                        context.externalCacheDir,
                        "corrected_${System.currentTimeMillis()}.jpg"
                    )
                    correctedFile.outputStream().use { out ->
                        correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }

                    capturedImageUri = Uri.fromFile(correctedFile)
                    onImageCaptured(correctedFile.absolutePath)

                    Log.d(
                        "CameraScreen",
                        "Фото сохранено (исправленное): ${correctedFile.absolutePath}"
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraScreen", "Ошибка при съемке фото: ${exception.message}", exception)
                }
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (capturedImageUri == null) {
                Button(
                    onClick = { takePhoto() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Сделать фото")
                }
            }

            Button(
                onClick = onClose,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Закрыть")
            }

            capturedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Снятое фото",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}
