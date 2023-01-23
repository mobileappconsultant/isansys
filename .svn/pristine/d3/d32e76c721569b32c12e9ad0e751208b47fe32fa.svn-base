package com.isansys.patientgateway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;


public class TimestampConversion
{
    private static String convertDateToHumanReadableFormat(DateTime dt, String desired_date_format)
    {
        return dt.toString(DateTimeFormat.forPattern(desired_date_format));
    }


    private static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "yyyy-MM-dd HH:mm:ss");
    }
    public static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(new DateTime(timestamp_as_long));
    }
    public static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(Date timestamp_as_date)
    {
        return convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(new DateTime(timestamp_as_date));
    }


    public static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableFormat(new DateTime(timestamp_as_long), "yyyy-MM-dd HH:mm:ss.SSS");
    }


    public static String convertDateToHumanReadableStringHoursMinutesSeconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableFormat(new DateTime(timestamp_as_long), "HH:mm:ss");
    }


    public static String convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(long timestamp_as_long)
    {
    	/* This function is used to log timestamps in the format of milliseconds since the start of the day.
    	 * This means we get a DateTime on 1/1/1970.
    	 * Have to specify UTC here, as otherwise we get a datetime in UTC+1, since the UK was in permanent BST in 1970.
    	 */
        return convertDateToHumanReadableFormat(new DateTime(timestamp_as_long, DateTimeZone.UTC), "HH:mm:ss.SSS");
    }
}
