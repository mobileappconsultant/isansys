package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.Assert.assertTrue;

public class StopMonitoringCurrentPatient extends AppiumTest
{
    @Test
    public void check_the_end_session() throws IOException
    {
        String case_id = "22603";
        helper.printTestStart(" check_the_end_session",case_id);
        
        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page

        clickOnBack();

        Log("Leads to Mode selection page ");

        endSession();
        helper.updateTestRail(PASSED);
    }


    @Test
    public void check_the_transfer_session() throws IOException
    {
        String case_id = "22604";
        helper.printTestStart(" check_the_end_session",case_id);

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        //Enter the back button from Observation page
        clickOnBack();

        Log("Leads to Mode selection page ");

        transferSession();
        helper.updateTestRail(PASSED);
    }


    private void endSession()
    {
        Log("Click on the End Session");

        // Click on the Stop Monitoring Current Patient
        driver.findElement(By.id("buttonBigButtonThree")).click();

        // Click on End session
        driver.findElement(By.id("buttonEndSession")).click();

        //Click on Enter
        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();

        //Click on Confirm button
        WebElement confirm_button=  driver.findElement(By.id("buttonEndSessionBigButtonBottom"));
        assertTrue(confirm_button.isDisplayed());
        Log("Confirm button displayed");
        confirm_button.click();
    }


    private void transferSession()
    {
        // Click on the Stop Monitoring Current Patient
        driver.findElement(By.id("buttonBigButtonThree")).click();
        // Click on End session
        driver.findElement(By.id("buttonTransferSession")).click();
        //Click on Enter
        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonEndSessionBigButtonBottom")).click();
    }

    public void endSessionWithDetail()
    {
        // Click on the Stop Monitoring Current Patient
        driver.findElement(By.id("buttonBigButtonThree")).click();

        // Click on End session
        driver.findElement(By.id("buttonEndSession")).click();

        clickOnBack();

        //Click on Enter
        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();

        //Click on Confirm button
        driver.findElement(By.id("buttonEndSessionBigButtonBottom")).click();
    }
}