package com.isansys.pse_isansysportal;

import com.isansys.pse_isansysportal.enums.ObservationSetEntryType;

class ManuallyEnteredVitalSignDescriptor
{
    public final String display_name;
    public final int vital_sign_id;
    public final ObservationSetEntryType observation_set_entry_type;

    public ManuallyEnteredVitalSignDescriptor(String display_name, int vital_sign_id, ObservationSetEntryType observation_set_entry_type)
    {
        this.display_name = display_name;
        this.vital_sign_id = vital_sign_id;
        this.observation_set_entry_type = observation_set_entry_type;
    }
}
