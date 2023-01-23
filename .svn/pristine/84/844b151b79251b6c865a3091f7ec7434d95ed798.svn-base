package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;

public class ModeSelectionPage extends AppiumTest
{
    @Test
    public void TestTheAppearanceOfCorrectButtonsBeforeSessionStarted() throws IOException
    {
        String case_id = "23187";

        helper.printTestStart("TestTheAppearanceOfCorrectButtonsBeforeSessionStarted",case_id);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();

        // wait for mode selection to show - should always be at least one button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonBigButtonOne")), 30);

        // check number of buttons
        WebElement main_screen = driver.findElement(By.id("fragment_main"));
        List<WebElement> buttons = main_screen.findElements(By.className("android.widget.Button"));
        int numButtons = buttons.size();
        Log("Number of big button display on the modeselection page is:" + numButtons);

        assertEquals(3, numButtons);

        //Check the appearance of  Start monitoring a patient button
        WebElement start_monitoring = driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(start_monitoring.isDisplayed());
        Log("Big button one is called: " + start_monitoring.getText());

        assertEquals(helper.getAppString("textStartMonitoringAPatient"), start_monitoring.getText());

        //Check the appearance of Device Status button
        WebElement device_status = driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(device_status.isDisplayed());
        Log("Big button two is called: " + device_status.getText());

        assertEquals(helper.getAppString("textCheckDeviceStatus"), device_status.getText());

        //Check the appearance of Device Status button
        WebElement web_pages = driver.findElement(By.id("buttonBigButtonThree"));
        assertTrue(web_pages.isDisplayed());
        Log("Big button three is called: " + web_pages.getText());

        assertEquals(helper.getAppString("view_webpages"), web_pages.getText());

        helper.updateTestRail(PASSED);
    }


    @Test
    public void checkStartMonitoringAPatientFunctionality() throws IOException
    {
        String case_id = "23188";

        helper.printTestStart("checkStartMonitoringAPatientFunctionality",case_id);
        //Start a session with non adult age range
        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Check the appearance of  Start monitoring a patient button
        WebElement StartMonitoringApatient= driver.findElement(By.id("buttonBigButtonOne"));

        waitUntil(ExpectedConditions.elementToBeClickable(StartMonitoringApatient), 120);

        Log("Click on the StartMonitoring Button");
        StartMonitoringApatient.click();
        WebElement PatientId= driver.findElementById("labelPatientID");
        assertTrue(PatientId.isDisplayed());
        Log("Lead us to the Correct page");

        helper.setPatientId("not used");

        //Check the appearance of Restart button
        WebElement RestartSessionButton= driver.findElement(By.id("buttonBack"));
        assertTrue(RestartSessionButton.isDisplayed());
        Log("Click on the "+RestartSessionButton.getText()+" Button");
        RestartSessionButton.click();

        StartMonitoringApatient= driver.findElement(By.id("buttonBigButtonOne"));
        WebElement CheckDevicestatusButton= driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(StartMonitoringApatient.isDisplayed());
        assertTrue(CheckDevicestatusButton.isDisplayed());
        Log("Lead us back to the starting page with two big blue button ");
        helper.updateTestRail(PASSED);
    }


