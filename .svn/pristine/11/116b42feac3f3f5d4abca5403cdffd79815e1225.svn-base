/*
 * Copyright 2015 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This code comes from https://github.com/Ereza/CustomActivityOnCrash

package com.isansys.patientgateway;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CustomErrorActivity extends Activity
{
    private String getVersionNumberOfApp()
    {
        String version_name = "";

        PackageInfo package_info;
        try
        {
            package_info = getPackageManager().getPackageInfo("com.isansys.patientgateway", 0);
            version_name = package_info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return version_name;
    }

    private String getWardName()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("gateways_assigned_ward_name", "X");
    }

    private String getBedName()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("gateways_assigned_bed_name", "X");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Remove Android title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove Android notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.activity_custom_error);

        //These four methods are available for you to use:
        //CustomActivityOnCrash.getStackTraceFromIntent(getIntent()): gets the stack trace as a string
        //CustomActivityOnCrash.getAllErrorDetailsFromIntent(context, getIntent()): returns all error details including stacktrace as a string
        //CustomActivityOnCrash.getRestartActivityClassFromIntent(getIntent()): returns the class of the restart activity to launch, or null if none
        //CustomActivityOnCrash.getEventListenerFromIntent(getIntent()): returns the event listener that must be passed to restartApplicationWithIntent or closeApplication

        String timestamp = DateFormat.getDateTimeInstance().format(new Date());
        String version_string = "v" + getVersionNumberOfApp();
        String gateway_name = BluetoothAdapter.getDefaultAdapter().getName();

        String info = timestamp;
        info += "   -   ";
        info += version_string;

        TextView textViewInformationLineOne = findViewById(R.id.textViewInformationLineOne);
        textViewInformationLineOne.setText(info);

        info = getWardName() + "/" + getBedName();
        info += "   -   ";
        info += gateway_name;

        TextView textViewInformationLineTwo = findViewById(R.id.textViewInformationLineTwo);
        textViewInformationLineTwo.setText(info);

        TextView textErrorDetails = findViewById(R.id.textErrorDetails);
        textErrorDetails.setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));

        final Class<? extends Activity> restartActivityClass = CustomActivityOnCrash.getRestartActivityClassFromIntent(getIntent());
        final CustomActivityOnCrash.EventListener eventListener = CustomActivityOnCrash.getEventListenerFromIntent(getIntent());

        Button buttonRestart = findViewById(R.id.buttonRestart);
        buttonRestart.setOnClickListener(v -> {
            Intent intent = new Intent(CustomErrorActivity.this, restartActivityClass);
            CustomActivityOnCrash.restartApplicationWithIntent(CustomErrorActivity.this, intent, eventListener);
        });
    }
}
