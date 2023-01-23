package com.isansys.appiumtests.tablet_only;

import com.isansys.appiumtests.AppiumTest;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.PASSED;


/**
 * Created by Soumya on 09/10/2017.
 */

public class Installation  extends AppiumTest
{
    /** Test installationProcessPage - Goes through the installation process to verify normal
     * operation of installation mode
     *
     * Corresponds to test case C22618 in TestRail
     * and Patient Gateway - Software Specification section 7.2.5 (a) Installation Mode
     */
    @Test
    public void installationProcessPage() throws IOException
    {
        String case_id = "22618";

        helper.printTestStart("installationProcessPage", case_id);

        // Send a restart_installation_wizard command in case installation is completed.
        helper.spoofCommandIntent_restartInstallationWizard();

        installationProcessFull();

        helper.updateTestRail(PASSED);
    }


    /** Test restartInstallationWizard - Goes through the installation process to verify the Restart
     * Installation Wizard button works at multiple points in the installation process.
     *
     * Corresponds to test case C22619 in TestRail
     * and Patient Gateway - Software Specification section 7.2.5 (a) (i) Restart Installation Wizard
     */
    @Test
    public void restartInstallationWizard() throws IOException
    {
        String case_id = "22619";

        helper.printTestStart("restartInstallationWizard", case_id);

        Log("Case1: Click on Reinstallation wizard button from the Scan Installation QR code page");

        // Send a restart_installation_wizard command in case installation is completed.
        helper.spoofCommandIntent_restartInstallationWizard();

        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Click on Reinstallationwizard button from the scan QR code page
        clickReinstallationWizardButton();
        Log("Back to Start installation wizard page");
        Log("Case2: Click on Reinstallation wizard button from patient gateway setup wizard page");
        driver.findElement(By.id("buttonBigButtonTwo")).click();
        //Scan the Installation QR Code - spoof by sending an intent to the UI
        helper.spoofQrCode_installation();

        //Check the server address
        WebElement lifeguardServerAddress = driver.findElement(By.id("textViewServerAddress"));
        String lSAddress = lifeguardServerAddress.getText();
        Log(lSAddress);
        clickReinstallationWizardButton();
        Log("Back to Start installation wizard page");

        Log("Case3: Click on Reinstallation wizard button from Set up complete page");
        installationProcessWithoutCompleting();

        clickReinstallationWizardButton();
        Log("Back to Start installation wizard page");

        installationProcessFull();

        helper.updateTestRail(PASSED);
    }

    /** Test installationModeReSyncThresholds - Checks that if the gateway loses EWS thresholds
     * unexpectedly it will re-sync them from the server.
     *
     * Loss of thresholds is simulated by a command intent.
     *
     * Corresponds to test case C22620 in TestRail
     * and Patient Gateway - Software Specification section 7.1.12 Installation Mode State Machine
     */
    @Test
    public void installationModeReSyncThresholds() throws IOException
    {
        String case_id = "22620";

        helper.printTestStart("installationModeReSyncThresholds", case_id);
        // First do normal installation
        helper.spoofCommandIntent_restartInstallationWizard();

        installationProcessFull();

        Log("Starting re-sync");

        // then send intent to trigger thresholds to be deleted for test purposes
        helper.spoofCommandIntent_deleteEwsThresholdsTestOnly();


        // check re-sync is in progress
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textViewGetGatewayConfig")), 30);

        Log("Waiting for re-sync to finish");

        // wait for resync to complete and installation fragment to be dismissed
        waitUntil(ExpectedConditions.invisibilityOf(driver.findElement(By.id("textViewGetGatewayConfig"))), 180);

        checkAdminPageForFinishedInstallation();

        helper.updateTestRail(PASSED);
    }


