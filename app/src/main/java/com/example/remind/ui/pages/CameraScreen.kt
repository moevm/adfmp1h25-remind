package com.example.remind.ui.pages

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.tooling.preview.Preview
import androidx.camera.core.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.remind.ui.models.Task
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable
fun CameraScreen(onImageCaptured: (String) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val takePicture = {
        val outputDirectory = context.filesDir
        val photoFile = File(outputDirectory, SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    imageUri = Uri.fromFile(photoFile)
                    onImageCaptured(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraScreen", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider = try {
            cameraProviderFuture.get()
        } catch (e: Exception) {
            Log.e("CameraScreen", "Camera initialization failed: ${e.message}", e)
            null
        }
        imageCapture = ImageCapture.Builder().build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        cameraProvider?.let {
            imageCapture?.let { capture ->
                CameraPreview(cameraProvider = it, cameraSelector = cameraSelector, imageCapture = capture)
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Button(onClick = { takePicture() }) {
                Text("Сделать фото")
            }
            Button(onClick = onClose) {
                Text("Закрыть")
            }
        }

        imageUri?.let {
            ImagePreview(path = it.toString())
        }
    }
}




@Composable
fun CameraPreview(
    cameraProvider: ProcessCameraProvider,
    cameraSelector: CameraSelector,
    imageCapture: ImageCapture
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = remember { Preview.Builder().build() }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }

        onDispose {
            cameraProvider.unbindAll()
        }
    }
}

@Composable
fun ImagePreview(path: String) {
    // Display the captured image preview here.
}