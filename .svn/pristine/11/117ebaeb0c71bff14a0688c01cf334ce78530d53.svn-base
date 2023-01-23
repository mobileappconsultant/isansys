package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static junit.framework.TestCase.assertTrue;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.time.Duration;

import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class PoincarePopup extends AppiumTest
{
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


    private void setupSessionAndShowPoincarePopup() throws IOException
    {
        setUpSessionWithAdultAgeRange();

        helper.spoofQrCode_scanDummyDataLifetouch();

        clickOnConnect();

        clickOnStartMonitoring();

        WebElement OptionCheckbox= driver.findElement(By.id("checkBoxPatientVitalsDisplayLifetouchOptions"));
        assertTrue(OptionCheckbox.isDisplayed());

        OptionCheckbox.click();

        WebElement poincare_button = driver.findElement(By.id("checkBoxLifetouchOptionsHeartRatePoincare"));
        assertTrue(poincare_button.isDisplayed());

        poincare_button.click();
    }


    // Quick upwards swipe to unlock screen after power button pressed (off) and again (on)
    private void swipeToUnlock()
    {
        TouchAction ts = new TouchAction(driver);

        ts.press(PointOption.point(1500,800)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(1500,200)).release().perform();
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


    @Test
    public void testPoincareZoomOut() throws IOException
    {
        String case_id = "22556";
        helper.printTestStart("testPoincareZoomOut",case_id);

        checkAdminSettingsCorrect(false, false, false);

        setupSessionAndShowPoincarePopup();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("rangeView")), 120);

        TouchAction holdDown = new TouchAction(driver);  // similar to holding down a section of the Poincare graph
        TouchAction rightSwipe = new TouchAction(driver); // slow swipe to the right

        int numberOfTouches = 5;   // Zooming Poincare seems to work best using repeated, progressive swipes

        holdDown.press(PointOption.point( 580,93)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(numberOfTouches * 1000))).release();

        // Multitouch actions do not seem to work at all if both touches commence at the same time, so ensure a wait before any press/moveTo in one of the touches
        for (int i=0; i<numberOfTouches; i++)
        {
            rightSwipe.press(PointOption.point( 600,800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(100)))
                    .moveTo(PointOption.point(800, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(900, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(1000, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(1100, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(1200, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(1300, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(1400, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(1500, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(250)))
                    .release().waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)));
        }

        MultiTouchAction zoom = new MultiTouchAction(driver);
        zoom.add(holdDown).add(rightSwipe);
        zoom.perform();

        helper.captureScreenShot("testPoincareZoomOut");

        switchOffThenOnThenUnlock();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("qr_bar_code")), 120);
        helper.updateTestRail(PASSED);

    }


    @Test
    public void testPoincareZoomIn() throws IOException
    {
        String case_id = "22557";
        helper.printTestStart("testPoincareZoomIn",case_id);

        checkAdminSettingsCorrect(false, false, false);

        setupSessionAndShowPoincarePopup();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("rangeView")), 120);

        TouchAction holdDown = new TouchAction(driver);  // similar to holding down a section of the Poincare graph
        TouchAction rightSwipe = new TouchAction(driver); // slow swipe to the right

        int numberOfTouches = 10;   // Zooming Poincare seems to work best using repeated, progressive swipes

        holdDown.press(PointOption.point( 880,93)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(numberOfTouches * 1000))).release();

        for (int i=0; i<numberOfTouches; i++)
        {
            rightSwipe.press(PointOption.point( 900,800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(100)))
                    .moveTo(PointOption.point(902, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(904, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(906, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(908, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(910, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(912, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(914, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)))
                    .moveTo(PointOption.point(916, 800 )).waitAction(WaitOptions.waitOptions(Duration.ofMillis(250)))
                    .release().waitAction(WaitOptions.waitOptions(Duration.ofMillis(50)));
        }

        MultiTouchAction zoom = new MultiTouchAction(driver);
        zoom.add(holdDown).add(rightSwipe);
        zoom.perform();

        helper.captureScreenShot("testPoincareZoomIn");

        switchOffThenOnThenUnlock();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("qr_bar_code")), 120);
        helper.updateTestRail(PASSED);

    }
}
