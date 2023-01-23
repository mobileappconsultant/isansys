package com.isansys.pse_isansysportal;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import java.util.ArrayList;

public class FragmentDeviceConnection extends ListFragmentIsansys
{
    final boolean resume_in_progress_scan;

    public FragmentDeviceConnection(boolean resume_scan)
    {
        resume_in_progress_scan = resume_scan;
    }


    class MyListAdapter extends ArrayAdapter<DeviceInfo>
    {
        final Context context;
        final int number_of_rows;

        private final MainActivityInterface main_activity_interface;

        MyListAdapter(Context context, MainActivityInterface main_activity_interface, ArrayList<DeviceInfo> device_types_in_use)
        {
            super(context, -1, device_types_in_use);
            
            this.number_of_rows = main_activity_interface.getNumberOfBluetoothDevicesToConnect();
            
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
                
                Log.e(TAG, "getView. Position = " + position);

                // Find out from MainActivity which device type is in this row
                final DeviceInfo device_info = sensor_types_to_connect.get(position);

                int main_fragment_height = main_activity_interface.getMainFragmentHeight();
                
                int row_height = main_fragment_height / number_of_rows;

                if (device_info != null)
                {
                    Log.e(TAG, "getView. Position = " + position + " : " + device_info.getSensorTypeAndDeviceTypeAsString());

                    switch (device_info.sensor_type)
                    {
                        case SENSOR_TYPE__LIFETOUCH:
                        {
                            v = inflater.inflate(R.layout.device_connection__lifetouch, parent, false);

                            // Only do the following once. Not sure why this works however....
                            if (device_controls_lifetouch.progressBar == null)
                            {
                                // Adjust the size of the Linear Layout depending on how many rows are in the List.
                                LinearLayout ll = v.findViewById(R.id.linear_layout__device_connection__lifetouch);
                                ll.getLayoutParams().height = row_height;
                                ll.requestLayout();

                                TextView textViewLifetouchHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceConnectionLifetouchHumanReadableSerialNumber);
                                textViewLifetouchHumanReadableSerialNumber.setText(String.valueOf(device_info.human_readable_device_id));

                                device_controls_lifetouch.textViewSearchStatus = v.findViewById(R.id.textViewLifetouchSearchStatus);
                                device_controls_lifetouch.progressBar = v.findViewById(R.id.progressBarLifetouchConnection);
                                device_controls_lifetouch.buttonCancelSearchOrSearchAgain = v.findViewById(R.id.buttonLifetouchCancelSearchOrSearchAgain);

                                resetDeviceSearchProgressBar(device_controls_lifetouch);
                                setupButton(device_controls_lifetouch, device_info);
                            }
                        }
                        break;

                        case SENSOR_TYPE__TEMPERATURE:
                        {
                            v = inflater.inflate(R.layout.device_connection__lifetemp, parent, false);

                            // Only do the following once. Not sure why this works however....
                            if (device_controls_thermometer.progressBar == null)
                            {
                                // Adjust the size of the Linear Layout depending on how many rows are in the List.
                                LinearLayout ll = v.findViewById(R.id.linear_layout__device_connection__lifetemp);
                                ll.getLayoutParams().height = row_height;
                                ll.requestLayout();

                                TextView textViewLifetempHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceConnectionLifetempHumanReadableSerialNumber);
                                textViewLifetempHumanReadableSerialNumber.setText(String.valueOf(device_info.human_readable_device_id));

                                device_controls_thermometer.textViewSearchStatus = v.findViewById(R.id.textViewLifetempSearchStatus);
                                device_controls_thermometer.progressBar = v.findViewById(R.id.progressBarLifetempConnection);
                                device_controls_thermometer.buttonCancelSearchOrSearchAgain = v.findViewById(R.id.buttonLifetempCancelSearchOrSearchAgain);

                                resetDeviceSearchProgressBar(device_controls_thermometer);
                                setupButton(device_controls_thermometer, device_info);
                            }
                        }
                        break;

                        case SENSOR_TYPE__SPO2:
                        {
                            v = inflater.inflate(R.layout.device_connection__pulse_ox, parent, false);

                            // Only do the following once. Not sure why this works however....
                            if (device_controls_pulse_ox.progressBar == null)
                            {
                                // Adjust the size of the Linear Layout depending on how many rows are in the List.
                                LinearLayout ll = v.findViewById(R.id.linear_layout__device_connection__pulse_ox);
                                ll.getLayoutParams().height = row_height;
                                ll.requestLayout();

                                TextView textViewPulseOxHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceConnectionPulseOxHumanReadableSerialNumber);
                                textViewPulseOxHumanReadableSerialNumber.setText(String.valueOf(device_info.human_readable_device_id));

                                device_controls_pulse_ox.textViewSearchStatus = v.findViewById(R.id.textViewPulseOxSearchStatus);
                                device_controls_pulse_ox.progressBar = v.findViewById(R.id.progressBarPulseOxConnection);
                                device_controls_pulse_ox.buttonCancelSearchOrSearchAgain = v.findViewById(R.id.buttonPulseOxCancelSearchOrSearchAgain);

                                resetDeviceSearchProgressBar(device_controls_pulse_ox);
                                setupButton(device_controls_pulse_ox, device_info);
                            }
                        }
                        break;

