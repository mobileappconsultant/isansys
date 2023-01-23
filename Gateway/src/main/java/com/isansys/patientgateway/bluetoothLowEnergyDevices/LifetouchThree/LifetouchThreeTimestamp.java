package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

public class LifetouchThreeTimestamp {

    public static long parse(byte[] data)
    {
        long timestamp = (data[3] & 0xFF);
        timestamp <<= 8;
        timestamp += (data[2] & 0xFF);
        timestamp <<= 8;
        timestamp += (data[1] & 0xFF);
        timestamp <<= 8;
        timestamp += (data[0] & 0xFF);

        return timestamp;
    }

    public static long[] rawDataSampleTimestampExtractor(int timeDelayBetweenSamplesInMilliseconds, byte[] data, int number_of_samples) {
        long[] return_value = new long[number_of_samples];

        long first_sample_timestamp = (data[3] & 0xFF);
        first_sample_timestamp <<= 8;
        first_sample_timestamp += (data[2] & 0xFF);
        first_sample_timestamp <<= 8;
        first_sample_timestamp += (data[1] & 0xFF);
        first_sample_timestamp <<= 8;
        first_sample_timestamp += (data[0] & 0xFF);

        for (int counter = 0; counter < number_of_samples; counter++) {
            return_value[counter] = first_sample_timestamp + ((long) counter * timeDelayBetweenSamplesInMilliseconds);
        }

        return return_value;
    }

}
