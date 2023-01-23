package com.isansys.appiumtests.utils;

import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;
import com.isansys.common.enums.BarcodeDeviceType;
import com.isansys.common.enums.Commands;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.BuildConfig;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * Useful resources for the appium tests - QR code unlocks, screenshot locations etc.
 */

public class TestHelper
{
    final String screenshot_path = "Appium/screenshot/";

    private final Map<String, String> m_string_map;
    private final AndroidDriver m_driver;
    private WebDriver m_web_driver = null;


    private final String muffin_address = "192.168.1.195";
    private final String muffin_external_address = "185.55.62.6";
    private final String muffin_port = "88";
    private final String muffin_wamp_port = "9008";
    private final String muffin_mqtt_port = "8883";

    private final String cookie_address = "185.55.62.6";
    private final String cookie_port = "84";
    private final String cookie_wamp_port = "9004";

    private final String quiche_address = "192.168.1.127";
    private final String quiche_port = "86";
    private final String quiche_wamp_port = "9006";

    private final String external_test_server_address = "185.55.62.6";
    private final String external_test_server_port = "81";
    private final String external_test_server_wamp_port = "9002";

    private final String win2019_address = "win2019.isansys.com";
    private final String win2019_port = "80";
    private final String win2019_real_time_port = "8883";

    private final String isansys_testrail_instance = "https://isansys.testrail.io";

    private final APIClient testrail_client;

    private final String adb_string;

    private final String string_broadcast_command_to_user_interface = "shell am broadcast -a com.isansys.patientgateway.commands_to_user_interface";
    private final String string_broadcast_command_to_patient_gateway = "shell am broadcast -a com.isansys.patientgateway.commands_to_patient_gateway";

    // QR code strings to spoof installation mode
    private final String muffin_internal_wamp_qr_code = "\"o\\\\+!k4]7IY\\(e57L!\\`aN^\"";
    private final String muffin_external_wamp_qr_code = "\"nF\\\\#\\'7+b\\(s12_?m+L\\$\\>%\"";

    private final String muffin_internal_mqtt_qr_code = "SW\\(.R5\\>QJI,G9_h\\$K2r\\$";
    private final String muffin_external_mqtt_qr_code = "\\<E18nPK!h\\\\\\\"%H\\'\\>AFY\\'le";

    public TestHelper(AndroidDriver driver)
    {
        m_driver = driver;
        m_string_map = m_driver.getAppStringMap();

        testrail_client = new APIClient(isansys_testrail_instance);

        testrail_client.setUser("rory.morrison@isansys.com");
        testrail_client.setPassword("M/jUcPGsb.arG9Bf94nQ-lu/wL0zlw7z2OjnxnP4t"); // API key

        if(BuildConfig.tabletSerialNumber != null)
        {
            adb_string = "adb -s " + BuildConfig.tabletSerialNumber + " ";
        }
        else
        {
            adb_string = "adb ";
        }
    }


    public String getAppString(String key)
    {
        return m_string_map.get(key).replaceAll("\\\\n", "\n"); // regex replace is so that we get the "\n" as a single character - due to the way getAppStringMap parses the file it was coming out as two separate characters.
    }


