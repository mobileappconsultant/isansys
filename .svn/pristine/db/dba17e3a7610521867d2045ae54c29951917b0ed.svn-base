package com.isansys.patientgateway;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.isansys.common.enums.RealTimeServer;

public class Settings
{
    public final String NOT_SET_YET;
    public final int NOT_SET_YET_INT;
    private final ContextInterface gateway_context_interface;

    public Settings(ContextInterface context_interface)
    {
        gateway_context_interface = context_interface;

        NOT_SET_YET = gateway_context_interface.getAppContext().getResources().getString(R.string.not_set_yet);
        NOT_SET_YET_INT = -1;
    }


    public void resetToDefaults()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    
    public String getGatewaysAssignedBedId()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("gateways_assigned_bed_id", NOT_SET_YET);
    }

    
    public String getGatewaysAssignedBedName()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("gateways_assigned_bed_name", NOT_SET_YET);
    }
    
    
    public String getGatewaysAssignedWardName()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("gateways_assigned_ward_name", NOT_SET_YET);
    }


    public boolean isGatewayAssignedWardAndBedSet()
    {
        return (isGatewayAssignedWardNameSet() && isGatewayAssignedBedNameSet());
    }


    private boolean isGatewayAssignedWardNameSet()
    {
        return !getGatewaysAssignedWardName().equals(NOT_SET_YET);
    }


    private boolean isGatewayAssignedBedNameSet()
    {
        return !getGatewaysAssignedBedName().equals(NOT_SET_YET);
    }


    public void storeGatewayAssignedBedDetails(String bed_id, String ward_name, String bed_name)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("gateways_assigned_bed_id", bed_id);
        editor.putString("gateways_assigned_ward_name", ward_name);
        editor.putString("gateways_assigned_bed_name", bed_name);
        editor.commit();
    }

    
    public String getServerAddress()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("server_address", "185.55.62.6");
    }
    
    
    public void storeServerAddress(String server_address)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server_address", server_address);
        editor.commit();
    }


    public boolean isServerAddressSet()
    {
        return !getServerAddress().equals(NOT_SET_YET);
    }


    public String getServerPort()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("server_port", "80");
    }


    public void storeServerPort(String server_port)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server_port", server_port);
        editor.commit();
    }


    public String getRealTimeServerPort()
    {
        if(getRealTimeServerType() == RealTimeServer.MQTT)
        {
            return getMqttServerPort();
        }
        else
        {
            return getWampServerPort();
        }
    }

    public void storeRealTimeServerPort(String server_port)
    {
        if(getRealTimeServerType() == RealTimeServer.MQTT)
        {
            storeMqttServerPort(server_port);
        }
        else
        {
            storeWampServerPort(server_port);
        }
    }


    private String getWampServerPort()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("wamp_server_port", "9001");
    }
    
    
    private void storeWampServerPort(String server_port)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("wamp_server_port", server_port);
        editor.commit();
    }


    private String getMqttServerPort()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("mqtt_server_port", "8883");
    }


    private void storeMqttServerPort(String server_port)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("mqtt_server_port", server_port);
        editor.commit();
    }


    public boolean getRealTimeLinkEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("realtime_link_enabled", false);
    }
    
    
    public void storeRealTimeLinkEnableStatus(boolean realtime_link_enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("realtime_link_enabled", realtime_link_enabled);
        editor.commit();
    }
    
    public boolean getHttpsEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("https_enabled", false);
    }
    
    
    public void storeHttpsEnableStatus(boolean https_enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("https_enabled", https_enabled);
        editor.commit();
    }
    
    
    public boolean getWebServiceAuthenticationEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("webservice_authentication_enabled", true);
    }
    
    
    public void storeWebServiceAuthenticationEnabled(boolean webservice_authentication_enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("webservice_authentication_enabled", webservice_authentication_enabled);
        editor.commit();
    }


    public boolean getWebServiceEncryptionEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("webservice_encryption_enabled", true);
    }
    

    public void storeWebServiceEncryptionEnabled(boolean webservice_encryption_enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("webservice_encryption_enabled", webservice_encryption_enabled);
        editor.commit();
    }

    
    public boolean getServerSyncEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("server_sync_enabled", false);
    }
    

    public void storeServerSyncEnableStatus(boolean server_sync_enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("server_sync_enabled", server_sync_enabled);
        editor.commit();
    }

    
    public void storePatientNameLookupEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("patient_id_check_status", enabled);
        editor.commit();
    }

    
    public boolean getPatientNameLookupEnabledStatus()
    {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
    	return preferences.getBoolean("patient_id_check_status", false);
    }
    
    
    public void storePeriodicModeEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("periodic_mode_enabled_status", enabled);
        editor.commit();
    }
    
    
    public boolean getPeriodicModeEnabledStatus()
    {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
    	return preferences.getBoolean("periodic_mode_enabled_status", false);
    }
    
    
    public boolean getCsvOutputEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("csv_output_enabled", false);
    }
    
    
    public void storeCsvOutputEnableStatus(boolean csv_output_enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("csv_output_enabled", csv_output_enabled);
        editor.commit();
    }


    public int getNumberOfDatabaseRowsPerJsonMessage()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("number_of_database_rows_per_json_message", 500);
    }


    public void storeNumberOfDatabaseRowsPerJsonMessage(int number_of_database_rows_per_json_message)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("number_of_database_rows_per_json_message", number_of_database_rows_per_json_message);
        editor.commit();
    }


    public int getNumberOfDummyDataModeMeasurementsPerTick()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("number_of_dummy_data_mode_measurements_per_tick", 1);
    }


    public void storeDummyDataModeMeasurementsPerTick(int number_of_dummy_data_mode_measurements_per_tick)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("number_of_dummy_data_mode_measurements_per_tick", number_of_dummy_data_mode_measurements_per_tick);
        editor.commit();
    }
    
    
    public int getSetupModeTimeInSeconds()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("setup_mode_time_in_seconds", 60);
    }
    
    
    public void storeSetupModeTimeInSeconds(int time_in_seconds)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("setup_mode_time_in_seconds", time_in_seconds);
        editor.commit();
    }
    

    public int getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("MaxNumberNoninPulseOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid", 55);
    }
    
    
    public void storeMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(int number)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details 
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("MaxNumberNoninPulseOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid", number);
        editor.commit();
    }


    public int getDisplayTimeoutLengthInSeconds()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("DisplayTimeoutLengthInSeconds", 120);
    }


    public boolean getDisplayTimeoutAppliesToPatientVitals()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("DisplayTimeoutAppliesToPatientVitals", false);
    }


    public void storeDisplayTimeoutLengthInSeconds(int number)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("DisplayTimeoutLengthInSeconds", number);
        editor.commit();
    }


    public void storeDisplayTimeoutAppliesToPatientVitals(boolean applies)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DisplayTimeoutAppliesToPatientVitals", applies);
        editor.commit();
    }


    public boolean getRunDevicesInTestMode()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("run_devices_in_test_mode", false);
    }


    public void storeRunDevicesInTestMode(boolean enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("run_devices_in_test_mode", enabled);
        editor.commit();
    }


    public void storeEnableManualVitalSignsEntry(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_manual_vital_signs_entry", enabled);
        editor.apply();
    }
    
    
    public boolean getEnableManualVitalSignsEntry()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_manual_vital_signs_entry", true);
    }


    public void storeSpO2LongTermMeasurementTimeoutInMinutes(int timeout)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("spo2_long_term_measurement_timeout", timeout);
        editor.apply();
    }

    public int getSpO2LongTermMeasurementTimeoutInMinutes()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("spo2_long_term_measurement_timeout", 60);
    }


    public void storeBloodPressureLongTermMeasurementTimeoutInMinutes(int timeout)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("blood_pressure_long_term_measurement_timeout", timeout);
        editor.apply();
    }

    public int getBloodPressureLongTermMeasurementTimeoutInMinutes()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("blood_pressure_long_term_measurement_timeout", 60);
    }


    public void storeWeightLongTermMeasurementTimeoutInMinutes(int timeout)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("weight_long_term_measurement_timeout", timeout);
        editor.apply();
    }

    public int getWeightLongTermMeasurementTimeoutInMinutes()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("weight_long_term_measurement_timeout", 60);
    }


    public void storeThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes(int timeout)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("third_party_temperature_long_term_measurement_timeout", timeout);
        editor.apply();
    }

    public int getThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("third_party_temperature_long_term_measurement_timeout", 60);
    }


    public boolean getUnpluggedOverlayEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("unplugged_overlay_enabled_status", true);
    }


    public void storeUnpluggedOverlayEnableStatus(boolean enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unplugged_overlay_enabled_status", enabled);
        editor.commit();
    }

    public boolean getLT3KHzSetupModeEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("lt3_khz_setup_mode_enabled_status", true);
    }


    public void storeLT3KHzSetupModeEnableStatus(boolean enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("lt3_khz_setup_mode_enabled_status", enabled);
        editor.commit();
    }


    public boolean getAutoEnableEwsEnabledStatus()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("auto_ews_enable", true);
    }


    public void storeAutoEnableEwsEnableStatus(boolean enabled)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("auto_ews_enable", enabled);
        editor.commit();
    }


    public boolean getDisableCommentsForSpeed()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("disable_comments_for_speed", false);
    }

    public void storeDisableCommentsForSpeed(boolean disable)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disable_comments_for_speed", disable);
        editor.commit();
    }


    public boolean getManufacturingModeEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("manufacturing_mode", false);
    }

    public void storeManufacturingModeEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("manufacturing_mode", enabled);
        editor.commit();
    }


    public boolean getSimpleHeartRateEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("simple_heart_rate", false);
    }

    public void storeSimpleHeartRateEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("simple_heart_rate", enabled);
        editor.commit();
    }


    public boolean getGsmOnlyModeEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("gsm_only_mode", false);
    }

    public void storeGsmModeOnlyEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("gsm_only_mode", enabled);
        editor.commit();
    }


    public void storeShowMacAddressOnSettingsPage(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("show_mac_address_on_settings_page", enabled);
        editor.apply();
    }


    public boolean getShowMacAddressOnSettingsPage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("show_mac_address_on_settings_page", false);
    }


    public void storeUsaOnlyMode(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("usa_only_mode", enabled);
        editor.apply();
    }


    public boolean getUsaOnlyMode()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("usa_only_mode", false);
    }


    public void storeShowLifetouchActivityLevel(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("lifetouch_activity_level", enabled);
        editor.apply();
    }


    public boolean getShowLifetouchActivityLevel()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("lifetouch_activity_level", true);
    }


    public void storeEnablePatientOrientation(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_patient_orientation", enabled);
        editor.apply();
    }


    public boolean getEnablePatientOrientation()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_patient_orientation", true);
    }


    public void storePatientOrientationMeasurementIntervalInSeconds(int time_in_seconds)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("patient_orientation_measurement_interval", time_in_seconds);
        editor.apply();
    }


    public int getPatientOrientationMeasurementIntervalInSeconds()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("patient_orientation_measurement_interval", 60);
    }


    public void storeShowNumbersOnBatteryIndicator(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("show_numbers_on_battery_indicator", enabled);
        editor.apply();
    }


    public boolean getShowNumbersOnBatteryIndicator()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("show_numbers_on_battery_indicator", true);
    }


    public void storeLifetempMeasurementInterval(int time_in_seconds)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lifetemp_measurement_interval", time_in_seconds);
        editor.apply();
    }


    public int getLifetempMeasurementInterval()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("lifetemp_measurement_interval", 60);
    }


    public void storeUseBackCameraEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("use_back_camera", enabled);
        editor.apply();
    }


    public boolean getUseBackCameraEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("use_back_camera", false);
    }


    public void storeDeveloperPopupEnabled(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("developer_popup_enabled", enabled);
        editor.apply();
    }

    public boolean getDeveloperPopupEnabled()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("developer_popup_enabled", false);
    }


    public void storeShowIpAddressOnWifiPopupEnabled(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("show_ip_address_on_wifi_popup_enabled", enabled);
        editor.apply();
    }

    public boolean getShowIpAddressOnWifiPopupEnabled()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("show_ip_address_on_wifi_popup_enabled", true);
    }


    public void storePercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int number)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("PercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid", number);
        editor.commit();
    }


    public int getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("PercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid", 10);
    }


    public void setAutoResumeEnabled(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("session_auto_resume", enabled);
        editor.apply();
    }


    public boolean getAutoResumeEnabled()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("session_auto_resume", false);
    }


    public void storeInstallationComplete(boolean complete)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("InstallationComplete", complete);
        editor.commit();
    }


    public boolean getInstallationComplete()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("InstallationComplete", false);
    }


    public void storeEnableAutoUploadLogsToServer(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_auto_upload_logs_to_server", enabled);
        editor.apply();
    }


    public boolean getEnableAutoUploadLogsToServer()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_auto_upload_logs_to_server", false);
    }


    public int getPeriodicModeActiveTimeInSeconds()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("periodic_mode_active_time_in_seconds", 60);
    }


    public void storePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("periodic_mode_active_time_in_seconds", time_in_seconds);
        editor.commit();
    }


    public int getPeriodicModePeriodTimeInSeconds()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("periodic_mode_period_time_in_seconds", 60 * 60);
    }


    public void storePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("periodic_mode_period_time_in_seconds", time_in_seconds);
        editor.commit();
    }


    public boolean getEnableWifiLogging()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_wifi_logging", true);
    }
    public void storeEnableWifiLogging(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_wifi_logging", enable);
        editor.commit();
    }


    public boolean getEnableGsmLogging()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_gsm_logging", true);
    }
    public void storeEnableGsmLogging(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_gsm_logging", enable);
        editor.commit();
    }


    public boolean getEnableDatabaseLogging()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_database_logging", true);
    }
    public void storeEnableDatabaseLogging(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_database_logging", enable);
        editor.commit();
    }


    public boolean getEnableServerLogging()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_server_logging", true);
    }
    public void storeEnableServerLogging(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_server_logging", enable);
        editor.commit();
    }


    public boolean getEnableBatteryLogging()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_battery_logging", true);
    }
    public void storeEnableBatteryLogging(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_battery_logging", enable);
        editor.commit();
    }


    public boolean getEnableDfuBootloader()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_dfu_bootloader", true);
    }
    public void storeEnableDfuBootloader(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_dfu_bootloader", enable);
        editor.commit();
    }

    public boolean getEnableSpO2SpotMeasurements()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_spo2_spot_measurements", false);
    }
    public void storeEnableSpO2SpotMeasurements(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_spo2_spot_measurements", enable);
        editor.commit();
    }

    public boolean getEnablePredefineAnnotations()
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("enable_predefined_annotations", false);
    }
    public void storeEnablePredefinedAnnotations(boolean enable)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_predefined_annotations", enable);
        editor.commit();
    }


    public boolean getSoftwareUpdateModeActive()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("software_update_mode_active", false);
    }


    public void storeSoftwareUpdateModeActive(boolean active)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("software_update_mode_active", active);
        editor.commit();
    }


    public int getGatewayServerSettingsVersion()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getInt("server_settings_downloaded_as_of_gateway_version", NOT_SET_YET_INT);
    }


    public void storeGatewayServerSettingsVersion(int value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("server_settings_downloaded_as_of_gateway_version", value);
        editor.commit();
    }


    public String getFirmwareImages()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getString("firmware_images_available_for_update", "[]");
    }


    public void storeFirmwareImages(String firmware_images_as_string)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firmware_images_available_for_update", firmware_images_as_string);
        editor.commit();
    }


    public boolean getVideoCallsEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("video_calls", false);
    }


    public void storeVideoCallsEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("video_calls", enabled);
        editor.commit();
    }


    public boolean getViewWebPagesEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("view_webpages", false);
    }


    public void storeViewWebPagesEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("view_webpages", enabled);
        editor.commit();
    }


    public boolean getDisplayTemperatureInFahrenheitEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("display_temperature_in_fahrenheit", true);
    }


    public void storeDisplayTemperatureInFahrenheitEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("display_temperature_in_fahrenheit", enabled);
        editor.commit();
    }


    public boolean getDisplayWeightInLbsEnabledStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        return preferences.getBoolean("display_weight_in_lbs", false);
    }


    public void storeDisplayWeightInLbsEnabledStatus(boolean enabled)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("display_weight_in_lbs", enabled);
        editor.commit();
    }


    public RealTimeServer getRealTimeServerType()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        int type = preferences.getInt("realtime_server_type", RealTimeServer.MQTT.ordinal());
        return RealTimeServer.values()[type];
    }


    public void setRealTimeServerType(RealTimeServer type)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gateway_context_interface.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("realtime_server_type", type.ordinal());
        editor.commit();
    }
}
