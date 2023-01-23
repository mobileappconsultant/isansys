package com.isansys.patientgateway.serverlink.webservices;

import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IsansysAuthentication
{
    final RemoteLoggingWithEnable Log;
    private final String TAG = "IsansysAuthentication";
    private final String gateway_unique_id;
    final PatientGatewayInterface patient_gateway_interface;

    public IsansysAuthentication(RemoteLoggingWithEnable logger, String desired_gateway_unique_id, PatientGatewayInterface patient_gateway_interface)
    {
        Log = logger;
        gateway_unique_id = desired_gateway_unique_id;
        this.patient_gateway_interface = patient_gateway_interface;
    }

    public String username()
    {
        long ntpTicks = patient_gateway_interface.getNtpTimeNowInMilliseconds() * 10000;
        String strNtpTicks = String.valueOf(ntpTicks);
        return (gateway_unique_id + "_" + strNtpTicks);
    }


    public String password(String input)
    {
        String hash = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(input.getBytes(StandardCharsets.UTF_8));

            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();

            for (byte b : digest)
            {
                sb.append(String.format("%02x", b & 0xff));
            }

            hash = sb.toString().toUpperCase();
        }
        catch (NoSuchAlgorithmException e)
        {
            Log.d(TAG,"ERROR but MD5 is not a valid message digest algorithm");
        }

        return hash;
    }
}
