package com.isansys.common;

import android.os.Parcel;
import android.os.Parcelable;

public class ThresholdSetLevel implements Parcelable
{
    public float top;
    public float bottom;
    public int early_warning_score;
    public int measurement_type;
    public String measurement_type_string;
    public String display_text;                                                                     // Measurement text. E.g. "Unresponsive" instead of a number
    public String information_text;                                                                 // Text to show on the RHS when entering the measurement
    public int servers_database_row_id;
    public int local_database_row_id;

    public ThresholdSetLevel()
    {
    	
    }
    

    public ThresholdSetLevel(float top_val, float bottom_val, int ews, int type, String type_string, String display_text, String information_text)
    {
        this.top = top_val;
        this.bottom = bottom_val;
        this.early_warning_score = ews;
        this.measurement_type = type;
        this.measurement_type_string = type_string;
        this.display_text = display_text;
        this.information_text = information_text;
    }

    
    @Override
    public int describeContents() 
    {
        return 0;
    }
    

    @Override
    public void writeToParcel(Parcel dest, int flags) 
    {
        dest.writeFloat(top);
        dest.writeFloat(bottom);
        dest.writeInt(early_warning_score);
        dest.writeInt(measurement_type);
        dest.writeString(measurement_type_string);
        dest.writeString(display_text);
        dest.writeString(information_text);
        dest.writeInt(servers_database_row_id);
        dest.writeInt(local_database_row_id);
    }
    

	private ThresholdSetLevel(Parcel in)
	{
		this.top = in.readFloat();
		this.bottom = in.readFloat();
		this.early_warning_score = in.readInt();
		this.measurement_type = in.readInt();
		this.measurement_type_string = in.readString();
        this.display_text = in.readString();
        this.information_text = in.readString();
		this.servers_database_row_id = in.readInt();
		this.local_database_row_id = in.readInt();
	}

	public static final Parcelable.Creator<ThresholdSetLevel> CREATOR = new Parcelable.Creator<ThresholdSetLevel>() 
	{
		@Override
		public ThresholdSetLevel createFromParcel(Parcel source) 
		{
			return new ThresholdSetLevel(source);
		}

		@Override
		public ThresholdSetLevel[] newArray(int size) 
		{
			return new ThresholdSetLevel[size];
		}
	};
}
