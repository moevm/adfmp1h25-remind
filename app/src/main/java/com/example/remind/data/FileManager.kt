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


//    fun saveImageToFile(context: Context, bitmap: Bitmap, fileName: String) {
//        try {
//            val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
//            fos.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun loadImageFromFile(context: Context, fileName: String): Bitmap? {
//        try {
//            val fis = context.openFileInput(fileName)
//            fis.use { return BitmapFactory.decodeStream(it) }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return null
//    }
}
