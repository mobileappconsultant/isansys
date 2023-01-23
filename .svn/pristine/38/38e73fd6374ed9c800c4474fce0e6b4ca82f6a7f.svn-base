package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertTrue;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;


public class ChangeSessionSettings  extends AppiumTest
{
    @Test
    public void test_Button_In_Change_Session_Setting_Page() throws IOException
    {
        String case_id = "23200";
        helper.printTestStart("test_Button_In_Change_Session_Setting_Page",case_id);
        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        // Check AddLifetouch button is clickable
        driver.findElement(By.id("buttonAddLifetouch")).click();
        Log("Add lifetouch button is clickable");
        //Lead us to Scan Device QR codes page
        assertTrue(helper.findTextViewElementWithString("scan_device_qr_codes").isDisplayed());
        Log("Scan QR code page displayed");
        //Click on back button from Scan device QR code page
        clickOnBack();

        // Check buttonAddLife button is clickable
        driver.findElement(By.id("buttonAddLifetemp")).click();
        Log("Add Lifetemp button is clickable");
        //Lead us to Scan Device QR codes page
        assertTrue(helper.findTextViewElementWithString("scan_device_qr_codes").isDisplayed());
        Log("Scan QR code page displayed");
        //Click on back button from Scan device QR code page
        clickOnBack();

        // Check Add Nonin WristOx button is clickable
        driver.findElement(By.id("buttonAddPulseOx")).click();
        Log("Add Nonin WristOx button is clickable");
        //Lead us to Scan Device QR codes page
        assertTrue(helper.findTextViewElementWithString("scan_device_qr_codes").isDisplayed());
        Log("Scan QR code page displayed");
        //Click on back button from Scan device QR code page
        clickOnBack();

        // Check Add Blood Pressure Monitor button is clickable
        driver.findElement(By.id("buttonAddBloodPressure")).click();
        Log("Add Blood Pressure Monitor button is clickable");
        //Lead us to Scan Device QR codes page
        assertTrue(helper.findTextViewElementWithString("scan_device_qr_codes").isDisplayed());
        Log("Scan QR code page displayed");
        //Click on back button from Scan device QR code page
        clickOnBack();
        // Check Add Weight Scale button is clickable
        driver.findElement(By.id("buttonAddWeightScale")).click();
        Log("Add Weight Scale button is clickable");
        //Lead us to Scan Device QR codes page
        assertTrue(helper.findTextViewElementWithString("scan_device_qr_codes").isDisplayed());
        Log("Scan QR code page displayed");
        //Click on back button from Scan device QR code page
        clickOnBack();

        // Check Remove Early warning Scores is not  clickable
        WebElement removeEWS=driver.findElement(By.id("buttonChangeSessionSettingsRemoveEarlyWarningScores"));
        //Log(removeEWS.getTagName());

        assertTrue(removeEWS.isEnabled());
        Log("Remove EWS button is clickable");
        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_clicking_on_the_remove_button_remove_the_device_id_from_scan_device_QR_code_page() throws IOException
    {
        String case_id = "23201";
        helper.printTestStart("test_clicking_on_the_remove_button_remove_the_device_id_from_scan_device_QR_code_page",case_id);

        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        //scan the fake Lifetouch
        helper.spoofQrCode_scanFakeLifetouch();
        AddLifeTouch();
        //scan the fake Lifetemp
        helper.spoofQrCode_scanFakeLifetemp();
        AddLifeTemp();
        //scan the fake Nonin
        helper.spoofQrCode_scanFakeNonin();
        AddNonin();
        //scan the fake AnD
        helper.spoofQrCode_scanFakeAnD();
        addBPMachine();
        //scan the fake Weight Scale
        helper.spoofQrCode_scanFakeWeightScale();
        addWeightScale();
        Log("All the device got scan and Remove button appear for the scan devices");

        String six_dashes = helper.getAppString("six_dashes");

        //Click on Remove button for lifetouch
        Log("Click on the Remove button for Lifetouch");
        WebElement LifeTouchRemove= driver.findElement(By.id("buttonRemoveLifetouch"));
        LifeTouchRemove.click();
        String lifetouch_id = driver.findElement(By.id("textViewLifetouchHumanReadableSerialNumber")).getText();
        Log("Lifetouch id is " + lifetouch_id);
        Assert.assertEquals(six_dashes, lifetouch_id);

        // Click on Remove button for Lifetemp from ScanDevice page
        Log("Click on the Remove button for Lifetemp");
        WebElement LifeTempRemove = driver.findElement(By.id("buttonRemoveThermometer"));
        LifeTempRemove.click();
        String lifetemp_id = driver.findElement(By.id("textViewAddDevicesThermometerHumanReadableSerialNumber")).getText();
        Log("LifeTemp id is " + lifetemp_id);
        Assert.assertEquals(six_dashes, lifetemp_id);

        // Click on Remove button for Nonin from ScanDevice page
        Log("Click on the Remove button for Nonin");
        WebElement NoninRemove = driver.findElement(By.id("buttonRemovePulseOximeter"));
        NoninRemove.click();
        String nonin_id = driver.findElement(By.id("textViewPulseOximeterHumanReadableSerialNumber")).getText();
        Log("Nonin id is " + nonin_id);
        Assert.assertEquals(six_dashes, nonin_id);

        // Click on Remove button for BloodPressure from ScanDevice page
        Log("Click on the Remove button for BPMachine");
        WebElement BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        BPRemove.click();
        WebElement BPid= driver.findElement(By.id("textViewBloodPressureHumanReadableSerialNumber"));
        String BPMachine=BPid.getText();
        Log("BP machine id is " +BPMachine);
        Assert.assertEquals(six_dashes, BPMachine);


        // Click on Remove button for WeightScale from ScanDevice page
        Log("Click on the Remove button for WeightScale");
        WebElement WeightScaleRemove = driver.findElement(By.id("buttonRemoveScales"));
        WeightScaleRemove.click();
        WebElement WeightScaleid= driver.findElement(By.id("textViewScalesHumanReadableSerialNumber"));
        String WeightScale=WeightScaleid.getText();
        Log("WeightScale id is " + WeightScale);
        Assert.assertEquals(six_dashes, WeightScale);


        helper.updateTestRail(PASSED);
    }


    @Test
    public void test_Add_Devices_via_Change_session_setting_page_adding_5_device_together() throws IOException
    {
        String case_id = "23202";
        helper.printTestStart("test_Add_Devices_via_Change_session_setting_page_adding_5_device_together",case_id);
        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();
       // Click on Lifetouch button from change session setting page
        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();
        //LifeTemp
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();
        //Nonin
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();
        //BloodPressure
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        //weightScale
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();
        //Click on connect button
        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is "+ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is "+ABloodPressureMStatus);
        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is "+AWeightScaleStatus);

        clickOnStartMonitoring();

        Log("Start monitoring button appear after all devices got connected.");

        clickOnLock();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_removing_all_5_Devices_via_Change_session_setting_page_after_adding_5_device_together() throws IOException
    {
        String case_id = "23203";
        helper.printTestStart("test_By_removing_all_5_Devices_via_Change_session_setting_page_adding_5_device_together",case_id);
        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        //Scan the Lifetouch device manually
        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is "+ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the Weight Scale machine is " + AWeightScaleStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove lifetouch from change session setting page");
        changeSessionSettings_removeLifetouch(true);
        changeSessionSettings_removeLifetemp(true);

        removeNonin(true);
        removeBPMachine(true);
        removeWeightScale(true);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_removing_all_5_Devices_with_no_ews_session() throws IOException
    {
        String case_id = "57098";
        helper.printTestStart("test_By_removing_all_5_Devices_with_no_ews_session",case_id);

        // ensure auto-add ews is off
        checkAdminSettingsCorrect(false, false, true);

        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        //Scan the Lifetouch device manually
        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is "+ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the Weight Scale machine is " + AWeightScaleStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove lifetouch from change session setting page");
        changeSessionSettings_removeLifetouch(false);
        changeSessionSettings_removeLifetemp(false);

        removeNonin(false);
        removeBPMachine(false);
        removeWeightScale(false);

        // turn auto-add ews back on
        clickOnLock();
        checkAdminSettingsCorrect(false, true, true);

        helper.updateTestRail(PASSED);

    }


    @Test
    public void Test_to_check_the_apearance__of_device_serial_Number_and_Firmware_version_on_change_session_setting_page() throws IOException
    {
        String case_id = "23204";
        helper.printTestStart("Test_to_check_the_apearance__of_device_serial_Number_and_Firmware_version_on_change_session_setting_page",case_id);
        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        //Scan the Lifetouch device manually
        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is "+ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        WebElement Lifetouch_serial_number= driver.findElement(By.id("textViewChangeSessionSettingsLifetouchHumanReadableSerialNumber"));
        WebElement Lifetemp_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber"));
        WebElement Nonin_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsPulseOxHumanReadableSerialNumber"));
        WebElement Bloodpressure_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsBloodPressureHumanReadableSerialNumber"));
        WebElement WeightScale_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsWeightScaleHumanReadableSerialNumber"));

        WebElement Lifetouch_Firmware_version=driver.findElement(By.id("textViewChangeSessionSettingsLifetouchFirmwareVersion"));
        Log("Lifetouch serial number is:"+Lifetouch_serial_number.getText()+" and firmware version is : "+ Lifetouch_Firmware_version.getText());
        WebElement Lifetemp_Firmware_version=driver.findElement(By.id("textViewChangeSessionSettingsLifetempFirmwareVersion"));
        Log("Lifetemp serial number is: "+Lifetemp_serial_number.getText()+" and Lifetemp firmware version : "+ Lifetemp_Firmware_version.getText());
        WebElement Nonin_Firmware_version=driver.findElement(By.id("textViewChangeSessionSettingsPulseOxFirmwareVersion"));
        Log("Nonin Serial number is : "+Nonin_serial_number.getText()+" and Nonin firmware version : "+ Nonin_Firmware_version.getText());
        WebElement Bloodpressure_Firmware_version=driver.findElement(By.id("textViewChangeSessionSettingsBloodPressureFirmwareVersion"));
        Log("Bloodpressure device Serial number is "+Bloodpressure_serial_number.getText()+" and Bloodpressure firmware version : "+ Bloodpressure_Firmware_version.getText());
        WebElement WeightScale_Firmware_version=driver.findElement(By.id("textViewChangeSessionSettingsWeightScaleFirmwareVersion"));
        Log("WeightScale  Serial number is "+WeightScale_serial_number.getText()+" and Weight Scale firmware version : "+ WeightScale_Firmware_version.getText());

        helper.updateTestRail(PASSED);

    }



    @Test
    public void Test_to_compare_the_serial_number_for_the_devices_diplay_on_the_scan_devicepage_and__on_the_change_session_setting_page_are_same() throws IOException
    {
        String case_id = "23205";
        helper.printTestStart("Test_to_compare_the_serial_number_for_the_devices_diplay_on_the_scan_devicepage_and__on_the_change_session_setting_page_are_same",case_id);
        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        //Scan the Lifetouch device manually
        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();

        String Scandevice_lifetouch_id = driver.findElement(By.id("textViewLifetouchHumanReadableSerialNumber")).getText();
        String scandevice_lifetemp_id = driver.findElement(By.id("textViewAddDevicesThermometerHumanReadableSerialNumber")).getText();
        String scandevice_nonin_id = driver.findElement(By.id("textViewPulseOximeterHumanReadableSerialNumber")).getText();
        String scandevice_BP_id= driver.findElement(By.id("textViewBloodPressureHumanReadableSerialNumber")).getText();
        String scandevice_WeightScale_id= driver.findElement(By.id("textViewScalesHumanReadableSerialNumber")).getText();
        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is "+ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale  is " + AWeightScaleMStatus);

        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        String Lifetouch_serial_number= driver.findElement(By.id("textViewChangeSessionSettingsLifetouchHumanReadableSerialNumber")).getText();
        String  Lifetemp_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber")).getText();
        String Nonin_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsPulseOxHumanReadableSerialNumber")).getText();
        String Bloodpressure_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsBloodPressureHumanReadableSerialNumber")).getText();
        String WeightScale_serial_number= driver.findElement(By.id("textViewDeviceSpecificSettingsWeightScaleHumanReadableSerialNumber")).getText();

        Log("Lifetouch serial number on change session setting page is :"+ Lifetouch_serial_number);
        Log("Lifetemp serial number on change session setting page is: "+ Lifetemp_serial_number);
        Log("Nonin Serial number on change session setting page is : "+ Nonin_serial_number);
        Log("Bloodpressure device Serial number on change session setting page is "+ Bloodpressure_serial_number);
        Log("WeightScale device Serial number on change session setting page is "+ WeightScale_serial_number);

        Assert.assertEquals(Scandevice_lifetouch_id,Lifetouch_serial_number);
        Assert.assertEquals(scandevice_lifetemp_id,Lifetemp_serial_number);
        Assert.assertEquals(scandevice_nonin_id,Nonin_serial_number);
        Assert.assertEquals(scandevice_BP_id,Bloodpressure_serial_number);
        Assert.assertEquals(scandevice_WeightScale_id,WeightScale_serial_number);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_adding_and_connecting_Lifetouch_via_change_session_setting_page() throws IOException
    {
        String case_id = "23206";
        helper.printTestStart("test_By_adding_and_connecting_Lifetouch_via_change_session_setting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        Log("Click on the add Lifetouch button");
        driver.findElement(By.id("buttonAddLifetouch")).click();
        //Add a dummy lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        //check the logo
        WebElement lifetouchLogo=driver.findElement(By.id("logo_lifetouch"));
        assertTrue(lifetouchLogo.isDisplayed());
        Log("Status of the life touch is : " + ALTStatus);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Lifetouch got connected successfully");
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_removing_Lifetouch_from_changeSessionSetting_page() throws IOException
    {
        String case_id = "23301";
        helper.printTestStart("Test_By_removing_Lifetouch_from_changeSessionSetting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        Log("Click on the add Lifetouch button");
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is : " + ALTStatus);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Lifetouch got connected successfully");

        Log("Click on start monitoring button");
        clickOnNext();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        Log("Click on the Remove lifetouch from change session setting page");
        clickOnChangeSessionSettings();
        changeSessionSettings_removeLifetouch(true);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_adding_and_connecting_Lifetemp_via_change_session_setting_page() throws IOException
    {
        String case_id = "23207";
        helper.printTestStart("test_By_adding_and_connecting_Lifetemp_via_change_session_setting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddLifetemp")).click();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();

        clickOnConnect();

        //wait until start monitoring button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        //Get the status of lifetemp
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);

        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Lifetemp got connected successfully");
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_removing_Lifetemp_from_changeSessionSetting_page() throws IOException
    {
        String case_id = "23302";
        helper.printTestStart("Test_By_removing_Lifetouch_from_changeSessionSetting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddLifetemp")).click();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();

        clickOnConnect();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the life temp is " + ALTempStatus);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Lifetemp got connected successfully");

        Log("Click on start monitoring button");
        clickOnNext();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        Log("Click on the Remove lifetemp from change session setting page");
        clickOnChangeSessionSettings();
        changeSessionSettings_removeLifetemp(true);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_adding_and_connecting_Nonin_via_change_session_setting_page() throws IOException
    {
        String case_id = "23208";
        helper.printTestStart("test_By_adding_and_connecting_Nonin_via_change_session_setting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();

        clickOnConnect();

        //wait until start monitoring button appear
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonPulseOxCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        //Get the status of Nonin
        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);

        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Nonin got connected successfully");
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_adding_and_connecting_BLE_Nonin_via_change_session_setting_page() throws IOException
    {
        String case_id = "23209";
        helper.printTestStart("test_By_adding_and_connecting_BLE_Nonin_via_change_session_setting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataBtleNonin();
        AddNonin();

        clickOnConnect();

        //wait until start monitoring button appear
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonPulseOxCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        //Get the status of Nonin
        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);

        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Nonin got connected successfully");
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_removing_Nonin_from_changeSessionSetting_page() throws IOException
    {
        String case_id = "23303";
        helper.printTestStart("Test_By_removing_Nonin_from_changeSessionSetting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();

        clickOnConnect();

        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonPulseOxCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Nonin got connected successfully");

        Log("Click on start monitoring button");
        clickOnNext();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the Remove Nonin from change session setting page");
        removeNonin(true);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_removing__BLE_Nonin_from_changeSessionSetting_page() throws IOException
    {
        String case_id = "23304";
        helper.printTestStart("Test_By_removing_BLENonin_from_changeSessionSetting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddPulseOx")).click();
        helper.spoofQrCode_scanDummyDataBtleNonin();
        AddNonin();

        clickOnConnect();

        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonPulseOxCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + noninConnectionStatus);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after Nonin got connected successfully");

        Log("Click on start monitoring button");
        clickOnNext();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the Remove Nonin from change session setting page");
        removeNonin(true);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void test_By_adding_and_connecting_BPMachine_via_change_session_setting_page() throws IOException
    {
        String case_id = "23305";
        helper.printTestStart("test_By_adding_and_connecting_BPMachine_via_change_session_setting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddBloodPressure")).click();
        helper.spoofQrCode_scanDummyDataAnD();
        //Scan the Nonin device manually
        addBPMachine();
        String ButtonName = driver.findElement(By.id("buttonNext")).getText();

        Assert.assertEquals(ButtonName, helper.getAppString("textConnect"));
        Log("Click on connect button");
        clickOnNext();

        //wait until start monitoring button appear
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonBloodPressureCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        //Get the status of Nonin
        String BPConnectionStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine  is " + BPConnectionStatus);

        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after BP got connected successfully");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_By_removing_Blood_Pressure_Device_from_changeSessionSetting_page() throws IOException
    {
        String case_id = "23210";
        helper.printTestStart("test_By_removing_Blood_Pressure_Device_from_changeSessionSetting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        //Click on Add BP button
        driver.findElement(By.id("buttonAddBloodPressure")).click();
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        String ButtonName = driver.findElement(By.id("buttonNext")).getText();
        Assert.assertEquals(ButtonName, helper.getAppString("textConnect"));
        Log("Click on connect button");
        clickOnNext();

        //Wait until the appearance of either search again button or start monitoring button;
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonBloodPressureCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        //Get the status of Bp device
        String BPConnectionStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the Blood Pressure machine is : " + BPConnectionStatus);

        String bp_not_found = helper.getAppString("textBloodPressureNotFound");
        String bp_pairing_failure = helper.getAppString("textBloodPressurePairingFailed");
        //Check Bloodepressure reconnection process
        while ((BPConnectionStatus.equals(bp_not_found)) || (BPConnectionStatus.equals(bp_pairing_failure))) {
            Log("Click on the search again button");
            driver.findElement(By.id("buttonBloodPressureCancelSearchOrSearchAgain")).click();

            waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonBloodPressureCancelSearchOrSearchAgain")),
                    ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                    , 45);

            BPConnectionStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();

            Log("Status of the Blood Pressure device is : " + BPConnectionStatus);

        }

        //Click on the start monitoring button after BP connected
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Click on the start monitor button afterBlood Pressure device got connected");
        clickOnNext();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the Remove Blood Pressure from change session setting page");
        removeBPMachine(true);
        helper.updateTestRail(PASSED);
    }
    @Test
    public void test_By_adding_and_connecting_WeightScale_via_change_session_setting_page() throws IOException
    {
        String case_id = "23212";
        helper.printTestStart("test_By_adding_and_connecting_WeightScale_via_change_session_setting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddWeightScale")).click();
        helper.spoofQrCode_scanDummyWeightScale();
        //Scan the WeightScale device manually
        addWeightScale();
        String ButtonName = driver.findElement(By.id("buttonNext")).getText();

        Assert.assertEquals(ButtonName, helper.getAppString("textConnect"));
        Log("Click on connect button");
        clickOnNext();

        //wait until start monitoring button appear
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonWeightScaleCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        //Get the status of WeightScale
        String WeightScaleConnectionStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the Weight Scale is " + WeightScaleConnectionStatus);

        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after WeightScale got connected successfully");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void test_By_removing_WeightScale_from_changeSessionSetting_page() throws IOException
    {
        String case_id = "23211 ";
        helper.printTestStart("Test_By_removing_WeightScale_from_changeSessionSetting_page",case_id);
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();
        Log("Lead us to Mode selection page ");

        clickOnChangeSessionSettings();

        driver.findElement(By.id("buttonAddWeightScale")).click();
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();

        clickOnConnect();

        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonWeightScaleCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")))
                , 45);
        String WeightScaleConnectionStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + WeightScaleConnectionStatus);
        assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Start monitoring button appear on the connection page after WeightScale got connected successfully");

        Log("Click on start monitoring button");
        clickOnNext();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();
        Log("Click on the Remove WeightScale from change session setting page");
        removeWeightScale(true);
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Check_the_appearance_of_correct_Logo_for_the_devices_in_conncetion_page() throws IOException
    {
        String case_id = "23213";
        helper.printTestStart("Check_the_appearance_of_correct_Logo_for_the_devices_in_conncetion_page",case_id);
        //scan User Qr code
        setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly();

        clickOnChangeSessionSettings();

        //Scan the Lifetouch device manually
        Log("Click on the add Lifetouch button");
        //Lifetouch
        driver.findElement(By.id("buttonAddLifetouch")).click();
        helper.spoofQrCode_scanDummyDataLifetouch();
        AddLifeTouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        AddLifeTemp();
        helper.spoofQrCode_scanDummyDataNonin();
        AddNonin();
        helper.spoofQrCode_scanDummyDataAnD();
        addBPMachine();
        helper.spoofQrCode_scanDummyWeightScale();
        addWeightScale();

        clickOnConnect();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);

        assertTrue(driver.findElement(By.id("fragment_main")).isDisplayed());
        Log("lead us to the Connection page");
        Log("Check the appearance of log for the scan devices ");
        //Check the appearance of Lifetouch log
        WebElement LifetouchLog=driver.findElement(By.id("logo_lifetouch"));
        assertTrue(LifetouchLog.isDisplayed());

        //Check the appearance of Lifetemp logo
        WebElement LifetempLog=driver.findElement(By.id("logo_lifetemp"));
        assertTrue(LifetempLog.isDisplayed());

        //Check the appearance of Nonin logo
        WebElement NoninLog=driver.findElement(By.id("logo_pulse_ox"));
        assertTrue(NoninLog.isDisplayed());

        //Check the appearance of Bloodpressure logo
        WebElement BloodpressureLog=driver.findElement(By.id("logo_blood_pressure"));
        assertTrue(BloodpressureLog.isDisplayed());

       //Check the appearance of WeightScale logo
        WebElement WeightScaleLogo=driver.findElement(By.id("logo_lifetouch"));
        assertTrue(WeightScaleLogo.isDisplayed());
        clickOnLock();
        helper.updateTestRail(PASSED);
    }


    private void setUpSessionWithAdultAgeRangeAndSelectManualVitalsOnly() throws IOException
    {
        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // Enter the back button from Observation page
        clickOnBack();
    }


    private void AddLifeTouch()
    {
        WebElement LifeTouchRemove= driver.findElement(By.id("buttonRemoveLifetouch"));
        assertTrue(LifeTouchRemove.isDisplayed());
        String LTR=LifeTouchRemove.getText();
        Log("After scanning the Lifetouch, status of button is "+LTR);
        String lifetouch_id = driver.findElement(By.id("textViewLifetouchHumanReadableSerialNumber")).getText();
        Log("Lifetouch id is " + lifetouch_id);
    }


    private void AddLifeTemp()
    {
        //scan lifeTemp
        WebElement LifeTempRemove = driver.findElement(By.id("buttonRemoveThermometer"));
        assertTrue(LifeTempRemove.isDisplayed());
        String LTempR=LifeTempRemove.getText();
        Log("After scanning the Lifetemp, button status is "+LTempR);
        String lifetemp_id = driver.findElement(By.id("textViewAddDevicesThermometerHumanReadableSerialNumber")).getText();
        Log("LifeTemp id is " + lifetemp_id);
    }


    private void AddNonin()
    {
        //Scan Nonin
        WebElement NoninRemove = driver.findElement(By.id("buttonRemovePulseOximeter"));
        assertTrue(NoninRemove.isDisplayed());
        String NoninR = NoninRemove.getText();
        Log("After scanning the Nonin the button status is "+NoninR);
        String nonin_id = driver.findElement(By.id("textViewPulseOximeterHumanReadableSerialNumber")).getText();
        Log("Nonin id is " + nonin_id);
    }


    private void addBPMachine()
    {
        //Scan BP machine
        WebElement BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        assertTrue(BPRemove.isDisplayed());
        String BPR = BPRemove.getText();
        Log("After scanning the Bp machine the button status is "+BPR);
        WebElement BPid= driver.findElement(By.id("textViewBloodPressureHumanReadableSerialNumber"));
        String BPMachine=BPid.getText();
        Log("BP machine id is " +BPMachine);
    }
    private void addWeightScale()
    {
        //Scan WeightScale
        WebElement WeightScaleRemove = driver.findElement(By.id("buttonRemoveScales"));
        assertTrue(WeightScaleRemove.isDisplayed());
        String WeightScaleR = WeightScaleRemove.getText();
        Log("After scanning the Weight Scale  the button status is "+WeightScaleR);
        WebElement WeightSacleid= driver.findElement(By.id("textViewScalesHumanReadableSerialNumber"));
        String WeightScale_id=WeightSacleid.getText();
        Log("WeightScale id is " +WeightScale_id);
    }

    protected void changeSessionSettings_removeLifetouch(boolean expect_ews_popup)
    {
        //driver.findElement(By.id("buttonBigButtonTwo")).click();
        WebElement LifeTouchRemove = driver.findElement(By.id("buttonRemoveLifetouch"));
        assertTrue(LifeTouchRemove.isDisplayed());
        String LTR=LifeTouchRemove.getText();
        Log("Button display for lifetouch is :"+LTR+" Button");
        Log("Click on the Remove Lifetouch button");
        LifeTouchRemove.click();

        if(expect_ews_popup)
        {
            checkRemoveDeviceEwsPopup(true);
        }

        checkRecyclingReminderPopup();

        WebElement Lifetouchdisconnection = driver.findElement(By.id("LifetouchChangeSessionDisconnectTextView"));
        //String LifetouchDisconnectionMessage= Lifetouchdisconnection.getText();
        assertTrue(Lifetouchdisconnection.isDisplayed());
        //Log("Liftouch disconnection message: "+LifetouchDisconnectionMessage);

        Log("Lifetouch removed ");
    }


    protected void changeSessionSettings_removeLifetemp(boolean expect_ews_popup)
    {
        //scan lifeTemp
        WebElement LifeTempRemove = driver.findElement(By.id("buttonRemoveThermometer"));
        assertTrue(LifeTempRemove.isDisplayed());
        String LTempR=LifeTempRemove.getText();
        Log("Button display for lifetemp is "+LTempR+" Button");
        Log("Click on the Remove Lifetemp button");
        LifeTempRemove.click();

        if(expect_ews_popup)
        {
            checkRemoveDeviceEwsPopup(true);
        }

        checkRecyclingReminderPopup();

        WebElement Lifetempdisconnection = driver.findElement(By.id("LifetempChangeSessionDisconnectTextView"));
        assertTrue(Lifetempdisconnection.isDisplayed());
        //Log("Liftemp disconnection message: "+Lifetempdisconnection.getText());
        Log("Lifetemp removed ");
    }


    private void removeNonin(boolean expect_ews_popup)
    {
        //Scan Nonin
        WebElement NoninRemove = driver.findElement(By.id("buttonRemovePulseOximeter"));
        assertTrue(NoninRemove.isDisplayed());
        String NoninR = NoninRemove.getText();
        Log("Button display for Nonin is "+NoninR+" Button");
        Log("Click on the Remove Nonin button");
        NoninRemove.click();
        Log("Nonin removed ");
        if(expect_ews_popup)
        {
            checkRemoveDeviceEwsPopup(true);
        }
    }


    private void removeBPMachine(boolean expect_ews_popup)
    {
        //Scan BP machine
        WebElement BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        assertTrue(BPRemove.isDisplayed());
        String BPR = BPRemove.getText();
        Log("Button display for BP machine is " + BPR + " Button");
        Log("Click on the Remove BP button");
        BPRemove.click();
        Log("BP removed ");
        if(expect_ews_popup)
        {
            checkRemoveDeviceEwsPopup(true);
        }
    }


    private void removeWeightScale(boolean expect_ews_popup)
    {
        WebElement WeightScaleRemove = driver.findElement(By.id("buttonRemoveWeightScale"));
        assertTrue(WeightScaleRemove.isDisplayed());
        String WeightScaleR = WeightScaleRemove.getText();
        Log("Button display for  Weight scale  is " + WeightScaleR + " Button");
        Log("Click on the Remove Weight scale button");
        WeightScaleRemove.click();
        Log("Weight scale removed ");
        if(expect_ews_popup)
        {
            checkRemoveDeviceEwsPopup(true);
        }
    }
}

