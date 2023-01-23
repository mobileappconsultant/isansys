package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isansys.pse_isansysportal.enums.KeyboardMode;

public class FragmentPatientDetails extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    public static final String KEY_KEYBOARD_MODE = "keyboard_mode";
    public static final String KEY_SHOW_ONSCREEN_KEYBOARD = "show_onscreen_keyboard";

    private EditText editHospitalPatientID;

    private LinearLayout linearLayoutPressDoneOrEnterWhenFinished;
    private TextView textViewPressDoneWhenFinished;

    private LinearLayout linearLayoutPatientDetailsShowNextArrow;
    private Button buttonAppendDatetime;

    private String desired_patient_id = "";

    private boolean patient_id_entry_allowed;

    public enum Stage
    {
    	ALL_INVISIBLE,
    	INITIAL,
    	EDITING,
    	EDITING_DONE,
        GOT_SERVER_RESPONSE,
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.patient_details, container, false);

        editHospitalPatientID = v.findViewById(R.id.editPatientID);

        buttonAppendDatetime = v.findViewById(R.id.buttonAppendDateTime);

        linearLayoutPressDoneOrEnterWhenFinished = v.findViewById(R.id.linearLayoutPressDoneWhenFinished);
        textViewPressDoneWhenFinished = v.findViewById(R.id.textViewPressDoneWhenFinished);
        ImageView imagePressDoneWhenFinishedDownArrow = v.findViewById(R.id.imagePressDoneWhenFinishedDownArrow);

        linearLayoutPatientDetailsShowNextArrow = v.findViewById(R.id.linearLayoutPatientDetailsShowNextArrow);


        setPageContentVisibilitiesToStage(Stage.INITIAL);
        
        editHospitalPatientID.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) 
            {
                Log.d(TAG, "afterTextChanged : callback executed");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) 
            {
                Log.d(TAG, "beforeTextChanged: callback executed");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) 
            {
                Log.d(TAG, "onTextChanged: callback executed");
                
                desired_patient_id = s.toString();
                main_activity_interface.setHospitalPatientId(desired_patient_id);

                main_activity_interface.touchEventSoResetTimers();

                setPageContentVisibilitiesToStage(Stage.EDITING);

                if (isPatientIdBlank())
                {
                    textViewPressDoneWhenFinished.setVisibility(View.INVISIBLE);
                    imagePressDoneWhenFinishedDownArrow.setVisibility(View.INVISIBLE);
                }
                else
                {
                    textViewPressDoneWhenFinished.setVisibility(View.VISIBLE);
                    imagePressDoneWhenFinishedDownArrow.setVisibility(View.VISIBLE);
                }
            }
        });


        editHospitalPatientID.setOnKeyListener((view, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
            {
                // Perform action on key press
                patientIdComplete();

                return true;
            }

            return false;
        });
        
        editHospitalPatientID.setOnEditorActionListener((x, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                Log.d(TAG, "onEditorAction action == DONE");

                patientIdComplete();
            }

            return false;
        });


        buttonAppendDatetime.setOnClickListener(x -> {
            if ((!main_activity_interface.getManufacturingModeEnabled()) && desired_patient_id.equals(""))
            {
                // No ID so show "touch here"
                setPageContentVisibilitiesToStage(Stage.INITIAL);
            }
            else
            {
                // Valid Patient ID or in Manufacturing Mode

                // Append datetime to desired_patient_id
                String string = desired_patient_id + "-" + TimestampConversion.convertDateToBchFormatHumanReadableStringYearMonthDayHoursMinutesSeconds(main_activity_interface.getNtpTimeNowInMilliseconds());
                editHospitalPatientID.setText(string);

                setPageContentVisibilitiesToStage(Stage.GOT_SERVER_RESPONSE);
            }
        });


        editHospitalPatientID.setOnClickListener(x -> {
            Log.d(TAG, "setOnClickListener : on click happened");

            if (patient_id_entry_allowed)
            {
                Log.d(TAG, "setOnClickListener : on click happened and is editable");

                setPageContentVisibilitiesToStage(Stage.EDITING);
            }
        });

        editHospitalPatientID.requestFocus();


        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            KeyboardMode keyboardMode = KeyboardMode.values()[bundle.getInt(KEY_KEYBOARD_MODE)];

            if (keyboardMode == KeyboardMode.BLUETOOTH)
            {
                Log.e(TAG, "KeyboardMode.BLUETOOTH");

                textViewPressDoneWhenFinished.setText(getResources().getString(R.string.press_enter_on_keyboard_when_finished));
                imagePressDoneWhenFinishedDownArrow.setVisibility(View.INVISIBLE);
            }
            else
            {
                Log.e(TAG, "KeyboardMode.ONSCREEN");

                boolean show_keyboard = bundle.getBoolean(KEY_SHOW_ONSCREEN_KEYBOARD);
                if (show_keyboard)
                {
                    Log.e(TAG, "Showing keyboard");

                    main_activity_interface.forceShowOnScreenKeyboard(editHospitalPatientID);
                }
                else
                {
                    Log.e(TAG, "Not showing keyboard");
                }
            }
        }

        return v;
    }

    private void patientIdComplete()
    {
        boolean server_link_enabled = main_activity_interface.getServerSyncingEnableStatusVariableOnly();
        boolean server_lookup_of_patient_name_from_patient_id_enabled = main_activity_interface.isServerLookupOfPatientNameFromPatientIdEnabled();

        Log.e(TAG, "server_link_enabled = " + server_link_enabled);
        Log.e(TAG, "server_lookup_of_patient_name_from_patient_id_enabled = " + server_lookup_of_patient_name_from_patient_id_enabled);

        if((server_link_enabled) && (server_lookup_of_patient_name_from_patient_id_enabled))
        {
            // Editing done, now need to check ID.
            setPageContentVisibilitiesToStage(Stage.EDITING_DONE);
        }
        else
        {
            // No Server Patient Name lookup, so continue without it
            if (isPatientIdBlank())
            {
                // Nothing entered for Hospital Patient ID so show "Touch Here"
                setPageContentVisibilitiesToStage(Stage.INITIAL);
            }
            else
            {
                // Have Hospital Patient ID, so go straight to OK
                setPageContentVisibilitiesToStage(Stage.EDITING_DONE);
            }
        }
    }

    private boolean isPatientIdBlank()
    {
        String desired_patient_id_stripped_of_whitespace = desired_patient_id.replaceAll("\\s+","");

        return desired_patient_id_stripped_of_whitespace.equals("");
    }


    @Override
    public void onResume()
    {
    	Log.d(TAG, "onResume");

        // Get the Patient ID from the Gateway. First time we get to Fragment Patient Details, this will be an empty string
        String patient_id = main_activity_interface.getPatientInfo().getHospitalPatientId();
        
        editHospitalPatientID.setText(patient_id);

        patient_id_entry_allowed = true;

        // Just start from the top, since can't be sure if server_link_enabled or not.
        setPageContentVisibilitiesToStage(Stage.INITIAL);

        if (main_activity_interface.getManufacturingModeEnabled())
        {
            buttonAppendDatetime.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonAppendDatetime.setVisibility(View.INVISIBLE);
        }
        
        super.onResume();
    }


    @Override
    public void onDestroyView()
    {
        if (editHospitalPatientID != null)
        {
            main_activity_interface.dismissOnScreenKeyboard(getContext(), editHospitalPatientID);
        }

        super.onDestroyView();
    }


    @Override
    public void onDestroy()
    {
        main_activity_interface.forceDismissPatientNamePopup();

        super.onDestroy();
    }
    
    
    private void setPageContentVisibilitiesToStage(Stage stage)
    {
        Log.d(TAG, "setPageContentVisibilitiesToStage : " + stage.toString());

    	switch(stage)
    	{
	    	case ALL_INVISIBLE:
	    	{
                linearLayoutPressDoneOrEnterWhenFinished.setVisibility(View.INVISIBLE);
	            buttonAppendDatetime.setVisibility(View.INVISIBLE);

	            showNextButton(false);
	    	}
	    	break;
	    	
	    	case INITIAL:
	    	{
	    		// Clear all previous ID
                String blank_string = getResources().getString(R.string.blank_string);
	    		editHospitalPatientID.setText(blank_string);
	    		main_activity_interface.setHospitalPatientId(blank_string);
	    		desired_patient_id = blank_string;

	    		// Everything else invisible
                linearLayoutPressDoneOrEnterWhenFinished.setVisibility(View.INVISIBLE);
	            buttonAppendDatetime.setVisibility(View.INVISIBLE);

	            showNextButton(false);
	    	}
	    	break;
	    	
	    	case EDITING:
	    	{
                // Show instructions to press "Done" when finished.
                linearLayoutPressDoneOrEnterWhenFinished.setVisibility(View.VISIBLE);
	    		
	    		// Everything else invisible
                buttonAppendDatetime.setVisibility(View.INVISIBLE);

                showNextButton(false);
	    	}
	    	break;
	    	
	    	case EDITING_DONE:
	    	{
                if (main_activity_interface.isServerLookupOfPatientNameFromPatientIdEnabled())
                {
                    if (!isPatientIdBlank())
                    {
                        showNextButton(false);
                    }
                }
                else
                {
                    showNextButton(true);
                }

                // Everything else invisible
                linearLayoutPressDoneOrEnterWhenFinished.setVisibility(View.INVISIBLE);
                buttonAppendDatetime.setVisibility(View.INVISIBLE);
	    	}
	    	break;
	    	
	    	case GOT_SERVER_RESPONSE:
	    	{
	    		// Everything else invisible
                linearLayoutPressDoneOrEnterWhenFinished.setVisibility(View.INVISIBLE);
                buttonAppendDatetime.setVisibility(View.INVISIBLE);
	    	}
	    	break;
    	}
    }


    private void showNextButton(boolean show)
    {
        if (show)
        {
            linearLayoutPatientDetailsShowNextArrow.setVisibility(View.VISIBLE);
            main_activity_interface.showNextButton(true);
        }
        else
        {
            linearLayoutPatientDetailsShowNextArrow.setVisibility(View.INVISIBLE);
            main_activity_interface.showNextButton(false);
        }
    }
}
