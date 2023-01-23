package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.common.enums.SensorType;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class QrUnlockPage extends AppiumTest {
    @Test
    public void test_UI_unlock_fragment_page() {
        String case_id = "22558";
        helper.printTestStart("test_UI_unlock_fragment_page",case_id);

        // Check the appearance of Qr Scanner camera
        Log("Examining the QrUnlock fragment page");
        WebElement QR_scanner_camera = driver.findElement(By.id("qr_bar_code"));
        assertTrue(QR_scanner_camera.isDisplayed());
        Log("Appearance of QR scanner camera is :" + QR_scanner_camera.isDisplayed());

        // Test the appearance of text
        WebElement ScanIdText = driver.findElementById("textPleaseScanIdQrCode");
        assertTrue(ScanIdText.isDisplayed());
        String ActualText = ScanIdText.getText();
        Log("Text appear for scanIdQrCode is  :" + ActualText);
        String Expectedtext = helper.getAppString("please_scan_general_user_qr_code_to_access_patient_gateway");
        Assert.assertEquals(Expectedtext, ActualText);

        // QR code UnlockPage help text
        ScanIdText = driver.findElementById("textQrCodeUnlockPageHelp");
        assertTrue(ScanIdText.isDisplayed());
        ActualText = ScanIdText.getText();
        Log("Text appear for Qr code UnlockPage help is :" + ActualText);
        Expectedtext = helper.getAppString("hold_qr_code_so_it_fits_in_the_smaller_lighter_square_on_the_left");
        Assert.assertEquals(Expectedtext, ActualText);

        // Wait until UnlockQrCodeScanScreen button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonUnlockQrCodeScanScreen")), 60);
        WebElement buttonUnlockQrCodeScanScreen = driver.findElementById("buttonUnlockQrCodeScanScreen");

        //Check the appearance of UnlockQrCodeScanScreen button
        assertTrue(buttonUnlockQrCodeScanScreen.isDisplayed());
        Log("Text appear for the button is: " + buttonUnlockQrCodeScanScreen.getText());

        // Click on the buttonUnlockQrCodeScanScreen
        Log("Click on the big blue button");
        buttonUnlockQrCodeScanScreen.click();
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_manual_vital_sign_Small_square_On_the_lock_screen_page_if_the_session_has_got_manual_vital_sign_only() throws IOException {
        String case_id = "22559";

        helper.printTestStart("test_the_appearance_of_manual_vital_sign_Small_square_On_the_lock_screen_page_if_the_session_has_got_manual_vital_sign_only",case_id);

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

        //Enter the value 78
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for RR is 78");
        /* Click on next */
        clickOnNext();
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();

        //Enter the value 98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        //Click on next
        clickOnNext();
        // Click on the Temp button
        Log("Enter the value for Temp");
        helper.findTextViewElementWithString("temperature").click();
        //Enter the value 38
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
        WebElement ConfirmButton = driver.findElement(By.id("buttonBigButtonBottom"));
        ConfirmButton.click();

        clickOnLock();

        //Check the appearance of small square with tag Manual Vitals Only
        Log("Check the appearance of small square tag as manual vital only on the lock screen page");
        WebElement manualVitalOnly = driver.findElement(By.id("textManualVitalsOnlySession"));
        assertTrue(manualVitalOnly.isDisplayed());
        Log("Small text display on the small square is " + manualVitalOnly.getText());
        helper.updateTestRail(PASSED);
    }


    @Test
    public void Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_Lifetouch_to_the_session() throws IOException
    {
        String case_id = "22560";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_Lifetouch_to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnNext();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the Lifetouch is " + ALTStatus);

        clickOnNext();

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Lifetouch to the session");
        WebElement Lifetouchsmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetouch"));
        assertTrue(Lifetouchsmallsquare.isDisplayed());
        Log("Lifetouch smallsquare appearance status is : " + Lifetouchsmallsquare.isDisplayed());
        WebElement Lifetouch = driver.findElement(By.id("textLifetouchLabel"));
        assertTrue(Lifetouch.isDisplayed());
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textLifetouchBatteryPercentage")), 90);

        WebElement LifetouchBatteryvalue = driver.findElement(By.id("textLifetouchBatteryPercentage"));
        assertTrue(LifetouchBatteryvalue.isDisplayed());
        // assertTrue(manualVitalOnly.isDisplayed());
        //Log("Small text display on the small square is "+manualVitalOnly.getText());
        Log("Details display inside the Lifetouch small squares is:");
        Log(Lifetouch.getText());
        Log(LifetouchId.getText());
        Log(LifetouchBatteryvalue.getText());
        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_the_detail_displayed_in_small_square_for_lifetouch_is_same_as_the_detail_display_on_the_graph() throws IOException
    {
        String case_id = "22561";

        helper.printTestStart("test_the_detail_displayed_in_small_square_for_lifetouch_is_same_as_the_detail_display_on_the_graph",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the lifetouch is " + ALTStatus);

        clickOnStartMonitoring();

        Log("Left hand side of the graph is showing");
        //Lifetouch Id
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        String Lifetouchid = LifetouchId.getText();
        Log("Lifetouch ID in patient vital display page is:" + LifetouchId.getText());

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Lifetouch to the session");
        WebElement Lifetouchsmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetouch"));
        assertTrue(Lifetouchsmallsquare.isDisplayed());
        Log("Lifetouch small square appearance status is : " + Lifetouchsmallsquare.isDisplayed());
        WebElement Lifetouch = driver.findElement(By.id("textLifetouchLabel"));
        assertTrue(Lifetouch.isDisplayed());
        Log(Lifetouch.getText());
        WebElement LifetouchId1 = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId1.isDisplayed());
        Log(LifetouchId1.getText());
        //combine two string together
        StringBuilder newValue = new StringBuilder().append(Lifetouch.getText()).append(" ").append(LifetouchId1.getText());
        Log("Lifetouch id in scan Qr code page is: " + newValue.toString());
        //compare the Lifetouch id
        Assert.assertEquals(Lifetouchid, newValue.toString());
        Log("Showing same Lifetouch Id in both lock screen page as well as in lifetouch graph in patientvital display page");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Lifetouch_small_square_after_Lifetouch_removed_from_the_session() throws IOException
    {
        String case_id = "22562";

        helper.printTestStart(" test_the_appearance_of_Lifetouch_small_square_after_Lifetouch_removed_from_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Lifetouch from change session setting page");
        //Remove the Lifetouch
        removeLTDevice();

        checkRecyclingReminderPopup();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        //Check the Remove status for Lifetouch
        Log("Check the Lifetouch status,after device got remove from the session");
        String Expected = helper.getAppString("textDeviceRemoved");

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeartRate"), Expected), 90);

        String ActualHR = driver.findElement(By.id("textHeartRate")).getText();
        Assert.assertEquals(Expected, ActualHR);
        String ActualRR = driver.findElement(By.id("textRespirationRate")).getText();
        Assert.assertEquals(Expected, ActualRR);
        Log("Heart rate and respiration rate status is showing removed");
        //Check the disappearance of option button and device ID for lifetouch
        assertTrue((driver.findElements(By.id("textLifetouchHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");
        assertTrue((driver.findElements(By.id("checkBoxLifetouchConfigurable")).size() == 0));
        assertTrue((driver.findElements(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).size() == 0));
        Log("No checkbox for Lifetouch,after it removed");

        clickOnLock();

        Log("Check the appearance of small square on lock screen page after Removing a Lifetouch from the session");
        WebElement Lifetouchsmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetouch"));
        assertTrue(Lifetouchsmallsquare.isDisplayed());
        Log("Lifetouch smallsquare appearance status is : " + Lifetouchsmallsquare.isDisplayed());
        WebElement Lifetouch = driver.findElement(By.id("textLifetouchLabel"));
        assertTrue(Lifetouch.isDisplayed());
        Log(Lifetouch.getText());
        Log("Text appear inside the small square is: " + Lifetouch.getText());
        //Check the presence of "X" mark on the remove square
        WebElement RemoveImage=driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetouchShowWhenRemoved"));
        RemoveImage.isDisplayed();
        //Check there shouldn't be any device ID and battery image after device removed
        assertTrue((driver.findElements(By.id("imageBatteryLifetouch")).size() == 0));
        assertTrue((driver.findElements(By.id("textLifetouchHumanReadableDeviceId")).size() == 0));
        Log("It shouldn't show any device ID or battery image after device removed");

        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Lifetouch_small_square_after_Lifetouch_is_Leadoff() throws IOException
    {
        String case_id = "22563";

        helper.printTestStart("test_the_appearance_of_Lifetouch_small_square_after_Lifetouch_is_Leadoff",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__LIFETOUCH, true);

        //Check the Leads off status for Lifetouch
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeartRate"), helper.getAppString("textWaitingForData")), 130);

//        Log("Check the Lifetouch status,after device got remove from the session");
//        String Expected = helper.getAppString("textLeadsOff");
//        String ActualHR = driver.findElement(By.id("textHeartRate")).getText();
//        Assert.assertEquals(Expected, ActualHR);
//        String ActualRR = driver.findElement(By.id("textRespirationRate")).getText();
//        Assert.assertEquals(Expected, ActualRR);
        Log("Heart rate and respiration rate status is showing Leadoff");
        WebElement LifetouchLeadoff = driver.findElement(By.id("textViewLifetouchNotAttached"));
        assertTrue(LifetouchLeadoff.isDisplayed());
        Log("Graph page is showing :" + LifetouchLeadoff.getText());
        Log("Check the Screen QR code page");

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textLifetouchRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textLifetouchRedBorderStatus")).isDisplayed());
        Log("Text appear inside the Red Border is: " + Qrscrenview.getText());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_Lifetemp_to_the_session() throws IOException
    {
        String case_id = "22564";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_Lifetemp_to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Lifetouch to the session");
        WebElement LifetempSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetemp"));
        assertTrue(LifetempSmallsquare.isDisplayed());
        Log("Lifetouch smallsquare appearance status is : " + LifetempSmallsquare.isDisplayed());
        WebElement Lifetemp = driver.findElement(By.id("textLifetempLabel"));
        assertTrue(Lifetemp.isDisplayed());
        WebElement LifetempId = driver.findElement(By.id("textLifetempHumanReadableDeviceId"));
        assertTrue(LifetempId.isDisplayed());

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textLifetempBatteryPercentage")), 90);

        WebElement LifetempBatteryvalue = driver.findElement(By.id("textLifetempBatteryPercentage"));
        assertTrue(LifetempBatteryvalue.isDisplayed());
        Log("Details display inside the Lifetemp small squares is:");
        Log(Lifetemp.getText());
        Log(LifetempId.getText());
        Log(LifetempBatteryvalue.getText());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_detail_displayed_in_small_square_for_lifetemp_is_same_as_the_detail_display_on_the_graph() throws IOException
    {
        String case_id = "22565";

        helper.printTestStart("test_the_detail_displayed_in_small_square_for_lifetemp_is_same_as_the_detail_display_on_the_graph",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        WebElement LifetempId = driver.findElement(By.id("textLifetempHumanReadableDeviceId"));
        String Lifetempid = LifetempId.getText();
        Log("Lifetemp ID in patient vital display page is :" + Lifetempid);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Lifetouch to the session");
        WebElement LifetempSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetemp"));
        assertTrue(LifetempSmallsquare.isDisplayed());
        Log("Lifetouch smallsquare appearance status is : " + LifetempSmallsquare.isDisplayed());
        WebElement Lifetemp = driver.findElement(By.id("textLifetempLabel"));
        assertTrue(Lifetemp.isDisplayed());
        WebElement Lifetemp_number = driver.findElement(By.id("textLifetempHumanReadableDeviceId"));
        assertTrue(Lifetemp_number.isDisplayed());
        StringBuilder newValue = new StringBuilder().append(Lifetemp.getText()).append(" ").append(Lifetemp_number.getText());
        Log("Lifetemp id in Lockscreen page is : " + newValue.toString());
        Assert.assertEquals(Lifetempid, newValue.toString());
        Log("Showing same Lifetemp Id in both lock screen page as well as in lifetemp graph in patientvital display page ");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Lifetemp_small_square_after_Lifetemp_devices_removed_from_the_session() throws IOException
    {
        String case_id = "22566";

        helper.printTestStart("test_the_appearance_of_Lifetemp_small_square_after_Lifetemp_devices_removed_from_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Lifetemp from change session setting page");
        //Remove the Lifetemp
        removeLTempDevice();

        checkRecyclingReminderPopup();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        //Check the Remove status for Lifetemp
        Log("Check the Lifetemp status,after device got remove from the session");

        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement temp_element = driver.findElement(By.id("textTemperatureReading"));

        waitUntil(ExpectedConditions.textToBePresentInElement(temp_element, expected_string), 90);

        String ActualTemp = temp_element.getText();
        Assert.assertEquals(expected_string, ActualTemp);
        Log("Temperature status is showing removed");

        //Check the appearance of small square on the scan fragment page
        clickOnLock();

        Log("Check the appearance of small square on lock screen page after adding a Lifetemp to the session");
        WebElement LifetempSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoLifetemp"));
        assertTrue(LifetempSmallsquare.isDisplayed());
        Log("Lifetemp smallsquare appearance status is : " + LifetempSmallsquare.isDisplayed());
        WebElement Lifetemp = driver.findElement(By.id("textLifetempLabel"));
        Log("Text appear inside the small square is: " + Lifetemp.getText());
        //Check the presence of "X" mark on the remove square
        WebElement RemoveImage=driver.findElement(By.id("linearLayoutLockScreenDeviceInfoThermometerShowWhenRemoved"));
        RemoveImage.isDisplayed();
        //Check there shouldn't be any deviceID and battery image after device removed
        assertTrue((driver.findElements(By.id("imageBatteryLifetemp")).size() == 0));
        assertTrue((driver.findElements(By.id("textLifetempHumanReadableDeviceId")).size() == 0));
        Log("It shouldn't show any device ID or battery image after device removed");


        helper.updateTestRail(PASSED);
    }

    @Test
    public void check_the_appearance_of_Lifetemp_small_square_on_QR_Unlock_page_after_lifetemp_leadoff() throws IOException
    {
        String case_id = "22567";

        helper.printTestStart("check_the_appearance_of_Lifetemp_small_square_on_QR_Unlock_page_after_lifetemp_leadoff",case_id);

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

        clickOnLock();

        WebElement Qrscreenview = driver.findElement(By.id("textLifetempRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textLifetempRedBorderStatus")).isDisplayed());
        Log(Qrscreenview.getText());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_Nonin_to_the_session() throws IOException
    {
        String case_id = "22568";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_Nonin_to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement NoninSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoNoninWristOx"));
        assertTrue(NoninSmallsquare.isDisplayed());
        Log("Nonin smallsquare appearance status is : " + NoninSmallsquare.isDisplayed());
        WebElement Noninlabel = driver.findElement(By.id("textNoninWristOxLabel"));
        assertTrue(Noninlabel.isDisplayed());
        WebElement NoninDeviceId = driver.findElement(By.id("textNoninWristOxHumanReadableDeviceId"));
        assertTrue(NoninDeviceId.isDisplayed());

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageBatteryNoninWristOx")), 90);

        WebElement NoninBattery = driver.findElement(By.id("imageBatteryNoninWristOx"));
        assertTrue(NoninBattery.isDisplayed());
        Log("Details display inside the Nonin small squares is:");
        Log(Noninlabel.getText());
        Log(NoninDeviceId.getText());
        Log("Battery shell display status is " + NoninBattery.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_BLE_Nonin_to_the_session() throws IOException
    {
        String case_id = "22569";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_BLE_Nonin_to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        //Scan BLE Nonin
        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement NoninSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoNoninWristOx"));
        assertTrue(NoninSmallsquare.isDisplayed());
        Log("Nonin smallsquare appearance status is : " + NoninSmallsquare.isDisplayed());
        WebElement Noninlabel = driver.findElement(By.id("textNoninWristOxLabel"));
        assertTrue(Noninlabel.isDisplayed());
        WebElement NoninDeviceId = driver.findElement(By.id("textNoninWristOxHumanReadableDeviceId"));
        assertTrue(NoninDeviceId.isDisplayed());

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageBatteryNoninWristOx")), 90);

        WebElement NoninBattery = driver.findElement(By.id("imageBatteryNoninWristOx"));
        assertTrue(NoninBattery.isDisplayed());
        Log("Details display inside the Nonin small squares is:");
        Log(Noninlabel.getText());
        Log(NoninDeviceId.getText());
        Log("Battery shell display status is " + NoninBattery.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_detail_displayed_in_small_square_for_Nonin_is_same_as_the_detail_display_on_the_graph() throws IOException
    {
        String case_id = "22570";

        helper.printTestStart("test_the_detail_displayed_in_small_square_for_Nonin_is_same_as_the_detail_display_on_the_graph",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        clickOnNext();
        WebElement NoninId = driver.findElementById("textPulseOxHumanReadableDeviceId");
        String Noninid = NoninId.getText();
        Log("Nonin ID in patient vital display page is :" + Noninid);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement NoninSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoNoninWristOx"));
        assertTrue(NoninSmallsquare.isDisplayed());
        Log("Nonin smallsquare appearance status is : " + NoninSmallsquare.isDisplayed());
        WebElement Noninlabel = driver.findElement(By.id("textNoninWristOxLabel"));
        assertTrue(Noninlabel.isDisplayed());
        WebElement NoninDeviceId = driver.findElement(By.id("textNoninWristOxHumanReadableDeviceId"));
        assertTrue(NoninDeviceId.isDisplayed());

        String nonin_label_and_id = Noninlabel.getText() + " " + NoninDeviceId.getText();
        Log("Nonin id in Lockscreen page is : " + nonin_label_and_id);

        Assert.assertEquals(Noninid, nonin_label_and_id);

        Log("Showing same Nonin Id number in both lock screen page as well as in Nonin graph in patientvital display page ");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_the_detail_displayed_in_small_square_for_BLE_Nonin_is_same_as_the_detail_display_on_the_graph() throws IOException
    {
        String case_id = "22571";

        helper.printTestStart("test_the_detail_displayed_in_small_square_for_BLE_Nonin_is_same_as_the_detail_display_on_the_graph",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        clickOnNext();
        WebElement NoninId = driver.findElementById("textPulseOxHumanReadableDeviceId");
        String Noninid = NoninId.getText();
        Log("Nonin ID in patient vital display page is :" + Noninid);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement NoninSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoNoninWristOx"));
        assertTrue(NoninSmallsquare.isDisplayed());
        Log("Nonin smallsquare appearance status is : " + NoninSmallsquare.isDisplayed());
        WebElement Noninlabel = driver.findElement(By.id("textNoninWristOxLabel"));
        assertTrue(Noninlabel.isDisplayed());
        WebElement NoninDeviceId = driver.findElement(By.id("textNoninWristOxHumanReadableDeviceId"));
        assertTrue(NoninDeviceId.isDisplayed());

        String nonin_label_and_id = Noninlabel.getText() + " " + NoninDeviceId.getText();
        Log("Nonin id in Lockscreen page is : " + nonin_label_and_id);

        Assert.assertEquals(Noninid, nonin_label_and_id);

        Log("Showing same Nonin Id number in both lock screen page as well as in Nonin graph in patientvital display page ");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Nonin_small_square_after_Nonin_devices_removed_from_the_session() throws IOException
    {
        String case_id = "22572";

        helper.printTestStart("test_the_appearance_of_Nonin_small_square_after_Nonin_devices_removed_from_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Nonin from change session setting page");
        //Remove the Nonin
        removeNonin();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        //Check the Remove status for Nonin
        Log("Check the Nonin status,after device got remove from the session");
        String Expected = helper.getAppString("textDeviceRemoved");

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textSpO2"), Expected), 90);

        String ActualSpO2 = driver.findElement(By.id("textSpO2")).getText();
        Assert.assertEquals(Expected, ActualSpO2);
        Log("SpO2 status is showing : " + ActualSpO2);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after Removing a Nonin from the session");
        WebElement NoninSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoNoninWristOx"));
        assertTrue(NoninSmallsquare.isDisplayed());
        Log("Nonin smallsquare appearance status is : " + NoninSmallsquare.isDisplayed());
        Log(NoninSmallsquare.getText());
        WebElement Noninlabel = driver.findElement(By.id("textNoninWristOxLabel"));
        Log("Text appear inside the small square is : " + Noninlabel.getText());
        //Check the presence of "X" mark on the remove square
        WebElement RemoveImage=driver.findElement(By.id("linearLayoutLockScreenDeviceInfoPulseOxShowWhenRemoved"));
        RemoveImage.isDisplayed();
        //Check there shouldn't be any battery image after device removed
        assertTrue((driver.findElements(By.id("imageBatteryNoninWristOx")).size() == 0));
        assertTrue((driver.findElements(By.id("textNoninWristOxHumanReadableDeviceId")).size() == 0));
        Log("It shouldn't show any device ID or battery image after device removed");

        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Nonin_small_square_after_BLE_Nonin_devices_removed_from_the_session() throws IOException
    {
        String case_id = "22573";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_Removeing_a_BLE_Nonin_to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Nonin from change session setting page");
        //Remove the Nonin
        removeNonin();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        //Check the Remove status for Nonin
        Log("Check the Nonin status,after device got remove from the session");
        String Expected = helper.getAppString("textDeviceRemoved");

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textSpO2"), Expected), 90);

        String ActualSpO2 = driver.findElement(By.id("textSpO2")).getText();
        Assert.assertEquals(Expected, ActualSpO2);
        Log("SpO2 status is showing: " + ActualSpO2);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement NoninSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoNoninWristOx"));
        assertTrue(NoninSmallsquare.isDisplayed());
        Log("Nonin smallsquare appearance status is : " + NoninSmallsquare.isDisplayed());
        Log(NoninSmallsquare.getText());
        WebElement Noninlabel = driver.findElement(By.id("textNoninWristOxLabel"));
        Log("Text appear inside the small square is : " + Noninlabel.getText());
        //Check the presence of "X" mark on the remove square
        WebElement RemoveImage=driver.findElement(By.id("linearLayoutLockScreenDeviceInfoPulseOxShowWhenRemoved"));
        RemoveImage.isDisplayed();
        //Check there shouldn't be any battery image after device removed
        assertTrue((driver.findElements(By.id("imageBatteryNoninWristOx")).size() == 0));
        assertTrue((driver.findElements(By.id("textNoninWristOxHumanReadableDeviceId")).size() == 0));
        Log("It shouldn't show any device ID or battery image after device removed");

        helper.updateTestRail(PASSED);
    }

    @Test
    public void check_the_appearance_of_Nonin_small_square_after_Nonin_leadoff() throws IOException
    {
        String case_id = "22574";

        helper.printTestStart("check_the_appearance_of_Nonin_small_square_after_Nonin_leadoff",case_id);

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

        clickOnLock();

        WebElement Qrscreenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textNoninRedBorderStatus")).isDisplayed());
        Log(Qrscreenview.getText());
        //Checking the prasence of the battery image
        WebElement Batteryimage = driver.findElement(By.id("imageBatteryNoninWristOx"));
        assertTrue(Batteryimage.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void check_the_appearance_of_BLE_Nonin_small_square_after_Nonin_leadoff() throws IOException
    {
        String case_id = "23310";

        helper.printTestStart("check_the_appearance_of_BLE_Nonin_small_square_after_Nonin_leadoff",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataBtleNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__SPO2, true);
        //checkNoninLeadoff();
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

        clickOnLock();

        WebElement Qrscreenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textNoninRedBorderStatus")).isDisplayed());
        Log("Text showing inside small squear for BLE Nonin when Lead off  is:" + Qrscreenview.getText());
        WebElement Batteryimage = driver.findElement(By.id("imageBatteryNoninWristOx"));
        assertTrue(Batteryimage.isDisplayed());
        helper.updateTestRail(PASSED);
    }
    @Test
    public void Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_WeightScale_to_the_session() throws IOException
    {
        String case_id = "C54558";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_WeightScale_to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String AWeightStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightStatus);

        clickOnStartMonitoring();

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a WeightScale to the session");
        WebElement WeightSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoWeightScale"));
        assertTrue(WeightSmallsquare.isDisplayed());
        Log("WeightScale smallsquare appearance status is : " + WeightSmallsquare.isDisplayed());
        WebElement WeightScalelabel = driver.findElement(By.id("textWeightScale"));
        assertTrue(WeightScalelabel.isDisplayed());
        WebElement WeightScaleDeviceId = driver.findElement(By.id("textWeightScaleHumanReadableDeviceId"));
        assertTrue(WeightScaleDeviceId.isDisplayed());

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageBatteryWeightScale")), 90);

        WebElement WeightScaleBattery = driver.findElement(By.id("imageBatteryWeightScale"));
        assertTrue(WeightScaleBattery.isDisplayed());
        Log("Details display inside the WeightScale small squares is:");
        Log(WeightScalelabel.getText());
        Log(WeightScaleDeviceId.getText());
        Log("Battery shell display status is " + WeightScaleBattery.isDisplayed());
        helper.updateTestRail(PASSED);
    }
    @Test
    public void test_the_detail_displayed_in_small_square_for_WeightSacel_is_same_as_the_detail_display_on_the_graph() throws IOException
    {
        String case_id = "C54559";

        helper.printTestStart("test_the_detail_displayed_in_small_square_for_WeightSacel_is_same_as_the_detail_display_on_the_graph",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleStatus);
        clickOnNext();
        WebElement WeightScaleID = driver.findElementById("textWeightScaleHumanReadableDeviceId");
        String WeightScaleId = WeightScaleID.getText();
        Log("WeightScale ID in patient vital display page is :" + WeightScaleId);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a WeightScale to the session");
        WebElement WeightScaleSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoWeightScale"));
        assertTrue(WeightScaleSmallsquare.isDisplayed());
        Log("WeightScale small square appearance status is : " + WeightScaleSmallsquare.isDisplayed());
        WebElement WeightScalelabel = driver.findElement(By.id("textWeightScale"));
        assertTrue(WeightScalelabel.isDisplayed());
        WebElement WeightScaleDeviceId = driver.findElement(By.id("textWeightScaleHumanReadableDeviceId"));
        assertTrue(WeightScaleDeviceId.isDisplayed());

        String nonin_label_and_id = WeightScalelabel.getText() + " " + WeightScaleDeviceId.getText();
        Log("WeightScale id in Lockscreen page is : " + nonin_label_and_id);

        Assert.assertEquals(WeightScaleId, nonin_label_and_id);

        Log("Showing same WeightScale Id number in both lock screen page as well as in SwightScale graph in patient vital display page ");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_WeightScale_small_square_after_WeightScale_devices_removed_from_the_session() throws IOException
    {
        String case_id = "C54560";

        helper.printTestStart("test_the_appearance_of_WeightScale_small_square_after_WeightScale_devices_removed_from_the_session",case_id);

        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyWeightScale();

        clickOnConnect();

        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the weightScale is " + AWeightScaleMStatus);

        clickOnStartMonitoring();

        clickOnLock();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageBatteryWeightScale")), 90);
        WebElement WeightScaleBattery = driver.findElement(By.id("imageBatteryWeightScale"));
        WeightScaleBattery.isDisplayed();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove WeightScale from change session setting page");
        //Remove the WeightScale
        removeWeightscale();
        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement WeightScale_element = driver.findElement(By.id("textWeightScaleMeasurement"));

        waitUntil(ExpectedConditions.textToBePresentInElement(WeightScale_element, expected_string), 135);

        //Check the Remove status for WeightScale
        Log("Check the Weightscale status,after device got remove from the session");

        String ActualWeightScaleStatus = WeightScale_element.getText();
        Assert.assertEquals(expected_string, ActualWeightScaleStatus);
        Log("WeightScale status is showing removed");

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after removing WeightScale to the session");
        WebElement weightScaleSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoWeightScale"));
        assertTrue(weightScaleSmallsquare.isDisplayed());
        Log("WeightScale smallsquare appearance status is : " + weightScaleSmallsquare.isDisplayed());
        WebElement WeightScalelabel = driver.findElement(By.id("textWeightScale"));
        Log("Text appear inside the small square is : " + WeightScalelabel.getText());
        //Check the presence of "X" mark on the remove square
        WebElement RemoveImage=driver.findElement(By.id("linearLayoutLockScreenDeviceInfoWeightScaleShowWhenRemoved"));
        RemoveImage.isDisplayed();
        //shouldn't show any device Id and battery information after device removed
        assertTrue((driver.findElements(By.id("imageBatteryWeightScale")).size() == 0));
        assertTrue((driver.findElements(By.id("textWeightScaleHumanReadableDeviceId")).size() == 0));
        Log("It shouldn't show any device ID or battery image after device removed");

        helper.updateTestRail(PASSED);
    }



    @Test
    public void Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_BloodPressureDevice_to_the_session() throws IOException
    {
        String case_id = "22575";

        helper.printTestStart("Check_the_appearance_of_small_square_On_the_Scan_fragmentpage_after_adding_a_BloodPressureDevice__to_the_session",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement BPSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoBloodPressure"));
        assertTrue(BPSmallsquare.isDisplayed());
        Log("BP smallsquare appearance status is : " + BPSmallsquare.isDisplayed());
        WebElement BPlabel = driver.findElement(By.id("textBloodPressure"));
        assertTrue(BPlabel.isDisplayed());
        WebElement BPDeviceId = driver.findElement(By.id("textBloodPressureHumanReadableDeviceId"));
        assertTrue(BPDeviceId.isDisplayed());
        // WebElement BPBattery= driver.findElement(By.id("imageBatteryBloodPressure"));
        //assertTrue(BPBattery.isDisplayed());
        Log("Details display inside the BP small squares is:");
        Log(BPlabel.getText());
        Log(BPDeviceId.getText());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_detail_displayed_in_small_square_for_Bloodpressure_is_same_as_the_detail_display_on_the_graph() throws IOException
    {
        String case_id = "22576";

        helper.printTestStart("test_the_detail_displayed_in_small_square_for_Bloodpressure_is_same_as_the_detail_display_on_the_graph",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        WebElement BPId = driver.findElementById("textBloodPressureHumanReadableDeviceId");
        String pvd_screen_info = BPId.getText();
        Log("Blood Pressure ID in patient vital display page is :" + pvd_screen_info);

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        WebElement BPSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoBloodPressure"));
        assertTrue(BPSmallsquare.isDisplayed());
        Log("BP smallsquare appearance status is : " + BPSmallsquare.isDisplayed());

        WebElement BPlabel = driver.findElement(By.id("textBloodPressure"));
        assertTrue(BPlabel.isDisplayed());

        WebElement BPDeviceId = driver.findElement(By.id("textBloodPressureHumanReadableDeviceId"));
        assertTrue(BPDeviceId.isDisplayed());

        String lock_screen_info = BPlabel.getText() + " " + BPDeviceId.getText();

        Log(lock_screen_info);
        Assert.assertEquals(pvd_screen_info, lock_screen_info);
        Log("Showing same BP Id number in both lock screen page as well as in BP graph in patientvital display page ");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Bloodpressure_small_square_after_Bloodpressure_devices_removed_from_the_session() throws IOException
    {
        String case_id = "22577";

        helper.printTestStart("test_the_appearance_of_Bloodpressure_small_square_after_Bloodpressure_devices_removed_from_the_session",case_id);

        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Blood Pressure device from change session setting page");
        //Remove the BP
        removeBPMachine();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        String expected_string = helper.getAppString("textDeviceRemoved");
        WebElement systolic_element = driver.findElement(By.id("textBloodPressureMeasurementSystolic"));

        waitUntil(ExpectedConditions.textToBePresentInElement(systolic_element, expected_string), 135);

        //Check the Remove status for BP
        Log("Check the BP status,after device got remove from the session");

        String ActualBP = systolic_element.getText();
        Assert.assertEquals(expected_string, ActualBP);
        Log("BloodPressure status is showing removed");

        clickOnLock();

        //Check the appearance of small square on the scan fragment page
        Log("Check the appearance of small square on lock screen page after adding a Nonin to the session");
        WebElement BPSmallsquare = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoBloodPressure"));
        assertTrue(BPSmallsquare.isDisplayed());
        Log("BP smallsquare appearance status is : " + BPSmallsquare.isDisplayed());
        WebElement BPlabel = driver.findElement(By.id("textBloodPressure"));
        Log("Text appear inside the small square is : " + BPlabel.getText());
        //shouldn't show any device Id and battery information after device removed
        assertTrue((driver.findElements(By.id("imageBatteryWeightScale")).size() == 0));
        assertTrue((driver.findElements(By.id("textWeightScaleHumanReadableDeviceId")).size() == 0));
        Log("It shouldn't show any device ID or battery image after device removed");

        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_setupmode_label_on_QR_Screen_page_while_Lifetouch_setup_mode_is_running() throws IOException {
        String case_id = "22578";

        helper.printTestStart("test_the_appearance_of_setupmode_label_on_QR_Screen_page_while_Lifetouch_setup_mode_is_running",case_id);

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
        //Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        setupmode();
        Log("The screenshot has captured for Setupmode graph");
        //to  check setup mode graph is running on the screen
        helper.captureScreenShot();
        //click on the lock screen button
        Log("Check the Screen QR code page");

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textLifetouchRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textLifetouchRedBorderStatus")).isDisplayed());
        Log("Can see " + Qrscrenview.getText());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_Accelarationmode_label_on_the_QR_Screen_page_while_Motionmode_is_running() throws IOException {
        String case_id = "22579";

        helper.printTestStart("test_the_appearance_of_Accelarationmode_label_on_the_QR_Screen_page_while_Motionmode_is_running",case_id);

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
        //Click on the option checkbox
        Log("Click on the option checkbox");
        OptionCheckbox.click();
        motionmode();
        Log("The screenshot has captured for motion mode graph");
        //to  check motionmode graph is running on the screen
        helper.captureScreenShot();//click on the lock screen button

        clickOnLock();

        WebElement Qrscreenview = driver.findElement(By.id("textLifetouchRedBorderStatus"));
        assertTrue(driver.findElement(By.id("textLifetouchRedBorderStatus")).isDisplayed());
        Log(Qrscreenview.getText());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_the_appearance_of_setupmode_label_on_QR_Screen_page_while_Nonin_setup_mode_is_running() throws IOException
    {
        String case_id = "22580";

        helper.printTestStart("test_the_appearance_of_setupmode_label_on_QR_Screen_page_while_Nonin_setup_mode_is_running",case_id);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        //Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText() + " Checkbox");
        Log("Start the setup mode");
        SetUpmodeLHS.click();
        //Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : " + SetUpModeGraphView.isDisplayed());
        //click on the lock screen button
        Log("Check the Screen QR code page");

        clickOnLock();

        WebElement Qrscrenview = driver.findElement(By.id("textNoninRedBorderStatus"));
        assertTrue(Qrscrenview.isDisplayed());
        Log("Can see " + Qrscrenview.getText());
        helper.updateTestRail(PASSED);
    }

    /**
     * Check the appearance of the Connection indicator square after unbonding a device
     * <p/>
     * The device should show as disconnected on the unlock screen
     *
     * @throws IOException
     */
    @Test
    public void checkSquareShownAfterUnexpectedUnbond_AnDTM2441() throws IOException
    {
        String case_id = "22581";

        helper.printTestStart("checkUnexpectedUnbondAlert_AnDTM2441",case_id);

        checkAdminSettingsCorrect(false, false, false);

        setUpSessionWithAdultAgeRange();

        // Add AnD
        helper.spoofQrCode_scanDummyDataAnD_TM2441();

        clickOnConnect();

        String connection_status = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP is " + connection_status);

        clickOnStartMonitoring();

        // spoof intent
        helper.spoofCommandIntent_simulateUnexpectedUnbond();

        clickOnLock();

        // check indicator shown
        Log("Check the appearance of small square on lock screen page after adding a AnD to the session");
        WebElement bp_indicator_element = driver.findElement(By.id("linearLayoutLockScreenDeviceInfoBloodPressure"));
        assertTrue(bp_indicator_element.isDisplayed());
        Log("BP small square appearance status is : " + bp_indicator_element.isDisplayed());
        helper.updateTestRail(PASSED);
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

        //waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifetouch_measurement_element, helper.getAppString("textWaitingForData"))), 130);

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

    public void checkLifetouch() {
        Log("Left hand side of the graph is showing");
        //Lifetouch Id
        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        Log("Device ID is:" + LifetouchId.getText());
        String battery_percentage = driver.findElement(By.id("textLifetouchBatteryPercentage")).getText();
        Log("Battery value showing is " + battery_percentage);
    }

    public void setupmode() {
        WebElement setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        assertTrue(setupmode.isDisplayed());
        String isChecked = setupmode.getAttribute("checked");
        Log("Status of Setupmode checkbox is " + isChecked);
        Log("Click on setupmode checkbox");
        setupmode.click();
        //Click on the option checkbox and check the setup mode checkbox status
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        setupmode = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        isChecked = setupmode.getAttribute("checked");
        Log("Check the Status of Setupmode checkbox in option overlay is  " + isChecked);
        Log("Check the Setupmode check box status in Left-hand side of the Lifetouch graph");
        //Check the setup mode checkbox status in LHS side of the graph
        WebElement setupmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = setupmodeLHS.getAttribute("checked");
        Log("Check the Setupmode check box status in Left-hand side of the Lifetouch graph is :" + isChecked);
        //uncheck the option checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
    }

    public void motionmode() {
        WebElement MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        assertTrue(MotionMode.isDisplayed());
        String isChecked = MotionMode.getAttribute("checked");
        Log("Status of Motion Mode checkbox is " + isChecked);
        Log("Click on Motion Mode checkbox");
        MotionMode.click();
        //Click on the option checkbox and check the Motion mode checkbox status
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
        //check the Motion Mode status from option overly
        MotionMode = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        isChecked = MotionMode.getAttribute("checked");
        Log("Check the Status of Motion Mode checkbox in option overlay is  " + isChecked);
        Log("Check the Motiomode check box status in Left-hand side of the Lifetouch graph");
        //Check the Motion Mode checkbox status in LHS side of the graph
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        isChecked = motionmodeLHS.getAttribute("checked");
        Log("Check the Motion Mode check box status in Left-hand side of the Lifetouch graph is :" + isChecked);
        //uncheck the option checkbox
        driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions")).click();
    }

    public void removeBPMachine() {
        WebElement BPRemove;
        BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        String BPR = BPRemove.getText();
        Log("Button display for BP machine is " + BPR + " Button");
        Log("Click on the Remove BP button");
        BPRemove.click();
        Log("BP removed ");
    }
   public void removeWeightscale()
    {
       WebElement WeightScaleRemove;
       WeightScaleRemove=driver.findElement(By.id("buttonRemoveWeightScale"));
       String WEightScaleR=WeightScaleRemove.getText();
       Log("Button display for WeightSacle is " + WEightScaleR + " Button");
       Log("Click on the Remove WeightScale button");
       WeightScaleRemove.click();
       Log("WeightScale removed ");
    }
    public void removeNonin() {
        WebElement NoninRemove;
        NoninRemove = driver.findElement(By.id("buttonRemovePulseOximeter"));
        String NoninR = NoninRemove.getText();
        Log("Button display for Nonin is " + NoninR + " Button");
        Log("Click on the Remove Nonin button");
        NoninRemove.click();
        Log("Nonin removed ");
    }

    public void removeLTempDevice() {
        WebElement LifeTempRemove;
        LifeTempRemove = driver.findElement(By.id("buttonRemoveThermometer"));
        String LTempR = LifeTempRemove.getText();
        Log("Button display for lifetemp is " + LTempR + " Button");
        Log("Click on the Remove Lifetemp button");
        LifeTempRemove.click();
        Log("Lifetemp removed ");
    }

    public void removeLTDevice() {
        WebElement LifeTouchRemove;
        LifeTouchRemove = driver.findElement(By.id("buttonRemoveLifetouch"));
        String LTR = LifeTouchRemove.getText();
        Log("Button display for lifetouch is :" + LTR + " Button");
        Log("Click on the Remove Lifetouch button");
        LifeTouchRemove.click();
        Log("Lifetouch removed ");
    }
}
