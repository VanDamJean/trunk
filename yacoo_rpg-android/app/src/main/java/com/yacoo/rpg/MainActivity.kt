package com.yacoo.rpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.yacoo.rpg.navigation.YacooNavGraph
import com.yacoo.rpg.ui.theme.YacooTheme
import com.yacoo.rpg.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YacooTheme {
                YacooNavGraph(viewModel = viewModel)
            }
        }
    }
}
