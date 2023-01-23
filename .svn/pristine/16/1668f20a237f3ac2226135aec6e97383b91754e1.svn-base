package com.isansys.patientgateway;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.format.DateUtils;

import com.isansys.common.ErrorCodes;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.PatientPositionOrientation;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.HeartBeatInfo.ActivityLevel;
import com.isansys.patientgateway.algorithms.Algorithms;
import com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType;
import com.isansys.patientgateway.bluetooth.SpO2.NoninWristOx;
import com.isansys.patientgateway.bluetooth.bloodPressure.AnD_UA767;
import com.isansys.patientgateway.bluetooth.bloodPressure.And_UA767_MeasurementStatus;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UC352BLE.BluetoothLe_AnD_UC352BLE;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetemp.BluetoothLeLifetempV2;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetouch.BluetoothLeLifetouch;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.NoninWristOxBtle.BluetoothLeNoninWristOx;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.isansys.common.DeviceInfoConstants.DUMMY_FIRMWARE_VERSION;

public class DummyDataMode
{
    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    private final String TAG = "DummyDataMode";

    private final Handler dummy_data_handler = new Handler();

    private boolean simulate_lifetouch_setup_mode = false;
    private boolean simulate_pulse_ox_setup_mode = false;

    private boolean simulate_lifetouch_raw_accelerometer_mode = false;

    public boolean spoof_early_warning_scores = false;
    
    private int number_of_simulated_measurements_per_tick = 1;

    private boolean enable_backfill = false;
    private int number_of_hours_to_backfill = 1;

    private final Settings settings;
    private final DeviceInfoManager device_info_manager;

    static private int dummy_data_timer_tick_counter = 0;

    private final PatientGatewayInterface patient_gateway_interface;


    public DummyDataMode(ContextInterface context_interface, RemoteLogging logger, Settings passed_settings, DeviceInfoManager manager, PatientGatewayInterface patient_gateway_interface)
    {
        gateway_context_interface = context_interface;
        Log = logger;
        settings = passed_settings;
        device_info_manager = manager;
        this.patient_gateway_interface = patient_gateway_interface;
    }


    public boolean isDummyDataModeEnabled()
    {
        return  device_info_manager.isDummyDataModeEnabled();
    }


    public boolean isDummyDataModeBackFillEnabled()
    {
        return enable_backfill;
    }


    public int getNumberOfHoursToBackfill()
    {
        return number_of_hours_to_backfill;
    }


    public void setNumberOfHoursToBackfill(int hours)
    {
        number_of_hours_to_backfill = hours;
        Log.d(TAG, "number_of_hours_to_backfill = " + number_of_hours_to_backfill);
    }


