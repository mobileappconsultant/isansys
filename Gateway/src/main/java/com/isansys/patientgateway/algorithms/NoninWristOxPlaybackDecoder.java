package com.isansys.patientgateway.algorithms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.IntermediateSpO2;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.isansys.patientgateway.bluetoothLowEnergyDevices.NoninWristOxBtle.BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_EXPECTED;
import static com.isansys.patientgateway.bluetoothLowEnergyDevices.NoninWristOxBtle.BluetoothLeNoninWristOx.ACTION_NONIN_SETUP_MODE_IS_BLOCKED;
import static java.lang.Long.MAX_VALUE;


public class NoninWristOxPlaybackDecoder
{
    private final ContextInterface gateway_context_interface;
    private final RemoteLogging Log;
    private final String TAG = "NoninWristOxPlaybackDecoder";

    public final static String NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK = "com.isansys.patientgateway.NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK";
    public final static String NONIN_WRIST_OX__READINGS_FROM_PLAYBACK = "com.isansys.patientgateway.NONIN_WRIST_OX__READINGS_FROM_PLAYBACK";
    public final static String NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE = "com.isansys.patientgateway.NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE";
    public final static String NONIN_WRIST_OX__START_PLAYBACK_SIMULATION_FROM_FILE = "com.isansys.patientgateway.NONIN_WRIST_OX__START_PLAYBACK_SIMULATION_FROM_FILE";

    // These copied from NoninWristOx.java
    public final static String NONIN_WRIST_OX__SP_O2 = "com.isansys.patientgateway.NONIN_WRIST_OX__SP_O2";
    public final static String NONIN_WRIST_OX__HEART_RATE = "com.isansys.patientgateway.NONIN_WRIST_OX__HEART_RATE";
    public final static String NONIN_WRIST_OX__TIMESTAMP = "com.isansys.patientgateway.NONIN_WRIST_OX__TIMESTAMP";

    private long time_of_last_reading = MAX_VALUE;

    private Timer nonin_playback_decoder_timer;

    private ArrayList<NoninPlaybackStudy> all_studies;
    private int study_to_send_index = 0;
    private long interval_between_playback_samples = -1;
    private boolean playback_session_was_started = false;
    private boolean sending = false;


    // Used only when testing via a Python file via the developer popup window. Python data happens in the past, i.e. before the time of the last reading,
    // so would not ordinarily be sent without this being true...
    private boolean debug_test_send_all = false;

    // READINGS_TO_SEND_PER_TICK is the amount of readings sent per sendNext().
    // 1499 is fast and works on files with 108 hrs of data.
    // However, at 1499 the session ends unexpectedly with 270 hrs of data (maximum Nonin capacity at 1 second/sample).
    // 500 works with 270 hrs of data but takes ~ 40 mins to process.
    // Values of 600 - 1499 failed with 270 hrs of data.
    private final int READINGS_TO_SEND_PER_TICK = 1500;

    private ConcurrentLinkedQueue<SpO2AndPulse> readingsQueue;
    private long mTimestamp_in_ms;


    static class SpO2AndPulse
    {
        public final int SpO2;
        public final int Pulse;

        public SpO2AndPulse(int spO2, int pulse)
        {
            SpO2 = spO2;
            Pulse = pulse;
        }
    }

    private static class NoninPlaybackStudy
    {
        long start_time = -1;
        long end_time = -1;

        ArrayList<SpO2AndPulse> readings = new ArrayList<>();

        long interval_between_playback_samples = -1;
    }


    public interface StudyCompleteCallback
    {
        void onStudyComplete();
    }

    // To help prevent requesting a playback before the previous playback has been fully processed
    public boolean isPlaybackOngoing()
    {
        return playback_session_was_started;
    }

    private StudyCompleteCallback completion_callback;


    public void newPlaybackSession(StudyCompleteCallback callback)
    {
        Log.d(TAG, "newPlaybackSession playback_session_was_started = true");

        if (all_studies != null)
        {
            all_studies.clear();
        }

        study_to_send_index = 0;

        completion_callback = callback;

        playback_session_was_started = true;
    }


    public void playbackFailed()
    {
        Log.d(TAG, "playbackFailed playback_session_was_started = false");

        playback_session_was_started = false;

        informGatewayThatNoninPlaybackIsNotExpected();

        if (completion_callback != null)
        {
            completion_callback.onStudyComplete();
        }

        informGatewayThatNoninSetupModeIsNotBlocked();
    }


