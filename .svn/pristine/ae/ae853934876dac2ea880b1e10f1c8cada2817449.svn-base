package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FragmentGatewayNotResponding extends FragmentIsansys implements OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    private Button buttonRestartGatewayApp;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.gateway_not_responding, container, false); // Inflate the layout for this fragment

        buttonRestartGatewayApp = v.findViewById(R.id.buttonRestartGatewayApp);
        buttonRestartGatewayApp.setOnClickListener(this);
        
        TextView lastPingTime = v.findViewById(R.id.textLastGatewayPingTime);
        
        String last_ping_time = main_activity_interface.getLastPingTime();

        lastPingTime.setText(last_ping_time);
        
        return v;
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.buttonRestartGatewayApp)
        {
            buttonRestartGatewayAppClick();
        }
    }


    private void buttonRestartGatewayAppClick()
    {
        main_activity_interface.restartGatewayApp();
        
        buttonRestartGatewayApp.setVisibility(View.INVISIBLE);
        buttonRestartGatewayApp.setClickable(false);
    }
}