package com.easyui.launcher

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.easyui.launcher.navigation.EasyUiNavGraph

class MainActivity : ComponentActivity() {
    private var currentIntentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        currentIntentState = intent

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
            EasyUiApp((application as EasyUiApplication), currentIntentState)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntentState = intent
    }
}

@Composable
private fun EasyUiApp(application: EasyUiApplication, intent: Intent?) {
    EasyUiNavGraph(container = application.container, initialIntent = intent)
}
