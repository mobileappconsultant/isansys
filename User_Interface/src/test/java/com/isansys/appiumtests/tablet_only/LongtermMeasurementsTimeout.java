package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
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

public class LongtermMeasurementsTimeout extends AppiumTest {

    @Test
    public void Test_AnD_LongTermMeasurementsTimeOut() throws IOException, ParseException {
        String case_id = "22706";

        helper.printTestStart("Test_AnD_LongTermMeasurementsTimeOut",case_id);
        //Setting Long term Measurement time out
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an AnD to the session
        helper.spoofQrCode_scanDummyDataAnD();
        clickOnConnect();
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));

        // Check the status of AnD
        String bp_device_id = left_hand_side.findElement(By.id("textBloodPressureHumanReadableDeviceId")).getText();
        Log("Device ID is:" + bp_device_id);
        String bp_label = right_hand_side.findElement(By.id("textBloodPressureLabel")).getText();
        Log("RHS of Blood Pressure graph is showing :" + bp_label);
        WebElement bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        assertTrue(bp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        waitUntil((ExpectedConditions.presenceOfElementLocated(By.id("textBloodPressureMeasurementDiastolic"))), 70);

        //check the diastolic BP value after measurement appear on the screen
        WebElement bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));

        Log("RHS of Blood Pressure graph is showing :" + bp_label+"with BP measurement value as: "+bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time starts at: " + Start_time);

        // Check the EWS status after BP measurement arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //Disconnect the AnD
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, false);

        //wait and check the measurement should valid for 2 mins
        // We expect Device Disconnected, because we have to set the device to disconnected (done just above) to stop Dummy Data generating any more measurements.
        waitUntil(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData")), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);
        helper.captureScreenShot();

        String Expected = helper.getAppString("textWaitingForData");
        String ActualBP = driver.findElement(By.id("textBloodPressureMeasurementSystolic")).getText();
        Assert.assertEquals(Expected, ActualBP);
        Log("After time out RHS Blood pressure status changes to :" + ActualBP);


      /*  //check the status of AnD after time out, should show Attempt to reconnect icon.
        left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));

        WebElement Out_of_Range_Icon_After_time_Out= left_hand_side.findElement(By.id("imagePatientVitalsDisplayBloodPressureOutOfRange"));
        assertTrue(Out_of_Range_Icon_After_time_Out.isDisplayed());
        Log("After time out LHS Blood pressure status changes to Attempt to reconnect Icon");*/


        //compare the actual and expected validity period
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);
        Assert.assertTrue(error < TIMEOUT_ERROR_MS);

        // To Check the EWS Status After Time out
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, "Waiting for data"), 650);
        String EWSmeasurement = EWS_Measurement.getText();
        Assert.assertEquals(Expected, EWSmeasurement);
        Log("After BP measurement time out EWS value shown as:"+EWSmeasurement);

        helper.updateTestRail(PASSED);
    }
    @Test
    public void Test_Nonin_LongTermMeasurementsTimeOut() throws IOException, ParseException {
        String case_id = "C54552";

        helper.printTestStart("Test_Nonin_LongTermMeasurementsTimeOut",case_id);
        //Setting Long term Measurement time out
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__SPO2);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an Nonin to the session
        helper.spoofQrCode_scanDummyDataNonin();
        clickOnConnect();
        String ANoninMStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        // Check the status of Nonin
        String Nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + Nonin_device_id);
        String Nonin_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();

        WebElement Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(Nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("RHS of Nonin graph is showing :" + Nonin_label+" with value :"+Nonin_measurement_element.getText());

        Log("Validity time starts at: " + Start_time);

        // Check the EWS status after Nonin measurement arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());


        //Disconnect the Nonin
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__SPO2, false);

        //wait and check the measurement should valid for 2 mins
        // We expect Device Disconnected, because we have to set the device to disconnected (done just above) to stop Dummy Data generating any more measurements.
        waitUntil(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textWaitingForData")), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);
        helper.captureScreenShot();
        /*
        //check the status of Nonin after time out, should show Attempt to reconnect icon.
        WebElement Out_of_Range_Icon_After_time_Out= left_hand_side.findElement(By.id("imagePatientVitalsDisplayPulseOxOutOfRange"));
        assertTrue(Out_of_Range_Icon_After_time_Out.isDisplayed());
        Log("After time out LHS Nonin status changes to Attempt to reconnect Icon");
        */
        String Expected = helper.getAppString("textWaitingForData");
        String ActualNonin = driver.findElement(By.id("textSpO2")).getText();
        Assert.assertEquals(Expected, ActualNonin);
        Log("After time out RHS Nonin status changes to :" + ActualNonin);


        //compare the actual and expected validity period
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;
        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);

        // To Check the EWS Status After Time out
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, "Waiting for data"), 650);
        String EWSmeasurement = EWS_Measurement.getText();
        Assert.assertEquals(Expected, EWSmeasurement);
        Log("After Nonin measurement time out EWS value shown as:"+EWSmeasurement);

        helper.updateTestRail(PASSED);
    }
    @Test
    public void Test_WeightScale_LongTermMeasurementsTimeOut() throws IOException, ParseException {
        String case_id = "C54553";

        helper.printTestStart("Test_WeightScale_LongTermMeasurementsTimeOut",case_id);
        //Setting Long term Measurement time out
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add a weightscale to the session
        helper.spoofQrCode_scanDummyWeightScale();
        clickOnConnect();
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoWeightScale"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutWeightScaleMeasurementBackground"));

        // Check the status of WeightScale
        String WeightScale_id = left_hand_side.findElement(By.id("textWeightScaleHumanReadableDeviceId")).getText();
        Log("Device ID is:" + WeightScale_id);
        String WeightScale_label = right_hand_side.findElement(By.id("textWeightScaleLabel")).getText();

        WebElement WeightScale_measurement_element = right_hand_side.findElement(By.id("textWeightScaleMeasurement"));
        assertTrue(WeightScale_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("RHS of WeightScale graph is showing :" + WeightScale_label+" With Measurement :"+WeightScale_measurement_element.getText());

        Log("Validity time starts at: " + Start_time);

        // Check the EWS status  showing Waiting for data as WeightScale measurement is not part of EWS
        // EWS measurement shouldn't include weightscale measuremnt in to account, So EWS value should show as Waiting for data
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        String ExpectedEWS = helper.getAppString("textWaitingForData");
        String ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);
        Log("EWS measurement value shown as:"+EWS_Measurement.getText());

        //Disconnect the WeightScale
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__WEIGHT_SCALE, false);

        //wait and check the measurement should valid for 2 mins
        // We expect Device Disconnected, because we have to set the device to disconnected (done just above) to stop Dummy Data generating any more measurements.
        waitUntil(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textWaitingForData")), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);
        helper.captureScreenShot();
        /*
        //check the status of WeightScale after time out, should show Attempt to reconnect icon.
        WebElement Out_of_Range_Icon_After_time_Out= left_hand_side.findElement(By.id("imagePatientVitalsDisplayWeightScaleOutOfRange"));
        assertTrue(Out_of_Range_Icon_After_time_Out.isDisplayed());
        Log("After time out LHS WeightScale status changes to Attempt to reconnect Icon");
        */
        String Expected = helper.getAppString("textWaitingForData");
        String ActualWeightScale = driver.findElement(By.id("textWeightScaleMeasurement")).getText();
        Assert.assertEquals(Expected, ActualWeightScale);
        Log("After time out RHS WeightScale status changes to :" + ActualWeightScale);


        //compare the actual and expected validity period
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);

        // No change in EWS status after Weight scale validity expired.
        // EWS measurement shouldn't include weightscale measuremnt in to account, So EWS value should show as Waiting for data
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        ExpectedEWS = helper.getAppString("textWaitingForData");
        ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);
        Log("After WeightScale measurement time out EWS value still showing as:"+EWS_Measurement.getText());
        helper.updateTestRail(PASSED);
    }
    @Test
    public void Test_by_Removing_the_WeightScale_device_within_the_validity_time_of_longterm_measurement_time_2_mins() throws IOException, ParseException {
        String case_id = "C54555";

        helper.printTestStart("Test_by_Removing_the_WeightScale_device_within_the_validity_time_of_longterm_measurement_time_2_mins",case_id);

        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an AnD to the session
        helper.spoofQrCode_scanDummyWeightScale();
        clickOnConnect();
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoWeightScale"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutWeightScaleMeasurementGroup"));

        // Check the status of WeightScale
        String WeightScale_device_id = left_hand_side.findElement(By.id("textWeightScaleHumanReadableDeviceId")).getText();
        Log("Device ID is:" + WeightScale_device_id);
        String WeightScale_label = right_hand_side.findElement(By.id("textWeightScaleLabel")).getText();
        Log("RHS of WeightScale graph is showing :" + WeightScale_label);

        WebElement WeightScale_measurement_element = right_hand_side.findElement(By.id("textWeightScaleMeasurement"));
        assertTrue(WeightScale_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("WeightScale measurement appear as :"+WeightScale_measurement_element.getText());

        Log("Validity time starts at: " + Start_time);

      /*  // Check the EWS status after WeightScale measurement arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());*/
        // EWS measurement shouldn't include weightscale measuremnt in to account, So EWS value should show as Waiting for data
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        String ExpectedEWS = helper.getAppString("textWaitingForData");
        String ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);


        Log("EWS status should show "+EWS_Measurement.getText()+" as Weight scale isn't part of EWS ");

        //Disconnect the WeightScale
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__WEIGHT_SCALE, false);

        clickOnLock();
        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();

        Log("Click on the Remove WeightScale from change session setting page");
        //Remove the WeightScale device
        WebElement WeightScaleRemove = driver.findElement(By.id("buttonRemoveWeightScale"));
        Log("Click on the Remove WeightScale button");
        WeightScaleRemove.click();
        Log("WeightScale removed ");

        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        //open the patient vital display page
        clickOnPatientVitalsDisplay();
        // Check the Remove status for WeightScale
        Log("Check the WeightScale and EWS status,after device got remove from the session but still with in validity time period");
        // Check the LHS side of WeightScale graph
        assertTrue((driver.findElements(By.id("textWeightScaleHumanReadableDeviceId")).size() == 0));
        Log("No device Id found - so device was removed");

        // refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutWeightScaleMeasurementGroup"));
        WeightScale_measurement_element = right_hand_side.findElement(By.id("textWeightScaleMeasurement"));


        Log("Weight scale measurement still showing as :"+WeightScale_measurement_element.getText());
        //EWS value should show same as before as Weight scale not part of EWS
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        ExpectedEWS = helper.getAppString("textWaitingForData");
        ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);

        Log("EWS status still showing as :"+EWS_Measurement.getText());

        Log("Wait until WeightScale measurement time out happen");
        //wait and check the measurement should valid for 2 mins
        waitUntil(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textDeviceRemoved")), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);

        helper.captureScreenShot();
        String Expected = helper.getAppString("textDeviceRemoved");
        String ActualWeightScale = driver.findElement(By.id("textWeightScaleMeasurement")).getText();
        Assert.assertEquals(Expected, ActualWeightScale);
        Log("After time out Weight scale status changes to :" + ActualWeightScale);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);

        // To Check the EWS Status After Time out
        //EWS value should show same as before as Weight scale not part of EWS
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        ExpectedEWS = helper.getAppString("textWaitingForData");
        ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);
        Log("After WeightScale measurement time out EWS value still showing as:"+EWS_Measurement.getText());

        helper.updateTestRail(PASSED);
    }


    @Test
    public void Test_by_Removing_the_AnD_device_within_the_validity_time_of_longterm_measurement_time_2_mins() throws IOException, ParseException {
        String case_id = "22707";

        helper.printTestStart("Test_by_Removing_the_AnD_device_within_the_validity_time_of_longterm_measurement_time_2_mins",case_id);

        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);

        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an AnD to the session
        helper.spoofQrCode_scanDummyDataAnD();
        clickOnConnect();
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));

        // Check the status of AnD
        String bp_device_id = left_hand_side.findElement(By.id("textBloodPressureHumanReadableDeviceId")).getText();
        Log("Device ID is:" + bp_device_id);
        String bp_label = right_hand_side.findElement(By.id("textBloodPressureLabel")).getText();

        WebElement bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        assertTrue(bp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        waitUntil((ExpectedConditions.presenceOfElementLocated(By.id("textBloodPressureMeasurementDiastolic"))), 70);

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        //check the diastolic BP value after measurement appear on the screen
        WebElement bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));

        Log("RHS of Blood Pressure graph is showing :" + bp_label+"with BP measurement value as: "+bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());

        Log("Validity time starts at: " + Start_time);

        // Check the EWS status after BP measurement arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());


        //Disconnect the AnD
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, false);

        clickOnLock();
        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();

        Log("Click on the Remove Blood Pressure from change session setting page");
        //Remove the BP device
        WebElement BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        Log("Click on the Remove BP button");
        BPRemove.click();
        Log("BP removed ");
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        //open the patient vital display page
        clickOnPatientVitalsDisplay();
        // Check the Remove status for Blood Pressure
        Log("Check the Blood Pressure and EWS status,after device got remove from the session ");
        // Check the LHS side of BP graph
        assertTrue((driver.findElements(By.id("textBloodPressureHumanReadableDeviceId")).size() == 0));
        Log("No device Id found - so device was removed");

        // refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));
        bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));

        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));


        Log("BP measurement still showing as :"+bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());
        Log("EWS status still showing as :"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());


        Log("Wait until BP measurement time out happen");
        //wait and check the measurement should valid for 2 mins
        waitUntil(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textDeviceRemoved")), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);

        helper.captureScreenShot();
        String Expected = helper.getAppString("textDeviceRemoved");
        String ActualBP = driver.findElement(By.id("textBloodPressureMeasurementSystolic")).getText();
        Assert.assertEquals(Expected, ActualBP);
        Log("After time out Blood pressure status changes to :" + ActualBP);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);
        // To Check the EWS Status After Time out
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, "Waiting for data"), 650);
        String EWSmeasurement = EWS_Measurement.getText();
        Expected = helper.getAppString("textWaitingForData");
        Assert.assertEquals(Expected, EWSmeasurement);
        Log("After BP measurement time out EWS value shown as:"+EWSmeasurement);

        helper.updateTestRail(PASSED);
    }
    @Test
    public void Test_by_Removing_the_Nonin_device_within_the_validity_time_of_longterm_measurement_time_2_mins() throws IOException, ParseException {
        String case_id = " C54554";

        helper.printTestStart("Test_by_Removing_the_Nonin_device_within_the_validity_time_of_longterm_measurement_time_2_mins",case_id);

        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__SPO2);

        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an AnD to the session
        helper.spoofQrCode_scanDummyDataNonin();
        clickOnConnect();
        String ANoninMStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        // Check the status of Nonin
        String Nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + Nonin_device_id);
        String Nonin_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();
        Log("RHS of Nonin graph is showing :" + Nonin_label);

        WebElement Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(Nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        String desired_date_format = "HH:mm:ss";
        DateTime Start_date = new DateTime();
        String Start_time = Start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Nonin measurement appear as :"+Nonin_measurement_element.getText());

        Log("Validity time starts at: " + Start_time);
        // Check the EWS status after BP measurement arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());


        //Disconnect the Nonin
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__SPO2, false);

        clickOnLock();
        helper.spoofQrCode_userUnlock();
        clickOnChangeSessionSettings();

        Log("Click on the Remove Nonin from change session setting page");
        //Remove the Nonin device
        WebElement NoninRemove = driver.findElement(By.id("buttonRemovePulseOximeter"));
        Log("Click on the Remove Nonin button");
        NoninRemove.click();
        Log("Nonin removed ");
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        //open the patient vital display page
        clickOnPatientVitalsDisplay();
        // Check the Remove status for Nonin
        Log("Check the Nonin and EWS status,after device got remove from the session ");
        // Check the LHS side of Nonin graph
        assertTrue((driver.findElements(By.id("textPulseOxHumanReadableDeviceId")).size() == 0));
        Log("No device Id found - so device was removed");

        // refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));
        Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));


        Log("Nonin measurement still showing as :"+Nonin_measurement_element.getText());
        Log("EWS status still showing as :"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());


        Log("Wait until Nonin measurement time out happen");
        //wait and check the measurement should valid for 2 mins
        waitUntil(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textDeviceRemoved")), 180);

        DateTime End_date = new DateTime();
        String End_time = End_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("Validity time Ends at " + End_time);

        helper.captureScreenShot();
        String Expected = helper.getAppString("textDeviceRemoved");
        String ActualNonin = driver.findElement(By.id("textSpO2")).getText();
        Assert.assertEquals(Expected, ActualNonin);
        Log("After time out Nonin status changes to :" + ActualNonin);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(Start_time);
        Date date2 = format.parse(End_time);
        long time_difference = date2.getTime() - date1.getTime();
        long expected_difference = 2 * DateUtils.MINUTE_IN_MILLIS;

        long error = Math.abs(time_difference - expected_difference);

        Log("Timeout error (ms) is " + error);

        Assert.assertTrue(error < TIMEOUT_ERROR_MS);
        // To Check the EWS Status After Time out
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, "Waiting for data"), 650);
        String EWSmeasurement = EWS_Measurement.getText();
        Expected = helper.getAppString("textWaitingForData");
        Assert.assertEquals(Expected, EWSmeasurement);
        Log("After Nonin measurement time out EWS value shown as:"+EWSmeasurement);

        helper.updateTestRail(PASSED);
    }

    @Test
    public void Testing_by_adding_Manual_Vital_in_middle_of_Sensor_Validity_for_BloodPressure_device() throws IOException
    {
        String case_id = "22708";

        helper.printTestStart("Testing_by_adding_Manual_Vital_in_middle_of_Sensor_Validity_for_BloodPressure_device",case_id);
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an AnD to the session
        helper.spoofQrCode_scanDummyDataAnD();
        clickOnConnect();
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));

        // Check the status of AnD
        String bp_device_id = left_hand_side.findElement(By.id("textBloodPressureHumanReadableDeviceId")).getText();
        Log("Device ID is:" + bp_device_id);
        String bp_label = right_hand_side.findElement(By.id("textBloodPressureLabel")).getText();

        WebElement bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        assertTrue(bp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        waitUntil((ExpectedConditions.presenceOfElementLocated(By.id("textBloodPressureMeasurementDiastolic"))), 70);

        //check the diastolic BP value after measurement appear on the screen
        WebElement bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));
        Log("RHS of Blood Pressure graph is showing :" + bp_label+"with value"+bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());

        // Check the EWS status after BP measurement arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //Disconnect the AnD
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, false);

        //Enter a manual BP reading to the session while BP measurement is valid
        //Click on the lock screen button
        clickOnLock();
        manuallyEnteredVitalSign_BloodPressure();
        //open patient vital display
        clickOnPatientVitalsDisplay();
        Log("Check the value display for BP and EWS  after manually entered BP measurement added to the session");

        // refresh element references
        right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));
        bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));
        Log("RHS of Blood Pressure graph is still showing :" +bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());

        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value still showing:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //wait and check the measurement should valid for 2 mins
        waitUntil(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData")), 180);
        helper.captureScreenShot();
        //check the status of AnD after timeout should be Attempt to reconnect.
        String Expected = helper.getAppString("textWaitingForData");
        String ActualBP = driver.findElement(By.id("textBloodPressureMeasurementSystolic")).getText();
        Assert.assertEquals(Expected, ActualBP);
        Log("After time out Blood pressure status changes to :" + ActualBP);
        // To Check the EWS Status After Time out
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, "Waiting for data"), 650);
        String EWSmeasurement = EWS_Measurement.getText();
        Assert.assertEquals(Expected, EWSmeasurement);
        Log("After BP measurement time out EWS value shown as:"+EWSmeasurement);

        helper.updateTestRail(PASSED);
    }
    @Test
    public void Testing_by_adding_Manual_Vital_Weight_in_middle_of_Sensor_Validity_for_WeightScale_device() throws IOException
    {
        String case_id = "C54556";

        helper.printTestStart("Testing_by_adding_Manual_Vital_Weight_in_middle_of_Sensor_Validity_for_WeightScale_device",case_id);
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an WeightScale to the session
        helper.spoofQrCode_scanDummyWeightScale();
        clickOnConnect();
        String AWeightScaleMStatus = driver.findElement(By.id("textViewWeightScaleSearchStatus")).getText();
        Log("Status of the WeightScale is " + AWeightScaleMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoWeightScale"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutWeightScaleMeasurementGroup"));

        // Check the status of WeightScale
        String WeightScale_device_id = left_hand_side.findElement(By.id("textWeightScaleHumanReadableDeviceId")).getText();
        Log("Device ID is:" + WeightScale_device_id);
        String WeightScale_label = right_hand_side.findElement(By.id("textWeightScaleLabel")).getText();

        WebElement WeightScale_measurement_element = right_hand_side.findElement(By.id("textWeightScaleMeasurement"));
        assertTrue(WeightScale_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        Log("RHS of WeightScale graph is showing :" + WeightScale_label+" with value: "+WeightScale_measurement_element.getText());

        // EWS measurement shouldn't include weightscale measuremnt in to account, So EWS value should show as Waiting for data
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        String ExpectedEWS = helper.getAppString("textWaitingForData");
        String ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);

        //Disconnect the WeightScale
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__WEIGHT_SCALE, false);

        //Enter a manual Weight Value to the session while WeightScale measurement is valid
        //Click on the lock screen button
        clickOnLock();
        manuallyEnteredVitalSign_WeightScale();
        //open patient vital display
        clickOnPatientVitalsDisplay();
        Log("Check the value display for Weight and EWS  after manually entered Weight measurement added to the session");

        // refresh element references
        right_hand_side = driver.findElement(By.id("linearLayoutWeightScaleMeasurementGroup"));
        WeightScale_measurement_element = right_hand_side.findElement(By.id("textWeightScaleMeasurement"));
        Log("RHS of WeightScale graph is still showing :" +WeightScale_measurement_element.getText());

        // EWS measurement shouldn't include weightscale measuremnt in to account, So EWS value should show as Waiting for data
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        ExpectedEWS = helper.getAppString("textWaitingForData");
        ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);
        Log("EWS measurement value still showing:"+EWS_Measurement.getText());

        //wait and check the measurement should valid for 2 mins
        waitUntil(ExpectedConditions.textToBePresentInElement(WeightScale_measurement_element, helper.getAppString("textWaitingForData")), 180);
        helper.captureScreenShot();
        //check the status of WaightScale after timeout should be Waiting for Data.
        String Expected = helper.getAppString("textWaitingForData");
        String ActualWeight = driver.findElement(By.id("textWeightScaleMeasurement")).getText();
        Assert.assertEquals(Expected, ActualWeight);
        Log("After time out WeightScale status changes to :" + ActualWeight);
        // To Check the EWS Status After Time out
        // EWS measurement shouldn't include weightscale measuremnt in to account, So EWS value should show as Waiting for data
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        ExpectedEWS = helper.getAppString("textWaitingForData");
        ActualEWS =EWS_Measurement.getText();
        Assert.assertEquals(ExpectedEWS, ActualEWS);
        Log("After WeightScale measurement time out EWS value still showing as:"+EWS_Measurement.getText());
        helper.updateTestRail(PASSED);
    }
    @Test
    public void Testing_by_adding_Manual_Vital_SpO2_in_middle_of_Sensor_Validity_for_Nonin_device() throws IOException
    {
        String case_id = "C54557";

        helper.printTestStart("Testing_by_adding_Manual_Vital_SpO2_in_middle_of_Sensor_Validity_for_Nonin_device", case_id);
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__SPO2);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add an WeightScale to the session
        helper.spoofQrCode_scanDummyDataNonin();
        clickOnConnect();
        String ANoninMStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        // Check the status of Nonin
        String Nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + Nonin_device_id);
        String Nonin_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();

        WebElement Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(Nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        Log("RHS of Nonin graph is showing :" + Nonin_label+" with value: "+Nonin_measurement_element.getText());

        // Check the EWS status after Nonin  arrived
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //Disconnect the Nonin
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__SPO2, false);

        //Enter a manual Spo2 Value to the session while Nonin measurement is valid
        //Click on the lock screen button
        clickOnLock();
        manuallyEnteredVitalSign_SpO2();
        //open patient vital display
        clickOnPatientVitalsDisplay();
        Log("Check the value display for Spo2 and EWS  after manually entered SpO2 measurement added to the session");

        // refresh element references
        right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));
        Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        Log("RHS of Nonin graph is still showing :" +Nonin_measurement_element.getText());

        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value still showing:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //wait and check the measurement should valid for 2 mins
        waitUntil(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textWaitingForData")), 180);
        helper.captureScreenShot();
        //check the status of Nonin after timeout ,should be Waiting for Data.
        String Expected = helper.getAppString("textWaitingForData");
        String ActualSpo2 = driver.findElement(By.id("textSpO2")).getText();
        Assert.assertEquals(Expected, ActualSpo2);
        Log("After time out Nonin status changes to :" + ActualSpo2);
        // To Check the EWS Status After Time out
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, "Waiting for data"), 650);
        String EWSmeasurement = EWS_Measurement.getText();
        Assert.assertEquals(Expected, EWSmeasurement);
        Log("After Nonin measurement time out EWS value shown as:"+EWSmeasurement);

        helper.updateTestRail(PASSED);
    }


    @Test
    //check auto switching of EWS value after BP device removed from the session while manually entered BP measurement is valid and Long term measurement validity has expired.
    public void check_Auto_Switching_Of_EWS_value_After_BP_device_Removed_From_the_session_while_manually_entered_BP_measurement_is_valid_and_Long_term_measurement_validity_has_expired() throws IOException {
        String case_id = "C54570";

        helper.printTestStart("check auto switching of EWS value after BP device removed from the session while manually entered BP measurement is valid and Long term measurement validity has expired.");
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add manual vital sign
        clickOnManualVitalsOnly();
        clickOnBack();
        clickOnLock();
        manuallyEnteredVitalSign_BloodPressure();
        //open patient vital display
        clickOnPatientVitalsDisplay();
        //store the EWS value
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(EWS_Measurement, helper.getAppString("textWaitingForData"))), 130);

        // Check the EWS status after BP measurement arrived
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        //store the 1st value
        String Manual_EWS_Measurement=EWS_Measurement.getText();

        String ManualEWS = EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText();
        Log("EWS measurement value shown as:"+ManualEWS);
        //Add BP device
        clickOnLock();
        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();
        WebElement BPAdd;
        BPAdd= driver.findElement(By.id("buttonAddBloodPressure"));
        BPAdd.click();

        Log("Click on the Add Blood Pressure from change session setting page");
        //Add an AnD to the session
        helper.spoofQrCode_scanDummyDataAnD();
        clickOnConnect();
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoBloodPressure"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));

        // Check the status of AnD
        String bp_device_id = left_hand_side.findElement(By.id("textBloodPressureHumanReadableDeviceId")).getText();
        Log("Device ID is:" + bp_device_id);
        String bp_label = right_hand_side.findElement(By.id("textBloodPressureLabel")).getText();

        WebElement bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        assertTrue(bp_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        waitUntil((ExpectedConditions.presenceOfElementLocated(By.id("textBloodPressureMeasurementDiastolic"))), 70);

        //check the diastolic BP value after measurement appear on the screen
        WebElement bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));
        Log("RHS of Blood Pressure graph is showing :" + bp_label+"with value"+bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());

        // Check the EWS status after BP measurement arrived
         EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
         EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());
        String SensorEWS= EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText();

        //Disconnect the AnD
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, false);

        //Remove the BP device
        clickOnLock();
        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();
        WebElement BPRemove;
        BPRemove= driver.findElement(By.id("buttonRemoveBloodPressure"));
        BPRemove.click();
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();
        helper.spoofQrCode_userUnlock();
        //open the patient vital display page
        clickOnPatientVitalsDisplay();
        // Check the bloodpressure status in graph after status device removed during be validity time
        Log("Check the Blood Pressure status,after device got remove from the session ");
        // Check the LHS side of BP graph
        assertTrue((driver.findElements(By.id("textBloodPressureHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");
        //refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));
        bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        bp_diastolic_measurement=right_hand_side.findElement(By.id("textBloodPressureMeasurementDiastolic"));

        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));


        Log("BP measurement still showing as :"+bp_measurement_element.getText()+"/"+bp_diastolic_measurement.getText());
        Log("EWS status still showing as :"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //Wait until device time out
        waitUntil(ExpectedConditions.textToBePresentInElement(bp_measurement_element, helper.getAppString("textDeviceRemoved")), 180);
        Log("Wait until device time out");
        //check the EWS value,which is same as
        Log("After Long term measurement validity expired");
        //refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutBloodPressureMeasurementGroup"));
        bp_measurement_element = right_hand_side.findElement(By.id("textBloodPressureMeasurementSystolic"));
        Log("BP status is showing"+bp_measurement_element.getText());
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        Log(Manual_EWS_Measurement);
       // waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, helper.getAppString("2")), 180);
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, Manual_EWS_Measurement), 180);

       // EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        String EWSmeasurement=EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText();
        Log("EWS measuremnt is showing as:"+EWSmeasurement);
        Assert.assertEquals(ManualEWS, EWSmeasurement);
        Log("After long term measurement validity expired,EWS value change and include manually entered  BP to calculate EWS for rest for the manually entered validity time.");

    }

    @Test
    //check auto switching of EWS value after Nonin device removed from the session while manually entered SpO2 measurement is valid and Long term measurement validity has expired.
    public void check_Auto_Switching_Of_EWS_value_After_Nonin_device_Removed_From_the_session_while_manually_entered_Spo2_measurement_is_valid_and_Long_term_measurement_validity_has_expired() throws IOException {
        String case_id = "C54571";

        helper.printTestStart("check auto switching of EWS value after Nonin device removed from the session while manually entered SpO2 measurement is valid and Long term measurement validity has expired.");
        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__SPO2);
        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();
        //Add manual vital sign
        clickOnManualVitalsOnly();
        clickOnBack();
        clickOnLock();
        manuallyEnteredVitalSign_SpO2();
        //open patient vital display
        clickOnPatientVitalsDisplay();
        //store the EWS value for Manual entry so we can compare it later
        WebElement EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(EWS_Measurement, helper.getAppString("textWaitingForData"))), 130);

        // Check the EWS status after Spo2 measurement arrived
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        WebElement EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
       //store the 1st value
        String Manual_EWS_Measurement=EWS_Measurement.getText();
        String ManualEWS = EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText();
        Log("EWS measurement value shown as:"+ManualEWS);
        //Add Nonin device
        clickOnLock();
        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();
        WebElement AddDevice;
        AddDevice= driver.findElement(By.id("buttonAddPulseOx"));
        AddDevice.click();

        Log("Click on the Add PulseOx from change session setting page");
        //Add an Nonin to the session
        helper.spoofQrCode_scanDummyDataNonin();
        clickOnConnect();
        String ANoninMStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin  is " + ANoninMStatus);
        clickOnStartMonitoring();
        WebElement left_hand_side = driver.findElement(By.id("linearLayoutDeviceInfoPulseOx"));
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));

        // Check the status of Nonin
        String Nonin_device_id = left_hand_side.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + Nonin_device_id);
        String Nonin_label = right_hand_side.findElement(By.id("textSpO2PercentLabel")).getText();

        WebElement Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        assertTrue(Nonin_measurement_element.isDisplayed());

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textWaitingForData"))), 130);
        Log("RHS of Nonin graph is showing :" + Nonin_label+" with value: "+Nonin_measurement_element.getText());

        // Check the EWS status after Nonin  arrived
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        Log("EWS measurement value shown as:"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //Disconnect the Nonin
        helper.spoofCommandIntent_simulateDeviceConnectionState(SensorType.SENSOR_TYPE__SPO2, false);


        //Remove the Nonin device
        clickOnLock();
        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();
        WebElement RemoveDevice;
        RemoveDevice= driver.findElement(By.id("buttonRemovePulseOximeter"));
        RemoveDevice.click();
        checkRemoveDeviceEwsPopup(true);

        clickOnLock();
        helper.spoofQrCode_userUnlock();
        //open the patient vital display page
        clickOnPatientVitalsDisplay();
        // Check the Nonin status in graph after status device removed during be validity time
        Log("Check the Nonin status,after device got remove from the session ");
        // Check the LHS side of BP graph
        assertTrue((driver.findElements(By.id("textPulseOxHumanReadableDeviceId")).size() == 0));
        Log("No device Id found");
        //refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));
        Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));

        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));


        Log("Spo2 measurement still showing as :"+Nonin_measurement_element.getText());
        Log("EWS status still showing as :"+EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText());

        //Wait until device time out
        waitUntil(ExpectedConditions.textToBePresentInElement(Nonin_measurement_element, helper.getAppString("textDeviceRemoved")), 180);
        Log("Wait until device time out");
        //check the EWS value,which is same as
        Log("After Long term measurement validity expired");
        //refresh the measurement element
        right_hand_side = driver.findElement(By.id("linearLayoutPulseOxMeasurementGroup"));
        Nonin_measurement_element = right_hand_side.findElement(By.id("textSpO2"));
        Log("Nonin status is showing"+Nonin_measurement_element.getText());
        EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        //compare the current EWS value with manual EWS from start of the session
        waitUntil(ExpectedConditions.textToBePresentInElement(EWS_Measurement, Manual_EWS_Measurement), 180);

        // EWS_Measurement=driver.findElement(By.id("textEarlyWarningScoreMeasurement"));
        EWS_MAx_Measurement= driver.findElement(By.id("textEarlyWarningScoreMaxPossible"));
        String EWSmeasurement=EWS_Measurement.getText()+"/"+EWS_MAx_Measurement.getText();
        Log("EWS measuremnt is showing as:"+EWSmeasurement);
        Assert.assertEquals(ManualEWS, EWSmeasurement);
        Log("After long term measurement validity expired,EWS value change and include manually entered  SpO2 to calculate EWS for rest for the manually entered validity time.");

    }

    @Test
    // Check EWS still generated if devices not kept as part of EWS
     public void checkEWSStillGeneratedIfDevicesNotKept_AdultAgeRange() throws IOException {
        String case_id = "57114";

        helper.printTestStart("checkEWSStillGeneratedIfDevicesNotKept_AdultAgeRange", case_id);

        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithAdultAgeRange();

        // Add all 5 devices
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

        // Wait for EWS to update... expected max is 15 - 6 vitals, but only 5 are part of adult EWS so max would be  5*3
        checkEarlyWarningScoreUpdate("15");

        // remove 1 by 1
        //Remove the Nonin device
        removeDeviceAndDontKeepInEWS("buttonRemovePulseOximeter");
        checkEarlyWarningScoreUpdate("12");

        //Remove the Lifetouch device
        removeDeviceAndDontKeepInEWS("buttonRemoveLifetouch");
        checkEarlyWarningScoreUpdate("6");

        //Remove the Blood Pressure device
        removeDeviceAndDontKeepInEWS("buttonRemoveBloodPressure");
        checkEarlyWarningScoreUpdate("3");

        //Remove the Lifetemp device
        removeDeviceAndDontKeepInEWS("buttonRemoveThermometer");

        //Wait for waiting for data - EWS not being calculated as the max possible score is now 0
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textEarlyWarningScoreMeasurement"), helper.getAppString("textWaitingForData")), 70);

        //Remove the Weight Scale device
        removeDeviceAndDontKeepInEWS("buttonRemoveWeightScale");

        // Should still be waiting for data
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textEarlyWarningScoreMeasurement"), helper.getAppString("textWaitingForData")), 70);

        helper.updateTestRail(PASSED);
    }


    @Test
    // Check EWS still generated if devices not kept as part of EWS
    public void checkEWSStillGeneratedIfDevicesNotKept_NonAdultAgeRange() throws IOException {
        String case_id = "57115";

        helper.printTestStart("checkEWSStillGeneratedIfDevicesNotKept_NonAdultAgeRange", case_id);

        //Scan the user QR code
        helper.spoofQrCode_userUnlock();
        setUpSessionWithNonAdultAgeRange();

        // Add all 5 devices
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

        // Wait for EWS to update... expected max is 15 - 6 vitals, but only 5 are part of adult EWS so max would be  5*3
        checkEarlyWarningScoreUpdate("16");

        // remove 1 by 1
        //Remove the Weight Scale device
        removeDeviceAndDontKeepInEWS("buttonRemoveWeightScale");
        checkEarlyWarningScoreUpdate("16");

        //Remove the Blood Pressure device
        removeDeviceAndDontKeepInEWS("buttonRemoveBloodPressure");
        checkEarlyWarningScoreUpdate("12");

        //Remove the Lifetouch device
        removeDeviceAndDontKeepInEWS("buttonRemoveLifetouch");
        checkEarlyWarningScoreUpdate("4");

        //Remove the Lifetemp device
        removeDeviceAndDontKeepInEWS("buttonRemoveThermometer");
        checkEarlyWarningScoreUpdate("4");

        //Remove the Nonin device
        removeDeviceAndDontKeepInEWS("buttonRemovePulseOximeter");

        //Wait for waiting for data - EWS not being calculated as the max possible score is now 0
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textEarlyWarningScoreMeasurement"), helper.getAppString("textWaitingForData")), 70);


        helper.updateTestRail(PASSED);
    }


    private void checkEarlyWarningScoreUpdate(String expected_max_possible)
    {
        WebElement right_hand_side = driver.findElement(By.id("linearLayoutEarlyWarningScoreMeasurementGroup"));

        WebElement ews_measurement_element = right_hand_side.findElement(By.id("textEarlyWarningScoreMeasurement"));
        assertTrue(ews_measurement_element.isDisplayed());

        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(ews_measurement_element, helper.getAppString("textWaitingForData"))), 200);
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textEarlyWarningScoreMaxPossible")), 200);

        // Now EWS shown and not waiting for data, wait until expected max possible shown
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textEarlyWarningScoreMaxPossible"), expected_max_possible), 70);
    }


    public void removeDeviceAndDontKeepInEWS(String device_id) throws IOException
    {
        clickOnLock();

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("qr_bar_code")), 20);

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        driver.findElement(By.id(device_id)).click();

        checkRemoveDeviceEwsPopup(false);

        if (driver.findElements(By.id("imageRecyclingBox")).size() > 0)
        {
            driver.findElement(By.id("dismiss")).click();
        }

        clickOnLock();

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("qr_bar_code")), 20);

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();
    }


    public void currenttime()
    {
        String desired_date_format = "HH:mm:ss";
        DateTime start_date = new DateTime();
        String End_time = start_date.toString(DateTimeFormat.forPattern(desired_date_format));
        Log("End time " + End_time);
    }

    private void manuallyEnteredVitalSign_BloodPressure()throws IOException
    {
        //scan the user QR code
        helper.spoofQrCode_userUnlock();
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();

        //select the measurement interval
        // Select the measurement interval as 5 mins
        String  MeasurementTime = helper.findTextViewElementWithString("five_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
          helper.findTextViewElementWithString("five_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
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
        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }
    private void manuallyEnteredVitalSign_WeightScale()throws IOException
    {
        //scan the user QR code
        helper.spoofQrCode_userUnlock();
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("fifteen_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
        Log("Click on Weight button");
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight_scale_measurements").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for Weight is 72");
        clickOnNext();

        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }
    private void manuallyEnteredVitalSign_SpO2()throws IOException
    {
       //scan the user QR code
        helper.spoofQrCode_userUnlock();
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
       // click on Next button from observation set time page
       clickOnNext();

       //select the measurement interval
       // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("ten_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("fifteen_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
        // Click on the SpO2 button
        Log("Enter the value for SpO2");
        helper.findTextViewElementWithString("spo2").click();
        //Enter the value-98
        driver.findElement(By.id("buttonNumberNine")).click();
        driver.findElement(By.id("buttonNumberEight")).click();
        Log("Value entered for SpO2 is 98");
        // Click on next
        clickOnNext();

        clickOnNext();
        // Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();

        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
}

    /*
    // Click on the Weight button
        Log("Enter the value for Weight");
        helper.findTextViewElementWithString("weight_scale_measurements").click();
        //Enter the value-72
        driver.findElement(By.id("buttonNumberSeven")).click();
        driver.findElement(By.id("buttonNumberTwo")).click();
        Log("Value entered for Weight is 72");
        clickOnNext();
     */
}