    @Test
    public void checkDeviceStatusPageAppearance() throws IOException
    {
        String case_id = "23189";

        helper.printTestStart("checkDeviceStatusPageAppearance",case_id);
        //scan User Qr code
        helper.spoofQrCode_userUnlock();

        //Check the appearance of Device Status button
        WebElement CheckDevicestatusButton= driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(CheckDevicestatusButton.isDisplayed());
        Log("Big button two is called: "+CheckDevicestatusButton.getText());
        Log("Click on the Check Device Status button");
        CheckDevicestatusButton.click();
        //lead us to the Check Device status page
        WebElement CheckDeviceStatusPage= driver.findElement(By.id("barCodeActivity"));
        assertTrue(CheckDeviceStatusPage.isDisplayed());
        //Check teh appearance of scanner camera
        WebElement scanner= driver.findElement(By.id("check_device_status_qr_bar_code"));
        assertTrue(scanner.isDisplayed());
        Log("Status for presence of Qr scanner is :"+scanner.isDisplayed());
        //Check the appearance of text on the page
        String ActualText=helper.findTextViewElementWithString("check_device_status").getText();
        String Expected=helper.getAppString("textCheckDeviceStatus");
        assertEquals(Expected,ActualText);
        Log("Heading : "+ActualText);
        //Check the text appear for device help
        WebElement DevicesPageHelp= driver.findElementById("textAddDevicesPageHelp");
        assertTrue(DevicesPageHelp.isDisplayed());
        String ActualtextAddDevicesPageHelp=DevicesPageHelp.getText();
        String ExpectedtextAddDevicesPageHelp= helper.getAppString("hold_qr_code_so_it_fits_in_the_smaller_lighter_square_on_the_left");
        assertEquals(ExpectedtextAddDevicesPageHelp,ActualtextAddDevicesPageHelp);
        Log("Help Text : "+ActualtextAddDevicesPageHelp);
        //Check the appearance of back button
        WebElement BackButton= driver.findElement(By.id("buttonBack"));
        assertTrue(BackButton.isDisplayed());
        Log("Appearance of Back button is "+ BackButton.isDisplayed());
        //Click on the back button
        Log("Click on the back button");
        BackButton.click();
        WebElement StartMonitoringApatient= driver.findElement(By.id("buttonBigButtonOne"));
        CheckDevicestatusButton= driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(StartMonitoringApatient.isDisplayed());
        assertTrue(CheckDevicestatusButton.isDisplayed());
        Log("Lead us to the starting page with two big blue button ");
        helper.updateTestRail(PASSED);
    }

 //Removing this test as now we have a separate test for CheckDevicesStatus page.
   /* @Test
    public void checkDeviceStatusPageContentsAndFunctionality() throws IOException
    {
        String case_id = "23190";

        helper.printTestStart("checkDeviceStatusPageContentsAndFunctionality",case_id);
        //scan User Qr code
        helper.spoofQrCode_userUnlock();

        //Check the appearance of Device Status button
        WebElement CheckDevicestatusButton= driver.findElement(By.id("buttonBigButtonTwo"));
        Log("Click on the Check Device Status button");
        CheckDevicestatusButton.click();

        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        Log("Lifetouch status is: ");
        checkDeviceStatus_NotInUse(helper.getAppString("textLifetouch"), "1111");

        //Scan a Lifetemp
        helper.spoofQrCode_scanDummyDataLifetemp();
        Log("Lifetemp status is:");
        checkDeviceStatus_NotInUse(helper.getAppString("textLifetemp"), "2222");

        //Scan a Nonin
        helper.spoofQrCode_scanDummyDataNonin();
        Log("Nonin status is:");
        checkDeviceStatus_NotInUse(helper.getAppString("textNonin"), "3333");

        //Scan AnDBp
        helper.spoofQrCode_scanDummyDataAnD();
        Log("AnD status is:");
        checkDeviceStatus_NotInUse("A&D UA767", "4444"); // Not in app strings

        //Scan TM 2441
        helper.spoofQrCode_scanDummyDataAnD_TM2441();
        Log("TM2441 status is:");
        checkDeviceStatus_NotInUse("A&D TM2441", "4444"); // Not in app strings

        helper.updateTestRail(PASSED);
    }*/


