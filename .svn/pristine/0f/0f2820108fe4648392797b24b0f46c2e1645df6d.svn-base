package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.Assert.assertTrue;

public class USAMode extends AppiumTest
{

    @Test
    //test no age range selection radiobutton is present in Age selection page in USA Mode
    public void test_no_age_range_selection_radiobutton_is_present_in_Ageselectionpage_in_USA_Mode() throws IOException {
        String case_id = "22609";
        helper.printTestStart("test_no_age_range_selection_radiobutton_is_present_in_Ageselectionpage_in_USA_Mode",case_id);

        helper.spoofQrCode_featureEnableUnlock();
        String isCheckedUsamode;
        WebElement Usamode = driver.findElement(By.id("checkBoxUsaMode"));
        isCheckedUsamode = Usamode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUsamode);
        if(isCheckedUsamode.equals("false")) {
            Usamode.click();
            Log("USA mode checked");
        } else {
            Log("USA Mode already  checked");
        }

        //Click on lock screen button
        driver.findElementById("buttonLock").click();

        scanQrCodeAndEnterPatientId();

        WebElement heading= driver.findElementById("textTextExplainingWhatRadioButtonsDo");
        Log(heading.getText());
        //check there is no radio button for age selection
        assertTrue((driver.findElements(By.className("android.widget.RadioButton")).size()==0));
        assertTrue((driver.findElements(By.id("linearLayoutPictureAgeRange")).size()==0));

        Log("No radio button present for age selection along with images");
        helper.updateTestRail(PASSED);


