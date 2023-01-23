package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.isansys.pse_isansysportal.enums.KeyboardMode;

public class FragmentAnnotationTextEntry extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    public static final String KEY_KEYBOARD_MODE = "keyboard_mode";

    private EditText editTextAnnotationEntry;
    private Button buttonFinishedTyping;

    private String annotation_string = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.annotation_text_entry_view, container, false);

        TextView textTop = v.findViewById(R.id.textTop);
        textTop.setText(getResources().getString(R.string.please_enter_the_annotation));

        buttonFinishedTyping = v.findViewById(R.id.buttonFinishedTyping);
        buttonFinishedTyping.setOnClickListener(x -> {
            dismissKeyboard();
            buttonFinishedTyping.setVisibility(View.GONE);
        });

        editTextAnnotationEntry = v.findViewById(R.id.editTextAnnotationEntry);
        editTextAnnotationEntry.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                annotation_string = s.toString();

                main_activity_interface.annotationEnteredViaKeyboard(annotation_string);

                main_activity_interface.touchEventSoResetTimers();

                String annotation_string_stripped_of_whitespace = annotation_string.replaceAll("\\s+","");

                main_activity_interface.showNextButton(!annotation_string_stripped_of_whitespace.isEmpty());
            }
        });


        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            KeyboardMode keyboardMode = KeyboardMode.values()[bundle.getInt(KEY_KEYBOARD_MODE)];

            if (keyboardMode == KeyboardMode.BLUETOOTH)
            {
                Log.e(TAG, "KeyboardMode.BLUETOOTH");

                buttonFinishedTyping.setVisibility(View.GONE);
            }
            else
            {
                Log.e(TAG, "KeyboardMode.ONSCREEN");

                forceShowKeyboardIfAnnotationStringEmpty();
            }

            editTextAnnotationEntry.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus)
                {
                    if (keyboardMode == KeyboardMode.BLUETOOTH)
                    {
                        expandTextBox(true);
                    }
                    else
                    {
                        expandTextBox(false);

                        buttonFinishedTyping.setVisibility(View.VISIBLE);
                    }
                }
            });

            editTextAnnotationEntry.requestFocus();
        }

        return v;
    }


    @Override
    public void onDestroyView()
    {
        if (editTextAnnotationEntry != null)
        {
            main_activity_interface.dismissOnScreenKeyboard(getContext(), editTextAnnotationEntry);
        }

        super.onDestroyView();
    }


    private void dismissKeyboard()
    {
        // Strip any leading and trailing Returns (and then update the screen)
        annotation_string = main_activity_interface.removeLeadingCharacters(annotation_string, '\n');
        annotation_string = main_activity_interface.removeTrailingCharacters(annotation_string, '\n');
        editTextAnnotationEntry.setText(annotation_string);

        editTextAnnotationEntry.clearFocus();

        main_activity_interface.dismissOnScreenKeyboard(getContext(), editTextAnnotationEntry);

        expandTextBox(true);
    }


    private void forceShowKeyboardIfAnnotationStringEmpty()
    {
        if(annotation_string.isEmpty())
        {
            expandTextBox(false);
            main_activity_interface.forceShowOnScreenKeyboard(editTextAnnotationEntry);
        }
    }


    private void expandTextBox(boolean expand)
    {
        int lines;

        if (main_activity_interface.isScreenLandscape())
        {
            lines = 8;
        }
        else
        {
            lines = 18;
        }

        if (expand)
        {
            if (main_activity_interface.isScreenLandscape())
            {
                lines = 20;
            }
            else
            {
                lines = 30;
            }
        }

        editTextAnnotationEntry.setLines(lines);
        editTextAnnotationEntry.setMinLines(lines);
    }
}
