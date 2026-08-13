package com.easyui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * A dedicated activity to ensure EasyUI appears in the app drawer.
 */
class SetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Simply launch the main activity and finish
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