    private void broadcastQrCode(BarcodeDeviceType barcodeType) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_user_interface
                + " --ei \"command\" " + Commands.CMD_REPORT_QR_CODE_DETAILS.ordinal()
                + " --ei \"barcode_type\" " + barcodeType.ordinal()
                + " --ez \"qr_code_validity\" true"
                + " --el \"human_readable_product_id\" 1"   // if this defaults to zero, weird things happen, so setting to 1
        );
    }


    public void spoofQrCode_userUnlock() throws IOException
    {
        broadcastQrCode(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_REQUEST_GENERAL);
    }


    public void spoofQrCode_adminUnlock() throws IOException
    {
        broadcastQrCode(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_REQUEST_ADMIN);
    }


    public void spoofQrCode_featureEnableUnlock() throws IOException
    {
        broadcastQrCode(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_REQUEST_FEATURE_ENABLE);
    }


    public void spoofQrCode_dummyDataUnlock() throws IOException
    {
        broadcastQrCode(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_DUMMY_DATA_MODE);
    }


    private void spoofQrCode_installationCookieServer() throws IOException
    {
        spoofQrCode_installationCustom("'i7ScG;aO)^pg_RN(d,Y['");
    }


    private void spoofQrCode_installationMuffinServer() throws IOException
    {
        spoofQrCode_installationCustom(muffin_internal_mqtt_qr_code);

       // spoofQrCode_installationCustom("'PV<J](-fDb$YU1uDc^+/'");
    }


    private void spoofQrCode_installationMuffinExternalServer() throws IOException
    {
        spoofQrCode_installationCustom(muffin_external_mqtt_qr_code);
    }


    private void spoofQrCode_installationQuicheServer() throws IOException
    {
    	// This connects to 127 port 86/9006

        // Note the double escape characters needed for > & and < and quadruple escape for a literal \.
        // and the string must contain the \" at the beginning and end as it gets parsed as part of sending it to the command line
        spoofQrCode_installationCustom( "\"?.?r*\\>iF%Do\\&L5\\<5#\\\\#k\"");

       // Old QR code for 127 port 80
       //         + " --es \"QR_CODE_CONTENTS\" \"\\'K\\)KO],=:\\<[WA6TcNsUr\""
    }


    private void spoofQrCode_installationExternalTestServer() throws IOException
    {
        // Note the double escape characters needed for ( $ ;    and quadruple escape for a literal \.
        // and the string must contain the \" at the beginning and end as it gets parsed as part of sending it to the command line
        spoofQrCode_installationCustom( "\"N\\(,j=\\$FHZLAGs\\;@!N\\\\sY\"");
    }


    private void spoofQrCode_installationDomainNameMuffin() throws IOException
    {
        // port 88, 9008
        spoofQrCode_installationCustom("\"[IclQ!_aVfHj/qBi6[MI*_FQYA\\<RKd02n\\$*V@CT7\\;T@*\\(fU69a\\'hRS?5Y+H*T:T\\$R_+2%R!7nJU,L7EkWap*hDc\\$\\`\\`.?-a,k-BMn2E\\&\\\\fG\\>:8a!R/r\\>BacXO\"");

        // port 80, 9001
        //spoofQrCode_installationCustom("\"[IclQ!_aVfHj/qBi6[MI*_FQYA\\<RKd02n\\$*V@CT7=bF_SG*0BV\\$*__%\\(p[:\\`On*aW\\(C8=A],1CtJ[\\)PJWap*hDc\\$\\`\\`.?-a,k-BMn2E\\&\\\\fG\\>:8a!R/r\\>BacXO\"");
    }


    private void spoofQrCode_installationDomainNameQuiche() throws IOException
    {
        // ports 86, 9006
    //    spoofQrCode_installationCustom("'199-2[OEVHHn(gB2W^dBd]P`.#bY(F<#5nlnYHjlXWi(\\L<J!B+OqF:,<WR`2ZAW\\\"D&\\qnq(QltafY-+Wap*hDc$``.?-a,k-BMn2E&\\fG>:8a!R/r>BacXO'");
        spoofQrCode_installationCustom("'199-2[OEVHHn(gB2W^dBd]P`.#bY(F<#5nlnYHjl=bF_SG*0BV$*__%(p[:`On*aW(C8=A],1CtJ[)PJWap*hDc$``.?-a,k-BMn2E&\\fG>:8a!R/r>BacXO'");
    }

    /* Note escape characters for the below:
     * we use '\\'' to render a single quote in the eventual command. This is because the shell will
     * treat a ' as the end of the command, so we need the shell command to receive '\''
     * (see https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings)
     * BUT we then have to escape the backslash for java string parsing - the result is '\\''
     * Finally we also have to use \\ to render as \ in the command.
     * Everything else seems to work fine.
     */

    private void spoofQrCode_installationDomainNameWin2019() throws IOException
    {
        spoofQrCode_installationCustom("'_4(oa8M$qOlafLY961`+ghe8Kg;Fb5^Ah9E6@`nT`6n?%l(1OQbA/9H7n,#AT?_B8EkH\\gkukUu'\\''sjcj:e)V(ncWuYo:aiJg'\\'')rN1`8$l)MaXe6Km0]pR^b`'");
    }

    private void spoofQrCode_installationCustom(String custom_qr_contents) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_patient_gateway
                        + " --ei \"command\" " + Commands.CMD_VALIDATE_INSTALLATION_QR_CODE.ordinal()
                        + " --es \"QR_CODE_CONTENTS\" " + custom_qr_contents
        );
    }


    public void spoofQrCode_installation() throws IOException
    {
        switch(BuildConfig.testServerName)
        {
            case "Muffin":
            case "muffin":
            {
                spoofQrCode_installationMuffinServer();
            }
            break;

            case "MuffinExternal":
            case "muffinexternal":
            {
                spoofQrCode_installationMuffinExternalServer();
            }
            break;

            case "Cookie":
            case "cookie":
            {
                spoofQrCode_installationCookieServer();
            }
            break;

            case "Quiche":
            case "quiche":
            {
                spoofQrCode_installationQuicheServer();
            }
            break;

            case "Test":
            case "test":
            {
                spoofQrCode_installationExternalTestServer();
            }
            break;

            case "Win2019":
            case "win2019":
            {
                spoofQrCode_installationDomainNameWin2019();
            }
            break;

            default:
            {
                if(BuildConfig.testServerName.length() >= 20)
                {
                    // assume we've passed in an encrypted QR code string
                    spoofQrCode_installationCustom(BuildConfig.testServerName);
                }
            }
            break;
        }
    }


    public void spoofQrCode_installationDomainName() throws IOException
    {
        switch(BuildConfig.testServerName)
        {
            case "Muffin":
            case "muffin":
            {
                spoofQrCode_installationDomainNameMuffin();
            }
            break;

            case "Quiche":
            case "quiche":
            {
                spoofQrCode_installationDomainNameQuiche();
            }
            break;

            default:
            {
                if(BuildConfig.testServerName.length() == 20)
                {
                    // assume we've passed in an encrypted QR code string
                    spoofQrCode_installationCustom(BuildConfig.testServerName);
                }
            }
            break;
        }
    }


    public String getServerAddress()
    {
        switch(BuildConfig.testServerName)
        {
            case "Muffin":
            case "muffin":
            {
                return muffin_address;
            }

            case "MuffinExternal":
            case "muffinexternal":
            {
                return muffin_external_address;
            }

            case "Cookie":
            case "cookie":
            {
                return cookie_address;
            }

            case "Quiche":
            case "quiche":
            {
                return quiche_address;
            }

            case "test":
            case "Test":
            {
                return external_test_server_address;
            }

            case "Win2019":
            case "win2019":
            {
                return win2019_address;
            }
        }

        return "";
    }


    public String getServerPort()
    {
        switch(BuildConfig.testServerName)
        {
            case "Muffin":
            case "muffin":
            case "MuffinExternal":
            case "muffinexternal":
            {
                return muffin_port;
            }

            case "Cookie":
            case "cookie":
            {
                return cookie_port;
            }


            case "Quiche":
            case "quiche":
            {
                return quiche_port;
            }


            case "test":
            case "Test":
            {
                return external_test_server_port;
            }

            case "Win2019":
            case "win2019":
            {
                return win2019_port;
            }
        }

        return "";
    }


    public String getServerRealTimePort()
    {
        switch(BuildConfig.testServerName)
        {
            case "Muffin":
            case "muffin":
            case "MuffinExternal":
            case "muffinexternal":
            {
                return muffin_mqtt_port;
            }

            case "Cookie":
            case "cookie":
            {
                return cookie_wamp_port;
            }


            case "Quiche":
            case "quiche":
            {
                return quiche_wamp_port;
            }


            case "test":
            case "Test":
            {
                return external_test_server_wamp_port;
            }

            case "Win2019":
            case "win2019":
            {
                return win2019_real_time_port;
            }
        }

        return "";
    }


    private void spoofQrCode_addDevice(BarcodeDeviceType barcode_type, String spoofed_id, boolean dummy_data) throws IOException
    {
        DeviceType device_type = DeviceType.DEVICE_TYPE__INVALID;
        SensorType sensor_type = SensorType.SENSOR_TYPE__INVALID;

        switch (barcode_type)
        {
            case BARCODE_TYPE__LIFETOUCH:
            {
                device_type = DeviceType.DEVICE_TYPE__LIFETOUCH;
                sensor_type = SensorType.SENSOR_TYPE__LIFETOUCH;
            }
            break;

            case BARCODE_TYPE__LIFETEMP_V2:
            {
                device_type = DeviceType.DEVICE_TYPE__LIFETEMP_V2;
                sensor_type = SensorType.SENSOR_TYPE__TEMPERATURE;
            }
            break;

            case BARCODE_TYPE__NONIN_WRIST_OX:
            {
                device_type = DeviceType.DEVICE_TYPE__NONIN_WRIST_OX;
                sensor_type = SensorType.SENSOR_TYPE__SPO2;
            }
            break;

            case BARCODE_TYPE__AND_UA767:
            {
                device_type = DeviceType.DEVICE_TYPE__AND_UA767;
                sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
            }
            break;

            case BARCODE_TYPE__NONIN_WRIST_OX_BTLE:
            {
                device_type = DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE;
                sensor_type = SensorType.SENSOR_TYPE__SPO2;
            }
            break;

            case BARCODE_TYPE__LIFETOUCH_BLUE_V2:
            {
                device_type = DeviceType.DEVICE_TYPE__LIFETOUCH_BLUE_V2;
                sensor_type = SensorType.SENSOR_TYPE__LIFETOUCH;
            }
            break;

            case BARCODE_TYPE__AND_TM2441:
            {
                device_type = DeviceType.DEVICE_TYPE__AND_ABPM_TM2441;
                sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
            }
            break;

            case BARCODE_TYPE__AND_UC352BLE: // Weight Scale
            {
                device_type = DeviceType.DEVICE_TYPE__AND_UC352BLE;
                sensor_type = SensorType.SENSOR_TYPE__WEIGHT_SCALE;
            }
            break;
        }

        // Assuming here we're unlikely to have a real device with the MAC address FF:FF:FF:FF:FF:FF...
        String command = adb_string + string_broadcast_command_to_user_interface
                + " --ei \"command\" " + Commands.CMD_REPORT_QR_CODE_DETAILS.ordinal()
                + " --es \"bluetooth_device_address\" \"" + generateDummyMacAddressByType(barcode_type) + "\""
                + " --ei \"barcode_type\" " + barcode_type.ordinal()
                + " --ei \"device_type\" " + device_type.ordinal()
                + " --ei \"sensor_type\" " + sensor_type.ordinal()
                + " --el \"human_readable_product_id\" " + spoofed_id
                + " --ez \"qr_code_validity\" true"
                + " --ez \"dummy_data\" " + dummy_data;

        Runtime.getRuntime().exec(command);
    }


    public void spoofQrCode_scanFakeLifetouch() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__LIFETOUCH, "1111", false);
    }

    public void spoofQrCode_scanDummyDataLifetouch() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__LIFETOUCH, "1111", true);
    }

    public void spoofQrCode_scanFakeLifetouchBlueV2() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__LIFETOUCH_BLUE_V2, "1111", false);
    }

    public void spoofQrCode_scanDummyDataLifetouchBlueV2() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__LIFETOUCH_BLUE_V2, "1111", true);
    }

    public void spoofQrCode_scanFakeLifetemp() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__LIFETEMP_V2, "2222", false);
    }

    public void spoofQrCode_scanDummyDataLifetemp() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__LIFETEMP_V2, "2222", true);
    }

    public void spoofQrCode_scanFakeNonin() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__NONIN_WRIST_OX, "3333", false);
    }

    public void spoofQrCode_scanDummyDataNonin() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__NONIN_WRIST_OX, "3333", true);
    }

    public void spoofQrCode_scanFakeBtleNonin() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__NONIN_WRIST_OX_BTLE, "3333", false);
    }

    public void spoofQrCode_scanDummyDataBtleNonin() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__NONIN_WRIST_OX_BTLE, "3333", true);
    }

    public void spoofQrCode_scanFakeAnD() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__AND_UA767, "4444", false);
    }

    public void spoofQrCode_scanDummyDataAnD() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__AND_UA767, "4444", true);
    }

    public void spoofQrCode_scanFakeAnD_TM2441() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__AND_TM2441, "4444", false);
    }

    public void spoofQrCode_scanDummyDataAnD_TM2441() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__AND_TM2441, "4444", true);
    }

    public void spoofQrCode_scanFakeWeightScale() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__AND_UC352BLE, "5555", false);
    }

    public void spoofQrCode_scanDummyWeightScale() throws IOException
    {
        spoofQrCode_addDevice(BarcodeDeviceType.BARCODE_TYPE__AND_UC352BLE, "5555", true);
    }

    public void spoofCommandIntent_restartInstallationWizard()
    {
        spoofCommandNoParameters(string_broadcast_command_to_patient_gateway, Commands.CMD_RESTART_INSTALLATION_WIZARD);
    }

    private void spoofCommandNoParameters(String string_broadcast_command_to_destination, Commands command)
    {
        try
        {
            Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_destination
                    + " --ei \"command\" " + command.ordinal()
            );
        }
        catch (IOException e)
        {
            System.out.println("spoofCommandNoParameters FAILED");

            e.printStackTrace();
        }
    }

    public void spoofCommandIntent_endExistingSession()
    {
        spoofCommandNoParameters(string_broadcast_command_to_patient_gateway, Commands.CMD_END_EXISTING_SESSION);
    }

    public void spoofCommandIntent_deleteEwsThresholdsTestOnly()
    {
        spoofCommandNoParameters(string_broadcast_command_to_patient_gateway, Commands.CMD_TEST_ONLY_DELETE_EWS_THRESHOLDS);
    }

    public void spoofCommandIntent_reconnectWifi()
    {
        spoofCommandNoParameters(string_broadcast_command_to_patient_gateway, Commands.CMD_RECONNECT_WIFI);
    }

    public void spoofCommandIntent_emptyDatabase()
    {
        spoofCommandNoParameters(string_broadcast_command_to_patient_gateway, Commands.CMD_EMPTY_LOCAL_DATABASE);
    }

    public void spoofCommandIntent_stopFastUiUpdates()
    {
        spoofCommandNoParameters(string_broadcast_command_to_user_interface, Commands.CMD_TEST_ONLY_STOP_FAST_UI_UPDATES);
    }

    public void spoofCommandIntent_simulateDeviceLeadsOff(SensorType sensor_type, boolean simulate_leads_off) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_patient_gateway
                + " --ei \"command\" " + Commands.CMD_FORCE_DEVICE_LEADS_OFF_STATE.ordinal()
                + " --ei \"sensor_type\" " + sensor_type.ordinal()
                + " --ez \"simulate_leads_off\" " + simulate_leads_off
        );
    }

    public void spoofCommandIntent_simulateDeviceConnectionState(SensorType sensor_type, boolean simulated_connection_state) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_patient_gateway
                + " --ei \"command\" " + Commands.CMD_SPOOF_DEVICE_CONNECTION_STATE.ordinal()
                + " --ei \"sensor_type\" " + sensor_type.ordinal()
                + " --ez \"connected\" " + simulated_connection_state
        );
    }

    public void spoofCommandIntent_setLongTermMeasurementTimeout(SensorType sensor_type, int timeout) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_patient_gateway
                + " --ei \"command\" " + Commands.CMD_SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES.ordinal()
                + " --ei \"sensor_type\" " + sensor_type.ordinal()
                + " --ei \"timeout\" " + timeout
        );
    }

    public void spoofCommandIntent_simulateUnexpectedUnbond() throws IOException
    {
        Runtime.getRuntime().exec(adb_string + "shell am broadcast -a BluetoothLe_AnD_TM2441.ACTION_UNEXPECTED_UNBOND");
    }


    public void spoofCommandIntent_allSoftwareUpdatesAvailable(String available_version) throws IOException
    {
        spoofCommandIntent_softwareUpdateAvailable(available_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);
        spoofCommandIntent_softwareUpdateAvailable(available_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);
    }


    public void spoofCommandIntent_softwareUpdateAvailable(String available_version, DeviceType device_type) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_user_interface
                + " --ei \"command\" " + Commands.CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE.ordinal()
                + " --ei \"available_version\" " + available_version
                + " --ei \"device_type\" " + device_type.ordinal()
        );
    }


    public void spoofCommandIntent_allSoftwareUpdatesCompleted(String available_version) throws IOException
    {
        spoofCommandIntent_softwareUpdateCompleted(available_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);
        spoofCommandIntent_softwareUpdateCompleted(available_version, DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);
    }


    public void spoofCommandIntent_softwareUpdateCompleted(String available_version, DeviceType device_type) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_user_interface
                + " --ei \"command\" " + Commands.CMD_TEST_ONLY_SPOOF_SOFTWARE_UPDATE_INSTALLED.ordinal()
                + " --ei \"available_version\" " + available_version
                + " --ei \"device_type\" " + device_type.ordinal()
        );
    }


    public void spoofCommandIntent_serverSyncingTestMode(boolean test_mode_on) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_patient_gateway
                + " --ei \"command\" " + Commands.CMD_SERVER_SYNC_DATA_IN_TEST_MODE.ordinal()
                + " --ez \"test_mode\" " + test_mode_on
        );
    }


    public void spoofCommandIntent_forceResyncServerData() throws IOException
    {
        Runtime.getRuntime().exec(adb_string + string_broadcast_command_to_patient_gateway
                + " --ei \"command\" 500");
    }


    public void sendStartIntent_patientGateway() throws IOException
    {
        Runtime.getRuntime().exec(adb_string + "shell monkey -p com.isansys.patientgateway -c android.intent.category.LAUNCHER 1");
    }

    public void sendStopIntent_patientGateway() throws IOException
    {
        Runtime.getRuntime().exec(adb_string + "shell am force-stop com.isansys.patientgateway");
    }

    /**
     * Goes through a drop-down list looking for an option with the provided text.
     *
     * This is a bit of a hack - the proper way to handle scrolling down a list id via UiAutomator
     * See e.g. https://github.com/appium/java-client/issues/747
     *
     * This method exploits the fact that when an option is selected, the next time the list is opened that
     * option is at the top. So if the desired option is off the bottom of the screen, select the last element
     * and re-open the drop-down. Keep doing this until the desired option is found.
     * If the last element is the same one as before, we've hit the end and return false.
     *
     * @param spinner_id - id of the drop-down selector
     * @param element_text - text of desired selection
     * @return true if desired text was found, false if not
     */
    public boolean clickElementOnSpinner(String spinner_id, String element_text)
    {
        String previous_selection = "";

        while(true)
        {
            m_driver.findElement(By.id(spinner_id)).click();

            // Not recommended, but adding in to help reliability...
            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            List<WebElement> element_list = m_driver.findElements(By.id("android:id/text1"));
            for (WebElement element : element_list)
            {
                if(element.getText().equals(element_text))
                {
                    element.click();

                    return true;
                }
            }

            if(element_list.size() <= 0)
            {
                return false;
            }

            if(previous_selection.equals(element_list.get(element_list.size() - 1).getText()))
            {
                return false;
            }
            else
            {
                previous_selection = element_list.get(element_list.size() - 1).getText();

                element_list.get(element_list.size() - 1).click();
            }
        }
    }


    public void captureScreenShot() throws IOException
    {
        captureScreenShot("");
    }


    // Files are copied from temp area (e.g. C:\Users\Neal\AppData\Local\Temp)
    // to Appium screenshot directory of Android Studio svn area (e.g. D:\svn\gateway\trunk\User_Interface\Appium\screenshot), then deleted from temp
    public void captureScreenShot(String name) throws IOException
    {
        File appium_img = m_driver.getScreenshotAs(OutputType.FILE);
        File web_img = null;

        if(m_web_driver != null)
        {
            web_img = ((TakesScreenshot)m_web_driver).getScreenshotAs(OutputType.FILE);
        }


        File output_path = new File(screenshot_path);

        if(!output_path.exists())
        {
            output_path.mkdirs();
        }

        //Date format for screenshot file name
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss_SSS");

        //Setting file name
        String file_name = name + "_" + df.format(new Date())+".png";

        //copy screenshot file into screenshot folder.
        FileUtils.copyFile(appium_img, new File(screenshot_path + file_name));
        if(web_img != null)
        {
            FileUtils.copyFile(web_img, new File(screenshot_path + "WEB_" + file_name));
        }

        System.out.println("Screenshot captured " + screenshot_path + file_name);
    }

    /**
     * Hacky way to press the done button
     *
     * Can't find a way to make Appium trigger the IME_ACTION_DONE we're listening for in the UI, so
     * the only real option is to do it via screen co-ordinates. Normally this would be bad, but
     * because we're targeting a specific tablet we can get away with it.
     *
     */
    public void pressDone()
    {
        TouchAction done = new TouchAction(m_driver);
        done.tap(PointOption.point(1800, 900)).perform();
    }

    public void swipeActionScrollDown()
    {
        m_driver.findElement(By.id("patient_vitals_display_scrollview")).click();
        Dimension dim = m_driver.manage().window().getSize();
        int height = dim.getHeight();
        int width = dim.getWidth();
        int x = width/2;
        int top_y = (int)(height*0.80);
        int bottom_y = (int)(height*0.20);
        //System.out.println("coordinates :" + x + "  "+ top_y + " "+ bottom_y);
        TouchAction ts = new TouchAction(m_driver);
        long duration = 1000;
        ts.press(PointOption.point(x,top_y)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).moveTo(PointOption.point(x, bottom_y)).release().perform();
    }

    public void swipeActionScrollUp()
    {
        m_driver.findElement(By.id("patient_vitals_display_scrollview")).click();
        Dimension dim = m_driver.manage().window().getSize();
        int height = dim.getHeight();
        int width = dim.getWidth();
        int x = width/2;
        int top_y = (int)(height*0.80);
        int bottom_y = (int)(height*0.20);
        TouchAction ts = new TouchAction(m_driver);
        long duration = 1000;
        ts.press(PointOption.point(x,bottom_y)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration))).moveTo(PointOption.point(x,top_y)).release().perform();

    }


    public void printTestStart(String test_name)
    {
        System.out.println("**** TEST START ****");

        String time = m_driver.getDeviceTime();

        System.out.println("**** START TIME " + time + " ****");

        System.out.println("**** Test Case: " + test_name + " ****");
    }


    public void printTestStart(String test_name, String case_id)
    {
        System.out.println("**** TEST START ****");

        String time = m_driver.getDeviceTime();

        System.out.println("**** START TIME " + time + " ****");

        case_id_in_progress = case_id;

        System.out.println("**** Test Case: " + test_name + " Case ID: "+ case_id_in_progress + " ****");
    }


    public void printTestEnd()
    {
        System.out.println("**** TEST END ****");
        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.println("");
    }


    private String generateDummyMacAddressByType(BarcodeDeviceType type)
    {
        String address;
        switch (type)
        {
            case BARCODE_TYPE__LIFETOUCH:
            {
                address = "AA:AA:AA:AA:AA:AA";
            }
            break;

            case BARCODE_TYPE__LIFETEMP_V2:
            {
                address = "BB:BB:BB:BB:BB:BB";
            }
            break;

            case BARCODE_TYPE__NONIN_WRIST_OX:
            {
                address = "DD:DD:DD:DD:DD:DD";
            }
            break;

            case BARCODE_TYPE__AND_UA767:
            {
                address = "EE:EE:EE:EE:EE:EE";
            }
            break;

            default:
            {
                address = "11:22:33:44:55:66";
            }
            break;
        }

        return address;
    }

    public WebElement findTextViewElementWithString(String string_id)
    {
//        List<WebElement> elements = m_driver.findElements(By.className("TextView"));

        String text_value = getAppString(string_id);

        String xpath_query = "//android.widget.TextView[@text='" + text_value + "']";

        return m_driver.findElement(By.xpath(xpath_query));

//        for(WebElement element : elements)
//        {
//            if(element.getText().equals(text_value))
//            {
//                return element;
//            }
//        }
//
//        return m_driver.findElement(By.id("thisElementDoesntExistSoWillFailTheTest"));
    }


    public void setPatientId(String id)
    {
        m_driver.findElement(By.id("editPatientID")).click();

        m_driver.findElement(By.id("editPatientID")).sendKeys(id);

        WebElement set_id =  m_driver.findElement(By.id("textHeaderPatientId"));

        WebDriverWait wait = new WebDriverWait(m_driver, 20, 345);

        wait.until(m_driver -> ExpectedConditions.textToBePresentInElement(set_id, id).apply(m_driver));

        pressDone();
    }


    public void quitDriver()
    {
        try
        {
            m_driver.quit();

        }
        catch(Exception e)
        {
            System.out.println("Could not quit Appium driver");
            e.printStackTrace();
        }

        if (m_web_driver != null)
        {
            try
            {
                m_web_driver.quit();
            }
            catch(Exception e)
            {
                System.out.println("Could not quit Selenium driver");
                e.printStackTrace();
            }
        }

    }


    public void setWebDriver(WebDriver web_driver)
    {
        m_web_driver = web_driver;
    }


    public void beginScreenRecord(String file_name) throws IOException
    {
        Runtime.getRuntime().exec(adb_string + "shell screenrecord /sdcard/" + file_name + ".mp4");
    }


    public enum TestRailResult
    {
        INVALID_DO_NOT_USE, // not used by TestRail, but here so that PASSED has the value 1 not 0
        PASSED,
        BLOCKED,
        UNTESTED,
        RETEST,
        FAILED
    }

    private String case_id_in_progress = "";

    public void updateTestRail(TestRailResult result)
    {
        int run_id = BuildConfig.testRunId;
        if(run_id > 0)
        {
            try {
                JSONObject c = (JSONObject) testrail_client.sendGet("get_case/" + case_id_in_progress);

                System.out.println(c.get("title"));

                Map data = new HashMap();
                data.put("status_id", result.ordinal());
                data.put("comment", "Automated test result");

                c = (JSONObject) testrail_client.sendPost("add_result_for_case/" + run_id + "/" + case_id_in_progress + "/", data);

                System.out.println(c.get("title"));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Run ID not set");
        }
    }
}
