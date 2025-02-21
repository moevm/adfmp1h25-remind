package com.example.remind.ui.models

data class Task(
    val id: Int,
    val title: String,
    var isCompleted: Boolean,
    var completedAt: String?,
    val category: String,
    val image: String?,
    val imageDate: String?
)
