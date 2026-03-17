package com.easyui.core.domain.model

data class BackupData(
    val version: Int,
    val settings: LauncherSettings,
    val tiles: List<HomeTile>,
    val hiddenPackages: Set<String>,
)

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}
