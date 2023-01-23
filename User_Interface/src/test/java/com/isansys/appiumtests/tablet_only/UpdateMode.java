package com.isansys.appiumtests.tablet_only;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.isansys.appiumtests.AppiumTest;
import com.isansys.common.enums.DeviceType;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

public class UpdateMode extends AppiumTest
{
    String spoof_pending_software_version = "999999";
    String spoof_done_software_version = "0";

    @Test
    /*
        Start update mode from the start session page, spoof installing the APKs by refreshing the screen, and press done on the update mode page.
     */
    public void enterAndExitUpdateMode() throws IOException
    {
        String case_id = "22605";
        helper.printTestStart("enterAndExitUpdateMode",case_id);

        // send update available intent
        helper.spoofCommandIntent_allSoftwareUpdatesAvailable(spoof_pending_software_version);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();

        driver.findElement(By.id("textUpdateMustBeInstalled"));

        enterUpdateMode();

        checkAppVersions(spoof_pending_software_version);

        // Assume all apps are at same version
        spoof_done_software_version = driver.findElement(By.id("textCurrentPatientGatewaySoftwareVersion")).getText();

        // Update available versions to simulate an install
        helper.spoofCommandIntent_allSoftwareUpdatesCompleted(spoof_done_software_version);

        switchOffThenOnThenUnlock();

        completeUpdateAndCheckStartMonitoring();
        helper.updateTestRail(PASSED);

    }


