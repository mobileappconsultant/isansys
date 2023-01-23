package com.isansys.pse_isansysportal;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.Spinner;

import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.common.enums.DeviceType;

import static com.isansys.common.DeviceInfoConstants.CONTINUOUS_SETUP_MODE;

class UtilityFunctions
{
    /**
     * Is the fragment (not null), added, and resumed - i.e. is it safe to call methods inside the fragment?
     *
     * @param fragment : Fragment = Reference of fragment to be checked
     * @return : true if fragment is ready for operations
     **/
    public static boolean isFragmentAddedAndResumed(Fragment fragment)
    {
        // Order is important here. The double AND - && - evaluates left to right, and stops as soon as it gets a false. So, check fragment != null first.
        return ((fragment != null) && (fragment.isAdded()) && (fragment.isResumed()));
    }


    public static String padDeviceType(DeviceType device_type)
    {
        return ("                                                         " + device_type.toString()).substring(device_type.toString().length());
    }


    public static String padVitalSignName(VitalSignType vital_sign_type)
    {
        return ("                                          " + vital_sign_type.toString()).substring(vital_sign_type.toString().length());
    }


    public static String padHumanReadableDeviceId(long human_readable_device_id)
    {
        return ("              " + human_readable_device_id).substring(String.valueOf(human_readable_device_id).length());
    }


    public static String padBluetoothAddress(String bluetooth_address)
    {
        if (bluetooth_address != null)
        {
            return ("                  " + bluetooth_address).substring(bluetooth_address.length());
        }
        else
        {
            return ("                  ");
        }
    }


    public static String dumpMeasurementInfo(VitalSignType vital_sign_type, MeasurementVitalSign measurement)
    {
        return padVitalSignName(vital_sign_type)
                + " : Timestamp = " + TimestampConversion.convertDateToHumanReadableStringDayHoursMinutes(measurement.timestamp_in_ms)
                + " : Validity = " + TimestampConversion.convertDateToGmtHoursMinuteSeconds(measurement.measurement_validity_time_in_seconds * DateUtils.SECOND_IN_MILLIS)
                + " : Validity Left = " + TimestampConversion.convertDateToGmtHoursMinuteSeconds(measurement.measurement_validity_time_left_in_seconds* DateUtils.SECOND_IN_MILLIS)
                + " : Value " + measurement.getPrimaryMeasurement();
    }


    public static String padLeft(String s, int n)
    {
        return String.format("%1$" + n + "s", s);
    }


    public static String padRight(String s, int n)
    {
        return String.format("%1$-" + n + "s", s);
    }


    public static String removeLeadingCharacters(String inputString, char characterToRemove)
    {
        int index;
        for (index = 0; index < inputString.length() - 1; index++)
        {
            if (inputString.charAt(index) != characterToRemove)
            {
                break;
            }
        }
        return inputString.substring(index);
    }

    public static String removeTrailingCharacters(String inputString, char characterToRemove)
    {
        int index;
        for (index = inputString.length() - 1; index > 0; index--)
        {
            if (inputString.charAt(index) != characterToRemove)
            {
                break;
            }
        }
        return inputString.substring(0, index + 1);
    }


    public static String convertSecondsToStringForSpinnerCheckFixedEnglishStrings(int setup_mode_time_in_seconds)
    {
        String length_as_string;

        if (setup_mode_time_in_seconds < 60)
        {
            length_as_string = setup_mode_time_in_seconds + " " + "seconds";
        }
        else if (setup_mode_time_in_seconds == 60)
        {
            length_as_string = "1 minute";
        }
        else
        {
            length_as_string = (setup_mode_time_in_seconds / 60) + " minutes";
        }

        return length_as_string;
    }


    public static String convertSecondsToStringForSpinnerCheck(int setup_mode_time_in_seconds, Context context)
    {
        String length_as_string;

        if(setup_mode_time_in_seconds == CONTINUOUS_SETUP_MODE)
        {
            length_as_string = context.getResources().getString(R.string.continuous_setup_mode);
        }
        else if (setup_mode_time_in_seconds < 60)
        {
            length_as_string = setup_mode_time_in_seconds + " " + context.getResources().getString(R.string.seconds);
        }
        else if (setup_mode_time_in_seconds == 60)
        {
            length_as_string = "1 " + context.getResources().getString(R.string.minute);
        }
        else
        {
            length_as_string = (setup_mode_time_in_seconds / 60) + " " + context.getResources().getString(R.string.minutes);
        }

        return length_as_string;
    }


    public static int getLengthInSecondsFromSpinnerString(String time_as_string, Context context)
    {
        if (time_as_string.contains(context.getResources().getString(R.string.continuous_setup_mode)))
        {
            return CONTINUOUS_SETUP_MODE;
        }

        String number_part_of_selection = time_as_string.substring(0, time_as_string.indexOf(" "));
        int length_in_units = Integer.parseInt(number_part_of_selection);

        if (time_as_string.contains(context.getResources().getString(R.string.seconds)))
        {
            // Seconds
            return length_in_units;
        }
        else
        {
            // Minute(s)
            return length_in_units * 60;
        }
    }

    public static int getSpinnerIndex(Spinner spinner, String myString)
    {
        int spinner_count = spinner.getCount();

        for (int i = 0; i < spinner_count; i++)
        {
            String spinner_string_at_position = spinner.getItemAtPosition(i).toString();

            if (spinner_string_at_position.equalsIgnoreCase(myString))
            {
                return i;
            }
        }

        return -1;
    }


    public static void setSpinnerValue(Spinner spinner, String value)
    {
        if(spinner != null)
        {
            int spinner_index = getSpinnerIndex(spinner, value);

            spinner.setSelection(spinner_index, false);
        }
    }
}
