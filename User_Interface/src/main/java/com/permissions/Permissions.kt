package com.permissions

import android.app.Activity
import androidx.lifecycle.LiveData

interface Permissions {



//Interface to check for permissions

fun haveCamera():Boolean
fun haveOverlay(): Boolean
fun haveWriteSystemSetting(): Boolean
fun haveRecordAudio() : Boolean
fun haveAccessNotification():Boolean
fun haveInstallPackage(): Boolean
fun haveWritePermission(): Boolean
fun haveReadPermission(): Boolean

}