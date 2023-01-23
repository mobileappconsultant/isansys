package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import androidx.annotation.NonNull;

import com.isansys.common.ErrorCodes;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;

public class LifetouchThreeHeartbeats {

    private final RemoteLogging Log;
    private final static String TAG = LifetouchThreeHeartbeats.class.getSimpleName();

    private final int BEAT_SIZE_BYTES = 8;
    private final int BEATS_PER_PAYLOAD = 2;

    private final LifetouchThreeAuthentication auth;

    public LifetouchThreeHeartbeats(RemoteLogging logger, LifetouchThreeAuthentication auth) {
        Log = logger;
        this.auth = auth;
    }

    public ArrayList<HeartBeatInfo> parse(final byte[] encryptedPayload) {
        byte[] data = auth.decryptBlocks(encryptedPayload);
        ArrayList<HeartBeatInfo> heart_beat_list = new ArrayList<>();

        int num_beats = data.length / BEAT_SIZE_BYTES;

        for (int i = 0; i < num_beats; i++)
        {
            int byteIndex = i * BEAT_SIZE_BYTES;
            HeartBeatInfo this_heart_beat = parseBeatInfo(data, byteIndex);

            if (this_heart_beat.getAmplitude() != ErrorCodes.ERROR_CODE__LIFETOUCH_NO_DATA)
            {
//                Log.i(TAG, "RX beat ID " + this_heart_beat.getTag() + ", amplitude " + this_heart_beat.getAmplitude() + ", timestamp " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(this_heart_beat.getTimestampInMs()));

                heart_beat_list.add(this_heart_beat);
            }
        }

        return heart_beat_list;
    }

    @NonNull
    private HeartBeatInfo parseBeatInfo(byte[] data, int startOfBeat) {
        HeartBeatInfo this_heart_beat = new HeartBeatInfo();

        this_heart_beat.setActivity(parseActivityLevel(data[startOfBeat]));

        this_heart_beat.setTag(data[startOfBeat + 1] & 0xFF | (data[startOfBeat] & 0x1F) << 8);

        this_heart_beat.setAmplitude(data[startOfBeat + 3] & 0xFF | (data[startOfBeat + 2] & 0xFF) << 8);
        this_heart_beat.setTimestampInMs(data[startOfBeat + 7] & 0xFF | (data[startOfBeat + 6] & 0xFF) << 8 | (data[startOfBeat + 5] & 0xFF) << 16 | (data[startOfBeat + 4] & 0xFF) << 24);

        return this_heart_beat;
    }

    @NonNull
    private HeartBeatInfo.ActivityLevel parseActivityLevel(byte level) {
        HeartBeatInfo.ActivityLevel activityLevel = HeartBeatInfo.ActivityLevel.fromInt((level & 0xE0) >> 5);
        if (activityLevel == null)
            activityLevel = HeartBeatInfo.ActivityLevel.NO_DATA;
        return activityLevel;
    }

    public int parseNumPending(final byte[] data) {
        // Final four bytes are num beats remaining in memory
        return (data[0] & 0xFF) << 24 |
               (data[1] & 0xFF) << 16 |
               (data[2] & 0xFF) << 8 |
                data[3] & 0xFF;
    }

}
