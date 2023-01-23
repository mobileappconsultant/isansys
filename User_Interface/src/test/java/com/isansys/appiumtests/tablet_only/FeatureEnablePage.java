package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.Assert.assertTrue;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;


/**
 * Created by Soumya on 05/09/2017.
 */

public class FeatureEnablePage extends AppiumTest
{
    @Test
    public void testappearance_of_FeatureEnablePage_checkbox_and_its_status() throws IOException
    {

        String case_id = "22624";
        helper.printTestStart("testFeatureEnablePage",case_id);

        helper.spoofQrCode_featureEnableUnlock();

        //Checking Enable Manually  Entered Vital Signs
        //Checked it if not or Skip it if checked
        String isCheckedManuallyEnteredVitalSign;
        WebElement ManuallyEnteredVitalSigns = driver.findElement(By.id("checkBoxEnableManuallyEnteredVitalSigns"));
        assertTrue(ManuallyEnteredVitalSigns.isDisplayed());
        isCheckedManuallyEnteredVitalSign = ManuallyEnteredVitalSigns.getAttribute("checked");
        Log("Enable Manually  Entered Vital Signs Check box Status " + isCheckedManuallyEnteredVitalSign);
        if(isCheckedManuallyEnteredVitalSign.equals("false")) {
            ManuallyEnteredVitalSigns.click();
            Log("Manually  Entered Vital Signs checked");
        } else {
            Log("Manually  Entered Vital Signs already checked");
        }
        
        // Enable CSV output
        String isCheckedCSV;
        WebElement EnableCSVOutput = driver.findElement(By.id("checkBoxEnableCsvOutput"));
        assertTrue(EnableCSVOutput.isDisplayed());
        isCheckedCSV = EnableCSVOutput.getAttribute("checked");
        Log("Enable CSV Output Checkbox status " + isCheckedCSV);
        if(isCheckedCSV.equals("true")) {
            EnableCSVOutput.click();
            Log("Enable CSV Output not checked");
        } else {
            Log("Enable CSV Output already not checked");
        }

        //Show numbers on battery indicator
        //Checked it if not or Skip it if checked
        String isCheckedShowNumbersOnBatteryIndicator;
        WebElement ShowNumbersOnBatteryIndicator = driver.findElement(By.id("checkBoxShowNumbersOnBatteryIndicator"));
        assertTrue(ShowNumbersOnBatteryIndicator.isDisplayed());
        isCheckedShowNumbersOnBatteryIndicator = ShowNumbersOnBatteryIndicator.getAttribute("checked");
        Log("Show numbers on battery indicator Checkbox status " + isCheckedShowNumbersOnBatteryIndicator);
        if(isCheckedShowNumbersOnBatteryIndicator.equals("false")) {
            ShowNumbersOnBatteryIndicator.click();
            Log("Show numbers on battery indicator checked");
        } else {
            Log("Show numbers on battery indicator already checked");
        }
        //Show IP Address on Wifi popup
        //Checked it if not or Skip it if checked
        String isCheckedShowIPAddressOnWifiPopup;
        WebElement ShowIPAddressOnWifiPopup = driver.findElement(By.id("checkBoxShowIpAddressOnWifiPopup"));
        assertTrue(ShowIPAddressOnWifiPopup.isDisplayed());
        isCheckedShowIPAddressOnWifiPopup = ShowIPAddressOnWifiPopup.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowIPAddressOnWifiPopup);
        if(isCheckedShowIPAddressOnWifiPopup.equals("false")) {
            ShowIPAddressOnWifiPopup.click();
            Log("Show IP Address on Wifi popup checked");
        } else {
            Log("Show IP Address on Wifi popup  already checked");
        }

        //checkBoxRunDevicesInTestMode
        String isCheckedRunDeviceInTestMode;
        WebElement RunDeviceInTestMode = driver.findElement(By.id("checkBoxRunDevicesInTestMode"));
        assertTrue(RunDeviceInTestMode.isDisplayed());
        isCheckedRunDeviceInTestMode = RunDeviceInTestMode.getAttribute("checked");
        Log("RunDeviceTestMode Checkbox status " + isCheckedRunDeviceInTestMode);
        if (isCheckedRunDeviceInTestMode.equals("true")) {
            RunDeviceInTestMode.click();
            Log("RunDeviceTestMode Unchecked");
        } else {
            Log("RunDeviecTestMode already unchecked");
        }


        //SimpleHeartRate

