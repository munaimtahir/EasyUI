package com.easyui.core.domain.rules

object LayoutLockRules {
    fun blocksHomeLongPress(layoutLocked: Boolean): Boolean = layoutLocked

    fun allowsIntentionalCaregiverEdit(layoutLocked: Boolean): Boolean = true
}
