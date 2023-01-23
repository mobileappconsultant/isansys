package com.isansys.pse_isansysportal;

import android.content.Context;
import androidx.fragment.app.ListFragment;

import com.isansys.remotelogging.RemoteLogging;

import org.jetbrains.annotations.NotNull;

public class ListFragmentIsansys extends ListFragment
{
    MainActivityInterface main_activity_interface;
    RemoteLogging Log;

    @Override
    public void onAttach(@NotNull Context context)
    {
        super.onAttach(context);

        if (context instanceof MainActivityInterface)
        {
            main_activity_interface = (MainActivityInterface) context;
            Log = main_activity_interface.getMainActivityLogger();
        }
        else
        {
            throw new ClassCastException(context.toString() + " must implement MainActivityInterface");
        }
    }
}
