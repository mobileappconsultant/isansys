package com.isansys.appiumtests.utils;

import com.isansys.pse_isansysportal.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.android.AndroidDriver;

public class endSessionIfRunning
{
    AndroidDriver driver;
    TestHelper helper;

    AppiumTestWatcher watcher = new AppiumTestWatcher();

    @Before
    public void setUp() throws MalformedURLException
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

        capabilities.setCapability("autoGrantPermissions", true);

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        //RemoteWebDriver remotedriver = (RemoteWebDriver) driver;
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        helper = new TestHelper(driver);

        watcher.initialise(helper);
    }

    /**
     * Utility test - ends a session if one is running.
     *
     * Useful if a remote test device is running a session when it shouldn't be.
     *
     * @throws IOException - if the qr code spoof fails for some reason
     */
    @Test
    public void endSessionIfRunning() throws IOException
    {
        helper.spoofQrCode_userUnlock();

        // Click on the Stop Monitoring Current Patient
        driver.findElement(By.id("buttonBigButtonThree")).click();
        // Click on End session
        driver.findElement(By.id("buttonEndSession")).click();
        //Click on Enter
        driver.findElement(By.id("buttonEndSessionBigButtonTop")).click();
        //Click on Confirm button
        driver.findElement(By.id("buttonEndSessionBigButtonBottom")).click();
    }
}
