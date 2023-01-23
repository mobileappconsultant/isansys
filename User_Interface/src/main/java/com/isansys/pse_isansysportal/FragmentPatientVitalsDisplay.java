package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.isansys.common.ErrorCodes;
import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.PatientPositionOrientation;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Locale;

import roo.clockanimation.ClockDrawable;

public class FragmentPatientVitalsDisplay extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private static final int TOTAL_LEADS_OFF_COUNT_BEFORE_DISCONNECTION_PULSE_OX = 60 * 10;            // 1 leads-off packet every second for 10 mins
    private static final int MARGIN_LEADS_OFF_COUNT_BEFORE_DISCONNECTION_PULSE_OX = 5;                 // Margin of 5 packets

    private LinearLayout linear_layout__patient_vitals_display__lifetouch;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_heart_rate;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_respiration_rate;
    private LinearLayout linear_layout__patient_vitals_display__temperature;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_temperature;
    private LinearLayout linear_layout__patient_vitals_display__pulse_ox;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_spo2;
    private LinearLayout linear_layout__patient_vitals_display__blood_pressure;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_blood_pressure;
    private LinearLayout linear_layout__patient_vitals_display__weight_scale;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_weight;
    private LinearLayout linear_layout__patient_vitals_display__early_warning_scores;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_supplemental_oxygen;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_consciousness_level;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_capillary_refill_time;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_respiration_distress;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_family_or_nurse_concern;
    private LinearLayout linear_layout__patient_vitals_display__manually_entered_urine_output;

    private final LeftHandSideDisplayItems left_hand_side_display_items__lifetouch = new LeftHandSideDisplayItems();
    private final LeftHandSideDisplayItems left_hand_side_display_items__lifetemp = new LeftHandSideDisplayItems();
    private final LeftHandSideDisplayItems left_hand_side_display_items__pulse_ox = new LeftHandSideDisplayItems();
    private final LeftHandSideDisplayItems left_hand_side_display_items__blood_pressure = new LeftHandSideDisplayItems();
    private final LeftHandSideDisplayItems left_hand_side_display_items__weight_scale = new LeftHandSideDisplayItems();
    private final LeftHandSideDisplayItems left_hand_side_display_items__early_warning_score = new LeftHandSideDisplayItems();
    // Do not need these for Manually Entered measurement as there isn't anything that can change for them.

    // Right hand side of screen. Everything to do with the spot measurement
    static class CurrentValidMeasurementDisplayItems
    {
        // Right Hand Side
        LinearLayout linearLayoutMeasurementGroup = null;
        TextView textMeasurementDescription = null;
        TextView textMeasurementUnits = null;
        TextView textMeasurementOne = null;
        TextView textMeasurementSeparator = null;
        TextView textMeasurementTwo = null;
        TextView textMeasurementTimestamp = null;
        ProgressBar progressBarMeasurementValidity = null;

        void setProgress(int progress)
        {
            progressBarMeasurementValidity.setProgress(progress);
        }
    }

    private ImageView lifetouch_patient_orientation_image;

    private CheckBox checkBoxLifetouchConfigurable;
    private CheckBox checkBoxLifetouchOptions;

    private CheckBox checkBoxPulseOxSetupMode;

    private TextView textEarlyWarningScoreType;


    static class PatientVitalsDisplayItems
    {
        final VitalSignType vital_sign_type;

        ArrayList<GraphColourBand> graph_colour_bands = new ArrayList<>();

        // LHS Connection status
        LeftHandSideConnectionDisplayStates connection_display_state = LeftHandSideConnectionDisplayStates.INVALID;

        // Centre graph state
        GraphDisplayStates graph_state = GraphDisplayStates.INVALID;

        // RHS Measurement status
        final CurrentValidMeasurementDisplayItems measurement_display_items = new CurrentValidMeasurementDisplayItems();
        RightHandSideMeasurementDisplayStates right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;

        Handler measurement_validity_timeout_handler = new Handler();

        PatientVitalsDisplayItems(VitalSignType desired_vital_sign_type)
        {
            vital_sign_type = desired_vital_sign_type;
        }
    }


    class AllPatientVitalsDisplayItems
    {
        final ArrayList<PatientVitalsDisplayItems> measurement_display_items = new ArrayList<>();

        AllPatientVitalsDisplayItems()
        {
            for (VitalSignType vital_sign_type : VitalSignType.values())
            {
                measurement_display_items.add(new PatientVitalsDisplayItems(vital_sign_type));
            }
        }


        PatientVitalsDisplayItems getDisplayItem(VitalSignType vital_sign_type)
        {
            for (PatientVitalsDisplayItems measurement_display_item : measurement_display_items)
            {
                if (measurement_display_item.vital_sign_type == vital_sign_type)
                {
                    return measurement_display_item;
                }
            }

            Log.e(TAG, "Error PatientVitalsDisplayItems getDisplayItem returning null for " + vital_sign_type.toString());

            return null;
        }


        void clearAllMeasurementValidityTimeoutHandlers()
        {
            for (PatientVitalsDisplayItems measurement_display_item : measurement_display_items)
            {
                if (measurement_display_item.measurement_validity_timeout_handler != null)
                {
                    measurement_display_item.measurement_validity_timeout_handler.removeCallbacksAndMessages(null);
                }
            }
        }


        void resetMeasurementValidityTimeoutHandlers()
        {
            for (PatientVitalsDisplayItems measurement_display_item : measurement_display_items)
            {
                if (measurement_display_item.measurement_validity_timeout_handler != null)
                {
                    measurement_display_item.measurement_validity_timeout_handler = new Handler();
                }
            }
        }
    }

    // These cover the state of the Left Hand Side, Graphs and Right Hand Sides
    private final AllPatientVitalsDisplayItems all_patient_vitals_display_items = new AllPatientVitalsDisplayItems();


    private final Handler lifetouch_heartbeat_handler = new Handler();

    private final Handler lifetemp_measurement_handler = new Handler();

    
    // Fragment
    private FragmentGraphSetupMode lifetouch_setup_mode_fragment = null;
    private FragmentGraphLifetouchNormalMode lifetouch_normal_mode_fragment = null;
    private FragmentGraphRawAccelerometerMode lifetouch_raw_accelerometer_mode_fragment = null;
    private FragmentLifetouchNotAttached lifetouch_not_attached_fragment = null;
    private FragmentLifetouchPoorSignal lifetouch_poor_signal_fragment = null;
    private FragmentPatientVitalsDisplayLifetouchOptions lifetouch_options_fragment = null;

    private FragmentGraphTemperature temperature_graph_fragment = null;
    private FragmentLifetempNotAttached lifetemp_not_attached_fragment = null;

    private FragmentPulseOxNotAttached pulse_ox_not_attached_fragment = null;
    private FragmentGraphPulseOxNormalMode pulse_ox_normal_mode_fragment = null;
    private FragmentGraphSetupMode pulse_ox_setup_mode_fragment = null;
    private FragmentPulseOxDisconnectedLeadsOff pulse_ox_disconnected_because_of_leads_off = null;

    private FragmentGraphBloodPressure blood_pressure_graph_fragment = null;
    private FragmentAndBleNeedsPairing blood_pressure_unbonded_fragment = null;

    private FragmentGraphWeight weight_scale_graph_fragment = null;

    private FragmentGraphEarlyWarningScores early_warning_scores_graph_fragment = null;
    
    private FragmentGraphManuallyEntered manually_entered_heart_rate_graph_fragment = null;
    private FragmentGraphManuallyEntered manually_entered_respiration_rate_graph_fragment = null;
    private FragmentGraphManuallyEntered manually_entered_temperature_graph_fragment = null;
    private FragmentGraphManuallyEntered manually_entered_spo2_graph_fragment = null;
    private FragmentGraphManuallyEntered manually_entered_blood_pressure_graph_fragment = null;
    private FragmentGraphManuallyEntered manually_entered_graph_fragment_weight = null;
    private FragmentGraphManuallyEnteredBlocks manually_entered_graph_fragment_supplemental_oxygen = null;
    private FragmentGraphManuallyEnteredBlocks manually_entered_graph_fragment_consciousness_level = null;
    private FragmentGraphManuallyEnteredBlocks manually_entered_graph_fragment_capillary_refill_time = null;
    private FragmentGraphManuallyEnteredBlocks manually_entered_graph_fragment_respiration_distress = null;
    private FragmentGraphManuallyEnteredBlocks manually_entered_graph_fragment_family_or_nurse_concern = null;
    private FragmentGraphManuallyEntered manually_entered_graph_fragment_urine_output = null;

    private ArrayList<FragmentGraph> list_of_fragment_graphs_sensor = null;
    private ArrayList<FragmentGraph> list_of_fragment_graphs_algorithm = null;
    private ArrayList<FragmentGraphManuallyEntered> list_of_fragment_graph_manually_entered = null;
    private ArrayList<FragmentGraphManuallyEnteredBlocks> list_of_fragment_graph_manually_entered_blocks = null;

    private View view;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.patient_vitals_display, container, false);

        linear_layout__patient_vitals_display__lifetouch = view.findViewById(R.id.linear_layout__patient_vitals_display__lifetouch);
        linear_layout__patient_vitals_display__lifetouch.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_heart_rate = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_heart_rate);
        linear_layout__patient_vitals_display__manually_entered_heart_rate.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_respiration_rate = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_respiration_rate);
        linear_layout__patient_vitals_display__manually_entered_respiration_rate.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__temperature = view.findViewById(R.id.linear_layout__patient_vitals_display__temperature);
        linear_layout__patient_vitals_display__temperature.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_temperature = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_temperature);
        linear_layout__patient_vitals_display__manually_entered_temperature.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__pulse_ox = view.findViewById(R.id.linear_layout__patient_vitals_display__pulse_ox);
        linear_layout__patient_vitals_display__pulse_ox.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_spo2 = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_spo2);
        linear_layout__patient_vitals_display__manually_entered_spo2.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__blood_pressure = view.findViewById(R.id.linear_layout__patient_vitals_display__blood_pressure);
        linear_layout__patient_vitals_display__blood_pressure.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_blood_pressure = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_blood_pressure);
        linear_layout__patient_vitals_display__manually_entered_blood_pressure.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__weight_scale = view.findViewById(R.id.linear_layout__patient_vitals_display__weight_scale);
        linear_layout__patient_vitals_display__weight_scale.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_weight = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_weight);
        linear_layout__patient_vitals_display__manually_entered_weight.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__early_warning_scores = view.findViewById(R.id.linear_layout__patient_vitals_display__early_warning_scores);
        linear_layout__patient_vitals_display__early_warning_scores.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_consciousness_level = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_consciousness_level);
        linear_layout__patient_vitals_display__manually_entered_consciousness_level.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_supplemental_oxygen = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_supplemental_oxygen);
        linear_layout__patient_vitals_display__manually_entered_supplemental_oxygen.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_capillary_refill_time = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_capillary_refill_time);
        linear_layout__patient_vitals_display__manually_entered_capillary_refill_time.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_respiration_distress = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_respiration_distress);
        linear_layout__patient_vitals_display__manually_entered_respiration_distress.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_family_or_nurse_concern = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_family_or_nurse_concern);
        linear_layout__patient_vitals_display__manually_entered_family_or_nurse_concern.setVisibility(View.GONE);

        linear_layout__patient_vitals_display__manually_entered_urine_output = view.findViewById(R.id.linear_layout__patient_vitals_display__manually_entered_urine_output);
        linear_layout__patient_vitals_display__manually_entered_urine_output.setVisibility(View.GONE);

        return view;
    }


    private void setupLifetouchUi(View v, int row_height, DeviceInfo device_info)
    {
        // Using normal_mode_fragment == null to check if this has already run. Not nice but works
        if (lifetouch_normal_mode_fragment == null)
        {
            // Create Fragments objects for Normal Mode and Setup Mode
            lifetouch_setup_mode_fragment = new FragmentGraphSetupMode();
            configureSetupModeGraph(lifetouch_setup_mode_fragment, device_info.max_setup_mode_sample_size, 1000); // change this to handle high-hz sampling

            lifetouch_normal_mode_fragment = new FragmentGraphLifetouchNormalMode();
            lifetouch_raw_accelerometer_mode_fragment = new FragmentGraphRawAccelerometerMode();
            lifetouch_not_attached_fragment = new FragmentLifetouchNotAttached();
            lifetouch_poor_signal_fragment = new FragmentLifetouchPoorSignal();
            lifetouch_options_fragment = new FragmentPatientVitalsDisplayLifetouchOptions();

            // Adjust the size of linear_layout__patient_vitals_display__lifetouch depending on how many rows are in the List.
            LinearLayout linear_layout__patient_vitals_display__lifetouch = v.findViewById(R.id.linear_layout__patient_vitals_display__lifetouch);
            linear_layout__patient_vitals_display__lifetouch.getLayoutParams().height = row_height * 2;
            linear_layout__patient_vitals_display__lifetouch.requestLayout();

            // Lifetouch GUI elements on the left hand side part of the screen
            left_hand_side_display_items__lifetouch.textHumanReadableDeviceId = v.findViewById(R.id.textLifetouchHumanReadableDeviceId);
            left_hand_side_display_items__lifetouch.imageBatteryIcon = v.findViewById(R.id.imageBatteryLifetouch);
            left_hand_side_display_items__lifetouch.imageBatteryIcon.setImageResource(R.drawable.battery_empty);
            setDeviceHumanReadableDeviceId(device_info);

            left_hand_side_display_items__lifetouch.textNumberOfMeasurementsPending = v.findViewById(R.id.textNumberOfHeartBeatsPending);
            setNumberOfMeasurementsPending(left_hand_side_display_items__lifetouch, 0);

            lifetouch_patient_orientation_image = v.findViewById(R.id.imagePatientOrientation);
            lifetouch_patient_orientation_image.setVisibility(View.INVISIBLE);
            system_commands.reportPatientOrientation();

            left_hand_side_display_items__lifetouch.textBatteryPercentage = v.findViewById(R.id.textLifetouchBatteryPercentage);
            left_hand_side_display_items__lifetouch.imageMeasurement = v.findViewById(R.id.heartbeat);
            left_hand_side_display_items__lifetouch.imageMeasurement.setImageResource(R.drawable.heart_current_data);
            left_hand_side_display_items__lifetouch.imageMeasurement.setVisibility(View.INVISIBLE);
            left_hand_side_display_items__lifetouch.clockDrawable = new ClockDrawable(getResources());

            CheckBox checkboxConfig = v.findViewById(R.id.checkBoxLifetouchConfigurable);
            specialCaseForCheckBoxes(checkboxConfig);

            CheckBox checkboxOptions = v.findViewById(R.id.checkBoxPatientVitalsDisplayLifetouchOptions);
            specialCaseForCheckBoxes(checkboxOptions);

            left_hand_side_display_items__lifetouch.imageOutOfRange = v.findViewById(R.id.imagePatientVitalsDisplayLifetouchOutOfRange);
            main_activity_interface.pulseImage(left_hand_side_display_items__lifetouch.imageOutOfRange);
            left_hand_side_display_items__lifetouch.linearLayoutBatteryAndControls = v.findViewById(R.id.linearLayout_lifetouchBatteryAndControls);

            // Middle GUI graph dealt with by the deviceStateChange function, when we get a device info update from the gateway

            // Lifetouch GUI elements on the right hand side part of the screen
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutHeartRateMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textHeartRate);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textHeartRateBeatsPerMinLabel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textHeartRateTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarLifetouchHeartRateMeasurementValidity);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutRespirationRateMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.textMeasurementDescription = v.findViewById(R.id.textRespirationRateDescription);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textRespirationRate);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textRespirationRateBreathMinLabel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textRespirationRateTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarLifetouchRespirationRateMeasurementValidity);

            specialCaseForRespirationRateString(all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).measurement_display_items.textMeasurementDescription);

            // Get the Lifetouch Device Info handler. Will be used to set the background colour depending on if its connected or disconnected (out of range for example)
            left_hand_side_display_items__lifetouch.linearLayoutDeviceInfo = v.findViewById(R.id.linearLayoutDeviceInfoLifetouch);

            checkBoxLifetouchConfigurable = v.findViewById(R.id.checkBoxLifetouchConfigurable);
            resumeLifetouchConfigurableCheckboxBasedOnPreviousSelection();

            checkBoxLifetouchOptions = v.findViewById(R.id.checkBoxPatientVitalsDisplayLifetouchOptions);
            checkBoxLifetouchOptions.setOnClickListener(v1 -> {
                if (checkBoxLifetouchOptions.isChecked())
                {
                    showLifetouchOptionsOverlay();
                }
                else
                {
                    removeLifetouchOptionsOverlayIfShown();
                }
            });

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.HEART_RATE).graph_state = GraphDisplayStates.INVALID;

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.RESPIRATION_RATE).graph_state = GraphDisplayStates.INVALID;
        }
    }


    private void specialCaseForCheckBoxes(CheckBox checkbox)
    {
        String language = Locale.getDefault().toString();

        switch (language) {
            case "da_DK":
                checkbox.setTextSize(11);
                break;
            case "nb_NO":
            case "de_DE":
                checkbox.setTextSize(13);
                break;
        }

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) checkbox.getLayoutParams();
        lp.setMarginStart(2);
        checkbox.setLayoutParams(lp);
    }


    private void specialCaseForRespirationRateString(TextView textView)
    {
        String language = Locale.getDefault().toString();
        if (language.equals("nb_NO") || language.equals("da_DK"))
        {
            // Adjusting margins as the AutoResizeTextView seems to make the height double what it should be when displaying a very long string
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
            lp.setMargins(0, -15, 0, -20);
            textView.setLayoutParams(lp);
        }
    }


    private void setupTemperatureUi(View v, int row_height, DeviceInfo device_info)
    {
        if (temperature_graph_fragment == null)
        {
            temperature_graph_fragment = new FragmentGraphTemperature();
            list_of_fragment_graphs_sensor.add(temperature_graph_fragment);

            lifetemp_not_attached_fragment = new FragmentLifetempNotAttached();

            // Adjust the size of linear_layout__patient_vitals_display__lifetemp depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__temperature, row_height);

            // Lifetemp GUI elements on the left hand side part of the screen
            left_hand_side_display_items__lifetemp.textHumanReadableDeviceId = v.findViewById(R.id.textLifetempHumanReadableDeviceId);
            left_hand_side_display_items__lifetemp.imageBatteryIcon = v.findViewById(R.id.imageBatteryLifetemp);
            left_hand_side_display_items__lifetemp.imageBatteryIcon.setImageResource(R.drawable.battery_empty);
            setDeviceHumanReadableDeviceId(device_info);

            left_hand_side_display_items__lifetemp.textBatteryPercentage = v.findViewById(R.id.textLifetempBatteryPercentage);

            left_hand_side_display_items__lifetemp.textNumberOfMeasurementsPending = v.findViewById(R.id.textLifetempNumberOfMeasurementsPending);
            setNumberOfMeasurementsPending(left_hand_side_display_items__lifetemp, 0);

            left_hand_side_display_items__lifetemp.clockDrawable = new ClockDrawable(getResources());

            left_hand_side_display_items__lifetemp.imageMeasurement = v.findViewById(R.id.imageLifetempMeasurement);
            left_hand_side_display_items__lifetemp.imageMeasurement.setVisibility(View.INVISIBLE);

            left_hand_side_display_items__lifetemp.imageOutOfRange = v.findViewById(R.id.imagePatientVitalsDisplayThermometerOutOfRange);
            main_activity_interface.pulseImage(left_hand_side_display_items__lifetemp.imageOutOfRange);
            left_hand_side_display_items__lifetemp.linearLayoutBatteryAndControls = v.findViewById(R.id.linearLayout_thermometerBatteryAndControls);

            // Middle GUI graph dealt with by the deviceStatChange function, when we get a device info update from the gateway

            // Lifetemp GUI elements on the right hand side part of the screen
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutTemperatureMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textTemperatureReading);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textTemperatureTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textTemperatureLabel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarLifetempTemperatureMeasurementValidity);

            // Get the Lifetemp Device Info handler. Will be used to set the background colour depending on if its connected or disconnected (out of range for example)
            left_hand_side_display_items__lifetemp.linearLayoutDeviceInfo = v.findViewById(R.id.linearLayoutDeviceInfoLifetemp);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.TEMPERATURE).graph_state = GraphDisplayStates.INVALID;
        }
    }


    private void setupPulseOxUi(View v, int row_height, DeviceInfo device_info)
    {
        if (pulse_ox_normal_mode_fragment == null)
        {
            pulse_ox_normal_mode_fragment = new FragmentGraphPulseOxNormalMode();
            list_of_fragment_graphs_sensor.add(pulse_ox_normal_mode_fragment);

            pulse_ox_setup_mode_fragment = new FragmentGraphSetupMode();
            configureSetupModeGraph(pulse_ox_setup_mode_fragment, device_info.max_setup_mode_sample_size, 1000);

            pulse_ox_not_attached_fragment = new FragmentPulseOxNotAttached();
            pulse_ox_disconnected_because_of_leads_off = new FragmentPulseOxDisconnectedLeadsOff();

            // Adjust the size of linear_layout__patient_vitals_display__pulse_ox depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__pulse_ox, row_height);

            // Pulse Ox GUI elements on the left hand side part of the screen
            left_hand_side_display_items__pulse_ox.textHumanReadableDeviceId = v.findViewById(R.id.textPulseOxHumanReadableDeviceId);
            setDeviceHumanReadableDeviceId(device_info);

            left_hand_side_display_items__pulse_ox.imageBatteryIcon = v.findViewById(R.id.imageBatteryPulseOx);
            left_hand_side_display_items__pulse_ox.imageBatteryIcon.setImageResource(R.drawable.battery_empty);

            CheckBox checkBoxSetupMode = v.findViewById(R.id.checkBoxPulseOxSetupMode);
            specialCaseForCheckBoxes(checkBoxSetupMode);

            left_hand_side_display_items__pulse_ox.imageOutOfRange = v.findViewById(R.id.imagePatientVitalsDisplayPulseOxOutOfRange);
            main_activity_interface.pulseImage(left_hand_side_display_items__pulse_ox.imageOutOfRange);
            left_hand_side_display_items__pulse_ox.linearLayoutBatteryAndControls = v.findViewById(R.id.linearLayout_pulseOxBatteryAndControls);

            // Middle GUI graph dealt with by the deviceStatChange function, when we get a device info update from the gateway

            // GUI elements on the right hand side part of the screen
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutPulseOxMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textSpO2);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textPulseOximeterTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textSpO2PercentLabel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarPulseOxMeasurementValidity);

            // Get the Pulse Ox Device Info handler. Will be used to set the background colour depending on if its connected or disconnected (out of range for example)
            left_hand_side_display_items__pulse_ox.linearLayoutDeviceInfo = v.findViewById(R.id.linearLayoutDeviceInfoPulseOx);

            checkBoxPulseOxSetupMode = v.findViewById(R.id.checkBoxPulseOxSetupMode);
            createPulseOxSetupModeCheckBoxOnCheckedChangeListener();

            // Hide Setup mode button if necessary
            if (device_info.supports_setup_mode == false)
            {
                setCheckBoxVisible(checkBoxPulseOxSetupMode, View.INVISIBLE);
            }

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.SPO2).graph_state = GraphDisplayStates.INVALID;
        }
    }


    private void configureSetupModeGraph(FragmentGraphSetupMode fragment, int max_sample_size, int number_of_samples)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentGraphSetupMode.MAX_SAMPLE_SIZE, max_sample_size);
        bundle.putInt(FragmentGraphSetupMode.NUMBER_OF_SAMPLES, number_of_samples);

        fragment.setArguments(bundle);
    }


    private void setupBloodPressureUi(View v, int row_height, DeviceInfo device_info)
    {
        if (blood_pressure_graph_fragment == null)
        {
            blood_pressure_graph_fragment = new FragmentGraphBloodPressure();
            list_of_fragment_graphs_sensor.add(blood_pressure_graph_fragment);

            if ((device_info.device_type == DeviceType.DEVICE_TYPE__AND_ABPM_TM2441)
                    || (device_info.device_type == DeviceType.DEVICE_TYPE__AND_UA651)
                    || (device_info.device_type == DeviceType.DEVICE_TYPE__AND_UA656BLE)
                    || (device_info.device_type == DeviceType.DEVICE_TYPE__AND_UA1200BLE)
            )
            {
                blood_pressure_unbonded_fragment = new FragmentAndBleNeedsPairing();
            }

            // Adjust the size of linear_layout__patient_vitals_display__blood_pressure depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__blood_pressure, row_height);

            // GUI elements on the left hand side part of the screen
            left_hand_side_display_items__blood_pressure.textHumanReadableDeviceId = v.findViewById(R.id.textBloodPressureHumanReadableDeviceId);
            setDeviceHumanReadableDeviceId(device_info);

            left_hand_side_display_items__blood_pressure.imageBatteryIcon = v.findViewById(R.id.imageBatteryBloodPressure);
            left_hand_side_display_items__blood_pressure.imageBatteryIcon.setImageResource(R.drawable.battery_empty);

            left_hand_side_display_items__blood_pressure.imageOutOfRange = v.findViewById(R.id.imagePatientVitalsDisplayBloodPressureOutOfRange);
            main_activity_interface.pulseImage(left_hand_side_display_items__blood_pressure.imageOutOfRange);
            left_hand_side_display_items__blood_pressure.linearLayoutBatteryAndControls = v.findViewById(R.id.linearLayout_bloodPressureBatteryAndControls);

            // Middle GUI graph dealt with by the deviceStateChange function, when we get a device info update from the gateway

            // GUI elements on the right hand side part of the screen
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutBloodPressureMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textBloodPressureMeasurementSystolic);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.textMeasurementSeparator = v.findViewById(R.id.textBloodPressureMeasurementSlash);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.textMeasurementTwo = v.findViewById(R.id.textBloodPressureMeasurementDiastolic);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textBloodPressureMeasurementTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textBloodPressureLabel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarBloodPressureMeasurementValidity);

            // Get the Blood Pressure Device Info handler. Will be used to set the background colour depending on if its connected or disconnected (out of range for example)
            left_hand_side_display_items__blood_pressure.linearLayoutDeviceInfo = v.findViewById(R.id.linearLayoutDeviceInfoBloodPressure);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.BLOOD_PRESSURE).graph_state = GraphDisplayStates.INVALID;
        }
    }


    private void setupWeightUi(View v, int row_height, DeviceInfo device_info)
    {
        if (weight_scale_graph_fragment == null)
        {
            weight_scale_graph_fragment = new FragmentGraphWeight();
            list_of_fragment_graphs_sensor.add(weight_scale_graph_fragment);

            // Adjust the size of linear_layout__patient_vitals_display__blood_pressure depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__weight_scale, row_height);

            // GUI elements on the left hand side part of the screen
            left_hand_side_display_items__weight_scale.textHumanReadableDeviceId = v.findViewById(R.id.textWeightScaleHumanReadableDeviceId);
            setDeviceHumanReadableDeviceId(device_info);

            left_hand_side_display_items__weight_scale.imageBatteryIcon = v.findViewById(R.id.imageBatteryWeightScale);
            left_hand_side_display_items__weight_scale.imageBatteryIcon.setImageResource(R.drawable.battery_empty);

            left_hand_side_display_items__weight_scale.imageOutOfRange = v.findViewById(R.id.imagePatientVitalsDisplayWeightScaleOutOfRange);
            main_activity_interface.pulseImage(left_hand_side_display_items__weight_scale.imageOutOfRange);
            left_hand_side_display_items__weight_scale.linearLayoutBatteryAndControls = v.findViewById(R.id.linearLayout_weightScaleBatteryAndControls);

            // Middle GUI graph dealt with by the deviceStateChange function, when we get a device info update from the gateway

            // GUI elements on the right hand side part of the screen
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutWeightScaleMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textWeightScaleMeasurement);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textWeightScaleMeasurementTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textWeightScaleLabel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarWeightScaleMeasurementValidity);

            // Get the Weight Scale Device Info handler. Will be used to set the background colour depending on if its connected or disconnected (out of range for example)
            left_hand_side_display_items__weight_scale.linearLayoutDeviceInfo = v.findViewById(R.id.linearLayoutDeviceInfoWeightScale);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.WEIGHT).graph_state = GraphDisplayStates.INVALID;
        }
    }


    private void setupRowHeight(View v, int desired_linear_layout_id, int row_height)
    {
        LinearLayout ll = v.findViewById(desired_linear_layout_id);
        ll.getLayoutParams().height = row_height;
        ll.requestLayout();
    }


    private void setupEws(View v, int row_height)
    {
        if (early_warning_scores_graph_fragment == null)
        {
            early_warning_scores_graph_fragment = new FragmentGraphEarlyWarningScores();
            list_of_fragment_graphs_algorithm.add(early_warning_scores_graph_fragment);

            // Adjust the size of linear_layout__patient_vitals_display__early_warning_scores depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__early_warning_scores, row_height);

            // GUI elements on the left hand side part of the screen
            textEarlyWarningScoreType = v.findViewById(R.id.textEarlyWarningScoreType);
            left_hand_side_display_items__early_warning_score.linearLayoutDeviceInfo = v.findViewById(R.id.linearLayoutEarlyWarningScoreType);

            setEarlyWarningScoreType(main_activity_interface.reportEarlyWarningScoreType());

            // GUI elements on the right hand side part of the screen
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutEarlyWarningScoreMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textEarlyWarningScoreMeasurement);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).measurement_display_items.textMeasurementSeparator = v.findViewById(R.id.textEarlyWarningScoreMeasurementSlash);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).measurement_display_items.textMeasurementTwo = v.findViewById(R.id.textEarlyWarningScoreMaxPossible);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textEarlyWarningScoreTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarEarlyWarningScoreMeasurementValidity);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.EARLY_WARNING_SCORE).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;
//                            early_warning_score_graph_state = GraphDisplayStates.INVALID;
        }
    }


    private void setupManualVitalsCommon(VitalSignType vital_sign_type)
    {
        DeviceInfo device_info = main_activity_interface.getDeviceInfoForVitalSign(vital_sign_type);

        all_patient_vitals_display_items.getDisplayItem(vital_sign_type).right_measurement_display_state = RightHandSideMeasurementDisplayStates.INVALID;

        boolean display_manually_entered_graph = device_info.isDeviceTypePartOfPatientSession();
        if (display_manually_entered_graph)
        {
            deviceStateChange(device_info);
        }
    }


    private void setupManuallyEnteredHeartRate(View v, int row_height)
    {
        if (manually_entered_heart_rate_graph_fragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.HEART_RATE.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_HEART_RATE.ordinal());
            manually_entered_heart_rate_graph_fragment = new FragmentGraphManuallyEntered();
            manually_entered_heart_rate_graph_fragment.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_heart_rate_graph_fragment);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_heart_rate depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_heart_rate, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredHeartRateMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textManuallyEnteredHeartRateUnits);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredHeartRate);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredHeartRateTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredHeartRateMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_HEART_RATE);
        }
    }


    private void setupManuallyEnteredRespirationRate(View v, int row_height)
    {
        if (manually_entered_respiration_rate_graph_fragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.RESPIRATION_RATE.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE.ordinal());
            manually_entered_respiration_rate_graph_fragment = new FragmentGraphManuallyEntered();
            manually_entered_respiration_rate_graph_fragment.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_respiration_rate_graph_fragment);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_heart_rate depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_respiration_rate, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredRespirationRateMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.textMeasurementDescription = v.findViewById(R.id.textManuallyEnteredRespirationRateDescription);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textManuallyEnteredRespirationRateUnits);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredRespirationRate);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredRespirationRateTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredRespirationRateMeasurementValidity);

            specialCaseForRespirationRateString(all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement_display_items.textMeasurementDescription);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE);
        }
    }


    private void setupManuallyEnteredTemperature(View v, int row_height)
    {
        if (manually_entered_temperature_graph_fragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.TEMPERATURE.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_TEMPERATURE.ordinal());
            manually_entered_temperature_graph_fragment = new FragmentGraphManuallyEntered();
            manually_entered_temperature_graph_fragment.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_temperature_graph_fragment);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_heart_rate depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_temperature, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_TEMPERATURE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredTemperatureMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_TEMPERATURE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textManuallyEnteredTemperatureUnits);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_TEMPERATURE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredTemperature);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_TEMPERATURE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredTemperatureTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_TEMPERATURE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredTemperatureMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_TEMPERATURE);
        }
    }


    private void setupManuallyEnteredSpO2(View v, int row_height)
    {
        if (manually_entered_spo2_graph_fragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.SPO2.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_SPO2.ordinal());
            manually_entered_spo2_graph_fragment = new FragmentGraphManuallyEntered();
            manually_entered_spo2_graph_fragment.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_spo2_graph_fragment);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_spo2 depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_spo2, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SPO2).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredSpO2MeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SPO2).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textManuallyEnteredSpO2Units);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SPO2).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredSpO2);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SPO2).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredSpO2Timestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SPO2).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredSpO2MeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_SPO2);
        }
    }


    private void setupManuallyEnteredBloodPressure(View v, int row_height)
    {
        if (manually_entered_blood_pressure_graph_fragment == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.BLOOD_PRESSURE.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE.ordinal());
            manually_entered_blood_pressure_graph_fragment = new FragmentGraphManuallyEntered();
            manually_entered_blood_pressure_graph_fragment.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_blood_pressure_graph_fragment);

            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_blood_pressure, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredBloodPressureMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textManuallyEnteredBloodPressureUnits);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredBloodPressureMeasurementSystolic);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.textMeasurementSeparator = v.findViewById(R.id.textManuallyEnteredBloodPressureMeasurementSlash);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.textMeasurementTwo = v.findViewById(R.id.textManuallyEnteredBloodPressureMeasurementDiastolic);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredBloodPressureTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredBloodPressureMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE);
        }
    }


    // Reminder - this plots the bars on graphViewSeries as this is the primary measurement
    private void setupManuallyEnteredWeight(View v, int row_height)
    {
        if (manually_entered_graph_fragment_weight == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.WEIGHT.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_WEIGHT.ordinal());
            manually_entered_graph_fragment_weight = new FragmentGraphManuallyEntered();
            manually_entered_graph_fragment_weight.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_graph_fragment_weight);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_measurement depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_weight, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_WEIGHT).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredWeightMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_WEIGHT).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredWeightMeasurement);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_WEIGHT).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredWeightTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_WEIGHT).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredWeightMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_WEIGHT);
        }
    }

    // Reminder - this plots the bars on graphViewSeries as this is the primary measurement
    private void setupManuallyEnteredCapillaryRefillTime(View v, int row_height)
    {
        if (manually_entered_graph_fragment_capillary_refill_time == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME.ordinal());
            manually_entered_graph_fragment_capillary_refill_time = new FragmentGraphManuallyEnteredBlocks();
            manually_entered_graph_fragment_capillary_refill_time.setArguments(bundle);

            list_of_fragment_graph_manually_entered_blocks.add(manually_entered_graph_fragment_capillary_refill_time);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_measurement depending on how many rows are in the List.
            setupRowHeight(v, R.id.fragment_graph_manually_entered_capillary_refill_time, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredCapillaryRefillTimeMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredCapillaryRefillTime);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredCapillaryRefillTimeTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredCapillaryRefillTimeMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
        }
    }


    // Reminder - this plots the bars on graphViewSeries as this is the primary measurement
    private void setupManuallyEnteredSupplementalOxygen(View v, int row_height)
    {
        if (manually_entered_graph_fragment_supplemental_oxygen == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN.ordinal());
            manually_entered_graph_fragment_supplemental_oxygen = new FragmentGraphManuallyEnteredBlocks();
            manually_entered_graph_fragment_supplemental_oxygen.setArguments(bundle);

            list_of_fragment_graph_manually_entered_blocks.add(manually_entered_graph_fragment_supplemental_oxygen);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_measurement depending on how many rows are in the List.
            setupRowHeight(v, R.id.fragment_graph_manually_entered_supplemental_oxygen, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredSupplementalOxygenLevelMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredSupplementalOxygenLevel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredSupplementalOxygenLevelTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredSupplementalOxygenLevelMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN);
        }
    }


    // Reminder - this plots the bars on graphViewSeries as this is the primary measurement
    private void setupManuallyEnteredConsciousnessLevel(View v, int row_height)
    {
        if (manually_entered_graph_fragment_consciousness_level == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL.ordinal());
            manually_entered_graph_fragment_consciousness_level = new FragmentGraphManuallyEnteredBlocks();
            manually_entered_graph_fragment_consciousness_level.setArguments(bundle);

            list_of_fragment_graph_manually_entered_blocks.add(manually_entered_graph_fragment_consciousness_level);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_measurement depending on how many rows are in the List.
            setupRowHeight(v, R.id.fragment_graph_manually_entered_consciousness_level, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredConsciousnessLevelMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredConsciousnessLevel);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredConsciousnessLevelTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredConsciousnessLevelMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL);
        }
    }


    // Reminder - this plots the bars on graphViewSeries as this is the primary measurement
    private void setupManuallyEnteredRespirationDistress(View v, int row_height)
    {
        if (manually_entered_graph_fragment_respiration_distress == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal());
            manually_entered_graph_fragment_respiration_distress = new FragmentGraphManuallyEnteredBlocks();
            manually_entered_graph_fragment_respiration_distress.setArguments(bundle);

            list_of_fragment_graph_manually_entered_blocks.add(manually_entered_graph_fragment_respiration_distress);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_measurement depending on how many rows are in the List.
            setupRowHeight(v, R.id.fragment_graph_manually_entered_respiration_distress, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredRespirationDistressMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredRespirationDistress);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredRespirationDistressTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredRespirationDistressMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS);
        }
    }


    // Reminder - this plots the bars on graphViewSeries as this is the primary measurement
    private void setupManuallyEnteredFamilyOrNurseConcern(View v, int row_height)
    {
        if (manually_entered_graph_fragment_family_or_nurse_concern == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal());
            manually_entered_graph_fragment_family_or_nurse_concern = new FragmentGraphManuallyEnteredBlocks();
            manually_entered_graph_fragment_family_or_nurse_concern.setArguments(bundle);

            list_of_fragment_graph_manually_entered_blocks.add(manually_entered_graph_fragment_family_or_nurse_concern);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_measurement depending on how many rows are in the List.
            setupRowHeight(v, R.id.fragment_graph_manually_entered_family_or_nurse_concern, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredFamilyOrNurseConcernMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredFamilyOrNurseConcern);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredFamilyOrNurseConcernTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredFamilyOrNurseConcernMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);
        }
    }


    private void setupManuallyEnteredUrineOutput(View v, int row_height)
    {
        if (manually_entered_graph_fragment_urine_output == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT.ordinal());
            bundle.putInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT.ordinal());
            manually_entered_graph_fragment_urine_output = new FragmentGraphManuallyEntered();
            manually_entered_graph_fragment_urine_output.setArguments(bundle);

            list_of_fragment_graph_manually_entered.add(manually_entered_graph_fragment_urine_output);

            // Adjust the size of linear_layout__patient_vitals_display__manually_entered_urine_output depending on how many rows are in the List.
            setupRowHeight(v, R.id.linear_layout__patient_vitals_display__manually_entered_urine_output, row_height);

            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT).measurement_display_items.linearLayoutMeasurementGroup = v.findViewById(R.id.linearLayoutManuallyEnteredUrineOutputMeasurementGroup);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT).measurement_display_items.textMeasurementUnits = v.findViewById(R.id.textManuallyEnteredUrineOutputUnits);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT).measurement_display_items.textMeasurementOne = v.findViewById(R.id.textManuallyEnteredUrineOutput);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT).measurement_display_items.textMeasurementTimestamp = v.findViewById(R.id.textManuallyEnteredUrineOutputTimestamp);
            all_patient_vitals_display_items.getDisplayItem(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT).measurement_display_items.progressBarMeasurementValidity = v.findViewById(R.id.progressBarManuallyEnteredUrineOutputMeasurementValidity);

            setupManualVitalsCommon(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
 
        super.onCreate(savedInstanceState);
    }
    

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart");
        super.onStart();
    }
    
    
    @Override
    public void onResume()
    {
        Bundle bundle;

        Log.d(TAG, "onResume");

        list_of_fragment_graphs_sensor = new ArrayList<>();
        list_of_fragment_graphs_algorithm = new ArrayList<>();
        list_of_fragment_graph_manually_entered = new ArrayList<>();
        list_of_fragment_graph_manually_entered_blocks = new ArrayList<>();

        for (VitalSignType vital_sign_type : VitalSignType.values())
        {
            bundle = main_activity_interface.getGraphColourBands(vital_sign_type);
            if (bundle != null)
            {
                all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_colour_bands = bundle.getParcelableArrayList("graph_colour_bands");
            }
        }

        int number_of_rows = 0;
        ArrayList<DeviceInfo> device_types_in_use = main_activity_interface.getDeviceTypesInUse();

        for (DeviceInfo device_info : device_types_in_use)
        {
            if(device_info.isDeviceTypePartOfPatientSession())
            {
                switch(device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        number_of_rows = number_of_rows + 2;
                        linear_layout__patient_vitals_display__lifetouch.setVisibility(View.VISIBLE);
                    }
                    break;

                    case SENSOR_TYPE__TEMPERATURE:
                    {
                        number_of_rows = number_of_rows + 1;
                        linear_layout__patient_vitals_display__temperature.setVisibility(View.VISIBLE);
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        number_of_rows = number_of_rows + 1;
                        linear_layout__patient_vitals_display__pulse_ox.setVisibility(View.VISIBLE);
                    }
                    break;

                    case SENSOR_TYPE__BLOOD_PRESSURE:
                    {
                        number_of_rows = number_of_rows + 1;
                        linear_layout__patient_vitals_display__blood_pressure.setVisibility(View.VISIBLE);
                    }
                    break;

                    case SENSOR_TYPE__WEIGHT_SCALE:
                    {
                        number_of_rows = number_of_rows + 1;
                        linear_layout__patient_vitals_display__weight_scale.setVisibility(View.VISIBLE);
                    }
                    break;

                    case SENSOR_TYPE__ALGORITHM:
                    {
                        switch (device_info.device_type)
                        {
                            case DEVICE_TYPE__EARLY_WARNING_SCORE:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__early_warning_scores.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                    }
                    break;

                    case SENSOR_TYPE__MANUAL_VITAL:
                    {
                        switch (device_info.device_type)
                        {
                            case DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE:
                            {
                                if(!checkForMatchingDevice(device_types_in_use, SensorType.SENSOR_TYPE__LIFETOUCH))
                                {
                                    number_of_rows = number_of_rows + 1;
                                    linear_layout__patient_vitals_display__manually_entered_heart_rate.setVisibility(View.VISIBLE);
                                }
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE:
                            {
                                if(!checkForMatchingDevice(device_types_in_use, SensorType.SENSOR_TYPE__LIFETOUCH))
                                {
                                    number_of_rows = number_of_rows + 1;
                                    linear_layout__patient_vitals_display__manually_entered_respiration_rate.setVisibility(View.VISIBLE);
                                }
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE:
                            {
                                if(!checkForMatchingDevice(device_types_in_use, SensorType.SENSOR_TYPE__TEMPERATURE))
                                {
                                    number_of_rows = number_of_rows + 1;
                                    linear_layout__patient_vitals_display__manually_entered_temperature.setVisibility(View.VISIBLE);
                                }
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_SPO2:
                            {
                                if(!checkForMatchingDevice(device_types_in_use, SensorType.SENSOR_TYPE__SPO2))
                                {
                                    number_of_rows = number_of_rows + 1;
                                    linear_layout__patient_vitals_display__manually_entered_spo2.setVisibility(View.VISIBLE);
                                }
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE:
                            {
                                if(!checkForMatchingDevice(device_types_in_use, SensorType.SENSOR_TYPE__BLOOD_PRESSURE))
                                {
                                    number_of_rows = number_of_rows + 1;
                                    linear_layout__patient_vitals_display__manually_entered_blood_pressure.setVisibility(View.VISIBLE);
                                }
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT:
                            {
                                if(!checkForMatchingDevice(device_types_in_use, SensorType.SENSOR_TYPE__WEIGHT_SCALE))
                                {
                                    number_of_rows = number_of_rows + 1;
                                    linear_layout__patient_vitals_display__manually_entered_weight.setVisibility(View.VISIBLE);
                                }
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__manually_entered_supplemental_oxygen.setVisibility(View.VISIBLE);
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__manually_entered_consciousness_level.setVisibility(View.VISIBLE);
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__manually_entered_capillary_refill_time.setVisibility(View.VISIBLE);
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__manually_entered_respiration_distress.setVisibility(View.VISIBLE);
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__manually_entered_family_or_nurse_concern.setVisibility(View.VISIBLE);
                            }
                            break;

                            case DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT:
                            {
                                number_of_rows = number_of_rows + 1;
                                linear_layout__patient_vitals_display__manually_entered_urine_output.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }

        int max_rows;

        if (main_activity_interface.isScreenLandscape())
        {
            max_rows = 6;
        }
        else
        {
            max_rows = 9;
        }


        if (number_of_rows > max_rows)
        {
            number_of_rows = max_rows;             // Scroll down to see the rest of them
        }

        if (number_of_rows > 0)
        {
            int row_height = main_activity_interface.getMainFragmentHeight() / number_of_rows;

            for (DeviceInfo device_info : device_types_in_use)
            {
                if(device_info.isDeviceTypePartOfPatientSession())
                {
                    switch(device_info.sensor_type)
                    {
                        case SENSOR_TYPE__LIFETOUCH:
                        {
                            setupLifetouchUi(view, row_height, device_info);
                        }
                        break;

                        case SENSOR_TYPE__TEMPERATURE:
                        {
                            setupTemperatureUi(view, row_height, device_info);
                        }
                        break;

                        case SENSOR_TYPE__SPO2:
                        {
                            setupPulseOxUi(view, row_height, device_info);
                        }
                        break;

                        case SENSOR_TYPE__BLOOD_PRESSURE:
                        {
                            setupBloodPressureUi(view, row_height, device_info);
                        }
                        break;

                        case SENSOR_TYPE__WEIGHT_SCALE:
                        {
                            setupWeightUi(view, row_height, device_info);
                        }
                        break;

                        case SENSOR_TYPE__ALGORITHM:
                        {
                            switch (device_info.device_type)
                            {
                                case DEVICE_TYPE__EARLY_WARNING_SCORE:
                                {
                                    setupEws(view, row_height);
                                }
                                break;
                            }
                        }
                        break;

                        case SENSOR_TYPE__MANUAL_VITAL:
                        {
                            switch (device_info.device_type)
                            {
                                case DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE:
                                {
                                    setupManuallyEnteredHeartRate(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE:
                                {
                                    setupManuallyEnteredRespirationRate(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE:
                                {
                                    setupManuallyEnteredTemperature(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_SPO2:
                                {
                                    setupManuallyEnteredSpO2(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE:
                                {
                                    setupManuallyEnteredBloodPressure(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT:
                                {
                                    setupManuallyEnteredWeight(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                                {
                                    setupManuallyEnteredSupplementalOxygen(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                                {
                                    setupManuallyEnteredConsciousnessLevel(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                                {
                                    setupManuallyEnteredCapillaryRefillTime(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                                {
                                    setupManuallyEnteredRespirationDistress(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                                {
                                    setupManuallyEnteredFamilyOrNurseConcern(view, row_height);
                                }
                                break;

                                case DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT:
                                {
                                    setupManuallyEnteredUrineOutput(view, row_height);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        all_patient_vitals_display_items.resetMeasurementValidityTimeoutHandlers();

        main_activity_interface.getAllDeviceInfosFromGateway();

        main_activity_interface.refreshDeviceLeadsOffStatus();

        super.onResume();
    }


    private void showStringOnRightHandSide(CurrentValidMeasurementDisplayItems display_items, String string, int text_size)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            showStringOnRightHandSide(display_items, string, text_size, ContextCompat.getColor(activity, R.color.gray));
        }
    }


    private void showStringOnRightHandSide(CurrentValidMeasurementDisplayItems display_items, String string, int text_size, int colour)
    {
        Log.d(TAG, "showStringOnRightHandSide : " + string + " : " + text_size);

        Activity activity = getActivity();
        if (activity != null)
        {
            if (display_items.textMeasurementDescription != null)
            {
                display_items.textMeasurementDescription.setTextColor(ContextCompat.getColor(activity, R.color.white));
            }

            if(display_items.textMeasurementOne != null)
            {
                display_items.textMeasurementOne.setText(string);
                display_items.textMeasurementOne.setTextColor(ContextCompat.getColor(activity, R.color.white));
                display_items.textMeasurementOne.setTextSize(text_size);
            }

            if (display_items.textMeasurementTwo != null)
            {
                display_items.textMeasurementTwo.setText(R.string.blank_string);
                display_items.textMeasurementTwo.setTextColor(ContextCompat.getColor(activity, R.color.white));
            }

            if (display_items.progressBarMeasurementValidity != null)
            {
                display_items.progressBarMeasurementValidity.setVisibility(View.INVISIBLE);
            }

            if(display_items.textMeasurementUnits != null)
            {
                display_items.textMeasurementUnits.setTextColor(ContextCompat.getColor(activity, R.color.white));
            }

            if(display_items.textMeasurementTimestamp != null)
            {
                display_items.textMeasurementTimestamp.setText(R.string.blank_string);
                display_items.textMeasurementTimestamp.setTextColor(ContextCompat.getColor(activity, R.color.white));
                display_items.textMeasurementTimestamp.setVisibility(View.GONE);
            }

            if(display_items.linearLayoutMeasurementGroup != null)
            {
                display_items.linearLayoutMeasurementGroup.setBackgroundColor(colour);
            }
        }
    }


    private void showWaitingForData(CurrentValidMeasurementDisplayItems display_items)
    {
        showStringOnRightHandSide(display_items, getString(R.string.textWaitingForData), 24);
    }


    private void showDeviceRemoved(CurrentValidMeasurementDisplayItems display_items)
    {
        showStringOnRightHandSide(display_items, getString(R.string.textDeviceRemoved), 24);
    }


    private void showDownloadingPreviousData(CurrentValidMeasurementDisplayItems display_items, String sample_time)
    {
        showStringOnRightHandSide(display_items, getString(R.string.textDownloadingPreviousData), 20);

        Activity activity = getActivity();
        if (activity != null)
        {
            if (display_items.textMeasurementTimestamp != null)
            {
                display_items.textMeasurementTimestamp.setText(sample_time);
                display_items.textMeasurementTimestamp.setTextColor(ContextCompat.getColor(activity, R.color.white));
                display_items.textMeasurementTimestamp.setVisibility(View.VISIBLE);
            }
        }

    }

    private void showVitalsMeasurement(VitalSignType vital_sign_type, MeasurementVitalSign measurement, CurrentValidMeasurementDisplayItems display_items)
    {
        int text_colour = Color.WHITE;
        int background_colour = Color.BLUE;

        String measurement_one;
        String measurement_two;

        String timestamp_in_ms_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement.timestamp_in_ms);

        int right_hand_side_vital_sign_measurement_text_size = 32;

        measurement_one = main_activity_interface.formatMeasurementForDisplay(measurement);

        // Special case for BP and EWS as we want to display in two different text boxes
        switch (vital_sign_type)
        {
            case BLOOD_PRESSURE:
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            case EARLY_WARNING_SCORE:
            {
                String[] split_measurement = measurement_one.split("/");

                measurement_one = split_measurement[0];

                if(split_measurement.length > 1)
                {
                    measurement_two = split_measurement[1];
                }
                else
                {
                    measurement_two = "";
                }

                if (display_items.textMeasurementSeparator != null)
                {
                    display_items.textMeasurementSeparator.setVisibility(View.VISIBLE);
                }

                if (display_items.textMeasurementTwo != null)
                {
                    display_items.textMeasurementTwo.setTextSize(right_hand_side_vital_sign_measurement_text_size);
                    display_items.textMeasurementTwo.setText(measurement_two);
                    display_items.textMeasurementTwo.setVisibility(View.VISIBLE);
                }
            }
            break;
        }

        ArrayList<GraphColourBand> graph_colour_bands = all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_colour_bands;

        for (GraphColourBand graph_colour_band : graph_colour_bands)
        {
            if (measurement.getPrimaryMeasurement() < graph_colour_band.less_than_value)
            {
                background_colour = graph_colour_band.band_colour;
                text_colour = graph_colour_band.text_colour;
                break;
            }
        }

        if(background_colour == Color.BLUE)
        {
            try
            {
                GraphColourBand graph_colour_band = graph_colour_bands.get(graph_colour_bands.size() - 1);

                background_colour = graph_colour_band.band_colour;
                text_colour = graph_colour_band.text_colour;
            }
            catch (Exception e)
            {
                Log.e(TAG, "Exception looking up graph background color");
            }
        }

        if (display_items.textMeasurementDescription != null)
        {
            display_items.textMeasurementDescription.setTextColor(text_colour);
        }

        if (display_items.textMeasurementOne != null)
        {
            display_items.textMeasurementOne.setText(measurement_one);
            display_items.textMeasurementOne.setTextColor(text_colour);
            display_items.textMeasurementOne.setTextSize(right_hand_side_vital_sign_measurement_text_size);
            display_items.textMeasurementOne.setVisibility(View.VISIBLE);
        }

        if (display_items.textMeasurementSeparator != null)
        {
            display_items.textMeasurementSeparator.setTextColor(text_colour);
        }

        if (display_items.textMeasurementTwo != null)
        {
            display_items.textMeasurementTwo.setTextColor(text_colour);
        }

        if (display_items.textMeasurementUnits != null)
        {
            display_items.textMeasurementUnits.setTextColor(text_colour);
        }

        if (display_items.textMeasurementTimestamp != null)
        {
            display_items.textMeasurementTimestamp.setTextColor(text_colour);
            display_items.textMeasurementTimestamp.setText(timestamp_in_ms_as_string);
            display_items.textMeasurementTimestamp.setVisibility(View.VISIBLE);
        }

        if (display_items.progressBarMeasurementValidity != null)
        {
            display_items.progressBarMeasurementValidity.setVisibility(View.VISIBLE);
            display_items.progressBarMeasurementValidity.setMax(main_activity_interface.getMeasurementValidityTimeInSeconds(vital_sign_type));
        }

        if (display_items.linearLayoutMeasurementGroup != null)
        {
            display_items.linearLayoutMeasurementGroup.setBackgroundColor(background_colour);
        }
    }


    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause");

        super.onPause();
    }
    
    
    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop");
        super.onStop();
    }


    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy");

        FragmentManager fragmentManager;
        Fragment fragment;

        fragmentManager = getChildFragmentManager();

        fragment = fragmentManager.findFragmentById(R.id.fragment_graph_lifetouch);
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        fragment = fragmentManager.findFragmentById(R.id.fragment_graph_temperature);
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        fragment = fragmentManager.findFragmentById(R.id.fragment_graph_pulse_ox);
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        fragment = fragmentManager.findFragmentById(R.id.fragment_graph_blood_pressure);
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

// need to tidy up other fragments??? Or get rid of the above as not needed??

        Log.d(TAG, "Closing Poincare popup from FragmentPatientVitalsDisplay onDestroy");
        main_activity_interface.closePoincarePopupIfShowing();

        // Removing the timeout that shows Waiting For Data
        all_patient_vitals_display_items.clearAllMeasurementValidityTimeoutHandlers();

        super.onDestroy();
    }


    public void showHeartBeatIcon(HeartBeatInfo heart_beat)
    {
        if (left_hand_side_display_items__lifetouch.imageMeasurement != null)
        {
            int display_time_in_ms;

            left_hand_side_display_items__lifetouch.imageMeasurement.setVisibility(View.VISIBLE);

            if (heart_beat.isBeatRealTime())
            {
                if (main_activity_interface.getShowLifetouchActivityLevel())
                {
                    switch(heart_beat.getActivity())
                    {
                        case LOW:
                            left_hand_side_display_items__lifetouch.imageMeasurement.setImageResource(R.drawable.heart_low_activity);
                            break;

                        case HIGH:
                            /* Instructions For Use say two bars are displayed for high activity (which is the "medium" icon). Since we haven't defined this anywhere else,
                             * and changing the instructions requires new translations, for convenience changing this to "medium" icon (two bars) instead of "high" (three bars)
                             */
                            left_hand_side_display_items__lifetouch.imageMeasurement.setImageResource(R.drawable.heart_medium_activity);
                            break;

                        case NO_DATA:
                        case NONE:
                            left_hand_side_display_items__lifetouch.imageMeasurement.setImageResource(R.drawable.heart_current_data);
                    }
                }
                else
                {
                    left_hand_side_display_items__lifetouch.imageMeasurement.setImageResource(R.drawable.heart_current_data);
                }

                display_time_in_ms = 250;
            }
            else
            {
                if(left_hand_side_display_items__lifetouch.clockDrawable != null)
                {
                    left_hand_side_display_items__lifetouch.imageMeasurement.setImageDrawable(left_hand_side_display_items__lifetouch.clockDrawable);
                    left_hand_side_display_items__lifetouch.clockDrawable.start(new DateTime(heart_beat.getTimestampInMs()));
                }

                display_time_in_ms = 1500;
            }

            lifetouch_heartbeat_handler.removeCallbacksAndMessages(null);
            lifetouch_heartbeat_handler.postDelayed(runnableHideLifetouchHeartBeat, display_time_in_ms);
        }
    }


    private void onHeartBeatImageTimeoutTimerTick()
    {
        left_hand_side_display_items__lifetouch.imageMeasurement.setVisibility(View.INVISIBLE);
    }


    private final Runnable runnableHideLifetouchHeartBeat = this::onHeartBeatImageTimeoutTimerTick;


    private void showLifetempMeasurement(MeasurementTemperature measurement)
    {
        if (left_hand_side_display_items__lifetemp.imageMeasurement != null)
        {
            int display_time_in_ms = 0;

            if (measurement.measurement_validity_time_left_in_seconds > 0)
            {
                if(left_hand_side_display_items__lifetemp.clockDrawable != null)
                {
                    left_hand_side_display_items__lifetemp.imageMeasurement.setVisibility(View.VISIBLE);
                    left_hand_side_display_items__lifetemp.imageMeasurement.setImageDrawable(left_hand_side_display_items__lifetemp.clockDrawable);
                    left_hand_side_display_items__lifetemp.clockDrawable.start(new DateTime(measurement.timestamp_in_ms));
                }

                display_time_in_ms = 1500;
            }

            if (display_time_in_ms > 0)
            {
                lifetemp_measurement_handler.removeCallbacksAndMessages(null);
                lifetemp_measurement_handler.postDelayed(runnableHideLifetempMeasurement, display_time_in_ms);
            }
        }
    }


    private void onLifetempTemperatureImageTimeoutTimerTick()
    {
        left_hand_side_display_items__lifetemp.imageMeasurement.setVisibility(View.INVISIBLE);
    }


    private final Runnable runnableHideLifetempMeasurement = this::onLifetempTemperatureImageTimeoutTimerTick;


    private void createDeviceSetupModeCheckBoxOnCheckedChangeListener(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                createLifetouchSetupModeCheckBoxOnCheckedChangeListener();
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                createPulseOxSetupModeCheckBoxOnCheckedChangeListener();
            }
            break;
        }
    }


    private void createLifetouchSetupModeCheckBoxOnCheckedChangeListener()
    {
        if(checkBoxLifetouchConfigurable != null)
        {
            checkBoxLifetouchConfigurable.setOnClickListener(v -> main_activity_interface.showLifetouchSetupMode(checkBoxLifetouchConfigurable.isChecked()));
        }

        lifetouch_configurable_checkbox_state = ConfigurableCheckboxStates.SETUP_MODE;
        setupCheckboxForLocalControl(checkBoxLifetouchConfigurable);
    }


    private void createLifetouchRawAccelerometerCheckBoxOnCheckedChangeListener()
    {
        if(checkBoxLifetouchConfigurable != null)
        {
            checkBoxLifetouchConfigurable.setOnClickListener(v -> main_activity_interface.showLifetouchRawAccelerometer(checkBoxLifetouchConfigurable.isChecked()));
        }

        lifetouch_configurable_checkbox_state = ConfigurableCheckboxStates.ACCELEROMETER_MODE;
        setupCheckboxForLocalControl(checkBoxLifetouchConfigurable);
    }


    private void createLifetouchPoincareCheckBoxOnCheckedChangeListener()
    {
        if(checkBoxLifetouchConfigurable != null)
        {
            checkBoxLifetouchConfigurable.setOnClickListener(v -> {
                if (checkBoxLifetouchConfigurable.isChecked())
                {
                    main_activity_interface.showPoincarePopup();
                    setCheckBoxChecked(checkBoxLifetouchConfigurable, false);  // Poincare is a popup dialog, no need to have checkbox checked in this case
                }
            });
        }

        lifetouch_configurable_checkbox_state = ConfigurableCheckboxStates.POINCARE_MODE;
        setupCheckboxForLocalControl(checkBoxLifetouchConfigurable);
    }


    public void setConfigButtonToPoincare()
    {
        setCheckBoxLifetouchConfigurableVisibleAndDisplayString(getString(R.string.poincare));
        createLifetouchPoincareCheckBoxOnCheckedChangeListener();
    }


    private void createSetupModeCheckBoxOnCheckedChangeListener(final CheckBox checkBoxSetupMode, final DeviceInfo device_info)
    {
        checkBoxSetupMode.setOnClickListener(v -> {
            if (checkBoxSetupMode.isChecked())
            {
                main_activity_interface.sendStartSetupModeCommand(device_info);
            }
            else
            {
                main_activity_interface.sendStopSetupModeCommand(device_info);
            }

            // Call this function to enable the graph for setup mode
            deviceStateChange(device_info);
        });
    }

    private void createPulseOxSetupModeCheckBoxOnCheckedChangeListener()
    {
        createSetupModeCheckBoxOnCheckedChangeListener(checkBoxPulseOxSetupMode, main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__SPO2));
    }
    

    private void setupCheckboxForLocalControl(CheckBox checkbox)
    {
        if (checkbox != null)
        {
            checkbox.setButtonDrawable(R.drawable.button_radio);

            checkbox.setEnabled(true);

            setCheckBoxVisible(checkbox, View.VISIBLE);
        }
    }


    private void setupCheckboxForServerInited(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                setupCheckboxForSystemInited(checkBoxLifetouchConfigurable);

                // Hide options button
                setCheckBoxVisible(checkBoxLifetouchOptions, View.INVISIBLE);
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                setupCheckboxForSystemInited(checkBoxPulseOxSetupMode);
            }
            break;
        }
    }


    private void updateNoninBleCheckBoxesBeforeOrAfterPlayback(DeviceInfo device_info, VitalSignType vital_sign_type)
    {
        Log.d(TAG,"updateNoninBleCheckBoxesBeforeOrAfterPlayback graph_state:" + all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state + " nonin_playback_ongoing:" + device_info.nonin_playback_ongoing);

        if (device_info.nonin_playback_ongoing)
        {
            Log.d(TAG, "calling disableCheckboxBecauseNoninPlaybackIsRunning from updateNoninBleCheckBoxesBeforeOrAfterPlayback");

            disableCheckboxBecauseNoninPlaybackIsRunning(checkBoxPulseOxSetupMode);
        }
        else
        {
            switch (all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state)
            {
                case SETUP_MODE:
                {
                    if (device_info.isDeviceInPeriodicSetupMode())
                    {
                        setupCheckboxForPeriodicSetupMode(checkBoxPulseOxSetupMode);
                    }

                    if (!device_info.isDeviceInPeriodicSetupMode() && !device_info.isDeviceUnderServerControl())
                    {
                        setupCheckBoxesForSetupMode(checkBoxPulseOxSetupMode);
                    }
                }
                break;

                case NORMAL_GRAPH_MODE:
                {
                    setupCheckBoxesForNormalMode(checkBoxPulseOxSetupMode);
                }
                break;

                default:
                {

                }
            }
        }
    }


    private void disableCheckboxBecauseNoninPlaybackIsRunning(CheckBox checkbox)
    {
        if (checkbox != null)
        {
            checkbox.setButtonDrawable(R.drawable.button_radio_grey);

            // Disable the configurable checkbox so it can't be clicked at all, because setup mode cannot be started when playback is ongoing
            checkbox.setEnabled(false);

            checkbox.setChecked(false);

            setCheckBoxTextAndMakeVisible(checkBoxPulseOxSetupMode, getString(R.string.textSetupMode));
        }
    }


    private void setupCheckbox(CheckBox checkbox, String string)
    {
        if (checkbox != null)
        {
            checkbox.setButtonDrawable(R.drawable.button_radio_grey);

            setCheckBoxChecked(checkbox, true);

            // Disable the configurable checkbox so it can't be clicked at all, because server control should be cancelled by the server
            checkbox.setEnabled(false);

            setCheckBoxTextAndMakeVisible(checkbox, string);
        }
    }


    private void setupCheckboxForSystemInited(CheckBox checkbox)
    {
        setupCheckbox(checkbox, getString(R.string.textServerControl));
    }


    private void setupCheckboxForPeriodicSetupMode(CheckBox checkbox)
    {
        setupCheckbox(checkbox, getString(R.string.textPeriodicSetupMode));
    }


    private void setupCheckBoxesForNormalMode(CheckBox checkbox)
    {
        setupCheckboxForLocalControl(checkbox);

        checkbox.setText(R.string.textSetupMode);
        setCheckBoxChecked(checkbox, false);
    }


    private void setupCheckBoxesForSetupMode(CheckBox checkbox)
    {
        setCheckBoxChecked(checkbox, true);
        setCheckBoxVisible(checkbox, View.VISIBLE);
    }


    private void setupCheckBoxesForLeadsOffOrDisconnected(CheckBox checkbox)
    {
        // Set Checkbox unchecked and invisible
        setCheckBoxChecked(checkbox, false);
        setCheckBoxVisible(checkbox, View.INVISIBLE);
    }


    public void setupCheckboxForPeriodicSetupMode(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                setupCheckboxForPeriodicSetupMode(checkBoxLifetouchConfigurable);

                // Hide Options button
                setCheckBoxVisible(checkBoxLifetouchOptions, View.INVISIBLE);
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                setupCheckboxForPeriodicSetupMode(checkBoxPulseOxSetupMode);
            }
            break;
        }
    }


    public <T extends MeasurementVitalSign> void redrawGraphUsingCachedMeasurements(VitalSignType vital_sign_type)
    {
        ArrayList<T> cached_measurements_list = (ArrayList<T>)main_activity_interface.getCachedMeasurements(vital_sign_type);

        if (cached_measurements_list != null)
        {
            Log.d(TAG, "redrawGraphUsingCachedMeasurements : " + vital_sign_type + " cache size = " + cached_measurements_list.size());

            switch (vital_sign_type)
            {
                case HEART_RATE:
                case RESPIRATION_RATE:
                {
                    if (UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
                    {
                        lifetouch_normal_mode_fragment.redrawNormalMeasurementGraph(vital_sign_type, cached_measurements_list);
                    }
                }
                break;

                case TEMPERATURE:
                case SPO2:
                case BLOOD_PRESSURE:
                case WEIGHT:
                case EARLY_WARNING_SCORE:
                {
                    FragmentGraph fragment = getGraphFragmentFromVitalSignType(vital_sign_type);
                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        fragment.redrawNormalMeasurementGraph(cached_measurements_list);
                    }
                }
                break;

                // Manual Vitals that can be plotted on Sensor graphs
                case MANUALLY_ENTERED_HEART_RATE:
                case MANUALLY_ENTERED_RESPIRATION_RATE:
                {
                    // Need to show Manual Vitals measurements on Sensor graphs
                    if (UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
                    {
                        lifetouch_normal_mode_fragment.redrawManuallyEnteredMeasurementGraph(cached_measurements_list);
                    }

                    // Now put measurements on Manual Vitals graphs if present
                    for (FragmentGraphManuallyEntered fragment : list_of_fragment_graph_manually_entered)
                    {
                        if (fragment.graph_manually_entered_vital_sign_type == vital_sign_type)
                        {
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.redrawManuallyEnteredMeasurementGraph(cached_measurements_list);
                            }
                        }
                    }
                }
                break;

                case MANUALLY_ENTERED_TEMPERATURE:
                case MANUALLY_ENTERED_SPO2:
                case MANUALLY_ENTERED_BLOOD_PRESSURE:
                case MANUALLY_ENTERED_WEIGHT:
                {
                    for (FragmentGraph fragment : list_of_fragment_graphs_sensor)
                    {
                        if (fragment.graph_manually_entered_vital_sign_type == vital_sign_type)
                        {
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.redrawManuallyEnteredMeasurementGraph(cached_measurements_list);
                            }
                        }
                    }

                    // Now put measurements on Manual Vitals graphs if present
                    for (FragmentGraphManuallyEntered fragment : list_of_fragment_graph_manually_entered)
                    {
                        if (fragment.graph_manually_entered_vital_sign_type == vital_sign_type)
                        {
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.redrawManuallyEnteredMeasurementGraph(cached_measurements_list);
                            }
                        }
                    }
                }
                break;

                // Manual Vitals that are not plotted on any graphs apart from themselves
                case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                case MANUALLY_ENTERED_URINE_OUTPUT:
                {
                    for (FragmentGraphManuallyEnteredBlocks fragment : list_of_fragment_graph_manually_entered_blocks)
                    {
                        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                        {
                            fragment.redrawManuallyEnteredMeasurementGraph(cached_measurements_list);
                        }
                    }
                }
                break;

                case MANUALLY_ENTERED_ANNOTATION:
                {
                    if (UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
                    {
                        lifetouch_normal_mode_fragment.redrawAnnotationsOnGraph(cached_measurements_list);
                    }

                    for (FragmentGraph fragment : list_of_fragment_graphs_sensor)
                    {
                        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                        {
                            fragment.redrawAnnotationsOnGraph(cached_measurements_list);
                        }
                    }

                    for (FragmentGraph fragment : list_of_fragment_graphs_algorithm)
                    {
                        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                        {
                            fragment.redrawAnnotationsOnGraph(cached_measurements_list);
                        }
                    }

                    for (FragmentGraphManuallyEntered fragment : list_of_fragment_graph_manually_entered)
                    {
                        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                        {
                            fragment.redrawAnnotationsOnGraph(cached_measurements_list);
                        }
                    }
                }
                break;
            }
        }
    }


    private FragmentGraph getGraphFragmentFromVitalSignType(VitalSignType vital_sign_type)
    {
        switch (vital_sign_type)
        {
            case TEMPERATURE:                               return temperature_graph_fragment;
            case SPO2:                                      return pulse_ox_normal_mode_fragment;
            case BLOOD_PRESSURE:                            return blood_pressure_graph_fragment;
            case WEIGHT:                                    return weight_scale_graph_fragment;

            case EARLY_WARNING_SCORE:                       return early_warning_scores_graph_fragment;

            case MANUALLY_ENTERED_HEART_RATE:               return manually_entered_heart_rate_graph_fragment;
            case MANUALLY_ENTERED_RESPIRATION_RATE:         return manually_entered_respiration_rate_graph_fragment;
            case MANUALLY_ENTERED_TEMPERATURE:              return manually_entered_temperature_graph_fragment;
            case MANUALLY_ENTERED_SPO2:                     return manually_entered_spo2_graph_fragment;
            case MANUALLY_ENTERED_BLOOD_PRESSURE:           return manually_entered_blood_pressure_graph_fragment;
            case MANUALLY_ENTERED_WEIGHT:                   return manually_entered_graph_fragment_weight;
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:      return manually_entered_graph_fragment_supplemental_oxygen;
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:      return manually_entered_graph_fragment_consciousness_level;
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:    return manually_entered_graph_fragment_capillary_refill_time;
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:     return manually_entered_graph_fragment_respiration_distress;
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:  return manually_entered_graph_fragment_family_or_nurse_concern;
            case MANUALLY_ENTERED_URINE_OUTPUT:             return manually_entered_graph_fragment_urine_output;
        }

        return null;
    }


    public <T extends MeasurementVitalSign> void updateMeasurement(final VitalSignType vital_sign_type, T measurement, boolean saved_measurement_updated)
    {
        final DeviceInfo device_info = main_activity_interface.getDeviceInfoForVitalSign(vital_sign_type);

        switch (vital_sign_type)
        {
            case HEART_RATE:
            case RESPIRATION_RATE:
            {
                if (UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
                {
                    // Add the value to the graph
                    if (!device_info.isDeviceInSetupMode())
                    {
                        lifetouch_normal_mode_fragment.redrawNormalMeasurementGraph(vital_sign_type, main_activity_interface.getCachedMeasurements(vital_sign_type));
                    }
                }
            }
            break;

            case TEMPERATURE:
            {
                showLifetempMeasurement((MeasurementTemperature)measurement);
            }
            // No BREAK on purpose. Want to fall through to the below

            default:
            {
                FragmentGraph fragment = getGraphFragmentFromVitalSignType(vital_sign_type);

                if (fragment != null)
                {
                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        ArrayList<? extends MeasurementVitalSign> measurements = main_activity_interface.getCachedMeasurements(vital_sign_type);

                        // Add the value to the graph
                        if (main_activity_interface.isVitalSignTypeAManualVital(vital_sign_type))
                        {
                            fragment.redrawManuallyEnteredMeasurementGraph(measurements);
                        }
                        else
                        {
                            fragment.redrawNormalMeasurementGraph(measurements);
                        }
                    }
                }
            }
            break;
        }

        // Update the saved measurement if the timestamp is newer than the previous measurement
        if (saved_measurement_updated)
        {
            // Reset the previous measurement timeout and start a new one
            all_patient_vitals_display_items.getDisplayItem(vital_sign_type).measurement_validity_timeout_handler.removeCallbacksAndMessages(null);
            all_patient_vitals_display_items.getDisplayItem(vital_sign_type).measurement_validity_timeout_handler.postDelayed(() -> {
                Log.d(TAG, "measurement_validity_timeout_handler fired : " + vital_sign_type);

                deviceStateChange(device_info);
            }, measurement.measurement_validity_time_in_seconds * DateUtils.SECOND_IN_MILLIS);

            deviceStateChange(device_info);
        }
    }


    public void newSetupModeSample(DeviceInfo device_info, int setup_mode_sample, long timestamp_in_ms)
    {
        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                if (device_info.isDeviceInSetupMode() && UtilityFunctions.isFragmentAddedAndResumed(lifetouch_setup_mode_fragment))
                {
                    lifetouch_setup_mode_fragment.updateSetupMode(setup_mode_sample, timestamp_in_ms);
                }
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                if (device_info.isDeviceInSetupMode() && UtilityFunctions.isFragmentAddedAndResumed(pulse_ox_setup_mode_fragment))
                {
                    pulse_ox_setup_mode_fragment.updateSetupMode(setup_mode_sample, timestamp_in_ms);
                }
            }
            break;
        }
    }


    public void newLifetouchRawAccelerometerSamples(short x, short y, short z, long timestamp_in_ms)
    {
        DeviceInfo device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
        if (device_info.isDeviceInRawAccelerometerMode() && UtilityFunctions.isFragmentAddedAndResumed(lifetouch_raw_accelerometer_mode_fragment))
        {
            lifetouch_raw_accelerometer_mode_fragment.updateSample(x, y, z, timestamp_in_ms);
        }
    }

    
    private void updatePatientOrientation(PatientPositionOrientation orientation)
    {
        if (lifetouch_patient_orientation_image != null)
        {
            switch (orientation)
            {
                case ORIENTATION_UPRIGHT:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_upright);
                    break;

                case ORIENTATION_LEFT_SIDE:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_left);
                    break;

                case ORIENTATION_RIGHT_SIDE:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_right);
                    break;

                case ORIENTATION_FRONT:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_front);
                    break;

                case ORIENTATION_BACK:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_back);
                    break;

                case ORIENTATION_UPSIDE_DOWN:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_upside_down);
                    break;

                case ORIENTATION_UNKNOWN:
                default:
                    lifetouch_patient_orientation_image.setImageResource(R.drawable.orientation_unknown_t);
                    break;
            }
        }
    }


    public void setDeviceBatteryLevel(DeviceInfo device_info)
    {
        LeftHandSideDisplayItems left_hand_side_display_items = getLeftHandSideDisplayItemsForSensorType(device_info.sensor_type);

        if (left_hand_side_display_items != null)
        {
            switch (device_info.sensor_type)
            {
                case SENSOR_TYPE__LIFETOUCH:
                {
                    left_hand_side_display_items.setDeviceBatteryLevel(device_info.last_battery_reading_in_millivolts,
                            device_info.last_battery_reading_percentage,
                            10,
                            main_activity_interface.getShowNumbersOnBatteryIndicator());
                }
                break;

                case SENSOR_TYPE__TEMPERATURE:
                {
                    left_hand_side_display_items.setDeviceBatteryLevel(device_info.last_battery_reading_in_millivolts,
                            device_info.last_battery_reading_percentage,
                            20,
                            main_activity_interface.getShowNumbersOnBatteryIndicator());
                }
                break;

                case SENSOR_TYPE__SPO2:
                case SENSOR_TYPE__BLOOD_PRESSURE:
                case SENSOR_TYPE__WEIGHT_SCALE:
                {
                    left_hand_side_display_items.setDeviceBatteryLevel(device_info.last_battery_reading_percentage,
                            0
                    );
                }
                break;
            }
        }
    }


    public void setLifetouchPatientOrientation(PatientPositionOrientation orientation, boolean visible)
    {
        if(lifetouch_patient_orientation_image != null)
        {
            updatePatientOrientation(orientation);

            if(visible)
            {
                lifetouch_patient_orientation_image.setVisibility(View.VISIBLE);
            }
            else
            {
                lifetouch_patient_orientation_image.setVisibility(View.GONE);
            }
        }
    }


    private void setNumberOfMeasurementsPending(LeftHandSideDisplayItems display_items, int number_of_measurements_pending)
    {
        if (display_items.textNumberOfMeasurementsPending != null)
        {
            if (number_of_measurements_pending == 0)
            {
                display_items.textNumberOfMeasurementsPending.setText(R.string.blank_string);
            }
            else
            {
                display_items.textNumberOfMeasurementsPending.setText(String.valueOf(number_of_measurements_pending));
            }
        }
    }


    public void setNumberOfMeasurementsPending(SensorType sensor_type, int number_of_measurements_pending)
    {
        LeftHandSideDisplayItems left_hand_side_display_items = getLeftHandSideDisplayItemsForSensorType(sensor_type);

        if (left_hand_side_display_items != null)
        {
            setNumberOfMeasurementsPending(left_hand_side_display_items, number_of_measurements_pending);
        }
    }

    private LeftHandSideDisplayItems getLeftHandSideDisplayItemsForSensorType(SensorType sensor_type)
    {
        switch (sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
                return left_hand_side_display_items__lifetouch;

            case SENSOR_TYPE__TEMPERATURE:
                return left_hand_side_display_items__lifetemp;

            case SENSOR_TYPE__SPO2:
                return left_hand_side_display_items__pulse_ox;

            case SENSOR_TYPE__BLOOD_PRESSURE:
                return left_hand_side_display_items__blood_pressure;

            case SENSOR_TYPE__WEIGHT_SCALE:
                return left_hand_side_display_items__weight_scale;
        }

        return null;
    }


    private TextView getTextViewHumanReadableDeviceId(DeviceInfo device_info)
    {
        LeftHandSideDisplayItems items = getLeftHandSideDisplayItemsForSensorType(device_info.sensor_type);
        if (items != null)
        {
            return items.textHumanReadableDeviceId;
        }
        else
        {
            return null;
        }
    }


    private void setDeviceHumanReadableDeviceId(DeviceInfo device_info)
    {
        TextView text_view = getTextViewHumanReadableDeviceId(device_info);
        if (text_view != null)
        {
            if(device_info.isDeviceHumanReadableDeviceIdValid())
            {
                text_view.setVisibility(View.VISIBLE);

                String device_display_name = main_activity_interface.getDeviceNameFromSensorType(device_info.sensor_type);

                String string = device_display_name + " " + device_info.human_readable_device_id;
                text_view.setText(string);
            }
            else
            {
                text_view.setVisibility(View.INVISIBLE);
            }
        }
    }


    void setEarlyWarningScoreType(String type)
    {
        if (textEarlyWarningScoreType != null)
        {
            textEarlyWarningScoreType.setVisibility(View.VISIBLE);
            textEarlyWarningScoreType.setText(type);
        }
        else
        {
            Log.e(TAG, "textEarlyWarningScoreType == null");
        }
    }


    private void showDeviceConnectionStatus(DeviceInfo device_info, boolean connected, boolean removed)
    {
        LeftHandSideDisplayItems left_hand_side_display_items = getLeftHandSideDisplayItemsForSensorType(device_info.sensor_type);

        if ((left_hand_side_display_items != null) && (getActivity() != null))
        {
            left_hand_side_display_items.hideAttemptingToReconnected();

            if(main_activity_interface.getDeviceDummyDataModeEnableStatus(device_info.device_type))
            {
                left_hand_side_display_items.setBackgroundColour(ContextCompat.getColor(getActivity(), R.color.light_yellow));
            }
            else if (connected)
            {
                left_hand_side_display_items.setBackgroundColour(ContextCompat.getColor(getActivity(), R.color.green));
            }
            else if(removed)
            {
                left_hand_side_display_items.setBackgroundColour(ContextCompat.getColor(getActivity(), R.color.gray));

                left_hand_side_display_items.hideAllBatteryElements();
            }
            else
            {
                left_hand_side_display_items.setBackgroundColour(ContextCompat.getColor(getActivity(), R.color.red));

                left_hand_side_display_items.hideAllBatteryElements();

                left_hand_side_display_items.showAttemptingToReconnected();
            }
        }
    }


    /**
     * Setting the progress level of the device measurement
     * @param vital_sign_type    vital sign type
     * @param progressBarValue    value in integer to set progress level
     */
    // measurement_validity_time_left_in_seconds
    public void setDevicesProgressBarValue(VitalSignType vital_sign_type, int progressBarValue)
    {
        PatientVitalsDisplayItems measurement_display_item = all_patient_vitals_display_items.getDisplayItem(vital_sign_type);

        if (measurement_display_item.measurement_display_items.progressBarMeasurementValidity != null)
        {
            measurement_display_item.measurement_display_items.setProgress(progressBarValue);

            if (progressBarValue == 0)
            {
                DeviceInfo device_info = main_activity_interface.getDeviceInfoForVitalSign(vital_sign_type);
                deviceStateChange(device_info);
            }
        }
    }


    /*
     * enum to hold the state for each sensors connection state
     */
    public enum LeftHandSideConnectionDisplayStates
    {
        INVALID,
        DEVICE_CONNECTED,
        DEVICE_DISCONNECTED,
        REMOVED,
    }


    /*
     * enum to hold the state for each measurements graph
     */
    public enum GraphDisplayStates
    {
        INVALID,
        NORMAL_GRAPH_MODE,
        SETUP_MODE,
        ACCELEROMETER_MODE,
        LEADS_OFF_MODE,
        POOR_SIGNAL_OR_NO_BEATS_DETECTED,
        DISCONNECT_MODE,
        DEVICE_REMOVED,
        DEVICE_UNBONDED,
    }

    /*
     * This enum represents all the possible state (Right hand side) for Vital Signs display
     */
    public enum RightHandSideMeasurementDisplayStates
    {
    	INVALID,
        WAITING_FOR_DATA,
        SHOW_VALUE,
        DOWNLOADING_DATA,
        REMOVED,
        REMOVED_BUT_MEASUREMENT_STILL_VALID,
    }


    /*
     *  This enum represents all the states of the configurable button which are user selected, which should remain after user has viewed Lifetouch screen
     */
    public enum ConfigurableCheckboxStates
    {
        INVALID,
        SETUP_MODE,
        POINCARE_MODE,
        ACCELEROMETER_MODE
    }


    // lifetouch_configurable button states variable to hold previous selected state to setup button on resume
    private static ConfigurableCheckboxStates lifetouch_configurable_checkbox_state = ConfigurableCheckboxStates.INVALID;


    // Separate logging for state machine. Easier for Debugging
    private static final String log_graph_states = "log_graph_states";
    
    /**
     * This function changes the graph state and display state of the requested device type
     * @param device_info : DeviceInfo to request which device to update
     * 
     * The device's leads-off state, setup-mode state, connection state and data validity are all queried from 
     * the MainActivity (or set manually to false if the requested device doesn't have such a state).
     */
    public void deviceStateChange(DeviceInfo device_info)
    {
        /* 
         * Create the temporary graph and display states
         */

        LeftHandSideConnectionDisplayStates connection_display_state;
        GraphDisplayStates current_graph_state;
        RightHandSideMeasurementDisplayStates data_display_state;

        VitalSignType vital_sign_type = main_activity_interface.getVitalSignTypeForDeviceInfo(device_info);

        /* 
         * Get the device state variables for the requested device
         */
        boolean in_setup_mode = device_info.isDeviceInSetupMode();
        boolean in_accelerometer_mode = false;
        boolean is_saved_data_valid = main_activity_interface.isSavedMeasurementValid(vital_sign_type);
        boolean leads_off_state = device_info.isDeviceOffBody();
        boolean poor_signal_in_last_minute_or_no_beats_detected = false;

        DeviceConnectionStatus connection_state = device_info.getActualDeviceConnectionStatus();

        Log.d(TAG, "deviceStateChange : " + UtilityFunctions.padDeviceType(device_info.device_type) + " : is_saved_data_valid = " + is_saved_data_valid);

        switch(device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                in_accelerometer_mode = device_info.isDeviceInRawAccelerometerMode();

                // getVitalSignTypeForDeviceType above has got the validity of Heart Rate already
                is_saved_data_valid &= main_activity_interface.isSavedMeasurementValid(VitalSignType.RESPIRATION_RATE);

                poor_signal_in_last_minute_or_no_beats_detected = main_activity_interface.isPoorSignalInLastMinute() || main_activity_interface.hasNoBeatsDetectedTimerFired();
            }
            break;

            case SENSOR_TYPE__ALGORITHM:
            case SENSOR_TYPE__MANUAL_VITAL:
            {
                connection_state = DeviceConnectionStatus.CONNECTED;
            }
            break;

            // Special case for BP: if data is valid, force connected state so we display the data. Otherwise use the existing value
            // The BP devices auto disconnected after sending their measurement(s) but we do not want the RHS saying "Attempting To Reconnect"
            case SENSOR_TYPE__BLOOD_PRESSURE:
            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                //noinspection StatementWithEmptyBody
                if(connection_state == DeviceConnectionStatus.UNBONDED)
                {
                    // Do nothing - leave it as it is so the Pairing Failed warning shows properly.
                }
                else if (device_info.isDeviceSessionInProgress())
                {
                    connection_state = is_saved_data_valid ? DeviceConnectionStatus.CONNECTED : connection_state;
                }
                else
                {
                    connection_state = DeviceConnectionStatus.NOT_PAIRED;
                }
            }
            break;

            // Forcing connected state so that Fora temperature is displayed, similar to as done for Blood pressure above
            case SENSOR_TYPE__TEMPERATURE:
            {
                if (device_info.device_type == DeviceType.DEVICE_TYPE__FORA_IR20)
                {
                    if (device_info.isDeviceSessionInProgress())
                    {
                        connection_state = is_saved_data_valid ? DeviceConnectionStatus.CONNECTED : DeviceConnectionStatus.DISCONNECTED;
                    }
                }
            }
            break;

            // Hide Setup mode button if necessary
            case SENSOR_TYPE__SPO2:
            {
                if (device_info.supports_setup_mode == false)
                {
                    setCheckBoxVisible(checkBoxPulseOxSetupMode, View.INVISIBLE);
                }
            }
            break;

            default:
                break;
        }
        
        // Log that we're doing a state change
        Log.d(log_graph_states, "deviceStateChange : " + device_info.getSensorTypeAndDeviceTypeAsString() + ". leads_off_state = " + leads_off_state + ". connection_state = " + connection_state + ". setup_mode_state = " + in_setup_mode + ". is_saved_data_valid = " + is_saved_data_valid);

        /* 
         * Logic: if device is not connected over Bluetooth, that takes precedence. We go to the "Disconnected" state.
         * Otherwise, if leads are off, we don't display the graph at all - go to leads off mode.
         * If leads are on and device connected:
         *  - check graph state - setup mode or normal mode - and display the appropriate graph.
         *  - check data validity and display appropriate message on RHS
         */

        // Sort out Graph state
        if(connection_state == DeviceConnectionStatus.UNBONDED)
        {
            current_graph_state = GraphDisplayStates.DEVICE_UNBONDED;
        }
        else if((connection_state == DeviceConnectionStatus.DISCONNECTED) || (connection_state == DeviceConnectionStatus.SEARCHING))
        {
            current_graph_state = GraphDisplayStates.DISCONNECT_MODE;
        }
        else if(connection_state == DeviceConnectionStatus.NOT_PAIRED)
        {
            current_graph_state = GraphDisplayStates.DEVICE_REMOVED;
        }
        else if(leads_off_state)
        {
            // Leads Off detected
            if (in_accelerometer_mode)
            {
                current_graph_state = GraphDisplayStates.ACCELEROMETER_MODE;
            }
            else
            {
                current_graph_state = GraphDisplayStates.LEADS_OFF_MODE;
            }
        }
        else if(poor_signal_in_last_minute_or_no_beats_detected)
        {
            if(in_setup_mode)
            {
                current_graph_state = GraphDisplayStates.SETUP_MODE;
            }
            else if (in_accelerometer_mode)
            {
                current_graph_state = GraphDisplayStates.ACCELEROMETER_MODE;
            }
            else
            {
                current_graph_state = GraphDisplayStates.POOR_SIGNAL_OR_NO_BEATS_DETECTED;
            }
        }
        else 
        {
            if(in_setup_mode)
            {
                // In Setup Mode
                current_graph_state = GraphDisplayStates.SETUP_MODE;
            }
            else if(in_accelerometer_mode)
            {
                // In Accelerometer Mode
                current_graph_state = GraphDisplayStates.ACCELEROMETER_MODE;
            }
            else
            {
                // Normal Mode
                current_graph_state = GraphDisplayStates.NORMAL_GRAPH_MODE;
            }
        }

        // Sort out RHS state
        if(connection_state == DeviceConnectionStatus.NOT_PAIRED)
        {
            if (is_saved_data_valid)
            {
                data_display_state = RightHandSideMeasurementDisplayStates.REMOVED_BUT_MEASUREMENT_STILL_VALID;
            }
            else
            {
                data_display_state = RightHandSideMeasurementDisplayStates.REMOVED;
            }
        }
        else if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE && device_info.nonin_playback_ongoing)
        {
            data_display_state = RightHandSideMeasurementDisplayStates.DOWNLOADING_DATA;
        }
        else if(is_saved_data_valid)
        {
            data_display_state = RightHandSideMeasurementDisplayStates.SHOW_VALUE;
        }
        else
        {
            // Invalid data, so show waiting for data
            data_display_state = RightHandSideMeasurementDisplayStates.WAITING_FOR_DATA;
        }


        Log.d(log_graph_states, "deviceStateChange : connection_state = " + connection_state + " : current_graph_state = " + current_graph_state + " : right_measurement_display_state = " + data_display_state);


        if (device_info.sensor_type != SensorType.SENSOR_TYPE__INVALID)
        {
            // LHS connection status
            switch (connection_state)
            {
                case CONNECTED:
                case DFU:
                {
                    connection_display_state = LeftHandSideConnectionDisplayStates.DEVICE_CONNECTED;
                }
                break;

                case SEARCHING:
                case FOUND:
                case DISCONNECTED:
                case UNBONDED:
                {
                    connection_display_state = LeftHandSideConnectionDisplayStates.DEVICE_DISCONNECTED;
                }
                break;

                case NOT_PAIRED:
                {
                    connection_display_state = LeftHandSideConnectionDisplayStates.REMOVED;
                }
                break;

                default:
                {
                    connection_display_state = LeftHandSideConnectionDisplayStates.INVALID;
                }
                break;
            }

            if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
            {
                // There are TWO vital signs for this device....so a bit of a fudge
                changeDeviceConnectionDisplayState(device_info, connection_display_state, VitalSignType.HEART_RATE);
                changeDeviceConnectionDisplayState(device_info, connection_display_state, VitalSignType.RESPIRATION_RATE);
            }
            else
            {
                changeDeviceConnectionDisplayState(device_info, connection_display_state, vital_sign_type);
            }

            if(all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state != current_graph_state)
            {
                Log.i(log_graph_states, "deviceStateChange : changing graph state : " + device_info.getSensorTypeAndDeviceTypeAsString() + " from " + all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state + " -> " + current_graph_state);

                all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state = current_graph_state;

                changeDeviceGraphState(device_info, current_graph_state);
            }
            else
            {
                Log.w(log_graph_states, "deviceStateChange : NOT changing graph state : " + device_info.getSensorTypeAndDeviceTypeAsString());
            }

            // Right hand side measurement display
            if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
            {
                // There are TWO vital signs for this device....so a bit of a fudge
                changeDeviceMeasurementAndConnectionDisplayState(device_info, data_display_state, is_saved_data_valid, VitalSignType.HEART_RATE);
                changeDeviceMeasurementAndConnectionDisplayState(device_info, data_display_state, is_saved_data_valid, VitalSignType.RESPIRATION_RATE);
            }
            else
            {
                changeDeviceMeasurementAndConnectionDisplayState(device_info, data_display_state, is_saved_data_valid, vital_sign_type);
            }
        }
    }


    private void changeDeviceMeasurementAndConnectionDisplayState(DeviceInfo device_info, RightHandSideMeasurementDisplayStates data_display_state, boolean is_saved_data_valid, VitalSignType vital_sign_type)
    {
        if (all_patient_vitals_display_items.getDisplayItem(vital_sign_type).right_measurement_display_state != data_display_state)
        {
            Log.i(TAG, "all_patient_vitals_display_items.getDisplayItem(vital_sign_type).right_measurement_display_state != data_display_state");
        }

        if (is_saved_data_valid)
        {
            Log.i(TAG, "is_saved_data_valid");
        }

        if ((all_patient_vitals_display_items.getDisplayItem(vital_sign_type).right_measurement_display_state != data_display_state) || is_saved_data_valid)
        {
            all_patient_vitals_display_items.getDisplayItem(vital_sign_type).right_measurement_display_state = data_display_state;

            PatientVitalsDisplayItems measurement_display_items = all_patient_vitals_display_items.getDisplayItem(vital_sign_type);

            Log.i(TAG, "changeDeviceMeasurementAndConnectionDisplayState : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : " + measurement_display_items.right_measurement_display_state);

            try
            {
                switch (measurement_display_items.right_measurement_display_state)
                {
                    case WAITING_FOR_DATA:
                    {
                        //Show waiting for data in Right hand side
                        showWaitingForData(measurement_display_items.measurement_display_items);

                        if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE)
                        {
                            updateNoninBleCheckBoxesBeforeOrAfterPlayback(device_info, vital_sign_type);
                        }
                    }
                    break;

                    case REMOVED:
                    {
                        // Show Device Removed in Right hand side
                        showDeviceRemoved(measurement_display_items.measurement_display_items);
                    }
                    break;

                    case REMOVED_BUT_MEASUREMENT_STILL_VALID:
                    {
                        // Show measurement on the right hand side
                        showVitalsMeasurement(vital_sign_type, main_activity_interface.getSavedMeasurement(vital_sign_type), measurement_display_items.measurement_display_items);
                    }
                    break;

                    /*
                     * This case is used only for Nonin BLE Playback
                     */
                    case DOWNLOADING_DATA:
                    {
                        String current_timestamp = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(main_activity_interface.getNtpTimeNowInMilliseconds());

                        // Show waiting for data in Right hand side
                        showDownloadingPreviousData(measurement_display_items.measurement_display_items, current_timestamp);

                        if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE)
                        {
                            Log.d(TAG, "calling disableCheckboxBecauseNoninPlaybackIsRunning from changeDeviceMeasurementAndConnectionDisplayState");

                            disableCheckboxBecauseNoninPlaybackIsRunning(checkBoxPulseOxSetupMode);
                        }
                    }
                    break;

                    case SHOW_VALUE:
                    {
                        // Update right hand side - measurement

                        // Setup mode for Nonin BLE if playback is no longer ongoing
                        if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE)
                        {
                            updateNoninBleCheckBoxesBeforeOrAfterPlayback(device_info, vital_sign_type);
                        }

                        if (vital_sign_type == VitalSignType.RESPIRATION_RATE)
                        {
                            //TODO - why do we need this???
                            MeasurementRespirationRate measurement_respiration_rate = (MeasurementRespirationRate) main_activity_interface.getSavedMeasurement(vital_sign_type);

                            if (measurement_respiration_rate.respiration_rate == ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM)
                            {
                                // Show waiting for data in Right hand side
                                showWaitingForData(all_patient_vitals_display_items.getDisplayItem(vital_sign_type).measurement_display_items);
                            }
                            else
                            {
                                showVitalsMeasurement(vital_sign_type, measurement_respiration_rate, all_patient_vitals_display_items.getDisplayItem(vital_sign_type).measurement_display_items);
                            }
                        }
                        else
                        {
                            showVitalsMeasurement(vital_sign_type, main_activity_interface.getSavedMeasurement(vital_sign_type), measurement_display_items.measurement_display_items);
                        }
                    }
                    break;

                    default:
                        break;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "changeDeviceMeasurementAndConnectionDisplayState Exception : " + e);
            }
        }
        else
        {
            Log.w(log_graph_states, "deviceStateChange : NOT changing display state " + device_info.getSensorTypeAndDeviceTypeAsString());
        }
    }


    private void changeDeviceConnectionDisplayState(DeviceInfo device_info, LeftHandSideConnectionDisplayStates connection_display_state, VitalSignType vital_sign_type)
    {
        if ((all_patient_vitals_display_items.getDisplayItem(vital_sign_type).connection_display_state != connection_display_state))
        {
            Log.i(TAG, "changeDeviceConnectionDisplayState : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : " + connection_display_state);

            all_patient_vitals_display_items.getDisplayItem(vital_sign_type).connection_display_state = connection_display_state;

            try
            {
                switch (connection_display_state)
                {
                    case DEVICE_CONNECTED:
                    {
                        showDeviceConnectionStatus(device_info, true, false);
                    }
                    break;

                    case DEVICE_DISCONNECTED:
                    {
                        showDeviceConnectionStatus(device_info, false, false);
                    }
                    break;

                    case REMOVED:
                    {
                        showDeviceConnectionStatus(device_info, false, true);
                    }
                    break;

                    default:
                        break;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "changeDeviceConnectionDisplayState Exception : " + e);
            }
        }
        else
        {
            Log.w(log_graph_states, "changeDeviceConnectionDisplayState : NOT changing display state " + device_info.getSensorTypeAndDeviceTypeAsString());
        }
    }


    private void removeLifetouchAccelerometerModeIfShown()
    {
        removeFragmentIfShown(lifetouch_raw_accelerometer_mode_fragment);
    }


    private void removeLifetouchLeadsOffIfShown()
    {
        removeFragmentIfShown(lifetouch_not_attached_fragment);
    }


    private void removeLifetouchPoorSignalIfShown()
    {
        removeFragmentIfShown(lifetouch_poor_signal_fragment);
    }


    private void addLifetouchNormalModeIfNotAdded()
    {
        replaceFragmentIfSafe(R.id.fragment_graph_lifetouch, lifetouch_normal_mode_fragment);

        system_commands.reportPatientOrientation();
    }


    private void changeDeviceGraphState(DeviceInfo device_info, GraphDisplayStates current_graph_state)
    {
        VitalSignType vital_sign_type = main_activity_interface.getVitalSignTypeForDeviceInfo(device_info);

        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state = current_graph_state;

                changeDeviceGraphState(device_info, vital_sign_type);
                changeDeviceCheckboxState(device_info, vital_sign_type);

            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            case SENSOR_TYPE__SPO2:
            {
                changeDeviceGraphState(device_info, vital_sign_type);
                changeDeviceCheckboxState(device_info, vital_sign_type);
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                changeDeviceGraphState(device_info, vital_sign_type);
            }
            break;

            case SENSOR_TYPE__ALGORITHM:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__EARLY_WARNING_SCORE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_early_warning_scores, early_warning_scores_graph_fragment);
                    }
                    break;
                }
            }
            break;

            case SENSOR_TYPE__MANUAL_VITAL:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_heart_rate, manually_entered_heart_rate_graph_fragment);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_respiration_rate, manually_entered_respiration_rate_graph_fragment);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_temperature, manually_entered_temperature_graph_fragment);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_SPO2:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_spo2, manually_entered_spo2_graph_fragment);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_blood_pressure, manually_entered_blood_pressure_graph_fragment);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_weight, manually_entered_graph_fragment_weight);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_capillary_refill_time, manually_entered_graph_fragment_capillary_refill_time);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_consciousness_level, manually_entered_graph_fragment_consciousness_level);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_respiration_distress, manually_entered_graph_fragment_respiration_distress);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_supplemental_oxygen, manually_entered_graph_fragment_supplemental_oxygen);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_family_or_nurse_concern, manually_entered_graph_fragment_family_or_nurse_concern);
                    }
                    break;

                    case DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_manually_entered_urine_output, manually_entered_graph_fragment_urine_output);
                    }
                    break;
                }
            }
            break;
        }
    }


    private void changeDeviceGraphState(DeviceInfo device_info, VitalSignType vital_sign_type)
    {
        Log.d(TAG, "changeDeviceGraphState : " + device_info.getSensorTypeAndDeviceTypeAsString() + " = " + all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state);

        switch (all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state)
        {
            case NORMAL_GRAPH_MODE:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        removeLifetouchLeadsOffIfShown();
                        removeLifetouchPoorSignalIfShown();
                        removeLifetouchAccelerometerModeIfShown();
                        removeLifetouchOptionsOverlayIfShown();

                        addLifetouchNormalModeIfNotAdded();
                    }
                    break;

                    case SENSOR_TYPE__TEMPERATURE:
                    {
                        removeLifetempLeadsOffIfShown();

                        addLifetempNormalModeIfNotAdded();
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        removePulseOxLeadsOffIfShown();

                        removePulseOxDisconnectedBecauseOfLeadsOffIfShown();

                        addPulseOxNormalModeIfNotAdded();

                        // Hide Setup mode button if necessary
                        if (device_info.supports_setup_mode)
                        {
                            setupCheckBoxesForNormalMode(checkBoxPulseOxSetupMode);
                        }
                        else
                        {
                            setCheckBoxVisible(checkBoxPulseOxSetupMode, View.INVISIBLE);
                        }
                    }
                    break;

                    case SENSOR_TYPE__BLOOD_PRESSURE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_blood_pressure, blood_pressure_graph_fragment);

                        // Add the Device Unbonded fragment to the main graph view
                        removeFragmentIfShown(blood_pressure_unbonded_fragment);
                    }
                    break;

                    case SENSOR_TYPE__WEIGHT_SCALE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_weight_scale, weight_scale_graph_fragment);

                        // Add the Device Unbonded fragment to the main graph view
                        //removeFragmentIfShown(blood_pressure_unbonded_fragment);
                    }
                    break;
                }
            }
            break;

            case DEVICE_REMOVED:
            case DISCONNECT_MODE:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        removeLifetouchLeadsOffIfShown();
                        removeLifetouchPoorSignalIfShown();
                        removeLifetouchOptionsOverlayIfShown();
                        removeLifetouchAccelerometerModeIfShown();

                        addLifetouchNormalModeIfNotAdded();
                    }
                    break;

                    case SENSOR_TYPE__TEMPERATURE:
                    {
                        removeLifetempLeadsOffIfShown();

                        addLifetempNormalModeIfNotAdded();
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        // Get Pulse Ox device info and check the leads-off counter.
                        DeviceInfo pulse_ox_device_info = main_activity_interface.getDeviceByType(device_info.sensor_type);

                        Log.d(TAG, "changePulseOxGraphState : DEVICE_DISCONNECTED pulse_ox_device_info.counter_leads_off_after_last_valid_data = " + pulse_ox_device_info.counter_leads_off_after_last_valid_data );

                        removePulseOxLeadsOffIfShown();

                        // If leads-off counter is greater than or equal to (600 - margin_count) then disconnection is because of Leads-off
                        if(pulse_ox_device_info.counter_leads_off_after_last_valid_data >= (TOTAL_LEADS_OFF_COUNT_BEFORE_DISCONNECTION_PULSE_OX - MARGIN_LEADS_OFF_COUNT_BEFORE_DISCONNECTION_PULSE_OX))
                        {
                            String timestamp = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(pulse_ox_device_info.timestamp_leads_off_disconnection);

                            // Leads-off detected more than 10 mins. Display
                            showPulseOxDisconnectedBecauseOfLeadsOff(timestamp);
                        }
                        else
                        {
                            Log.d(TAG, "changePulseOxGraphState : DEVICE_DISCONNECTED Pulse Ox disconnected without leads-off");

                            addPulseOxNormalModeIfNotAdded();
                        }

                        setupCheckBoxesForLeadsOffOrDisconnected(checkBoxPulseOxSetupMode);
                    }
                    break;

                    case SENSOR_TYPE__BLOOD_PRESSURE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_blood_pressure, blood_pressure_graph_fragment);
                    }
                    break;

                    case SENSOR_TYPE__WEIGHT_SCALE:
                    {
                        replaceFragmentIfSafe(R.id.fragment_graph_weight_scale, weight_scale_graph_fragment);
                    }
                    break;
                }
            }
            break;
            
            case LEADS_OFF_MODE:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        removeLifetouchPoorSignalIfShown();
                        removeLifetouchOptionsOverlayIfShown();

                        // First put the normal fragment in the main graph view, as it is displayed behind the leads off fragment
                        addLifetouchNormalModeIfNotAdded();

                        // Add the leads off fragment to the main graph view
                        addFragmentIfSafe(R.id.fragment_graph_lifetouch, lifetouch_not_attached_fragment);

                        if (checkBoxLifetouchConfigurable != null && lifetouch_configurable_checkbox_state != ConfigurableCheckboxStates.POINCARE_MODE)
                        {
                            checkBoxLifetouchConfigurable.setChecked(false);
                        }
                    }
                    break;

                    case SENSOR_TYPE__TEMPERATURE:
                    {
                        // First put the normal fragment in the main graph view, as it is displayed behind the leads off fragment
                        addLifetempNormalModeIfNotAdded();

                        // Add the leads off fragment to the main graph view
                        showLifetempLeadsOffIfNotAdded();
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        // First remove Pulse Ox turn-off
                        removePulseOxDisconnectedBecauseOfLeadsOffIfShown();

                        // Then put the normal fragment in the main graph view, as it is displayed behind the leads off fragment
                        addPulseOxNormalModeIfNotAdded();

                        // Add the leads off fragment to the main graph view
                        showPulseOxLeadsOff();

                        setupCheckBoxesForLeadsOffOrDisconnected(checkBoxPulseOxSetupMode);
                    }
                    break;
                }
            }
            break;

            case POOR_SIGNAL_OR_NO_BEATS_DETECTED:
            {
                if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
                {
                    removeLifetouchLeadsOffIfShown();
                    removeLifetouchOptionsOverlayIfShown();

                    // First put the normal fragment in the main graph view, as it is displayed behind the leads off fragment
                    addLifetouchNormalModeIfNotAdded();

                    // Add the Low Beat Amplitude fragment to the main graph view
                    addFragmentIfSafe(R.id.fragment_graph_lifetouch, lifetouch_poor_signal_fragment);
                }
            }
            break;

            case SETUP_MODE:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        removeLifetouchLeadsOffIfShown();
                        removeLifetouchPoorSignalIfShown();
                        removeLifetouchOptionsOverlayIfShown();

                        replaceFragmentIfSafe(R.id.fragment_graph_lifetouch, lifetouch_setup_mode_fragment);
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        showPulseOxSetupMode();

                        setupCheckBoxesForSetupMode(checkBoxPulseOxSetupMode);
                    }
                    break;
                }
            }
            break;

            case ACCELEROMETER_MODE:
            {
                if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
                {
                    removeLifetouchLeadsOffIfShown();
                    removeLifetouchPoorSignalIfShown();

                    replaceFragmentIfSafe(R.id.fragment_graph_lifetouch, lifetouch_raw_accelerometer_mode_fragment);
                }
            }
            break;

            case DEVICE_UNBONDED:
            {
                if (device_info.sensor_type == SensorType.SENSOR_TYPE__BLOOD_PRESSURE)
                {
                    replaceFragmentIfSafe(R.id.fragment_graph_blood_pressure, blood_pressure_graph_fragment);

                    // Add the Device Unbonded fragment to the main graph view
                    addFragmentIfSafe(R.id.fragment_graph_blood_pressure, blood_pressure_unbonded_fragment);
                }
                else if (device_info.sensor_type == SensorType.SENSOR_TYPE__WEIGHT_SCALE)
                {
                    replaceFragmentIfSafe(R.id.fragment_graph_weight_scale, weight_scale_graph_fragment);

//TODO??
                    // Add the Device Unbonded fragment to the main graph view
                    //addFragmentIfSafe(R.id.fragment_graph_weight_scale, blood_pressure_unbonded_fragment);
                }
            }

            default:
            {
                Log.e(log_graph_states, "changeDeviceGraphState : lifetouch_graph_state = " + all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state.toString() + " is UNHANDLED");
            }
            break;
        }
    }


    private void changeDeviceCheckboxState(DeviceInfo device_info, VitalSignType vital_sign_type)
    {
        Log.d(TAG, "changeDeviceCheckboxState : graph_state = " + all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state);

        switch (all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state)
        {
            case NORMAL_GRAPH_MODE:
            case POOR_SIGNAL_OR_NO_BEATS_DETECTED:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        setupCheckBoxesForNormalMode(checkBoxLifetouchConfigurable);

                        // Show Options button
                        setCheckBoxVisible(checkBoxLifetouchOptions, View.VISIBLE);

                        resumeLifetouchConfigurableCheckboxBasedOnPreviousSelection();
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        // Hide Setup mode button if necessary
                        if (device_info.supports_setup_mode)
                        {
                            setupCheckBoxesForNormalMode(checkBoxPulseOxSetupMode);
                        }
                        else
                        {
                            setCheckBoxVisible(checkBoxPulseOxSetupMode, View.INVISIBLE);
                        }
                    }
                    break;
                }
            }
            break;

            case DEVICE_REMOVED:
            case DISCONNECT_MODE:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        setCheckBoxVisible(checkBoxLifetouchConfigurable, View.INVISIBLE);
                        setCheckBoxVisible(checkBoxLifetouchOptions, View.INVISIBLE);

                        setLifetouchPatientOrientation(PatientPositionOrientation.ORIENTATION_UNKNOWN, false);
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        setCheckBoxVisible(checkBoxPulseOxSetupMode, View.INVISIBLE);
                    }
                    break;
                }
            }
            break;

            case LEADS_OFF_MODE:
            {
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        if(lifetouch_configurable_checkbox_state != ConfigurableCheckboxStates.POINCARE_MODE)
                        {
                            setCheckBoxVisible(checkBoxLifetouchConfigurable, View.INVISIBLE);
                        }

                        setCheckBoxVisible(checkBoxLifetouchOptions, View.VISIBLE);

                        setLifetouchPatientOrientation(PatientPositionOrientation.ORIENTATION_UNKNOWN, false);
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        setCheckBoxVisible(checkBoxPulseOxSetupMode, View.INVISIBLE);
                    }
                    break;
                }
            }
            break;

            case SETUP_MODE:
            {
                if(device_info.isDeviceUnderServerControl())
                {
                    setupCheckboxForServerInited(device_info);
                }
                else if (device_info.isDeviceInPeriodicSetupMode())
                {
                    setupCheckboxForPeriodicSetupMode(device_info);
                }
                else
                {
                    createDeviceSetupModeCheckBoxOnCheckedChangeListener(device_info);

                    if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
                    {
                        if (checkBoxLifetouchConfigurable != null)
                        {
                            setCheckBoxTextAndMakeVisible(checkBoxLifetouchConfigurable, getString(R.string.textSetupMode));
                            checkBoxLifetouchConfigurable.setChecked(true);
                        }
                    }
                }
            }
            break;

            case ACCELEROMETER_MODE:
            {
                if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
                {
                    setCheckBoxChecked(checkBoxLifetouchConfigurable, false);

                    if(device_info.isDeviceUnderServerControl())
                    {
                        setupCheckboxForServerInited(device_info);
                    }
                    else
                    {
                        setCheckBoxLifetouchConfigurableVisibleAndDisplayString(getString(R.string.motion_mode));
                        createLifetouchRawAccelerometerCheckBoxOnCheckedChangeListener();
                        setCheckBoxChecked(checkBoxLifetouchConfigurable, true);
                    }
                }
            }
            break;

            default:
            {
                Log.e(log_graph_states, "changeDeviceCheckboxState : graph_state = " + all_patient_vitals_display_items.getDisplayItem(vital_sign_type).graph_state.toString() + " is UNHANDLED");
            }
            break;
        }
    }


    private void showLifetempLeadsOffIfNotAdded()
    {
        addFragmentIfSafe(R.id.fragment_graph_temperature, lifetemp_not_attached_fragment);
    }


    private void removeLifetempLeadsOffIfShown()
    {
        removeFragmentIfShown(lifetemp_not_attached_fragment);
    }


    private void addLifetempNormalModeIfNotAdded()
    {
        replaceFragmentIfSafe(R.id.fragment_graph_temperature, temperature_graph_fragment);
    }


    private void showPulseOxLeadsOff()
    {
        addFragmentIfSafe(R.id.fragment_graph_pulse_ox, pulse_ox_not_attached_fragment);
    }


    private void showPulseOxDisconnectedBecauseOfLeadsOff(String disconnection_timestamp)
    {
        if(isFragmentNotAddedButSafeToAdd(pulse_ox_disconnected_because_of_leads_off, getActivity()))
        {
            Bundle mBundle = new Bundle();
            mBundle.putString("disconnection_timestamp", disconnection_timestamp);
            pulse_ox_disconnected_because_of_leads_off.setArguments(mBundle);

            getChildFragmentManager().beginTransaction().add(R.id.fragment_graph_pulse_ox, pulse_ox_disconnected_because_of_leads_off).commit();
        }
        else
        {
            Log.e(TAG,"showPulseOxDisconnectedBecauseOfLeadsOff : getActivity() is Null");
        }
    }


    private void removePulseOxLeadsOffIfShown()
    {
        removeFragmentIfShown(pulse_ox_not_attached_fragment);
    }


    private void addPulseOxNormalModeIfNotAdded()
    {
        replaceFragmentIfSafe(R.id.fragment_graph_pulse_ox, pulse_ox_normal_mode_fragment);
    }


    private void removePulseOxDisconnectedBecauseOfLeadsOffIfShown()
    {
        removeFragmentIfShown(pulse_ox_disconnected_because_of_leads_off);
    }


    private void showPulseOxSetupMode()
    {
        replaceFragmentIfSafe(R.id.fragment_graph_pulse_ox, pulse_ox_setup_mode_fragment);
    }


    public void scaleAllGraphs(ScaleGestureDetector detector)
    {
        Log.e(TAG, "scaleAllGraphs");

        if(UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
        {
            lifetouch_normal_mode_fragment.scaleGraph(detector);
        }

        for (FragmentGraph fragment : list_of_fragment_graphs_sensor)
        {
            scaleFragmentIfAddedAndResumed(fragment, detector);
        }

        for (FragmentGraph fragment : list_of_fragment_graphs_algorithm)
        {
            scaleFragmentIfAddedAndResumed(fragment, detector);
        }

        for (FragmentGraphManuallyEntered fragment : list_of_fragment_graph_manually_entered)
        {
            scaleFragmentIfAddedAndResumed(fragment, detector);
        }

        for (FragmentGraphManuallyEnteredBlocks fragment : list_of_fragment_graph_manually_entered_blocks)
        {
            scaleFragmentIfAddedAndResumed(fragment, detector);
        }
    }


    private void scaleFragmentIfAddedAndResumed(FragmentGraph fragment, ScaleGestureDetector detector)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragment.scaleGraph(detector);
        }
    }

    
    public void scrollOrScale(long minX, long maxX)
    {
        Log.e(TAG, "Graph Scroll Or Scale : minX = " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(minX + main_activity_interface.getSessionStartDate()) + " : maxX = " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(maxX + main_activity_interface.getSessionStartDate()));

        if(UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
        {
            lifetouch_normal_mode_fragment.scrollOrScaleToTimeWindow(minX, maxX);
        }

        for (FragmentGraph fragment : list_of_fragment_graphs_sensor)
        {
            scrollFragmentIfAddedAndResumed(fragment, minX, maxX);
        }

        for (FragmentGraph fragment : list_of_fragment_graphs_algorithm)
        {
            scrollFragmentIfAddedAndResumed(fragment, minX, maxX);
        }

        for (FragmentGraphManuallyEntered fragment : list_of_fragment_graph_manually_entered)
        {
            scrollFragmentIfAddedAndResumed(fragment, minX, maxX);
        }

        for (FragmentGraphManuallyEnteredBlocks fragment : list_of_fragment_graph_manually_entered_blocks)
        {
            scrollFragmentIfAddedAndResumed(fragment, minX, maxX);
        }
    }


    private void scrollFragmentIfAddedAndResumed(FragmentGraph fragment, long minX, long maxX)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragment.scrollOrScaleToTimeWindow(minX, maxX);
        }
    }


    private void showLifetouchOptionsOverlay()
    {
        addFragmentIfSafe(R.id.fragment_graph_lifetouch, lifetouch_options_fragment);
    }


    public void removeLifetouchOptionsOverlayIfShown()
    {
        removeFragmentIfShown(lifetouch_options_fragment);

        // Force Lifetouch Options button to be unchecked since the overlay is no longer shown
        if (checkBoxLifetouchOptions != null)
        {
            checkBoxLifetouchOptions.setChecked(false);
        }
    }


    private void setCheckBoxLifetouchConfigurableVisibleAndDisplayString(String text)
    {
        setCheckBoxTextAndMakeVisible(checkBoxLifetouchConfigurable, text);
    }


    private void setCheckBoxTextAndMakeVisible(CheckBox checkbox, String text)
    {
        if(checkbox != null)
        {
            checkbox.setText(text);
        }

        setCheckBoxVisible(checkbox, View.VISIBLE);
    }


    private boolean checkForMatchingDevice(ArrayList<DeviceInfo> devices_in_use, SensorType sensor_type_to_match)
    {
        for(DeviceInfo device_info : devices_in_use)
        {
            if(device_info.sensor_type == sensor_type_to_match)
            {
                return true;
            }
        }

        return false;
    }


    private void resumeLifetouchConfigurableCheckboxBasedOnPreviousSelection()
	{
        Log.e(TAG, "resumeLifetouchConfigurableCheckboxBasedOnPreviousSelection " + lifetouch_configurable_checkbox_state);

        switch (lifetouch_configurable_checkbox_state)
		{
			case ACCELEROMETER_MODE:
			{
                setCheckBoxLifetouchConfigurableVisibleAndDisplayString(getString(R.string.motion_mode));
                createLifetouchRawAccelerometerCheckBoxOnCheckedChangeListener();
            }
            break;
            
			case POINCARE_MODE:
			{
                setCheckBoxLifetouchConfigurableVisibleAndDisplayString(getString(R.string.poincare));
                createLifetouchPoincareCheckBoxOnCheckedChangeListener();
            }
            break;

            case SETUP_MODE:
            case INVALID:
            default:
            {
                setCheckBoxLifetouchConfigurableVisibleAndDisplayString(getString(R.string.textSetupMode));
                createLifetouchSetupModeCheckBoxOnCheckedChangeListener();
            }
            break;
        }
    }


    private void setCheckBoxVisible(CheckBox checkbox, int visible)
    {
        if(checkbox != null)
        {
            checkbox.setVisibility(visible);
        }
    }


    private void setCheckBoxChecked(CheckBox checkbox, boolean checked)
    {
        if(checkbox != null)
        {
            checkbox.setChecked(checked);
        }
    }


    // Show or hide Setup mode blobs
    public void showSetupModeBlobs(boolean show)
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
        {
            lifetouch_normal_mode_fragment.showSetupModeBlobs(show);
        }

        if (UtilityFunctions.isFragmentAddedAndResumed(pulse_ox_normal_mode_fragment))
        {
            pulse_ox_normal_mode_fragment.showSetupModeBlobs(show);
        }
    }


    public void redrawGraphSetupModeIndicators(SensorType sensor_type)
    {
        if (main_activity_interface.getSetupModeBlobsShouldBeShown())
        {

            ArrayList<SetupModeLog> setup_mode_logs = main_activity_interface.getSetupModeLog(sensor_type);

            switch (sensor_type)
            {
                case SENSOR_TYPE__LIFETOUCH:
                    {
                    if (UtilityFunctions.isFragmentAddedAndResumed(lifetouch_normal_mode_fragment))
                    {
                        lifetouch_normal_mode_fragment.redrawSetupModeLogGraph(setup_mode_logs);
                    }
                }
                break;

                case SENSOR_TYPE__SPO2:
                    {
                    if (UtilityFunctions.isFragmentAddedAndResumed(pulse_ox_normal_mode_fragment))
                    {
                        pulse_ox_normal_mode_fragment.redrawSetupModeLogGraph(setup_mode_logs);
                    }
                }
                break;
            }
        }
    }
}
