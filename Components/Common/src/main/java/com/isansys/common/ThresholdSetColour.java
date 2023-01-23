package com.isansys.common;

import android.os.Parcel;
import android.os.Parcelable;

public class ThresholdSetColour implements Parcelable
{
    public int score;
    public int colour;
    public int text_colour;
    public int servers_database_row_id;
    public int local_database_row_id;

    public ThresholdSetColour()
    {
    }
    

    public ThresholdSetColour(int score, int colour, int text_colour)
    {
        this.score = score;
        this.colour = colour;
        this.text_colour = text_colour;
    }

    
    @Override
    public int describeContents() 
    {
        return 0;
    }
    

    @Override
    public void writeToParcel(Parcel dest, int flags) 
    {
        dest.writeInt(score);
        dest.writeInt(colour);
        dest.writeInt(text_colour);
        dest.writeInt(servers_database_row_id);
        dest.writeInt(local_database_row_id);
    }
    

	private ThresholdSetColour(Parcel in)
	{
		this.score = in.readInt();
		this.colour = in.readInt();
        this.text_colour = in.readInt();
		this.servers_database_row_id = in.readInt();
		this.local_database_row_id = in.readInt();
	}

	public static final Parcelable.Creator<ThresholdSetColour> CREATOR = new Parcelable.Creator<ThresholdSetColour>()
	{
		@Override
		public ThresholdSetColour createFromParcel(Parcel source)
		{
			return new ThresholdSetColour(source);
		}

		@Override
		public ThresholdSetColour[] newArray(int size)
		{
			return new ThresholdSetColour[size];
		}
	};
}
