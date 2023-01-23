package com.permissions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\b\u0010\u0005\u001a\u00020\u0003H&J\b\u0010\u0006\u001a\u00020\u0003H&J\b\u0010\u0007\u001a\u00020\u0003H&J\b\u0010\b\u001a\u00020\u0003H&J\b\u0010\t\u001a\u00020\u0003H&J\b\u0010\n\u001a\u00020\u0003H&\u00a8\u0006\u000b"}, d2 = {"Lcom/permissions/Permissions;", "", "haveAccessNotification", "", "haveCamera", "haveInstallPackage", "haveOverlay", "haveReadPermission", "haveRecordAudio", "haveWritePermission", "haveWriteSystemSetting", "User_Interface_debug"})
public abstract interface Permissions {
    
    public abstract boolean haveCamera();
    
    public abstract boolean haveOverlay();
    
    public abstract boolean haveWriteSystemSetting();
    
    public abstract boolean haveRecordAudio();
    
    public abstract boolean haveAccessNotification();
    
    public abstract boolean haveInstallPackage();
    
    public abstract boolean haveWritePermission();
    
    public abstract boolean haveReadPermission();
}