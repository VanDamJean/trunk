package com.silentmoon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.silentmoon.app.navigation.SilentMoonNavGraph
import com.silentmoon.app.ui.theme.SilentMoonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilentMoonTheme {
                val navController = rememberNavController()
                SilentMoonNavGraph(navController = navController)
            }
        }
    }
}
