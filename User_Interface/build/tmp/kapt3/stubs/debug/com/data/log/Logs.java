package com.data.log;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0019\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0007H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\n\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u000b"}, d2 = {"Lcom/data/log/Logs;", "", "createLogFile", "", "deleteLogFile", "", "fileName", "", "writeToLogFile", "data", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "User_Interface_debug"})
public abstract interface Logs {
    
    public abstract void createLogFile();
    
    public abstract boolean deleteLogFile(@org.jetbrains.annotations.NotNull()
    java.lang.String fileName);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object writeToLogFile(@org.jetbrains.annotations.NotNull()
    java.lang.String data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
}