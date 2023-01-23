package com.isansys.pse_isansysportal;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.isansys.remotelogging.RemoteLogging;

import org.jetbrains.annotations.NotNull;

// All fragments extend this. This way they do not have to worry about the Main Activity Interface code.
public abstract class FragmentIsansys extends Fragment
{
    private final String TAG = this.getClass().getSimpleName();

    MainActivityInterface main_activity_interface;
    SystemCommands system_commands;
    RemoteLogging Log;

    @Override
    public void onCreate(Bundle saved)
    {
        super.onCreate(saved);

        Log.d(TAG, "onCreate");
    }


    @Override
    public void onAttach(@NotNull Context context)
    {
        super.onAttach(context);

        if (context instanceof MainActivityInterface)
        {
            main_activity_interface = (MainActivityInterface) context;
            system_commands = main_activity_interface.getSystemCommands();
            Log = main_activity_interface.getMainActivityLogger();
        }
        else
        {
            throw new ClassCastException(context.toString() + " must implement MainActivityInterface");
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Log.d(TAG, "onStart");
    }


    @Override
    public void onResume()
    {
        super.onResume();

        Log.d(TAG, "onResume");
    }


    @Override
    public void onPause()
    {
        super.onPause();

        Log.d(TAG, "onPause");
    }


    @Override
    public void onStop()
    {
        super.onStop();

        Log.d(TAG, "onStop");
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView");
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }


    @Override
    public void onDetach()
    {
        super.onDetach();

        Log.d(TAG, "onDetach");
    }


    private boolean isFragmentAdded(Fragment fragment)
    {
        return fragment.isAdded();
    }


    /**
     * This Function checks if the Fragment and it's activity is null. This function should be used before calling isFragmentAdded(fragment)
     * @param fragment : Fragment = Reference of fragment to be checked
     * @param fragmentActivity : Activity = Reference to the Fragment Activity
     * @return false if fragment or fragmentActivity is null.
     */
    private boolean isActivityAndFragmentNotNull(Fragment fragment, FragmentActivity fragmentActivity)
    {
        // Order is important here. The double AND - && - evaluates left to right, and stops as soon as it gets a false. So, check fragmentActivity != null first.
        return ( (fragmentActivity != null) && (fragment != null) );
    }

    /**
     * Function to check if fragment reference and its activity is NOT NULL, and fragment is added.
     * If this function returns true, then the fragment can be removed.
     *
     * @param fragment : Fragment = Reference of fragment to be checked
     * @param fragmentActivity : Activity = Reference to the Fragment Activity
     * @return : true if fragment is not added
     */
    protected boolean isFragmentAddedAndSafeToRemove(Fragment fragment, FragmentActivity fragmentActivity)
    {
        // Set default value to false, because It is better not to do operations in uncertain and undefined situation
        boolean return_boolean = false;

        if(isActivityAndFragmentNotNull(fragment, fragmentActivity))
        {
            if(isFragmentAdded(fragment))
            {
                return_boolean = true;
            }
            else
            {
                Log.e(TAG, "isFragmentAddedAndSafeToRemove fragment is not added");
            }
        }
        else
        {
            Log.e(TAG, "isFragmentAddedAndSafeToRemove fragment null? " + (fragment == null) + " fragmentActivity null? " + (fragmentActivity == null));
        }

        return return_boolean;
    }


    /**
     * Function to check if fragment reference and its activity is NOT NULL, and fragment is NOT added.
     * If this function returns true, then the fragment can be added.
     *
     * @param fragment : Fragment = Reference of fragment to be checked
     * @param fragmentActivity : Activity = Reference to the Fragment Activity
     * @return : true if fragment is not added
     */
    public boolean isFragmentNotAddedButSafeToAdd(Fragment fragment, FragmentActivity fragmentActivity)
    {
        // Set default value to false, because It is better not to do operations in uncertain and undefined situation
        boolean return_boolean = false;

        if(isActivityAndFragmentNotNull(fragment, fragmentActivity))
        {
            if(!isFragmentAdded(fragment))
            {
                return_boolean = true;
            }
            else
            {
                Log.e(TAG, "isFragmentNotAddedButSafeToAdd fragment has already been added");
            }
        }
        else
        {
            Log.e(TAG, "isFragmentNotAddedButSafeToAdd fragment null? " + (fragment == null) + " fragmentActivity null? " + (fragmentActivity == null));
        }

        return return_boolean;
    }


    protected void removeFragmentIfShown(FragmentIsansys fragment)
    {
        if(isFragmentAddedAndSafeToRemove(fragment, getActivity()))
        {
            try
            {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
            catch (Exception e)
            {
                Log.e(TAG, "removeFragmentIfShown : Exception : " + e.getMessage());
            }
        }
    }


    protected void replaceFragmentIfSafe(int id_to_replace, Fragment fragment)
    {
        if(isFragmentNotAddedButSafeToAdd(fragment, getActivity()))
        {
            try
            {
                getChildFragmentManager().beginTransaction().replace(id_to_replace, fragment).commitNow();
            }
            catch (Exception e)
            {
                Log.e(TAG, "replaceFragmentIfSafe : Exception : " + e.getMessage());
            }
        }
        else
        {
            Log.e(TAG, "replaceFragmentIfSafe : getActivity() is null or fragment already added");
        }
    }


    protected void addFragmentIfSafe(int id_to_replace, Fragment fragment)
    {
        if(isFragmentNotAddedButSafeToAdd(fragment, getActivity()))
        {
            try
            {
                getChildFragmentManager().beginTransaction().add(id_to_replace, fragment).commitNow();
            }
            catch (Exception e)
            {
                Log.e(TAG, "addFragmentIfSafe : Exception : " + e.getMessage());
            }
        }
        else
        {
            Log.e(TAG, "addFragmentIfSafe : getActivity() is null or fragment already added");
        }
    }
}
