package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.appiumtests.utils.TestHelper;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertTrue;

/**
 * Checks the appearance and contents of {@link com.isansys.pse_isansysportal.FragmentHeader}
 * and {@link com.isansys.pse_isansysportal.FragmentFooter}
 *
 * Some interactions with other features (e.g. Wifi and Server Syncing popups, Age Range and
 * Patient ID) are checked elsewhere
 */

// ToDo: check image matches for age range, logo, etc?
// Not sure it's easily possible without copying the image or finding its location...
// ToDo: test the night mode toggle button better - can we find an attribute that tells us if it's enabled somehow?
// ToDo: Find a way to avoid screensaver or disable screen saver.
public class HeaderAndFooter extends AppiumTest {
    /**
     * Check that all the contents of the header are present, except the age range image which
     * should be absent.
     * <p/>
     * The setting of values for the Bed and Ward tested as part of the
     * {@link AdminPage} and {@link Installation} tests.
     * <p/>
     * Checking the appearance of the age range logo is in the {@link AgeSelectionPage} tests
     * <p/>
     * The header patient ID value is checked by the {@link TestHelper}
     * class every time we set a patient ID.
     */
    @Test
    public void checkHeader() {
        String case_id = "23194";
        helper.printTestStart("checkHeader", case_id);

        // Check fragment present
        Log("Checking Header is present");
        Assert.assertTrue(driver.findElement(By.id("linearLayoutHeader")).isDisplayed());

        // Check contents exist
        Log("Checking Hospital Patient ID is NOT displayed as no session is running");
        Assert.assertTrue(elementNotFound(By.id("textHeaderPatientId")));

        Log("Checking Ward Name display present");
        Assert.assertTrue(driver.findElement(By.id("textHeaderWardName")).isDisplayed());

        Log("Checking Bed Name display present");
        Assert.assertTrue(driver.findElement(By.id("textHeaderBedName")).isDisplayed());

        Log("Checking Age Range display NOT present as no age range set");
        Assert.assertEquals(0, driver.findElements(By.id("imageHeaderAgeRange")).size());

        Log("Checking Isansys Logo present");
        Assert.assertTrue(driver.findElement(By.id("imageIsansysLogo")).isDisplayed());
        helper.updateTestRail(PASSED);
    }