                        case SENSOR_TYPE__BLOOD_PRESSURE:
                        {
                            v = inflater.inflate(R.layout.device_connection__blood_pressure, parent, false);

                            // Only do the following once. Not sure why this works however....
                            if (device_controls_blood_pressure.progressBar == null)
                            {
                                // Adjust the size of the Linear Layout depending on how many rows are in the List.
                                LinearLayout ll = v.findViewById(R.id.linear_layout__device_connection__blood_pressure);
                                ll.getLayoutParams().height = row_height;
                                ll.requestLayout();

                                TextView textViewBloodPressureHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceConnectionBloodPressureHumanReadableSerialNumber);
                                textViewBloodPressureHumanReadableSerialNumber.setText(String.valueOf(device_info.human_readable_device_id));

                                device_controls_blood_pressure.textViewSearchStatus = v.findViewById(R.id.textViewBloodPressureSearchStatus);
                                device_controls_blood_pressure.progressBar = v.findViewById(R.id.progressBarBloodPressureConnection);
                                device_controls_blood_pressure.buttonCancelSearchOrSearchAgain = v.findViewById(R.id.buttonBloodPressureCancelSearchOrSearchAgain);

                                resetDeviceSearchProgressBar(device_controls_blood_pressure);
                                setupButton(device_controls_blood_pressure, device_info);
                            }
                        }
                        break;

