package com.isansys.ui.permissions_fragment

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.permissions.Permissions
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.NetworkMonitor.init
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
private val permissions: Permissions
):ViewModel() {

//live data
private val _haveCamera = MutableLiveData<Boolean>()
val haveCamera : LiveData<Boolean> = _haveCamera

val haveOverlay: MutableLiveData<Boolean> by lazy {
MutableLiveData<Boolean>()
}

val haveWriteSystemSetting: MutableLiveData<Boolean> by lazy {
MutableLiveData<Boolean>()
}

val haveRecordAudio: MutableLiveData<Boolean> by lazy {
MutableLiveData<Boolean>()
}

val haveAccessNotification: MutableLiveData<Boolean> by lazy {
MutableLiveData<Boolean>()
}

val haveInstallPackage: MutableLiveData<Boolean> by lazy {
MutableLiveData<Boolean>()
}
val haveReadWrite: MutableLiveData<Boolean> by lazy {
MutableLiveData<Boolean>()
}

//

fun isHaveCamera(){
_haveCamera.value = permissions.haveCamera()
}
fun isHaveReadWrite(){
haveReadWrite.value = permissions.haveWritePermission()
}

fun isHaveRecordAudio(){
haveRecordAudio.value = permissions.haveRecordAudio()
}


fun isHaveOverlay(){
haveOverlay.value = permissions.haveOverlay()
}


fun isHaveWriteSystemSetting(){
haveWriteSystemSetting.value = permissions.haveWriteSystemSetting()
}


fun isHaveAccessNotification(){
haveAccessNotification.value = permissions.haveAccessNotification()
}


fun isHaveInstallPackage(){
haveInstallPackage.value = permissions.haveInstallPackage()
}




init {
isHaveCamera()
isHaveOverlay()
isHaveRecordAudio()
isHaveAccessNotification()
isHaveWriteSystemSetting()
isHaveInstallPackage()
isHaveReadWrite()
}





}