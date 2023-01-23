package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.pse_isansysportal.BuildConfig;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.List;

public class WifiPopup extends AppiumTest
{
    @Test
    public void showWifiPopup()
    {
        String case_id = "22614";
        helper.printTestStart("showWifiPopup",case_id);

        driver.findElement(By.id("imageSignalStrength")).click();

        String expected;
        String actual;

        // Check the popup row names...
        expected = helper.getAppString("wifi_enabled_status");
        actual = driver.findElement(By.id("textView_wifi_enabled_title")).getText();
        assertEquals(expected, actual);

        expected = helper.getAppString("wifi_connected_to_ssid");
        actual = driver.findElement(By.id("textView_wifi_connected_title")).getText();
        assertEquals(expected, actual);

        expected = helper.getAppString("wifi_SSID");
        actual = driver.findElement(By.id("textView_SSID_title")).getText();
        assertEquals(expected, actual);

        expected = helper.getAppString("wifi_signal_level");
        actual = driver.findElement(By.id("textView_signal_level_title")).getText();
        assertEquals(expected, actual);

        expected = helper.getAppString("wifi_connection_status");
        actual = driver.findElement(By.id("textView_connection_status_title")).getText();
        assertEquals(expected, actual);

        expected = helper.getAppString("wifi_BSSID");
        actual = driver.findElement(By.id("textView_BSSID_title")).getText();
        assertEquals(expected, actual);

        expected = helper.getAppString("wifi_failure_reason");
        actual = driver.findElement(By.id("textView_connection_problem_title")).getText();
        assertEquals(expected, actual);

        // Dismiss it
        driver.findElement(By.id("button_wifi_popup_dismiss")).click();

        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("textView_wifi_enabled_title")), 120);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void checkWifiConnected()
    {
        String case_id = "22615";
        helper.printTestStart("checkWifiConnected",case_id);

        driver.findElement(By.id("imageSignalStrength")).click();

        String expected;
        String actual;

        // Check the popup is shown...
        expected = helper.getAppString("wifi_enabled_status");
        actual = driver.findElement(By.id("textView_wifi_enabled_title")).getText();
        assertEquals(expected, actual);

        // ...wifi is enabled...
        expected = "true";
        actual = driver.findElement(By.id("textView_wifi_enabled_status")).getText();
        assertEquals(expected, actual);

        // ... and connected.
        expected = "CONNECTED";
        actual = driver.findElement(By.id("textView_wifi_connection_status")).getText();
        assertEquals(expected, actual);

        // Dismiss it
        driver.findElement(By.id("button_wifi_popup_dismiss")).click();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void checkWifiAutoDismiss()
    {
        String case_id = "22616";
        helper.printTestStart("checkWifiAutoDismiss",case_id);

        driver.findElement(By.id("imageSignalStrength")).click();

        String expected;
        String actual;

        // Check the popup is shown...
        expected = helper.getAppString("wifi_enabled_status");
        actual = driver.findElement(By.id("textView_wifi_enabled_title")).getText();
        assertEquals(expected, actual);

        // wait for the popup to be dismissed automatically
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("textView_wifi_enabled_title")), 120);
        helper.updateTestRail(PASSED);

    }



    @Test
    public void checkWifiAutoReconnect() throws IOException {
        String case_id = "22617";
        helper.printTestStart("checkWifiAutoReconnect",case_id);

        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        //Find the OS version from admin page
        driver.findElement(By.id("labelAndroidVersion")).isDisplayed();
        WebElement AndroidV= driver.findElement(By.id("textAndroidVersion"));
        String OS_Version=AndroidV.getText();
        Log(OS_Version);
        // trim the version number to two character (helpful  if version has subversion)
        String firstTwoCharacters = OS_Version.substring(0, 2);
        //Log(firstTwoCharacters);
        //Ignore the decimal part if the version has 8.0.1 for example
        Float Version =Float.parseFloat(firstTwoCharacters);
        int MatchVersion = Version.intValue();
        System.out.println("OS Version:"+MatchVersion + " to check ");

        //Lock the screen from admin page

        driver.findElement(By.id("buttonLock")).click();
        /* Android version is defined in gradle.properties and is used to specify different behaviour
         * for different tests - it is NOT automatically picked up from the tablet under test!
         * To specify anything other than the default (8) add a line to the bottom of gradle.properties
         * with androidVersion=11 or similar. Note capitalisation and no spaces.
         */
        if(MatchVersion == 8)
        {
            System.out.println("Test expects android 8, so running reconnection test");

            driver.findElement(By.id("imageSignalStrength")).click();

            String expected;
            String actual;

            // Check the popup is shown...
            expected = helper.getAppString("wifi_enabled_status");
            actual = driver.findElement(By.id("textView_wifi_enabled_title")).getText();
            assertEquals(expected, actual);

            // ... and connected.
            expected = "CONNECTED";
            actual = driver.findElement(By.id("textView_wifi_connection_status")).getText();
            assertEquals(expected, actual);

            // now click reconnect
            driver.findElement(By.id("button_wifi_reconnect")).click();

            // check wifi has disconnected
            waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textView_wifi_connection_status"), "DISCONNECTED"), 120);

            // and wait for it to reconnect.
            waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textView_wifi_connection_status"), "CONNECTED"), 120);

            // Finally dismiss the popup
            driver.findElement(By.id("button_wifi_popup_dismiss")).click();
        }
        else
        {
            System.out.println("Test expects android 11, so checking reconnect not shown");

            driver.findElement(By.id("imageSignalStrength")).click();

            String expected;
            String actual;

            // Check the popup is shown...
            expected = helper.getAppString("wifi_enabled_status");
            actual = driver.findElement(By.id("textView_wifi_enabled_title")).getText();
            assertEquals(expected, actual);

            List<WebElement> reconnect_button = driver.findElements(By.id("button_wifi_reconnect"));

            assertEquals(0, reconnect_button.size());
        }

        helper.updateTestRail(PASSED);
    }
}
