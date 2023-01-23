package com.isansys.pse_isansysportal;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener
{
    boolean userSelect = false;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        userSelect = true;
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        if (userSelect)
        {
            // Your selection handling code here
            userSelect = false;

            onItemSelectedByUser(parent, view, pos, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
    {
        // override to handle user selections.
    }
}
