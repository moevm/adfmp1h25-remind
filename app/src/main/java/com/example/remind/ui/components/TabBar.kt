package com.example.remind.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TabBar(isEmpty: Boolean, onAddClick: () -> Unit,navController: NavController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("main") }) {
                Icon(Icons.Default.Home, contentDescription = "Главная")
            }

            if (!isEmpty) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0E57C))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить дело", tint = Color.White)
                    }
                    Text("Добавить дело", fontSize = 12.sp, color = Color.Gray)
                }
            }

            IconButton(onClick = { }, enabled = false) {
                Icon(Icons.Default.Person, contentDescription = "О нас", tint = Color.Gray)
            }
        }
    }
}