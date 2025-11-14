package com.example.bodega

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bodega.ui.screens.MainScreen
import com.example.bodega.ui.theme.BodegaTheme
import com.example.bodega.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = application as BodegaApplication
            val viewModelFactory = ViewModelFactory(application.repository)

            BodegaTheme {
                MainScreen(viewModelFactory)
            }
        }
    }
}
