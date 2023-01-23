package com.isansys.appiumtests.tablet_only;
import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;




public class CheckDeviceStatus  extends AppiumTest
{
    @Test
    public void TestTheUIForCheckDeviceStatusPage() throws IOException
    {
        String case_id = "C54561";
        helper.printTestStart("TestTheUIForCheckDeviceStatusPage",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("check_device_status").getText();
        String ExpectedTitle= helper.getAppString("check_device_status");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("Page heading is: "+ActualTitle);
        //Text appearing on the Scandevice page
        WebElement TextInScanDevicePage= driver.findElementById("textAddDevicesPageHelp");
        assertTrue(TextInScanDevicePage.isDisplayed());
        ActualTitle= TextInScanDevicePage.getText();
        ExpectedTitle=helper.getAppString("hold_qr_code_so_it_fits_in_the_smaller_lighter_square_on_the_left");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("Help Text appear on the ScanDeviceRQpage is :"+ActualTitle);
        //appearance of scanner
        WebElement Scanner= driver.findElement(By.id("check_device_status_qr_bar_code"));
        assertTrue(Scanner.isDisplayed());
        Log("Presence of QR code scanner is:"+Scanner.isDisplayed());
        //Test the appearance of back button
        WebElement Back= driver.findElement(By.id("buttonBack"));
        assertTrue(Back.isDisplayed());
        Log("Back button present");
        //Test the appearance of Lockscreen button
        WebElement LockScreen=driver.findElement(By.id("buttonLock"));
        assertTrue(LockScreen.isDisplayed());
        Log("Lockscreen button present");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void ScanLifetouchQRCodeNotPartOfanySession() throws IOException
    {
        String case_id = "C54562";
        helper.printTestStart("ScanLifetouchQRCodeNotPartOfanySession",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //Check device type
        WebElement DeviceType= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        Log("Device Type "+DeviceType.getText());
        //Check device Serial number
        WebElement SerialNumber= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        Log("Serial Number "+SerialNumber.getText());
        //Check device status
        WebElement DeviceStatus= driver.findElement(By.id("textViewDeviceUseStatus"));
        Log("DeviceStatus "+DeviceStatus.getText());
        // Shouldn't show ward and bed name
        assertEquals(0, driver.findElements(By.id("textViewDeviceWardNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusDeviceWardName")).size());
        //Bed name and label
        assertEquals(0, driver.findElements(By.id("textViewDeviceBedNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusBedName")).size());
        Log("No Ward and bed detail shown as Lifetouch is not part of any session");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void ScanLifetempQRCodeNotPartOfanySession() throws IOException
    {
        String case_id = "C54563";
        helper.printTestStart("ScanLifetempQRCodeNotPartOfanySession",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan a lifetemp
        helper.spoofQrCode_scanDummyDataLifetemp();
        //Check device type
        WebElement DeviceType= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        Log("Device Type "+DeviceType.getText());
        //Check device Serial number
        WebElement SerialNumber= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        Log("Serial Number "+SerialNumber.getText());
        //Check device status
        WebElement DeviceStatus= driver.findElement(By.id("textViewDeviceUseStatus"));
        Log("DeviceStatus "+DeviceStatus.getText());
        // Shouldn't show ward and bed name
        assertEquals(0, driver.findElements(By.id("textViewDeviceWardNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusDeviceWardName")).size());
        //Bed name and label
        assertEquals(0, driver.findElements(By.id("textViewDeviceBedNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusBedName")).size());
        Log("No Ward and bed detail shown as Lifetemp is not part of any session");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void ScanNoninQRCodeNotPartOfanySession() throws IOException
    {
        String case_id = "C54564";
        helper.printTestStart("ScanNoninQRCodeNotPartOfanySession",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan a Nonin
        helper.spoofQrCode_scanDummyDataNonin();
        //Check device type
        WebElement DeviceType= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        Log("Device Type "+DeviceType.getText());
        //Check device Serial number
        WebElement SerialNumber= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        Log("Serial Number "+SerialNumber.getText());
        //Check device status
        WebElement DeviceStatus= driver.findElement(By.id("textViewDeviceUseStatus"));
        Log("DeviceStatus "+DeviceStatus.getText());
        // Shouldn't show ward and bed name
        assertEquals(0, driver.findElements(By.id("textViewDeviceWardNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusDeviceWardName")).size());
        //Bed name and label
        assertEquals(0, driver.findElements(By.id("textViewDeviceBedNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusBedName")).size());
        Log("No Ward and bed detail shown as Nonin is not part of any session");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void ScanBLENoninQRCodeNotPartOfanySession() throws IOException
    {
        String case_id = "C54564";
        helper.printTestStart("ScanBLENoninQRCodeNotPartOfanySession",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan a Nonin
        helper.spoofQrCode_scanDummyDataBtleNonin();
        //Check device type
        WebElement DeviceType= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        Log("Device Type "+DeviceType.getText());
        //Check device Serial number
        WebElement SerialNumber= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        Log("Serial Number "+SerialNumber.getText());
        //Check device status
        WebElement DeviceStatus= driver.findElement(By.id("textViewDeviceUseStatus"));
        Log("DeviceStatus "+DeviceStatus.getText());
        // Shouldn't show ward and bed name
        assertEquals(0, driver.findElements(By.id("textViewDeviceWardNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusDeviceWardName")).size());
        //Bed name and label
        assertEquals(0, driver.findElements(By.id("textViewDeviceBedNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusBedName")).size());
        Log("No Ward and bed detail shown as Nonin is not part of any session");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void ScanAnDQRCodeNotPartOfanySession() throws IOException
    {
        String case_id = "C54566";
        helper.printTestStart("ScanAnDQRCodeNotPartOfanySession",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan a AnD
        helper.spoofQrCode_scanDummyDataAnD();
        //Check device type
        WebElement DeviceType= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        Log("Device Type "+DeviceType.getText());
        //Check device Serial number
        WebElement SerialNumber= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        Log("Serial Number "+SerialNumber.getText());
        //Check device status
        WebElement DeviceStatus= driver.findElement(By.id("textViewDeviceUseStatus"));
        Log("DeviceStatus "+DeviceStatus.getText());
        // Shouldn't show ward and bed name
        assertEquals(0, driver.findElements(By.id("textViewDeviceWardNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusDeviceWardName")).size());
        //Bed name and label
        assertEquals(0, driver.findElements(By.id("textViewDeviceBedNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusBedName")).size());
        Log("No Ward and bed detail shown as AnD is not part of any session");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void ScanWeightScaleQRCodeNotPartOfanySession() throws IOException
    {
        String case_id = "C54568";
        helper.printTestStart("ScanAnDQRCodeNotPartOfanySession",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan a WeightScale
        helper.spoofQrCode_scanDummyWeightScale();
        //Check device type
        WebElement DeviceType= driver.findElement(By.id("textViewChangeDeviceStatusDeviceType"));
        Log("Device Type "+DeviceType.getText());
        //Check device Serial number
        WebElement SerialNumber= driver.findElement(By.id("textViewDeviceHumanReadableSerialNumber"));
        Log("Serial Number "+SerialNumber.getText());
        //Check device status
        WebElement DeviceStatus= driver.findElement(By.id("textViewDeviceUseStatus"));
        Log("DeviceStatus "+DeviceStatus.getText());
        // Shouldn't show ward and bed name
        assertEquals(0, driver.findElements(By.id("textViewDeviceWardNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusDeviceWardName")).size());
        //Bed name and label
        assertEquals(0, driver.findElements(By.id("textViewDeviceBedNameLabel")).size());
        assertEquals(0, driver.findElements(By.id("textViewCheckDeviceStatusBedName")).size());
        Log("No Ward and bed detail shown as WeightScale is not part of any session");
        helper.updateTestRail(PASSED);

    }
    @Test
    public void PressingBackButtonLeadUsToTheModeSelectionPage() throws IOException
    {
        String case_id = "C54569";
        helper.printTestStart("PressingBackButtonLeadUsToTheModeSelectionPage",case_id);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();

        //Click on the Check device status button.

        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonTwo")), 1200);

        // Click on the CheckDevice status button
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("check_device_status").getText();
        String ExpectedTitle= helper.getAppString("check_device_status");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("Page heading is: "+ActualTitle);
        //Press back button
        WebElement Back= driver.findElement(By.id("buttonBack"));
        assertTrue(Back.isDisplayed());
        Log("Back button present");
        //Click on back button
        Log("Click on Back button");
        Back.click();
        //Check that lead to Mode selection page
        //Check the appearance of  Start monitoring a patient button
        WebElement start_monitoring = driver.findElement(By.id("buttonBigButtonOne"));
        junit.framework.Assert.assertTrue(start_monitoring.isDisplayed());
        Log("Lead us to Mode selection page");
        Log("Big button one is called: " + start_monitoring.getText());

        assertEquals(helper.getAppString("textStartMonitoringAPatient"), start_monitoring.getText());

        //Check the appearance of Device Status button
        WebElement device_status = driver.findElement(By.id("buttonBigButtonTwo"));
        junit.framework.Assert.assertTrue(device_status.isDisplayed());
        Log("Big button two is called: " + device_status.getText());

        assertEquals(helper.getAppString("textCheckDeviceStatus"), device_status.getText());
        helper.updateTestRail(PASSED);

    }
}
