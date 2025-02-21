package com.example.remind.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remind.R

@Composable
fun TabBar(isEmpty: Boolean, navController: NavController) {
    BottomAppBar(
        containerColor = Color(0xFFF0F0F0),
        tonalElevation = 8.dp,
        modifier = Modifier.height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { navController.navigate("main") }
                    .padding(bottom = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Главная"
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("Главная", fontSize = 12.sp, color = Color.Gray)
            }

            if (!isEmpty) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate("addTask") },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0E57C)),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Добавить дело",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Добавить дело",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { navController.navigate("about") }
                    .padding(bottom = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_about),
                    contentDescription = "О нас"
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("О нас", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

