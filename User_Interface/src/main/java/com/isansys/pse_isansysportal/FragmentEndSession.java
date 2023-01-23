package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import java.util.Timer;
import java.util.TimerTask;

public class FragmentEndSession extends FragmentIsansys implements OnClickListener
{
    private static final String TAG = FragmentEndSession.class.getName();

    private Button buttonEndSession;
    private Button buttonTransferSession;
    private Animation end_session_scale_animation = null;
    
    private LinearLayout linearLayout_deviceMeasurementsPending;
    private LinearLayout linearLayout_progressBarAndTextViewDeviceMeasurementPending;
    private Button buttonCancelPendingTransfer;
    private ProgressBar deviceStatusProgressBar;
    private TextView textViewBLEDeviceDisconnectionStatus;
    
    private TextView textViewWarningDevicesPendingMeasurements;
    private TextView textAreYouSure;
    private TimerTask progressBarTimerTask;
    private Timer progressBarFixedTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.end_session, container, false);

        buttonEndSession = v.findViewById(R.id.buttonEndSession);
        buttonTransferSession = v.findViewById(R.id.buttonTransferSession);
        
        buttonCancelPendingTransfer = v.findViewById(R.id.buttonCancelLifetouchPendingHeartBeatTransfer);
        deviceStatusProgressBar = v.findViewById(R.id.progressBarPendingHeartBeatEndDevice);
        textViewBLEDeviceDisconnectionStatus = v.findViewById(R.id.textViewDeviceDisconnectionStatus);

        textViewWarningDevicesPendingMeasurements = v.findViewById(R.id.textView_warningDevicePendingMeasurement);
        textViewWarningDevicesPendingMeasurements.setVisibility(View.INVISIBLE);
        
        textAreYouSure = v.findViewById(R.id.textAreYouSure);
        textAreYouSure.setVisibility(View.VISIBLE);
        
        linearLayout_progressBarAndTextViewDeviceMeasurementPending = v.findViewById(R.id.LinearLayoutProgressBarAndTextViewDeviceMeasurementPending);
        linearLayout_progressBarAndTextViewDeviceMeasurementPending.setVisibility(View.INVISIBLE);
        linearLayout_deviceMeasurementsPending = v.findViewById(R.id.linearLayout_deviceMeasurementsPending);
        linearLayout_deviceMeasurementsPending.setVisibility(View.GONE);

        if(getActivity() != null)
        {
            end_session_scale_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation);
        }
        else
        {
            Log.e(TAG,"onCreateView : FragmentEndSession getActivity() is null");
        }

        buttonEndSession.setOnClickListener(this);

        buttonTransferSession.setOnClickListener(this);
        
        return v;
    }


    @Override
    public void onStop()
    {
        super.onStop();

        main_activity_interface.checkAndCancel_timer(progressBarFixedTimer);

        main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        if(end_session_scale_animation == null)
        {
            if(getActivity() != null)
            {
                end_session_scale_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation);
            }
            else
            {
                Log.e(TAG, "onResume : FragmentEndSession getActivity() is null");
            }
        }

        buttonEndSession.setAnimation(end_session_scale_animation);

        DeviceInfo device_info__lifetouch = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
        DeviceInfo device_info__thermometer = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE);

        if(device_info__lifetouch.isActualDeviceConnectionStatusDisconnected() || (device_info__thermometer.isActualDeviceConnectionStatusDisconnected() && device_info__thermometer.device_type != DeviceType.DEVICE_TYPE__FORA_IR20))
        {
            linearLayout_deviceMeasurementsPending.setVisibility(View.VISIBLE);
            linearLayout_progressBarAndTextViewDeviceMeasurementPending.setVisibility(View.INVISIBLE);
            buttonCancelPendingTransfer.setVisibility(View.INVISIBLE);

            textViewWarningDevicesPendingMeasurements.setVisibility(View.VISIBLE);
        }
        else
        {
            // If Lifetouch is connected and measurements are pending, display and update the progressBar and its textView.
            if((device_info__lifetouch.isActualDeviceConnectionStatusConnected()) && (device_info__lifetouch.getMeasurementsPending() > 0))
            {
                //Setting the ProgressBar and number of pending heartbeat textView to VISIBLE
                linearLayout_deviceMeasurementsPending.setVisibility(View.VISIBLE);
                linearLayout_progressBarAndTextViewDeviceMeasurementPending.setVisibility(View.VISIBLE);
                buttonCancelPendingTransfer.setVisibility(View.INVISIBLE);
                textViewWarningDevicesPendingMeasurements.setVisibility(View.INVISIBLE);

                deviceStatusProgressBar.setMax(device_info__lifetouch.getMeasurementsPending());
                textViewBLEDeviceDisconnectionStatus.setText(R.string.string_waitForLifetouchPendingHeartBeat);

                main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);
                progressBarTimerTask = new TimerTask() {

                    @Override
                    public void run()
                    {
                        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
                        String string = getString(R.string.string_waitForLifetouchPendingHeartBeat);

                        updateProgressBarAndTextViewForMeasurementsPending(device_info, string);
                    }
                };

                main_activity_interface.checkAndCancel_timer(progressBarFixedTimer);
                progressBarFixedTimer = new Timer();
                progressBarFixedTimer.scheduleAtFixedRate(progressBarTimerTask, 0, DateUtils.SECOND_IN_MILLIS);
            }
            // If Lifetemp is connected and measurements are pending then display this
            else if((device_info__thermometer.isActualDeviceConnectionStatusConnected()) && (device_info__thermometer.getMeasurementsPending() > 0))
            {
                //Setting the ProgressBar and number of pending heartbeat textView to VISIBLE
                linearLayout_deviceMeasurementsPending.setVisibility(View.VISIBLE);
                linearLayout_progressBarAndTextViewDeviceMeasurementPending.setVisibility(View.VISIBLE);
                buttonCancelPendingTransfer.setVisibility(View.INVISIBLE);
                textViewWarningDevicesPendingMeasurements.setVisibility(View.INVISIBLE);

                deviceStatusProgressBar.setMax(device_info__thermometer.getMeasurementsPending());
                textViewBLEDeviceDisconnectionStatus.setText(R.string.string_waitForLifetempPendingMeasurement);

                main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);
                progressBarTimerTask = new TimerTask() {

                    @Override
                    public void run()
                    {
                        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE);
                        String string = getString(R.string.string_waitForLifetempPendingMeasurement);

                        updateProgressBarAndTextViewForMeasurementsPending(device_info, string);
                    }
                };

                main_activity_interface.checkAndCancel_timer(progressBarFixedTimer);
                progressBarFixedTimer = new Timer();
                progressBarFixedTimer.scheduleAtFixedRate(progressBarTimerTask, 0, DateUtils.SECOND_IN_MILLIS);
            }
            else
            {
                Log.d(TAG, "onResume : No pending data remained to download");
            }
        }
    }


    private void updateProgressBarAndTextViewForMeasurementsPending(final DeviceInfo device_info, final String display_string)
    {
        if(getActivity() != null)
        {
            // Update the ProgressBar and textView main UI thread
            getActivity().runOnUiThread(() -> {
                try
                {
                    int measurement_pending_to_be_received = device_info.getMeasurementsPending();

                    Log.d(TAG, "updateProgressBarAndTextViewForMeasurementsPending " + device_info.sensor_type + " : " + measurement_pending_to_be_received);

                    if(measurement_pending_to_be_received <= 0)
                    {
                        // Cancel the previous timer task(Pending heart beat) and create new one for disconnection
                        main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);

                        deviceStatusProgressBar.setProgress(0);
                        deviceStatusProgressBar.setVisibility(View.INVISIBLE);
                        textViewBLEDeviceDisconnectionStatus.setVisibility(View.INVISIBLE);
                        linearLayout_progressBarAndTextViewDeviceMeasurementPending.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        deviceStatusProgressBar.setProgress(measurement_pending_to_be_received);

                        String string = display_string + " " + measurement_pending_to_be_received;
                        textViewBLEDeviceDisconnectionStatus.setText(string);
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "updateProgressBarAndTextViewForMeasurementsPending : " + device_info.sensor_type + " could not update due to error");
                    Log.e(TAG, e.toString());
                }
            });
        }
        else
        {
            Log.e(TAG,"updateProgressBarAndTextViewForMeasurementsPending : " + device_info.sensor_type + " getActivity() is NULL");
        }
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonEndSession)
        {
            Log.d(TAG, "buttonEndSession : end session pressed");

            sessionEndButtonPressed(true);
        }
        else if (id == R.id.buttonTransferSession)
        {
            Log.d(TAG, "buttonTransferSession : transfer session pressed");

            sessionEndButtonPressed(false);
        }
    }


    private void sessionEndButtonPressed(boolean send_turn_off_command)
    {
        DeviceInfo device_info_lifetouch = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
        DeviceInfo device_info_thermometer = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE);

        // Check lifetouch device status. Check if device is added to the session
        if (device_info_lifetouch.isDeviceHumanReadableDeviceIdValid() || device_info_thermometer.isDeviceHumanReadableDeviceIdValid())
        {
            Log.d(TAG, "buttonEndSession : Lifetouch or lifetemp added to Gateway");

            // Lifetemp added to Gateway
            if (device_info_thermometer.isDeviceHumanReadableDeviceIdValid())
            {
                Log.d(TAG, "buttonEndSession : Lifetemp added to Gateway");

                // Check if Lifetouch is connected to Gateway
                if(device_info_lifetouch.getActualDeviceConnectionStatus() != DeviceConnectionStatus.CONNECTED)
                {
                    // Lifetouch is temporary disconnected. Need to check other devices
                    Log.d(TAG, "buttonEndSession : Ending Session, Lifetouch is temporary disconnected. Check other devices");

                    // Check if Lifetemp is connected to Gateway
                    if (device_info_thermometer.getActualDeviceConnectionStatus() != DeviceConnectionStatus.CONNECTED)
                    {
                        Log.d(TAG, "buttonEndSession : Ending Session, Lifetemp is temporary disconnected.");

                        // Both Lifetouch and Lifetemp are temporary disconnected, show dialog box
                        showEndSessionDialogBoxForDisconnectedDevices(send_turn_off_command);
                    }
                    else
                    {
                        Log.d(TAG, "buttonEndSession :  Lifetouch is disconnected but Lifetemp is still connected so wait till pending measurements are downloaded");

                        // Lifetouch is disconnected (or a dummy device). Wait for Lifetemp pending measurements
                        checkLifetempPendingMeasurementsAndDisconnect(send_turn_off_command);
                    }
                }
                else
                {
                    Log.d(TAG, "buttonEndSession : Ending Session, Lifetouch is Connected. Check other devices");

                    // Lifetouch is connected. Check if Lifetemp is also disconnected
                    if ((device_info_thermometer.getActualDeviceConnectionStatus() != DeviceConnectionStatus.CONNECTED) || (device_info_thermometer.dummy_data_mode))
                    {
                        Log.d(TAG, "buttonEndSession : Lifetemp is disconnected");

                        // Lifetemp is disconnected (or a dummy device). No need to wait for Lifetemp reconnection
                        checkLifetouchPendingHeartBeatAndDisconnect(send_turn_off_command);
                    }
                    else
                    {
                        // Both Lifetemp and Lifetouch are connected. Disconnect Lifetouch first then Lifetemp
                        Log.d(TAG, "buttonEndSession : Ending Session, Both lifetouch and lifetemp are connected. Wait for Lifetouch pending HB and Lifetemp disconnection");

                        if((device_info_lifetouch.isActualDeviceConnectionStatusConnected()) && (device_info_lifetouch.getMeasurementsPending() > 0))
                        {
                            checkLifetouchPendingHeartBeatAndDisconnect(send_turn_off_command);
                        }
                        else
                        {
                            Log.d(TAG, "buttonEndSession :  Lifetouch is disconnected but lifetemp is connected so wait for Gateway to send Turn-off command");

                            checkLifetempPendingMeasurementsAndDisconnect(send_turn_off_command);
                        }
                    }
                }
            }
            else
            {
                Log.d(TAG, "buttonEndSession : Only lifetouch added to Gateway");

                // Check if lifetouch is temporary disconnected
                if(device_info_lifetouch.getActualDeviceConnectionStatus() != DeviceConnectionStatus.CONNECTED)
                {
                    Log.d(TAG, "buttonEndSession : Only lifetouch added and connected");

                    // Lifetouch temporary disconnected, show dialog box
                    showEndSessionDialogBoxForDisconnectedDevices(send_turn_off_command);
                }
                else
                {
                    checkLifetouchPendingHeartBeatAndDisconnect(send_turn_off_command);
                }
            }
        }
        else
        {
            endOrTransferSession(send_turn_off_command);
        }
    }


    /**
     * Function to create Dialog box for end device pressed 
     */
    private void showEndSessionDialogBoxForDisconnectedDevices(final boolean send_turn_off_command)
    {
        if(getActivity() != null)
        {
            // Show dialog box before existing the session
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.string_remove_disconnected_devices_dialog));
            builder.setCancelable(true);

            // If pressed yes then run the disconnection progressBar. This might be case for battery finished.
            builder.setPositiveButton(R.string.stringYes, (dialog, id) -> {
                Log.w(TAG, "showEndSessionDialogBoxForDisconnectedDevices : 'DialogBox Yes pressed' lifetouch/lifetemp temporary disconnected and user want to end/transfer session right now");

                // Hide the progressBar and its textView
                clearDisconnectingDevicesProgressBar();

                // Cancel the previous timer task(Pending heart beat) and create new one for disconnection
                main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);

                // re-enable the footer back button and lock screen button. Time out for User interface lock screen is re-enabled
                main_activity_interface.enableFooterBackButtonAndUserInterfaceTimeout();

                // This booleans check determines if the transfer session or end session button is pressed
                endOrTransferSession(send_turn_off_command);

                dialog.cancel();
            });

            // If pressed cancel then don't do anything
            builder.setNegativeButton(R.string.stringNo, (dialog, id) -> {
                Log.w(TAG, "showEndSessionDialogBoxForDisconnectedDevices : 'DialogBox No pressed' lifetouch/Lifetemp temporary disconnected and user want to reconnect to lifetouch");
                // do nothing
                dialog.cancel();
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            Log.e(TAG,"showEndSessionDialogBoxForDisconnectedDevices : getActivity() is null");
        }
    }
    
    /**
     * Create the dialogBox for ending session with pending measurements
     * @param send_turn_off_command : boolean - 'True' if device connected to Gateway
     */
    private void showEndSessionDialogBoxForPendingMeasurementDevices(final boolean send_turn_off_command)
    {
        Log.d(TAG, "Device connected but pending measurement.");
        if(getActivity() != null)
        {
            // show dialog box before existing the session
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.string_WarningPendingMeasurementButRemoveDevicesTextView));
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.stringYes, (dialog, id) -> {
                Log.d(TAG, "showEndSessionDialogBoxForPendingMeasurementDevices : DialogBox Yes pressed. Download of device pending measurement is ended");

                // Re-enable the footer back button and lock screen button. Time out for User interface lock screen is re-enabled
                main_activity_interface.enableFooterBackButtonAndUserInterfaceTimeout();

                if(deviceStatusProgressBar != null)
                {
                    clearDisconnectingDevicesProgressBar();
                }

                endOrTransferSession(send_turn_off_command);

                dialog.cancel();
            });

            builder.setNegativeButton(R.string.stringNo, (dialog, id) -> {
                Log.w(TAG, "showEndSessionDialogBoxForPendingMeasurementDevices : DialogBox No Pressed. Downloading of device pending measurement");
                // do nothing
                dialog.cancel();
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            Log.e(TAG, "showEndSessionDialogBoxForPendingMeasurementDevices : getActivity() is null");
        }
    }
    

    private void setupDevicePendingDataProgressBar(DeviceInfo device_info, String display_string)
    {
        // Cancel the previous progressBarTimerTask created in onResume()
        main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);
        
        // Main linearLayout hold the warning textView and linearLayout for progressBar + textView
        linearLayout_deviceMeasurementsPending.setVisibility(View.VISIBLE);

        // Warning textView for temporary disconnected Lifetouch
        textViewWarningDevicesPendingMeasurements.setVisibility(View.INVISIBLE);

        // Make visible the linear layout holding progressbar and textView for it
        linearLayout_progressBarAndTextViewDeviceMeasurementPending.setVisibility(View.VISIBLE);
        
        textAreYouSure.setVisibility(View.VISIBLE);
        
        // Make the "End Now" button visible to end session any time user wants
        buttonCancelPendingTransfer.setVisibility(View.VISIBLE);
        
        // Make progressBar and its textView Visible
        deviceStatusProgressBar.setVisibility(View.VISIBLE);
        textViewBLEDeviceDisconnectionStatus.setVisibility(View.VISIBLE);
        textViewBLEDeviceDisconnectionStatus.setText(display_string);
        
        deviceStatusProgressBar.setMax(device_info.getMeasurementsPending());
    }


    private void updateLifetouchProgressBarPendingHeartBeats(final int number_of_measurements_pending_to_receive, final boolean send_turn_off_command)
    {
        if(getActivity() != null)
        {
            // Update the ProgressBar and textView main UI thread
            getActivity().runOnUiThread(() -> {
                try
                {
                    // lifetouch pending heartbeat is zero than start the disconnection procedure
                    if (number_of_measurements_pending_to_receive <= 0)
                    {
                        Log.i(TAG, "updateLifetouchHeartBeatPendingProgressBar : lifetouch pending heart rate == 0");

                        // Hide the progressBar and its textView
                        clearDisconnectingDevicesProgressBar();

                        // Cancel the previous timer task(Pending heart beat) and create new one for disconnection
                        main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);

                        // re-enable the footer back button and lock screen button. Time out for User interface lock screen is re-enabled
                        main_activity_interface.enableFooterBackButtonAndUserInterfaceTimeout();

                        checkLifetempPendingMeasurementsAndDisconnect(send_turn_off_command);
                    }
                    else
                    {
                        // update the progress bar value and its text view with new number of lifetouch pending heart beat
                        Log.d(TAG, "updateLifetouchHeartBeatPendingProgressBar");
                        setLifetouchProgressBarValueAndTextView(number_of_measurements_pending_to_receive);
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "updateLifetempDisconnectingProgressBar: could not update due to error");
                    Log.e(TAG, e.toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "updateLifetouchProgressBarPendingHeartBeats : getActivity() is null");
        }
    }


    /**
     * Function to update the progress bar of Lifetemp pending data measurement
     * @param number_of_measurements_pending_to_receive : int - value of progress bar
     * @param send_turn_off_command : boolean - If true end current session
     */
    private void updateLifetempPendingMeasurement(final int number_of_measurements_pending_to_receive, final boolean send_turn_off_command)
    {
        if(getActivity() != null)
        {
            // Update the ProgressBar and textView in main UI thread
            getActivity().runOnUiThread(() -> {
                try
                {
                    // Check if Lifetemp pending measurement is zero. If so, then start the disconnection procedure
                    if (number_of_measurements_pending_to_receive <= 0)
                    {
                        Log.i(TAG, "updateLifetempPendingMeasurement : Lifetemp pending measurement = " + number_of_measurements_pending_to_receive);

                        // Hide the progressBar and its textView
                        clearDisconnectingDevicesProgressBar();

                        // Cancel the previous timer task(Pending Measurements) and create new one for disconnection
                        main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);

                        // Re-enable the footer back button and lock screen button. Time-out for User interface lock screen is re-enabled
                        main_activity_interface.enableFooterBackButtonAndUserInterfaceTimeout();

                        // This booleans check determines if the transfer session or end session button is pressed
                        endOrTransferSession(send_turn_off_command);
                    }
                    else
                    {
                        // update the progress bar value and its text view with new pending measurement value

                        Log.d(TAG, "updateLifetempPendingMeasurement");
                        setLifetempProgressBarValueAndTextView(number_of_measurements_pending_to_receive);
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "updateLifetempPendingMeasurement: could not update due to error");
                    Log.e(TAG, e.toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "updateLifetempPendingMeasurement : getActivity() is null");
        }
    }

    /**
    * Function to set the progressBar and its textView with given number of pending heart beats
    * @param heart_beat_pending_to_send : int number of pending heart beats
    */
   private void setLifetouchProgressBarValueAndTextView(int heart_beat_pending_to_send)
   {
       // More lifetouch heartBeats pending to send to Gateway
       deviceStatusProgressBar.setProgress(heart_beat_pending_to_send);
       
       // Updating textView with number of heartbeat pending 
       String string = getString(R.string.string_waitForLifetouchPendingHeartBeat) + " " + heart_beat_pending_to_send;
       
       textViewBLEDeviceDisconnectionStatus.setText(string);
   }


    /**
     * Function to set the progressBar and its textView with given number of pending Lifetemp Measurement
     * @param pending_measurement : int - number of pending measurement
     */
    private void setLifetempProgressBarValueAndTextView(int pending_measurement)
    {
        // More lifetouch heartBeats pending to send to Gateway
        deviceStatusProgressBar.setProgress(pending_measurement);

        // Updating textView with number of heartbeat pending
        String string = getString(R.string.string_waitForLifetempPendingMeasurement) + " " + pending_measurement;

        textViewBLEDeviceDisconnectionStatus.setText(string);
    }


   /**
    * Make progressBar and textView for lifetouch pending heartbeat Invisible
    */
     private void clearDisconnectingDevicesProgressBar()
     {
         // Hide progressBar, its textView and "End now" button
         deviceStatusProgressBar.setVisibility(View.INVISIBLE);
         textViewBLEDeviceDisconnectionStatus.setVisibility(View.INVISIBLE);
         textViewBLEDeviceDisconnectionStatus.setText(R.string.textViewLifetouchChangeSessionDisconnect);
         buttonCancelPendingTransfer.setVisibility(View.INVISIBLE);
         
         textAreYouSure.setVisibility(View.INVISIBLE);
         
         linearLayout_deviceMeasurementsPending.setVisibility(View.INVISIBLE);
     }
     
     /**
      * Function to disable the endSession Button, transferSession button, footer Back button and UserInterface timeout
      */
     private void disableFooterBackButtonAndUserInterfaceTimeout()
     {
         Activity activity = getActivity();
         if (activity != null)
         {
             // Lifetouch has pending heartbeats. Wait until all the heartbeats are downloaded. until this happens, all the button to navigate back to session are disabled.
             // Disable the endSession Button. "End Now" has onClickListener to end/transfer session any time user wants
             if(buttonEndSession != null)
             {
                 buttonEndSession.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                 buttonEndSession.setClickable(false);
             }

             if(buttonTransferSession != null)
             {
                 buttonTransferSession.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                 buttonTransferSession.setClickable(false);
             }

             // Restart the screen lock timer, Footer back and lock screen button
             main_activity_interface.endSessionPressed_DisableFooterAndUserInterfaceTimeout();
         }
     }


    private void checkDeviceForPendingDataAndDisconnect(final DeviceInfo device_info, String display_string, final boolean send_turn_off_command)
    {
        if((device_info.isActualDeviceConnectionStatusConnected()) && (device_info.getMeasurementsPending() > 0))
        {
            Log.d(TAG, "checkDeviceForPendingDataAndDisconnect : " + device_info.getSensorTypeAndDeviceTypeAsString() + " Pending data present");

            disableFooterBackButtonAndUserInterfaceTimeout();

            setupDevicePendingDataProgressBar(device_info, display_string);

            main_activity_interface.checkAndCancel_timerTask(progressBarTimerTask);

            // set the timer task and schedule every second
            progressBarTimerTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    int number_of_measurements_pending_to_receive = device_info.getMeasurementsPending();

                    Log.d(TAG, "buttonEndSession : " + device_info.sensor_type + " pending data progress bar update" + number_of_measurements_pending_to_receive);

                    switch (device_info.sensor_type)
                    {
                        case SENSOR_TYPE__LIFETOUCH:
                        {
                            updateLifetouchProgressBarPendingHeartBeats(number_of_measurements_pending_to_receive, send_turn_off_command);
                        }
                        break;

                        case SENSOR_TYPE__TEMPERATURE:
                        {
                            updateLifetempPendingMeasurement(number_of_measurements_pending_to_receive, send_turn_off_command);
                        }
                        break;
                    }
                }
            };

            main_activity_interface.checkAndCancel_timer(progressBarFixedTimer);
            progressBarFixedTimer = new Timer();
            progressBarFixedTimer.scheduleAtFixedRate(progressBarTimerTask, 0, DateUtils.SECOND_IN_MILLIS);

            // If End Now button is pressed then don't wait for pending heart beats
            buttonCancelPendingTransfer.setVisibility(View.VISIBLE);
            buttonCancelPendingTransfer.setOnClickListener(v -> {
                Log.w(TAG, "buttonEndSession : onClick listener triggered. End Session Pressed");
                showEndSessionDialogBoxForPendingMeasurementDevices(send_turn_off_command);
            });
        }
        else
        {
            endOrTransferSession(send_turn_off_command);
        }
    }


    private void checkLifetouchPendingHeartBeatAndDisconnect(final boolean send_turn_off_command)
    {
        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
        String display_string = getString(R.string.string_waitForLifetouchPendingHeartBeat);

        checkDeviceForPendingDataAndDisconnect(device_info, display_string, send_turn_off_command);
    }


    private void checkLifetempPendingMeasurementsAndDisconnect(final boolean send_turn_off_command)
    {
        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE);
        String display_string = getString(R.string.string_waitForLifetempPendingMeasurement);

        checkDeviceForPendingDataAndDisconnect(device_info, display_string, send_turn_off_command);
    }


    private void endOrTransferSession(boolean send_turn_off_command)
    {
        main_activity_interface.setSendTurnOffCommand(send_turn_off_command);
    }
}
