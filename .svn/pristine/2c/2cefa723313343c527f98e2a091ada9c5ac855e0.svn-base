package com.isansys.pse_isansysportal;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;


public class TimestampConversion
{
    private static String convertDateToHumanReadableFormat(DateTime dt, String desired_date_format)
    {
        return dt.toString(DateTimeFormat.forPattern(desired_date_format));
    }

    public static String convertDateToGmtHoursMinuteSeconds(long timestamp)
    {
        return convertDateToHumanReadableFormat(new DateTime(new Date(timestamp), DateTimeZone.UTC), "HH:mm:ss");
    }


    private static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "yyyy-MM-dd HH:mm:ss");
    }
    public static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(new DateTime(timestamp_as_long));
    }

    public static String convertDateToHumanReadableStringYearMonthDay(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "dd MMM yyyy");
    }


    private static String convertDateToHumanReadableStringDayHoursMinutes(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "E HH:mm");
    }
    public static String convertDateToHumanReadableStringDayHoursMinutes(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringDayHoursMinutes(new DateTime(timestamp_as_long));
    }


    private static String convertDateToHumanReadableStringDayHoursMinutesSeconds(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "E HH:mm:ss");
    }
    public static String convertDateToHumanReadableStringDayHoursMinutesSeconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringDayHoursMinutesSeconds(new DateTime(timestamp_as_long));
    }


    public static String convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(long timestamp)
    {
        // Monday 14 Dec 11:22
        return convertDateToHumanReadableFormat(new DateTime(timestamp), "E dd MMM HH:mm");
    }


    public static String convertDateToBchFormatHumanReadableStringYearMonthDayHoursMinutesSeconds(long timestamp)
    {
        return convertDateToHumanReadableFormat(new DateTime(timestamp), "dd/MM/yy-HH:mm");
    }

    private static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "yyyy-MM-dd HH:mm:ss.SSS");
    }
    public static String convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(new DateTime(timestamp_as_long));
    }


    public static String convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(new DateTime(timestamp_as_long, DateTimeZone.UTC));
    }

    public static String convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSeconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(new DateTime(timestamp_as_long, DateTimeZone.UTC));
    }


    private static String convertDateToHumanReadableStringHoursMinutes(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "HH:mm");
    }
    public static String convertDateToHumanReadableStringHoursMinutes(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringHoursMinutes(new DateTime(timestamp_as_long));
    }


    public static String convertDateToHumanReadableStringHoursMinutesSeconds(DateTime timestamp_as_datetime)
    {
        return convertDateToHumanReadableFormat(timestamp_as_datetime, "HH:mm:ss");
    }
    public static String convertDateToHumanReadableStringHoursMinutesSeconds(long timestamp_as_long)
    {
        return convertDateToHumanReadableStringHoursMinutesSeconds(new DateTime(timestamp_as_long));
    }
    public static String convertDateToHumanReadableStringHoursMinutesSeconds(Date timestamp_as_date)
    {
        return convertDateToHumanReadableStringHoursMinutesSeconds(new DateTime(timestamp_as_date));
    }


    public static String convertDateToHumanReadableStringDayMonthHoursMinutesSeconds(long timestamp)
    {
        // 14 Dec 11:22:55
        return convertDateToHumanReadableFormat(new DateTime(timestamp), "dd MMM HH:mm:ss");
    }
}
