package com.isansys.appiumtests.tablet_only;

import android.text.format.DateUtils;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.TestCase.assertTrue;

/** Checks the functionality of {@link com.isansys.pse_isansysportal.PopupServerSyncing}
 *
 */
public class ServerSyncingPopup extends AppiumTest
{
    List<WebElement> Option;
    WebElement optionArea;
    @Test
    public void showSyncingPopup() throws IOException {
        String case_id = "22598";
        helper.printTestStart("showSyncingPopup",case_id);

        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        Assert.assertTrue(driver.findElement(By.id("textViewSessionStatusHeader")).isDisplayed());

        String expected;
        String actual;

        // Check the popup row/column headers...
        expected = helper.getAppString("session_status");
        actual = driver.findElement(By.id("textViewSessionStatusHeader")).getText();
        Assert.assertEquals(expected, actual);

        expected = helper.getAppString("stringDataNonSyncable");
        actual = driver.findElement(By.id("textViewLeftSideNonSyncableData")).getText();
        Assert.assertEquals(expected, actual);

        expected = helper.getAppString("stringDataFailedToSend");
        actual = driver.findElement(By.id("textViewLeftSideFailedToSendData")).getText();
        Assert.assertEquals(expected, actual);

        expected = helper.getAppString("stringDataPending");
        actual = driver.findElement(By.id("textViewLeftSideSyncableData")).getText();
        Assert.assertEquals(expected, actual);

        expected = helper.getAppString("stringDataNonSyncable");
        actual = driver.findElement(By.id("textViewRightSideNonSyncableData")).getText();
        Assert.assertEquals(expected, actual);

        expected = helper.getAppString("stringDataFailedToSend");
        actual = driver.findElement(By.id("textViewRightSideFailedToSendData")).getText();
        Assert.assertEquals(expected, actual);

        expected = helper.getAppString("stringDataPending");
        actual = driver.findElement(By.id("textViewRightSideSyncableData")).getText();
        Assert.assertEquals(expected, actual);


        helper.captureScreenShot();

        // Dismiss it
        driver.findElement(By.id("dismiss")).click();

        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("textViewSessionStatusHeader")), 120);
        helper.updateTestRail(PASSED);

    }

    /**
     * WARNING will take at least 15 minutes
     */
    @Test
    public void checkAutoDismissTimer()
    {
        String case_id = "22599";
        helper.printTestStart("checkAutoDismissTimer",case_id);

        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        driver.findElement(By.id("textViewSessionStatusHeader"));

        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("textViewSessionStatusHeader")),
                16 * (int) DateUtils.MINUTE_IN_MILLIS);
        helper.updateTestRail(PASSED);

    }

    @Test
    public void checkNoDataPending() throws IOException
    {
        String case_id = "22600";
        helper.printTestStart("checkNoDataPending",case_id);

        // empty the database of any pending data from other tests
        helper.spoofCommandIntent_emptyDatabase();

        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        driver.findElement(By.id("textViewSessionStatusHeader"));

        // wait for data to load
        waitForPendingDataToBeShown();

        // wait for old session data to be deleted...
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionEarlyWarningScoresPending"), "0"), 120);

        // Check the Pending columns
        // Session status...
        checkNoDataPending__sessionStatus();

        // Active Session
        checkNoDataPending__activeSession();

        // Historical Session
        checkNoDataPending__oldSession();

        // Dismiss it
        driver.findElement(By.id("dismiss")).click();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void checkSessionDataSyncsToServer() throws IOException
    {
        String case_id = "22601";
        helper.printTestStart("checkSessionDataSyncsToServer",case_id);

        checkAdminSettingsCorrect(false, false, false);

        // Turn off server syncing

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        WebElement server_syncing_checkbox = driver.findElement(By.id("checkBoxServerDataSyncEnabled"));

        if(server_syncing_checkbox.getAttribute("checked").equals("true"))
        {
            server_syncing_checkbox.click();
            Log("ServerDataSyncEnable unchecked");
        }
        else
        {
            Log("ServerDataSyncEnable already unchecked");
        }

        clickOnLock();

        // empty the database of any pending data from other tests
        helper.spoofCommandIntent_emptyDatabase();


        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();

        clickOnAddEws();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 60);

        clickOnNext();

        // add manual vitals
        Log("Adding manual vitals");

        clickOnLock();

        //scan User Qr code
        helper.spoofQrCode_userUnlock();

        driver.findElement(By.id("buttonBigButtonFour")).click();

        clickOnNext();

        helper.findTextViewElementWithString("ten_minutes").click();


        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        //Click on next
        clickOnNext();
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Temp is 38");
        //Click on next
        clickOnNext();
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        //Click on the Blood Pressure button
        Log("Click on Blood Pressure button");
        helper.findTextViewElementWithString("blood_pressure").click();
        Log("Enter the value for Blood Pressure");
        //Enter the value for BloodPressure.98/68
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonDecimal")).click();
        driver.findElement(By.id("buttonNumberSix")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for BP is 98/68");
        clickOnNext();
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        consciounesslevelOfNonadultAgerange();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        SupplementalOxygenForNonAdultAgeRange();
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
        RespirationDistressLevel();


        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        CapillaryRefillTime();

        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");
        FamilyNurseConcern();
        //Click on Next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();



        // Wait for data to come through
        driver.findElement(By.id("buttonBigButtonOne")).click();

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeartRate"), helper.getAppString("textWaitingForData"))), 75);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textTemperatureReading"), helper.getAppString("textWaitingForData"))), 75);

        helper.swipeActionScrollDown();

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textEarlyWarningScoreMeasurement"), helper.getAppString("textWaitingForData"))), 75);

        clickOnLock();

        //scan User Qr code
        helper.spoofQrCode_userUnlock();



        // end session
        driver.findElement(By.id("buttonBigButtonThree")).click();

        driver.findElement(By.id("buttonEndSession")).click();

        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();

        driver.findElement(By.id("buttonEndSessionBigButtonBottom")).click();

        checkRecyclingReminderPopup();

        clickOnLock();

        // check data is pending/un-syncable
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        Log("Checking session status data");

        waitForPendingDataToBeShown();

        String pending_patient_details = driver.findElement(By.id("textViewPopupServerSyncStatusPatientDetailsPending")).getText();
        Log("Pending patient details: " + pending_patient_details);

        Assert.assertTrue(Integer.parseInt(pending_patient_details) > 0);


        String pending_device_info = driver.findElement(By.id("textViewPopupServerSyncStatusDeviceInfoPending")).getText();
        Log("Pending device info: " + pending_device_info);

        Assert.assertTrue(Integer.parseInt(pending_device_info) > 0);


        String non_syncable_patient_session_starts = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableStartPatientSessionPending")).getText();
        Log("Non-syncable patient session start: " + non_syncable_patient_session_starts);
        Assert.assertEquals(pending_patient_details, non_syncable_patient_session_starts);

        String non_syncable_patient_session_ends = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableEndPatientSessionPending")).getText();
        Log("Non-syncable patient session end: " + non_syncable_patient_session_ends);
        Assert.assertEquals(non_syncable_patient_session_starts, non_syncable_patient_session_ends);


        String non_syncable_device_session_starts = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableStartDeviceSessionPending")).getText();
        Log("Non-syncable device session start: " + non_syncable_device_session_starts);
        Assert.assertEquals(pending_device_info, non_syncable_device_session_starts);

        String non_syncable_device_session_ends = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableEndDeviceSessionPending")).getText();
        Log("Non-syncable device session end: " + non_syncable_device_session_ends);
        Assert.assertEquals(non_syncable_device_session_starts, non_syncable_device_session_ends);



        Log("--------Checking historical vital signs data--------");

        // Lifetouch
        Log("--------Lifetouch--------");
        String non_syncable_hr = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchHeartRatesPending")).getText();
        Log("Non-syncable old session heart rates: " + non_syncable_hr);
        Assert.assertTrue(Integer.parseInt(non_syncable_hr) > 0);

        String non_syncable_rr = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchRespirationMeasurementsPending")).getText();
        Log("Non-syncable old session respiration rates: " + non_syncable_rr);
        Assert.assertTrue(Integer.parseInt(non_syncable_rr) > 0);

        String non_syncable_heart_beats = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchHeartBeatsPending")).getText();
        Log("Non-syncable old session heart beats: " + non_syncable_heart_beats);
        Assert.assertTrue(Integer.parseInt(non_syncable_heart_beats) > 0);

        String non_syncable_lifetouch_battery = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchBatteryMeasurementsPending")).getText();
        Log("Non-syncable old session Lifetouch battery measurements: " + non_syncable_lifetouch_battery);
        Assert.assertTrue(Integer.parseInt(non_syncable_lifetouch_battery) > 0);

        // Lifetemp
        Log("--------Lifetemp--------");
        String non_syncable_temperature = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionLifetempTemperatureMeasurementsPending")).getText();
        Log("Non-syncable old session temperature measurements: " + non_syncable_temperature);
        Assert.assertTrue(Integer.parseInt(non_syncable_hr) > 0);

        String non_syncable_lifetemp_battery = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionLifetempBatteryMeasurementsPending")).getText();
        Log("Non-syncable old session Lifetemp battery measurements: " + non_syncable_lifetemp_battery);
        Assert.assertTrue(Integer.parseInt(non_syncable_lifetemp_battery) > 0);

        // Nonin
        Log("--------Nonin--------");
        String non_syncable_spo2 = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxMeasurementsPending")).getText();
        Log("Non-syncable old session SpO2 measurements: " + non_syncable_spo2);
        Assert.assertTrue(Integer.parseInt(non_syncable_spo2) > 0);

        String non_syncable_intermediate_spo2 = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxIntermediateMeasurementsPending")).getText();
        Log("Non-syncable old session intermediate SpO2: " + non_syncable_intermediate_spo2);
        Assert.assertTrue(Integer.parseInt(non_syncable_intermediate_spo2) > 0);

        String non_syncable_nonin_battery = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxBatteryMeasurementsPending")).getText();
        Log("Non-syncable old session nonin battery measurements: " + non_syncable_nonin_battery);
        Assert.assertTrue(Integer.parseInt(non_syncable_nonin_battery) > 0);

        // BP
        Log("--------Blood Pressure--------");
        String non_syncable_bp = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionBloodPressureMeasurementsPending")).getText();
        Log("Non-syncable old session Blood Pressure measurements: " + non_syncable_bp);
        Assert.assertTrue(Integer.parseInt(non_syncable_bp) > 0);

        String non_syncable_bp_battery = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionBloodPressureBatteryMeasurementsPending")).getText();
        Log("Non-syncable old session BP battery measurements: " + non_syncable_bp_battery);
        Assert.assertTrue(Integer.parseInt(non_syncable_bp_battery) > 0);


        // manual vitals
        Log("--------Manually Entered Vitals--------");
        String non_syncable_manual_hr = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredHeartRateMeasurementsPending")).getText();
        Log("Non-syncable old session manual heart rate measurements: " + non_syncable_manual_hr);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_hr));

        String non_syncable_manual_rr = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredRespirationRateMeasurementsPending")).getText();
        Log("Non-syncable old session manual respiration rate measurements: " + non_syncable_manual_rr);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_rr));

        String non_syncable_manual_temperature = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredTemperatureMeasurementsPending")).getText();
        Log("Non-syncable old session manual temperature measurements: " + non_syncable_manual_temperature);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_temperature));

        String non_syncable_manual_spo2 = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredSpo2MeasurementsPending")).getText();
        Log("Non-syncable old session manual SpO2 measurements: " + non_syncable_manual_spo2);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_spo2));

        String non_syncable_manual_bp = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredBloodPressureMeasurementsPending")).getText();
        Log("Non-syncable old session manual blood pressure measurements: " + non_syncable_manual_bp);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_bp));

        String non_syncable_manual_consciousness_level = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredConsciousnessLevelsPending")).getText();
        Log("Non-syncable old session manual consciousness level measurements: " + non_syncable_manual_consciousness_level);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_consciousness_level));

        String non_syncable_manual_supplemental_oxygen = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredSupplementalOxygenLevelsPending")).getText();
        Log("Non-syncable old session manual supplemental oxygen measurements: " + non_syncable_manual_supplemental_oxygen);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_supplemental_oxygen));

        String non_syncable_manual_capillary_refill = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredCapillaryRefillTimePending")).getText();
        Log("Non-syncable old session manual capillary refill time measurements: " + non_syncable_manual_capillary_refill);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_capillary_refill));

        String non_syncable_manual_respiration_distress = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredRespirationDistressPending")).getText();
        Log("Non-syncable old session manual respiration distress measurements: " + non_syncable_manual_respiration_distress);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_respiration_distress));

        String non_syncable_manual_family_or_nurse_concern = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredFamilyOrNurseConcernPending")).getText();
        Log("Non-syncable old session manual family or nurse concern: " + non_syncable_manual_family_or_nurse_concern);
        Assert.assertEquals(1, Integer.parseInt(non_syncable_manual_family_or_nurse_concern));

        Log("--------EWS--------");
        String non_syncable_ews = driver.findElement(By.id("textViewPopupServerSyncStatusNonSyncableOldSessionEarlyWarningScoresPending")).getText();
        Log("Non-syncable old session ews: " + non_syncable_ews);
        Assert.assertTrue(Integer.parseInt(non_syncable_ews) > 0);  // EWS may or may not have been generated...


        // dismiss popup, turn on syncing
        driver.findElement(By.id("dismiss")).click();

        try
        {
            // try locking the screen
            WebElement lock_button = driver.findElement(By.id("buttonLock"));
            lock_button.click();
        }
        catch(NoSuchElementException | StaleElementReferenceException e)
        {
            // assume it failed because the screen is already locked...
            Log("Impossible to click the lock button. Reason: " + e.toString());
        }

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        server_syncing_checkbox = driver.findElement(By.id("checkBoxServerDataSyncEnabled"));

        if(server_syncing_checkbox.getAttribute("checked").equals("false"))
        {
            server_syncing_checkbox.click();
            Log("ServerDataSyncEnable checked again");
        }
        else
        {
            Log("ServerDataSyncEnable already checked");
        }

        clickOnLock();

        // wait for pending data to reach zero
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        Log("Waiting for session status data to sync");

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusPatientDetailsPending"), "0"), 120);

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionEarlyWarningScoresPending"), "0"), 120);

        // close and re-open the popup
        driver.findElement(By.id("dismiss")).click();

        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("textViewSessionStatusHeader")), 120);

        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textViewSessionStatusHeader")), 120);

        // wait for data to load
        waitForPendingDataToBeShown();

        // now check nothing is pending or wait for it to sync
        checkNoDataPending__sessionStatus();
        checkNoDataPending__activeSession();
        checkNoDataPending__oldSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void checkAllDataDisplaysCorrectly() throws IOException
    {
        String case_id = "22602";
        helper.printTestStart("checkAllDataDisplaysCorrectly",case_id);

        checkAdminSettingsCorrect(false, false, false);

        // Turn off server syncing

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        WebElement server_syncing_checkbox = driver.findElement(By.id("checkBoxServerDataSyncEnabled"));

        if(server_syncing_checkbox.getAttribute("checked").equals("true"))
        {
            server_syncing_checkbox.click();
            Log("ServerDataSyncEnable unchecked");
        }
        else
        {
            Log("ServerDataSyncEnable already unchecked");
        }

        clickOnLock();

        // empty the database of any pending data from other tests
        helper.spoofCommandIntent_emptyDatabase();

        helper.spoofCommandIntent_serverSyncingTestMode(true);

        // check data is pending/un-syncable
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        waitForPendingDataToBeShown();
        helper.updateTestRail(PASSED);


        // ToDo: check values
    }

    private void checkNoDataPending__sessionStatus()
    {
        String expected = "0";

        Log("Checking no session status data pending");

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusPatientDetailsPending"), expected), 120);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusDeviceInfoPending"), expected), 120);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableStartPatientSessionPending"), expected), 120);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableEndPatientSessionPending"), expected), 120);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableStartDeviceSessionPending"), expected), 120);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableEndDeviceSessionPending"), expected), 120);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionAuditTrailPending"), expected), 120);
    }


    private void checkNoDataPending__activeSession()
    {
        String expected = "0";
        String actual;

        Log("Checking no active session data pending");

        // start with a wait for the last pending data to be zero
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionConnectionEventPending"), expected), 120);


        // Lifetouch
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchHeartRatesPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchHeartBeatsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchRespirationMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        // Use a wait here, as there's likely to be more of this data pending
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchSetupModeSamplesPending"), expected), 120);

        // Use a wait here, as there's likely to be more of this data pending
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchRawAccelerometerModeSamplesPending"), expected), 120);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionPatientOrientationPending")).getText();
        Assert.assertEquals(expected, actual);


        // Lifetemp
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetempTemperatureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetempBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);


        //Nonin
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxIntermediateMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        // Use a wait here, as there's likely to be more of this data pending
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxSetupModeSamplesPending"), expected), 120);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);


        // Blood Pressure
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionBloodPressureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionBloodPressureBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);


        // Manually Entered Vitals
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredHeartRateMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredRespirationRateMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredTemperatureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredSpo2MeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredBloodPressureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredConsciousnessLevelsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredSupplementalOxygenLevelsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredAnnotationsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredCapillaryRefillTimePending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredRespirationDistressPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredFamilyOrNurseConcernPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionEarlyWarningScoresPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionSetupModeLogsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableActiveSessionConnectionEventPending")).getText();
        Assert.assertEquals(expected, actual);
    }


    private void checkNoDataPending__oldSession()
    {
        String expected = "0";
        String actual;

        Log("Checking no historical session data pending");

        // start with a wait for the last pending data to be zero
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionConnectionEventPending"), expected), 120);


        // Lifetouch
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchHeartRatesPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchHeartBeatsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchRespirationMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        // Use a wait here, as there's likely to be more of this data pending
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchSetupModeSamplesPending"), expected), 120);

        // Use a wait here, as there's likely to be more of this data pending
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchRawAccelerometerModeSamplesPending"), expected), 120);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionPatientOrientationPending")).getText();
        Assert.assertEquals(expected, actual);


        // Lifetemp
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetempTemperatureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetempBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);


        //Nonin
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxIntermediateMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        // Use a wait here, as there's likely to be more of this data pending
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxSetupModeSamplesPending"), expected), 120);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);


        // Blood Pressure
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionBloodPressureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionBloodPressureBatteryMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);


        // Manually Entered Vitals
        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredHeartRateMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredRespirationRateMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredTemperatureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredSpo2MeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredBloodPressureMeasurementsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredConsciousnessLevelsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredSupplementalOxygenLevelsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredAnnotationsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredCapillaryRefillTimePending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredRespirationDistressPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredFamilyOrNurseConcernPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionEarlyWarningScoresPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionSetupModeLogsPending")).getText();
        Assert.assertEquals(expected, actual);

        actual = driver.findElement(By.id("textViewPopupServerSyncStatusSyncableOldSessionConnectionEventPending")).getText();
        Assert.assertEquals(expected, actual);
    }


    private void waitForPendingDataToBeShown()
    {
        // Check a subset of the "pending" numbers, starting with the first to be updated, patient details
        String four_dashes = helper.getAppString("four_dashes");
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusPatientDetailsPending"), four_dashes)), 120);

        // Then one from each of the devices - lifetouch, lifetemp, pulse ox, BP.
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetouchBatteryMeasurementsPending"), four_dashes)), 120);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetouchBatteryMeasurementsPending"), four_dashes)), 120);


        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionLifetempBatteryMeasurementsPending"), four_dashes)), 120);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionLifetempBatteryMeasurementsPending"), four_dashes)), 120);


        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxBatteryMeasurementsPending"), four_dashes)), 120);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxBatteryMeasurementsPending"), four_dashes)), 120);


        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionBloodPressureBatteryMeasurementsPending"), four_dashes)), 120);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionBloodPressureBatteryMeasurementsPending"), four_dashes)), 120);


        // Then EWS
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionEarlyWarningScoresPending"), four_dashes)), 120);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionEarlyWarningScoresPending"), four_dashes)), 120);

        // finally the last entries, setup mode logs.
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableActiveSessionSetupModeLogsPending"), four_dashes)), 120);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewPopupServerSyncStatusSyncableOldSessionSetupModeLogsPending"), four_dashes)), 120);
    }

    public void SupplementalOxygenForNonAdultAgeRange()
    {
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(1).getText());
        Option.get(1).click();
    }
    public void RespirationDistressLevel()
    {
        helper.findTextViewElementWithString("respiration_distress").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Log("Value Enter  for RespirationDistressLevel is  "+Option.get(0).getText());
        Option.get(0).click();
    }
    public void CapillaryRefillTime()
    {
        helper.findTextViewElementWithString("capillary_refill_time").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Log("Value Enter  for CapillaryRefillTime is  "+Option.get(1).getText());
        Option.get(1).click();
    }
    public void FamilyNurseConcern()
    {
        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Log("Value Enter  for FamilyNurseConcern is  "+Option.get(0).getText());
        Option.get(0).click();
    }
    public void consciounesslevelOfNonadultAgerange()
    {

        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        WebElement Alert= Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());

        Alert.click();
    }
}