    @Test
    public void correctButtonsShowWithManualVitals() throws IOException
    {
        String case_id = "23191";

        helper.printTestStart("correctButtonsShowWithManualVitalsAndVideoCallsEnabled", case_id);

        checkFeaturesEnabled(true, false);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        // patient vital sign button
        WebElement PatientVitalDisplay=driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(PatientVitalDisplay.isDisplayed());
        Log("Big button one is called : "+PatientVitalDisplay.getText());
        //ChangeSessionSetting
        WebElement ChangeSessionSettings=driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(ChangeSessionSettings.isDisplayed());
        Log("Big button Two is called : "+ChangeSessionSettings.getText());
        //StopMonitoringCurrentPatient
        WebElement StopMonitoringCurrentPatient=driver.findElement(By.id("buttonBigButtonThree"));
        assertTrue(StopMonitoringCurrentPatient.isDisplayed());
        Log("Big button Three is called : "+StopMonitoringCurrentPatient.getText());
        //Manual vitalSign Entry
        WebElement ManualVitalSignEntry=driver.findElement(By.id("buttonBigButtonFour"));
        assertTrue(ManualVitalSignEntry.isDisplayed());
        Log("Big button Four is called : "+ManualVitalSignEntry.getText());
        //Commenting the ViewManuallyEnteredVitalSigns part as its Removed from mode selection page
       /* //ViewManuallyEnteredVitalSigns
        WebElement ViewManuallyEnteredVitalSigns=driver.findElement(By.id("buttonBigButtonFive"));
        assertTrue(ViewManuallyEnteredVitalSigns.isDisplayed());
        Log("Big button Five is called : "+ViewManuallyEnteredVitalSigns.getText());*/
        //Annotations
        WebElement Annotations=driver.findElement(By.id("buttonBigButtonFive"));
        assertTrue(Annotations.isDisplayed());
        Log("Big button Five is called : "+Annotations.getText());

        WebElement ButtonViewWebpages = driver.findElement(By.id("buttonBigButtonSix"));
        assertTrue(ButtonViewWebpages.isDisplayed());
        Log("Big button Six is called : " + ButtonViewWebpages.getText());

        //restoreDefaultFeatures();

        helper.updateTestRail(PASSED);
    }


