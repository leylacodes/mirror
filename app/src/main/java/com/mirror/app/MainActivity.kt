package com.mirror.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mirror.app.ui.navigation.NavGraph
import com.mirror.app.ui.theme.MirrorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as MirrorApp).container
        setContent {
            MirrorTheme {
                NavGraph(container)
            }
        }
    }
}
