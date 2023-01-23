package com.permissions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\u0006H\u0016J\b\u0010\b\u001a\u00020\u0006H\u0016J\b\u0010\t\u001a\u00020\u0006H\u0016J\b\u0010\n\u001a\u00020\u0006H\u0016J\b\u0010\u000b\u001a\u00020\u0006H\u0016J\b\u0010\f\u001a\u00020\u0006H\u0016J\b\u0010\r\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/permissions/PermissionsImpl;", "Lcom/permissions/Permissions;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "haveAccessNotification", "", "haveCamera", "haveInstallPackage", "haveOverlay", "haveReadPermission", "haveRecordAudio", "haveWritePermission", "haveWriteSystemSetting", "Companion", "User_Interface_debug"})
public final class PermissionsImpl implements com.permissions.Permissions {
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.permissions.PermissionsImpl.Companion Companion = null;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    public static final int REQUEST_AUDIO_CODE = 101;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 102;
    
    @javax.inject.Inject()
    public PermissionsImpl(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    public boolean haveCamera() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveOverlay() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveWriteSystemSetting() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveWritePermission() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveReadPermission() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveRecordAudio() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveAccessNotification() {
        return false;
    }
    
    @java.lang.Override()
    public boolean haveInstallPackage() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/permissions/PermissionsImpl$Companion;", "", "()V", "CAMERA_PERMISSION_REQUEST_CODE", "", "REQUEST_AUDIO_CODE", "WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE", "User_Interface_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}