package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;


public class FragmentAndroidPermissions extends FragmentIsansys implements OnClickListener
{
    public static final String KEY_PERMISSION_OVERLAY = "KEY_PERMISSION_OVERLAY";
    public static final String KEY_PERMISSION_WRITE_SETTINGS = "KEY_PERMISSION_WRITE_SETTINGS";
    public static final String KEY_PERMISSION_CAMERA = "KEY_PERMISSION_CAMERA";
    public static final String KEY_PERMISSION_RECORD_AUDIO = "KEY_PERMISSION_RECORD_AUDIO";
    public static final String KEY_PERMISSION_WRITE_EXTERNAL_STORAGE = "KEY_PERMISSION_WRITE_EXTERNAL_STORAGE";
    public static final String KEY_PERMISSION_ACCESS_NOTIFICATION_POLICY = "KEY_PERMISSION_ACCESS_NOTIFICATION_POLICY";
    public static final String KEY_PERMISSION_REQUEST_PACKAGE_INSTALLS = "KEY_PERMISSION_REQUEST_PACKAGE_INSTALLS";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.android_permissions, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            View viewOverlayPermission = v.findViewById(R.id.viewOverlayPermission);
            Button buttonEnableOverlayPermissions = v.findViewById(R.id.buttonEnableOverlayPermissions);
            buttonEnableOverlayPermissions.setOnClickListener(this);
            showIndicator(viewOverlayPermission, buttonEnableOverlayPermissions, bundle.getBoolean(KEY_PERMISSION_OVERLAY));

            View viewWriteSettingsPermission = v.findViewById(R.id.viewWriteSettingsPermission);
            Button buttonEnableWriteSettingsPermissions = v.findViewById(R.id.buttonEnableWriteSettingsPermissions);
            buttonEnableWriteSettingsPermissions.setOnClickListener(this);
            showIndicator(viewWriteSettingsPermission, buttonEnableWriteSettingsPermissions, bundle.getBoolean(KEY_PERMISSION_WRITE_SETTINGS));

            View viewCameraPermission = v.findViewById(R.id.viewCameraPermission);
            Button buttonEnableCameraPermissions = v.findViewById(R.id.buttonEnableCameraPermissions);
            buttonEnableCameraPermissions.setOnClickListener(this);
            showIndicator(viewCameraPermission, buttonEnableCameraPermissions, bundle.getBoolean(KEY_PERMISSION_CAMERA));

            View viewRecordAudioPermission = v.findViewById(R.id.viewRecordAudioPermission);
            Button buttonEnableRecordAudioPermissions = v.findViewById(R.id.buttonEnableRecordAudioPermissions);
            buttonEnableRecordAudioPermissions.setOnClickListener(this);
            showIndicator(viewRecordAudioPermission, buttonEnableRecordAudioPermissions, bundle.getBoolean(KEY_PERMISSION_RECORD_AUDIO));

            View viewWriteExternalStoragePermission = v.findViewById(R.id.viewWriteExternalStoragePermission);
            Button buttonEnableWriteExternalStoragePermissions = v.findViewById(R.id.buttonEnableWriteExternalStoragePermissions);
            buttonEnableWriteExternalStoragePermissions.setOnClickListener(this);
            showIndicator(viewWriteExternalStoragePermission, buttonEnableWriteExternalStoragePermissions, bundle.getBoolean(KEY_PERMISSION_WRITE_EXTERNAL_STORAGE));

            View viewAccessNotificationPolicyPermission = v.findViewById(R.id.viewAccessNotificationPolicyPermission);
            Button buttonEnableAccessNotificationPolicyPermissions = v.findViewById(R.id.buttonEnableAccessNotificationPolicyPermissions);
            buttonEnableAccessNotificationPolicyPermissions.setOnClickListener(this);
            showIndicator(viewAccessNotificationPolicyPermission, buttonEnableAccessNotificationPolicyPermissions, bundle.getBoolean(KEY_PERMISSION_ACCESS_NOTIFICATION_POLICY));

            View viewInstallPackagesPermission = v.findViewById(R.id.viewInstallPackagesPermission);
            Button buttonEnableInstallPackagesPermissions = v.findViewById(R.id.buttonEnableInstallPackagesPermissions);
            buttonEnableInstallPackagesPermissions.setOnClickListener(this);
            showIndicator(viewInstallPackagesPermission, buttonEnableInstallPackagesPermissions, bundle.getBoolean(KEY_PERMISSION_REQUEST_PACKAGE_INSTALLS));
        }

        return v;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.buttonEnableOverlayPermissions)
        {
            main_activity_interface.requestOverlayPermission();
        }
        else if (id == R.id.buttonEnableWriteSettingsPermissions)
        {
            main_activity_interface.requestWriteSettingsPermission();
        }
        else if (id == R.id.buttonEnableCameraPermissions)
        {
            main_activity_interface.requestCameraPermission();
        }
        else if (id == R.id.buttonEnableRecordAudioPermissions)
        {
            main_activity_interface.requestRecordAudioPermission();
        }
        else if (id == R.id.buttonEnableWriteExternalStoragePermissions)
        {
            main_activity_interface.requestWriteExternalStoragePermission();
        }
        else if (id == R.id.buttonEnableAccessNotificationPolicyPermissions)
        {
            main_activity_interface.requestAccessNotificationPolicyPermission();
        }
        else if (id == R.id.buttonEnableInstallPackagesPermissions)
        {
            main_activity_interface.requestInstallPackagesPermission();
        }
    }


    private void showIndicator(View indicator, Button button, boolean success)
    {
        indicator.setVisibility(View.VISIBLE);

        if (success)
        {
            indicator.setBackground(ContextCompat.getDrawable(main_activity_interface.getAppContext(), R.drawable.circle_green));
            button.setVisibility(View.INVISIBLE);
        }
        else
        {
            indicator.setBackground(ContextCompat.getDrawable(main_activity_interface.getAppContext(), R.drawable.circle_red));
            button.setVisibility(View.VISIBLE);
        }
    }
}
