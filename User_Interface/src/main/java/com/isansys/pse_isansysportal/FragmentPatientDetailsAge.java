package com.isansys.pse_isansysportal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ajit.customseekbar.CustomSeekBar;
import com.ajit.customseekbar.ProgressItem;
import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.measurements.VitalSignType;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import static android.widget.LinearLayout.HORIZONTAL;

public class FragmentPatientDetailsAge extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private RadioGroup radioGroupPatientAge;

    private CustomSeekBar seekBarLifetouchHeartRate;
    private CustomSeekBar seekBarLifetouchRespirationRate;
    private CustomSeekBar seekBarLifetemp;
    private CustomSeekBar seekBarSpO2;
    private CustomSeekBar seekBarBloodPressure;

    private LinearLayout linearLayoutPatientDetailsAgeRangeTextExplaining;
    private LinearLayout linearLayoutPictureAgeRange;

    private TextView textTextExplainingWhatRadioButtonsDo;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.patient_details_age_range_2, container, false); // Inflate the layout for this fragment

        main_activity_interface.showNextButton(false);

        textTextExplainingWhatRadioButtonsDo = v.findViewById(R.id.textTextExplainingWhatRadioButtonsDo);

        linearLayoutPatientDetailsAgeRangeTextExplaining = v.findViewById(R.id.linearLayoutPatientDetailsAgeRangeTextExplaining);
        linearLayoutPictureAgeRange = v.findViewById(R.id.linearLayoutPictureAgeRange);

        // Make the default (image holding) linear layout GONE. If we do not include one in the XML then screwy stuff happens to the layout
        LinearLayout ll = v.findViewById(R.id.linearLayoutPatientAgeRangeA);
        ll.setVisibility(View.GONE);

        radioGroupPatientAge = v.findViewById(R.id.radioGroupPatientAge);
        radioGroupPatientAge.setOnCheckedChangeListener((group, checkedId) -> {
            main_activity_interface.showNextButton(true);

            int radioButtonID = group.getCheckedRadioButtonId();
            View radioButton = group.findViewById(radioButtonID);
            int index = group.indexOfChild(radioButton);

            // Check to ensure a radio button is actually checked before referencing it by its index. Otherwise -1 is pass into the function
            if (index >= 0)
            {
                main_activity_interface.PatientAgeSelected(index);
            }

            initThresholdLevels();
        });


        ArrayList<ThresholdSet> thresholdSets = main_activity_interface.getEarlyWarningScoreThresholdSets();

        // Create Radio Buttons and Images for each of the Threshold Set Age Blocks
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


                // Create Image
                ImageView imageView = new ImageView(getContext());
                int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(dimensionInDp, dimensionInDp));

                byte[] byteArray = age_block_detail.image_binary;
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imageView.setImageBitmap(bitmap);

                LinearLayout linearLayout = new LinearLayout(getContext());
                // Width = WRAP_CONTENT. Height = WRAP_CONTENT. Weight = 1;
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                linearLayout.setOrientation(HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);

                // Add Image to Linear Layout
                linearLayout.addView(imageView);

                // Add Linear Layout to Parent Linear Layout
                linearLayoutPictureAgeRange.addView(linearLayout);
            }
        }


        seekBarLifetouchHeartRate = v.findViewById(R.id.seekBarLifetouchHeartRate);
        seekBarLifetouchHeartRate.setEnabled(false);

        seekBarLifetouchRespirationRate = v.findViewById(R.id.seekBarLifetouchRespirationRate);
        seekBarLifetouchRespirationRate.setEnabled(false);

        seekBarLifetemp = v.findViewById(R.id.seekBarLifetemp);
        seekBarLifetemp.setEnabled(false);

        seekBarSpO2 = v.findViewById(R.id.seekBarPulseOx);
        seekBarSpO2.setEnabled(false);

        seekBarBloodPressure = v.findViewById(R.id.seekBarBloodPressure);
        seekBarBloodPressure.setEnabled(false);
        
        return v;
    }


    public void writeTextTextExplainingWhatRadioButtonsDo(String thresholdSetName)
    {
        String displayString = thresholdSetName + " " + getResources().getString(R.string.early_warning_score_thresholds_below);
        textTextExplainingWhatRadioButtonsDo.setText(displayString);
    }


    private void initThresholdLevels()
    {
        initHeartRateThresholdLevels();
        initRespirationRateThresholdLevels();
        initTemperatureThresholdLevels();
        initSpO2ThresholdLevels();
        initBloodPressureThresholdLevels();
    }
    
    
    private ArrayList<ProgressItem> generateThresholdBars(Bundle bundle, boolean decimal_point)
    {
        ArrayList<ProgressItem> progressItemList = new ArrayList<>();
        ProgressItem mProgressItem;
        ArrayList<GraphColourBand> graph_colour_bands;
        
        // Get the colour bands from the Bundle
        graph_colour_bands = bundle.getParcelableArrayList("graph_colour_bands");

        assert graph_colour_bands != null;

        try
        {
            double smallest_number = graph_colour_bands.get(0).greater_than_or_equal_value;
            double largest_number = graph_colour_bands.get(graph_colour_bands.size() - 1).less_than_value;
            
            double range = largest_number - smallest_number;
            
            // Convert the Graph Colour bands to percentages
            for (GraphColourBand graph_colour_band : graph_colour_bands)
            {
                mProgressItem = new ProgressItem();

                double temp = (graph_colour_band.less_than_value - smallest_number) / range;
                temp = temp * 100;
                mProgressItem.progressItemPercentage = (float)temp;
                
                mProgressItem.color = graph_colour_band.band_colour;

                if (decimal_point)
                {
                    BigDecimal rounded_value;

                    rounded_value = new BigDecimal(String.valueOf(graph_colour_band.less_than_value)).setScale(1, BigDecimal.ROUND_HALF_UP);

                    mProgressItem.label = rounded_value.toString();
                }
                else
                {
                    mProgressItem.label = String.valueOf((int)graph_colour_band.less_than_value);
                }

                progressItemList.add(mProgressItem);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "generateThresholdBars : e = " + e.toString());
        }

        return progressItemList;
    }

    
    private void initHeartRateThresholdLevels()
    {
        Bundle bundle = main_activity_interface.getGraphColourBands(VitalSignType.HEART_RATE);

        try
        {
            ArrayList<ProgressItem> progressItemList = generateThresholdBars(bundle, false);

            seekBarLifetouchHeartRate.initData(progressItemList);
            seekBarLifetouchHeartRate.invalidate();
        }
        catch (Exception e)
        {
            Log.d(TAG, "initHeartRateThresholdLevels : e = " + e.toString());
        }
    }
    
    private void initRespirationRateThresholdLevels()
    {
        Bundle bundle = main_activity_interface.getGraphColourBands(VitalSignType.RESPIRATION_RATE);

        try
        {
            ArrayList<ProgressItem> progressItemList = generateThresholdBars(bundle, false);
            
            seekBarLifetouchRespirationRate.initData(progressItemList);
            seekBarLifetouchRespirationRate.invalidate();
        }
        catch (Exception e)
        {
            Log.e(TAG, "initRespirationRateThresholdLevels : e = " + e.toString());
        }
    }
    
    
    private void initTemperatureThresholdLevels()
    {
        Bundle bundle = main_activity_interface.getGraphColourBands(VitalSignType.TEMPERATURE);

        try
        {
            ArrayList<ProgressItem> progressItemList = generateThresholdBars(bundle, true);
            
            seekBarLifetemp.initData(progressItemList);
            seekBarLifetemp.invalidate();
        }
        catch(Exception e)
        {
            Log.e(TAG, "initTemperatureThresholdLevels : e = " + e.toString());
        }
    }
    
    
    private void initSpO2ThresholdLevels()
    {
        Bundle bundle = main_activity_interface.getGraphColourBands(VitalSignType.SPO2);

        try
        {
            ArrayList<ProgressItem> progressItemList = generateThresholdBars(bundle, false);
            
            seekBarSpO2.initData(progressItemList);
            seekBarSpO2.invalidate();
        }
        catch(Exception e)
        {
            Log.e(TAG, "initSpO2ThresholdLevels : e = " + e.toString());
        }
    }

    
    private void initBloodPressureThresholdLevels()
    {
        Bundle bundle = main_activity_interface.getGraphColourBands(VitalSignType.BLOOD_PRESSURE);

        try
        {
            ArrayList<ProgressItem> progressItemList = generateThresholdBars(bundle, false);
            
            seekBarBloodPressure.initData(progressItemList);
            seekBarBloodPressure.invalidate();
        }
        catch(Exception e)
        {
            Log.e(TAG, "initBloodPressureThresholdLevels : e = " + e.toString());
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();

        PatientInfo patientInfo = main_activity_interface.getPatientInfo();
        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = patientInfo.getThresholdSetAgeBlockDetails();

        if (main_activity_interface.getUsaMode())
        {
            thresholdSetAgeBlockDetail = main_activity_interface.getThresholdSetAgeBlockDetailIdForAdults();

            linearLayoutPatientDetailsAgeRangeTextExplaining.setVisibility(View.GONE);
            linearLayoutPictureAgeRange.setVisibility(View.GONE);
            radioGroupPatientAge.setVisibility(View.GONE);
        }

        setupCheckboxesAndThresholdDisplay(thresholdSetAgeBlockDetail, patientInfo);
    }


    private void setupCheckboxesAndThresholdDisplay(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail, PatientInfo patientInfo)
    {
        if (thresholdSetAgeBlockDetail != null)
        {
            int buttonNumber = main_activity_interface.getRadioButtonNumberFromThresholdSetAgeBlockDetail(thresholdSetAgeBlockDetail);
            // buttonNumber will be -1 if a matching age block wasn't found - meaning the gateway has synced new thresholds
            // and the stored age block is no longer valid.
            if(buttonNumber < 0)
            {
                // Set the stored age block to null
                patientInfo.setThresholdSetAgeBlockDetails(null);
            }
            else
            {
                setupPatientAgeCheckboxes(buttonNumber, thresholdSetAgeBlockDetail.display_name);

                initThresholdLevels();
            }
        }
    }


    private void setupPatientAgeCheckboxes(int buttonNumber, String display_name)
    {
        View v = getView();
        if (v != null)
        {
            radioGroupPatientAge = v.findViewById(R.id.radioGroupPatientAge);

            ((RadioButton) radioGroupPatientAge.getChildAt(buttonNumber)).setChecked(true);

            writeTextTextExplainingWhatRadioButtonsDo(display_name);
        }
    }
}