    /** Test installationModeReSyncAndLockScreen - Checks that if the gateway loses EWS thresholds
     * unexpectedly it will re-sync them from the server, and continue to re-sync even if the
     * UI screen is then locked.
     *
     * Loss of thresholds is simulated by a command intent.
     *
     * Corresponds to test case C22621 in TestRail
     * and Patient Gateway - Software Specification section 7.1.12 Installation Mode State Machine
     */
    @Test
    public void installationModeReSyncAndLockScreen() throws IOException
    {
        String case_id = "22621";

        helper.printTestStart("installationModeReSyncAndLockScreen", case_id);

        // First do normal installation
        helper.spoofCommandIntent_restartInstallationWizard();

        installationProcessFull();

        Log("Starting re-sync");

        // then send intent to trigger thresholds to be deleted for test purposes
        helper.spoofCommandIntent_deleteEwsThresholdsTestOnly();

        // check re-sync is in progress
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textViewGetGatewayConfig")), 30);

        clickOnLock();

        Log("Waiting for re-sync to finish");

        // wait for resync to complete and installation fragment to be dismissed
        waitUntil(ExpectedConditions.and(ExpectedConditions.visibilityOf(driver.findElement(By.id("imageServer"))), ExpectedConditions.visibilityOf(driver.findElement(By.id("imageWamp")))), 180);

        checkAdminPageForFinishedInstallation();

        helper.updateTestRail(PASSED);
    }


    /** Test installationModeReSyncThresholdsWithWifiReconnection - Checks that if the gateway
     * loses EWS thresholds unexpectedly it will re-sync them from the server, and will recover and
     * re-sync even if the wifi connection drops.
     *
     * Loss of thresholds is simulated by a command intent.
     *
     * Corresponds to test case C22622 in TestRail
     * and Patient Gateway - Software Specification section 7.1.12 Installation Mode State Machine
     */
    @Test
    public void installationModeReSyncThresholdsWithWifiReconnection() throws IOException
    {
        String case_id = "22622";

        helper.printTestStart("installationModeReSyncThresholdsWithWifiReconnection", case_id);

        // First do normal installation
        helper.spoofCommandIntent_restartInstallationWizard();

        installationProcessFull();

        Log("Starting re-sync");

        // start the wifi reconnect running, so that resyncing is likely to fail at some point in the process.
        helper.spoofCommandIntent_reconnectWifi();

        // then send intent to trigger thresholds to be deleted for test purposes
        helper.spoofCommandIntent_deleteEwsThresholdsTestOnly();

        // check re-sync is in progress
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textViewGetGatewayConfig")), 30);

        Log("Waiting for re-sync to finish");

        // wait for resync to complete and installation fragment to be dismissed
        waitUntil(ExpectedConditions.invisibilityOf(driver.findElement(By.id("textViewGetGatewayConfig"))), 180);

        checkAdminPageForFinishedInstallation();

        helper.updateTestRail(PASSED);
    }


    /** Test installationModeReSyncServerData - Checks that if the gateway
     * loses configuration information unexpectedly it will re-sync it from the server.
     *
     * Loss of configuration info is simulated by a command intent.
     *
     * Corresponds to test case C22623 in TestRail
     * and Patient Gateway - Software Specification section 7.1.12 Installation Mode State Machine
     */
    @Test
    public void installationModeReSyncServerData() throws IOException
    {
        String case_id = "22623";

        helper.printTestStart("installationModeReSyncServerData", case_id);

        // First do normal installation
        helper.spoofCommandIntent_restartInstallationWizard();

        installationProcessFull();

        Log("Starting forced sync");

        helper.spoofCommandIntent_forceResyncServerData();

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textViewGetGatewayConfig")), 30);

        Log("Sync running");

        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("buttonSetupComplete")), 60);

        Log("Sync Complete");

        driver.findElement(By.id("buttonSetupComplete")).click();

        // Check for QR scan page
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("textPleaseScanIdQrCode")), 60);

        helper.updateTestRail(PASSED);
    }


    public void clickReinstallationWizardButton() {
        WebElement RIWizardButton = driver.findElement(By.id("buttonBack"));
        String RIWB=  RIWizardButton.getText();
        //captureScreenShots();
        Log("Button name is back:  "+RIWB);
        RIWizardButton.click();
    }


    private void checkAdminPageForFinishedInstallation() throws IOException
    {
        // Scan the Admin QR code (spoof via an intent) and then click on the Server Connecting tab on the Admin page
        UnlockWithAdminQrCodeAndShowServerConnectionSettings();

        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.id("textReceivedDefaultEarlyWarningScoringTypes"), "EWS"), 60);

        String thresholds = driver.findElement(By.id("textReceivedDefaultEarlyWarningScoringTypes")).getAttribute("text");

        assert(thresholds.contains("PEWS") && thresholds.contains("NEWS"));
    }
}
