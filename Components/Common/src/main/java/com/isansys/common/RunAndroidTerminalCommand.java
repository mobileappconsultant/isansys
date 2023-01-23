package com.isansys.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunAndroidTerminalCommand
{
    private final String TAG = "RunAndroidTerminalCmd";

    public RunAndroidTerminalCommand()
    {
    }

    public boolean run(String command)
    {
        try
        {
            boolean response = true;

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null)
            {
                Log.d(TAG,"******** OUTPUT ******** " + s);
            }

            // Read any errors from the attempted command
            while ((s = stdError.readLine()) != null)
            {
                Log.d(TAG,"******** ERROR ******** " + s);
                response = false;
            }

            return response;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return false;
        }
    }
}
