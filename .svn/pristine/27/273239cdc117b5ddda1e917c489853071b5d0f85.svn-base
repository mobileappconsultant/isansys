package com.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPref @Inject constructor(val context: Context) {

private val SYSTEM_SETTINGS = "Settings"

private val preferences: SharedPreferences = context.getSharedPreferences("Pref",Context.MODE_PRIVATE)

var permission: String?
get() = preferences.getString(SYSTEM_SETTINGS,"")
set(value) = preferences.edit().putString(SYSTEM_SETTINGS, value).apply()


}