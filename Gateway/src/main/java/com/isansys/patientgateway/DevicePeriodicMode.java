package com.isansys.patientgateway;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class DevicePeriodicMode
{
    // Interface to call back to the Patient Gateway when the count down counter hits zero
    public interface DevicePeriodicModeEvents
    {
        void periodicMode_enterDeviceSetupMode();
        void periodicMode_exitDeviceSetupMode();
    }

    private final String TAG = this.getClass().getSimpleName();
    private final RemoteLogging Log;
    
    private int period_time_in_seconds;                                                 // How often Setup mode is triggered in Periodic mode
    private int active_time_in_seconds;                                                 // How long it runs for once triggered
    
    private int count_down_counter;
    
    private boolean enabled;
    
    private final DevicePeriodicModeEvents device_periodic_mode_events;
    
    public enum PeriodicModeState
    {
        PERIOD,
        ACTIVE
    }
    
    private PeriodicModeState current_state;
            
    public DevicePeriodicMode(RemoteLogging logger, DevicePeriodicModeEvents event)
    {
        Log = logger;
        device_periodic_mode_events = event;

        disable();
    }

    
    public void enable(int desired_period_time_in_seconds, int desired_active_time_in_seconds)
    {
        enabled = true;

        period_time_in_seconds = desired_period_time_in_seconds;                        // How often Setup mode is triggered in Periodic mode
        active_time_in_seconds = desired_active_time_in_seconds;                        // How long it runs for once triggered

        Log.i(TAG, "enable : period_time_in_seconds = " + period_time_in_seconds + " : active_time_in_seconds = " + active_time_in_seconds);

        setupForPeriodicMode();
    }


    public void disable()
    {
        enabled = false;

        Log.i(TAG, "disable");
    }


    public boolean isEnabled()
    {
        return enabled;
    }
    
    
    private void setupForPeriodicMode()
    {
        count_down_counter = period_time_in_seconds - active_time_in_seconds;           // Make sure we run the right period
        current_state = PeriodicModeState.PERIOD;
    }
    
    
    private void setupForActiveMode()
    {
        count_down_counter = active_time_in_seconds;
        current_state = PeriodicModeState.ACTIVE;
    }
    
    
    // This should be called every second
    public void tick()
    {
        count_down_counter--;
        
        if (count_down_counter <= 0)
        {
            if (current_state == PeriodicModeState.PERIOD)
            {
                // Period has finished. Goto Active
                device_periodic_mode_events.periodicMode_enterDeviceSetupMode();

                // Setup for Active mode
                setupForActiveMode();
            }
            else
            {
                // Active has finished. Goto Period
                device_periodic_mode_events.periodicMode_exitDeviceSetupMode();
                
                // Setup for Period mode
                setupForPeriodicMode();
            }

            Log.i(TAG, "current_state = " + current_state.toString() + " : count_down_counter = " + count_down_counter);
        }
    }
}
