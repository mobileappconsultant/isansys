package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.text.format.DateUtils;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.common.enums.SensorType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;


public class PatientVitalsDisplay  extends AppiumTest {
    private final long NANOSEC_PER_SEC = 1000 * 1000 * 1000;
    List<WebElement> Option;
    WebElement optionArea;


    @Test
    public void test_the_patient_vital_page_for_Non_adult_age_range_if_the_session_has_got_no_manual_vital_Entry_aswellas_no_realdevices() throws IOException {
        String case_id = "22475";

        helper.printTestStart("test_the_patient_vital_page_for_Non_adult_age_range_if_the_session_has_got_no_manual_vital_Entry_aswellas_no_realdevices",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is :" + message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, message_displayed);
        Log("No device added to the session ");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        clickOnLock();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_the_patient_vital_page_for_adult_age_range_if_the_session_has_got_no_manual_vital_Entry_aswellas_no_realdevices() throws IOException {
        String case_id = "22476";

        helper.printTestStart("test_the_patient_vital_page_for_adult_age_range_if_the_session_has_got_no_manual_vital_Entry_aswellas_no_realdevices",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is: " + message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, message_displayed);
        Log("No device added to the session ");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_the_patient_vital_display_page_if_only_annotation_has_added_to_the_session_with_out_any_vital_sign() throws IOException {
        String case_id = "22478";

        helper.printTestStart("test_the_patient_vital_display_page_if_only_annotation_has_added_to_the_session_with_out_any_vital_sign",case_id);

        checkAdminSettingsCorrect(false, false, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        // Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        // Click on the Next button
        clickOnNext();
        // Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean finished_typing_button_displayed = driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is " + finished_typing_button_displayed);

        assertTrue(finished_typing_button_displayed);

        // Click on Hide Keyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();

        clickOnNext();

        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Check the value enter for annotation is not showing on the  patient vital display page
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String actual_message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is" + actual_message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, actual_message_displayed);
        Log("No device added to the session ");
        ShowHideSetupModeCheckBoxNotAppear();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_the_patient_vital_displaypage_after_adding_all_vitalsign_manually_for_adult_age_range() throws IOException {
        String case_id = "22479";

        helper.printTestStart("test_the_patient_vital_displaypage_after_adding_all_vitalsign_manually_for_adult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        // Click on ManualVitals Only
        manualVitalSignEntryForAdultAgeRange();

        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForAdultAgerange();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_the_patient_vital_displaypage_after_adding_all_vitalsign_manually_for_NonAdult_age_range() throws IOException {
        String case_id = "22480";

        helper.printTestStart("test_the_patient_vital_displaypage_after_adding_all_vitalsign_manually_for_NonAdult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        // Click on ManualVitals Only
        manualVitalSignEntryForNonAdultAgeRange();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForNonAdultAgerange();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_Waitingfordata_status_for_manually_enter_vital_sign_in_patient_vital_display_page_after_End_of_the_measurement_interval_For_non_adult_age_range() throws IOException {
        String case_id = "22481";

        helper.printTestStart("test_the_appearance_of_Waitingfordata_status_for_manually_enter_vital_sign_in_patient_vital_display_page_after_End_of_the_measurement_interval_For_non_adult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        // Click on ManualVitals Only
        manualVitalSignEntryForNonAdultAgeRange();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForNonAdultAgerange();

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textManuallyEnteredFamilyOrNurseConcern"), "Waiting for data"), 650);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textManuallyEnteredSupplementalOxygenLevel"), "Waiting for data"), 650);

        Log("After 5 mins");
        Log("At the end of the measurement interval, RHS status for manually entered vital sign: ");
        String FONmeasurement = driver.findElement(By.id("textManuallyEnteredFamilyOrNurseConcern")).getText();
        String RDmeasurement = driver.findElement(By.id("textManuallyEnteredRespirationDistress")).getText();
        String CRTmeasurement = driver.findElement(By.id("textManuallyEnteredCapillaryRefillTime")).getText();
        String CLmeasurement = driver.findElement(By.id("textManuallyEnteredConsciousnessLevel")).getText();
        String SOLmeasurement = driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevel")).getText();
        String WeightScalemeasurement = driver.findElement(By.id("textManuallyEnteredWeightMeasurement")).getText();
        helper.swipeActionScrollUp();
        String EWSmeasurement = driver.findElement(By.id("textEarlyWarningScoreMeasurement")).getText();
        String BPSystolicvalue = driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementSystolic")).getText();
        String SpO2value = driver.findElement(By.id("textManuallyEnteredSpO2")).getText();
        String Tempvalue = driver.findElement(By.id("textManuallyEnteredTemperature")).getText();
        helper.swipeActionScrollUp();
        String RRvalue = driver.findElement(By.id("textManuallyEnteredRespirationRate")).getText();
        String HRvalue = driver.findElement(By.id("textManuallyEnteredHeartRate")).getText();

        Log("Value for HR is " + HRvalue);
        Log("Value for RR is " + RRvalue);
        Log("Value for Temp is " + Tempvalue);
        Log("Value for SpO2 is " + SpO2value);
        Log("Value for BP is " + BPSystolicvalue);
        Log("Value for EWS is " + EWSmeasurement);
        Log("Value for Weight is " + WeightScalemeasurement);

        Log("Value for Supplemental Oxygen Levels is " + SOLmeasurement);
        Log("Value for Consciousness Levels is " + CLmeasurement);
        Log("Value for Capillary Refill is " + CRTmeasurement);
        Log("Value for  Respiration Distress is " + RDmeasurement);
        Log("Value for  Family or Nurse Concern is " + FONmeasurement);
        //To Compare the value
        String Expected = helper.getAppString("textWaitingForData");
        Assert.assertEquals(Expected, HRvalue);
        Assert.assertEquals(Expected, RRvalue);
        Assert.assertEquals(Expected, Tempvalue);
        Assert.assertEquals(Expected, SpO2value);
        Assert.assertEquals(Expected, BPSystolicvalue);
        Assert.assertEquals(Expected, EWSmeasurement);
        Assert.assertEquals(Expected, WeightScalemeasurement);
        Assert.assertEquals(Expected, SOLmeasurement);
        Assert.assertEquals(Expected, CLmeasurement);
        Assert.assertEquals(Expected, CRTmeasurement);
        Assert.assertEquals(Expected, RDmeasurement);
        Assert.assertEquals(Expected, FONmeasurement);
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_Waitingfordata_status_for_manually_enter_vital_sign_in_patient_vital_display_page_after_End_of_the_measurement_interval_For_an_adult_age_range() throws IOException {
        String case_id = "22482";

        helper.printTestStart("test_the_appearance_of_Waitingfordata_status_for_manually_enter_vital_sign_in_patient_vital_display_page_after_End_of_the_measurement_interval_Foradult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        // Click on ManualVitals Only
        manualVitalSignEntryForAdultAgeRange();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForAdultAgerange();
        helper.swipeActionScrollUp();
        helper.swipeActionScrollUp();

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textManuallyEnteredHeartRate"), "Waiting for data"), 650);

        Log("After 5 mins");
        Log("Check the status the Right hand side of the graph for manually entered ");
        String Expected = helper.getAppString("textWaitingForData");
        String HRvalue = driver.findElement(By.id("textManuallyEnteredHeartRate")).getText();
        String RRvalue = driver.findElement(By.id("textManuallyEnteredRespirationRate")).getText();
        String Tempvalue = driver.findElement(By.id("textManuallyEnteredTemperature")).getText();
        String SpO2value = driver.findElement(By.id("textManuallyEnteredSpO2")).getText();
        String BPSystolicvalue = driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementSystolic")).getText();
        helper.swipeActionScrollDown();
        String WeightScalemeasurement = driver.findElement(By.id("textManuallyEnteredWeightMeasurement")).getText();
        String EWSmeasurement = driver.findElement(By.id("textEarlyWarningScoreMeasurement")).getText();
        String CLmeasurement = driver.findElement(By.id("textManuallyEnteredConsciousnessLevel")).getText();
        String SOLmeasurement = driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevel")).getText();
        Log("Value for HR is " + HRvalue);
        Log("Value for RR is " + RRvalue);
        Log("Value for Temp is " + Tempvalue);
        Log("Value for SpO2 is " + SpO2value);
        Log("Value for BP is " + BPSystolicvalue);
        Log("Value for EWS is " + EWSmeasurement);
        Log("Value for Weight is " + WeightScalemeasurement);
        Log("Value for Supplemental Oxygen Levels is " + SOLmeasurement);
        Log("Value for Consciousness Levels is " + CLmeasurement);
        // Compare the value
        Assert.assertEquals(Expected, HRvalue);
        Assert.assertEquals(Expected, RRvalue);
        Assert.assertEquals(Expected, Tempvalue);
        Assert.assertEquals(Expected, SpO2value);
        Assert.assertEquals(Expected, BPSystolicvalue);
        Assert.assertEquals(Expected, EWSmeasurement);
        Assert.assertEquals(Expected, WeightScalemeasurement);
        Assert.assertEquals(Expected, SOLmeasurement);
        Assert.assertEquals(Expected, CLmeasurement);
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_appearance_of_Lifetouch_graph_In_patientvitaldisplaypage() throws IOException {
        String case_id = "22483";

        helper.printTestStart("check_the_appearance_of_Lifetouch_graph_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        checkLifetouch();
       //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }
    @Test
    public void check_the_appearance_of_WeightScale_graph_In_patientvitaldisplaypage() throws IOException {
        String case_id = "C54547";

        helper.printTestStart("check_the_appearance_of_WeightScale_graph_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleStatus);

        clickOnStartMonitoring();

        checkWeightScale();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_WeightSacle_for_Adult_agerange() throws IOException {
        String case_id = "C54548";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_WeightScale_for_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleStatus);

        clickOnStartMonitoring();

        String Weightvalue = driver.findElement(By.id("textWeightScaleMeasurement")).getText();
        Log("Value showing for Weight is: " + Weightvalue);
        checkNoEarlyWarningScoreCalculated();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }
    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_WeightSacle_for_Non_Adult_agerange() throws IOException {
        String case_id = "C54549";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_WeightScale_for_Non_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleStatus);

        clickOnStartMonitoring();

        String Weightvalue = driver.findElement(By.id("textWeightScaleMeasurement")).getText();
        Log("Value showing for Weight is: " + Weightvalue);
        checkNoEarlyWarningScoreCalculated();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void check_the_Lifetouch_graph_In_patientvitaldisplaypage_When_Lifetouch_is_Leadoff() throws IOException {
        String case_id = "22484";

        helper.printTestStart("check_the_Lifetouch_graph_In_patientvitaldisplaypage_When_Lifetouch_is_Leadoff",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__LIFETOUCH, true);
        checkLifetouchLeadoff();
        WebElement LifetouchLeadoff = driver.findElement(By.id("textViewLifetouchNotAttached"));
        assertTrue(LifetouchLeadoff.isDisplayed());
        Log("Graph page is showing :" + LifetouchLeadoff.getText());
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetouch_for_Adult_agerange() throws IOException {
        String case_id = "22485";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetouch_for_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        String Heartratevalue = driver.findElement(By.id("textHeartRate")).getText();
        Log("Value showing for heart rate is: " + Heartratevalue);
        String RespirationValue = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value :" + RespirationValue);
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetouch_for_Non_Adult_agerange() throws IOException {
        String case_id = "22486";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetouch_for_Non_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        String Heartratevalue = driver.findElement(By.id("textHeartRate")).getText();
        Log("Value showing for heart rate is: " + Heartratevalue);
        String RespirationValue = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value :" + RespirationValue);
        checkEarlyWarningScore();
       //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_Lifetouch_graph_after_removing_Lifetouch_from_the_session() throws IOException {
        String case_id = "22487";

        helper.printTestStart("check_the_Lifetouch_graph_after_removing_Lifetouch_from_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        String Heartratevalue = driver.findElement(By.id("textHeartRate")).getText();
        Log("Value showing for heart rate is: " + Heartratevalue);
        String RespirationValue = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value :" + RespirationValue);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Lifetouch from change session setting page");

        //Remove the Lifetouch
        removeLTDevice();
        checkRemoveDeviceEwsPopup(true);
        checkRecyclingReminderPopup();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Check the Remove status for Lifetouch
        Log("Check the Lifetouch status,after device got remove from the session");
        String Expected = helper.getAppString("textDeviceRemoved");

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeartRate"), Expected), 120);

        String ActualHR = driver.findElement(By.id("textHeartRate")).getText();
        Assert.assertEquals(Expected, ActualHR);
        String ActualRR = driver.findElement(By.id("textRespirationRate")).getText();
        Assert.assertEquals(Expected, ActualRR);
        Log("Heart rate and respiration rate status is showing removed");
        // Check the disappearance of option button and device ID for lifetouch
        assertTrue((driver.findElements(By.id("textLifetouchHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");
        assertTrue((driver.findElements(By.id("checkBoxLifetouchConfigurable")).size() == 0));
        assertTrue((driver.findElements(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).size() == 0));
        Log("No checkbox for Lifetouch,after it removed");
       //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_appearance_of_Lifetemp_graph_In_patientvitaldisplaypage() throws IOException {
        String case_id = "22489";

        helper.printTestStart("check_the_appearance_of_Lifetemp_graph_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        checkLifetemp();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);


    }

    @Test
    public void check_the_appearance_of_Lifetemp_graph_In_patientvitaldisplaypage_after_lifetemp_leadoff() throws IOException {
        String case_id = "22490";

        helper.printTestStart("check_the_appearance_of_Lifetemp_graph_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__TEMPERATURE, true);
        checkLifetempLeadOff();
        WebElement LifetempLeadoff = driver.findElement(By.id("textViewLifetempNotAttached"));
        assertTrue(LifetempLeadoff.isDisplayed());
        Log("Graph page is showing :" + LifetempLeadoff.getText());
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetemp_for_Adult_agerange() throws IOException {
        String case_id = "22491";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetemp_for_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        checkLifetemp();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetemp_for_NonAdult_agerange() throws IOException {
        String case_id = "22492";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_Lifetemp_for_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        checkLifetemp();
        checkNoEarlyWarningScoreCalculated();

        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void check_the_Lifetemp_graph_after_removing_Lifetemp_from_the_session() throws IOException {
        String case_id = "22493";

        helper.printTestStart("check_the_Lifetemp_graph_after_removing_Lifetemp_from_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        checkLifetemp();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Lifetouch from change session setting page");
        //Remove the Lifetemp
        removeLTempDevice();
        checkRemoveDeviceEwsPopup(true);
        checkRecyclingReminderPopup();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Check the Remove status for Lifetemp
        Log("Check the Lifetemp status,after device got remove from the session");
        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement temp_element = driver.findElement(By.id("textTemperatureReading"));

        Log("Waiting for measurement to time out");
        // wait until removed status is shown
        waitUntil(ExpectedConditions.textToBePresentInElement(temp_element, expected_string), 90);

        String ActualTemp = driver.findElement(By.id("textTemperatureReading")).getText();
        Assert.assertEquals(expected_string, ActualTemp);
        Log("Temperature status is showing removed");
        // Check the Left hand side of the Lifetemp graph
        assertTrue((driver.findElements(By.id("textLifetempHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_Nonin_for_NonAdult_agerange() throws IOException {
        String case_id = "22494";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_Nonin_for_NonAdult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        checkNonin();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_EWS_graph_In_patientvitaldisplaypage_After_adding_Nonin_for_Adult_agerange() throws IOException {
        String case_id = "22495";

        helper.printTestStart("check_EWS_graph_In_patientvitaldisplaypage_After_adding_Nonin_for_Adult_agerange",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        checkNonin();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_appearance_of_Nonin_graph_In_patientvitaldisplaypage() throws IOException {
        String case_id = "22496";

        helper.printTestStart("check_the_appearance_of_Nonin_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        checkNonin();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_appearance_of_BLENonin_graph_In_patientvitaldisplaypage() throws IOException {
        String case_id = "22497";

        helper.printTestStart("check_the_appearance_of_BLRNonin_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        String nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);

        //Battery appearance - wait for battery to appear
        waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(left_hand_side, By.id("imageBatteryPulseOx")), 130);
        Log("Battery sign displayed");

        WebElement nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(nonin_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        String spo2_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();
        Log("RHS of Nonin graph is showing :" + spo2_label);
        String spo2_value = right_hand_side.findElement(By.id("textSpO2")).getText();
        Log("Value showing for SpO2 is: " + spo2_value);
        String spo2_timestamp = right_hand_side.findElement(By.id("textPulseOximeterTimestamp")).getText();
        Log("Value showing for timestamp: " + spo2_timestamp);
        WebElement nonin_Graph = driver.findElement(By.id("fragment_graph_pulse_ox"));
        assertTrue(nonin_Graph.isDisplayed());
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void check_the_appearance_of_Nonin_graph_In_patientvitaldisplaypage_after_Nonin_leadoff() throws IOException {
        String case_id = "22498";

        helper.printTestStart("check_the_appearance_of_Nonin_graph_In_patientvitaldisplaypage_after_Nonin_leadoff",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__SPO2, true);
        checkNoninLeadoff();
        WebElement NoninLeadoff = driver.findElement(By.id("textViewPulseOxNotAttached"));
        assertTrue(NoninLeadoff.isDisplayed());
        Log("Graph page is showing :" + NoninLeadoff.getText());
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_appearance_of_BLE_Nonin_graph_In_patientvitaldisplaypage_after_Nonin_leadoff() throws IOException {
        String case_id = "22499";

        helper.printTestStart("check_the_appearance_of_BLE_Nonin_graph_In_patientvitaldisplaypage_after_Nonin_leadoff",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__SPO2, true);

        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        String nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        //Battery appearance - wait for battery to appear
        waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(left_hand_side, By.id("imageBatteryPulseOx")), 130);
        Log("Battery sign displayed");

        WebElement nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.textToBePresentInElement(nonin_measurement_element, helper.getAppString("textWaitingForData")), 130);

        String spo2_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();
        Log("RHS of Nonin graph is showing :" + spo2_label);
        String spo2_value = right_hand_side.findElement(By.id("textSpO2")).getText();
        Log("Value showing for SpO2 is: " + spo2_value);

        WebElement NoninLeadoff = driver.findElement(By.id("textViewPulseOxNotAttached"));
        assertTrue(NoninLeadoff.isDisplayed());
        Log("Graph page is showing :" + NoninLeadoff.getText());
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_Nonin_graph_after_removing_Nonin_from_the_session() throws IOException, ParseException {
        String case_id = "22500";

        helper.printTestStart("check_the_Nonin_graph_after_removing_Nonin_from_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        helper.spoofCommandIntent_setLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__SPO2, 2);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        checkNonin();

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time starts at: " + Start_time);


        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Nonin from change session setting page");
        //Remove the Nonin
        removeNonin();
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Check the Remove status for Nonin
        Log("Check the Nonin status,after device got remove from the session");
        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement spo2_element = driver.findElement(By.id("textSpO2"));

        Log("Waiting for measurement to time out");
        // wait until removed status is shown
        waitUntil(ExpectedConditions.textToBePresentInElement(spo2_element, expected_string), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);

        //compare the actual and expected validity period
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);

        String ActualSpO2 = spo2_element.getText();
        Log(ActualSpO2);
        Assert.assertEquals(expected_string, ActualSpO2);

        Log("Nonin status is showing removed");
        // Check the Lefthand side of Nonin graph
        assertTrue((driver.findElements(By.id("textPulseOxHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void check_the_Nonin_graph_after_removing_BLENonin_from_the_session() throws IOException, ParseException {
        String case_id = "22501";

        helper.printTestStart("check_the_Nonin_graph_after_removing_BLENonin_from_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        //checkNonin();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        String nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);

        //Battery appearance - wait for battery to appear
        waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(left_hand_side, By.id("imageBatteryPulseOx")), 130);
        Log("Battery sign displayed");

        WebElement nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(nonin_measurement_element, helper.getAppString("textWaitingForData"))), 200);

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time starts at: " + Start_time);


        String spo2_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();
        Log("RHS of Nonin graph is showing :" + spo2_label);
        String spo2_value = right_hand_side.findElement(By.id("textSpO2")).getText();
        Log("Value showing for SpO2 is: " + spo2_value);
        String spo2_timestamp = right_hand_side.findElement(By.id("textPulseOximeterTimestamp")).getText();
        Log("Value showing for timestamp: " + spo2_timestamp);
        WebElement nonin_Graph = driver.findElement(By.id("fragment_graph_pulse_ox"));
        assertTrue(nonin_Graph.isDisplayed());

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Nonin from change session setting page");

        //Remove the Nonin
        removeNonin();
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Check the Remove status for Nonin
        Log("Check the Nonin status,after device got remove from the session");
        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement spo2_element = driver.findElement(By.id("textSpO2"));
        // Check the Lefthand side of Nonin graph
        assertTrue((driver.findElements(By.id("textPulseOxHumanReadableDeviceId")).size() == 0));
        Log("No device Id found on LHS");
        Log("RHS of Nonin status is showing: " + spo2_element.getText()+" before time out");

        Log("Waiting for measurement to time out");


        // wait until removed status is shown
        waitUntil(ExpectedConditions.textToBePresentInElement(spo2_element, expected_string), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);

        //compare the actual and expected validity period
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);


        String ActualSpO2 = driver.findElement(By.id("textSpO2")).getText();
        //Log(ActualSpO2);
        Assert.assertEquals(expected_string, ActualSpO2);
        Log("After measurement time out RHS of Nonin status is showing: " + ActualSpO2);
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_appearance_of_BPAnD_graph_In_patientvitaldisplaypage() throws IOException {
        String case_id = "22502";

        helper.printTestStart("check_the_appearance_of_BPAnD_graph_In_patientvitaldisplaypage",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        checkBloodPressureAnD();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_adult_age_range_if_AnD_has_added() throws IOException {
        String case_id = "22504";

        helper.printTestStart("check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_adult_age_range_if_only_one_devices_added",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        checkBloodPressureAnD();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_Nonadult_age_range_if_AnD_has_added() throws IOException {
        String case_id = "22506";

        helper.printTestStart("check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_Nonadult_age_range_if_only_one_devices_added",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        checkBloodPressureAnD();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_Nonadult_age_range_if_all_5_devices_added() throws IOException {
        String case_id = "22507";

        helper.printTestStart("check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_Nonadult_age_range_if_all_5_devices_added",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleMStatus);

        clickOnStartMonitoring();

        Log("EWS status after adding all five devices");
        helper.swipeActionScrollDown();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_adult_age_range_if_all_5_devices_added() throws IOException {
        String case_id = "22508";

        helper.printTestStart("check_the_value_and_appearance_of_EWS_graph_In_patientvitaldisplaypage_for_adult_age_range_if_all_5_devices_added",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale  is " + AWeightScaleMStatus);

        clickOnStartMonitoring();

        Log("EWS status after adding all five devices");

        helper.swipeActionScrollDown();
        checkEarlyWarningScore();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);


    }

    @Test
    public void test_the_patientvitaldisplaypage_after_adding_dummy_devices_to_the_session() throws IOException {
        String case_id = "22509";

        helper.printTestStart("test_the_patientvitaldisplaypage_after_adding_dummy_devices_to_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale machine is " + AWeightScaleMStatus);

        clickOnStartMonitoring();

        checkLifetouch();
        checkLifetemp();
        checkNonin();
        checkBloodPressureAnD();
        helper.swipeActionScrollDown();
        checkWeightScale();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_patientvitaldisplaypage_after_removing_all_short_term_timeout_devices_From_the_session() throws IOException {
        String case_id = "22510";

        helper.printTestStart("test_the_patientvitaldisplaypage_after_removing_all_short_term_timeout_devices_From_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        // Check lifetouch status
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        Log("Lifetouch id is " + LifetouchId.getText());
        String Heartratevalue = driver.findElement(By.id("textHeartRate")).getText();
        Log("Value showing for heart rate is: " + Heartratevalue);
        String RespirationValue = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value :" + RespirationValue);
        checkLifetemp();

        //Lock the screen and choose the change session setting page

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        //Remove all the devices
        removeLTDevice();
        checkRemoveDeviceEwsPopup(true);
        checkRecyclingReminderPopup();

        removeLTempDevice();
        checkRemoveDeviceEwsPopup(true);
        checkRecyclingReminderPopup();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Check the Remove status for Lifetouch
        Log("Check the Lifetouch status,after device got remove from the session");
        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement hr_element = driver.findElement(By.id("textHeartRate"));
        Log("Waiting for measurement to time out");
        // wait until removed status is shown
        waitUntil(ExpectedConditions.textToBePresentInElement(hr_element, expected_string), 90);


        String ActualHR = hr_element.getText();
        Assert.assertEquals(expected_string, ActualHR);
        String ActualRR = driver.findElement(By.id("textRespirationRate")).getText();
        Assert.assertEquals(expected_string, ActualRR);
        Log("Heart rate and respiration rate status is showing removed");
        // Check the disappearance of option button and device ID for lifetouch
        assertTrue((driver.findElements(By.id("textLifetouchHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");

        // Check the Remove status for Lifetemp
        Log("Check the Lifetemp status,after device got remove from the session");
        WebElement temp_element = driver.findElement(By.id("textTemperatureReading"));
        Log("Waiting for measurement to time out");
        // wait until removed status is shown
        waitUntil(ExpectedConditions.textToBePresentInElement(temp_element, expected_string), 90);

        String ActualTemp = temp_element.getText();
        Assert.assertEquals(expected_string, ActualTemp);
        Log("Temperature status is showing" + ActualTemp);
        // Check the Left hand side of the Lifetemp graph
        assertTrue((driver.findElements(By.id("textLifetempHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");

        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_patientvitaldisplaypage_after_removing_all_LongTerm_term_timeout_devices_From_the_session() throws IOException {
        String case_id = "C54551";

        helper.printTestStart("test_the_patientvitaldisplaypage_after_removing_all_LongTerm_term_timeout_devices_From_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        //Set Long term measurement time out as 2 mins
        helper.spoofCommandIntent_setLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__SPO2, 2);
        helper.spoofCommandIntent_setLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, 2);

        helper.spoofQrCode_userUnlock();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyWeightScale();
        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale machine is " + AWeightScaleMStatus);

        clickOnStartMonitoring();


        checkNonin();
        checkBloodPressureAnD();
        checkWeightScale();
        //Lock the screen and choose the change session setting page

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        //Remove all the devices

        removeNonin();
        checkRemoveDeviceEwsPopup(true);

        removeBPMachine();
        checkRemoveDeviceEwsPopup(true);

        removeWeightScale();
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();


        // Check the Remove status for Nonin
        Log("Check the Nonin status,after device got remove from the session");
        // Check the Lefthand side of Nonin graph
        assertTrue((driver.findElements(By.id("textPulseOxHumanReadableDeviceId")).size() == 0));
        Log("No device Id found on LHS");
        //RHS of the Nonin Graph
        WebElement right_hand_side_Nonin = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));
        WebElement Nonin_measurement_element = right_hand_side_Nonin.findElement(By.id("textSpO2"));
        Log("Before time out the Nonin Measurements showing as"+Nonin_measurement_element.getText());

        // Check the Remove status for BP
        Log("Check the BP status,after device got remove from the session");
        // Check the Lefthand side of BP graph
        assertTrue((driver.findElements(By.id("textBloodPressureHumanReadableDeviceId")).size() == 0));
        Log("No device Id found on LHS");
        //RHS of the BP Graph
        WebElement right_hand_side_BP= driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));
        WebElement BP_measurement_element_Systolic = right_hand_side_BP.findElement(By.id("textBloodPressureMeasurementSystolic"));
        WebElement BP_measurement_element_Diastolic = right_hand_side_BP.findElement(By.id("textBloodPressureMeasurementDiastolic"));
        Log("Before time out the BP Measurements showing as"+BP_measurement_element_Systolic.getText()+"/"+BP_measurement_element_Diastolic.getText());

        // Check the Remove status for WeightScale
        Log("Check the WeightScale status,after device got remove from the session");
        // Check the Lefthand side of WeightScale graph
        assertTrue((driver.findElements(By.id("textWeightScaleHumanReadableDeviceId")).size() == 0));
        Log("No device Id found on LHS");
        //RHS of the WeightScale Graph
        WebElement right_hand_side_Weightscale= driver.findElement(By.id("linearLayoutWeightScaleMeasurementGroup"));
        WebElement Weightscale_measurement_element = right_hand_side_Weightscale.findElement(By.id("textWeightScaleMeasurement"));
        Log("Before time out the Weight Measurements showing as"+Weightscale_measurement_element.getText());

        Log("Waiting for measurement to time out");

        //Check the Nonin Status after time out
        waitUntil(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textDeviceRemoved")), 180);
        String Expected = helper.getAppString("textDeviceRemoved");
        String ActualNonin = driver.findElement(By.id("textSpO2")).getText();
        Assert.assertEquals(Expected, ActualNonin);
        Log("After time out Nonin status changes to :" + ActualNonin);

        //Check the BP status after time out
        waitUntil(ExpectedConditions.textToBePresentInElement(BP_measurement_element_Systolic, helper.getAppString("textDeviceRemoved")), 180);
        String ActualBP = driver.findElement(By.id("textBloodPressureMeasurementSystolic")).getText();
        Assert.assertEquals(Expected, ActualBP);
        Log("After time out BP status changes to :" + ActualBP);

        //Check the WeightScale status after time out
        waitUntil(ExpectedConditions.textToBePresentInElement(Weightscale_measurement_element, helper.getAppString("textDeviceRemoved")), 180);
        String ActualWeightScale = driver.findElement(By.id("textWeightScaleMeasurement")).getText();
        Assert.assertEquals(Expected, ActualWeightScale);
        Log("After time out Weight status changes to :" + ActualWeightScale);
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }
    @Test
    public void Test_the_patientvitalpage_after_adding_both_manual_entry_vital_sign_aswell_as_real_device_measurements_for_adult_age_range() throws IOException {
        String case_id = "22511";

        helper.printTestStart("Test_the_patientvitalpage_after_adding_both_manual_entry_vital_sign_aswell_as_real_device_measurements_for_adult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        manualVitalSignEntryForAdultAgeRange();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForAdultAgerange();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        helper.spoofQrCode_scanDummyWeightScale();
        //click on next
        clickOnNext();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale machine is " + AWeightScaleMStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
        Assert.assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_heart_rate")).size());
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_respiration_rate")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_lifetouch")).isDisplayed());
        Log("Previously displayed heart rate /respiration rate graph is replace by Lifetouch graph");
        // Check manually entered temp is overwritten by lifetemp graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_temperature")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_temperature")).isDisplayed());
        Log("Previously displayed Temp graph is replace by Lifetemp graph");
        //// Check manually entered SpO2 is overwritten by Nonin graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_spo2")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());
        Log("Previously displayed SpO2 graph is replace by Nonin graph");
        //// Check manually entered Blood Pressure graph  is overwritten by AnD graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_blood_pressure")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_blood_pressure")).isDisplayed());
        Log("Previously displayed BP graph is replace by AnD graph");

        helper.swipeActionScrollDown();
        //// Check manually entered Weight is overwritten by WeightScalegraph graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_weight")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_weight_scale")).isDisplayed());
        Log("Previously displayed Weight graph is replace by WeightScale graph");

        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_supplemental_oxygen")).isDisplayed());
        Log("Previously displayed Supplemental Oxygen level graph is still visisble");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_consciousness_level")).isDisplayed());
        Log("Previously displayed Consciousness level graph is still visisble");
        assertTrue(driver.findElement(By.id("fragment_graph_early_warning_scores")).isDisplayed());
        Log("EWS graph  is  visisble");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Test_the_patientvitalpage_after_adding_both_manual_entry_vital_sign_aswell_as_real_device_measurements_for_Non_adult_age_range() throws IOException {
        String case_id = "22512";

        helper.printTestStart("Test_the_patientvitalpage_after_adding_both_manual_entry_vital_sign_aswell_as_real_device_measurements_for_Non_adult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();
        manualVitalSignEntryForNonAdultAgeRange();

        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForNonAdultAgerange();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        helper.spoofQrCode_scanDummyWeightScale();
        //click on next
        clickOnNext();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_heart_rate")).size());
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_respiration_rate")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_lifetouch")).isDisplayed());
        Log("Previously displayed heart rate /respiration rate graph is replace by Lifetouch graph");
        // Check manually entered temp is overwritten by lifetemp graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_temperature")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_temperature")).isDisplayed());
        Log("Previously displayed Temp graph is replace by Lifetemp graph");
        //// Check manually entered SpO2 is overwritten by Nonin graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_spo2")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());
        Log("Previously displayed SpO2 graph is replace by Nonin graph");
        //// Check manually entered Blood Pressure graph  is overwritten by AnD graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_blood_pressure")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_blood_pressure")).isDisplayed());
        Log("Previously displayed BP graph is replace by AnD graph");
        helper.swipeActionScrollDown();
        assertTrue(driver.findElement(By.id("fragment_graph_early_warning_scores")).isDisplayed());
        Log("EWS graph  is  visisble");
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_weight")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_weight_scale")).isDisplayed());
        Log("Previously displayed Weight graph is replace by WeightScale graph");


        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_supplemental_oxygen")).isDisplayed());
        Log("Previously displayed Supplemental Oxygen level graph is still visisble");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_consciousness_level")).isDisplayed());
        Log("Previously displayed Consciousness level graph is still visisble");
        helper.swipeActionScrollDown();

        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_capillary_refill_time")).isDisplayed());
        Log("Previously displayed Capillary refill time graph is still visisble");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_respiration_distress")).isDisplayed());
        Log("Previously displayed Respiration distress graph is still visisble");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_family_or_nurse_concern")).isDisplayed());
        Log("Previously displayed Family or nurse concern graph is still visisble");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_Respiration_rate_and_Heart_rate_graph_got_replace_with_Lifetouch_graph_after_adding_lifetouch_to_the_session() throws IOException {
        String case_id = "22513";

        helper.printTestStart(" test_Respiration_rate_and_Heart_rate_graph_got_replace_with_Lifetouch_graph_after_adding_lifetouch_to_the_sessionn",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();

        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Click on next
        clickOnNext();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on next
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_heart_rate")).isDisplayed());
        Log("Manually eantered Heartrate graph display");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_respiration_rate")).isDisplayed());
        Log("Manually eantered Respiration rate graph display");
        Log("Add lifetouch device to the session");

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add Lifetouch button");
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_heart_rate")).size());
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_respiration_rate")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_lifetouch")).isDisplayed());
        Log("Previously displayed Heart rate /Respiration rate graph is replace by Lifetouch graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_Manually_entered_Temperature_graph_got_replace_with_Lifetemp_graph_after_adding_lifetemp_to_the_session() throws IOException {
        String case_id = "22514";

        helper.printTestStart("test_Manually_entered_Temperature_graph_got_replace_with_Lifetemp_graph_after_adding_lifetemp_to_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Temp is 38");
        // Click on next
        clickOnNext();
        // Click on next
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_temperature")).isDisplayed());
        Log("Manually eantered Temperature graph display");
        Log("Add lifetemp device to the session");

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add Lifetemp button");
        driver.findElement(By.id("buttonAddLifetemp")).click();
        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the lifetemp is " + ALTStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_temperature")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_temperature")).isDisplayed());
        Log("Previously displayed Temperature graph is replace by Lifetemp graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_Manually_entered_SpO2_graph_got_replace_with_Nonin_graph_after_adding_Nonin_to_the_session() throws IOException {
        String case_id = "22515";
        helper.printTestStart("test_Manually_entered_SpO2_graph_got_replace_with_Nonin_graph_after_adding_Nonin_to_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        // Click on next
        clickOnNext();
        // Click on next
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_spo2")).isDisplayed());
        Log("Manually eantered SpO2 graph display");
        Log("Add Nonin device to the session");

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add Nonin button");
        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_spo2")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());
        Log("Previously displayed SpO2 graph is replace by Nonin graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_Manually_entered_SpO2_graph_got_replace_with_Nonin_graph_after_adding_BLE_Nonin_to_the_session() throws IOException {
        String case_id = "22516";
        helper.printTestStart("test_Manually_entered_SpO2_graph_got_replace_with_Nonin_graph_after_adding_BLE_Nonin_to_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        // Click on next
        clickOnNext();
        // Click on next
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_spo2")).isDisplayed());
        Log("Manually eantered SpO2 graph display");
        Log("Add Nonin device to the session");

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add Nonin button");
        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_spo2")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());
        Log("Previously displayed SpO2 graph is replace by Nonin graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_Manually_entered_Bloodpressure_graph_got_replace_with_AnD_graph_after_adding_AnD_to_the_session() throws IOException {
        String case_id = "22517";
        helper.printTestStart("test_Manually_entered_Bloodpressure_graph_got_replace_with_AnD_graph_after_adding_AnD_to_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the Blood Pressure button
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
        // Click on next
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_blood_pressure")).isDisplayed());
        Log("Manually eantered Blood Pressure graph display");
        Log("Add AnD device to the session");

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add Blood Pressure button");
        driver.findElement(By.id("buttonAddBloodPressure")).click();
        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String BPConnectionStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP is " + BPConnectionStatus);

        clickOnStartMonitoring();

        //to check  manually entered heart rate /respiration rate graph is over written by bloodpressure graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_blood_pressure")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_blood_pressure")).isDisplayed());
        Log("Previously displayed Blood Pressure graph is replace by AnD graph");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void test_Manually_entered_Weight_graph_got_replace_with_WeightScale_graph_after_adding_WeightScale_to_the_session() throws IOException {
        String case_id = "C54550";
        helper.printTestStart("test_Manually_entered_Weight_graph_got_replace_with_WeightScale_graph_after_adding_WeightScale_to_the_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the Weight button
        Log("Click on Weight button");
        helper.findTextViewElementWithString("weight").click();
        Log("Enter the value for Weight");
        //Enter the value for Weight.98.6
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonDecimal")).click();
        driver.findElement(By.id("buttonNumberSix")).click();
        Log("Value entered for BP is 98.6");
        clickOnNext();
        // Click on next
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_weight")).isDisplayed());
        Log("Manually eantered Weight Scale graph display");
        Log("Add WeightScale device to the session");

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add WeightScale button");
        driver.findElement(By.id("buttonAddWeightScale")).click();
        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String WeightScaleConnectionStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + WeightScaleConnectionStatus);

        clickOnStartMonitoring();

        //to check  manually entered weightscale graph is over written by WeightScale graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_weight")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_weight_scale")).isDisplayed());
        Log("Previously displayed Manual weightScale graph is replace by WeightScale graph");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_checkbox_under_options_checkboxes_for_Lifetouch() throws IOException {
        String case_id = "22518";
        helper.printTestStart("test_the_appearance_of_checkbox_under_options_checkboxes_for_Lifetouch",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        String isOptionCheckbox = OptionCheckbox.getAttribute("checked");
        Log(OptionCheckbox.getText() + " status is " + isOptionCheckbox);
        //setupmode
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        Log("1st checkbox is : " + setupmode.getText());
        String isSetupmode = setupmode.getAttribute("checked");
        Log(setupmode.getText() + " status is " + isSetupmode);
        WebElement Poincare = driver.findElement(By.id("checkBoxLifetouchOptionsHeartRatePoincare"));
        assertTrue(Poincare.isDisplayed());
        Log("2nd checkbox is : " + Poincare.getText());
        String ispoincare = setupmode.getAttribute("checked");
        Log(Poincare.getText() + " status is " + ispoincare);
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        Log("3rd checkbox is : " + MotionMode.getText());
        String isMotionmode = setupmode.getAttribute("checked");
        Log(MotionMode.getText() + " status is " + isMotionmode);
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_dismisal_of_all_the_checkbox_after_option_checkbox_got_unchecked_for_Lifetouch() throws IOException {
        String case_id = "22519";
        helper.printTestStart("test_the_dismisal_of_all_the_checkbox_after_option_checkbox_got_unchecked_for_Lifetouch",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        String isOptionCheckbox = OptionCheckbox.getAttribute("checked");
        Log(OptionCheckbox.getText() + " status is " + isOptionCheckbox);
        //setupmode
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        Log("1st checkbox is : " + setupmode.getText());
        WebElement Poincare = driver.findElement(By.id("checkBoxLifetouchOptionsHeartRatePoincare"));
        assertTrue(Poincare.isDisplayed());
        Log("2nd checkbox is : " + Poincare.getText());
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        Log("3rd checkbox is : " + MotionMode.getText());
        //Uncheck the uption checkbox
        Log("Uncheck the Options checkbox");
        OptionCheckbox.click();
        String isOptionCheckbox1 = OptionCheckbox.getAttribute("checked");
        Log(OptionCheckbox.getText() + " status is " + isOptionCheckbox1);
        assertEquals(0, driver.findElements(By.id("checkBoxLifetouchOptionsSetupMode")).size());
        Log("Setupmode checkbox dissapear from the graph view");
        assertEquals(0, driver.findElements(By.id("checkBoxLifetouchOptionsHeartRatePoincare")).size());
        Log("PoinCare checkbox dissapear from the graph view");
        assertEquals(0, driver.findElements(By.id("checkBoxLifetouchOptionsRawAccelerometerMode")).size());
        Log("MotionMode checkbox dissapear from the graph view");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_Poincare_graph_window_for_Lifetouch() throws IOException {
        String case_id = "22520";
        helper.printTestStart("test_the_Poincare_graph_window_for_Lifetouch",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        WebElement Poincare = driver.findElement(By.id("checkBoxLifetouchOptionsHeartRatePoincare"));
        assertTrue(Poincare.isDisplayed());
        String isChecked = Poincare.getAttribute("checked");
        Log("Status of Poincare checkbox is " + isChecked);
        Log("Click on Poincare checkbox");
        Poincare.click();
        Log("Poincare checkbox is checked");

        poincare();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_Setupmode_checkbox_for_Lifetouch() throws IOException {
        String case_id = "22521";
        helper.printTestStart("test_the_Setupmode_checkbox_for_Lifetouch",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setSetupModeLength();

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        setupmode();
        Log("The screenshot has captured for Setupmode graph");
        //to  check setup mode graph is running on the screen
        helper.captureScreenShot();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }
    @Test
    //test that Setupmode blob disappear after ShowHide setupmode blob gotUnchecked from the footer.
    public void test_the_Setupmode_blob_disappear_after_ShowHidesetupmodeblob_gotUnchecked() throws IOException {
        String case_id = "22522";
        helper.printTestStart("test_the_Setupmode_blob_disappear_after_ShowHidesetupmodeblob_gotUnchecked",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        setupmode();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        //Uncheck the showhidesetup mode blob
        ShowAndHodeSetupModeCheckBoxUnchecked();

        //Run the setup mode again
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        Log("Run the setup mode again");
        setupmodeLHS.click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("plotRealTimeECG")),30);
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        // uncheck the setupmode check box
        setupmodeLHS.click();

        waitUntil(ExpectedConditions.attributeToBe(setupmodeLHS, "checked", "false"), 30);

        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
        //Check there is no setup mode blob on the patient vital screen
        //Click on teh setup mode blob point even if the blob was not there
        clickonSetupmodeBlob();
        //Check the setup mode bwindow didn't appear
        assertTrue((driver.findElements(By.id("textPopUpHistoricalSetupModeViewerTitle")).size() == 0));
        Log("No Setup mode blob on the Patient vital page if Show/Hide setup mode check box is unchecked. ");
        //Check that the show/hide check box remain unchecked after patient vital screen got refreshed
        clickOnLock();
        helper.spoofQrCode_userUnlock();
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");
        Log("Check the status of Show/Hide setup mode blob check box");
        WebElement ShowHideSetupModeBlobs = driver.findElement(By.id("checkBoxShowHideSetupModeBlobs"));
        Log("Status of Show/Hide setup mode checkbox is still "+ShowHideSetupModeBlobs.getAttribute("checked")+" after refreshing the patient vital screen");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_Setupmode_graph_for_Lifetouch_after_the_patientvitalpage_graph_got_refresh() throws IOException {
        String case_id = "22523";
        helper.printTestStart("test_the_appearance_of_Setupmode_graph_for_Lifetouch_after_the_patientvitalpage_graph_got_refresh",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        LifeTouchsetupmodewithExperiment();
        Log("The screenshot has captured for Setupmode graph");
        //to  check setup mode graph is running on the screen
        helper.captureScreenShot();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_Setupmode_graph_for_Lifetouch_is_running_for_specific_amount_of_time() throws IOException, ParseException {
        String case_id = "22524";
        helper.printTestStart("test_the_Setupmode_graph_for_Lifetouch_is_running_for_specific_amount_of_time",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        LifetouchSetupmoderuningForCertainPeriodOfTime();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Check_the_appearance_of_Lifetouch_setup_mode_popup_window() throws IOException {
        String case_id = "22525";
        helper.printTestStart("Check_the_appearance_of_Lifetouch_setup_mode_popup_window",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of Setupmode checkbox is " + isChecked);
        Log("Click on setupmode checkbox");
        setupmode.click();
        String time = driver.getDeviceTime();
        Log(time);
        // waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textHeaderGuiTime")), 130);
        //uncheck the setup mode check box
        Log("Uncheck the setupmode checkbox");
        WebElement checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        checkbox1.click();
        // click on the setup mode blob
        Log("Click on the setup mode blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_lifetouch_heart_rate")), 30);
        clickonSetupmodeBlob();
        WebElement setupmodeheading = driver.findElement(By.id("textPopUpHistoricalSetupModeViewerTitle"));
        assertTrue(setupmodeheading.isDisplayed());
        Log("setupmode window open");
        Log("Setup mode heading showing: " + setupmodeheading.getText());
        // Check the presence of setup mode time button
        WebElement Historicalsetupmodebutton = driver.findElement(By.id("textHistoricalSetupModeButton"));
        assertTrue(Historicalsetupmodebutton.isDisplayed());
        Log("Historical setupmode button read as : " + Historicalsetupmodebutton.getText());
        //check the apperance of lifetouch graph view in the setup mode window
        WebElement graphview = driver.findElement(By.id("plotHistoricalSetupModeLifetouch"));
        assertTrue(graphview.isDisplayed());
        Log("Appearance of graph view is :" + graphview.isDisplayed());

        // Check the ap[pearance of dismiss button
        WebElement Dismissbutton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        assertTrue(Dismissbutton.isDisplayed());
        Log("Preasence of dismiss button is: " + Dismissbutton.isDisplayed());
        // check the appearance of zoom in and out button
        WebElement ZoomInButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDecreaseViewport"));
        ZoomInButton.click();
        assertTrue(ZoomInButton.isDisplayed());
        Log("status of Zoom in button is: " + ZoomInButton.isDisplayed());
        WebElement ZoomOutButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerIncreaseViewport"));
        ZoomOutButton.click();
        assertTrue(ZoomOutButton.isDisplayed());
        Log("status of Zoom out button is: " + ZoomOutButton.isDisplayed());
        assertTrue((driver.findElements(By.id("patient_vitals_display_scrollview")).size() == 0));
        Log("Setup mode window overlap on patientvital dispaly graph view");
        helper.captureScreenShot();
        //Test the dismiss button dismiss the setup mode popup window
        Log("Click on the dismiss button");
        Dismissbutton.click();
        assertTrue(driver.findElement(By.id("patient_vitals_display_scrollview")).isDisplayed());
        Log("Setup mode popup window dismissed");
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void lifetouchPeriodicSetupMode() throws IOException {
        String case_id = "22526";
        helper.printTestStart("lifetouchPeriodicSetupMode",case_id);
        //checkAdminSettingsCorrect(false, true, false);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());

        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("one_minute"));
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("two_minutes"));
        Log("Periodic setup mode run for : 1 minute in every 2");

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        WebElement Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        //assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxLifetouchConfigurable"), helper.getAppString("textPeriodicSetupMode")), 135);
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        isCheckedShowPeriodicSetupMode = Checkbox1.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        //check the appearnce of option check box when periodic setup mode is running
        assertTrue((driver.findElements(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).size() == 0));
        Log("Wait until periodic setup mode finish and check the appearance of setup mode button and Lifetouch page");
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        //Check the appearance of option checkbox after periodic setup mode finish
        assertTrue(OptionCheckbox.isDisplayed());
        Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_Periodicsetup_mode_for_Lifetouch_after_the_patientvitalpage_graph_got_refresh() throws IOException {
        String case_id = "22527";
        helper.printTestStart("test_the_appearance_of_Periodicsetup_mode_for_Lifetouch_after_the_patientvitalpage_graph_got_refresh",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement PeriodicSetupModeLength = driver.findElement(By.id("android:id/text1"));
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("two_minutes"));
        WebElement PeriodicSetupModeRunInEvery = driver.findElement(By.id("android:id/text1"));
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + "In Every" + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        WebElement Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        //assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxLifetouchConfigurable"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        //while periodicsetup mode is running refresh the page
        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textLifetouchRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textLifetouchRedBorderStatus")).isDisplayed());
        Log("On the QR unlock screen page ,Can see " + Qrscrenview.getText() + " For lifetouch");
        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");

        Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isCheckedShowPeriodicSetupMode = Checkbox1.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        Log("Wait until periodic setup mode finish and check the appearance of setup mode button and Lifetouch page");
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        //Check the appearance of option checkbox after periodic setup mode finish
        OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_Periodic_setupmode_over_ride_upon_Lifetouch_setup_mode_while_running() throws IOException {
        String case_id = "22528";
        helper.printTestStart("test_Periodic_setupmode_over_ride_upon_Lifetouch_setup_mode_while_running",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());

        // Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("one_minute"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        OptionCheckbox.click();
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("start the setupmode ");
        setupmode.click();

        Log("Check the Setupmode check box status in Lefthandsisde of the Lifetouch graph");
        // Check the setup mode checkbox status in LHS side of the graph
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = setupmodeLHS.getAttribute("checked");
        Log("Check the Setupmode check box status in Lefthandsisde of the Lifetouch graph is :" + isChecked);

        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());

        WebElement Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        //assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxLifetouchConfigurable"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        isCheckedShowPeriodicSetupMode = Checkbox1.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is: " + isCheckedShowPeriodicSetupMode);
        //check the appearnce of option check box when periodic setup mode is running
        // Assert.assertFalse(OptionCheckbox.isDisplayed());
        Log("Wait until periodic setup mode finish and check the appearance of setup mode button and Lifetouch page");
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        //Check the appearance of option checkbox after periodic setup mode finish
        assertTrue(OptionCheckbox.isDisplayed());
        Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        //check the status of setup mode checkbox
        setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = setupmodeLHS.getAttribute("checked");
        Log("Setupmode check box is " + isChecked);
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_the_Motionmode_checkbox_for_Lifetouch() throws IOException {
        String case_id = "22529";
        helper.printTestStart("test_the_MotionMode_checkbox_for_Lifetouch",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        motionmode();
        Log("The screenshot has captured for motion mode graph");
        //to  check motionmode graph is running on the screen
        helper.captureScreenShot();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_Motionmode_graph_for_Lifetouch_after_the_patientvitalpage_graph_got_refresh() throws IOException {
        String case_id = "22530";
        helper.printTestStart("test_the_appearance_of_Motionmode_graph_for_Lifetouch_after_the_patientvitalpage_graph_got_refresh",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        motionmodewithExperiment();
        Log("The screenshot has captured for motion mode graph");
        //to  check motionmode graph is running on the screen
        helper.captureScreenShot();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_Motionmode_graph_for_Lifetouch_is_running_mimnimum_of_3mins() throws IOException {
        String case_id = "22531";
        helper.printTestStart("test_the_Motionmode_graph_for_Lifetouch_is_running_mimnimum_of_3mins",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        motionmodeRunningForCeratainTime();
        Log("The screenshot has captured for motion mode graph");
        //to  check motionmode graph is running on the screen
        helper.captureScreenShot();
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_periodicsetupmode_override_while_MotionMode_is_running_for_lifetouch() throws IOException {
        String case_id = "22532";
        helper.printTestStart("test_periodicsetupmode_override_while_MotionMode_is_running_for_lifetouch",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("one_minute"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show periodic setup mode  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();


        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        Log("Check the Motiomode check box status in Lefthandsisde of the Lifetouch graph");
        // Check the Motion Mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Lefthandside of the Lifetouch graph is :" + isChecked);
        // Check the appearance and presence of graph
        WebElement motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
        assertTrue(motionModeGraphView.isDisplayed());
        WebElement Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        //assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxLifetouchConfigurable"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        //check the appearnce of option check box when periodic setup mode is running
        assertTrue((driver.findElements(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).size() == 0));
        // driver.findElement(By.id("plotRawAccelerometer")
        assertTrue((driver.findElements(By.id("plotRawAccelerometer")).size() == 0));
        Log("Wait until periodic setup mode finish and check the appearance of setup mode button and Lifetouch page");
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        //Check the appearance of option checkbox after periodic setup mode finish
        assertTrue(OptionCheckbox.isDisplayed());
        Checkbox1 = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        Log("LHS Checkbox Text change to " + Checkbox1.getText());
        helper.captureScreenShot();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);


    }

    @Test
    public void test_the_switch_between_motionmode_to_setupmode_graph() throws IOException {
        String case_id = "22533";
        helper.printTestStart("test_the_switch_between_motionmode_to_setupmode_graph",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        Motionmode_to_setupMode();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_switch_between_setupmode_to_motionmode_graph() throws IOException {
        String case_id = "22534";
        helper.printTestStart("test_the_switch_between_motionmode_to_setupmode_graph",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        Setupmode_to_motionMode();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_appearance_of_annotation_Popup_window() throws IOException {
        String case_id = "22535";
        helper.printTestStart("test_the_appearance_of_annotation_Popup_window",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Enter vital signHH
        manuallyEnteredVitalSign();
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Click on next
        clickOnNext();
        // Click on Next button
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        // Click on the Next button
        clickOnNext();
        // Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton = driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is " + HideButton);
        // Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        // Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on patientvital display button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Click on Annotation Blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_manually_entered_heart_rate")), 30);
        clickonAnnotationBlob();
        String ActualMeasurementTime = helper.findTextViewElementWithString("annotations").getText();
        String Expected = "Annotations";
        Assert.assertEquals(ActualMeasurementTime, Expected);
        Log("Lead us to the Annotion pop up window with heading" + ActualMeasurementTime);
        assertTrue((driver.findElements(By.id("fragment_graph_manually_entered_heart_rate")).size() == 0));
        Log("The annotation popup window is overwritten the patientvitaldisplay page");
        WebElement AnnotionTimestamp = driver.findElement(By.id("textViewTimestamp"));
        Log("Annotation time stamp : " + ActualMeasurementTime);
        WebElement TextAppear = driver.findElementById("textViewAnnotation");
        Log("Text appear for annotion is : " + TextAppear.getText());
        Log("Click on the Dismiss button");
        WebElement Dismiss = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        Dismiss.click();
        assertTrue((driver.findElements(By.id("listview")).size() == 0));
        Log("Annotation popup window dismissed");
        Log("Back to patientviatl dispaly page graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Test_the_appearance_of_Annotation_pop_up_window_If_multiple_annotation_has_entered() throws IOException {
        String case_id = "22536";
        helper.printTestStart("Test_the_appearance_of_Annotation_pop_up_window_If_multiple_annotation_has_entered",case_id);

        checkAdminSettingsCorrect(false, false, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Enter vital signHH
        manuallyEnteredVitalSign();
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Click on next
        clickOnNext();
        // Click on Next button
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        // Click on the Next button
        clickOnNext();
        // Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton = driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is " + HideButton);
        // Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        // Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on annotation button again
        // Click on the Annotation button
        Log("Enter annotation again ");
        driver.findElement(By.id("buttonBigButtonFive")).click();
        predefinedAnnotation();
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Click on Annotation Blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_manually_entered_heart_rate")), 30);
        clickonAnnotationBlob();
        String ActualMeasurementTime = helper.findTextViewElementWithString("annotations").getText();
        String Expected = "Annotations";
        Assert.assertEquals(ActualMeasurementTime, Expected);
        Log("Lead us to the Annotation pop up window with heading : " + ActualMeasurementTime);
        Log("Print all the time stamps with annotation Text");
        List<WebElement> annotationtimestamp = driver.findElements(By.id("textViewTimestamp"));
        int iSize = annotationtimestamp.size();
        List<WebElement> annotationText = driver.findElements(By.id("textViewAnnotation"));
        for (int i = 0; i < iSize; i++) {
            Log("Annotation time stamp : " + annotationtimestamp.get(i).getText());
            Log("Text appear for annotation is : " + annotationText.get(i).getText());
        }

        Log("Click on the Dismiss button");
        WebElement Dismiss = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        Dismiss.click();

        Log("Back to patient vital dispaly page graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Check_the_value_entered_for_annotation_is_same_as_value_displayed_in_the_Annotation_pop_up_window() throws IOException {
        String case_id = "22537";
        helper.printTestStart("Check_the_value_entered_for_annotation_is_same_as_value_displayed_in_the_Annotation_pop_up_window",case_id);

        checkAdminSettingsCorrect(false, false, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Enter vital signHH
        manuallyEnteredVitalSign();
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Click on next
        clickOnNext();
        // Click on Next button
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();

        String expected_display = predefinedAnnotation();

        Log("Check the patient vital display page");
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Click on Annotation Blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_manually_entered_heart_rate")), 30);
        clickonAnnotationBlob();
        String ActualHeading = helper.findTextViewElementWithString("annotations").getText();
        String Expected = "Annotations";
        Assert.assertEquals(ActualHeading, Expected);
        Log("Lead us to the Annotion pop up window with heading : " + ActualHeading);
        WebElement AnnotionTimestamp = driver.findElement(By.id("textViewTimestamp"));
        Log("Annotation time stamp : " + AnnotionTimestamp.getText());
        WebElement TextAppear = driver.findElementById("textViewAnnotation");
        String AnnotionTextAppear = TextAppear.getText();
        Log("Text appear for annotion is : " + AnnotionTextAppear);
        //Dismiss the pop up window
        Log("Click on the Dismiss button");
        WebElement Dismiss = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        Dismiss.click();
        // ToDo: this wait is sometimes flaky - not sure why
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("patient_vitals_display_scrollview")), 60);

        //assertTrue(driver.findElement(By.id("patient_vitals_display_scrollview")).isDisplayed());

        Log("Compare the value displayed for Annotion on Patient Vitals Display is same as expected");
        Assert.assertEquals(expected_display, AnnotionTextAppear);
        Log("Value display for Annotion is same in both the place");

        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Noninsetupmode() throws IOException {
        String case_id = "22538";
        helper.printTestStart("Noninsetupmode",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());

        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 130);
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void BLENoninsetupmode() throws IOException {
        String case_id = "22539";
        helper.printTestStart("BLENoninsetupmode",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());
        helper.captureScreenShot();
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 130);
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_Setupmode_graph_for_Nonin_is_running_for_specific_amount_of_time() throws IOException, ParseException {
        String case_id = "22540";
        helper.printTestStart("test_the_Setupmode_graph_for_Nonin_is_running_for_specific_amount_of_time",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        SetupmodeforNoninrunningForCertainPeriodOfTime();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_Setupmode_graph_for_BLENonin_is_running_for_specific_amount_of_time() throws IOException, ParseException {
        String case_id = "22541";
        helper.printTestStart("test_the_Setupmode_graph_for_BLENonin_is_running_for_specific_amount_of_time",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        SetupmodeforNoninrunningForCertainPeriodOfTime();
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Test_the_Appearance_of_Nonin_setupMode_graph_after_patient_vital_page_graph_got_refresh() throws IOException {
        String case_id = "22542";
        helper.printTestStart("Test_the_Appearance_of_Nonin_setupMode_graph_after_patient_vital_page_graph_got_refresh",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());
        Log("While setup mode is running Click on unlock button");

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(Qrscrenview.isDisplayed());
        Log("Can see " + Qrscrenview.getText());

        Log("On the QR unlock screen page ,Can see " + Qrscrenview.getText() + " For Nonin");
        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");

        //check the motion mode graph is still running
        SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Setupmode graph is still running");

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("plotRealTimeECG")), 50);
        // uncheck the setupmode check box
        Log("Uncheck the setup mode checkbox");
        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        SetUpmodeLHS.click();
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 130);
        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Test_the_Appearance_of_BLENonin_setupMode_graph_after_patient_vital_page_graph_got_refresh() throws IOException {
        String case_id = "22543";
        helper.printTestStart("Test_the_Appearance_of_BLENonin_setupMode_graph_after_patient_vital_page_graph_got_refresh",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());
        Log("While setup mode is running Click on unlock button");

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(Qrscrenview.isDisplayed());
        Log("Can see " + Qrscrenview.getText());

        Log("On the QR unlock screen page ,Can see " + Qrscrenview.getText() + " For Nonin");
        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();

        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");

        //check the setup mode graph is still running
        SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Setupmode graph is still running");

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("plotRealTimeECG")), 50);
        // uncheck the setupmode check box
        Log("Uncheck the setup mode checkbox");
        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        SetUpmodeLHS.click();
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 130);
        Assert.assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Nonin_Periodic_setupmode() throws IOException {
        String case_id = "22544";
        helper.printTestStart("Nonin_Periodic_setupmode",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("one_minute"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxPulseOxSetupMode"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        isCheckedShowPeriodicSetupMode = SetUpmodeLHS.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        //Wait until periodic setup mode finish and check the appearance of setup mode button and Nonin Graph page
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void BLE_Nonin_Periodic_setupmode() throws IOException {
        String case_id = "22545";
        helper.printTestStart("BLE_Nonin_Periodic_setupmode",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("one_minute"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxPulseOxSetupMode"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        isCheckedShowPeriodicSetupMode = SetUpmodeLHS.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        //Wait until periodic setup mode finish and check the appearance of setup mode button and Nonin Graph page
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Check_By_refreshing_the_page_while_Nonin_Periodic_setupmode_is_running() throws IOException {
        String case_id = "22546";
        helper.printTestStart("check_By_refreshing_the_page_while_Nonin_Periodic_setupmode_is_running",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("one_minute"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("two_minutes"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxPulseOxSetupMode"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        isCheckedShowPeriodicSetupMode = SetUpmodeLHS.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        //Refresh the page while periodc setup  mode is running

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(Qrscrenview.isDisplayed());
        Log("Can see " + Qrscrenview.getText());

        Log("On the QR unlock screen page ,Can see " + Qrscrenview.getText() + " For Nonin");
        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");
        //check the motion mode graph is still running
        WebElement setupmodegraph = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(setupmodegraph.isDisplayed());
        Log("Setupmode graph is still running");
        //Wait until periodic setup mode finish and check the appearance of setup mode button and Nonin Graph page
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void Check_By_refreshing_the_page_while_BLENonin_Periodic_setupmode_is_running() throws IOException {
        String case_id = "22547";
        helper.printTestStart("check_By_refreshing_the_page_while_BLENonin_Periodic_setupmode_is_running",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("one_minute"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("two_minutes"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());
        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxPulseOxSetupMode"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        isCheckedShowPeriodicSetupMode = SetUpmodeLHS.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        //Refresh the page while periodc setup  mode is running

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(Qrscrenview.isDisplayed());
        Log("Can see " + Qrscrenview.getText());

        Log("On the QR unlock screen page ,Can see " + Qrscrenview.getText() + " For Nonin");
        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");
        //check the motion mode graph is still running
        WebElement setupmodegraph = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(setupmodegraph.isDisplayed());
        Log("Setupmode graph is still running after refreshing the page ");
        //Wait until periodic setup mode finish and check the appearance of setup mode button and Nonin Graph page
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    // Check the appearance of popup window after periodic setup mode finish for BLE Nonin
    public void Check_the_appearance_of_BLENonin_popup_window_after_periodic_setup_mode_finish() throws IOException {
        String case_id = "22548";
        helper.printTestStart("Check_the_appearance_of_BLENonin_popup_window_after_periodic_setup_mode_finish",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("one_minute"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxPulseOxSetupMode"), helper.getAppString("textPeriodicSetupMode")), 120);
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        isCheckedShowPeriodicSetupMode = SetUpmodeLHS.getAttribute("checked");
        Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
        //Wait until periodic setup mode finish and check the appearance of setup mode button and Nonin Graph page
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
        // click on the setup mode blob
        Log("Click on the setup mode blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_pulse_ox")), 30);
        clickonSetupmodeBlob();
        WebElement setupmodeheading = driver.findElement(By.id("textPopUpHistoricalSetupModeViewerTitle"));
        assertTrue(setupmodeheading.isDisplayed());
        Log("setupmode window open");
        Log("Setup mode heading showing: " + setupmodeheading.getText());
        // Check the presence of setup mode time button
        WebElement Historicalsetupmodebutton = driver.findElement(By.id("textHistoricalSetupModeButton"));
        assertTrue(Historicalsetupmodebutton.isDisplayed());
        Log("Historical setupmode button read as : " + Historicalsetupmodebutton.getText());
        //check the apperance of lifetouch graph view in the setup mode window
        WebElement graphview = driver.findElement(By.id("plotHistoricalSetupModeLifetouch"));
        assertTrue(graphview.isDisplayed());
        Log("Appearance of graph view is :" + graphview.isDisplayed());

        // Check the appearance of dismiss button
        WebElement Dismissbutton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        assertTrue(Dismissbutton.isDisplayed());
        Log("Preasence of dismiss button is: " + Dismissbutton.isDisplayed());
        // check the appearance of zoom in and out button
        WebElement ZoomInButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDecreaseViewport"));
        // Click on Zoom InButton
        ZoomInButton.click();
        assertTrue(ZoomInButton.isDisplayed());
        Log("status of Zoom in button is: " + ZoomInButton.isDisplayed());
        WebElement ZoomOutButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerIncreaseViewport"));
        // Click on ZoomOutButton
        ZoomOutButton.click();
        assertTrue(ZoomOutButton.isDisplayed());
        Log("status of Zoom out button is: " + ZoomOutButton.isDisplayed());
        assertTrue((driver.findElements(By.id("patient_vitals_display_scrollview")).size() == 0));
        Log("Setup mode window overlap on patientvital dispaly graph view");
        helper.captureScreenShot();
        //Test the dismiss button dismiss the setup mode popup window
        Log("Click on the dismiss button");
        Dismissbutton.click();
        assertTrue(driver.findElement(By.id("patient_vitals_display_scrollview")).isDisplayed());
        Log("Setup mode popup window dismissed");
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);
    }

    @Test
    // Check the appearance of popup window after periodic setup mode finish for Nonin
    public void Check_the_appearance_of_Nonin_popup_window_after_periodic_setup_mode_finish() throws IOException {
        String case_id = "22549";
        helper.printTestStart("Check_the_appearance_of_Nonin_popup_window_after_periodic_setup_mode_finish",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Select the periodic setup mode value
        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Periodic Setup Mode
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");
        assertTrue(checkbox_periodic_setup_mode.isDisplayed());
        //Set the periodic setup mode interval
        helper.clickElementOnSpinner("spinnerDevicePeriodicModeActiveTimeInSeconds", helper.getAppString("thirty_seconds"));
        WebElement ActiveTime = driver.findElement(By.id("spinnerDevicePeriodicModeActiveTimeInSeconds"));
        WebElement PeriodicSetupModeLength = ActiveTime.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeLength.getText());
        helper.clickElementOnSpinner("spinnerDevicePeriodicModePeriodTimeInSeconds", helper.getAppString("one_minute"));
        WebElement RunInevery = driver.findElement(By.id("spinnerDevicePeriodicModePeriodTimeInSeconds"));
        WebElement PeriodicSetupModeRunInEvery = RunInevery.findElement(By.id("android:id/text1"));
        Log(PeriodicSetupModeRunInEvery.getText());
        Log("Periodic setup mode run for : " + PeriodicSetupModeLength.getText() + " In Every " + PeriodicSetupModeRunInEvery.getText());

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if (isCheckedShowPeriodicSetupMode.equals("false")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box checked");
        } else {
            Log("Periodic setup mode check box  already checked");
        }

        driver.findElementById("buttonLock").click();

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());

        //Wait until periodic set up mode  run for couple of times
        int i;
        for (i = 0; i <= 5; i++) {
            waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("checkBoxPulseOxSetupMode"), helper.getAppString("textPeriodicSetupMode")), 120);
            Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
            isCheckedShowPeriodicSetupMode = SetUpmodeLHS.getAttribute("checked");
            Log("Periodic setup mode Checkbox status is :" + isCheckedShowPeriodicSetupMode);
            //Wait until periodic setup mode finish and check the appearance of setup mode button and Nonin Graph page
            waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

            SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
            Log("LHS Checkbox Text change to " + SetUpmodeLHS.getText());
            i++;
        }
        // click on the setup mode blob
        Log("Click on the setup mode blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_pulse_ox")), 30);
        clickonSetupmodeBlob();
        helper.captureScreenShot();
        WebElement setupmodeheading = driver.findElement(By.id("textPopUpHistoricalSetupModeViewerTitle"));
        assertTrue(setupmodeheading.isDisplayed());
        Log("setupmode window open");
        // Check the presence of setup mode time button
        WebElement Historicalsetupmodebutton = driver.findElement(By.id("textHistoricalSetupModeButton"));
        assertTrue(Historicalsetupmodebutton.isDisplayed());
        List<WebElement> historical_setup_mode_time_buttons = driver.findElements(By.id("textHistoricalSetupModeButton"));
        Log("Number of Setup mode time buttons displayed on the page is " + historical_setup_mode_time_buttons.size());

        for (i = 0; i <= historical_setup_mode_time_buttons.size(); i++)
        {
            Log("-------------------------------------------------------");
            WebElement historical_button = historical_setup_mode_time_buttons.get(i);
            Log("Click on the Historical button");
            historical_button.click();
            Log(" Historical setupmode button read as : " + historical_button.getText());
            Log("Setup mode heading showing: " + setupmodeheading.getText());
            //check the apperance of lifetouch graph view in the setup mode window
            WebElement graphview = driver.findElement(By.id("plotHistoricalSetupModeLifetouch"));
            assertTrue(graphview.isDisplayed());
            Log("Appearance of graph view is :" + graphview.isDisplayed());
            Log("-------------------------------------------------------");
            i++;
        }

        // Check the appearance of dismiss button
        WebElement Dismissbutton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        assertTrue(Dismissbutton.isDisplayed());
        Log("Preasence of dismiss button is: " + Dismissbutton.isDisplayed());
        // check the appearance of zoom in and out button
        WebElement ZoomInButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDecreaseViewport"));
        // Click on Zoom InButton
        ZoomInButton.click();
        assertTrue(ZoomInButton.isDisplayed());
        Log("status of Zoom in button is: " + ZoomInButton.isDisplayed());
        WebElement ZoomOutButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerIncreaseViewport"));
        // Click on ZoomOutButton
        ZoomOutButton.click();
        assertTrue(ZoomOutButton.isDisplayed());
        Log("status of Zoom out button is: " + ZoomOutButton.isDisplayed());
        assertTrue((driver.findElements(By.id("patient_vitals_display_scrollview")).size() == 0));
        Log("Setup mode window overlap on patientvital dispaly graph view");
        helper.captureScreenShot();
        //Test the dismiss button dismiss the setup mode popup window
        Log("Click on the dismiss button");
        Dismissbutton.click();
        assertTrue(driver.findElement(By.id("patient_vitals_display_scrollview")).isDisplayed());
        Log("Setup mode popup window dismissed");
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);


    }


    @Test
    public void Check_the_appearance_of_Nonin_setup_mode_popup_window() throws IOException {
        String case_id = "22550";
        helper.printTestStart("Check_the_appearance_of_Nonin_setup_mode_popup_window",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());

        String time = driver.getDeviceTime();
        Log(time);
        // waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textHeaderGuiTime")), 130);
        //uncheck the setup mode check box
        Log("Uncheck the setupmode checkbox");
        SetUpmodeLHS.click();
        // click on the setup mode blob
        Log("Click on the setup mode blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_pulse_ox")), 30);
        clickonSetupmodeBlob();
        WebElement setupmodeheading = driver.findElement(By.id("textPopUpHistoricalSetupModeViewerTitle"));
        assertTrue(setupmodeheading.isDisplayed());
        Log("setupmode window open");
        Log("Setup mode heading showing: " + setupmodeheading.getText());
        // Check the presence of setup mode time button
        WebElement Historicalsetupmodebutton = driver.findElement(By.id("textHistoricalSetupModeButton"));
        assertTrue(Historicalsetupmodebutton.isDisplayed());
        Log("Historical setupmode button read as : " + Historicalsetupmodebutton.getText());
        //check the apperance of lifetouch graph view in the setup mode window
        WebElement graphview = driver.findElement(By.id("plotHistoricalSetupModeLifetouch"));
        assertTrue(graphview.isDisplayed());
        Log("Appearance of graph view is :" + graphview.isDisplayed());

        // Check the appearance of dismiss button
        WebElement Dismissbutton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        assertTrue(Dismissbutton.isDisplayed());
        Log("Preasence of dismiss button is: " + Dismissbutton.isDisplayed());
        // check the appearance of zoom in and out button
        WebElement ZoomInButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDecreaseViewport"));
        // Click on Zoom InButton
        ZoomInButton.click();
        assertTrue(ZoomInButton.isDisplayed());
        Log("status of Zoom in button is: " + ZoomInButton.isDisplayed());
        WebElement ZoomOutButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerIncreaseViewport"));
        // Click on ZoomOutButton
        ZoomOutButton.click();
        assertTrue(ZoomOutButton.isDisplayed());
        Log("status of Zoom out button is: " + ZoomOutButton.isDisplayed());
        assertTrue((driver.findElements(By.id("patient_vitals_display_scrollview")).size() == 0));
        Log("Setup mode window overlap on patientvital dispaly graph view");
        helper.captureScreenShot();
        //Test the dismiss button dismiss the setup mode popup window
        Log("Click on the dismiss button");
        Dismissbutton.click();
        assertTrue(driver.findElement(By.id("patient_vitals_display_scrollview")).isDisplayed());
        Log("Setup mode popup window dismissed");
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Check_the_appearance_of_BLE_Nonin_setup_mode_popup_window() throws IOException {
        String case_id = "22551";
        helper.printTestStart("Check_the_appearance_of_BLE_Nonin_setup_mode_popup_window",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());

        String time = driver.getDeviceTime();
        Log(time);
        // waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textHeaderGuiTime")), 130);
        //uncheck the setup mode check box
        Log("Uncheck the setupmode checkbox");
        SetUpmodeLHS.click();
        // click on the setup mode blob
        Log("Click on the setup mode blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_pulse_ox")), 30);
        clickonSetupmodeBlob();
        WebElement setupmodeheading = driver.findElement(By.id("textPopUpHistoricalSetupModeViewerTitle"));
        assertTrue(setupmodeheading.isDisplayed());
        Log("setupmode window open");
        Log("Setup mode heading showing: " + setupmodeheading.getText());
        // Check the presence of setup mode time button
        WebElement Historicalsetupmodebutton = driver.findElement(By.id("textHistoricalSetupModeButton"));
        assertTrue(Historicalsetupmodebutton.isDisplayed());
        Log("Historical setupmode button read as : " + Historicalsetupmodebutton.getText());
        //check the apperance of lifetouch graph view in the setup mode window
        WebElement graphview = driver.findElement(By.id("plotHistoricalSetupModeLifetouch"));
        assertTrue(graphview.isDisplayed());
        Log("Appearance of graph view is :" + graphview.isDisplayed());

        // Check the appearance of dismiss button
        WebElement Dismissbutton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDismiss"));
        assertTrue(Dismissbutton.isDisplayed());
        Log("Preasence of dismiss button is: " + Dismissbutton.isDisplayed());
        // check the appearance of zoom in and out button
        WebElement ZoomInButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerDecreaseViewport"));
        // Click on Zoom InButton
        ZoomInButton.click();
        assertTrue(ZoomInButton.isDisplayed());
        Log("status of Zoom in button is: " + ZoomInButton.isDisplayed());
        WebElement ZoomOutButton = driver.findElement(By.id("buttonHistoricalSetupModeViewerIncreaseViewport"));
        // Click on ZoomOutButton
        ZoomOutButton.click();
        assertTrue(ZoomOutButton.isDisplayed());
        Log("status of Zoom out button is: " + ZoomOutButton.isDisplayed());
        assertTrue((driver.findElements(By.id("patient_vitals_display_scrollview")).size() == 0));
        Log("Setup mode window overlap on patientvital dispaly graph view");
        helper.captureScreenShot();
        //Test the dismiss button dismiss the setup mode popup window
        Log("Click on the dismiss button");
        Dismissbutton.click();
        assertTrue(driver.findElement(By.id("patient_vitals_display_scrollview")).isDisplayed());
        Log("Setup mode popup window dismissed");
        //Check the appearance of setup mode blob
        AppearanceOfShowHideSetupModeCheckBox();
        helper.updateTestRail(PASSED);

    }

    @Test
    public void Test_By_adding_two_sensor_type_Nonin_device_to_the_same_session() throws IOException {
        String case_id = "22552";
        helper.printTestStart("Test_By_adding_two_sensor_type_Nonin_device_to_the_same_session",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        removeNonin();
        checkRemoveDeviceEwsPopup(true);

        // Check the status for nonin in patient vital display page
        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Check the Remove status for Nonin
        Log("Check the Nonin status,after device got remove from the session");
        String Expected = helper.getAppString("textDeviceRemoved");
        WebElement spo2_element = driver.findElement(By.id("textSpO2"));
        waitUntil(ExpectedConditions.textToBePresentInElement(spo2_element, Expected), 180);

        String ActualSpO2 = spo2_element.getText();
        Assert.assertEquals(Expected, ActualSpO2);
        Log("Nonin status is showing " + ActualSpO2);
        // Check the Lefthand side of Nonin graph
        assertTrue((driver.findElements(By.id("textPulseOxHumanReadableDeviceId")).size() == 0));

        //Add New Nonin

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the add Nonin button");
        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);

        clickOnStartMonitoring();

        Log("Device ID is:" + nonin_device_id);
        // Check the presence of graph
        assertTrue(driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());
        Log("Presence of Nonin graph is:" + driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());

        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        Log("RHS of Nonin is indicating: " + driver.findElement(By.id("textSpO2")).getText());
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);


    }

    @Test
    public void Check_patient_vital_displaypage_graph_after_adding_all_vitalsign_manually_along_with_BLE_Nonin_for_adult_age_range() throws IOException {
        String case_id = "22553";
        helper.printTestStart("Check_patient_vital_displaypage_graph_after_adding_all_vitalsign_manually_along_with_BLE_Nonin_for_adult_age_range",case_id);

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        // Click on ManualVitals Only
        manualVitalSignEntryForAdultAgeRange();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForAdultAgerange();

        //Add New Nonin

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the add Nonin button");
        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);

        clickOnStartMonitoring();

        // Log("Device ID is:" + nonin_device_id);
        //// Check manually entered SpO2 is overwritten by Nonin graph
        assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_spo2")).size());
        assertTrue(driver.findElement(By.id("fragment_graph_pulse_ox")).isDisplayed());
        Log("Previously displayed SpO2 graph is replace by Nonin graph");
        //Check Show/hide setup mode blob shouldn't appear if no setup mode has run for the session
        ShowHideSetupModeCheckBoxNotAppear();
        helper.updateTestRail(PASSED);

    }

    /**
     * Check the appearance of the Unbonded warning.
     * <p/>
     * This should show a red-bordered overlay (similar to the leads-off one) if the AnD becomes unbonded
     * unexpectedly. It will display the BP pairing failed string (R.string.textBloodPressurePairingFailed).
     *
     * @throws IOException
     */
    @Test
    public void checkUnexpectedUnbondAlert_AnDTM2441() throws IOException {
        String case_id = "22554";
        helper.printTestStart("checkUnexpectedUnbondAlert_AnDTM2441",case_id);

        checkAdminSettingsCorrect(false, false, false);

        setUpSessionWithAdultAgeRange();

        // Add AnD
        helper.spoofQrCode_scanDummyDataAnD_TM2441();

        clickOnConnect();

        String connection_status = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP is " + connection_status);

        clickOnStartMonitoring();

        // DummyData hasn't got TM-2441 implemented yet?
//        checkBloodPressureAnD();

        // spoof intent
        helper.spoofCommandIntent_simulateUnexpectedUnbond();

        // check unbonded alert shown
        String expected_string = helper.getAppString("textBloodPressurePairingFailed");
        WebElement warning_element = driver.findElement(By.id("autoSizeTextViewAndBleNeedsPairing"));

        Assert.assertEquals(expected_string, warning_element.getText());
        helper.updateTestRail(PASSED);

    }

    /**
     * Check the appearance of the Unbonded warning after loading the patient vitals display
     * <p/>
     * The red-bordered overlay should still be there even if we're not on the patient vitals
     * display when it's triggered.
     *
     * @throws IOException
     */
    @Test
    public void checkUnexpectedUnbondAlertAfterUnlockingScreen_AnDTM2441() throws IOException {
        String case_id = "22555";
        helper.printTestStart("checkUnexpectedUnbondAlert_AnDTM2441",case_id);

        checkAdminSettingsCorrect(false, false, false);

        setUpSessionWithAdultAgeRange();

        // Add AnD
        helper.spoofQrCode_scanDummyDataAnD_TM2441();

        clickOnConnect();

        String connection_status = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP is " + connection_status);

        clickOnStartMonitoring();

        clickOnLock();

        // spoof intent
        helper.spoofCommandIntent_simulateUnexpectedUnbond();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // check unbonded alert shown
        String expected_string = helper.getAppString("textBloodPressurePairingFailed");
        WebElement warning_element = driver.findElement(By.id("autoSizeTextViewAndBleNeedsPairing"));

        Assert.assertEquals(expected_string, warning_element.getText());
        helper.updateTestRail(PASSED);

    }


    private String predefinedAnnotation()
    {
        String expected_display = "";

        WebElement Predefined = driver.findElement(By.id("buttonAnnotationPredefined"));
        Log("Click on the predefined Button");
        Predefined.click();
        // Check the Predefined button selected and Predefined Text appear
        String PredefineText = driver.findElement(By.id("textVitalSignValue")).getText();
        String expected = helper.getAppString("predefined");
        Assert.assertEquals(PredefineText, expected);
        Log("The value appear after selecting predefined button is " + PredefineText);
        // Check the appearance of Next button after selecting Predefined and click on the next button
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        clickOnNext();
        // Check the Next button from Annotation entry mode page took us to the right page
        String Texttop = driver.findElement(By.id("textTop")).getText();
        Log("Title name is " + Texttop);
        String TexttopExpected = helper.getAppString("enter_the_annotation_time_by_moving_the_green_dot");
        Assert.assertEquals(TexttopExpected, Texttop);
        Log("We are in correct page");
        clickOnNext();
        //Select the condition
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_conditions").isDisplayed());
        String SelectTheCondition = helper.findTextViewElementWithString("select_the_annotation_conditions").getText();
        Log("Lead us to :" + SelectTheCondition);
        List<WebElement> textCondition = driver.findElements(By.id("checkBoxAnnotationName"));

        int[] conditions_to_use = {0, 1, 5};
        int[] actions_to_use = {0, 3};
        int[] outcomes_to_use = {0, 1, 2};

        for(int i : conditions_to_use)
        {
            WebElement condition = textCondition.get(i);

            Log("click on " + condition.getText());

            expected_display += condition.getText() + ", ";

            condition.click();
        }

        expected_display = expected_display.substring(0, expected_display.length() - 2);
        expected_display += " : ";

        //press next button
        Log("Click on the next button");
        clickOnNext();

        //Select some actions
        String SelectTheAction = helper.findTextViewElementWithString("select_the_annotation_actions").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_actions").isDisplayed());
        Log("Lead us to :" + SelectTheAction);
        List<WebElement> textAction = driver.findElements(By.id("checkBoxAnnotationName"));

        for(int i : actions_to_use)
        {
            WebElement action = textAction.get(i);

            Log("click on " + action.getText());

            expected_display += action.getText() + ", ";

            action.click();
        }

        expected_display = expected_display.substring(0, expected_display.length() - 2);
        expected_display += " : ";

        //press next button
        clickOnNext();
        //Select some Outcomes
        String SelectTheOutcomes = helper.findTextViewElementWithString("select_the_annotation_outcomes").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_outcomes").isDisplayed());
        Log("Lead us to :" + SelectTheOutcomes);
        List<WebElement> textOutCome = driver.findElements(By.id("checkBoxAnnotationName"));

        for(int i : outcomes_to_use)
        {
            WebElement outcome = textOutCome.get(i);

            Log("click on " + outcome.getText());

            expected_display += outcome.getText() + ", ";

            outcome.click();
        }

        expected_display = expected_display.substring(0, expected_display.length() - 2);

        Log("Click on the next button");
        //press next button
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        return expected_display;
    }
  /*  @Test
    public void check_the_appearance_of_Lifetouch_graph_In_patientvitaldisplaypage_after_lifetouch_disconnection()throws IOException
    {

        helper.printTestStart("check_the_appearance_of_Lifetouch_graph_In_patientvitaldisplaypage_after_lifetouch_disconnection");

        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();
        //Check the Lifetouch status from the graph
        //lifetouch Id check
        String lifetouch_device_id = driver.findElement(By.id("textLifetouchHumanReadableDeviceId")).getText();
        Log("Device ID is:" + lifetouch_device_id);

        // presence of Checkbox1
        assertTrue((driver.findElement(By.id("checkBoxLifetouchConfigurable")).isDisplayed()));
        String checkbox_1 = driver.findElement(By.id("checkBoxLifetouchConfigurable")).getText();
        Log(checkbox_1 + " checkbox displayed");

        // Checkbox 2
        assertTrue((driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).isDisplayed()));
        String checkbox_2 = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).getText();
        Log(checkbox_2 + " Options checkbox displayed");
        //RHS of the graph
        WebElement lifetouch_measurement_element = driver.findElement(By.id("textHeartRate"));
        assertTrue(lifetouch_measurement_element.isDisplayed());
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifetouch_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        String heart_rate_value = lifetouch_measurement_element.getText();
        Log("Value showing for heart rate is: " + heart_rate_value);
        //Respiration Rate
        String respitation_value = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Value showing for Respiration value:" + respitation_value);

        //Disconnect the Lifetouch
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__LIFETOUCH,false);
        waitUntil(ExpectedConditions.textToBePresentInElement(lifetouch_measurement_element, helper.getAppString("text_device_disconnected")), 65);
        heart_rate_value = lifetouch_measurement_element.getText();
        WebElement LifetempLeadoff= driver.findElement(By.id("textViewLifetempNotAttached"));
        Log("Value showing for heart rate after disconnection is: " + heart_rate_value);
        respitation_value = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Value showing for RespirationRate after disconnection is" + respitation_value);


    }
    @Test
    public void Check_the_lifetemp_graph_after_device_the_disconnected()throws IOException
    {}*/

    private void manuallyEnteredVitalSign() { // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
    }

    private void clickonSetupmodeBlob() {
        TouchAction click = new TouchAction(driver);
        click.tap(PointOption.point(1690, 135)).perform();


        //click.tap(PointOption.point(width,hightfinal)).perform();
    }

    private void clickonAnnotationBlob() {
        TouchAction click = new TouchAction(driver);
        click.tap(PointOption.point(1690, 135)).perform();
        //click.tap(PointOption.point(width,hightfinal)).perform();
    }

    private void removeBPMachine() {
        WebElement BPRemove;
        BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        String BPR = BPRemove.getText();
        Log("Button display for BP machine is " + BPR + " Button");
        Log("Click on the Remove BP button");
        BPRemove.click();
        Log("BP removed ");
    }

    private void removeNonin() {
        WebElement NoninRemove;
        NoninRemove = driver.findElement(By.id("buttonRemovePulseOximeter"));
        String NoninR = NoninRemove.getText();
        Log("Button display for Nonin is " + NoninR + " Button");
        Log("Click on the Remove Nonin button");
        NoninRemove.click();
        Log("Nonin removed ");
    }
    private void removeWeightScale(){
        WebElement WeightScaleRemove;
        WeightScaleRemove = driver.findElement(By.id("buttonRemoveWeightScale"));
        String WeightScaleR = WeightScaleRemove.getText();
        Log("Button display for WeightScale is " + WeightScaleR + " Button");
        Log("Click on the Remove Weight Scale button");
        WeightScaleRemove.click();
        Log("WeightScale removed ");

    }

    private void removeLTempDevice() {
        WebElement LifeTempRemove;
        LifeTempRemove = driver.findElement(By.id("buttonRemoveThermometer"));
        String LTempR = LifeTempRemove.getText();
        Log("Button display for lifetemp is " + LTempR + " Button");
        Log("Click on the Remove Lifetemp button");
        LifeTempRemove.click();
        Log("Lifetemp removed ");

    }

    private void removeLTDevice() {
        WebElement LifeTouchRemove;
        LifeTouchRemove = driver.findElement(By.id("buttonRemoveLifetouch"));
        String LTR = LifeTouchRemove.getText();
        Log("Button display for lifetouch is :" + LTR + " Button");
        Log("Click on the Remove Lifetouch button");
        LifeTouchRemove.click();
        Log("Lifetouch removed ");

    }

    private void Setupmode_to_motionMode() throws IOException {

        // Click on setup mode checkbox
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of setupmode checkbox is " + isChecked);
        Log("Click on Setupmode checkbox");
        setupmode.click();
        // Check the setupmode checkbox status in LHS side of the graph
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String SetupmodeCheckbox = setupmodeLHS.getText();
        String isChecked2 = setupmodeLHS.getAttribute("checked");
        Log("Check the setup mode check box status in Left hand side of the Lifetouch graph is :" + isChecked2);
        Log("The screenshot has captured for Setup mode graph");
        helper.captureScreenShot();
        Log("Click on the Motion mode check box while setup mode is running");
        // Click on option check box and click on the Motion mode checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        // Click on Motion mode checkbox
        driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode")).click();
        Log("The screenshot has captured for Motion mode graph");
        helper.captureScreenShot();
        // Check the setup mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String motionmodecheckbox = motionmodeLHS.getText();
        //Log(SetupmodeCheckbox);
        String isChecked3 = motionmodeLHS.getAttribute("checked");
        Log("Left hand side of the Lifetouch graph is contain:" + motionmodecheckbox + " checkbox and the status is " + isChecked3);
        Assert.assertNotEquals(SetupmodeCheckbox, motionmodecheckbox);
    }

    private void Motionmode_to_setupMode() throws IOException {
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        // Check the Motion Mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String motionmodecheckbox = motionmodeLHS.getText();
        //Log(motionmodecheckbox);
        String isChecked2 = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Lefthandsisde of the Lifetouch graph is :" + isChecked2);
        Log("The screenshot has captured for motion mode graph");
        helper.captureScreenShot();
        Log("Click on the setup mode check box while motion mode is running");
        // Click on option check box and click on the setup mode checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        // Click on setup mode checkbox
        driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode")).click();
        Log("The screenshot has captured for setup mode graph");
        helper.captureScreenShot();
        // Check the setup mode checkbox status in LHS side of the graph
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String SetupmodeCheckbox = setupmodeLHS.getText();
        //Log(SetupmodeCheckbox);
        String isChecked3 = setupmodeLHS.getAttribute("checked");
        Log("Left hand side of the Lifetouch graph is contain:" + SetupmodeCheckbox + " checkbox and the status is " + isChecked3);
        Assert.assertNotEquals(motionmodecheckbox, SetupmodeCheckbox);
    }

    private void motionmodeRunningForCeratainTime() {
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        //get the currenttime
        Log(driver.getDeviceTime());

        // Click on the option checkbox and check the Motion mode checkbox status
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        OptionCheckbox.click();
        //check the Motion Mode status from option overly
        MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        isChecked = MotionMode.getAttribute("checked");
        Log("Check the Status of Motion Mode checkbox in option overlay is  " + isChecked);
        //uncheck the options check box
        OptionCheckbox.click();
        Log("Check the Motiomode check box status in Lefthandsisde of the Lifetouch graph");
        // Check the Motion Mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Lefthandside of the Lifetouch graph is :" + isChecked);

        //Motion mode run for 3 mins
        long startTime = System.nanoTime();
        while ((System.nanoTime() - startTime) < 3 * 60 * NANOSEC_PER_SEC) {
            WebElement motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
            assertTrue(motionModeGraphView.isDisplayed());
        }

        //uncheck the option checkbox
        motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        motionmodeLHS.click();
        Log(driver.getDeviceTime());
    }

    private void motionmode() {
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        // Click on the option checkbox and check the Motion mode checkbox status
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        OptionCheckbox.click();
        //check the Motion Mode status from option overly
        MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        isChecked = MotionMode.getAttribute("checked");
        Log("Check the Status of Motion Mode checkbox in option overlay is  " + isChecked);
        // Uncheck the option check box
        OptionCheckbox.click();

        Log("Check the Motiomode check box status in Lefthandsisde of the Lifetouch graph");
        // Check the Motion Mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Lefthandside of the Lifetouch graph is :" + isChecked);
        // Check the appearance and presence of graph
        WebElement motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
        assertTrue(motionModeGraphView.isDisplayed());
        //uncheck the option checkbox
        motionmodeLHS.click();
        Log(driver.getDeviceTime());
    }

    private void motionmodewithExperiment() throws IOException {
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        // Click on the option checkbox and check the Motion mode checkbox status
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        OptionCheckbox.click();
        //check the Motion Mode status from option overly
        MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        isChecked = MotionMode.getAttribute("checked");
        Log("Check the Status of Motion Mode checkbox in option overlay is  " + isChecked);
        // Uncheck the option check box
        OptionCheckbox.click();

        Log("Check the Motiomode check box status in Lefthandsisde of the Lifetouch graph");
        // Check the Motion Mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Lefthandside of the Lifetouch graph is :" + isChecked);
        // Check the appearance and presence of graph
        WebElement motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
        assertTrue(motionModeGraphView.isDisplayed());
        Log("While Motion mode is running Click on unlock button");

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textLifetouchRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textLifetouchRedBorderStatus")).isDisplayed());
        Log("On the QR unlock screen page ,Can see " + Qrscrenview.getText() + " For lifetouch");
        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();

        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");

        //check the motion mode graph is still running
        motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
        assertTrue(motionModeGraphView.isDisplayed());
        Log("MotionMode graph is still running");

        //Motion mode run for 1 mins
        long startTime = System.nanoTime();
        while ((System.nanoTime() - startTime) < 1 * 60 * NANOSEC_PER_SEC) {
            motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
            assertTrue(motionModeGraphView.isDisplayed());
        }

        // uncheck the Motionmode check box
        Log("Uncheck the Motion mode checkbox");
        motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        motionmodeLHS.click();
        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
    }

    private void setupmode() {
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of Setupmode checkbox is " + isChecked);
        Log("Click on setupmode checkbox");
        setupmode.click();
        // Click on the option checkbox and check the setup mode checkbox status
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        isChecked = setupmode.getAttribute("checked");
        Log("Check the Status of Setupmode checkbox in option overlay is  " + isChecked);
        Log("Check the Setupmode check box status in Lefthandsisde of the Lifetouch graph");
        // Check the setup mode checkbox status in LHS side of the graph
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = setupmodeLHS.getAttribute("checked");
        Log("Check the Setupmode check box status in Lefthandsisde of the Lifetouch graph is :" + isChecked);
        //uncheck the option checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());

        // uncheck the setupmode check box
        setupmodeLHS.click();
        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
    }

    private void LifeTouchsetupmodewithExperiment() throws IOException {
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of Setupmode checkbox is " + isChecked);
        Log("Click on setupmode checkbox");
        setupmode.click();

        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("While setup mode is running Click on unlock button");

        clickOnLock();

        //Scan the user Qr code
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Reopen the patientvital dispaly page");
        //check the motion mode graph is still running
        SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Setupmode graph is still running");
        // uncheck the setupmode check box
        Log("Uncheck the setup mode checkbox");
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        setupmodeLHS.click();

        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
    }

    private void LifetouchSetupmoderuningForCertainPeriodOfTime() throws IOException {
        //adjust the setup mode length in admin page
        driver.findElementById("buttonLock").click();

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));
        Log("Setup mode length set as : " + SetupModeLength.getText());

        //Back to patient vital display page
        driver.findElementById("buttonLock").click();
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        // Click on the option checkbox

        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of Setupmode checkbox is " + isChecked);

        setupmode.click();

        String desired_date_format = "HH:mm:ss";
        DateTime start_date = new DateTime();
        String setup_mode_start_time = start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Setup mode checkbox checked at " + setup_mode_start_time);
        // Click on the option checkbox and check the setup mode checkbox status
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        isChecked = setupmode.getAttribute("checked");
        Log("Check the Status of Setupmode checkbox in option overlay is  " + isChecked);

        // Check the setup mode checkbox status in LHS side of the graph
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = setupmodeLHS.getAttribute("checked");
        Log("Check the Setupmode check box status in Lefthandside of the Lifetouch graph is :" + isChecked);

        //uncheck the option checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());

        // check that the setup mode is running for specified time and check box will uncheck automatically
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        DateTime end_date = new DateTime();
        String setup_mode_end_time = end_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("setup mode stop at:" + setup_mode_end_time);

        // need to find the time setup mode run for
        long time_difference = end_date.getMillis() - start_date.getMillis();
        int difference_in_seconds = (int) (time_difference / DateUtils.SECOND_IN_MILLIS);

        // we expect the time difference to by 2 minutes +/- 10 seconds...
        int expected_length_seconds = 120;
        int upper_bound = expected_length_seconds + 10;
        int lower_bound = expected_length_seconds - 10;

        Log("Time interval setup mode run for : " + difference_in_seconds + " seconds");

        // Compare the setupmode length between actual time run and set time.
        assertTrue(difference_in_seconds < upper_bound);
        assertTrue(difference_in_seconds > lower_bound);

        // Check no setup mode graph  after setup mode stop
        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
    }

    private void SetupmodeforNoninrunningForCertainPeriodOfTime() throws IOException {
        //adjust the setup mode length in admin page
        driver.findElementById("buttonLock").click();

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Scan the admin page and set the setup mode time to 2mins
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("two_minutes"));
        WebElement SetupModeLength = driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());
        //Back to patient vital display page
        driver.findElementById("buttonLock").click();
        helper.spoofQrCode_userUnlock();
        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());

        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();

        String desired_date_format = "HH:mm:ss";
        DateTime start_date = new DateTime();
        String start_time = start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Setup mode checkbox checked at " + start_time);

        // Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());

        // check that the setup mode is running for specified time and check box will uncheck automatically
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("plotRealTimeECG")), 135);

        DateTime end_date = new DateTime();
        String setup_mode_end_time = end_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("setup mode stop at:" + setup_mode_end_time);

        // need to find the time setup mode run for
        long time_difference = end_date.getMillis() - start_date.getMillis();
        int difference_in_seconds = (int) (time_difference / DateUtils.SECOND_IN_MILLIS);

        // we expect the time difference to by 2 minutes +/- 10 seconds...
        int expected_length_seconds = 120;
        int upper_bound = expected_length_seconds + 10;
        int lower_bound = expected_length_seconds - 10;

        Log("Time interval setup mode run for : " + difference_in_seconds + " seconds");

        // Compare the setupmode length between actual time run and set time.
        assertTrue(difference_in_seconds < upper_bound);
        assertTrue(difference_in_seconds > lower_bound);

        // Check no setup mode graph  after setup mode stop
        assertTrue((driver.findElements(By.id("plotRealTimeECG")).size() == 0));
    }

    private void poincare() throws IOException {
        Log("Poincare window is open");
        assertTrue(driver.findElement(By.id("linear_layout_poincare_chart")).isDisplayed());
        Log("Poincare graph displayed");
        Log("Check the appearance  and statusof of time checkbox graph ");
        //1 min graph
        WebElement graph1mins = driver.findElement(By.id("checkBox1MinuteGraph"));
        assertTrue(graph1mins.isDisplayed());
        String isChecked1 = graph1mins.getAttribute("checked");
        Log(graph1mins.getText() + "status is " + isChecked1);
        //2 mins graph
        WebElement graph2mins = driver.findElement(By.id("checkBox2MinutesGraph"));
        assertTrue(graph2mins.isDisplayed());
        String isChecked2 = graph2mins.getAttribute("checked");
        Log(graph2mins.getText() + "status is " + isChecked2);
        //3 mins graph
        WebElement graph3mins = driver.findElement(By.id("checkBox3MinutesGraph"));
        assertTrue(graph3mins.isDisplayed());
        String isChecked3 = graph3mins.getAttribute("checked");
        Log(graph3mins.getText() + "status is " + isChecked3);
        //5 mins graph
        WebElement graph4mins = driver.findElement(By.id("checkBox5MinutesGraph"));
        assertTrue(graph4mins.isDisplayed());
        String isChecked4 = graph4mins.getAttribute("checked");
        Log(graph4mins.getText() + "status is " + isChecked4);
        //10 mins graph
        WebElement graph5mins = driver.findElement(By.id("checkBox10MinutesGraph"));
        assertTrue(graph5mins.isDisplayed());
        String isChecked5 = graph5mins.getAttribute("checked");
        Log(graph5mins.getText() + "status is " + isChecked5);
        //15 mins graph
        WebElement graph6mins = driver.findElement(By.id("checkBox15MinutesGraph"));
        assertTrue(graph6mins.isDisplayed());
        String isChecked6 = graph6mins.getAttribute("checked");
        Log(graph6mins.getText() + "status is " + isChecked6);
        //spinner day and hour selection
        WebElement Dayselection = driver.findElement(By.id("spinnerDaySelection"));
        assertTrue(Dayselection.isDisplayed());
        Log(Dayselection.getText() + " Spinner day selection exit");
        //spinner day and hour selection
        WebElement Hoursselection = driver.findElement(By.id("spinnerHoursSelection"));
        assertTrue(Hoursselection.isDisplayed());
        Log(Hoursselection.getText() + " Spinner Hours selection exit");
        Log("The screenshot has captured for Poincaregraph");
        //to  check motionmode graph is running on the screen
        helper.captureScreenShot();

        //Dismiss button
        Log("Test the appearance of Dismiss button");
        WebElement dismiss = driver.findElement(By.id("buttonDismissPoincare"));
        assertTrue(dismiss.isDisplayed());
        Log(dismiss.getText() + "Button present");
        Log("Dismiss the popup window");
        driver.findElement(By.id("buttonDismissPoincare")).click();
    }

    private void checkLifetouch() {
        // Check the status for lifetouch for lhs
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoLifetouch"));
        WebElement right_hand_side = driver.findElement(By.id("layoutHeartAndRespiration"));

        Log("Left hand side of the graph is showing");

        //Lifetouch Id
        String lifetouch_device_id = left_hand_side.findElement(By.id("textLifetouchHumanReadableDeviceId")).getText();
        Log("Device ID is:" + lifetouch_device_id);

//        waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(left_hand_side, By.id("textLifetouchBatteryPercentage")), 130;
//        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textLifetouchBatteryPercentage")), 130);

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("imageBatteryLifetouch")), 130);

        // ToDo: Figure out how to test the flashing battery image
//        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("imageBatteryLifetouch")), 100);
//        assertTrue(driver.findElement(By.id("imageBatteryLifetouch")).isDisplayed());
//        Log("Battery sign displayed");

        String battery_percentage = left_hand_side.findElement(By.id("textLifetouchBatteryPercentage")).getText();
        Log("Battery value showing is " + battery_percentage);

        // ToDo: Figure out how to test the flashing heart beat image
        //appearance of heartbeat
//        assertTrue((driver.findElement(By.id("heartbeat")).isDisplayed()));
//        Log("Hearbeat sign displayed");

        //appearance of patient orientations <- not simulated by dummy data mode
        /*
        assertTrue((driver.findElement(By.id("imagePatientOrientation")).isDisplayed()));
        Log("PatientOrientation displayed");
        */

        // Checkbox1
        assertTrue((left_hand_side.findElement(By.id("checkBoxLifetouchConfigurable")).isDisplayed()));
        String checkbox_1 = left_hand_side.findElement(By.id("checkBoxLifetouchConfigurable")).getText();
        Log(checkbox_1 + " checkbox displayed");

        // Checkbox 2
        assertTrue((left_hand_side.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).isDisplayed()));
        String checkbox_2 = left_hand_side.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).getText();
        Log(checkbox_2 + " Options checkbox displayed");

        //Right hand side
        assertTrue(right_hand_side.findElement(By.id("textHeartRateBeatsPerMinLabel")).isDisplayed());
        String heart_rate_label = right_hand_side.findElement(By.id("textHeartRateBeatsPerMinLabel")).getText();
        Log("Right hand side of the graph is showing" + heart_rate_label);

        WebElement lifetouch_measurement_element = right_hand_side.findElement(By.id("textHeartRate"));
        assertTrue(lifetouch_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifetouch_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        // Check the presence of patient orientation image in LHS of the graph
        WebElement PatientOrientationImage = driver.findElement(By.id("imagePatientOrientation"));
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("imagePatientOrientation")), 130);
        assertTrue(PatientOrientationImage.isDisplayed());
        Log("Appearance of PatientOrientation status is:" + PatientOrientationImage.isDisplayed());
        // Heart Rate
        assertTrue(right_hand_side.findElement(By.id("textHeartRate")).isDisplayed());
        String heart_rate_value = lifetouch_measurement_element.getText();
        Log("Value showing for heart rate is: " + heart_rate_value);
        assertTrue(right_hand_side.findElement(By.id("textHeartRateTimestamp")).isDisplayed());
        String heart_rate_timestamp = right_hand_side.findElement(By.id("textHeartRateTimestamp")).getText();
        Log("Timestamp value showing for heart rate is :" + heart_rate_timestamp);

        //Respiration Rate
        Log("Respiration rate");
        String respiration_label = right_hand_side.findElement(By.id("textRespirationRateBreathMinLabel")).getText();
        Log("Right hand side of Lifetouch graph is showing:" + respiration_label);
        String respitation_value = right_hand_side.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value:" + respitation_value);
        String respiration_timestamp = right_hand_side.findElement(By.id("textHeartRateTimestamp")).getText();
        Log("Timestamp value showing for respiration rate is:" + respiration_timestamp);
    }
    private void checkWeightScale()
    {
        // Check the status for WeightScale for LHS
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoWeightScale"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutWeightScaleMeasurementGroup"));

        String WeightScale_device_id = left_hand_side.findElement(By.id("textWeightScaleHumanReadableDeviceId")).getText();
        Log("Device ID is:" + WeightScale_device_id);

        // First wait for measurement to be *not* Waiting for Data
        WebElement WeightScale_measurement_element = right_hand_side.findElement(By.id("textWeightScaleMeasurement"));
        assertTrue(WeightScale_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        // RHS
        String WeightScale_label = right_hand_side.findElement(By.id("textWeightScaleLabel")).getText();
        Log("RHS of WeightScale graph is showing :" + WeightScale_label);
        String Weight_value = WeightScale_measurement_element.getText();
        Log("Value showing for Weight is: " + Weight_value);
        String WeightScale_timestamp = right_hand_side.findElement(By.id("textWeightScaleMeasurementTimestamp")).getText();
        Log("Value showing for timestamp: " + WeightScale_timestamp);

    }

    private void checkLifetouchLeadoff() {
        // Check the status for lifetouch for lhs
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoLifetouch"));
        WebElement right_hand_side = driver.findElement(By.id("layoutHeartAndRespiration"));

        Log("Left hand side of the graph is showing");

        //Lifetouch Id
        String lifetouch_device_id = left_hand_side.findElement(By.id("textLifetouchHumanReadableDeviceId")).getText();
        Log("Device ID is:" + lifetouch_device_id);
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("imageBatteryLifetouch")), 130);
        String battery_percentage = left_hand_side.findElement(By.id("textLifetouchBatteryPercentage")).getText();
        Log("Battery value showing is " + battery_percentage);
        // Checkbox 2
        assertTrue((left_hand_side.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).isDisplayed()));
        String checkbox_2 = left_hand_side.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).getText();
        Log(checkbox_2 + " Options checkbox displayed");

        //Right hand side
        assertTrue(right_hand_side.findElement(By.id("textHeartRateBeatsPerMinLabel")).isDisplayed());
        String heart_rate_label = right_hand_side.findElement(By.id("textHeartRateBeatsPerMinLabel")).getText();
        Log("Right hand side of the graph is showing" + heart_rate_label);

        WebElement lifetouch_measurement_element = right_hand_side.findElement(By.id("textHeartRate"));
        assertTrue(lifetouch_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.textToBePresentInElement(lifetouch_measurement_element, helper.getAppString("textWaitingForData")), 130);

        // Heart Rate
        assertTrue(right_hand_side.findElement(By.id("textHeartRate")).isDisplayed());
        String heart_rate_value = lifetouch_measurement_element.getText();
        Log("Value showing for heart rate is: " + heart_rate_value);
        //Respiration Rate
        Log("Respiration rate");
        String respiration_label = right_hand_side.findElement(By.id("textRespirationRateBreathMinLabel")).getText();
        Log("Right hand side of Lifetouch graph is showing:" + respiration_label);
        String respitation_value = right_hand_side.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value:" + respitation_value);
    }

    private void checkLifetemp() {
        // Check the status for Lifetemp for lhs
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoLifetemp"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutTemperatureMeasurementGroup"));

        String lifetemp_device_id = left_hand_side.findElement(By.id("textLifetempHumanReadableDeviceId")).getText();
        Log("Device ID is:" + lifetemp_device_id);

        // First wait for measurement to be *not* Waiting for Data
        WebElement lifetemp_measurement_element = right_hand_side.findElement(By.id("textTemperatureReading"));
        assertTrue(lifetemp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifetemp_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        // ToDo: Figure out how to test the flashing battery image
//        // Now try to find battery image...
//        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("imageBatteryLifetemp")), 60);
//        assertTrue(driver.findElement(By.id("imageBatteryLifetemp")).isDisplayed());
//        Log("Battery sign displayed");

        assertTrue(driver.findElement(By.id("imageBatteryLifetemp")).isDisplayed());
        String battery_percentage = left_hand_side.findElement(By.id("textLifetempBatteryPercentage")).getText();
        Log("Battery value showing is " + battery_percentage);

        // RHS
        String lifetemp_label = right_hand_side.findElement(By.id("textTemperatureLabel")).getText();
        Log("RHS of Lifetemp graph is showing :" + lifetemp_label);
        String temp_value = lifetemp_measurement_element.getText();
        Log("Value showing for temp is: " + temp_value);
        String temp_timestamp = right_hand_side.findElement(By.id("textTemperatureTimestamp")).getText();
        Log("Value showing for timestamp: " + temp_timestamp);
    }

    private void checkLifetempLeadOff() {
        // Check the status for Lifetemp for lhs
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoLifetemp"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutTemperatureMeasurementGroup"));

        String lifetemp_device_id = left_hand_side.findElement(By.id("textLifetempHumanReadableDeviceId")).getText();
        Log("Device ID is:" + lifetemp_device_id);

        // First wait for measurement to be *not* Waiting for Data
        WebElement lifetemp_measurement_element = right_hand_side.findElement(By.id("textTemperatureReading"));
        assertTrue(lifetemp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageBatteryLifetemp")), 130);
        String battery_percentage = left_hand_side.findElement(By.id("textLifetempBatteryPercentage")).getText();
        Log("Battery value showing is " + battery_percentage);

        // RHS
        String lifetemp_label = right_hand_side.findElement(By.id("textTemperatureLabel")).getText();
        Log("RHS of Lifetemp graph is showing :" + lifetemp_label);
        String temp_value = lifetemp_measurement_element.getText();
        Log("Value showing for temp is: " + temp_value);
    }

    private void checkNonin() {
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        String nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);

        //Battery appearance - wait for battery to appear
        waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(left_hand_side, By.id("imageBatteryPulseOx")), 130);
        Log("Battery sign displayed");

        WebElement nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(nonin_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        String spo2_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();
        Log("RHS of Nonin graph is showing :" + spo2_label);
        String spo2_value = right_hand_side.findElement(By.id("textSpO2")).getText();
        Log("Value showing for SpO2 is: " + spo2_value);
        String spo2_timestamp = right_hand_side.findElement(By.id("textPulseOximeterTimestamp")).getText();
        Log("Value showing for timestamp: " + spo2_timestamp);
        WebElement nonin_Graph = driver.findElement(By.id("fragment_graph_pulse_ox"));
        assertTrue(nonin_Graph.isDisplayed());
    }

    private void checkNoninLeadoff() {
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        String nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);

        //Battery appearance - wait for battery to appear
        waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(left_hand_side, By.id("imageBatteryPulseOx")), 130);
        Log("Battery sign displayed");

        WebElement nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.textToBePresentInElement(nonin_measurement_element, helper.getAppString("textWaitingForData")), 130);

        String spo2_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();
        Log("RHS of Nonin graph is showing :" + spo2_label);
        String spo2_value = right_hand_side.findElement(By.id("textSpO2")).getText();
        Log("Value showing for SpO2 is: " + spo2_value);
    }

    private void checkBloodPressureAnD() {
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));

        // Check the status of AnD for rhs
        String bp_device_id = left_hand_side.findElement(By.id("textBloodPressureHumanReadableDeviceId")).getText();
        Log("Device ID is:" + bp_device_id);
        String bp_label = right_hand_side.findElement(By.id("textBloodPressureLabel")).getText();
        Log("RHS of Blood Pressure graph is showing :" + bp_label);

        WebElement bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        assertTrue(bp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textBloodPressureMeasurementDiastolic")), 130);

        assertTrue(right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic")).isDisplayed());
        String bp_value_systolic = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic")).getText();
        assertTrue(right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic")).isDisplayed());
        String bp_value_diastolic = right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic")).getText();
        Log("Value showing for BP is: " + bp_value_systolic + "/" + bp_value_diastolic);
        String bp_timestamp = right_hand_side.findElement(By.id("textBloodPressureMeasurementTimestamp")).getText();
        Log("Value showing for timestamp: " + bp_timestamp);
        WebElement AnDGraph = driver.findElement(By.id("fragment_graph_blood_pressure"));
        assertTrue(AnDGraph.isDisplayed());
    }

    private void checkEarlyWarningScore()
    {
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutEarlyWarningScoreType"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutEarlyWarningScoreMeasurementGroup"));

        String ews_description = left_hand_side.findElement(By.id("textEarlyWarningScoreDescription")).getText();
        Log("EWS description is:" + ews_description);
        String ews_type = left_hand_side.findElement(By.id("textEarlyWarningScoreType")).getText();
        Log("EWS type is :" + ews_type);

        WebElement ews_measurement_element = right_hand_side.findElement(By.id("textEarlyWarningScoreMeasurement"));
        assertTrue(ews_measurement_element.isDisplayed());

        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(ews_measurement_element, helper.getAppString("textWaitingForData"))), 200);
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textEarlyWarningScoreMaxPossible")), 200);

        String ews_measurement = ews_measurement_element.getText();
        assertTrue(right_hand_side.findElement(By.id("textEarlyWarningScoreMeasurement")).isDisplayed());
        String max_possible = right_hand_side.findElement(By.id("textEarlyWarningScoreMaxPossible")).getText();
        Log("Value showing for EWS is: " + ews_measurement + "/" + max_possible);
        String ews_timestamp = right_hand_side.findElement(By.id("textEarlyWarningScoreTimestamp")).getText();
        Log("Value showing for timestamp: " + ews_timestamp);
        WebElement EWSGraph = driver.findElement(By.id("fragment_graph_early_warning_scores"));
        assertTrue(EWSGraph.isDisplayed());
    }

    private void checkNoEarlyWarningScoreCalculated() {
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutEarlyWarningScoreType"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutEarlyWarningScoreMeasurementGroup"));

        String ews_description = left_hand_side.findElement(By.id("textEarlyWarningScoreDescription")).getText();
        Log("EWS description is:" + ews_description);
        String ews_type = left_hand_side.findElement(By.id("textEarlyWarningScoreType")).getText();
        Log("EWS type is :" + ews_type);

        // Check waiting for data
        WebElement ews_measurement_element = right_hand_side.findElement(By.id("textEarlyWarningScoreMeasurement"));
        assertTrue(ews_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.textToBePresentInElement(ews_measurement_element, helper.getAppString("textWaitingForData")), 10);
    }

    private void manualVitalSignEntryForAdultAgeRange() {
        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, actual_title);
        Log("Landed on the correct page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();

        Log("Lead us to the manual vital entry page with set time and measurement length ");

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
        // Click on next
        clickOnNext();
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Temp is 38");
        // Click on next
        clickOnNext();
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Click on next
        clickOnNext();
        // Click on the Blood Pressure button
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
        // Click on the Weight button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight_scale_measurements").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for Weight is 72");
        clickOnNext();
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //option avelable for Consciousness lavel
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize = Option.size();
        //System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for (int i = 0; i < iSize; i++) {
            Option.get(i).getText();

        }
        WebElement Alert = Option.get(0);

        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  " + Alert.getText());
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize = Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: " + Option.size());
        for (int i = 0; i < iSize; i++) {
            Option.get(i).getText();
        }
        Log("Value Enter  for Supplemental Oxygen is  " + Option.get(0).getText());
        Option.get(0).click();
        // Click on Next button
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }

    private void manualVitalSignEntryForNonAdultAgeRange() {
        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, actual_title);
        Log("Landed on the correct page");
        // Select the measurement interval as 5 mins
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();

        Log("Lead us to the manual vital entry page with set time and measurement length ");

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
        // Click on next
        clickOnNext();
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Temp is 38");
        // Click on next
        clickOnNext();
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Click on next
        clickOnNext();
        // Click on the Blood Pressure button
        Log("Click on Blood Pressure button");
        helper.findTextViewElementWithString("blood_pressure").click();
        Log("Enter the value for Blood Pressure");
        //Enter the value for BloodPressure 98/68
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonDecimal")).click();
        driver.findElement(By.id("buttonNumberSix")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for BP is 98/68");
        clickOnNext();
        // Click on the Weight button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight_scale_measurements").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for Weight is 72");
        clickOnNext();

        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        //option avelable for Consciousness lavel
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        int iSize = Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: " + Option.size());
        for (int i = 0; i < iSize; i++) {
            Option.get(i).getText();

        }
        WebElement Alert = Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  " + Alert.getText());
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize = Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: " + Option.size());
        for (int i = 0; i < iSize; i++) {
            Option.get(i).getText();

        }
        Log("Value Enter  for Supplemental Oxygen is  " + Option.get(1).getText());
        Option.get(1).click();
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
        helper.findTextViewElementWithString("respiration_distress").click();
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize = Option.size();
        System.out.println("Number of option available for RespirationDistressLevel is: " + Option.size());
        for (int i = 0; i < iSize; i++) {
            Option.get(i).getText();

        }
        Log("Value Enter  for RespirationDistressLevel is  " + Option.get(0).getText());
        Option.get(0).click();
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        helper.findTextViewElementWithString("capillary_refill_time").click();
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        iSize = Option.size();
        System.out.println("Number of option available for CapillaryRefillTime is: " + Option.size());
        for (int i = 0; i < iSize; i++) {
            Log(Option.get(i).getText());

        }
        Log("Value Enter  for CapillaryRefillTime is  " + Option.get(0).getText());
        Option.get(0).click();
        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");

        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        optionArea = driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize = Option.size();
        System.out.println("Number of option available for FamilyNurseConcern is: " + Option.size());
        for (int i = 0; i < iSize; i++) {
            Option.get(i).getText();

        }
        Log("Value Enter  for FamilyNurseConcern is  " + Option.get(0).getText());
        Option.get(0).click();
        // Click on Next button
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }

    private void patientVitalsDisplayPageForAdultAgerange() {
        Log("PatientVital display page after adding the manualvital sign only");
        Log("--------------------------------------------");
        Log("Checking the LHS and RHS of the  graph for Heart Rate");
        HRgraphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for Respiration Rate");
        RRgraphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for Temperature");
        Tempgraphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for SpO2");
        SpO2graphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for BloodPressure");
        Bloodpressuregraphdetails();
        Log("--------------------------------------------");
        helper.swipeActionScrollDown();
        Log("Scrol the page to buttom");
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for EWS");
        EWStype();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for WeightScale");
        WeightScalegraphdetails();
        Log("--------------------------------------------");

        Log("Checking the graph LHS and  RHS of the for SupplementalOxygenLevel");
        supplementOL();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for ConsciousnessLevel");
        ConsciousnessLevel();
    }

    private void patientVitalsDisplayPageForNonAdultAgerange() {
        Log("PatientVital display page after adding the manualvital sign only");
        Log("--------------------------------------------");
        Log("Checking the LHS and RHS of the  graph for Heart Rate");
        HRgraphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for Respiration Rate");
        RRgraphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for Temperature");
        Tempgraphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for SpO2");
        SpO2graphdetails();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for BloodPressure");
        Bloodpressuregraphdetails();
        Log("--------------------------------------------");

        Log("Checking the LHS and  RHS of the graph for EWS");
        EWStype();
        Log("--------------------------------------------");
        helper.swipeActionScrollDown();
        Log("Checking the LHS and  RHS of the graph for WeightScale");
        WeightScalegraphdetails();
        Log("--------------------------------------------");

        Log("Checking the graph LHS and  RHS of the for SupplementalOxygenLevel");
        supplementOL();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for ConsciousnessLevel");
        ConsciousnessLevel();
        Log("--------------------------------------------");
        //Scroll down again
        helper.swipeActionScrollDown();
        Log("Checking the LHS and  RHS of the graph for CapillaryRefillTime");
        CapillaryRefillTime();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for RespirationDistress");
        RespirationDistress();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for FamilyOrNurseConcern");
        FamilyOrNurseConcern();
    }

    private void HRgraphdetails() {
        String ManualyEnterHR = helper.findTextViewElementWithString("multiline_manually_entered_heart_rate").getText();
        Log("Left hand side of the graph for HR is showing: " + ManualyEnterHR);
        //value showing for HR
        String HRvalue = driver.findElement(By.id("textManuallyEnteredHeartRate")).getText();
        Log("Value for HR is " + HRvalue);
        String HRtimestamp = driver.findElement(By.id("textManuallyEnteredHeartRateTimestamp")).getText();
        Log("Time stamp details for HR is : " + HRtimestamp);
        String HRmeasurementvalidity = driver.findElement(By.id("progressBarManuallyEnteredHeartRateMeasurementValidity")).getText();
        Log(HRmeasurementvalidity);
        WebElement HRgraph = driver.findElement(By.id("fragment_graph_manually_entered_heart_rate"));
        assertTrue(HRgraph.isDisplayed());
    }

    private void RRgraphdetails() {
        //Value showing for RR
        String ManualyEnterRR = helper.findTextViewElementWithString("multiline_manually_entered_respiration_rate").getText();
        Log("Left hand side of the graph for RR is showing: " + ManualyEnterRR);
        String RRvalue = driver.findElement(By.id("textManuallyEnteredRespirationRate")).getText();
        Log("Value for RR is " + RRvalue);
        String RRtimestamp = driver.findElement(By.id("textManuallyEnteredRespirationRateTimestamp")).getText();
        Log("Time stamp details for RR is : " + RRtimestamp);
        String RRmeasurementvalidity = driver.findElement(By.id("progressBarManuallyEnteredRespirationRateMeasurementValidity")).getText();
        Log(RRmeasurementvalidity);
        WebElement RRgraph = driver.findElement(By.id("fragment_graph_manually_entered_respiration_rate"));
        assertTrue(RRgraph.isDisplayed());
    }

    private void Tempgraphdetails() {
        //Value showing for Temp
        String ManualyEnterTemp = helper.findTextViewElementWithString("multiline_manually_entered_temperatures").getText();
        Log("Left hand side of the graph for Temperatures is showing: " + ManualyEnterTemp);
        String Tempvalue = driver.findElement(By.id("textManuallyEnteredTemperature")).getText();
        Log("Value for Temp is " + Tempvalue);
        String Temptimestamp = driver.findElement(By.id("textManuallyEnteredTemperatureTimestamp")).getText();
        Log("Time stamp details for Temp is : " + Temptimestamp);
        WebElement Tempgraph = driver.findElement(By.id("fragment_graph_manually_entered_temperature"));
        assertTrue(Tempgraph.isDisplayed());

    }

    private void SpO2graphdetails() {
        String ManualyEnterSpO2 = helper.findTextViewElementWithString("multiline_manually_entered_spo2_measurements").getText();
        Log("Left hand side of the graph for SpO2 is showing: " + ManualyEnterSpO2);
        String SpO2value = driver.findElement(By.id("textManuallyEnteredSpO2")).getText();
        Log("Value for SpO2 is " + SpO2value);
        String SpO2timestamp = driver.findElement(By.id("textManuallyEnteredSpO2Timestamp")).getText();
        Log("Time stamp details for SpO2 is : " + SpO2timestamp);
        WebElement SpO2graph = driver.findElement(By.id("fragment_graph_manually_entered_spo2"));
        assertTrue(SpO2graph.isDisplayed());
    }
    private void Bloodpressuregraphdetails() {
        //Value showing for Blood Pressure
        String ManualyEnterBP = helper.findTextViewElementWithString("multiline_manually_entered_blood_pressures").getText();
        Log("Left hand side of the graph for BloodPressure is showing: " + ManualyEnterBP);
        String BPSystolicvalue = driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementSystolic")).getText();
        String BPDiastolic = driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementDiastolic")).getText();
        Log("Value for BP is " + BPSystolicvalue + "/" + BPDiastolic);
        String BPtimestamp = driver.findElement(By.id("textManuallyEnteredBloodPressureTimestamp")).getText();
        Log("Time stamp details for BP is : " + BPtimestamp);
        WebElement BloodPressuregraph = driver.findElement(By.id("fragment_graph_manually_entered_blood_pressure"));
        assertTrue(BloodPressuregraph.isDisplayed());
    }

    private void EWStype() {
        String EWSdescription = driver.findElement(By.id("textEarlyWarningScoreDescription")).getText();
        String EWSType = driver.findElement(By.id("textEarlyWarningScoreType")).getText();
        Log("Left hand side of the graph for EWS is showing: " + EWSdescription + EWSType);

        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textEarlyWarningScoreTimestamp")), 100);

        String EWSmeasurement = driver.findElement(By.id("textEarlyWarningScoreMeasurement")).getText();
        String EWSMaxpossible = driver.findElement(By.id("textEarlyWarningScoreMaxPossible")).getText();
        Log("Value for EWS is " + EWSmeasurement + "/" + EWSMaxpossible);
        String EWStimestamp = driver.findElement(By.id("textEarlyWarningScoreTimestamp")).getText();
        Log("Time stamp details for EWS is : " + EWStimestamp);
        WebElement EWSgraph = driver.findElement(By.id("fragment_graph_early_warning_scores"));
        assertTrue(EWSgraph.isDisplayed());

    }
    private void WeightScalegraphdetails()
    {
        String ManualyEnterWeight = helper.findTextViewElementWithString("manually_entered_weight").getText();
        Log("Left hand side of the graph for SpO2 is showing: " + ManualyEnterWeight);
        String WeightValue = driver.findElement(By.id("textManuallyEnteredWeightMeasurement")).getText();
        Log("Value for Weight is " + WeightValue);
        String Weighttimestamp = driver.findElement(By.id("textManuallyEnteredWeightTimestamp")).getText();
        Log("Time stamp details for Weight is : " + Weighttimestamp);
        WebElement Weightgraph = driver.findElement(By.id("fragment_graph_manually_entered_weight"));
        assertTrue(Weightgraph.isDisplayed());

    }

    private void supplementOL() {
        String SOLdescription = helper.findTextViewElementWithString("multiline_manually_entered_supplemental_oxygen_levels").getText();
        Log("Left hand side of the graph for Supplemental Oxygen Levels is showing: " + SOLdescription);
        String SOLmeasurement = driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevel")).getText();
        Log("Value for Supplemental Oxygen Levels is " + SOLmeasurement);
        String SOLtimestamp = driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevelTimestamp")).getText();
        Log("Time stamp details for Supplemental Oxygen Levels is : " + SOLtimestamp);
        WebElement SupplementOLgraph = driver.findElement(By.id("fragment_graph_manually_entered_supplemental_oxygen"));
        assertTrue(SupplementOLgraph.isDisplayed());

    }

    private void ConsciousnessLevel() {
        String CLdescription = helper.findTextViewElementWithString("multiline_manually_entered_consciousness_levels").getText();
        Log("Left hand side of the graph for Consciousness Levels is showing: " + CLdescription);
        String CLmeasurement = driver.findElement(By.id("textManuallyEnteredConsciousnessLevel")).getText();
        Log("Value for Consciousness Levels is " + CLmeasurement);
        String CLtimestamp = driver.findElement(By.id("textManuallyEnteredConsciousnessLevelTimestamp")).getText();
        Log("Time stamp details for Consciousness Levels is : " + CLtimestamp);
        WebElement Consciousnesslevelgraph = driver.findElement(By.id("fragment_graph_manually_entered_consciousness_level"));
        assertTrue(Consciousnesslevelgraph.isDisplayed());

    }

    private void CapillaryRefillTime() {
        String CRTdescription = helper.findTextViewElementWithString("multiline_manually_entered_capillary_refill_time").getText();
        Log("Left hand side of the graph for Capillary Refill Time is showing: " + CRTdescription);
        String CRTmeasurement = driver.findElement(By.id("textManuallyEnteredCapillaryRefillTime")).getText();
        Log("Value for Capillary Refill is " + CRTmeasurement);
        String CRTtimestamp = driver.findElement(By.id("textManuallyEnteredCapillaryRefillTimeTimestamp")).getText();
        Log("Time stamp details for Capillary Refill is : " + CRTtimestamp);
        WebElement CapillaryRefillTimegraph = driver.findElement(By.id("fragment_graph_manually_entered_capillary_refill_time"));
        assertTrue(CapillaryRefillTimegraph.isDisplayed());

    }

    private void RespirationDistress() {
        String RDdescription = helper.findTextViewElementWithString("multiline_manually_entered_respiration_distress").getText();
        Log("Left hand side of the graph for  Respiration Distress is showing: " + RDdescription);
        Log("Right hand side of the graph showing for  Respiration Distress is :");
        String RDmeasurement = driver.findElement(By.id("textManuallyEnteredRespirationDistress")).getText();
        Log("Value for  Respiration Distress is " + RDmeasurement);
        String RDtimestamp = driver.findElement(By.id("textManuallyEnteredRespirationDistressTimestamp")).getText();
        Log("Time stamp details for  Respiration Distress is : " + RDtimestamp);
        WebElement RespirationDistressgraph = driver.findElement(By.id("fragment_graph_manually_entered_respiration_distress"));
        assertTrue(RespirationDistressgraph.isDisplayed());
    }

    private void FamilyOrNurseConcern() {
        String FONdescription = helper.findTextViewElementWithString("multiline_manually_entered_family_or_nurse_concern").getText();
        Log("Left hand side of the graph for  Family or Nurse Concern is showing: " + FONdescription);
        String FONmeasurement = driver.findElement(By.id("textManuallyEnteredFamilyOrNurseConcern")).getText();
        Log("Value for  Family or Nurse Concern is " + FONmeasurement);
        String FONtimestamp = driver.findElement(By.id("textManuallyEnteredFamilyOrNurseConcernTimestamp")).getText();
        Log("Time stamp details for  Family or Nurse Concern is : " + FONtimestamp);
        WebElement FamilyOrNurseConcerngraph = driver.findElement(By.id("fragment_graph_manually_entered_family_or_nurse_concern"));
        assertTrue(FamilyOrNurseConcerngraph.isDisplayed());
    }

    private void AppearanceOfShowHideSetupModeCheckBox()
    {
        //Check the appearance of Show and hide setupmode blob checkbox
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("checkBoxShowHideSetupModeBlobs")), 130);
        WebElement ShowHideSetupModeBlobs = driver.findElement(By.id("checkBoxShowHideSetupModeBlobs"));
        ShowHideSetupModeBlobs.isDisplayed();
        Log("Show Hide Setup Mode CheckBox appeared");
    }
    private void ShowAndHodeSetupModeCheckBoxUnchecked()
    {
        //Check the appearance of Show and hide setupmode blob checkbox
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("checkBoxShowHideSetupModeBlobs")), 130);
        WebElement ShowHideSetupModeBlobs = driver.findElement(By.id("checkBoxShowHideSetupModeBlobs"));
        ShowHideSetupModeBlobs.isDisplayed();
        //uncheck the ShowAndHodeSetupModeCheckBox
        String ShowHideSetupModeBlobsCheckBox = ShowHideSetupModeBlobs.getAttribute("checked");
        Log("Status of ShowHideSetupModeBlobs CheckBox is " + ShowHideSetupModeBlobsCheckBox);
        if (ShowHideSetupModeBlobsCheckBox.equals("true")) {
            ShowHideSetupModeBlobs.click();
            Log("Show HideSetupModeBlobs check box is  "+ ShowHideSetupModeBlobs.getAttribute("checked"));
        } else {
            Log("Show HideSetupModeBlobs check box is : " + ShowHideSetupModeBlobsCheckBox);
        }
    }

    private void ShowHideSetupModeCheckBoxNotAppear() {
        //Check Show and hide setupmode blob checkbox not appear
        assertTrue((driver.findElements(By.id("checkBoxShowHideSetupModeBlobs")).size() == 0));
        Log("Show Hide Setup Mode CheckBox not appeared");
    }
}

