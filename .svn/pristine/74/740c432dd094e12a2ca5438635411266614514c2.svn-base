package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.isansys.common.measurements.VitalSignType;

public class FragmentKeypadVitalSignEntry extends FragmentIsansys implements OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    private TextView textEnteredValue;

    private Button buttonNumberZero;
    private Button buttonNumberOne;
    private Button buttonNumberTwo;
    private Button buttonNumberThree;
    private Button buttonNumberFour;
    private Button buttonNumberFive;
    private Button buttonNumberSix;
    private Button buttonNumberSeven;
    private Button buttonNumberEight;
    private Button buttonNumberNine;
    
    private Button buttonDecimalOrSlash;
   
    private Button buttonDelete;
    
    private String enteredText;

    private VitalSignType vital_sign_type;
    
    private boolean button_decimal_or_slash_enabled;


    public enum KeyPadEntryType
    {
        INVALID,
        VITAL_SIGN,
        PATIENT_CASE_ID
    }

    private KeyPadEntryType keyPadEntryType = KeyPadEntryType.INVALID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.manual_vital_signs_entry__keypad, container, false);
        
        buttonNumberZero = v.findViewById(R.id.buttonNumberZero);
        buttonNumberZero.setOnClickListener(this);

        buttonNumberOne = v.findViewById(R.id.buttonNumberOne);
        buttonNumberOne.setOnClickListener(this);

        buttonNumberTwo = v.findViewById(R.id.buttonNumberTwo);
        buttonNumberTwo.setOnClickListener(this);

        buttonNumberThree = v.findViewById(R.id.buttonNumberThree);
        buttonNumberThree.setOnClickListener(this);

        buttonNumberFour = v.findViewById(R.id.buttonNumberFour);
        buttonNumberFour.setOnClickListener(this);

        buttonNumberFive = v.findViewById(R.id.buttonNumberFive);
        buttonNumberFive.setOnClickListener(this);

        buttonNumberSix = v.findViewById(R.id.buttonNumberSix);
        buttonNumberSix.setOnClickListener(this);

        buttonNumberSeven = v.findViewById(R.id.buttonNumberSeven);
        buttonNumberSeven.setOnClickListener(this);

        buttonNumberEight = v.findViewById(R.id.buttonNumberEight);
        buttonNumberEight.setOnClickListener(this);

        buttonNumberNine = v.findViewById(R.id.buttonNumberNine);
        buttonNumberNine.setOnClickListener(this);

        buttonDecimalOrSlash = v.findViewById(R.id.buttonDecimal);
        buttonDecimalOrSlash.setOnClickListener(this);
        buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
        
        buttonDelete = v.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);

        textEnteredValue = v.findViewById(R.id.textEnteredValue);
        
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            int x = bundle.getInt(MainActivity.KEYPAD_ENTRY_TYPE, 0);
            keyPadEntryType = KeyPadEntryType.values()[x];

            if (keyPadEntryType == KeyPadEntryType.VITAL_SIGN)
            {
                x = bundle.getInt(MainActivity.VITAL_SIGN_TYPE, 0);
                vital_sign_type = VitalSignType.values()[x];
            }

            enteredText = bundle.getString(MainActivity.KEYPAD_INITIAL_VALUE, "");
            textEnteredValue.setText(enteredText);
        }

        if (keyPadEntryType == KeyPadEntryType.VITAL_SIGN)
        {
            updateScreenBasedOnVitalSignType();
        }
        else
        {
            button_decimal_or_slash_enabled = false;
        }

        validateText();

        return v;
    }


    private void updateScreenBasedOnVitalSignType()
    {
        switch (vital_sign_type)
        {
            case MANUALLY_ENTERED_HEART_RATE:
            case MANUALLY_ENTERED_RESPIRATION_RATE:
            case MANUALLY_ENTERED_SPO2:
            {
                button_decimal_or_slash_enabled = false;
            }
            break;
            
            case MANUALLY_ENTERED_TEMPERATURE:
            case MANUALLY_ENTERED_WEIGHT:
            {
                button_decimal_or_slash_enabled = true;

                if(buttonDecimalOrSlash != null)
                {
                    buttonDecimalOrSlash.setText(R.string.full_stop);
                }
                else
                {
                    Log.d(TAG, "updateScreenBasedOnVitalSignType : buttonDecimalOrSlash is not INITIALIZED");
                }

                buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
            }
            break;
            
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            {
                button_decimal_or_slash_enabled = true;

                if(buttonDecimalOrSlash != null)
                {
                    buttonDecimalOrSlash.setText(R.string.slash);
                }
                else
                {
                    Log.d(TAG, "updateScreenBasedOnVitalSignType : buttonDecimalOrSlash is not INITIALIZED");
                }

                buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
            }
            break;

            default:
                break;
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        enableKeypad(true);
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonNumberZero)
        {
            buttonNumberPressed("0");
        }
        else if (id == R.id.buttonNumberOne)
        {
            buttonNumberPressed("1");
        }
        else if (id == R.id.buttonNumberTwo)
        {
            buttonNumberPressed("2");
        }
        else if (id == R.id.buttonNumberThree)
        {
            buttonNumberPressed("3");
        }
        else if (id == R.id.buttonNumberFour)
        {
            buttonNumberPressed("4");
        }
        else if (id == R.id.buttonNumberFive)
        {
            buttonNumberPressed("5");
        }
        else if (id == R.id.buttonNumberSix)
        {
            buttonNumberPressed("6");
        }
        else if (id == R.id.buttonNumberSeven)
        {
            buttonNumberPressed("7");
        }
        else if (id == R.id.buttonNumberEight)
        {
            buttonNumberPressed("8");
        }
        else if (id == R.id.buttonNumberNine)
        {
            buttonNumberPressed("9");
        }
        else if (id == R.id.buttonDecimal)
        {
            buttonNumberPressed(buttonDecimalOrSlash.getText().toString());
        }
        else if (id == R.id.buttonDelete)
        {
            buttonDeletePressed();
        }
    }
    
    
    private void buttonNumberPressed(String character)
    {
        switch (character)
        {
            case ".":
                if (enteredText.contains("."))
                {
                    // Can only have a single . in the number

                    if (button_decimal_or_slash_enabled)
                    {
                        buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    // Need to check if '.' is pressed without integer
                    if(enteredText.length() >= 1)
                    {
                        // Integer is previously added.
                        enteredText = enteredText + character;

                        if (button_decimal_or_slash_enabled)
                        {
                            buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        // No integer is added before.
                        // Don't change the manually_entered_vital_sign
                        Log.d(TAG, "buttonDecimal pressed. But '.' is added before integer");
                    }
                }
                break;

            case "/":
                if (enteredText.contains("/"))
                {
                    // Can only have a single / in the number

                    if (button_decimal_or_slash_enabled)
                    {
                        buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    if(enteredText.length() >= 1)
                    {
                        // Integer is previously added.
                        enteredText = enteredText + character;

                        if (button_decimal_or_slash_enabled)
                        {
                            buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        // No integer is added before.
                        // Don't change the manually_entered_vital_sign
                        Log.d(TAG, "buttonDecimal pressed. But '/' is added before integer");
                    }
                }

                break;

            default:
                enteredText = enteredText + character;
                break;
        }

        // Only display special character for Temperature and Blood Pressure
        if (button_decimal_or_slash_enabled)
        {
            // Only display special character if Decimal is added before and '.' '/' are not added
            if((enteredText.length() >= 1))
            {
                if((!enteredText.contains(".")) && (!enteredText.contains("/")))
                {
                    buttonDecimalOrSlash.setVisibility(View.VISIBLE);
                }
                else
                {
                    buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
            }
        }
        
        textEnteredValue.setText(enteredText);

        validateText();
    }

    
    private void validateText()
    {
        if (keyPadEntryType == KeyPadEntryType.VITAL_SIGN)
        {
            enableKeypad(main_activity_interface.validateManuallyEnteredVitalSignValue(vital_sign_type, enteredText));
        }
        else if (keyPadEntryType == KeyPadEntryType.PATIENT_CASE_ID)
        {
            enableKeypad(true);
            main_activity_interface.validateEnteredContactId(enteredText);
        }
    }


    private void buttonDeletePressed()
    {
        if (enteredText.length() > 0)
        {
            enteredText = enteredText.substring(0, enteredText.length() - 1);
        }

        if(enteredText.length() >= 1)
        {
            if (enteredText.contains(".") || enteredText.contains("/"))
            {
                if (button_decimal_or_slash_enabled)
                {
                    buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                if (button_decimal_or_slash_enabled)
                {
                    buttonDecimalOrSlash.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            if (button_decimal_or_slash_enabled)
            {
                buttonDecimalOrSlash.setVisibility(View.INVISIBLE);
            }
        }
        
        textEnteredValue.setText(enteredText);

        validateText();
    }
    
    
    public void enableKeypad(boolean enable_numbers_and_decimal_or_slash)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            Drawable button;

            if (enable_numbers_and_decimal_or_slash)
            {
                button = ContextCompat.getDrawable(activity, R.drawable.button_green);

                buttonNumberZero.setOnClickListener(this);
                buttonNumberOne.setOnClickListener(this);
                buttonNumberTwo.setOnClickListener(this);
                buttonNumberThree.setOnClickListener(this);
                buttonNumberFour.setOnClickListener(this);
                buttonNumberFive.setOnClickListener(this);
                buttonNumberSix.setOnClickListener(this);
                buttonNumberSeven.setOnClickListener(this);
                buttonNumberEight.setOnClickListener(this);
                buttonNumberNine.setOnClickListener(this);
                buttonDecimalOrSlash.setOnClickListener(this);
            }
            else
            {
                button = ContextCompat.getDrawable(activity, R.drawable.button_gray);

                buttonNumberZero.setOnClickListener(null);
                buttonNumberOne.setOnClickListener(null);
                buttonNumberTwo.setOnClickListener(null);
                buttonNumberThree.setOnClickListener(null);
                buttonNumberFour.setOnClickListener(null);
                buttonNumberFive.setOnClickListener(null);
                buttonNumberSix.setOnClickListener(null);
                buttonNumberSeven.setOnClickListener(null);
                buttonNumberEight.setOnClickListener(null);
                buttonNumberNine.setOnClickListener(null);
                buttonDecimalOrSlash.setOnClickListener(null);
            }

            buttonNumberZero.setBackground(button);
            buttonNumberOne.setBackground(button);
            buttonNumberTwo.setBackground(button);
            buttonNumberThree.setBackground(button);
            buttonNumberFour.setBackground(button);
            buttonNumberFive.setBackground(button);
            buttonNumberSix.setBackground(button);
            buttonNumberSeven.setBackground(button);
            buttonNumberEight.setBackground(button);
            buttonNumberNine.setBackground(button);
            buttonDecimalOrSlash.setBackground(button);

            button = ContextCompat.getDrawable(activity, R.drawable.button_green);
            buttonDelete.setOnClickListener(this);
            buttonDelete.setBackground(button);
        }
    }
    

    public String getEnteredText()
    {
        return enteredText;
    }
}
