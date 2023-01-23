package com.isansys.appiumtests;

import com.isansys.common.enums.DeviceType;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Soumya on 24/10/2017.
 */

public class ScreenshotEndToEnd extends AppiumTest
{
    String folder_name;
    List<WebElement> Option;
    WebElement optionArea;

    String spoof_pending_software_version = "999999";
    String spoof_done_software_version = "0";


    @Test
    public void installation()throws IOException
    {
        helper.spoofCommandIntent_restartInstallationWizard();
        //Click on Start Installation Wizard
        Log("Welcome");
        helper.captureScreenShot();

        driver.findElement(By.id("buttonBigButtonTwo")).click();

        //Scan the Installation QR Code - spoof by sending an intent to the UI
        helper.spoofQrCode_installation();

        //Check the server address
        WebElement lifeguardServerAddress = driver.findElement(By.id("textViewServerAddress"));

        String three_dashes = helper.getAppString("textThreeDashes");

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifeguardServerAddress, three_dashes)), 30);
        String lSAddress = lifeguardServerAddress.getText();
        Log(lSAddress);
        helper.captureScreenShot();



        Assert.assertEquals(helper.getServerAddress(), lSAddress);
        Log("Correct server address ");

        //Check the port address
        WebElement lifeguardServerPort = driver.findElement(By.id("textViewServerPort"));
        String lSPort = lifeguardServerPort.getText();
        Log("Actual port address is " + lSPort);

        Assert.assertEquals(helper.getServerPort(), lSPort);
        Log("Correct Port address ");

        //Check the server WAMP Port
        WebElement lifeguardServerWAMPPort = driver.findElement(By.id("textViewRealTimePort"));
        String lSWampPort = lifeguardServerWAMPPort.getText();
        Log("Actual port address is " + lSWampPort);
        Assert.assertEquals(helper.getServerRealTimePort(), lSWampPort);
        Log("Correct Port address ");

        String test_ward = "Cardiology";

        waitUntil(elementClickablePropertyIsTrue(By.id("spinnerWardList")), 60);

        //Click on the ward spinner and select the  ward
        helper.clickElementOnSpinner("spinnerWardList", test_ward);

        //Click on the bed spinner and select the bed

        // Log("Selecting bed by computer name: " + BuildConfig.buildMachineName);
        String test_Bed = "Bed3";

        helper.clickElementOnSpinner("spinnerBedList",test_Bed );
        helper.captureScreenShot();

        //Click on Set bed Details
        driver.findElement(By.id("buttonSetBedDetails")).click();

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderWardName"), test_ward), 30);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderBedName"), test_Bed), 30);

        //to check Ward detail has set
        String wardDetail = driver.findElement(By.id("textViewSelectedWard")).getText();
        Log("Selected ward detail is " + wardDetail);

        //To Check the bed detail has set
        String BedDetail = driver.findElement(By.id("textViewSelectedBed")).getText();
        Log("Selected bed detail is " + BedDetail);

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonSetupComplete")), 60);
        helper.captureScreenShot();
        Log("Setup Complete");
        driver.findElement(By.id("buttonSetupComplete")).click();
        helper.captureScreenShot();
    }


    @Test
    public void End_to_End_screen_shot_for_Adult_age_range() throws IOException {
        helper.printTestStart("End_to_End_screen_shot_for_Adult_age_range");

        checkAdminSettings(false, true, true);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        helper.captureScreenShot();

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();

        helper.captureScreenShot();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        helper.captureScreenShot();

        //Click on the Start monitoring a patient
        helper.captureScreenShot();
        driver.findElement(By.id("buttonBigButtonOne")).click();
        helper.captureScreenShot();
        //to click on the Patient id field and entered the id
        helper.setPatientId(generatePatientId());
        helper.captureScreenShot();
        //Click on the Next button
        clickOnNext();
        //check the adult check box

        // wait for page/thresholds to load...
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.RadioButton")), 120);

        // Select Adult Age Range
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        int iSize = age.size();
        Log("Select "+age.get(iSize-1).getText()+" age range");
        age.get(iSize-1).click();
        helper.captureScreenShot();
        //click on next
        clickOnNext();
        helper.captureScreenShot();
        manualVitalSignEntryForAdultAgeRange();
        //Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        //Click on Lockscreen and open the patient vital page again to refresh the screen
        clickOnLock();
        helper.spoofQrCode_userUnlock();
        driver.findElement(By.id("buttonBigButtonOne")).click();

        patientVitalsDisplayPageForAdultAgerange();
        clickOnLock();

        helper.captureScreenShot();
        helper.spoofQrCode_userUnlock();
        helper.captureScreenShot();
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        helper.captureScreenShot();
        driver.findElement(By.id("buttonAddLifetouch")).click();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        helper.spoofQrCode_scanDummyWeightScale();
        helper.captureScreenShot();

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
        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleStatus);

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("buttonNext")), 100);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        helper.captureScreenShot();

        clickOnStartMonitoring();

        helper.captureScreenShot();
       //to check  manually entered heart rate /respiration rate graph is over written by Lifetouch graph
       // Assert.assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_heart_rate")).size());
        //assertEquals(0, driver.findElements(By.id("fragment_graph_manually_entered_respiration_rate")).size());
        //assertTrue(driver.findElement(By.id("fragment_graph_lifetouch")).isDisplayed());
       // Log("Previously displayed heart rate /respiration rate graph is replace by Lifetouch graph");
        checkLifetouch();
        //option check box
        WebElement OptionCheckbox= driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        //Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        String isOptionCheckbox=OptionCheckbox.getAttribute("checked");
        Log(OptionCheckbox.getText()+" status is " +isOptionCheckbox);
        helper.captureScreenShot();
        //uncheck the check box
        OptionCheckbox.click();
        //Wait until EWS measurement appear to take screenshots
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutEarlyWarningScoreMeasurementGroup"));
        WebElement ews_measurement_element = right_hand_side.findElement(By.id("textEarlyWarningScoreMeasurement"));
        assertTrue(ews_measurement_element.isDisplayed());
        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(ews_measurement_element, helper.getAppString("textWaitingForData"))), 200);
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textEarlyWarningScoreMaxPossible")), 200);
        helper.captureScreenShot();

        helper.swipeActionScrollDown();

        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_supplemental_oxygen")).isDisplayed());
        Log("Previously displayed Supplemental Oxygen level graph is still visisble" );
        assertTrue(driver.findElement(By.id("fragment_graph_manually_entered_consciousness_level")).isDisplayed());
        Log("Previously displayed Consciousness level graph is still visisble");
        assertTrue(driver.findElement(By.id("fragment_graph_early_warning_scores")).isDisplayed());
        Log("EWS graph  is  visisble");
        helper.captureScreenShot();

        clickOnLock();

        helper.captureScreenShot();
        helper.spoofQrCode_userUnlock();
        helper.captureScreenShot();
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        helper.captureScreenShot();
        //click on the back button
        clickOnBack();
        helper.captureScreenShot();
        //Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        helper.captureScreenShot();
        //Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        helper.captureScreenShot();
        //Click on the Next button
        clickOnNext();
        helper.captureScreenShot();
        //Click on the Next button from Time page
        clickOnNext();
        helper.captureScreenShot();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        helper.captureScreenShot();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton = driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is " + HideButton);
        //Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        //Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        Predefinedannotation();

        //patientvital display
        driver.findElement(By.id("buttonBigButtonOne")).click();
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
       // assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
       // assertTrue(OptionCheckbox.isDisplayed());
        //Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        motionmode();
        OptionCheckbox.click();
        Setupmode();
        OptionCheckbox.click();
        WebElement Poincare= driver.findElement(By.id("checkBoxLifetouchOptionsHeartRatePoincare"));
        assertTrue(Poincare.isDisplayed());
        String isChecked = Poincare.getAttribute("checked");
        Log("Status of Poincare checkbox is " + isChecked);
        Log("Click on Poincare checkbox");
        Poincare.click();
        Log("Poincare checkbox is checked");
        poincare();
        helper.captureScreenShot();
        OptionCheckbox.isDisplayed();
        helper.captureScreenShot();

        clickOnLock();

        helper.spoofQrCode_userUnlock();


        // Click on the Stop Monitoring Current Patient
        driver.findElement(By.id("buttonBigButtonThree")).click();
        helper.captureScreenShot();
        // Click on End session
        driver.findElement(By.id("buttonEndSession")).click();
        helper.captureScreenShot();
        //Click on Enter
        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();
        helper.captureScreenShot();

        //Click on Confirm button
        WebElement confirm_button=  driver.findElement(By.id("buttonEndSessionBigButtonBottom"));
        junit.framework.Assert.assertTrue(confirm_button.isDisplayed());
        Log("Confirm button displayed");
        confirm_button.click();
    }


    @Test
    public void test_the_patient_vital_displaypage_after_adding_all_vitalsign_manually_for_NonAdult_age_range() throws IOException {
        helper.printTestStart("test_the_patient_vital_displaypage_after_adding_all_vitalsign_manually_for_NonAdult_age_range");

        //checkAdminSettingsCorrect(false, true, false);

        //Start a session with non adult age range
        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient

        driver.findElement(By.id("buttonBigButtonOne")).click();
        //to click on the Patient id field and entered the id
        helper.setPatientId(generatePatientId());

        clickOnNext();
        // wait for page/thresholds to load...
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.RadioButton")), 120);

        //Check the presence of age range selection radio button
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        Log("Select "+age.get(0).getText()+" age range");
        age.get(0).click();
        clickOnNext();
        //Click on ManualVitals Only
        manualVitalSignEntryForNonAdultAgeRange();
        helper.captureScreenShot();
        //Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
        patientVitalsDisplayPageForNonAdultAgerange();
    }


    @Test
    public void updateModeScreenshots() throws IOException
    {
        helper.printTestStart("updateModeScreenshots");
        
        // send update available intent
        helper.spoofCommandIntent_allSoftwareUpdatesAvailable(spoof_pending_software_version);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        driver.findElement(By.id("textSoftwareUpdatePending"));

        helper.captureScreenShot();



        helper.spoofQrCode_userUnlock();

        checkUpdateAvailablePage();

        helper.captureScreenShot();

        enterUpdateMode();

        // Check install buttons are clickable
        WebElement gateway_install = driver.findElement(By.id("buttonInstallGatewayApk"));

        String clickable = gateway_install.getAttribute("clickable");

        assertTrue(clickable.equals("true"));

        // Check other buttons not visible...

        helper.captureScreenShot();

        // Assume all apps are at same version
        spoof_done_software_version = driver.findElement(By.id("textCurrentPatientGatewaySoftwareVersion")).getText();

        // spoof GW update
        helper.spoofCommandIntent_softwareUpdateCompleted(spoof_done_software_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);

        switchOffThenOnThenUnlock();


        helper.captureScreenShot();

        WebElement ui_install = driver.findElement(By.id("buttonInstallUserInterfaceApk"));

        clickable = ui_install.getAttribute("clickable");

        assertTrue(clickable.equals("true"));


        // Spoof UI update
        helper.spoofCommandIntent_softwareUpdateCompleted(spoof_done_software_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);

        switchOffThenOnThenUnlock();

        helper.captureScreenShot();

        //       checkAppVersions(spoof_done_software_version);

        completeUpdateAndCheckStartMonitoring();
        helper.updateTestRail(PASSED);

    }

