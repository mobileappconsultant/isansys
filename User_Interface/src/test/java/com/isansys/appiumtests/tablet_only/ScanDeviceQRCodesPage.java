package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class ScanDeviceQRCodesPage extends AppiumTest {
    @Test
    public void Test_the_UI_For_Scan_Device_QR_Code_page() throws IOException
    {
        String case_id = "22582";
        helper.printTestStart("Test_the_UI_For_Scan_Device_QR_Code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("scan_device_qr_codes").getText();
        String ExpectedTitle= helper.getAppString("scan_device_qr_codes");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("Page heading is: "+ActualTitle);
        //Text appearing on the Scandevice page
        WebElement TextInScanDevicePage= driver.findElementById("textAddDevicesPageHelp");
        assertTrue(TextInScanDevicePage.isDisplayed());
        ActualTitle= TextInScanDevicePage.getText();
        ExpectedTitle=helper.getAppString("hold_qr_code_so_it_fits_in_the_smaller_lighter_square_on_the_left");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("Help Text appear on the ScanDeviceRQpage is :"+ActualTitle);
        //Lifetouch
        WebElement lifeTouch= driver.findElement(By.id("textViewLifetouch"));
        assertTrue(lifeTouch.isDisplayed());
        Log(lifeTouch.getText());
        //No id
        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        String ActualValue=LifetouchID.getText();
        String ExpectedValue= helper.getAppString("six_dashes");
        Assert.assertEquals(ActualValue,ExpectedValue);
        Log("Value displayed for Lifetouch is: "+ActualValue);
        //Lifetemp
        WebElement lifeTemp= driver.findElement(By.id("textViewThermometer"));
        assertTrue(lifeTemp.isDisplayed());
        Log(lifeTemp.getText());
        //No id
        WebElement LifetempID= driver.findElementById("textViewAddDevicesThermometerHumanReadableSerialNumber");
        ActualValue=LifetempID.getText();
        ExpectedValue= helper.getAppString("six_dashes");
        Assert.assertEquals(ActualValue,ExpectedValue);
        Log("Value displayed for Lifetemp is: "+ActualValue);
        //Nonin
        WebElement Nonin= driver.findElement(By.id("textViewPulseOximeter"));
        assertTrue(Nonin.isDisplayed());
        Log(Nonin.getText());
        //No id
        WebElement NoninID= driver.findElementById("textViewPulseOximeterHumanReadableSerialNumber");
        ActualValue=NoninID.getText();
        ExpectedValue= helper.getAppString("six_dashes");
        Assert.assertEquals(ActualValue,ExpectedValue);
        Log("Value displayed for Nonin is: "+ActualValue);
        //Bloodpressuer
        WebElement BloodPressure= driver.findElement(By.id("textViewBloodPressure"));
        assertTrue(BloodPressure.isDisplayed());
        Log(BloodPressure.getText());
        //No id
        WebElement BPID= driver.findElementById("textViewBloodPressureHumanReadableSerialNumber");
        ActualValue=BPID.getText();
        ExpectedValue= helper.getAppString("six_dashes");
        Assert.assertEquals(ActualValue,ExpectedValue);
        Log("Value displayed for BP is: "+ActualValue);
        //Weight Scale
        WebElement WeightScale= driver.findElement(By.id("textViewScales"));
        assertTrue(WeightScale.isDisplayed());
        Log(WeightScale.getText());
        //No id
        WebElement WightScaleID= driver.findElementById("textViewScalesHumanReadableSerialNumber");
        ActualValue=WightScaleID.getText();
        ExpectedValue= helper.getAppString("six_dashes");
        Assert.assertEquals(ActualValue,ExpectedValue);
        Log("Value displayed for WeightScale is: "+ActualValue);

        //Ews
        WebElement EWS= driver.findElement(By.id("textViewEarlyWarningScores"));
        assertTrue(EWS.isDisplayed());
        Log("EWS status is showing as "+ EWS.getText());

        //Text appearance
        WebElement manual_vitals_only = driver.findElement(By.id("linearLayoutManualVitalsOnly"));
        String ActualText = manual_vitals_only.findElement(By.className("android.widget.TextView")).getText();  //helper.findTextViewElementWithString("or_press_manual_vitals_only").getText();

        Log("Text appear is:"+ActualText);
        String ExpectedText= helper.getAppString("or_press_manual_vitals_only");
        Assert.assertEquals(ActualText,ExpectedText);

        //appearance of manual vitals only button
        WebElement ManualVitalOnlyButton= driver.findElement(By.id("buttonManualVitalsOnly"));
        assertTrue(ManualVitalOnlyButton.isDisplayed());
        Log(ManualVitalOnlyButton.getText()+" Status is "+ManualVitalOnlyButton.isDisplayed() );
        //appearance of scanner
        WebElement Scanner= driver.findElement(By.id("qr_bar_code"));
        assertTrue(Scanner.isDisplayed());
        Log("Presence of QR code scanner is:"+Scanner.isDisplayed());
        //Test the appearance of back button
        WebElement Back= driver.findElement(By.id("buttonBack"));
        assertTrue(Back.isDisplayed());
        helper.updateTestRail(PASSED);
    }


    @Test
    public void Test_the_appearance_of_LifeTouch_Id_and_remove_button_after_scanning_the_device() throws IOException {
        String case_id = "22583";
        helper.printTestStart("Test_the_appearance_of_LifeTouch_Id_and_remove_button_after_scanning_the_device",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        String LifetouchIdBeforeScanning=LifetouchID.getText();
        Log("Value displayed for Lifetouch is: "+LifetouchIdBeforeScanning);
        assertTrue((driver.findElements(By.id("buttonRemoveLifetouch")).size()==0));
        assertTrue((driver.findElements(By.id("viewLifetouchGoodToUse")).size()==0));
        Log("No remove button or Lifetouch use indicator appeared befor scanning the device");
        Log("Scan a Lifetouch");
        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveLifetouch")),30);
        String lifetouch_id_after_scanning = LifetouchID.getText();
        Log(" Lifetouch ID is: " + lifetouch_id_after_scanning);
        assertNotSame(LifetouchIdBeforeScanning, lifetouch_id_after_scanning);
        //Appearance of Remove button
        WebElement RemoveButton = driver.findElement(By.id("buttonRemoveLifetouch"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:" + RemoveButton.isDisplayed());
        //Lifetouch indicator
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("viewLifetouchGoodToUse")), 45);
        WebElement LifetouchIndicator = driver.findElement(By.id("viewLifetouchGoodToUse"));
        assertTrue(LifetouchIndicator.isDisplayed());
        Log("Lifetouch Indicator appearance status is:" + LifetouchIndicator.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Test_the_appearance_of_LifeTemp_Id_and_remove_button_after_scanning_the_device() throws IOException {
        String case_id = "22584";
        helper.printTestStart("Test_the_appearance_of_LifeTemp_Id_and_remove_button_after_scanning_the_device",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement LifetempID= driver.findElementById("textViewAddDevicesThermometerHumanReadableSerialNumber");
        String LifetempIdBeforeScanning=LifetempID.getText();
        Log("Value displayed for Lifetemp before device scan is : "+LifetempIdBeforeScanning);
        assertTrue((driver.findElements(By.id("buttonRemoveThermometer")).size()==0));
        assertTrue((driver.findElements(By.id("viewThermometerGoodToUse")).size()==0));
        Log("No remove button or Lifetemp use indicator appeared befor scanning the device");
        //Scan a lifetemp
        Log("Scan a Lifetemp");
        helper.spoofQrCode_scanDummyDataLifetemp();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveThermometer")), 45);
        String LifetempIdafterScanning=LifetempID.getText();
        Log(" Lifetemp Id is: "+LifetempIdafterScanning);
        assertNotSame(LifetempIdafterScanning,LifetempIdBeforeScanning);
        //Appearance of Remove button
        WebElement RemoveButton=driver.findElement(By.id("buttonRemoveThermometer"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:"+RemoveButton.isDisplayed());
        //Thermometer indicator
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("viewThermometerGoodToUse")), 45);
        WebElement LifetempIndicator= driver.findElement(By.id("viewThermometerGoodToUse"));
        assertTrue(LifetempIndicator.isDisplayed());
        Log("Lifetemp Indicator appearance status is:"+LifetempIndicator.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Test_the_appearance_of_Nonin_Id_and_remove_button_after_scanning_the_device() throws IOException {
        String case_id = "22585";
        helper.printTestStart("Test_the_appearance_of_Nonin_Id_and_remove_button_after_scanning_the_device",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement NoninID= driver.findElementById("textViewPulseOximeterHumanReadableSerialNumber");
        String NoninIdBeforeScanning=NoninID.getText();
        Log("Value displayed for Nonin before device scan is : "+NoninIdBeforeScanning);
        assertTrue((driver.findElements(By.id("buttonRemovePulseOximeter")).size()==0));
        assertTrue((driver.findElements(By.id("viewPulseOximeterGoodToUse")).size()==0));
        Log("No remove button or Nonin use indicator appeared befor scanning the device");
        //Scan a Nonin
        Log("Scan a Nonin");
        helper.spoofQrCode_scanDummyDataNonin();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemovePulseOximeter")), 45);
        String NoninIdafterScanning=NoninID.getText();
        Log("Value displayed for Nonin is: "+NoninIdafterScanning);
        assertNotSame(NoninIdafterScanning,NoninIdBeforeScanning);
        //Appearance of Remove button
        WebElement RemoveButton=driver.findElement(By.id("buttonRemovePulseOximeter"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:"+RemoveButton.isDisplayed());
        //Nonin indicator
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("viewPulseOximeterGoodToUse")), 45);
        WebElement NoninIndicator= driver.findElement(By.id("viewPulseOximeterGoodToUse"));
        assertTrue(NoninIndicator.isDisplayed());
        Log("Nonin Indicator appearance status is:"+NoninIndicator.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Test_the_appearance_of_Bloodpressure_Id_and_remove_button_after_scanning_the_device() throws IOException {
        String case_id = "22586";
        helper.printTestStart("Test_the_appearance_of_Bloodpressure_Id_and_remove_button_after_scanning_the_device",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement BPID= driver.findElementById("textViewBloodPressureHumanReadableSerialNumber");
        String BpIdBeforeScanning=BPID.getText();
        Log("Value displayed for Blood Pressure device before device scan is : "+BpIdBeforeScanning);
        assertTrue((driver.findElements(By.id("buttonRemoveBloodPressure")).size()==0));
        assertTrue((driver.findElements(By.id("viewBloodPressureGoodToUse")).size()==0));
        Log("No remove button or BP use indicator appeared befor scanning the device");
        //Scan a BP
        Log("Scan a Blood Pressure device");
        helper.spoofQrCode_scanDummyDataAnD();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveBloodPressure")), 45);
        String BPIdafterScanning=BPID.getText();
        Log("Value displayed for BP is: "+BPIdafterScanning);
        assertNotSame(BPIdafterScanning,BpIdBeforeScanning);
        //Appearance of Remove button
        WebElement RemoveButton=driver.findElement(By.id("buttonRemoveBloodPressure"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:"+RemoveButton.isDisplayed());
        //BP indicator
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("viewBloodPressureGoodToUse")), 45);
        WebElement BPIndicator= driver.findElement(By.id("viewBloodPressureGoodToUse"));
        assertTrue(BPIndicator.isDisplayed());
        Log("BP Indicator appearance status is:"+BPIndicator.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void Test_the_appearance_of_WeightScale_Id_and_remove_button_after_scanning_the_device() throws IOException {
        String case_id = "54545";
        helper.printTestStart("Test_the_appearance_of_WeightScale_Id_and_remove_button_after_scanning_the_device",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement WeightScaleID= driver.findElementById("textViewScalesHumanReadableSerialNumber");
        String WeightScaleIdBeforeScanning=WeightScaleID.getText();
        Log("Value displayed for WeightScale is: "+WeightScaleIdBeforeScanning);
        assertTrue((driver.findElements(By.id("buttonRemoveScales")).size()==0));
        assertTrue((driver.findElements(By.id("viewScalesGoodToUse")).size()==0));
        Log("No remove button or WeightScale use indicator appeared before scanning the device");
        Log("Scan a WeightScale");
        //Scan a WeightScale
        helper.spoofQrCode_scanDummyWeightScale();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveScales")),30);
        String WeightScale_id_after_scanning = WeightScaleID.getText();
        Log(" WeightScale ID is: " + WeightScale_id_after_scanning);
        assertNotSame(WeightScaleIdBeforeScanning, WeightScale_id_after_scanning);
        //Appearance of Remove button
        WebElement RemoveButton = driver.findElement(By.id("buttonRemoveScales"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:" + RemoveButton.isDisplayed());
        //WeightScale indicator
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("viewScalesGoodToUse")), 45);
        WebElement WeightScaleIndicator = driver.findElement(By.id("viewScalesGoodToUse"));
        assertTrue(WeightScaleIndicator.isDisplayed());
        Log("WeightScaleIndicator Indicator appearance status is:" + WeightScaleIndicator.isDisplayed());
        helper.updateTestRail(PASSED);
    }


    @Test
    public void Test_the_appearance_of_connect_button_after_scanning_a_device() throws IOException {
        String case_id = "22587";
        helper.printTestStart("Test_the_appearance_of_connect_button_after_scanning_a_device",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        //check the status of connect button befor device scan
        assertTrue((driver.findElements(By.id("buttonNext")).size()==0));
        Log("No Connect button appeared befor scanning the device");
        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveLifetouch")), 45);
        String lifetouch_id_after_scanning=LifetouchID.getText();
        Log("Value displayed for Lifetouch is: "+lifetouch_id_after_scanning);
        WebElement RemoveButton=driver.findElement(By.id("buttonRemoveLifetouch"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:"+RemoveButton.isDisplayed());
        //Lifetouch indicator
        WebElement LifetouchIndicator= driver.findElement(By.id("viewLifetouchGoodToUse"));
        assertTrue(LifetouchIndicator.isDisplayed());
        Log("Lifetouch Indicator appearance status is:"+LifetouchIndicator.isDisplayed());
        //Connect button
        WebElement Connect= driver.findElement(By.id("buttonNext"));
        assertTrue(Connect.isDisplayed());
        Log("Connection button appearance status is :"+Connect.isDisplayed());
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_by_clicking_on_the_Lifetouch_remove_button_remove_the_device_id_and_Remove_button_from_scan_device_QR_code_page() throws IOException {
        String case_id = "22588";
        helper.printTestStart("test_by_clicking_on_the_Lifetouch_remove_button_remove_the_device_id_and_Remove_button_from_scan_device_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveLifetouch")), 45);
        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        String LifetouchIdBeforeScanning=LifetouchID.getText();
        Log("Lifetouch ID is: "+LifetouchIdBeforeScanning);
        WebElement Removebutton= driver.findElementById("buttonRemoveLifetouch");
        assertTrue(Removebutton.isDisplayed());
        Log("Remove button appearance status is:"+Removebutton.isDisplayed());
        WebElement LifetouchIndicator= driver.findElement(By.id("viewLifetouchGoodToUse"));
        assertTrue(LifetouchIndicator.isDisplayed());
        Log("Lifetouch Indicator appearance status is:"+LifetouchIndicator.isDisplayed());
        //Click on the Remove button
        Log("Click on the Lifetouch remove button");
        Removebutton.click();
        Log("Lifetouch ID is: "+LifetouchID.getText());
        assertTrue((driver.findElements(By.id("buttonRemoveLifetouch")).size()==0));
        assertTrue((driver.findElements(By.id("viewLifetouchGoodToUse")).size()==0));
        Log("No remove button or Lifetouch use indicator appeared after removing the device ID");
        helper.updateTestRail(PASSED);

    }

    @Test
    public void test_by_clicking_on_the_Lifetemp_remove_button_remove_the_device_id_and_Remove_buttonfrom_scan_device_QR_code_page() throws IOException {
        String case_id = "22589";
        helper.printTestStart("test_by_clicking_on_the_Lifetemp_remove_button_remove_the_device_id_and_Remove_buttonfrom_scan_device_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetemp();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveThermometer")), 45);
        WebElement LifetempID= driver.findElementById("textViewAddDevicesThermometerHumanReadableSerialNumber");
        String LifetempIdBeforeScanning=LifetempID.getText();
        Log("Lifetouch ID is: "+LifetempIdBeforeScanning);
        WebElement Removebutton= driver.findElementById("buttonRemoveThermometer");
        assertTrue(Removebutton.isDisplayed());
        Log("Remove button appearance status is:"+Removebutton.isDisplayed());
        WebElement LifetempIndicator= driver.findElement(By.id("viewThermometerGoodToUse"));
        assertTrue(LifetempIndicator.isDisplayed());
        Log("Lifetemp Indicator appearance status is:"+LifetempIndicator.isDisplayed());
        //Click on the Remove button
        Log("Click on the Lifetemp remove button");
        Removebutton.click();
        Log("Lifetemp ID is: "+LifetempID.getText());
        assertTrue((driver.findElements(By.id("buttonRemoveThermometer")).size()==0));
        assertTrue((driver.findElements(By.id("viewThermometerGoodToUse")).size()==0));
        Log("No remove button or Lifetemp use indicator appeared after removing the device ID");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_by_clicking_on_the_Nonin_remove_button_remove_the_device_id_andRemove_button_from_scan_device_QR_code_page() throws IOException {
        String case_id = "22590";
        helper.printTestStart("test_by_clicking_on_the_Nonin_remove_button_remove_the_device_id_andRemove_button_from_scan_device_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Scan a Nonin
        helper.spoofQrCode_scanDummyDataNonin();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemovePulseOximeter")), 45);
        WebElement NoninID= driver.findElementById("textViewPulseOximeterHumanReadableSerialNumber");
        String NoninIdBeforeScanning=NoninID.getText();
        Log("Nonin ID is: "+NoninIdBeforeScanning);
        WebElement Removebutton= driver.findElementById("buttonRemovePulseOximeter");
        assertTrue(Removebutton.isDisplayed());
        Log("Remove button appearance status is:"+Removebutton.isDisplayed());
        WebElement NoninIndicator= driver.findElement(By.id("viewPulseOximeterGoodToUse"));
        assertTrue(NoninIndicator.isDisplayed());
        Log("Nonin Indicator appearance status is:"+NoninIndicator.isDisplayed());
        //Click on the Remove button
        Log("Click on the Nonin remove button");
        Removebutton.click();
        Log("Nonin ID is: "+NoninID.getText());
        assertTrue((driver.findElements(By.id("buttonRemovePulseOximeter")).size()==0));
        assertTrue((driver.findElements(By.id("viewPulseOximeterGoodToUse")).size()==0));
        Log("No remove button or Nonin use indicator appeared after removing the device ID");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_by_clicking_on_the_Bloodpressure_remove_button_remove_the_device_id_andRemove_button_from_scan_device_QR_code_page() throws IOException {
        String case_id = "22591";
        helper.printTestStart("test_by_clicking_on_the_Bloodpressure_remove_button_remove_the_device_id_andRemove_button_from_scan_device_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Scan a Blood Pressure device
        helper.spoofQrCode_scanDummyDataAnD();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveBloodPressure")), 45);
        WebElement BPID= driver.findElementById("textViewBloodPressureHumanReadableSerialNumber");
        String BpIdBeforeScanning=BPID.getText();
        Log("Blood Pressure device ID is: "+BpIdBeforeScanning);
        WebElement Removebutton= driver.findElementById("buttonRemoveBloodPressure");
        assertTrue(Removebutton.isDisplayed());
        Log("Remove button appearance status is:"+Removebutton.isDisplayed());
        WebElement BPIndicator= driver.findElement(By.id("viewBloodPressureGoodToUse"));
        assertTrue(BPIndicator.isDisplayed());
        Log("BP Indicator appearance status is:"+BPIndicator.isDisplayed());
        //Click on the Remove button
        Log("Click on the Blood Pressure remove button");
        Removebutton.click();
        Log("Blood Pressure Id  is: "+BPID.getText());
        assertTrue((driver.findElements(By.id("buttonRemoveBloodPressure")).size()==0));
        assertTrue((driver.findElements(By.id("viewBloodPressureGoodToUse")).size()==0));
        Log("No remove button or Blood Pressure use indicator appeared after removing the device ID");
        helper.updateTestRail(PASSED);
    }

    @Test
    public void test_by_clicking_on_the_WeightSacle_remove_button_remove_the_device_id_and_Remove_button_from_scan_device_QR_code_page() throws IOException {
        String case_id = "54546";
        helper.printTestStart("test_by_clicking_on_the_Weightscale_remove_button_remove_the_device_id_and_Remove_button_from_scan_device_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Scan a Weightscale
        helper.spoofQrCode_scanDummyWeightScale();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveScales")), 45);
        WebElement WeightscaleID= driver.findElementById("textViewScalesHumanReadableSerialNumber");
        String WeightScaleIdBeforeScanning=WeightscaleID.getText();
        Log("WeightScale ID is: "+WeightScaleIdBeforeScanning);
        WebElement Removebutton= driver.findElementById("buttonRemoveScales");
        assertTrue(Removebutton.isDisplayed());
        Log("Remove button appearance status is:"+Removebutton.isDisplayed());
        WebElement WeightScaleIndicator= driver.findElement(By.id("viewScalesGoodToUse"));
        assertTrue(WeightScaleIndicator.isDisplayed());
        Log("WeightScale Indicator appearance status is:"+WeightScaleIndicator.isDisplayed());
        //Click on the Remove button
        Log("Click on the WeightScale remove button");
        Removebutton.click();
        Log("WeightScale ID is: "+WeightscaleID.getText());
        assertTrue((driver.findElements(By.id("buttonRemoveScales")).size()==0));
        assertTrue((driver.findElements(By.id("viewScalesGoodToUse")).size()==0));
        Log("No remove button or WeightScale use indicator appeared after removing the device ID");
        helper.updateTestRail(PASSED);

    }

    @Test
    //Test no  connect button if there is no device Id
    public void Test_No_connect_button_on_the_scan_device_QR_code_page_until_there_is_no_device_scanned_or_added_to_the_session() throws IOException {
        String case_id = "22592";
        helper.printTestStart("Test_No_connect_button_on_the_scan_device_QR_code_page_until_there_is_no_device_scanned_or_added_to_the_session",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("scan_device_qr_codes").getText();
        String ExpectedTitle= helper.getAppString("scan_device_qr_codes");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("We are in : "+ActualTitle+" Page");
        //Lifetouch
        WebElement lifeTouch= driver.findElement(By.id("textViewLifetouch"));
        Log(lifeTouch.getText());
        //No id
        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        String ActualValue=LifetouchID.getText();
        Log("Value displayed for Lifetouch is: "+ActualValue);
        //Lifetemp
        WebElement lifeTemp= driver.findElement(By.id("textViewThermometer"));
        Log(lifeTemp.getText());
        //No id
        WebElement LifetempID= driver.findElementById("textViewAddDevicesThermometerHumanReadableSerialNumber");
        ActualValue=LifetempID.getText();
        Log("Value displayed for Lifetemp is: "+ActualValue);
        //Nonin
        WebElement Nonin= driver.findElement(By.id("textViewPulseOximeter"));
        Log(Nonin.getText());
        //No id
        WebElement NoninID= driver.findElementById("textViewPulseOximeterHumanReadableSerialNumber");
        ActualValue=NoninID.getText();
        Log("Value displayed for Nonin is: "+ActualValue);
        //Bloodpressuer
        WebElement BloodPressure= driver.findElement(By.id("textViewBloodPressure"));
        Log(BloodPressure.getText());
        //No id
        WebElement BPID= driver.findElementById("textViewBloodPressureHumanReadableSerialNumber");
        Log("Value displayed for BP is: "+ActualValue);
        //WeightScale
        WebElement WeightScale= driver.findElement(By.id("textViewScales"));
        Log(WeightScale.getText());
        //No id
        WebElement WeightScaleID= driver.findElementById("textViewScalesHumanReadableSerialNumber");
        Log("Value displayed for WeightScale is: "+ActualValue);

        Log("Check the presence of connect button before any device got scanned");
        assertTrue((driver.findElements(By.id("buttonNext")).size()==0));
        Log("No connect button in the screen ");
        helper.updateTestRail(PASSED);

    }

    //Test that Back button is clickable
    @Test
    public void test_back_button_is_clickable_from_scan_device_QR_code_page() throws IOException {
        String case_id = "22594";
        helper.printTestStart("test_back_button_is_clickable_from_scan_device_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("scan_device_qr_codes").getText();
        String ExpectedTitle= helper.getAppString("scan_device_qr_codes");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("We are in : "+ActualTitle+" page");
        Log("Click on the back button");
        WebElement backbutton= driver.findElement(By.id("buttonBack"));
        assertTrue(backbutton.isDisplayed());
        backbutton.click();
        WebElement agerangepage= driver.findElement(By.id("textPleaseSelectPatientAgeRange"));
        assertTrue(agerangepage.isDisplayed());
        Log("Lead us to "+agerangepage.getText()+" page");
        helper.updateTestRail(PASSED);
    }

    //Test connect button is clickable and lead to correct page
    @Test
    public void test_connect_button_is_clickable_and_lead_us_to_the_correct_page() throws IOException {
        String case_id = "22595";
        helper.printTestStart("test_connect_button_is_clickable_and_lead_us_to_the_correct_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveLifetouch")), 45);
        String lifetouch_id_after_scanning=LifetouchID.getText();
        Log("Value displayed for Lifetouch is: "+lifetouch_id_after_scanning);
        WebElement RemoveButton=driver.findElement(By.id("buttonRemoveLifetouch"));
        assertTrue(RemoveButton.isDisplayed());
        Log("Remove button appearance status is:"+RemoveButton.isDisplayed());
        //Lifetouch indicator
        WebElement LifetouchIndicator= driver.findElement(By.id("viewLifetouchGoodToUse"));
        assertTrue(LifetouchIndicator.isDisplayed());
        Log("Lifetouch Indicator appearance status is:"+LifetouchIndicator.isDisplayed());
        //Connect button
        WebElement Connect= driver.findElement(By.id("buttonNext"));
        assertTrue(Connect.isDisplayed());
        Log("Connection button appearance status is :"+Connect.isDisplayed());
        //Click on the connect button
        Log("Click on Connect button");
        Connect.click();
        //Check the its landed in right page
        WebElement Deviceconnectionpage= driver.findElement(By.id("linear_layout__device_connection__lifetouch"));
        assertTrue(Deviceconnectionpage.isDisplayed());
        Log("Connect button is clickable and Lead us to device connection page");
        helper.updateTestRail(PASSED);
    }

    //Test the disappearance of manual Vitals button from scandevice QR code page after a device got scanned/added to the session
    @Test
    public void Test_the_disappearance_of_manual_Vitals_button_from_scandevice_QR_code_page_after_a_device_got_scanned_to_the_session() throws IOException {
        String case_id = "22596";

        helper.printTestStart("Test_the_disappearance_of_manual_Vitals_button_from_scandevice_QR_code_page_after_a_device_got_scanned_added_to_the_session",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("scan_device_qr_codes").getText();
        String ExpectedTitle= helper.getAppString("scan_device_qr_codes");
        Assert.assertEquals(ActualTitle,ExpectedTitle);
        Log("We are in : "+ActualTitle+" Page");
        //Lifetouch
        WebElement lifeTouch= driver.findElement(By.id("textViewLifetouch"));
        Log(lifeTouch.getText());
        //No id
        WebElement LifetouchID= driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        String ActualValue=LifetouchID.getText();
        Log("Value displayed for Lifetouch is: "+ActualValue);
        //Lifetemp
        WebElement lifeTemp= driver.findElement(By.id("textViewThermometer"));
        Log(lifeTemp.getText());
        //No id
        WebElement LifetempID= driver.findElementById("textViewAddDevicesThermometerHumanReadableSerialNumber");
        ActualValue=LifetempID.getText();
        Log("Value displayed for Lifetemp is: "+ActualValue);
        //Nonin
        WebElement Nonin= driver.findElement(By.id("textViewPulseOximeter"));
        Log(Nonin.getText());
        //No id
        WebElement NoninID= driver.findElementById("textViewPulseOximeterHumanReadableSerialNumber");
        ActualValue=NoninID.getText();
        Log("Value displayed for Nonin is: "+ActualValue);
        //Bloodpressuer
        WebElement BloodPressure= driver.findElement(By.id("textViewBloodPressure"));
        Log(BloodPressure.getText());
        //No id
        WebElement BPID= driver.findElementById("textViewBloodPressureHumanReadableSerialNumber");
        Log("Value displayed for BP is: "+ActualValue);
        //WeightScale
        WebElement WeightScale= driver.findElement(By.id("textViewScales"));
        Log(WeightScale.getText());
        //No id
        WebElement WeightScaleID= driver.findElementById("textViewScalesHumanReadableSerialNumber");
        Log("Value displayed for WeightScale is: "+ActualValue);

        //Check the presence of ManualVitalsOnly button
        WebElement manualVitalsOnly = driver.findElement(By.id("buttonManualVitalsOnly"));
        assertTrue(manualVitalsOnly.isDisplayed());
        Log("Status of manualVitalsOnly button is: "+ manualVitalsOnly.isDisplayed());
        Log("Scan a device to teh session");
        //Scan a lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //wait until remove button appear
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveLifetouch")), 45);
        String lifetouch_id_after_scanning=LifetouchID.getText();
        Log("Value displayed for Lifetouch is: "+lifetouch_id_after_scanning);
        //check the presence of ManualVitalsOnly button
        Log("Check the presence of ManualVitalOnly button after adding a device");
        boolean presenceOfElement=driver.findElements(By.id("buttonManualVitalsOnly")).size()==0;
        assertTrue((presenceOfElement));
        Log("No ManualVitalonly button display ");
        helper.updateTestRail(PASSED);
    }

    @Test
    //Test the appearance of manualVitalonly button and disappearance of Connect button after removing all the Id from the Scandevice QR code page
    public void Test_the_appearance_of_manualVitalonly_button_and_disappearance_of_Connect_button_after_removing_all_the_Id_from_the_Scandevice_QR_code_page() throws IOException {
        String case_id = "22597";
        helper.printTestStart("Test_the_appearance_of_manualVitalonly_button_and_disappearance_of_Connect_button_after_removing_all_the_Id_from_the_Scandevice_QR_code_page",case_id);

        setUpSessionWithNonAdultAgeRange();

        //Check the heading
        String ActualTitle = helper.findTextViewElementWithString("scan_device_qr_codes").getText();
        String ExpectedTitle = helper.getAppString("scan_device_qr_codes");
        Assert.assertEquals(ActualTitle, ExpectedTitle);
        Log("We are in : " + ActualTitle + " Page");
        Log("Scan all four devices");
        //add Lifetouch
        helper.spoofQrCode_scanDummyDataLifetouch();
        //Lifetemp
        helper.spoofQrCode_scanDummyDataLifetemp();
        //Nonin
        helper.spoofQrCode_scanDummyDataNonin();
        //AnD
        helper.spoofQrCode_scanDummyDataAnD();
        //WeightScale
        helper.spoofQrCode_scanDummyWeightScale();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonRemoveScales")), 45);

        WebElement lifeTouch = driver.findElement(By.id("textViewLifetouch"));
        WebElement LifetouchID = driver.findElementById("textViewLifetouchHumanReadableSerialNumber");
        String ActualValue = LifetouchID.getText();
        Log(lifeTouch.getText() + " ID is " + ActualValue);

        WebElement lifeTemp = driver.findElement(By.id("textViewThermometer"));
        WebElement LifetempID = driver.findElementById("textViewAddDevicesThermometerHumanReadableSerialNumber");
        ActualValue = LifetempID.getText();
        Log(lifeTemp.getText() + " ID is " + ActualValue);

        WebElement Nonin = driver.findElement(By.id("textViewPulseOximeter"));
        WebElement NoninID = driver.findElementById("textViewPulseOximeterHumanReadableSerialNumber");
        ActualValue = NoninID.getText();
        Log(Nonin.getText() + " ID is" + ActualValue);

        WebElement BloodPressure = driver.findElement(By.id("textViewBloodPressure"));
        WebElement BPID = driver.findElementById("textViewBloodPressureHumanReadableSerialNumber");
        ActualValue = BPID.getText();
        Log(BloodPressure.getText() + " ID is " + ActualValue);

        WebElement WeightScale = driver.findElement(By.id("textViewScales"));
        WebElement WeightScaleID = driver.findElementById("textViewScalesHumanReadableSerialNumber");
        ActualValue = WeightScaleID.getText();
        Log(WeightScale.getText() + " ID is " + ActualValue);

        //Check the presence of Connect button
        WebElement connect = driver.findElement(By.id("buttonNext"));
        assertTrue(connect.isDisplayed());
        Log("Connect button status is : " + connect.isDisplayed());
        //Check the presence of ManualVitalOnly button
        boolean presenceOfElement = driver.findElements(By.id("buttonManualVitalsOnly")).size() == 0;
        assertTrue((presenceOfElement));
        Log("No ManualVitalonly button display ");
        //Remove all device ID
        Log("Remove all the device ID by clicking on remove button");
        WebElement RemoveButton = driver.findElement(By.id("buttonRemoveLifetouch"));
        RemoveButton.click();
        RemoveButton = driver.findElement(By.id("buttonRemoveThermometer"));
        RemoveButton.click();
        RemoveButton = driver.findElement(By.id("buttonRemovePulseOximeter"));
        RemoveButton.click();
        RemoveButton = driver.findElement(By.id("buttonRemoveBloodPressure"));
        RemoveButton.click();
        RemoveButton = driver.findElement(By.id("buttonRemoveScales"));
        RemoveButton.click();


        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("buttonNext")), 60);
        // check the presence of connect button
        assertTrue((driver.findElements(By.id("buttonNext")).size()==0));
        Log("Connect button disappear");
        assertTrue(driver.findElement(By.id("buttonManualVitalsOnly")).isDisplayed());
        Log("ManualVitalOnly button reappear");
        helper.updateTestRail(PASSED);
    }
}

