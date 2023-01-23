package com.isansys.pse_isansysportal;

import java.util.ArrayList;

public class MeasurementSetBeingEntered
{
    private ArrayList<ManualVitalSignBeingEntered> measurements_being_entered;

    private long timestamp_in_ms;
    private VitalSignValidityTimeDescriptor validity_time;

    private final int INVALID_TIMESTAMP = -1;

    public MeasurementSetBeingEntered()
    {
        reset();
    }


    public void reset()
    {
        measurements_being_entered = new ArrayList<>();
        timestamp_in_ms = INVALID_TIMESTAMP;
        validity_time = null;
    }


    public void addVitalSign(int vital_sign_id, String human_readable_name, int button_id, int ews_score)
    {
        addToSet(vital_sign_id, new ManualVitalSignBeingEntered(vital_sign_id, human_readable_name, button_id, ews_score));
    }


    public void addVitalSign(int vital_sign_id, String human_readable_name, String measurement_value, int ews_score)
    {
        addToSet(vital_sign_id, new ManualVitalSignBeingEntered(vital_sign_id, human_readable_name, measurement_value, ews_score));
    }


    private void addToSet(int vital_sign_id, ManualVitalSignBeingEntered manual_vital_sign_being_entered)
    {
        // Search for measurement of this type already entered - if there is one discard it and use this one instead

        int position_of_existing_measurement = -1;
        boolean found_existing_vital_sign_id = false;

        for (int i = 0; i< measurements_being_entered.size(); i++)
        {
            if (measurements_being_entered.get(i).getVitalSignId() == vital_sign_id)
            {
                found_existing_vital_sign_id = true;
                position_of_existing_measurement = i;
                break;
            }
        }

        if (found_existing_vital_sign_id)
        {
            measurements_being_entered.remove(position_of_existing_measurement);
        }

        measurements_being_entered.add(manual_vital_sign_being_entered);
    }


    public void setTimestamp(long timestamp)
    {
        this.timestamp_in_ms = timestamp;
    }


    public long getTimestamp()
    {
        return timestamp_in_ms;
    }


    public ArrayList<ManualVitalSignBeingEntered> getMeasurements()
    {
        return measurements_being_entered;
    }


    public int getSize()
    {
        return measurements_being_entered.size();
    }


    public ManualVitalSignBeingEntered getMeasurement(int vital_sign_id)
    {
        for (ManualVitalSignBeingEntered measurement : measurements_being_entered)
        {
            if (measurement.getVitalSignId() == vital_sign_id)
            {
                return measurement;
            }
        }

        return null;
    }


    public void setValidityTime(VitalSignValidityTimeDescriptor validity_time)
    {
        this.validity_time = validity_time;
    }


    public int getValidityTime()
    {
        return validity_time.value;
    }


    public String getValidityTimeForDisplay()
    {
        return validity_time.display_value;
    }


    public void remove(ManuallyEnteredVitalSignDescriptor vital_sign_selected)
    {
        ManualVitalSignBeingEntered measurement_to_remove = null;

        for (ManualVitalSignBeingEntered measurement : measurements_being_entered)
        {
            if (measurement.getVitalSignId() == vital_sign_selected.vital_sign_id)
            {
                measurement_to_remove = measurement;
            }
        }

        if (measurement_to_remove != null)
        {
            measurements_being_entered.remove(measurement_to_remove);
        }
    }
}
