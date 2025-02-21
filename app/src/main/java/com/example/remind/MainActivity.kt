package com.example.remind
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remind.ui.pages.AboutScreen
import com.example.remind.ui.pages.MainScreen
import com.example.remind.ui.pages.NewTaskLayout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(navController)
                }
                composable("addTask") {
                    NewTaskLayout(navController)
                }
                composable("about") {
                    AboutScreen(navController)
                }
            }
        }
    }
}
