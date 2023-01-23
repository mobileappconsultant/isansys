package com.data.log

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LogImpl @Inject constructor(
val context: Context
) : Logs{

override fun createLogFile() {
TODO("Not yet implemented")
}

override fun deleteLogFile(fileName: String):Boolean {
return fileName.let {
val file = File(context.filesDir, it)
file.delete()
}

}

override suspend fun writeToLogFile(data: String) {
withContext(Dispatchers.IO) {
val fileOutputStream: FileOutputStream = context.openFileOutput(FILE, Context.MODE_PRIVATE)
fileOutputStream.write("$data".toByteArray())
}
}

companion object {
const val FILE_NAME : String = "ISANSY_LOG_FILE"
const val FILE_EXT : String = ".txt"
const val FILE = "$FILE_NAME$FILE_EXT"
}

}