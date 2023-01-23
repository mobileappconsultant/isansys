package com.isansys.appiumtests;

import com.isansys.appiumtests.utils.AppiumTestWatcher;
import com.isansys.appiumtests.utils.TestHelper;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.BuildConfig;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.Setting;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public abstract class AppiumTest
{
    protected AndroidDriver driver;
    protected TestHelper helper;

    @Rule
    public AppiumTestWatcher watcher = new AppiumTestWatcher();

    protected final int IMPLICIT_TIMEOUT_SECONDS = 10;

    static int test_count;

    private final static String ACTION_ENTER_TEST_MODE = "com.isansys.patientgateway.ACTION_ENTER_TEST_MODE";

    // Allow 10 seconds timeout error when checking measurement validity (mostly to account for the time it takes Appium to respond)
    public static final int TIMEOUT_ERROR_MS = 10000;

    @BeforeClass
    public static void classSetup()
    {
        test_count = 1;

        try
        {
            sendCommandToGateway_enableTestMode(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Before
    public void testSetup() throws MalformedURLException
    {
        // Set up the UI apk location to access the contents of strings.xml
        File app = new File(BuildConfig.apk_path);

        // Created object of DesiredCapabilities class.
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Set Android deviceName desired capability. This is ignored for Android devices, but still required by the driver, so we have to set it.
        capabilities.setCapability("deviceName", "ignored");

        // Set Android platformName desired capability. It's Android in our case here.
        capabilities.setCapability("platformName", "Android");

        // In order to get the app's strings.xml resource, need to specify the APK we're using.
        capabilities.setCapability("app", app.getAbsolutePath());

        // Set android appPackage desired capability. It is
        // com.android.calculator2 for calculator application.
        // Set your application's appPackage if you are using any other app.
        capabilities.setCapability("appPackage", "com.isansys.pse_isansysportal");

        // Set android appActivity desired capability. It is
        // com.android.calculator2.Calculator for calculator application.
        // Set your application's appPackage if you are using any other app.
        capabilities.setCapability("appActivity", "com.isansys.pse_isansysportal.MainActivity");
        //capabilities.setCapability("unicodeKeyboard", true);

        capabilities.setCapability("automationName", "UiAutomator2");

        if(BuildConfig.tabletSerialNumber != null)
        {
            capabilities.setCapability("udid", BuildConfig.tabletSerialNumber);
        }

        capabilities.setCapability("noReset", true);

        // Set Appium server address and port number in URL string.
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        driver.manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        driver.setSetting(Setting.WAIT_FOR_IDLE_TIMEOUT, 500);

        helper = new TestHelper(driver);

        //System.out.println("Test helper initialised");

        watcher.initialise(helper);

        // Wait for the isansys logo in the header so we know the UI has stabilised
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageIsansysLogo")), 30);

        helper.spoofCommandIntent_stopFastUiUpdates();
    }


    @After
    public void testTearDown()
    {
        helper.printTestEnd();

        test_count++;
    }

    @AfterClass
    public static void classCleanUp()
    {
        try
        {
            sendCommandToGateway_enableTestMode(false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    protected void waitUntil(ExpectedCondition condition, int wait_timeout_in_seconds)
    {
    	driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
    	
        WebDriverWait wait = new WebDriverWait(driver, wait_timeout_in_seconds, 500);

        wait.until(driver -> condition.apply(driver));

        driver.manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }


    protected void installationProcessWithoutCompleting() throws IOException
    {
        // Click on Start Installation Wizard
        Log("Welcome");

        driver.findElement(By.id("buttonBigButtonTwo")).click();

        // Scan the Installation QR Code - spoof by sending an intent to the UI
        helper.spoofQrCode_installation();

        // Check the server address
        WebElement lifeguardServerAddress = driver.findElement(By.id("textViewServerAddress"));

        String three_dashes = helper.getAppString("textThreeDashes");

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(lifeguardServerAddress, three_dashes)), 30);
        String lSAddress = lifeguardServerAddress.getText();
        Log(lSAddress);

        Assert.assertEquals(helper.getServerAddress(), lSAddress);
        Log("Correct server address ");

        // Check the port address
        WebElement lifeguardServerPort = driver.findElement(By.id("textViewServerPort"));
        String lSPort = lifeguardServerPort.getText();
        Log("Actual port address is " + lSPort);

        Assert.assertEquals(helper.getServerPort(), lSPort);
        Log("Correct Port address ");

        // Check the server WAMP Port
        WebElement lifeguardServerWAMPPort = driver.findElement(By.id("textViewRealTimePort"));
        String lSWampPort = lifeguardServerWAMPPort.getText();
        Log("Actual port address is " + lSWampPort);
        Assert.assertEquals(helper.getServerRealTimePort(), lSWampPort);
        Log("Correct Port address ");

        String test_ward = "Test Ward";


        waitUntil(elementClickablePropertyIsTrue(By.id("spinnerWardList")), 120);

        // Click on the ward spinner and select the  ward
        helper.clickElementOnSpinner("spinnerWardList", test_ward);


        // Click on the bed spinner and select the bed
        Log("Selecting bed by computer name: " + BuildConfig.buildMachineName);
        helper.clickElementOnSpinner("spinnerBedList", BuildConfig.buildMachineName);

        // Click on Set bed Details
        driver.findElement(By.id("buttonSetBedDetails")).click();

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderWardName"), test_ward), 30);
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderBedName"), BuildConfig.buildMachineName), 30);

        // Check Ward detail has set
        String wardDetail = driver.findElement(By.id("textViewSelectedWard")).getText();
        Log("Selected ward detail is " + wardDetail);

        // Check the bed detail has set
        String BedDetail = driver.findElement(By.id("textViewSelectedBed")).getText();
        Log("Selected bed detail is " + BedDetail);

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonSetupComplete")), 300);

        Log("Setup Complete");
    }


    protected void installationProcessFull() throws IOException
    {
        installationProcessWithoutCompleting();

        driver.findElement(By.id("buttonSetupComplete")).click();

        // Check for QR scan page
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textPleaseScanIdQrCode")), 60);

        Log("Installation process finished");
    }


    protected String generatePatientId()
    {
        String id = this.getClass().getSimpleName();

        id += " test " + String.format("%03d", test_count);

        Log("**** Generating Hospital Patient ID: " + id + " ****");

        return id;
    }


    protected void checkAdminSettingsCorrect(boolean check_patient_name_lookup, boolean check_auto_add_ews, boolean check_use_predefined_annotations) throws IOException
    {
        // Scan the Admin QR code - spoof via an intent
        helper.spoofQrCode_adminUnlock();

        // Check if the patient name lookup needs changing
        boolean patient_vitals_checked;
        WebElement EVitalSigns = driver.findElement(By.id("checkBoxPatientNameLookup"));
        patient_vitals_checked = EVitalSigns.getAttribute("checked").equals("true");

        Log("PatientNameLookup Check box Status " + patient_vitals_checked);
        if(patient_vitals_checked != check_patient_name_lookup)
        {
            EVitalSigns.click();
            Log("PatientNameLookup changed");
        }
        else
        {
            Log("PatientNameLookup already in correct state");
        }

        // Check if the auto-add ews needs changing
        boolean auto_add_ews_checked;
        WebElement ews_checkbox = driver.findElement(By.id("checkBoxAutoAddEarlyWarningScores"));
        auto_add_ews_checked = ews_checkbox.getAttribute("checked").equals("true");

        Log("AutoAddEWS Check box Status " + auto_add_ews_checked);
        if(auto_add_ews_checked != check_auto_add_ews)
        {
            ews_checkbox.click();
            Log("EWS checkbox changed");
        }
        else
        {
            Log("EWS checkbox already in correct state");
        }

        // Check if use predefined annotations needs changing
        boolean use_predefined_annotations_checked;
        WebElement annotations_checkbox = driver.findElement(By.id("checkBoxEnablePredefinedAnnotations"));
        use_predefined_annotations_checked = annotations_checkbox.getAttribute("checked").equals("true");

        Log("Use Pre-defined Annotations Check box Status " + use_predefined_annotations_checked);
        if(use_predefined_annotations_checked != check_use_predefined_annotations)
        {
            annotations_checkbox.click();
            Log("Pre-defined Annotations checkbox changed");
        }
        else
        {
            Log("Pre-defined Annotations checkbox already in correct state");
        }

        // Turn of periodic setup mode by default - individual tests can enable it if they need it
        WebElement checkbox_periodic_setup_mode = driver.findElementById("checkBoxDevicePeriodicSetupModeEnabled");

        // Check the periodic setup mode check box
        String isCheckedShowPeriodicSetupMode = checkbox_periodic_setup_mode.getAttribute("checked");
        Log("Show IP Address on Wifi popup  Checkbox status " + isCheckedShowPeriodicSetupMode);
        if(isCheckedShowPeriodicSetupMode.equals("true")) {
            checkbox_periodic_setup_mode.click();
            Log("Periodic setup mode check box un-checked");
        } else {
            Log("Periodic setup mode check box  already un-checked");
        }

        //set the display time out as No time out from Admin page
        helper.clickElementOnSpinner("spinnerDisplayTimeoutLength", helper.getAppString("two_minutes"));

        WebElement checkbox_patient_vitals_timeout = driver.findElementById("checkBoxApplyDisplayTimeoutToPatientVitalsDisplay");

        String Usedisplaytimeout_onchatpage = checkbox_patient_vitals_timeout.getAttribute("checked");
        Log("Use display timeout on chart page status is " + Usedisplaytimeout_onchatpage);
        if (Usedisplaytimeout_onchatpage.equals("true"))
        {
            checkbox_patient_vitals_timeout.click();
            Log("Use display timeout on chart page unchecked");
        }
        else
        {
            Log("Use display timeout on chart page already unchecked");
        }

        clickOnLock();
    }


    protected void setSetupModeLength() throws IOException
    {
        // Wait until QR unlock shown
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("barCodeActivity_QrUnlock")), 30);

        UnlockWithAdminQrCodeAndShowGatewaySettings();

        // Wait until Admin page shown with setup mode spinner
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("spinnerSetupModeLength")), 30);

        // Set to 20 minutes to allow enough time for tests to run.
        helper.clickElementOnSpinner("spinnerSetupModeLength", helper.getAppString("twenty_minutes"));
        WebElement SetupModeLength=driver.findElement(By.id("android:id/text1"));

        Log("Setup mode length set as : " + SetupModeLength.getText());

        // Back to patient vital display page
        driver.findElementById("buttonLock").click();
    }


    protected void Log(String line)
    {
        System.out.println(line);
    }



    /**
     * An expectation for checking an element is visible and has the clickable property set to true.
     *
     * @param locator used to find the element
     * @return the WebElement once it is located and clickable (visible and enabled)
     */
    public static ExpectedCondition<WebElement> elementClickablePropertyIsTrue(final By locator) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                WebElement element = ExpectedConditions.visibilityOfElementLocated(locator).apply(driver);
                try {
                    String clickable = element.getAttribute("clickable");
                    if (element != null && clickable.equals("true")) {
                        return element;
                    }
                    return null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @NotNull
            @Override
            public String toString() {
                return "element to be clickable: " + locator;
            }
        };
    }


    protected void scanQrCodeAndEnterPatientId() throws IOException
    {
        helper.spoofQrCode_userUnlock();

        // Wait for up to 20 minutes(!) - yes really. Won't normally take this long, except if the log file zip tasks are running.
        // This is to ensure that in those rare cases we wait for the EWS thresholds to sync properly.
        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonOne")), 1200);

        // Click on the Start monitoring a patient
        driver.findElement(By.id("buttonBigButtonOne")).click();

        // Click on the Patient ID field and entered the ID
        helper.setPatientId(generatePatientId());

        // Click on the Next button
        clickOnNext();
    }


    public void setUpSessionWithAdultAgeRange() throws IOException
    {
        scanQrCodeAndEnterPatientId();

        // wait for page/thresholds to load...
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.RadioButton")), 120);

        // Select Adult Age Range
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        int iSize = age.size();
        Log("Select "+age.get(iSize-1).getText()+" age range");
        age.get(iSize-1).click();
        clickOnNext();
    }


    public void setUpSessionWithNonAdultAgeRange() throws IOException
    {
        scanQrCodeAndEnterPatientId();

        // wait for page/thresholds to load...
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.RadioButton")), 120);

        //Check the presence of age range selection radio button
        List<WebElement> age = driver.findElements(By.className("android.widget.RadioButton"));
        Log("Select "+age.get(0).getText()+" age range");
        age.get(0).click();
        clickOnNext();
    }


    public void clickOnBack()
    {
        driver.findElement(By.id("buttonBack")).click();
    }


    public void clickOnNext()
    {
        driver.findElement(By.id("buttonNext")).click();
    }


    public void clickOnLock()
    {
        Log("Lock the screen");

        driver.findElement(By.id("buttonLock")).click();
    }


    public void clickOnAdminTabGatewaySettings()
    {
        Log("Click on Admin Tab - Gateway Settings");

        driver.findElement(By.id("buttonAdminTabGatewaySettings")).click();
    }


    public void clickOnAdminTabServerConnectionSettings()
    {
        Log("Click on Admin Tab - Server Connection Settings");

        driver.findElement(By.id("buttonAdminTabServerConnectionSettings")).click();
    }


    public void UnlockWithAdminQrCodeAndShowServerConnectionSettings() throws IOException
    {
        helper.spoofQrCode_adminUnlock();

        // Show the Server Connection tab on the Admin page
        clickOnAdminTabServerConnectionSettings();
    }


    public void UnlockWithAdminQrCodeAndShowGatewaySettings() throws IOException
    {
        // Scan the Admin QR code - spoof via an intent
        helper.spoofQrCode_adminUnlock();

        // Show the Gateway Settings tab on the Admin page
        clickOnAdminTabGatewaySettings();
    }


    public void clickOnManualVitalsOnly()
    {
        driver.findElement(By.id("buttonManualVitalsOnly")).click();

        Log("Leads to Observation page to set Time ");
    }


    public void clickOnStartMonitoring()
    {
        // wait until device connects...
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonNext")), 30);

        Log("Click on start monitor button");

        // Click on start monitoring button
        clickOnNext();
    }


    public void clickOnConnect()
    {
        Log("Click on connect button");

        clickOnNext();
    }


    public void clickOnChangeSessionSettings()
    {
        Log("Click on Change Session settings");

        driver.findElement(By.id("buttonBigButtonTwo")).click();
    }


    public void clickOnPatientVitalsDisplay()
    {
        driver.findElement(By.id("buttonBigButtonOne")).click();
    }


    protected void clickOnAddEws()
    {
        driver.findElement(By.id("buttonAddEarlyWarningScore")).click();
    }


    public void longTermMeasurementTimeoutSetup(SensorType sensor_type) throws IOException
    {
        helper.spoofCommandIntent_setLongTermMeasurementTimeout(sensor_type, 2);
    }


    public void switchOffThenOnThenUnlock()
    {
        KeyEvent event = new KeyEvent();

        event.withKey(AndroidKey.POWER);

        Log("pressing power button (to switch screen off)");
        driver.pressKey(event);

        sleep(5000);

        Log("pressing power button (to switch screen on)");
        driver.pressKey(event);

        sleep(2000);

        Log("swiping screen to unlock, expecting to show Gateway app afterwards (last running app)");
        swipeToUnlock();

        sleep(2000);
    }


    private void sleep(long sleepTimeInMillis)
    {
        try
        {
            Thread.sleep(sleepTimeInMillis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    // Quick upwards swipe to unlock screen after power button pressed (off) and again (on)
    private void swipeToUnlock()
    {
        TouchAction ts = new TouchAction(driver);

        ts.press(PointOption.point(1500,800)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(1500,200)).release().perform();
    }


    protected void checkRecyclingReminderPopup()
    {
        // Handle recycling reminder popup
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("imageRecyclingBox")), 60);
        driver.findElement(By.id("dismiss")).click();
    }


    protected void checkRemoveDeviceEwsPopup(boolean keep_for_ews)
    {
        // Handle EWS popup
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("layoutRemoveFromEws")), 60);

        if(keep_for_ews)
        {
            driver.findElement(By.id("buttonKeep")).click();
        }
        else
        {
            driver.findElement(By.id("buttonRemove")).click();
        }
    }


    private static void sendCommandToGateway_enableTestMode(Boolean enabled) throws IOException
    {
        Runtime.getRuntime().exec("adb shell am broadcast -a "
                + ACTION_ENTER_TEST_MODE
                + " --ez \"enabled\" " + enabled
        );
    }

    public boolean elementNotFound(By by)
    {
        List<WebElement> elements = driver.findElements(by);

        return elements.size() == 0;
    }
}
