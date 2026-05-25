package com.easyui.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * A dummy activity used to force the system to show the "Select Home App" chooser.
 * This activity is disabled by default in the manifest and only enabled briefly
 * by [com.easyui.core.platform.launcher.AndroidDefaultLauncherManager].
 */
class FakeLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Finish immediately if it's ever actually started
        finish()
    }
}
