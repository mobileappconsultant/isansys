package com.isansys.ui.permissions_fragment;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001a\u001a\u00020\u0002H\u0014J\b\u0010\u001b\u001a\u00020\u0019H\u0016J-\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u001e2\u000e\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\u001f2\u0006\u0010 \u001a\u00020!H\u0016\u00a2\u0006\u0002\u0010\"J\b\u0010#\u001a\u00020\u0019H\u0016J\b\u0010$\u001a\u00020\u0019H\u0016J\u0018\u0010%\u001a\u00020\u00192\u0006\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020)H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001b\u0010\u0012\u001a\u00020\u00138BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006*"}, d2 = {"Lcom/isansys/ui/permissions_fragment/FragmentPermissions;", "Lcom/isansys/ui/common/BaseFragment;", "Lcom/isansys/pse_isansysportal/databinding/AndroidPermissionsBinding;", "()V", "TAG", "", "permissions", "Lcom/permissions/Permissions;", "getPermissions", "()Lcom/permissions/Permissions;", "setPermissions", "(Lcom/permissions/Permissions;)V", "sharedPref", "Lcom/data/local/SharedPref;", "getSharedPref", "()Lcom/data/local/SharedPref;", "setSharedPref", "(Lcom/data/local/SharedPref;)V", "viewModel", "Lcom/isansys/ui/permissions_fragment/PermissionViewModel;", "getViewModel", "()Lcom/isansys/ui/permissions_fragment/PermissionViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "checkSettings", "", "getViewBinding", "observeData", "onRequestPermissionsResult", "requestCode", "", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "setUpViews", "showIndicator", "indicator", "Landroid/view/View;", "success", "", "User_Interface_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class FragmentPermissions extends com.isansys.ui.common.BaseFragment<com.isansys.pse_isansysportal.databinding.AndroidPermissionsBinding> {
    private final java.lang.String TAG = "PERMISSIONS";
    private final kotlin.Lazy viewModel$delegate = null;
    @javax.inject.Inject()
    public com.permissions.Permissions permissions;
    @javax.inject.Inject()
    public com.data.local.SharedPref sharedPref;
    
    public FragmentPermissions() {
        super();
    }
    
    private final com.isansys.ui.permissions_fragment.PermissionViewModel getViewModel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.permissions.Permissions getPermissions() {
        return null;
    }
    
    public final void setPermissions(@org.jetbrains.annotations.NotNull()
    com.permissions.Permissions p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.data.local.SharedPref getSharedPref() {
        return null;
    }
    
    public final void setSharedPref(@org.jetbrains.annotations.NotNull()
    com.data.local.SharedPref p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected com.isansys.pse_isansysportal.databinding.AndroidPermissionsBinding getViewBinding() {
        return null;
    }
    
    @java.lang.Override()
    public void setUpViews() {
    }
    
    @java.lang.Override()
    public void observeData() {
    }
    
    private final void showIndicator(android.view.View indicator, boolean success) {
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    @java.lang.Override()
    public void onResume() {
    }
    
    private final void checkSettings() {
    }
}