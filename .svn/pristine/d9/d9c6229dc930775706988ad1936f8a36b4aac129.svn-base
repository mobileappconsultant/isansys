Acts as USB host, reads NFC data and forwards it to the patient gateway app via USB.

Uses the Arduino USBHost library, specificaly the ADK code. In Android, uses the UsbAccessory library (android.hardware.usb.UsbAccessory).



When plugged in to the tablet, should have a popup asking for permission to connect to the USB Accessory. When accepted, should get a Toast message saying the device is connected.

Then can use the NFC tags in the same way as a QR code - scanning unlock codes and/or device codes.


TO ENABLE:
set boolean include_usb_accessory_code in the Patient Gateway MainActivity to true.