package com.easyui.launcher

import android.app.Application
import com.easyui.launcher.di.AppContainer

class EasyUiApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
