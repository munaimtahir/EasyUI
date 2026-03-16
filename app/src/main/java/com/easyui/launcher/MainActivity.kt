package com.easyui.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.launcher.navigation.EasyUiNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyUiApp((application as EasyUiApplication))
        }
    }
}

@Composable
private fun EasyUiApp(application: EasyUiApplication) {
    EasyUiTheme {
        EasyUiNavGraph(container = application.container)
    }
}
