package com.isansys.patientgateway;

import android.bluetooth.BluetoothAdapter;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateUtils;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


class OldLogFileCleaner
{
    private final String TAG = OldLogFileCleaner.class.getName();
    private final RemoteLogging Log;

    private boolean old_data_cleaner_running = false;
    private final Handler handler = new Handler();

    private final int OLD_DATA_CLEANING_FOUR_HOURS_RUN_TIMEOUT = (int) DateUtils.HOUR_IN_MILLIS;     // Do this every hour
    private final int OLD_DATA_NUMBER_OF_DAYS = 90;                                      // Store the (zipped) log files for up to 90 days


    public OldLogFileCleaner(RemoteLogging logger)
    {
        Log = logger;
    }


    public void Start()
    {
        if (!old_data_cleaner_running)
        {
            old_data_cleaner_running = true;

            // Run the code to zip up old log files
            oldFilesCleaner_runnable();
        }
    }


    public void Stop()
    {
        old_data_cleaner_running = false;
    }


    private void oldFilesCleaner_runnable()
    {
        if (old_data_cleaner_running)
        {
            doLogFolderUpdateInBackground();

            // Setup to run again in OLD_DATA_CLEANING_FOUR_HOURS_RUN_TIMEOUT seconds
            Runnable runnable = this::oldFilesCleaner_runnable;

            handler.postDelayed(runnable, OLD_DATA_CLEANING_FOUR_HOURS_RUN_TIMEOUT);
        }
    }


    private long folderSize(File directory)
    {
        long length = 0;

        File[] files = directory.listFiles();

        if(files != null)
        {
            for (File file : files)
            {
                if (file.isFile())
                {
                    length += file.length();
                }
                else
                {
                    length += folderSize(file);
                }
            }
        }

        return length;
    }


