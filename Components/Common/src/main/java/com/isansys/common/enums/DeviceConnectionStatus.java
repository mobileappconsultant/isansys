package com.isansys.common.enums;

public enum DeviceConnectionStatus
{
    SEARCHING,      // Searching during initial connection on the FragmentDeviceConnection page
    CONNECTED,      // Device is fully connected and transmitting data
    FOUND,          // Device has been found in initial scan, but not yet fully connected
    DISCONNECTED,   // Device was previously found and connected, but has gone out of range.
    NOT_PAIRED,     // Device either disconnected and removed from session, or has not yet been connected
    PAIRED,         // Device (AnD_UA767) is in CONNECTED state while taking measurements otherwise DISCONNECTED.
                    // So CONNECTED state is not ideal for AnD_UA767 for starting session.
    DFU,            // Device is doing a firmware update
    UNBONDED,       // BLE AnD only - sometimes the device unbonds during a session
}