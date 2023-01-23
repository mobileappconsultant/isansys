package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import androidx.annotation.NonNull;

import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.Arrays;

public class LifetouchThreeSetupMode {

    public final static int GAP_IN_DATA = -1;
    public static final int TIME_STAMP_SIZE = 4;

    private final static int NO_DATA = 0xC0;
    private final static int WHOLE_DATA_POINT = 0x80;
    private final static int POSITIVE_DELTA = 0x40;
    private final static int NEGATIVE_DELTA = 0x00;
    public static final int PREFIX_MASK = 0xC0;
    public static final int WHOLE_SAMPLE_MASK = 0x3FFF;
    public static final int MAX_GAP_MS = 150;
    public static final int DELTA_MASK = 0x3F;

    private final RemoteLogging Log;
    private final static String TAG = LifetouchThreeSetupMode.class.getSimpleName();

    private final LifetouchThreeAuthentication auth;

    private MeasurementSetupModeDataPoint last_measurement = new MeasurementSetupModeDataPoint(0, 0);

    public LifetouchThreeSetupMode(RemoteLogging logger, LifetouchThreeAuthentication auth) {
        Log = logger;
        this.auth = auth;
    }

    public void reset() {
        last_measurement = new MeasurementSetupModeDataPoint(0, 0);
    }

    public ArrayList<MeasurementSetupModeDataPoint> parse(final byte[] data, long sample_period_ms)
    {
        byte[] encryptedSamples = Arrays.copyOfRange(data, TIME_STAMP_SIZE, data.length);
        byte[] decryptedSamples = auth.decryptBlocks(encryptedSamples);

        ArrayList<Short> setup_mode_data = parseSamples(decryptedSamples);

        // setup_mode_data = downsample(setup_mode_data); // For testing

        long first_timestamp = LifetouchThreeTimestamp.parse(data);

        ArrayList<MeasurementSetupModeDataPoint> measurements = new ArrayList<>();

        for (int i = 0; i < setup_mode_data.size(); i++) {
            long timestamp = first_timestamp + sample_period_ms * i;
            measurements.add(new MeasurementSetupModeDataPoint(setup_mode_data.get(i), timestamp));
        }

        return measurements; // fillAnyGaps(measurements);
    }

    private ArrayList<Short> parseSamples(byte[] data) {
        int last_sample = 0;
        ArrayList<Short> samples = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            int prefix = (data[i] & PREFIX_MASK) & 0xFF;

            switch (prefix) {
                case WHOLE_DATA_POINT: {
                    last_sample = parseWholeSample(data, i);
                    i += 1;
                    samples.add((short) last_sample);
                }
                break;

                case POSITIVE_DELTA: {
                    int delta = data[i] & DELTA_MASK;

                    last_sample = last_sample + delta;
                    samples.add((short) last_sample);
                }
                break;

                case NEGATIVE_DELTA: {
                    int delta = data[i] & DELTA_MASK;

                    last_sample = last_sample - delta;
                    samples.add((short) last_sample);
                }
                break;

                case NO_DATA: {
                    // Nothing to do. Lifetouch has detected there is not enough room to write a sample.
                    // Can only happen when trying to write a full 2 byte sample but only 1 byte left
                }
                break;

                default: {
                    // Can never get here
                }
                break;
            }
        }

        return samples;
    }

    private int parseWholeSample(byte[] data, int i) {
        // Then we have the whole value in two bytes
        int sample = (data[i] & 0xFF);
        sample = sample << 8;
        sample += (data[i +1] & 0xFF);
        return sample & WHOLE_SAMPLE_MASK;
    }

    private ArrayList<MeasurementSetupModeDataPoint> fillAnyGaps(ArrayList<MeasurementSetupModeDataPoint> measurements) {
        if (last_measurement.timestamp_in_ms == 0)
            last_measurement.timestamp_in_ms = measurements.get(0).timestamp_in_ms;

        ArrayList<MeasurementSetupModeDataPoint> filled_measurements = new ArrayList<>();

        for (MeasurementSetupModeDataPoint measurement : measurements) {
            long gapBetweenSamplesMS = measurement.timestamp_in_ms - last_measurement.timestamp_in_ms;
            if (gapBetweenSamplesMS > MAX_GAP_MS)
                fillGap(filled_measurements, measurement, gapBetweenSamplesMS);

            last_measurement = measurement;
            filled_measurements.add(measurement);
        }

        return filled_measurements;
    }

    private void fillGap(ArrayList<MeasurementSetupModeDataPoint> filled_measurements, MeasurementSetupModeDataPoint measurement, long gap_between_samples) {
        Log.e(TAG, "Gap at " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms) + " of " + gap_between_samples);

        filled_measurements.add(new MeasurementSetupModeDataPoint(GAP_IN_DATA, last_measurement.timestamp_in_ms + 1));
        filled_measurements.add(new MeasurementSetupModeDataPoint(GAP_IN_DATA, measurement.timestamp_in_ms - 1));
    }

}
