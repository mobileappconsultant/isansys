package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.isansys.appiumtests.AppiumTest;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;


public class PatientNameLookUp extends AppiumTest
{
    @Test
    // Check the patient lookup feature is enable and Get patientname From Server button is appearing
    public void checkPatientLookupKeypadShown()throws IOException
    {
        String case_id = "23214";
        helper.printTestStart("checkPatientLookupKeypadShown",case_id);


        // check that patient lookup check box is checked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(true, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        assertTrue(driver.findElement(By.id("frameLayoutCaseIdEntry")).isDisplayed());


        Log("Check the keypad apperance");

        assertTrue(driver.findElement(By.id("buttonNumberOne")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberTwo")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberThree")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberFour")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberFive")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberSix")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberSeven")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberEight")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberNine")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonNumberZero")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonDelete")).isDisplayed());

        driver.findElement(By.id("buttonNumberZero")).click();

        //test the appearance of get patient details from server button
        assertTrue(driver.findElement(By.id("buttonGetPatientDetailsFromServer")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonGetPatientDetailsFromServer")).isEnabled());
        Log("Get patient details From Server button is visible and clickable");
        helper.updateTestRail(PASSED);
    }


    @Test
    public void patientLookupByCaseId_1() throws IOException {
        String case_id = "23215";

        helper.printTestStart("patientLookupByCaseId_1", case_id);

        // check that patient lookup check box is checked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(true, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        Log("Enter case ID of 1");
        // enter case ID 1
        driver.findElement(By.id("buttonNumberOne")).click();
        waitUntil(ExpectedConditions.textToBe(By.id("textEnteredValue"), "1"), 30);

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();

        // wait for lookup results to show
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("linearLayoutShowPatientDetails")), 30);

        // check values
        String first_name = driver.findElement(By.id("textViewPopupFirstName")).getText();
        String last_name = driver.findElement(By.id("textViewPopupLastName")).getText();
        String date_of_birth = driver.findElement(By.id("textViewPopupDateOfBirth")).getText();
        String gender = driver.findElement(By.id("textViewPopupGender")).getText();

        Log("Lookup Complete - " + first_name + " " + last_name + ", " + date_of_birth + ", " + gender);

        assertEquals("Full", first_name);
        assertEquals("Details", last_name);
        assertEquals("1969-04-01", date_of_birth);
        assertEquals("Male", gender);

        // check buttons
        assertTrue(driver.findElement(By.id("buttonCorrectPatientDetails")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonIncorrectPatientDetails")).isDisplayed());

        Log("Go back and re-enter case ID");
        // check "back" goes back to editing
        driver.findElement(By.id("buttonIncorrectPatientDetails")).click();

        // click delete
        driver.findElement(By.id("buttonDelete")).click();

        waitUntil(ExpectedConditions.textToBe(By.id("textEnteredValue"), ""), 30);

        // enter 1 again
        driver.findElement(By.id("buttonNumberOne")).click();
        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();


        Log("Go to next page and check patient name shown");
        // click "correct" to progress
        driver.findElement(By.id("buttonCorrectPatientDetails")).click();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("startSessionSettingsActivity")), 30);

        // Check patient name is present
        assertEquals("Patient : Full Details (1969-04-01)", driver.findElement(By.id("textHeaderPatientId")).getText());

        helper.updateTestRail(PASSED);
    }


    @Test
    public void patientLookupByCaseId_2() throws IOException {
        String case_id = "23216";

        helper.printTestStart("patientLookupByCaseId_2", case_id);

        // check that patient lookup check box is checked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(true, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        Log("Enter case ID of 2");
        // enter case ID 2
        driver.findElement(By.id("buttonNumberTwo")).click();

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();


        // wait for lookup results to show
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("linearLayoutShowPatientDetails")), 30);

        // check values
        String first_name = driver.findElement(By.id("textViewPopupFirstName")).getText();
        String last_name = driver.findElement(By.id("textViewPopupLastName")).getText();
        String date_of_birth = driver.findElement(By.id("textViewPopupDateOfBirth")).getText();
        String gender = driver.findElement(By.id("textViewPopupGender")).getText();

        Log("Lookup Complete - " + first_name + " " + last_name + ", " + date_of_birth + ", " + gender);

        assertEquals("No", first_name);
        assertEquals("DOB or Gender", last_name);
        assertEquals("----", date_of_birth);
        assertEquals("----", gender);

        // check buttons
        assertTrue(driver.findElement(By.id("buttonCorrectPatientDetails")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonIncorrectPatientDetails")).isDisplayed());

        Log("Go to next page and check patient name shown");
        // click "correct" to progress
        driver.findElement(By.id("buttonCorrectPatientDetails")).click();


        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("startSessionSettingsActivity")), 30);

        // Check patient name is present
        assertEquals("Patient : No DOB or Gender (----)", driver.findElement(By.id("textHeaderPatientId")).getText());

        helper.updateTestRail(PASSED);
    }


    @Test
    public void patientLookupByCaseId_3() throws IOException {
        String case_id = "23217";

        helper.printTestStart("patientLookupByCaseId_3", case_id);

        // check that patient lookup check box is checked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(true, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        Log("Enter case ID of 3");
        // enter case ID 3
        driver.findElement(By.id("buttonNumberThree")).click();

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();

        // wait for lookup to complete - should see a back button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonGoBackToEditing")), 30);

        // check buttons
        assertTrue(driver.findElement(By.id("buttonGoBackToEditing")).isDisplayed());

        assertEquals(helper.getAppString("patient_id_not_found"), driver.findElement(By.id("textViewPatientIdLookupStatus")).getText());

        Log("Go back and delete case ID");
        // click "buttonGoBackToEditing" to dismiss
        driver.findElement(By.id("buttonGoBackToEditing")).click();

        // click delete to back button worked
        driver.findElement(By.id("buttonDelete")).click();
        waitUntil(ExpectedConditions.textToBe(By.id("textEnteredValue"), ""), 30);

        helper.updateTestRail(PASSED);
    }


    @Test
    public void patientLookupByCaseId_4() throws IOException {
        String case_id = "23218";

        helper.printTestStart("patientLookupByCaseId_4", case_id);

        // check that patient lookup check box is checked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(true, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        Log("Enter case ID of 4");
        // enter case ID 4
        driver.findElement(By.id("buttonNumberFour")).click();

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();

        // wait for lookup to complete - should see a back button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonGoBackToEditing")), 30);

        // check buttons
        assertTrue(driver.findElement(By.id("buttonGoBackToEditing")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonUseCaseId")).isDisplayed());


        // check status is timed out
        assertEquals(helper.getAppString("server_timed_out"), driver.findElement(By.id("textViewPatientIdLookupStatus")).getText());

        Log("Go back and re-enter case ID");
        // click "buttonGoBackToEditing" to dismiss
        driver.findElement(By.id("buttonGoBackToEditing")).click();

        // click delete to back button worked
        driver.findElement(By.id("buttonDelete")).click();
        waitUntil(ExpectedConditions.textToBe(By.id("textEnteredValue"), ""), 30);

        // enter case ID 4 again
        driver.findElement(By.id("buttonNumberFour")).click();

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();

        Log("Go to next page and check patient name shown");
        // click "use case ID" to progress
        driver.findElement(By.id("buttonUseCaseId")).click();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("startSessionSettingsActivity")), 30);

        // Check patient name is case ID
        assertEquals("Patient : 4", driver.findElement(By.id("textHeaderPatientId")).getText());

        helper.updateTestRail(PASSED);
    }


    @Test
    public void patientLookupByCaseId_5() throws IOException {
        String case_id = "23219";

        helper.printTestStart("patientLookupByCaseId_5", case_id);

        // check that patient lookup check box is checked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(true, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        Log("Enter case ID of 5");
        // enter case ID 5
        driver.findElement(By.id("buttonNumberFive")).click();

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();

        // wait for lookup to complete - should see a back button
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonGoBackToEditing")), 30);

        // check buttons
        assertTrue(driver.findElement(By.id("buttonGoBackToEditing")).isDisplayed());
        assertTrue(driver.findElement(By.id("buttonUseCaseId")).isDisplayed());

        // check status is timed out
        assertEquals(helper.getAppString("server_returned_an_error"), driver.findElement(By.id("textViewPatientIdLookupStatus")).getText());

        Log("Go back and re-enter case ID");
        // click "buttonGoBackToEditing" to dismiss
        driver.findElement(By.id("buttonGoBackToEditing")).click();

        // click delete to back button worked
        driver.findElement(By.id("buttonDelete")).click();
        waitUntil(ExpectedConditions.textToBe(By.id("textEnteredValue"), ""), 30);

        // enter case ID 5 again
        driver.findElement(By.id("buttonNumberFive")).click();

        driver.findElement(By.id("buttonGetPatientDetailsFromServer")).click();

        Log("Go to next page and check patient name shown");
        // click "use case ID" to progress
        driver.findElement(By.id("buttonUseCaseId")).click();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("startSessionSettingsActivity")), 30);

        // Check patient name is case ID
        assertEquals("Patient : 5", driver.findElement(By.id("textHeaderPatientId")).getText());

        helper.updateTestRail(PASSED);
    }


    @Test
    // Check the patient lookup feature if unchecked
    public void disablePatientLookup()throws IOException
    {
        String case_id = "23220";

        helper.printTestStart("disablePatientLookup",case_id);

        // check that patient lookup check box is unchecked and autoaddEWS check box is checked
        checkAdminSettingsCorrect(false, true, false);

        //scan User Qr code
        helper.spoofQrCode_userUnlock();
        //Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        List<WebElement> keypad = driver.findElements(By.id("frameLayoutCaseIdEntry"));
        assertEquals(0, keypad.size());

        //to click on the Patient id field and entered the id
        helper.setPatientId(generatePatientId());

        // Check the PatientnameLookup button is not visible

        if( driver.findElements(By.id("buttonLookupPatientNameFromServer")).size() == 0)
        {
            Log("Get patientname From Server button is not visible");
        }

        Log("Click on the next button");
        //Click on the Next button
        clickOnNext();
        helper.updateTestRail(PASSED);
    }


    @Override
    @After
    public void testTearDown()
    {
        clickOnLock();

        try
        {
            checkAdminSettingsCorrect(false, true, true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        super.testTearDown();
    }
}