        String isCheckedSimpleHeartRate;
        WebElement SimpleHeartRate = driver.findElement(By.id("checkBoxSimpleHeartRate"));
        assertTrue(SimpleHeartRate.isDisplayed());
        isCheckedSimpleHeartRate = SimpleHeartRate.getAttribute("checked");
        Log("SimpleHeartRate Checkbox status " + isCheckedSimpleHeartRate);
        if (isCheckedSimpleHeartRate.equals("true")) {
            SimpleHeartRate.click();
            Log("SimpleHeartRate Unchecked");
        } else {
            Log("SimpleHeartRate already unchecked");
        }

        //checkBoxEnableDeveloperPopup
        String isCheckedEnableDeveloperPopUp;
        WebElement EnableDeveloperPopUp = driver.findElement(By.id("checkBoxEnableDeveloperPopup"));
        assertTrue(EnableDeveloperPopUp.isDisplayed());
        isCheckedEnableDeveloperPopUp = EnableDeveloperPopUp.getAttribute("checked");
        Log("Enable developer pop up Checkbox status " + isCheckedEnableDeveloperPopUp);
        if (isCheckedEnableDeveloperPopUp.equals("false")) {
            EnableDeveloperPopUp.click();
            Log("Enable developer pop up checked");
        } else {
            Log("Enable developer Pop Up already checked");
        }
       //checkBoxEnableVideoCalls

        String isCheckBoxEnableVideoCalls;
        WebElement EnableVideoCalls = driver.findElement(By.id("checkBoxEnableVideoCalls"));
        assertTrue(EnableVideoCalls.isDisplayed());
        isCheckBoxEnableVideoCalls = EnableVideoCalls.getAttribute("checked");
        Log("EnableVideoCalls Checkbox status " + isCheckBoxEnableVideoCalls);
        if (isCheckBoxEnableVideoCalls.equals("true")) {
            EnableVideoCalls.click();
            Log("EnableVideoCalls Unchecked");
        } else {
            Log("EnableVideoCalls already unchecked");
        }

        //checkBoxShowMacAddressOnDeviceStatusScreen
        String isCheckedShowMacAddressOnDeviceStatusScreen;
        WebElement ShowMacAddressOnDeviceStatusScreen = driver.findElement(By.id("checkBoxShowMacAddressOnDeviceStatusScreen"));
        assertTrue(ShowMacAddressOnDeviceStatusScreen.isDisplayed());
        isCheckedShowMacAddressOnDeviceStatusScreen = ShowMacAddressOnDeviceStatusScreen.getAttribute("checked");
        Log("ShowMacAddressOnDeviceStatusScreen Checkbox status " + isCheckedShowMacAddressOnDeviceStatusScreen);
        if (isCheckedShowMacAddressOnDeviceStatusScreen.equals("true")) {
            ShowMacAddressOnDeviceStatusScreen.click();
            Log("ShowMacAddressOnDeviceStatusScreen Unchecked");
        } else {
            Log("ShowMacAddressOnDeviceStatusScreen already unchecked");
        }

