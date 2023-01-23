package com.isansys.patientgateway;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.Process;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity implements ContextInterface {
    private RemoteLogging Log;

    private Context context;

    private final String TAG = "PG Main Activity";

    private boolean bound_to_patient_gateway_service = false;

    // This boolean is set true when logger's activity triggers this activity with the extras in intent.
    private boolean is_admin_mode_close_app_triggered = false;

    // Used by Appium tests to prevent the Gateway app from restarting the UI - as the UI is under test
    // so will be created by Appium
    private boolean test_mode;

    private final boolean include_usb_accessory_code = false;

    private View viewFineLocationPermission;
    private View viewBackgroundLocationPermission;
    private View viewWriteExternalStoragePermission;
    private View tableNearbyConnectivityPermission;
    private static final int SELECT_DEVICE_REQUEST_CODE = 1002;


    private enum PermissionCodes {
        FINE_LOCATION_PERMISSION_REQUEST_CODE,
        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE,
        READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE,
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log = new RemoteLogging();

        TextView textViewBuildVersion = findViewById(R.id.textViewBuildVersion);
        textViewBuildVersion.setText(String.valueOf(Build.VERSION.SDK_INT));

        TextView textViewAndroidVersion = findViewById(R.id.textViewAndroidVersion);
        textViewAndroidVersion.setText(String.valueOf(Build.VERSION.RELEASE));

        viewFineLocationPermission = findViewById(R.id.viewFineLocationPermission);
        showIndicator(viewFineLocationPermission, checkForAccessFineLocationPermission());

        TableRow tableRowBackgroundLocationPermissions = findViewById(R.id.tableRowBackgroundLocationPermissions);
        if (needBackgroundLocationPermission()) {
            viewBackgroundLocationPermission = findViewById(R.id.viewBackgroundLocationPermission);
            showIndicator(viewBackgroundLocationPermission, checkForAccessBackgroundLocationPermission());
        } else {
            tableRowBackgroundLocationPermissions.setVisibility(View.GONE);
        }

        viewWriteExternalStoragePermission = findViewById(R.id.viewWriteExternalStoragePermission);
        setupIndicatorForReadWriteStoragePermissions();

        if (checkNeedForBluetoothPermissionForAndroid12()) {
            setupIndicatorForNearbyConnectivityPermissions();
        }

        // See if there is UI initiated Shut Down info as part of the intent
        is_admin_mode_close_app_triggered = getIntent().getStringExtra("CloseApplicationTriggered") != null;

        registerTestModeReceiver();

        Log.d(TAG, "onCreate : is_admin_mode_close_app_triggered = " + is_admin_mode_close_app_triggered);

        if (!is_admin_mode_close_app_triggered) {
            deleteOldUpdateFiles();

            if (gotAllNeededPermissions()) {
                // Start the Patient Gateway Service running
                startService(new Intent(getAppContext(), PatientGatewayService.class));
            }
        }

        if (include_usb_accessory_code) {
            usbAccessoryOnCreate();
        }


//        CompanionDeviceManager deviceManager =
//                (CompanionDeviceManager) getSystemService(
//                        Context.COMPANION_DEVICE_SERVICE
//                );
//
//        BluetoothDeviceFilter deviceFilter =
//                new BluetoothDeviceFilter.Builder()
//                        //.setNamePattern(Pattern.compile("My device"))
//                        //.addServiceUuid(new ParcelUuid(new UUID(0x123abcL, -1L)), null)
//                        .build();
//
//        AssociationRequest pairingRequest = new AssociationRequest.Builder()
//                // .addDeviceFilter(deviceFilter)
//                //   .setSingleDevice(true)
//                .build();
//
//
//        deviceManager.associate(pairingRequest,
//                new CompanionDeviceManager.Callback() {
//                    @Override
//                    public void onDeviceFound(IntentSender chooserLauncher) {
//                        try {
//                            startIntentSenderForResult(chooserLauncher,
//                                    SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0);
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.d("Bluetooth", e.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(CharSequence error) {
//                        Log.d("Bluetooth", error.toString());
//                    }
//                }, null);
//
//
    }

    private void setupIndicatorForReadWriteStoragePermissions() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            TableRow tableWriteExternalStoragePermission = findViewById(R.id.tableWriteExternalStoragePermission);
            tableWriteExternalStoragePermission.setVisibility(View.GONE);
        } else {
            showIndicator(viewWriteExternalStoragePermission, checkForWriteExternalStoragePermission());
        }
    }

    private void setupIndicatorForNearbyConnectivityPermissions() {
        View tableNearbyConnectivityPermission = findViewById(R.id.tableNearbyConnectivityPermission);
        tableNearbyConnectivityPermission.setVisibility(View.VISIBLE);
        View viewNearbyConnectivityPermission = findViewById(R.id.viewNearbyConnectivityPermission);
        showIndicator(viewNearbyConnectivityPermission, checkForBluetoothPermission());
    }

    // Check for SDK 29 or above (for new installs) or Android version >=10 for upgrades from existing Tablets going from 9-10 or 9-11.
    private boolean needBackgroundLocationPermission() {
        double androidVersion = getAndroidVersionFromExistingInstallOSUpgrades();

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) || (androidVersion >= 10)) {
            Log.d(TAG, "needBackgroundLocationPermission = " + true);
            return true;
        } else {
            Log.d(TAG, "needBackgroundLocationPermission = " + false);
            return false;
        }
    }

    private boolean checkNeedForBluetoothPermissionForAndroid12() {
        double androidVersion = getAndroidVersionFromExistingInstallOSUpgrades();
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || androidVersion >= 12;
    }

    private double getAndroidVersionFromExistingInstallOSUpgrades() {
        String androidVersionAsString = Build.VERSION.RELEASE;

        if (androidVersionAsString.contains(".")) {
            // Get from the the start to the first "."
            // E.g. 8.1.0 gets processed to 8
            androidVersionAsString = androidVersionAsString.substring(0, androidVersionAsString.indexOf("."));
        }

        double androidVersion = Double.parseDouble(androidVersionAsString);
        return androidVersion;
    }

    private void showIndicator(View indicator, boolean success) {
        indicator.setVisibility(View.VISIBLE);

        if (success) {
            indicator.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_green));
        } else {
            indicator.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_red));
        }
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart : is_admin_mode_close_app_triggered = " + is_admin_mode_close_app_triggered);

        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();

        context = getApplicationContext();

        Log.d(TAG, "onResume : is_admin_mode_close_app_triggered = " + is_admin_mode_close_app_triggered);

        if (is_admin_mode_close_app_triggered) {
            finishAndRemoveTask();
        } else {
            if (!test_mode) {
                Log.d(TAG, "Check for required Android Permissions");
                if (gotAllNeededPermissions()) {
                    Log.d(TAG, "Got all needed permissions. Starting UI after 3 seconds");

                    Executors.newSingleThreadExecutor().execute(() -> {

                        // Delay for 3 seconds so that we have a few seconds before the UI launches.
                        // Found that in some cases when running Appium tests, the gateway relaunching the
                        // UI can interfere with appium launching the test.
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startUserInterface();
                    });

                }
            }
        }

        if (include_usb_accessory_code) {
            usbAccessoryOnResume();
        }
    }

    private boolean gotAllNeededPermissions() {
        // Dont start the UI until got permission for Location Services
        // BTLE needs Coarse Location, but on Android 10+ Wifi SSID needs Fine Location.
        // Code asks for Fine as when permission is given that also gives Coarse permission by hierarchy
        // Also now need to ask for Write Settings permission from user

        boolean gotAllNeededPermissions = checkForAccessFineLocationPermission();
        if (!gotAllNeededPermissions) {
            Log.e(TAG, "Permissions : Dont have Fine Location Permission. Requesting from gotAllNeededPermissions");
            requestAccessFineLocationPermission();
        } else {
            Log.d(TAG, "Got Fine Location Permission");
        }

        if (checkNeedForBluetoothPermissionForAndroid12()) {
            gotAllNeededPermissions = gotAllNeededPermissions && checkForBluetoothPermission();
            if (!gotAllNeededPermissions) {
                Log.e(TAG, "Permissions : Dont have  Android 12 Bluetooth Permission. Requesting from gotAllNeededPermissions");
                requestForBluetoothPermission();
            } else {
                Log.d(TAG, "Got Android 12 Bluetooth Permission");
            }
        }

        // This is only needed on Android 10
        if (needBackgroundLocationPermission()) {
            gotAllNeededPermissions = gotAllNeededPermissions && checkForAccessBackgroundLocationPermission();
            if (!gotAllNeededPermissions) {
                Log.e(TAG, "Permissions : Dont have Background Location Permission. Requesting from gotAllNeededPermissions");
                // Todo --> Sam changes
                requestAccessBackgroundLocationPermission();
                //pairBluetoothDevice();
            } else {
                Log.d(TAG, "Got Background Location Permission");
            }
        }

        gotAllNeededPermissions = gotAllNeededPermissions && checkForWriteExternalStoragePermission() && checkForReadExternalStoragePermission();
        if (!gotAllNeededPermissions) {
            Log.e(TAG, "Permissions : Dont have Read and/or Write External Storage Permission. Requesting from gotAllNeededPermissions");
            requestReadAndWriteExternalStoragePermission();
        }

        return gotAllNeededPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionCodes.FINE_LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            showIndicator(viewFineLocationPermission, checkForAccessFineLocationPermission());

            if (grantResults.length > 0) {
                boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (!granted) {
                    Log.e(TAG, "Denied Fine Location permission. Asking again");

                    requestAccessFineLocationPermission();
                } else {
                    Log.d(TAG, "Got Fine Location permission");

                    // Work out the next permission to ask for.
                    if (needBackgroundLocationPermission()) {
                        Log.d(TAG, "Requesting Background Location permission from onRequestPermissionsResult (FINE_LOCATION_PERMISSION_REQUEST_CODE)");
                        requestAccessBackgroundLocationPermission();
                    } else {
                        Log.d(TAG, "Requesting Write External Storage permission from onRequestPermissionsResult (FINE_LOCATION_PERMISSION_REQUEST_CODE)");
                        requestReadAndWriteExternalStoragePermission();
                    }
                }
            }
        } else if (requestCode == PermissionCodes.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            showIndicator(viewBackgroundLocationPermission, checkForAccessBackgroundLocationPermission());

            if (grantResults.length > 0) {
                boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (!granted) {
                    Log.e(TAG, "Denied permission. Asking again");

                    requestAccessBackgroundLocationPermission();
                } else {
                    Log.d(TAG, "Got Background Location permission");

                    Log.d(TAG, "Requesting Write External Storage permission from onRequestPermissionsResult (BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE)");
                    requestReadAndWriteExternalStoragePermission();
                }
            }
        } else if (requestCode == PermissionCodes.READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE.ordinal()) {
            setupIndicatorForReadWriteStoragePermissions();
            if (grantResults.length > 0) {
                boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (!granted) {
                    Log.e(TAG, "Denied permission. Asking again");

                    requestReadAndWriteExternalStoragePermission();
                } else {
                    Log.d(TAG, "Got Write External Storage permission");

                    Log.d(TAG, "Auto restarting the Gateway app so permissions are valid");
                    triggerApplicationRestart();
                }
            }
        }
    }

    public void triggerApplicationRestart() {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "onPause : is_admin_mode_close_app_triggered = " + is_admin_mode_close_app_triggered);
        super.onPause();
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        // Unbind if it is bound to the service
        if (bound_to_patient_gateway_service) {
            Log.d(TAG, "unbindService");
            this.unbindService(myConnection);
            bound_to_patient_gateway_service = false;
        }
    }


    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy : isFinishing = " + isFinishing());

        super.onDestroy();

        if (include_usb_accessory_code) {
            usbAccessoryOnDestroy();
        }

        unregisterTestModeReceiver();

        Log.d(TAG, " onDestroy : kill process by PID =  " + Process.myPid());

        if (is_admin_mode_close_app_triggered) {
            Log.d(TAG, "onDestroy kill process by PID");
            Process.killProcess(Process.myPid());
        }
    }


    private final ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Connected to Patient Gateway Service");

            bound_to_patient_gateway_service = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "Disconnected from Patient Gateway Service");

            bound_to_patient_gateway_service = false;
        }
    };


    private void startUserInterface() {
        Intent launch_intent = getPackageManager().getLaunchIntentForPackage("com.isansys.pse_isansysportal");

        if (launch_intent != null) {
            Log.i(TAG, "Starting User Interface");
            startActivity(launch_intent);
        } else {
            Log.e(TAG, "No User Interface found");
        }
    }


    private void deleteOldUpdateFiles() {
        File PGFileToCheck = new File(Environment.getExternalStorageDirectory(), "PatientGateway.apk");

        if (PGFileToCheck.exists()) {
            if (PGFileToCheck.delete()) {
                Log.d(TAG, "Apk deleted");
            }
        }
    }


    // Start USB Accessory stuff

    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final String ACTION_USB_ACCESSORY_TRANSMIT_DATA = "com.isansys.patientgateway.usb.accessory.ACTION_USB_ACCESSORY_TRANSMIT_DATA";
    public static final String ACTION_USB_ACCESSORY_RECEIVED_DATA = "com.isansys.patientgateway.usb.accessory.ACTION_USB_ACCESSORY_RECEIVED_DATA";
    public static final String ACTION_USB_ACCESSORY_CONNECTED = "com.isansys.patientgateway.usb.accessory.ACTION_USB_ACCESSORY_CONNECTED";
    public static final String ACTION_USB_ACCESSORY_DISCONNECTED = "com.isansys.patientgateway.usb.accessory.ACTION_USB_ACCESSORY_DISCONNECTED";

    private static final String ACTION_USB_PERMISSION = "com.isansys.patientgateway.usb.accessory.USB_PERMISSION";

    private void usbAccessoryOnCreate() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction(ACTION_USB_STATE);
        registerReceiver(myUsbReceiver, filter);

        registerReceiver(broadcastReceiverSendDataToUsbAccessory, makeUsbAccessoryIntentFilter());
    }


    private void usbAccessoryOnResume() {
        if (myUsbAccessory != null) {
            setConnectionStatus(true);
        } else {
            UsbAccessory[] accessories = mUsbManager.getAccessoryList();
            UsbAccessory accessory = (accessories == null ? null : accessories[0]);
            if (accessory != null) {
                if (mUsbManager.hasPermission(accessory)) {
                    openAccessory(accessory);
                } else {
                    setConnectionStatus(false);

                    synchronized (myUsbReceiver) {
                        if (!mPermissionRequestPending) {
                            mUsbManager.requestPermission(accessory, mPermissionIntent);
                            mPermissionRequestPending = true;
                        }
                    }
                }
            } else {
                setConnectionStatus(false);

                Log.d(TAG, "myUsbAccessory is null");
            }
        }
    }


    private void usbAccessoryOnDestroy() {
        closeAccessory();
        unregisterReceiver(myUsbReceiver);
        unregisterReceiver(broadcastReceiverSendDataToUsbAccessory);
    }


    private UsbAccessory myUsbAccessory;
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;
    private ReadThread mReadThread;
    private WriteThread mWriteThread;


    private static IntentFilter makeUsbAccessoryIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_USB_ACCESSORY_TRANSMIT_DATA);
        return intentFilter;
    }


    private final BroadcastReceiver broadcastReceiverSendDataToUsbAccessory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (MainActivity.ACTION_USB_ACCESSORY_TRANSMIT_DATA.equals(action)) {
                writeViaAdk(intent.getStringExtra("message"));
            }
        }
    };


    private final BroadcastReceiver myUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);                                                   // RM - this seems to restart the gateway app somehow? So maybe problematic down the line...
                    } else {
                        Log.d(TAG, "Permission denied for accessory " + accessory);
                    }

                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(myUsbAccessory)) {
                    closeAccessory();
                }
            } else if (ACTION_USB_STATE.equals(action)) {
                boolean connected = intent.getExtras().getBoolean("connected");

                Log.d(TAG, "ACTION_USB_STATE: connected = " + connected);

                if (connected) {
                    usbAccessoryOnResume();
                }
            }
        }
    };


    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            myUsbAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);

            Toast.makeText(context, "Starting Connected Threads", Toast.LENGTH_LONG).show();

            mReadThread = new ReadThread();
            mReadThread.start();

            mWriteThread = new WriteThread();
            mWriteThread.start();

            setConnectionStatus(true);
        } else {
            setConnectionStatus(false);
        }
    }

    private void setConnectionStatus(boolean connected) {
        if (connected) {
            Log.d(TAG, "Accessory opened");
            getApplicationContext().sendBroadcast(new Intent(ACTION_USB_ACCESSORY_CONNECTED));
        } else {
            Log.d(TAG, "Accessory open failed");
            getApplicationContext().sendBroadcast(new Intent(ACTION_USB_ACCESSORY_DISCONNECTED));
        }
    }

    private void closeAccessory() {
        setConnectionStatus(false);

        // Cancel any thread currently running a connection
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }

        if (mWriteThread != null) {
            mWriteThread.cancel();
            mWriteThread = null;
        }


        // Close all streams
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (Exception ignored) {
        } finally {
            mInputStream = null;
        }

        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
        } catch (Exception ignored) {
        } finally {
            mOutputStream = null;
        }

        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException ignored) {
        } finally {
            mFileDescriptor = null;
            myUsbAccessory = null;
        }
    }


    private void writeViaAdk(String text) {
        byte[] buffer = text.getBytes();

        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed : " + e);
            }
        }
    }


    private class ReadThread extends Thread {
        final byte[] buffer = new byte[16384];
        boolean running;

        ReadThread() {
            running = true;
        }

        public void run() {
            while (running) {
                try {
                    final int bytes_received = mInputStream.read(buffer);
                    if (bytes_received > 0) {
                        byte[] payload = Arrays.copyOfRange(buffer, 0, bytes_received);
                        String message = Utils.byteArrayToHexString(payload);

                        Intent intent = new Intent(ACTION_USB_ACCESSORY_RECEIVED_DATA);
                        intent.putExtra("message", message);

                        intent.putExtra("payload", payload);

                        getApplicationContext().sendBroadcast(intent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }

        void cancel() {
            running = false;
        }
    }

    private class WriteThread extends Thread {
        boolean running;

        WriteThread() {
            running = true;
        }

        public void run() {
            while (running) {
                try {
                    Thread.sleep(250);

                    writeViaAdk("ping");
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }

        void cancel() {
            running = false;
        }
    }


    // End USB Accessory stuff


    // Test Mode for Appium tests to prevent UI launching
    private final static String ACTION_ENTER_TEST_MODE = "com.isansys.patientgateway.ACTION_ENTER_TEST_MODE";


    private void registerTestModeReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_ENTER_TEST_MODE);
        registerReceiver(testModeBroadcastReceiver, filter);
    }


    private void unregisterTestModeReceiver() {
        unregisterReceiver(testModeBroadcastReceiver);
    }


    private final BroadcastReceiver testModeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ACTION_ENTER_TEST_MODE)) {
                // true if Appium is running tests
                test_mode = intent.getBooleanExtra("enabled", false);

                // start the UI again to prevent the screen timing out at the end of the test suite
                if (!test_mode) {
                    startUserInterface();
                }
            }
        }
    };
    // End test mode

    public Context getAppContext() {
        return getApplicationContext();
    }


    public void sendBroadcastIntent(Intent intent) {
        try {
            sendBroadcast(intent);
        } catch (Exception ex) {
            Log.e("sendBroadcastIntent", "Send broadcast failed: " + ex);
        }
    }


    private boolean checkForPermission(String permission) {
        return ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }


    private boolean checkForAccessFineLocationPermission() {
        return checkForPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestAccessFineLocationPermission() {
        Log.d(TAG, "Requesting ACCESS_FINE_LOCATION permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionCodes.FINE_LOCATION_PERMISSION_REQUEST_CODE.ordinal());
    }


    private void requestAccessBackgroundLocationPermission() {
        if (needBackgroundLocationPermission()) {
            Log.d(TAG, "Requesting ACCESS_BACKGROUND_LOCATION permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PermissionCodes.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE.ordinal());
        }
    }

    private boolean checkForAccessBackgroundLocationPermission() {
        if (needBackgroundLocationPermission()) {
            return checkForPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        } else {
            return false;
        }
    }


    private boolean checkForWriteExternalStoragePermission() {
        if (isAndroid12AndBelow()) {
            return checkForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            return true;
        }
    }

    private boolean checkForReadExternalStoragePermission() {
        if (isAndroid12AndBelow()) {
            return checkForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            return true;
        }
    }

    private boolean isAndroid12AndBelow() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU;
    }

    private void requestReadAndWriteExternalStoragePermission() {
        if (isAndroid12AndBelow()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionCodes.READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE.ordinal());
        }
    }

    private boolean checkForBluetoothPermission() {
        return checkForPermission(Manifest.permission.BLUETOOTH_CONNECT) && checkForPermission(Manifest.permission.BLUETOOTH_SCAN);
    }

    private void requestForBluetoothPermission() {
        Log.d(TAG, "Requesting BLUETOOTH_CONNECT permission");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_DEVICE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                BluetoothDevice deviceToPair = data.getParcelableExtra(
                        CompanionDeviceManager.EXTRA_DEVICE
                );

                if (deviceToPair != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    deviceToPair.createBond();
                    // ... Continue interacting with the paired device.
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

