package com.isansys.appiumtests.tablet_and_server;

import android.util.Log;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.BuildConfig;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EndToEndVitals extends AppiumTest
{
    WebDriver web_driver;
    final String expected_setup_mode_message = "Setup mode running";
    final String expected_motion_mode_message = "Motion mode running";
    final String expected_leads_off_banner_text = "Leads off";

    final String server_expected_status_removed_text ="Removed";
    final String server_expected_removed_text = "Removed";

    final String waiting_for_data__server = "Waiting for data";
    final String last_data = "Last Data";

    final String chromedriver_path = "C:\\seleniumWebDriver\\chromedriver.exe"; // Change this to the correct path for your test system if not running on the Autotest box

    @Before
    @Override
    public void testSetup() throws MalformedURLException
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-gpu");

        System.setProperty("webdriver.chrome.driver", chromedriver_path);

        web_driver = new ChromeDriver(options);

        web_driver.manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        web_driver.manage().window().maximize();

        super.testSetup();

        helper.setWebDriver(web_driver);

        try
        {
            setSetupModeLength();
        }
        catch (IOException e)
        {
            Log("error setting setup mode length");
        }
    }


    @After
    @Override
    public void testTearDown() {
        super.testTearDown();
    }


    @Test
    public void lifetouchVitalsData() throws IOException {
        helper.printTestStart("lifetouchVitalsData");

        Log("Starting Lifetouch session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        checkLifetouchAppAndServerValuesMatch();
    }


    @Test
    public void lifetouchLeadsOff() throws IOException
    {
        helper.printTestStart("lifetouchLeadsOff");

        Log("Starting Lifetouch session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        // Wait for data on the APP
        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__LIFETOUCH,true);

        logInToServerAndClickOnDashboard();

        // The following happens instead of clickOnCurrentSession to check for leads off first.
        int number_of_retries = 10;
        WebElement session_row = null;

        while((number_of_retries > 0) && (session_row == null))
        {
            // Wait for the session to load...
            try
            {
                Thread.sleep(2500);
            }
            catch (InterruptedException e)
            {
                Log(e.getMessage());
            }

            List<WebElement> dashboard_items =  web_driver.findElements(By.className("trWard"));

            for (WebElement item : dashboard_items) {
                WebElement bed = item.findElement(By.className("dashboardBedColumn"));

                if (bed.getText().contains(BuildConfig.buildMachineName)) {
                    session_row = item;

                    break;
                }
            }

            number_of_retries--;

            if(session_row == null)
            {
                Log("Desired session is null and retries remaining is " + number_of_retries);

                web_driver.navigate().refresh();
            }
            else
            {
                Log("Desired session found with retries remaining " + number_of_retries);
            }
        }

        Log("Waiting for leads off on dashboard");
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("gwStatusLeadsOffIcon")), 60);

        session_row.click();


        //hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData")), 130);

        //rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData")), 130);

        String app_hr = hr_measurement_element__app.getText();
        String app_rr = rr_measurement_element__app.getText();


        WebElement not_attached = driver.findElement(By.id("textViewLifetouchNotAttached"));
        assertEquals(helper.getAppString("lifetouch_not_attached_to_patient"), not_attached.getText());


        WebElement banner = web_driver.findElement(By.id("lifetouchheaderBanner"));
        //Wait until lead off display in the banner
        waitUntil(ExpectedConditions.textToBePresentInElement(banner,"Leads off"), 130);

        Log("After leads off the left hand side of server graph indicate "+banner.getText());
        WebElement leads_off_banner = banner.findElement(By.id("status_LifeT"));
        webPageWaitUntil(ExpectedConditions.visibilityOf(leads_off_banner), 60);

        String banner_text = leads_off_banner.getText();
        Log("Server showing: " + banner_text);
        assertEquals(expected_leads_off_banner_text, banner_text);
        //Check the presence of red border after lead off
        WebElement lead_off_border=web_driver.findElement(By.id("leadsModeLifeTouchDivBox"));
        assertTrue(lead_off_border.isDisplayed());

        WebElement hr_element = web_driver.findElement(By.id("heartRateLifetouchPatientBox")).findElement(By.className("chartVitalReading"));
        WebElement rr_element = web_driver.findElement(By.id("respirationRateLifetouchPatientBox")).findElement(By.className("chartVitalReading"));

        //Wait until Lead off value display on the server screen
        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(hr_element, "Leads off"), 60);
        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(rr_element, "Leads off"), 60);

        String server_hr = hr_element.getText();
        String server_rr = rr_element.getText();

        //replace the "O" for Lead Off to "o", to match the text for both gateway and server.
        app_hr = app_hr.replaceAll("O", "o");
        app_rr = app_rr.replaceAll("O", "o");
        Log("Heart rate on app: " + app_hr);
        Log("Respiration rate on app: " + app_rr);
        Log("Heart rate on server: " + server_hr);
        Log("Respiration rate on server: " + server_rr);

        //ToDo: this assert fails, as the server is blank but the gateway reads "Waiting for Data"
