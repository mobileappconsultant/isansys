package com.isansys.patientgateway;

import com.isansys.common.ErrorCodes;
import com.isansys.common.enums.PatientPositionOrientation;
import com.isansys.common.enums.QueryType;
import com.isansys.common.measurements.VitalSignType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

public class Utils
{
    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String byteArrayToHexString(byte[] bytes)
    {
        if (bytes != null)
        {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
            {
                sb.append(String.format("%02X ", b & 0xff));
            }
            return sb.toString();
        }
        else
        {
            return "";
        }
    }


    public static int convertByteToInt(byte value)
    {
        return (int)value & 0xFF;
    }


    public static String convertByteToString(byte value)
    {
        return String.format("%02X ", value & 0xff);
    }


    public static String convertIntToString(int value)
    {
        return String.format("%08X ", value);
    }


    public static boolean convertToBoolean(int value)
    {
        return value > 0;
    }


    public static String leftPadWithZeroes(String originalString, int length)
    {
        StringBuilder paddedString = new StringBuilder(originalString);
        while (paddedString.length() < length) {
            paddedString.insert(0, "0");
        }
        return paddedString.toString();
    }


    public static String padLeft(String s, int n)
    {
        return String.format("%1$" + n + "s", s);
    }


    public static String padRight(String s, int n)
    {
        return String.format("%1$-" + n + "s", s);
    }