    @Test
    public void checkAllButtonsDuringSession() throws IOException
    {
        String case_id = "23192";

        helper.printTestStart("checkAllButtonsDuringSession",case_id);

        checkFeaturesEnabled(true, false);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");

        // wait for mode selection to show - should always be at least one button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonBigButtonOne")), 30);

        WebElement main_screen = driver.findElement(By.id("fragment_main"));
        List<WebElement> buttons = main_screen.findElements(By.className("android.widget.Button"));
        int numButtons = buttons.size();
        Log("Number of big button display on the modeselection page is  is :" + numButtons);

        assertEquals(6, numButtons);


        // patient vital sign button
        WebElement PatientVitalDisplay=driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(PatientVitalDisplay.isDisplayed());
        Log("Big button one is called : "+PatientVitalDisplay.getText());
        assertEquals(helper.getAppString("textPatientVitalsDisplay"), PatientVitalDisplay.getText());

        PatientVitalDisplay.click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        assertEquals(no_devices_message, message_displayed);
        Log("Big button one is  clickable");

        //click on lock screen button
        driver.findElementById("buttonLock").click();
        helper.spoofQrCode_userUnlock();

        //ChangeSessionSetting
        WebElement ChangeSessionSettings=driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(ChangeSessionSettings.isDisplayed());
        Log("Big button Two is called : "+ChangeSessionSettings.getText());
        assertEquals(helper.getAppString("textChangeSessionSettingsMultiLine"), ChangeSessionSettings.getText());

        ChangeSessionSettings.click();
        assertTrue(driver.findElement(By.id("linear_layout__change_session_settings__lifetouch")).isDisplayed());
        Log("Big button Two is  clickable");
        //click on back button
        driver.findElementById("buttonBack").click();

        //StopMonitoringCurrentPatient
        WebElement StopMonitoringCurrentPatient=driver.findElement(By.id("buttonBigButtonThree"));
        assertTrue(StopMonitoringCurrentPatient.isDisplayed());
        Log("Big button Three is called : "+StopMonitoringCurrentPatient.getText());
        assertEquals(helper.getAppString("textStopMonitoringCurrentPatient"), StopMonitoringCurrentPatient.getText());

        StopMonitoringCurrentPatient.click();
        assertTrue(driver.findElement(By.id("textAreYouSure")).isDisplayed());
        Log("Big button Three is  clickable");
        //click on back button
        driver.findElementById("buttonBack").click();

        //Manual vitalSign Entry
        WebElement ManualVitalSignEntry=driver.findElement(By.id("buttonBigButtonFour"));
        assertTrue(ManualVitalSignEntry.isDisplayed());
        Log("Big button Four is called : "+ManualVitalSignEntry.getText());
        assertEquals(helper.getAppString("textManualVitalSignEntry"), ManualVitalSignEntry.getText());

        ManualVitalSignEntry.click();
        assertTrue(driver.findElement(By.id("textTop")).isDisplayed());
        Log("Big button four is  clickable");
        //click on back button
        driver.findElementById("buttonBack").click();

        //Annotations
        WebElement Annotations=driver.findElement(By.id("buttonBigButtonFive"));
        assertTrue(Annotations.isDisplayed());
        Log("Big button Five is called : "+Annotations.getText());
        assertEquals(helper.getAppString("annotations"), Annotations.getText());

        Annotations.click();
        assertTrue(driver.findElement(By.id("textTop")).isDisplayed());
        Log("Big button five is  clickable");
        //click on back button
        driver.findElementById("buttonBack").click();

        // View Web Pages
        WebElement ButtonViewWebpages = driver.findElement(By.id("buttonBigButtonSix"));
        assertTrue(ButtonViewWebpages.isDisplayed());
        Log("Big button Six is called : " + ButtonViewWebpages.getText());
        assertEquals(helper.getAppString("view_webpages"), ButtonViewWebpages.getText());

        ButtonViewWebpages.click();

        assertTrue(driver.findElement(By.id("linearLayoutWebpage")).isDisplayed());
        Log("Big button six is  clickable");
        //click on back button
        driver.findElementById("buttonBack").click();

        //ViewManuallyEnteredVitalSigns - REMOVED
//        WebElement ViewManuallyEnteredVitalSigns=driver.findElement(By.id("buttonBigButtonFive"));
//        assertTrue(ViewManuallyEnteredVitalSigns.isDisplayed());
//        Log("Big button Five is called : "+ViewManuallyEnteredVitalSigns.getText());
//        ViewManuallyEnteredVitalSigns.click();
//        assertTrue(driver.findElement(By.id("linearLayoutManualVitalsSignSelectionText")).isDisplayed());
//        Log("Big button five is  clickable");
//        //click on back button
//        driver.findElementById("buttonBack").click();

      /*  // Video Call
        WebElement videoCall = driver.findElement(By.id("buttonBigButtonSix"));
        assertTrue(videoCall.isDisplayed());
        Log("Big button Six is called : "+videoCall.getText());
        videoCall.click();
        assertTrue(driver.findElement(By.id("imageVideoCall")).isDisplayed());
        Log("Big button Six is  clickable");
        //click on back button
        driver.findElementById("buttonBack").click();

        restoreDefaultFeatures();*/

        helper.updateTestRail(PASSED);
    }
  // Commenting this as this test case is usefull if we are video call enable button(not part of this release)
    /*@Test
    public void checkCorrectButtonsShowWithManualVitalsOnly() throws IOException {
        String case_id = "null";

        helper.printTestStart("checkCorrectButtonsShowWithManualVitalsOnly",case_id);

        checkFeaturesEnabled(true, false);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");

        // wait for mode selection to show - should always be at least one button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonBigButtonOne")), 30);

        WebElement main_screen = driver.findElement(By.id("fragment_main"));
        List<WebElement> buttons = main_screen.findElements(By.className("android.widget.Button"));
        int numButtons = buttons.size();
        Log("Number of big button display on the modeselection page is  is :" + numButtons);

        assertEquals(5, numButtons);

        // patient vital sign button
        WebElement patient_vitals = driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(patient_vitals.isDisplayed());
        Log("Big button one is called : " + patient_vitals.getText());
        assertEquals(helper.getAppString("textPatientVitalsDisplay"), patient_vitals.getText());


        //ChangeSessionSetting
        WebElement change_session = driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(change_session.isDisplayed());
        Log("Big button Two is called : " + change_session.getText());
        assertEquals(helper.getAppString("textChangeSessionSettings"), change_session.getText());


        //StopMonitoringCurrentPatient
        WebElement stop_monitoring = driver.findElement(By.id("buttonBigButtonThree"));
        assertTrue(stop_monitoring.isDisplayed());
        Log("Big button Three is called : " + stop_monitoring.getText());
        assertEquals(helper.getAppString("textStopMonitoringCurrentPatient"), stop_monitoring.getText());


        //Manual vitalSign Entry
        WebElement ManualVitalSignEntry=driver.findElement(By.id("buttonBigButtonFour"));
        assertTrue(ManualVitalSignEntry.isDisplayed());
        Log("Big button Four is called : "+ManualVitalSignEntry.getText());
        assertEquals(helper.getAppString("textManualVitalSignEntry"), ManualVitalSignEntry.getText());


        //Annotations
        WebElement Annotations=driver.findElement(By.id("buttonBigButtonFive"));
        assertTrue(Annotations.isDisplayed());
        Log("Big button Five is called : " + Annotations.getText());
        assertEquals(helper.getAppString("annotations"), Annotations.getText());


        restoreDefaultFeatures();

        helper.updateTestRail(PASSED);
    }*/
    //Commenting this as not part of this release
    /*@Test
    public void checkCorrectButtonsShowWithVideoCallsOnly() throws IOException {
        String case_id = "null";

        helper.printTestStart("checkCorrectButtonsShowWithManualVitalsOnly",case_id);

        checkFeaturesEnabled(false, true);

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        Log("Start monitoring button appear after all devices got connected.");
        clickOnNext();

        //click on lock screen button
        driver.findElementById("buttonLock").click();
        helper.spoofQrCode_userUnlock();
        Log("Lead us to Mode selection page ");

        // wait for mode selection to show - should always be at least one button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonBigButtonOne")), 30);

        WebElement main_screen = driver.findElement(By.id("fragment_main"));
        List<WebElement> buttons = main_screen.findElements(By.className("android.widget.Button"));
        int numButtons = buttons.size();
        Log("Number of big button display on the modeselection page is :" + numButtons);

        assertEquals(4, numButtons);

        // patient vital sign button
        WebElement patient_vitals = driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(patient_vitals.isDisplayed());
        Log("Big button one is called : " + patient_vitals.getText());
        assertEquals(helper.getAppString("textPatientVitalsDisplay"), patient_vitals.getText());


        //ChangeSessionSetting
        WebElement change_session = driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(change_session.isDisplayed());
        Log("Big button Two is called : " + change_session.getText());
        assertEquals(helper.getAppString("textChangeSessionSettings"), change_session.getText());


        //StopMonitoringCurrentPatient
        WebElement stop_monitoring = driver.findElement(By.id("buttonBigButtonThree"));
        assertTrue(stop_monitoring.isDisplayed());
        Log("Big button Three is called : " + stop_monitoring.getText());
        assertEquals(helper.getAppString("textStopMonitoringCurrentPatient"), stop_monitoring.getText());


        // Video Call
        WebElement videoCall = driver.findElement(By.id("buttonBigButtonFour"));
        assertTrue(videoCall.isDisplayed());
        Log("Big button Four is called : " + videoCall.getText());
        assertEquals(helper.getAppString("video_call"), videoCall.getText());


        restoreDefaultFeatures();

        helper.updateTestRail(PASSED);
    }
*/