//        assertEquals(server_hr, app_hr);
//        assertEquals(server_rr, app_rr);
    }

    
    @Test
    public void lifetouchSetupMode() throws IOException
    {
        helper.printTestStart("lifetouchSetupMode");
        Log("Starting Lifetouch session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        // Wait for data on the APP
        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);


        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        WebElement LifetouchId = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(LifetouchId.isDisplayed());
        Log("Lifetouch id is: " + LifetouchId.getText());
        WebElement checkbox_options = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(checkbox_options.isDisplayed());

        //Click on the option checkbox
        Log("Start the setup mode for lifetouch ");
        checkbox_options.click();

        WebElement checkbox_setup_mode_options = driver.findElement(By.id("checkBoxLifetouchOptionsSetupMode"));
        checkbox_setup_mode_options.click();
        WebElement checkbox_setup_mode_lhs = driver.findElement(By.id("checkBoxLifetouchConfigurable"));

        String text_setup_mode_checked = checkbox_setup_mode_lhs.getAttribute("checked");
        assertEquals("true", text_setup_mode_checked);
        Log("The Setup Mode check box on the left-hand side of the Lifetouch graph is checked");

        //Wait until Setup Mode value display on the server screen
        webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated(By.className("setupModeClass"), expected_setup_mode_message), 60);

        waitForServerLifetouchData();

        String app_hr = hr_measurement_element__app.getText();
        String app_rr = rr_measurement_element__app.getText();

        // Remove whitespace from app strings
        app_hr = app_hr.replaceAll("\\s", "");
        app_rr = app_rr.replaceAll("\\s", "");

        WebElement hr_box = web_driver.findElement(By.id("heartRateLifetouchPatientBox"));
        WebElement rr_box = web_driver.findElement(By.id("respirationRateLifetouchPatientBox"));
        String server_hr = hr_box.findElement(By.className("chartVitalReading")).getText();
        String server_rr = rr_box.findElement(By.className("chartVitalReading")).getText();
        Log("Heart rate on server: " + server_hr);
        Log("Respiration rate on server: " + server_rr);
       // LHS of the server graph show
        WebElement banner = web_driver.findElement(By.id("lifetouchheaderBanner"));
        Log("Left hand side of the graph contain while setup mode is running "+banner.getText());
        assertTrue(banner.isDisplayed());
        // Check app and server show same values.
        assertEquals(app_hr, server_hr);
        assertEquals(app_rr, server_rr);
       // Exit teh setup mode
        checkbox_setup_mode_lhs.click();
        //Check show setup mode button appear after setup mode finish
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("show_setup_mode_text")), 60);
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("setupModeNoninSwitch1")), 60);
        WebElement Show_setup_mode_text=web_driver.findElement(By.id("show_setup_mode_text"));
        WebElement Show_setup_mode_Switch=web_driver.findElement(By.id("setupModeNoninSwitch1"));
        assertTrue(Show_setup_mode_Switch.isDisplayed());
        assertTrue(Show_setup_mode_text.isDisplayed());
        Log(Show_setup_mode_text.getText());

    }


    @Test
    public void lifetouchMotionMode() throws IOException
    {
        helper.printTestStart("lifetouchMotionMode");
        Log("Starting Lifetouch session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        // Wait for data on the APP
        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        WebElement lifetouch_id = driver.findElement(By.id("textLifetouchHumanReadableDeviceId"));
        assertTrue(lifetouch_id.isDisplayed());
        Log("Lifetouch id is: " + lifetouch_id.getText());

        WebElement checkbox_options = driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(checkbox_options.isDisplayed());
        //Click on the option checkbox
        Log("Start the Motion mode for lifetouch ");
        checkbox_options.click();
        WebElement checkbox_motion_mode_options = driver.findElement(By.id("checkBoxLifetouchOptionsRawAccelerometerMode"));
        checkbox_motion_mode_options.click();

        //Check the Motion Mode checkbox status in LHS side of the graph
        WebElement checkbox_motion_mode_lhs = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String text_motion_mode_checked = checkbox_motion_mode_lhs.getAttribute("checked");
        assertEquals("true", text_motion_mode_checked);
        Log("The Motion Mode check box on the left hand side of the Lifetouch graph is checked");

        WebElement hr_element = web_driver.findElement(By.id("heartRateLifetouchPatientBox")).findElement(By.className("chartVitalReading"));
        WebElement rr_element = web_driver.findElement(By.id("respirationRateLifetouchPatientBox")).findElement(By.className("chartVitalReading"));

        webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated(By.className("motionModeClass"), expected_motion_mode_message), 60);

        waitForServerLifetouchData();

        WebElement motion_mode_message = web_driver.findElement(By.className("motionModeClass"));
        Log("Message shown: " + motion_mode_message.getText());

        String app_hr = hr_measurement_element__app.getText();
        String app_rr = rr_measurement_element__app.getText();

        // Remove whitespace from app strings
        app_hr = app_hr.replaceAll("\\s", "");
        app_rr = app_rr.replaceAll("\\s", "");

        String server_hr = hr_element.getText();
        String server_rr = rr_element.getText();
        Log("Heart rate on server: " + server_hr);
        Log("Respiration rate on server: " + server_rr);
        assertEquals(app_hr, server_hr);
        assertEquals(app_rr, server_rr);
    }

    @Test
    public void lifetouchDeviceRemoved() throws IOException
    {
        helper.printTestStart("lifetouchDeviceRemoved");

        checkAdminSettingsCorrect(false, true, true);

        Log("Starting Lifetouch session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        //Log in to the server
        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        //wait until data appear on the gateway screen
        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        //Remove the Lifetouch
        WebElement button_remove_lifetouch = driver.findElement(By.id("buttonRemoveLifetouch"));
        String text_remove_lifetouch = button_remove_lifetouch.getText();
        String expected_text = helper.getAppString("remove_lifetouch");
        assertEquals(expected_text, text_remove_lifetouch);

        Log("Click on the Remove Lifetouch button");
        button_remove_lifetouch.click();
        Log("Lifetouch removed ");

        checkRemoveDeviceEwsPopup(true); // Shouldn't matter as validity is only 1 minute
        checkRecyclingReminderPopup();
        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Wait for data on the APP
        hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textDeviceRemoved")), 130);

        rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textDeviceRemoved")), 130);

        String app_hr = hr_measurement_element__app.getText();
        String app_rr = rr_measurement_element__app.getText();

        WebElement hr_box = web_driver.findElement(By.id("heartRateLifetouchPatientBox"));
        WebElement rr_box = web_driver.findElement(By.id("respirationRateLifetouchPatientBox"));

        webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated((By.id("status_LifeT")), server_expected_status_removed_text), 60);

        String server_hr = hr_box.findElement(By.className("chartVitalReading")).getText();
        String server_rr = rr_box.findElement(By.className("chartVitalReading")).getText();

        Log("Heart rate on app: " + app_hr);
        Log("Respiration rate on app: " + app_rr);
        Log("Heart rate on server: " + server_hr);
        Log("Respiration rate on server: " + server_rr);
        assertEquals(server_expected_removed_text, server_hr);
        assertEquals(server_expected_removed_text, server_rr);
        //Check the LHS of teh serevr graph.
        WebElement banner = web_driver.findElement(By.id("lifetouchheaderBanner"));
        Log("Left hand side of the graph after device removed "+banner.getText());
        assertTrue(banner.isDisplayed());
    }

    @Test
    public void lifetempVitalsData() throws IOException
    {
        helper.printTestStart("lifetempVitalsData");

        Log("Starting Lifetemp session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        checkLifetempAppAndServerValuesMatch();
        //Check the LHS of teh Lifetemp graph
        WebElement banner = web_driver.findElement(By.id("lifeTempchartBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }

    @Test
    public void lifetempLeadsOff() throws IOException
    {
        helper.printTestStart("lifetempLeadsOff");

        Log("Starting Lifetemp session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        clickOnStartMonitoring();

        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__TEMPERATURE,true);

        logInToServerAndClickOnDashboard();

        // The following happens instead of clickOnCurrentSession to check for leads off first.
        int number_of_retries = 10;

        WebElement session_row = null;

        while((number_of_retries > 0) && (session_row == null))
        {
            // Wait for the session to load...
            try
            {
                Thread.sleep(2500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            List<WebElement> dashboard_items = web_driver.findElements(By.className("trWard"));

            for(WebElement item : dashboard_items)
            {
                WebElement bed = item.findElement(By.className("dashboardBedColumn"));

                if(bed.getText().contains(BuildConfig.buildMachineName))
                {
                    session_row = item;

                    break;
                }
            }

            number_of_retries--;

            if(session_row == null)
            {
                Log("Desired session is null and retries remaining is " + number_of_retries);

                web_driver.navigate().refresh();
            }
            else
            {
                Log("Desired session found with retries remaining " + number_of_retries);
            }
        }

        Log("Waiting for leads off on dashboard");
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("gwStatusLeadsOffIcon")), 60);

        session_row.click();


        // Wait for data on the APP
        WebElement Temp_measurement_element__app = driver.findElement(By.id("textTemperatureReading"));

        waitUntil(ExpectedConditions.textToBePresentInElement(Temp_measurement_element__app, helper.getAppString("textWaitingForData")), 130);

        String app_temp = Temp_measurement_element__app.getText();

        Log("Check leads off warning over graph");
        WebElement not_attached = driver.findElement(By.id("textViewLifetempNotAttached"));
        assertEquals(helper.getAppString("lifetemp_not_attached_to_patient"), not_attached.getText());

        Log("Waiting for leads off on server chart");
        //Wait until Lead off value display on the server screen
        WebElement temp_box = web_driver.findElement(By.id("temperaturePatientBox"));
        //Wait until Lead off value display on the server screen
        //wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("chartVitalReading"), "Leads off"));
        WebElement Lifetemp_status= web_driver.findElement(By.id("StatusClassTem"));

        webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated((By.id("StatusClassTem")), "Leads off"), 75);

        Log(Lifetemp_status.getText());
        String server_temp = temp_box.findElement(By.className("chartVitalReading")).getText();

        //replace the "O" for Lead Off to "o", to match the text for both gateway and server.
        app_temp = app_temp.replaceAll("O", "o");

        Log("Temperature on app: " + app_temp);
        Log("Temperature on server: " + server_temp);

        //ToDo: this assert fails, as the server is blank but the gateway reads "Waiting for Data"
//        assertEquals(server_temp, app_temp);

        //Check the LHS of the Lifetemp server chart
        WebElement banner = web_driver.findElement(By.id("lifeTempchartBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }

    @Test
    public void lifetempDeviceRemoved() throws IOException
    {
        helper.printTestStart("lifetempDeviceRemoved");

        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetemp();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the Lifetemp is " + ALTStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        // Wait for data on the APP
        WebElement Temp_measurement_element__app = driver.findElement(By.id("textTemperatureReading"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(Temp_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        clickOnCurrentSession();

        Log("Check the appearance of lifetemp value in server graph");

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Lifetemp from change session setting page");
        //Remove the Lifetemp
        WebElement LifeTempRemove = driver.findElement(By.id("buttonRemoveThermometer"));
        String LTR=LifeTempRemove.getText();
        Log("Click on the Remove Lifetemp button");
        LifeTempRemove.click();
        Log("Lifetemp removed ");

        checkRemoveDeviceEwsPopup(true); // Shouldn't matter as validity is only 1 minute
        checkRecyclingReminderPopup();
        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // wait for device removed on app
        Temp_measurement_element__app = driver.findElement(By.id("textTemperatureReading"));
        waitUntil(ExpectedConditions.textToBePresentInElement(Temp_measurement_element__app, helper.getAppString("textDeviceRemoved")), 130);
        String app_temp = Temp_measurement_element__app.getText();

        WebElement temp_box = web_driver.findElement(By.id("temperaturePatientBox"));
        WebElement server_reading = temp_box.findElement(By.className("chartVitalReading"));

        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(server_reading, server_expected_removed_text), 90);

        // Finally, output the results...
        String server_temp = server_reading.getText();
        Log("Temperature on app: " + app_temp);
        Log("Temperature on server: " + server_temp);

        //Check the LHS of teh Lifetemp graph
        WebElement banner = web_driver.findElement(By.id("lifeTempchartBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }


    @Test
    public void noninDataEndToEnd() throws IOException {
        helper.printTestStart("noninDataEndToEnd");

        Log("Starting Nonin session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        checkNoninAppAndServerValuesMatch();
        //Check the LHS of the Nonin graph
        WebElement banner = web_driver.findElement(By.id("spO2NoninWristOx2ChartBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }


    @Test
    public void checkNoninLeadsOff()throws IOException
    {
        helper.printTestStart("checkNoninLeadsOff");

        Log("Starting Nonin session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        clickOnStartMonitoring();

        // Set leads off
        helper.spoofCommandIntent_simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__SPO2,true);

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        // Wait for data on the APP
        WebElement SpO2_measurement_element__app = driver.findElement(By.id("textSpO2"));
        waitUntil(ExpectedConditions.textToBePresentInElement(SpO2_measurement_element__app, helper.getAppString("textWaitingForData")), 130);

        String app_SpO2 = SpO2_measurement_element__app.getText();

        Log("Check leads off warning over graph");
        WebElement not_attached = driver.findElement(By.id("textViewPulseOxNotAttached"));
        assertEquals(helper.getAppString("pulse_oximeter_not_attached_to_patient"), not_attached.getText());


        WebElement SpO2_box = web_driver.findElement(By.id("spO2NoninWristOx2PatientBox"));
        WebElement Nonin_status= web_driver.findElement(By.id("spO2NoninStatus"));
        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(Nonin_status, "Leads off"), 130);

        //wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("chartVitalReading"), "Leads off"));
        Log(Nonin_status.getText());

        String server_SpO2 = SpO2_box.findElement(By.className("chartVitalReading")).getText();

        Log("SpO2 on app: " + app_SpO2);
        Log("SpO2 on server: " + server_SpO2);

        //Check the LHS of the Nonin graph
        WebElement banner = web_driver.findElement(By.id("spO2NoninWristOx2ChartBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }


    @Test
    public void noninDeviceRemoved() throws IOException
    {
        helper.printTestStart("noninDeviceRemoved");

        checkAdminSettingsCorrect(false, true, true);

        // Start a session on the tablet
        Log("Starting Nonin session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        clickOnStartMonitoring();

        // Log on to the server
        logInToServerAndClickOnDashboard();

        // Wait for data on the APP
        WebElement SpO2_measurement_element__app = driver.findElement(By.id("textSpO2"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(SpO2_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        clickOnCurrentSession();
        
        //Process for Removing Nonin

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Remove the nonin from the session");

        WebElement button_remove_nonin = driver.findElement(By.id("buttonRemovePulseOximeter"));
        button_remove_nonin.click();
        Log("Nonin removed ");

        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        SpO2_measurement_element__app = driver.findElement(By.id("textSpO2"));

        // Check that the server updates with the expected value
        WebElement SpO2_box = web_driver.findElement(By.id("spO2NoninWristOx2PatientBox"));
        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(SpO2_box, server_expected_removed_text), 90);

        // Check the tablet *hasn't* updated to removed - SpO2 is a long-term measurement so a numerical value should still be shown.
        String app_SpO2 = SpO2_measurement_element__app.getText();
        Assert.assertNotEquals(helper.getAppString("textDeviceRemoved"), app_SpO2);

        // Finally, output the results...
        String server_SpO2 = SpO2_box.findElement(By.className("chartVitalReading")).getText();
        Log("SpO2 on app: " + app_SpO2);
        Log("SpO2 on server: " + server_SpO2);
        //Check the LHS of the Nonin graph
        WebElement banner = web_driver.findElement(By.id("spO2NoninWristOx2ChartBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }

   //Nonin setupmode
    @Test
    public void noninDeviceSetupmode() throws IOException
    {
        helper.printTestStart("noninDeviceSetupmode");

        // Start a session on the tablet
        Log("Starting Nonin session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        clickOnStartMonitoring();

        // Log on to the server
        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        // Wait for data on the APP
        WebElement SpO2_measurement_element__app = driver.findElement(By.id("textSpO2"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(SpO2_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);
        String nonin_device_id = driver.findElement(By.id("textPulseOxHumanReadableDeviceId")).getText();
        Log("Device ID is:" + nonin_device_id);
        // Check the appearance of setup mode checkbox
        WebElement SetUpmodeLHS=driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        assertTrue(SetUpmodeLHS.isDisplayed());
        // String isChecked =SetUpModeButton.getAttribute("checked");
        Log("Left hand side of the Nonin graph is showing :"+SetUpmodeLHS.getText()+" Checkbox");


        // Check that the server updates with the expected value
        WebElement SpO2_box = web_driver.findElement(By.id("spO2NoninWristOx2PatientBox"));
        WebElement spo2_reading = SpO2_box.findElement(By.className("chartVitalReading"));

        waitUntilTextExistsInServerElement(spo2_reading);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(spo2_reading, waiting_for_data__server)), 160);

        String app_SpO2 = SpO2_measurement_element__app.getText();
        String server_SpO2 = spo2_reading.getText();
        Log("SpO2 on app: " + app_SpO2);
        Log("SpO2 on server: " + server_SpO2);
        app_SpO2 = app_SpO2.replaceAll("%", "");
        // Check app and server show same values.
        assertEquals(app_SpO2,server_SpO2);


        Log("Start the setup mode");
        SetUpmodeLHS.click();
        //Wait until Setup Mode value display on the server screen
        webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("setupModeClassOxSpO2"), expected_setup_mode_message), 60);

        //check the presence of setup mode label while setup mode is running
        WebElement SetupMode_Label = web_driver.findElement(By.id("setupModeClassOxSpO2"));
        assertTrue(SetupMode_Label.isDisplayed());

        Log("Setup mode label indicating :" + SetupMode_Label.getText());

        //Exit the SetUp mode graph
        SetUpmodeLHS.click();
        //Check show setup mode button appear after setup mode finish
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("show_setup_mode_text")), 60);
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("setupModeNoninSwitch1")), 60);
        WebElement Show_setup_mode_text=web_driver.findElement(By.id("show_setup_mode_text"));
        WebElement Show_setup_mode_Switch=web_driver.findElement(By.id("setupModeNoninSwitch1"));
        assertTrue(Show_setup_mode_Switch.isDisplayed());
        assertTrue(Show_setup_mode_text.isDisplayed());
        Log(Show_setup_mode_text.getText());
    }

    @Test
    public void bloodPressureDataEndToEnd() throws IOException {
        helper.printTestStart("bloodPressureDataEndToEnd");

        Log("Starting Blood Pressure session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        checkBloodPressureAppAndServerValuesMatch();
        //Check the LHS of the Bloodpressure graph
        WebElement banner = web_driver.findElement(By.id("blood_pressureBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }

    @Test
    public void bloodPressureAndDeviceRemoved() throws IOException {
        helper.printTestStart("bloodPressureAndDeviceRemoved");

        checkAdminSettingsCorrect(false, true, true);

        longTermMeasurementTimeoutSetup(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);

        Log("Starting Blood Pressure session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        Log("Check the appearance of BP value in server graph");
        // Wait for data on the APP
        WebElement BP_Systolic_measurement_element__app = driver.findElement(By.id("textBloodPressureMeasurementSystolic"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Systolic_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement BP_Diastolic_measurement_element__app = driver.findElement(By.id("textBloodPressureMeasurementDiastolic"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Diastolic_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnChangeSessionSettings();

        Log("Click on the Remove Blood Pressure from change session setting page");
        //Remove the BP device
        WebElement BPRemove = driver.findElement(By.id("buttonRemoveBloodPressure"));
        String BPR = BPRemove.getText();
        Log("Button display for BP machine is " + BPR + " Button");
        Log("Click on the Remove BP button");
        BPRemove.click();
        Log("BP removed ");

        checkRemoveDeviceEwsPopup(true);

        clickOnLock();

        helper.spoofQrCode_userUnlock();

        clickOnPatientVitalsDisplay();

        // Wait for the tablet to update the status
        BP_Systolic_measurement_element__app = driver.findElement(By.id("textBloodPressureMeasurementSystolic"));

        waitUntil(ExpectedConditions.textToBePresentInElement(BP_Systolic_measurement_element__app, helper.getAppString("textDeviceRemoved")), 180);

        String app_Systolic = BP_Systolic_measurement_element__app.getText();

        WebElement BP_box = web_driver.findElement(By.id("bloodPressureAndUa767PatientBox"));

        waitUntilTextExistsInServerElement(BP_box);
        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(BP_box, server_expected_removed_text), 90);

        // Finally, output the results...
        String server_BP = BP_box.findElement(By.className("chartVitalReading")).getText();

        Log("BP measurement on app: " + app_Systolic);
        Log("BP measurement: " + server_BP);
        //Check the LHS of the Bloodpressure graph
        WebElement banner = web_driver.findElement(By.id("blood_pressureBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());
    }

    @Test
    public void EndToEnd_with_all_four_devices() throws IOException {
        helper.printTestStart("EndToEnd_with_all_four_devices");

       // Log("Starting Blood Pressure session...");
        checkAdminSettingsCorrect(false, true, true);


        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the Lifetemp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);


        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        Log("Check the appearance of server graph with all the value");

        checkLifetouchAppAndServerValuesMatch();
        checkLifetempAppAndServerValuesMatch();
        checkNoninAppAndServerValuesMatch();
        helper.swipeActionScrollDown();
        checkBloodPressureAppAndServerValuesMatch();
        checkEwsAppAndServerValuesMatch();
    }

    @Test
    public void EndToEnd_for_adult_agerange_with_manuallyEnteredVitalSign() throws IOException
    {
        helper.printTestStart("EndToEnd_for_adult_agerange_with_manuallyEnteredVitalSign");
        Log("Start a session with only mnually enter vital sign only...");
        checkAdminSettingsCorrect(false, true, true);

        setUpSessionWithAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        assertEquals(expected_title, actual_title);
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
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        List<WebElement> Option;
        WebElement optionArea;
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        WebElement Alert= Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(1).getText());
        Option.get(1).click();
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

        clickOnPatientVitalsDisplay();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        Log("Check the appearance of server graph with all the value");
        ManuallyEnteredHeartRateRespirationRateGraphWithData();
        ManuallyenteredTemperatureGraphWithData();
        ManuallyEnteredSpO2GraphWithData();
        ManuallyEnteredBloodpressureGraphWithData();
        helper.swipeActionScrollDown();
        ConsciousnessLevel();
        supplementOL();
        EWS();
    }
    
    @Test
    public void EndToEnd_with_all_four_devices_and_manuallyEnteredData_for_Non_adult_Age_Range() throws IOException {
        helper.printTestStart("EndToEnd_with_all_four_devices_and_manuallyEnteredData_for_Non_adult_Age_Range");

        checkAdminSettingsCorrect(false, false, true);

       // Log("Starting Blood Pressure session...");

        setUpSessionWithNonAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the Lifetemp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);


        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        manuallyEnteredVitalSign();

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
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        List<WebElement> Option;
        WebElement optionArea;
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        WebElement Alert= Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(1).getText());
        Option.get(1).click();
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
        helper.findTextViewElementWithString("respiration_distress").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option available for RespirationDistressLevel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for RespirationDistressLevel is  "+Option.get(0).getText());
        Option.get(0).click();
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        helper.findTextViewElementWithString("capillary_refill_time").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        iSize=Option.size();
        System.out.println("Number of option available for CapillaryRefillTime is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        Log("Value Enter  for CapillaryRefillTime is  "+Option.get(0).getText());
        Option.get(0).click();
        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");

        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option available for FamilyNurseConcern is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for FamilyNurseConcern is  "+Option.get(0).getText());
        Option.get(0).click();
        // Click on Next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        //Enter annoation to teh session
        EnterAnnotation();

        clickOnPatientVitalsDisplay();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        Log("Check the appearance of server graph with all the value");
        checkLifetouchAppAndServerValuesMatch();
        checkLifetempAppAndServerValuesMatch();
        checkNoninAppAndServerValuesMatch();
        checkBloodPressureAppAndServerValuesMatch();
        helper.swipeActionScrollDown();
        ConsciousnessLevel();
        supplementOL();
        EWS();
        CapillaryRifilTime();
        RespirationDistress();
        helper.swipeActionScrollDown();
        familyOrNurseConcern();
        Annotataion();
    }

    @Test
    public void EndToEnd_for_Nonadult_agerange_with_manuallyEnteredVitalSign() throws IOException {
        helper.printTestStart("EndToEnd_for_Nonadult_agerange_with_manuallyEnteredVitalSign");
        Log("Start a session with only mnually enter vital sign only...");

        setUpSessionWithNonAdultAgeRange();

        clickOnManualVitalsOnly();

        // click on Next button from observation set time page
        clickOnNext();
        // To check Next button Leads to the correct page.
        String actual_title = driver.findElement(By.id("textTop")).getText();
        Log("Next button leads to " + actual_title + "Page");
        String expected_title = helper.getAppString("select_how_long_to_use_this_measurement_in_the_early_warning_score");
        assertEquals(expected_title, actual_title);
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
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        List<WebElement> Option;
        WebElement optionArea;
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        WebElement Alert= Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(1).getText());
        Option.get(1).click();
        // Click on Respiration Distress
        Log("Click on Respiration Distress button");
        helper.findTextViewElementWithString("respiration_distress").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option available for RespirationDistressLevel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for RespirationDistressLevel is  "+Option.get(0).getText());
        Option.get(0).click();
        //click on Capillary Refill Time button
        Log("Click on Capillary Refill Time button");
        helper.findTextViewElementWithString("capillary_refill_time").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        iSize=Option.size();
        System.out.println("Number of option available for CapillaryRefillTime is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Log(Option.get(i).getText());

        }
        Log("Value Enter  for CapillaryRefillTime is  "+Option.get(0).getText());
        Option.get(0).click();
        //click on Family or Nurse Concern button
        Log("Click on Family or Nurse Concern button");

        helper.findTextViewElementWithString("family_or_nurse_concern").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option available for FamilyNurseConcern is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for FamilyNurseConcern is  "+Option.get(0).getText());
        Option.get(0).click();
        // Click on Next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();

        clickOnPatientVitalsDisplay();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        Log("Check the appearance of server graph with all the value");
        ManuallyEnteredHeartRateRespirationRateGraphWithData();
        ManuallyenteredTemperatureGraphWithData();
        ManuallyEnteredSpO2GraphWithData();
        ManuallyEnteredBloodpressureGraphWithData();
        helper.swipeActionScrollDown();
        ConsciousnessLevel();
        supplementOL();
        EWS();
        CapillaryRifilTime();
        RespirationDistress();
        helper.swipeActionScrollDown();
        familyOrNurseConcern();
    }

    @Test
    public void EndToEnd_with_all_four_devices_and_manuallyEnteredData_for_adult_Age_Range() throws IOException {
        helper.printTestStart("EndToEnd_with_all_four_devices_and_manuallyEnteredData_for_adult_Age_Range");

        checkAdminSettingsCorrect(false, false, true);

        Log("Starting Blood Pressure session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();
        helper.spoofQrCode_scanDummyDataLifetemp();
        helper.spoofQrCode_scanDummyDataNonin();
        helper.spoofQrCode_scanDummyDataAnD();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);
        String ALTempStatus = driver.findElement(By.id("textViewLifetempSearchStatus")).getText();
        Log("Status of the Lifetemp is " + ALTempStatus);
        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);
        String ABloodPressureMStatus = driver.findElement(By.id("textViewBloodPressureSearchStatus")).getText();
        Log("Status of the BP machine is " + ABloodPressureMStatus);


        clickOnStartMonitoring();

        clickOnLock();

        helper.spoofQrCode_userUnlock();
        manuallyEnteredVitalSign();

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
        // Click on Consciousness Level button
        Log("Click on Consciousness Level button");
        helper.findTextViewElementWithString("consciousness_level").click();
        List<WebElement> Option;
        WebElement optionArea;
        //option avelable for Consciousness lavel
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));
        int iSize=Option.size();
        System.out.println("Number of option avaliable for Consciousness lavel is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        WebElement Alert= Option.get(0);
        //helper.findTextViewElementWithString("alert").click();
        Log("Value Enter  for Consciousness Level is  "+Alert.getText());
        Alert.click();
        // Click on Supplemental Oxygen button
        Log("Click on supplemental Oxygen level button");
        helper.findTextViewElementWithString("supplemental_oxygen").click();
        optionArea= driver.findElement(By.id("recyclerView"));
        Option = optionArea.findElements(By.className("android.widget.TextView"));

        iSize=Option.size();
        System.out.println("Number of option avaliable for Supplemental Oxygen is: "+Option.size());
        for(int i=0; i<iSize; i++)
        {
            Option.get(i).getText();

        }
        Log("Value Enter  for Supplemental Oxygen is  "+Option.get(1).getText());
        Option.get(1).click();

        // Click on Next button
        clickOnNext();
        //Click on Enter
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Cancel button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Enter button again
        driver.findElement(By.id("buttonBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
        //EnterAnnotation
        EnterAnnotation();

        clickOnPatientVitalsDisplay();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        Log("Check the appearance of server graph with all the value");
        checkLifetouchAppAndServerValuesMatch();
        checkLifetempAppAndServerValuesMatch();
        checkNoninAppAndServerValuesMatch();
        checkBloodPressureAppAndServerValuesMatch();
        helper.swipeActionScrollDown();
        EWS();
        ConsciousnessLevel();
        supplementOL();
        Annotataion();

    }

    @Test
    public void Lifetouch_MotionMode_initiate_from_Server() throws IOException
    {
        helper.printTestStart("Lifetouch_MotionMode_initiate_from_Server");
        Log("Starting Lifetemp session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);


        waitForServerLifetouchData();


        WebElement MotionModeButton = web_driver.findElement(By.className("chartDeviceAccelerometerButton2"));
        Log("start the motion mode from server");
        MotionModeButton.click();
        //Wait until Motion Mode  display on the server screen
        WebElement motion_mode_message = web_driver.findElement(By.className("motionModeClass"));

        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(motion_mode_message, expected_motion_mode_message), 60);

        Log("Message shown: " + motion_mode_message.getText());

        //Check the presence of motion mode graph in server page
        WebElement MotionModeGraph = web_driver.findElement(By.id("lifeTouchCanvas"));
        assertTrue(MotionModeGraph.isDisplayed());

        //Check the Motion Mode checkbox status in LHS side of the graph
        Log("Check the PatientVital display graph");
        WebElement motionmodeLHS = driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String isChecked = motionmodeLHS.getAttribute("checked");

        Log("Left hand side of the Lifetouch graph is showing :" + motionmodeLHS.getText());
        //Check the presence of motion mode graph on the app screen
        WebElement motionModeGraphView = driver.findElement(By.id("plotRawAccelerometer"));
        assertTrue(motionModeGraphView.isDisplayed());
        Log("Appearance of motion mode graph in patientVital Display page is : " + motionModeGraphView.isDisplayed());

        //Exit the motion mode
        MotionModeButton.click();
        //check the motionmode label disapear after motion mode exit
    }

    @Test
    public void Lifetouch_SetupMode_initiate_from_Server() throws IOException {
        helper.printTestStart("Lifetouch_SetupMode_initiate_from_Server");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        String ALTStatus = driver.findElement(By.id("textViewLifetouchSearchStatus")).getText();
        Log("Status of the life touch is " + ALTStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);


        waitForServerLifetouchData();


        WebElement SetUpModeButton=web_driver.findElement(By.className("chartDeviceButton2"));
        Log("start the Setup mode from server");
        SetUpModeButton.click();

        //Wait until Setup Mode  display on the server screen
        webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated(By.className("setupModeClass"), expected_setup_mode_message), 60);
       // Check the presence of setup mode label while setup mode is running
        WebElement SetupMode_label= web_driver.findElement(By.id("setupmode_is_running"));
        assertTrue(SetupMode_label.isDisplayed());
        Log("Setup mode label indicating :"+SetupMode_label.getText());

        //Check the presence setup  mode graph in server page
        WebElement SetUpModeGraph=web_driver.findElement(By.id("lifeTouchCanvas"));
        assertTrue(SetUpModeGraph.isDisplayed());

        //Check the SetUp Mode checkbox status in LHS side of the graph
        Log("Check the PatientVital display graph");
        WebElement SetUpmodeLHS=driver.findElement(By.id("checkBoxLifetouchConfigurable"));
        String isChecked =SetUpModeButton.getAttribute("checked");

        Log("Left hand side of the Lifetouch graph is showing :"+SetUpmodeLHS.getText());
        //Check the presence of motion mode graph on the app screen
        WebElement SetUpModeGraphView=driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("SetUp mode graph in patientVital Display page is displayed");

        //Exit the SetUp mode graph
        SetUpModeButton.click();
        waitUntil(ExpectedConditions.invisibilityOf(SetUpModeGraphView), 30);
        Log("SetUp mode graph in patientVital Display page is no longer displayed after ending setup mode");

        //Check setup mode label disappear with the Exit of setup mode
        Log("Setup mode label display status is "+SetupMode_label.isDisplayed()+" after setup mode exit");
        assertFalse(SetupMode_label.isDisplayed());
       // webPageWaitUntil(ExpectedConditions.presenceOfElementLocated(By.id("setupModeNoninSwitch")), 30);
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("setupModeNoninSwitch1")), 60);


        //Check the appearance of setup mode show button for Lifetouch with the Exit of setup mode
        WebElement ShowSetupMode_button=web_driver.findElement(By.id("setupModeNoninSwitch1"));
        Log("Slid the show setup mode switch to right");
        ShowSetupMode_button.click();
        Log("Show Setup mode blob status is "+web_driver.findElement(By.id("divBlob")).isDisplayed());
        Log("Slid the show setup mode switch to Left");
        ShowSetupMode_button.click();
        Log("Show Setup mode blob status is  "+web_driver.findElement(By.id("divBlob")).isDisplayed());
        WebElement ShowSetupMode_Text=web_driver.findElement(By.id("show_setup_mode_text"));
        Log("Status of show setup mode Test is "+ShowSetupMode_Text.isDisplayed()+" While setup mode is running");
        // assertFalse(ShowSetupMode_Text.isDisplayed());
        ShowSetupMode_Text=web_driver.findElement(By.id("show_setup_mode_text"));
        Log(ShowSetupMode_Text.getText()+" text status is  "+ShowSetupMode_Text.isDisplayed()+" after setup mode exit");
        //Check the presence of show setup mode test
        assertTrue(ShowSetupMode_Text.isDisplayed());
        //Check the presence of show setup mode switch.
        assertTrue(ShowSetupMode_button.isDisplayed());
        webPageWaitUntil(ExpectedConditions.presenceOfElementLocated(By.id("setupModeChart")), 30);
        //Setupmode caht
        WebElement setupModeChat= web_driver.findElement(By.id("setupModeChart"));
       //Setup mode blob
        WebElement setupmodeBlob=setupModeChat.findElement(By.className("highcharts-point"));
        setupmodeBlob.click();
        Log("setupmode window open");
        webPageWaitUntil(ExpectedConditions.presenceOfElementLocated(By.className("modal-content")), 30);
        WebElement setupWindow=web_driver.findElement(By.className("modal-content"));
        //assertTrue(setupWindow.isDisplayed());
        //presence of header
        WebElement setupmodeWindowHeader=setupWindow.findElement(By.id("modal-top"));

        waitUntil(ExpectedConditions.textToBePresentInElement(setupmodeWindowHeader, "Lifetouch Setup Mode Chart"), 60);

        Log("Header showing as "+setupmodeWindowHeader.getText());
        WebElement setupmodelog=setupWindow.findElement(By.className("ng-scope"));
        assertTrue(setupmodelog.isDisplayed());
        WebElement setupmodechat=setupWindow.findElement(By.id("modalpopup"));
        assertTrue(setupmodechat.isDisplayed());

        //check the presence of footer
        WebElement footer=setupWindow.findElement(By.className("modal-footer"));
        assertTrue(footer.isDisplayed());

        // find ok button
        WebElement setupmodeOKButton=footer.findElement(By.className("btn-danger"));
        assertTrue(setupmodeOKButton.isDisplayed());
        assertTrue(setupmodeOKButton.isEnabled());

        // Not recommended, but adding in to help reliability...
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //close the setup mode window
        footer.findElement(By.className("btn-danger")).click();
    }


    @Test
    public void NoninSetupMode_Initiated_from_server() throws IOException
    {
        helper.printTestStart("NoninSetupMode_Initiated_from_server");

        Log("Starting Nonin session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataNonin();

        clickOnConnect();

        String ANoninStatus = driver.findElement(By.id("textViewPulseOxSearchStatus")).getText();
        Log("Status of the Nonin is " + ANoninStatus);

        clickOnStartMonitoring();

        logInToServerAndClickOnDashboard();

        clickOnCurrentSession();

        WebElement SpO2_measurement_element__app = driver.findElement(By.id("textSpO2"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(SpO2_measurement_element__app, helper.getAppString("textWaitingForData"))), 160);

        WebElement SpO2_box = web_driver.findElement(By.id("spO2NoninWristOx2PatientBox"));
        WebElement SpO2_element=SpO2_box.findElement(By.className("chartVitalReading"));

        waitUntilTextExistsInServerElement(SpO2_element);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(SpO2_element, waiting_for_data__server)), 90);

        String server_SpO2 = SpO2_element.getText();
        String app_SpO2 = SpO2_measurement_element__app.getText();

        app_SpO2 = app_SpO2.replaceAll("%", "");

        Log("SpO2 on server: " + server_SpO2);

        assertEquals(app_SpO2, server_SpO2);



        WebElement SetUpModeButton = web_driver.findElement(By.id("setupPulseOximeterButton"));
        Log("start the Setup mode from server");
        SetUpModeButton.click();

       // webPageWaitUntil(ExpectedConditions.textToBePresentInElementLocated(By.className("setupModeClassOxSpO2"), expected_setup_mode_message), 60);
       //check the presence of setup mode label while setup mode is running
         WebElement SetupMode_Label=web_driver.findElement(By.id("setupModeClassOxSpO2"));
         assertTrue(SetupMode_Label.isDisplayed());
         Log("Setup mode label indicating :"+SetupMode_Label.getText());
        WebElement ShowSetupMode_Text = web_driver.findElement(By.id("show_setup_mode_text"));
        Log("Status of show setup mode text is "+ShowSetupMode_Text.isDisplayed()+" While setup mode is running");
        assertFalse(ShowSetupMode_Text.isDisplayed());


        //Check the presence of Setup mode graph in server page
        WebElement SetUpModeGraph=web_driver.findElement(By.id("noninCanvas"));
        assertTrue(SetUpModeGraph.isDisplayed());

        //Check the SetUp Mode checkbox status in LHS side of the graph
        Log("Check the PatientVital display graph");
        WebElement SetUpmodeLHS = driver.findElement(By.id("checkBoxPulseOxSetupMode"));
        String isChecked = SetUpmodeLHS.getAttribute("checked");
        assertEquals("true", isChecked);

        Log("Left hand side of the Nonin graph is showing :" + SetUpmodeLHS.getText());
        //Check the presence of Setup mode graph on the app screen
        WebElement SetUpModeGraphView = driver.findElement(By.id("plotRealTimeECG"));
        assertTrue(SetUpModeGraphView.isDisplayed());
        Log("Appearance of SetUp mode graph in patientVital Display page is : true");

        //Exit the SetUp mode graph
        SetUpModeButton.click();
        //Check setup mode label disappear with the Exit of setup mode
        Log("Setup mode label display status is "+SetupMode_Label.isDisplayed()+" after setup mode exit");
        assertFalse(SetupMode_Label.isDisplayed());
        webPageWaitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("setupModeNoninSwitch1")), 75);

        //Check the appearance of setup mode show button for nonin with the Exit of setup mode
        WebElement ShowSetupMode_button=web_driver.findElement(By.id("setupModeNoninSwitch1"));
        Log("Slid the show setup mode switch to right");
        ShowSetupMode_button.click();
        Log("Show Setup mode blob status is "+web_driver.findElement(By.id("divNoin")).isDisplayed());
        Log("Slid the show setup mode switch to Left");
        ShowSetupMode_button.click();
        Log("Show Setup mode blob status is  "+web_driver.findElement(By.id("divNoin")).isDisplayed());
        ShowSetupMode_Text=web_driver.findElement(By.id("show_setup_mode_text"));
        Log(ShowSetupMode_Text.getText()+" text status is  "+ShowSetupMode_Text.isDisplayed()+" after setup mode exit");
        //Check the presence of show setup mode test
        assertTrue(ShowSetupMode_Text.isDisplayed());
        //Check the presence of show setup mode switch.
        assertTrue(ShowSetupMode_button.isDisplayed());
    }

    //Annotation
    @Test
    public void annotationEntryAndDisplay() throws IOException {
        helper.printTestStart("annotationEntryAndDisplay");

        checkAdminSettingsCorrect(false, false, true);

        Log("Starting Lifetouch session...");

        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();
        clickOnLock();
        helper.spoofQrCode_userUnlock();
        EnterAnnotation();
        clickOnPatientVitalsDisplay();
        logInToServerAndClickOnDashboard();
        clickOnCurrentSession();

        //checkLifetouchAppAndServerValuesMatch();
        Annotataion();
    }

    private void logInToServerAndClickOnDashboard()
    {
        Log("Logging in to server...");

        String address = "http://" + helper.getServerAddress() + ":" + helper.getServerPort();

        web_driver.get(address);

        WebElement login = web_driver.findElement(By.id("UserName"));
        WebElement password = web_driver.findElement(By.id("Password"));
        login.sendKeys("user");
      //  password.sendKeys("L1feC4r3!");
        password.sendKeys("try");
        web_driver.findElement(By.className("loginSubmit")).submit();

        Log("Checking dashboard for patient session...");

        web_driver.findElement(By.linkText("Dashboard")).click();
    }


    private void clickOnCurrentSession() throws IOException
    {
        int number_of_retries = 10;

        WebElement desired_session = null;

        while((number_of_retries > 0) && (desired_session == null))
        {
            // Wait for the session to load...
            try
            {
                Thread.sleep(2500);
            }
            catch (InterruptedException e)
            {
                Log(e.getMessage());
            }

            List<WebElement> beds = web_driver.findElements(By.className("dashboardBedColumn"));

            // search through the available beds
            for (WebElement bed : beds)
            {
                //Log(bed.getText());

                if (bed.getText().contains(BuildConfig.buildMachineName))
                {
                    desired_session = bed;

                    break;
                }
            }

            number_of_retries--;

            if(desired_session == null)
            {
                Log("Desired session is null and retries remaining is " + number_of_retries);

                web_driver.navigate().refresh();
            }
            else
            {
                Log("Desired session found with retries remaining " + number_of_retries);
            }
        }

        desired_session.click();
    }


    public void checkBloodPressureAppAndServerValuesMatch()
    {
        Log("Check the appearance of BP vital values in server graph");

        WebElement BP_Systolic_measurement_element__app = driver.findElement(By.id("textBloodPressureMeasurementSystolic"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Systolic_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);
        WebElement BP_Diastolic_measurement_element__app = driver.findElement(By.id("textBloodPressureMeasurementDiastolic"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Diastolic_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement BP_box = web_driver.findElement(By.id("bloodPressureAndUa767PatientBox"));

        webPageWaitUntil(ExpectedConditions.presenceOfElementLocated(By.className("BPmeasurement")), 60);

        WebElement bp_reading = BP_box.findElement(By.className("BPmeasurement"));

        waitUntilTextExistsInServerElement(bp_reading);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_reading, waiting_for_data__server)), 60);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(bp_reading, last_data)), 130);

        String app_Systolic = BP_Systolic_measurement_element__app.getText();

        // WAIT FOR NEXT UPDATE
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Systolic_measurement_element__app, app_Systolic)), 75);

        // Should have just updated on tablet so can get BP values
        app_Systolic = BP_Systolic_measurement_element__app.getText();
        String app_Diastolic=BP_Diastolic_measurement_element__app.getText();
        String app_BP=app_Systolic+"/"+app_Diastolic;

        // removing whitespace, as something is adding it...
        app_BP = app_BP.replaceAll("\\s", "");

        // Wait for server to update with new value
        webPageWaitUntil(ExpectedConditions.textToBePresentInElement(bp_reading, app_BP), 60);

        String server_BP = bp_reading.getText();

        Log("BP measurement on app: " + app_BP);
        Log("BP Measurement on server: " + server_BP);
        assertEquals(server_BP, app_BP);
    }


    public void checkNoninAppAndServerValuesMatch()
    {
        Log("Check the appearance of Nonin vital values in server graph");

        WebElement spo2_measurement_element__app = driver.findElement(By.id("textSpO2"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(spo2_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement spo2_box = web_driver.findElement(By.id("spO2NoninWristOx2PatientBox"));

        WebElement server_spo2 = spo2_box.findElement(By.className("chartVitalReading"));

        waitUntilTextExistsInServerElement(server_spo2);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(server_spo2, waiting_for_data__server)), 130);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(server_spo2, last_data)), 130);

        String app_spo2 = spo2_measurement_element__app.getText();
        String server_spo2_value = server_spo2.getText();

        server_spo2_value = server_spo2_value + "%";

        // removing whitespace, as something is adding it...
        app_spo2 = app_spo2.replaceAll("\\s", "");
        Log("SpO2 on app: " + app_spo2);
        Log("SpO2 on server: " + server_spo2_value);
        assertEquals(server_spo2_value, app_spo2);
    }


    public void checkLifetempAppAndServerValuesMatch()
    {
        Log("Check the appearance of Lifetemp vital values in server graph");

        WebElement temp_measurement_element__app = driver.findElement(By.id("textTemperatureReading"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(temp_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement temp_measurement_element__server = web_driver.findElement(By.id("temperaturePatientBox")).findElement(By.className("chartVitalReading"));

        waitUntilTextExistsInServerElement(temp_measurement_element__server);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(temp_measurement_element__server, waiting_for_data__server)), 130);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(temp_measurement_element__server, last_data)), 130);

        String app_temp = temp_measurement_element__app.getText();

        String server_temp = temp_measurement_element__server.getText();

        // removing whitespace, as something is adding it...
        app_temp = app_temp.replaceAll("\\s", "");

        String degrees_c = helper.getAppString("degreesC");
        String degrees_f = helper.getAppString("degreesF");
        app_temp = app_temp.replace(degrees_c, "");
        app_temp = app_temp.replace(degrees_f, "");


        double server_temp_double = Double.parseDouble(server_temp); // ToDo: this comes out as "last data received" - I think this is a server bug...
        double app_temp_double = Double.parseDouble(app_temp);

        Log("Temperature on app: " + app_temp_double);
        Log("Temperature on server: " + server_temp_double);

        assertEquals(app_temp_double, server_temp_double, 0.01);
    }


    public void checkLifetouchAppAndServerValuesMatch()
    {
        Log("Check the appearance of Lifetouch vital values in server graph");

        WebElement hr_measurement_element__app = driver.findElement(By.id("textHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);


        waitForServerLifetouchData();


        String app_hr = hr_measurement_element__app.getText();
        String app_rr = rr_measurement_element__app.getText();
        WebElement banner = web_driver.findElement(By.id("lifetouchheaderBanner"));
        Log("Left hand side of the graph contain "+banner.getText());
        assertTrue(banner.isDisplayed());

        WebElement hr_box = web_driver.findElement(By.id("heartRateLifetouchPatientBox"));
        WebElement rr_box = web_driver.findElement(By.id("respirationRateLifetouchPatientBox"));

        String server_hr = hr_box.findElement(By.className("chartVitalReading")).getText();
        String server_rr = rr_box.findElement(By.className("chartVitalReading")).getText();

        // removing whitespace, as something is adding it...
        app_hr = app_hr.replaceAll("\\s", "");
        app_rr = app_rr.replaceAll("\\s", "");
        Log("Heart rate on app: " + app_hr);
        Log("Respiration rate on app: " + app_rr);
        Log("Heart rate on server: " + server_hr);
        Log("Respiration rate on server: " + server_rr);
        assertEquals(server_hr, app_hr);
        assertEquals(server_rr, app_rr);
    }


    public void checkEwsAppAndServerValuesMatch()
    {
        Log("Check the appearance of EWS values in server graph");

        WebElement ews_measurement_element = driver.findElement(By.id("textEarlyWarningScoreMeasurement"));

        // Needs to be at least 60 seconds to allow time for EWS to be calcuated...
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(ews_measurement_element, helper.getAppString("textWaitingForData"))), 130);

        //Server EWS
        WebElement EWS_box = web_driver.findElement(By.id("ewsPatientBox"));
        WebElement server_EWS = EWS_box.findElement(By.className("chartVitalReading"));

        waitUntilTextExistsInServerElement(server_EWS);

        String ews_measurement = ews_measurement_element.getText();
        String max_possible  = driver.findElement(By.id("textEarlyWarningScoreMaxPossible")).getText();
        String server_EWS_value = server_EWS.getText();

        Log(" EWS on app : " + ews_measurement + "/" + max_possible);
        Log("EWS on Server : " + server_EWS_value);
    }


    public void ManuallyEnteredHeartRateRespirationRateGraphWithData()
    {
        WebElement hr_measurement_element__app = driver.findElement(By.id("textManuallyEnteredHeartRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        WebElement rr_measurement_element__app = driver.findElement(By.id("textManuallyEnteredRespirationRate"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        String app_hr = driver.findElement(By.id("textManuallyEnteredHeartRate")).getText();
        String app_rr = driver.findElement(By.id("textManuallyEnteredRespirationRate")).getText();
        //Server HR value
        WebElement hr_box = web_driver.findElement(By.id("heartRateLifetouchPatientBox2"));

       //Server RR value
        WebElement rr_box = web_driver.findElement(By.id("respirationRateLifetouchPatientBox2"));

        WebElement hr_element = hr_box.findElement(By.className("chartVitalReading"));
        WebElement rr_element = rr_box.findElement(By.className("chartVitalReading"));

        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(hr_element,waiting_for_data__server)), 130);
        waitUntilTextExistsInServerElement(hr_element);

        String server_hr = hr_element.getText();

        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(rr_element,waiting_for_data__server)), 130);
        waitUntilTextExistsInServerElement(rr_element);

        String server_rr = rr_element.getText();


        // removing whitespace, as something is adding it...
        app_hr = app_hr.replaceAll("\\s", "");
        app_rr = app_rr.replaceAll("\\s", "");
        Log("Heart rate on app: " + app_hr);
        Log("Respiration rate on app: " + app_rr);
        Log("Heart rate on server: " + server_hr);
        Log("Respiration rate on server: " + server_rr);
        assertEquals(server_hr, app_hr);
        assertEquals(server_rr, app_rr);

    }


    public void ManuallyenteredTemperatureGraphWithData()
    {
        WebElement temp_measurement_element__app = driver.findElement(By.id("textManuallyEnteredTemperature"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(temp_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        String app_temp = temp_measurement_element__app.getText();

        WebElement temp_measurement_element__server = web_driver.findElement(By.id("temperaturePatientBox2")).findElement(By.className("chartVitalReading"));

        String server_temp = temp_measurement_element__server.getText();

        app_temp = app_temp.replaceAll("\\s", "");

        String degrees_c = helper.getAppString("degreesC");
        String degrees_f = helper.getAppString("degreesF");

        app_temp = app_temp.replace(degrees_c, "");
        app_temp = app_temp.replace(degrees_f, "");

        double server_temp_double = Double.parseDouble(server_temp);
        double app_temp_double = Double.parseDouble(app_temp);

        Log("Temperature on app: " + app_temp_double);
        Log("Temperature on server: " + server_temp_double);

        assertEquals(app_temp_double, server_temp_double, 0.01);
    }


    public void ManuallyEnteredSpO2GraphWithData()
    {
        WebElement SpO2_measurement_element__app = driver.findElement(By.id("textManuallyEnteredSpO2"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(SpO2_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        String app_SpO2 = SpO2_measurement_element__app.getText();

        WebElement SpO2_box = web_driver.findElement(By.id("spO2NoninWristOx2PatientBox2"));

        String server_SpO2 = SpO2_box.findElement(By.className("chartVitalReading")).getText();
        server_SpO2=server_SpO2+"%";

        // removing whitespace, as something is adding it...
        app_SpO2 = app_SpO2.replaceAll("\\s", "");
        Log("SpO2 on app: " + app_SpO2);
        Log("SpO2 on server: " + server_SpO2);
        assertEquals(server_SpO2, app_SpO2);

    }


    public void ManuallyEnteredBloodpressureGraphWithData()
    {
        WebElement BP_Systolic_measurement_element__app = driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementSystolic"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Systolic_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);
        WebElement BP_Diastolic_measurement_element__app = driver.findElement(By.id("textManuallyEnteredBloodPressureMeasurementDiastolic"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(BP_Diastolic_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);

        String app_Systolic = BP_Systolic_measurement_element__app.getText();
        String app_Diastolic=BP_Diastolic_measurement_element__app.getText();
        String app_BP=app_Systolic+"/"+app_Diastolic;

        WebElement BP_box = web_driver.findElement(By.id("bloodPressureAndUa767PatientBox2"));
        String server_BP = BP_box.findElement(By.className("chartVitalReading")).getText();

        // removing whitespace, as something is adding it...
        app_BP = app_BP.replaceAll("\\s", "");
        Log("BP measurement on app: " + app_BP);
        Log("BP Measurement on server: " + server_BP);
        assertEquals(server_BP, app_BP);

    }


    private void supplementOL()
    {
        WebElement SOL_measurement_element__app = driver.findElement(By.id("textManuallyEnteredSupplementalOxygenLevel"));
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(SOL_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);
        String SOL_app_measurement = SOL_measurement_element__app.getText();
        WebElement SOL_box = web_driver.findElement(By.id("supplementalOxygenLevelPatientBox"));
        String server_SOL = SOL_box.findElement(By.className("chartVitalReading")).getText();
        Log("SupplimentOxygenLevels measurement on app: " + SOL_app_measurement);
        Log("SupplimentOxygenLevles on server: " + server_SOL);
        assertEquals(server_SOL,SOL_app_measurement);


    }


    private void EWS()
    {
       WebElement EWSBanner=web_driver.findElement(By.id("ewsEnabledBanner"));
       Log("LHS of the EWS indicate "+EWSBanner.getText());
        String server_EWS= web_driver.findElement(By.className("chartVitalReading")).getText();
        Log("RHS of the graph indicates "+server_EWS);

    }


    private void ConsciousnessLevel()
    {
        WebElement CL_measurement_element__app = driver.findElement(By.id("textManuallyEnteredConsciousnessLevel"));
        WebElement CL_box = web_driver.findElement(By.id("consciousnessLevelPatientBox"));
        WebElement CL_measurement_element__server = CL_box.findElement(By.className("chartVitalReading"));

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(CL_measurement_element__app, helper.getAppString("textWaitingForData"))), 130);
        waitUntilTextExistsInServerElement(CL_measurement_element__server);

        String server_CL = CL_measurement_element__server.getText();
        String CL_app_measurement = CL_measurement_element__app.getText();

        Log("Consciousness Level measurement on app: " + CL_app_measurement);
        Log("Consciousness Level on server: " + server_CL);
        assertEquals(server_CL, CL_app_measurement);
    }


    private void CapillaryRifilTime()
    {
        String CRTdescription = helper.findTextViewElementWithString("multiline_manually_entered_capillary_refill_time").getText();
        Log("Left hand side of the graph for Capillary Refill Time is showing: " +CRTdescription );
        String CRTmeasurement= driver.findElement(By.id("textManuallyEnteredCapillaryRefillTime")).getText();
        Log("Value for Capillary Refill is " +CRTmeasurement);
        WebElement CRT_box = web_driver.findElement(By.id("capillaryRefillTimePatientBox"));
        String server_CRT = CRT_box.findElement(By.className("chartVitalReading")).getText();
        Log("CapillaryRifilTime on app: " +  CRTmeasurement);
        Log("CapillaryRifilTime on server: " + server_CRT);
        assertEquals(server_CRT,CRTmeasurement);
    }


    private void RespirationDistress()
    {
        String RDdescription = helper.findTextViewElementWithString("multiline_manually_entered_respiration_distress").getText();
        Log("Left hand side of the graph for  Respiration Distress is showing: " +RDdescription );
        Log("Right hand side of the graph showing for  Respiration Distress is :");
        String RDmeasurement= driver.findElement(By.id("textManuallyEnteredRespirationDistress")).getText();
        Log("Value for  Respiration Distress is " +RDmeasurement);

        WebElement RDT_box = web_driver.findElement(By.id("respirationDistressPatientBox"));
        String server_RDT = RDT_box.findElement(By.className("chartVitalReading")).getText();
        Log("RespirationDistress on app: " +  RDmeasurement);
        Log("RespirationDistress on server: " + server_RDT);
        assertEquals(server_RDT,RDmeasurement);
    }


    private void familyOrNurseConcern()
    {
        String FONdescription = helper.findTextViewElementWithString("multiline_manually_entered_family_or_nurse_concern").getText();
        Log("Left hand side of the graph for  Family or Nurse Concern is showing: " +FONdescription );
        String FONmeasurement= driver.findElement(By.id("textManuallyEnteredFamilyOrNurseConcern")).getText();
        Log("Value for  Family or Nurse Concern is " +FONmeasurement);
        WebElement FON_box = web_driver.findElement(By.id("familyOrNurseConcernPatientBox"));
        String server_FON = FON_box.findElement(By.className("chartVitalReading")).getText();
        Log("FamilyOrNurseConcern on App: " + FONmeasurement);
        Log("FamilyOrNurseConcern on server: " + server_FON);
        assertEquals(server_FON,FONmeasurement);
    }


    private void webPageWaitUntil(ExpectedCondition condition, int wait_timeout_in_seconds)
    {
    	web_driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);

        WebDriverWait wait = new WebDriverWait(web_driver, wait_timeout_in_seconds, 500);

        wait.until(web_driver -> condition.apply(web_driver));

        web_driver.manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }


    private void manuallyEnteredVitalSign()
    {
        // Click  the Manual Vital sign Entry button
        Log("Click on the Manual Vital sign button from the mode selection page ");
        driver.findElement(By.id("buttonBigButtonFour")).click();
        // click on Next button from observation set time page
        clickOnNext();

        //select the measurement interval
        // Select the measurement interval as 10 mins
        String  MeasurementTime = helper.findTextViewElementWithString("thirty_minutes").getText();
        Log("measurement Interval set is " + MeasurementTime);
        // Click on the selected measurement type
        helper.findTextViewElementWithString("thirty_minutes").click();
        Log("Lead us to the manual vital entry page with set time and measurement length " );
    }


    private void Annotataion()
    {
        WebElement AnnotationLabel=web_driver.findElement(By.id("combinedBoxPatientBox"));
        Log("LHS of Annotataion graph show "+AnnotationLabel.getText());
        // WebElement chatAnnotation=web_driver.findElement(By.className("highcharts-series-3"));

        webPageWaitUntil(ExpectedConditions.presenceOfElementLocated(By.className("highcharts-point")), 60);
        
        WebElement annotation =web_driver.findElement(By.id("annotation"));
        WebElement AnnotationBlob=annotation.findElement(By.className("highcharts-point"));
        //assertTrue(AnnotationBlob.isDisplayed());
        //click on annotation blob
        AnnotationBlob.click();
        assertTrue(annotation.isDisplayed());
        Log("Annotation window is open");
        //Annotation window header
        WebElement annotationWindow=web_driver.findElement(By.id("modal-top"));
        Log("Header indicating "+annotationWindow.getText());
        //Annotation body
        WebElement AnnotationContain=web_driver.findElement(By.className("annotation-body"));
        Log("Contain of annotation showing "+AnnotationContain.getText());
        //presence Ok button in Annotataion window
        WebElement AnnotationOKButton=web_driver.findElement(By.className("btn-danger"));
        //assertTrue(AnnoationOKButton.isDisplayed());
        AnnotationOKButton.click();
    }


    private void EnterAnnotation()
    {
        // Click on the Annotation button
        driver.findElement(By.id("buttonBigButtonFive")).click();
        // Click on Keyboard button
        driver.findElement(By.id("buttonAnnotationKeyboard")).click();
        // Click on the Next button
        clickOnNext();
        // Click on the Next button from Time page
        clickOnNext();
        //Write the message for Annotation
        driver.findElement(By.id("editTextAnnotationEntry")).click();
        driver.findElement(By.id("editTextAnnotationEntry")).sendKeys("All ok");
        boolean HideButton = driver.findElement(By.id("buttonFinishedTyping")).isDisplayed();
        Log("Button is " + HideButton);
        // Click on Hidekeyboard button
        driver.findElement(By.id("buttonFinishedTyping")).click();
        // Click on the Next button after typing the text
        clickOnNext();
        // Click on the Enter button
        driver.findElement(By.id("buttonBigButtonTop")).click();
        // Click on Confirm button
        driver.findElement(By.id("buttonBigButtonBottom")).click();
    }


    private void waitForServerLifetouchData()
    {
        WebElement hr_box = web_driver.findElement(By.id("heartRateLifetouchPatientBox"));
        WebElement rr_box = web_driver.findElement(By.id("respirationRateLifetouchPatientBox"));

        WebElement server_hr = hr_box.findElement(By.className("chartVitalReading"));
        WebElement server_rr = rr_box.findElement(By.className("chartVitalReading"));

        // wait for text on the server
        waitUntilTextExistsInServerElement(server_hr);
        waitUntilTextExistsInServerElement(server_rr);

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(server_hr, waiting_for_data__server)), 130);
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(server_rr, waiting_for_data__server)), 130);

        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(server_hr, last_data)), 130);
        webPageWaitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(server_rr, last_data)), 130);
    }


    private void waitUntilTextExistsInServerElement(final WebElement element)
    {
        webPageWaitUntil(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return element.getText().length() != 0;
            }
        }, 90);
    }
}

