package com.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
fun Context.permissionState(permission :String) : Boolean{
return this.checkSelfPermission(permission) != PackageManager.PERMISSION_DENIED
}

fun View.visibility(isPermissionAvailable : Boolean){
if(isPermissionAvailable) this.visibility = View.INVISIBLE else this.visibility = View.VISIBLE
}

fun Context.requestPermissionFromSettings(action :String){
val intent = Intent(action)
intent.data = Uri.parse("package:${this.packageName}")
//intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
startActivity(intent)
}