    public void reset()
    {
        Log.d(TAG, "reset()");

        setTimeOfLastReading(MAX_VALUE);

        sending = false;
        playback_session_was_started = false;
    }


    private void informGatewayThatNoninPlaybackIsExpected()
    {
        Log.d(TAG, "informGatewayThatNoninPlaybackIsExpected");

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_PLAYBACK_EXPECTED);
        intent.putExtra("expected", true);

        gateway_context_interface.sendBroadcastIntent(intent);
    }



    private void informGatewayThatNoninPlaybackIsNotExpected()
    {
        Log.d(TAG, "informGatewayThatNoninPlaybackIsNotExpected");

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_PLAYBACK_EXPECTED);
        intent.putExtra("expected", false);

        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void informGatewayThatNoninSetupModeIsBlocked()
    {
        Log.d(TAG, "informGatewayThatNoninSetupModeIsBlocked");

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_SETUP_MODE_IS_BLOCKED);
        intent.putExtra("nonin_setup_mode_blocked", true);

        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void informGatewayThatNoninSetupModeIsNotBlocked()
    {
        Log.d(TAG, "informGatewayThatNoninSetupModeIsNotBlocked");

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_SETUP_MODE_IS_BLOCKED);
        intent.putExtra("nonin_setup_mode_blocked", false);

        gateway_context_interface.sendBroadcastIntent(intent);
    }


    private final BroadcastReceiver broadcast_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (all_studies != null)
            {
                Log.d(TAG, "Decoder received " + action + " study_to_send_index " + study_to_send_index + " no.OfStudies:" + all_studies.size() + " sending?" + sending);
            }

            if (all_studies != null)
            {
                if (action.equals(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE))
                {
                    Log.d(TAG, "NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE study_to_send_index:" + study_to_send_index);

                    if (study_to_send_index  < all_studies.size()  && !sending)
                    {
                        study_to_send_index++;

                        GenericStartStopTimer.cancelTimer(nonin_playback_decoder_timer, Log);

                        sending = true;

                        if (study_to_send_index < all_studies.size())
                        {
                            sendStudy(study_to_send_index);
                        }
                    }
                    else
                    {
                        // if (study_to_send_index == all_studies.size() && playback_session_was_started)
                        if (study_to_send_index == all_studies.size() )
                        {
                            Log.d(TAG, "Decoder thinks it has sent everything for this playback session (all studies) playback_session_was_started = false");

                            GenericStartStopTimer.cancelTimer(nonin_playback_decoder_timer, Log);

                            sending = false;

                            playback_session_was_started = false;

                            informGatewayThatNoninPlaybackIsNotExpected();

                            if (completion_callback != null)
                            {
                                completion_callback.onStudyComplete();
                            }

                            //informGatewayThatNoninSetupModeIsNotBlocked();

                            // Set back to false so that subsequent real playbacks are not sent to the Gateway unless their time is later than the last live reading
                            debug_test_send_all = false;
                        }
                    }
                }
            }
            else
            {
                Log.d(TAG, "all_studies is null ! playback_session_was_started = false");

                informGatewayThatNoninPlaybackIsNotExpected();

                if (completion_callback != null)
                {
                    completion_callback.onStudyComplete();
                }

                informGatewayThatNoninSetupModeIsNotBlocked();

                playback_session_was_started = false;
            }
        }
    };


    public NoninWristOxPlaybackDecoder(ContextInterface gateway_context_interface, RemoteLogging logger)
    {
        this.gateway_context_interface = gateway_context_interface;
        this.Log = logger;
    }


    public void setTimeOfLastReading(long timeOfLastReading)
    {
        Log.d(TAG, "setTimeOfLastReading: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timeOfLastReading));

        time_of_last_reading = timeOfLastReading;
    }


    public void setDebugSendAll(boolean debug)
    {
        Log.d(TAG, "setting DebugSendAll to " + debug);

        debug_test_send_all = debug;
    }


    private void decodeStudy(final String study)
    {
        Log.d(TAG, "decodeStudy()");

        // A "study" is a period of saved data saved on the Nonin, a minimum of one minute, typically covering the period between inserting and removing a finger

        NoninPlaybackStudy this_study = new NoninPlaybackStudy();

        // Hard coded numbers below correspond to positions of data in the Extended Checksum Memory Data Format of the Nonin 3150
        // See Appendix A of document 3150 Memory Playback Document 110520-000 REV B.pdf

        // Seconds/sample
        String secondsPerSampleStr = study.substring(1,3);
        int secondsPerSample = Integer.parseInt(secondsPerSampleStr, 16);

        // Start of this study
        String startYearStr = (study.substring(73, 75));
        String startMonthStr = (study.substring(64, 66));
        String startDateStr = (study.substring(67, 69));
        String startHourStr = (study.substring(85, 87));
        String startMinuteStr = (study.substring(76, 78));
        String startSecondStr = (study.substring(82, 84));

        // end of this study
        String endYearStr = (study.substring(46, 48));
        String endMonthStr = (study.substring(37, 39));
        String endDateStr = (study.substring(40, 42));
        String endHourStr = (study.substring(58, 60));
        String endMinuteStr = (study.substring(49, 51));
        String endSecondStr = (study.substring(55, 57));

        int startYear = Integer.parseInt(startYearStr, 16);
        int startMonth = Integer.parseInt(startMonthStr, 16);
        int startDate = Integer.parseInt(startDateStr, 16);
        int startHour = Integer.parseInt(startHourStr, 16);
        int startMinute = Integer.parseInt(startMinuteStr, 16);
        int startSecond = Integer.parseInt(startSecondStr, 16);

        int endYear = Integer.parseInt(endYearStr, 16);
        int endMonth = Integer.parseInt(endMonthStr, 16);
        int endDate = Integer.parseInt(endDateStr, 16);
        int endHour = Integer.parseInt(endHourStr, 16);
        int endMinute = Integer.parseInt(endMinuteStr, 16);
        int endSecond = Integer.parseInt(endSecondStr, 16);

        startYear += 2000;  // Nonin stores years as 1 byte, e.g. 0x12 = 18 = 2018
        endYear += 2000;

        startMonth -= 1;  // Nonin stores months starting from 1 (January), Android Calendar class from 0 (January)
        endMonth -=1;

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, startYear);
        startTime.set(Calendar.MONTH, startMonth);
        startTime.set(Calendar.DATE, startDate);
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.MINUTE, startMinute);
        startTime.set(Calendar.SECOND, startSecond);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.YEAR, endYear);
        endTime.set(Calendar.MONTH, endMonth);
        endTime.set(Calendar.DATE, endDate);
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endMinute);
        endTime.set(Calendar.SECOND, endSecond);

        Log.d(TAG, "STUDY START " + startHour + ":" + startMinute + ":" + startSecond + "  date " + startDate+  "/" + (startMonth+1) + "/" + startYear + " " + startTime.getTimeInMillis() + "  END   " + endHour + ":" + endMinute + ":" + endSecond + "  date " + endDate + "/" + (endMonth+1) + "/" + endYear + " " + endTime.getTimeInMillis() + " secondsPerSample:" + secondsPerSample);

        this_study.start_time = startTime.getTimeInMillis();
        this_study.end_time = endTime.getTimeInMillis();

        this_study.interval_between_playback_samples = secondsPerSample * 1000;

        ArrayList<SpO2AndPulse> readings = new ArrayList<>();

        // TODO : need to handle pulse rate compression for rates > 200
        // 3150 Memory Playback Document 110520-000 REV 8.pdf page 9 section 4.3

        // Read the study to obtain every pulse, SpO2 and checksum value in the study which are 1 byte each, i.e. 3 bytes, i.e. 9 characters in total (3 x hex MSB, hex LSB, space)

        for (int j = 91; j < study.length(); j += 9)
        {
            String pulseHex = study.substring(j, j + 2);
            int pulse = Integer.parseInt(pulseHex, 16);

            // Because pulse rate can be > 255 i.e. > than an 8-bit value can store, compression is used on pulse rates > 200 bpm
            // 3150 Memory Playback Document 110520-000 REV B.pdf Sheet 9 of 11
            // ** UNTESTED ** - are pulses required from Nonin?
            if (pulse > 200)
            {
                // Pulse rate values of 201-300 bpm are compressed
                if (pulse < 251)
                {
                    int realPulse = pulse - 200;
                    realPulse = 200 + (2 * realPulse);
                    pulse = realPulse;
                }

                // According to playback document, a value of 251 represents a pulse of > 300 bpm
                if (pulse == 251)
                {
                    pulse = 300;
                }

                // Missing data
                if (pulse == 255)
                {
                    pulse = -1;
                }
            }

            //int pulse =  -1;

            String spo2Hex = study.substring(j + 3, j + 5);
            int spo2= Integer.parseInt(spo2Hex, 16);

            // a SpO2 reading of 255 is actually 0xff, i.e. Missing data so mark as SpO2Processor INVALID_MEASUREMENT, i.e. -1
            if (spo2 == 255)
            {
                spo2 = -1;
            }

            readings.add(new SpO2AndPulse(spo2, pulse));
        }

        this_study.readings = readings;

        all_studies.add(this_study);

        Log.d(TAG, "  just added a study with " + readings.size() + " readings, number of studies: " + all_studies.size());

        // DEBUG STUFF