    @Test
    /*
        Check there's no way to start a session once update mode has been triggered.
     */
    public void cantStartPatientSessionWhenUpdatePending() throws IOException
    {
        String case_id = "22606";
        helper.printTestStart("cantStartPatientSessionWhenUpdatePending",case_id);

        // send update available intent
        helper.spoofCommandIntent_allSoftwareUpdatesAvailable(spoof_pending_software_version);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();

        // verify there's only one main button - the update mode one - plus the lock button in the footer.
        checkUpdateAvailablePage();


        clickOnLock();


        // Unlock a second time and check we're still on the
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();


        checkUpdateAvailablePage();



        switchOffThenOnThenUnlock();

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();


        checkUpdateAvailablePage();

        enterUpdateMode();

        // Assume all apps are at same version
        spoof_done_software_version = driver.findElement(By.id("textCurrentPatientGatewaySoftwareVersion")).getText();

        // Update available versions to 0 to simulate an install
        helper.spoofCommandIntent_allSoftwareUpdatesCompleted(spoof_done_software_version);

        switchOffThenOnThenUnlock();


        checkAppVersions(spoof_done_software_version);

        completeUpdateAndCheckStartMonitoring();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void cantExitUpdateModeWithoutInstalling() throws IOException
    {
        String case_id = "22607";
        helper.printTestStart("cantExitUpdateModeWithoutInstalling",case_id);

        // send update available intent
        helper.spoofCommandIntent_allSoftwareUpdatesAvailable(spoof_pending_software_version);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();

        enterUpdateMode();

        // Check footer is hidden
        List<WebElement> footer = driver.findElements(By.id("relativeLayoutFooter"));

        assertEquals(0, footer.size());

        // check re-loading the UI doesn't get past update mode
        switchOffThenOnThenUnlock();

        // check update not done still
        List<WebElement> updates_complete = driver.findElements(By.id("buttonUpdatesComplete"));

        assertEquals(0, updates_complete.size());


        // Assume all apps are at same version
        spoof_done_software_version = driver.findElement(By.id("textCurrentPatientGatewaySoftwareVersion")).getText();

        checkAppVersions(spoof_pending_software_version);


        // Update available versions to 0 to simulate an install
        helper.spoofCommandIntent_allSoftwareUpdatesCompleted(spoof_done_software_version);

        switchOffThenOnThenUnlock();

        // confirm we're on the correct page still
        driver.findElement(By.id("labelPatientGateway"));
        driver.findElement(By.id("labelUserInterface"));

        checkAppVersions(spoof_done_software_version);

        completeUpdateAndCheckStartMonitoring();
        helper.updateTestRail(PASSED);

    }


    @Test
    public void checkUpdatePageContents() throws IOException
    {
        String case_id = "22608";
        helper.printTestStart("checkUpdatePageContents",case_id);

        helper.beginScreenRecord("checkUpdatePageContents");

        // send update available intent
        helper.spoofCommandIntent_allSoftwareUpdatesAvailable(spoof_pending_software_version);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        driver.findElement(By.id("textSoftwareUpdatePending"));

        helper.spoofQrCode_userUnlock();

        checkUpdateAvailablePage();

        enterUpdateMode();

        // Check install buttons are clickable
        WebElement gateway_install = driver.findElement(By.id("buttonInstallGatewayApk"));

        String clickable = gateway_install.getAttribute("clickable");

        assertTrue(clickable.equals("true"));

        // Check other buttons not visible...




        // Assume all apps are at same version
        spoof_done_software_version = driver.findElement(By.id("textCurrentPatientGatewaySoftwareVersion")).getText();

        // spoof GW update
        helper.spoofCommandIntent_softwareUpdateCompleted(spoof_done_software_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);

        switchOffThenOnThenUnlock();


        WebElement ui_install = driver.findElement(By.id("buttonInstallUserInterfaceApk"));

        clickable = ui_install.getAttribute("clickable");

        assertTrue(clickable.equals("true"));




        // Spoof UI update
        helper.spoofCommandIntent_softwareUpdateCompleted(spoof_done_software_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);

        switchOffThenOnThenUnlock();


 //       checkAppVersions(spoof_done_software_version);

        completeUpdateAndCheckStartMonitoring();
        helper.updateTestRail(PASSED);

    }


    private void enterUpdateMode()
    {
        // Click on enter update mode
        driver.findElement(By.id("buttonEnterUpdateMode")).click();

        // confirm we're on the correct page
        driver.findElement(By.id("labelPatientGateway"));
        driver.findElement(By.id("labelUserInterface"));
    }


    private void checkAppVersions(String expected_pending_version)
    {
        String gateway_version = driver.findElement(By.id("textCurrentPatientGatewaySoftwareVersion")).getText();
        String ui_version = driver.findElement(By.id("textCurrentUserInterfaceSoftwareVersion")).getText();

        assertEquals(gateway_version, ui_version);

        String available_gateway_version = driver.findElement(By.id("textAvailablePatientGatewayVersion")).getText();
        String available_ui_version = driver.findElement(By.id("textAvailableUserInterfaceVersion")).getText();

        assertEquals(expected_pending_version, available_gateway_version);
        assertEquals(expected_pending_version, available_ui_version);
    }


    private void checkUpdateAvailablePage()
    {
        // Wait for up to 20 minutes(!) - yes really. Won't normally take this long, except if the log file zip tasks are running.
        // This is to ensure that in those rare cases we wait for the EWS thresholds to sync properly.
        waitUntil(elementClickablePropertyIsTrue(By.id("buttonEnterUpdateMode")), 60); //1200);

        String button_one_text = driver.findElement(By.id("buttonEnterUpdateMode")).getText();

        assertEquals(button_one_text, helper.getAppString("enter_update_mode"));

        WebElement main_fragment = driver.findElement(By.id("fragment_main"));

        List<WebElement> buttons = main_fragment.findElements(By.className("android.widget.Button"));

        assertEquals(1, buttons.size());
    }


    private void completeUpdateAndCheckStartMonitoring() throws IOException
    {
        WebElement updates_complete = driver.findElement(By.id("buttonUpdatesComplete"));

        String clickable = updates_complete.getAttribute("clickable");

        assertTrue(clickable.equals("true"));

        updates_complete.click();

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        helper.spoofQrCode_userUnlock();

        // Check we're back on the normal start monitoring page
        waitUntil(elementClickablePropertyIsTrue(By.id("buttonBigButtonOne")), 60); //1200);

        String button_one_text = driver.findElement(By.id("buttonBigButtonOne")).getText();

        assertEquals(button_one_text, helper.getAppString("textStartMonitoringAPatient"));
    }
}