    @Test
    public void correctButtonsShowWithManualVitalsAndVideoCallsDisabled() throws IOException
    {
        String case_id = "23193";

        helper.printTestStart("correctButtonsShowWithManualVitalsAndVideoCallsDisabled",case_id);

        checkFeaturesEnabled(false, false);
        
        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        Log("Start monitoring button appear after all devices got connected.");
        clickOnNext();

        //click on lock screen button
        driver.findElementById("buttonLock").click();
        helper.spoofQrCode_userUnlock();
        Log("Lead us to Mode selection page ");

        // wait for mode selection to show - should always be at least one button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonBigButtonOne")), 30);

        WebElement main_screen = driver.findElement(By.id("fragment_main"));
        List<WebElement> buttons = main_screen.findElements(By.className("android.widget.Button"));
        int numButtons = buttons.size();
        Log("Number of big button display on the modeselection page is  is :" + numButtons);

        assertEquals(4, numButtons);

        // patient vital sign button
        WebElement patient_vitals = driver.findElement(By.id("buttonBigButtonOne"));
        assertTrue(patient_vitals.isDisplayed());
        Log("Big button one is called : " + patient_vitals.getText());
        assertEquals(helper.getAppString("textPatientVitalsDisplay"), patient_vitals.getText());


        //ChangeSessionSetting
        WebElement change_session = driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(change_session.isDisplayed());
        Log("Big button Two is called : " + change_session.getText());
        assertEquals(helper.getAppString("textChangeSessionSettings"), change_session.getText());


        //StopMonitoringCurrentPatient
        WebElement stop_monitoring = driver.findElement(By.id("buttonBigButtonThree"));
        assertTrue(stop_monitoring.isDisplayed());
        Log("Big button Three is called : " + stop_monitoring.getText());
        assertEquals(helper.getAppString("textStopMonitoringCurrentPatient"), stop_monitoring.getText());


        //View Web Pages
        WebElement view_webpages = driver.findElement(By.id("buttonBigButtonFour"));
        assertTrue(view_webpages.isDisplayed());
        Log("Big button Four is called : " + view_webpages.getText());
        assertEquals(helper.getAppString("view_webpages"), view_webpages.getText());


        restoreDefaultFeatures();

        helper.updateTestRail(PASSED);
    }


