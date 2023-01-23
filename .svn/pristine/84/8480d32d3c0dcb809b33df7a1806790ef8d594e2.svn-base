package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentGatewayConfigurationError extends FragmentIsansys implements View.OnClickListener
{
    private int text_id = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.gateway_not_configured_yet, container, false); // Inflate the layout for this fragment

        Button buttonBackToUnlockScreen = v.findViewById(R.id.buttonBackToUnlockScreen);
        buttonBackToUnlockScreen.setOnClickListener(this);

        TextView warning_text = v.findViewById(R.id.textGatewayNotConfiguredYet);

        warning_text.setText(text_id);

        return v;
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.buttonBackToUnlockScreen)
        {
            buttonBackToUnlockScreenClick();
        }
    }


    private void buttonBackToUnlockScreenClick()
    {
        main_activity_interface.lockScreenSelected();
    }

    public void setWarningText(int text_id)
    {
        this.text_id = text_id;
    }
}
