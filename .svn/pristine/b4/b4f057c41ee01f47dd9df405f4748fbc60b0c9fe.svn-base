package com.isansys.pse_isansysportal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isansys.common.ThresholdSetAgeBlockDetail;

import org.jetbrains.annotations.NotNull;

public class FragmentHeader extends FragmentIsansys
{
    private TextView textHospitalPatientId;
    private TextView textWardName;
    private TextView textBedName;
    
    private ImageView imageHeaderAgeRange;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.header, container, false);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        View v = getView();
        if (v != null)
        {
            textHospitalPatientId = v.findViewById(R.id.textHeaderPatientId);
            setHospitalPatientIdText("");

            textWardName = v.findViewById(R.id.textHeaderWardName);
            textBedName = v.findViewById(R.id.textHeaderBedName);

            imageHeaderAgeRange = v.findViewById(R.id.imageHeaderAgeRange);

            setAgeRange(main_activity_interface.getThresholdSetAgeBlockDetails());

            TextView textHeaderCaution = v.findViewById(R.id.textHeaderCaution);
            if (main_activity_interface.inNonCeMode())
            {
                textHeaderCaution.setVisibility(View.VISIBLE);
            }
            else
            {
                textHeaderCaution.setVisibility(View.GONE);
            }
        }
    }


    public void reset()
    {
        setBedDetailsText("", "");

        setHospitalPatientIdText("");

        setAgeRange(null);
    }


    public void setHospitalPatientIdText(String desired_text)
    {
        if (textHospitalPatientId != null)
        {
            if (desired_text.equals(""))
            {
                textHospitalPatientId.setVisibility(View.INVISIBLE);
            }
            else
            {
                String patient_string = getResources().getString(R.string.textPatient);
                patient_string += " : " + desired_text;

                textHospitalPatientId.setText(patient_string);

                textHospitalPatientId.setVisibility(View.VISIBLE);
            }
        }
    }
    
    
    public void setBedDetailsText(String ward_name, String bed_name)
    {
        if (textWardName != null)
        {
            textWardName.setText(ward_name);
            textBedName.setText(bed_name);
        }
    }
    
    
    public void setAgeRange(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetails)
    {
        if (thresholdSetAgeBlockDetails != null)
        {
            byte[] byteArray = thresholdSetAgeBlockDetails.image_binary;
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageHeaderAgeRange.setImageBitmap(bitmap);
            imageHeaderAgeRange.setVisibility(View.VISIBLE);
        }
        else
        {
            imageHeaderAgeRange.setVisibility(View.INVISIBLE);
        }
    }
}