/* update mode */
    private void checkUpdateAvailablePage()
    {
        // Wait for up to 20 minutes(!) - yes really. Won't normally take this long, except if the log file zip tasks are running.
        // This is to ensure that in those rare cases we wait for the EWS thresholds to sync properly.
        waitUntil(elementClickablePropertyIsTrue(By.id("buttonEnterUpdateMode")), 60); //1200);

        String button_one_text = driver.findElement(By.id("buttonEnterUpdateMode")).getText();

        assertEquals(button_one_text, helper.getAppString("enter_update_mode"));

        List<WebElement> buttons = driver.findElements(By.className("android.widget.Button"));

        assertEquals(2, buttons.size());
    }


    private void enterUpdateMode()
    {
        // Click on enter update mode
        driver.findElement(By.id("buttonEnterUpdateMode")).click();

        // confirm we're on the correct page
        driver.findElement(By.id("labelPatientGateway"));
        driver.findElement(By.id("labelUserInterface"));
    }


    private void completeUpdateAndCheckStartMonitoring() throws IOException
    {
        WebElement updates_complete = driver.findElement(By.id("buttonUpdatesComplete"));

        String clickable = updates_complete.getAttribute("clickable");

        assertTrue(clickable.equals("true"));

        updates_complete.click();

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();

        // Check we're back on the normal start monitoring page
        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonOne")), 60); //1200);

        String button_one_text = driver.findElement(By.id("buttonBigButtonOne")).getText();

        assertEquals(button_one_text, helper.getAppString("textStartMonitoringAPatient"));
    }
    /* END update mode */


    public void motionmode() throws IOException{
        WebElement MotionMode= driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        //Click on the option checkbox and check the Motion mode checkbox status
        WebElement OptionCheckbox=driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        OptionCheckbox.click();
        helper.captureScreenShot();
        //check the Motion Mode status from option overly
        MotionMode= driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        isChecked = MotionMode.getAttribute("checked");
        Log("Check the Status of Motion Mode checkbox in option overlay is  " + isChecked);
        // Uncheck the option check box
        OptionCheckbox.click();

       Log("Check the Motiomode check box status in Lefthandsisde of the Lifetouch graph");
        //Check the Motion Mode checkbox status in LHS side of the graph
         WebElement motionmodeLHS=driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Lefthandside of the Lifetouch graph is :"+isChecked);
        //Check the appearance and presence of graph
        WebElement motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
        assertTrue(motionModeGraphView.isDisplayed());
        helper.captureScreenShot();
        //uncheck the option checkbox
        motionmodeLHS.click();
        Log(driver.getDeviceTime());
    }

    public void Setupmode()throws IOException
    {
        WebElement setupmode= driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of Setupmode checkbox is " + isChecked);
        Log("Click on setupmode checkbox");
        setupmode.click();
        //Click on the option checkbox and check the setup mode checkbox status
        //driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        //setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        helper.captureScreenShot();
       // isChecked = setupmode.getAttribute("checked");
       // Log("Check the Status of Setupmode checkbox in option overlay is  " + isChecked);
        Log("Check the Setupmode check box status in Lefthandsisde of the Lifetouch graph");
        //Check the setup mode checkbox status in LHS side of the graph
        WebElement setupmodeLHS=driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = setupmodeLHS.getAttribute("checked");
       Log("Check the Setupmode check box status in Lefthandsisde of the Lifetouch graph is :"+isChecked);
        //uncheck the option checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        //WebElement SetUpModeGraphView=driver.findElement(By.id("plotRealTimeECG"));
       // assertTrue(SetUpModeGraphView.isDisplayed());
        helper.captureScreenShot();

        // uncheck the setupmode check box
        setupmodeLHS.click();
        //assertTrue((driver.findElements(By.id("plotRealTimeECG")).size()==0));
    }

    public void poincare() throws IOException {
        Log("Poincare window is open");
        assertTrue(driver.findElement(By.id("linear_layout_poincare_chart")).isDisplayed());
        Log("Poincare graph displayed");
        Log("Check the appearance  and statusof of time checkbox graph ");
        //1 min graph
        WebElement graph1mins =driver.findElement(By.id("checkBox1MinuteGraph"));
        assertTrue(graph1mins.isDisplayed());
        String isChecked1 = graph1mins.getAttribute("checked");
        Log(graph1mins.getText()+"status is " +isChecked1);

        //spinner day and hour selection
        WebElement Dayselection =driver.findElement(By.id("spinnerDaySelection"));
        assertTrue(Dayselection.isDisplayed());
        Log(Dayselection.getText()+" Spinner day selection exit");
        //spinner day and hour selection
        WebElement Hoursselection =driver.findElement(By.id("spinnerHoursSelection"));
        assertTrue(Hoursselection.isDisplayed());
        Log(Hoursselection.getText()+" Spinner Hours selection exit");
        Log("The screenshot has captured for Poincaregraph");
        //to  check motionmode graph is running on the screen
        helper.captureScreenShot();

        //Dismiss button
        Log("Test the appearance of Dismiss button");
        WebElement dismiss=driver.findElement(By.id("buttonDismissPoincare"));
        assertTrue(dismiss.isDisplayed());
        Log(dismiss.getText()+"Button present");
        Log("Dismiss the popup window");

        driver.findElement(By.id("buttonDismissPoincare")).click();
    }

    private void checkLifetouch()
    {
        //Lifetouch Id
        String lifetouch_device_id = driver.findElement(By.id("textLifetouchHumanReadableDeviceId")).getText();
        Log("Device ID is:" + lifetouch_device_id);

        String heart_rate_label = driver.findElement(By.id("textHeartRateBeatsPerMinLabel")).getText();
        Log("Right hand side of the graph is showing" + heart_rate_label);

        WebElement lifetouch_measurement_element = driver.findElement(By.id("textHeartRate"));
        assertTrue(lifetouch_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifetouch_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        assertTrue(driver.findElement(By.id("textHeartRate")).isDisplayed());
        String heart_rate_value = lifetouch_measurement_element.getText();
        Log("Value showing for heart rate is: " + heart_rate_value);
        assertTrue(driver.findElement(By.id("textHeartRateTimestamp")).isDisplayed());

        //Respiration Rate
        Log("Respiration rate");
        String respiration_label = driver.findElement(By.id("textRespirationRateBreathMinLabel")).getText();
        Log("Right hand side of Lifetouch graph is showing:" + respiration_label);
        String respitation_value = driver.findElement(By.id("textRespirationRate")).getText();
        Log("Respiration value:" + respitation_value);
    }

    private void checkLifetouchLeadoff()
    {
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
        //Checkbox 2
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

    private void checkLifetemp()
    {
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

    private void checkLifetempLeadOff()
    {
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

    private void checkNonin()
    {
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
        WebElement nonin_Graph= driver.findElement(By.id("fragment_graph_pulse_ox"));
        assertTrue(nonin_Graph.isDisplayed());
    }

    private void checkNoninLeadoff()
    {
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
    }

    private void checkBloodPressureAnD()
    {
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));

        // Check the status for Lifetemp for rhs
        String bp_device_id = left_hand_side.findElement(By.id("textBloodPressureHumanReadableDeviceId")).getText();
        Log("Device ID is:" + bp_device_id);
        String bp_label = right_hand_side.findElement(By.id("textBloodPressureLabel")).getText();
        Log("RHS of Blood Pressure graph is showing :" + bp_label);

        WebElement bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        assertTrue(bp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        assertTrue(right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic")).isDisplayed());
        String bp_value_systolic = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic")).getText();
        assertTrue(right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic")).isDisplayed());
        String bp_value_diastolic = right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic")).getText();
        Log("Value showing for BP is: " + bp_value_systolic + "/" + bp_value_diastolic);
        String bp_timestamp = right_hand_side.findElement(By.id("textBloodPressureMeasurementTimestamp")).getText();
        Log("Value showing for timestamp: " + bp_timestamp);
        WebElement AnDGraph= driver.findElement(By.id("fragment_graph_blood_pressure"));
        assertTrue(AnDGraph.isDisplayed());
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


    public void manualVitalSignEntryForAdultAgeRange()throws IOException
    {
        helper.captureScreenShot();

        clickOnManualVitalsOnly();

        helper.captureScreenShot();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        helper.captureScreenShot();
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
       // String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
       // Assert.assertEquals(expected_title, actual_title);
        Log("Landed on the correct page");
        Log("Measurement Interval set to ten minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("thirty_minutes").click();
        helper.captureScreenShot();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
        helper.captureScreenShot();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        helper.captureScreenShot();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        helper.captureScreenShot();
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        //Click on next
        clickOnNext();
        helper.captureScreenShot();
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Temp is 38");
        //Click on next
        clickOnNext();
        helper.captureScreenShot();
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        helper.captureScreenShot();
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
        helper.captureScreenShot();
        clickOnNext();
        // Click on the Weight  button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize=Option.size();
        //System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        WebElement Alert= Option.get(0);

        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        helper.captureScreenShot();
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();
        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(0).getText());
        helper.captureScreenShot();
        Option.get(0).click();
        // Click on Next button
        clickOnNext();
        helper.captureScreenShot();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        helper.captureScreenShot();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }
    private void manualVitalSignEntryForNonAdultAgeRange()throws IOException
    {
        clickOnManualVitalsOnly();

        helper.captureScreenShot();

        // click on Next button from observation set time page
        clickOnNext();
        helper.captureScreenShot();
        // To check Next button Leads to the correct page.
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, actual_title);
        Log("Landed on the correct page");
        // Select the measurement interval as 30 mins
        Log("Measurement Interval set to thirty_minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        helper.captureScreenShot();

        Log("Lead us to the manual vital entry page with set time and measurement length " );

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
        // Click on the Weight  button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();

        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        WebElement Alert= Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        helper.captureScreenShot();

        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(1).getText());
        helper.captureScreenShot();
        Option.get(1).click();
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
        helper.findTextViewElementWithString("respiration_distress").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option available for RespirationDistressLevel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for RespirationDistressLevel is  "+Option.get(0).getText());
        helper.captureScreenShot();
        Option.get(0).click();
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        helper.findTextViewElementWithString("capillary_refill_time").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        iSize=Option.size();
        System.out.println("Number of option available for CapillaryRefillTime is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        Log("Value Enter  for CapillaryRefillTime is  "+Option.get(0).getText());
        helper.captureScreenShot();
        Option.get(0).click();
        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");

        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option available for FamilyNurseConcern is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for FamilyNurseConcern is  "+Option.get(0).getText());
        helper.captureScreenShot();
        Option.get(0).click();
        helper.captureScreenShot();
        //Click on Next button
        clickOnNext();
        helper.captureScreenShot();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }

    public void patientVitalsDisplayPageForAdultAgerange()throws IOException
    {

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

        helper.captureScreenShot();
        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textEarlyWarningScoreTimestamp")), 100);

        helper.swipeActionScrollDown();
        Log("Scrol the page to buttom");
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for EWS");
        EWStype();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for Weight");
        WeightScalegraphdetails();
        Log("--------------------------------------------");
        Log("Checking the graph LHS and  RHS of the for SupplementalOxygenLevel");
        supplementOL();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for ConsciousnessLevel");
        ConsciousnessLevel();
        helper.captureScreenShot();
    }
    private void patientVitalsDisplayPageForNonAdultAgerange()throws IOException
    {
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
        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textEarlyWarningScoreTimestamp")), 100);
        Log("Checking the LHS and  RHS of the graph for EWS");
        EWStype();
        Log("--------------------------------------------");
        helper.captureScreenShot();
        helper.swipeActionScrollDown();

        Log("Checking the LHS and  RHS of the graph for Weight");
        WeightScalegraphdetails();
        Log("--------------------------------------------");

        Log("Checking the graph LHS and  RHS of the for SupplementalOxygenLevel");
        supplementOL();
        Log("--------------------------------------------");
        Log("Checking the LHS and  RHS of the graph for ConsciousnessLevel");
        ConsciousnessLevel();
        Log("--------------------------------------------");
        helper.captureScreenShot();
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
        helper.captureScreenShot();
    }

    private void HRgraphdetails()
    {
        String ManualyEnterHR = helper.findTextViewElementWithString("multiline_manually_entered_heart_rate").getText();
        Log("Left hand side of the graph for HR is showing: " +ManualyEnterHR);
        //value showing for HR
        String HRvalue= driver.findElement(By.id("textManuallyEnteredHeartRate")).getText();
        Log("Value for HR is "+HRvalue);
       // String HRtimestamp= driver.findElement(By.id("textManuallyEnteredHeartRateTimestamp")).getText();
        //Log("Time stamp details for HR is : "+HRtimestamp);
       // String HRmeasurementvalidity=driver.findElement(By.id("progressBarManuallyEnteredHeartRateMeasurementValidity")).getText();
       // Log(HRmeasurementvalidity);
        WebElement HRgraph=driver.findElement(By.id("fragment_graph_manually_entered_heart_rate"));
        assertTrue(HRgraph.isDisplayed());
    }

    private void RRgraphdetails()
    {
        //Value showing for RR
        String ManualyEnterRR = helper.findTextViewElementWithString("multiline_manually_entered_respiration_rate").getText();
        Log("Left hand side of the graph for RR is showing: " +ManualyEnterRR);
        String RRvalue= driver.findElement(By.id("textManuallyEnteredRespirationRate")).getText();
        Log("Value for RR is "+RRvalue);
       // String RRtimestamp= driver.findElement(By.id("textManuallyEnteredRespirationRateTimestamp")).getText();
       // Log("Time stamp details for RR is : "+RRtimestamp);
        //String RRmeasurementvalidity=driver.findElement(By.id("progressBarManuallyEnteredRespirationRateMeasurementValidity")).getText();
        //Log(RRmeasurementvalidity);
        WebElement RRgraph=driver.findElement(By.id("fragment_graph_manually_entered_respiration_rate"));
        assertTrue(RRgraph.isDisplayed());
    }

    private void Tempgraphdetails()
    {
        //Value showing for Temp
        String ManualyEnterTemp = helper.findTextViewElementWithString("multiline_manually_entered_temperatures").getText();
        Log("Left hand side of the graph for Temperatures is showing: " +ManualyEnterTemp);
        String Tempvalue= driver.findElement(By.id("textManuallyEnteredTemperature")).getText();
        Log("Value for Temp is "+Tempvalue);
       // String Temptimestamp= driver.findElement(By.id("textManuallyEnteredTemperatureTimestamp")).getText();
        //Log("Time stamp details for Temp is : "+Temptimestamp);
        WebElement Tempgraph=driver.findElement(By.id("fragment_graph_manually_entered_temperature"));
        assertTrue(Tempgraph.isDisplayed());

    }


    private void SpO2graphdetails()
    {
        String ManualyEnterSpO2 = helper.findTextViewElementWithString("multiline_manually_entered_spo2_measurements").getText();
        Log("Left hand side of the graph for SpO2 is showing: " +ManualyEnterSpO2);
        String SpO2value= driver.findElement(By.id("textManuallyEnteredSpO2")).getText();
        Log("Value for SpO2 is "+SpO2value);
        //String SpO2timestamp= driver.findElement(By.id("textManuallyEnteredSpO2Timestamp")).getText();
       // Log("Time stamp details for SpO2 is : "+SpO2timestamp);
        WebElement SpO2graph=driver.findElement(By.id("fragment_graph_manually_entered_spo2"));
        assertTrue(SpO2graph.isDisplayed());
    }

    private void WeightScalegraphdetails()
    {
        String ManualyEnterWeight = helper.findTextViewElementWithString("manually_entered_weight").getText();
        Log("Left hand side of the graph for Weight is showing: " + ManualyEnterWeight);
        String WeightValue = driver.findElement(By.id("textManuallyEnteredWeightMeasurement")).getText();
        Log("Value for Weight is " + WeightValue);
        WebElement Weightgraph = driver.findElement(By.id("fragment_graph_manually_entered_weight"));
        assertTrue(Weightgraph.isDisplayed());

    }

    private void Bloodpressuregraphdetails()
    {
        //Value showing for Blood Pressure
        String ManualyEnterBP = helper.findTextViewElementWithString("multiline_manually_entered_blood_pressures").getText();
        Log("Left hand side of the graph for BloodPressure is showing: " +ManualyEnterBP);
        String BPSystolicvalue= driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementSystolic")).getText();
        String BPDiastolic=driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementDiastolic")).getText();
        Log("Value for BP is "+BPSystolicvalue+"/"+BPDiastolic);
        String BPtimestamp= driver.findElement(By.id("textManuallyEnteredBloodPressureTimestamp")).getText();
        Log("Time stamp details for BP is : "+BPtimestamp);
        WebElement BloodPressuregraph=driver.findElement(By.id("fragment_graph_manually_entered_blood_pressure"));
        assertTrue(BloodPressuregraph.isDisplayed());
    }

    private void EWStype()
    {
        String EWSdescription= driver.findElement(By.id("textEarlyWarningScoreDescription")).getText();
        String EWSType= driver.findElement(By.id("textEarlyWarningScoreType")).getText();
        Log("Left hand side of the graph for EWS is showing: " +EWSdescription +EWSType);

        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textEarlyWarningScoreTimestamp")), 100);

        String EWSmeasurement= driver.findElement(By.id("textEarlyWarningScoreMeasurement")).getText();
        String EWSMaxpossible= driver.findElement(By.id("textEarlyWarningScoreMaxPossible")).getText();
        Log("Value for EWS is "+EWSmeasurement+"/"+EWSMaxpossible);
        String EWStimestamp= driver.findElement(By.id("textEarlyWarningScoreTimestamp")).getText();
        Log("Time stamp details for EWS is : "+EWStimestamp);
        WebElement EWSgraph=driver.findElement(By.id("fragment_graph_early_warning_scores"));
        assertTrue(EWSgraph.isDisplayed());

    }

    private void supplementOL()
    {
        String SOLdescription = helper.findTextViewElementWithString("multiline_manually_entered_supplemental_oxygen_levels").getText();
        Log("Left hand side of the graph for Supplemental Oxygen Levels is showing: " +SOLdescription );
        String SOLmeasurement= driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevel")).getText();
        Log("Value for Supplemental Oxygen Levels is " +SOLmeasurement);
        String SOLtimestamp= driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevelTimestamp")).getText();
        Log("Time stamp details for Supplemental Oxygen Levels is : "+SOLtimestamp);
        WebElement SupplementOLgraph=driver.findElement(By.id("fragment_graph_manually_entered_supplemental_oxygen"));
        assertTrue(SupplementOLgraph.isDisplayed());

    }

    private void ConsciousnessLevel()
    {
        String CLdescription = helper.findTextViewElementWithString("multiline_manually_entered_consciousness_levels").getText();
        Log("Left hand side of the graph for Consciousness Levels is showing: " +CLdescription );
        String CLmeasurement= driver.findElement(By.id("textManuallyEnteredConsciousnessLevel")).getText();
        Log("Value for Consciousness Levels is " +CLmeasurement);
        String CLtimestamp= driver.findElement(By.id("textManuallyEnteredConsciousnessLevelTimestamp")).getText();
        Log("Time stamp details for Consciousness Levels is : "+CLtimestamp);
        WebElement Consciousnesslevelgraph=driver.findElement(By.id("fragment_graph_manually_entered_consciousness_level"));
        assertTrue(Consciousnesslevelgraph.isDisplayed());

    }

    private void CapillaryRefillTime()
    {
        String CRTdescription = helper.findTextViewElementWithString("multiline_manually_entered_capillary_refill_time").getText();
        Log("Left hand side of the graph for Capillary Refill Time is showing: " +CRTdescription );
        String CRTmeasurement= driver.findElement(By.id("textManuallyEnteredCapillaryRefillTime")).getText();
        Log("Value for Capillary Refill is " +CRTmeasurement);
        String CRTtimestamp= driver.findElement(By.id("textManuallyEnteredCapillaryRefillTimeTimestamp")).getText();
        Log("Time stamp details for Capillary Refill is : "+CRTtimestamp);
        WebElement CapillaryRefillTimegraph=driver.findElement(By.id("fragment_graph_manually_entered_capillary_refill_time"));
        assertTrue(CapillaryRefillTimegraph.isDisplayed());

    }

    private void RespirationDistress()
    {
        String RDdescription = helper.findTextViewElementWithString("multiline_manually_entered_respiration_distress").getText();
        Log("Left hand side of the graph for  Respiration Distress is showing: " +RDdescription );
        Log("Right hand side of the graph showing for  Respiration Distress is :");
        String RDmeasurement= driver.findElement(By.id("textManuallyEnteredRespirationDistress")).getText();
        Log("Value for  Respiration Distress is " +RDmeasurement);
        String RDtimestamp= driver.findElement(By.id("textManuallyEnteredRespirationDistressTimestamp")).getText();
        Log("Time stamp details for  Respiration Distress is : "+RDtimestamp);
        WebElement RespirationDistressgraph=driver.findElement(By.id("fragment_graph_manually_entered_respiration_distress"));
        assertTrue(RespirationDistressgraph.isDisplayed());
    }

    private void FamilyOrNurseConcern()
    {
        String FONdescription = helper.findTextViewElementWithString("multiline_manually_entered_family_or_nurse_concern").getText();
        Log("Left hand side of the graph for  Family or Nurse Concern is showing: " +FONdescription );
        String FONmeasurement= driver.findElement(By.id("textManuallyEnteredFamilyOrNurseConcern")).getText();
        Log("Value for  Family or Nurse Concern is " +FONmeasurement);
        String FONtimestamp= driver.findElement(By.id("textManuallyEnteredFamilyOrNurseConcernTimestamp")).getText();
        Log("Time stamp details for  Family or Nurse Concern is : "+FONtimestamp);
        WebElement FamilyOrNurseConcerngraph=driver.findElement(By.id("fragment_graph_manually_entered_family_or_nurse_concern"));
        assertTrue(FamilyOrNurseConcerngraph.isDisplayed());
    }

    public void Predefinedannotation()throws IOException
    {
        WebElement Predefined= driver.findElement(By.id("buttonAnnotationPredefined"));
        Log("Click on the predefined Button");
        Predefined.click();
        // Check the Predefined button selected and Predefined Text appear
        String PredefineText= driver.findElement(By.id("textVitalSignValue")).getText();
        String expected= helper.getAppString("predefined");
        Assert.assertEquals(PredefineText,expected);
        Log("The value appear after selecting predefined button is "+PredefineText);
        helper.captureScreenShot();
        // Check the appearance of Next button after selecting Predefined and click on the next button
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        clickOnNext();
        // Check the Next button from Annotation entry mode page took us to the right page
        String Texttop= driver.findElement(By.id("textTop")).getText();
        Log("Title name is "+Texttop);
        //String TexttopExpected="Enter the Annotation Time by moving the green dot";
        //Assert.assertEquals(Texttop,TexttopExpected);
        Log("We are in correct page");
        helper.captureScreenShot();
        clickOnNext();
        //Select the condition
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_conditions").isDisplayed());
        String SelectTheCondition=helper.findTextViewElementWithString("select_the_annotation_conditions").getText();
        Log("Lead us to :"+SelectTheCondition);
        List<WebElement> textCondition=driver.findElements(By.id("checkBoxAnnotationName"));
        int i=0;
        // Cardiac Arrest
        WebElement Cardiac_Arrest=textCondition.get(i);
        Log("click on "+Cardiac_Arrest.getText());
        Cardiac_Arrest.click();
        i=1;
        //Respiratory Arrest
        WebElement Respiratory_Arrest=textCondition.get(i);
        Respiratory_Arrest.click();
        Log("click on "+Respiratory_Arrest.getText());
        i=5;
        //Respiratory Distress
        WebElement Respiratory_Distress=textCondition.get(i);
        Respiratory_Distress.click();
        Log("click on "+Respiratory_Distress.getText());
        helper.captureScreenShot();
        //press next button
        Log("Click on the next button");
        clickOnNext();
        //Select some actions
        String SelectTheAction=helper.findTextViewElementWithString("select_the_annotation_actions").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_actions").isDisplayed());
        Log("Lead us to :"+SelectTheAction);
        List<WebElement> textAction=driver.findElements(By.id("checkBoxAnnotationName"));
        //Ventilation
        WebElement Ventilation= textAction.get(0);
        Ventilation.click();
        Log("click on "+Ventilation.getText());
        //Given Oxygen
        WebElement Given_Oxygen=textAction.get(3);
        Given_Oxygen.click();
        Log("click on "+Given_Oxygen.getText());
        helper.captureScreenShot();
        Log("Click on the next button");
        //press next button
        clickOnNext();
        //Select some Outcomes
        String SelectTheOutcomes=helper.findTextViewElementWithString("select_the_annotation_outcomes").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_outcomes").isDisplayed());
        Log("Lead us to :"+SelectTheOutcomes);
        List<WebElement> textOutCome=driver.findElements(By.id("checkBoxAnnotationName"));
        // CPR
        WebElement CPR=textOutCome.get(0);
        CPR.click();
        Log("click on "+CPR.getText());
        //Patient transferred to PIC
        WebElement Patient_transferred_to_PIC=textOutCome.get(1);
        Patient_transferred_to_PIC.click();
        Log("click on "+Patient_transferred_to_PIC.getText());
        //Patient transferred to HDU
        WebElement Patient_transferred_to_HDU=textOutCome.get(2);
        Patient_transferred_to_HDU.click();
        Log("click on "+Patient_transferred_to_HDU.getText());
        helper.captureScreenShot();
        Log("Click on the next button");
        //press next button
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    // This is different to checkAdminSettingsCorrect because it ignores the timeout length setting.
    // ToDo: replace this with a version of checkAdminSettingsCorrect that works in different languages (IIT-2171)
    public void checkAdminSettings(boolean check_patient_name_lookup, boolean check_auto_add_ews, boolean check_use_predefined_annotations) throws IOException
    {
        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Check if the patient name lookup needs changing
        boolean patient_vitals_checked;
        WebElement EVitalSigns = driver.findElement(By.id("checkBoxPatientNameLookup"));
        patient_vitals_checked = EVitalSigns.getAttribute("checked").equals("true");

        Log("PatientNameLookup Check box Status " + patient_vitals_checked);
        if(patient_vitals_checked != check_patient_name_lookup)
        {
            EVitalSigns.click();
            Log("PatientNameLookup changed");
        }
        else
        {
            Log("PatientNameLookup already in correct state");
        }

        // Check if the auto-add ews needs changing
        boolean auto_add_ews_checked;
        WebElement ews_checkbox = driver.findElement(By.id("checkBoxAutoAddEarlyWarningScores"));
        auto_add_ews_checked = ews_checkbox.getAttribute("checked").equals("true");

        Log("AutoAddEWS Check box Status " + auto_add_ews_checked);
        if(auto_add_ews_checked != check_auto_add_ews)
        {
            ews_checkbox.click();
            Log("EWS checkbox changed");
        }
        else
        {
            Log("EWS checkbox already in correct state");
        }

        // Check if use predefined annotations needs changing
        boolean use_predefined_annotations_checked;
        WebElement annotations_checkbox = driver.findElement(By.id("checkBoxEnablePredefinedAnnotations"));
        use_predefined_annotations_checked = annotations_checkbox.getAttribute("checked").equals("true");

        Log("Use Pre-defined Annotations Check box Status " + use_predefined_annotations_checked);
        if(use_predefined_annotations_checked != check_use_predefined_annotations)
        {
            annotations_checkbox.click();
            Log("Pre-defined Annotations checkbox changed");
        }
        else
        {
            Log("Pre-defined Annotations checkbox already in correct state");
        }

        // Turn of periodic setup mode by default - individual tests can enable it if they need it
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if(isCheckedShowPeriodicSetupMode.equals("true")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box un-checked");
        } else {
            Log("Periodic setup mode check box  already un-checked");
        }
        //set the display time out as No time out from Admin page
        //Displaytimeout
        WebElement DisplayTimeout=driver.findElementById("labelDisplayTimeoutLength");
        assertTrue(DisplayTimeout.isDisplayed());
      //Check the presence of Dropdown associated with DispalyTimeout option
        WebElement DisplaytimoutDropdown= driver.findElementById("spinnerDisplayTimeoutLength");
        assertTrue(DisplaytimoutDropdown.isDisplayed());

        WebElement checkbox_patient_vitals_timeout = driver.findElementById("checkBoxApplyDisplayTimeoutToPatientVitalsDisplay");

        String Usedisplaytimeout_onchatpage = checkbox_patient_vitals_timeout.getAttribute("checked");
        Log("Use display timeout on chart page status is " + Usedisplaytimeout_onchatpage);
        if (Usedisplaytimeout_onchatpage.equals("true"))
        {
            checkbox_patient_vitals_timeout.click();
            Log("Use display timeout on chart page unchecked");
        }
        else
        {
            Log("Use display timeout on chart page already unchecked");
        }

        clickOnLock();
    }


    @Override
    protected String generatePatientId()
    {
        return "Example Patient ID";
    }

}