    private void makeDummyPulseOxSpotMeasurementAndSendAsIntent(DeviceInfo device_info, long time_in_ms)
    {
        boolean simulate_pulse_ox_finger_off = device_info.simulate_device_disconnected_from_body;

        boolean valid_reading = !simulate_pulse_ox_finger_off;

        boolean low_battery_condition;

        switch (device_info.device_type)
        {
            case DEVICE_TYPE__NONIN_WRIST_OX:
            {
                low_battery_condition = dummy_pulse_ox_battery_value < 10;

                Intent intent = new Intent();
                intent.setAction(NoninWristOx.NONIN_WRIST_OX__NEW_MEASUREMENT_DATA);
                intent.putExtra(NoninWristOx.NONIN_WRIST_OX__VALID_READING, valid_reading);
                intent.putExtra(NoninWristOx.NONIN_WRIST_OX__HEART_RATE, dummy_pulse_ox_pulse);
                intent.putExtra(NoninWristOx.NONIN_WRIST_OX__SP_O2, dummy_pulse_wrist_ox_sp_o2);
                intent.putExtra(NoninWristOx.NONIN_WRIST_OX__TIMESTAMP, time_in_ms);
                intent.putExtra(NoninWristOx.NONIN_WRIST_OX__LOW_BATTERY, low_battery_condition);
                intent.putExtra(NoninWristOx.NONIN_WRIST_OX__DETACHED_FROM_PATIENT, simulate_pulse_ox_finger_off);
                //intent.putExtra(NoninWristOx.NONIN_WRIST_OX__ARTIFACT, artifact);
                //intent.putExtra(NoninWristOx.NONIN_WRIST_OX__OUT_OF_TRACK, out_of_track);
                //intent.putExtra(NoninWristOx.NONIN_WRIST_OX__LOW_PERFUSION, low_perfusion);
                //intent.putExtra(NoninWristOx.NONIN_WRIST_OX__MARGINAL_PERFUSION, marginal_perfusion);
                //intent.putExtra(NoninWristOx.NONIN_WRIST_OX__SMART_POINT_ALGORITHM, smartpoint_algorithm);
                //context.sendBroadcast(intent);

                intent.putExtra("device_type", device_info.device_type.ordinal());
                LocalBroadcastManager.getInstance(gateway_context_interface.getAppContext()).sendBroadcast(intent);
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
            {
                Intent intent = new Intent();
                intent.setAction(BluetoothLeNoninWristOx.ACTION_DATA_AVAILABLE);
                intent.putExtra(BluetoothLeNoninWristOx.DATA_TYPE, BluetoothLeNoninWristOx.DATATYPE_SPO2_MEASUREMENT);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__VALID_READING, valid_reading);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__SPO2, dummy_pulse_wrist_ox_sp_o2);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE, dummy_pulse_ox_pulse);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__COUNTER, counter);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP, time_in_ms);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX, 0);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__BATTERY_VOLTAGE, dummy_pulse_ox_battery_value);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE, dummy_pulse_ox_battery_value);

                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION, true);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_BATTERY, false);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, simulate_pulse_ox_finger_off);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, false);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM, true);
                intent.putExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL, false);
                intent.putExtra(BluetoothLeNoninWristOx.DATA_AS_STRING, "DUMMY DATA AS STRING");
                intent.putExtra(BluetoothLeNoninWristOx.DEVICE_ADDRESS, "DUMMY DATA DEVICE MAC ADDRESS");

                sendIntent(intent, device_info.device_type);
            }
            break;
        }
    }


    private void backfillWithDummyDataLifetouchHeartBeatsIfRequired(long backfilled_session_start_time, int number_of_seconds_to_backfill)
    {
        Log.d(TAG, "backfillWithDummyDataLifetouchHeartBeatsIfRequired start");

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            if (device_info.dummy_data_mode)
            {
                ArrayList<HeartBeatInfo> heart_beat_list = new ArrayList<>();

                long heart_beat_timestamp = backfilled_session_start_time;

                int minute_counter = 0;

                for (int i = 0; i < number_of_seconds_to_backfill; i++)
                {
                    heart_beat_list.add(makeDummyDataLifetouchHeartBeat(heart_beat_timestamp));

                    heart_beat_timestamp += (int)DateUtils.SECOND_IN_MILLIS;

                    minute_counter++;
                    if (minute_counter == 60)
                    {
                        minute_counter = 0;

                        // Make a fake battery measurement
                        dummyLifetouchMinuteEvent(device_info, heart_beat_timestamp);
                    }
                }

                // Simulate the Lifetouch as much as possible by pretending to be the BluetoothLeServiceLifetouch service sending an intent to PatientGatewayService
                sendHeartBeatListAsIntent(device_info.device_type, heart_beat_list);
            }
        }

        Log.d(TAG, "backfillWithDummyDataLifetouchHeartBeatsIfRequired end");
    }


    private void backfillWithDummyDataLifetempMeasurementsIfRequired(long backfilled_session_start_time, int number_of_seconds_to_backfill)
    {
        Log.d(TAG, "backfillWithDummyDataLifetempMeasurementsIfRequired start");

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            if (device_info.dummy_data_mode)
            {
                long measurement_timestamp = backfilled_session_start_time;

                for (int i = 0; i < number_of_seconds_to_backfill; i = i + 60)
                {
                    dummyLifetempMinuteEvent(device_info, measurement_timestamp);

                    measurement_timestamp += DateUtils.MINUTE_IN_MILLIS;
                }
            }
        }

        Log.d(TAG, "backfillWithDummyDataLifetempMeasurementsIfRequired end");
    }


    private void backfillWithDummyDataPulseOxSpotMeasurementsIfRequired(long backfilled_session_start_time, int number_of_seconds_to_backfill)
    {
        Log.d(TAG, "backfillWithDummyDataPulseOxSpotMeasurementsIfRequired start");

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            if (device_info.dummy_data_mode)
            {
                long spot_measurement_timestamp = backfilled_session_start_time;

                int minute_counter = 0;

                for (int i = 0; i < number_of_seconds_to_backfill; i++)
                {
                    makeDummyPulseOxSpotMeasurementAndSendAsIntent(device_info, spot_measurement_timestamp);

                    spot_measurement_timestamp += (int)DateUtils.SECOND_IN_MILLIS;

                    minute_counter++;
                    if (minute_counter == 60)
                    {
                        minute_counter = 0;

                        dummyPulseOxMinuteEvent();
                    }
                }
            }
        }

        Log.d(TAG, "backfillWithDummyDataPulseOxSpotMeasurementsIfRequired end");
    }


    private void backfillWithDummyDataBloodPressureMeasurementsIfRequired(long backfilled_session_start_time, int number_of_seconds_to_backfill)
    {
        Log.d(TAG, "backfillWithDummyDataBloodPressureMeasurementsIfRequired start");

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            if (device_info.dummy_data_mode)
            {
                long measurement_timestamp = backfilled_session_start_time;

                for (int i = 0; i < number_of_seconds_to_backfill; i = i + 60)
                {
                    dummyBloodPressureMinuteEvent(device_info, measurement_timestamp);

                    measurement_timestamp += DateUtils.MINUTE_IN_MILLIS;
                }
            }
        }

        Log.d(TAG, "backfillWithDummyDataBloodPressureMeasurementsIfRequired end");
    }


    private void backfillWithDummyDataEarlyWarningScoreMeasurementsIfRequired(long backfilled_session_start_time, int number_of_seconds_to_backfill)
    {
        Log.d(TAG, "backfillWithDummyDataEarlyWarningScoreMeasurementsIfRequired start");

        if (device_info_manager.isEarlyWarningScoreDevicePartOfSession())
        {
            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);
            if ((device_info.dummy_data_mode) && (spoof_early_warning_scores))
            {
                long measurement_timestamp = backfilled_session_start_time;

                for (int i = 0; i < number_of_seconds_to_backfill; i = i + 60)
                {
                    dummyEarlyWarningScoreMinuteEvent(device_info, measurement_timestamp);

                    measurement_timestamp += DateUtils.MINUTE_IN_MILLIS;
                }
            }
        }

        Log.d(TAG, "backfillWithDummyDataEarlyWarningScoreMeasurementsIfRequired end");
    }


    // If Backfill is enabled, this function gets called at the end of createNewSession.
    // This goes back X hours and fills in fake dummy data
    public void backfillWithDummyData(long time_now_in_milliseconds)
    {
        int number_of_minutes_to_backfill = number_of_hours_to_backfill * 60;
        int number_of_seconds_to_backfill = number_of_minutes_to_backfill * 60;

        long backfilled_session_start_time = time_now_in_milliseconds - ((long) number_of_seconds_to_backfill * (int)DateUtils.SECOND_IN_MILLIS);

        Log.d(TAG, "backfillWithDummyData : number_of_hours_to_backfill = " + number_of_hours_to_backfill + " starting from " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(backfilled_session_start_time));

        backfillWithDummyDataLifetouchHeartBeatsIfRequired(backfilled_session_start_time, number_of_seconds_to_backfill);
        backfillWithDummyDataLifetempMeasurementsIfRequired(backfilled_session_start_time, number_of_seconds_to_backfill);
        backfillWithDummyDataPulseOxSpotMeasurementsIfRequired(backfilled_session_start_time, number_of_seconds_to_backfill);
        backfillWithDummyDataBloodPressureMeasurementsIfRequired(backfilled_session_start_time, number_of_seconds_to_backfill);
        backfillWithDummyDataEarlyWarningScoreMeasurementsIfRequired(backfilled_session_start_time, number_of_seconds_to_backfill);

        Log.d(TAG, "backfillWithDummyData finished");
    }


    public void enableBackfill(boolean enabled)
    {
        enable_backfill = enabled;

        // If backfill enabled == true, set disable heart beat processing log lines to true
        settings.storeDisableCommentsForSpeed(enabled);
    }


    public void tick()
    {
        dummy_data_handler.post(() -> {
            dummy_data_timer_tick_counter--;

            Log.d(TAG, "Running. Runnables pending = " + dummy_data_timer_tick_counter);

            long time_now_in_milliseconds = patient_gateway_interface.getNtpTimeNowInMilliseconds();

            dummyDataTimerTick(time_now_in_milliseconds);
        });
        
        dummy_data_timer_tick_counter++;
        
        Log.d(TAG, "Queued : Runnables pending = " + dummy_data_timer_tick_counter);
    }

    
    public void setNumberOfSimulatedMeasurementsPerTick(int desired_number)
    {
        number_of_simulated_measurements_per_tick = desired_number;
    }


    public void enableDummyDeviceSetupMode(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                if(!simulate_lifetouch_setup_mode)
                {
                    dummyLifetouchSetupModeEnable(device_info.device_type);
                }
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                if(!simulate_pulse_ox_setup_mode)
                {
                    dummyPulseOxSetupModeEnable(device_info.device_type);
                }
            }
            break;
        }
    }


    public void enableDummyDeviceRawAccelerometerMode(DeviceInfo device_info)
    {
        if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
        {
            if(!simulate_lifetouch_raw_accelerometer_mode)
            {
                dummyLifetouchRawAccelerometerModeEnable(device_info.device_type);
            }
        }
    }


    public void stopDeviceSetupMode(SensorType sensor_type)
    {
        switch (sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                disableLifetouchDummyDataSetupMode();
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                disablePulseOxDummyDataSetupMode();
            }
            break;
        }
    }


    public void stopDeviceRawAccelerometerMode(SensorType sensor_type)
    {
        if (sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
        {
            disableLifetouchDummyDataRawAccelerometerMode();
        }
    }


    private void dummyLifetouchSetupModeEnable(final DeviceType device_type)
    {
        simulate_lifetouch_setup_mode = true;

        GenericStartStopTimer.cancelTimer(lifetouch_setup_mode_generator_timer, Log);
        lifetouch_setup_mode_generator_timer = new Timer();

        // Runs every 10mS = 100 Hz Setup Mode simulation
        lifetouch_setup_mode_generator_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                long end_timestamp = patient_gateway_interface.getNtpTimeNowInMilliseconds();
                ArrayList<MeasurementSetupModeDataPoint> measurements = new ArrayList<>();

                for(int i = 0; i < 10; i++)
                {
                    int setup_mode_sample = simulated_setup_mode_data[simulated_lifetouch_setup_mode_data_sample_number];
                    long setup_mode_timestamp = end_timestamp - ((9 - i) * 10);

                    measurements.add(new MeasurementSetupModeDataPoint((short) setup_mode_sample, setup_mode_timestamp));

                    if ((simulated_lifetouch_setup_mode_data_sample_number + 1) < TABLE_SIZE)
                    {
                        simulated_lifetouch_setup_mode_data_sample_number++;
                    }
                    else
                    {
                        simulated_lifetouch_setup_mode_data_sample_number = 0;
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(BluetoothLeLifetouch.SETUP_MODE_DATA, measurements);

                Intent intent = new Intent(BluetoothLeLifetouch.ACTION_DATA_AVAILABLE);
                intent.putExtra(BluetoothLeLifetouch.DATA_TYPE, BluetoothLeLifetouch.DATATYPE_SETUP_MODE_RAW_SAMPLES);
                intent.putExtra(BluetoothLeLifetouch.DATA_AS_STRING, "DUMMY DATA AS STRING");
                intent.putExtra(BluetoothLeLifetouch.DEVICE_ADDRESS, "DUMMY DATA DEVICE MAC ADDRESS");
                intent.putExtras(bundle);
                sendIntent(intent, device_type);
            }
        }, 0, LIFETOUCH_DATA_SEND_INTERVAL);
    }
    
    
    private void dummyPulseOxSetupModeEnable(final DeviceType device_type)
    {
        simulate_pulse_ox_setup_mode = true;

        GenericStartStopTimer.cancelTimer(pulse_ox_setup_mode_generator_timer, Log);
        pulse_ox_setup_mode_generator_timer = new Timer();

        switch (device_type)
        {
            case DEVICE_TYPE__NONIN_WRIST_OX:         // Runs every 10mS = 100 Hz Setup Mode simulation
            {
                pulse_ox_setup_mode_generator_timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        short setup_mode_sample = (short) ((simulated_setup_mode_data[simulated_pulse_ox_setup_mode_data_sample_number]) / 16);
                        long setup_mode_timestamp = patient_gateway_interface.getNtpTimeNowInMilliseconds();

                        Intent intent = new Intent(NoninWristOx.NONIN_WRIST_OX__NEW_SETUP_DATA_SAMPLE);
                        intent.putExtra(NoninWristOx.NONIN_WRIST_OX__SETUP_MODE_SAMPLE, setup_mode_sample);
                        intent.putExtra(NoninWristOx.NONIN_WRIST_OX__SETUP_MODE_TIMESTAMP, setup_mode_timestamp);
                        LocalBroadcastManager.getInstance(gateway_context_interface.getAppContext()).sendBroadcast(intent);

                        if ((simulated_pulse_ox_setup_mode_data_sample_number + 1) < TABLE_SIZE)
                        {
                            simulated_pulse_ox_setup_mode_data_sample_number++;
                        }
                        else
                        {
                            simulated_pulse_ox_setup_mode_data_sample_number = 0;
                        }
                    }
                }, 0, OLD_NONIN_DATA_SEND_INTERVAL);
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:         // Runs every 100mS, sending 10 samples per time = 100 Hz Setup Mode simulation
            {
                pulse_ox_setup_mode_generator_timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        long end_timestamp = patient_gateway_interface.getNtpTimeNowInMilliseconds();
                        ArrayList<MeasurementSetupModeDataPoint> measurements = new ArrayList<>();

                        for(int i = 0; i < 10; i++)
                        {
                            int setup_mode_sample = simulated_setup_mode_data_16_bit[simulated_pulse_ox_setup_mode_data_sample_number];
                            long setup_mode_timestamp = end_timestamp - ((9 - i) * 10);

                            measurements.add(new MeasurementSetupModeDataPoint(setup_mode_sample, setup_mode_timestamp));

                            if ((simulated_pulse_ox_setup_mode_data_sample_number + 1) < TABLE_SIZE)
                            {
                                simulated_pulse_ox_setup_mode_data_sample_number++;
                            }
                            else
                            {
                                simulated_pulse_ox_setup_mode_data_sample_number = 0;
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(BluetoothLeNoninWristOx.PPG_DATA_POINTS, measurements);

                        Intent intent = new Intent(BluetoothLeNoninWristOx.ACTION_DATA_AVAILABLE);
                        intent.putExtra(BluetoothLeNoninWristOx.DATA_TYPE, BluetoothLeNoninWristOx.DATATYPE_PPG_DATA);
                        intent.putExtra(BluetoothLeNoninWristOx.DATA_AS_STRING, "DUMMY DATA AS STRING");
                        intent.putExtra(BluetoothLeNoninWristOx.DEVICE_ADDRESS, "DUMMY DATA DEVICE MAC ADDRESS");
                        intent.putExtras(bundle);
                        sendIntent(intent, device_type);
                    }
                }, 0, LIFETOUCH_DATA_SEND_INTERVAL);
            }
            break;
        }
    }

    
    private void dummyLifetouchRawAccelerometerModeEnable(final DeviceType device_type)
    {
        simulate_lifetouch_raw_accelerometer_mode = true;

        GenericStartStopTimer.cancelTimer(lifetouch_raw_accelerometer_mode_generator_timer, Log);
        lifetouch_raw_accelerometer_mode_generator_timer = new Timer();

        // Runs every 10mS = 100 Hz Raw Accelerometer Mode simulation
        lifetouch_raw_accelerometer_mode_generator_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                short[] x_axis_samples = new short[10];
                short[] y_axis_samples = new short[10];
                short[] z_axis_samples = new short[10];
                long[] timestamps = new long[10];

                long end_timestamp = patient_gateway_interface.getNtpTimeNowInMilliseconds();

                for(int i = 0; i < 10; i++)
                {
                    x_axis_samples[i] = (short) simulated_raw_accelerometer_x_axis_data[simulated_lifetouch_raw_accelerometer_mode_data_sample_number];
                    y_axis_samples[i] = (short) simulated_raw_accelerometer_y_axis_data[simulated_lifetouch_raw_accelerometer_mode_data_sample_number];
                    z_axis_samples[i] = (short) simulated_raw_accelerometer_z_axis_data[simulated_lifetouch_raw_accelerometer_mode_data_sample_number];
                    timestamps[i] = end_timestamp - ((9 - i) * 10);

                    if ((simulated_lifetouch_raw_accelerometer_mode_data_sample_number + 1) < TABLE_SIZE)
                    {
                        simulated_lifetouch_raw_accelerometer_mode_data_sample_number++;
                    }
                    else
                    {
                        simulated_lifetouch_raw_accelerometer_mode_data_sample_number = 0;
                    }
                }

                Intent intent = new Intent(BluetoothLeLifetouch.ACTION_DATA_AVAILABLE);
                intent.putExtra(BluetoothLeLifetouch.DATA_TYPE, BluetoothLeLifetouch.DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES);
                intent.putExtra(BluetoothLeLifetouch.DATA_AS_STRING, "DUMMY DATA AS STRING");
                intent.putExtra(BluetoothLeLifetouch.DEVICE_ADDRESS, "DUMMY DATA DEVICE MAC ADDRESS");
                intent.putExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES, x_axis_samples);
                intent.putExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES, y_axis_samples);
                intent.putExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES, z_axis_samples);
                intent.putExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__TIMESTAMPS, timestamps);
                sendIntent(intent, device_type);
            }
        }, 0, LIFETOUCH_DATA_SEND_INTERVAL);
    }


    private void disableLifetouchDummyDataSetupMode()
    {
        GenericStartStopTimer.cancelTimer(lifetouch_setup_mode_generator_timer, Log);

        simulate_lifetouch_setup_mode = false;
        
        simulated_lifetouch_setup_mode_data_sample_number = 0;
    }

    
    private void disablePulseOxDummyDataSetupMode()
    {
        GenericStartStopTimer.cancelTimer(pulse_ox_setup_mode_generator_timer, Log);

        simulate_pulse_ox_setup_mode = false;

        simulated_pulse_ox_setup_mode_data_sample_number = 0;
    }


    private void disableLifetouchDummyDataRawAccelerometerMode()
    {
        GenericStartStopTimer.cancelTimer(lifetouch_raw_accelerometer_mode_generator_timer, Log);

        simulate_lifetouch_raw_accelerometer_mode = false;

        simulated_lifetouch_raw_accelerometer_mode_data_sample_number = 0;
    }


    // Setup mode data is updated every 10ms. Table size of 100 gives 1 second period of sine wave
    private static final int TABLE_SIZE = 100;
    private static final float TWO_PIE = (float) (2 * 3.1459);
    private static final float PHASE_INCREMENT = (TWO_PIE / TABLE_SIZE);
    private static final int OLD_NONIN_DATA_SEND_INTERVAL = 10;                                      // Run every 10mS = 100 Hz simulated setup mode data
    private static final int LIFETOUCH_DATA_SEND_INTERVAL = 100;
    
    private static int simulated_lifetouch_setup_mode_data_sample_number = 0;
    private static int simulated_pulse_ox_setup_mode_data_sample_number = 0;

    private static int simulated_lifetouch_raw_accelerometer_mode_data_sample_number = 0;

    private final int[] simulated_setup_mode_data = sineWaveGenerator(4096/2, 0);

    private final int[] simulated_setup_mode_data_16_bit = sineWaveGenerator(65536/2, 0);

    private final int[] simulated_raw_accelerometer_x_axis_data = sineWaveGenerator(256/2, 0);
    private final int[] simulated_raw_accelerometer_y_axis_data = sineWaveGenerator(256/2, 10);
    private final int[] simulated_raw_accelerometer_z_axis_data = sineWaveGenerator(256/2, 20);

    //static short sinewave[] = sawtoothGenerator();
    //short sinewave[] = fakeECGSignalGenerator();
    
    private static Timer lifetouch_setup_mode_generator_timer;
    private static Timer pulse_ox_setup_mode_generator_timer;

    private static Timer lifetouch_raw_accelerometer_mode_generator_timer;


    private int[] sineWaveGenerator(int max_amplitude, int phase_offset)
    {
        int[] sineWave = new int[TABLE_SIZE];
        float current_phase = (float) 0.00;
        for(int i = 0; i < TABLE_SIZE; i++)
        {
            sineWave[i] = (int) (max_amplitude + 1 * max_amplitude * (Math.sin(current_phase + phase_offset)));
            current_phase = current_phase + PHASE_INCREMENT;
        }
        return sineWave;
    }

