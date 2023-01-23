package com.isansys.patientgateway.ntpTimeSync;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.text.DecimalFormat;

import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.DateUtils;


public class NTP_Simple
{
	private final String TAG = NTP_Simple.class.getName();
	private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;


    /**
	 * local clock offset in milliseconds
	 */
	private double local_clock_offset_in_milliseconds = 0;


	public NTP_Simple(ContextInterface context_interface, RemoteLogging logger)
	{
        this.gateway_context_interface = context_interface;
        this.Log = logger;
	}


    public boolean doTimeSync(String server_name)
    {
        boolean success = false;

        WakeLock wakeLock = ((PowerManager) gateway_context_interface.getAppContext().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NtpSimple:NtpSyncWakeLock");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        // Send request

        try (DatagramSocket socket = new DatagramSocket())
        {
            // Set the timeout so that we don't get blocked forever waiting for the package.
            int TIMEOUT_MILLIS = (int) (DateUtils.SECOND_IN_MILLIS * 6);
            socket.setSoTimeout(TIMEOUT_MILLIS);

            InetAddress server_ip_address = InetAddress.getByName(server_name);
            byte[] buf = new NtpMessage().toByteArray();

            int SERVER_PORT = 123;
            DatagramPacket packet = new DatagramPacket(buf, buf.length, server_ip_address, SERVER_PORT);

            // Doing this log line before the actual send to make sure the time it takes to store the log line does not interfere with the TCP/IP packet sending/receiving
            Log.d(TAG, "NTP request about to be sent. Waiting for response...");

            // Set the transmit timestamp *just* before sending the packet
            double SECONDS_SINCE_1900_TILL_1970 = 2208988800.0;
            NtpMessage.encodeTimestamp(packet.getData(), 40, (System.currentTimeMillis() / 1000.0) + SECONDS_SINCE_1900_TILL_1970);
            socket.send(packet);

            // Get response
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            // Immediately record the incoming timestamp
            double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + SECONDS_SINCE_1900_TILL_1970;

            // Process response
            NtpMessage msg = new NtpMessage(packet.getData());

            // Corrected, according to RFC2030 errata
            double round_trip_delay_in_seconds = (destinationTimestamp - msg.originateTimestamp) - (msg.transmitTimestamp - msg.receiveTimestamp);
            double local_clock_offset_in_seconds = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;

            local_clock_offset_in_milliseconds = local_clock_offset_in_seconds * 1000;

            // Display response
            Log.d(TAG, msg.toString());
            Log.d(TAG, "Dest. timestamp:    " + NtpMessage.timestampToString(destinationTimestamp));
            Log.d(TAG, "Round-trip delay:   " + new DecimalFormat("0.00").format(round_trip_delay_in_seconds * 1000) + " ms");
            Log.d(TAG, "Local clock offset: " + new DecimalFormat("0.00").format(local_clock_offset_in_milliseconds) + " ms");

            success = true;
        }
        catch (InterruptedIOException e)
        {
            Log.e(TAG, "InterruptedIOException : " + e.toString());
        }
        catch (BindException e)
        {
            Log.e(TAG, "BindException : " + e.toString());
        }
        catch (ConnectException e)
        {
            Log.e(TAG, "ConnectException : " + e.toString());
        }
        catch (NoRouteToHostException e)
        {
            Log.e(TAG, "NoRouteToHostException : " + e.toString());
        }
        catch (PortUnreachableException e)
        {
            Log.e(TAG, "PortUnreachableException : " + e.toString());
        }
        catch (SocketException e)
        {
            Log.e(TAG, "SocketException : " + e.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException : " + e.toString());
        }

        if (wakeLock.isHeld())
        {
            wakeLock.release();
        }

        return success;
    }


	public double getLocalClockOffsetInMilliseconds()
	{
		return local_clock_offset_in_milliseconds;
	}
}
