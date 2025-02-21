package com.example.remind.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remind.ui.components.TabBar

@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            TabBar(isEmpty = false, navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "О нас",
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Королева Полина", fontSize = 24.sp)
                Text(text = "Куклина Юлия", fontSize = 24.sp)
                Text(text = "Сырцева Дарья", fontSize = 24.sp)
            }
        }
    }
}
