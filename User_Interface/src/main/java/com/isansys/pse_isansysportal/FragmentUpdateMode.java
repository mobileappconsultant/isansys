package com.isansys.pse_isansysportal;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.Locale;

public class FragmentUpdateMode extends FragmentIsansys implements View.OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    public static final String UPDATE_MODE_STATUS = "UPDATE_MODE_STATUS";

    private final static int ACTIVE_TEXT_SIZE = 36;
    private final static int INACTIVE_TEXT_SIZE = 24;

    private TextView labelPatientGateway;
    private TextView labelUserInterface;

    private TextView textAvailableGatewayVersion;
    private TextView textAvailableUserInterfaceVersion;

    private TextView textPatientGatewaySoftwareVersion;
    private TextView textUserInterfaceSoftwareVersion;

    Button buttonInstallGatewayApk;
    Button buttonInstallUserInterfaceApk;

    View viewGatewayInstallDone;
    View viewUserInterfaceInstallDone;

    private Button buttonUpdatesComplete;

    String patient_gateway_software_revision;
    String user_interface_software_revision;

    UpdateModeStatus update_mode_status;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.update_mode, container, false); // Inflate the layout for this fragment

        labelPatientGateway = v.findViewById(R.id.labelPatientGateway);
        labelUserInterface = v.findViewById(R.id.labelUserInterface);

        textAvailableGatewayVersion = v.findViewById(R.id.textAvailablePatientGatewayVersion);
        textAvailableUserInterfaceVersion = v.findViewById(R.id.textAvailableUserInterfaceVersion);

        textPatientGatewaySoftwareVersion = v.findViewById(R.id.textCurrentPatientGatewaySoftwareVersion);
        textUserInterfaceSoftwareVersion = v.findViewById(R.id.textCurrentUserInterfaceSoftwareVersion);

        buttonInstallGatewayApk = v.findViewById(R.id.buttonInstallGatewayApk);
        buttonInstallUserInterfaceApk = v.findViewById(R.id.buttonInstallUserInterfaceApk);

        viewGatewayInstallDone = v.findViewById(R.id.viewGatewayInstallDone);
        viewUserInterfaceInstallDone = v.findViewById(R.id.viewUserInterfaceInstallDone);

        buttonUpdatesComplete = v.findViewById(R.id.buttonUpdatesComplete);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            update_mode_status = bundle.getParcelable((UPDATE_MODE_STATUS));
            Log.d(TAG, "update_mode_status - " + update_mode_status.apk_install_status);
        }

        patient_gateway_software_revision = main_activity_interface.getPatientGatewaySoftwareVersionNumber();
        user_interface_software_revision = main_activity_interface.getUserInterfaceSoftwareVersionNumber();

        textPatientGatewaySoftwareVersion.setText(patient_gateway_software_revision);
        textUserInterfaceSoftwareVersion.setText(user_interface_software_revision);

        textAvailableGatewayVersion.setText(String.format(Locale.getDefault(),"%d", update_mode_status.available_gateway_version));
        textAvailableUserInterfaceVersion.setText(String.format(Locale.getDefault(),"%d", update_mode_status.available_ui_version));

        Log.e(TAG, "install_status = " + update_mode_status.apk_install_status);

        switch(update_mode_status.apk_install_status)
        {
            // Something has gone wrong
            case FAILED:
            {
                installationFailed();
            }
            break;

            // User pressed the Cancel button on the onscreen Android popup
            case ABORTED:
            {
                resetGui();
            }
            break;

            case INSTALLED_RESTART_GATEWAY:
            {
                // Restart Patient Gateway
                main_activity_interface.restartGatewayApp();

                update_mode_status.apk_install_status = UpdateModeStatus.ApkInstallStatus.NONE;
            }
            break;

            case NONE:
            default:
                break;
        }

        if(!gatewayUpToDate())
        {
            Log.d(TAG, "Gateway not up to date. App = " + patient_gateway_software_revision + " and available = " + update_mode_status.available_gateway_version);

            // Set GW button active
            if(update_mode_status.isGatewayInstallationInProgress())
            {
                Log.d(TAG, "Gateway update in progress");

                // Gateway update running...
                setInProgress(buttonInstallGatewayApk);
            }
            else
            {
                Log.d(TAG, "Gateway update NOT in progress");

                buttonInstallGatewayApk.setText(R.string.textInstallSoftwareUpdateNow);
                setActive(buttonInstallGatewayApk);
            }

            // Set other buttons inactive
            setInactive(buttonInstallUserInterfaceApk);
            setInactive(buttonUpdatesComplete);

            labelPatientGateway.setTextSize(ACTIVE_TEXT_SIZE);

            labelUserInterface.setTextSize(INACTIVE_TEXT_SIZE);
        }
        else if(!userInterfaceUpToDate())
        {
            Log.d(TAG, "UI not up to date. App = " + user_interface_software_revision + " and available = " + update_mode_status.available_ui_version);

            // Set UI button active
            if(update_mode_status.isUserInterfaceInstallationInProgress())
            {
                Log.d(TAG, "UI update in progress");

                // UI update running...
                setInProgress(buttonInstallUserInterfaceApk);
            }
            else
            {
                Log.d(TAG, "UI update NOT in progress");

                buttonInstallUserInterfaceApk.setText(R.string.textInstallSoftwareUpdateNow);
                setActive(buttonInstallUserInterfaceApk);
            }

            // set other buttons inactive
            setInactive(buttonInstallGatewayApk);

            viewGatewayInstallDone.setVisibility(VISIBLE);

            setInactive(buttonUpdatesComplete);

            labelPatientGateway.setTextSize(INACTIVE_TEXT_SIZE);

            labelUserInterface.setTextSize(ACTIVE_TEXT_SIZE);
        }
        else if(updatesComplete())
        {
            update_mode_status.setInstallationNotInProgress();

            // all apps must be up to date, so set the Update Complete button active
            setActive(buttonUpdatesComplete);

            // set other buttons inactive
            setInactive(buttonInstallGatewayApk);
            viewGatewayInstallDone.setVisibility(VISIBLE);

            setInactive(buttonInstallUserInterfaceApk);
            viewUserInterfaceInstallDone.setVisibility(VISIBLE);

            labelPatientGateway.setTextSize(INACTIVE_TEXT_SIZE);

            labelUserInterface.setTextSize(INACTIVE_TEXT_SIZE);
        }
    }


    private boolean updatesComplete()
    {
        return (gatewayUpToDate() && userInterfaceUpToDate());
    }


    private boolean gatewayUpToDate()
    {
        return (Integer.parseInt(patient_gateway_software_revision) >= update_mode_status.available_gateway_version);
    }


    private boolean userInterfaceUpToDate()
    {
        return (Integer.parseInt(user_interface_software_revision) >= update_mode_status.available_ui_version);
    }


    private void setActive(Button button)
    {
        button.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));

        button.setOnClickListener(this);

        button.setClickable(true);

        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_blue, null));

        button.setVisibility(VISIBLE);
    }


    private void setInProgress(Button button)
    {
        button.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));

        button.setOnClickListener(null);

        button.setClickable(false);

        button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_gray, null));

        button.setText(R.string.please_wait);

        button.setVisibility(VISIBLE);
    }


    private void setInactive(Button button)
    {
        button.clearAnimation();

        button.setOnClickListener(null);

        button.setVisibility(GONE);
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonInstallGatewayApk)
        {
            setInProgress(buttonInstallGatewayApk);

            if (!main_activity_interface.installGatewayApk())
            {
                installationFailed();
            }
        }
        else if (id == R.id.buttonInstallUserInterfaceApk)
        {
            setInProgress(buttonInstallUserInterfaceApk);

            if (!main_activity_interface.installUserInterfaceApk())
            {
                installationFailed();
            }
        }
        else if (id == R.id.buttonUpdatesComplete)
        {
            main_activity_interface.softwareUpdateComplete();
        }
    }

    public void installationFailed()
    {
        main_activity_interface.showOnScreenMessage(getResources().getString(R.string.update_install_failed_onscreen_text));

        resetGui();
    }

    public void resetGui()
    {
        if (update_mode_status.isGatewayInstallationInProgress())
        {
            buttonInstallGatewayApk.setText(R.string.textInstallSoftwareUpdateNow);
            setActive(buttonInstallGatewayApk);
        }
        else if (update_mode_status.isUserInterfaceInstallationInProgress())
        {
            buttonInstallUserInterfaceApk.setText(R.string.textInstallSoftwareUpdateNow);
            setActive(buttonInstallUserInterfaceApk);
        }

        update_mode_status.setInstallationNotInProgress();
    }
}
