package com.easyui.senior.apps

import android.graphics.drawable.Drawable

interface AppIconLoader {
    fun loadIcon(app: LaunchableApp): Drawable
}

