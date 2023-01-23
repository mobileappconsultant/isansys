package com.isansys.patientgateway;

import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isansys.common.FirmwareImage;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class FirmwareImageManager
{
    private final ArrayList<FirmwareImage> firmware_list;

    final String filepath;

    private final RemoteLogging Log;

    private final String TAG = this.getClass().getSimpleName();

    private final Settings settings;

    private final Gson gson;

    public FirmwareImageManager(RemoteLogging log, Settings gateway_settings)
    {
        firmware_list = new ArrayList<>();

        for (DeviceType device_type : DeviceType.values())
        {
            firmware_list.add(new FirmwareImage(device_type));
        }

        Log = log;

        settings = gateway_settings;

        gson = new Gson();

        // Retrieve saved settings
        loadFirmwareInfoFromSettings();

        filepath = Environment.getExternalStorageDirectory() + File.separator + "firmware_images" + File.separator;
    }


    public void updateLatestStoredFirmwareInfoAndWriteImage(DeviceType device_type, int firmware_version, byte[] firmware_binary_image, String filename)
    {
        updateLatestStoredFirmwareInfo(device_type, firmware_version, filename);

        boolean success = writeFirmwareBytesToDisk(firmware_binary_image, filename);
        if (success)
        {
            Log.d(TAG, "writeFirmwareBytesToDisk : success");
        }
        else
        {
            Log.d(TAG, "writeFirmwareBytesToDisk : failed");
        }

        writeFirmwareInfoToSettings();
    }


    public void updateLatestStoredFirmwareInfo(DeviceType device_type, int firmware_version, String filename)
    {
        FirmwareImage image_to_update = getImageByType(device_type);

        if(image_to_update != null)
        {
            image_to_update.latest_stored_firmware_version = firmware_version;
            image_to_update.file_name = filename;
        }
    }


    public void updateLatestStoredFirmwareVersionIfNewer(DeviceType device_type, int firmware_version)
    {
        FirmwareImage image_to_update = getImageByType(device_type);

        if((image_to_update != null) && (firmware_version > image_to_update.latest_stored_firmware_version))
        {
            image_to_update.latest_stored_firmware_version = firmware_version;
        }

        writeFirmwareInfoToSettings();
    }


    private FirmwareImage getImageByType(DeviceType device_type)
    {
        for (FirmwareImage firmware_image : firmware_list)
        {
            if (firmware_image.device_type == device_type)
            {
                return firmware_image;
            }
        }

        return null;
    }


    public boolean firmwareUpdatePending()
    {
        for (FirmwareImage firmware_image : firmware_list)
        {
            if (firmware_image.firmware_update_pending)
            {
                return true;
            }
        }

        return false;
    }


    public boolean firmwareUpdatePending(DeviceType device_type)
    {
        FirmwareImage firmware_image = getImageByType(device_type);
        if (firmware_image != null)
        {
            return firmware_image.firmware_update_pending;
        }

        return false;
    }


    public byte[] getLatestStoredBinary(DeviceType device_type)
    {
        FirmwareImage firmware_image = getImageByType(device_type);
        if (firmware_image != null)
        {
            // read in from file...
            return readFirmwareBytesFromDisk(firmware_image.file_name);
        }

        return null;
    }


    public int getLatestStoredFirmwareVersion(DeviceType device_type)
    {
        FirmwareImage firmware_image = getImageByType(device_type);
        if (firmware_image != null)
        {
            return firmware_image.latest_stored_firmware_version;
        }

        return 0;
    }

    public String getFirmwareFileName(DeviceType device_type)
    {
        FirmwareImage firmware_image = getImageByType(device_type);
        if (firmware_image != null)
        {
            return firmware_image.file_name;
        }

        return "";
    }


    public void clearLatestStoredFirmwareVersions()
    {
        for (FirmwareImage firmware_image : firmware_list)
        {
            firmware_image.latest_stored_firmware_version = INVALID_FIRMWARE_VERSION;
        }
    }


    public void setFirmwareUpdatePending(DeviceType device_type, boolean pending)
    {
        FirmwareImage firmware_image = getImageByType(device_type);
        if (firmware_image != null)
        {
            firmware_image.firmware_update_pending = pending;
        }
    }


    public ArrayList<FirmwareImage> getFirmwareImageList()
    {
        return firmware_list;
    }


    public boolean writeFirmwareBytesToDisk(byte[] firmware, String filename)
    {
        try
        {
            boolean success;

            Log.d(TAG, "writeFirmwareBytesToDisk : writing " + filename);

            File file = new File(filepath);
            if(!file.exists())
            {
                success = file.mkdirs();
                if (success)
                {
                    Log.d(TAG, "writeFirmwareBytesToDisk : mkdirs success");
                }
                else
                {
                    Log.d(TAG, "writeFirmwareBytesToDisk : mkdirs failed");
                }
            }

            file = new File(filepath + filename);
            success = file.createNewFile();
            if (success)
            {
                Log.d(TAG, "writeFirmwareBytesToDisk : createNewFile success");
            }
            else
            {
                Log.d(TAG, "writeFirmwareBytesToDisk : createNewFile failed");
            }

            OutputStream outputStream = null;

            try
            {
                int fileSize = firmware.length;

                outputStream = new FileOutputStream(file);

                outputStream.write(firmware, 0, fileSize);

                outputStream.flush();

                return true;
            }
            catch (IOException e)
            {
                Log.e(TAG, "writeFirmwareBytesToDisk failed with error message " + e.getMessage());
                return false;
            }
            finally
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "writeFirmwareBytesToDisk failed with error message " + e.getMessage());
            return false;
        }
    }


    private byte[] readFirmwareBytesFromDisk(String file_to_read)
    {
        byte[] firmware = new byte[0];

        try
        {
            File file = new File(filepath);
            if(file.exists())
            {

                file = new File(filepath + file_to_read);

                InputStream input_stream = null;

                try
                {
                    int fileSize = (int) file.length(); // files can be up to ~2.1 Gb assuming signed int which is plenty for our purposes
                    firmware = new byte[fileSize];

                    input_stream = new FileInputStream(file);

                    input_stream.read(firmware, 0, fileSize);
                }
                catch (IOException e)
                {
                    Log.e(TAG, "readFirmwareBytesFromDisk : Exception " + e.toString());
                }
                finally
                {
                    if (input_stream != null)
                    {
                        input_stream.close();
                    }
                }
            }
            else
            {
                Log.w(TAG, "readFirmwareBytesFromDisk : firmware directory not found");
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "readFirmwareBytesFromDisk : Exception " + e.toString());
        }

        return firmware;
    }


    private void loadFirmwareInfoFromSettings()
    {
        String firmware_images_as_string = settings.getFirmwareImages();
        ArrayList<FirmwareImage> populated_firmware_images = parseFirmwareString(firmware_images_as_string);

        for(FirmwareImage image : populated_firmware_images)
        {
            updateLatestStoredFirmwareInfo(image.device_type, image.latest_stored_firmware_version, image.file_name);
        }
    }


    private void writeFirmwareInfoToSettings()
    {
        ArrayList<FirmwareImage> populated_firmware_images = new ArrayList<>();

        for(FirmwareImage image : firmware_list)
        {
            if(image.latest_stored_firmware_version != INVALID_FIRMWARE_VERSION)
            {
                populated_firmware_images.add(image);
            }
        }

        String firmware_images_as_string = firmwareListToString(populated_firmware_images);

        settings.storeFirmwareImages(firmware_images_as_string);
    }


    private String firmwareListToString(ArrayList<FirmwareImage> populated_firmware_images)
    {
        String json = gson.toJson(populated_firmware_images);

        Log.d(TAG, "firmwareListToString : " + json);

        return json;
    }


    private ArrayList<FirmwareImage> parseFirmwareString(String firmware_images_as_string)
    {
        Type firmwareImageList = new TypeToken<ArrayList<FirmwareImage>>(){}.getType();

        Log.d(TAG, "parseFirmwareString : " + firmware_images_as_string);

        return gson.fromJson(firmware_images_as_string, firmwareImageList);
    }


    public void deleteAllApksInFirmwareFolder()
    {
        File dir = new File(filepath);

        File[] files = dir.listFiles((dir1, filename) -> filename.endsWith(".apk"));

        if(files != null)
        {
            for (File f : files)
            {
                if (f.exists())
                {
                    if (f.delete())
                    {
                        Log.d(TAG, "Apk deleted");
                    }
                }
            }
        }
    }


    public boolean checkForMatchingFirmware(String filename)
    {
        for(FirmwareImage image : firmware_list)
        {
            if(image.file_name.equals(filename))
            {
                return true;
            }
        }

        return false;
    }


    public boolean downloadRequired(DeviceType type, int available_firmware_version)
    {
        String filename = getFirmwareFileName(type);
        File file = new File(filepath + filename);

        // force download if file is a .bin and it doesn't exist locally
        boolean file_download_required = (!file.exists() && (filename.endsWith(".bin")));

        // otherwise download if reported firmware is newer than local version
        return (file_download_required || (getLatestStoredFirmwareVersion(type) < available_firmware_version));
    }


    public void deleteAllFirmwareImagesNotInManager()
    {
        File dir = new File(filepath);

        if (dir.exists())
        {
            File[] files = dir.listFiles();

            if(files != null)
            {
                for (File f : files)
                {
                    if (!checkForMatchingFirmware(f.getName()) && f.exists())
                    {
                        boolean success = f.delete();
                        if (success)
                        {
                            Log.d(TAG, "deleteAllFirmwareImagesNotInManager : File deleted");
                        }
                        else
                        {
                            Log.d(TAG, "deleteAllFirmwareImagesNotInManager : File deletion FAILED");
                        }
                    }
                }
            }
        }
    }
}
