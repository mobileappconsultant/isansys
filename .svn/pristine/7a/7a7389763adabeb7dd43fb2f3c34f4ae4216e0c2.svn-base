package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

public class AgeSelectionPage extends AppiumTest {
    @Test
    //test the presence of heading in age selection page and radio buttons along with images
    public void CheckPresenceOfheadingAndRadiobutton() throws IOException {

        String case_id = "23298";
        helper.printTestStart("Check Presence Of heading And Radiobutton",case_id);
        startSessionAndGotoPatientAgeRange();
        // Check the presence of Header
        WebElement Pageheader = driver.findElementById("textPleaseSelectPatientAgeRange");
        Assert.assertTrue(Pageheader.isDisplayed());
        Log("Header of the page is : " + Pageheader.getText());
        //Check the presence of age range selection radio button
        WebElement Image_group=driver.findElement(By.id("linearLayoutPictureAgeRange"));
        //Radio button
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
       // Image
        List<WebElement>image=Image_group.findElements((By.className("android.widget.ImageView"))) ;
        int iSize = age.size();
        int jSize= image.size();
        //compare number of image display is same as number of radio button
        assertEquals(iSize,jSize);
        Log("Number age selection radio button is " + age.size());
        Log("Number image dispaly is: "+jSize);
        Log("Radio button display on the screen is");

        for(int i=0; i<iSize; i++)
        {
            Log(age.get(i).getText());
            Assert.assertTrue(age.get(i).isDisplayed());
            Assert.assertTrue(image.get(i).isDisplayed());
        }
        helper.updateTestRail(PASSED);

    }

    @Test
    //Test the radio buttons are clickable and appearance of TextExplaining What RadioButtons Do
    public void imageAndRadioButtons() throws IOException {
        String case_id = "23299";
        helper.printTestStart("imageAndRadioButtons",case_id);
        startSessionAndGotoPatientAgeRange();

        // Check the presence of radio button and image related to it
        // Check the presence of Header
        WebElement Pageheader = driver.findElementById("textPleaseSelectPatientAgeRange");
        Assert.assertTrue(Pageheader.isDisplayed());
        Log("Header of the page is : " + Pageheader.getText());
        //Check the presence of age range selection radio button
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        int iSize = age.size();
        Log("Number age selection radio button is " + age.size());
        Log("Radio button display on the screen is");

        for(int i=0; i<iSize; i++)
        {
            Log(age.get(i).getText());
            Assert.assertTrue(age.get(i).isDisplayed());
            age.get(i).click();
            // Check the Text line for early warning score threshold
            WebElement heading = driver.findElementById("textTextExplainingWhatRadioButtonsDo");
            Assert.assertTrue(heading.isDisplayed());
            Log(heading.getText());
            helper.updateTestRail(PASSED);

        }
    }


    @Test
    public void testEwsThresholdColourBars() throws IOException {

        String case_id = "23300";
        helper.printTestStart("testEwsThresholdColourBars",case_id);

        scanQrCodeAndEnterPatientId();
        
        // Check the Text line for early warning score threshold
        WebElement heading = driver.findElementById("textTextExplainingWhatRadioButtonsDo");
        assertTrue(heading.isDisplayed());
        Log(heading.getText());
        
        // Check the Threshold heading
        
        // Heart rate
        WebElement HeartRate = driver.findElementById("textThresholdDescriptionHeartRate");
        assertTrue(HeartRate.isDisplayed());
        WebElement LifetouchHeartrate = driver.findElement(By.id("seekBarLifetouchHeartRate"));
        assertTrue(LifetouchHeartrate.isDisplayed());
        Log(HeartRate.getText() + " bar status is :" + LifetouchHeartrate.isDisplayed());
        
        // Respiration rate
        WebElement RespirationRate = driver.findElementById("textThresholdDescriptionRespirationRate");
        assertTrue(RespirationRate.isDisplayed());
        WebElement LifetouchRespirationrate = driver.findElement(By.id("seekBarLifetouchHeartRate"));
        assertTrue(LifetouchRespirationrate.isDisplayed());
        Log(RespirationRate.getText() + " bar status is :" + LifetouchRespirationrate.isDisplayed());
        
        // Temperature
        WebElement Temperature = driver.findElementById("textThresholdDescriptionTemperature");
        assertTrue(Temperature.isDisplayed());
        WebElement LifetempTemperature = driver.findElement(By.id("seekBarLifetemp"));
        assertTrue(LifetempTemperature.isDisplayed());
        Log(Temperature.getText() + " bar status is :" + LifetempTemperature.isDisplayed());
        
        // SpO2
        WebElement SpO2 = driver.findElementById("textThresholdDescriptionSpO2");
        assertTrue(SpO2.isDisplayed());
        WebElement PulseOX = driver.findElement(By.id("seekBarPulseOx"));
        assertTrue(PulseOX.isDisplayed());
        Log(SpO2.getText() + " bar status is :" + PulseOX.isDisplayed());
        
        // Systolic Blood Pressure
        WebElement Bloodpressure = driver.findElementById("textThresholdDescriptionBloodPressure");
        assertTrue(Bloodpressure.isDisplayed());
        WebElement Bloodpressurebar = driver.findElement(By.id("seekBarBloodPressure"));
        assertTrue(Bloodpressurebar.isDisplayed());
        Log(Bloodpressure.getText() + " bar status is :" + Bloodpressurebar.isDisplayed());
        helper.updateTestRail(PASSED);
    }




    private void startSessionAndGotoPatientAgeRange() throws IOException
    {
        scanQrCodeAndEnterPatientId();

        // Check the presence of Header
        WebElement Pageheader = driver.findElementById("textPleaseSelectPatientAgeRange");
        assertTrue(Pageheader.isDisplayed());
        //Log("Header of the page is : " + Pageheader.getText());
    }
}
