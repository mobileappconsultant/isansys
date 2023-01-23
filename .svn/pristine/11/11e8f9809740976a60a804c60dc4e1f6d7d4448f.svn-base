package com.permissions

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.isansys.pse_isansysportal.Permissions.CAMERA_PERMISSION_REQUEST_CODE
import com.isansys.pse_isansysportal.Permissions.REQUEST_AUDIO_CODE
import com.isansys.pse_isansysportal.Permissions.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
import com.util.permissionState
import javax.inject.Inject

class PermissionsImpl @Inject constructor(
private val context: Context
) : Permissions{


override fun haveCamera(): Boolean {
return context.permissionState(Manifest.permission.CAMERA)
}

override fun haveOverlay(): Boolean {
return Settings.canDrawOverlays(context)
}

override fun haveWriteSystemSetting(): Boolean {
return Settings.System.canWrite(context)
}

override fun haveWritePermission(): Boolean {
return context.permissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

override fun haveReadPermission(): Boolean {
return context.permissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
}

override fun haveRecordAudio(): Boolean {
return context.permissionState(Manifest.permission.RECORD_AUDIO)
}

override fun haveAccessNotification(): Boolean {
val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
return notificationManager.isNotificationPolicyAccessGranted
}

override fun haveInstallPackage(): Boolean {
return context.packageManager.canRequestPackageInstalls()
}

companion object{

const val CAMERA_PERMISSION_REQUEST_CODE = 100
const val REQUEST_AUDIO_CODE = 101
const val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 102

}

}