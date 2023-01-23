package com.isansys.pse_isansysportal;

import com.isansys.pse_isansysportal.enums.ObservationSetEntryType;

import java.util.ArrayList;

class ManualVitalSignInfo
{
    int vital_sign_id;

    String vital_sign_display_name;

    ObservationSetEntryType entry_type;

    String units_display_string;

    boolean show_rhs_description;

    String please_enter_the_xxxx;

    ArrayList<ManualVitalSignButtonDescriptor> button_info = new ArrayList<>();
}