    private boolean createMultipartZIP(String[] files, String zipFile)
    {
        try
        {
            final int BUFFER_SIZE = 1024 * 1024;                                        // 1 MB
            final long MAX_ZIP_SIZE = BUFFER_SIZE * 50;                                 // 50 MB

            long currentSize = 0;
            int zipSplitCount = 0;

            byte[] data = new byte[BUFFER_SIZE];

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            ZipEntry entry;
            FileInputStream origin;

            for (String file : files)
            {
                Log.d(TAG, "Zipping " + file + " to " + zipFile);

                origin = new FileInputStream(file);

                entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                if (currentSize >= MAX_ZIP_SIZE)
                {
                    zipSplitCount++;
                    out.close();
                    out = new ZipOutputStream(new FileOutputStream(zipFile.replace(".zip", "_" + zipSplitCount + ".zip")));
                    currentSize = 0;
                }

                out.putNextEntry(entry);

                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1)
                {
                    out.write(data, 0, count);
                }

                origin.close();
                out.closeEntry();
                currentSize += entry.getCompressedSize();
            }

            out.close();
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "FileNotFoundException = " + e.getMessage());
            return false;
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException = " + e.getMessage());
            return false;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception = " + e.getMessage());
            return false;
        }

        return true;
    }


    private String makeZipFilename(String zip_file_name)
    {
        // This zip file MAY already exist (only if the Gateway has crashed/turned off)
        File temp_file = new File(zip_file_name + ".zip");
        if(temp_file.exists())
        {
            // Its too expensive time wise to unzip and zip again (cant append to a zip in Android apparently) so just add something on the end of the filename
            zip_file_name = zip_file_name + "Part2";
        }

        zip_file_name = zip_file_name + ".zip";

        return zip_file_name;
    }


    private void zipUpAndDeleteFiles(List<String> specific_days_file_list_for_zip_file, String zip_file_name)
    {
        Log.d(TAG, "zipUpAndDeleteFiles");

        String[] log_filenames_array = specific_days_file_list_for_zip_file.toArray(new String[0]);

        if (createMultipartZIP(log_filenames_array, zip_file_name))
        {
            // Delete the original log files here now we have them zipped
            for (String log_filename : log_filenames_array)
            {
                Log.d(TAG, "Deleting " + log_filename);

                File file_to_delete = new File(log_filename);

                if (file_to_delete.delete())
                {
                    Log.d(TAG, "Deleted");
                }
                else
                {
                    Log.e(TAG, "PROBLEM DELETING FILE");
                }
            }
        }
    }


    private void cleanOldLoggingFiles()
    {
        Log.d(TAG, "cleanOldLoggingFiles : Updating the log folder size in background");

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        String log_directory = "/IsansysLogging/PatientGateway/";
        String full_log_directory_path = SD_CARD_PATH + log_directory;

        File f_f = new File(full_log_directory_path);
        long folder_space = folderSize(f_f);

        long BYTES_IN_ONE_MB = 1000000;
        Log.d(TAG, "cleanOldLoggingFiles : " + log_directory + " size = " + (folder_space / BYTES_IN_ONE_MB) + " MB");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        File[] file_list = f_f.listFiles();

        long BYTES_IN_ONE_GB = 1000000000;
        long MAX_FOLDER_SIZE_IN_GB = 2;
        if (folder_space > (BYTES_IN_ONE_GB * MAX_FOLDER_SIZE_IN_GB))                       // Check if total folder size is greater than 4GB
        {
            // If greater than 4 GB, sort the folder according to date of modified
            Arrays.sort(file_list, Comparator.comparingLong(File::lastModified));

            int j = 0;
            int length_of_file = file_list.length;

            Log.d(TAG, "file length = " + length_of_file);

            long MB_TO_BE_FREED = 100 * BYTES_IN_ONE_MB;
            long total_deleting_file_size = 0;
            int length_of_file_limit = 0;

            for(int i = 0; i < length_of_file-1; i++)
            {
                // check if the folder space minus file size to be deleted is less than 1.90 GB space
                if((folder_space - total_deleting_file_size) > ((BYTES_IN_ONE_GB * MAX_FOLDER_SIZE_IN_GB) - MB_TO_BE_FREED))
                {
                    total_deleting_file_size += file_list[i].length();
                    length_of_file_limit++;
                }
            }

            Log.d(TAG, "Deleting total_file_size = " + total_deleting_file_size + " . Length of file deleted = " + length_of_file_limit);

            for (int i = length_of_file_limit; i > 0; i--) // start to delete from the top of array as they are the oldest ones
            {
                Log.d(TAG,"Deleting the file with time last modified time stamp = " + formatter.format(new Date(file_list[j].lastModified())) + "     name = " + file_list[j].getName());

                if (file_list[j].delete())
                {
                    Log.d(TAG, "Deleted");
                }
                else
                {
                    Log.e(TAG, "PROBLEM DELETING FILE");
                }

                j++;
            }
        }
        
        
        try
        {
            File[] files = f_f.listFiles();

            // Sort the files in alphabetical and ascending order
            Arrays.sort(files);


            String gateway_name = BluetoothAdapter.getDefaultAdapter().getName();
            String todays_filename = convertDateToHumanReadableStringYearMonthDay(new Date(cal.getTimeInMillis()));

            List<String> specific_days_file_list_for_zip_file = new ArrayList<>();

            boolean oneshot__new_days_files = true;
            String specific_days_date = "";
            String zip_file_name = "";

            for (File file : files)                                                     // Go through each of the files in the list
            {
                try
                {
                    String filename = file.getName();
                    String extension = filename.substring(filename.lastIndexOf(".") + 1);

                    if (filename.equals("gateway_log_file.log"))
                    {
// NEED TO CHECK FOR LAST MODIFIED ON THIS FILE
                        Log.d(TAG, "Ignoring files from today");
                    }
                    else if (filename.contains(todays_filename))
                    {
                        Log.d(TAG, "Ignoring files from today");
                    }
                    else if (extension.equals("log"))
                    {
                        String filename_without_extension = filename.substring(0, filename.lastIndexOf("."));
                        String date_from_filename = filename_without_extension.substring(filename_without_extension.indexOf(".") + 1);

                        if (date_from_filename.contains("."))
                        {
                            // It has a .XX on the end of the file
                            date_from_filename = date_from_filename.substring(0, date_from_filename.indexOf("."));
                        }

                        // One shot to fire the first time around the loop
                        if (oneshot__new_days_files)
                        {
                            specific_days_date = date_from_filename;

                            oneshot__new_days_files = false;

                            zip_file_name = makeZipFilename(full_log_directory_path + gateway_name + "_" + filename_without_extension.substring(0, filename_without_extension.length()-2));
                        }


                        if (date_from_filename.equals(specific_days_date))
                        {
                            // Add to todays list of log files
                            specific_days_file_list_for_zip_file.add(full_log_directory_path + filename);
                        }
                        else
                        {
                            // Then we have finished this days logs (date_from_filename is NOT specific_days_date).

                            // Zip this list of files
                            if (specific_days_file_list_for_zip_file.size() > 0)
                            {
                                zipUpAndDeleteFiles(specific_days_file_list_for_zip_file, zip_file_name);
                            }

                            // Reset for new day
                            specific_days_date = date_from_filename;

                            specific_days_file_list_for_zip_file.clear();
                            specific_days_file_list_for_zip_file.add(full_log_directory_path + filename);

                            zip_file_name = makeZipFilename(full_log_directory_path + gateway_name + "_" + filename_without_extension.substring(0, filename_without_extension.length()-2));
                        }
                    }
/*
                    else
                    {
                        // Not a log file (probably a .zip file)
                    }
*/
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            // Need to zip up the last set of files
            if (specific_days_file_list_for_zip_file.size() > 0)
            {
                zipUpAndDeleteFiles(specific_days_file_list_for_zip_file, zip_file_name);
            }

            // Now check that none of the ZIP files are over OLD_DATA_NUMBER_OF_DAYS days old. If so delete them
            File[] zip_file_list = f_f.listFiles();

            // Build compare date
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.add(Calendar.DAY_OF_YEAR, -OLD_DATA_NUMBER_OF_DAYS);

            String limitDateString = formatter.format(calendar.getTime());

            // Format it for date only
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            int fileCounter = zip_file_list.length;

            // Parse date from the limit date string. Needs to be inside a try catch in case it fails the parse
            Date limitDate = sdf.parse(limitDateString);

            while (fileCounter > 0)
            {
                fileCounter--;

                // Get files last modified date
                Date lastModDate = new Date(zip_file_list[fileCounter].lastModified());

                if (lastModDate.before(limitDate))
                {
                    // Delete the file
                    if (zip_file_list[fileCounter].delete())
                    {
                        Log.d(TAG, "Deleted");
                    }
                    else
                    {
                        Log.e(TAG, "PROBLEM DELETING FILE");
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "cleanOldLoggingFiles Exception : " + e.toString());
        }

        Log.i(TAG, "doLogFolderUpdateInBackground : Finished");
    }


    private void doLogFolderUpdateInBackground()
    {
        // Long running operation
        Executors.newSingleThreadExecutor().execute(this::cleanOldLoggingFiles);
    }


    private String convertDateToHumanReadableStringYearMonthDay(Date timestamp_as_date)
    {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timestamp_as_date);
    }
}
