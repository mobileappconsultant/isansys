package com.isansys.appiumtests;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

public class RealDeviceConnection extends AppiumTest
{
    @Test
    public void test_The_reconnection_process_for_Lifetouch() throws IOException
    {
        helper.printTestStart("test_The_reconnection_process_for_Lifetouch");

        setUpSessionWithAdultAgeRange();

        Log("Now on QR code scan page ");
        //Scan the Lifetouch device manualy
        checkLifetouchAdded();

        clickOnConnect();

        //Click on Search again button
        // WebElement searchbutton= driver.findElement(By.id("buttonLifetouchCancelSearchOrSearchAgain"));
        // waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonLifetouchCancelSearchOrSearchAgain")));
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonLifetouchCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                45);
        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is : " + ALTStatus);
        if(driver.findElement(By.id("buttonLifetouchCancelSearchOrSearchAgain")).isDisplayed())
        {
            Log("search again button appear after lifetouch not found text appear");
        }

        String ALTStatus1="Lifetouch NOT found";
        while (ALTStatus.equals(ALTStatus1))
        {
            Log("Click on the search again button");
            driver.findElement(By.id("buttonLifetouchCancelSearchOrSearchAgain")).click();
            waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonLifetouchCancelSearchOrSearchAgain")),
                    ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                    45);
            ALTStatus1 = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
            Log("Status of the life touch is : " + ALTStatus1);

        }
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        if(driver.findElement(By.id("buttonNext")).isDisplayed())
        {
            Log("Click on the start monitor button after Lifetouch got connected");
            clickOnNext();
        }
    }


    @Test
    public void test_The_reconnection_process_for_Lifetemp() throws IOException
    {
        helper.printTestStart("test_The_reconnection_process_for_Lifetemp");

        setUpSessionWithAdultAgeRange();

        Log("Now on QR code scan page ");

        //Scan the Lifetemp device manualy
        checkLifetempAdded();

        clickOnConnect();

        //Wait until the appearance of either search again button or start monitoring button;
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonLifetempCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                45);
        //Get the status of Life temp
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the Lifetemp is : " + ALTempStatus);
        //Check the appearance of search again button
        if(driver.findElement(By.id("buttonLifetempCancelSearchOrSearchAgain")).isDisplayed())
        {
            Log("search again button appear after lifetemp not found text appear");
        }

        String ALTStatus1="Lifetemp NOT found";
        //Check Lifetemp reconnection process
        while (ALTempStatus.equals(ALTStatus1))
        {
            Log("Click on the search again button");
            driver.findElement(By.id("buttonLifetempCancelSearchOrSearchAgain")).click();
            waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonLifetempCancelSearchOrSearchAgain")),
                    ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                    45);
            ALTStatus1 = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
            Log("Status of the Lifetemp is : " + ALTStatus1);

        }
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        //Click on teh startmonitoring button after lifetemp connected
        if(driver.findElement(By.id("buttonNext")).isDisplayed())
        {
            Log("Click on the start monitor button after Lifetemp got connected");
            clickOnNext();
        }
    }

    @Test
    public void test_The_reconnection_process_for_Nonin() throws IOException
    {
        helper.printTestStart("test_The_reconnection_process_for_Nonin");

        setUpSessionWithAdultAgeRange();

        Log("Now on QR code scan page ");

        checkNoninAdded();

        clickOnConnect();

        //Wait until the appearance of either search again button or start monitoring button;
        waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonPulseOxCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                45);
        //Get the status of Nonin
        String noninConnectionStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is : " + noninConnectionStatus);
        //Check the appearance of search again button
        if(driver.findElement(By.id("buttonPulseOxCancelSearchOrSearchAgain")).isDisplayed())
        {
            Log("search again button appear after Nonin not found text appear");
        }

        String noninConnectionStatus1="Nonin WristOx device NOT found";
        //Check Nonin reconnection process
        while (noninConnectionStatus.equals(noninConnectionStatus1))
        {
            Log("Click on the search again button");
            driver.findElement(By.id("buttonPulseOxCancelSearchOrSearchAgain")).click();
            waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonPulseOxCancelSearchOrSearchAgain")),
                    ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                    45);
            noninConnectionStatus1 = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
            Log("Status of the Nonin is : " +noninConnectionStatus1);

        }
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 45);
        //Click on the startmonitoring button after Nonin connected
        if(driver.findElement(By.id("buttonNext")).isDisplayed())
        {
            Log("Click on the start monitor button after Nonin got connected");
            clickOnNext();
        }
    }

    @Test
    public void test_The_reconnection_process_for_BPmachine() throws IOException
    {
        helper.printTestStart("test_The_reconnection_process_for_BPmachine");

        setUpSessionWithAdultAgeRange();

        Log("Now on QR code scan page ");

        //Scan the Nonin device manually

        checkBPAdded();

        String ButtonName = driver.findElement(By.id("buttonNext")).getText();
        if(ButtonName.equals("Device Settings"))
        {
            clickOnNext();
            //Click on the Connect button from BlueBp -05 MeasurementPlan
            clickOnNext();

        }
        else
        {
            Log("Click on connect button");
            clickOnNext();
        }

        //Wait until the appearance of either search again button or start monitoring button;
        waitUntil(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.id("buttonBloodPressureCancelSearchOrSearchAgain")),
                ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                45);
        //Get the status of Nonin
        String BPConnectionStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the Blood Pressure machine is : " + BPConnectionStatus);
        //Check the appearance of search again button
        Assert.assertTrue(driver.findElement(By.id("buttonBloodPressureCancelSearchOrSearchAgain")).isDisplayed());
        Log("search again button appear after Blood Pressure device NOT found text appear");

        String bp_not_found = helper.getAppString("textBloodPressureNotFound");
        String bp_pairing_failure = helper.getAppString("textBloodPressurePairingFailed");
        //Check Blood pressure reconnection process
        while ((BPConnectionStatus.equals(bp_not_found)) || (BPConnectionStatus.equals(bp_pairing_failure))) {
            Log("Click on the search again button");
            driver.findElement(By.id("buttonBloodPressureCancelSearchOrSearchAgain")).click();

            waitUntil(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.id("buttonBloodPressureCancelSearchOrSearchAgain")),
                    ExpectedConditions.presenceOfElementLocated(By.id("buttonNext"))),
                    45);

            BPConnectionStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();

            Log("Status of the Blood Pressure device is : " + BPConnectionStatus);
        }

        //Click on the startmonitoring button after Nonin connected
        Assert.assertTrue(driver.findElement(By.id("buttonNext")).isDisplayed());
        Log("Click on the start monitor button after Blood Pressure device got connected");
        clickOnNext();
    }


    public void checkLifetouchAdded()
    {
        String LTR = driver.findElement(By.id("buttonRemoveLifetouch")).getText();
        Log("After scanning the Lifetouch,status of button is "+LTR);
        WebElement Lifetouchid= driver.findElement(By.id("textViewLifetouchHumanReadableSerialNumber"));
        String LT=Lifetouchid.getText();
        Log("Lifetouch id is " +LT);
    }

    public void checkLifetempAdded()
    {
        //scan lifeTemp
        String LTempR = driver.findElement(By.id("buttonRemoveThermometer")).getText();
        Log("After scanning the Lifetemp,button status is "+LTempR);
        WebElement Lifetempid= driver.findElement(By.id("textViewAddDevicesThermometerHumanReadableSerialNumber"));
        String LTemp=Lifetempid.getText();
        Log("LifeTemp id is " +LTemp);
    }

    public void checkNoninAdded()
    {
        //Scan Nonin
        String NoninR = driver.findElement(By.id("buttonRemovePulseOximeter")).getText();
        Log("After scanning the Nonin the button status is "+NoninR);
        WebElement Noninid= driver.findElement(By.id("textViewPulseOximeterHumanReadableSerialNumber"));
        String Nonin=Noninid.getText();
        Log("Nonin id is " +Nonin);
    }

    public void checkBPAdded()
    {
        //Scan BPmachine
        String BPR = driver.findElement(By.id("buttonRemoveBloodPressure")).getText();
        Log("After scanning the Bp machine the button status is "+BPR);
        WebElement BPid= driver.findElement(By.id("textViewBloodPressureHumanReadableSerialNumber"));
        String BPMachine=BPid.getText();
        Log("BP machine id is " +BPMachine);
    }
}
