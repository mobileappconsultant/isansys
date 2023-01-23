package com.isansys.pse_isansysportal;

import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.ThresholdSetLevel;
import com.isansys.common.enums.MeasurementTypes;
import com.isansys.common.measurements.VitalSignType;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FragmentManualVitalSignsDisplayAll extends FragmentIsansys
{
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_heartRate;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_respirationRate;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_temperature;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_spO2;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_bloodPressure;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_weight;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_consciousnessLevel;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_supplementalOxygen;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_annotations;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_capillary_refill_time;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_respiration_distress;
    private FragmentManualVitalSignsDisplayIndividual fragmentManualVitalSignsDisplayIndividual_family_or_nurse_concern;

    private LinearLayout linearLayoutManualVitalSignSelectionHeartRate;
    private LinearLayout linearLayoutManualVitalSignSelectionRespirationRate;
    private LinearLayout linearLayoutManualVitalSignSelectionTemperature;
    private LinearLayout linearLayoutManualVitalSignSelectionSpO2;
    private LinearLayout linearLayoutManualVitalSignSelectionBloodPressure;
    private LinearLayout linearLayoutManualVitalSignSelectionConsciousnessLevel;
    private LinearLayout linearLayoutManualVitalSignSelectionSupplementalOxygen;
    private LinearLayout linearLayoutManualVitalSignSelectionCapillaryRefillTime;
    private LinearLayout linearLayoutManualVitalSignSelectionRespirationDistress;
    private LinearLayout linearLayoutManualVitalSignSelectionFamilyOrNurseConcern;
    private LinearLayout linearLayoutManualVitalSignSelectionWeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        v = inflater.inflate(R.layout.manual_vital_signs_display_all, container, false);

        linearLayoutManualVitalSignSelectionHeartRate = v.findViewById(R.id.linearLayoutManualVitalSignSelectionHeartRate);
        linearLayoutManualVitalSignSelectionHeartRate.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionRespirationRate = v.findViewById(R.id.linearLayoutManualVitalSignSelectionRespirationRate);
        linearLayoutManualVitalSignSelectionRespirationRate.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionTemperature = v.findViewById(R.id.linearLayoutManualVitalSignSelectionTemperature);
        linearLayoutManualVitalSignSelectionTemperature.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionSpO2 = v.findViewById(R.id.linearLayoutManualVitalSignSelectionSpO2);
        linearLayoutManualVitalSignSelectionSpO2.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionBloodPressure = v.findViewById(R.id.linearLayoutManualVitalSignSelectionBloodPressure);
        linearLayoutManualVitalSignSelectionBloodPressure.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionConsciousnessLevel = v.findViewById(R.id.linearLayoutManualVitalSignSelectionConsciousnessLevel);
        linearLayoutManualVitalSignSelectionConsciousnessLevel.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionSupplementalOxygen = v.findViewById(R.id.linearLayoutManualVitalSignSelectionSupplementalOxygen);
        linearLayoutManualVitalSignSelectionSupplementalOxygen.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionCapillaryRefillTime = v.findViewById(R.id.linearLayoutManualVitalSignSelectionCapillaryRefillTime);
        linearLayoutManualVitalSignSelectionCapillaryRefillTime.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionRespirationDistress = v.findViewById(R.id.linearLayoutManualVitalSignSelectionRespirationDistress);
        linearLayoutManualVitalSignSelectionRespirationDistress.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionFamilyOrNurseConcern = v.findViewById(R.id.linearLayoutManualVitalSignSelectionFamilyOrNurseConcern);
        linearLayoutManualVitalSignSelectionFamilyOrNurseConcern.setVisibility(View.GONE);

        linearLayoutManualVitalSignSelectionWeight = v.findViewById(R.id.linearLayoutManualVitalSignSelectionWeight);
        linearLayoutManualVitalSignSelectionWeight.setVisibility(View.GONE);

        return v;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        fragmentManualVitalSignsDisplayIndividual_heartRate = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_respirationRate = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_temperature = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_spO2 = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_bloodPressure = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_weight = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_consciousnessLevel = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_supplementalOxygen = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_annotations = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_capillary_refill_time = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_respiration_distress = new FragmentManualVitalSignsDisplayIndividual();
        fragmentManualVitalSignsDisplayIndividual_family_or_nurse_concern = new FragmentManualVitalSignsDisplayIndividual();

        Bundle bundle_heart_rate = new Bundle();
        bundle_heart_rate.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_HEART_RATE.ordinal());
        fragmentManualVitalSignsDisplayIndividual_heartRate.setArguments(bundle_heart_rate);

        Bundle bundle_respiration_rate = new Bundle();
        bundle_respiration_rate.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE.ordinal());
        fragmentManualVitalSignsDisplayIndividual_respirationRate.setArguments(bundle_respiration_rate);

        Bundle bundle_temperature = new Bundle();
        bundle_temperature.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_TEMPERATURE.ordinal());
        fragmentManualVitalSignsDisplayIndividual_temperature.setArguments(bundle_temperature);

        Bundle bundle_spo2 = new Bundle();
        bundle_spo2.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_SPO2.ordinal());
        fragmentManualVitalSignsDisplayIndividual_spO2.setArguments(bundle_spo2);

        Bundle bundle_blood_pressure = new Bundle();
        bundle_blood_pressure.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE.ordinal());
        fragmentManualVitalSignsDisplayIndividual_bloodPressure.setArguments(bundle_blood_pressure);

        Bundle bundle_weight = new Bundle();
        bundle_weight.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_WEIGHT.ordinal());
        fragmentManualVitalSignsDisplayIndividual_weight.setArguments(bundle_weight);

        Bundle bundle_consciousness_level = new Bundle();
        bundle_consciousness_level.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL.ordinal());
        fragmentManualVitalSignsDisplayIndividual_consciousnessLevel.setArguments(bundle_consciousness_level);

        Bundle bundle_supplemental_oxygen = new Bundle();
        bundle_supplemental_oxygen.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN.ordinal());
        fragmentManualVitalSignsDisplayIndividual_supplementalOxygen.setArguments(bundle_supplemental_oxygen);

        Bundle bundle_annotations = new Bundle();
        bundle_annotations.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_ANNOTATION.ordinal());
        fragmentManualVitalSignsDisplayIndividual_annotations.setArguments(bundle_annotations);

        Bundle bundle_capillary_refill_time = new Bundle();
        bundle_capillary_refill_time.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME.ordinal());
        fragmentManualVitalSignsDisplayIndividual_capillary_refill_time.setArguments(bundle_capillary_refill_time);

        Bundle bundle_respiration_distress = new Bundle();
        bundle_respiration_distress.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal());
        fragmentManualVitalSignsDisplayIndividual_respiration_distress.setArguments(bundle_respiration_distress);

        Bundle bundle_family_or_nurse_concern = new Bundle();
        bundle_family_or_nurse_concern.putInt(MainActivity.VITAL_SIGN_TYPE, VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal());
        fragmentManualVitalSignsDisplayIndividual_family_or_nurse_concern.setArguments(bundle_family_or_nurse_concern);
    }


    @Override
    public void onResume()
    {
        super.onResume();

		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

        PatientInfo patientInfo = main_activity_interface.getPatientInfo();
        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = patientInfo.getThresholdSetAgeBlockDetails();

        for (ArrayList<ThresholdSetLevel> device_specific_list : thresholdSetAgeBlockDetail.list_of_threshold_set_levels_by_measurement_type)
        {
            for (ThresholdSetLevel level : device_specific_list)
            {
                MeasurementTypes type = MeasurementTypes.values()[level.measurement_type];

                switch (type)
                {
                    case HEART_RATE:
                    {
                        linearLayoutManualVitalSignSelectionHeartRate.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionHeartRate, fragmentManualVitalSignsDisplayIndividual_heartRate);
                    }
                    break;

                    case RESPIRATION_RATE:
                    {
                        linearLayoutManualVitalSignSelectionRespirationRate.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionRespirationRate, fragmentManualVitalSignsDisplayIndividual_respirationRate);
                    }
                    break;

                    case TEMPERATURE:
                    {
                        linearLayoutManualVitalSignSelectionTemperature.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionTemperature, fragmentManualVitalSignsDisplayIndividual_temperature);
                    }
                    break;

                    case SPO2:
                    {
                        linearLayoutManualVitalSignSelectionSpO2.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionSpO2, fragmentManualVitalSignsDisplayIndividual_spO2);
                    }
                    break;

                    case BLOOD_PRESSURE:
                    {
                        linearLayoutManualVitalSignSelectionBloodPressure.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionBloodPressure, fragmentManualVitalSignsDisplayIndividual_bloodPressure);
                    }
                    break;

                    case WEIGHT:
                    {
                        linearLayoutManualVitalSignSelectionWeight.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionWeight, fragmentManualVitalSignsDisplayIndividual_weight);
                    }
                    break;

                    case CONSCIOUSNESS_LEVEL:
                    {
                        linearLayoutManualVitalSignSelectionConsciousnessLevel.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionConsciousnessLevel, fragmentManualVitalSignsDisplayIndividual_consciousnessLevel);
                    }
                    break;

                    case SUPPLEMENTAL_OXYGEN:
                    {
                        linearLayoutManualVitalSignSelectionSupplementalOxygen.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionSupplementalOxygen, fragmentManualVitalSignsDisplayIndividual_supplementalOxygen);
                    }
                    break;

                    case CAPILLARY_REFILL_TIME:
                    {
                        linearLayoutManualVitalSignSelectionCapillaryRefillTime.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionCapillaryRefillTime, fragmentManualVitalSignsDisplayIndividual_capillary_refill_time);
                    }
                    break;

                    case RESPIRATION_DISTRESS:
                    {
                        linearLayoutManualVitalSignSelectionRespirationDistress.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionRespirationDistress, fragmentManualVitalSignsDisplayIndividual_respiration_distress);
                    }
                    break;

                    case FAMILY_OR_NURSE_CONCERN:
                    {
                        linearLayoutManualVitalSignSelectionFamilyOrNurseConcern.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.frameLayoutManualVitalSignSelectionFamilyOrNurseConcern, fragmentManualVitalSignsDisplayIndividual_family_or_nurse_concern);
                    }
                    break;

                    default:
                        break;
                }
            }
        }

        transaction.replace(R.id.frameLayoutManualVitalSignSelectionAnnotations, fragmentManualVitalSignsDisplayIndividual_annotations);

        transaction.addToBackStack(null);
		transaction.commit();
    }
}
