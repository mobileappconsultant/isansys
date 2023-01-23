package com.isansys.pse_isansysportal;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

class AnnotationAdapter extends SimpleAdapter
{
    public AnnotationAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
    {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);

        if (position % 2 == 1)
        {
            view.setBackgroundColor(Color.parseColor("#F0FFFF"));
        }

        return view;
    }
}
