package com.isansys.ui.permissions_fragment;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010 \u001a\u00020!J\u0006\u0010\"\u001a\u00020!J\u0006\u0010#\u001a\u00020!J\u0006\u0010$\u001a\u00020!J\u0006\u0010%\u001a\u00020!J\u0006\u0010&\u001a\u00020!J\u0006\u0010\'\u001a\u00020!R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R!\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R!\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\f\u001a\u0004\b\u0012\u0010\nR!\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0016\u0010\f\u001a\u0004\b\u0015\u0010\nR!\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0019\u0010\f\u001a\u0004\b\u0018\u0010\nR!\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001c\u0010\f\u001a\u0004\b\u001b\u0010\nR!\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\f\u001a\u0004\b\u001e\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/isansys/ui/permissions_fragment/PermissionViewModel;", "Landroidx/lifecycle/ViewModel;", "permissions", "Lcom/permissions/Permissions;", "(Lcom/permissions/Permissions;)V", "_haveCamera", "Landroidx/lifecycle/MutableLiveData;", "", "haveAccessNotification", "getHaveAccessNotification", "()Landroidx/lifecycle/MutableLiveData;", "haveAccessNotification$delegate", "Lkotlin/Lazy;", "haveCamera", "Landroidx/lifecycle/LiveData;", "getHaveCamera", "()Landroidx/lifecycle/LiveData;", "haveInstallPackage", "getHaveInstallPackage", "haveInstallPackage$delegate", "haveOverlay", "getHaveOverlay", "haveOverlay$delegate", "haveReadWrite", "getHaveReadWrite", "haveReadWrite$delegate", "haveRecordAudio", "getHaveRecordAudio", "haveRecordAudio$delegate", "haveWriteSystemSetting", "getHaveWriteSystemSetting", "haveWriteSystemSetting$delegate", "isHaveAccessNotification", "", "isHaveCamera", "isHaveInstallPackage", "isHaveOverlay", "isHaveReadWrite", "isHaveRecordAudio", "isHaveWriteSystemSetting", "User_Interface_debug"})
public final class PermissionViewModel extends androidx.lifecycle.ViewModel {
    private final com.permissions.Permissions permissions = null;
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _haveCamera = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Boolean> haveCamera = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy haveOverlay$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy haveWriteSystemSetting$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy haveRecordAudio$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy haveAccessNotification$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy haveInstallPackage$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy haveReadWrite$delegate = null;
    
    @javax.inject.Inject()
    public PermissionViewModel(@org.jetbrains.annotations.NotNull()
    com.permissions.Permissions permissions) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getHaveCamera() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getHaveOverlay() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getHaveWriteSystemSetting() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getHaveRecordAudio() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getHaveAccessNotification() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getHaveInstallPackage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getHaveReadWrite() {
        return null;
    }
    
    public final void isHaveCamera() {
    }
    
    public final void isHaveReadWrite() {
    }
    
    public final void isHaveRecordAudio() {
    }
    
    public final void isHaveOverlay() {
    }
    
    public final void isHaveWriteSystemSetting() {
    }
    
    public final void isHaveAccessNotification() {
    }
    
    public final void isHaveInstallPackage() {
    }
}