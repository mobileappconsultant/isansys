package com.isansys.patientgateway;

import java.util.ArrayList;

public abstract class IntermediateMeasurement
{
    protected int amplitude;
    protected long timestamp_in_ms;

    public abstract int getAmplitude();
    public abstract long getTimestampInMs();
    public abstract <T extends IntermediateMeasurement> void calculateAndSetRrIntervalIfPossible(T previous_beat, ArrayList<T> list_to_report);

    public abstract boolean hasRrIntervals();
}
