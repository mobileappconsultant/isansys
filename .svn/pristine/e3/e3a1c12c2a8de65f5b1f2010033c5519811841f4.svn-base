package com.isansys.pse_isansysportal;

import android.os.Parcel;
import android.os.Parcelable;


public class GraphColourBand implements Parcelable
{
    public final int ews_value;
    public final double less_than_value;
    public final double greater_than_or_equal_value;
    public final int band_colour;
    public final int text_colour;
    
    public GraphColourBand(int ews_value, double desired_less_than_value, double desired_greater_than_or_equal_value, int desired_band_colour, int desired_text_colour)
    {
        this.ews_value = ews_value;
    	this.greater_than_or_equal_value = desired_greater_than_or_equal_value;
        this.less_than_value = desired_less_than_value;
        this.band_colour = desired_band_colour;
        this.text_colour = desired_text_colour;
    }
    

    private GraphColourBand(Parcel in)
    {
        this.ews_value = in.readInt();
    	this.greater_than_or_equal_value = in.readDouble();
        this.less_than_value = in.readDouble();
        this.band_colour = in.readInt();
        this.text_colour = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.ews_value);
    	dest.writeDouble(this.greater_than_or_equal_value);
        dest.writeDouble(this.less_than_value);
        dest.writeInt(this.band_colour);
        dest.writeInt(this.text_colour);
    }

    public static final Creator<GraphColourBand> CREATOR = new Parcelable.Creator<GraphColourBand>()
    {
        public GraphColourBand createFromParcel(Parcel in)
        {
            return new GraphColourBand(in);
        }


        public GraphColourBand[] newArray(int size)
        {
            return new GraphColourBand[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}