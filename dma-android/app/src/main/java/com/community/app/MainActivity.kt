package com.community.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.community.app.navigation.CommunityNavGraph
import com.community.app.ui.theme.CommunityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommunityTheme {
                val navController = rememberNavController()
                CommunityNavGraph(navController = navController)
            }
        }
    }
}
