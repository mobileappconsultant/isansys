package com.isansys.patientgateway.bluetooth

import android.app.Activity
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentSender
import android.os.ParcelUuid
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.isansys.patientgateway.MainActivity
import java.util.UUID
import java.util.regex.Pattern

class Bluetooth(val context: Context) {

 val deviceManager: CompanionDeviceManager by lazy {
context.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
}

val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
//.setNamePattern(Pattern.compile("My device"))
//.addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null)
.build()

val pairingRequest: AssociationRequest = AssociationRequest.Builder()
//.addDeviceFilter(deviceFilter)
//.setSingleDevice(true)
.build()

fun pairDevice(
onFailure : (String) -> Unit
){
deviceManager.associate(pairingRequest,
object :CompanionDeviceManager.Callback(){

override fun onFailure(error: CharSequence?) {
onFailure.invoke(error.toString())
}

// Todo-> pass activity into Activity()
override fun onDeviceFound(intentSender: IntentSender) {
super.onDeviceFound(intentSender)
ActivityCompat.startIntentSenderForResult(Activity(),intentSender,SELECT_BLUETOOTH_DEVICE_REQUEST_CODE,null,0,0,0,null)
}

},
null
)
}

companion object{
const val SELECT_BLUETOOTH_DEVICE_REQUEST_CODE = 8030
}

}