                        case SENSOR_TYPE__WEIGHT_SCALE:
                        {
                            v = inflater.inflate(R.layout.device_connection__weight_scale, parent, false);

                            // Only do the following once. Not sure why this works however....
                            if (device_controls_scales.progressBar == null)
                            {
                                // Adjust the size of the Linear Layout depending on how many rows are in the List.
                                LinearLayout ll = v.findViewById(R.id.linear_layout__device_connection__weight_scale);
                                ll.getLayoutParams().height = row_height;
                                ll.requestLayout();

                                TextView textViewWeightScaleHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceConnectionWeightScaleHumanReadableSerialNumber);
                                textViewWeightScaleHumanReadableSerialNumber.setText(String.valueOf(device_info.human_readable_device_id));

                                device_controls_scales.textViewSearchStatus = v.findViewById(R.id.textViewWeightScaleSearchStatus);
                                device_controls_scales.progressBar = v.findViewById(R.id.progressBarWeightScaleConnection);
                                device_controls_scales.buttonCancelSearchOrSearchAgain = v.findViewById(R.id.buttonWeightScaleCancelSearchOrSearchAgain);

                                resetDeviceSearchProgressBar(device_controls_scales);
                                setupButton(device_controls_scales, device_info);

                            }
                        }
                        break;

                        default:
                            break;
                    }
                }
                else
                {
                    Log.e(TAG, "DeviceInfo is null");
                }
            }
            
            return v;
        }


        private void setupButton(final DeviceControls device_controls, final DeviceInfo device_info)
        {
            device_controls.buttonCancelSearchOrSearchAgain.setText(getResources().getString(R.string.textSearchAgain));
            device_controls.buttonCancelSearchOrSearchAgain.setVisibility(View.INVISIBLE);
            device_controls.buttonCancelSearchOrSearchAgain.setOnClickListener(v -> {
                main_activity_interface.searchAgainForDevice(device_info);

                resetDeviceSearchProgressBar(device_controls);
            });

            if(resume_in_progress_scan)
            {
                updateDeviceConnectionProgress_ResumeCurrentScan(device_info);
            }
        }
    }


    private void updateDeviceConnectionProgress_ResumeCurrentScan(DeviceInfo device_info)
    {
        Log.d(TAG, "updateDeviceConnectionProgress_ResumeCurrentScan : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : " + device_info.getActualDeviceConnectionStatus());

        if (device_info.isDeviceTypeASensorDevice())
        {
            switch(device_info.getActualDeviceConnectionStatus())
            {
                case CONNECTED:
                case DISCONNECTED:
                {
                    /* All of these mean the device has been successfully found and connected initially,
                       but may now be disconnected and trying to reconnect in the background.
                       For the purposes of the FragmentDeviceConnection, can treat it as connected.
                     */
                    showDeviceConnectedAndHideDeviceCancelSearchOrSearchAgainButton(device_info);
                }
                break;

                case SEARCHING:
                {
                    showDeviceScanStarted(device_info);
                }
                break;

                case FOUND:
                {
                    showDeviceFound(device_info);
                }
                break;

                case NOT_PAIRED:
                case UNBONDED:
                {
                    showDeviceNotFound(device_info);
                }
                break;
            }
        }
    }


    private final String TAG = this.getClass().getSimpleName();

    private static class DeviceControls
    {
        TextView textViewSearchStatus;
        RoundCornerProgressBar progressBar;
        Button buttonCancelSearchOrSearchAgain;

        String string_device_connected;
        String string_device_not_connected;

        DeviceControls()
        {
            textViewSearchStatus = null;
            progressBar = null;
            buttonCancelSearchOrSearchAgain = null;
            string_device_connected = "";
            string_device_not_connected = "";
        }
    }


    private DeviceControls device_controls_lifetouch;
    private DeviceControls device_controls_thermometer;
    private DeviceControls device_controls_pulse_ox;
    private DeviceControls device_controls_blood_pressure;
    private DeviceControls device_controls_scales;

    ArrayList<DeviceInfo> sensor_types_to_connect = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.device_connection, container, false);

        device_controls_lifetouch = new DeviceControls();
        device_controls_lifetouch.string_device_connected = getResources().getString(R.string.textLifetouchConnected);
        device_controls_lifetouch.string_device_not_connected = getResources().getString(R.string.textLifetouchNotFound);

        device_controls_thermometer = new DeviceControls();
        device_controls_thermometer.string_device_connected = getResources().getString(R.string.textThermometerConnected);
        device_controls_thermometer.string_device_not_connected = getResources().getString(R.string.textThermometerNotFound);

        device_controls_pulse_ox = new DeviceControls();
        device_controls_pulse_ox.string_device_connected = getResources().getString(R.string.textPulseOximeterConnected);
        device_controls_pulse_ox.string_device_not_connected = getResources().getString(R.string.textPulseOximeterNotFound);

        device_controls_blood_pressure = new DeviceControls();
        device_controls_blood_pressure.string_device_connected = getResources().getString(R.string.textBloodPressureConnected);
        device_controls_blood_pressure.string_device_not_connected = getResources().getString(R.string.textBloodPressureNotFound);

        device_controls_scales = new DeviceControls();
        device_controls_scales.string_device_connected = getResources().getString(R.string.textScalesConnected);
        device_controls_scales.string_device_not_connected = getResources().getString(R.string.textScalesNotFound);

        if(getActivity() != null)
        {
            sensor_types_to_connect = main_activity_interface.getBluetoothDevicesTypesInUse();

            MyListAdapter myListAdapter = new MyListAdapter(getActivity(), main_activity_interface, sensor_types_to_connect);
            setListAdapter(myListAdapter);
        }
        else
        {
            Log.e(TAG, "onCreateView : FragmentDeviceConnection getActivity() is null");
        }
        return v;
    }


    public void setDeviceProgressBarValue(DeviceInfo device_info, int value)
    {
        DeviceControls device_controls = getDeviceControls(device_info);

        if (device_controls != null)
        {
            setDeviceProgressBarValue(device_controls, value);
        }
    }


    private void setDeviceProgressBarValue(DeviceControls device_controls, int value)
    {
        if (device_controls.progressBar != null)
        {
            device_controls.progressBar.setProgress(value);
        }
    }


    public void showDeviceScanStarted(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                showDeviceScanStarted(device_controls_lifetouch, getResources().getString(R.string.textSearchingForLifetouch));
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                showDeviceScanStarted(device_controls_thermometer, getResources().getString(R.string.textSearchingForThermometer));
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                showDeviceScanStarted(device_controls_pulse_ox, getResources().getString(R.string.textSearchingForPulseOximeter));
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                showDeviceScanStarted(device_controls_blood_pressure, getResources().getString(R.string.textSearchingForBloodPressure));
            }
            break;

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                showDeviceScanStarted(device_controls_scales, getResources().getString(R.string.textSearchingForScales));
            }
            break;
        }

        hideAllSearchAgainButtons();
    }


    public void showDeviceConnectedAndHideDeviceCancelSearchOrSearchAgainButton(DeviceInfo device_info)
    {
        DeviceControls device_controls = getDeviceControls(device_info);

        if (device_controls != null)
        {
            showDeviceConnectedAndHideDeviceCancelSearchOrSearchAgainButton(device_controls);
        }
    }


    public void showDeviceNotFound(DeviceInfo device_info)
    {
        DeviceControls device_controls = getDeviceControls(device_info);

        if (device_controls != null)
        {
            showDeviceNotFound(device_controls);
        }
    }


    public void showDeviceSearchAgainButton(DeviceInfo device_info)
    {
        DeviceControls device_controls = getDeviceControls(device_info);

        if (device_controls != null)
        {
            showDeviceSearchAgainButton(device_controls.buttonCancelSearchOrSearchAgain);
        }
    }


    public void showDeviceFound(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__TEMPERATURE:
            {
                showDeviceFound(device_controls_thermometer, getResources().getString(R.string.textThermometerFound));
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                showDeviceFound(device_controls_pulse_ox, getResources().getString(R.string.textPulseOximeterFound));
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                showDeviceFound(device_controls_blood_pressure, getResources().getString(R.string.textBloodPressureFound));
            }
            break;
        }
    }


    public void showDevicePairing(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__TEMPERATURE:
            {
                showDevicePairing(device_controls_thermometer, getResources().getString(R.string.textTherometerPairing));
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                showDevicePairing(device_controls_pulse_ox, getResources().getString(R.string.textPulseOximeterPairing));
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                showDevicePairing(device_controls_blood_pressure, getResources().getString(R.string.textBloodPressurePairing));
            }
            break;
        }
    }


    public void showDevicePairingFailed(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__TEMPERATURE:
            {
                showDevicePairingFailed(device_controls_thermometer, getResources().getString(R.string.textThermometerPairingFailed));
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                showDevicePairingFailed(device_controls_pulse_ox, getResources().getString(R.string.textPulseOximeterPairingFailed));
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                showDevicePairingFailed(device_controls_blood_pressure, getResources().getString(R.string.textBloodPressurePairingFailed));
            }
            break;
        }
    }


    public void showDevicePaired(DeviceInfo device_info)
    {
        switch(device_info.sensor_type)
        {
            case SENSOR_TYPE__TEMPERATURE:
            {
                showDevicePaired(device_controls_thermometer, getResources().getString(R.string.textThermometerConnected));
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                showDevicePaired(device_controls_pulse_ox, getResources().getString(R.string.textPulseOximeterPairedNowConnecting));
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                showDevicePaired(device_controls_blood_pressure, getResources().getString(R.string.textBloodPressureConnected));
            }
            break;
        }
    }


    private void showDeviceScanStarted(DeviceControls device_controls, String string)
    {
        setDeviceProgressBarValue(device_controls, 0);

        setProgressBarColour(device_controls, Color.BLUE);

        setTextViewSearchStatus(device_controls, string);
    }


    private void showDeviceFound(DeviceControls device_controls, String string)
    {
        setDeviceProgressBarValue(device_controls, 100);

        setProgressBarColour(device_controls, Color.YELLOW);

        setTextViewSearchStatus(device_controls, string);
    }


    private void showDeviceConnectedAndHideDeviceCancelSearchOrSearchAgainButton(DeviceControls device_controls)
    {
        setDeviceProgressBarValue(device_controls, 100);

        setProgressBarColour(device_controls, Color.GREEN);

        setTextViewSearchStatus(device_controls, device_controls.string_device_connected);

        hideDeviceCancelSearchOrSearchAgainButton(device_controls);
    }


    private void showDeviceFirmwareUpdate(DeviceControls device_controls, String string, int percentage)
    {
        setDeviceProgressBarValue(device_controls, percentage);

        setProgressBarColour(device_controls, Color.GREEN);

        setTextViewSearchStatus(device_controls, string);
    }


    private void showDeviceNotFound(DeviceControls device_controls)
    {
        setDeviceProgressBarValue(device_controls, 100);

        setProgressBarColour(device_controls, Color.RED);

        setTextViewSearchStatus(device_controls, device_controls.string_device_not_connected);
    }


    private void showDeviceSearchAgainButton(Button button)
    {
        if (button != null)
        {
            button.setVisibility(View.VISIBLE);
        }
    }


    private void showDevicePairing(DeviceControls device_controls, String string)
    {
        setProgressBarColour(device_controls, Color.CYAN);

        setTextViewSearchStatus(device_controls, string);
    }


    private void showDevicePairingFailed(DeviceControls device_controls, String string)
    {
        setProgressBarColour(device_controls, Color.RED);

        setTextViewSearchStatus(device_controls, string);
    }


    private void showDevicePaired(DeviceControls device_controls, String string)
    {
        setProgressBarColour(device_controls, Color.BLUE);

        setTextViewSearchStatus(device_controls, string);
    }


    private void resetDeviceSearchProgressBar(DeviceControls device_controls)
    {
        setDeviceProgressBarValue(device_controls, 0);

        if (getContext() != null)
        {
            if (device_controls.progressBar != null)
            {
                device_controls.progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.progress_bar_background_colour));
            }

            setProgressBarColour(device_controls, ContextCompat.getColor(getContext(), R.color.progress_bar_colour));
        }
    }


    private DeviceControls getDeviceControls(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                return device_controls_lifetouch;
            }

            case SENSOR_TYPE__TEMPERATURE:
            {
                return device_controls_thermometer;
            }

            case SENSOR_TYPE__SPO2:
            {
                return device_controls_pulse_ox;
            }

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                return device_controls_blood_pressure;
            }

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                return device_controls_scales;
            }
        }

        return null;
    }


    public void hideAllSearchAgainButtons()
    {
        Log.d(TAG, "hideAllSearchAgainButtons : Hiding all 'Search Again' buttons");

        hideDeviceCancelSearchOrSearchAgainButton(device_controls_lifetouch);

        hideDeviceCancelSearchOrSearchAgainButton(device_controls_thermometer);

        hideDeviceCancelSearchOrSearchAgainButton(device_controls_pulse_ox);

        hideDeviceCancelSearchOrSearchAgainButton(device_controls_blood_pressure);

        hideDeviceCancelSearchOrSearchAgainButton(device_controls_scales);
    }


    private void hideDeviceCancelSearchOrSearchAgainButton(DeviceControls device_controls)
    {
        if(device_controls.buttonCancelSearchOrSearchAgain != null)
        {
            device_controls.buttonCancelSearchOrSearchAgain.setVisibility(View.INVISIBLE);
        }
    }


    public void showFirmwareUpdate(DeviceInfo device_info, int percentage)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            showDeviceFirmwareUpdate(device_controls, getResources().getString(R.string.firmware_updating), percentage);
        }
    }


    private void setProgressBarColour(DeviceControls device_controls, int colour)
    {
        if (device_controls.progressBar != null)
        {
            device_controls.progressBar.setProgressColor(colour);
        }
    }


    private void setTextViewSearchStatus(DeviceControls device_controls, String string)
    {
        if (device_controls.textViewSearchStatus != null)
        {
            device_controls.textViewSearchStatus.setText(string);
        }
    }
}
