package com.isansys.patientgateway.deviceInfo;

import android.net.Uri;

public class SetupModeInfo
{
    final boolean supports_setup_mode;

    // The Database table URI to write the Setup Mode data to
    final Uri database_uri;

    // Max size is sent to the UI so it knows how big to make the graph
    int max_sample_size;

    SetupModeInfo()
    {
        this.supports_setup_mode = false;
        database_uri = null;
    }

    SetupModeInfo(Uri database_uri, int max_sample_size)
    {
        this.supports_setup_mode = true;
        this.database_uri = database_uri;
        this.max_sample_size = max_sample_size;
    }
}
