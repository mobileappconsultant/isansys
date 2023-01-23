package com.isansys.pse_isansysportal;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isansys.common.measurements.VitalSignType;
import com.isansys.pse_isansysportal.MainActivity.VitalSignAsStrings;

import org.jetbrains.annotations.NotNull;

public class FragmentManualVitalSignsDisplayIndividual extends ListFragmentIsansys
{
    private static final String TAG = FragmentManualVitalSignsDisplayIndividual.class.getName();

    private MeasurementListAdapter measurement_list_adaptor;
    private LayoutInflater mInflater;

    private VitalSignType vital_sign_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	Log.d(TAG, "onCreateView");

        return inflater.inflate(R.layout.manual_vital_signs_display_individual, container, false);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            int x = bundle.getInt(MainActivity.VITAL_SIGN_TYPE, 0);
            vital_sign_type = VitalSignType.values()[x];
        }

        Log.d(TAG, "onStart");
    }


    @Override
    public void onResume()
    {
        super.onResume();

        Log.d(TAG, "onResume");

        ArrayList<VitalSignAsStrings> vital_signs = main_activity_interface.getDataForManuallyEnteredVitalSignsDisplayIndividualFragment(vital_sign_type);

        int vital_signs_size = vital_signs.size();
        for (int i = 0; i < vital_signs_size; i++)
        {
            VitalSignAsStrings vital_sign_as_strings = vital_signs.get(i);

            addMeasurement(vital_sign_as_strings.timestamp_in_ms + " : " + vital_sign_as_strings.measurement);
        }
    }


    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated");

		getListView().setStackFromBottom(true);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		getListView().setDividerHeight(0);

        if(getActivity() != null)
        {
            measurement_list_adaptor = new MeasurementListAdapter(getActivity());
            setListAdapter(measurement_list_adaptor);
        }
        else
        {
            Log.e(TAG, "onViewCreated() : FragmentManualVitalSignsDisplayIndividual getActivity() is null");
        }
    }


    private void addMeasurement(String log_line)
    {
    	if(measurement_list_adaptor != null)
    	{
    		measurement_list_adaptor.addLine(log_line);

            int MAX_LINES = 500;
            if(measurement_list_adaptor.getCount() >= MAX_LINES)
    		{
    			measurement_list_adaptor.mLines.remove(0);
    			measurement_list_adaptor.notifyDataSetChanged();
    		}
    	}
    }



    /*
     * This is the list adapter for the Logger, it holds an array of strings and adds them
     * to the list view recycling views for obvious performance reasons.
     */
    class MeasurementListAdapter extends BaseAdapter
    {
        private final ArrayList<String> mLines;

        MeasurementListAdapter(Context c)
        {
            mLines = new ArrayList<>();
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount()
        {
            return mLines.size();
        }

        public long getItemId(int pos)
        {
            return pos;
        }

        public Object getItem(int pos)
        {
            return mLines.get(pos);
        }

        public View getView(int pos, View convertView, ViewGroup parent)
        {
            TextView holder;
            String line = mLines.get(pos);

            if (convertView == null)
            {
                //inflate the view here because there's no existing view object.
                convertView = mInflater.inflate(R.layout.manual_vital_sign_for_listview, parent, false);

                holder = convertView.findViewById(R.id.measurement_line);
                holder.setTypeface(Typeface.MONOSPACE);
                holder.setTextSize(15);

                convertView.setTag(holder);
            }
            else
            {
                holder = (TextView) convertView.getTag();
            }

            holder.setText(line);

            getListView().setSelection(mLines.size() - 1);

            return convertView;
        }

        void addLine(String line)
        {
            mLines.add(line);
            notifyDataSetChanged();
        }
    }
}