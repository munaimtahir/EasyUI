package com.easyui.core.domain.repository

import com.easyui.core.domain.model.BackupData
import com.easyui.core.domain.model.ValidationResult

interface BackupRepository {
    /** Export current launcher state as a JSON string suitable for writing to a file. */
    suspend fun exportJson(): String

    /** Validate a JSON string as an EasyUI backup. */
    fun validate(json: String): ValidationResult

    /** Apply a previously validated backup, replacing all current state. */
    suspend fun applyBackup(data: BackupData)
}
