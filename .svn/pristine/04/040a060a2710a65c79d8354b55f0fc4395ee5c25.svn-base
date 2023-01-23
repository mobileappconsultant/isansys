package com.isansys.appiumtests.permissions;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.appiumtests.utils.TestHelper;
import com.isansys.pse_isansysportal.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.Setting;
import io.appium.java_client.android.AndroidDriver;

public class UserInterfacePermissions extends AppiumTest
{
    @Before
    @Override
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

        capabilities.setCapability("autoGrantPermissions", true);

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

    @Test
    public void allowWriteSettings()
    {
        helper.printTestStart("allowWriteSettings");

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("tableRowWriteSettingPermissions")), 30);

        driver.findElement(By.id("buttonEnableWriteSettingsPermissions")).click();

        // assuming there's only one Switch widget on screen...
        driver.findElement(By.className("android.widget.Switch")).click();
    }

    @Test
    public void allowAccessNotificationPolicy()
    {
        helper.printTestStart("allowAccessNotificationPolicy");

        try
        {
            driver.findElement(By.id("textViewWelcomeText"));

            System.out.println("On Welcome Page - permissions already granted");

            return;
        }
        catch(Exception e)
        {
            System.out.println("Permission pending, wait for permission screen to load");
        }

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("tableAccessNotificationPolicyPermission")), 30);

        try
        {
            driver.findElement(By.id("buttonEnableAccessNotificationPolicyPermissions")).click();

            try
            {
                String xpath_query = "//android.widget.TextView[@text='PSE Isansys Portal']";

                driver.findElement(By.xpath(xpath_query)).click();

                // assuming there's only one Switch widget on screen...
                driver.findElement(By.className("android.widget.Switch")).click();

                xpath_query = "//android.widget.Button[@text='Allow']";

                driver.findElement(By.xpath(xpath_query)).click();
            } catch (Exception e)
            {
                System.out.println("Could not click portal line or widget - dialog may already be present");

                // Hope for the best, and try to accept the dialog popup...
                driver.switchTo().alert().accept();
            }
        }
        catch(Exception e)
        {
            System.out.println("Could not click allow button - assume permission already granted");
        }
    }

    @Test
    public void allowInstallUnknownSources()
    {
        helper.printTestStart("allowInstallUnknownSources");

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("tableInstallPackagesPermission")), 30);

        driver.findElement(By.id("buttonEnableInstallPackagesPermissions")).click();

        // assuming there's only one Switch widget on screen...
        driver.findElement(By.className("android.widget.Switch")).click();
    }
}
