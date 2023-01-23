package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertTrue;

import android.text.format.DateUtils;

import com.isansys.appiumtests.AppiumTest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;

import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;

public class DisplayTimeout extends AppiumTest
{
    @Test
    //check the display time out for Admin page after 30 seconds
    public void CheckTheTimeOutForAdminPage() throws IOException {
        String case_id = "23154";
        helper.printTestStart("check the display time out for Admin page after 30 seconds",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Display Timeout
        WebElement DisplayTimeout = driver.findElementById("labelDisplayTimeoutLength");
        assertTrue(DisplayTimeout.isDisplayed());

        // Use display timeout on chart page check box.
        WebElement UseDisplayTimeout = driver.findElementById("checkBoxApplyDisplayTimeoutToPatientVitalsDisplay");
        assertTrue(UseDisplayTimeout.isDisplayed());

        // Check the presence of Dropdown associated with DisplayTimeout option
        WebElement DisplaytimoutDropdown = driver.findElementById("spinnerDisplayTimeoutLength");
        assertTrue(DisplaytimoutDropdown.isDisplayed());

        // Set the time out value as 30 seconds
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", helper.getAppString("thirty_seconds"));
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    //Check Admin page time out if servers sync popup window is open
    @Test
    //check the display time out for Admin page after 30 seconds
    public void CheckTheTimeOutForAdminPageifServerPopUpWindowIsOpen() throws IOException {
        String case_id = "23155";
        helper.printTestStart("CheckTheTimeOutForAdminPageifServerPopUpWindowIsOpen",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Set the time out value as 30 seconds
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", helper.getAppString("thirty_seconds"));
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        Assert.assertTrue(driver.findElement(By.id("textViewSessionStatusHeader")).isDisplayed());
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //check the display time out for start monitoring page after  1 minute
    public void CheckTheTimeOutForStartmonitoringpage() throws IOException {
        String case_id = "23156";

        helper.printTestStart("check the display time out for start monitoring page after  1 minute",case_id);

        setDisplayTimeout(false, helper.getAppString("one_minute"));

        //scan the user QR code
        helper.spoofQrCode_userUnlock();
        // Check the presence of  Start monitoring a patient button
        WebElement Startmonitoring = driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(Startmonitoring.isDisplayed());
        checkTimeout(60);
        helper.updateTestRail(PASSED);
    }

    @Test
    //check the display time out for PatientIdentifierPage after  30 seconds
    public void CheckTheTimeOutForPatientIdentifierPage() throws IOException {
        String case_id = "23157";

        helper.printTestStart("check the display time out for PatientIdentifierPage after  30 seconds",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        //scan teh user QR code
        helper.spoofQrCode_userUnlock();
        // Check the presence of  Start monitoring a patient button
        WebElement Startmonitoring = driver.findElement(By.id("buttonBigButtonOne"));
        Startmonitoring.click();
        WebElement PatientId = driver.findElement(By.id("editPatientID"));
        assertTrue(PatientId.isDisplayed());
        PatientId.click();
        Log(driver.findElement(By.id("labelPatientID")).getText() + "page is open");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //check the Nodisplay time out for PatientIdentifierPage after  display time out change to No time out
    public void CheckNOTimeOutForPatientIdentifierPageIfdispalyTimeOutChangeToNoTimeOut() throws IOException, InterruptedException
    {
        String case_id = "23159";
        helper.printTestStart("CheckNOTimeOutForPatientIdentifierPage",case_id);

        setDisplayTimeout(false, helper.getAppString("no_timeout"));

        //scan teh user QR code
        helper.spoofQrCode_userUnlock();
        // Check the presence of  Start monitoring a patient button
        WebElement Startmonitoring = driver.findElement(By.id("buttonBigButtonOne"));
        Startmonitoring.click();
        WebElement PatientId = driver.findElement(By.id("editPatientID"));
        assertTrue(PatientId.isDisplayed());
        PatientId.click();
        Log(driver.findElement(By.id("labelPatientID")).getText() + "page is open");
        checkNoTimeout(310);

        helper.pressDone();

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }

    @Test
    //check the display time out for Age selection Page after  30 seconds
    public void CheckTheTimeOutForAgeSelectionPage() throws IOException {
        String case_id = "23158";
        helper.printTestStart("check the display time out for Age selection Page after  30 seconds",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        //scan code and go to patient age selection page
        scanQrCodeAndEnterPatientId();
        // wait for page/thresholds to load...
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.RadioButton")), 120);
        //select a age range
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        int iSize = age.size();
        age.get(1).click();
        //Age selection page is open
        Log(driver.findElement(By.id("textPleaseSelectPatientAgeRange")).getText() + " page is open");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //check the display time out for scan device QR codePage after  1 minute
    public void CheckTheTimeOutForScanDeviceQRcodePage() throws IOException {
        String case_id = "23160";
        helper.printTestStart("check the display time out for scan device QR codePage after  1 minute",case_id);

        setDisplayTimeout(false, helper.getAppString("one_minute"));

        setUpSessionWithNonAdultAgeRange();
        Log(helper.findTextViewElementWithString("scan_device_qr_codes").getText() + "page is open");
        checkTimeout(60);
        helper.updateTestRail(PASSED);

    }

    @Test
    //check the display time out for Leads to Observation page to set Time Page after  30 seconds
    public void CheckTheTimeOutForLeadstoObservationpagetosetTimePage() throws IOException {
        String case_id = "23161";
        helper.printTestStart("check the display time out for Leads to Observation page to set Time Page after  30 seconds",case_id);
        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));
        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        Log(driver.findElement(By.id("textTop")).getText() + " page is open");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check The Time Out For Select How Long To Use This Measurement In The Early Warning Score page
    public void CheckTheTimeOutForSelectHowLongToUseThisMeasurementInTheEarlyWarningScorePage() throws IOException {
        String case_id = "23162";

        helper.printTestStart("Check The Time Out For Select How Long To Use This Measurement In The Early Warning Score page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        //Click on next button from display time out for Leads to Observation page to set Time Page
        clickOnNext();
        Log(driver.findElement(By.id("textTop")).getText() + " page is open");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check The Time Out For selected measurement type page
    public void CheckTheTimeOutForSelectedMeasurementTypePage() throws IOException {
        String case_id = "23164";

        helper.printTestStart("Check The Time Out For selected measurement type page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        //Click on next button from display time out for Leads to Observation page to set Time Page
        clickOnNext();
        driver.findElement(By.id("buttonVitalSignValidityTime")).click();
        Log(driver.findElement(By.id("textTop")).getText() + " page is open");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check The Time Out For confirm vital sign details page
    public void CheckTheTimeOutForConfirmVitalSignDetailsPage() throws IOException {
        String case_id = "23163";

        helper.printTestStart("Check The Time Out For confirm vital sign details page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        //Click on next button from display time out for Leads to Observation page to set Time Page
        clickOnNext();
        driver.findElement(By.id("buttonVitalSignValidityTime")).click();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        //Enter the value and click next
        clickOnNext();
        //Click next from Observation time page after entering the RR value
        clickOnNext();
        assertTrue(helper.findTextViewElementWithString("confirm_vital_sign_details").isDisplayed());
        Log((helper.findTextViewElementWithString("confirm_vital_sign_details")).getText() + " page is open");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check display time out on Mode selection page
    public void CheckDisplayTimeOutForModeselectionPage() throws IOException {
        String case_id = "23165";

        helper.printTestStart("Check display time out on Mode selection page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        Log("Lead to Modeselection page");
        assertTrue(driver.findElement(By.id("buttonBigButtonTwo")).isDisplayed());
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check No time out Mode selection apge
    public void CheckNoTimeOutForModeselectionPage() throws IOException, InterruptedException {
        String case_id = "23166";

        helper.printTestStart("Check No time out Mode selection apge",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Set the display time out for 1 mins from admin page
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", helper.getAppString("no_timeout"));

        //Click on lock screen button
        driver.findElement(By.id("buttonLock")).click();
        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        Log("Lead to Modeselection page");
        assertTrue(driver.findElement(By.id("buttonBigButtonTwo")).isDisplayed());
        checkNoTimeout(310);

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check The Time Out For change session setting page
    public void CheckTheTimeOutForChangeSessionSettingPage() throws IOException {
        String case_id = "23167";

        helper.printTestStart("Check The Time Out For change session setting page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        clickOnChangeSessionSettings();
        assertTrue(driver.findElement(By.id("linear_layout__change_session_settings__lifetouch")).isDisplayed());
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check The Time Out For change session setting page if Serversync popup window is open.
    public void CheckTheTimeOutForChangeSessionSettingPageIfServerSyncPopupWindowIsOpen() throws IOException {
        String case_id = "23168";

        helper.printTestStart("Check The Time Out For change session setting page if Serversync popup window is open",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        clickOnChangeSessionSettings();
        assertTrue(driver.findElement(By.id("linear_layout__change_session_settings__lifetouch")).isDisplayed());
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();
        //ServersyncPopup window is open
        Assert.assertTrue(driver.findElement(By.id("textViewSessionStatusHeader")).isDisplayed());
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    // check no timeout for  change session setting page
    public void CheckNoTimeOutForChangeSessionSettingPage() throws IOException, InterruptedException {
        String case_id = "23169";

        helper.printTestStart("Check The No Time Out For change session setting page",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Set the display time out for 1 mins from admin page
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", helper.getAppString("no_timeout"));
        Log("Display time out value set as No timeout");

        // Click on lock screen button
        driver.findElement(By.id("buttonLock")).click();
        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        clickOnChangeSessionSettings();
        assertTrue(driver.findElement(By.id("linear_layout__change_session_settings__lifetouch")).isDisplayed());
        checkNoTimeout(310);

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }
    //Commenting the test as View manual vital page no longer needed as removed from the Gateway

    /*@Test
    //Check No time out View Manual Vital Display page
    public void CheckNoTimeOutForViewManualVitalDisplayPage() throws IOException, InterruptedException {
        String case_id = "23170";

        helper.printTestStart("Check No time out View Manual Vital Display page",case_id);
        //scan the admin QR code
        helper.spoofQrCode_adminUnlock();
        //set the display time out for 1 mins from admin page
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", helper.getAppString("no_timeout"));
        //Click on lock screen button
        driver.findElement(By.id("buttonLock")).click();
        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        Log("Lead to View Manual vital display page");
        checkNoTimeout(310);

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }*/
   // Commenting the test as View manual vital page no longer needed as removed from the Gateway

   /* @Test
    //Check display time out for  View Manual Vital Display page
    public void CheckDisplayTimeOutForViewManualVitalDisplayPage() throws IOException, InterruptedException {
        String case_id = "23171";

        helper.printTestStart("Check display time out for View Manual Vital Display page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        Log("Lead to View Manual vital display page");
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }
*/
    @Test
    //Check display time out for Select Annotation Entry Mode page
    public void CheckDisplayTimeOutForSelectAnnotationEntryModePage() throws IOException {
        String case_id = "23172";

        helper.printTestStart("CheckDisplayTimeOutForSelectAnnotationEntryModePage",case_id);
        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        assertTrue(driver.findElement(By.id("buttonAnnotationKeyboard")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonAnnotationPredefined")).isDisplayed());
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check No time out for Select Annotation Entry Mode page
    public void CheckNoTimeOutForSelectAnnotationEntryModePage() throws IOException, InterruptedException {
        String case_id = "23173";

        helper.printTestStart("CheckNoForSelectAnnotationEntryModePage",case_id);
        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setDisplayTimeout(false, helper.getAppString("no_timeout"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        assertTrue(driver.findElement(By.id("buttonAnnotationKeyboard")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonAnnotationPredefined")).isDisplayed());
        checkNoTimeout(310);

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check display time out for enter the Annotation time by moving the green dot.( Annotation page)
    public void CheckDisplayTimeOutForEnterTheAnnotaionTimeByMovingTheGreenDot() throws IOException {
        String case_id = "23174";
        helper.printTestStart("Check display time out for enter the Annotation time by moving the green dot",case_id);
        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        assertTrue(driver.findElement(By.id("buttonAnnotationKeyboard")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonAnnotationPredefined")).isDisplayed());
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        clickOnNext();
        checkTimeout(30);
        helper.captureScreenShot();
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check display time out for StopMonitoring current patient page
    public void CheckDisplayTimeOutForStopMonitoringCurrentPatientPage() throws IOException {
        String case_id = "23175";

        helper.printTestStart("Check display time out for StopMonitoring current patient page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        Log("Click on Stop monitorning the current session button");
        driver.findElement(By.id("buttonBigButtonThree")).click();
        assertTrue(driver.findElement(By.id("textAreYouSure")).isDisplayed());
        Log("Lead us to Are you Sure page");
        checkTimeout(30);
        helper.updateTestRail(PASSED);

    }

    @Test
    //Check NO display time out for StopMonitoring current patient page
    public void CheckNoTimeOutForStopMonitoringCurrentPatientPage() throws IOException, InterruptedException {
        String case_id = "23176";

        helper.printTestStart("Check No  display time out for StopMonitoring current patient page",case_id);

        setDisplayTimeout(false, helper.getAppString("no_timeout"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        Log("Click on Stop monitorning the current session button");
        driver.findElement(By.id("buttonBigButtonThree")).click();
        assertTrue(driver.findElement(By.id("textAreYouSure")).isDisplayed());
        Log("Lead us to Are you Sure page");
        checkNoTimeout(310);

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }

    @Test
    //Check display time out for StopMonitoring current patient page if Serevr sync popup window is open
    public void CheckTimeOutForStopMonitoringCurrentPatientPageifServerSyncPopUpWindowIsOpen() throws IOException, InterruptedException {
        String case_id = "23177";

        helper.printTestStart("Check display time out for StopMonitoring current patient page if Serevr sync popup window is open",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        clickOnBack();
        Log("Click on Stop monitorning the current session button");
        driver.findElement(By.id("buttonBigButtonThree")).click();
        assertTrue(driver.findElement(By.id("textAreYouSure")).isDisplayed());
        Log("Lead us to Are you Sure page");
        //Open teh server sync Popup window
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        Assert.assertTrue(driver.findElement(By.id("textViewSessionStatusHeader")).isDisplayed());
        checkTimeout(30);
        helper.updateTestRail(PASSED);

    }

    //Check display time out for patient vital display page .
    @Test
    public void CheckDisplayTimeOutForPatientVitalDisplayPage() throws IOException {
        String case_id = "23178";

        helper.printTestStart("Check The Time Out For PatientVitalDisplay page",case_id);

        setDisplayTimeout(true, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

       //Click on start monitoring button
        clickOnNext();
        // PatientVitaldiaplaypage
        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    //Check the display time out for patient vital page if Setup mode popup window is open
    @Test
    public void CheckDisplayTimeOutForPatientVitalDisplayPageIfSetupModePopUpWindowIsOpen() throws IOException {
        String case_id = "23179";

        helper.printTestStart("Check the display time out for patient viatal page if Setup mode popup window is open",case_id);

        setDisplayTimeout(true, helper.getAppString("two_minutes"));

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        //Click on start monitoring button
        clickOnNext();
        // PatientVitaldiaplaypage
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
       //assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        Log("Click on setupmode checkbox");
        WebElement setupmode= driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        setupmode.click();
        String time = driver.getDeviceTime();
        Log(time);
        // waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("textHeaderGuiTime")), 130);
        //uncheck the setup mode check box
        Log("Uncheck the setupmode checkbox");
        WebElement checkbox1=driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        checkbox1.click();
        // click on the setup mode blob
        Log("Click on the setup mode blob");
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("fragment_graph_lifetouch_heart_rate")), 30);
        clickonSetupmodeBlob();
        WebElement setupmodeheading=driver.findElement(By.id("textPopUpHistoricalSetupModeViewerTitle"));
        assertTrue(setupmodeheading.isDisplayed());
        Log("setupmode window open");
        //wait until display time out happen
        checkTimeout(120);
        helper.updateTestRail(PASSED);
    }

    //Check the display time out for patient vital page if  HeartRatePoincare popup window is open
    @Test
    public void CheckDisplayTimeOutForPatientVitalDisplayPageIfHeartRatePoincarePopUpWindowIsOpen() throws IOException {
        String case_id = "23180";

        helper.printTestStart("Check the display time out for patient viatal page if  HeartRatePoincare  popup window is open",case_id);

        setDisplayTimeout(true, helper.getAppString("two_minutes"));

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        //Click on start monitoring button
        clickOnNext();
        // PatientVitaldiaplaypage
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        //assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement OptionCheckbox = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());
        // Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        Log("Click on setupmode checkbox");
        WebElement Poincare= driver.findElement(By.id("checkBoxLifetouchOptionsHeartRatePoincare"));
        assertTrue(Poincare.isDisplayed());
        String isChecked = Poincare.getAttribute("checked");
        Log("Click on Poincare checkbox");
        Poincare.click();
        Log("Poincare window is open");
        assertTrue(driver.findElement(By.id("linear_layout_poincare_chart")).isDisplayed());
        //wait until display time out happen
        checkTimeout(120);
        helper.updateTestRail(PASSED);
    }

    //Check patient vital page display time out if Annotation popup window is open
    @Test
    public void CheckDisplayTimeOutForPatientVitalDisplayPageIfAnnotationWindowIsOpen() throws IOException {
        String case_id = "23181";

        helper.printTestStart("Check patient vital page display time out if Annotation popup window is open",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setDisplayTimeout(true, helper.getAppString("one_minute"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        //Click on next button from display time out for Leads to Observation page to set Time Page
        clickOnNext();
        driver.findElement(By.id("buttonVitalSignValidityTime")).click();
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

        //Click on Annotation
        driver.findElement(By.id("buttonBigButtonFive")).click();
        assertTrue(driver.findElement(By.id("buttonAnnotationKeyboard")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonAnnotationPredefined")).isDisplayed());
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        clickOnNext();
        // Click on the Next button
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
        checkTimeout(60);
        helper.captureScreenShot();
        helper.updateTestRail(PASSED);
    }

    //Check patient vital page display time out if servers sync popup window is open
    @Test
    public void CheckDisplayTimeOutForPatientVitalDisplayPageIfServersyncPopUpWindowIsOpen() throws IOException {
        String case_id = "23182";

        helper.printTestStart("Check patient vital page display time out if serevrsync popup window is open",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);
        
        setDisplayTimeout(true, helper.getAppString("one_minute"));

        setUpSessionWithAdultAgeRange();
        driver.findElement(By.id("buttonManualVitalsOnly")).click();
        //Click on next button from display time out for Leads to Observation page to set Time Page
        clickOnNext();
        driver.findElement(By.id("buttonVitalSignValidityTime")).click();
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
        // Click on patientvital display button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        //open the serevr sync popup window
        driver.findElement(By.id("linearLayoutFooterServerLink")).click();

        Assert.assertTrue(driver.findElement(By.id("textViewSessionStatusHeader")).isDisplayed());
        checkTimeout(60);
        helper.updateTestRail(PASSED);
    }


    //Check No Display timeOut for patient vital display page .
    @Test
    public void CheckNoDisplayTimeOutForPatientVitalDisplayPage() throws IOException, InterruptedException {
        String case_id = "23183";

        helper.printTestStart("Check No Display timeOut for patient vital display page",case_id);

        setDisplayTimeout(false, helper.getAppString("two_minutes"));

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        //Click on start monitoring button
        clickOnNext();
        // PatientVitaldiaplaypage
        checkNoTimeout(310);
        helper.updateTestRail(PASSED);
    }

    //Check no time out for device connection page.
    @Test
    public void CheckNoTimeOutForDeviceConnectionPage() throws IOException, InterruptedException {
        String case_id = "23184";

        helper.printTestStart("Check no time out for device connection page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        checkNoTimeout(310);
        helper.updateTestRail(PASSED);
    }

    //Check display time out on feature enable page
    @Test
    public void CheckDisplayTimeOutOnFeatureEnablePage() throws IOException {
        String case_id = "23185";

        helper.printTestStart("Check display time out on feature enable page",case_id);

        setDisplayTimeout(false, helper.getAppString("thirty_seconds"));

        helper.spoofQrCode_featureEnableUnlock();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("checkBoxEnableManuallyEnteredVitalSigns")), 30); // wait for one of the feature enable options to appear

        checkTimeout(30);
        helper.updateTestRail(PASSED);
    }

    //Check No display time out on feature enable page
    @Test
    public void CheckNoDisplayTimeOutOnFeatureEnablePage() throws IOException, InterruptedException {
        String case_id = "23186";

        helper.printTestStart("Check No display time out on feature enable page",case_id);

        setDisplayTimeout(false, helper.getAppString("no_timeout"));

        helper.spoofQrCode_featureEnableUnlock();
        checkNoTimeout(310);

        clickOnLock();
        setDisplayTimeout(false, helper.getAppString("two_minutes"));
        helper.updateTestRail(PASSED);
    }

    private void  checkTimeout(int expected_length_seconds) throws IOException
    {
        String desired_date_format = "HH:mm:ss";
        DateTime start_date = new DateTime();
        String start_time = start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Display time out start at " + start_time);

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("barCodeActivity_QrUnlock")), expected_length_seconds + 60 );
        DateTime end_date = new DateTime();
        String Timeout_end_time = end_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Display time out happen at:" + Timeout_end_time);

        // check display time out lead to scan QR code page
        Assert.assertTrue(driver.findElement(By.id("barCodeActivity_QrUnlock")).isDisplayed());

        // Find the interval for display time out time
        long time_difference = end_date.getMillis() - start_date.getMillis();
        int difference_in_seconds = (int) (time_difference / DateUtils.SECOND_IN_MILLIS);
        Log("Displaytime out period is " + difference_in_seconds + " seconds");
        // Expected the time difference to by 30 seconds +/- 10 seconds...

        int upper_bound = expected_length_seconds + 10;
        int lower_bound = expected_length_seconds - 10;
        // Compare the display time out length between actual time and set time.
        assertTrue(difference_in_seconds < upper_bound);
        assertTrue(difference_in_seconds > lower_bound);
        Log("Display time out happen as per the set time.");
    }

    public void  checkNoTimeout(int expected_length_seconds) throws IOException, InterruptedException {
        String desired_date_format = "HH:mm:ss";
        DateTime start_date = new DateTime();
        String start_time = start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Display time monitor  start at " + start_time);

        long start_millis = start_date.getMillis();
        long end_millis = start_millis + (expected_length_seconds*DateUtils.SECOND_IN_MILLIS);
        long current_millis = start_millis;
        while(current_millis < end_millis)
        {
            Thread.sleep(15000);

            ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("textPleaseScanIdQrCode"))).apply(driver);
            ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("buttonUnlockQrCodeScanScreen"))).apply(driver);

            current_millis = new DateTime().getMillis();

            long seconds_elapsed = (current_millis - start_millis) / DateUtils.SECOND_IN_MILLIS;

            Log("No display time out after " + seconds_elapsed + " seconds");
        }

        DateTime end_date = new DateTime();
        String Timeout_end_time = end_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Stop Display time monitor at:" + Timeout_end_time);
    }

    private void clickonSetupmodeBlob()
    {
        TouchAction click = new TouchAction(driver);
        click.tap(PointOption.point(1690, 135)).perform();
        //click.tap(PointOption.point(width,hightfinal)).perform();
    }

    private void clickonAnnotationBlob()
    {
        TouchAction click = new TouchAction(driver);
        click.tap(PointOption.point(1690, 135)).perform();
        //click.tap(PointOption.point(width,hightfinal)).perform();
    }


    private void setDisplayTimeout(boolean timeout_on_patient_vitals, String timeout_time) throws IOException
    {
        // Scan the Admin QR code (spoof via an intent) and then click on the Gateway Settings tab on the Admin page
        UnlockWithAdminQrCodeAndShowGatewaySettings();

        WebElement checkbox_Usedisplaytimeout_onchatpage = driver.findElement(By.id("checkBoxApplyDisplayTimeoutToPatientVitalsDisplay"));

        boolean use_display_timeout = checkbox_Usedisplaytimeout_onchatpage.getAttribute("checked").equals("true");

        Log("Use display timeout on chart page status is " + use_display_timeout);
        if (use_display_timeout != timeout_on_patient_vitals)
        {
            checkbox_Usedisplaytimeout_onchatpage.click();
            Log("Use display timeout on chart page changed");
        }
        else
        {
            Log("Use display timeout on chart page already correct");
        }

        //set the display time out as No time out from Admin page
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", timeout_time);

        clickOnLock();
    }
}
