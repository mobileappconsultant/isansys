package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableRow;
import android.widget.TextView;

import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementBloodPressure;
import com.isansys.common.measurements.MeasurementEarlyWarningScore;
import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.MeasurementWeight;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.isansys.common.DeviceInfoConstants.INVALID_HUMAN_READABLE_DEVICE_ID;

public class FragmentDummyDataMode extends FragmentIsansys implements OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    private Button buttonCreateNewPatient;
    private TextView textHospitalPatientId;

    private Button buttonAddLifetouch;
    private Button buttonRemoveLifetouch;
    private Button buttonLifetouchSimulateConnectionState;
    private CheckBox checkBoxLifetouchLeadsOff;

    private Button buttonAddLifetemp;
    private Button buttonRemoveLifetemp;
    private Button buttonLifetempSimulateConnectionState;
    private CheckBox checkBoxLifetempLeadsOff;
    
    private Button buttonAddNoninWristOx;
    private Button buttonRemoveNoninWristOx;
    private Button buttonNoninWristOxSimulateConnectionState;
    private CheckBox checkBoxNoninWristOxFingerOff;

    private Button buttonAddNoninWristOxBle;
    private Button buttonRemoveNoninWristOxBle;
    private Button buttonNoninWristOxBleSimulateConnectionState;
    private CheckBox checkBoxNoninWristOxBleFingerOff;

    private Button buttonAddBloodPressure;
    private Button buttonRemoveBloodPressure;
    private Button buttonBloodPressureSimulateConnectionState;

    private Button buttonAddWeightScale;
    private Button buttonRemoveWeightScale;
    private Button buttonWeightScaleSimulateConnectionState;

    private Button buttonAddEarlyWarningScore;
    private Button buttonRemoveEarlyWarningScore;
    private CheckBox checkBoxSpoofEarlyWarningScores;
    
    private Button buttonStartPatientSession;
    private Button buttonStopPatientSession;

    private CheckBox checkBoxSpoofBackfillSession;
    private SeekBar seekBarNumberOfHoursToBackfill;
    private TextView textViewNumberOfHoursToBackfillDescription;
    private TextView textViewNumberOfHoursToBackfill;

    private RadioGroup radioGroupPatientAge;

    private TableRow tableRowPatientDetails;
    private TableRow tableRowLifetouch;
    private TableRow tableRowLifetemp;
    private TableRow tableRowNoninWristOx;
    private TableRow tableRowNoninWristOxBle;
    private TableRow tableRowBloodPressure;
    private TableRow tableRowWeightScale;
    private TableRow tableRowEarlyWarningScores;
    private TableRow tableRowPatientSession;
    
    private TextView textHeartRateMeasurement;
    private TextView textViewRespirationRateMeasurement;
    private TextView textHeartAndRespirationRateMeasurementTimestamp;

    private TextView textTemperatureMeasurement;
    private TextView textTemperatureMeasurementTimestamp;

    private TextView textNoninWristOxMeasurement;
    private TextView textNoninWristOxMeasurementTimestamp;

    private TextView textNoninWristOxBleMeasurement;
    private TextView textNoninWristOxBleMeasurementTimestamp;

    private TextView textBloodPressureSystolicMeasurement;
    private TextView textBloodPressureDiastolicMeasurement;
    private TextView textBloodPressureMeasurementTimestamp;

    private TextView textWeightScaleWeightMeasurement;
    private TextView textWeightScaleWeightMeasurementTimestamp;

    private TextView textEarlyWarningScoreMeasurement;
    private TextView textEarlyWarningScoreOutOf;
    private TextView textEarlyWarningScoreMeasurementTimestamp;
    
    private SeekBar seekBarDummyDataModeMeasurementPerTick;
    private TextView textDummyDataModeMeasurementPerTick;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dummy_data_mode, container, false); // Inflate the layout for this fragment

        radioGroupPatientAge = v.findViewById(R.id.radioGroupPatientAge);
        radioGroupPatientAge.setOnCheckedChangeListener((group, checkedId) -> {
            int radioButtonID = group.getCheckedRadioButtonId();
            View radioButton = group.findViewById(radioButtonID);
            int index = group.indexOfChild(radioButton);

            // Check to ensure a radio button is actually checked before referencing it by its index. Otherwise -1 is pass into the function
            if (index >= 0)
            {
                main_activity_interface.PatientAgeSelected(index);
            }

            enableAgeRangeRadioButtons(false);

            tableRowPatientDetails.setVisibility(View.VISIBLE);
        });


        ArrayList<ThresholdSet> thresholdSets = main_activity_interface.getEarlyWarningScoreThresholdSets();

        // Create Radio Buttons for each of the Threshold Set Age Blocks
        for (ThresholdSet thresholdSet : thresholdSets)
        {
            for (ThresholdSetAgeBlockDetail age_block_detail : thresholdSet.list_threshold_set_age_block_detail)
            {
                // Create Radio Button
                RadioButton radioButton = new RadioButton(getContext());
                // Width = 0. Height = MATCH_PARENT. Weight = 1
                radioButton.setLayoutParams(new RadioGroup.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                radioButton.setButtonDrawable(R.drawable.button_radio);
                radioButton.setText(age_block_detail.display_name);
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                radioButton.setGravity(Gravity.CENTER);

                // Add Radio Button to Radio Group
                radioGroupPatientAge.addView(radioButton);
            }
        }

        tableRowPatientDetails = v.findViewById(R.id.tableRowPatientDetails);

        tableRowLifetouch = v.findViewById(R.id.tableRowLifetouch);
        tableRowLifetemp = v.findViewById(R.id.tableRowLifetemp);
        tableRowNoninWristOx = v.findViewById(R.id.tableRowPulseOx);
        tableRowNoninWristOxBle = v.findViewById(R.id.tableRowNoninWristOxBle);
        tableRowBloodPressure = v.findViewById(R.id.tableRowBloodPressure);
        tableRowWeightScale = v.findViewById(R.id.tableRowWeightScale);
        tableRowEarlyWarningScores = v.findViewById(R.id.tableRowEarlyWarningScores);
        tableRowPatientSession = v.findViewById(R.id.tableRowPatientSession);

        buttonCreateNewPatient = v.findViewById(R.id.buttonCreateNewPatient);
        buttonCreateNewPatient.setOnClickListener(this);

        textHospitalPatientId = v.findViewById(R.id.textCreatedPatientName);

        buttonAddLifetouch = v.findViewById(R.id.buttonAddLifetouch);
        buttonAddLifetouch.setOnClickListener(this);
        buttonRemoveLifetouch = v.findViewById(R.id.buttonRemoveLifetouch);
        buttonRemoveLifetouch.setOnClickListener(this);

        textHeartRateMeasurement = v.findViewById(R.id.textHeartRateMeasurement);
        textViewRespirationRateMeasurement = v.findViewById(R.id.textViewRespirationRateMeasurement);
        textHeartAndRespirationRateMeasurementTimestamp = v.findViewById(R.id.textHeartAndRespirationRateMeasurementTimestamp);

        textTemperatureMeasurement = v.findViewById(R.id.textTemperatureMeasurement);
        textTemperatureMeasurementTimestamp = v.findViewById(R.id.textTemperatureMeasurementTimestamp);

        textNoninWristOxMeasurement = v.findViewById(R.id.textPulseOxMeasurement);
        textNoninWristOxMeasurementTimestamp = v.findViewById(R.id.textPulseOxMeasurementTimestamp);

        textNoninWristOxBleMeasurement = v.findViewById(R.id.textNoninWristOxBleMeasurement);
        textNoninWristOxBleMeasurementTimestamp = v.findViewById(R.id.textNoninWristOxBleMeasurementTimestamp);

        textBloodPressureSystolicMeasurement = v.findViewById(R.id.textBloodPressureSystolicMeasurement);
        textBloodPressureDiastolicMeasurement = v.findViewById(R.id.textBloodPressureDiastolicMeasurement);
        textBloodPressureMeasurementTimestamp = v.findViewById(R.id.textBloodPressureMeasurementTimestamp);

        textWeightScaleWeightMeasurement = v.findViewById(R.id.textWeightScaleWeightMeasurement);
        textWeightScaleWeightMeasurementTimestamp = v.findViewById(R.id.textWeightScaleWeightMeasurementTimestamp);

        textEarlyWarningScoreMeasurement = v.findViewById(R.id.textEarlyWarningScoresMeasurement);
        textEarlyWarningScoreOutOf = v.findViewById(R.id.textEarlyWarningScoresOutOf);
        textEarlyWarningScoreMeasurementTimestamp = v.findViewById(R.id.textEarlyWarningScoresMeasurementTimestamp);

        buttonLifetouchSimulateConnectionState = v.findViewById(R.id.buttonLifetouchSimulateConnectionState);
        buttonLifetouchSimulateConnectionState.setOnClickListener(x -> {
            if (main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__LIFETOUCH))
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__LIFETOUCH, false);
                buttonLifetouchSimulateConnectionState.setText(R.string.textConnect);
            }
            else
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__LIFETOUCH, true);
                buttonLifetouchSimulateConnectionState.setText(R.string.textDisconnect);
            }
        });
        
        checkBoxLifetouchLeadsOff = v.findViewById(R.id.checkBoxLifetouchLeadsOff);
        checkBoxLifetouchLeadsOff.setOnClickListener(x -> main_activity_interface.simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__LIFETOUCH, checkBoxLifetouchLeadsOff.isChecked()));


        buttonAddLifetemp = v.findViewById(R.id.buttonAddLifetemp);
        buttonAddLifetemp.setOnClickListener(this);
        buttonRemoveLifetemp = v.findViewById(R.id.buttonRemoveLifetemp);
        buttonRemoveLifetemp.setOnClickListener(this);

        buttonLifetempSimulateConnectionState = v.findViewById(R.id.buttonLifetempSimulateConnectionState);
        buttonLifetempSimulateConnectionState.setOnClickListener(x -> {
            if (main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__TEMPERATURE))
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__TEMPERATURE, false);
                buttonLifetempSimulateConnectionState.setText(R.string.textConnect);
            }
            else
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__TEMPERATURE, true);
                buttonLifetempSimulateConnectionState.setText(R.string.textDisconnect);
            }
        });

        checkBoxLifetempLeadsOff = v.findViewById(R.id.checkBoxLifetempLeadsOff);
        checkBoxLifetempLeadsOff.setOnClickListener(x -> main_activity_interface.simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__TEMPERATURE, checkBoxLifetempLeadsOff.isChecked()));


        buttonAddNoninWristOx = v.findViewById(R.id.buttonAddPulseOx);
        buttonAddNoninWristOx.setOnClickListener(this);
        buttonRemoveNoninWristOx = v.findViewById(R.id.buttonRemoveNoninWristOx);
        buttonRemoveNoninWristOx.setOnClickListener(this);

        buttonNoninWristOxSimulateConnectionState = v.findViewById(R.id.buttonNoninWristOxSimulateConnectionState);
        buttonNoninWristOxSimulateConnectionState.setOnClickListener(x -> {
            if (main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__SPO2))
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__SPO2, false);
                buttonNoninWristOxSimulateConnectionState.setText(R.string.textConnect);
            }
            else
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__SPO2, true);
                buttonNoninWristOxSimulateConnectionState.setText(R.string.textDisconnect);
            }
        });

        checkBoxNoninWristOxFingerOff = v.findViewById(R.id.checkBoxPulseOxFingerOff);
        checkBoxNoninWristOxFingerOff.setOnClickListener(x -> main_activity_interface.simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__SPO2, checkBoxNoninWristOxFingerOff.isChecked()));


        buttonAddNoninWristOxBle = v.findViewById(R.id.buttonAddNoninWristOxBle);
        buttonAddNoninWristOxBle.setOnClickListener(this);
        buttonRemoveNoninWristOxBle = v.findViewById(R.id.buttonRemoveNoninWristOxBle);
        buttonRemoveNoninWristOxBle.setOnClickListener(this);

        buttonNoninWristOxBleSimulateConnectionState = v.findViewById(R.id.buttonNoninWristOxBleSimulateConnectionState);
        buttonNoninWristOxBleSimulateConnectionState.setOnClickListener(x -> {
            if (main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__SPO2))
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__SPO2, false);
                buttonNoninWristOxBleSimulateConnectionState.setText(R.string.textConnect);
            }
            else
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__SPO2, true);
                buttonNoninWristOxBleSimulateConnectionState.setText(R.string.textDisconnect);
            }
        });

        checkBoxNoninWristOxBleFingerOff = v.findViewById(R.id.checkBoxNoninWristOxBleFingerOff);
        checkBoxNoninWristOxBleFingerOff.setOnClickListener(x -> main_activity_interface.simulateDeviceLeadsOff(SensorType.SENSOR_TYPE__SPO2, checkBoxNoninWristOxBleFingerOff.isChecked()));


        buttonAddBloodPressure = v.findViewById(R.id.buttonAddBloodPressure);
        buttonAddBloodPressure.setOnClickListener(this);
        buttonRemoveBloodPressure = v.findViewById(R.id.buttonRemoveBloodPressure);
        buttonRemoveBloodPressure.setOnClickListener(this);

        buttonBloodPressureSimulateConnectionState = v.findViewById(R.id.buttonBloodPressureSimulateConnectionState);
        buttonBloodPressureSimulateConnectionState.setOnClickListener(x -> {
            if (main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE))
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, false);
                buttonBloodPressureSimulateConnectionState.setText(R.string.textConnect);
            }
            else
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, true);
                buttonBloodPressureSimulateConnectionState.setText(R.string.textDisconnect);
            }
        });

        buttonAddWeightScale = v.findViewById(R.id.buttonAddWeightScale);
        buttonAddWeightScale.setOnClickListener(this);
        buttonRemoveWeightScale = v.findViewById(R.id.buttonRemoveWeightScale);
        buttonRemoveWeightScale.setOnClickListener(this);
        buttonWeightScaleSimulateConnectionState = v.findViewById(R.id.buttonWeightScaleSimulateConnectionState);
        buttonWeightScaleSimulateConnectionState.setOnClickListener(x -> {
            if (main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__WEIGHT_SCALE))
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__WEIGHT_SCALE, false);
                buttonWeightScaleSimulateConnectionState.setText(R.string.textConnect);
            }
            else
            {
                main_activity_interface.simulateDeviceConnectionEvent(SensorType.SENSOR_TYPE__WEIGHT_SCALE, true);
                buttonWeightScaleSimulateConnectionState.setText(R.string.textDisconnect);
            }
        });

        buttonAddEarlyWarningScore = v.findViewById(R.id.buttonAddDummyEarlyWarningScores);
        buttonAddEarlyWarningScore.setOnClickListener(this);
        buttonRemoveEarlyWarningScore = v.findViewById(R.id.buttonRemoveDummyEarlyWarningScores);
        buttonRemoveEarlyWarningScore.setOnClickListener(this);

        checkBoxSpoofEarlyWarningScores = v.findViewById(R.id.checkBoxSpoofEarlyWarningScores);
        checkBoxSpoofEarlyWarningScores.setOnClickListener(x -> main_activity_interface.spoofEarlyWarningScores(checkBoxSpoofEarlyWarningScores.isChecked()));
        
        
        buttonStartPatientSession = v.findViewById(R.id.buttonStartPatientSession);
        buttonStartPatientSession.setOnClickListener(this);
        buttonStopPatientSession = v.findViewById(R.id.buttonStopPatientSession);
        buttonStopPatientSession.setOnClickListener(this);

        checkBoxSpoofBackfillSession = v.findViewById(R.id.checkBoxSpoofBackfillSession);
        checkBoxSpoofBackfillSession.setOnClickListener(x -> {
            if(checkBoxSpoofBackfillSession.isChecked())
            {
                textViewNumberOfHoursToBackfillDescription.setVisibility(View.VISIBLE);
                textViewNumberOfHoursToBackfill.setVisibility(View.VISIBLE);
                seekBarNumberOfHoursToBackfill.setVisibility(View.VISIBLE);

                main_activity_interface.enableDummyDataModeBackfillSessionWithData(true);
            }
            else
            {
                textViewNumberOfHoursToBackfillDescription.setVisibility(View.INVISIBLE);
                textViewNumberOfHoursToBackfill.setVisibility(View.INVISIBLE);
                seekBarNumberOfHoursToBackfill.setVisibility(View.INVISIBLE);

                main_activity_interface.enableDummyDataModeBackfillSessionWithData(false);
            }
        });

        textViewNumberOfHoursToBackfillDescription = v.findViewById(R.id.textViewNumberOfHoursToBackfillDescription);
        textViewNumberOfHoursToBackfill = v.findViewById(R.id.textViewNumberOfHoursToBackfill);

        seekBarNumberOfHoursToBackfill = v.findViewById(R.id.seekBarNumberOfHoursToBackfill);
        seekBarNumberOfHoursToBackfill.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                progressValue++;                                                         // +1 to make it 1 to 10 instead of 0 to 9
                main_activity_interface.setDummyDataModeNumberOfHoursToBackfill(progressValue);
                textViewNumberOfHoursToBackfill.setText(String.valueOf(progressValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // Nothing to do
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // Nothing to do
            }
        });

        textDummyDataModeMeasurementPerTick = v.findViewById(R.id.textViewDummyDataModeMeasurementPerTick);

        seekBarDummyDataModeMeasurementPerTick = v.findViewById(R.id.seekBarDummyDataModeMeasurementPerTick);
        seekBarDummyDataModeMeasurementPerTick.setMax(9);

        seekBarDummyDataModeMeasurementPerTick.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser)
            {
                progressValue++;                                                         // +1 to make it 1 to 10 instead of 0 to 9
                main_activity_interface.setDummyDataModeNumberOfMeasurementsPerTick(progressValue);
                textDummyDataModeMeasurementPerTick.setText(String.valueOf(progressValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // Nothing to do
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // Nothing to do
            }
        });

        resetButtons();

        return v;
    }


    @Override
    public void onPause()
    {
        if (!main_activity_interface.isSessionInProgress())
        {
            // If we are closing down the Dummy Data Session without having a session running, then reset the Patient so we do not have the Hospital Patient ID in the Header.
            main_activity_interface.resetPatientInfo();
        }

        super.onPause();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        main_activity_interface.getDummyDataModeDeviceLeadsOffEnabledStatus(SensorType.SENSOR_TYPE__LIFETOUCH);
        main_activity_interface.getDummyDataModeDeviceLeadsOffEnabledStatus(SensorType.SENSOR_TYPE__TEMPERATURE);
        main_activity_interface.getDummyDataModeDeviceLeadsOffEnabledStatus(SensorType.SENSOR_TYPE__SPO2);

        main_activity_interface.getDummyDataModeSpoofEarlyWarningScores();

        main_activity_interface.getAllDeviceInfosFromGateway();

        main_activity_interface.getDummyDataModeNumberOfMeasurementsPerTick();

        String hospital_patient_id = main_activity_interface.getPatientInfo().getHospitalPatientId();

        setupPatientAgeCheckboxes(main_activity_interface.getPatientInfo().getThresholdSetAgeBlockDetails());

        radioGroupPatientAge.setVisibility(View.VISIBLE);

        if (main_activity_interface.isSessionInProgress())
        {
            tableRowPatientDetails.setVisibility(View.VISIBLE);

            enableAgeRangeRadioButtons(false);

            if (!hospital_patient_id.equals(""))
            {
                Log.e(TAG, "hospital_patient_id = " + hospital_patient_id);
                textHospitalPatientId.setText(hospital_patient_id);
                buttonCreateNewPatient.setVisibility(View.INVISIBLE);

                tableRowLifetouch.setVisibility(View.VISIBLE);
                tableRowLifetemp.setVisibility(View.VISIBLE);
                tableRowNoninWristOx.setVisibility(View.VISIBLE);
                tableRowNoninWristOxBle.setVisibility(View.VISIBLE);
                tableRowBloodPressure.setVisibility(View.VISIBLE);
                tableRowWeightScale.setVisibility(View.VISIBLE);
                tableRowEarlyWarningScores.setVisibility(View.VISIBLE);
            }
            else
            {
                Log.e(TAG, "hospital_patient_id = blank");
                buttonCreateNewPatient.setVisibility(View.VISIBLE);

                tableRowLifetouch.setVisibility(View.INVISIBLE);
                tableRowLifetemp.setVisibility(View.INVISIBLE);
                tableRowNoninWristOx.setVisibility(View.INVISIBLE);
                tableRowNoninWristOxBle.setVisibility(View.INVISIBLE);
                tableRowBloodPressure.setVisibility(View.INVISIBLE);
                tableRowWeightScale.setVisibility(View.INVISIBLE);
                tableRowEarlyWarningScores.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            // No session in progress.
            buttonCreateNewPatient.setVisibility(View.VISIBLE);
        }

        showStartStopPatientSessionButton();
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonCreateNewPatient)
        {
            buttonCreateNewPatientClick();
        }
        else if (id == R.id.buttonAddLifetouch)
        {
            buttonAddLifetouchClick(DeviceType.DEVICE_TYPE__LIFETOUCH);
        }
        else if (id == R.id.buttonAddLifetemp)
        {
            buttonAddLifetempClick(DeviceType.DEVICE_TYPE__LIFETEMP_V2);
        }
        else if (id == R.id.buttonAddPulseOx)
        {
            buttonAddPulseOxClick(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);
        }
        else if (id == R.id.buttonAddNoninWristOxBle)
        {
            buttonAddPulseOxClick(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE);
        }
        else if (id == R.id.buttonAddBloodPressure)
        {
            buttonAddBloodPressureClick(DeviceType.DEVICE_TYPE__AND_UA767);
        }
        else if (id == R.id.buttonAddWeightScale)
        {
            buttonAddWeightScaleClick(DeviceType.DEVICE_TYPE__AND_UC352BLE);
        }
        else if (id == R.id.buttonAddDummyEarlyWarningScores)
        {
            buttonAddEarlyWarningScoreClick();
        }
        else if (id == R.id.buttonRemoveLifetouch)
        {
            buttonRemoveLifetouchClick();
        }
        else if (id == R.id.buttonRemoveLifetemp)
        {
            buttonRemoveLifetempClick();
        }
        else if ((id == R.id.buttonRemoveNoninWristOx) || (id == R.id.buttonRemoveNoninWristOxBle))
        {
            buttonRemovePulseOxClick();
        }
        else if (id == R.id.buttonRemoveBloodPressure)
        {
            buttonRemoveBloodPressureClick();
        }
        else if (id == R.id.buttonRemoveWeightScale)
        {
            buttonRemoveWeightScaleClick();
        }
        else if (id == R.id.buttonRemoveDummyEarlyWarningScores)
        {
            buttonRemoveEarlyWarningScoreClick();
        }
        else if (id == R.id.buttonStartPatientSession)
        {
            buttonStartPatientSessionClick();
        }
        else if (id == R.id.buttonStopPatientSession)
        {
            buttonStopPatientSessionClick();
        }
    }
    
    
    public void showStartStopPatientSessionButton()
    {
        if (main_activity_interface.getNumberOfDevicesToShow() > 0)
        {
            if (main_activity_interface.isSessionInProgress())
            {
                buttonStartPatientSession.setVisibility(View.INVISIBLE);
                buttonStopPatientSession.setVisibility(View.VISIBLE);

                showBackfillControls(false);
            }
            else
            {
                buttonStartPatientSession.setVisibility(View.VISIBLE);
                buttonStopPatientSession.setVisibility(View.INVISIBLE);

                showBackfillControls(true);
            }
        }
        else
        {
            buttonStartPatientSession.setVisibility(View.INVISIBLE);
            buttonStopPatientSession.setVisibility(View.INVISIBLE);

            showBackfillControls(false);
        }
    }


    private void showBackfillControls(boolean show)
    {
        if (show)
        {
            checkBoxSpoofBackfillSession.setVisibility(View.VISIBLE);
        }
        else
        {
            checkBoxSpoofBackfillSession.setVisibility(View.INVISIBLE);
            textViewNumberOfHoursToBackfillDescription.setVisibility(View.INVISIBLE);
            textViewNumberOfHoursToBackfill.setVisibility(View.INVISIBLE);
            seekBarNumberOfHoursToBackfill.setVisibility(View.INVISIBLE);
        }
    }


    private void buttonStartPatientSessionClick()
    {
        main_activity_interface.createNewSession();

        showBackfillControls(false);

        buttonStartPatientSession.setVisibility(View.INVISIBLE);
        buttonStopPatientSession.setVisibility(View.VISIBLE);
    }

    
    private void buttonStopPatientSessionClick()
    {
        main_activity_interface.endSessionPressed(main_activity_interface.getNtpTimeNowInMilliseconds());
        
        resetButtons();
    }
    
    
    private void resetButtons()
    {
        radioGroupPatientAge.clearCheck();
        enableAgeRangeRadioButtons(true);

        radioGroupPatientAge.setVisibility(View.VISIBLE);

        tableRowPatientDetails.setVisibility(View.INVISIBLE);
        buttonCreateNewPatient.setVisibility(View.VISIBLE);

        tableRowLifetouch.setVisibility(View.INVISIBLE);
        tableRowLifetemp.setVisibility(View.INVISIBLE);
        tableRowNoninWristOx.setVisibility(View.INVISIBLE);
        tableRowNoninWristOxBle.setVisibility(View.INVISIBLE);
        tableRowBloodPressure.setVisibility(View.INVISIBLE);
        tableRowWeightScale.setVisibility(View.INVISIBLE);
        tableRowEarlyWarningScores.setVisibility(View.INVISIBLE);
        tableRowPatientSession.setVisibility(View.VISIBLE);

        textHeartRateMeasurement.setVisibility(View.INVISIBLE);
        textViewRespirationRateMeasurement.setVisibility(View.INVISIBLE);
        textHeartAndRespirationRateMeasurementTimestamp.setVisibility(View.INVISIBLE);

        textTemperatureMeasurement.setVisibility(View.INVISIBLE);
        textTemperatureMeasurementTimestamp.setVisibility(View.INVISIBLE);

        textNoninWristOxMeasurement.setVisibility(View.INVISIBLE);
        textNoninWristOxMeasurementTimestamp.setVisibility(View.INVISIBLE);

        textNoninWristOxBleMeasurement.setVisibility(View.INVISIBLE);
        textNoninWristOxBleMeasurementTimestamp.setVisibility(View.INVISIBLE);

        textBloodPressureSystolicMeasurement.setVisibility(View.INVISIBLE);
        textBloodPressureDiastolicMeasurement.setVisibility(View.INVISIBLE);
        textBloodPressureMeasurementTimestamp.setVisibility(View.INVISIBLE);

        textWeightScaleWeightMeasurement.setVisibility(View.INVISIBLE);
        textWeightScaleWeightMeasurementTimestamp.setVisibility(View.INVISIBLE);

        textEarlyWarningScoreMeasurement.setVisibility(View.INVISIBLE);
        textEarlyWarningScoreOutOf.setVisibility(View.INVISIBLE);
        textEarlyWarningScoreMeasurementTimestamp.setVisibility(View.INVISIBLE);

        buttonRemoveLifetouch.setVisibility(View.INVISIBLE);
        buttonLifetouchSimulateConnectionState.setVisibility(View.INVISIBLE);
        checkBoxLifetouchLeadsOff.setVisibility(View.INVISIBLE);

        buttonRemoveLifetemp.setVisibility(View.INVISIBLE);
        buttonLifetempSimulateConnectionState.setVisibility(View.INVISIBLE);
        checkBoxLifetempLeadsOff.setVisibility(View.INVISIBLE);

        buttonRemoveNoninWristOx.setVisibility(View.INVISIBLE);
        buttonNoninWristOxSimulateConnectionState.setVisibility(View.INVISIBLE);
        checkBoxNoninWristOxFingerOff.setVisibility(View.INVISIBLE);

        buttonRemoveNoninWristOxBle.setVisibility(View.INVISIBLE);
        buttonNoninWristOxBleSimulateConnectionState.setVisibility(View.INVISIBLE);
        checkBoxNoninWristOxBleFingerOff.setVisibility(View.INVISIBLE);

        buttonRemoveBloodPressure.setVisibility(View.INVISIBLE);
        buttonBloodPressureSimulateConnectionState.setVisibility(View.INVISIBLE);

        buttonRemoveWeightScale.setVisibility(View.INVISIBLE);
        buttonWeightScaleSimulateConnectionState.setVisibility(View.INVISIBLE);

        buttonRemoveEarlyWarningScore.setVisibility(View.INVISIBLE);
        checkBoxSpoofEarlyWarningScores.setVisibility(View.INVISIBLE);

        buttonStartPatientSession.setVisibility(View.INVISIBLE);
        buttonCreateNewPatient.setVisibility(View.VISIBLE);

        buttonStopPatientSession.setVisibility(View.INVISIBLE);

        checkBoxSpoofBackfillSession.setVisibility(View.INVISIBLE);
        seekBarNumberOfHoursToBackfill.setVisibility(View.INVISIBLE);


        textHospitalPatientId.setText(R.string.blank_string);

        showStartStopPatientSessionButton();
    }


    private void enableAgeRangeRadioButtons(boolean enabled)
    {
        for (int i = 0; i < radioGroupPatientAge.getChildCount(); i++) {
            radioGroupPatientAge.getChildAt(i).setEnabled(enabled);
        }
    }
    
    
    private void buttonCreateNewPatientClick()
    {
        main_activity_interface.createNewDummyDataPatient();

        buttonCreateNewPatient.setVisibility(View.INVISIBLE);

        tableRowLifetouch.setVisibility(View.VISIBLE);
        tableRowLifetemp.setVisibility(View.VISIBLE);
        tableRowNoninWristOx.setVisibility(View.VISIBLE);
        tableRowNoninWristOxBle.setVisibility(View.VISIBLE);
        tableRowBloodPressure.setVisibility(View.VISIBLE);
        tableRowWeightScale.setVisibility(View.VISIBLE);
        tableRowEarlyWarningScores.setVisibility(View.VISIBLE);
    }
    
    
    private void addDeviceCommon()
    {
        if (main_activity_interface.isSessionInProgress())
        {
            system_commands.sendGatewayCommand_updateExistingSessionCommand(main_activity_interface.getGatewayUserId());
        }
    }
    

    private boolean showAddDeviceButton(boolean show_button, String button_text, long human_readable_device_id, boolean device_in_dummy_data_mode, Button buttonAddDevice, Button buttonRemoveDevice, TextView measurement_one, TextView measurement_two, TextView timestamp)
    {
        boolean radio_buttons_visible = false;

        Activity activity = getActivity();

        if (activity != null)
        {
            if (show_button)
            {
                buttonAddDevice.setVisibility(View.VISIBLE);
                buttonAddDevice.setText(button_text);

                buttonAddDevice.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_blue));
                buttonAddDevice.setOnClickListener(this);

                buttonRemoveDevice.setVisibility(View.INVISIBLE);

                if (measurement_one != null) {
                    measurement_one.setVisibility(View.INVISIBLE);
                }

                if (measurement_two != null) {
                    measurement_two.setVisibility(View.INVISIBLE);
                }

                if (timestamp != null) {
                    timestamp.setVisibility(View.INVISIBLE);
                }

                radio_buttons_visible = false;
            }
            else
            {
                String text;
                if (device_in_dummy_data_mode)
                {
                    text = getResources().getString(R.string.dummy_id) + human_readable_device_id;
                }
                else
                {
                    text = getResources().getString(R.string.real_id) + human_readable_device_id;
                }
                buttonAddDevice.setText(text);

                buttonAddDevice.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                buttonAddDevice.setOnClickListener(null);

                if (device_in_dummy_data_mode)
                {
                    buttonRemoveDevice.setVisibility(View.VISIBLE);
                }
                else
                {
                    buttonRemoveDevice.setVisibility(View.INVISIBLE);
                }

                if (measurement_one != null)
                {
                    measurement_one.setVisibility(View.VISIBLE);
                }

                if (measurement_two != null)
                {
                    measurement_two.setVisibility(View.VISIBLE);
                }

                if (timestamp != null)
                {
                    timestamp.setVisibility(View.VISIBLE);
                }

                radio_buttons_visible = device_in_dummy_data_mode;
            }
        }

        return radio_buttons_visible;
    }
    
    private void buttonAddLifetouchClick(DeviceType device_type)
    {
        main_activity_interface.addDummyLifetouch(device_type);
        
        addDeviceCommon();
    }
    private void buttonRemoveLifetouchClick()
    {
        main_activity_interface.removeDummyDevice(SensorType.SENSOR_TYPE__LIFETOUCH);
        
        showAddLifetouchButton(true, INVALID_HUMAN_READABLE_DEVICE_ID);
    }
    private void showAddLifetouchButton(boolean show_button, long human_readable_device_id)
    {
        Log.e(TAG, "showAddLifetouchButton. show_button = " + show_button + ". human_readable_device_id = " + human_readable_device_id);

        boolean radio_buttons_visible = showAddDeviceButton(show_button,
                getResources().getString(R.string.add_lifetouch),
                human_readable_device_id,
                main_activity_interface.getDeviceDummyDataModeEnableStatus(SensorType.SENSOR_TYPE__LIFETOUCH),
                buttonAddLifetouch,
                buttonRemoveLifetouch,
                textHeartRateMeasurement,
                textViewRespirationRateMeasurement,
                textHeartAndRespirationRateMeasurementTimestamp);

        int visible;

        if (radio_buttons_visible)
        {
            visible = View.VISIBLE;
        }
        else
        {
            visible = View.INVISIBLE;
        }

        if(main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__LIFETOUCH))
        {
            buttonLifetouchSimulateConnectionState.setText(R.string.textDisconnect);
        }
        else
        {
            buttonLifetouchSimulateConnectionState.setText(R.string.textConnect);
        }

        buttonLifetouchSimulateConnectionState.setVisibility(visible);

        checkBoxLifetouchLeadsOff.setVisibility(visible);
    }


    public void setDeviceLeadsOffEnableStatus(SensorType sensor_type, boolean tick_checkbox)
    {
        switch (sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                checkBoxLifetouchLeadsOff.setChecked(tick_checkbox);
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                checkBoxLifetempLeadsOff.setChecked(tick_checkbox);
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                checkBoxNoninWristOxFingerOff.setChecked(tick_checkbox);
            }
            break;
        }
    }


    public void showAddDevicesButtons(DeviceInfo device_info, boolean show_button)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                showAddLifetouchButton(show_button, device_info.human_readable_device_id);
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                showAddThermometerButton(show_button, device_info.human_readable_device_id);
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                showAddPulseOxButton(show_button, device_info);
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                showAddBloodPressureButton(show_button, device_info.human_readable_device_id);
            }
            break;

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                showAddWeightScaleButton(show_button, device_info.human_readable_device_id);
            }
            break;

            case SENSOR_TYPE__ALGORITHM:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__EARLY_WARNING_SCORE:
                    {
                        showAddEarlyWarningScoreButton(show_button, device_info.human_readable_device_id);
                    }
                    break;
                }
            }
            break;
        }
    }


    private void buttonAddLifetempClick(DeviceType device_type)
    {
        main_activity_interface.addDummyLifetemp(device_type);
        
        addDeviceCommon();
    }


    private void buttonRemoveLifetempClick()
    {
        main_activity_interface.removeDummyDevice(SensorType.SENSOR_TYPE__TEMPERATURE);

        showAddThermometerButton(true, INVALID_HUMAN_READABLE_DEVICE_ID);
    }


    private void showAddThermometerButton(boolean show_button, long human_readable_device_id)
    {
        Log.e(TAG, "showAddThermometerButton. show_button = " + show_button + ". human_readable_device_id = " + human_readable_device_id);

        boolean radio_buttons_visible = showAddDeviceButton(show_button,
                getResources().getString(R.string.add_lifetemp),
                human_readable_device_id,
                main_activity_interface.getDeviceDummyDataModeEnableStatus(SensorType.SENSOR_TYPE__TEMPERATURE),
                buttonAddLifetemp,
                buttonRemoveLifetemp,
                textTemperatureMeasurement,
                null,
                textTemperatureMeasurementTimestamp);

        int visible;

        if (radio_buttons_visible)
        {
            visible = View.VISIBLE;
        }
        else
        {
            visible = View.INVISIBLE;
        }

        if(main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__TEMPERATURE))
        {
            buttonLifetempSimulateConnectionState.setText(R.string.textDisconnect);
        }
        else
        {
            buttonLifetempSimulateConnectionState.setText(R.string.textConnect);
        }

        buttonLifetempSimulateConnectionState.setVisibility(visible);
        checkBoxLifetempLeadsOff.setVisibility(visible);
    }


    private void buttonAddPulseOxClick(DeviceType device_type)
    {
        main_activity_interface.addDummyPulseOx(device_type);
        
        addDeviceCommon();
    }


    private void buttonRemovePulseOxClick()
    {
        main_activity_interface.removeDummyDevice(SensorType.SENSOR_TYPE__SPO2);

        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__SPO2);

        showAddPulseOxButton(true, device_info);
    }


    private void showAddPulseOxButton(boolean show_button, DeviceInfo device_info)
    {
        Log.e(TAG, "showAddPulseOxButton. show_button = " + show_button + ". human_readable_device_id = " + device_info.human_readable_device_id);

        boolean radio_buttons_visible;

        switch (device_info.device_type)
        {
            case DEVICE_TYPE__NONIN_WRIST_OX:
            default:
                {
                radio_buttons_visible = showAddDeviceButton(show_button,
                        getResources().getString(R.string.add_nonin_wrist_ox),
                        device_info.human_readable_device_id,
                        main_activity_interface.getDeviceDummyDataModeEnableStatus(SensorType.SENSOR_TYPE__SPO2),
                        buttonAddNoninWristOx,
                        buttonRemoveNoninWristOx,
                        textNoninWristOxMeasurement,
                        null,
                        textNoninWristOxMeasurementTimestamp);
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
            {
                radio_buttons_visible = showAddDeviceButton(show_button,
                        getResources().getString(R.string.add_nonin_wrist_ox_ble),
                        device_info.human_readable_device_id,
                        device_info.dummy_data_mode,
                        buttonAddNoninWristOxBle,
                        buttonRemoveNoninWristOxBle,
                        textNoninWristOxBleMeasurement,
                        null,
                        textNoninWristOxBleMeasurementTimestamp);
            }
            break;
        }

        int visible;

        if (radio_buttons_visible)
        {
            visible = View.VISIBLE;
        }
        else
        {
            visible = View.INVISIBLE;
        }


        switch (device_info.device_type)
        {
            case DEVICE_TYPE__NONIN_WRIST_OX:
            default:
            {
                if(main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__SPO2))
                {
                    buttonNoninWristOxSimulateConnectionState.setText(R.string.textDisconnect);
                }
                else
                {
                    buttonNoninWristOxSimulateConnectionState.setText(R.string.textConnect);
                }
        
                buttonNoninWristOxSimulateConnectionState.setVisibility(visible);
        
                checkBoxNoninWristOxFingerOff.setVisibility(visible);
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
            {
                if(main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__SPO2))
                {
                    buttonNoninWristOxBleSimulateConnectionState.setText(R.string.textDisconnect);
                }
                else
                {
                    buttonNoninWristOxBleSimulateConnectionState.setText(R.string.textConnect);
                }

                buttonNoninWristOxBleSimulateConnectionState.setVisibility(visible);

                checkBoxNoninWristOxBleFingerOff.setVisibility(visible);
            }
            break;
        }
    }

    private void buttonAddBloodPressureClick(DeviceType device_type)
    {
        main_activity_interface.addDummyBloodPressure(device_type);
        
        addDeviceCommon();
    }


    private void buttonRemoveBloodPressureClick()
    {
        main_activity_interface.removeDummyDevice(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        
        showAddBloodPressureButton(true, INVALID_HUMAN_READABLE_DEVICE_ID);
    }


    private void showAddBloodPressureButton(boolean show_button, long human_readable_device_id)
    {
        Log.e(TAG, "showAddBloodPressureButton. show_button = " + show_button + ". human_readable_device_id = " + human_readable_device_id);

        boolean radio_buttons_visible = showAddDeviceButton(show_button,
                getResources().getString(R.string.add_blood_pressure),
                human_readable_device_id,
                main_activity_interface.getDeviceDummyDataModeEnableStatus(SensorType.SENSOR_TYPE__BLOOD_PRESSURE),
                buttonAddBloodPressure,
                buttonRemoveBloodPressure,
                textBloodPressureSystolicMeasurement,
                textBloodPressureDiastolicMeasurement,
                textBloodPressureMeasurementTimestamp);

        int visible;

        if (radio_buttons_visible)
        {
            visible = View.VISIBLE;
        }
        else
        {
            visible = View.INVISIBLE;
        }

        if(main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE))
        {
            buttonBloodPressureSimulateConnectionState.setText(R.string.textDisconnect);
        }
        else
        {
            buttonBloodPressureSimulateConnectionState.setText(R.string.textConnect);
        }

        buttonBloodPressureSimulateConnectionState.setVisibility(visible);
    }


    private void buttonAddWeightScaleClick(DeviceType device_type)
    {
        main_activity_interface.addDummyWeightScale(device_type);

        addDeviceCommon();
    }


    private void buttonRemoveWeightScaleClick()
    {
        main_activity_interface.removeDummyDevice(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

        showAddWeightScaleButton(true, INVALID_HUMAN_READABLE_DEVICE_ID);
    }


    private void showAddWeightScaleButton(boolean show_button, long human_readable_device_id)
    {
        Log.e(TAG, "showAddWeightScaleButton. show_button = " + show_button + ". human_readable_device_id = " + human_readable_device_id);

        boolean radio_buttons_visible = showAddDeviceButton(show_button,
                getResources().getString(R.string.add_weight_scale),
                human_readable_device_id,
                main_activity_interface.getDeviceDummyDataModeEnableStatus(SensorType.SENSOR_TYPE__WEIGHT_SCALE),
                buttonAddWeightScale,
                buttonRemoveWeightScale,
                textWeightScaleWeightMeasurement,
                null,
                textWeightScaleWeightMeasurementTimestamp);

        int visible;

        if (radio_buttons_visible)
        {
            visible = View.VISIBLE;
        }
        else
        {
            visible = View.INVISIBLE;
        }

        if(main_activity_interface.getDeviceCurrentlyConnectedByType(SensorType.SENSOR_TYPE__WEIGHT_SCALE))
        {
            buttonWeightScaleSimulateConnectionState.setText(R.string.textDisconnect);
        }
        else
        {
            buttonWeightScaleSimulateConnectionState.setText(R.string.textConnect);
        }

        buttonWeightScaleSimulateConnectionState.setVisibility(visible);
    }


    private void buttonAddEarlyWarningScoreClick()
    {
        main_activity_interface.enableDummyEarlyWarningScoreDevice(true);
        
        addDeviceCommon();
        
        showAddEarlyWarningScoreButton(false, 100005);
    }


    private void buttonRemoveEarlyWarningScoreClick()
    {
        main_activity_interface.enableDummyEarlyWarningScoreDevice(false);
        
        showAddEarlyWarningScoreButton(true, INVALID_HUMAN_READABLE_DEVICE_ID);
    }


    private void showAddEarlyWarningScoreButton(boolean show_button, long human_readable_device_id)
    {
        Log.e(TAG, "showAddEarlyWarningScoreButton. show_button = " + show_button + ". human_readable_device_id = " + human_readable_device_id);

        boolean radio_buttons_visible = showAddDeviceButton(show_button,
                getResources().getString(R.string.add_early_warning_scores),
                human_readable_device_id,
                main_activity_interface.getDeviceDummyDataModeEnableStatus(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE),
                buttonAddEarlyWarningScore,
                buttonRemoveEarlyWarningScore,
                textEarlyWarningScoreMeasurement,
                textEarlyWarningScoreOutOf,
                textEarlyWarningScoreMeasurementTimestamp);
        
        int visible;

        if (radio_buttons_visible)
        {
            visible = View.VISIBLE;
        }
        else
        {
            visible = View.INVISIBLE;
        }

        checkBoxSpoofEarlyWarningScores.setVisibility(visible);
    }


    public void setDummyDataCheckBoxSpoofEarlyWarningScores(boolean spoof)
    {
    	checkBoxSpoofEarlyWarningScores.setChecked(spoof);
    }
    
    
    public void setHospitalPatientID(String desired_value)
    {
        textHospitalPatientId.setText(desired_value);
    }


    public <T extends MeasurementVitalSign> void showMeasurement(VitalSignType vital_sign_type, T measurement)
    {
        switch (vital_sign_type)
        {
            case HEART_RATE:
            {
                showHeartRateMeasurement((MeasurementHeartRate)measurement);
            }
            break;

            case RESPIRATION_RATE:
            {
                showRespirationRateMeasurement((MeasurementRespirationRate)measurement);
            }
            break;

            case TEMPERATURE:
            {
                showTemperatureMeasurement((MeasurementTemperature)measurement);
            }
            break;

            case SPO2:
            {
                showPulseOxMeasurement((MeasurementSpO2)measurement);
            }
            break;

            case BLOOD_PRESSURE:
            {
                showBloodPressureMeasurement((MeasurementBloodPressure)measurement);
            }
            break;

            case WEIGHT:
            {
                showWeightScaleMeasurement((MeasurementWeight)measurement);
            }
            break;

            case EARLY_WARNING_SCORE:
            {
                showEarlyWarningScoreMeasurement((MeasurementEarlyWarningScore)measurement);
            }
            break;
        }
    }

    
    private void showHeartRateMeasurement(MeasurementHeartRate measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        textHeartRateMeasurement.setText(String.valueOf(measurement.heart_rate));
        textHeartAndRespirationRateMeasurementTimestamp.setText(timestamp_as_human_readable_string);
    }


    private void showRespirationRateMeasurement(MeasurementRespirationRate measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        textViewRespirationRateMeasurement.setText(String.valueOf(measurement.respiration_rate));
        textHeartAndRespirationRateMeasurementTimestamp.setText(timestamp_as_human_readable_string);
    }
    
    
    private void showTemperatureMeasurement(MeasurementTemperature measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        textTemperatureMeasurement.setText(String.valueOf(measurement.temperature));
        textTemperatureMeasurementTimestamp.setText(timestamp_as_human_readable_string);
    }
    

    private void showPulseOxMeasurement(MeasurementSpO2 measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__SPO2);
        switch (device_info.device_type)
        {
            case DEVICE_TYPE__NONIN_WRIST_OX:
            {
                textNoninWristOxMeasurement.setText(String.valueOf(measurement.SpO2));
                textNoninWristOxMeasurementTimestamp.setText(timestamp_as_human_readable_string);
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
            {
                textNoninWristOxBleMeasurement.setText(String.valueOf(measurement.SpO2));
                textNoninWristOxBleMeasurementTimestamp.setText(timestamp_as_human_readable_string);
            }
            break;
        }
    }


    private void showBloodPressureMeasurement(MeasurementBloodPressure measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        textBloodPressureSystolicMeasurement.setText(String.valueOf(measurement.systolic));
        textBloodPressureDiastolicMeasurement.setText(String.valueOf(measurement.diastolic));
        textBloodPressureMeasurementTimestamp.setText(timestamp_as_human_readable_string);
    }


    private void showWeightScaleMeasurement(MeasurementWeight measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        textWeightScaleWeightMeasurement.setText(String.valueOf(measurement.weight));
        textWeightScaleWeightMeasurementTimestamp.setText(timestamp_as_human_readable_string);
    }


    private void showEarlyWarningScoreMeasurement(MeasurementEarlyWarningScore measurement)
    {
        String timestamp_as_human_readable_string = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        textEarlyWarningScoreMeasurement.setText(String.valueOf(measurement.early_warning_score));
        textEarlyWarningScoreOutOf.setText(String.valueOf(measurement.max_possible_score));
        textEarlyWarningScoreMeasurementTimestamp.setText(timestamp_as_human_readable_string);
    }
    
    
    public void setNumberOfMeasurementsPerTick(int measurements_per_tick)
    {
        // The -1 is because the seekBarDummyDataModeMeasurementPerTick onProgressChanged function will increment it to take it from 0 -> 9 to 1 -> 10
        seekBarDummyDataModeMeasurementPerTick.setProgress(measurements_per_tick - 1);
    }


    private void setupPatientAgeCheckboxes(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail)
    {
        if (thresholdSetAgeBlockDetail != null)
        {
            View v = getView();
            if (v != null)
            {
                int buttonNumber = main_activity_interface.getRadioButtonNumberFromThresholdSetAgeBlockDetail(thresholdSetAgeBlockDetail);

                radioGroupPatientAge = v.findViewById(R.id.radioGroupPatientAge);

                ((RadioButton)radioGroupPatientAge.getChildAt(buttonNumber)).setChecked(true);
            }
        }
    }
}
