package com.isansys.pse_isansysportal;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.isansys.remotelogging.RemoteLogging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentLogCatDisplay extends ListFragmentIsansys implements OnItemClickListener
{
    private static final String TAG = FragmentLogCatDisplay.class.getName();
	
    private LoggerListAdapter mAdapter;
    private LayoutInflater mInflater;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	Log.d(TAG, "FragmentLogCatDisplay onCreateView");

        return inflater.inflate(R.layout.logcat_display, container, false);
    }


    @Override
    public void onStop()
    {
        super.onStop();

        Log.d(TAG, "FragmentLogCatDisplay onStop");
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
            mAdapter = new LoggerListAdapter(getActivity());
            setListAdapter(mAdapter);
        }
        else
        {
            Log.e(TAG, "onViewCreated : FragmentLogCatDisplay getActivity() is null");
        }

		getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
    {
        if(getActivity() != null)
        {
            Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.e(TAG, "onItemClick : FragmentLogCatDisplay getActivity() is null");
        }
    }
    
    private boolean auto_scroll = true;
    
    public void setStackStatus(boolean stackStatusButtonEnabled)
    {
        Log.d(TAG, "setStackStatus : stackStatusButtonEnabled =" + stackStatusButtonEnabled);

    	if(stackStatusButtonEnabled)
    	{
    		auto_scroll = (getListView().getScrollY() + getListView().getHeight() >= getListView().getBottom());
    		
    		Log.d(TAG, "setStackStatus : auto_scroll =" + auto_scroll);
    	}
    	else
    	{
    		auto_scroll = false;
    	}
    }
    
    
    public void putLogLineInFragment(String log_line)
    {
    	if(mAdapter != null)
    	{
    		mAdapter.addLine(log_line);

            int LOG_CAT_MAX_LINES_TO_KEEP = 1000;
            if(mAdapter.getCount() >= LOG_CAT_MAX_LINES_TO_KEEP)
    		{
    			mAdapter.mLines.remove(0);
    			mAdapter.notifyDataSetChanged();
    		}
    	}
    }
    

   
    /*
     * This is the list adapter for the Logger, it holds an array of strings and adds them
     * to the list view recycling views for obvious performance reasons.
     */
    class LoggerListAdapter extends BaseAdapter
    {
        private final ArrayList<String> mLines;

        LoggerListAdapter(Context c)
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
                convertView = mInflater.inflate(R.layout.log_item, parent, false);

                holder = convertView.findViewById(R.id.log_line);
                holder.setTypeface(Typeface.MONOSPACE);

                convertView.setTag(holder);
            } 
            else 
            {
                holder = (TextView) convertView.getTag();
            }

            holder.setText(new LogFormattedString(line, Log));

            if (auto_scroll)
            {
                getListView().setSelection(mLines.size() - 1);
            }

            return convertView;
        }

        void addLine(String line)
        {
            mLines.add(line);
            notifyDataSetChanged();
        }
    }
    

    private static class LogFormattedString extends SpannableString 
    {
        LogFormattedString(String line, RemoteLogging Log)
        {
            super(line);

            try 
            {
                if (line.length() < 4) 
                {
                    Log.e(TAG, "Log line Length is less than 4");
                }
            }
            catch (Exception e) 
            {
                setSpan(new ForegroundColorSpan(0xffddaacc), 0, length(), 0);
            }
        }
    }
}