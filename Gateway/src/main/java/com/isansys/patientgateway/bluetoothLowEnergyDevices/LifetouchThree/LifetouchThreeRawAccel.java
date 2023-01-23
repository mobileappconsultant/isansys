package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import androidx.annotation.NonNull;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.Arrays;

public class LifetouchThreeRawAccel {

    public static final int TIME_STAMP_SIZE = 4;

    private final RemoteLogging Log;
    private final static String TAG = LifetouchThreeRawAccel.class.getSimpleName();

    private final LifetouchThreeAuthentication auth;

    public LifetouchThreeRawAccel(RemoteLogging logger, LifetouchThreeAuthentication auth) {
        Log = logger;
        this.auth = auth;
    }

    @NonNull
    public AccelData parse(byte[] encrypted_data) {
        byte[] encrypted_samples = Arrays.copyOfRange(encrypted_data, TIME_STAMP_SIZE, encrypted_data.length);
        byte[] data = auth.decryptBlocks(encrypted_samples);

        int number_of_samples = data[data.length-1] & 0xFF; // Final byte contains number of samples in payload

        AccelData accelData = new AccelData();

        short[] x_axis_samples = new short[number_of_samples];
        short[] y_axis_samples = new short[number_of_samples];
        short[] z_axis_samples = new short[number_of_samples];

        final int time_delay_between_samples_in_milliseconds = 100;  // 10 Hz Raw Accelerometer Mode data
        long[] timestamps = LifetouchThreeTimestamp.rawDataSampleTimestampExtractor(time_delay_between_samples_in_milliseconds, data, number_of_samples);

        int i, j;

        // Convert the remaining bytes to into Samples (shorts)
        j = 0;
        for (i = 0; i < number_of_samples; i++) {
            // The +128 takes it from signed to unsigned "measurement". The &0xFF takes its to a Java unsigned variable
            int x_axis_sample = (data[j++] + 128) & 0xFF;
            int y_axis_sample = (data[j++] + 128) & 0xFF;
            int z_axis_sample = (data[j++] + 128) & 0xFF;

            x_axis_samples[i] = (short) x_axis_sample;
            y_axis_samples[i] = (short) y_axis_sample;
            z_axis_samples[i] = (short) z_axis_sample;
        }

        accelData.x = x_axis_samples;
        accelData.y = y_axis_samples;
        accelData.z = z_axis_samples;
        accelData.timestamps = timestamps;
        return accelData;
    }

}
