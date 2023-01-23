package com.isansys.patientgateway;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.DateUtils;

import java.util.Timer;
import java.util.TimerTask;

public class DiskUsage
{
    private int free_percentage;

    private final SystemCommands commands;

    public DiskUsage(SystemCommands outgoing_commands)
    {
        commands = outgoing_commands;

        Timer timer_gui_disk_usage_updater = new Timer();
        timer_gui_disk_usage_updater.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                calculateDiskUsage();
            }
        }, 0, 10 * DateUtils.MINUTE_IN_MILLIS);
    }

    private void calculateDiskUsage()
    {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long total_memory = (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
        long free_memory = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());

        float free_percentage_as_float = (free_memory * 100f) / total_memory;
        free_percentage = (int)free_percentage_as_float;

        commands.reportFreeDiskSpace(free_percentage);
    }

    public int getFreeDiskSpace()
    {
        return free_percentage;
    }
}