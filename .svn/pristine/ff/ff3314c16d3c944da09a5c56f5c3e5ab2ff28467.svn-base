package com.isansys.appiumtests.permissions;

import com.isansys.appiumtests.utils.AppiumTestWatcher;
import com.isansys.appiumtests.utils.TestHelper;
import com.isansys.pse_isansysportal.BuildConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.android.AndroidDriver;

public class GatewayPermissions
{
    protected AndroidDriver driver;
    protected TestHelper helper;

    @Rule
    public AppiumTestWatcher watcher = new AppiumTestWatcher();

    @Before
    public void setUp() throws MalformedURLException
    {
        // Set up the UI apk location to access the contents of strings.xml
 //       File app = new File(BuildConfig.apk_path);

        // Created object of DesiredCapabilities class.
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Set Android deviceName desired capability. This is ignored for Android devices, but still required by the driver, so we have to set it.
        capabilities.setCapability("deviceName", "ignored");

        // Set Android platformName desired capability. It's Android in our case here.
        capabilities.setCapability("platformName", "Android");

        // In order to get the app's strings.xml resource, need to specify the APK we're using.
  //      capabilities.setCapability("app", app.getAbsolutePath());

        // Set android appPackage desired capability. It is
        // com.android.calculator2 for calculator application.
        // Set your application's appPackage if you are using any other app.
        capabilities.setCapability("appPackage", "com.isansys.patientgateway");

        // Set android appActivity desired capability. It is
        // com.android.calculator2.Calculator for calculator application.
        // Set your application's appPackage if you are using any other app.
        capabilities.setCapability("appActivity", "com.isansys.patientgateway.MainActivity");
        //capabilities.setCapability("unicodeKeyboard", true);

        capabilities.setCapability("automationName", "UiAutomator2");

        if(BuildConfig.tabletSerialNumber != null)
        {
            capabilities.setCapability("udid", BuildConfig.tabletSerialNumber);
        }

        capabilities.setCapability("autoGrantPermissions", true);

        // Set Appium server address and port number in URL string.
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        helper = new TestHelper(driver);

        //System.out.println("Test helper initialised");

        watcher.initialise(helper);
    }

    @Test
    public void allowGatewayPermissions()
    {
        helper.printTestStart("allowGatewayPermissions");

        // No test content required

        helper.printTestEnd();
    }
}