/*
    static public short[] sawtoothGenerator()
    {
        short samples[] = new short[TABLE_SIZE];

        for(int i = 0; i < TABLE_SIZE; i++)
        {
            samples[i] = (short)i;
        }
        
        return samples;
    }
    
    // Makes a weird shape waveform
    static public short[] fakeECGSignalGenerator()
    {
        short SineWave_Sample[] = new short[TABLE_SIZE];
        
        for(int i = 0; i < TABLE_SIZE; i++)
        {
            if(i <= 59)
            {
                SineWave_Sample[i] = 2048;
            }
            else
            {
                if(i <= 69)
                {
                    SineWave_Sample[i] = (short) (SineWave_Sample[i-1] + 200);
                }
                else
                {
                    if(i <= 79)
                    {
                        SineWave_Sample[i] = (short) (SineWave_Sample[i-1] - 200);
                    }
                    else
                    {
                        SineWave_Sample[i] = 2048;
                    }
                }
            }
        }
        return SineWave_Sample;
    }
*/

    public void initVariables(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                initLifetouch();
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                initLifetemp();
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                initPulseOx();
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                initBloodPressure();
            }
            break;

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                initWeightScales();
            }
            break;

            case SENSOR_TYPE__ALGORITHM:
            {
                initEws();
            }
            break;

            default:
                break;
        }
    }


    private void initLifetouch()
    {
        dummy_lifetouch_heart_beat_amplitude = 200;
        previous_dummy_lifetouch_heart_beat_amplitude = -1;
        dummy_lifetouch_battery_value = 10;
        dummy_lifetouch_tag = 0;

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        device_info.simulate_device_disconnected_from_body = false;
        device_info.setDeviceFirmwareVersion(Integer.toString(DUMMY_FIRMWARE_VERSION), DUMMY_FIRMWARE_VERSION);

        simulate_lifetouch_setup_mode = false;
        simulate_lifetouch_raw_accelerometer_mode = false;
    }


    private void initLifetemp()
    {
        dummy_lifetemp_temperature = new BigDecimal("30.0");
        dummy_lifetemp_battery_value = 20;
        dummy_lifetemp_packet_id = 1;

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
        device_info.setDeviceFirmwareVersion(Integer.toString(DUMMY_FIRMWARE_VERSION), DUMMY_FIRMWARE_VERSION);
        device_info.simulate_device_disconnected_from_body = false;
    }


    private void initPulseOx()
    {
        dummy_pulse_ox_pulse = 10;
        dummy_pulse_wrist_ox_sp_o2 = 90;
        dummy_pulse_ox_battery_value = 10;

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        device_info.setDeviceFirmwareVersion(Integer.toString(DUMMY_FIRMWARE_VERSION), DUMMY_FIRMWARE_VERSION);
        device_info.simulate_device_disconnected_from_body = false;

        simulate_pulse_ox_setup_mode = false;
    }


    private void initBloodPressure()
    {
        dummy_blood_pressure_systolic = 40;
        dummy_blood_pressure_diastolic = 10;
        dummy_blood_pressure_heart_rate = 10;
        dummy_blood_pressure_battery_value = 10;

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        device_info.setDeviceFirmwareVersion(Integer.toString(DUMMY_FIRMWARE_VERSION), DUMMY_FIRMWARE_VERSION);
    }


    private void initWeightScales()
    {
        dummy_weight_scales_weight = new BigDecimal("70.0");
        dummy_weigh_scales_battery_percentage = 10;

        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        device_info.setDeviceFirmwareVersion(Integer.toString(DUMMY_FIRMWARE_VERSION), DUMMY_FIRMWARE_VERSION);
    }


    private void initEws()
    {
        dummy_early_warning_score_value = 0;
        dummy_max_possible = 15;
    }

    static private int dummy_lifetouch_heart_beat_amplitude;
    static private int previous_dummy_lifetouch_heart_beat_amplitude;
    static private int dummy_lifetouch_activity_level;
    static private int dummy_lifetouch_tag;
    static private int dummy_lifetouch_battery_value;

    static private int dummy_lifetouch_orientation = PatientPositionOrientation.ORIENTATION_UNKNOWN.ordinal();
    
    static private BigDecimal dummy_lifetemp_temperature;
    static private int dummy_lifetemp_battery_value;
    static private long dummy_lifetemp_packet_id;
    
    static private int dummy_pulse_ox_pulse;
    static private int dummy_pulse_wrist_ox_sp_o2;
    static private int dummy_pulse_ox_battery_value;
    
    static private int dummy_blood_pressure_systolic;
    static private int dummy_blood_pressure_diastolic;
    static private int dummy_blood_pressure_heart_rate;
    static private int dummy_blood_pressure_battery_value;

    static private BigDecimal dummy_weight_scales_weight;
    static private int dummy_weigh_scales_battery_percentage;

    static private int dummy_early_warning_score_value;
    static private int dummy_max_possible;
    static private int counter = 0;


    private HeartBeatInfo makeDummyDataLifetouchHeartBeat(long timestamp)
    {
        HeartBeatInfo heart_beat = new HeartBeatInfo();

        if(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).simulate_device_disconnected_from_body)
        {
            Log.e(TAG, "Leads off beat");
            heart_beat.setAmplitude(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF);
        }
        else
        {
            if (previous_dummy_lifetouch_heart_beat_amplitude == ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF)
            {
                Log.e(TAG, "Leads on beat");
                heart_beat.setAmplitude(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_ON);
            }
            else
            {
                heart_beat.setAmplitude(dummy_lifetouch_heart_beat_amplitude);

                // Edit these numbers to change the sawtooth ramp and length.
                dummy_lifetouch_heart_beat_amplitude += 20;
                if (dummy_lifetouch_heart_beat_amplitude >= 300)
                {
                    dummy_lifetouch_heart_beat_amplitude = 200;
                }
            }
        }

        previous_dummy_lifetouch_heart_beat_amplitude = heart_beat.getAmplitude();

        heart_beat.setTag(dummy_lifetouch_tag);
        heart_beat.setActivity(ActivityLevel.values()[dummy_lifetouch_activity_level]);
        heart_beat.setTimestampInMs(timestamp);

        dummy_lifetouch_activity_level++;
        {
            if (dummy_lifetouch_activity_level >= ActivityLevel.values().length)
            {
                dummy_lifetouch_activity_level = 0;
            }
        }

        dummy_lifetouch_tag++;
        if (dummy_lifetouch_tag >= 8192)
        {
            dummy_lifetouch_tag = 0;
        }

        return heart_beat;
    }


    // Break up an array list into many smaller parts of a specified size
    private <T> List<List<T>> splitArrayIntoSmallerListOfListsForIntent(ArrayList<T> list, int partition_size)
    {
        List<List<T>> partitions = new LinkedList<>();

        for (int i = 0; i < list.size(); i += partition_size)
        {
            partitions.add(list.subList(i, Math.min(i + partition_size, list.size())));
        }

        return partitions;
    }


    private void sendHeartBeatListAsIntent(DeviceType device_type, ArrayList<HeartBeatInfo> heart_beat_list)
    {
        // Default dummy data mode HR is 60, so one a second. 3600 is an hours worth of heart beats at a time
        int partition_size = 3600;

        List<List<HeartBeatInfo>> partitions = splitArrayIntoSmallerListOfListsForIntent(heart_beat_list, partition_size);

        for (List<HeartBeatInfo> sub_list : partitions)
        {
            ArrayList<HeartBeatInfo> sub_heart_beat_list = new ArrayList<>(sub_list);

            // Simulate the Lifetouch as much as possible by pretending to be the BluetoothLeServiceLifetouch service sending an intent to PatientGatewayService
            final Intent intent = new Intent(BluetoothLeLifetouch.ACTION_DATA_AVAILABLE);
            intent.putExtra(BluetoothLeLifetouch.DATA_TYPE, BluetoothLeLifetouch.DATATYPE_BEAT_INFO);
            intent.putExtra(BluetoothLeLifetouch.DATA_AS_STRING, "Dummy Data Mode");
            intent.putParcelableArrayListExtra(BluetoothLeLifetouch.HEART_BEAT_INFO, sub_heart_beat_list);
            intent.putExtra(BluetoothLeLifetouch.HEART_BEATS_PENDING, 0);
            intent.putExtra(BluetoothLeLifetouch.DEVICE_ADDRESS, "Dummy Lifetouch Device");

            sendIntent(intent, device_type);
        }
    }


    private final ArrayList<HeartBeatInfo> heart_beat_list = new ArrayList<>();

    private void dummyDataTimerTick(long timestamp)
    {
        // Create dummy data for events that happen approx every second.
        DeviceInfo device_info;

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        if (device_info.isDeviceSessionInProgress())
        {
            if (device_info.dummy_data_mode)
            {
                int time_period_between_beats = ((int) DateUtils.SECOND_IN_MILLIS / number_of_simulated_measurements_per_tick);

                for (int i = 0; i < number_of_simulated_measurements_per_tick; i++)
                {
                    long heart_beat_timestamp = timestamp +
                            ((long) i * time_period_between_beats) +   // Offset to allow each beat to have a different timestamp if multiple beats per tick
                            (int)(time_period_between_beats * 0.25 * Math.random());    // random offset so that beats display on the poincare plot.

                    heart_beat_list.add(makeDummyDataLifetouchHeartBeat(heart_beat_timestamp));
                }

                if (device_info.isActualDeviceConnectionStatusConnected())
                {
                    // Simulate the Lifetouch as much as possible by pretending to be the BluetoothLeServiceLifetouch service sending an intent to PatientGatewayService
                    sendHeartBeatListAsIntent(device_info.device_type, heart_beat_list);
                    heart_beat_list.clear();
                }
                else
                {
                    Log.e(TAG, "Heart beats added to list, but not sent as pretending to be disconnected");
                }
            }
        }


        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        if (device_info.isDeviceSessionInProgress())
        {
            if (device_info.dummy_data_mode)
            {
                if(device_info.isActualDeviceConnectionStatusConnected())
                {
                    makeDummyPulseOxSpotMeasurementAndSendAsIntent(device_info, timestamp);
                }
            }
        }


        // Create dummy data for events that happen every minute
        counter++;
        if (counter == 60)
        {
            counter = 0;

            device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
            if (device_info.dummyDataModeRunningAndConnected())
            {
                // Make a fake battery measurement
                dummyLifetouchMinuteEvent(device_info, timestamp);
            }

            device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
            if (device_info.dummyDataModeRunningAndConnected())
            {
                dummyLifetempMinuteEvent(device_info, timestamp);
            }

            device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
            if (device_info.dummyDataModeRunningAndConnected())
            {
                dummyPulseOxMinuteEvent();
            }

            device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
            if (device_info.dummyDataModeRunningAndConnected())
            {
                dummyBloodPressureMinuteEvent(device_info, timestamp);
            }

            device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
            if (device_info.dummyDataModeRunningAndConnected())
            {
                dummyWeightScalesMinuteEvent(device_info, timestamp);
            }

            if (device_info_manager.isEarlyWarningScoreDevicePartOfSession())
            {
                if ((device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).dummy_data_mode) && (spoof_early_warning_scores))
                {
                    dummyEarlyWarningScoreMinuteEvent(device_info, timestamp);
                }
            }
        }
    }


    private void dummyLifetouchMinuteEvent(DeviceInfo device_info, long time_now_in_milliseconds)
    {
        // Simulate the Lifetouch as much as possible by pretending to be the BluetoothLeLifetouch sending an intent to PatientGatewayService
        final Intent battery_intent = new Intent(BluetoothLeLifetouch.ACTION_DATA_AVAILABLE);
        battery_intent.putExtra(BluetoothLeLifetouch.DATA_TYPE, BluetoothLeLifetouch.DATATYPE_BATTERY_LEVEL);
        battery_intent.putExtra(BluetoothLeLifetouch.DATA_AS_STRING, "Dummy Data Mode");
        battery_intent.putExtra(BluetoothLeLifetouch.BATTERY_PERCENTAGE, dummy_lifetouch_battery_value);
        battery_intent.putExtra(BluetoothLeLifetouch.BATTERY_VOLTAGE, dummy_lifetouch_battery_value);
        battery_intent.putExtra(BluetoothLeLifetouch.BATTERY_PERCENTAGE_TIMESTAMP, time_now_in_milliseconds);
        battery_intent.putExtra(BluetoothLeLifetouch.DEVICE_ADDRESS, "Dummy Lifetouch Device");
        sendIntent(battery_intent, device_info.device_type);

        dummy_lifetouch_battery_value++;
        if (dummy_lifetouch_battery_value >100)
        {
            dummy_lifetouch_battery_value = 1;
        }

        if(settings.getEnablePatientOrientation())
        {
            int amplitude = ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_BASE + dummy_lifetouch_orientation;

            HeartBeatInfo orientation_beat = makeDummyDataLifetouchHeartBeat(time_now_in_milliseconds);

            orientation_beat.setAmplitude(amplitude);

            heart_beat_list.add(orientation_beat);

            dummy_lifetouch_orientation++;
            if (dummy_lifetouch_orientation > PatientPositionOrientation.ORIENTATION_UPSIDE_DOWN.ordinal())
            {
                dummy_lifetouch_orientation = PatientPositionOrientation.ORIENTATION_UNKNOWN.ordinal();
            }
        }
    }

    
    private void dummyLifetempMinuteEvent(DeviceInfo device_info, long time_now_in_milliseconds)
    {
        DecimalFormat formatter = new DecimalFormat("00");

    	int temperature_integer;
    	int temperature_fraction = 0;
    	
        if(device_info.simulate_device_disconnected_from_body)
        {
        	temperature_integer = ErrorCodes.ERROR_CODE__LIFETEMP_LEADS_OFF;

            Log.e(TAG, "Dummy Lifetemp Finger Off");
        }
        else
        {
            temperature_integer = dummy_lifetemp_temperature.intValue();

            BigDecimal fractional_part = dummy_lifetemp_temperature.remainder(BigDecimal.ONE);
            temperature_fraction = fractional_part.multiply(new BigDecimal(100)).intValue();
		}

		int data_pending = 0;

        final Intent intent = new Intent(BluetoothLeLifetempV2.ACTION_DATA_AVAILABLE);
        intent.putExtra(BluetoothLeLifetempV2.DATA_TYPE, BluetoothLeLifetempV2.DATATYPE_TEMPERATURE_MEASUREMENT);
        intent.putExtra(BluetoothLeLifetempV2.DEVICE_ADDRESS, "Dummy Data Mode");
        intent.putExtra(BluetoothLeLifetempV2.DATA_AS_STRING, (formatter.format(temperature_integer) + "." + formatter.format(temperature_fraction) + "__" + time_now_in_milliseconds + "__" + dummy_lifetemp_battery_value + "__" + dummy_lifetemp_battery_value + "__" + data_pending));

        intent.putExtra(BluetoothLeLifetempV2.PACKET_ID, dummy_lifetemp_packet_id);



        sendIntent(intent, device_info.device_type);

        if(settings.getManufacturingModeEnabledStatus())
        {
            dummy_lifetemp_temperature = dummy_lifetemp_temperature.add(new BigDecimal("0.01"));
        }
        else
        {
            dummy_lifetemp_temperature = dummy_lifetemp_temperature.add(new BigDecimal("0.1"));
        }

        if (dummy_lifetemp_temperature.compareTo(new BigDecimal("40.0")) > 0)
        {
            dummy_lifetemp_temperature = new BigDecimal("30.0");
        }

        dummy_lifetemp_battery_value++;
        if (dummy_lifetemp_battery_value > 100)
        {
            dummy_lifetemp_battery_value = 1;
        }

        dummy_lifetemp_packet_id++;
        if (dummy_lifetemp_packet_id > 100)
        {
            dummy_lifetemp_packet_id = 1;
        }
    }


    private void dummyPulseOxMinuteEvent()
    {
        dummy_pulse_ox_pulse++;
        if (dummy_pulse_ox_pulse > 150)
        {
            dummy_pulse_ox_pulse = 30;
        }

        dummy_pulse_wrist_ox_sp_o2++;
        if (dummy_pulse_wrist_ox_sp_o2 > 100)
        {
            dummy_pulse_wrist_ox_sp_o2 = 50;
        }

        dummy_pulse_ox_battery_value++;
        if (dummy_pulse_ox_battery_value > 100)
        {
            dummy_pulse_ox_battery_value = 1;
        }
    }


    private void dummyBloodPressureMinuteEvent(DeviceInfo device_info, long time_now_in_milliseconds)
    {
        switch (device_info.device_type)
        {
            case DEVICE_TYPE__AND_UA767:
            {
                // Simulate the UA767 as much as possible by pretending to be the AnD_UA767 service sending an intent to PatientGatewayService
                final Intent intent = new Intent(AnD_UA767.NEW_DATA);
                intent.putExtra(AnD_UA767.SYSTOLIC, dummy_blood_pressure_systolic);
                intent.putExtra(AnD_UA767.DIASTOLIC, dummy_blood_pressure_diastolic);
                intent.putExtra(AnD_UA767.PULSE_RATE, dummy_blood_pressure_heart_rate);
                intent.putExtra(AnD_UA767.MEASUREMENT_STATUS, And_UA767_MeasurementStatus.Status.CORRECT_MEASUREMENT.ordinal());
                intent.putExtra(AnD_UA767.MEASUREMENT_TIME, time_now_in_milliseconds);
                if (dummy_blood_pressure_battery_value > 50)
                {
                    intent.putExtra(AnD_UA767.DEVICE_BATTERY_STATUS, AnD_UA767.BATTERY_GOOD);
                }
                else
                {
                    intent.putExtra(AnD_UA767.DEVICE_BATTERY_STATUS, AnD_UA767.BATTERY_BAD);
                }
                sendIntent(intent, device_info.device_type);
            }
            break;

            case DEVICE_TYPE__AND_ABPM_TM2441:
            case DEVICE_TYPE__AND_UA651:
            {
                // TODO
            }
            break;
        }

        dummy_blood_pressure_systolic++;
        if (dummy_blood_pressure_systolic > 250)
        {
            dummy_blood_pressure_systolic = 40;
        }

        dummy_blood_pressure_diastolic++;
        if (dummy_blood_pressure_diastolic > 100)
        {
            dummy_blood_pressure_diastolic = 10;
        }

        dummy_blood_pressure_heart_rate++;
        if (dummy_blood_pressure_heart_rate > 20)
        {
            dummy_blood_pressure_heart_rate = 10;
        }

        dummy_blood_pressure_battery_value++;
        if (dummy_blood_pressure_battery_value > 100)
        {
            dummy_blood_pressure_battery_value = 1;
        }
    }


    private void dummyWeightScalesMinuteEvent(DeviceInfo device_info, long time_now_in_milliseconds)
    {
        switch (device_info.device_type)
        {
            case DEVICE_TYPE__AND_UC352BLE:
            {
                Log.d(TAG, "dummyWeightScalesMinuteEvent");

                // Simulate the UC352BLE as much as possible by pretending to be the UC352BLE service sending an intent to PatientGatewayService

                final Intent intentWeight = new Intent(BluetoothLe_AnD_UC352BLE.ACTION_DATA_AVAILABLE);
                intentWeight.putExtra(BluetoothLe_AnD_UC352BLE.DATA_TYPE, BluetoothLe_AnD_UC352BLE.DATATYPE_WEIGHT_MEASUREMENT);
                intentWeight.putExtra(BluetoothLe_AnD_UC352BLE.DATA_AS_STRING, "DUMMY DATA AS STRING");
                intentWeight.putExtra(BluetoothLe_AnD_UC352BLE.DEVICE_ADDRESS, "DUMMY DATA DEVICE MAC ADDRESS");
                intentWeight.putExtra(BluetoothLe_AnD_UC352BLE.WEIGHT, dummy_weight_scales_weight.doubleValue());
                intentWeight.putExtra(BluetoothLe_AnD_UC352BLE.TIMESTAMP, time_now_in_milliseconds);
                sendIntent(intentWeight, device_info.device_type);

                final Intent intentBattery = new Intent(BluetoothLe_AnD_UC352BLE.ACTION_DATA_AVAILABLE);
                intentBattery.putExtra(BluetoothLe_AnD_UC352BLE.DATA_TYPE, BluetoothLe_AnD_UC352BLE.DATATYPE_BATTERY_LEVEL);
                intentBattery.putExtra(BluetoothLe_AnD_UC352BLE.DATA_AS_STRING, "DUMMY DATA AS STRING");
                intentBattery.putExtra(BluetoothLe_AnD_UC352BLE.DEVICE_ADDRESS, "DUMMY DATA DEVICE MAC ADDRESS");
                intentBattery.putExtra(BluetoothLe_AnD_UC352BLE.BATTERY_PERCENTAGE, dummy_weigh_scales_battery_percentage);
                intentBattery.putExtra(BluetoothLe_AnD_UC352BLE.BATTERY_TIMESTAMP, time_now_in_milliseconds);
                sendIntent(intentBattery, device_info.device_type);
            }
            break;
        }

        dummy_weight_scales_weight = dummy_weight_scales_weight.add(new BigDecimal("0.1"));
        if (dummy_weight_scales_weight.compareTo(new BigDecimal("100.0")) > 0)
        {
            dummy_weight_scales_weight = new BigDecimal("70.0");
        }

        dummy_weigh_scales_battery_percentage++;
        if (dummy_weigh_scales_battery_percentage > 100)
        {
            dummy_weigh_scales_battery_percentage = 1;
        }
    }
    
    private void dummyEarlyWarningScoreMinuteEvent(DeviceInfo device_info, long time_now_in_milliseconds)
    {
        Intent outgoing_intent = new Intent(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS);
        outgoing_intent.putExtra("measurement_type", AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal());
        outgoing_intent.putExtra("early_warning_score", dummy_early_warning_score_value);
        outgoing_intent.putExtra("is_special_alert", false);
        outgoing_intent.putExtra("max_possible", dummy_max_possible);
        outgoing_intent.putExtra("timestamp", time_now_in_milliseconds);
        sendIntent(outgoing_intent, device_info.device_type);
        
		dummy_early_warning_score_value++;
		if(dummy_early_warning_score_value > dummy_max_possible)
		{
			dummy_early_warning_score_value = 0;
		}
	}


    public void setSimulateLeadsOff(SensorType sensor_type, boolean simulate_leads_off)
    {
        device_info_manager.getDeviceInfoBySensorType(sensor_type).simulate_device_disconnected_from_body = simulate_leads_off;
    }


    public boolean getSimulateLeadsOff(SensorType sensor_type)
    {
        return device_info_manager.getDeviceInfoBySensorType(sensor_type).simulate_device_disconnected_from_body;
    }


    public void sendIntent(Intent intent, DeviceType device_type)
    {
        intent.putExtra("device_type", device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(intent);
    }
}
