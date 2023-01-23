package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import androidx.annotation.NonNull;

import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class LifetouchThreeBattery {

    private final RemoteLogging Log;
    private final static String TAG = LifetouchThreeBattery.class.getSimpleName();

    private final LifetouchThreeAuthentication auth;

    public LifetouchThreeBattery(RemoteLogging logger, LifetouchThreeAuthentication auth) {
        Log = logger;
        this.auth = auth;
    }

    @NonNull
    public BatteryLevel parse(byte[] data) {
        BatteryLevel level = new BatteryLevel();

        level.percentage = data[0] & 0xFF;

        int battery_voltage = (data[1] & 0xFF);
        battery_voltage = battery_voltage * 256;
        battery_voltage = battery_voltage + (data[2] & 0xFF);
        level.averageVoltage_mV = battery_voltage;

        level.timestampMS = ((long) (data[3] & 0xFF) << 24) |
                ((data[4] & 0xFF) << 16) |
                ((data[5] & 0xFF) << 8) |
                ((data[6] & 0xFF));

        int battery_instant_voltage = (data[7] & 0xFF);
        battery_instant_voltage = battery_instant_voltage * 256;
        battery_instant_voltage = battery_instant_voltage + (data[8] & 0xFF);
        level.instantVoltage_mV = battery_instant_voltage;

        Log.i(TAG, "UUID_ISANSYS_BATTERY_LEVEL : Received Lifetouch Battery Level % : " + level.percentage +
                " and measured Battery Voltage of " + level.averageVoltage_mV +
                " . Timestamp received = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(level.timestampMS));

        return level;
    }

}
