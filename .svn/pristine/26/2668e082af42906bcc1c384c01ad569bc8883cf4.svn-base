package com.isansys.pse_isansysportal;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentChangeSessionSettings extends ListFragmentIsansys
{
    private static final String TAG = FragmentChangeSessionSettings.class.getName();

    class MyListAdapter extends ArrayAdapter<DeviceInfo>
    {
        final Context context;

        private final MainActivityInterface main_activity_interface;

        MyListAdapter(Context context, MainActivityInterface main_activity_interface, ArrayList<DeviceInfo> device_types_in_use)
        {
            super(context, -1, device_types_in_use);
            
            this.context = context;
            this.main_activity_interface = main_activity_interface;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            View v = convertView;

            if (v == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
                // Position is a bit of a crude way of doing this, but it works for now.
                switch (position)
                {
                    case 0:
                    {
                        final DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

                        v = inflater.inflate(R.layout.change_session_settings__lifetouch, parent, false);

                        device_controls_lifetouch.linearLayoutRemoveProgressBar = v.findViewById(R.id.LinearLayout_progressBarLifetouchChangeSessionDisconnect);
                        device_controls_lifetouch.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);

                        device_controls_lifetouch.linearLayoutRemoveWarningText = v.findViewById(R.id.LinearLayout_textViewLifetouchDisconnectedWarning);
                        device_controls_lifetouch.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);

                        device_controls_lifetouch.progressBarDisconnect = v.findViewById(R.id.progressBarLifetouchChangeSessionDisconnect);
                        device_controls_lifetouch.progressBarDisconnect.setProgress(device_controls_lifetouch.progressBarValue);
                        device_controls_lifetouch.progressBarDisconnect.setVisibility(View.INVISIBLE);

                        device_controls_lifetouch.textViewDisconnectProgress = v.findViewById(R.id.LifetouchChangeSessionDisconnectTextView);
                        device_controls_lifetouch.textViewDisconnectProgress.setVisibility(View.INVISIBLE);

                        device_controls_lifetouch.textViewDisconnectionWarning = v.findViewById(R.id.textView_lifetouchDisconnectedWarning);
                        device_controls_lifetouch.textViewDisconnectionWarning.setVisibility(View.INVISIBLE);

                        if (device_controls_lifetouch.textViewHumanReadableSerialNumber == null)
                        {
                            device_controls_lifetouch.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewChangeSessionSettingsLifetouchHumanReadableSerialNumber);
                            device_controls_lifetouch.textViewHumanReadableSerialNumber.setText(blank_human_readable_device_number);
                            device_controls_lifetouch.textViewFirmwareVersion = v.findViewById(R.id.textViewChangeSessionSettingsLifetouchFirmwareVersion);

                            createButtonAdd(device_controls_lifetouch, v, R.id.buttonAddLifetouch);
                            createButtonRemove(device_controls_lifetouch, v, R.id.buttonRemoveLifetouch, device_info);

                            doInitialDisplay(device_info, device_controls_lifetouch);
                        }
                    }
                    break;
    
                    case 1:
                    {
                        final DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE);

                        v = inflater.inflate(R.layout.change_session_settings__lifetemp, parent, false);

                        device_controls_thermometer.linearLayoutRemoveProgressBar = v.findViewById(R.id.LinearLayout_progressBarLifetempChangeSessionDisconnect);
                        device_controls_thermometer.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);

                        device_controls_thermometer.linearLayoutRemoveWarningText = v.findViewById(R.id.LinearLayout_textViewLifetempDisconnectedWarning);
                        device_controls_thermometer.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);

                        device_controls_thermometer.progressBarDisconnect = v.findViewById(R.id.progressBarLifetempChangeSessionDisconnect);
                        device_controls_thermometer.progressBarDisconnect.setProgress(device_controls_thermometer.progressBarValue);
                        device_controls_thermometer.progressBarDisconnect.setVisibility(View.INVISIBLE);

                        device_controls_thermometer.textViewDisconnectProgress = v.findViewById(R.id.LifetempChangeSessionDisconnectTextView);
                        device_controls_thermometer.textViewDisconnectProgress.setVisibility(View.INVISIBLE);

                        device_controls_thermometer.textViewDisconnectionWarning = v.findViewById(R.id.textView_lifetempDisconnectedWarning);
                        device_controls_thermometer.textViewDisconnectionWarning.setVisibility(View.INVISIBLE);
                        
                        if (device_controls_thermometer.textViewHumanReadableSerialNumber == null)
                        {
                            device_controls_thermometer.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber);
                            device_controls_thermometer.textViewHumanReadableSerialNumber.setText(blank_human_readable_device_number);
                            device_controls_thermometer.textViewFirmwareVersion = v.findViewById(R.id.textViewChangeSessionSettingsLifetempFirmwareVersion);

                            createButtonAdd(device_controls_thermometer, v, R.id.buttonAddLifetemp);
                            createButtonRemove(device_controls_thermometer, v, R.id.buttonRemoveThermometer, device_info);

                            setThermometerDisconnectionWarningText(device_info);

                            doInitialDisplay(device_info, device_controls_thermometer);
                        }
                    }
                    break;
    
                    case 2:
                    {
                        final DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__SPO2);

                        v = inflater.inflate(R.layout.change_session_settings__pulse_ox, parent, false);
                        
                        if (device_controls_pulse_ox.textViewHumanReadableSerialNumber == null)
                        {
                            boolean device_connected = device_info.isDeviceHumanReadableDeviceIdValid();

                            device_controls_pulse_ox.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceSpecificSettingsPulseOxHumanReadableSerialNumber);
                            device_controls_pulse_ox.textViewHumanReadableSerialNumber.setText(blank_human_readable_device_number);

                            device_controls_pulse_ox.textViewFirmwareVersion = v.findViewById(R.id.textViewChangeSessionSettingsPulseOxFirmwareVersion);

                            createButtonAdd(device_controls_pulse_ox, v, R.id.buttonAddPulseOx);

                            device_controls_pulse_ox.buttonRemove = v.findViewById(R.id.buttonRemovePulseOximeter);
                            device_controls_pulse_ox.buttonRemove.setOnClickListener(x -> {

                                main_activity_interface.removeDeviceFromGatewayAndRemoveFromEwsIfRequested(device_info.sensor_type);

                                showAddButtonHideRemoveButton(device_controls_pulse_ox);
                            });

                            if (device_connected)
                            {
                                if(device_info.isActualDeviceConnectionStatusConnected() || device_info.isActualDeviceConnectionStatusDisconnected())
                                {
                                    setHumanReadableNumberDeviceId(device_controls_pulse_ox, device_info);

                                    hideAddButtonShowRemoveButton(device_controls_pulse_ox);
                                }
                                else
                                {
                                    showAddButtonHideRemoveButton(device_controls_pulse_ox);
                                }
                            }
                            else
                            {
                                showAddButtonHideRemoveButton(device_controls_pulse_ox);
                            }
                        }
                    }
                    break;

                    case 3:
                    {
                        final DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);

                        v = inflater.inflate(R.layout.change_session_settings__blood_pressure, parent, false);
                        
                        if (device_controls_blood_pressure.textViewHumanReadableSerialNumber == null)
                        {
                            boolean device_connected = device_info.isDeviceHumanReadableDeviceIdValid();

                            device_controls_blood_pressure.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceSpecificSettingsBloodPressureHumanReadableSerialNumber);
                            device_controls_blood_pressure.textViewHumanReadableSerialNumber.setText(blank_human_readable_device_number);

                            device_controls_blood_pressure.textViewFirmwareVersion = v.findViewById(R.id.textViewChangeSessionSettingsBloodPressureFirmwareVersion);

                            createButtonAdd(device_controls_blood_pressure, v, R.id.buttonAddBloodPressure);

                            device_controls_blood_pressure.buttonRemove = v.findViewById(R.id.buttonRemoveBloodPressure);
                            device_controls_blood_pressure.buttonRemove.setOnClickListener(x -> main_activity_interface.removeDeviceFromGatewayAndRemoveFromEwsIfRequested(device_info.sensor_type));

                            if (device_connected)
                            {
                                if(device_info.isActualDeviceConnectionStatusConnected() || device_info.isActualDeviceConnectionStatusDisconnected())
                                {
                                    setHumanReadableNumberDeviceId(device_controls_blood_pressure, device_info);

                                    hideAddButtonShowRemoveButton(device_controls_blood_pressure);
                                }
                                else
                                {
                                    showAddButtonHideRemoveButton(device_controls_blood_pressure);
                                }
                            }
                            else
                            {
                                showAddButtonHideRemoveButton(device_controls_blood_pressure);
                            }
                        }
                    }
                    break;

                    case 4:
                    {
                        final DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

                        v = inflater.inflate(R.layout.change_session_settings__weight_scale, parent, false);

                        if (device_controls_weight_scale.textViewHumanReadableSerialNumber == null)
                        {
                            boolean device_connected = device_info.isDeviceHumanReadableDeviceIdValid();

                            device_controls_weight_scale.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceSpecificSettingsWeightScaleHumanReadableSerialNumber);
                            device_controls_weight_scale.textViewHumanReadableSerialNumber.setText(blank_human_readable_device_number);

                            device_controls_weight_scale.textViewFirmwareVersion = v.findViewById(R.id.textViewChangeSessionSettingsWeightScaleFirmwareVersion);

                            createButtonAdd(device_controls_weight_scale, v, R.id.buttonAddWeightScale);

                            device_controls_weight_scale.buttonRemove = v.findViewById(R.id.buttonRemoveWeightScale);
                            device_controls_weight_scale.buttonRemove.setOnClickListener(x -> main_activity_interface.removeDeviceFromGatewayAndRemoveFromEwsIfRequested(device_info.sensor_type));

                            if (device_connected)
                            {
                                if(device_info.isActualDeviceConnectionStatusConnected() || device_info.isActualDeviceConnectionStatusDisconnected())
                                {
                                    setHumanReadableNumberDeviceId(device_controls_weight_scale, device_info);

                                    hideAddButtonShowRemoveButton(device_controls_weight_scale);
                                }
                                else
                                {
                                    showAddButtonHideRemoveButton(device_controls_weight_scale);
                                }
                            }
                            else
                            {
                                showAddButtonHideRemoveButton(device_controls_weight_scale);
                            }
                        }
                    }
                    break;

                    // Early Warning Scores
                    case 5:
                    {
                        v = inflater.inflate(R.layout.change_session_settings__early_warning_scores, parent, false);

                        final DeviceInfo device_info = main_activity_interface.getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);

                        device_controls_early_warning_score.buttonAdd = v.findViewById(R.id.buttonChangeSessionSettingsAddEarlyWarningScores);
                        device_controls_early_warning_score.buttonAdd.setOnClickListener(v1 -> {
                            main_activity_interface.enableEarlyWarningScoreDevice(true);

                            main_activity_interface.getAllDeviceInfosFromGateway();
                        });


                        device_controls_early_warning_score.buttonRemove = v.findViewById(R.id.buttonChangeSessionSettingsRemoveEarlyWarningScores);

                        if (device_info.isActualDeviceConnectionStatusConnected())
                        {
                            hideAddButtonShowRemoveButton(device_controls_early_warning_score);
                        }
                        else
                        {
                            showAddButtonHideRemoveButton(device_controls_early_warning_score);
                        }
                    }
                    break;
                }
            }
            
            return v;
        }


        private void doInitialDisplay(final DeviceInfo device_info, final DeviceControls device_controls)
        {
            setFirmwareVersion(device_controls, device_info);

            if (device_info.isDeviceHumanReadableDeviceIdValid())
            {
                switch (device_info.getActualDeviceConnectionStatus())
                {
                    case CONNECTED:
                    {
                        setHumanReadableNumberDeviceId(device_controls, device_info);
                        device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                        device_controls.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);

                        hideAddButtonShowRemoveButton(device_controls);

                        device_controls.progressBarDisconnect.setVisibility(View.INVISIBLE);

                        int number_of_measurements_pending_to_receive = device_info.getMeasurementsPending();
                        Log.d(TAG, "number_of_measurements_pending_to_receive = " + number_of_measurements_pending_to_receive);

                        if(number_of_measurements_pending_to_receive > 0)
                        {
                            hideAddButtonShowRemoveButton(device_controls);

                            device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                            device_controls.linearLayoutRemoveProgressBar.setVisibility(View.VISIBLE);

                            setDeviceMeasurementsPendingProgressBar(device_info);

                            main_activity_interface.checkAndCancel_timerTask(device_controls.progressBarTimerTask);
                            //timer task to update the progress bar
                            device_controls.progressBarTimerTask = new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " pending measurement = " + device_info.getMeasurementsPending());

                                    updateDeviceMeasurementsPendingProgressBar(device_info, device_controls);
                                }
                            };

                            main_activity_interface.checkAndCancel_timer(device_controls.progressBarTimer);
                            device_controls.progressBarTimer = new Timer();
                            device_controls.progressBarTimer.scheduleAtFixedRate(device_controls.progressBarTimerTask, 0, (int)DateUtils.SECOND_IN_MILLIS);
                        }
                        else
                        {
                            // No measurements pending
                        }
                    }
                    break;

                    case DISCONNECTED:
                    {
                        setHumanReadableNumberDeviceId(device_controls, device_info);
                        hideAddButtonShowRemoveButton(device_controls);
                        device_controls.linearLayoutRemoveWarningText.setVisibility(View.VISIBLE);
                        device_controls.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);

                        // Updating the warning text for device disconnection
                        device_controls.textViewDisconnectionWarning.setText(device_controls.string_device_not_connected_disconnecting_now_will_lose_data);
                        device_controls.textViewDisconnectionWarning.setVisibility(View.VISIBLE);

                        device_controls.progressBarDisconnect.setVisibility(View.INVISIBLE);
                    }
                    break;

                    case FOUND:
                    case NOT_PAIRED:
                    case SEARCHING:
                    {
                        device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                        device_controls.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);

                        showAddButtonHideRemoveButton(device_controls);
                    }
                    break;

                    default:
                        break;
                }
            }
            else
            {
                showAddButtonHideRemoveButton(device_controls);
            }
        }
    }


    static class DeviceControls
    {
        private Button buttonAdd;
        private Button buttonRemove;

        ProgressBar progressBarDisconnect;
        TextView textViewDisconnectProgress;
        LinearLayout linearLayoutRemoveProgressBar;
        LinearLayout linearLayoutRemoveWarningText;
        Timer progressBarTimer;
        TimerTask progressBarTimerTask;
        volatile int progressBarValue;
        TextView textViewDisconnectionWarning;
        TextView textViewHumanReadableSerialNumber;
        TextView textViewFirmwareVersion;

        String string_please_wait_device_disconnected;
        String string_please_wait_download_measurements;
        String string_device_not_connected_disconnecting_now_will_lose_data;

        DeviceControls()
        {
            progressBarValue = 0;
            progressBarTimer = null;
        }
    }


    private DeviceControls device_controls_lifetouch;
    private DeviceControls device_controls_thermometer;
    private DeviceControls device_controls_pulse_ox;
    private DeviceControls device_controls_blood_pressure;
    private DeviceControls device_controls_weight_scale;
    private DeviceControls device_controls_early_warning_score;



    private final long PROGRESS_BAR_UPDATE_TIME_MS = 300;
    private final long PROGRESS_BAR_SLOW_UPDATE_TIME_MS = 3000;

    private long progress_bar_update_time;

    private final int PROGRESS_BAR_DISCONNECTION_COUNT = 100;

    private String blank_human_readable_device_number = "";


    private final ArrayList<DeviceInfo> device_types_in_use = new ArrayList<>();            // Only contains the devices that have been scanned.
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.change_session_settings, container, false);

        device_controls_lifetouch = new DeviceControls();
        device_controls_lifetouch.string_please_wait_device_disconnected = getString(R.string.textViewLifetouchChangeSessionDisconnect);
        device_controls_lifetouch.string_please_wait_download_measurements = getString(R.string.string_waitForLifetouchPendingHeartBeat);
        device_controls_lifetouch.string_device_not_connected_disconnecting_now_will_lose_data = getString(R.string.string_WarningPendingHeartBeatButRemoveLifetouchTextView);

        device_controls_thermometer = new DeviceControls();
        device_controls_thermometer.string_please_wait_device_disconnected = getString(R.string.textViewLifetempChangeSessionDisconnect);
        device_controls_thermometer.string_please_wait_download_measurements = getString(R.string.string_waitForLifetempPendingMeasurement);
        device_controls_thermometer.string_device_not_connected_disconnecting_now_will_lose_data = getString(R.string.string_WarningPendingTemperatureButRemoveLifetempTextView);

        device_controls_pulse_ox = new DeviceControls();
        device_controls_blood_pressure = new DeviceControls();
        device_controls_weight_scale = new DeviceControls();

        device_controls_early_warning_score = new DeviceControls();

        device_types_in_use.add(null);                                                  // Lifetouch
        device_types_in_use.add(null);                                                  // Lifetemp
        device_types_in_use.add(null);                                                  // SpO2
        device_types_in_use.add(null);                                                  // Blood Pressure
        device_types_in_use.add(null);                                                  // Weight Scale
        device_types_in_use.add(null);                                                  // Early Warning Scores

        return v;
    }


    @Override
    public void onResume()
    {
        if(getActivity() != null)
        {
            MyListAdapter myListAdapter = new MyListAdapter(getActivity(), main_activity_interface, device_types_in_use);
            setListAdapter(myListAdapter);
        }
        else
        {
            Log.e(TAG, "onResume : FragmentChangeSessionSetting getActivity() is null");
        }

        // Get latest info from Gateway
        main_activity_interface.getAllDeviceInfosFromGateway();

        blank_human_readable_device_number = getResources().getString(R.string.six_dashes);

        // Disable ListView scrolling. This allows the touch event to be passed to the graphs
        getListView().setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                view.smoothScrollBy(0,0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                view.smoothScrollBy(0,0);
            }
        });

        if(main_activity_interface.stopUiFastUpdates())
        {
            progress_bar_update_time = PROGRESS_BAR_SLOW_UPDATE_TIME_MS;
        }
        else
        {
            progress_bar_update_time = PROGRESS_BAR_UPDATE_TIME_MS;
        }

        super.onResume();
    }
    

    private void setHumanReadableNumberDeviceId(DeviceControls device_controls, DeviceInfo device_info)
    {
        if (device_controls.textViewHumanReadableSerialNumber != null)
        {
            if (device_info.isDeviceHumanReadableDeviceIdValid())
            {
                device_controls.textViewHumanReadableSerialNumber.setText(String.valueOf(device_info.human_readable_device_id));
            }
            else
            {
                device_controls.textViewHumanReadableSerialNumber.setText(blank_human_readable_device_number);
            }
        }
    }


    private void showAddButtonHideRemoveButton(DeviceControls device_controls)
    {
        if(device_controls.buttonAdd != null)
        {
            device_controls.buttonAdd.setVisibility(View.VISIBLE);
        }

        if(device_controls.buttonRemove != null)
        {
            device_controls.buttonRemove.setVisibility(View.INVISIBLE);
        }
    }


    private void hideAddButtonShowRemoveButton(DeviceControls device_controls)
    {
        if(device_controls.buttonAdd != null)
        {
            device_controls.buttonAdd.setVisibility(View.INVISIBLE);
        }

        if(device_controls.buttonRemove != null)
        {
            device_controls.buttonRemove.setVisibility(View.VISIBLE);
        }
    }


    private DeviceControls getDeviceControls(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:            return device_controls_lifetouch;
            case SENSOR_TYPE__TEMPERATURE:          return device_controls_thermometer;
            case SENSOR_TYPE__SPO2:                 return device_controls_pulse_ox;
            case SENSOR_TYPE__BLOOD_PRESSURE:       return device_controls_blood_pressure;
            case SENSOR_TYPE__WEIGHT_SCALE:         return device_controls_weight_scale;

            case SENSOR_TYPE__ALGORITHM:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__EARLY_WARNING_SCORE:      return device_controls_early_warning_score;
                }
            }
        }

        return null;
    }


    public void showDeviceStatus(DeviceInfo device_info)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            setHumanReadableNumberDeviceId(device_controls, device_info);
            setFirmwareVersion(device_controls, device_info);

            if (!device_info.isDeviceSessionInProgress())
            {
                showAddButtonHideRemoveButton(device_controls);
            }
            else
            {
                hideAddButtonShowRemoveButton(device_controls);
            }
        }
    }


    private void setFirmwareVersion(DeviceControls device_controls, DeviceInfo device_info)
    {
        if (device_controls.textViewFirmwareVersion != null)
        {
            if (!device_info.firmware_string.isEmpty())
            {
                String string = getResources().getString(R.string.fw_equals) + device_info.firmware_string;
                device_controls.textViewFirmwareVersion.setText(string);

                device_controls.textViewFirmwareVersion.setVisibility(View.VISIBLE);
            }
            else
            {
                device_controls.textViewFirmwareVersion.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void updateDeviceDisconnectionProgressBar(final DeviceInfo device_info, final DeviceControls device_controls)
    {
        if(getActivity() != null)
        {
            getActivity().runOnUiThread(() -> {
                try
                {
                    device_controls.progressBarValue++;
                    if(device_controls.progressBarValue >= PROGRESS_BAR_DISCONNECTION_COUNT)
                    {
                        device_controls.progressBarValue = 0;

                        showAddButtonHideRemoveButton(device_controls);

                        device_controls.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);
                        device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                        device_controls.progressBarDisconnect.setVisibility(View.INVISIBLE);
                        device_controls.textViewDisconnectProgress.setVisibility(View.INVISIBLE);

                        main_activity_interface.clearDesiredDeviceInPatientGateway(device_info.sensor_type);
                        main_activity_interface.checkAndCancel_timerTask(device_controls.progressBarTimerTask);
                    }
                    else
                    {
                        device_controls.progressBarDisconnect.setProgress(device_controls.progressBarValue);
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "updateDeviceDisconnectionProgressBar: could not update due to error");
                    Log.e(TAG, e.toString());
                }
            });
        }
        else
        {
            Log.e(TAG,"updateDeviceDisconnectionProgressBar : getActivity() is null");
        }
    }


    public void bleDeviceDisconnectionCompleted(DeviceInfo device_info)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            Log.i(TAG, "bleDeviceDisconnectionCompleted : " + device_info.getSensorTypeAndDeviceTypeAsString() + " disconnect command from the gateway");
            device_controls.progressBarValue = 0;

            // Lifetouch and Lifetemp display a progress bar showing their cached measurements being download.
            // Other BLE devices do not...so have a null check here

            if (device_controls.linearLayoutRemoveProgressBar != null)
            {
                device_controls.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);
            }

            if (device_controls.linearLayoutRemoveWarningText != null)
            {
                device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
            }

            if (device_controls.progressBarDisconnect != null)
            {
                device_controls.progressBarDisconnect.setVisibility(View.INVISIBLE);
            }

            showDeviceStatus(device_info);

            if (device_controls.textViewDisconnectProgress != null)
            {
                device_controls.textViewDisconnectProgress.setVisibility(View.INVISIBLE);
            }

            main_activity_interface.clearDesiredDeviceInPatientGateway(device_info.sensor_type);

            if (device_controls.progressBarTimerTask != null)
            {
                main_activity_interface.checkAndCancel_timerTask(device_controls.progressBarTimerTask);
            }
        }
    }


    private void setDeviceMeasurementsPendingProgressBar(DeviceInfo device_info)
    {
        int measurements_pending_to_download = device_info.getMeasurementsPending();

        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                device_controls_lifetouch.textViewDisconnectProgress.setVisibility(View.VISIBLE);
                device_controls_lifetouch.textViewDisconnectProgress.setText(R.string.string_waitForLifetouchPendingHeartBeat);

                setProgressBarVisibleAndSetMax(device_controls_lifetouch, measurements_pending_to_download);
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                if (main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE).device_type == DeviceType.DEVICE_TYPE__LIFETEMP_V2)
                {
                    device_controls_thermometer.textViewDisconnectProgress.setVisibility(View.VISIBLE);
                    device_controls_thermometer.textViewDisconnectProgress.setText(R.string.string_waitForLifetempDisconnection);

                    setProgressBarVisibleAndSetMax(device_controls_thermometer, measurements_pending_to_download);
                }
            }
            break;
        }
    }


    private void setProgressBarVisibleAndSetMax(DeviceControls device_controls, int max_value)
    {
        if (device_controls != null)
        {
            if (device_controls.progressBarDisconnect != null)
            {
                device_controls.progressBarDisconnect.setVisibility(View.VISIBLE);
                device_controls.progressBarDisconnect.setMax(max_value);
            }
        }
    }


    /**
	 * Update ProgressBar value every second. If user leaves the FragmentChangeSessionSetting then disconnection doesn't happens
	 * TextView also update it text with number of heartbeat pending
	 */
	private void updateDeviceMeasurementsPendingProgressBar(final DeviceInfo device_info, final DeviceControls device_controls)
	{
        if(getActivity() != null)
        {
            // Update the ProgressBar and textView main UI thread
            getActivity().runOnUiThread(() -> {
                try
                {
                    int number_of_measurements_pending_to_receive = device_info.getMeasurementsPending();

                    // lifetouch pending heartbeat is zero than start the disconnection procedure
                    if(number_of_measurements_pending_to_receive <= 0)
                    {
                        // Cancel the previous timer task(Pending heart beat) and create new one for disconnection
                        main_activity_interface.checkAndCancel_timerTask(device_controls.progressBarTimerTask);

                        // Replace the linearLayout warning textView with the Disconnection progressBar
                        device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                        device_controls.linearLayoutRemoveProgressBar.setVisibility(View.INVISIBLE);

                        hideAddButtonShowRemoveButton(device_controls);

                        // reset the progress bar max value and enable it
                        device_controls.progressBarValue = 0;
                        device_controls.progressBarDisconnect.setProgress(device_controls.progressBarValue);
                        device_controls.progressBarDisconnect.setVisibility(View.INVISIBLE);

                        device_controls.textViewDisconnectProgress.setText(device_controls.string_please_wait_device_disconnected);
                        device_controls.textViewDisconnectProgress.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        // More lifetouch heartBeats pending to send to Gateway
                        device_controls.progressBarDisconnect.setProgress(number_of_measurements_pending_to_receive);

                        // Updating textView with number of heartbeat pending
                        String string = device_controls.string_please_wait_download_measurements + " " + number_of_measurements_pending_to_receive;
                        device_controls.textViewDisconnectProgress.setText(string);
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "updateDeviceMeasurementsPendingProgressBar: could not update due to error");
                    Log.e(TAG, e.toString());
                }
            });
        }
        else
        {
            Log.e(TAG,"updateDeviceMeasurementsPendingProgressBar : getActivity() is null");
        }
	}


    /**
	 * Create the dialog box to warn the user that device data are pending. Removing now will result loss of pending data
	 * @param deviceTemporaryDisconnected : boolean true if device is temporary disconnected
	 */
	private void createDialogBox(boolean deviceTemporaryDisconnected, final DeviceInfo device_info)
	{
        if(getActivity() != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            if(deviceTemporaryDisconnected)
            {
                builder.setMessage(getResources().getString(R.string.string_remove_disconnected_devices_dialog));
            }
            else
            {
                builder.setMessage(getResources().getString(R.string.string_WarningPendingDataDownloading));
            }

            builder.setCancelable(true);
            // if pressed yes then run the disconnection progressBar. This might be case for battery finished.
            builder.setPositiveButton(R.string.stringYes, (dialog, id) -> {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                        {
                            main_activity_interface.removeDeviceFromGateway(device_info.sensor_type);
                            device_controls_lifetouch.buttonAdd.setVisibility(View.INVISIBLE);

                            device_controls_lifetouch.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                            device_controls_lifetouch.linearLayoutRemoveProgressBar.setVisibility(View.VISIBLE);
                            device_controls_lifetouch.buttonRemove.setVisibility(View.VISIBLE);

                            device_controls_lifetouch.progressBarValue = 0;
                            device_controls_lifetouch.progressBarDisconnect.setProgress(device_controls_lifetouch.progressBarValue);
                            setProgressBarVisibleAndSetMax(device_controls_lifetouch, PROGRESS_BAR_DISCONNECTION_COUNT);

                            device_controls_lifetouch.textViewDisconnectProgress.setText(R.string.textViewLifetouchChangeSessionDisconnect);
                            device_controls_lifetouch.textViewDisconnectProgress.setVisibility(View.VISIBLE);

                            main_activity_interface.checkAndCancel_timerTask(device_controls_lifetouch.progressBarTimerTask);
                            device_controls_lifetouch.progressBarTimerTask = new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    Log.d(TAG, " Handler for progress bar executed : " + device_controls_lifetouch.progressBarValue);
                                    updateDeviceDisconnectionProgressBar(device_info, device_controls_lifetouch);
                                }
                            };

                            main_activity_interface.checkAndCancel_timer(device_controls_lifetouch.progressBarTimer);
                            device_controls_lifetouch.progressBarTimer = new Timer();
                            device_controls_lifetouch.progressBarTimer.scheduleAtFixedRate(device_controls_lifetouch.progressBarTimerTask, 0, progress_bar_update_time);
                        }
                        break;

                    case SENSOR_TYPE__TEMPERATURE:
                        {
                            main_activity_interface.removeDeviceFromGateway(device_info.sensor_type);
                            device_controls_thermometer.buttonAdd.setVisibility(View.INVISIBLE);

                            device_controls_thermometer.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);

                            if (device_info.device_type == DeviceType.DEVICE_TYPE__LIFETEMP_V2)
                            {
                                device_controls_thermometer.linearLayoutRemoveProgressBar.setVisibility(View.VISIBLE);
                                device_controls_thermometer.buttonRemove.setVisibility(View.VISIBLE);

                                device_controls_thermometer.progressBarValue = 0;
                                device_controls_thermometer.progressBarDisconnect.setProgress(device_controls_thermometer.progressBarValue);
                                setProgressBarVisibleAndSetMax(device_controls_thermometer, PROGRESS_BAR_DISCONNECTION_COUNT);

                                device_controls_thermometer.textViewDisconnectProgress.setText(R.string.textViewLifetempChangeSessionDisconnect);
                                device_controls_thermometer.textViewDisconnectProgress.setVisibility(View.VISIBLE);

                                main_activity_interface.checkAndCancel_timerTask(device_controls_thermometer.progressBarTimerTask);
                                device_controls_thermometer.progressBarTimerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, " Handler for progress bar executed : " + device_controls_thermometer.progressBarValue);
                                        updateDeviceDisconnectionProgressBar(device_info, device_controls_thermometer);
                                    }
                                };

                                main_activity_interface.checkAndCancel_timer(device_controls_thermometer.progressBarTimer);
                                device_controls_thermometer.progressBarTimer = new Timer();
                                device_controls_thermometer.progressBarTimer.scheduleAtFixedRate(device_controls_thermometer.progressBarTimerTask, 0, progress_bar_update_time);
                            }
                        }
                        break;

                    default:
                        {
                            Log.e(TAG, " Dialog box is created for non BLE device ALERT ALERT ALERT !!!!!!!");
                        }
                        break;
                }

                dialog.cancel();
            });
            // If pressed cancel then don't do anything
            builder.setNegativeButton(R.string.stringNo, (dialog, id) -> {
                // do nothing
                dialog.cancel();
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            Log.e(TAG, "createDialogBox : getActivity() is null");
        }
	}
	
	
	@Override
	public void onStop()
	{
		super.onStop();

        main_activity_interface.checkAndCancel_timer(device_controls_lifetouch.progressBarTimer);
        main_activity_interface.checkAndCancel_timerTask(device_controls_lifetouch.progressBarTimerTask);

        main_activity_interface.checkAndCancel_timer(device_controls_thermometer.progressBarTimer);
        main_activity_interface.checkAndCancel_timerTask(device_controls_thermometer.progressBarTimerTask);
	}


    private void createButtonAdd(DeviceControls device_controls, View v, int id)
    {
        device_controls.buttonAdd = v.findViewById(id);
        device_controls.buttonAdd.setOnClickListener(x -> main_activity_interface.addDevicesSelected());
    }


    private void createButtonRemove(final DeviceControls device_controls, View v, int id, final DeviceInfo device_info)
    {
        device_controls.buttonRemove = v.findViewById(id);
        device_controls.buttonRemove.setOnClickListener(x -> {
            if(!main_activity_interface.getDeviceCurrentlyConnectedByType(device_info.sensor_type))
            {
                Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " temporary disconnect is true ");
                createDialogBox(true, device_info);
            }
            else
            {
                int number_of_measurements_pending_to_receive = device_info.getMeasurementsPending();
                if((device_info.isActualDeviceConnectionStatusConnected()) && (number_of_measurements_pending_to_receive > 0))
                {
                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " connected but pending measurements = " + number_of_measurements_pending_to_receive);
                    createDialogBox(false, device_info);
                }
                else
                {
                    // Lifetouch/Thermometer is in connected state and measurements are being synced to gateway

                    main_activity_interface.removeDeviceFromGatewayAndRemoveFromEwsIfRequested(device_info.sensor_type);

                    device_controls.linearLayoutRemoveWarningText.setVisibility(View.INVISIBLE);
                    device_controls.linearLayoutRemoveProgressBar.setVisibility(View.VISIBLE);

                    hideAddButtonShowRemoveButton(device_controls);

                    if (device_info.supports_disconnecting_progress_bar) // Lifetemp and Lifetouch only
                    {
                        device_controls.progressBarValue = 0;
                        device_controls.progressBarDisconnect.setProgress(device_controls.progressBarValue);
                        device_controls.progressBarDisconnect.setVisibility(View.VISIBLE);
                        device_controls.textViewDisconnectProgress.setVisibility(View.VISIBLE);

                        main_activity_interface.checkAndCancel_timerTask(device_controls.progressBarTimerTask);
                        device_controls.progressBarTimerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " Handler for progress bar executed " + device_controls.progressBarValue);
                                updateDeviceDisconnectionProgressBar(device_info, device_controls);
                            }
                        };

                        main_activity_interface.checkAndCancel_timer(device_controls.progressBarTimer);
                        device_controls.progressBarTimer = new Timer();
                        device_controls.progressBarTimer.scheduleAtFixedRate(device_controls.progressBarTimerTask, 0, progress_bar_update_time);
                    }
                }
            }
        });
    }


    private void setThermometerDisconnectionWarningText(DeviceInfo device_info)
    {
        if (device_info.sensor_type == SensorType.SENSOR_TYPE__TEMPERATURE)
        {
            if (device_info.device_type == DeviceType.DEVICE_TYPE__LIFETEMP_V2)
            {
                device_controls_thermometer.string_device_not_connected_disconnecting_now_will_lose_data = getString(R.string.string_WarningPendingTemperatureButRemoveLifetempTextView);
            }
            else
            {
                device_controls_thermometer.string_device_not_connected_disconnecting_now_will_lose_data = "";
            }
        }
    }
}