/*        String debugStr ="pulse readings for this study:";
        for (int i=0; i < pulseReadingsReversed.size(); i++)
        {
            debugStr += pulseReadingsReversed.get(i) + " ";
        }
        Log.d("-pr", "  <3 " + debugStr + " <3");*/
    }


    public void sendNext()
    {
        Log.d(TAG, "sendNext has " + readingsQueue.size() + " readings, study_to_send_index:" + study_to_send_index + " time_of_last_reading:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(time_of_last_reading));

        int sentSpos = 0;

        ArrayList<IntermediateSpO2> sentSpo2s = new ArrayList<>();

        while (sentSpos < READINGS_TO_SEND_PER_TICK && !readingsQueue.isEmpty())
        {
            SpO2AndPulse reading = readingsQueue.poll();

            if (mTimestamp_in_ms > time_of_last_reading || debug_test_send_all)
            {
                if (reading != null)
                {
                    sentSpo2s.add(new IntermediateSpO2(reading.SpO2, reading.Pulse, mTimestamp_in_ms));

                    // Commented out log because logging every reading can overwhelm logger
                    //Log.d(TAG, " SENDING because timestamp of reading is" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(mTimestamp_in_ms ) + " and t_o_l_r is " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(time_of_last_reading ) + " " + reading.SpO2);

                    sentSpos++;
                }
            }
            else
            {
                // Commented out log because logging every reading can overwhelm logger
                //Log.d(TAG, " Not sending because timestamp of reading is" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(mTimestamp_in_ms ) + " and t_o_l_r is " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(time_of_last_reading ) + " " + reading.SpO2);
            }

            mTimestamp_in_ms -= interval_between_playback_samples;
        }

        Bundle spos2Bundle = new Bundle();
        spos2Bundle.putParcelableArrayList(NONIN_WRIST_OX__READINGS_FROM_PLAYBACK, sentSpo2s);

        Intent intent = new Intent();
        intent.setAction(NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK);
        intent.putExtras(spos2Bundle);

        gateway_context_interface.sendBroadcastIntent(intent);

        Log.d(TAG, " >> sent ! <<  remaining " + readingsQueue.size() + " sending?" + sending);
    }


    private void makeNewTimer()
    {
        NoninPlaybackStudy study = all_studies.get(study_to_send_index);

        interval_between_playback_samples = study.interval_between_playback_samples;

        Log.d(TAG, "making new timer for study, index:" + study_to_send_index + " start_time:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(study.start_time)  + " end_time:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(study.start_time) +" readings:" + study.readings.size() + " interval:" + interval_between_playback_samples);

        readingsQueue = new ConcurrentLinkedQueue<>();
        readingsQueue.addAll(study.readings);

        mTimestamp_in_ms = study.end_time;

        nonin_playback_decoder_timer = new Timer("nonin_playback_decoder_timer");
        nonin_playback_decoder_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                sendNext();

                if (readingsQueue.size() == 0 )
                {
                    sending = false;

                    Log.d(TAG, "finished sending that study to Gateway so cancelling timer study_to_send_index:" + study_to_send_index);

                    nonin_playback_decoder_timer.cancel();

                    GenericStartStopTimer.cancelTimer(nonin_playback_decoder_timer, Log);
                }
            }
        }, (int)DateUtils.SECOND_IN_MILLIS, (int)DateUtils.SECOND_IN_MILLIS);
    }


    private void sendStudy(int index)
    {
        sending = true;

        try
        {
            makeNewTimer();
        }
        // This exception has happened once when the batteries were removed during playback
        catch(IndexOutOfBoundsException e)
        {
            Log.d(TAG, "Error IndexOutOfBoundsException index:" + " size of all_studies:" + all_studies.size());
            Log.e(TAG, "Error IndexOutOfBoundsException index:" + " size of all_studies:" + all_studies.size());
        }
    }


    private void startSendingStudiesToGateway()
    {
        Log.d(TAG, "startSendingStudiesToGateway (set study_to_send_index to 0)");

        informGatewayThatNoninPlaybackIsExpected();

        study_to_send_index = 0;

        if (all_studies.size() > 0)
        {
            sendStudy(study_to_send_index);
        }
    }


    public void decodePlayback(String playbackOnlyAsString)
    {
        // Find somewhere else to do this that doesn't 1. crash (constructor)  2. needlessly repeat creation (here)
        gateway_context_interface.getAppContext().registerReceiver(broadcast_receiver, new IntentFilter(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE));

        all_studies = new ArrayList<>();

        int locn = playbackOnlyAsString.length();  // The end of the playback data without the end of data marker (18 zeroes as defined in Nonin Model 3150 Memory Playback Document 3150 Memory Playback Document 110520-000 REV B.pdf

        // TODO: implications of "The byte count can overflow, and the number may be verified by taking the modulus of the actual bytes count and 65536."  MemPlaybackDoc p.10
        // Unless otherwise specified, the byte in the checksum field will always be the 8-bit result of adding Byte 0 to Byte 1
        try {
            // Claimed bytecount
            String claimedBytecountAsHex = playbackOnlyAsString.substring(locn - 6, locn - 1);

            Log.d(TAG, "CLAIMED BYTECOUNT got : <" + claimedBytecountAsHex + ">");

            String msb = claimedBytecountAsHex.substring(0, 2);
            String lsb = claimedBytecountAsHex.substring(3, 5);

            long claimedByteCount;

            claimedByteCount = Long.parseLong(msb, 16) * 256;
            claimedByteCount += Long.parseLong(lsb, 16);

            if (claimedByteCount > 65535)
            {
                Log.d(TAG, "(bytecount overflow)");

                claimedByteCount = claimedByteCount % 65536;
            }

            Log.d(TAG, "pBlength " + playbackOnlyAsString.length());

            // Now the checksum
            String claimedChecksumStr = playbackOnlyAsString.substring(locn - 15, locn - 10);
            msb = claimedChecksumStr.substring(0, 2);
            lsb = claimedChecksumStr.substring(3, 5);

            Log.d(TAG, "claimed checksum <" + claimedChecksumStr + ">" + "  msb:<" + msb + ">  lsb:<" + lsb + "<");

            long claimedChecksum;
            claimedChecksum = Long.parseLong(msb, 16) * 256;
            claimedChecksum += Long.parseLong(lsb, 16);

            long actualChecksum = 0;
            int startOfPlaybackBytes = playbackOnlyAsString.indexOf("06 FE FD FB") + 3;

            Log.d(TAG, "startOfPlaybackBytes 06 FE FD FB:" + startOfPlaybackBytes);

            for (int i = startOfPlaybackBytes; i < locn - 18; i += 3)
            {
                String hex = playbackOnlyAsString.substring(i, i + 2);

                actualChecksum += Long.parseLong(hex, 16);
            }

            if (actualChecksum > 65535)  // Checksum has overflowed, i.e. is a number too large to be represented by 2 bytes, i.e. > 65535
            {
                Log.d(TAG, "(checksum overflow)");

                actualChecksum = actualChecksum % 65536;
            }

            String[] studies = playbackOnlyAsString.split("FE FD FB"); // Studies are separated by FE FD FB

            Log.d(TAG, "CHECKSUM claimed:" + claimedChecksum + " calculated:" + actualChecksum + " BYTECOUNT claimed " + claimedByteCount + " counted ??????"  + " studies:" + (studies.length-1));

            if (studies.length > 1)
            {
                for (int i = studies.length - 2; i > 0; i--)   // The final study is actually the Closing Header, i.e. not patient readings.
                {
                    Log.d(TAG, "decoding the studies...");

                    decodeStudy(studies[i]);
                }
            }

            // All studies decoded for this playback so
            Log.d(TAG, " All studies decoded " + all_studies.size());

            if (all_studies.size() > 0)
            {
                startSendingStudiesToGateway();
            }
            else
            {
                // This can happen e.g. if batteries fail while recording the first study
                // Also happened once when a 5 min study was followed by a 13 hr study and Nonin took longer than expected to send its playback,
                // causing 2 playback commands to be sent, one minute after the other, confusing the decoder.
                Log.d(TAG, "decodePlayback() did not find any studies playback_session_was_started = false");

                playback_session_was_started = false;

                informGatewayThatNoninSetupModeIsNotBlocked();
            }

            Log.d(TAG, "Unblocking Nonin setup mode from decodePlayback()");

        }
        catch(Exception ex)
        {
            Log.d(TAG, "exception " + ex.toString());

            Log.d(TAG, "Unblocking Nonin setup mode from decodePlayback() exception");

            playback_session_was_started = false;

            informGatewayThatNoninSetupModeIsNotBlocked();
        }
    }
}
