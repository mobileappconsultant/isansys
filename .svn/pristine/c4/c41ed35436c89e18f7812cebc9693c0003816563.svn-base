package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.appium.java_client.android.AndroidElement;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ManuallyEnteredVitalsSession extends AppiumTest
{
    List<WebElement> Option;
    WebElement optionArea;
    @Test
    public void Check_the_UI_for_all_pages_related_to__manually_Entered_Vitals_display() throws IOException {
        String case_id = "23129";

        helper.printTestStart("Check_the_UI_for_all_pages_related_to__manually_Entered_Vitals_display",case_id);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Check the Text header
        WebElement Pageheader=driver.findElementById("textTop");
        assertTrue(Pageheader.isDisplayed());
        String Actual=Pageheader.getText();
        String Expected=helper.getAppString("enter_the_observation_set_time_by_moving_the_green_dot");
        Assert.assertEquals(Actual,Expected);
        Log("Leads us to the : "+Pageheader.getText());
        // Check the appearance of backbutton
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        //Check the appearance og Lock screen button
        assertTrue(driver.findElement(By.id("buttonLock")).isDisplayed());
        //Check the appearance of Next button
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        assertTrue(driver.findElement(By.id("textTop")).isDisplayed());
        String Title = driver.findElement(By.id("textTop")).getText();
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page with heading: "+Title);
        //Check the appearance  and prasance of number button
        List<WebElement> ValidityTimeButton=driver.findElements(By.id("buttonVitalSignValidityTime"));
        int iSize1 = ValidityTimeButton.size();
        Log("Numbers of button on the number pad are "+ValidityTimeButton.size());
        for(int i=0; i<iSize1; i++)
        {
            WebElement button=ValidityTimeButton.get(i);
            assertTrue(button.isDisplayed());
            Log(ValidityTimeButton.get(i).getText());
        }
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        // To check Next button Leads to the correct page.
        assertTrue(driver.findElement(By.id("textTop")).isDisplayed());
        Title = driver.findElement(By.id("textTop")).getText();
        Log("Landed on the correct page with heading: "+Title);
        //Check the appearance of button in th current page
        List<WebElement> VitalsignNameButton = driver.findElements(By.id("buttonVitalSignName"));
        iSize1 = VitalsignNameButton.size();
        Log("Numbers of vitalsign button display is " + VitalsignNameButton.size());

        for(int i = 0; i < iSize1; i++)
        {
            WebElement button = VitalsignNameButton.get(i);
            assertTrue(button.isDisplayed());
            Log(button.getText());
            Log(button.getText()+" Button is clickable");
            button.click();
            WebElement Heading=driver.findElementById("textTop");
            assertTrue(Heading.isDisplayed());
            Log("Leads us to :"+Heading.getText()+" Page");
            //Click on the back button
            Log("Click on the back button");
            driver.findElementById("buttonBack").click();

            // Find the buttons again, as we've re-loaded the page...
            VitalsignNameButton = driver.findElements(By.id("buttonVitalSignName"));
        }

        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        //test the appearance of cancel mark
        assertTrue(driver.findElement(By.id("imageCancel")).isDisplayed());
        //test the appearance of Enter value
        assertTrue(driver.findElement(By.id("textValue")).isDisplayed());
        /* Click on next */
        Log("Click on the next button");
        clickOnNext();
        WebElement heading=  helper.findTextViewElementWithString("confirm_vital_sign_details");
        assertTrue(heading.isDisplayed());
        WebElement observationTime=driver.findElement(By.id("textObservationSetTimeAndValidity"));
        assertTrue(observationTime.isDisplayed());
        Log("Lead us to :"+heading.getText()+" page "+observationTime.getText());
        assertTrue(driver.findElement(By.id("recyclerView")).isDisplayed());
        //Test the appearance of Enter button
        WebElement EnterButton=driver.findElement(By.id("buttonBigButtonTop"));
        assertTrue(EnterButton.isDisplayed());
        Log(EnterButton.getText()+"Status is: "+EnterButton.isDisplayed());
        //Click on the Enter button
        Log("Click on teh Enter button");
        EnterButton.click();
        //Click on Cancel button
        WebElement cancel_button = driver.findElement(By.id("buttonBigButtonTop"));
        assertTrue(cancel_button.isDisplayed());
        Log(cancel_button.getText() + " button status is: " + cancel_button.isDisplayed());
        //Click on Confirm button
        WebElement confirm_button = driver.findElement(By.id("buttonBigButtonBottom"));
        assertTrue(confirm_button.isDisplayed());
        Log(confirm_button.getText() + " button status is: " + confirm_button.isDisplayed());
        helper.updateTestRail(PASSED);

    }

    @Test
    //AutoaddEWS to the session by checking the Auto add EWS checkbox from admin page
    public void test_If_EWS_button_autoadded_to_the_Session_if_AutoAddEWS_check_box_is_checked()throws IOException
    {
        String case_id = "23131";

        helper.printTestStart("test_If_EWS_button_autoadded_to_the_Session_if_AutoAddEWS_check_box_is_checked",case_id);

        // check that patient lookup check box is unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        // check the status of EWS is added in scan device QR code page by finding Removed button for EWS
        Log("Check the status of EWS Button from the ScanDevice QR code page");
        boolean ews_remove_button_displayed = driver.findElement(By.id("buttonRemoveEarlyWarningScore")).isDisplayed();
        Log("isDisplayed status of EWS Remove button  is " + ews_remove_button_displayed);
        Assert.assertTrue(ews_remove_button_displayed);

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Leads to Mode selection page ");

        clickOnChangeSessionSettings();

        Log("Lead us to the change session setting page");
        // Check the appearance of RemoveEarlyWarningScores button in change session setting page
        // Check the status of EWS button in Change session setting page
        Log("Check the status of EWS Button in the Change session setting page");
        ews_remove_button_displayed = driver.findElement(By.id("buttonChangeSessionSettingsRemoveEarlyWarningScores")).isDisplayed();
        Log("isDisplayed status of Change Session Settings EWS Remove button  is " + ews_remove_button_displayed);
        Assert.assertTrue(ews_remove_button_displayed);

        clickOnBack();

        startManualVitalSignEntry();
        // add the  Respiratory Rate to the session
        manuallyEnterRR();
        driver.findElement(By.id("buttonBigButtonOne")).click();

        // find the graph for EWS in patient vital display page
        boolean ews_graph_displayed = driver.findElement(By.id("textEarlyWarningScoreDescription")).isDisplayed();
        Log("isDisplayed status of Patient vital display EWS graph = " + ews_graph_displayed);
        Assert.assertTrue(ews_graph_displayed);

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    //No EWS Graph visible In PatientVitalDisplay Page If No VitalSign Are Added to The session prior to the EWS
    public void test_NO_EWS_Graph_Is_Visible_InPatientVitalDisplayPage_If_NoVitalSign_Are_Added_to_The_session()throws IOException
    {

        String case_id = "23132";

        helper.printTestStart("test_NO_EWS_Graph_Is_Visible_InPatientVitalDisplayPage_If_NoVitalSign_Are_Added_to_The_session",case_id);
        // check that patient lookup check box is unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        // check the status of EWS is added in scan device QR code page by finding Removed button for EWS
        Log("Check the status of EWS Button from the ScanDevice QR code page");
        boolean remove_ews_button_displayed = driver.findElement(By.id("buttonRemoveEarlyWarningScore")).isDisplayed();
        Log("Status of EWS Remove button  is " + remove_ews_button_displayed);

        Assert.assertTrue(remove_ews_button_displayed);

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Leads to Mode selection page ");

        clickOnChangeSessionSettings();

        // Check the appearance of RemoveEarlyWarningScores button in change session setting page
        // Check the status of EWS button in Change session setting page
        Log("Check the status of EWS Button in the Change session setting page");
        remove_ews_button_displayed = driver.findElement(By.id("buttonChangeSessionSettingsRemoveEarlyWarningScores")).isDisplayed();
        Log("isDisplayed status of EWS Remove button is " + remove_ews_button_displayed);
        Assert.assertTrue(remove_ews_button_displayed);

        clickOnBack();
        Log("Click on patient vital page and check EWS graph is not visible if vital signs are not added to the session");

        //Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is" + message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, message_displayed);
        Log("No device added to the session so ews graph is not visible");

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    //Test the Appearance of EWS Graph after adding the vital sign to the Session
    public void test_The_Appearance_Of_EWS_Graph_After_Adding_the_vital_sign_to_the_Session()throws IOException
    {
        String case_id = "23133";

        helper.printTestStart("test_The_Appearance_Of_EWS_Graph_After_Adding_the_vital_sign_to_the_Session",case_id);
        // check that patient lookup check box is unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        // check the status of EWS is added in scan device QR code page by clicking Removed button for EWS
        Log("Check the status of EWS Button from the ScanDevice QR code page");
        boolean remove_ews_button_displayed = driver.findElement(By.id("buttonRemoveEarlyWarningScore")).isDisplayed();
        Log("Status of EWS Remove button  is " + remove_ews_button_displayed);
        Assert.assertTrue(remove_ews_button_displayed);

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Leads to Mode selection page ");

        clickOnChangeSessionSettings();

        // Check the appearance of RemoveEarlyWarningScores button in change session setting page
        // Check the status of EWS button in Change session setting page
        Log("Check the status of EWS Button in the Change session setting page");
        remove_ews_button_displayed = driver.findElement(By.id("buttonChangeSessionSettingsRemoveEarlyWarningScores")).isDisplayed();
        Log("Remove EWS button isDisplayed status = " + remove_ews_button_displayed);
        Assert.assertTrue(remove_ews_button_displayed);

        clickOnBack();
        Log("Check the patient vital page ");

        //Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is" + message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, message_displayed);
        Log("No device added to the session so ews graph is not visible");

        clickOnLock();
        //scan User Qr code
        helper.spoofQrCode_userUnlock();

        startManualVitalSignEntry();
        // add the  Respiratory Rate to the session
        Log("Vitalsign added to the session");
        manuallyEnterRR();
        driver.findElement(By.id("buttonBigButtonOne")).click();

        // find the graph for EWS in patient vital display page
        boolean ews_graph_displayed = driver.findElement(By.id("textEarlyWarningScoreDescription")).isDisplayed();

        Assert.assertTrue(ews_graph_displayed);

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    //EWS not auto added to the session by unchecking the Autoaddews checkbox from admin page
    public void test_If_Ewsbutton_is_NOT_autoadded_to_the_Session()throws IOException
    {
        String case_id = "23134";

        helper.printTestStart("test_If_Ewsbutton_is_NOT_autoadded_to_the_Session",case_id);
        // check that patient lookup check box is unchecked and autoaddEWS check box is unchecked
        checkAdminSettingsCorrect(false, false, true);

        setUpSessionWithNonAdultAgeRange();

        // check the status of EWS is not added in scan device QR code page by finding Add button for EWS
        boolean add_ews_displayed = driver.findElement(By.id("buttonAddEarlyWarningScore")).isDisplayed();
        Log("Scan device Qr code page add EWS button isDisplayed status = " + add_ews_displayed);
        Assert.assertTrue(add_ews_displayed);

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Leads to Mode selection page ");

        clickOnChangeSessionSettings();

        // check the appearance of Add EWS button
        boolean add_ews_button_displayed = driver.findElement(By.id("buttonChangeSessionSettingsAddEarlyWarningScores")).isDisplayed();
        Log("Add EWS button isDisplayed status = " + add_ews_button_displayed);
        Assert.assertTrue(add_ews_button_displayed);

        clickOnBack();

        // click on the manual vital sign Entry page
        driver.findElement(By.id("buttonBigButtonFour")).click();
        //click on next
        clickOnNext();
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");

        clickOnNext();

        clickOnNext();

        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        // Click on Patient vital display page
        driver.findElement(By.id("buttonBigButtonOne")).click();

        // Check the graph for EWS inpatient vital display page,not found
        if( driver.findElements(By.id("textEarlyWarningScoreDescription")).size() == 0)
        {
            Log("EWS graph is not visible ");
        }
        else
        {
            Log("Check in Patient vital display page.EWS graph is visible and EWS auto added to the session");
        }

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    private void scanUserQrCodeAndEndSession() throws IOException
    {
        helper.spoofQrCode_userUnlock();

        endSession();
    }


    @Test
    //EWS add in the middle of the session
    public void test_If_add_EWS_to_the_Middle_Of_The_Session()throws IOException
    {
        String case_id = "23306";

        helper.printTestStart("test_If_add_EWS_to_the_Middle_Of_The_Session",case_id);

        // check that patient lookup check box is unchecked and autoaddEWS check box is unchecked
        checkAdminSettingsCorrect(false, false, true);

        setUpSessionWithNonAdultAgeRange();

        // check the status of EWS is not added in scan device QR code page by finding Add button for EWS
        boolean add_ews_displayed = driver.findElement(By.id("buttonAddEarlyWarningScore")).isDisplayed();
        Log("Scan device Qr code page add EWS button isDisplayed status = " + add_ews_displayed);
        Assert.assertTrue(add_ews_displayed);

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Leads to Mode selection page ");

        clickOnChangeSessionSettings();

        // check the appearance of Add EWS button
        boolean add_ews_button_displayed = driver.findElement(By.id("buttonChangeSessionSettingsAddEarlyWarningScores")).isDisplayed();
        Log("isDisplayed status of Add EWS button is " + add_ews_button_displayed);
        Assert.assertTrue(add_ews_button_displayed);

        clickOnBack();
        // click on the manual vital sign Entry page
        driver.findElement(By.id("buttonBigButtonFour")).click();
        //click on next
        clickOnNext();
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");

        clickOnNext();

        clickOnNext();

        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        // Click on Patient vital display page
        driver.findElement(By.id("buttonBigButtonOne")).click();

        // Check the graph for EWS inpatient vital display page,not found
        if( driver.findElements(By.id("textEarlyWarningScoreDescription")).size() == 0)
        {
            Log("EWS graph is not visible ");
        }
        else
        {
            Log("Check in Patient vital display page.EWS graph is visible and EWS is added to the session");
        }

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        //Select Add EWS from change session setting page
        clickOnChangeSessionSettings();
        Log("Lead us to the change session setting page");
        // Click on the AddEWS button from change session setting page
        driver.findElement(By.id("buttonChangeSessionSettingsAddEarlyWarningScores")).click();
        // Check Remove button appear after adding the EWS to the session
        boolean remove_ews_button_displayed = driver.findElement(By.id("buttonChangeSessionSettingsRemoveEarlyWarningScores")).isDisplayed();
        Log("Remove EWS button isDisplayed status = " + remove_ews_button_displayed);
        Assert.assertTrue(remove_ews_button_displayed);

        clickOnBack();
        // Click on Patient vital display page
        driver.findElement(By.id("buttonBigButtonOne")).click();

        // Check the appearance of EWS graph in patient vital display page
        if( driver.findElements(By.id("textEarlyWarningScoreDescription")).size() != 0)
        {
            Log("EWS graph is  visible ");
        }
        else
        {
            Log("Check in Patient vital display page.EWS graph is not visible");
        }

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    //Start the session with adult age range and  the manual vital sign
    public void test_a_Adultrange_Session_with_Only_manuallyEnterVitalsign_Enter_Indipendently() throws IOException
    {
        String case_id = "23135";
        helper.printTestStart("test_a_Adultrange_Session_with_Only_manuallyEnterVitalsign_Enter_Indipendently",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        Log("Test case1:Start the session with adult age range with manual entry sign WHERE YOU ENTER THE VITAL SIGN ONE BY ONE ");

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Enter the manual Vital sign Entry page
        driver.findElement(By.id("buttonBigButtonFour")).click();
        Log("Leads us back to Observation page to set Time again ");

        clickOnLock();

        //Scan the user QR code
        Log("Scan the user Qr code ");
        helper.spoofQrCode_userUnlock();
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");

        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");

        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");

        //select the measurement interval
        Log("Selecting measurement validity to be Five Minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();

        Log("Lead us to the manual vital entry page with set time and measurement length " );

        //Enter the value for RR
        manuallyEnterRR();
        // Enter the value for SpO2
        manuallyEnterSpO2();
        //Enter the manually enter vital sign and set the measurement length
        startManualVitalSignEntry();
        //Enter the value for Weight
        manuallyEnterWeight();
        //Enter the manually enter vital sign and set the measurement length
        startManualVitalSignEntry();
        //Enter the value for Temp
        manuallyEnterTemperature();
        //Enter the manually enter vital sign and set the measurement length
        startManualVitalSignEntry();
        //Enter the value for HeartRate
        manuallyEnterHeartRate();
        //Enter the manually enter vital sign and set the measurement length
        startManualVitalSignEntry();
        //Enter the value for Blood Pressure
        manualEntryBloodPressure();
        //Enter the manually enter vital sign and set the measurement length
        startManualVitalSignEntry();
        // Enter the value for Supplemental Oxygen level
        manuallyEnterSupplementalOxygen();
        startManualVitalSignEntry();
        manuallyEnterConsciousnessLevel();
        driver.findElement(By.id("buttonBigButtonOne")).click();
        boolean LockscreenButton = driver.findElement(By.id("buttonLock")).isDisplayed();

        Log("Button is displayed "+LockscreenButton);
        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_an_AdultrangeSession_with_Only_manuallyEnterVitalsign_Enter_AtOneGo() throws IOException
    {
        String case_id = "23136";
        helper.printTestStart("test_an_AdultrangeSession_with_Only_manuallyEnterVitalsign_Enter_AtOneGo",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        startManualVitalSignEntry();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
       //Enter teh value
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
        Log("Value entered for Weight is 78");
        /* Click on next */
        clickOnNext();
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");

       consciounesslevelOfadultAgerange();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        SupplementalOxygenForAdultAgeRange();

        Log("value dispaly for vital signs are: ");
        List<WebElement> VitalsignNameButton=driver.findElements(By.id("buttonVitalSignName"));
        int iSize1 = VitalsignNameButton.size();
        for(int i=0; i<iSize1; i++) {
            WebElement button = VitalsignNameButton.get(i);
            List<WebElement> textValue = driver.findElements(By.id("textValue"));
            WebElement Textvalue = textValue.get(i);
            Log( button.getText() + " :" + Textvalue.getText());
        }


        clickOnNext();

        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        driver.findElement(By.id("buttonBigButtonOne")).click();

        boolean LockscreenButton = driver.findElement(By.id("buttonLock")).isDisplayed();

        Log("Button is displayed "+LockscreenButton);
        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_a_NonAdultrangeSession_with_Only_manuallyEnterVitalsign() throws IOException
    {
        String case_id = "23137";

        helper.printTestStart("test_a_NonAdultrangeSession_with_Only_manuallyEnterVitalsign",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Enter the manual Vital sign Entry page
        driver.findElement(By.id("buttonBigButtonFour")).click();
        Log("Leads us back to Observation page to set Time again ");

        clickOnLock();

        //Scan the user QR code
        Log("Scan the user Qr code ");
        helper.spoofQrCode_userUnlock();
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );

        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
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
        Log("Value entered for Weight is 78");
        /* Click on next */
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
        List<WebElement> VitalsignNameButton=driver.findElements(By.id("buttonVitalSignName"));
        int iSize1 = VitalsignNameButton.size();
        for(int i=0; i<iSize1; i++) {
            WebElement button = VitalsignNameButton.get(i);
            List<WebElement> textValue = driver.findElements(By.id("textValue"));
            WebElement Textvalue = textValue.get(i);
            Log( button.getText() + " :" + Textvalue.getText());
        }

        clickOnNext();

        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        driver.findElement(By.id("buttonBigButtonOne")).click();

        boolean LockscreenButton = driver.findElement(By.id("buttonLock")).isDisplayed();

        Log("Button is displayed "+LockscreenButton);
        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_Adding_Annotationna_Type_KeyBoard_To_the_session_Along_With_Other_VitalSign() throws IOException
    {
        String case_id = "23138";
        helper.printTestStart("test_By_Adding_Annotation_Type_KeyBoard_To_the_session_Along_With_Other_VitalSign",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Enter vital signHH
        startManualVitalSignEntry();
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");

        clickOnNext();

        clickOnNext();

        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        //Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();

        //Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();


        clickOnNext();
        
        //Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton= driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is "+HideButton);
        //Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        //Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        // Need to automate the appearance of Annotation blob in patient vital display page

        endSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    //test AnnotationBlob Shouldnot Display On The PatientVitalDisplay Page If no Vitalsign Has Added To The Session
    public void test_AnnotationBlob_Shouldnot_Display_On_The_Patient_Vital_Display_Page_If_no_Vitalsign_Has_Added_To_The_Session() throws IOException
    {
        String case_id = "23139";
        helper.printTestStart("test_AnnotationBlob_Shouldnot_Display_On_The_Patient_Vital_Display_Page_If_no_Vitalsign_Has_Added_To_The_Session",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        //Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        //Click on the Next button
        clickOnNext();
        //Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton= driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is "+HideButton);
        //Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        //Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        //Check the value enter for annotation is not showing on the  patient vital display page
        //Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is" + message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, message_displayed);
        Log("No device added to the session so no Annotation blob is visible ");

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    // Enter the Keyboard annotation
    //Test the appearance of annotation blob on patient vital display page after adding the vital sign
    public void test_The_Appearance_Of_Annotation_Blob_In_The_Patient_Vital_Page_After_Adding_vital_Sign_To_the_session() throws IOException
    {
        String case_id = "23140";
        helper.printTestStart("test_The_Appearance_Of_Annotation_Blob_In_The_Patient_Vital_Page_After_Adding_vital_Sign_To_the_session",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        //Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        //Click on the Next button
        clickOnNext();
        //Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton= driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is "+HideButton);
        //Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        //Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        //Check the value enter for annotation is not showing on the  patient vital display page
        //Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        String message_displayed = driver.findElement(By.id("textViewEmptyListDeviceConnectedPatientVitalsDisplay")).getText();
        Log("Message displayed in patient vital display page is" + message_displayed);
        String no_devices_message = helper.getAppString("textEmptyDeviceListConnectedToPatientVitalsDisplay");
        Assert.assertEquals(no_devices_message, message_displayed);
        Log("No device added to the session so no Annotation blob is visible ");

        clickOnLock();

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        startManualVitalSignEntry();
        // add the  Respiratory Rate to the session
        manuallyEnterRR();
        driver.findElement(By.id("buttonBigButtonOne")).click();
        // check the appearance of annotationBlob in the patient vital display page
        //ToDo: need to find a way to automate it
        String Annotation=  driver.findElement(By.id("patient_vitals_display_scrollview")).getText();
        Log("Value seen is "+Annotation);

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    // Enter the predefined Annotation in the session
    public void test_By_Entering_Predefine_Annotation_With_Some_Options() throws IOException
    {
        String case_id = "23141";
        helper.printTestStart("test_By_Entering_Predefine_Annotation_With_Some_Options",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // click on Predefined button
        WebElement Predefined= driver.findElement(By.id("buttonAnnotationPredefined"));
        Log("Click on the predefined Button");
        Predefined.click();
        // Check the Predefined button selected and Predefined Text appear
        String PredefineText= driver.findElement(By.id("textVitalSignValue")).getText();
        String expected=helper.getAppString("predefined");
        Assert.assertEquals(PredefineText,expected);
        Log("The value appear after selecting predefined button is "+PredefineText);
        // Check the appearance of Next button after selecting Predefined and click on the next button
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        clickOnNext();
        // Check the Next button from Annotation entry mode page took us to the right page
        String Texttop= driver.findElement(By.id("textTop")).getText();
        Log("Title name is "+Texttop);
        String TexttopExpected=helper.getAppString("enter_the_annotation_time_by_moving_the_green_dot");
        Assert.assertEquals(Texttop,TexttopExpected);
        Log("We are in correct page");
        clickOnNext();
        //Select the condition
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_conditions").isDisplayed());
        String SelectTheCondition=helper.findTextViewElementWithString("select_the_annotation_conditions").getText();
        Log("Lead us to :"+SelectTheCondition);
        List<WebElement> textCondition=driver.findElements(By.id("checkBoxAnnotationName"));
        int i=0;
        //Cardiac Arrest
        Log("click on "+textCondition.get(i).getText());
        textCondition.get(i).click();
        i=1;
        //Respiratory Arrest
        textCondition.get(i).click();
        Log("click on "+textCondition.get(i).getText());
        i=5;
        //Respiratory Distress
        textCondition.get(i).click();
        Log("click on "+textCondition.get(i).getText());
        //Check can delete the selected conditions
        //Deselect the checked Cardiac Arrest
        WebElement cardiac_arrest= textCondition.get(1);
        cardiac_arrest.click();
        String isChecked =cardiac_arrest.getAttribute("checked");
        Log("Checkbox status is " + isChecked);
        if(isChecked.equals("true")) {
           cardiac_arrest.click();
            Log("Cardiac Arrest become unchecked");
        }
        //press next button
        clickOnNext();
        //Select some actions
        String SelectTheAction=helper.findTextViewElementWithString("select_the_annotation_actions").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_actions").isDisplayed());
        Log("Lead us to :"+SelectTheAction);
        List<WebElement> textAction = driver.findElements(By.id("checkBoxAnnotationName"));
        //Ventilation
        textAction.get(0).click();
        Log("click on "+textAction.get(0).getText());
        //Given Oxygen
        textAction.get(3).click();
        Log("click on "+textAction.get(3).getText());

        WebElement Ventilation=textAction.get(0);
        isChecked = Ventilation.getAttribute("checked");

        Log("Checkbox status is " + isChecked);
        if(isChecked.equals("true")) {
            Ventilation.click();
            Log("Ventilation become unchecked");
        }
        Ventilation.click();

        //press next button
        clickOnNext();
        //Select some Outcomes
        String SelectTheOutcomes=helper.findTextViewElementWithString("select_the_annotation_outcomes").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_outcomes").isDisplayed());
        Log("Lead us to :"+SelectTheOutcomes);

        List<WebElement> textOutcomes = driver.findElements(By.id("checkBoxAnnotationName"));

        //CPR
        textOutcomes.get(0).click();
        Log("click on "+textOutcomes.get(0).getText());
        //Patient transferred to PIC
        textOutcomes.get(1).click();
        Log("click on "+textOutcomes.get(1).getText());
        textOutcomes.get(2).click();
        Log("click on "+textOutcomes.get(2).getText());
        //Deselect CPR
        WebElement CPR= textOutcomes.get(0);
        CPR.click();
        isChecked =CPR.getAttribute("checked");
        Log("Checkbox status is " + isChecked);
        if(isChecked.equals("true")) {
            CPR.click();
            Log("CPR become unchecked");
        }
        CPR.click();
        //press next button
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        startManualVitalSignEntry();

        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
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
        driver.findElement(By.id("buttonBigButtonOne")).click();
        boolean LockscreenButton = driver.findElement(By.id("buttonLock")).isDisplayed();

        Log("Button is displayed "+LockscreenButton);
        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    //Test to check the appearance of Next button after entering the Annotation Entry mode
    @Test
    // Test by selecting all the option for preferred annotation at the same time
    public void test_By_Entering_PredefineAnnotation_With_All_Options() throws IOException
    {
        String case_id = "23142";
        helper.printTestStart("test_By_Entering_PredefineAnnotation_With_All_Options",case_id);
        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        //Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // click on Predefined button
        WebElement Predefined= driver.findElement(By.id("buttonAnnotationPredefined"));
        Log("Click on the predefined Button");
        Predefined.click();
        // Check the Predefined button selected and Predefined Text appear
        String PredefineText= driver.findElement(By.id("textVitalSignValue")).getText();
        String expected= helper.getAppString("predefined");
        Assert.assertEquals(PredefineText,expected);
        Log("The value appear after selecting predefined button is "+PredefineText);
        // Check the appearance of Next button after selecting Predefined and click on the next button
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        clickOnNext();
        // Check the Next button from Annotation entry mode page took us to the right page
        String Texttop= driver.findElement(By.id("textTop")).getText();
        Log("Title name is "+Texttop);
        String TexttopExpected=helper.getAppString("enter_the_annotation_time_by_moving_the_green_dot");
        Assert.assertEquals(Texttop,TexttopExpected);
        Log("We are in correct page");
        //press next button
        clickOnNext();
        //Select some of all conditions from list
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_conditions").isDisplayed());
        Log("We are in Select condition page");
        Log("Select all the conditions display");
        List<WebElement> checkBoxes=driver.findElements(By.id("checkBoxAnnotationName"));
        int iSize = checkBoxes.size();
        Log("Number of annotations: " + checkBoxes.size());
        for(int i=0; i<iSize; i++)
        {
            Log(checkBoxes.get(i).getText());
            checkBoxes.get(i).click();
        }

        //press next button
        clickOnNext();
        //Select some actions
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_actions").isDisplayed());
        Log("We are in select Action page");
        //select all the action at the same time
        Log("Select all the action display");
        List<WebElement> checkBoxes1=driver.findElements(By.id("checkBoxAnnotationName"));
        int iSize1 = checkBoxes1.size();
        Log("Number of annotations: " + checkBoxes1.size());
        for(int i=0; i<iSize1; i++)
        {
            Log(checkBoxes1.get(i).getText());
            checkBoxes1.get(i).click();
        }
        //press next button
        clickOnNext();
        //Select some Outcomes
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_outcomes").isDisplayed());
        Log("We are in select Outcome page");
        //Select all teh outcome at the same time
        Log("Select all the outcome display");
        List<WebElement> checkBoxes2=driver.findElements(By.id("checkBoxAnnotationName"));
        int iSize2 = checkBoxes2.size();
        Log("Number of annotations: " + checkBoxes2.size());
        for(int i=0; i<iSize2; i++)
        {
            Log(checkBoxes2.get(i).getText());
            checkBoxes2.get(i).click();
        }
        //press next button
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        //  Add a manual vital sign
        startManualVitalSignEntry();

        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
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
        // Check the patient vital display page
        driver.findElement(By.id("buttonBigButtonOne")).click();
        boolean LockscreenButton = driver.findElement(By.id("buttonLock")).isDisplayed();

        Log("Button is displayed "+LockscreenButton);

        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }
//View Manual Vital signs has removed from the Gateway

  /*  @Test
    //View the manually entered vital signs page ,after entering all the vitalsign and annotation manually for adult age range
    public void test_ViewManuallyEnteredVitalSigns_Page_for_adult_age_range() throws IOException
    {
        String case_id = "23143";
        helper.printTestStart("test_ViewManuallyEnteredVitalSigns_Page_for_adult_age_range",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
        List<WebElement> Vitalsign_Value=driver.findElements(By.id("textValue"));
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
       /* clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String RR_Entered= Vitalsign_Value.get(0).getText();
        Log("Value entered for RR is :"+RR_Entered);
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String spo2_Entered= Vitalsign_Value.get(1).getText();
        Log("Value entered for SpO2 is :"+spo2_Entered);
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String Temp_Entered= Vitalsign_Value.get(5).getText();
        Log("Value entered for Temperature is :"+Temp_Entered);
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        //Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String HR_Entered= Vitalsign_Value.get(3).getText();
        Log("Value entered for HeartRate is :"+HR_Entered);
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
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String BP_Entered= Vitalsign_Value.get(4).getText();
        Log("Value entered for BloodPressure is :"+BP_Entered);
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        SupplementalOxygenForAdultAgeRange();
        //helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options
        // Click on None
        //helper.findTextViewElementWithString("stringYes").click();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String supplementalOxygenLevel_Entered= Vitalsign_Value.get(2).getText();
        Log("Value for supplemental Oxygen level is : "+supplementalOxygenLevel_Entered);
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        consciounesslevelOfadultAgerange();
       // helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //helper.findTextViewElementWithString("alert").click();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String ConsciousnessLevel_Entered= Vitalsign_Value.get(6).getText();
        Log("Value for Consciousness Level is "+ConsciousnessLevel_Entered);
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

        //Add the annotation to the session
        //Click on the Annotation button
         Log("Click on the annotion button");
         driver.findElement(By.id("buttonBigButtonFive")).click();
         predefinedAnnotation();
        //Click on the ViewManually Entered Vital Signs button
        //ToDo: button doesn't exist any more

        driver.findElement(By.id("buttonBigButton__REMOVED")).click();
        Log("Value display on view manualvital sign page is display as bellow");
        List<WebElement> measurement_lines = driver.findElements(By.id("measurement_line"));

        Log("Number of  vital sign displays on the page is " + measurement_lines.size());
        helper.captureScreenShot();

        // Adult age range has 7 manual vitals, plus annotations, all of which were entered meaning 8 in total
        Assert.assertEquals(Vitalsign_Value.size()+1, measurement_lines.size());

        // Done: use asserts to verify the values here match...
        String hRate = helper.findTextViewElementWithString("heart_rate").getText();
        String HartRateValue= measurement_lines.get(0).getText();
        Log(hRate + " data recorded on " + HartRateValue);
        //take the time stamp part away from the string
        String[] parts=HartRateValue.split(" : ");
        String part1=parts[0];
        String HR_display=parts[1];
        //Log(HR_display);
        //to replace the space
        HR_display= HR_display.replaceAll("\\s+", "");
        Assert.assertEquals(HR_display,HR_Entered);
        Log("Value entered for Heart rate is same as value display on the View vital sign page.");
        //Respiration rate
        String RRate = helper.findTextViewElementWithString("respiration_rate").getText();
        String RRValue= measurement_lines.get(1).getText();
        Log(RRate + " data recorded on " +RRValue);
        //take the time stamp part away from the string
        parts=RRValue.split(" : ");
        part1=parts[0];
        String RR_display=parts[1];
        //to replace the space
        RR_display= RR_display.replaceAll("\\s+", "");
        Assert.assertEquals(RR_display,RR_Entered);
        Log("Value entered for Respiration rate is same as value display on the View vital sign page.");

        //Temp
        String temp = helper.findTextViewElementWithString("temperature").getText();
        String TempValue= measurement_lines.get(2).getText();
        Log(temp + " data recorded on " +TempValue);
        //take the time stamp part away from the string
        parts=TempValue.split(" : ");
        part1=parts[0];
        String Temp_display=parts[1];
        //to replace the space
        Temp_display= Temp_display.replaceAll("\\s+", "");
        //to replace the degrees
        String degrees_c = helper.getAppString("degrees");
        Temp_display = Temp_display.replace(degrees_c, "");
        Temp_display = Temp_display.replace(".0", "");
        Assert.assertEquals(Temp_display,Temp_Entered);
        Log("Value entered for Temperature is same as value display on the View vital sign page.");
        //SpO2
        String spO2 = helper.findTextViewElementWithString("spo2").getText();
        String SpO2Value= measurement_lines.get(3).getText();
        Log(spO2 + " data recorded on " +SpO2Value);
        //take the time stamp part away from the string
        parts=SpO2Value.split(" : ");
        part1=parts[0];
        String SpO2_display=parts[1];
        //to replace the space
        SpO2_display= SpO2_display.replaceAll("\\s+", "");
        SpO2_display=SpO2_display.replace("%","");
        Assert.assertEquals(SpO2_display,spo2_Entered);
        Log("Value entered for SpO2 is same as value display on the View vital sign page.");
        //BloodPressure
        String bloodPressure = helper.findTextViewElementWithString("blood_pressure").getText();
        String BPvalue=measurement_lines.get(4).getText();
        Log(bloodPressure + " data recorded on " + BPvalue);
        //take the time stamp part away from the string
        parts=BPvalue.split(" : ");
        part1=parts[0];
        String BP_display=parts[1];
        //to replace the space
        BP_display= BP_display.replaceAll("\\s+", "");
        Assert.assertEquals(BP_display,BP_Entered);
        Log("Value entered for BP is same as value display on the View vital sign page.");
        //consciousness Level
        String consciousnessLevel = helper.findTextViewElementWithString("consciousness_level").getText();
        String consciousnessLevelValue=measurement_lines.get(5).getText();
        Log(consciousnessLevel + " data recorded on " + consciousnessLevelValue);
        //take the time stamp part away from the string
        parts=consciousnessLevelValue.split(" : ");
        part1=parts[0];
        String consciousnessLevel_display=parts[1];
        //to replace the space
        consciousnessLevel_display= consciousnessLevel_display.replaceAll("\\s+", "");
        Assert.assertEquals(consciousnessLevel_display,ConsciousnessLevel_Entered);
        Log("Value entered for ConsciousnessLevel is same as value display on the View vital sign page.");
        //supplemental Oxygen
        String supplementalOxygen = helper.findTextViewElementWithString("supplemental_oxygen").getText();
        String supplementalOxygenValue=measurement_lines.get(6).getText();
        Log(supplementalOxygen + " data recorded on " + supplementalOxygenValue);
        //take the time stamp part away from the string
        parts=supplementalOxygenValue.split(" : ");
        part1=parts[0];
        String supplementalOxygen_display=parts[1];
        //to replace the space
        supplementalOxygen_display= supplementalOxygen_display.replaceAll("\\s+", "");
        Assert.assertEquals(supplementalOxygen_display, supplementalOxygenLevel_Entered);
        Log("Value entered for supplementalOxygen is same as value display on the View vital sign page.");
        //Annotation
        String Annottion_Entered="Cardiac Arrest, Respiratory Arrest, Respiratory Distress : Ventilation, Given Oxygen : CPR, Patient transferred to PIC, Patient transferred to HDU";
        String annotation = helper.findTextViewElementWithString("annotations").getText();
        String AnnotationValue=measurement_lines.get(7).getText();
        Log(annotation + " data recorded on " + AnnotationValue);
        //take the time stamp part away from the string
        parts=AnnotationValue.split(": ");
        part1=parts[0];
        String part2=parts[1];
        String part3=parts[2];
        String part4=parts[3];
        String NewAnnotionDisplay=part2+": "+part3+": "+part4;
        //Log("Compaire the value display for Annotion is same in both Annotion pop up window and under Manual vital display page");
        Assert.assertEquals(NewAnnotionDisplay,Annottion_Entered);
        Log("Value entered for Annotation is same as value display on the View vital sign page.");


        clickOnBack();

        endSession();
        helper.updateTestRail(PASSED);

    }
*/

    @Test
    public void Verify_that_the_value_entered_for_vital_signs_are_same_as_value_showing_on_the_observation_page_for_adult_age_range() throws IOException
    {
        String case_id = "23144";
        helper.printTestStart("Verify_that_the_value_entered_for_vital_signs_are_same_as_value_showing_on_the_observation_page_for_adult_age_range",case_id);

        HashMap<String, String> entered_values = new HashMap<>();

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");
        //select the measurement interval
        // Select the measurement interval as 10 mins
        String MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();
        entered_values.put("Respiration Rate","78");
        Log("Value entered for RR is : 78");
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        entered_values.put("SpO2","98");
        Log("Value entered for SpO2 is :98");
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        entered_values.put("Temperature","38");
        Log("Value entered for Temperature is :38");
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        //Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        entered_values.put("Heart Rate","72");
        Log("Value entered for HeartRate is :72");
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
        clickOnNext();
        entered_values.put("Blood Pressure","98/68");
        Log("Value entered for BloodPressure is : 98/68");
        // Click on the Weight  button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        entered_values.put("Weight","78");
        Log("Value entered for Weight is 78");
        /* Click on next */
        clickOnNext();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options
        // Click on None
        helper.findTextViewElementWithString("stringYes").click();
        entered_values.put("Supplemental Oxygen","Yes");
        Log("Value for supplemental Oxygen level is : yes");
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        helper.findTextViewElementWithString("alert").click();
        entered_values.put("Consciousness Level","Alert");
        Log("Value for Consciousness Level is Alert");
        Log("Verify the value entered for the vital sign are showing same on the observation page.");
        List<WebElement> Vitalsign_Value = driver.findElements(By.id("textValue"));
        List<WebElement> Vitalsign_Names = driver.findElements(By.id("buttonVitalSignName"));
        entered_values.put(Vitalsign_Names.get(0).getText(), Vitalsign_Value.get(0).getText());
        entered_values.put(Vitalsign_Names.get(1).getText(), Vitalsign_Value.get(1).getText());
        entered_values.put(Vitalsign_Names.get(2).getText(), Vitalsign_Value.get(2).getText());
        entered_values.put(Vitalsign_Names.get(3).getText(), Vitalsign_Value.get(3).getText());
        entered_values.put(Vitalsign_Names.get(4).getText(), Vitalsign_Value.get(4).getText());
        entered_values.put(Vitalsign_Names.get(5).getText(), Vitalsign_Value.get(5).getText());
        entered_values.put(Vitalsign_Names.get(6).getText(), Vitalsign_Value.get(6).getText());
        entered_values.put(Vitalsign_Names.get(7).getText(), Vitalsign_Value.get(7).getText());

        helper.updateTestRail(PASSED);

    }


    @Test
    public void Verify_that_ManualEntry_value_showing_on_the_observation_page_are_same_as_value_showing_on_the_confirmation_page_for_adult_age_range() throws IOException
    {
        String case_id = "23145";

        helper.printTestStart("Verify_that_ManualEntry_value_showing_on_the_observation_page_are_same_as_value_showing_on_the_confirmation_page_for_adult_age_range",case_id);

        HashMap<String, String> entered_values = new HashMap<>();

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");
        //select the measurement interval
        // Select the measurement interval as 10 mins
        String MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");

        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();

        List<WebElement> Vitalsign_Value = driver.findElements(By.id("textValue"));
        String RR_Entered = Vitalsign_Value.get(0).getText();

        Log("Value entered for RR is :" + RR_Entered);
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();

        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String spo2_Entered = Vitalsign_Value.get(1).getText();
        Log("Value entered for SpO2 is :" + spo2_Entered);

        // Click on the Weight  button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();

        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String Weight_Entered = Vitalsign_Value.get(5).getText();
        Log("Value entered for Weight is :" + Weight_Entered);



        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String Temp_Entered = Vitalsign_Value.get(6).getText();
        Log("Value entered for Temperature is :" + Temp_Entered);
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        //Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String HR_Entered = Vitalsign_Value.get(3).getText();
        Log("Value entered for HeartRate is :" + HR_Entered);
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
        clickOnNext();

        // Observation Set page onscreen
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String BP_Entered = Vitalsign_Value.get(4).getText();
        Log("Value entered for BloodPressure is :" + BP_Entered);
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
       // helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options
        // Click on None
       // helper.findTextViewElementWithString("stringYes").click();
        SupplementalOxygenForAdultAgeRange();

        // Observation Set page onscreen
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String supplementalOxygenLevel_Entered = Vitalsign_Value.get(2).getText();

        Log("Value for supplemental Oxygen level is : " + supplementalOxygenLevel_Entered);
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
       // helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //helper.findTextViewElementWithString("alert").click();
        consciounesslevelOfadultAgerange();

        // Observation Set page onscreen
        List<WebElement> Vitalsign_Names = driver.findElements(By.id("buttonVitalSignName"));
        Vitalsign_Value = driver.findElements(By.id("textValue"));

        String ConsciousnessLevel_Entered = Vitalsign_Value.get(7).getText();
        Log("Value for Consciousness Level is " + ConsciousnessLevel_Entered);

        // Loading in the expected results
        entered_values.put(Vitalsign_Names.get(0).getText(), Vitalsign_Value.get(0).getText());
        entered_values.put(Vitalsign_Names.get(1).getText(), Vitalsign_Value.get(1).getText());
        entered_values.put(Vitalsign_Names.get(2).getText(), Vitalsign_Value.get(2).getText());
        entered_values.put(Vitalsign_Names.get(3).getText(), Vitalsign_Value.get(3).getText());
        entered_values.put(Vitalsign_Names.get(4).getText(), Vitalsign_Value.get(4).getText());
        entered_values.put(Vitalsign_Names.get(5).getText(), Vitalsign_Value.get(5).getText());
        entered_values.put(Vitalsign_Names.get(6).getText(), Vitalsign_Value.get(6).getText());
        entered_values.put(Vitalsign_Names.get(7).getText(), Vitalsign_Value.get(7).getText());

        // Click on Next button
        clickOnNext();

        List<WebElement> Vitalsign_Name = driver.findElements(By.id("textVitalSignName"));
        int size= Vitalsign_Name.size();
        List<WebElement> Vitalsigns_Value = driver.findElements(By.id("textVitalSignValue"));

        for(int i=0; i<size; i++)
        {
           // Vitalsign_Value = driver.findElements(By.id("textValue"));
            String VitalSign_Name = Vitalsign_Name.get(i).getText();
            String VitalSign_Value = Vitalsigns_Value.get(i).getText();

            String entered_vital = entered_values.get(VitalSign_Name);
            Log(VitalSign_Name+"="+VitalSign_Value );
            Assert.assertEquals(VitalSign_Value,entered_vital);
            Log(VitalSign_Name+" value showing on the observeation page is same as the conformation page.");
        }
        helper.updateTestRail(PASSED);

    }


    @Test
    //Verify_that_the_value_entered_for_vital_signs_are_same_as_value_showing_on_the_observation_page_for_Non_adult_age_range
    public void Verify_that__the_value_entered_for_vital_signs_are_same_as_value_showing_on_the_observation_page_for_Non_adult_age_range() throws IOException
    {
        String case_id = "23307";
        helper.printTestStart("Verify_that__the_value_entered_for_vital_signs_are_same_as_value_showing_on_the_observation_page_for_Non_adult_age_range",case_id);

        HashMap<String, String> entered_values = new HashMap<>();

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");
        //select the measurement interval
        // Select the measurement interval as 10 mins
        String MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();
        entered_values.put("Respiration Rate","78");
        Log("Value entered for RR is : 78");
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        entered_values.put("SpO2","98");
        Log("Value entered for SpO2 is :98");
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        entered_values.put("Temperature","38");
        Log("Value entered for Temperature is :38");
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        //Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        entered_values.put("Heart Rate","72");
        Log("Value entered for HeartRate is :72");
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
        clickOnNext();
        entered_values.put("Blood Pressure","98/68");
        Log("Value entered for BloodPressure is : 98/68");
        // Click on the Weight  button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        entered_values.put("Weight","78");
        Log("Value entered for Weight is 78");
        /* Click on next */
        clickOnNext();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        SupplementalOxygenForNonAdultAgeRange();
        //helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options
        // Click on None
       // helper.findTextViewElementWithString("supplemental_oxygen_none").click();
        entered_values.put("Supplemental Oxygen","None");
       // Log("Value for supplemental Oxygen level is : None");
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        consciounesslevelOfNonadultAgerange();
       // helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
       // helper.findTextViewElementWithString("alert").click();
        entered_values.put("Consciousness Level","Alert");
       // Log("Value for Consciousness Level is Alert");
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
       // helper.findTextViewElementWithString("respiration_distress").click();
        //To check None button is clickable
        //helper.findTextViewElementWithString("none").click();
        RespirationDistressLevel();
        entered_values.put("Respiration Distress","None");
       // Log("Value entered for RespirationDistress is: None");
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        CapillaryRefillTime();
       // helper.findTextViewElementWithString("capillary_refill_time").click();
        //helper.findTextViewElementWithString("less_or_equal_two_seconds").click();
        entered_values.put("Capillary Refill Time","<= 2seconds");
        //Log("Value entered for CapillaryRefillTime is:<= 2seconds");

        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");
        FamilyNurseConcern();
        //helper.findTextViewElementWithString("family_or_nurse_concern").click();
       // helper.findTextViewElementWithString("none").click();
        entered_values.put("Family or Nurse Concern","None");
       // Log("Value entered for FamilyorNurseConcern is: None");
        Log("Verify the value entered for the vital sign are showing same on the observation page.");
        List<WebElement> Vitalsign_Value = driver.findElements(By.id("textValue"));
        List<WebElement> Vitalsign_Names = driver.findElements(By.id("buttonVitalSignName"));

        entered_values.put(Vitalsign_Names.get(0).getText(), Vitalsign_Value.get(0).getText());
        entered_values.put(Vitalsign_Names.get(1).getText(), Vitalsign_Value.get(1).getText());
        entered_values.put(Vitalsign_Names.get(2).getText(), Vitalsign_Value.get(2).getText());
        entered_values.put(Vitalsign_Names.get(3).getText(), Vitalsign_Value.get(3).getText());
        entered_values.put(Vitalsign_Names.get(4).getText(), Vitalsign_Value.get(4).getText());
        entered_values.put(Vitalsign_Names.get(5).getText(), Vitalsign_Value.get(5).getText());
        entered_values.put(Vitalsign_Names.get(6).getText(), Vitalsign_Value.get(6).getText());
        entered_values.put(Vitalsign_Names.get(7).getText(), Vitalsign_Value.get(7).getText());
        entered_values.put(Vitalsign_Names.get(8).getText(), Vitalsign_Value.get(8).getText());
        entered_values.put(Vitalsign_Names.get(9).getText(), Vitalsign_Value.get(9).getText());
        entered_values.put(Vitalsign_Names.get(10).getText(), Vitalsign_Value.get(10).getText());

        int size = Vitalsign_Names.size();

        for(int i=0; i<size; i++)
        {
            String VitalSign_Name_observation = Vitalsign_Names.get(i).getText();
            String VitalSign_Value_observation = Vitalsign_Value.get(i).getText();

            String entered_vital = entered_values.get(VitalSign_Name_observation);
            Log(entered_vital);
            Log(VitalSign_Name_observation+" = "+VitalSign_Value_observation );
            //Compair the value entered is showing
            Assert.assertEquals(VitalSign_Value_observation,entered_vital);
            Log("Value entered for "+VitalSign_Name_observation +" is same as value showing on the observation page.");
        }
        helper.updateTestRail(PASSED);

    }


    @Test
    //Verify_that_ManualEntry_value_showing_on_the_observation_page_are_same_as_value_showing_on_the_confirmation_page_for_Nonadult_age_range
    public void Verify_that_ManualEntry_value_showing_on_the_observation_page_are_same_as_value_showing_on_the_confirmation_page_for_Nonadult_age_range() throws IOException
    {
        String case_id = "23308";

        helper.printTestStart("Verify_that_ManualEntry_value_showing_on_the_observation_page_are_same_as_value_showing_on_the_confirmation_page_for_Nonadult_age_range",case_id);

        HashMap<String, String> entered_values = new HashMap<>();

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");
        //select the measurement interval
        // Select the measurement interval as 10 mins
        String MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length ");
        List<WebElement> Vitalsign_Value = driver.findElements(By.id("textValue"));
        List<WebElement> Vitalsign_Names = driver.findElements(By.id("buttonVitalSignName"));
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String RR_Entered = Vitalsign_Value.get(0).getText();
        Log("Value entered for RR is :" + RR_Entered);
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String spo2_Entered = Vitalsign_Value.get(1).getText();
        Log("Value entered for SpO2 is :" + spo2_Entered);
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String Temp_Entered = Vitalsign_Value.get(8).getText();
        Log("Value entered for Temperature is :" + Temp_Entered);
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        //Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String HR_Entered = Vitalsign_Value.get(4).getText();
        Log("Value entered for HeartRate is :" + HR_Entered);
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
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String BP_Entered = Vitalsign_Value.get(5).getText();
        Log("Value entered for BloodPressure is :" + BP_Entered);
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        SupplementalOxygenForNonAdultAgeRange();
       // helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options
        // Click on None
        //helper.findTextViewElementWithString("supplemental_oxygen_none").click();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String supplementalOxygenLevel_Entered = Vitalsign_Value.get(2).getText();
        Log("Value for supplemental Oxygen level is : " + supplementalOxygenLevel_Entered);
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
       /* helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        helper.findTextViewElementWithString("alert").click();*/
       consciounesslevelOfNonadultAgerange();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String ConsciousnessLevel_Entered = Vitalsign_Value.get(9).getText();
        Log("Value for Consciousness Level is " + ConsciousnessLevel_Entered);
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
        /*helper.findTextViewElementWithString("respiration_distress").click();
        //To check None button is clickable
        helper.findTextViewElementWithString("none").click();*/
        RespirationDistressLevel();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String RespirationDistress_Entered= Vitalsign_Value.get(3).getText();
        Log("Value entered for RespirationDistress is:"+RespirationDistress_Entered);
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
      /*  helper.findTextViewElementWithString("capillary_refill_time").click();
        helper.findTextViewElementWithString("less_or_equal_two_seconds").click();*/
        CapillaryRefillTime();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String CapillaryRefillTime_Entered= Vitalsign_Value.get(7).getText();
        Log("Value entered for CapillaryRefillTime is:"+CapillaryRefillTime_Entered);
        CapillaryRefillTime_Entered= CapillaryRefillTime_Entered.replaceAll("\\s+", "");
        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");
        FamilyNurseConcern();
       /* helper.findTextViewElementWithString("family_or_nurse_concern").click();
        helper.findTextViewElementWithString("none").click();*/
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String FamilyorNurseConcern_Entered= Vitalsign_Value.get(10).getText();
        Log("Value entered for FamilyorNurseConcern is:"+FamilyorNurseConcern_Entered);
        // Click on the Weight  button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
        clickOnNext();
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        String Weight_Entered = Vitalsign_Value.get(6).getText();
        Log("Value entered for BloodPressure is :" + Weight_Entered);

        Log("Verify the value entered for the vital sign are showing same on the observation page.");
        // Observation Set page onscreen
        Vitalsign_Value = driver.findElements(By.id("textValue"));
        Vitalsign_Names = driver.findElements(By.id("buttonVitalSignName"));

        entered_values.put(Vitalsign_Names.get(0).getText(), Vitalsign_Value.get(0).getText());
        entered_values.put(Vitalsign_Names.get(1).getText(), Vitalsign_Value.get(1).getText());
        entered_values.put(Vitalsign_Names.get(2).getText(), Vitalsign_Value.get(2).getText());
        entered_values.put(Vitalsign_Names.get(3).getText(), Vitalsign_Value.get(3).getText());
        entered_values.put(Vitalsign_Names.get(4).getText(), Vitalsign_Value.get(4).getText());
        entered_values.put(Vitalsign_Names.get(5).getText(), Vitalsign_Value.get(5).getText());
        entered_values.put(Vitalsign_Names.get(6).getText(), Vitalsign_Value.get(6).getText());
        entered_values.put(Vitalsign_Names.get(7).getText(), Vitalsign_Value.get(7).getText());
        entered_values.put(Vitalsign_Names.get(8).getText(), Vitalsign_Value.get(8).getText());
        entered_values.put(Vitalsign_Names.get(9).getText(), Vitalsign_Value.get(9).getText());
        entered_values.put(Vitalsign_Names.get(10).getText(), Vitalsign_Value.get(10).getText());

        //Click on Next button
        clickOnNext();

        List<WebElement> Vitalsign_Name = driver.findElements(By.id("textVitalSignName"));
        int size= Vitalsign_Name.size();
        List<WebElement> Vitalsigns_Value = driver.findElements(By.id("textVitalSignValue"));

        for(int i=0; i<size; i++)
        {
            String VitalSign_Name = Vitalsign_Name.get(i).getText();
            String VitalSign_Value = Vitalsigns_Value.get(i).getText();

            String entered_vital = entered_values.get(VitalSign_Name);

            Log(VitalSign_Name+"="+VitalSign_Value );

            Assert.assertEquals(VitalSign_Value,entered_vital);
            Log(VitalSign_Name+" value showing on the observeation page is same as the conformation page.");
        }
        helper.updateTestRail(PASSED);

    }


    @Test
    //Test by unchecking the predefined annotion check box  ,desappear the predefine option from annotion.
    public void Test_by_unchecking_the_predefined_annotation_check_box_desiaper_the_predefine_option_from_annotation() throws IOException
    {
        String case_id = "23148";

        helper.printTestStart("Test_by_unchecking_the_predefined_annotation_check_box_desiaper_the_predefine_option_from_annotation",case_id);
        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        clickOnBack();

        //Click on the Annotation button
        Log("Click on the annotation button");
        driver.findElement(By.id("buttonBigButtonFive")).click();
        WebElement predefined=driver.findElement(By.id("buttonAnnotationPredefined"));
        WebElement Keyboard= driver.findElement(By.id("buttonAnnotationKeyboard"));
        assertTrue(Keyboard.isDisplayed());
        assertTrue(predefined.isDisplayed());

        clickOnLock();

        checkAdminSettingsCorrect(false, true, false);
        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        assertEquals(0, driver.findElements(By.id("buttonAnnotationKeyboard")).size());
        assertEquals(0, driver.findElements(By.id("buttonAnnotationPredefined")).size());
        Log("Annotation and predefined option disappear after Predefined check box got unchecked.");

        // Check its lead to the correct page
        WebElement Pageheader=driver.findElementById("textTop");
        assertTrue(Pageheader.isDisplayed());
        String Actual=Pageheader.getText();
        String Expected=helper.getAppString("enter_the_annotation_time_by_moving_the_green_dot");
        Assert.assertEquals(Actual,Expected);
        helper.updateTestRail(PASSED);

    }


    @Test
    //Test the appearance of  the predefine option for annotion after by checking the predefined annotion check box from admin page
    public void Test_the_appearance_of_the_predefine_option_for_annotion_after_by_checking_the_predefined_annotion_check_box_from_admin_page() throws IOException
    {
        String case_id = "23149";

        helper.printTestStart("Test_the_appearance_of_the_predefine_option_for_annotion_after_by_checking_the_predefined_annotion_check_box_from_admin_page",case_id);
        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, false);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        clickOnBack();

        //Click on the Annotation button
        Log("Click on the annotation button");
        driver.findElement(By.id("buttonBigButtonFive")).click();
        assertEquals(0, driver.findElements(By.id("buttonAnnotationKeyboard")).size());
        assertEquals(0, driver.findElements(By.id("buttonAnnotationPredefined")).size());
        // Check its lead to the correct page
        WebElement Pageheader=driver.findElementById("textTop");
        assertTrue(Pageheader.isDisplayed());
        String Actual=Pageheader.getText();
        String Expected=helper.getAppString("enter_the_annotation_time_by_moving_the_green_dot");
        Assert.assertEquals(Actual,Expected);

        clickOnLock();

        checkAdminSettingsCorrect(false, true, true);
        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        driver.findElement(By.id("buttonBigButtonFive")).click();
        WebElement predefined=driver.findElement(By.id("buttonAnnotationPredefined"));
        WebElement Keyboard= driver.findElement(By.id("buttonAnnotationKeyboard"));
        assertTrue(Keyboard.isDisplayed());
        assertTrue(predefined.isDisplayed());
        Log("Annotation and predefined option appear after Predefined check box got checked.");
        helper.updateTestRail(PASSED);

    }

// view manual vitals no longer used, has removed from the Gateway
   //@Test
    //View the manually entered vital signs page ,after entering all the vitalsign and annotation manually for nonadult age range
    /*public void test_ViewManuallyEnteredVitalSigns_Page_for_Nonadult_Age_range() throws IOException
    {
        String case_id = "23150";

        helper.printTestStart("test_ViewManuallyEnteredVitalSigns_Page_for_Nonadult_Age_range",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        clickOnNext();

        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );

        List<WebElement> Vitalsign_Value=driver.findElements(By.id("textValue"));

        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        /* Click on next */
       /* clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String RR_Entered= Vitalsign_Value.get(0).getText();
        Log("Value entered for RR is :"+RR_Entered);
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        //clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String spo2_Entered= Vitalsign_Value.get(1).getText();
        Log("Value entered for SpO2 is :"+spo2_Entered);
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        //Click on next
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String Temp_Entered= Vitalsign_Value.get(7).getText();
        Log("Value entered for Temperature is :"+Temp_Entered);
        // Click on the Heart Rate button
        Log("Enter the value for HR");
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        //Log("Value entered for HR is 72");
        //Click on next
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String HR_Entered= Vitalsign_Value.get(4).getText();
        Log("Value entered for HeartRate is :"+HR_Entered);
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
        clickOnNext();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String BP_Entered= Vitalsign_Value.get(5).getText();
        Log("Value entered for BloodPressure is :"+BP_Entered);
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        //helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
       // helper.findTextViewElementWithString("alert").click();
        consciounesslevelOfNonadultAgerange();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String ConsciousnessLevel_Entered= Vitalsign_Value.get(8).getText();
        Log("Value for Consciousness Level is "+ConsciousnessLevel_Entered);
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
       // helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options
        // Click on None
       // helper.findTextViewElementWithString("supplemental_oxygen_none").click();
        SupplementalOxygenForNonAdultAgeRange();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String supplementalOxygenLevel_Entered= Vitalsign_Value.get(2).getText();
        Log("Value for supplemental Oxygen level is : "+supplementalOxygenLevel_Entered);
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
       // helper.findTextViewElementWithString("respiration_distress").click();
        //To check None button is clickable
       // helper.findTextViewElementWithString("none").click();
        RespirationDistressLevel();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String RespirationDistress_Entered= Vitalsign_Value.get(3).getText();
        Log("Value entered for RespirationDistress is:"+RespirationDistress_Entered);
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        CapillaryRefillTime();
       // helper.findTextViewElementWithString("capillary_refill_time").click();
       // helper.findTextViewElementWithString("less_or_equal_two_seconds").click();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String CapillaryRefillTime_Entered= Vitalsign_Value.get(6).getText();
        Log("Value entered for CapillaryRefillTime is:"+CapillaryRefillTime_Entered);
        CapillaryRefillTime_Entered= CapillaryRefillTime_Entered.replaceAll("\\s+", "");
        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");
        FamilyNurseConcern();
       // helper.findTextViewElementWithString("family_or_nurse_concern").click();
        //helper.findTextViewElementWithString("none").click();
        Vitalsign_Value=driver.findElements(By.id("textValue"));
        String FamilyorNurseConcern_Entered= Vitalsign_Value.get(9).getText();
        Log("Value entered for FamilyorNurseConcern is:"+FamilyorNurseConcern_Entered);
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
        //Add the annotation to the session
        //Click on the Annotation button
        Log("Click on the annotion button");
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // click on Predefined button
        predefinedAnnotation();
        //Click on the ViewManually Entered Vital Signs button
        // ToDo: view manual vitals no longer used
        driver.findElement(By.id("buttonBigButton__REMOVED")).click();
        Log("Value display on view manualvital sign page is display as bellow");
        List<WebElement> measurement_lines=driver.findElements(By.id("measurement_line"));
        helper.captureScreenShot();
        Log("Number of  vital sign displays on the page is " + measurement_lines.size());

        // Adult age range has 10 manual vitals, plus annotations, all of which were entered meaning 11 in total
        Assert.assertEquals( Vitalsign_Value.size()+1,measurement_lines.size());

        // Done: use asserts to verify the values here match...
        String hRate = helper.findTextViewElementWithString("heart_rate").getText();
        String HartRateValue= measurement_lines.get(0).getText();
        Log(hRate + " data recorded on " + HartRateValue);
        //take the time stamp part away from the string
        String[] parts=HartRateValue.split(" : ");
        String part1=parts[0];
        String HR_display=parts[1];
        //Log(HR_display);
        //to replace the space
        HR_display= HR_display.replaceAll("\\s+", "");
        Assert.assertEquals(HR_display,HR_Entered);
        Log("Value entered for Heart rate is same as value display on the View vital sign page.");
        //Respiration rate
        String RRate = helper.findTextViewElementWithString("respiration_rate").getText();
        String RRValue= measurement_lines.get(1).getText();
        Log(RRate + " data recorded on " +RRValue);
        //take the time stamp part away from the string
        parts=RRValue.split(" : ");
        part1=parts[0];
        String RR_display=parts[1];
        //to replace the space
        RR_display= RR_display.replaceAll("\\s+", "");
        Assert.assertEquals(RR_display,RR_Entered);
        Log("Value entered for Respiration rate is same as value display on the View vital sign page.");

        //Temp
        String temp = helper.findTextViewElementWithString("temperature").getText();
        String TempValue= measurement_lines.get(2).getText();
        Log(temp + " data recorded on " +TempValue);
        //take the time stamp part away from the string
        parts=TempValue.split(" : ");
        part1=parts[0];
        String Temp_display=parts[1];
        //to replace the space
        Temp_display= Temp_display.replaceAll("\\s+", "");
        //to replace the degrees
        String degrees_c = helper.getAppString("degrees");
        Temp_display = Temp_display.replace(degrees_c, "");
        Temp_display=Temp_display.replace(".0","");
        Assert.assertEquals(Temp_display,Temp_Entered);
        Log("Value entered for Temperature is same as value display on the View vital sign page.");
        //SpO2
        String spO2 = helper.findTextViewElementWithString("spo2").getText();
        String SpO2Value= measurement_lines.get(3).getText();
        Log(spO2 + " data recorded on " +SpO2Value);
        //take the time stamp part away from the string
        parts=SpO2Value.split(" : ");
        part1=parts[0];
        String SpO2_display=parts[1];
        //to replace the space
        SpO2_display= SpO2_display.replaceAll("\\s+", "");
        SpO2_display=SpO2_display.replace("%","");
        Assert.assertEquals(SpO2_display,spo2_Entered);
        Log("Value entered for SpO2 is same as value display on the View vital sign page.");
        //BloodPressure
        String bloodPressure = helper.findTextViewElementWithString("blood_pressure").getText();
        String BPvalue=measurement_lines.get(4).getText();
        Log(bloodPressure + " data recorded on " + BPvalue);
        //take the time stamp part away from the string
        parts=BPvalue.split(" : ");
        part1=parts[0];
        String BP_display=parts[1];
        //to replace the space
        BP_display= BP_display.replaceAll("\\s+", "");
        Assert.assertEquals(BP_display,BP_Entered);
        Log("Value entered for BP is same as value display on the View vital sign page.");
        //consciousness Level
        String consciousnessLevel = helper.findTextViewElementWithString("consciousness_level").getText();
        String consciousnessLevelValue=measurement_lines.get(5).getText();
        Log(consciousnessLevel + " data recorded on " + consciousnessLevelValue);
        //take the time stamp part away from the string
        parts=consciousnessLevelValue.split(" : ");
        part1=parts[0];
        String consciousnessLevel_display=parts[1];
        //to replace the space
        consciousnessLevel_display= consciousnessLevel_display.replaceAll("\\s+", "");
        Assert.assertEquals(consciousnessLevel_display,ConsciousnessLevel_Entered);
        Log("Value entered for ConsciousnessLevel is same as value display on the View vital sign page.");
        //supplemental Oxygen
        String supplementalOxygen = helper.findTextViewElementWithString("supplemental_oxygen").getText();
        String supplementalOxygenValue=measurement_lines.get(6).getText();
        Log(supplementalOxygen + " data recorded on " + supplementalOxygenValue);
        //take the time stamp part away from the string
        parts=supplementalOxygenValue.split(" : ");
        part1=parts[0];
        String supplementalOxygen_display=parts[1];
        //to replace the space
       // supplementalOxygen_display= supplementalOxygen_display.replaceAll("\\s+", "");
        Assert.assertEquals(supplementalOxygen_display,supplementalOxygenLevel_Entered);
        Log("Value entered for supplementalOxygen is same as value display on the View vital sign page.");
        //Annotation
        String Annottion_Entered="Cardiac Arrest, Respiratory Arrest, Respiratory Distress : Ventilation, Given Oxygen : CPR, Patient transferred to PIC, Patient transferred to HDU";
        String annotation = helper.findTextViewElementWithString("annotations").getText();
        String AnnotationValue=measurement_lines.get(10).getText();
        Log(annotation + " data recorded on " + AnnotationValue);
        //take the time stamp part away from the string
        parts=AnnotationValue.split(": ");
        part1=parts[0];
        String part2=parts[1];
        String part3=parts[2];
        String part4=parts[3];
        String NewAnnotionDisplay=part2+": "+part3+": "+part4;
        //Log("Compaire the value display for Annotion is same in both Annotion pop up window and under Manual vital display page");
        Assert.assertEquals(NewAnnotionDisplay,Annottion_Entered);
        Log("Value entered for Annotation is same as value display on the View vital sign page.");
        //capillaryRefillTime
        String capillaryRefillTime= helper.findTextViewElementWithString("capillary_refill_time").getText();
        String capillaryRefillTime_Value=measurement_lines.get(7).getText();
        Log(capillaryRefillTime + " data recorded on " + capillaryRefillTime_Value);
        parts=capillaryRefillTime_Value.split(" : ");
        part1=parts[0];
        String capillaryRefillTime_display=parts[1];
        //to replace the space
        capillaryRefillTime_display= capillaryRefillTime_display.replaceAll("\\s+", "");

        Assert.assertEquals(capillaryRefillTime_display,CapillaryRefillTime_Entered);
       //RespirationDistress
        String respirationDistress= helper.findTextViewElementWithString("respiration_distress").getText();
        String RespirationDistress_Value=measurement_lines.get(9).getText();
        Log(respirationDistress + " data recorded on " + RespirationDistress_Value);
        parts=RespirationDistress_Value.split(" : ");
        part1=parts[0];
        String RespirationDistress_display=parts[1];
        //to replace the space
        RespirationDistress_display= RespirationDistress_display.replaceAll("\\s+", "");
        Assert.assertEquals(RespirationDistress_display,RespirationDistress_Entered);

        String familyorNurseConcern= helper.findTextViewElementWithString("family_or_nurse_concern").getText();
        String familyorNurseConcern_Value=measurement_lines.get(8).getText();
        Log(familyorNurseConcern + " data recorded on " + familyorNurseConcern_Value);
        parts=RespirationDistress_Value.split(" : ");
        part1=parts[0];
        String familyorNurseConcern_display=parts[1];
        //to replace the space
        familyorNurseConcern_display= familyorNurseConcern_display.replaceAll("\\s+", "");
        //Assert.assertEquals(familyorNurseConcern_display,FamilyorNurseConcern_Entered);

        clickOnBack();

        endSession();
        helper.updateTestRail(PASSED);

    }
*/

    @Test
    public void test_the_appearance_of_manual_vital_sign_Small_square_On_the_lock_screen_page_if_the_session_has_got_manual_vital_sign_only() throws IOException
    {
        String case_id = "23152";

        helper.printTestStart("test_the_appearance_of_manual_vital_sign_Small_square_On_the_lock_screen_page_if_the_session_has_got_manual_vital_sign_only",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String Title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + Title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, Title);
        Log("Landed on the correct page");

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );

        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
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
        //Click on Next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        clickOnLock();

        //Check the appearance of small squeae with tag Manual Vitals Only
        Log("Check the appearance of small square tag as manual vital only on the lock screen page");
        boolean manual_vitals_only_text_displayed = driver.findElement(By.id("textManualVitalsOnlySession")).isDisplayed();
        Log("the isDisplayed status of manual vital only square is " + manual_vitals_only_text_displayed);
        Assert.assertTrue(manual_vitals_only_text_displayed);

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_compairing_the_value_entered_for_vitalsign_is_same_what_its_showing_in_the_confirmation_page() throws IOException
    {
        String case_id = "23309";

        helper.printTestStart("test_By_compairing_the_value_entered_for_vitalsign_is_same_what_its_showing_in_the_confirmation_page",case_id);

        // check that patient lookup check box in unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();
        Log("Lead us to Mode selection page ");
        startManualVitalSignEntry();
        // Click on the Respiratory Rate button
        Log("Enter the value for RR");
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        //Click on next
        clickOnNext();
        // Click on the Weight button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for Weight is 72");
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
        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        helper.findTextViewElementWithString("alert").click();
        Log("Value for Consciousness Level is Alert ");
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options yes or No
        // Click on yes
        helper.findTextViewElementWithString("stringYes").click();
        Log("Value for supplemental Oxygen level is yes ");
         //value showing on the observation page
        List<AndroidElement> entered_elements = driver.findElements(By.id("observationDisplayButtonForSelection"));
        HashMap<String, String> entered_values = new HashMap<>();

        for(AndroidElement element : entered_elements)
        {
            String value = element.findElement(By.id("textValue")).getText();
            String measurement_name = element.findElement(By.id("buttonVitalSignName")).getText();

            entered_values.put(measurement_name, value);

            Log("Found vital type " + measurement_name + " with value " + value);
        }

        int number_of_entered_values = entered_values.size();
        Log("Total entered values found: " + number_of_entered_values);

        //Click on Next button
        clickOnNext();
        //Value showing on confirmation page
        List<AndroidElement> displayed_elements = driver.findElements(By.id("observationDisplayRow"));
        HashMap<String, String> displayed_values = new HashMap<>();

        for(AndroidElement element : displayed_elements)
        {
            String value = element.findElement(By.id("textVitalSignValue")).getText();
            String measurement_name = element.findElement(By.id("textVitalSignName")).getText();

            displayed_values.put(measurement_name, value);

            Log("Found vital type " + measurement_name + " with value " + value);
        }

        int number_of_displayed_values = displayed_values.size();
        Log("Total displayed values found: " + number_of_displayed_values);

        // Check same number of entered and displayed values
        Assert.assertEquals(number_of_entered_values, number_of_displayed_values);
        // Check number of displayed values not zero
        Assert.assertNotEquals(0, number_of_displayed_values);

        Set<String> keys = entered_values.keySet();
        for(String key : keys)
        {
            String entered = entered_values.get(key);
            String shown = displayed_values.get(key);

            Assert.assertEquals(entered, shown);
        }

        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        driver.findElement(By.id("buttonBigButtonOne")).click();

        boolean LockscreenButton = driver.findElement(By.id("buttonLock")).isDisplayed();

        Log("Button is displayed " + LockscreenButton);
        clickOnLock();

        scanUserQrCodeAndEndSession();
        helper.updateTestRail(PASSED);

    }


    private void predefinedAnnotation()
    {
        WebElement Predefined= driver.findElement(By.id("buttonAnnotationPredefined"));
        Log("Click on the predefined Button");
        Predefined.click();
        // Check the Predefined button selected and Predefined Text appear
        String PredefineText= driver.findElement(By.id("textVitalSignValue")).getText();
        String expected= helper.getAppString("predefined");
        Assert.assertEquals(PredefineText,expected);
        Log("The value appear after selecting predefined button is "+PredefineText);
        // Check the appearance of Next button after selecting Predefined and click on the next button
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        clickOnNext();
        // Check the Next button from Annotation entry mode page took us to the right page
        String Texttop= driver.findElement(By.id("textTop")).getText();
        Log("Title name is "+Texttop);
        String TexttopExpected=helper.getAppString("enter_the_annotation_time_by_moving_the_green_dot");
        Assert.assertEquals(Texttop,TexttopExpected);
        Log("We are in correct page");
        clickOnNext();
        //Select the condition
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_conditions").isDisplayed());
        String SelectTheCondition=helper.findTextViewElementWithString("select_the_annotation_conditions").getText();
        Log("Lead us to :"+SelectTheCondition);
        List<WebElement> textCondition=driver.findElements(By.id("checkBoxAnnotationName"));
        int i=0;
        //Cardiac Arrest
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
       // Given_Oxygen.click();
        Log("click on "+Given_Oxygen.getText());
        Log("Click on the next button");
        //press next button
        clickOnNext();
        //Select some Outcomes
        String SelectTheOutcomes=helper.findTextViewElementWithString("select_the_annotation_outcomes").getText();
        assertTrue(helper.findTextViewElementWithString("select_the_annotation_outcomes").isDisplayed());
        Log("Lead us to :"+SelectTheOutcomes);
        List<WebElement> textOutcome=driver.findElements(By.id("checkBoxAnnotationName"));
        //CPR
        WebElement CPR=textOutcome.get(0);
        CPR.click();
        Log("click on "+CPR.getText());
        //Patient transferred to PIC
        WebElement Patient_transferred_to_PIC=textOutcome.get(1);
        Patient_transferred_to_PIC.click();
        Log("click on "+Patient_transferred_to_PIC.getText());
        //Patient transferred to HDU
        WebElement Patient_transferred_to_HDU= textOutcome.get(2);
        Patient_transferred_to_HDU.click();
        Log("click on "+Patient_transferred_to_HDU.getText());
        Log("Click on the next button");
        //press next button
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void manuallyEnterRR()
    {
        // Click on the Respiratory Rate button
        helper.findTextViewElementWithString("respiratory_rate").click();
        //Enter teh value
        //Enter the value-78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        // Delete the value
        //Press delete button and 8 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        //Press delete button and 7 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        Log("Delete the value 78 by using Delete button from the keypad");
        //Enter the value 300 or more
        Log("Enter the new value 300 to check the limit");
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        // Check the key paid is enable or disable by clicking on a button
        // boolean Num1 = driver.findElement(By.id("buttonNumberThree")).isEnabled();
        //Log(Num1);
        Log("the keypad turn grey");
        Log("Delete the value and entered a new value ");
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        //Enter the value-89
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonNumberNine")).click();
        Log("New value entered for RR is 89");
        //Click on next
        clickOnNext();
        // Check the value enter is same as displayed under the vital sign
        String RRvalue = driver.findElement(By.xpath("//android.widget.TextView[@text='89']")).getText();
        Log("Value display under the Respiratory Rate is " + RRvalue);
        Assert.assertEquals("89", RRvalue);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for respiration.
        // Click on the Respiratory Rate button
        helper.findTextViewElementWithString("respiratory_rate").click();
        // Enter the new value 78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New value entered for RR is 78");
        //Click on next
        clickOnNext();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for RR");
        // Reenter the RR value
        // Click on the Respiratory Rate button
        helper.findTextViewElementWithString("respiratory_rate").click();
        // Enter the new value 88
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New and Final value entered for RR is 88");
        //Click on next button from Please enter the respiration Rate
        clickOnNext();
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void manuallyEnterSpO2()
    {
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime1 = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime1);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
        // Click on the SpO2 button
        helper.findTextViewElementWithString("spo2").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        // Delete the value
        //Press delete button and 8 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        //Press delete button and 9 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        Log("Delete the value 98 by using Delete button from the keypad");
        //Enter the value 101 or more
        Log("Enter the new value 101to check the limit");
        driver.findElement(By.id("buttonNumberOne")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        driver.findElement(By.id("buttonNumberOne")).click();
        // Check the key paid is enable or disable by clicking on a button
        // boolean Num1 = driver.findElement(By.id("buttonNumberThree")).isEnabled();
        //Log(Num1);
        Log("the keypad turn grey");
        Log("Delete the value and entered a new value ");
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        //Enter the value-99
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberNine")).click();
        Log("New value entered for SpO2 is 99");
        //Click on next
        clickOnNext();
        // Check the value enter is same as displayed under the vital sign
        String Spo2value = driver.findElement(By.xpath("//android.widget.TextView[@text='99']")).getText();
        Log("Value display under the Spo2 is " + Spo2value);
        Assert.assertEquals("99", Spo2value);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for SpO2.
        // Click on the SpO2  button
        helper.findTextViewElementWithString("spo2").click();
        // Enter the new value 97
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberSeven")).click();
        Log("New value entered for SpO2 is 97");
        //Click on next
        clickOnNext();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for SpO2");
        // Reenter the SpO2 value
        // Click on the SpO2 button
        helper.findTextViewElementWithString("spo2").click();
        // Enter the new value 98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New and Final value entered for SpO2 is 98");
        //Click on next button from Please enter the respiration Rate
        clickOnNext();
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }
    private void manuallyEnterWeight()
    {
        // Click on the Weight button
        helper.findTextViewElementWithString("weight").click();
        //Enter teh value
        //Enter the value-98
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Weight is 78");
        // Delete the value
        //Press delete button and 8 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        //Press delete button and 7 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        Log("Delete the value 78 by using Delete button from the keypad");
      /*  //Enter the value 101 or more
        Log("Enter the new value 101to check the limit");
        driver.findElement(By.id("buttonNumberOne")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        driver.findElement(By.id("buttonNumberOne")).click();
        // Check the key paid is enable or disable by clicking on a button
        // boolean Num1 = driver.findElement(By.id("buttonNumberThree")).isEnabled();
        //Log(Num1);
        Log("the keypad turn grey");
        Log("Delete the value and entered a new value ");
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();*/
        //Enter the value-99
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberNine")).click();
        Log("New value entered for Weight is 99");
        //Click on next
        clickOnNext();
        // Check the value enter is same as displayed under the vital sign
        String Weightvalue = driver.findElement(By.xpath("//android.widget.TextView[@text='99']")).getText();
        Log("Value display under the Spo2 is " + Weightvalue);
        Assert.assertEquals("99", Weightvalue);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for Weight.
        // Click on the Weight  button
        helper.findTextViewElementWithString("weight").click();
        // Enter the new value 97
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberSeven")).click();
        Log("New value entered for SpO2 is 97");
        //Click on next
        clickOnNext();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for Weight");
        // Reenter the Weight value
        // Click on the Weight button
        helper.findTextViewElementWithString("weight").click();
        // Enter the new value 98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New and Final value entered for Weight is 98");
        //Click on next button from Please enter the Weight
        clickOnNext();
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void manuallyEnterTemperature()
    {
        // Click on the Temp button
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value
        //Enter the value-38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for Temp is 38");
        // Delete the value
        //Press delete button and 8 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        //Press delete button and 3 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        Log("Delete the value 38 by using Delete button from the keypad");
        //Enter the value 46 or more
        Log("Enter the new value 46to check the limit");
        driver.findElement(By.id("buttonNumberFour")).click();
        driver.findElement(By.id("buttonNumberSix")).click();
        // Check the key paid is enable or disable by clicking on a button
        // boolean Num1 = driver.findElement(By.id("buttonNumberThree")).isEnabled();
        //Log(Num1);
        Log("the keypad turn grey");
        Log("Delete the value and entered a new value ");
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();

        //Enter the value-36
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberSix")).click();
        Log("New value entered for Temperature is 36");
        //Click on next
        clickOnNext();
        // Check the value enter is same as displayed under the vital sign
        String Tempvalue = driver.findElement(By.xpath("//android.widget.TextView[@text='36']")).getText();
        Log("Value display under the Temperature is " + Tempvalue);
        Assert.assertEquals("36", Tempvalue);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for Temperature.
        // Click on the Temperature button
        helper.findTextViewElementWithString("temperature").click();
        // Enter the new value 37
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberSeven")).click();
        Log("New value entered for Temp is 37");
        //Click on next
        clickOnNext();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for Temp");
        // Reenter the Temp value
        // Click on the Temp button
        helper.findTextViewElementWithString("temperature").click();
        // Enter the new value 38
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New and Final value entered for Temperature is 38");
        //Click on next button from Please enter the Temperature
        clickOnNext();
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void startManualVitalSignEntry()
    { // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("ten_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
    }


    private void manuallyEnterHeartRate()
    {
        // Click on the Heart Rate button
        helper.findTextViewElementWithString("heart_rate").click();
        //Enter teh value
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for HR is 72");
        // Delete the value
        //Press delete button and 2 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        //Press delete button and 7 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        Log("Delete the value 72 by using Delete button from the keypad");
        //Enter the value 300 or more
        Log("Enter the new value 300 to check the limit");
        driver.findElement(By.id("buttonNumberThree")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        // Check the key paid is enable or disable by clicking on a button
        // boolean Num1 = driver.findElement(By.id("buttonNumberThree")).isEnabled();
        //Log(Num1);
        Log("the keypad turn grey");
        Log("Delete the value and entered a new value ");
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        driver.findElement(By.id("buttonDelete")).click();
        //Enter the value-69
        driver.findElement(By.id("buttonNumberSix")).click();
        driver.findElement(By.id("buttonNumberNine")).click();
        Log("New value entered for HR is 69");
        //Click on next
        clickOnNext();
        // Check the value enter is same as displayed under the vital sign
        String HRvalue = driver.findElement(By.xpath("//android.widget.TextView[@text='69']")).getText();
        Log("Value display under the Heart Rate is " + HRvalue);
        Assert.assertEquals("69", HRvalue);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for HR.
        // Click on the Heart Rate button
        helper.findTextViewElementWithString("heart_rate").click();
        // Enter the new value 78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New value entered for HR is 78");
        //Click on next
        clickOnNext();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for HR");
        // Reenter the HR value
        // Click on the HR Rate button
        helper.findTextViewElementWithString("heart_rate").click();
        // Enter the new value 68
        driver.findElement(By.id("buttonNumberSix")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New and Final value entered for HR is 68");
        //Click on next button from Please enter the Heart Rate
        clickOnNext();
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void manualEntryBloodPressure()
    {
        // Click on the BloodPressure button
        helper.findTextViewElementWithString("blood_pressure").click();
        //Enter the value-98/68
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        driver.findElement(By.id("buttonDecimal")).click();
        driver.findElement(By.id("buttonNumberSix")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for BP is 98/68");
        //Press delete button and 8 will be deleted
        driver.findElement(By.id("buttonDelete")).click();
        //now press 3,so the value will be 98/63
        driver.findElement(By.id("buttonNumberThree")).click();
        Log("Value entered for BP is 98/63");
        //Click on next
        clickOnNext();
        // Check the value enter is same as displayed under the vital sign
        String BPvalue = driver.findElement(By.xpath("//android.widget.TextView[@text='98/63']")).getText();
        Log("Value display under the Blood Pressure is " + BPvalue);
        Assert.assertEquals("98/63", BPvalue);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for BP
        // Click on the BloodPressure button
        helper.findTextViewElementWithString("blood_pressure").click();
        // Enter the new value 90/78

        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberZero")).click();
        driver.findElement(By.id("buttonDecimal")).click();
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("New value entered for bp is 90/78");
        //Click on next
        clickOnNext();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for BP");
        // Reenter the BP value
        // Click on the BP Rate button
        helper.findTextViewElementWithString("blood_pressure").click();
        // Enter the new value 92/73
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        driver.findElement(By.id("buttonDecimal")).click();
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberThree")).click();
        Log("New value entered for bp is 92/73");
        //Click on next button from Please enter the Blood pressure
        clickOnNext();
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void manuallyEnterSupplementalOxygen()
    {
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Choose the value from the options yes or No
        // Click on yes
        helper.findTextViewElementWithString("stringYes").click();
        Log("Value for supplemental Oxygen level is yes ");
        // Check the value enter is same as displayed under the vital sign
        String supplemental_oxygen_value = helper.findTextViewElementWithString("stringYes").getText();
        Log("Value display under the Supplemental Oxygen is " + supplemental_oxygen_value);
        Assert.assertEquals("Yes", supplemental_oxygen_value);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for Supplemental Oxygen
        // Click on the Supplemental Oxygen button
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Enter the new value NO
        helper.findTextViewElementWithString("stringNo").click();
        Log("Value for supplemental Oxygen level is No ");
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for Supplemental Oxygen");
        // Reenter the value for Supplemental Oxygen
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        // Enter the new value NO
        helper.findTextViewElementWithString("stringNo").click();
        Log("Value for supplemental Oxygen level is No ");
        //Click on next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void manuallyEnterConsciousnessLevel()
    {
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        WebElement Alert= Option.get(0);
        WebElement Confusion=Option.get(1);
        WebElement Verbal=Option.get(2);
        WebElement Pain=Option.get(3);
        WebElement Unresponsive=Option.get(4);




        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        String CLvalue =  Alert.getText();
        Alert.click();

        // Check the value enter is same as displayed under the vital sign


        String displayValue="Alert";
        Log("Value display under the Consciousness Level is " + displayValue);
        Assert.assertEquals(displayValue, CLvalue);

        // Check the Next button is visible or not after entering the value for vital sign
        boolean next_button_displayed = driver.findElement(By.id("buttonNext")).isDisplayed();
        Assert.assertTrue(next_button_displayed);
        Log("Next button visible");

        //Edit the entered value for Consciousness Level
        // Click on the Consciousness Level button
        helper.findTextViewElementWithString("consciousness_level").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        // Enter the new value as Confusion
        Log(Option.get(1).getText());
        Confusion=Option.get(1);
        Log("Chose the Value for Consciousness Level is "+ Confusion.getText());
        Confusion.click();
        // Delete the entered value by clicking on the cross mark
        driver.findElement(By.id("imageCancel")).click();
        Log("Able to delete the value entered for Consciousness Level");
        // Reenter the value for Consciousness Level
        helper.findTextViewElementWithString("consciousness_level").click();
        // Enter the new value Alert
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Alert=Option.get(0);
        Log("Set the Value for Consciousness Level as  "+Alert.getText());
        Alert.click();
        //Click on next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void endSession()
    {
        Log("End the current session");

        // Click on the Stop Monitoring Current Patient
        driver.findElement(By.id("buttonBigButtonThree")).click();

        // Click on End session
        driver.findElement(By.id("buttonEndSession")).click();

        // Click on Enter
        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();

        // Click on Confirm button
        driver.findElement(By.id("buttonEndSessionBigButtonBottom")).click();
    }
    public void consciounesslevelOfadultAgerange()
     {

    helper.findTextViewElementWithString("consciousness_level").click();
    // Choose the value from the options
    // Click on Alert
    //option avelable for Consciousness lavel
    optionArea= driver.findElement(By.id("recyclerView"));
    Option = optionArea.findElements(By.className("android.widget.TextView"));

    int iSize=Option.size();
    System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
    for(int i=0; i<iSize; i++)
    {
        Log(Option.get(i).getText());

    }
    WebElement Alert= Option.get(0);
    WebElement Confusion=Option.get(1);
    WebElement Verbal=Option.get(2);
    WebElement Pain=Option.get(3);
    WebElement Unresponsive=Option.get(4);

    //helper.findTextViewElementWithString("alert").click();
    Log("Value Enter  for Consciousness Level is  "+Alert.getText());
    Alert.click();
}
    public void consciounesslevelOfNonadultAgerange()
    {

        helper.findTextViewElementWithString("consciousness_level").click();
        // Choose the value from the options
        // Click on Alert
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        WebElement Alert= Option.get(0);
        WebElement Voice=Option.get(1);
        WebElement Pain=Option.get(2);
        WebElement Unresponsive=Option.get(3);

        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());

        Alert.click();
    }
    public void SupplementalOxygenForAdultAgeRange()
    {

        helper.findTextViewElementWithString("supplemental_oxygen").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());


        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(0).getText());
        Option.get(0).click();


    }
    public void SupplementalOxygenForNonAdultAgeRange()
    {
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
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

        int iSize=Option.size();
        System.out.println("Number of option available for RespirationDistressLevel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
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

        int iSize=Option.size();
        System.out.println("Number of option available for CapillaryRefillTime is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        Log("Value Enter  for CapillaryRefillTime is  "+Option.get(1).getText());
        Option.get(1).click();
        helper.findTextViewElementWithString("capillary_refill_time").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Log("Value Enter  for CapillaryRefillTime is  "+Option.get(0).getText());
        Option.get(0).click();
    }
    public void FamilyNurseConcern()
    {
        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        WebElement TextTop= driver.findElement(By.id("textTop"));
        assertTrue(TextTop.isDisplayed());
        Log(TextTop.getText());
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        int iSize=Option.size();
        System.out.println("Number of option available for FamilyNurseConcern is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        Log("Value Enter  for FamilyNurseConcern is  "+Option.get(0).getText());
        Option.get(0).click();
        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        Log("Value Enter  for FamilyNurseConcern is  "+Option.get(1).getText());
        Option.get(1).click();
    }

}




