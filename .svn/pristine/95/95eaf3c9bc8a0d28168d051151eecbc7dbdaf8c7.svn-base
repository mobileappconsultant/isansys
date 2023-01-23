package com.permissions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.isansys.pse_isansysportal.MainActivity

class PermissionsReceiver : BroadcastReceiver() {

override fun onReceive(context: Context, intent: Intent) {
if(context.packageManager.canRequestPackageInstalls()) {
// The permission was granted, you can start the activity
val activityIntent = Intent(context, MainActivity::class.java)
context.startActivity(activityIntent)
}
}
}