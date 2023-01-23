package com.isansys.ntpTimeSync;

/**
 * Implementation of the Synced Time Source
 * Function SetSyncTime(value) is called by the class "NTP_Simple".
 * Before Calling currentTimeMillis(), NTP_Simple class must be called.
 * When application needs Synced time just called currentTimeMillis()
 */
public class AlternateTimeSource implements TimeSource
{
	/**
	 * double time sync variable
	 */
	private static double time_offset_in_milliseconds = 0;
	
	
	@Override
	public long currentTimeMillis() 
	{
		return (long) (System.currentTimeMillis() + time_offset_in_milliseconds);
	}


	public static double getTimeOffsetInMilliseconds()
    {
        return time_offset_in_milliseconds;
    }


	/**
	 * Adjust the applications time (accessed via AlternativeTimeSource.currentTimeMillis())
	 * @param ntp_offset_in_milliseconds : double value to set Sync Time im ms
	 */
	public static void setTimeOffsetInMilliseconds(double ntp_offset_in_milliseconds)
	{	
        time_offset_in_milliseconds = ntp_offset_in_milliseconds;
    }
}
