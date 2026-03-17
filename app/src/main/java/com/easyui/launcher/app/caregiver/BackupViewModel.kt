package com.easyui.launcher.app.caregiver

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.data.backup.BackupSerializer
import com.easyui.core.domain.model.BackupData
import com.easyui.core.domain.model.ValidationResult
import com.easyui.launcher.di.AppContainer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BackupUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val lastResult: String? = null,
    val pendingImportJson: String? = null,
)

class BackupViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val messages = MutableSharedFlow<String>()
    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()

    /**
     * Export backup to a JSON string and return it so the UI can write it to a file.
     * Returns null on failure.
     */
    fun exportBackup(onReady: (String, String) -> Unit) {
        _state.update { it.copy(isExporting = true, lastResult = null) }
        viewModelScope.launch(container.ioDispatcher) {
            try {
                val json = container.backupRepository.exportJson()
                val filename = "easyui-backup-${java.time.LocalDate.now()}.json"
                onReady(json, filename)
                _state.update { it.copy(isExporting = false, lastResult = "Backup exported successfully.") }
            } catch (e: Exception) {
                _state.update { it.copy(isExporting = false, lastResult = "Export failed: ${e.message}") }
            }
        }
    }

    /**
     * Read a JSON string from a content URI selected by the user, then validate and queue it
     * for confirmation before applying.
     */
    fun loadImportFromUri(context: Context, uri: Uri) {
        _state.update { it.copy(isImporting = true, lastResult = null) }
        viewModelScope.launch(container.ioDispatcher) {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
                    ?: throw IllegalStateException("Could not read the selected file.")
                when (val result = container.backupRepository.validate(json)) {
                    is ValidationResult.Valid -> {
                        _state.update { it.copy(isImporting = false, pendingImportJson = json) }
                    }
                    is ValidationResult.Invalid -> {
                        _state.update {
                            it.copy(isImporting = false, lastResult = "Invalid backup file: ${result.reason}")
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isImporting = false, lastResult = "Could not read file: ${e.message}") }
            }
        }
    }

    fun cancelImport() {
        _state.update { it.copy(pendingImportJson = null) }
    }

    fun confirmImport() {
        val json = _state.value.pendingImportJson ?: return
        _state.update { it.copy(isImporting = true, pendingImportJson = null) }
        viewModelScope.launch(container.ioDispatcher) {
            try {
                val data = BackupSerializer.deserialize(json)
                container.backupRepository.applyBackup(data)
                _state.update { it.copy(isImporting = false, lastResult = "Backup restored successfully.") }
                messages.emit("EasyUI has been restored from backup.")
            } catch (e: Exception) {
                _state.update { it.copy(isImporting = false, lastResult = "Restore failed: ${e.message}") }
            }
        }
    }

    fun clearResult() {
        _state.update { it.copy(lastResult = null) }
    }
}