    public void checkDeviceStatus_NotInUse(String type, String id)
    {
        //Label
        WebElement DeviceTypeLabel= driver.findElement(By.id("textViewCheckDeviceStatusDeviceTypeLabel"));
        assertTrue(DeviceTypeLabel.isDisplayed());
        //Device type
        WebElement DeviceTypeValue= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        assertTrue(DeviceTypeValue.isDisplayed());
        Log(DeviceTypeLabel.getText()+ "  "+DeviceTypeValue.getText());

        assertEquals(type, DeviceTypeValue.getText());

        //Serial number
        WebElement SNLabel= driver.findElement(By.id("textViewCheckDeviceStatusDeviceHumanReadableSerialNumberLabel"));
        assertTrue(SNLabel.isDisplayed());
        WebElement SNValue= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        assertTrue(SNValue.isDisplayed());
        Log(SNLabel.getText()+ "  "+SNValue.getText());

        assertEquals(id, SNValue.getText());

        WebElement status = driver.findElement(By.id("textViewDeviceUseStatus"));
        assertTrue(status.isDisplayed());
        Log("Device Status is: " + status.getText());

        assertEquals(helper.getAppString("device_not_in_use"), status.getText());
    }


    private void restoreDefaultFeatures() throws IOException
    {
        driver.findElementById("buttonLock").click();

        checkFeaturesEnabled(true, false);
    }


    private void checkFeaturesEnabled(boolean manual_vitals_enabled, boolean video_calls_enabled) throws IOException
    {
        helper.spoofQrCode_featureEnableUnlock();

        // Re-enable manual vitals
        WebElement manual_vitals = driver.findElement(By.id("checkBoxEnableManuallyEnteredVitalSigns"));
        String isChecked = manual_vitals.getAttribute("checked");
        Log("Enable Manually  Entered Vital Signs Check box Status " + isChecked);


        if(isChecked.equals("true") == manual_vitals_enabled)
        {
            Log("Manually  Entered Vital Signs in desired state");
        }
        else
        {
            Log("Manually  Entered Vital Signs state changed");
            manual_vitals.click();
        }

        // Re-enable video calls
        WebElement video_calls = driver.findElement(By.id("checkBoxEnableVideoCalls"));
        isChecked = video_calls.getAttribute("checked");
        Log("Enable Video Calls Check box Status " + isChecked);

        if(isChecked.equals("true") == video_calls_enabled)
        {
            Log("Video Calls in desired state");
        }
        else
        {
            video_calls.click();
            Log("Video Calls state changed");
        }

        //Click on lock screen button
        driver.findElementById("buttonLock").click();
    }
}