    /**
     * Check all the elements of the footer are displayed correctly.
     * <p/>
     * Existence of signal strength image checked in {@link WifiPopup} tests.
     * <p/>
     * Server icon functionality checked in {@link ServerSyncingPopup}
     */
    @Test
    public void checkFooter() throws IOException {
        String case_id = "23195";
        helper.printTestStart("checkFooter", case_id);

        // Empty the database, as we're assuming later in the test that nothing is syncing...
        helper.spoofCommandIntent_emptyDatabase();

        // Check fragment present
        Log("Checking Footer is present");
        Assert.assertTrue(driver.findElement(By.id("relativeLayoutFooter")).isDisplayed());

        // Check the disk space
        Log("Checking Disk Space indicator is present");
        Assert.assertTrue(driver.findElement(By.id("imageFreeDiskSpacePercentage")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterFreeDiskSpacePercentage")).isDisplayed());

        // check battery image and text
        Log("Checking Battery Display is present");
        Assert.assertTrue(driver.findElement(By.id("imageFooterAndroidBattery")).isDisplayed());
        ToClickOnBatteryImage();
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryPercentage")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryCurrent")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryVoltage")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryTemperature")).isDisplayed());


        // Server icon, WAMP icon, green dot.
        Log("Checking Server Connection display is present");
        Assert.assertTrue(driver.findElement(By.id("imageServer")).isDisplayed());
        Assert.assertEquals(0, driver.findElements(By.id("viewFooterServerStatus")).size());  // should NOT be shown as no data being synced;
        Assert.assertTrue(driver.findElement(By.id("imageWamp")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("viewFooterWampStatus")).isDisplayed());

        // clock
        Log("Checking Large UI clock is present");
        Assert.assertTrue(driver.findElement(By.id("textHeaderGuiTime")).isDisplayed());

        // lock button - won't be visible as we start on lock screen
        Log("Checking Lock Button is NOT present");
        Assert.assertEquals(0, driver.findElements(By.id("buttonLock")).size());

        // Night mode image
        Log("Checking night mode toggle button is present");
        Assert.assertTrue(driver.findElement(By.id("imageDayOrNight")).isDisplayed());

        // Check the brightness image with brightness bar and increase and decrease button
        Log("Checking brightness image,slider and increase and decrease button is present");
        //Assert.assertTrue(driver.findElement(By.id("textViewSetScreenBrightness")).isDisplayed());
        //Brightness image
        Assert.assertTrue(driver.findElement(By.id("imageBrightness")).isDisplayed());
        //Brightness bar
        Assert.assertTrue(driver.findElement(By.id("seekBarScreenBrightness")).isDisplayed());
        //Increase button
        Assert.assertTrue(driver.findElement(By.id("buttonIncreaseScreenBrightness")).isDisplayed());
        //decrease button
        Assert.assertTrue(driver.findElement(By.id("buttonDecreaseScreenBrightness")).isDisplayed());

        // small clock
        Log("Checking Large UI clock is present");
        Assert.assertTrue(driver.findElement(By.id("textHeaderGuiTimeSmall")).isDisplayed());

        // Gateway ping indicator
        Log("Checking Gateway Ping indicator is present");
        Assert.assertTrue(driver.findElement(By.id("viewFooterSystemStatus")).isDisplayed());
        helper.updateTestRail(PASSED);
    }

    // Hide the "detailed" Gateway battery info in the Footer
    @Test
    public void HideBatteryDetails() {
        String case_id = "";
        helper.printTestStart("HideBatteryDetails", case_id);
        WebElement BatteryImage = driver.findElement(By.id("imageFooterAndroidBattery"));
        //Check the detail of the battery is hidden by default
        // Make random click on the screen to avoid the screensaver
        WebElement FooterFragment = driver.findElement(By.id("relativeLayoutFooter"));
        FooterFragment.click();
        Assert.assertTrue(BatteryImage.isDisplayed());
        Assert.assertEquals(0, driver.findElements(By.id("textViewFooterBatteryPercentage")).size());
        Assert.assertEquals(0, driver.findElements(By.id("textViewFooterBatteryCurrent")).size());
        Assert.assertEquals(0, driver.findElements(By.id("textViewFooterBatteryVoltage")).size());
        Assert.assertEquals(0, driver.findElements(By.id("textViewFooterBatteryTemperature")).size());
        Log("Battery details are hidden by default");
        // Make random click on the screen to avoid the screensaver
        FooterFragment.click();
        Log("Click on the battery image from the footer");
        BatteryImage.click();
        Log("Check the battery details");
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryPercentage")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryCurrent")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryVoltage")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("textViewFooterBatteryTemperature")).isDisplayed());
        Log("All the Battery details is visible now");

    }

    /**
     * Check the battery level, current etc are shown and are within sensible values
     */
    @Test
    public void checkBatteryLevel() {
        String case_id = "23196";
        helper.printTestStart("checkBatteryLevel", case_id);
        ToClickOnBatteryImage();
        // Wait for value to load
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewFooterBatteryPercentage"), helper.getAppString("filler_text_one"))), 60);

        String battery_percentage = driver.findElement(By.id("textViewFooterBatteryPercentage")).getText();

        Log("Tablet battery percentage = " + battery_percentage);

        int battery_level = Integer.parseInt(battery_percentage.split("%")[0]);

        // Tablet is plugged in, so should have ~100% battery. To allow for some fluctuation,
        // test for > 90% here.
        Assert.assertTrue(battery_level > 90);

        String charge_current = driver.findElement(By.id("textViewFooterBatteryCurrent")).getText();

        Log("Tablet battery charging current = " + charge_current);

        // Can't guarantee current +ve, as if battery is at 100% it will cut in and out.
//        int charge_level = Integer.parseInt(charge_current);
//        Assert.assertTrue(charge_level > 0);

        String battery_voltage = driver.findElement(By.id("textViewFooterBatteryVoltage")).getText();

        Log("Tablet battery voltage = " + battery_voltage);

        int voltage_level = Integer.parseInt(battery_voltage.split("mV")[0]);
        Assert.assertTrue(voltage_level > 4000);

        String temperature = driver.findElement(By.id("textViewFooterBatteryTemperature")).getText();

        Log("Tablet battery temperature = " + temperature);

        double temperature_level = Double.parseDouble(temperature.split("°")[0]);
        Assert.assertTrue(temperature_level > 10);
        Assert.assertTrue(temperature_level < 35);
        helper.updateTestRail(PASSED);
    }

    /**
     * Check the free space indicator is within sensible values
     */
    @Test
    public void checkFreeSpace() {
        String case_id = "23197";
        helper.printTestStart("checkFreeSpace", case_id);

        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textViewFooterFreeDiskSpacePercentage"), helper.getAppString("fake_free_disk_space"))), 60);

        String free_space_percentage = driver.findElement(By.id("textViewFooterFreeDiskSpacePercentage")).getText();
        Log("Free disk space is " + free_space_percentage);

        int free_space_value = Integer.parseInt(free_space_percentage.split("%")[0]);
        Assert.assertTrue(free_space_value > 10);
        helper.updateTestRail(PASSED);
    }

    /**
     * Check the small and large footer clocks show the same time and update together
     */
    @Test
    public void checkClocksMatch() {
        String case_id = "23198";
        helper.printTestStart("checkClocksMatch", case_id);

        String main_clock_value = driver.findElement(By.id("textHeaderGuiTime")).getText();
        Log("Footer clock time is " + main_clock_value);

        // wait for clock to update...
        waitUntil(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.id("textHeaderGuiTime"), main_clock_value)), 90);

        // get main clock value
        main_clock_value = driver.findElement(By.id("textHeaderGuiTime")).getText();
        Log("Footer clock updated time is " + main_clock_value);

        // get small clock value
        String small_clock_value = driver.findElement(By.id("textHeaderGuiTimeSmall")).getText();
        Log("Footer small clock time is " + small_clock_value);

        small_clock_value = small_clock_value.split(" ")[2];
        String small_clock_hours_minutes = small_clock_value.split(":")[0] + ":" + small_clock_value.split(":")[1];

        // check they match (without the seconds)
        Assert.assertEquals(main_clock_value, small_clock_hours_minutes);
        helper.updateTestRail(PASSED);
    }

    /**
     * Check the night mode element toggles when clicked
     */
    @Test
    public void checkNightModeClickable() {
        String case_id = "23199";
        helper.printTestStart("checkNightModeClickable", case_id);

        WebElement night_mode_toggle = driver.findElement(By.id("imageDayOrNight"));

        // "checked" always seems to return false, so we currently have no way of telling if the toggle button is checked or not.
        //   Log("Checking night mode toggle button is not checked");
        //Assert.assertEquals("false", night_mode_toggle.getAttribute("checked"));

        Log("Toggle the element");
        night_mode_toggle.click();

        //  Log("Checking night mode toggle button is now checked");
        //  Assert.assertEquals("true", night_mode_toggle.getAttribute("checked"));

        Log("Toggle the element again");
        night_mode_toggle.click();
        helper.updateTestRail(PASSED);
    }

    private void ToClickOnBatteryImage()
    {
        WebElement BatteryImage = driver.findElement(By.id("imageFooterAndroidBattery"));
        BatteryImage.click();

    }
}