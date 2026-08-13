package com.easyui.core.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0096@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\nH\u0016J\u001c\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0096@\u00a2\u0006\u0002\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/easyui/core/data/repository/RoomHomeLayoutRepository;", "Lcom/easyui/core/domain/repository/HomeLayoutRepository;", "homeTileDao", "Lcom/easyui/core/data/database/HomeTileDao;", "(Lcom/easyui/core/data/database/HomeTileDao;)V", "getTiles", "", "Lcom/easyui/core/domain/model/HomeTile;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeTiles", "Lkotlinx/coroutines/flow/Flow;", "replaceTiles", "", "tiles", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_debug"})
public final class RoomHomeLayoutRepository implements com.easyui.core.domain.repository.HomeLayoutRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.easyui.core.data.database.HomeTileDao homeTileDao = null;
    
    public RoomHomeLayoutRepository(@org.jetbrains.annotations.NotNull()
    com.easyui.core.data.database.HomeTileDao homeTileDao) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.easyui.core.domain.model.HomeTile>> observeTiles() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getTiles(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.easyui.core.domain.model.HomeTile>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object replaceTiles(@org.jetbrains.annotations.NotNull()
    java.util.List<com.easyui.core.domain.model.HomeTile> tiles, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}