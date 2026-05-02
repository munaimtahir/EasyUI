package com.easyui.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import com.easyui.launcher.navigation.EasyUiNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install back press handler that prevents app exit
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // In Compose NavGraph, back button press is handled by the NavController
                // We allow normal back navigation but the NavGraph will handle routing
                // This callback just ensures predictable behavior
                isEnabled = true
            }
        })
        
        setContent {
            EasyUiApp((application as EasyUiApplication))
        }
    }
}

@Composable
private fun EasyUiApp(application: EasyUiApplication) {
    EasyUiNavGraph(container = application.container)
}
