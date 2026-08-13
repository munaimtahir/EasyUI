package com.easyui.core.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0001&B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0012\u001a\u00020\u000bH\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\r2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0016\u0010\u001a\u001a\u00020\u000b2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002J\u0010\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u0014H\u0002J\u0010\u0010\u001e\u001a\u00020\u00162\u0006\u0010\f\u001a\u00020\rH\u0002J\u0010\u0010\u001f\u001a\u00020\u00162\u0006\u0010 \u001a\u00020\u0019H\u0002J\u0014\u0010!\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\"H\u0016J\u0016\u0010#\u001a\u00020\t2\u0006\u0010$\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010%R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/easyui/core/data/repository/DataStoreRemoteLinkRepository;", "Lcom/easyui/core/domain/repository/RemoteLinkRepository;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "dataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "addOrUpdateDevice", "", "deviceName", "", "packet", "Lcom/easyui/core/domain/model/RemoteStatusPacket;", "(Ljava/lang/String;Lcom/easyui/core/domain/model/RemoteStatusPacket;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "decodeDevices", "", "Lcom/easyui/core/domain/model/LinkedDevice;", "jsonStr", "decodeHealthState", "Lcom/easyui/core/domain/model/PhoneHealthState;", "json", "Lorg/json/JSONObject;", "decodePacket", "decodeSetupCompleteness", "Lcom/easyui/core/domain/model/SetupCompleteness;", "encodeDevices", "devices", "encodeHealthState", "state", "encodePacket", "encodeSetupCompleteness", "setup", "observeLinkedDevices", "Lkotlinx/coroutines/flow/Flow;", "removeDevice", "deviceId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Keys", "data_debug"})
public final class DataStoreRemoteLinkRepository implements com.easyui.core.domain.repository.RemoteLinkRepository {
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> dataStore = null;
    
    public DataStoreRemoteLinkRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.easyui.core.domain.model.LinkedDevice>> observeLinkedDevices() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addOrUpdateDevice(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceName, @org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.RemoteStatusPacket packet, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeDevice(@org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.String encodeDevices(java.util.List<com.easyui.core.domain.model.LinkedDevice> devices) {
        return null;
    }
    
    private final java.util.List<com.easyui.core.domain.model.LinkedDevice> decodeDevices(java.lang.String jsonStr) {
        return null;
    }
    
    private final org.json.JSONObject encodePacket(com.easyui.core.domain.model.RemoteStatusPacket packet) {
        return null;
    }
    
    private final com.easyui.core.domain.model.RemoteStatusPacket decodePacket(org.json.JSONObject json) {
        return null;
    }
    
    private final org.json.JSONObject encodeHealthState(com.easyui.core.domain.model.PhoneHealthState state) {
        return null;
    }
    
    private final com.easyui.core.domain.model.PhoneHealthState decodeHealthState(org.json.JSONObject json) {
        return null;
    }
    
    private final org.json.JSONObject encodeSetupCompleteness(com.easyui.core.domain.model.SetupCompleteness setup) {
        return null;
    }
    
    private final com.easyui.core.domain.model.SetupCompleteness decodeSetupCompleteness(org.json.JSONObject json) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u00c2\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/easyui/core/data/repository/DataStoreRemoteLinkRepository$Keys;", "", "()V", "LINKED_DEVICES", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getLINKED_DEVICES", "()Landroidx/datastore/preferences/core/Preferences$Key;", "data_debug"})
    static final class Keys {
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> LINKED_DEVICES = null;
        @org.jetbrains.annotations.NotNull()
        public static final com.easyui.core.data.repository.DataStoreRemoteLinkRepository.Keys INSTANCE = null;
        
        private Keys() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getLINKED_DEVICES() {
            return null;
        }
    }
}