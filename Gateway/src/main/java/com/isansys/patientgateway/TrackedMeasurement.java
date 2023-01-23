package com.isansys.patientgateway;

import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;

public class TrackedMeasurement implements Cloneable
{
    public boolean valid;
    public MeasurementVitalSign measurement;
    public long timeout_time;
    public long initial_measurement_time;

    public TrackedMeasurement(MeasurementVitalSign measurement, long timeout)
    {
        this.valid = true;
        this.measurement = measurement;
        this.timeout_time = measurement.timestamp_in_ms + timeout;
        this.initial_measurement_time = measurement.timestamp_in_ms;
    }

    @Override
    public TrackedMeasurement clone() throws CloneNotSupportedException
    {
        return (TrackedMeasurement)super.clone();
    }

    public VitalSignType getType()
    {
        return measurement.getType();
    }
}
