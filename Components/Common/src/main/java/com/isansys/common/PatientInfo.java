package com.isansys.common;

public class PatientInfo
{
    private ThresholdSet thresholdSet;
    private ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetails;

    private String hospital_patient_id;

    public PatientInfo()
    {
        reset();
    }

    public void reset()
    {
        resetServersThresholdSetInfo();

        hospital_patient_id = "";
    }

    public String getHospitalPatientId()
    {
        return hospital_patient_id;
    }

    public void setHospitalPatientId(String hospital_patient_id)
    {
        this.hospital_patient_id = hospital_patient_id;
    }

    public ThresholdSet getThresholdSet()
    {
        return thresholdSet;
    }

    public void setThresholdSet(ThresholdSet thresholdSet)
    {
        this.thresholdSet = thresholdSet;
    }

    public void resetServersThresholdSetInfo()
    {
        thresholdSet = null;
        thresholdSetAgeBlockDetails = null;
    }

    public ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetails()
    {
        return thresholdSetAgeBlockDetails;
    }

    public void setThresholdSetAgeBlockDetails(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetails)
    {
        this.thresholdSetAgeBlockDetails = thresholdSetAgeBlockDetails;
    }
}
