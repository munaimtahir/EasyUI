package com.easyui.core.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u000e\u0010\u000e\u001a\u00020\u000fH\u0096@\u00a2\u0006\u0002\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000fH\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/easyui/core/data/repository/LocalBackupRepository;", "Lcom/easyui/core/domain/repository/BackupRepository;", "homeLayoutRepository", "Lcom/easyui/core/domain/repository/HomeLayoutRepository;", "launcherSettingsRepository", "Lcom/easyui/core/domain/repository/LauncherSettingsRepository;", "hiddenAppRepository", "Lcom/easyui/core/domain/repository/HiddenAppRepository;", "(Lcom/easyui/core/domain/repository/HomeLayoutRepository;Lcom/easyui/core/domain/repository/LauncherSettingsRepository;Lcom/easyui/core/domain/repository/HiddenAppRepository;)V", "applyBackup", "", "data", "Lcom/easyui/core/domain/model/BackupData;", "(Lcom/easyui/core/domain/model/BackupData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exportJson", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "validate", "Lcom/easyui/core/domain/model/ValidationResult;", "json", "data_debug"})
public final class LocalBackupRepository implements com.easyui.core.domain.repository.BackupRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.easyui.core.domain.repository.HomeLayoutRepository homeLayoutRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.easyui.core.domain.repository.LauncherSettingsRepository launcherSettingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.easyui.core.domain.repository.HiddenAppRepository hiddenAppRepository = null;
    
    public LocalBackupRepository(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.repository.HomeLayoutRepository homeLayoutRepository, @org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.repository.LauncherSettingsRepository launcherSettingsRepository, @org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.repository.HiddenAppRepository hiddenAppRepository) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object exportJson(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.easyui.core.domain.model.ValidationResult validate(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object applyBackup(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.BackupData data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}