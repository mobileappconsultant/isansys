package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.isansys.pse_isansysportal.enums.AnnotationEntryType;

public class FragmentAnnotationConfirmation extends FragmentIsansys implements View.OnClickListener
{
    private TableLayout tableLayoutPredefinedAnnotationTable;
    private TableLayout tableLayoutKeyboardAnnotationTable;

    private AutoResizeTextView textAnnotationCondition;
    private AutoResizeTextView textAnnotationAction;
    private AutoResizeTextView textAnnotationOutcome;

    private TextView textKeyboardAnnotationTime;
    private AutoResizeTextView textKeyboardAnnotation;

    private Button buttonBigButtonTop;
    private Button buttonBigButtonBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.annotation_confirmation, container, false);

        buttonBigButtonTop = v.findViewById(R.id.buttonBigButtonTop);
        buttonBigButtonTop.setOnClickListener(this);
        buttonBigButtonTop.setVisibility(View.VISIBLE);

        buttonBigButtonBottom = v.findViewById(R.id.buttonBigButtonBottom);
        buttonBigButtonBottom.setOnClickListener(this);
        buttonBigButtonBottom.setVisibility(View.INVISIBLE);

        tableLayoutPredefinedAnnotationTable = v.findViewById(R.id.tableLayoutPredefinedAnnotationTable);
        tableLayoutPredefinedAnnotationTable.setVisibility(View.GONE);

        tableLayoutKeyboardAnnotationTable = v.findViewById(R.id.tableLayoutKeyboardAnnotationTable);
        tableLayoutKeyboardAnnotationTable.setVisibility(View.GONE);

        textAnnotationCondition = v.findViewById(R.id.textAnnotationCondition);
        textAnnotationAction = v.findViewById(R.id.textAnnotationAction);
        textAnnotationOutcome = v.findViewById(R.id.textAnnotationOutcome);

        textKeyboardAnnotationTime = v.findViewById(R.id.textKeyboardAnnotationTime);
        textKeyboardAnnotation = v.findViewById(R.id.textKeyboardAnnotation);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        AnnotationBeingEntered annotation_being_entered = main_activity_interface.getAnnotationBeingEntered();

        if (annotation_being_entered.getAnnotationEntryType() == AnnotationEntryType.PREDEFINED_FROM_SERVER)
        {
            tableLayoutPredefinedAnnotationTable.setVisibility(View.VISIBLE);

            textAnnotationCondition.setText(annotation_being_entered.getConditions());
            textAnnotationAction.setText(annotation_being_entered.getActions());
            textAnnotationOutcome.setText(annotation_being_entered.getOutcomes());
        }
        else
        {
            tableLayoutKeyboardAnnotationTable.setVisibility(View.VISIBLE);

            textKeyboardAnnotationTime.setText(TimestampConversion.convertDateToHumanReadableStringDayHoursMinutes(annotation_being_entered.getTimestamp()));
            textKeyboardAnnotation.setText(annotation_being_entered.getAnnotation());
        }

        resetButtons(true);
    }


    public enum ButtonMode
    {
        VALIDITY_TIME_ENTER,
        VALIDITY_TIME_CONFIRMATION
    }


    private ButtonMode button_mode = ButtonMode.VALIDITY_TIME_ENTER;


    @Override
    public void onClick(View v)
    {
        if(getActivity() != null)
        {
            int id = v.getId();
            if (id == R.id.buttonBigButtonTop)
            {
                switch (button_mode)
                {
                    case VALIDITY_TIME_ENTER:
                    {
                        buttonBigButtonBottom.setText(getResources().getString(R.string.confirm));
                        showBottomControlButton(true);

                        buttonBigButtonTop.setText(getResources().getString(R.string.cancel));
                        showTopControlButton(true);

                        button_mode = ButtonMode.VALIDITY_TIME_CONFIRMATION;
                    }
                    break;

                    case VALIDITY_TIME_CONFIRMATION:
                    {
                        // User pressed Cancel
                        resetButtons(true);
                    }
                    break;
                }
            }
            else if (id == R.id.buttonBigButtonBottom)
            {
                switch (button_mode)
                {
                    case VALIDITY_TIME_ENTER:
                    {
                        // Button invisible
                    }
                    break;

                    case VALIDITY_TIME_CONFIRMATION:
                    {
                        main_activity_interface.storeAnnotation();
                    }
                    break;
                }
            }
        }
    }


    private void showBottomControlButton(boolean show_button)
    {
        if (show_button)
        {
            buttonBigButtonBottom.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonBigButtonBottom.setVisibility(View.INVISIBLE);
        }
    }


    private void showTopControlButton(boolean show_button)
    {
        if (show_button)
        {
            buttonBigButtonTop.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonBigButtonTop.setVisibility(View.INVISIBLE);
        }
    }


    private void resetButtons(boolean show_top_button)
    {
        showBottomControlButton(false);

        buttonBigButtonTop.setText(getResources().getString(R.string.enter));

        showTopControlButton(show_top_button);

        button_mode = ButtonMode.VALIDITY_TIME_ENTER;
    }
}
