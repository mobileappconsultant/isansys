package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LifetouchThreeTest
{
    /**
     * Authentication:
     * Must have already read Timestamp from device
     * Begun by reading BLE CHARACTERISTIC_AUTHENTICATION - LT3 replies with plaintext token
     * Encrypt plaintext token to create auth token
     * Write auth token to BLE CHARACTERISTIC_AUTHENTICATION.
     * Write will fail if auth failed, otherwise authenticated successfully
     * Enable notifications following auth success
     */

    /**
     * Bluetooth Notifications / Indications:
     * Must be enabled before data will be sent by device
     * Indications and Notifications are handled identically
     * Check for success before processing data
     */

    /**
     * Setup Mode:
     * Samples are by default 100Hz (but may soon optionally be 1000Hz)
     * First four bytes are timestamp, MSB first
     * Decrypt following bytes
     * First two bits indicate sample type (whole, +delta, -delta, no_data)
     * Whole is an absolute 2 byte value
     * Deltas are single byte difference between current and last sample
     * No_data only occurs at end of packet when not enough room for a whole sample
     * Gaps in setup mode data are begun and ended with -1 (GAP_IN_DATA)
     * Samples and timestamps are bundled and sent as intent
     */

    /**
     * Accel Mode:
     * Samples are at 10Hz
     * First four bytes are timestamp, MSB first
     * Each sample is one signed byte. Must convert to unsigned (add 128)
     * Parsed samples sent as intent. Order is all x, all y, all z, all timestamps
     */
}