       /* //0to 1
        assertTrue((driver.findElements(By.id("radioButtonPatientAge0To1Years")).size()==0));
        Log("Radiobutton for 0 to 1 age range is  not present");
        //1 to 5
        assertTrue((driver.findElements(By.id("radioButtonPatientAge1To5Years")).size()==0));
        Log("Radiobutton for 1 to 5 age range is  not present");
        //5 to 12
        assertTrue((driver.findElements(By.id("radioButtonPatientAge5To12Years")).size()==0));
        Log("Radiobutton for 5 to 12 age range is  not present");
        //radioButtonPatientAgeGreaterThan12Years
        assertTrue((driver.findElements(By.id("radioButtonPatientAgeGreaterThan12Years")).size()==0));
        Log("Radiobutton for >12 years range is  not present");
        //radioButtonPatientAgeAdult
        assertTrue((driver.findElements(By.id("radioButtonPatientAgeAdult")).size()==0));
        Log("Radiobutton for adult age range is  not present");*/
    }


    @Test
    //test the appearance of early warning score thresholds in Ageselectionpage in USA Mode
    public void test_the_appearance_of_early_warning_score_thresholds_in_Ageselectionpage_in_USA_Mode() throws IOException {
        String case_id = "22610";
        helper.printTestStart("test_the_appearance_of_early_warning_score_thresholds_in_Ageselectionpage_in_USA_Mode",case_id);

        helper.spoofQrCode_featureEnableUnlock();
        String isCheckedUsamode;
        WebElement Usamode = driver.findElement(By.id("checkBoxUsaMode"));
        isCheckedUsamode = Usamode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUsamode);
        if(isCheckedUsamode.equals("false")) {
            Usamode.click();
            Log("USA mode checked");
        } else {
            Log("USA Mode already  checked");
        }
        //Click on lock screen button
        driver.findElementById("buttonLock").click();

        scanQrCodeAndEnterPatientId();

        WebElement heading= driver.findElementById("textTextExplainingWhatRadioButtonsDo");
        Log(heading.getText());
        //Check the heading and thresholds display
        //heart rate
        WebElement HeartRate= driver.findElementById("textThresholdDescriptionHeartRate");
        assertTrue(HeartRate.isDisplayed());
        WebElement LifetouchHeartrate= driver.findElement(By.id("seekBarLifetouchHeartRate"));
        assertTrue(LifetouchHeartrate.isDisplayed());
        Log("Lifetouch Heartrate bar status is :"+LifetouchHeartrate.isDisplayed());
        //Respiration rate
        WebElement RespirationRate= driver.findElementById("textThresholdDescriptionHeartRate");
        assertTrue(RespirationRate.isDisplayed());
        WebElement LifetouchRespirationrate= driver.findElement(By.id("seekBarLifetouchHeartRate"));
        assertTrue(LifetouchRespirationrate.isDisplayed());
        Log("Lifetouch Respiration rate  bar status is :"+LifetouchRespirationrate.isDisplayed());
        //Temperature
        WebElement Temperature= driver.findElementById("textThresholdDescriptionTemperature");
        assertTrue(Temperature.isDisplayed());
        WebElement LifetempTemperature= driver.findElement(By.id("seekBarLifetemp"));
        assertTrue(LifetempTemperature.isDisplayed());
        Log("Lifetemp Temperature  bar status is :"+LifetempTemperature.isDisplayed());
        //SpO2
        WebElement SpO2= driver.findElementById("textThresholdDescriptionSpO2");
        assertTrue(SpO2.isDisplayed());
        WebElement PulseOX= driver.findElement(By.id("seekBarPulseOx"));
        assertTrue(PulseOX.isDisplayed());
        Log("PulseOX  bar status is :"+PulseOX.isDisplayed());
        //Systolic Blood Pressure
        WebElement bp_threshold_description = driver.findElementById("textThresholdDescriptionBloodPressure");
        assertTrue(bp_threshold_description.isDisplayed());
        WebElement blood_pressure_bar = driver.findElement(By.id("seekBarBloodPressure"));
        assertTrue(blood_pressure_bar.isDisplayed());
        Log("Blood Pressure  bar status is :" + blood_pressure_bar.isDisplayed());
        helper.updateTestRail(PASSED);

    }


    @Test
    //Test the ageselection page in USA mode
    public void test_that_Age_selectionpage_in_USA_Mode_only_shows_threshold_for_adult_age_range() throws IOException {
        String case_id = "22611";
        helper.printTestStart("test_that_Age_selectionpage_in_USA_Mode_only_shows_threshold_for_adult_age_range",case_id);

        helper.spoofQrCode_featureEnableUnlock();
        String isCheckedUsamode;
        WebElement Usamode = driver.findElement(By.id("checkBoxUsaMode"));
        isCheckedUsamode = Usamode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUsamode);
        if(isCheckedUsamode.equals("false")) {
            Usamode.click();
            Log("USA mode checked");
        } else {
            Log("USA Mode already  checked");
        }
        //Click on lock screen button
        driver.findElementById("buttonLock").click();

        scanQrCodeAndEnterPatientId();

        WebElement heading = driver.findElementById("textTextExplainingWhatRadioButtonsDo");
        assertTrue(heading.isDisplayed());
        Log("Heading display on the page is :"+ heading.getText());
        helper.updateTestRail(PASSED);

    }


    @Test
    public void Start_a_manual_session_with_USA_Mode() throws IOException {
        String case_id = "22612";
        helper.printTestStart(" Start_a_manual_session_with_USA_Mode",case_id);
        helper.spoofQrCode_featureEnableUnlock();
        String isCheckedUsamode;
        WebElement Usamode = driver.findElement(By.id("checkBoxUsaMode"));
        isCheckedUsamode = Usamode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUsamode);
        if(isCheckedUsamode.equals("false")) {
            Usamode.click();
            Log("USA mode checked");
        } else {
            Log("USA Mode already  checked");
        }
        //Click on lock screen button
        driver.findElementById("buttonLock").click();

        scanQrCodeAndEnterPatientId();

        // Click on Next as can only select ADULT
        clickOnNext();

        // Click on ManualVitals Only
        manualVitalSignEntryForAdultAgeRange();

        // Click on patient vital sign button
        driver.findElement(By.id("buttonBigButtonOne")).click();
        Log("Lead to patient vital display page");
       //patientVitalsDisplayPageForAdultAgerange();
        helper.updateTestRail(PASSED);

    }


    // Check the age selection page after deselecting the USA mode
    @Test
    public void check_the_age_selection_page_after_deselecting_the_USA_mode() throws IOException {
        String case_id = "22613";
        helper.printTestStart("check_the_age_selection_page_after_deselecting_the_USA_mode",case_id);

        helper.spoofQrCode_featureEnableUnlock();
        String isCheckedUsamode;
        WebElement Usamode = driver.findElement(By.id("checkBoxUsaMode"));
        isCheckedUsamode = Usamode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUsamode);
        if(isCheckedUsamode.equals("true")) {
            Usamode.click();
            Log("USA mode unchecked");
        } else {
            Log("USA Mode already  Unchecked");
        }
        //Click on lock screen button
        driver.findElementById("buttonLock").click();

        scanQrCodeAndEnterPatientId();

        WebElement heading= driver.findElementById("textTextExplainingWhatRadioButtonsDo");
        Log(heading.getText());

        //Check the presence of age range selection radio button
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        int number_of_age_ranges = age.size();
        Log("Number age selection radio button is " + number_of_age_ranges);
        Log("Radio button display on the screen is");

        for(int i = 0; i < number_of_age_ranges; i++)
        {
            Log(age.get(i).getText());
            Assert.assertTrue(age.get(i).isDisplayed());
        }
        helper.updateTestRail(PASSED);


    }


    private void manualVitalSignEntryForAdultAgeRange()
    {
        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        Assert.assertEquals(expected_title, actual_title);
        Log("Landed on the correct page");
        Log("Measurement Interval set to five minutes");
        // Click on the selected measurement type
        helper.findTextViewElementWithString("five_minutes").click();

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
        // Choose the value from the options
        // Click on No
        helper.findTextViewElementWithString("stringNo").click();
        Log("Value for supplemental Oxygen level is No ");
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
    }

    @After
    @Override
    public void testTearDown()
    {
        //Lock screen
        driver.findElementById("buttonLock").click();

        // turn off USA mode
        try
        {
            helper.spoofQrCode_featureEnableUnlock();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String isCheckedUsamode;
        WebElement Usamode = driver.findElement(By.id("checkBoxUsaMode"));
        isCheckedUsamode = Usamode.getAttribute("checked");
        Log("USA mode Checkbox status " + isCheckedUsamode);
        if(isCheckedUsamode.equals("true")) {
            Usamode.click();
            Log("USA mode unchecked");
        } else {
            Log("USA Mode already  Unchecked");
        }
        //Click on lock screen button
        driver.findElementById("buttonLock").click();

        super.testTearDown();
    }
}
