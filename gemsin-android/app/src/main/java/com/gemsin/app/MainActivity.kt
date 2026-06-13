package com.gemsin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.gemsin.app.navigation.GemsinNavGraph
import com.gemsin.app.ui.theme.GemsinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GemsinTheme {
                val navController = rememberNavController()
                GemsinNavGraph(navController = navController)
            }
        }
    }
}
