package com.example.remind.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.remind.ui.models.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileManager {
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create()
    fun saveTasksToFile(context: Context, tasks: List<Task>) {
        val json = gson.toJson(tasks)

        try {
            val fos = context.openFileOutput("tasks.json", Context.MODE_PRIVATE)
            val writer = OutputStreamWriter(fos)
            writer.use { it.write(json) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadTasksFromFile(context: Context): List<Task> {
        return try {
            context.openFileInput("tasks.json").use { stream ->
                val reader = stream.bufferedReader()
                val jsonString = reader.lineSequence().joinToString("\n")
                if (jsonString.isBlank()) emptyList()
                else gson.fromJson(jsonString, object : TypeToken<List<Task>>() {}.type)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    fun getImageDateFromUri(context: Context, uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATE_TAKEN)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
            if (cursor.moveToFirst() && columnIndex != -1) {
                val dateTakenMillis = cursor.getLong(columnIndex)
                return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date(dateTakenMillis))
            }
        }
        return getCurrentTime()
    }
    fun saveCategoriesToFile(context: Context, categories: List<String>) {
        val json = gson.toJson(categories)

        try {
            val fos = context.openFileOutput("categories.json", Context.MODE_PRIVATE)
            val writer = OutputStreamWriter(fos)
            writer.use { it.write(json) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadCategoriesFromFile(context: Context): List<String> {
        return try {
            context.openFileInput("categories.json").use { stream ->
                val reader = stream.bufferedReader()
                val jsonString = reader.lineSequence().joinToString("\n")
                if (jsonString.isBlank()) listOf("Быт", "Работа", "Учеба")
                else gson.fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
            }
        } catch (e: Exception) {
            listOf("Быт", "Работа", "Учеба")
        }
    }

}
