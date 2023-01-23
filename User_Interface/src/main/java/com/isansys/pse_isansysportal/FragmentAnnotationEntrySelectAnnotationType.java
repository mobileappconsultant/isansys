package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.isansys.pse_isansysportal.enums.AnnotationEntryType;

public class FragmentAnnotationEntrySelectAnnotationType extends FragmentIsansysWithTimestamp implements View.OnClickListener
{
    private TextView textSelection;

    private Button buttonAnnotationKeyboard;
    private Button buttonAnnotationPredefined;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.annotation_type, container, false);

        buttonAnnotationKeyboard = v.findViewById(R.id.buttonAnnotationKeyboard);
        buttonAnnotationKeyboard.setOnClickListener(this);

        buttonAnnotationPredefined = v.findViewById(R.id.buttonAnnotationPredefined);
        buttonAnnotationPredefined.setOnClickListener(this);

        textSelection = v.findViewById(R.id.textVitalSignValue);
        textSelection.setText(R.string.blank_string);

        return v;
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonAnnotationKeyboard)
        {
            main_activity_interface.annotationTypeSelected(AnnotationEntryType.CUSTOM_VIA_ONSCREEN_KEYBOARD);

            textSelection.setText(buttonAnnotationKeyboard.getText());
        }
        else if (id == R.id.buttonAnnotationPredefined)
        {
            main_activity_interface.annotationTypeSelected(AnnotationEntryType.PREDEFINED_FROM_SERVER);

            textSelection.setText(buttonAnnotationPredefined.getText());
        }
    }
}
