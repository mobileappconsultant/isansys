package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.pse_isansysportal.BuildConfig;


import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AdminPage extends AppiumTest
{
    @Test // Check the set up in admin page by setting dummy data
    public void testManualServerConfiguration() throws IOException
    {
        String case_id = "22671";

        helper.printTestStart("testManualServerConfiguration",case_id);

        // Scan the Feature enable QR code - spoof by sending an intent to the UI
        helper.spoofQrCode_featureEnableUnlock();

        // Click on set dummy data
        driver.findElement(By.id("buttonSetDummyServer")).click();

        clickOnLock();

        //driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Check the Restart Installation Wizard button by clicking on the button

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        //set the display timeout to no timeout in case we take too long
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength",helper.getAppString("no_timeout"));

        clickOnAdminTabServerConnectionSettings();

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderWardName"), "Dummy Ward"), 45);

        //Log("Installation process start");
        String actual_text = driver.findElement(By.id("textHeaderWardName")).getText();
        String expected_text = "Dummy Ward";   // Defined as a "Magic String" in the Patient Gateway

        assertEquals(expected_text, actual_text);

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderBedName"), "Dummy Bed"), 45);

        actual_text = driver.findElement(By.id("textHeaderBedName")).getText();
        expected_text = "Dummy Bed";             // Defined as a "Magic String" in the Patient Gateway

        assertEquals(expected_text, actual_text);

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();

        // Clear the set server address and input the new server address
        WebElement server_address = driver.findElement(By.id("editServerAddress"));
        server_address.clear();
        driver.findElement(By.id("editServerAddress")).sendKeys(helper.getServerAddress()); //using muffin server

        // Click on Set Server button
        driver.findElement(By.id("buttonSetServer")).click();

        // Click on yes from the popup message
        driver.findElement(By.id("android:id/button1")).click();


        expected_text = helper.getAppString("not_set_yet");
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderBedName"), expected_text), 45);

        actual_text = driver.findElement(By.id("textHeaderWardName")).getText();
        assertEquals(expected_text, actual_text);

        actual_text = driver.findElement(By.id("textHeaderBedName")).getText();
        assertEquals(expected_text, actual_text);

        // Check if UseWebServiceAuthentication button is checked
        String webservice_authentication_checked;
        WebElement checkbox_webservice_authentication = driver.findElement(By.id("checkBoxUseWebServiceAuthentication"));
        webservice_authentication_checked = checkbox_webservice_authentication.getAttribute("checked");
        Log("Service Authentication Check box Status " + webservice_authentication_checked);
        if(webservice_authentication_checked.equals("false"))
        {
            checkbox_webservice_authentication.click();
            Log("checkBoxUseWebServiceAuthentication checked");
        }
        else
        {
            Log("checkBoxUseWebServiceAuthentication already checked");
        }

        // Check if checkBox Use WebServiceEncryption is checked
        String webservice_encryption_checked;
        WebElement checkbox_webservice_encryption = driver.findElement(By.id("checkBoxUseWebServiceEncryption"));
        webservice_encryption_checked = checkbox_webservice_encryption.getAttribute("checked");
        Log("ServiceEncryption Check box Status " + webservice_encryption_checked);
        if(webservice_encryption_checked.equals("false"))
        {
            checkbox_webservice_encryption.click();
            Log("checkBoxUseWebServiceEncryption checked");
        }
        else
        {
            Log("checkBoxUseWebServiceEncryption already checked");
        }

        // Click on test link
        driver.findElement(By.id("buttonTestServerLink")).click();

        //Set the value = 80 for server port
        driver.findElement(By.id("editServerPort")).clear();
        driver.findElement(By.id("editServerPort")).sendKeys(helper.getServerPort());  //using muffin server
        driver.findElement(By.id("buttonSetServerPort")).click();

        //Click on yes from the popup message
        driver.findElement(By.id("android:id/button1")).click();

        // Set Real Time port
        driver.findElement(By.id("editRealTimeServerPort")).clear();
        driver.findElement(By.id("editRealTimeServerPort")).sendKeys(helper.getServerRealTimePort()); //using muffin server
        driver.findElement(By.id("buttonSetRealTimeServerPort")).click();

        // Set Bed ID
        driver.findElement(By.id("buttonGetWardsAndBedsFromServer")).click();

        // Click on the Ward List and select the word from the spinner
        helper.clickElementOnSpinner("spinnerWardList", "Test Ward");

        // Select bed list from the spinner
        Log("Selecting bed by computer name: " + BuildConfig.buildMachineName);
        helper.clickElementOnSpinner("spinnerBedList", BuildConfig.buildMachineName);

        // Set bed detail
        driver.findElement(By.id("buttonSetBedDetails")).click();

        // Click on Get Type from Server
        driver.findElement(By.id("buttonGetDefaultEarlyWarningScoringTypesFromServer")).click();

        // Click on Get Configuration from server button
        WebElement button_get_configuration_from_server = driver.findElement(By.id("buttonGetGatewayConfigFromServer"));
        assertTrue(button_get_configuration_from_server.isDisplayed());
        button_get_configuration_from_server.click();

        // Get Server Configurable Text
        WebElement button_get_server_configurable_text = driver.findElementById("buttonGetServerConfigurableTextFromServer");
        assertTrue(button_get_server_configurable_text.isDisplayed());
        button_get_server_configurable_text.click();

        // Check for update firmware button
        WebElement button_check_for_updated_firmware = driver.findElement(By.id("buttonCheckForUpdatedFirmware"));
        assertTrue(button_check_for_updated_firmware.isDisplayed());
        button_check_for_updated_firmware.click();

        // ReceivedUpdateFirmwareVersions
        WebElement received_firmware_versions = driver.findElement(By.id("textReceivedUpdateFirmwareVersions"));
        String text_received_firmware_versions = received_firmware_versions.getText();
        //ToDo: figure out if we can test the value of this at all... since we don't have a fixed expected version number
        Log("Updated firmware versions is : " + text_received_firmware_versions);

        // Check the presence of buttonRestartInstallationWizard
        WebElement restart_installation_wizard_button = driver.findElement(By.id("buttonRestartInstallationWizard"));
        assertTrue(restart_installation_wizard_button.isDisplayed());

        // To check and Click on the server syncing
        String server_syncing_enabled_checked;
        WebElement checkbox_server_syncing_enabled = driver.findElement(By.id("checkBoxServerDataSyncEnabled"));
        server_syncing_enabled_checked = checkbox_server_syncing_enabled.getAttribute("checked");
        Log("Server Syncing Enable Check box Status " + server_syncing_enabled_checked);
        if(server_syncing_enabled_checked.equals("false"))
        {
            checkbox_server_syncing_enabled.click();
            Log("checkBoxServerDataSyncEnabled checked");
        }
        else
        {
            Log("checkBoxServerDataSyncEnabled already checked");
        }

        // Check the appearance of force Installation Complete button
        WebElement button_force_installation_complete = driver.findElement(By.id("buttonForceInstallationComplete"));
        assertTrue(button_force_installation_complete.isDisplayed());
        helper.captureScreenShot();

        // Show the Gateway Settings tab on the Admin page
        clickOnAdminTabGatewaySettings();

        //set the display timeout back to 2 to minutes
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength",helper.getAppString("two_minutes"));

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();

        // Re-find it as the page has changed
        button_force_installation_complete = driver.findElement(By.id("buttonForceInstallationComplete"));

        // Click on Force Installation Complete
        button_force_installation_complete.click();
        helper.updateTestRail(PASSED);

        //ToDo: check we end up on main page?
    }


    @Test
    public void checkReinstallationProcessFromAdminPage() throws IOException
    {
        String case_id = "22672";
        helper.printTestStart("checkReinstallationProcessFromAdminPage",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        Log("Click on RestartInstallation Wizard button");
        WebElement button_restart_installation_wizard = driver.findElementById("buttonRestartInstallationWizard");
        assertTrue(button_restart_installation_wizard.isDisplayed());
        button_restart_installation_wizard.click();

        //Lead to The initial welcome page is shown with two buttons - "Exit Patient Gateway to setup WIFI", and "Start Installation Wizard".

        WebElement StartInstallationButton = driver.findElement(By.id("buttonBigButtonTwo"));
        WebElement ExitPatientGatewaytoSetupWifiButton = driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(StartInstallationButton.isDisplayed());
        assertTrue(ExitPatientGatewaytoSetupWifiButton.isDisplayed());

        Log("Lead us to the initial welcome page with two big button");
        Log("Click on Start installation button ");
        StartInstallationButton.click();

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();
        helper.captureScreenShot();

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();
        helper.captureScreenShot();

        configurationByAdminPage();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void checkServerConfigCannotBeRequstedIfPatientSessionIsRunning() throws IOException
    {
        String case_id = "22673";
        helper.printTestStart("checkServerConfigCannotBeRequstedIfPatientSessionIsRunning",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        clickOnLock();

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        WebElement button_get_gateway_config = driver.findElementById("buttonGetGatewayConfigFromServer");

        button_get_gateway_config.click();

        WebElement you_must_end_session_first_alert = helper.findTextViewElementWithString("you_must_end_the_session_first");

        assertTrue(you_must_end_session_first_alert.isDisplayed());
        helper.updateTestRail(PASSED);
    }


    @Test
    public void checkAllButtonsAndCheckboxes() throws IOException
    {
        String case_id = "22674";
        helper.printTestStart("checkAllButtonsAndCheckboxes",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        //set the display timeout to no timeout in case we take too long
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength",helper.getAppString("no_timeout"));

        // FIRST ROW

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();

        // Set server address
        WebElement set_server_address_text = driver.findElementById("labelSetServerAddress");
        assertTrue(set_server_address_text.isDisplayed());
        String expected_value = helper.getAppString("set_server_address");
        String actual_value = set_server_address_text.getText();
        // replace the "A" for address to "a", to match the text .
        expected_value = expected_value.replaceAll("a", "A");
        assertEquals(expected_value, actual_value);

        // editServerAddress
        WebElement server_address_bar = driver.findElementById("editServerAddress");
        assertTrue(server_address_bar.isDisplayed());

        // buttonSetServer
        WebElement button_set_server = driver.findElementById("buttonSetServer");
        assertTrue(button_set_server.isDisplayed());

        // checkBoxUseWebServiceAuthentication
        WebElement checkbox_webservice_authentication = driver.findElementById("checkBoxUseWebServiceAuthentication");
        assertTrue(checkbox_webservice_authentication.isDisplayed());

        // checkBoxUseWebServiceAuthentication
        WebElement checkbox_webservice_encryption = driver.findElementById("checkBoxUseWebServiceEncryption");
        assertTrue(checkbox_webservice_encryption.isDisplayed());

        // checkBoxUseHttps
        WebElement checkbox_https = driver.findElementById("checkBoxUseHttps");
        assertTrue(checkbox_https.isDisplayed());

        // Test Link
        WebElement button_test_server_syncing = driver.findElementById("buttonTestServerLink");
        assertTrue(button_test_server_syncing.isDisplayed());

        //Log("Button and Check box in Row one are:\n "+buttonSetServer.getText()+" Button, "+checkBoxUseWebServiceAuthentication.getText()+" Checkbox, "+checkBoxUseWebServiceEncryption.getText()+" checkbox,"+checkBoxUseHttps.getText()+" Checkbox, "+buttonTestServerLink.getText());

        // SECOND ROW

        // labelSetServerPort
        WebElement label_set_server_port = driver.findElementById("labelSetServerPort");
        assertTrue(label_set_server_port.isDisplayed());
        expected_value = helper.getAppString("set_server_port");
        assertEquals(expected_value, label_set_server_port.getText());

        // editServerPort
        WebElement server_port_bar = driver.findElementById("editServerPort");
        assertTrue(server_port_bar.isDisplayed());

        // buttonSetServerPort
        WebElement button_set_server_port = driver.findElementById("buttonSetServerPort");
        assertTrue(button_set_server_port.isDisplayed());

        // labelSetRealTimeServerPort
        WebElement label_set_realtime_server_port = driver.findElementById("labelSetRealTimeServerPort");
        assertTrue(label_set_realtime_server_port.isDisplayed());
        actual_value = label_set_realtime_server_port.getText();
        expected_value = helper.getAppString("set_mqtt_server_port");
        assertEquals(expected_value, actual_value);

        // editRealTimeServerPort
        WebElement real_time_server_port_bar = driver.findElementById("editRealTimeServerPort");
        assertTrue(real_time_server_port_bar.isDisplayed());

        // buttonSetRealTimeServerPort
        WebElement button_set_real_time_server_port = driver.findElementById("buttonSetRealTimeServerPort");
        assertTrue(button_set_real_time_server_port.isDisplayed());

        // THIRD ROW

        // labelSetBedID
        WebElement label_set_bed_id = driver.findElementById("labelSetBedID");
        assertTrue(label_set_bed_id.isDisplayed());
        actual_value = label_set_bed_id.getText();
        expected_value = helper.getAppString("textSetBedID");
        assertEquals(expected_value, actual_value);

        // labelSetBedID
        WebElement button_get_wards_beds = driver.findElementById("buttonGetWardsAndBedsFromServer");
        assertTrue(button_get_wards_beds.isDisplayed());

        // ToDo test the spinners?
        //spinnerWardList
        // WebElement spinnerWardList=driver.findElementById("spinnerWardList");
        //assertTrue(spinnerWardList.isDisplayed());
        //spinnerWardList
        //WebElement spinnerBedList=driver.findElementById("spinnerBedList");
        //assertTrue(spinnerBedList.isDisplayed());

        // buttonSetBedDetails
        WebElement button_set_bed_details = driver.findElementById("buttonSetBedDetails");
        assertTrue(button_set_bed_details.isDisplayed());

        // change wifi button
        WebElement button_change_wifi = driver.findElement(By.id("buttonChangeWifi"));
        assertTrue(button_change_wifi.isDisplayed());

        // textAdminModeGatewaysAssignedBedId
        WebElement text_gateways_assigned_bed_id = driver.findElementById("textAdminModeGatewaysAssignedBedId");
        assertTrue(text_gateways_assigned_bed_id.isDisplayed());

        // textAdminModeGatewaysAssignedBedId
        WebElement button_get_ews_types = driver.findElementById("buttonGetDefaultEarlyWarningScoringTypesFromServer");
        assertTrue(button_get_ews_types.isDisplayed());

        // textReceivedDefaultEarlyWarningScoringTypes
        WebElement text_received_ews_types = driver.findElementById("textReceivedDefaultEarlyWarningScoringTypes");
        assertTrue(text_received_ews_types.isDisplayed());

        // GetGatewayConfigFromServerButton
        WebElement button_get_gateway_config = driver.findElementById("buttonGetGatewayConfigFromServer");
        assertTrue(button_get_gateway_config.isDisplayed());

        // Get ServerConfigurable Text
        WebElement button_get_server_configurable_text = driver.findElementById("buttonGetServerConfigurableTextFromServer");
        assertTrue(button_get_server_configurable_text.isDisplayed());

        // Check for Updated Firmware
        WebElement button_check_firmware = driver.findElementById("buttonCheckForUpdatedFirmware");
        assertTrue(button_check_firmware.isDisplayed());

        // Restart Installation Wizard
        WebElement button_restart_installation_wizard = driver.findElementById("buttonRestartInstallationWizard");
        assertTrue(button_restart_installation_wizard.isDisplayed());

        // Empty Local Database button
        WebElement button_empty_database = driver.findElementById("buttonEmptyLocalDatabase");
        assertTrue(button_empty_database.isDisplayed());

        // Empty Local Database INCLUDING EWS Types
        WebElement button_delete_ews_threshold_sets = driver.findElementById("buttonDeleteEarlyWarningScoreThresholdSets");
        assertTrue(button_delete_ews_threshold_sets.isDisplayed());

        // Export Database to Tablet Root
        WebElement button_export_database = driver.findElementById("buttonExportLocalDatabase");
        assertTrue(button_export_database.isDisplayed());

        // Delete Exported Databases
        WebElement button_delete_old_exported_databases = driver.findElementById("buttonDeleteOldExportedDatabases");
        assertTrue(button_delete_old_exported_databases.isDisplayed());

        // Manual Time Sync
        WebElement button_ntp_sync = driver.findElementById("ButtonNTPSync");
        assertTrue(button_ntp_sync.isDisplayed());

        // Log Cat
        WebElement button_logcat = driver.findElementById("ButtonLogCat");
        assertTrue(button_logcat.isDisplayed());

        // Show the Gateway Settings tab on the Admin page
        clickOnAdminTabGatewaySettings();

        // Setup Mode Length
        WebElement label_setup_mode_length = driver.findElementById("labelSetupModeLength");
        assertTrue(label_setup_mode_length.isDisplayed());

        // Percentage of Poor Signal Heart Beats before marking as invalid
        WebElement label_lifetouch_poor_signal_percentage = driver.findElementById("labelLifetouchPoorDeviceSignalPercentage");
        assertTrue(label_lifetouch_poor_signal_percentage.isDisplayed());

        // Number of Invalid SpO2 Intermediate Measurements before marking as invalid
        WebElement label_nonin_number_invalid_intermediate_measurements = driver.findElementById("labelNoninPulseOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid");
        assertTrue(label_nonin_number_invalid_intermediate_measurements.isDisplayed());

        // Long-term measurements timeout
        WebElement label_long_term_measurement_timeout = driver.findElementById("labelLongTermMeasurementTimeoutLength");
        assertTrue(label_long_term_measurement_timeout.isDisplayed());


        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();

        // ServerLinkEnabled
        WebElement checkbox_server_syncing_enabled = driver.findElementById("checkBoxServerDataSyncEnabled");
        assertTrue(checkbox_server_syncing_enabled.isDisplayed());

        // Show the Gateway Settings tab on the Admin page
        clickOnAdminTabGatewaySettings();

        // Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());

        //Displaytimeout
        WebElement DisplayTimeout=driver.findElementById("labelDisplayTimeoutLength");
        assertTrue(DisplayTimeout.isDisplayed());

        //Check the presence of Dropdown associated with DispalyTimeout option
        WebElement DisplaytimoutDropdown= driver.findElementById("spinnerDisplayTimeoutLength");
        assertTrue(DisplaytimoutDropdown.isDisplayed());

        //Use disply timeout on chat page check box.
        WebElement UseDisplayTimeout= driver.findElementById("checkBoxApplyDisplayTimeoutToPatientVitalsDisplay");
        assertTrue(UseDisplayTimeout.isDisplayed());

        // Auto Add EWS
        WebElement checkbox_auto_add_ews = driver.findElementById("checkBoxAutoAddEarlyWarningScores");
        assertTrue(checkbox_auto_add_ews.isDisplayed());
      // SpO2 spot Measurements
        WebElement checkbox_SpO2_spot_Measurements = driver.findElementById("checkSpO2SpotMeasurementsEnabled");
        assertTrue(checkbox_SpO2_spot_Measurements.isDisplayed());

        // Enabled Unplugged Overlay
        WebElement checkbox_unplugged_overlay = driver.findElementById("checkBoxEnableUnpluggedOverlay");
        assertTrue(checkbox_unplugged_overlay.isDisplayed());

        // Patient Name Lookup
        WebElement checkbox_patient_name_lookup = driver.findElementById("checkBoxPatientNameLookup");
        assertTrue(checkbox_patient_name_lookup.isDisplayed());

        // Predefined Annotation button
        WebElement checkbox_predefined_Annotations = driver.findElementById("checkBoxEnablePredefinedAnnotations");
        assertTrue( checkbox_predefined_Annotations.isDisplayed());

        // DFU
        WebElement dfu_enabled = driver.findElementById("checkDfuBootloaderEnabled");
        assertTrue(dfu_enabled.isDisplayed());

        //GSM Mode only
        WebElement GSMModeOnly = driver.findElement(By.id("checkBoxGsmModeOnly"));
        assertTrue(GSMModeOnly.isDisplayed());

        // Exit
        WebElement button_exit = driver.findElementById("adminExitButton");
        assertTrue(button_exit.isDisplayed());

        // Gateway version :
        WebElement label_gateway_software_version = driver.findElementById("labelPatientGatewaySoftwareVersion");
        assertTrue(label_gateway_software_version.isDisplayed());

        // textPatientGatewaySoftwareVersion
        WebElement text_gateway_software_version = driver.findElementById("textPatientGatewaySoftwareVersion");
        assertTrue(text_gateway_software_version.isDisplayed());
        Log(label_gateway_software_version.getText() + " " + text_gateway_software_version.getText());

        // UI version :
        WebElement label_ui_software_version = driver.findElementById("labelUserInterfaceSoftwareVersion");
        assertTrue(label_ui_software_version.isDisplayed());

        // text UI Version
        WebElement text_ui_software_version = driver.findElementById("textPatientGatewaySoftwareVersion");
        assertTrue(text_ui_software_version.isDisplayed());
        Log(label_ui_software_version.getText() + " " + text_ui_software_version.getText());

        // GTIN
        WebElement label_gtin = driver.findElementById("labelGatewayGTIN");
        assertTrue(label_gtin.isDisplayed());
        Log(label_gtin.getText());

        // Gateway Name
        WebElement label_gateway_name = driver.findElementById("labelGatewayName");
        assertTrue(label_gateway_name.isDisplayed());
        WebElement text_gateway_name=driver.findElement(By.id("textGatewayName"));
        Log("Gateway Name:"+text_gateway_name.getText());

        // Android Version
        WebElement label_os_version = driver.findElementById("labelAndroidVersion");
        assertTrue(label_os_version.isDisplayed());
        String os_label_text = helper.getAppString("os");
        assertEquals(os_label_text, label_os_version.getText());

        WebElement os_version = driver.findElementById("textAndroidVersion");
        assertTrue(os_version.isDisplayed());
        System.out.println("OS version: " + os_version.getText());

        // Show the Gateway Settings tab on the Admin page
        clickOnAdminTabGatewaySettings();

        // Set the display timeout back to 2 to minutes
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength",helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }


    @Test
    public void testAdminOptions() throws IOException
    {
        String case_id = "22675";
        helper.printTestStart("testAdminOptions",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Check if checkBox Enabled Unplugged Overlay is checked
        WebElement checkbox_unplugged_overlay = driver.findElement(By.id("checkBoxEnableUnpluggedOverlay"));
        String unplugged_overlay_checked = checkbox_unplugged_overlay.getAttribute("checked");
        Log("Nonin by inserting finger Check box Status " + unplugged_overlay_checked);
        if(unplugged_overlay_checked.equals("false"))
        {
            checkbox_unplugged_overlay.click();
            Log("Nonin by inserting finger checked");
        }
        else
        {
            Log("Nonin by inserting finger already checked");
        }


        // Set the setup mode length to 3 minutes
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("three_minutes"));

        // Percentage of poor signal Heart beats
        helper.clickElementOnSpinner("spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid", "10");

        // Number of invalid spo2
        helper.clickElementOnSpinner("spinnerNoninPulseOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid", "55");

        // Long-term measurement time out spinners
        helper.clickElementOnSpinner("spinnerLongTermMeasurementTimeoutLengthSpO2", helper.getAppString("sixty_minutes"));
        helper.clickElementOnSpinner("spinnerLongTermMeasurementTimeoutLengthBloodPressure", helper.getAppString("sixty_minutes"));
        helper.clickElementOnSpinner("spinnerLongTermMeasurementTimeoutLengthWeight", helper.getAppString("sixty_minutes"));

        //set the display timeout to 2 minutes
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength",helper.getAppString("two_minutes"));
        //Check if checkbox "Use display timeout on chat page" is unchecked
        WebElement checkbox_Usedisplaytimeout_onchatpage= driver.findElement(By.id("checkBoxApplyDisplayTimeoutToPatientVitalsDisplay"));
        String Usedisplaytimeout_onchatpage = checkbox_Usedisplaytimeout_onchatpage.getAttribute("checked");
        Log("Use display timeout on chat page status is "+ Usedisplaytimeout_onchatpage);
        if(Usedisplaytimeout_onchatpage.equals("true"))
        {
            checkbox_Usedisplaytimeout_onchatpage.click();
            Log("Use display timeout on chat page unchecked");
        }
        else
        {
            Log("Use display timeout on chat page already unchecked");
        }

        helper.updateTestRail(PASSED);
    }


    @Test
    public void testAdminPageButtons() throws IOException
    {
        String case_id = "22676";
        helper.printTestStart("testAdminPageButtons",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        // Export Database to tableRoot
        driver.findElement(By.id("buttonExportLocalDatabase")).click();
        // Empty local database
        driver.findElement(By.id("buttonEmptyLocalDatabase")).click();
        // Manual timesync
        driver.findElement(By.id("ButtonNTPSync")).click();

        String unexpected = helper.getAppString("time_drift_initial");

        // Ensure time-sync went through...
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewTimeDriftValue"), unexpected)), 45);

        String manual_time_sync_value = driver.findElement(By.id("textViewTimeDriftValue")).getText();
        Log(manual_time_sync_value);

        // Delete exported database
        driver.findElement(By.id("buttonDeleteOldExportedDatabases")).click();

        // Empty local Database including EWS
        driver.findElement(By.id("buttonDeleteEarlyWarningScoreThresholdSets")).click();

        // Lock screen to check we're back on the installation welcome page
        clickOnLock();

        // Check that the "Start Installation Wizard" button is displayed, not "Start Monitoring Patient" - both use the same button ID.
        String button_text = driver.findElement(By.id("buttonBigButtonTwo")).getText();
        String resource_text = helper.getAppString("start_installation_wizard");
        assertEquals(resource_text, button_text);

        driver.findElement(By.id("buttonBigButtonTwo")).click();

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        // Click on Get Type from Server
        driver.findElement(By.id("buttonGetDefaultEarlyWarningScoringTypesFromServer")).click();

        // Get Server Configurable Text
        WebElement button_get_server_configurable_text = driver.findElementById("buttonGetServerConfigurableTextFromServer");
        assertTrue(button_get_server_configurable_text.isDisplayed());
        button_get_server_configurable_text.click();

        // To check and Click on the server syncing
        WebElement checkbox_server_syncing_enabled = driver.findElement(By.id("checkBoxServerDataSyncEnabled"));
        String server_syncing_enabled = checkbox_server_syncing_enabled.getAttribute("checked");
        Log("Server Syncing Enable Check box Status " + server_syncing_enabled);
        if(server_syncing_enabled.equals("false"))
        {
            checkbox_server_syncing_enabled.click();
            Log("ServerLinkEnable checked");
        }
        else
        {
            Log("ServerLinkEnable checked already checked");
        }

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("buttonForceInstallationComplete")), 30);
        // Click on Force Installation Complete
        driver.findElement(By.id("buttonForceInstallationComplete")).click();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void testExitAndNotResponding1() throws IOException, InterruptedException
    {
        String case_id = "22677";
        helper.printTestStart("testExitAndNotResponding1",case_id);

        // Spoof the admin unlock to bring up the admin page again.
        helper.spoofQrCode_adminUnlock();

        // Click on Exit button
        driver.findElement(By.id("adminExitButton")).click();

        // Click on Yes button
        driver.findElement(By.id("android:id/button1")).click();

        // Adding a short wait here to ensure the apps quit cleanly
        Thread.sleep(5000);
        helper.updateTestRail(PASSED);
    }


    @Test
    public void testExitAndNotResponding2()throws IOException
    {
        String case_id = "22678";
        helper.printTestStart("testExitAndNotResponding2",case_id);
        helper.captureScreenShot();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRestartGatewayApp")), 45);
        Log("Click on Restart button");
        driver.findElement(By.id("buttonRestartGatewayApp")).click();

        // Check that after clicking Restart button the , The gateway app restarts, the UI reverts to the QR unlock screen .
        Log("Examine the UnLock fragment page");
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("qr_bar_code")), 130);

        WebElement QR_scanner_camera=driver.findElement(By.id("qr_bar_code"));
        assertTrue(QR_scanner_camera.isDisplayed());
        helper.updateTestRail(PASSED);
    }


    public void configurationByAdminPage()throws IOException
    {
        // Check the Force installation button is not present
        assertEquals(0, driver.findElements(By.id("buttonForceInstallationComplete")).size());

        // Configuration by Admin page
        WebElement server_address = driver.findElement(By.id("editServerAddress"));
        server_address.clear();
        driver.findElement(By.id("editServerAddress")).sendKeys(helper.getServerAddress()); //using muffin server

        // Click on Set Server button
        driver.findElement(By.id("buttonSetServer")).click();

        // Click on yes from the popup message
        driver.findElement(By.id("android:id/button1")).click();


        String actual_text = driver.findElement(By.id("textHeaderWardName")).getText();
        String expected_text = helper.getAppString("not_set_yet");

        assertEquals(expected_text, actual_text);

        actual_text = driver.findElement(By.id("textHeaderBedName")).getText();

        assertEquals(expected_text, actual_text);

        // Check if UseWebServiceAuthentication button is checked
        String webservice_authentication_checked;
        WebElement checkbox_webservice_authentication = driver.findElement(By.id("checkBoxUseWebServiceAuthentication"));
        webservice_authentication_checked = checkbox_webservice_authentication.getAttribute("checked");
        Log("Service Authentication Check box Status " + webservice_authentication_checked);
        if(webservice_authentication_checked.equals("false"))
        {
            checkbox_webservice_authentication.click();
            Log("checkBoxUseWebServiceAuthentication checked");
        }
        else
        {
            Log("checkBoxUseWebServiceAuthentication already checked");
        }

        // Check if checkBox Use WebServiceEncryption is checked
        String webservice_encryption_checked;
        WebElement checkbox_webservice_encryption = driver.findElement(By.id("checkBoxUseWebServiceEncryption"));
        webservice_encryption_checked = checkbox_webservice_encryption.getAttribute("checked");
        Log("ServiceEncryption Check box Status " + webservice_encryption_checked);
        if(webservice_encryption_checked.equals("false"))
        {
            checkbox_webservice_encryption.click();
            Log("checkBoxUseWebServiceEncryption checked");
        }
        else
        {
            Log("checkBoxUseWebServiceEncryption already checked");
        }

        // Click on test link
        driver.findElement(By.id("buttonTestServerLink")).click();

        // Set the value = 80 for server port
        driver.findElement(By.id("editServerPort")).clear();
        driver.findElement(By.id("editServerPort")).sendKeys(helper.getServerPort());  //using muffin server
        driver.findElement(By.id("buttonSetServerPort")).click();

        // Click on yes from the popup message
        driver.findElement(By.id("android:id/button1")).click();

        // Set Real Time port
        driver.findElement(By.id("editRealTimeServerPort")).clear();
        driver.findElement(By.id("editRealTimeServerPort")).sendKeys(helper.getServerRealTimePort()); //using muffin server
        driver.findElement(By.id("buttonSetRealTimeServerPort")).click();

        // Set Bed ID
        driver.findElement(By.id("buttonGetWardsAndBedsFromServer")).click();

        // Click on the Wardlist and select the word from the spinner
        helper.clickElementOnSpinner("spinnerWardList", "Test Ward");

        // Select bed list from the spinner
        Log("Selecting bed by computer name: " + BuildConfig.buildMachineName);
        helper.clickElementOnSpinner("spinnerBedList", BuildConfig.buildMachineName);

        // Click on Set Bed Details
        driver.findElement(By.id("buttonSetBedDetails")).click();

        // Click on Get Type from Server
        driver.findElement(By.id("buttonGetDefaultEarlyWarningScoringTypesFromServer")).click();

        // Click on Get Configuration from server button
        WebElement button_get_configuration_from_server = driver.findElement(By.id("buttonGetGatewayConfigFromServer"));
        assertTrue(button_get_configuration_from_server.isDisplayed());
        button_get_configuration_from_server.click();

        // Get Server Configurable Text
        WebElement button_get_server_configurable_text = driver.findElementById("buttonGetServerConfigurableTextFromServer");
        assertTrue(button_get_server_configurable_text.isDisplayed());
        button_get_server_configurable_text.click();

        // Check for update firmware button
        WebElement button_check_for_updated_firmware = driver.findElement(By.id("buttonCheckForUpdatedFirmware"));
        assertTrue(button_check_for_updated_firmware.isDisplayed());
        button_check_for_updated_firmware.click();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonForceInstallationComplete")), 120);

        // ReceivedUpdateFirmwareVersions
        WebElement received_firmware_versions = driver.findElement(By.id("textReceivedUpdateFirmwareVersions"));
        String text_received_firmware_versions = received_firmware_versions.getText();
        //ToDo: figure out if we can test the value of this at all... since we don't have a fixed expected version number
        Log("Updated firmware versions is : " + text_received_firmware_versions);

        // Check the presence of buttonRestartInstallationWizard
        WebElement restart_installation_wizard_button = driver.findElement(By.id("buttonRestartInstallationWizard"));
        assertTrue(restart_installation_wizard_button.isDisplayed());

        // To check and Click on the server syncing
        String server_syncing_enabled_checked;
        WebElement checkbox_server_syncing_enabled = driver.findElement(By.id("checkBoxServerDataSyncEnabled"));
        server_syncing_enabled_checked = checkbox_server_syncing_enabled.getAttribute("checked");
        Log("Server Syncing Enable Check box Status " + server_syncing_enabled_checked);
        if(server_syncing_enabled_checked.equals("false"))
        {
            checkbox_server_syncing_enabled.click();
            Log("checkBoxServerDataSyncEnabled checked");
        }
        else
        {
            Log("checkBoxServerDataSyncEnabled already checked");
        }

        // Check the appearance of force Installation Complete button
        WebElement button_force_installation_complete = driver.findElement(By.id("buttonForceInstallationComplete"));
        assertTrue(button_force_installation_complete.isDisplayed());
        helper.captureScreenShot();

        // Click on Force Installation Complete
        button_force_installation_complete.click();
    }

    @Test
    public void changeWifiPopup() throws IOException
    {
        String case_id = "44705";
        helper.printTestStart("changeWifiPopup",case_id);

        // Spoof the admin unlock to bring up the admin page again.
        helper.spoofQrCode_adminUnlock();

        // Find the OS version from admin page
        driver.findElement(By.id("labelAndroidVersion")).isDisplayed();
        WebElement AndroidV= driver.findElement(By.id("textAndroidVersion"));
        String OS_Version=AndroidV.getText();
        Log(OS_Version);
        // trim the version number to two character (helpful  if version has subversion)
        String firstTwoCharacters = OS_Version.substring(0, 2);
        //Log(firstTwoCharacters);
        //Ignore the decimal part if the version has 8.0.1 for example
        float Version = Float.parseFloat(firstTwoCharacters);
        int MatchVersion = (int) Version;
        System.out.println("OS Version:"+MatchVersion + " to check ");

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();

        WebElement button_change_wifi = driver.findElement(By.id("buttonChangeWifi"));

        button_change_wifi.click();

        // Check the version and show the Wifi popup window accordingly when click on WiFi button
        if(MatchVersion == 8)
        {
            // check for wifi popup - should be the same as in the footer.
            String expected;
            String actual;
            // Check the popup is shown...
            expected = helper.getAppString("wifi_enabled_status");
            actual = driver.findElement(By.id("textView_wifi_enabled_title")).getText();
            assertEquals(expected, actual);

            // ... and connected.
            expected = "CONNECTED";
            actual = driver.findElement(By.id("textView_wifi_connection_status")).getText();
            assertEquals(expected, actual);

            // now click reconnect
            driver.findElement(By.id("button_wifi_reconnect")).click();

            // check wifi has disconnected
            waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textView_wifi_connection_status"), "DISCONNECTED"), 120);

            // and wait for it to reconnect.
            waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textView_wifi_connection_status"), "CONNECTED"), 120);

            // Finally dismiss the popup
            driver.findElement(By.id("button_wifi_popup_dismiss")).click();
        }
        else
        {
            // check for settings panel
            WebElement title = driver.findElement(By.id("com.android.settings:id/panel_title"));

            System.out.println("Settings panel showing : " + title.getText());

            String wifi_enabled = driver.findElement(By.className("android.widget.Switch")).getAttribute("checked");

            assertEquals("true", wifi_enabled);

            System.out.println("wifi enabled");

            // click done
            driver.findElement(By.id("com.android.settings:id/done")).click();

            System.out.println("panel dismissed");
        }

        helper.updateTestRail(PASSED);
    }
}