    public static String[] concatStringArrays(String[] a, String[] b)
    {
        int aLen = a.length;
        int bLen = b.length;
        String[] c = new String[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


    private static String convertActivityLevelToHumanReadableString(HeartBeatInfo.ActivityLevel activity_level)
    {
        String return_string;

        if(activity_level == null)
        {
            return_string = "no heart beat (activity value = null )";
        }
        else
        {
            switch(activity_level)
            {
                case NO_DATA:
                case NONE:
                case LOW:
                case HIGH:
                    return_string = activity_level.toString();
                    break;

                default:
                    return_string = "no heart beat (activity value = " + activity_level.getValue() + ")";
                    break;
            }
        }

        // Pad to max normal length (NO_DATA = 7)
        return_string = padLeft(return_string, 7);

        return return_string;
    }


    public static String explainHeartBeat(HeartBeatInfo this_heart_beat)
    {
        String beat_type;

        switch (this_heart_beat.getAmplitude())
        {
            case ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF:
            {
                beat_type = "LEADS OFF";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_ON:
            {
                beat_type = "LEADS ON";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP:
            {
                beat_type = "NO TIMESTAMP";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_CONNECTION_TIMEOUT:
            {
                beat_type = "CONNECTION_TIMEOUT";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_DATA_CREDIT_TIMEOUT:
            {
                beat_type = "DATA_CREDIT_TIMEOUT";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ACK_TIMEOUT:
            {
                beat_type = "LIFETOUCH_ACK_TIMEOUT";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED:
            {
                beat_type = "NO_BEATS_DETECTED";
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_UNKNOWN:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_UNKNOWN.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_UPRIGHT:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_UPRIGHT.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_LEFT_SIDE:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_LEFT_SIDE.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_RIGHT_SIDE:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_RIGHT_SIDE.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_FRONT:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_FRONT.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_BACK:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_BACK.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_UPSIDE_DOWN:
            {
                beat_type = PatientPositionOrientation.ORIENTATION_UPSIDE_DOWN.toString();
            }
            break;

            case ErrorCodes.ERROR_CODE__LIFETOUCH_ADVERTISING_TIMEOUT:
            {
                beat_type = "LIFETOUCH_ADVERTISING_TIMEOUT";
            }
            break;

            default:
            {
                beat_type = "Heart Beat";
            }
            break;
        }

        String return_string = "ID = " + padLeft(String.valueOf(this_heart_beat.getTag()), 4) + " : " + padLeft(beat_type, 25) ;
        return_string = return_string + ". Timestamp = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(this_heart_beat.getTimestampInMs());
        return_string = return_string + ". Amplitude = " + padLeft(String.valueOf(this_heart_beat.getAmplitude()), 5) + ". ";
        return_string = return_string + "Activity = " + convertActivityLevelToHumanReadableString(this_heart_beat.getActivity());

        long time_now = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();
        long time_delta = time_now - this_heart_beat.getTimestampInMs();

        return_string = return_string + ". Delta = " + time_delta;

        return return_string;
    }


    /**
     * Divides a string into chunks of a given character size.
     *
     * @param text                  String text to be sliced
     * @param sliceSize             int Number of characters
     * @return  ArrayList<String>   Chunks of strings
     */
    private static ArrayList<String> splitString(String text, int sliceSize)
    {
        ArrayList<String> textList = new ArrayList<>();
        String aux;
        int left, right = 0;
        int charsLeft = text.length();

        while (charsLeft != 0)
        {
            left = right;
            if (charsLeft >= sliceSize)
            {
                right += sliceSize;
                charsLeft -= sliceSize;
            }
            else
            {
                right = text.length();
                charsLeft = 0;
            }
            aux = text.substring(left, right);
            textList.add(aux);
        }

        return textList;
    }

    /**
     * Divides a string into chunks.
     *
     * @param text                  String text to be sliced
     * @return  ArrayList<String>
     */
    public static ArrayList<String> splitString(String text)
    {
        return splitString(text, 1000);
    }

/*  Example call

    public static void splitAndLog(String text)
    {
        ArrayList<String> lines = Utils.splitString(text);
        for (String line : lines)
        {
            Log.d(TAG, line);
        }
    }
*/


    public static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;

        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static String convertTimestampToServerSqlDate(long timestamp)
    {
        return "/Date(" + timestamp + ")/";
    }


    public static String padQueryName(QueryType query_name)
    {
        return ("                                                           " + query_name.toString()).substring(query_name.toString().length());
    }


    public static String padVitalSignName(VitalSignType vital_sign_type)
    {
        return ("                                          " + vital_sign_type.toString()).substring(vital_sign_type.toString().length());
    }


    public static String padNumber(long number)
    {
        return padLeft(String.valueOf(number), 9);
    }



    // This code modified from https://github.com/opensensorhub/osh-comm/blob/master/sensorhub-comm-ble/src/main/java/org/sensorhub/api/comm/ble/BleUtils.java

    /***************************** BEGIN LICENSE BLOCK ***************************
     The contents of this file are subject to the Mozilla Public License, v. 2.0.
     If a copy of the MPL was not distributed with this file, You can obtain one
     at http://mozilla.org/MPL/2.0/.
     Software distributed under the License is distributed on an "AS IS" basis,
     WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
     for the specific language governing rights and limitations under the License.

     Copyright (C) 2012-2016 Sensia Software LLC. All Rights Reserved.

     ******************************* END LICENSE BLOCK ***************************/

    /**
     * Reads a decimal value as a IEEE-11073 16-bits float
     * @return java float value decoded from the given bytes
     */
    public static float readSFloat16(byte b0, byte b1)
    {
        int mantissa = unsignedToSigned((b0 & 0xFF) + ((b1 & 0x0F) << 8), 12);
        int exponent = unsignedToSigned((b1 & 0xFF) >> 4, 4);
        return (float)(mantissa * Math.pow(10, exponent));
    }


    private static int unsignedToSigned(int unsigned, int size)
    {
        if ((unsigned & (1 << size - 1)) != 0)
        {
            unsigned = -1 * ((1 << size - 1) - (unsigned & ((1 << size - 1) - 1)));
        }
        return unsigned;
    }


    public static String dumpTrackedMeasurementVitalSignNames(ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements)
    {
        String line = "";
        String comma_string = ", ";

        for (TrackedMeasurement x : tracked_measurements.values())
        {
            line = line.concat(x.getType().toString()).concat(comma_string);
        }

        if (!line.isEmpty())
        {
            line = line.substring(0, line.length() - comma_string.length());
        }
        else
        {
            line = "NONE";
        }

        return line;
    }


    public static String dumpVitalSignNames(ArrayList<VitalSignType> vital_sign_sessions_in_progress)
    {
        String line = "";
        String comma_string = ", ";

        for (VitalSignType vital_sign_type : vital_sign_sessions_in_progress)
        {
            line = line.concat(vital_sign_type.toString()).concat(comma_string);
        }

        if (!line.isEmpty())
        {
            line = line.substring(0, line.length() - comma_string.length());
        }

        return line;
    }

}