        //USA Mode
        String isCheckedUSAmode;
        WebElement USAmode = driver.findElement(By.id("checkBoxUsaMode"));
        assertTrue(USAmode.isDisplayed());
        isCheckedUSAmode = USAmode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUSAmode);
        if (isCheckedUSAmode.equals("true")) {
            USAmode.click();
            Log("USA mode not checked");
        } else {
            Log("USA Mode already not checked");
        }


        //Lifetouch Activity Level
        //Checked it if not or Skip it if checked
        String isCheckedLifetouchActivityLevel;
        WebElement LifetouchActivityLevel = driver.findElement(By.id("checkBoxShowLifetouchActivityLevel"));
        assertTrue(LifetouchActivityLevel.isDisplayed());
        isCheckedLifetouchActivityLevel = LifetouchActivityLevel.getAttribute("checked");
        Log("Lifetouch Activity Level Checkbox status " + isCheckedLifetouchActivityLevel);
        if(isCheckedLifetouchActivityLevel.equals("false")) {
            LifetouchActivityLevel.click();
            Log("Lifetouch Activity Level checked");
        } else {
            Log("Lifetouch Activity Level already checked");
        }
        //Enable Session Auto Resume
        //Checked it if not or Skip it if checked
        String isCheckedEnableSessionAutoResume;
        WebElement EnableSessionAutoResume = driver.findElement(By.id("checkBoxEnableAutoResume"));
        assertTrue(EnableSessionAutoResume.isDisplayed());
        isCheckedEnableSessionAutoResume = EnableSessionAutoResume.getAttribute("checked");
        Log("Enable Session Auto Resume Checkbox status " + isCheckedEnableSessionAutoResume);
        if (isCheckedEnableSessionAutoResume.equals("false")) {
            EnableSessionAutoResume.click();
            Log("Enable Session Auto Resume checked");
        } else {
            Log("Enable Session Auto Resume already checked");
        }
        //EnableAutoLogFileUpdate
        String isCheckedEnableAutoLogFileUpdate;
        WebElement EnableAutoLogFileUpdate = driver.findElement(By.id("checkBoxEnableAutoLogFileUploadToServer"));
        assertTrue(EnableAutoLogFileUpdate.isDisplayed());
        isCheckedEnableAutoLogFileUpdate = EnableAutoLogFileUpdate.getAttribute("checked");
        Log("EnableAutoLogFileUpdate Checkbox status " + isCheckedEnableAutoLogFileUpdate);
        if (isCheckedEnableAutoLogFileUpdate.equals("true")) {
            EnableAutoLogFileUpdate.click();
            Log("EnableAutoLogFileUpdate checkbox Unchecked");
        } else {
            Log("EnableAutoLogFileUpdate checkbox already unchecked");
        }

        //Manufacturing Mode
        //Checked it if not or Skip it if unchecked
        String isCheckedManufacturingMode;
        WebElement ManufacturingMode = driver.findElement(By.id("checkBoxEnableManufacturingMode"));
        assertTrue(ManufacturingMode.isDisplayed());
        isCheckedManufacturingMode = ManufacturingMode.getAttribute("checked");
        Log("Manufacturing Mode Checkbox status " + isCheckedManufacturingMode);
        if (isCheckedManufacturingMode.equals("true")) {
            ManufacturingMode.click();
            Log("Manufacturing Mode Unchecked");
        } else {
            Log("Manufacturing Mode already unchecked");
        }


        //Back camera
        String isCheckedBackCamera;
        WebElement BackCamera = driver.findElement(By.id("checkBoxEnableBackCamera"));
        assertTrue(BackCamera.isDisplayed());
        isCheckedBackCamera = BackCamera.getAttribute("checked");
        Log("Backcamera Checkbox status " + isCheckedBackCamera);
        if (isCheckedBackCamera.equals("true")) {
            BackCamera.click();
            Log("BackCamera Unchecked");
        } else {
            Log("BackCamera already unchecked");
        }


        //checkBoxEnableWifiLogging
        String isCheckedEnableWifiLogging;
        WebElement EnableWifiLogging = driver.findElement(By.id("checkBoxEnableWifiLogging"));
        assertTrue(EnableWifiLogging.isDisplayed());
        isCheckedEnableWifiLogging = EnableWifiLogging.getAttribute("checked");
        Log("EnableWifiLogging Check box Status " + isCheckedEnableWifiLogging);
        if (isCheckedEnableWifiLogging.equals("false")) {
           EnableWifiLogging.click();
            Log("EnableWifiLogging checkbox is  checked");
        } else {
            Log("Enable WifiLogging checkbox already checked");
        }
        //Enable GSM logging
        String isCheckedEnableGSMLogging;
        WebElement EnableGSMlogging = driver.findElement(By.id("checkBoxEnableGsmLogging"));
        assertTrue(EnableGSMlogging.isDisplayed());
        isCheckedEnableGSMLogging = EnableGSMlogging.getAttribute("checked");
        Log("EnableGSM Logging Check box Status " + isCheckedEnableGSMLogging);
        if (isCheckedEnableGSMLogging.equals("false")) {
            EnableGSMlogging.click();
            Log("EnableGSM Logging checkbox is  checked");
        } else {
            Log("Enable GSM Logging checkbox already checked");
        }
        //checkBoxEnableDatabaseLogging
        String isCheckedEnableDatabaseLogging;
        WebElement EnableDatabaselogging = driver.findElement(By.id("checkBoxEnableDatabaseLogging"));
        assertTrue(EnableDatabaselogging.isDisplayed());
        isCheckedEnableDatabaseLogging = EnableDatabaselogging.getAttribute("checked");
        Log("Enable Database Logging Check box Status " + isCheckedEnableDatabaseLogging);
        if (isCheckedEnableDatabaseLogging.equals("false")) {
            EnableDatabaselogging.click();
            Log("Enable Database Logging checkbox is  checked");
        } else {
            Log("Enable Database Logging checkbox already checked");
        }
        //checkBoxEnableServerLogging
        String isCheckedEnableServerLogging;
        WebElement EnableServerlogging = driver.findElement(By.id("checkBoxEnableServerLogging"));
        assertTrue(EnableServerlogging.isDisplayed());
        isCheckedEnableServerLogging = EnableServerlogging.getAttribute("checked");
        Log("Enable Server Logging Check box Status " + isCheckedEnableServerLogging);
        if (isCheckedEnableServerLogging.equals("false")) {
            EnableServerlogging.click();
            Log("Enable Server Logging checkbox is  checked");
        } else {
            Log("Enable Server Logging checkbox already checked");
        }
        //checkBoxEnableBatteryLogging
        String isCheckedEnableBatteryLogging;
        WebElement EnableBatterylogging = driver.findElement(By.id("checkBoxEnableBatteryLogging"));
        assertTrue(EnableBatterylogging.isDisplayed());
        isCheckedEnableBatteryLogging = EnableBatterylogging.getAttribute("checked");
        Log("Enable Battery Logging Check box Status " + 0);
        if (isCheckedEnableBatteryLogging.equals("false")) {
            EnableBatterylogging.click();
            Log("Enable Battery Logging checkbox is  checked");
        } else {
            Log("Enable Battery Logging checkbox already checked");
        }


        //Select Lifetemp Measurement Interval from the spinner
        helper.clickElementOnSpinner("spinnerLifetempMeasurementInterval", helper.getAppString("one_minute"));

        // Enable Patient Orientation
        //Checked it if not or Skip it if checked
        String isChecked21;
        WebElement EnablePatientOrientation = driver.findElement(By.id("checkBoxDisplayPatientOrientation"));
        assertTrue(EnablePatientOrientation.isDisplayed());
        isChecked21 = EnablePatientOrientation.getAttribute("checked");
        Log("Enable Patient Orientation Checkbox status " + isChecked21);
        if(isChecked21.equals("false")) {
            EnablePatientOrientation.click();
            Log("Enable Patient Orientation checked");
        } else {
            Log("Enable Patient Orientation already checked");
        }
        //Select patient orientation interval from the spinner
        helper.clickElementOnSpinner("spinnerPatientOrientationMeasurementInterval", helper.getAppString("ten_minutes"));

        //Select JSON size from the spinner as 500
        helper.clickElementOnSpinner("spinnerJsonArraySize", "500");
        //helper.captureScreenShot();
        helper.updateTestRail(PASSED);

    }
    @Test
    public void Test_the_appearance_of_all_buttons_in_featureEnable_page() throws IOException {
        String case_id = "22625";
        helper.printTestStart("Test_the_appearance_of_all_buttons_in_featureEnable_page",case_id);
        helper.spoofQrCode_featureEnableUnlock();
        //Test the appearance of  SetDummyServerDetailsButton
        WebElement SetDummyServerDetailsButton=driver.findElement(By.id("buttonSetDummyServer"));
        assertTrue(SetDummyServerDetailsButton.isDisplayed());
        Log("Appearance of "+SetDummyServerDetailsButton.getText()+"Button is: "+SetDummyServerDetailsButton.isDisplayed());
        //Test the appearance of Unit Off button
        WebElement UnitOff= driver.findElement(By.id("buttonUnitRadioOff"));
        assertTrue(UnitOff.isDisplayed());
        Log("appearance of "+UnitOff.getText()+"Button is: "+UnitOff.isDisplayed());
        helper.updateTestRail(PASSED);
    }
    @Test
    public void Enable_manufacturing_Mode() throws IOException {
        String case_id = "22626";
        helper.printTestStart("Enable_manufacturing_Mode",case_id);
        helper.spoofQrCode_featureEnableUnlock();
        //Enable the manufacturing mode
        String isCheckedManufacturingMode;
        WebElement ManufacturingMode = driver.findElement(By.id("checkBoxEnableManufacturingMode"));
        assertTrue(ManufacturingMode.isDisplayed());
        isCheckedManufacturingMode = ManufacturingMode.getAttribute("checked");
        Log("Manufacturing Mode Checkbox status " + isCheckedManufacturingMode);
        if (isCheckedManufacturingMode.equals("false")) {
            ManufacturingMode.click();
            Log("Manufacturing Mode checked");
        } else {
            Log("Manufacturing Mode already checked");
        }

        //Click on Lock screen button
        WebElement LockScreen = driver.findElement(By.id("buttonLock"));
        LockScreen.click();
        Log("LockScreen button replaced by: "+LockScreen.getText());
        Log("Click on simulate QR Code  button");
        LockScreen.click();
        WebElement StartMonitoringApatient= driver.findElement(By.id("buttonBigButtonOne"));
        WebElement CheckDevicestatusButton= driver.findElement(By.id("buttonBigButtonTwo"));
        assertTrue(StartMonitoringApatient.isDisplayed());
        assertTrue(CheckDevicestatusButton.isDisplayed());
        Log("Lead us to the Starting page with out scanning the USER QR code ");
        LockScreen.click();

        //Disabe the manufacturing Mode
        Log("Uncheck the manufaturing mode check box");
        helper.spoofQrCode_featureEnableUnlock();
        driver.findElement(By.id("checkBoxEnableManufacturingMode")).click();
        LockScreen.click();
        Assert.assertTrue((driver.findElements(By.id("buttonLock")).size()==0));
        Log("No more simulate QR Code button Found ");
        helper.updateTestRail(PASSED);

    }
}

