package com.easyui.core.data.datastore;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001qB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0002J8\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u00112\b\u0010\u0016\u001a\u0004\u0018\u00010\u00112\b\u0010\u0017\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0018\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00110\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0002J\u000e\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00110\u001cH\u0002J\u0016\u0010\u001d\u001a\u00020\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002J\u0016\u0010\u001f\u001a\u00020\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00110\u000eH\u0002J\u000e\u0010 \u001a\u00020\u0013H\u0096@\u00a2\u0006\u0002\u0010!J\u0016\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u0013H\u0096@\u00a2\u0006\u0002\u0010%J\u0016\u0010&\u001a\u00020#2\u0006\u0010\'\u001a\u00020(H\u0096@\u00a2\u0006\u0002\u0010)J\u0016\u0010*\u001a\u00020#2\u0006\u0010+\u001a\u00020,H\u0096@\u00a2\u0006\u0002\u0010-J\u0016\u0010.\u001a\u00020#2\u0006\u0010/\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u00101\u001a\u00020#2\u0006\u00102\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u00103J\u0016\u00104\u001a\u00020#2\u0006\u00105\u001a\u000206H\u0096@\u00a2\u0006\u0002\u00107J\u0016\u00108\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010:\u001a\u00020#2\u0006\u00105\u001a\u000206H\u0096@\u00a2\u0006\u0002\u00107J\u0016\u0010;\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010<\u001a\u00020#2\u0006\u0010=\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010>\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010?\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010@\u001a\u00020#2\u0006\u0010A\u001a\u000206H\u0096@\u00a2\u0006\u0002\u00107J\u0016\u0010B\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010C\u001a\u00020#2\u0006\u0010+\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u00103J\u001c\u0010D\u001a\u00020#2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0096@\u00a2\u0006\u0002\u0010EJ\u0016\u0010F\u001a\u00020#2\u0006\u0010G\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u00103J\u0016\u0010H\u001a\u00020#2\u0006\u0010I\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010J\u001a\u00020#2\u0006\u0010K\u001a\u000206H\u0096@\u00a2\u0006\u0002\u00107J\u0016\u0010L\u001a\u00020#2\u0006\u0010M\u001a\u00020NH\u0096@\u00a2\u0006\u0002\u0010OJ\u0016\u0010P\u001a\u00020#2\u0006\u0010Q\u001a\u000206H\u0096@\u00a2\u0006\u0002\u00107J\u0016\u0010R\u001a\u00020#2\u0006\u00102\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u00103J\u0016\u0010S\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010T\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010U\u001a\u00020#2\u0006\u0010V\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010W\u001a\u00020#2\u0006\u0010+\u001a\u00020XH\u0096@\u00a2\u0006\u0002\u0010YJ\u0016\u0010Z\u001a\u00020#2\u0006\u0010[\u001a\u000206H\u0096@\u00a2\u0006\u0002\u00107J\u0016\u0010\\\u001a\u00020#2\u0006\u0010]\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010^\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010_\u001a\u00020#2\u0006\u0010`\u001a\u00020aH\u0096@\u00a2\u0006\u0002\u0010bJ\u001c\u0010c\u001a\u00020#2\f\u0010d\u001a\b\u0012\u0004\u0012\u00020\u00110\u001cH\u0096@\u00a2\u0006\u0002\u0010eJ\u0016\u0010f\u001a\u00020#2\u0006\u0010g\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u00103J\u0016\u0010h\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u001e\u0010i\u001a\u00020#2\u0006\u0010j\u001a\u00020k2\u0006\u0010+\u001a\u00020,H\u0096@\u00a2\u0006\u0002\u0010lJ\u001c\u0010m\u001a\u00020#2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00110\u000eH\u0096@\u00a2\u0006\u0002\u0010EJ\u0016\u0010n\u001a\u00020#2\u0006\u00109\u001a\u00020\u0019H\u0096@\u00a2\u0006\u0002\u00100J\u0016\u0010o\u001a\u00020#2\u0006\u0010j\u001a\u00020kH\u0096@\u00a2\u0006\u0002\u0010pR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006r"}, d2 = {"Lcom/easyui/core/data/datastore/DataStoreLauncherSettingsRepository;", "Lcom/easyui/core/domain/repository/LauncherSettingsRepository;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "dataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "settings", "Lkotlinx/coroutines/flow/Flow;", "Lcom/easyui/core/domain/model/LauncherSettings;", "getSettings", "()Lkotlinx/coroutines/flow/Flow;", "decodeEmergencyNumbers", "", "Lcom/easyui/core/domain/model/EmergencyNumber;", "raw", "", "decodeSkinConfig", "Lcom/easyui/core/domain/model/SkinConfig;", "layoutModeName", "visualThemeName", "accessibilityModeName", "readabilityPresetName", "verySimpleModeEnabled", "", "decodeSosNumbers", "defaultOptionalPermissions", "", "encodeEmergencyNumbers", "numbers", "encodeSosNumbers", "getSkinConfig", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setSkinConfig", "", "config", "(Lcom/easyui/core/domain/model/SkinConfig;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "storePinCredential", "credential", "Lcom/easyui/core/domain/model/PinCredential;", "(Lcom/easyui/core/domain/model/PinCredential;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAccessibilityMode", "mode", "Lcom/easyui/core/domain/model/AccessibilityMode;", "(Lcom/easyui/core/domain/model/AccessibilityMode;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAllAppsVisible", "visible", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAppVisibilityPreset", "presetName", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateBatteryCriticalThreshold", "threshold", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateBatteryLowCheckEnabled", "enabled", "updateBatteryLowThreshold", "updateCaregiverProtectionEnabled", "updateClockPreference", "use24HourClock", "updateDefaultLauncherCheckEnabled", "updateEasyUiLockEnabled", "updateEasyUiLockTimeoutSeconds", "seconds", "updateEmergencyContactCheckEnabled", "updateEmergencyMode", "updateEmergencyNumbers", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateEmergencyPhoneNumber", "phoneNumber", "updateGuidedSetupCompleted", "completed", "updateGuidedSetupStep", "step", "updateHealthInfo", "healthInfo", "Lcom/easyui/core/domain/model/HealthInfo;", "(Lcom/easyui/core/domain/model/HealthInfo;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateHomePageCount", "pageCount", "updateHomeReadabilityPreset", "updateInternetCheckEnabled", "updateLayoutLockCheckEnabled", "updateLayoutLocked", "locked", "updateLayoutMode", "Lcom/easyui/core/domain/model/LayoutMode;", "(Lcom/easyui/core/domain/model/LayoutMode;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateNoInternetDelayMinutes", "minutes", "updateOnboardingComplete", "complete", "updatePermissionCheckEnabled", "updateReadabilityPreset", "preset", "Lcom/easyui/core/domain/model/HomeReadabilityPreset;", "(Lcom/easyui/core/domain/model/HomeReadabilityPreset;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSetupOptionalPermissions", "permissionNames", "(Ljava/util/Set;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSetupProtectionLevel", "levelName", "updateShowBatteryInfo", "updateSkinConfig", "theme", "Lcom/easyui/core/domain/model/VisualTheme;", "(Lcom/easyui/core/domain/model/VisualTheme;Lcom/easyui/core/domain/model/AccessibilityMode;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSosNumbers", "updateVerySimpleModeEnabled", "updateVisualTheme", "(Lcom/easyui/core/domain/model/VisualTheme;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Keys", "data_debug"})
public final class DataStoreLauncherSettingsRepository implements com.easyui.core.domain.repository.LauncherSettingsRepository {
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> dataStore = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.easyui.core.domain.model.LauncherSettings> settings = null;
    
    public DataStoreLauncherSettingsRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.easyui.core.domain.model.LauncherSettings> getSettings() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateOnboardingComplete(boolean complete, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateEmergencyPhoneNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateEmergencyNumbers(@org.jetbrains.annotations.NotNull()
    java.util.List<com.easyui.core.domain.model.EmergencyNumber> numbers, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateSosNumbers(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> numbers, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateClockPreference(boolean use24HourClock, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateCaregiverProtectionEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateLayoutLocked(boolean locked, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateEasyUiLockEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateEasyUiLockTimeoutSeconds(int seconds, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateAppVisibilityPreset(@org.jetbrains.annotations.NotNull()
    java.lang.String presetName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateHomeReadabilityPreset(@org.jetbrains.annotations.NotNull()
    java.lang.String presetName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateVerySimpleModeEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateShowBatteryInfo(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateHomePageCount(int pageCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateHealthInfo(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.HealthInfo healthInfo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object setSkinConfig(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.SkinConfig config, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateVisualTheme(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.VisualTheme theme, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateAccessibilityMode(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.AccessibilityMode mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateSkinConfig(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.VisualTheme theme, @org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.AccessibilityMode mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateLayoutMode(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.LayoutMode mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateReadabilityPreset(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.HomeReadabilityPreset preset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getSkinConfig(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.easyui.core.domain.model.SkinConfig> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object storePinCredential(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.PinCredential credential, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateSetupProtectionLevel(@org.jetbrains.annotations.NotNull()
    java.lang.String levelName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateSetupOptionalPermissions(@org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> permissionNames, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateGuidedSetupStep(int step, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateGuidedSetupCompleted(boolean completed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateEmergencyMode(@org.jetbrains.annotations.NotNull()
    java.lang.String mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateAllAppsVisible(boolean visible, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateBatteryLowCheckEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateBatteryLowThreshold(int threshold, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateBatteryCriticalThreshold(int threshold, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateInternetCheckEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateNoInternetDelayMinutes(int minutes, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateDefaultLauncherCheckEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateEmergencyContactCheckEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateLayoutLockCheckEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updatePermissionCheckEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.util.Set<java.lang.String> defaultOptionalPermissions() {
        return null;
    }
    
    private final java.lang.String encodeEmergencyNumbers(java.util.List<com.easyui.core.domain.model.EmergencyNumber> numbers) {
        return null;
    }
    
    private final java.util.List<com.easyui.core.domain.model.EmergencyNumber> decodeEmergencyNumbers(java.lang.String raw) {
        return null;
    }
    
    private final java.lang.String encodeSosNumbers(java.util.List<java.lang.String> numbers) {
        return null;
    }
    
    private final java.util.List<java.lang.String> decodeSosNumbers(java.lang.String raw) {
        return null;
    }
    
    private final com.easyui.core.domain.model.SkinConfig decodeSkinConfig(java.lang.String layoutModeName, java.lang.String visualThemeName, java.lang.String accessibilityModeName, java.lang.String readabilityPresetName, boolean verySimpleModeEnabled) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b>\n\u0002\u0010\"\n\u0002\b\u0012\b\u00c2\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0007R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0007R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0007R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0007R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0007R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0007R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0007R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0007R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0007R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0007R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0007R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0007R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0007R\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020\f0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0007R\u0017\u0010&\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u0007R\u0017\u0010(\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0007R\u0017\u0010*\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0007R\u0017\u0010,\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0007R\u0017\u0010.\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\u0007R\u0017\u00100\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010\u0007R\u0017\u00102\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010\u0007R\u0017\u00104\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\u0007R\u0017\u00106\u001a\b\u0012\u0004\u0012\u00020\f0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010\u0007R\u0017\u00108\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010\u0007R\u0017\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010\u0007R\u0017\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010\u0007R\u0017\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010\u0007R\u0017\u0010@\u001a\b\u0012\u0004\u0012\u00020\f0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u0010\u0007R\u0017\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010\u0007R\u0017\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010\u0007R\u0017\u0010F\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010\u0007R\u0017\u0010H\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010\u0007R\u001d\u0010J\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0K0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010\u0007R\u0017\u0010M\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u0010\u0007R\u0017\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u0010\u0007R\u0017\u0010Q\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bR\u0010\u0007R\u0017\u0010S\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bT\u0010\u0007R\u0017\u0010U\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bV\u0010\u0007R\u0017\u0010W\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bX\u0010\u0007R\u0017\u0010Y\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bZ\u0010\u0007R\u0017\u0010[\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\\\u0010\u0007\u00a8\u0006]"}, d2 = {"Lcom/easyui/core/data/datastore/DataStoreLauncherSettingsRepository$Keys;", "", "()V", "ALL_APPS_VISIBLE", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getALL_APPS_VISIBLE", "()Landroidx/datastore/preferences/core/Preferences$Key;", "APP_VISIBILITY_PRESET", "", "getAPP_VISIBILITY_PRESET", "BATTERY_CRITICAL_THRESHOLD", "", "getBATTERY_CRITICAL_THRESHOLD", "BATTERY_LOW_CHECK_ENABLED", "getBATTERY_LOW_CHECK_ENABLED", "BATTERY_LOW_THRESHOLD", "getBATTERY_LOW_THRESHOLD", "CAREGIVER_PROTECTION_ENABLED", "getCAREGIVER_PROTECTION_ENABLED", "DEFAULT_LAUNCHER_CHECK_ENABLED", "getDEFAULT_LAUNCHER_CHECK_ENABLED", "EASYUI_LOCK_ENABLED", "getEASYUI_LOCK_ENABLED", "EASYUI_LOCK_TIMEOUT_SECONDS", "getEASYUI_LOCK_TIMEOUT_SECONDS", "EMERGENCY_CONTACT_CHECK_ENABLED", "getEMERGENCY_CONTACT_CHECK_ENABLED", "EMERGENCY_MODE", "getEMERGENCY_MODE", "EMERGENCY_NUMBERS", "getEMERGENCY_NUMBERS", "EMERGENCY_PHONE_NUMBER", "getEMERGENCY_PHONE_NUMBER", "GUIDED_SETUP_COMPLETED", "getGUIDED_SETUP_COMPLETED", "GUIDED_SETUP_STEP", "getGUIDED_SETUP_STEP", "HEALTH_AGE", "getHEALTH_AGE", "HEALTH_ALLERGIES", "getHEALTH_ALLERGIES", "HEALTH_BLOOD_GROUP", "getHEALTH_BLOOD_GROUP", "HEALTH_CONDITIONS", "getHEALTH_CONDITIONS", "HEALTH_DOCTOR_CONTACT", "getHEALTH_DOCTOR_CONTACT", "HEALTH_MEDICINES", "getHEALTH_MEDICINES", "HEALTH_NAME", "getHEALTH_NAME", "HEALTH_NOTES", "getHEALTH_NOTES", "HOME_PAGE_COUNT", "getHOME_PAGE_COUNT", "HOME_READABILITY_PRESET", "getHOME_READABILITY_PRESET", "INTERNET_CHECK_ENABLED", "getINTERNET_CHECK_ENABLED", "LAYOUT_LOCKED", "getLAYOUT_LOCKED", "LAYOUT_LOCK_CHECK_ENABLED", "getLAYOUT_LOCK_CHECK_ENABLED", "NO_INTERNET_DELAY_MINUTES", "getNO_INTERNET_DELAY_MINUTES", "ONBOARDING_COMPLETE", "getONBOARDING_COMPLETE", "PERMISSION_CHECK_ENABLED", "getPERMISSION_CHECK_ENABLED", "PIN_HASH_HEX", "getPIN_HASH_HEX", "PIN_SALT_HEX", "getPIN_SALT_HEX", "SETUP_OPTIONAL_PERMISSIONS", "", "getSETUP_OPTIONAL_PERMISSIONS", "SETUP_PROTECTION_LEVEL", "getSETUP_PROTECTION_LEVEL", "SHOW_BATTERY_INFO", "getSHOW_BATTERY_INFO", "SKIN_ACCESSIBILITY_MODE", "getSKIN_ACCESSIBILITY_MODE", "SKIN_LAYOUT_MODE", "getSKIN_LAYOUT_MODE", "SKIN_VISUAL_THEME", "getSKIN_VISUAL_THEME", "SOS_NUMBERS", "getSOS_NUMBERS", "USE_24_HOUR_CLOCK", "getUSE_24_HOUR_CLOCK", "VERY_SIMPLE_MODE_ENABLED", "getVERY_SIMPLE_MODE_ENABLED", "data_debug"})
    static final class Keys {
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> ONBOARDING_COMPLETE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> EMERGENCY_PHONE_NUMBER = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> EMERGENCY_NUMBERS = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SOS_NUMBERS = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> USE_24_HOUR_CLOCK = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> CAREGIVER_PROTECTION_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> LAYOUT_LOCKED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> EASYUI_LOCK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> EASYUI_LOCK_TIMEOUT_SECONDS = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> PIN_SALT_HEX = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> PIN_HASH_HEX = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> APP_VISIBILITY_PRESET = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HOME_READABILITY_PRESET = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> VERY_SIMPLE_MODE_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> SHOW_BATTERY_INFO = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> HOME_PAGE_COUNT = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_NAME = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_AGE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_BLOOD_GROUP = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_ALLERGIES = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_CONDITIONS = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_MEDICINES = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_DOCTOR_CONTACT = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> HEALTH_NOTES = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SKIN_LAYOUT_MODE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SKIN_VISUAL_THEME = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SKIN_ACCESSIBILITY_MODE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SETUP_PROTECTION_LEVEL = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.util.Set<java.lang.String>> SETUP_OPTIONAL_PERMISSIONS = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> GUIDED_SETUP_STEP = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> GUIDED_SETUP_COMPLETED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> EMERGENCY_MODE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> ALL_APPS_VISIBLE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> BATTERY_LOW_CHECK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> BATTERY_LOW_THRESHOLD = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> BATTERY_CRITICAL_THRESHOLD = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> INTERNET_CHECK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> NO_INTERNET_DELAY_MINUTES = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> DEFAULT_LAUNCHER_CHECK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> EMERGENCY_CONTACT_CHECK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> LAYOUT_LOCK_CHECK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> PERMISSION_CHECK_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        public static final com.easyui.core.data.datastore.DataStoreLauncherSettingsRepository.Keys INSTANCE = null;
        
        private Keys() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getONBOARDING_COMPLETE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getEMERGENCY_PHONE_NUMBER() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getEMERGENCY_NUMBERS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSOS_NUMBERS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getUSE_24_HOUR_CLOCK() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getCAREGIVER_PROTECTION_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getLAYOUT_LOCKED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getEASYUI_LOCK_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getEASYUI_LOCK_TIMEOUT_SECONDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getPIN_SALT_HEX() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getPIN_HASH_HEX() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getAPP_VISIBILITY_PRESET() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHOME_READABILITY_PRESET() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getVERY_SIMPLE_MODE_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getSHOW_BATTERY_INFO() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getHOME_PAGE_COUNT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_NAME() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_AGE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_BLOOD_GROUP() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_ALLERGIES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_CONDITIONS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_MEDICINES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_DOCTOR_CONTACT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getHEALTH_NOTES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSKIN_LAYOUT_MODE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSKIN_VISUAL_THEME() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSKIN_ACCESSIBILITY_MODE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSETUP_PROTECTION_LEVEL() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.util.Set<java.lang.String>> getSETUP_OPTIONAL_PERMISSIONS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getGUIDED_SETUP_STEP() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getGUIDED_SETUP_COMPLETED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getEMERGENCY_MODE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getALL_APPS_VISIBLE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getBATTERY_LOW_CHECK_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getBATTERY_LOW_THRESHOLD() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getBATTERY_CRITICAL_THRESHOLD() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getINTERNET_CHECK_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getNO_INTERNET_DELAY_MINUTES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getDEFAULT_LAUNCHER_CHECK_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getEMERGENCY_CONTACT_CHECK_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getLAYOUT_LOCK_CHECK_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getPERMISSION_CHECK_ENABLED() {
            return null;
        }
    }
}